package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WonderLightDatabase
import com.example.data.mock.TravelMockData
import com.example.data.model.Destination
import com.example.data.model.DestinationCategory
import com.example.data.model.Flight
import com.example.data.model.Hotel
import com.example.data.model.TravelOffer
import com.example.data.model.TripBooking
import com.example.data.remote.AIAdventureMode
import com.example.data.remote.TravelAIEngine
import com.example.data.repository.BookingRepository
import com.example.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

import com.example.data.remote.AuthResult
import com.example.data.remote.SupabaseAuthManager
import com.example.data.remote.SupabaseUser

data class BookingFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val adults: Int = 2,
    val children: Int = 0,
    val selectedPaymentMethod: String = "UPI",
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

data class AIChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: AIAdventureMode = AIAdventureMode.FAST_TRAVEL_TIPS
)

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WonderLightDatabase.getDatabase(application)
    private val travelRepository = TravelRepository(db.wishlistDao(), db.searchHistoryDao())
    private val bookingRepository = BookingRepository(db.bookingDao())

    // Supabase Authentication & User Session
    private val _currentUser = MutableStateFlow<SupabaseUser?>(SupabaseAuthManager.getSavedUser(application))
    val currentUser: StateFlow<SupabaseUser?> = _currentUser.asStateFlow()

    // User Profile Picture
    private val _userAvatarUri = MutableStateFlow<String?>(null)
    val userAvatarUri: StateFlow<String?> = _userAvatarUri.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(_currentUser.value != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isAuthSheetOpen = MutableStateFlow(false)
    val isAuthSheetOpen: StateFlow<Boolean> = _isAuthSheetOpen.asStateFlow()

    private val _authPromptMessage = MutableStateFlow("Please sign in or create an account to book your reservation.")
    val authPromptMessage: StateFlow<String> = _authPromptMessage.asStateFlow()

    private var pendingBookingCallback: (() -> Unit)? = null

    // Dynamic live services loaded from Supabase & Admin Panel
    private val _flights = MutableStateFlow<List<Flight>>(travelRepository.getAllFlights())
    val flights: StateFlow<List<Flight>> = _flights.asStateFlow()

    private val _hotels = MutableStateFlow<List<Hotel>>(travelRepository.getAllHotels())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _holidays = MutableStateFlow<List<Destination>>(travelRepository.getAllDestinations())
    val holidays: StateFlow<List<Destination>> = _holidays.asStateFlow()

    val destinations: List<Destination>
        get() = _holidays.value

    val offers: List<TravelOffer> = travelRepository.getAllOffers()

    // Filtered by category
    private val _selectedCategory = MutableStateFlow(DestinationCategory.ALL)
    val selectedCategory: StateFlow<DestinationCategory> = _selectedCategory.asStateFlow()

    // Search query & results
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Destination>>(travelRepository.getAllDestinations())
    val searchResults: StateFlow<List<Destination>> = _searchResults.asStateFlow()

    // Wishlist / Saved IDs from Room
    val savedDestinationIds: StateFlow<List<String>> = travelRepository.getSavedDestinationIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All bookings from Room Database (User-scoped)
    private val _allBookings = MutableStateFlow<List<TripBooking>>(emptyList())
    val allBookings: StateFlow<List<TripBooking>> = _allBookings.asStateFlow()

    // Selected destination for details screen
    private val _selectedDestination = MutableStateFlow<Destination>(destinations.first())
    val selectedDestination: StateFlow<Destination> = _selectedDestination.asStateFlow()

    // Booking form state
    private val _bookingForm = MutableStateFlow(BookingFormState())
    val bookingForm: StateFlow<BookingFormState> = _bookingForm.asStateFlow()

    // Latest confirmed booking
    private val _latestConfirmedBooking = MutableStateFlow<TripBooking?>(null)
    val latestConfirmedBooking: StateFlow<TripBooking?> = _latestConfirmedBooking.asStateFlow()

    // AI Chat & Voice Assistant
    private val _aiMessages = MutableStateFlow<List<AIChatMessage>>(
        listOf(
            AIChatMessage(
                sender = "ai",
                text = "Hello! I am your WonderLight Travel Intelligence companion. Ask me for custom itineraries, Google Maps grounded attractions, or high thinking route planning!",
                mode = AIAdventureMode.FAST_TRAVEL_TIPS
            )
        )
    )
    val aiMessages: StateFlow<List<AIChatMessage>> = _aiMessages.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Voice conversation state
    private val _isVoiceActive = MutableStateFlow(false)
    val isVoiceActive: StateFlow<Boolean> = _isVoiceActive.asStateFlow()

    // Reload & Cloud Sync System State
    private val _isReloading = MutableStateFlow(false)
    val isReloading: StateFlow<Boolean> = _isReloading.asStateFlow()

    private val _lastReloadTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastReloadTimestamp: StateFlow<Long> = _lastReloadTimestamp.asStateFlow()

    private val _lastReloadMessage = MutableStateFlow<String?>("App initialized")
    val lastReloadMessage: StateFlow<String?> = _lastReloadMessage.asStateFlow()

    // Villa Search State
    private val _villaSearchResults = MutableStateFlow<List<com.example.data.model.VillaItem>>(emptyList())
    val villaSearchResults: StateFlow<List<com.example.data.model.VillaItem>> = _villaSearchResults.asStateFlow()
    private val _isSearchingVillas = MutableStateFlow(false)
    val isSearchingVillas: StateFlow<Boolean> = _isSearchingVillas.asStateFlow()

    // Train Search State
    private val _trainSearchResults = MutableStateFlow<List<com.example.data.model.TrainItem>>(emptyList())
    val trainSearchResults: StateFlow<List<com.example.data.model.TrainItem>> = _trainSearchResults.asStateFlow()
    private val _isSearchingTrains = MutableStateFlow(false)
    val isSearchingTrains: StateFlow<Boolean> = _isSearchingTrains.asStateFlow()

    // Bus Search State
    private val _busSearchResults = MutableStateFlow<List<com.example.data.model.BusItem>>(emptyList())
    val busSearchResults: StateFlow<List<com.example.data.model.BusItem>> = _busSearchResults.asStateFlow()
    private val _isSearchingBuses = MutableStateFlow(false)
    val isSearchingBuses: StateFlow<Boolean> = _isSearchingBuses.asStateFlow()

    // Cab Search State
    private val _cabSearchResults = MutableStateFlow<List<com.example.data.model.CabItem>>(emptyList())
    val cabSearchResults: StateFlow<List<com.example.data.model.CabItem>> = _cabSearchResults.asStateFlow()
    private val _isSearchingCabs = MutableStateFlow(false)
    val isSearchingCabs: StateFlow<Boolean> = _isSearchingCabs.asStateFlow()

    // Cruise Search State
    private val _cruiseSearchResults = MutableStateFlow<List<com.example.data.model.CruiseItem>>(emptyList())
    val cruiseSearchResults: StateFlow<List<com.example.data.model.CruiseItem>> = _cruiseSearchResults.asStateFlow()
    private val _isSearchingCruises = MutableStateFlow(false)
    val isSearchingCruises: StateFlow<Boolean> = _isSearchingCruises.asStateFlow()

    // Real Notifications from Supabase
    private val _notifications = MutableStateFlow<List<com.example.data.model.AppNotification>>(emptyList())
    val notifications: StateFlow<List<com.example.data.model.AppNotification>> = _notifications.asStateFlow()

    private val _isNotificationsLoading = MutableStateFlow(false)
    val isNotificationsLoading: StateFlow<Boolean> = _isNotificationsLoading.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Wallet State (Only active when authenticated)
    private val _walletBalance = MutableStateFlow(0)
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<com.example.data.model.WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<com.example.data.model.WalletTransaction>> = _walletTransactions.asStateFlow()

    // Dynamic Admin-Controlled Signup Wallet Bonus (default ₹2,500, controllable via Admin Panel)
    private val _signupBonus = MutableStateFlow(2500)
    val signupBonus: StateFlow<Int> = _signupBonus.asStateFlow()

    // Reviews State
    private val _userReviews = MutableStateFlow<List<com.example.data.model.UserReview>>(
        listOf(
            com.example.data.model.UserReview(
                id = "rev_1",
                userName = "Raghab Barik",
                destinationTitle = "Kashmir Paradise Valley",
                rating = 5,
                comment = "Unbelievable experience! The houseboat in Srinagar and pony ride in Gulmarg were flawless.",
                date = "25 Sep 2026"
            ),
            com.example.data.model.UserReview(
                id = "rev_2",
                userName = "Suman Barik",
                destinationTitle = "Goa Beachfront Escape",
                rating = 5,
                comment = "Super smooth flight and villa booking. Customer support answered immediately.",
                date = "26 Sep 2026"
            )
        )
    )
    val userReviews: StateFlow<List<com.example.data.model.UserReview>> = _userReviews.asStateFlow()

    init {
        // Load saved user avatar from preferences
        val userPrefs = application.getSharedPreferences("wonderlight_user_prefs", Context.MODE_PRIVATE)
        val savedAvatar = userPrefs.getString("user_avatar_uri", null)
        if (!savedAvatar.isNullOrBlank()) {
            _userAvatarUri.value = savedAvatar
        }

        // Continuously observe all bookings in Room so no booking is ever lost
        viewModelScope.launch {
            bookingRepository.allBookings.collect { localList ->
                _allBookings.value = localList.distinctBy { it.bookingId }
            }
        }

        val saved = _currentUser.value
        if (saved != null) {
            _bookingForm.value = BookingFormState(
                fullName = saved.fullName,
                email = saved.email,
                phone = saved.phone
            )
            loadUserBookings(saved.id)
            initUserWallet()
        }
        // Load all dynamic catalogs from Supabase and Cloud Admin
        loadAllLiveServices()
        // Refresh bookings from cloud & admin
        refreshBookings()
    }

    fun initUserWallet(customBonus: Int? = null) {
        val bonus = customBonus ?: _signupBonus.value
        _walletBalance.value = bonus
        _walletTransactions.value = listOf(
            com.example.data.model.WalletTransaction(
                id = "tx_welcome_init",
                title = "Welcome Bonus Credited",
                amount = bonus,
                isCredit = true,
                date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                description = "Account registration reward"
            )
        )
    }

    fun loadAllLiveServices() {
        viewModelScope.launch {
            try {
                val liveFlights = com.example.data.remote.SupabaseSearchManager.fetchFlights()
                if (liveFlights.isNotEmpty()) {
                    _flights.value = liveFlights
                }
            } catch (_: Exception) {}

            try {
                val liveHotels = com.example.data.remote.SupabaseSearchManager.fetchHotels()
                if (liveHotels.isNotEmpty()) {
                    _hotels.value = liveHotels
                }
            } catch (_: Exception) {}

            try {
                val liveHolidays = com.example.data.remote.SupabaseSearchManager.fetchHolidays()
                if (liveHolidays.isNotEmpty()) {
                    _holidays.value = liveHolidays
                    travelRepository.updateLiveDestinations(liveHolidays)
                    if (_searchQuery.value.isBlank()) {
                        _searchResults.value = liveHolidays
                    }
                }
            } catch (_: Exception) {}

            searchVillas("")
            searchTrains("", "")
            searchBuses("", "")
            searchCabs("All")
            searchCruises("", "")

            // 1. Fetch dynamic signup wallet bonus from Supabase / Admin
            try {
                val bonus = com.example.data.remote.SupabaseSearchManager.fetchSignupWalletBonus()
                _signupBonus.value = bonus
            } catch (_: Exception) {}

            // 2. Load notifications and begin background polling for live admin notifications
            loadNotifications(showSystemNotificationOnNew = false)
            startNotificationPolling()
        }
    }

    private var isNotificationPollingStarted = false

    /**
     * Periodically polls for new notifications sent from the WonderLight Admin Panel.
     * When a new notification arrives, it immediately sounds & appears in the Android phone status bar.
     */
    fun startNotificationPolling() {
        if (isNotificationPollingStarted) return
        isNotificationPollingStarted = true
        viewModelScope.launch {
            while (isActive) {
                delay(12000) // Poll every 12 seconds for fresh admin notifications
                try {
                    loadNotifications(showSystemNotificationOnNew = true)
                } catch (_: Exception) {}
            }
        }
    }

    fun loadNotifications(showSystemNotificationOnNew: Boolean = true) {
        viewModelScope.launch {
            _isNotificationsLoading.value = true
            try {
                val email = _currentUser.value?.email
                val liveList = com.example.data.remote.SupabaseSearchManager.fetchNotifications(email)
                _notifications.value = liveList

                // Display newly arrived notifications in the Android phone status bar
                if (showSystemNotificationOnNew && liveList.isNotEmpty()) {
                    val context = getApplication<android.app.Application>()
                    for (notif in liveList) {
                        if (!com.example.util.WLNotificationHelper.hasNotificationBeenShown(context, notif.id)) {
                            com.example.util.WLNotificationHelper.showSystemNotification(
                                context = context,
                                id = notif.id,
                                title = notif.title,
                                body = notif.body,
                                type = notif.type
                            )
                        }
                    }
                }
            } catch (_: Exception) {}
            finally {
                _isNotificationsLoading.value = false
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun addMoneyToWallet(amount: Int) {
        if (amount <= 0) return
        _walletBalance.value += amount
        val newTx = com.example.data.model.WalletTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "Added to Wallet",
            amount = amount,
            isCredit = true,
            date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
            description = "UPI / Card top-up"
        )
        _walletTransactions.value = listOf(newTx) + _walletTransactions.value
    }

    fun submitReview(destinationTitle: String, rating: Int, comment: String) {
        val userName = _currentUser.value?.fullName ?: "Verified Traveller"
        val newRev = com.example.data.model.UserReview(
            id = "rev_${System.currentTimeMillis()}",
            userName = userName,
            destinationTitle = destinationTitle,
            rating = rating,
            comment = comment,
            date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        )
        _userReviews.value = listOf(newRev) + _userReviews.value
    }

    fun loadUserBookings(userId: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            bookingRepository.getBookingsForUser(userId, user?.email ?: "").collect { localList ->
                if (localList.isNotEmpty()) {
                    _allBookings.value = localList.distinctBy { it.bookingId }
                }
            }
        }
        refreshBookings()
    }

    fun refreshBookings() {
        val user = _currentUser.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bookingRepository.deduplicateBookings()
                if (user != null && user.id.isNotBlank()) {
                    val cloudBookings = com.example.data.remote.SupabaseSearchManager.fetchUserBookings(
                        userId = user.id,
                        token = user.accessToken,
                        userEmail = user.email
                    )
                    if (cloudBookings.isNotEmpty()) {
                        cloudBookings.distinctBy { it.bookingId }.forEach { b ->
                            bookingRepository.createBooking(b)
                        }
                    }
                }
                com.example.data.remote.AdminSyncManager.reloadAllFromAdmin(bookingRepository)
                bookingRepository.deduplicateBookings()
            } catch (e: Exception) {
                android.util.Log.w("TravelViewModel", "Refresh bookings error: ${e.message}")
            }
        }
    }

    fun uploadUserProfilePicture(context: Context, imageUri: Uri, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val avatarFile = java.io.File(context.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
                avatarFile.outputStream().use { out ->
                    inputStream?.copyTo(out)
                }
                inputStream?.close()

                val savedPath = avatarFile.absolutePath
                val prefs = context.getSharedPreferences("wonderlight_user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("user_avatar_uri", savedPath).apply()
                _userAvatarUri.value = savedPath

                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Profile picture updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Failed to update picture: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun removeUserProfilePicture(context: Context) {
        val prefs = context.getSharedPreferences("wonderlight_user_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("user_avatar_uri").apply()
        _userAvatarUri.value = null
        android.widget.Toast.makeText(context, "Profile picture removed", android.widget.Toast.LENGTH_SHORT).show()
    }

    /**
     * Guards booking actions across all screens so that NO user can book without signing in or signing up.
     */
    fun requireAuthBeforeBooking(
        prompt: String = "Please sign in to continue with your booking.",
        onAuthenticated: () -> Unit
    ) {
        if (_isAuthenticated.value) {
            onAuthenticated()
        } else {
            _authPromptMessage.value = prompt
            pendingBookingCallback = onAuthenticated
            _isAuthSheetOpen.value = true
        }
    }

    fun openAuthSheet(prompt: String = "Sign in or create your account to continue.") {
        _authPromptMessage.value = prompt
        _isAuthSheetOpen.value = true
    }

    fun closeAuthSheet() {
        _isAuthSheetOpen.value = false
        pendingBookingCallback = null
    }

    fun signInWithSupabase(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = SupabaseAuthManager.signIn(email, password)) {
                is AuthResult.Success -> {
                    _currentUser.value = result.user
                    _isAuthenticated.value = true
                    _bookingForm.value = _bookingForm.value.copy(
                        fullName = result.user.fullName,
                        email = result.user.email,
                        phone = result.user.phone
                    )
                    SupabaseAuthManager.saveUserSession(getApplication(), result.user)
                    loadUserBookings(result.user.id)
                    loadNotifications()
                    initUserWallet()
                    _isAuthSheetOpen.value = false
                    val pending = pendingBookingCallback
                    pendingBookingCallback = null
                    pending?.invoke()
                    onComplete(true, result.message)
                }
                is AuthResult.Error -> {
                    onComplete(false, result.errorMessage)
                }
            }
        }
    }

    fun signUpWithSupabase(email: String, password: String, fullName: String, phone: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = SupabaseAuthManager.signUp(email, password, fullName, phone)) {
                is AuthResult.Success -> {
                    _currentUser.value = result.user
                    _isAuthenticated.value = true
                    _bookingForm.value = _bookingForm.value.copy(
                        fullName = result.user.fullName,
                        email = result.user.email,
                        phone = result.user.phone
                    )
                    SupabaseAuthManager.saveUserSession(getApplication(), result.user)
                    loadUserBookings(result.user.id)
                    loadNotifications()

                    // Admin-Controlled Signup Wallet Bonus
                    val bonus = _signupBonus.value
                    _walletBalance.value = bonus
                    val welcomeTx = com.example.data.model.WalletTransaction(
                        id = "tx_welcome_${System.currentTimeMillis()}",
                        title = "Welcome Bonus Credited",
                        amount = bonus,
                        isCredit = true,
                        date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                        description = "Admin Welcome Gift for signing up"
                    )
                    _walletTransactions.value = listOf(welcomeTx)

                    // Phone Status Bar Notification
                    val appCtx = getApplication<android.app.Application>()
                    com.example.util.WLNotificationHelper.showSystemNotification(
                        context = appCtx,
                        id = "welcome_signup_${result.user.id}",
                        title = "🎉 Welcome to WonderLight!",
                        body = "₹${"%,d".format(bonus)} welcome bonus has been credited to your WonderCash wallet.",
                        type = "wallet",
                        skipDuplicateCheck = true
                    )

                    // In-App Notification
                    val welcomeNotif = com.example.data.model.AppNotification(
                        id = "notif_welcome_${System.currentTimeMillis()}",
                        title = "🎉 Welcome to WonderLight!",
                        body = "₹${"%,d".format(bonus)} welcome credits have been added to your WonderCash wallet.",
                        type = "welcome",
                        target = result.user.email,
                        sentBy = "WonderLight Admin",
                        sentAt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
                        isRead = false
                    )
                    _notifications.value = listOf(welcomeNotif) + _notifications.value

                    _isAuthSheetOpen.value = false
                    val pending = pendingBookingCallback
                    pendingBookingCallback = null
                    pending?.invoke()
                    onComplete(true, result.message)
                }
                is AuthResult.Error -> {
                    onComplete(false, result.errorMessage)
                }
            }
        }
    }

    fun signInWithGoogle(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        onComplete(false, "Please sign in or register with your personal Supabase account.")
    }

    private var hasShownGuestPrompt = false

    fun checkFirstLaunchGuestPrompt() {
        if (!hasShownGuestPrompt && _currentUser.value == null) {
            hasShownGuestPrompt = true
            _authPromptMessage.value = "Sign in to unlock personalized deals and manage your bookings."
            _isAuthSheetOpen.value = true
        }
    }

    fun signOutFromSupabase() {
        val prevUserId = _currentUser.value?.id
        _currentUser.value = null
        _isAuthenticated.value = false
        _bookingForm.value = BookingFormState()
        _allBookings.value = emptyList()
        _walletBalance.value = 0
        _walletTransactions.value = emptyList()
        _notifications.value = emptyList()
        SupabaseAuthManager.clearSession(getApplication())
        viewModelScope.launch {
            if (!prevUserId.isNullOrBlank()) {
                bookingRepository.deleteBookingsForUser(prevUserId)
            }
        }
    }

    // Dynamic Search Functions
    fun searchVillas(location: String = "", minGuests: Int = 1, maxPrice: Int? = null) {
        viewModelScope.launch {
            _isSearchingVillas.value = true
            val results = com.example.data.remote.SupabaseSearchManager.searchVillas(location, minGuests, maxPrice)
            _villaSearchResults.value = results
            _isSearchingVillas.value = false
        }
    }

    fun searchTrains(origin: String = "", destination: String = "") {
        viewModelScope.launch {
            _isSearchingTrains.value = true
            val results = com.example.data.remote.SupabaseSearchManager.searchTrains(origin, destination)
            _trainSearchResults.value = results
            _isSearchingTrains.value = false
        }
    }

    fun searchBuses(origin: String = "", destination: String = "") {
        viewModelScope.launch {
            _isSearchingBuses.value = true
            val results = com.example.data.remote.SupabaseSearchManager.searchBuses(origin, destination)
            _busSearchResults.value = results
            _isSearchingBuses.value = false
        }
    }

    fun searchCabs(serviceType: String = "") {
        viewModelScope.launch {
            _isSearchingCabs.value = true
            val results = com.example.data.remote.SupabaseSearchManager.searchCabs(serviceType)
            _cabSearchResults.value = results
            _isSearchingCabs.value = false
        }
    }

    fun searchCruises(port: String = "", destination: String = "") {
        viewModelScope.launch {
            _isSearchingCruises.value = true
            val results = com.example.data.remote.SupabaseSearchManager.searchCruises(port, destination)
            _cruiseSearchResults.value = results
            _isSearchingCruises.value = false
        }
    }

    /**
     * Triggers a comprehensive reload of all app data, syncs Room bookings with
     * WonderLight Admin cloud backend, re-indexes destinations, and updates UI state.
     */
    fun reloadAllData(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        if (_isReloading.value) return
        viewModelScope.launch {
            _isReloading.value = true
            try {
                // 1. Refresh search results and category cache
                loadAllLiveServices()
                if (_searchQuery.value.isBlank()) {
                    _searchResults.value = _holidays.value
                } else {
                    _searchResults.value = travelRepository.searchDestinations(_searchQuery.value)
                }

                // 2. Sync with WonderLight Cloud Backend
                val result = com.example.data.remote.AdminSyncManager.reloadAllFromAdmin(bookingRepository)
                
                // 3. Small delay to provide satisfying tactile animation feedback
                kotlinx.coroutines.delay(400)

                _lastReloadTimestamp.value = System.currentTimeMillis()
                _lastReloadMessage.value = result.message
                _isReloading.value = false
                onComplete(result.success, result.message)
            } catch (e: Exception) {
                _isReloading.value = false
                val errMsg = "Reload completed (local offline mode): ${e.localizedMessage ?: "OK"}"
                _lastReloadMessage.value = errMsg
                onComplete(true, errMsg)
            }
        }
    }

    fun selectCategory(category: DestinationCategory) {
        _selectedCategory.value = category
        if (_searchQuery.value.isBlank()) {
            _searchResults.value = if (category == DestinationCategory.ALL) {
                _holidays.value
            } else {
                _holidays.value.filter { it.category == category }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        val list = _holidays.value
        if (query.isBlank()) {
            _searchResults.value = if (_selectedCategory.value == DestinationCategory.ALL) {
                list
            } else {
                list.filter { it.category == _selectedCategory.value }
            }
        } else {
            val q = query.trim().lowercase()
            _searchResults.value = list.filter {
                it.name.lowercase().contains(q) ||
                it.title.lowercase().contains(q) ||
                it.location.lowercase().contains(q) ||
                it.tagline.lowercase().contains(q)
            }
            viewModelScope.launch {
                travelRepository.saveSearch(query)
            }
        }
    }

    fun selectDestination(destination: Destination) {
        _selectedDestination.value = destination
    }

    fun selectDestinationById(id: String) {
        val found = _holidays.value.find { it.id.equals(id, ignoreCase = true) }
            ?: travelRepository.getDestinationById(id)
        if (found != null) {
            _selectedDestination.value = found
        }
    }

    fun toggleWishlist(destinationId: String) {
        viewModelScope.launch {
            travelRepository.toggleSaveDestination(destinationId)
        }
    }

    // Booking actions
    fun updateTravellerInfo(fullName: String, email: String, phone: String) {
        _bookingForm.value = _bookingForm.value.copy(
            fullName = fullName,
            email = email,
            phone = phone
        )
    }

    fun updateTravellerDetails(
        fullName: String,
        email: String,
        phone: String,
        adults: Int = 2,
        children: Int = 0,
        specialRequests: String? = null
    ) {
        _bookingForm.value = _bookingForm.value.copy(
            fullName = fullName,
            email = email,
            phone = phone,
            adults = adults,
            children = children
        )
    }

    fun updateAdultsCount(count: Int) {
        if (count in 1..10) {
            _bookingForm.value = _bookingForm.value.copy(adults = count)
        }
    }

    fun updateChildrenCount(count: Int) {
        if (count in 0..8) {
            _bookingForm.value = _bookingForm.value.copy(children = count)
        }
    }

    fun selectPaymentMethod(method: String) {
        _bookingForm.value = _bookingForm.value.copy(selectedPaymentMethod = method)
    }

    fun processBooking(onSuccess: (TripBooking) -> Unit) {
        requireAuthBeforeBooking("Please sign in to continue with your booking.") {
            viewModelScope.launch {
                _bookingForm.value = _bookingForm.value.copy(isProcessing = true, errorMessage = null)

                // Simulate quick physical payment transaction verification
                kotlinx.coroutines.delay(1200)

                val dest = _selectedDestination.value
                val currentForm = _bookingForm.value
                val user = _currentUser.value
                val travellerName = user?.fullName?.ifBlank { currentForm.fullName } ?: currentForm.fullName
                val email = user?.email?.ifBlank { currentForm.email } ?: currentForm.email
                val phone = if (!user?.phone.isNullOrBlank()) user.phone else currentForm.phone

                val totalAmount = dest.price * currentForm.adults + (dest.price * 0.5 * currentForm.children).toInt()

                val randomCode = (100000..999999).random()
                val bookingId = "WLAD$randomCode"

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateStr = dateFormat.format(Date())

                val newBooking = TripBooking(
                    bookingId = bookingId,
                    userId = user?.id ?: "",
                    destinationId = dest.id,
                    destinationTitle = dest.title,
                    duration = dest.duration,
                    travellerName = travellerName,
                    email = email,
                    phone = phone,
                    adults = currentForm.adults,
                    children = currentForm.children,
                    paymentMethod = currentForm.selectedPaymentMethod,
                    totalAmount = totalAmount,
                    bookingDate = dateStr,
                    status = "Confirmed",
                    imageRes = dest.imageRes
                )

                bookingRepository.createBooking(newBooking)
                _latestConfirmedBooking.value = newBooking
                _bookingForm.value = _bookingForm.value.copy(isProcessing = false)
                _allBookings.value = listOf(newBooking) + _allBookings.value.filter { it.bookingId != newBooking.bookingId }
                onSuccess(newBooking)
                try {
                    if (user != null) {
                        com.example.data.remote.SupabaseSearchManager.saveBookingToSupabase(newBooking, user.id, user.accessToken)
                    }
                    com.example.data.remote.AdminSyncManager.syncBookingToAdmin(newBooking)
                } catch (ignored: Exception) {}
            }
        }
    }

    fun createDirectBooking(booking: TripBooking, onSuccess: (TripBooking) -> Unit = {}) {
        requireAuthBeforeBooking("Please sign in to continue with your booking.") {
            val user = _currentUser.value
            val authenticatedBooking = if (user != null) {
                booking.copy(
                    userId = user.id,
                    travellerName = if (booking.travellerName == "Verified Guest" || booking.travellerName.isBlank()) user.fullName else booking.travellerName,
                    email = if (booking.email.contains("@wonderlight.com") || booking.email.isBlank()) user.email else booking.email,
                    phone = if (user.phone.isNotBlank()) user.phone else booking.phone
                )
            } else {
                booking
            }

            viewModelScope.launch {
                bookingRepository.createBooking(authenticatedBooking)
                _latestConfirmedBooking.value = authenticatedBooking
                _allBookings.value = listOf(authenticatedBooking) + _allBookings.value.filter { it.bookingId != authenticatedBooking.bookingId }
                onSuccess(authenticatedBooking)
                try {
                    if (user != null) {
                        com.example.data.remote.SupabaseSearchManager.saveBookingToSupabase(authenticatedBooking, user.id, user.accessToken)
                    }
                    com.example.data.remote.AdminSyncManager.syncBookingToAdmin(authenticatedBooking)
                } catch (ignored: Exception) {}
            }
        }
    }

    // AI queries
    fun sendAIMessage(prompt: String, mode: AIAdventureMode) {
        val userMsg = AIChatMessage(sender = "user", text = prompt, mode = mode)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAILoading.value = true

        viewModelScope.launch {
            val responseText = TravelAIEngine.askTravelAssistant(
                prompt = prompt,
                mode = mode,
                destinationContext = _selectedDestination.value.title
            )
            val aiMsg = AIChatMessage(sender = "ai", text = responseText, mode = mode)
            _aiMessages.value = _aiMessages.value + aiMsg
            _isAILoading.value = false
        }
    }

    fun setVoiceActive(active: Boolean) {
        _isVoiceActive.value = active
    }
}
