package com.example.ui.screens.trips

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TripBooking
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLSuccess
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.viewmodel.TravelViewModel
import com.example.util.ETicketManager

@Composable
fun TripsScreen(
    viewModel: TravelViewModel,
    onExploreClick: () -> Unit,
    onTripDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bookings by viewModel.allBookings.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBookingForETicket by remember { mutableStateOf<TripBooking?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshBookings()
    }

    val distinctBookings = remember(bookings) {
        bookings.distinctBy { it.bookingId }
    }

    val upcomingBookings = remember(distinctBookings) {
        distinctBookings.filter { b ->
            val s = b.status.lowercase().trim()
            s == "confirmed" || s == "in progress" || s == "pending" || s == "upcoming" || s.isBlank()
        }
    }
    val completedBookings = remember(distinctBookings) {
        distinctBookings.filter { b -> b.status.equals("completed", ignoreCase = true) }
    }
    val cancelledBookings = remember(distinctBookings) {
        distinctBookings.filter { b -> b.status.equals("cancelled", ignoreCase = true) || b.status.equals("canceled", ignoreCase = true) }
    }

    val currentTabBookings = when (selectedTab) {
        0 -> upcomingBookings
        1 -> completedBookings
        2 -> cancelledBookings
        else -> upcomingBookings
    }

    val tabs = listOf(
        "Upcoming (${upcomingBookings.size})",
        "Completed (${completedBookings.size})",
        "Cancelled (${cancelledBookings.size})"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Trips",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = {
                            viewModel.refreshBookings()
                            Toast.makeText(context, "Refreshing bookings...", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Bookings",
                            tint = WLTealPrimary
                        )
                    }
                }

                Text(
                    text = "Manage your confirmed bookings & itineraries",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = WLTealPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = WLTealPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) WLTealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("trips_tab_$index")
                        )
                    }
                }
            }
        }

        if (currentTabBookings.isNotEmpty()) {
            items(currentTabBookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x2208766E))
                        .clickable { selectedBookingForETicket = booking }
                        .testTag("booking_card_${booking.bookingId}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                val imageFallback = when {
                                    booking.destinationTitle.contains("Flight", ignoreCase = true) -> com.example.R.drawable.hero_flight_service
                                    booking.destinationTitle.contains("Hotel", ignoreCase = true) || booking.destinationTitle.contains("Stay", ignoreCase = true) || booking.destinationTitle.contains("Villa", ignoreCase = true) -> com.example.R.drawable.hero_hotel_service
                                    booking.destinationTitle.contains("Train", ignoreCase = true) || booking.destinationTitle.contains("Express", ignoreCase = true) -> com.example.R.drawable.hero_train_service
                                    booking.destinationTitle.contains("Goa", ignoreCase = true) -> com.example.R.drawable.goa_hero
                                    booking.destinationTitle.contains("Kerala", ignoreCase = true) -> com.example.R.drawable.kerala_hero
                                    booking.destinationTitle.contains("Rajasthan", ignoreCase = true) -> com.example.R.drawable.rajasthan_hero
                                    else -> com.example.R.drawable.kashmir_hero
                                }
                                AsyncImage(
                                    model = if (booking.imageRes != 0) booking.imageRes else imageFallback,
                                    contentDescription = booking.destinationTitle,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = WLSuccess.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "● ${booking.status}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLSuccess,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = booking.destinationTitle,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = "Booking ID: ${booking.bookingId}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val durText = if (booking.duration.isBlank() || booking.duration.equals("null", ignoreCase = true)) "Standard Trip" else booking.duration
                                Text(
                                    text = "${booking.adults} Adults • $durText",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Paid: ₹${booking.totalAmount}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WLTealPrimary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { selectedBookingForETicket = booking },
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ConfirmationNumber,
                                        contentDescription = null,
                                        tint = WLTealPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("E-Ticket", fontSize = 12.sp, color = WLTealPrimary)
                                }

                                Button(
                                    onClick = { selectedBookingForETicket = booking },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Details", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(WLTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Luggage,
                            contentDescription = "Luggage",
                            tint = WLTealPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (selectedTab == 1) "No Completed Trips Yet" else if (selectedTab == 2) "No Cancelled Bookings" else "No Upcoming Trips",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Your dream adventures across India await. Discover mountains, beaches, and royal palaces today!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Explore Destinations", color = Color.White)
                    }
                }
            }
        }
    }

    selectedBookingForETicket?.let { booking ->
        ETicketDetailsDialog(
            booking = booking,
            onDismiss = { selectedBookingForETicket = null }
        )
    }
}

@Composable
fun ETicketDetailsDialog(
    booking: TripBooking,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val route = remember(booking) { ETicketManager.parseRouteDetails(booking) }
    var dialogTab by remember { mutableIntStateOf(0) } // 0: Ticket & Route, 1: Payment & Receipt
    var isGeneratingPdf by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (dialogTab == 0) "WonderLight E-Ticket" else "Payment Receipt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLTealPrimary
                        )
                        Text(
                            text = if (dialogTab == 0) "Confirmed Boarding Pass" else "Tax Invoice & Breakdown",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs: [ 🎫 E-Ticket ] and [ 💳 Payment Details ]
                TabRow(
                    selectedTabIndex = dialogTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = WLTealPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[dialogTab]),
                            color = WLTealPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = dialogTab == 0,
                        onClick = { dialogTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.FlightTakeoff,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (dialogTab == 0) WLTealPrimary else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "E-Ticket",
                                    fontSize = 12.sp,
                                    fontWeight = if (dialogTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dialogTab == 0) WLTealPrimary else Color(0xFF64748B)
                                )
                            }
                        }
                    )
                    Tab(
                        selected = dialogTab == 1,
                        onClick = { dialogTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (dialogTab == 1) WLTealPrimary else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Payment",
                                    fontSize = 12.sp,
                                    fontWeight = if (dialogTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dialogTab == 1) WLTealPrimary else Color(0xFF64748B)
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (dialogTab == 0) {
                    // ==========================================
                    // PAGE 1: E-TICKET & FROM / TO ROUTE DETAILS
                    // ==========================================
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Status & PNR Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "PNR / BOOKING REF",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Booking ID", booking.bookingId))
                                            Toast.makeText(context, "Copied ${booking.bookingId}", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text(
                                            text = booking.bookingId,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WLTealPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = WLTealPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                // Status Pill
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = booking.status.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFE2E8F0)
                            )

                            // FROM & TO ROUTE CARD (Prominent display)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // FROM
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "FROM",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = route.fromCode,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WLTealPrimary
                                            )
                                            Text(
                                                text = route.fromCity,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = route.fromStation,
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "Dep: ${route.departureTime}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF0284C7),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        // ROUTE ARROW
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        ) {
                                            Text(
                                                text = "✈",
                                                fontSize = 18.sp,
                                                color = WLOrangeAccent
                                            )
                                            Text(
                                                text = "─────>",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }

                                        // TO
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "TO",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = route.toCode,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WLTealPrimary
                                            )
                                            Text(
                                                text = route.toCity,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = route.toStation,
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                            Text(
                                                text = "Arr: ${route.arrivalTime}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF0284C7),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = route.serviceNumber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF475569)
                                        )
                                        Text(
                                            text = route.seatOrClass,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = WLTealPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Trip Title & Date
                            Text(
                                text = booking.destinationTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            val durText = if (booking.duration.isBlank() || booking.duration.equals("null", ignoreCase = true)) "Standard Trip" else booking.duration
                            Text(
                                text = "Travel Date: ${booking.bookingDate} • $durText",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Passenger Information Block
                            Text(
                                text = "PASSENGER DETAILS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLTealPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Lead Guest", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = if (booking.travellerName.isNotBlank()) booking.travellerName else "Verified Guest",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Guests", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${booking.adults} Adults, ${booking.children} Kids",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            if (booking.email.isNotBlank() || booking.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    if (booking.email.isNotBlank()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Email", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(booking.email, fontSize = 11.sp, color = Color(0xFF334155))
                                        }
                                    }
                                    if (booking.phone.isNotBlank()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Phone", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(booking.phone, fontSize = 11.sp, color = Color(0xFF334155))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated Barcode
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                        .background(Color.White, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0..30) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(if (i % 3 == 0) 3.dp else 1.5.dp)
                                            .background(Color(0xFF0F172A))
                                        )
                                    }
                                }
                                Text(
                                    text = "* ${booking.bookingId} *",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Switch to Payment Details Banner link
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dialogTab = 1 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Payment,
                                            contentDescription = null,
                                            tint = WLTealPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "View Payment Details & Receipt",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = WLTealPrimary
                                        )
                                    }
                                    Text(
                                        text = "₹${booking.totalAmount} →",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLTealPrimary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // PAGE 2: PAYMENT DETAILS & TAX RECEIPT
                    // ==========================================
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Payment Status Banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDCFCE7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Payment Completed & Verified",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = "100% Secure Transaction via Razorpay",
                                            fontSize = 10.sp,
                                            color = Color(0xFF166534)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Transaction Information
                            Text(
                                text = "TRANSACTION DETAILS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLTealPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Transaction ID", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("TXN_${booking.bookingId}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Booking Reference", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(booking.bookingId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WLTealPrimary)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payment Method", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = if (booking.paymentMethod.isNotBlank()) booking.paymentMethod else "Online UPI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payment Date", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(booking.bookingDate, fontSize = 11.sp, color = Color(0xFF1E293B))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Paid by", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = if (booking.travellerName.isNotBlank()) booking.travellerName else "Verified User",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFE2E8F0)
                            )

                            // Itemized Tax Invoice Breakdown
                            Text(
                                text = "ITEMIZED FARE BREAKDOWN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLTealPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val baseFare = (booking.totalAmount * 0.82).toInt()
                            val cgst = (booking.totalAmount * 0.09).toInt()
                            val sgst = booking.totalAmount - baseFare - cgst

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Base Fare (${booking.adults} Adults)", fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("₹$baseFare", fontSize = 11.sp, color = Color(0xFF1E293B))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("CGST (9%)", fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("₹$cgst", fontSize = 11.sp, color = Color(0xFF1E293B))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("SGST / IGST (9%)", fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("₹$sgst", fontSize = 11.sp, color = Color(0xFF1E293B))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Convenience & Service Fee", fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("FREE (₹0.00)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF16A34A))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Total Amount Highlight
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.5.dp, WLTealPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL PAID",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Inclusive of all taxes",
                                            fontSize = 9.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Text(
                                        text = "₹${booking.totalAmount}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLTealPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Corporate Billing Info
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "TAX INVOICE DETAILS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "Invoice No: INV-2026-${booking.bookingId}",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "WonderLight Travel Technologies Pvt. Ltd. • GSTIN: 07AAACW8472M1Z5",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Back to Ticket Link
                            Text(
                                text = "← Back to Journey & E-Ticket Details",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WLTealPrimary,
                                modifier = Modifier
                                    .clickable { dialogTab = 0 }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Download PDF and Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Download & View Button
                    Button(
                        onClick = {
                            isGeneratingPdf = true
                            try {
                                val file = ETicketManager.generateETicketPdf(context, booking)
                                if (file != null && file.exists()) {
                                    Toast.makeText(context, "E-Ticket saved to Downloads!", Toast.LENGTH_SHORT).show()
                                    ETicketManager.openETicket(context, file)
                                } else {
                                    Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isGeneratingPdf = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGeneratingPdf) "Downloading..." else if (dialogTab == 0) "Download Ticket (PDF)" else "Download Receipt (PDF)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Share Button
                    OutlinedButton(
                        onClick = {
                            val file = ETicketManager.generateETicketPdf(context, booking)
                            if (file != null && file.exists()) {
                                ETicketManager.shareETicket(context, file, booking)
                            } else {
                                Toast.makeText(context, "Error sharing E-Ticket", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.6f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = WLTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            fontSize = 11.sp,
                            color = WLTealPrimary
                        )
                    }
                }
            }
        }
    )
}
