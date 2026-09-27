package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.WLPermissionsDialog
import com.example.util.WLNotificationHelper
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.remote.AIAdventureMode
import com.example.ui.components.WLBottomNavigation
import com.example.ui.components.WLNavTab
import com.example.ui.components.WLSideDrawer
import com.example.ui.components.WLVoiceDialog
import com.example.ui.screens.ai.AIAssistantScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.booking.BookingConfirmationScreen
import com.example.ui.screens.booking.BookingPaymentScreen
import com.example.ui.screens.booking.BookingTravellerScreen
import com.example.ui.screens.brand.BrandShowcaseScreen
import com.example.ui.screens.destination.DestinationDetailScreen
import com.example.ui.screens.destination.ItineraryScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.drawer.WalletScreen
import com.example.ui.screens.drawer.CustomerSupportScreen
import com.example.ui.screens.drawer.MyReviewsScreen
import com.example.ui.screens.drawer.ReferAndEarnScreen
import com.example.ui.screens.drawer.LanguagesScreen
import com.example.ui.screens.drawer.CountryCurrencyScreen
import com.example.ui.screens.offers.OffersScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.services.BusBookingScreen
import com.example.ui.screens.services.CabBookingScreen
import com.example.ui.screens.services.CruiseBookingScreen
import com.example.ui.screens.services.FlightBookingScreen
import com.example.ui.screens.services.FlightsScreen
import com.example.ui.screens.services.ForexBookingScreen
import com.example.ui.screens.services.HolidayBookingScreen
import com.example.ui.screens.services.HotelBookingScreen
import com.example.ui.screens.services.HotelDetailScreen
import com.example.ui.screens.services.HotelsScreen
import com.example.ui.screens.services.InsuranceBookingScreen
import com.example.ui.screens.services.ToursBookingScreen
import com.example.ui.screens.services.TrainBookingScreen
import com.example.ui.screens.services.VillaBookingScreen
import com.example.ui.screens.services.VisaBookingScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.trips.TripsScreen
import com.example.ui.theme.WonderLightTheme
import com.example.ui.viewmodel.TravelViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TravelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WonderLightTheme {
                WonderLightApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WonderLightApp(viewModel: TravelViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    val isVoiceActive by viewModel.isVoiceActive.collectAsState()
    var isSideDrawerOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var hasNotificationPerm by remember { mutableStateOf(WLNotificationHelper.hasNotificationPermission(context)) }
    var hasMicPerm by remember { mutableStateOf(WLNotificationHelper.hasMicPermission(context)) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var hasCheckedPermissionsOnLaunch by remember { mutableStateOf(false) }

    // System Permission Launcher for Notifications & Microphone
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permsMap ->
        val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permsMap[Manifest.permission.POST_NOTIFICATIONS] ?: hasNotificationPerm
        } else {
            true
        }
        val micGranted = permsMap[Manifest.permission.RECORD_AUDIO] ?: hasMicPerm

        hasNotificationPerm = notifGranted
        hasMicPerm = micGranted
        showPermissionsDialog = false

        if (notifGranted) {
            WLNotificationHelper.showTestNotification(context)
        }
    }

    val requestAllPermissions = {
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionsLauncher.launch(perms.toTypedArray())
    }

    // Auto-prompt permissions on first arrival at Home Screen if missing
    LaunchedEffect(currentRoute) {
        if (currentRoute == WLNavTab.HOME.route && !hasCheckedPermissionsOnLaunch) {
            hasCheckedPermissionsOnLaunch = true
            hasNotificationPerm = WLNotificationHelper.hasNotificationPermission(context)
            hasMicPerm = WLNotificationHelper.hasMicPermission(context)
            if (!hasNotificationPerm || !hasMicPerm) {
                showPermissionsDialog = true
            }
        }
    }

    // Handle deep navigation if opened by tapping a phone status bar notification
    LaunchedEffect(Unit) {
        val activity = context as? ComponentActivity
        val target = activity?.intent?.getStringExtra("extra_navigate_to")
        if (target == "notifications") {
            navController.navigate("notifications")
        }
    }

    val currentTab = when (currentRoute) {
        WLNavTab.EXPLORE.route -> WLNavTab.EXPLORE
        WLNavTab.TRIPS.route -> WLNavTab.TRIPS
        WLNavTab.OFFERS.route -> WLNavTab.OFFERS
        WLNavTab.PROFILE.route -> WLNavTab.PROFILE
        else -> WLNavTab.HOME
    }

    val shouldShowBottomBar = currentRoute in listOf(
        WLNavTab.HOME.route,
        WLNavTab.EXPLORE.route,
        WLNavTab.TRIPS.route,
        WLNavTab.OFFERS.route,
        WLNavTab.PROFILE.route
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 0. Splash Screen (Screen 1)
                composable("splash") {
                    SplashScreen(
                        onNavigateToHome = {
                            navController.navigate(WLNavTab.HOME.route) {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }

                // 1. Home
                composable(WLNavTab.HOME.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToExplore = {
                            navController.navigate(WLNavTab.EXPLORE.route)
                        },
                        onNavigateToSearch = {
                            navController.navigate("search")
                        },
                        onNavigateToDestination = { destId ->
                            viewModel.selectDestinationById(destId)
                            navController.navigate("destination_detail")
                        },
                        onNavigateToService = { serviceId ->
                            when (serviceId) {
                                "flights" -> navController.navigate("flights")
                                "hotels" -> navController.navigate("hotels")
                                "villas" -> navController.navigate("villas")
                                "holidays" -> navController.navigate("holidays")
                                "trains" -> navController.navigate("trains")
                                "buses" -> navController.navigate("buses")
                                "cabs" -> navController.navigate("cabs")
                                "tours" -> navController.navigate("tours")
                                "visa" -> navController.navigate("visa")
                                "cruise" -> navController.navigate("cruise")
                                "forex" -> navController.navigate("forex")
                                "insurance" -> navController.navigate("insurance")
                                else -> navController.navigate("flights")
                            }
                        },
                        onNavigateToAI = {
                            navController.navigate("ai_assistant")
                        },
                        onNavigateToProfile = {
                            navController.navigate(WLNavTab.PROFILE.route)
                        },
                        onNavigateToNotifications = {
                            navController.navigate("notifications")
                        },
                        onOpenSideMenu = {
                            isSideDrawerOpen = true
                        }
                    )
                }

                // 2. Explore
                composable(WLNavTab.EXPLORE.route) {
                    ExploreScreen(
                        viewModel = viewModel,
                        onDestinationSelect = { destId ->
                            viewModel.selectDestinationById(destId)
                            navController.navigate("destination_detail")
                        }
                    )
                }

                // 3. Trips
                composable(WLNavTab.TRIPS.route) {
                    TripsScreen(
                        viewModel = viewModel,
                        onExploreClick = {
                            navController.navigate(WLNavTab.EXPLORE.route)
                        },
                        onTripDetailsClick = { destId ->
                            viewModel.selectDestinationById(destId)
                            navController.navigate("destination_detail")
                        }
                    )
                }

                // 4. Offers
                composable(WLNavTab.OFFERS.route) {
                    OffersScreen(
                        viewModel = viewModel,
                        onBookPackage = { destId ->
                            viewModel.selectDestinationById(destId)
                            navController.navigate("destination_detail")
                        }
                    )
                }

                // 5. Profile
                composable(WLNavTab.PROFILE.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = {
                            if (!navController.popBackStack()) {
                                navController.navigate(WLNavTab.HOME.route) {
                                    popUpTo(WLNavTab.HOME.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route)
                        },
                        onNavigateToAI = {
                            navController.navigate("ai_assistant")
                        },
                        onNavigateToExplore = {
                            navController.navigate(WLNavTab.EXPLORE.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate("settings")
                        },
                        onNavigateToAuth = {
                            navController.navigate("auth")
                        },
                        onNavigateToWallet = {
                            navController.navigate("wallet")
                        },
                        onNavigateToSupport = {
                            navController.navigate("customer_support")
                        }
                    )
                }

                // 6. Search
                composable("search") {
                    SearchScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onDestinationSelect = { destId ->
                            viewModel.selectDestinationById(destId)
                            navController.navigate("destination_detail")
                        }
                    )
                }

                // 7. Destination Detail
                composable("destination_detail") {
                    DestinationDetailScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookNow = {
                            viewModel.requireAuthBeforeBooking("Please sign in to continue with your booking.") {
                                navController.navigate("booking_traveller")
                            }
                        }
                    )
                }

                // 7b. Itinerary Screen
                composable("itinerary") {
                    ItineraryScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookPackage = {
                            viewModel.requireAuthBeforeBooking("Please sign in to continue with your booking.") {
                                navController.navigate("booking_traveller")
                            }
                        }
                    )
                }

                // 8. Booking Flow - Step 1: Traveller Details
                composable("booking_traveller") {
                    BookingTravellerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onContinueToPayment = { navController.navigate("booking_payment") }
                    )
                }

                // 9. Booking Flow - Step 2: Payment
                composable("booking_payment") {
                    BookingPaymentScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onPaymentSuccess = {
                            navController.navigate("booking_confirm") {
                                popUpTo("destination_detail")
                            }
                        }
                    )
                }

                // 10. Booking Flow - Step 3: Confirmation
                composable("booking_confirm") {
                    BookingConfirmationScreen(
                        viewModel = viewModel,
                        onViewBookingDetails = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        },
                        onExploreMore = {
                            navController.navigate(WLNavTab.EXPLORE.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 11. Hotels Booking (Image 2)
                composable("hotels") {
                    HotelBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToHotelDetail = { navController.navigate("hotel_detail") }
                    )
                }

                // 11b. Hotel Detail (Screen 7)
                composable("hotel_detail") {
                    HotelDetailScreen(
                        onBack = { navController.popBackStack() },
                        onBookRoom = { room, price ->
                            viewModel.updateTravellerDetails(
                                fullName = viewModel.bookingForm.value.fullName,
                                email = viewModel.bookingForm.value.email,
                                phone = viewModel.bookingForm.value.phone,
                                adults = viewModel.bookingForm.value.adults,
                                children = viewModel.bookingForm.value.children,
                                specialRequests = "Selected Room: $room (₹$price/night)"
                            )
                            viewModel.requireAuthBeforeBooking("Please sign in to continue with your booking.") {
                                navController.navigate("booking_traveller")
                            }
                        }
                    )
                }

                // 11c. Brand Showcase / "Collect Moments Not Things" (Screen 12)
                composable("brand_showcase") {
                    BrandShowcaseScreen(
                        onExploreNow = {
                            navController.navigate(WLNavTab.EXPLORE.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                // 12. Flights Booking (Image 2 Left Panel)
                composable("flights") {
                    FlightBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookFlight = { deal ->
                            viewModel.requireAuthBeforeBooking("Please sign in to continue with your booking.") {
                                navController.navigate("booking_traveller")
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(WLNavTab.HOME.route) {
                                popUpTo(WLNavTab.HOME.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                                launchSingleTop = true
                            }
                        },
                        onNavigateToHotels = {
                            navController.navigate("hotels")
                        },
                        onNavigateToTrains = {
                            navController.navigate("trains")
                        }
                    )
                }

                // 12b. Trains Booking (Image 2 Middle Panel)
                composable("trains") {
                    TrainBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookTrain = {
                            viewModel.requireAuthBeforeBooking("Please sign in to continue with your booking.") {
                                navController.navigate("booking_traveller")
                            }
                        }
                    )
                }

                // 12c. Villas & Homestays Booking
                composable("villas") {
                    VillaBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12d. Holiday Packages Booking
                composable("holidays") {
                    HolidayBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12e. Buses Booking
                composable("buses") {
                    BusBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12f. Cabs Booking
                composable("cabs") {
                    CabBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12g. Tours & Activities Booking
                composable("tours") {
                    ToursBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12h. Visa Assistance Booking
                composable("visa") {
                    VisaBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12i. Cruise Booking
                composable("cruise") {
                    CruiseBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12j. Forex Card Booking
                composable("forex") {
                    ForexBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 12k. Travel Insurance Booking
                composable("insurance") {
                    InsuranceBookingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = {
                            navController.navigate(WLNavTab.TRIPS.route) {
                                popUpTo(WLNavTab.HOME.route)
                            }
                        }
                    )
                }

                // 13. AI Assistant
                composable("ai_assistant") {
                    AIAssistantScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                // 14. Onboarding Tour
                composable("onboarding") {
                    OnboardingScreen(
                        onFinishOnboarding = { navController.popBackStack() }
                    )
                }

                // 15. Auth & Login
                composable("auth") {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }

                // 16. App Settings
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onLaunchOnboarding = { navController.navigate("onboarding") }
                    )
                }

                // 17. Dedicated Notifications Screen
                composable("notifications") {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToTrips = { navController.navigate(WLNavTab.TRIPS.route) },
                        onNavigateToOffers = { navController.navigate(WLNavTab.OFFERS.route) }
                    )
                }

                // 18. Wallet Screen
                composable("wallet") {
                    WalletScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                // 19. Customer Support Screen
                composable("support") {
                    CustomerSupportScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToAIChat = { navController.navigate("ai_chat") }
                    )
                }

                // 20. My Reviews Screen
                composable("reviews") {
                    MyReviewsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                // 21. Refer and Earn Screen
                composable("refer") {
                    ReferAndEarnScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                // 22. Languages Screen
                composable("languages") {
                    LanguagesScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                // 23. Country and Currency Screen
                composable("country_currency") {
                    CountryCurrencyScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // Floating Bottom Navigation Bar
        AnimatedVisibility(
            visible = shouldShowBottomBar,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            WLBottomNavigation(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    if (tab == WLNavTab.HOME) {
                        if (currentRoute != WLNavTab.HOME.route) {
                            navController.navigate(WLNavTab.HOME.route) {
                                popUpTo(WLNavTab.HOME.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    } else if (tab.route != currentRoute) {
                        navController.navigate(tab.route) {
                            popUpTo(WLNavTab.HOME.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // Voice Conversation Dialog
        if (isVoiceActive) {
            WLVoiceDialog(
                onDismiss = { viewModel.setVoiceActive(false) },
                onVoiceQuery = { query ->
                    viewModel.sendAIMessage(query, AIAdventureMode.VOICE_LIVE_COMPANION)
                }
            )
        }

        // Right-to-Left Side Drawer
        WLSideDrawer(
            isOpen = isSideDrawerOpen,
            onDismiss = { isSideDrawerOpen = false },
            onNavigateToProfile = {
                isSideDrawerOpen = false
                navController.navigate(WLNavTab.PROFILE.route) {
                    launchSingleTop = true
                }
            },
            onNavigateToSettings = {
                isSideDrawerOpen = false
                navController.navigate("settings")
            },
            onNavigateToTrips = {
                isSideDrawerOpen = false
                navController.navigate(WLNavTab.TRIPS.route) {
                    launchSingleTop = true
                }
            },
            onNavigateToNotifications = {
                isSideDrawerOpen = false
                navController.navigate("notifications")
            },
            onNavigateToWallet = {
                isSideDrawerOpen = false
                navController.navigate("wallet")
            },
            onNavigateToLanguages = {
                isSideDrawerOpen = false
                navController.navigate("languages")
            },
            onNavigateToSupport = {
                isSideDrawerOpen = false
                navController.navigate("support")
            },
            onNavigateToReviews = {
                isSideDrawerOpen = false
                navController.navigate("reviews")
            },
            onNavigateToRefer = {
                isSideDrawerOpen = false
                navController.navigate("refer")
            },
            onNavigateToCountryCurrency = {
                isSideDrawerOpen = false
                navController.navigate("country_currency")
            },
            viewModel = viewModel
        )

        // Global Supabase Auth Bottom Sheet (Guards bookings & account access)
        val isAuthSheetOpen by viewModel.isAuthSheetOpen.collectAsState()
        val authPromptMessage by viewModel.authPromptMessage.collectAsState()

        if (isAuthSheetOpen) {
            com.example.ui.components.WonderLightAuthSheet(
                viewModel = viewModel,
                promptMessage = authPromptMessage,
                onDismiss = { viewModel.closeAuthSheet() }
            )
        }

        // Permission Request Dialog for Microphone and Notifications
        if (showPermissionsDialog) {
            WLPermissionsDialog(
                hasNotificationPermission = hasNotificationPerm,
                hasMicPermission = hasMicPerm,
                onRequestPermissions = { requestAllPermissions() },
                onDismiss = { showPermissionsDialog = false }
            )
        }
    }
}
