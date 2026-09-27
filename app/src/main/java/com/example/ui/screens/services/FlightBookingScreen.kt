package com.example.ui.screens.services

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.TripBooking
import com.example.ui.viewmodel.TravelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// DESIGN SYSTEM TOKENS
// -------------------------------------------------------------
private val WLRoyalBlue = Color(0xFF146EF5)
private val WLRoyalBlueDark = Color(0xFF0C56C5)
private val WLNavyPrimary = Color(0xFF0F172A)
private val WLNavySecondary = Color(0xFF334155)
private val WLNavyMuted = Color(0xFF64748B)
private val WLLightBlueSurface = Color(0xFFF0F6FF)
private val WLLightBlueSecondary = Color(0xFFE0EDFF)
private val WLScreenBg = Color(0xFFF8FAFC)
private val WLBorderLight = Color(0xFFE2E8F0)
private val WLEmeraldSuccess = Color(0xFF10B981)

// -------------------------------------------------------------
// DATA MODELS
// -------------------------------------------------------------
enum class FlightBookingStage {
    SEARCH,
    RESULTS,
    PASSENGER,
    PAYMENT,
    CONFIRMATION
}

data class FlightDeal(
    val id: String = "fl_deal",
    val airline: String = "IndiGo",
    val airlineColor: Color = Color(0xFF0052CC),
    val fromCode: String = "DEL",
    val toCode: String = "BOM",
    val departureTime: String = "08:10",
    val arrivalTime: String = "10:25",
    val duration: String = "2h 15m",
    val stops: String = "Non-stop",
    val price: Int = 5499
)

data class TravelLocationItem(
    val code: String,
    val city: String,
    val stationOrAirport: String,
    val isInternational: Boolean = false
)

data class FlightCardData(
    val id: String,
    val airline: String,
    val flightNumber: String,
    val airlineColor: Color,
    val fromCode: String,
    val toCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val stops: String,
    val isNonStop: Boolean,
    val price: Int,
    val cabinBaggage: String = "7 kg",
    val checkInBaggage: String = "15 kg",
    val departureAirport: String = "DEL • T3",
    val arrivalAirport: String = "BOM • T2",
    val baseFare: Int = 4650,
    val taxesAndFees: Int = 849,
    val isTrain: Boolean = false
)

data class PopularRouteData(
    val fromCity: String,
    val toCity: String,
    val fromCode: String,
    val toCode: String,
    val price: String,
    val duration: String
)

data class RecentSearchData(
    val fromCode: String,
    val toCode: String,
    val fromCity: String,
    val toCity: String,
    val date: String,
    val passengers: String,
    val mode: String = "Flight"
)

// -------------------------------------------------------------
// MAIN COMPOSABLE: FlightBookingScreen
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onBookFlight: (FlightDeal) -> Unit = {},
    onNavigateToHome: () -> Unit = onBack,
    onNavigateToTrips: () -> Unit = {},
    onNavigateToHotels: () -> Unit = {},
    onNavigateToTrains: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Current navigation stage within booking flow
    var currentStage by remember { mutableStateOf(FlightBookingStage.SEARCH) }

    // Booking mode: FLIGHTS or TRAINS
    var selectedBookingMode by remember { mutableStateOf("Flights") } // "Flights" or "Trains"

    // Search state
    var tripType by remember { mutableStateOf("One Way") } // One Way, Round Trip, Multi-City / Tatkal
    var selectedCabin by remember { mutableStateOf("Economy") }
    var adultCount by remember { mutableIntStateOf(1) }
    var childCount by remember { mutableIntStateOf(0) }
    var infantCount by remember { mutableIntStateOf(0) }

    // Origin & Destination
    var fromCity by remember { mutableStateOf("New Delhi") }
    var fromAirport by remember { mutableStateOf("DEL • Indira Gandhi International") }
    var fromCode by remember { mutableStateOf("DEL") }

    var toCity by remember { mutableStateOf("Mumbai") }
    var toAirport by remember { mutableStateOf("BOM • Chhatrapati Shivaji Maharaj International") }
    var toCode by remember { mutableStateOf("BOM") }

    var departureDate by remember { mutableStateOf("15 Oct 2026") }
    var departureDay by remember { mutableStateOf("Thursday") }
    var returnDate by remember { mutableStateOf("18 Oct 2026") }
    var returnDay by remember { mutableStateOf("Sunday") }

    // Filter & sort state
    var selectedStopsFilter by remember { mutableStateOf("All") }
    var selectedAirlineFilter by remember { mutableStateOf("All") }
    var selectedSortOption by remember { mutableStateOf("Recommended") }

    // Modal bottom sheet triggers
    var showFareDetailsSheet by remember { mutableStateOf(false) }
    var showLocationPickerSheet by remember { mutableStateOf(false) }
    var isPickingFromLocation by remember { mutableStateOf(true) }
    var showPassengerCabinSheet by remember { mutableStateOf(false) }
    var showDatePickerSheet by remember { mutableStateOf(false) }
    var isPickingDepartureDate by remember { mutableStateOf(true) }
    var showBoardingPassDialog by remember { mutableStateOf(false) }
    var showDealsDialog by remember { mutableStateOf(false) }

    // Selected fare tier
    var selectedFareTier by remember { mutableStateOf("Saver") }

    val currentUser by viewModel.currentUser.collectAsState()
    val defaultFirst = currentUser?.fullName?.split(" ")?.firstOrNull() ?: ""
    val defaultLast = currentUser?.fullName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
    val defaultEmail = currentUser?.email ?: ""
    val defaultPhone = currentUser?.phone ?: ""

    // Passenger details
    var passengerTitle by remember { mutableStateOf("Mr") }
    var passengerFirstName by remember(currentUser) { mutableStateOf(defaultFirst) }
    var passengerLastName by remember(currentUser) { mutableStateOf(defaultLast) }
    var passengerGender by remember { mutableStateOf("Select") }
    var passengerDob by remember { mutableStateOf("") }
    var passengerNationality by remember { mutableStateOf("Indian") }
    var passengerPassportNumber by remember { mutableStateOf("") }
    var passengerPassportExpiry by remember { mutableStateOf("") }
    var passengerEmail by remember(currentUser) { mutableStateOf(defaultEmail) }
    var passengerMobile by remember(currentUser) { mutableStateOf(defaultPhone) }
    var savePassengerDetails by remember { mutableStateOf(true) }

    // Payment State
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var upiIdInput by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8912") }
    var cardExpiry by remember { mutableStateOf("08/28") }
    var cardCvv by remember { mutableStateOf("789") }
    var promoCodeInput by remember { mutableStateOf("FLYMORE500") }
    var promoApplied by remember { mutableStateOf(true) }
    var isPaymentProcessing by remember { mutableStateOf(false) }

    // Confirmed booking state
    var confirmedPnr by remember { mutableStateOf("WL9482XF") }
    var confirmedSeat by remember { mutableStateOf("12A (Window)") }

    // Preset list of popular travel hubs (Airports and Railway Stations)
    val flightHubs = remember {
        listOf(
            TravelLocationItem("DEL", "New Delhi", "Indira Gandhi International Airport"),
            TravelLocationItem("BOM", "Mumbai", "Chhatrapati Shivaji Maharaj International"),
            TravelLocationItem("BLR", "Bengaluru", "Kempegowda International Airport"),
            TravelLocationItem("GOI", "Goa", "Dabolim / Manohar MOPA International"),
            TravelLocationItem("HYD", "Hyderabad", "Rajiv Gandhi International Airport"),
            TravelLocationItem("CCU", "Kolkata", "Netaji Subhash Chandra Bose International"),
            TravelLocationItem("MAA", "Chennai", "Chennai International Airport"),
            TravelLocationItem("DXB", "Dubai", "Dubai International Airport (DXB)", isInternational = true),
            TravelLocationItem("SIN", "Singapore", "Singapore Changi Airport (SIN)", isInternational = true),
            TravelLocationItem("SXR", "Srinagar", "Sheikh ul-Alam International Airport")
        )
    }

    val trainHubs = remember {
        listOf(
            TravelLocationItem("NDLS", "New Delhi", "New Delhi Railway Station (NDLS)"),
            TravelLocationItem("CSMT", "Mumbai", "Chhatrapati Shivaji Maharaj Terminus (CSMT)"),
            TravelLocationItem("MMCT", "Mumbai", "Mumbai Central Railway Station (MMCT)"),
            TravelLocationItem("SBC", "Bengaluru", "KSR Bengaluru City Junction (SBC)"),
            TravelLocationItem("MAS", "Chennai", "Puratchi Thalaivar Dr. MGR Central (MAS)"),
            TravelLocationItem("HWH", "Kolkata", "Howrah Railway Station (HWH)"),
            TravelLocationItem("JP", "Jaipur", "Jaipur Junction (JP)"),
            TravelLocationItem("MAO", "Goa", "Madgaon Junction (MAO)"),
            TravelLocationItem("ADI", "Ahmedabad", "Ahmedabad Junction (ADI)"),
            TravelLocationItem("PNBE", "Patna", "Patna Junction (PNBE)")
        )
    }

    // Dynamic Flights based on customized From / To
    val customizedFlights = remember(fromCode, toCode, fromCity, toCity) {
        listOf(
            FlightCardData(
                id = "fl_1_${fromCode}_${toCode}",
                airline = "IndiGo",
                flightNumber = "6E 5214",
                airlineColor = Color(0xFF0052CC),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "08:10",
                arrivalTime = "10:25",
                duration = "2h 15m",
                stops = "Non-stop",
                isNonStop = true,
                price = 5499,
                departureAirport = "$fromCode • Terminal 3",
                arrivalAirport = "$toCode • Terminal 2"
            ),
            FlightCardData(
                id = "fl_2_${fromCode}_${toCode}",
                airline = "Air India",
                flightNumber = "AI 887",
                airlineColor = Color(0xFFD32F2F),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "07:00",
                arrivalTime = "09:15",
                duration = "2h 15m",
                stops = "Non-stop",
                isNonStop = true,
                price = 5850,
                departureAirport = "$fromCode • Terminal 3",
                arrivalAirport = "$toCode • Terminal 2"
            ),
            FlightCardData(
                id = "fl_3_${fromCode}_${toCode}",
                airline = "Vistara",
                flightNumber = "UK 995",
                airlineColor = Color(0xFF512DA8),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "10:20",
                arrivalTime = "12:35",
                duration = "2h 15m",
                stops = "Non-stop",
                isNonStop = true,
                price = 6450,
                departureAirport = "$fromCode • Terminal 3",
                arrivalAirport = "$toCode • Terminal 2"
            ),
            FlightCardData(
                id = "fl_4_${fromCode}_${toCode}",
                airline = "Akasa Air",
                flightNumber = "QP 1332",
                airlineColor = Color(0xFFFF6600),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "14:15",
                arrivalTime = "16:30",
                duration = "2h 15m",
                stops = "Non-stop",
                isNonStop = true,
                price = 4899,
                departureAirport = "$fromCode • Terminal 1",
                arrivalAirport = "$toCode • Terminal 1"
            ),
            FlightCardData(
                id = "fl_5_${fromCode}_${toCode}",
                airline = "SpiceJet",
                flightNumber = "SG 8169",
                airlineColor = Color(0xFFC2185B),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "19:45",
                arrivalTime = "22:10",
                duration = "2h 25m",
                stops = "Non-stop",
                isNonStop = true,
                price = 4699,
                departureAirport = "$fromCode • Terminal 1D",
                arrivalAirport = "$toCode • Terminal 1"
            )
        )
    }

    // Dynamic Trains based on customized From / To
    val customizedTrains = remember(fromCity, toCity, fromCode, toCode) {
        listOf(
            FlightCardData(
                id = "tr_1_${fromCode}_${toCode}",
                airline = "Vande Bharat Express",
                flightNumber = "20601",
                airlineColor = Color(0xFF08766E),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "06:00",
                arrivalTime = "14:15",
                duration = "8h 15m",
                stops = "Non-stop",
                isNonStop = true,
                price = 1850,
                cabinBaggage = "40 kg",
                checkInBaggage = "Free",
                departureAirport = "$fromCity Junction",
                arrivalAirport = "$toCity Central",
                baseFare = 1600,
                taxesAndFees = 250,
                isTrain = true
            ),
            FlightCardData(
                id = "tr_2_${fromCode}_${toCode}",
                airline = "Rajdhani Superfast Express",
                flightNumber = "12952",
                airlineColor = Color(0xFFC2185B),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "16:55",
                arrivalTime = "08:35",
                duration = "15h 40m",
                stops = "3 Stops",
                isNonStop = false,
                price = 2450,
                cabinBaggage = "50 kg",
                checkInBaggage = "Free",
                departureAirport = "$fromCity Junction",
                arrivalAirport = "$toCity Terminus",
                baseFare = 2100,
                taxesAndFees = 350,
                isTrain = true
            ),
            FlightCardData(
                id = "tr_3_${fromCode}_${toCode}",
                airline = "Shatabdi Express",
                flightNumber = "12004",
                airlineColor = Color(0xFF146EF5),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "06:10",
                arrivalTime = "12:40",
                duration = "6h 30m",
                stops = "1 Stop",
                isNonStop = false,
                price = 1150,
                cabinBaggage = "40 kg",
                checkInBaggage = "Free",
                departureAirport = "$fromCity Junction",
                arrivalAirport = "$toCity Junction",
                baseFare = 980,
                taxesAndFees = 170,
                isTrain = true
            ),
            FlightCardData(
                id = "tr_4_${fromCode}_${toCode}",
                airline = "Duronto Express",
                flightNumber = "12268",
                airlineColor = Color(0xFFF59E0B),
                fromCode = fromCode,
                toCode = toCode,
                departureTime = "23:25",
                arrivalTime = "14:20",
                duration = "14h 55m",
                stops = "Non-stop",
                isNonStop = true,
                price = 1920,
                cabinBaggage = "40 kg",
                checkInBaggage = "Free",
                departureAirport = "$fromCity Terminal",
                arrivalAirport = "$toCity Central",
                baseFare = 1700,
                taxesAndFees = 220,
                isTrain = true
            )
        )
    }

    // Live flights from Supabase & Cloud Admin
    val liveFlightsState by viewModel.flights.collectAsState()
    val liveFlightCards = remember(liveFlightsState, fromCode, toCode) {
        liveFlightsState.map { fl ->
            val color = when {
                fl.airline.contains("IndiGo", ignoreCase = true) -> Color(0xFF0052CC)
                fl.airline.contains("Air India", ignoreCase = true) -> Color(0xFFD32F2F)
                fl.airline.contains("Vistara", ignoreCase = true) -> Color(0xFF4A148C)
                fl.airline.contains("SpiceJet", ignoreCase = true) -> Color(0xFFE65100)
                fl.airline.contains("Akasa", ignoreCase = true) -> Color(0xFFFF6F00)
                else -> Color(0xFF0D9488)
            }
            FlightCardData(
                id = fl.id,
                airline = fl.airline,
                flightNumber = fl.flightNumber,
                airlineColor = color,
                fromCode = fromCode,
                toCode = toCode,
                departureTime = fl.departureTime,
                arrivalTime = fl.arrivalTime,
                duration = fl.duration,
                stops = fl.stops,
                isNonStop = fl.stops.contains("Non", ignoreCase = true),
                price = fl.price,
                departureAirport = "${fl.origin} Airport",
                arrivalAirport = "${fl.destination} Airport",
                baseFare = (fl.price * 0.85).toInt(),
                taxesAndFees = (fl.price * 0.15).toInt()
            )
        }
    }

    // Current active travel list (merges live Supabase flights + custom routes)
    val currentTravelList = if (selectedBookingMode == "Trains") customizedTrains else (liveFlightCards + customizedFlights)

    // Selected flight or train
    var selectedFlight by remember(currentTravelList) { mutableStateOf(currentTravelList.firstOrNull() ?: customizedFlights.first()) }

    // Filter & Sort Logic
    val filteredTravelList = remember(currentTravelList, selectedStopsFilter, selectedAirlineFilter, selectedSortOption) {
        var list = currentTravelList
        if (selectedStopsFilter == "Non-stop") {
            list = list.filter { it.isNonStop }
        } else if (selectedStopsFilter == "1 Stop") {
            list = list.filter { !it.isNonStop }
        }

        if (selectedAirlineFilter != "All") {
            list = list.filter { it.airline.contains(selectedAirlineFilter, ignoreCase = true) }
        }

        when (selectedSortOption) {
            "Cheapest" -> list.sortedBy { it.price }
            "Fastest" -> list.sortedBy { it.duration }
            "Earliest" -> list.sortedBy { it.departureTime }
            else -> list
        }
    }

    // Popular Routes List
    val popularRoutes = remember {
        listOf(
            PopularRouteData("Delhi", "Mumbai", "DEL", "BOM", "₹4,299", "2h 10m"),
            PopularRouteData("Mumbai", "Dubai", "BOM", "DXB", "₹12,499", "3h 45m"),
            PopularRouteData("Bengaluru", "Delhi", "BLR", "DEL", "₹5,199", "2h 40m"),
            PopularRouteData("Delhi", "Goa", "DEL", "GOI", "₹4,899", "2h 25m")
        )
    }

    // Recent Searches List
    val recentSearches = remember {
        mutableListOf(
            RecentSearchData("DEL", "BOM", "New Delhi", "Mumbai", "15 Oct", "1 Passenger", "Flight"),
            RecentSearchData("NDLS", "CSMT", "New Delhi", "Mumbai", "18 Oct", "2 Passengers", "Train")
        )
    }

    val totalPassengers = adultCount + childCount + infantCount
    val passengerSummaryText = "$totalPassengers Passenger${if (totalPassengers > 1) "s" else ""}"

    // Total Fare Calculation
    val discountAmount = if (promoApplied) 500 else 0
    val tierMarkup = when (selectedFareTier) {
        "Flexi Plus" -> 800
        "Super 6E" -> 1900
        else -> 0
    }
    val finalPayableFare = (selectedFlight.price + tierMarkup - discountAmount).coerceAtLeast(499)

    // MAIN CONTAINER - NO BOTTOM NAV BAR
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WLScreenBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP HEADER (Properly Positioned - No excessive gap)
            FlightTopHeader(
                stage = currentStage,
                bookingMode = selectedBookingMode,
                toCity = toCity,
                onBackClicked = {
                    when (currentStage) {
                        FlightBookingStage.SEARCH -> onBack()
                        FlightBookingStage.RESULTS -> currentStage = FlightBookingStage.SEARCH
                        FlightBookingStage.PASSENGER -> currentStage = FlightBookingStage.RESULTS
                        FlightBookingStage.PAYMENT -> currentStage = FlightBookingStage.PASSENGER
                        FlightBookingStage.CONFIRMATION -> currentStage = FlightBookingStage.SEARCH
                    }
                },
                onNotificationClick = {
                    Toast.makeText(context, "Price alerts active for $fromCity to $toCity", Toast.LENGTH_SHORT).show()
                }
            )

            // BODY CONTENT ANIMATION BY STAGE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStage,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220)) + slideInHorizontally { width -> width / 3 })
                            .togetherWith(fadeOut(animationSpec = tween(180)) + slideOutHorizontally { width -> -width / 3 })
                    },
                    label = "BookingStageTransition"
                ) { stage ->
                    when (stage) {
                        FlightBookingStage.SEARCH -> {
                            FlightSearchScreen(
                                bookingMode = selectedBookingMode,
                                onBookingModeChange = { mode ->
                                    selectedBookingMode = mode
                                    if (mode == "Trains") {
                                        if (fromCode == "DEL") {
                                            fromCode = "NDLS"
                                            fromAirport = "NDLS • New Delhi Railway Station"
                                        }
                                        if (toCode == "BOM") {
                                            toCode = "CSMT"
                                            toAirport = "CSMT • Mumbai Central Terminus"
                                        }
                                    } else {
                                        if (fromCode == "NDLS") {
                                            fromCode = "DEL"
                                            fromAirport = "DEL • Indira Gandhi International"
                                        }
                                        if (toCode == "CSMT") {
                                            toCode = "BOM"
                                            toAirport = "BOM • Chhatrapati Shivaji Maharaj International"
                                        }
                                    }
                                },
                                tripType = tripType,
                                onTripTypeChanged = { tripType = it },
                                selectedCabin = selectedCabin,
                                passengerSummaryText = passengerSummaryText,
                                onOpenCabinPassengerSheet = { showPassengerCabinSheet = true },
                                fromCity = fromCity,
                                fromAirport = fromAirport,
                                fromCode = fromCode,
                                onPickFromLocation = {
                                    isPickingFromLocation = true
                                    showLocationPickerSheet = true
                                },
                                toCity = toCity,
                                toAirport = toAirport,
                                toCode = toCode,
                                onPickToLocation = {
                                    isPickingFromLocation = false
                                    showLocationPickerSheet = true
                                },
                                onSwapCities = {
                                    val tempCity = fromCity
                                    val tempAirport = fromAirport
                                    val tempCode = fromCode
                                    fromCity = toCity
                                    fromAirport = toAirport
                                    fromCode = toCode
                                    toCity = tempCity
                                    toAirport = tempAirport
                                    toCode = tempCode
                                },
                                departureDate = departureDate,
                                departureDay = departureDay,
                                onPickDepartureDate = {
                                    isPickingDepartureDate = true
                                    showDatePickerSheet = true
                                },
                                returnDate = returnDate,
                                returnDay = returnDay,
                                onPickReturnDate = {
                                    if (tripType == "One Way") {
                                        tripType = "Round Trip"
                                    }
                                    isPickingDepartureDate = false
                                    showDatePickerSheet = true
                                },
                                onSearch = {
                                    currentStage = FlightBookingStage.RESULTS
                                },
                                popularRoutes = popularRoutes,
                                onSelectPopularRoute = { route ->
                                    fromCity = route.fromCity
                                    toCity = route.toCity
                                    fromCode = route.fromCode
                                    toCode = route.toCode
                                    fromAirport = "${route.fromCode} • Terminal"
                                    toAirport = "${route.toCode} • Terminal"
                                },
                                availableTravelItems = currentTravelList,
                                onSelectTravelCard = { item ->
                                    selectedFlight = item
                                    showFareDetailsSheet = true
                                },
                                recentSearches = recentSearches,
                                onRestoreRecentSearch = { search ->
                                    fromCode = search.fromCode
                                    toCode = search.toCode
                                    fromCity = search.fromCity
                                    toCity = search.toCity
                                    fromAirport = "${search.fromCode} • Terminal"
                                    toAirport = "${search.toCode} • Terminal"
                                    departureDate = search.date + " 2026"
                                },
                                onExploreDeals = {
                                    showDealsDialog = true
                                }
                            )
                        }

                        FlightBookingStage.RESULTS -> {
                            FlightResultsScreen(
                                bookingMode = selectedBookingMode,
                                toCity = toCity,
                                departureDate = departureDate,
                                passengerSummaryText = passengerSummaryText,
                                cabinClass = selectedCabin,
                                onEditSearch = { currentStage = FlightBookingStage.SEARCH },
                                selectedStops = selectedStopsFilter,
                                onStopsSelected = { selectedStopsFilter = it },
                                selectedAirline = selectedAirlineFilter,
                                onAirlineSelected = { selectedAirlineFilter = it },
                                selectedSort = selectedSortOption,
                                onSortSelected = { selectedSortOption = it },
                                flights = filteredTravelList,
                                onSelectFlight = { flight ->
                                    selectedFlight = flight
                                    showFareDetailsSheet = true
                                }
                            )
                        }

                        FlightBookingStage.PASSENGER -> {
                            FlightPassengerDetailsScreen(
                                flight = selectedFlight,
                                title = passengerTitle,
                                onTitleChange = { passengerTitle = it },
                                firstName = passengerFirstName,
                                onFirstNameChange = { passengerFirstName = it },
                                lastName = passengerLastName,
                                onLastNameChange = { passengerLastName = it },
                                gender = passengerGender,
                                onGenderChange = { passengerGender = it },
                                dob = passengerDob,
                                onDobChange = { passengerDob = it },
                                nationality = passengerNationality,
                                onNationalityChange = { passengerNationality = it },
                                passportNumber = passengerPassportNumber,
                                onPassportNumberChange = { passengerPassportNumber = it },
                                passportExpiry = passengerPassportExpiry,
                                onPassportExpiryChange = { passengerPassportExpiry = it },
                                email = passengerEmail,
                                onEmailChange = { passengerEmail = it },
                                mobile = passengerMobile,
                                onMobileChange = { passengerMobile = it },
                                saveDetails = savePassengerDetails,
                                onSaveDetailsChange = { savePassengerDetails = it },
                                isInternational = selectedFlight.toCode == "DXB" || selectedFlight.toCode == "SIN",
                                onProceedToPayment = {
                                    if (passengerFirstName.isBlank() || passengerLastName.isBlank() || passengerMobile.isBlank()) {
                                        Toast.makeText(context, "Please enter all required passenger details", Toast.LENGTH_SHORT).show()
                                    } else {
                                        currentStage = FlightBookingStage.PAYMENT
                                    }
                                }
                            )
                        }

                        FlightBookingStage.PAYMENT -> {
                            FlightPaymentScreen(
                                flight = selectedFlight,
                                passengerName = "$passengerTitle $passengerFirstName $passengerLastName",
                                seat = confirmedSeat,
                                fareTier = selectedFareTier,
                                baseFare = selectedFlight.baseFare + tierMarkup,
                                taxes = selectedFlight.taxesAndFees,
                                discount = discountAmount,
                                finalTotal = finalPayableFare,
                                promoCode = promoCodeInput,
                                onPromoCodeChange = { promoCodeInput = it },
                                isPromoApplied = promoApplied,
                                onApplyPromo = {
                                    if (promoCodeInput.trim().uppercase() in listOf("FLYMORE500", "WONDERLIGHT", "ADVENTURE")) {
                                        promoApplied = true
                                        Toast.makeText(context, "Promo code applied! Saved ₹500", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Invalid promo code. Try FLYMORE500", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                selectedPaymentMethod = selectedPaymentMethod,
                                onSelectPaymentMethod = { selectedPaymentMethod = it },
                                selectedUpiApp = selectedUpiApp,
                                onSelectUpiApp = { selectedUpiApp = it },
                                upiId = upiIdInput,
                                onUpiIdChange = { upiIdInput = it },
                                cardNumber = cardNumber,
                                onCardNumberChange = { cardNumber = it },
                                cardExpiry = cardExpiry,
                                onCardExpiryChange = { cardExpiry = it },
                                cardCvv = cardCvv,
                                onCardCvvChange = { cardCvv = it },
                                isProcessing = isPaymentProcessing,
                                onPayClicked = {
                                    viewModel.requireAuthBeforeBooking("Sign in to WonderLight to book your tickets.") {
                                        isPaymentProcessing = true
                                        scope.launch {
                                            delay(1500)
                                            isPaymentProcessing = false
                                            confirmedPnr = (if (selectedFlight.isTrain) "PNR" else "WL") + (100000..999999).random()
                                            currentStage = FlightBookingStage.CONFIRMATION
                                        }
                                    }
                                }
                            )
                        }

                        FlightBookingStage.CONFIRMATION -> {
                            FlightBookingConfirmationScreen(
                                pnr = confirmedPnr,
                                flight = selectedFlight,
                                date = departureDate,
                                passengerName = "$passengerTitle $passengerFirstName $passengerLastName",
                                seat = confirmedSeat,
                                onCopyPnr = {
                                    clipboardManager.setText(AnnotatedString(confirmedPnr))
                                    Toast.makeText(context, "PNR copied to clipboard: $confirmedPnr", Toast.LENGTH_SHORT).show()
                                },
                                onViewTicket = {
                                    showBoardingPassDialog = true
                                },
                                onDownloadTicket = {
                                    Toast.makeText(context, "E-Ticket PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
                                },
                                onAddToTrips = {
                                    val newTrip = TripBooking(
                                        bookingId = confirmedPnr,
                                        destinationId = selectedFlight.toCode.lowercase(),
                                        destinationTitle = if (selectedFlight.isTrain) {
                                            "Train to $toCity (${selectedFlight.airline})"
                                        } else {
                                            "Flight to ${selectedFlight.toCode} (${selectedFlight.airline})"
                                        },
                                        duration = selectedFlight.duration,
                                        travellerName = "$passengerTitle $passengerFirstName $passengerLastName",
                                        email = passengerEmail,
                                        phone = passengerMobile,
                                        adults = adultCount,
                                        children = childCount,
                                        paymentMethod = selectedPaymentMethod,
                                        totalAmount = finalPayableFare,
                                        bookingDate = departureDate,
                                        status = "Confirmed",
                                        imageRes = if (selectedFlight.isTrain) R.drawable.hero_train_service else R.drawable.hero_flight_service
                                    )
                                    viewModel.createDirectBooking(newTrip) {
                                        Toast.makeText(context, "Successfully added to My Trips!", Toast.LENGTH_SHORT).show()
                                        onNavigateToTrips()
                                    }
                                },
                                onExploreMore = {
                                    currentStage = FlightBookingStage.SEARCH
                                }
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // BOTTOM SHEET: FARE DETAILS MODAL
        // -------------------------------------------------------------
        if (showFareDetailsSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showFareDetailsSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                FareDetailsBottomSheetContent(
                    flight = selectedFlight,
                    selectedTier = selectedFareTier,
                    onTierSelected = { selectedFareTier = it },
                    onContinue = {
                        showFareDetailsSheet = false
                        currentStage = FlightBookingStage.PASSENGER
                    }
                )
            }
        }

        // -------------------------------------------------------------
        // BOTTOM SHEET: LOCATION PICKER (Airports or Stations)
        // -------------------------------------------------------------
        if (showLocationPickerSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showLocationPickerSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                AirportPickerBottomSheet(
                    isOrigin = isPickingFromLocation,
                    isTrain = selectedBookingMode == "Trains",
                    locations = if (selectedBookingMode == "Trains") trainHubs else flightHubs,
                    onLocationSelected = { loc ->
                        if (isPickingFromLocation) {
                            fromCity = loc.city
                            fromAirport = "${loc.code} • ${loc.stationOrAirport}"
                            fromCode = loc.code
                        } else {
                            toCity = loc.city
                            toAirport = "${loc.code} • ${loc.stationOrAirport}"
                            toCode = loc.code
                        }
                        showLocationPickerSheet = false
                    }
                )
            }
        }

        // -------------------------------------------------------------
        // BOTTOM SHEET: PASSENGER & CABIN SELECTOR
        // -------------------------------------------------------------
        if (showPassengerCabinSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showPassengerCabinSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                CabinPassengerSelectorSheet(
                    adults = adultCount,
                    onAdultsChange = { adultCount = it },
                    children = childCount,
                    onChildrenChange = { childCount = it },
                    infants = infantCount,
                    onInfantsChange = { infantCount = it },
                    cabinClass = selectedCabin,
                    onCabinClassChange = { selectedCabin = it },
                    onApply = { showPassengerCabinSheet = false }
                )
            }
        }

        // -------------------------------------------------------------
        // BOTTOM SHEET: DATE PICKER
        // -------------------------------------------------------------
        if (showDatePickerSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showDatePickerSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                FlightDatePickerSheet(
                    isDeparture = isPickingDepartureDate,
                    currentDate = if (isPickingDepartureDate) departureDate else returnDate,
                    onDateSelected = { date, day ->
                        if (isPickingDepartureDate) {
                            departureDate = date
                            departureDay = day
                        } else {
                            returnDate = date
                            returnDay = day
                        }
                        showDatePickerSheet = false
                    }
                )
            }
        }

        // -------------------------------------------------------------
        // DIALOG: DIGITAL BOARDING PASS
        // -------------------------------------------------------------
        if (showBoardingPassDialog) {
            Dialog(onDismissRequest = { showBoardingPassDialog = false }) {
                DigitalBoardingPassCard(
                    flight = selectedFlight,
                    pnr = confirmedPnr,
                    passengerName = "$passengerTitle $passengerFirstName $passengerLastName",
                    seat = confirmedSeat,
                    departureDate = departureDate,
                    onClose = { showBoardingPassDialog = false }
                )
            }
        }

        // -------------------------------------------------------------
        // DIALOG: PROMOTIONAL DEALS
        // -------------------------------------------------------------
        if (showDealsDialog) {
            Dialog(onDismissRequest = { showDealsDialog = false }) {
                PromotionalDealsModal(
                    onClose = { showDealsDialog = false },
                    onSelectDeal = { code ->
                        promoCodeInput = code
                        promoApplied = true
                        showDealsDialog = false
                        Toast.makeText(context, "Deal $code applied to your booking!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: TOP HEADER (Properly Positioned - Clean, No Double Gap)
// -------------------------------------------------------------
@Composable
private fun FlightTopHeader(
    stage: FlightBookingStage,
    bookingMode: String,
    toCity: String,
    onBackClicked: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val titleText = when (stage) {
        FlightBookingStage.SEARCH -> "Book $bookingMode"
        FlightBookingStage.RESULTS -> "$bookingMode to $toCity"
        FlightBookingStage.PASSENGER -> "Passenger Details"
        FlightBookingStage.PAYMENT -> "Secure Payment"
        FlightBookingStage.CONFIRMATION -> "Booking Confirmed"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button with soft rounded touch target
            Surface(
                shape = CircleShape,
                color = WLLightBlueSurface,
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier.size(38.dp).testTag("flight_header_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WLNavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center: Screen Title & Subtle Location Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLNavyPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = WLRoyalBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "WonderLight Travel • INR (₹)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = WLNavyMuted
                    )
                }
            }

            // Right: Notification / Profile Icon
            Surface(
                shape = CircleShape,
                color = WLLightBlueSurface,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Alerts",
                            tint = WLRoyalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 1: FLIGHT & TRAIN SEARCH SCREEN
// -------------------------------------------------------------
@Composable
private fun FlightSearchScreen(
    bookingMode: String,
    onBookingModeChange: (String) -> Unit,
    tripType: String,
    onTripTypeChanged: (String) -> Unit,
    selectedCabin: String,
    passengerSummaryText: String,
    onOpenCabinPassengerSheet: () -> Unit,
    fromCity: String,
    fromAirport: String,
    fromCode: String,
    onPickFromLocation: () -> Unit,
    toCity: String,
    toAirport: String,
    toCode: String,
    onPickToLocation: () -> Unit,
    onSwapCities: () -> Unit,
    departureDate: String,
    departureDay: String,
    onPickDepartureDate: () -> Unit,
    returnDate: String,
    returnDay: String,
    onPickReturnDate: () -> Unit,
    onSearch: () -> Unit,
    popularRoutes: List<PopularRouteData>,
    onSelectPopularRoute: (PopularRouteData) -> Unit,
    availableTravelItems: List<FlightCardData>,
    onSelectTravelCard: (FlightCardData) -> Unit,
    recentSearches: List<RecentSearchData>,
    onRestoreRecentSearch: (RecentSearchData) -> Unit,
    onExploreDeals: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flight_search_scroll"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. FLIGHT VS TRAIN SERVICE MODE SWITCHER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modes = listOf("Flights" to Icons.Filled.Flight, "Trains" to Icons.Filled.Train)
                    modes.forEach { (mode, icon) ->
                        val isSelected = bookingMode == mode
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) WLRoyalBlue else Color.Transparent,
                            label = "BookingModeBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else WLNavySecondary,
                            label = "BookingModeText"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable { onBookingModeChange(mode) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mode,
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. TRIP TYPE SELECTOR (Segmented Control)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WLLightBlueSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tripOptions = if (bookingMode == "Trains") {
                        listOf("One Way", "Round Trip", "Tatkal")
                    } else {
                        listOf("One Way", "Round Trip", "Multi-City")
                    }

                    tripOptions.forEach { option ->
                        val isSelected = tripType == option
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) WLRoyalBlue else Color.Transparent,
                            label = "TripTypeBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else WLNavySecondary,
                            label = "TripTypeText"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(bgColor)
                                .clickable { onTripTypeChanged(option) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        // 3. CABIN & PASSENGERS COMPACT ROW
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(14.dp))
                    .clickable { onOpenCabinPassengerSheet() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WLLightBlueSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Passengers",
                            tint = WLRoyalBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (bookingMode == "Trains") "Sleeper / 3A • $passengerSummaryText" else "$selectedCabin • $passengerSummaryText",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLNavyPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Customize",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLRoyalBlue
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Select",
                        tint = WLRoyalBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. SEARCH FORM (From, Swap, To, Departure, Return)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = WLRoyalBlue.copy(alpha = 0.16f)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, WLBorderLight)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // ORIGIN & DESTINATION WITH FLOATING SWAP BUTTON
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. FROM CARD
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onPickFromLocation() }
                                    .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(16.dp)),
                                color = WLLightBlueSurface.copy(alpha = 0.45f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(WLLightBlueSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (bookingMode == "Trains") Icons.Filled.Train else Icons.Filled.FlightTakeoff,
                                            contentDescription = "From",
                                            tint = WLRoyalBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (bookingMode == "Trains") "From Station" else "From",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = WLNavyMuted
                                        )
                                        Text(
                                            text = fromCity,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLNavyPrimary
                                        )
                                        Text(
                                            text = fromAirport,
                                            fontSize = 11.sp,
                                            color = WLNavyMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = WLLightBlueSecondary
                                    ) {
                                        Text(
                                            text = fromCode,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLRoyalBlueDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // 2. TO CARD
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onPickToLocation() }
                                    .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(16.dp)),
                                color = WLLightBlueSurface.copy(alpha = 0.45f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(WLLightBlueSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (bookingMode == "Trains") Icons.Filled.Train else Icons.Filled.FlightLand,
                                            contentDescription = "To",
                                            tint = WLRoyalBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (bookingMode == "Trains") "To Station" else "To",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = WLNavyMuted
                                        )
                                        Text(
                                            text = toCity,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLNavyPrimary
                                        )
                                        Text(
                                            text = toAirport,
                                            fontSize = 11.sp,
                                            color = WLNavyMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = WLLightBlueSecondary
                                    ) {
                                        Text(
                                            text = toCode,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLRoyalBlueDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // CIRCULAR FLOATING SWAP BUTTON
                        Surface(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 56.dp)
                                .size(36.dp)
                                .shadow(6.dp, CircleShape, spotColor = WLRoyalBlue.copy(alpha = 0.25f))
                                .clip(CircleShape)
                                .clickable { onSwapCities() },
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.5.dp, WLRoyalBlue)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.SwapVert,
                                    contentDescription = "Swap Origin and Destination",
                                    tint = WLRoyalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // DATES ROW: DEPARTURE & RETURN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // DEPARTURE CARD
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onPickDepartureDate() }
                                .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(16.dp)),
                            color = WLLightBlueSurface.copy(alpha = 0.45f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarMonth,
                                        contentDescription = "Departure",
                                        tint = WLRoyalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (bookingMode == "Trains") "Travel Date" else "Departure",
                                        fontSize = 11.sp,
                                        color = WLNavyMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = departureDate,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WLNavyPrimary
                                )
                                Text(
                                    text = departureDay,
                                    fontSize = 11.sp,
                                    color = WLNavyMuted
                                )
                            }
                        }

                        // RETURN CARD (Disabled/Dimmed for One Way)
                        val isRoundTrip = tripType == "Round Trip"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onPickReturnDate() }
                                .border(
                                    BorderStroke(1.dp, if (isRoundTrip) WLBorderLight else Color(0xFFF1F5F9)),
                                    RoundedCornerShape(16.dp)
                                ),
                            color = if (isRoundTrip) WLLightBlueSurface.copy(alpha = 0.45f) else Color(0xFFFAFAFA)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarMonth,
                                        contentDescription = "Return",
                                        tint = if (isRoundTrip) WLRoyalBlue else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Return",
                                        fontSize = 11.sp,
                                        color = if (isRoundTrip) WLNavyMuted else Color(0xFF94A3B8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isRoundTrip) returnDate else "+ Add Return",
                                    fontSize = 14.sp,
                                    fontWeight = if (isRoundTrip) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isRoundTrip) WLNavyPrimary else WLRoyalBlue
                                )
                                Text(
                                    text = if (isRoundTrip) returnDay else "Tap for round trip",
                                    fontSize = 11.sp,
                                    color = if (isRoundTrip) WLNavyMuted else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // PRIMARY CTA: SEARCH BUTTON
                    Button(
                        onClick = onSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = WLRoyalBlue.copy(alpha = 0.35f)
                            )
                            .testTag("search_flights_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WLRoyalBlue
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search $bookingMode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 5. LIVE CUSTOMIZED RESULTS (DIRECT PREVIEW RIGHT BELOW SEARCH)
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available $bookingMode",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLNavyPrimary
                        )
                        Text(
                            text = "$fromCity ➔ $toCity • $departureDate",
                            fontSize = 12.sp,
                            color = WLNavyMuted
                        )
                    }

                    Text(
                        text = "View All (${availableTravelItems.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLRoyalBlue,
                        modifier = Modifier.clickable { onSearch() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Shows the flights or trains customized to this route
                availableTravelItems.take(3).forEach { item ->
                    FlightResultCard(
                        flight = item,
                        onSelect = { onSelectTravelCard(item) }
                    )
                }
            }
        }

        // 6. QUICK SEARCH / POPULAR ROUTES
        item {
            Column(modifier = Modifier.padding(top = 22.dp)) {
                Text(
                    text = "Popular Routes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLNavyPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(popularRoutes) { route ->
                        Card(
                            modifier = Modifier
                                .width(185.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onSelectPopularRoute(route) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, WLBorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${route.fromCity} → ${route.toCity}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLNavyPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Flight,
                                        contentDescription = "Flight",
                                        tint = WLRoyalBlue,
                                        modifier = Modifier.size(16.dp).rotate(45f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = "From ${route.price}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLRoyalBlue
                                        )
                                        Text(
                                            text = route.duration,
                                            fontSize = 11.sp,
                                            color = WLNavyMuted
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = WLLightBlueSurface,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.ChevronRight,
                                                contentDescription = "Select",
                                                tint = WLRoyalBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. RECENT SEARCHES
        item {
            Column(modifier = Modifier.padding(top = 22.dp, start = 20.dp, end = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLNavyPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "History",
                            tint = WLNavyMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-saved",
                            fontSize = 11.sp,
                            color = WLNavyMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                recentSearches.forEach { search ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onRestoreRecentSearch(search) }
                            .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(14.dp)),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = WLLightBlueSurface,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (search.mode == "Train") Icons.Filled.Train else Icons.Filled.AirplanemodeActive,
                                            contentDescription = "Recent Travel",
                                            tint = WLRoyalBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${search.fromCode} → ${search.toCode}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLNavyPrimary
                                    )
                                    Text(
                                        text = "${search.date} • ${search.passengers} • ${search.mode}",
                                        fontSize = 11.sp,
                                        color = WLNavyMuted
                                    )
                                }
                            }

                            Text(
                                text = "Restore",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WLRoyalBlue
                            )
                        }
                    }
                }
            }
        }

        // 8. PROMOTIONAL BANNER: "Fly More, Save More"
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = WLRoyalBlue.copy(alpha = 0.2f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_flight_service),
                    contentDescription = "Promotional Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xE6084D5C),
                                    Color(0xCC146EF5),
                                    Color(0x660052CC)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "SPECIAL OFFER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Travel More, Save More",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Exclusive deals on flights & trains",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Button(
                        onClick = onExploreDeals,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Explore Deals",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLRoyalBlue
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 2: RESULTS SCREEN (Filtered list)
// -------------------------------------------------------------
@Composable
private fun FlightResultsScreen(
    bookingMode: String,
    toCity: String,
    departureDate: String,
    passengerSummaryText: String,
    cabinClass: String,
    onEditSearch: () -> Unit,
    selectedStops: String,
    onStopsSelected: (String) -> Unit,
    selectedAirline: String,
    onAirlineSelected: (String) -> Unit,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    flights: List<FlightCardData>,
    onSelectFlight: (FlightCardData) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flight_results_scroll"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // RESULT HEADER & EDIT SEARCH BUTTON
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$bookingMode to $toCity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLNavyPrimary
                        )
                        Text(
                            text = "$departureDate • $passengerSummaryText • $cabinClass",
                            fontSize = 12.sp,
                            color = WLNavyMuted
                        )
                    }

                    OutlinedButton(
                        onClick = onEditSearch,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, WLRoyalBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Search",
                            tint = WLRoyalBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit Search",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WLRoyalBlue
                        )
                    }
                }
            }
        }

        // FILTER CHIPS ROW
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Stops Filter Chips
                    item {
                        FilterChip(
                            label = if (selectedStops == "All") "Stops" else selectedStops,
                            isSelected = selectedStops != "All",
                            onClick = {
                                onStopsSelected(
                                    when (selectedStops) {
                                        "All" -> "Non-stop"
                                        "Non-stop" -> "1 Stop"
                                        else -> "All"
                                    }
                                )
                            }
                        )
                    }

                    // Carriers/Lines Filter Chips
                    val options = if (bookingMode == "Trains") {
                        listOf("Vande Bharat", "Rajdhani", "Shatabdi", "Duronto")
                    } else {
                        listOf("IndiGo", "Air India", "Vistara", "Akasa Air")
                    }

                    options.forEach { opt ->
                        item {
                            FilterChip(
                                label = opt,
                                isSelected = selectedAirline == opt,
                                onClick = {
                                    onAirlineSelected(if (selectedAirline == opt) "All" else opt)
                                }
                            )
                        }
                    }

                    // Sort By Quick Chip
                    item {
                        FilterChip(
                            label = "Sort: $selectedSort",
                            isSelected = selectedSort != "Recommended",
                            leadingIcon = Icons.AutoMirrored.Filled.Sort,
                            onClick = {
                                onSortSelected(
                                    when (selectedSort) {
                                        "Recommended" -> "Cheapest"
                                        "Cheapest" -> "Fastest"
                                        "Fastest" -> "Earliest"
                                        else -> "Recommended"
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${flights.size} $bookingMode available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = WLNavyMuted
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sort by: ",
                            fontSize = 12.sp,
                            color = WLNavyMuted
                        )
                        Text(
                            text = selectedSort,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLRoyalBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // TRAVEL RESULT CARDS LIST
        items(flights) { item ->
            FlightResultCard(
                flight = item,
                onSelect = { onSelectFlight(item) }
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: FILTER CHIP
// -------------------------------------------------------------
@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) WLRoyalBlue else Color.White,
        border = BorderStroke(1.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else WLNavySecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else WLNavySecondary
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENT: FLIGHT / TRAIN CARD
// -------------------------------------------------------------
@Composable
private fun FlightResultCard(
    flight: FlightCardData,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = WLRoyalBlue.copy(alpha = 0.12f))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, WLBorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // HEADER: Carrier & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = flight.airlineColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (flight.isTrain) {
                                Icon(
                                    imageVector = Icons.Filled.Train,
                                    contentDescription = null,
                                    tint = flight.airlineColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = flight.airline.take(2).uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = flight.airlineColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = flight.airline,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLNavyPrimary
                        )
                        Text(
                            text = flight.flightNumber,
                            fontSize = 11.sp,
                            color = WLNavyMuted
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${flight.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLRoyalBlue
                    )
                    Text(
                        text = "per passenger",
                        fontSize = 10.sp,
                        color = WLNavyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TIMES & ROUTE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = flight.departureTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLNavyPrimary
                    )
                    Text(
                        text = flight.fromCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLNavySecondary
                    )
                    Text(
                        text = flight.departureAirport,
                        fontSize = 10.sp,
                        color = WLNavyMuted
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = flight.duration,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLNavySecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalDivider(
                            color = WLRoyalBlue.copy(alpha = 0.35f),
                            thickness = 1.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Icon(
                            imageVector = if (flight.isTrain) Icons.Filled.Train else Icons.Filled.Flight,
                            contentDescription = null,
                            tint = WLRoyalBlue,
                            modifier = Modifier.size(16.dp).rotate(if (flight.isTrain) 0f else 90f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = flight.stops,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (flight.isNonStop) WLEmeraldSuccess else WLNavyMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = flight.arrivalTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLNavyPrimary
                    )
                    Text(
                        text = flight.toCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLNavySecondary
                    )
                    Text(
                        text = flight.arrivalAirport,
                        fontSize = 10.sp,
                        color = WLNavyMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // BAGGAGE & SELECT CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Luggage,
                        contentDescription = "Baggage",
                        tint = WLNavyMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (flight.isTrain) "Luggage ${flight.cabinBaggage} Included" else "Cabin ${flight.cabinBaggage} • Check-in ${flight.checkInBaggage}",
                        fontSize = 11.sp,
                        color = WLNavyMuted
                    )
                }

                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Select",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// BOTTOM SHEET CONTENT: FARE DETAILS
// -------------------------------------------------------------
@Composable
private fun FareDetailsBottomSheetContent(
    flight: FlightCardData,
    selectedTier: String,
    onTierSelected: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (flight.isTrain) "Train Fare & Berth Details" else "Fare & Baggage Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )
        Text(
            text = "${flight.airline} ${flight.flightNumber} • ${flight.fromCode} → ${flight.toCode}",
            fontSize = 13.sp,
            color = WLNavyMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (flight.isTrain) "Select Berth / Class" else "Choose Fare Type",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val tiers = if (flight.isTrain) {
            listOf(
                Triple("Standard", flight.price, "Confirmed Berth • Linen & Bedding included"),
                Triple("Flexi Tatkal", flight.price + 450, "Priority Seat Allocation • Lower change fee"),
                Triple("Premium Class", flight.price + 950, "Executive Chair / 2A • Complimentary hot meal")
            )
        } else {
            listOf(
                Triple("Saver", flight.price, "Cabin 7kg • Check-in 15kg • Standard seat"),
                Triple("Flexi Plus", flight.price + 800, "Free seat choice • Complimentary snack • Low cancellation fee"),
                Triple("Super 6E", flight.price + 1900, "Check-in 25kg • Priority boarding • Zero change fee")
            )
        }

        tiers.forEach { (tierName, tierPrice, tierDesc) ->
            val isSelected = selectedTier == tierName
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onTierSelected(tierName) }
                    .border(
                        BorderStroke(1.5.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
                        RoundedCornerShape(14.dp)
                    ),
                color = if (isSelected) WLLightBlueSurface else Color.White
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onTierSelected(tierName) },
                            colors = RadioButtonDefaults.colors(selectedColor = WLRoyalBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = tierName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLNavyPrimary
                            )
                            Text(
                                text = tierDesc,
                                fontSize = 11.sp,
                                color = WLNavyMuted
                            )
                        }
                    }

                    Text(
                        text = "₹$tierPrice",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) WLRoyalBlue else WLNavyPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FARE BREAKDOWN CARD
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = WLLightBlueSurface.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, WLBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Fare Breakdown",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLNavyPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Base Fare", fontSize = 12.sp, color = WLNavyMuted)
                    Text(text = "₹${flight.baseFare}", fontSize = 12.sp, color = WLNavyPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Govt Taxes & Terminal Fees", fontSize = 12.sp, color = WLNavyMuted)
                    Text(text = "₹${flight.taxesAndFees}", fontSize = 12.sp, color = WLNavyPrimary)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = WLBorderLight)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Price", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                    val currentPrice = when (selectedTier) {
                        "Flexi Plus", "Flexi Tatkal" -> flight.price + 800
                        "Super 6E", "Premium Class" -> flight.price + 1900
                        else -> flight.price
                    }
                    Text(text = "₹$currentPrice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = WLRoyalBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Instant PNR generation • Easy refund policy",
                fontSize = 11.sp,
                color = WLNavyMuted
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = WLRoyalBlue.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue)
        ) {
            Text(
                text = "Continue to Passenger Details",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: PASSENGER DETAILS SCREEN
// -------------------------------------------------------------
@Composable
private fun FlightPassengerDetailsScreen(
    flight: FlightCardData,
    title: String,
    onTitleChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    nationality: String,
    onNationalityChange: (String) -> Unit,
    passportNumber: String,
    onPassportNumberChange: (String) -> Unit,
    passportExpiry: String,
    onPassportExpiryChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    mobile: String,
    onMobileChange: (String) -> Unit,
    saveDetails: Boolean,
    onSaveDetailsChange: (Boolean) -> Unit,
    isInternational: Boolean,
    onProceedToPayment: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flight_passenger_scroll"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(WLLightBlueSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Step 1 of 2: Passenger Information",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLRoyalBlue
                )
                Text(
                    text = "${flight.airline} ${flight.flightNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WLNavySecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Primary Passenger Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WLNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Mr", "Ms", "Mrs").forEach { itemTitle ->
                    val isSelected = title == itemTitle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) WLRoyalBlue else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onTitleChange(itemTitle) }
                    ) {
                        Text(
                            text = itemTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else WLNavySecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = { Text("First Name (as per Govt ID)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WLRoyalBlue,
                    unfocusedBorderColor = WLBorderLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WLRoyalBlue,
                    unfocusedBorderColor = WLBorderLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = onGenderChange,
                    label = { Text("Gender") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WLRoyalBlue,
                        unfocusedBorderColor = WLBorderLight,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = onDobChange,
                    label = { Text("Date of Birth") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WLRoyalBlue,
                        unfocusedBorderColor = WLBorderLight,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nationality,
                onValueChange = onNationalityChange,
                label = { Text("Nationality") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WLRoyalBlue,
                    unfocusedBorderColor = WLBorderLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (isInternational) {
            item {
                Text(
                    text = "Passport Details (Required for International Flights)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLRoyalBlue
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passportNumber,
                    onValueChange = onPassportNumberChange,
                    label = { Text("Passport Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passportExpiry,
                    onValueChange = onPassportExpiryChange,
                    label = { Text("Passport Expiry Date (DD/MM/YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        item {
            Text(
                text = "Contact Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WLNavyPrimary
            )
            Text(
                text = "Your e-ticket and travel updates will be sent here",
                fontSize = 11.sp,
                color = WLNavyMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = onMobileChange,
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Save passenger details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLNavyPrimary
                    )
                    Text(
                        text = "Auto-fill for your future travel bookings",
                        fontSize = 11.sp,
                        color = WLNavyMuted
                    )
                }

                Switch(
                    checked = saveDetails,
                    onCheckedChange = onSaveDetailsChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WLRoyalBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onProceedToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = WLRoyalBlue.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Proceed to Payment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: PAYMENT SCREEN
// -------------------------------------------------------------
@Composable
private fun FlightPaymentScreen(
    flight: FlightCardData,
    passengerName: String,
    seat: String,
    fareTier: String,
    baseFare: Int,
    taxes: Int,
    discount: Int,
    finalTotal: Int,
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    isPromoApplied: Boolean,
    onApplyPromo: () -> Unit,
    selectedPaymentMethod: String,
    onSelectPaymentMethod: (String) -> Unit,
    selectedUpiApp: String,
    onSelectUpiApp: (String) -> Unit,
    upiId: String,
    onUpiIdChange: (String) -> Unit,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    cardCvv: String,
    onCardCvvChange: (String) -> Unit,
    isProcessing: Boolean,
    onPayClicked: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flight_payment_scroll"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, WLBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${flight.airline} • ${flight.flightNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLRoyalBlue
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WLLightBlueSurface
                        ) {
                            Text(
                                text = fareTier,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WLRoyalBlueDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${flight.fromCode} ${flight.departureTime} → ${flight.toCode} ${flight.arrivalTime}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLNavyPrimary
                        )
                        Text(
                            text = flight.duration,
                            fontSize = 12.sp,
                            color = WLNavyMuted
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Passenger: $passengerName",
                            fontSize = 12.sp,
                            color = WLNavySecondary
                        )
                        Text(
                            text = "Seat: $seat",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WLRoyalBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, WLBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalOffer,
                        contentDescription = "Promo",
                        tint = WLRoyalBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = promoCode,
                        onValueChange = onPromoCodeChange,
                        placeholder = { Text("Enter Promo Code") },
                        modifier = Modifier.weight(1f).height(46.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApplyPromo,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isPromoApplied) WLEmeraldSuccess else WLRoyalBlue),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = if (isPromoApplied) "Applied ✓" else "Apply",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, WLBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Price Summary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Fare", fontSize = 13.sp, color = WLNavyMuted)
                        Text("₹$baseFare", fontSize = 13.sp, color = WLNavyPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aviation / Rail Taxes & Fees", fontSize = 13.sp, color = WLNavyMuted)
                        Text("₹$taxes", fontSize = 13.sp, color = WLNavyPrimary)
                    }
                    if (discount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Promo Discount", fontSize = 13.sp, color = WLEmeraldSuccess)
                            Text("-₹$discount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WLEmeraldSuccess)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = WLBorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Final Payable Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                        Text("₹$finalTotal", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            Text(
                text = "Select Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WLNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val methods = listOf(
                Pair("UPI", "Instant payment via GPay, PhonePe, Paytm"),
                Pair("Credit / Debit Card", "Visa, MasterCard, RuPay with zero fee"),
                Pair("Net Banking", "All major Indian banks supported"),
                Pair("Wallets", "Paytm, Amazon Pay, Mobikwik")
            )

            methods.forEach { (method, desc) ->
                val isSelected = selectedPaymentMethod == method
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectPaymentMethod(method) }
                        .border(
                            BorderStroke(1.5.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
                            RoundedCornerShape(14.dp)
                        ),
                    color = if (isSelected) WLLightBlueSurface else Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectPaymentMethod(method) },
                            colors = RadioButtonDefaults.colors(selectedColor = WLRoyalBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = method,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLNavyPrimary
                            )
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = WLNavyMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedPaymentMethod == "UPI") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, WLBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Popular UPI Apps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Google Pay", "PhonePe", "Paytm").forEach { app ->
                                val isAppSelected = selectedUpiApp == app
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isAppSelected) WLRoyalBlue else WLLightBlueSurface,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSelectUpiApp(app) }
                                ) {
                                    Text(
                                        text = app,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAppSelected) Color.White else WLNavySecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = onUpiIdChange,
                            label = { Text("Or Enter Virtual Payment Address (UPI ID)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            } else if (selectedPaymentMethod.contains("Card")) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, WLBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = onCardNumberChange,
                            label = { Text("Card Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = onCardExpiryChange,
                                label = { Text("Valid Thru (MM/YY)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = onCardCvvChange,
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Security, contentDescription = "Secure", tint = WLEmeraldSuccess, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "256-Bit SSL Bank Encrypted • 100% Secure Payment",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = WLNavyMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPayClicked,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = WLRoyalBlue.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Verifying with Bank...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Pay ₹$finalTotal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 5: BOOKING CONFIRMATION SCREEN
// -------------------------------------------------------------
@Composable
private fun FlightBookingConfirmationScreen(
    pnr: String,
    flight: FlightCardData,
    date: String,
    passengerName: String,
    seat: String,
    onCopyPnr: () -> Unit,
    onViewTicket: () -> Unit,
    onDownloadTicket: () -> Unit,
    onAddToTrips: () -> Unit,
    onExploreMore: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flight_confirm_scroll"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(WLEmeraldSuccess.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(WLEmeraldSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Booking Confirmed!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = WLNavyPrimary
            )

            Text(
                text = "Your ${if (flight.isTrain) "train" else "flight"} ticket has been successfully booked.",
                fontSize = 13.sp,
                color = WLNavyMuted
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, WLBorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = WLRoyalBlue.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Booking PNR",
                                fontSize = 11.sp,
                                color = WLNavyMuted
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onCopyPnr() }
                            ) {
                                Text(
                                    text = pnr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WLRoyalBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "📋", fontSize = 14.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = flight.airlineColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${flight.airline} ${flight.flightNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = flight.airlineColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WLBorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = flight.departureTime, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                            Text(text = flight.fromCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlueDark)
                            Text(text = flight.departureAirport, fontSize = 11.sp, color = WLNavyMuted)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = flight.duration, fontSize = 11.sp, color = WLNavyMuted)
                            Icon(
                                imageVector = if (flight.isTrain) Icons.Filled.Train else Icons.Filled.Flight,
                                contentDescription = null,
                                tint = WLRoyalBlue,
                                modifier = Modifier.size(18.dp).rotate(if (flight.isTrain) 0f else 90f)
                            )
                            Text(text = flight.stops, fontSize = 11.sp, color = WLEmeraldSuccess, fontWeight = FontWeight.SemiBold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = flight.arrivalTime, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                            Text(text = flight.toCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlueDark)
                            Text(text = flight.arrivalAirport, fontSize = 11.sp, color = WLNavyMuted)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WLBorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Passenger", fontSize = 11.sp, color = WLNavyMuted)
                            Text(text = passengerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Seat / Berth", fontSize = 11.sp, color = WLNavyMuted)
                            Text(text = seat, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Baggage", fontSize = 11.sp, color = WLNavyMuted)
                            Text(text = if (flight.isTrain) "40 kg" else "7kg + 15kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "||| | ||||| || |||||| | |||| || ||| | ||||||||",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = Color(0xFF334155),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewTicket,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue)
                ) {
                    Icon(imageVector = Icons.Filled.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "View Ticket", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onDownloadTicket,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, WLRoyalBlue)
                ) {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = null, tint = WLRoyalBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Download Ticket (PDF)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                }

                Button(
                    onClick = onAddToTrips,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Icon(imageVector = Icons.Filled.Luggage, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add to Trips", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                TextButton(
                    onClick = onExploreMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Book Another Journey", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WLRoyalBlue)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODAL: LOCATION PICKER (Airports or Stations)
// -------------------------------------------------------------
@Composable
private fun AirportPickerBottomSheet(
    isOrigin: Boolean,
    isTrain: Boolean,
    locations: List<TravelLocationItem>,
    onLocationSelected: (TravelLocationItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLocations = remember(searchQuery, locations) {
        if (searchQuery.isBlank()) locations
        else locations.filter {
            it.city.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.stationOrAirport.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (isOrigin) "Select Origin ${if (isTrain) "Station" else "Airport"}" else "Select Destination ${if (isTrain) "Station" else "Airport"}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search city or code (e.g. DEL, BOM, NDLS)") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = WLRoyalBlue)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.height(340.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredLocations) { loc ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onLocationSelected(loc) },
                    color = WLLightBlueSurface.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, WLBorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isTrain) Icons.Filled.Train else if (isOrigin) Icons.Filled.FlightTakeoff else Icons.Filled.FlightLand,
                                    contentDescription = null,
                                    tint = WLRoyalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = loc.city,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WLNavyPrimary
                                )
                                Text(
                                    text = loc.stationOrAirport,
                                    fontSize = 11.sp,
                                    color = WLNavyMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WLLightBlueSecondary
                        ) {
                            Text(
                                text = loc.code,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLRoyalBlueDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODAL: CABIN & PASSENGER SELECTOR
// -------------------------------------------------------------
@Composable
private fun CabinPassengerSelectorSheet(
    adults: Int,
    onAdultsChange: (Int) -> Unit,
    children: Int,
    onChildrenChange: (Int) -> Unit,
    infants: Int,
    onInfantsChange: (Int) -> Unit,
    cabinClass: String,
    onCabinClassChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Customize Passengers & Class",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        PassengerCounterRow(
            title = "Adults",
            subTitle = "Aged 12 years and above",
            count = adults,
            minCount = 1,
            onDecrement = { if (adults > 1) onAdultsChange(adults - 1) },
            onIncrement = { if (adults < 9) onAdultsChange(adults + 1) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PassengerCounterRow(
            title = "Children",
            subTitle = "Aged 2 to 12 years",
            count = children,
            minCount = 0,
            onDecrement = { if (children > 0) onChildrenChange(children - 1) },
            onIncrement = { if (children < 6) onChildrenChange(children + 1) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PassengerCounterRow(
            title = "Infants",
            subTitle = "Under 2 years (on lap)",
            count = infants,
            minCount = 0,
            onDecrement = { if (infants > 0) onInfantsChange(infants - 1) },
            onIncrement = { if (infants < 4) onInfantsChange(infants + 1) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Class Selection",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val cabinOptions = listOf("Economy / Sleeper", "Premium / 3A", "Business / 2A")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cabinOptions.forEach { option ->
                val isSelected = cabinClass == option
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCabinClassChange(option) }
                        .border(
                            BorderStroke(1.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
                            RoundedCornerShape(12.dp)
                        ),
                    color = if (isSelected) WLLightBlueSurface else Color.White
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) WLRoyalBlue else WLNavySecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue)
        ) {
            Text(text = "Apply Selection", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun PassengerCounterRow(
    title: String,
    subTitle: String,
    count: Int,
    minCount: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, WLBorderLight), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
            Text(text = subTitle, fontSize = 11.sp, color = WLNavyMuted)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = if (count > minCount) WLLightBlueSurface else Color(0xFFF1F5F9),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(enabled = count > minCount) { onDecrement() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (count > minCount) WLRoyalBlue else Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(text = "$count", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)

            Spacer(modifier = Modifier.width(14.dp))

            Surface(
                shape = CircleShape,
                color = WLLightBlueSurface,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onIncrement() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MODAL: DATE PICKER BOTTOM SHEET
// -------------------------------------------------------------
@Composable
private fun FlightDatePickerSheet(
    isDeparture: Boolean,
    currentDate: String,
    onDateSelected: (String, String) -> Unit
) {
    val sampleDates = listOf(
        Pair("15 Oct 2026", "Thursday"),
        Pair("16 Oct 2026", "Friday"),
        Pair("17 Oct 2026", "Saturday"),
        Pair("18 Oct 2026", "Sunday"),
        Pair("19 Oct 2026", "Monday"),
        Pair("20 Oct 2026", "Tuesday"),
        Pair("21 Oct 2026", "Wednesday")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (isDeparture) "Select Travel Date" else "Select Return Date",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WLNavyPrimary
        )
        Text(
            text = "October 2026",
            fontSize = 13.sp,
            color = WLNavyMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        sampleDates.forEach { (date, day) ->
            val isSelected = currentDate == date
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateSelected(date, day) }
                    .border(
                        BorderStroke(1.5.dp, if (isSelected) WLRoyalBlue else WLBorderLight),
                        RoundedCornerShape(12.dp)
                    ),
                color = if (isSelected) WLLightBlueSurface else Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                        Text(text = day, fontSize = 11.sp, color = WLNavyMuted)
                    }

                    if (isSelected) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = WLRoyalBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: DIGITAL BOARDING PASS
// -------------------------------------------------------------
@Composable
private fun DigitalBoardingPassCard(
    flight: FlightCardData,
    pnr: String,
    passengerName: String,
    seat: String,
    departureDate: String,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (flight.isTrain) "E-RAIL TICKET" else "BOARDING PASS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLRoyalBlue,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = WLNavyMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = flight.airline, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                    Text(text = flight.flightNumber, fontSize = 12.sp, color = WLNavyMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "PNR: $pnr", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                    Text(text = departureDate, fontSize = 11.sp, color = WLNavyMuted)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WLBorderLight)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = flight.fromCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                    Text(text = flight.departureTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WLRoyalBlueDark)
                }
                Icon(
                    imageVector = if (flight.isTrain) Icons.Filled.Train else Icons.Filled.Flight,
                    contentDescription = null,
                    tint = WLRoyalBlue,
                    modifier = Modifier.size(24.dp).rotate(if (flight.isTrain) 0f else 90f)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = flight.toCode, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                    Text(text = flight.arrivalTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WLRoyalBlueDark)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WLBorderLight)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "PASSENGER", fontSize = 10.sp, color = WLNavyMuted)
                    Text(text = passengerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                }
                Column {
                    Text(text = if (flight.isTrain) "PLATFORM" else "GATE", fontSize = 10.sp, color = WLNavyMuted)
                    Text(text = if (flight.isTrain) "PF 4" else "34B", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WLNavyPrimary)
                }
                Column {
                    Text(text = if (flight.isTrain) "BERTH" else "SEAT", fontSize = 10.sp, color = WLNavyMuted)
                    Text(text = seat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.QrCode, contentDescription = "QR", tint = WLNavyPrimary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Scan at gate / ticket inspection",
                        fontSize = 11.sp,
                        color = WLNavyMuted
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: PROMOTIONAL DEALS MODAL
// -------------------------------------------------------------
@Composable
private fun PromotionalDealsModal(
    onClose: () -> Unit,
    onSelectDeal: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WonderLight Exclusive Deals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLNavyPrimary
                )
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = WLNavyMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val promoDeals = listOf(
                Pair("FLYMORE500", "Flat ₹500 off on all domestic flights & trains"),
                Pair("WONDERLIGHT", "10% off up to ₹1,200 on premium bookings"),
                Pair("ADVENTURE", "Flat ₹750 off on round-trip itineraries")
            )

            promoDeals.forEach { (code, desc) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = WLLightBlueSurface,
                    border = BorderStroke(1.dp, WLBorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectDeal(code) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WLRoyalBlue)
                            Text(text = desc, fontSize = 11.sp, color = WLNavyMuted)
                        }

                        Button(
                            onClick = { onSelectDeal(code) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WLRoyalBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = "Use", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
