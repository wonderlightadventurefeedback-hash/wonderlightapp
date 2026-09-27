package com.example.ui.screens.services

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TripBooking
import com.example.ui.theme.WLBlueAccent
import com.example.ui.theme.WLBlueDark
import com.example.ui.theme.WLBluePrimary
import com.example.ui.theme.WLBlueSurface
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLStarGold
import com.example.ui.viewmodel.TravelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -------------------------------------------------------------
// REUSABLE SERVICE HEADER (NO STATUS-BAR PADDING TO AVOID DOUBLE GAP)
@Composable
fun UniversalServiceTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    isReloading: Boolean = false,
    onReload: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F5F9), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            if (onReload != null) {
                val reloadRotation = if (isReloading) {
                    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "TopBarReloadSpin")
                    infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                            animation = androidx.compose.animation.core.tween(durationMillis = 800, easing = androidx.compose.animation.core.LinearEasing),
                            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                        ),
                        label = "TopBarReloadSpinAngle"
                    ).value
                } else {
                    0f
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (isReloading) Color(0xFFE0F2FE) else Color(0xFFF1F5F9), CircleShape)
                        .clickable(enabled = !isReloading) { onReload() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reload service data",
                        tint = if (isReloading) WLBluePrimary else Color(0xFF334155),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(reloadRotation)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alerts",
                    tint = WLBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// UNIVERSAL BOOKING CONFIRMATION DIALOG
// -------------------------------------------------------------
@Composable
fun UniversalBookingConfirmationDialog(
    booking: TripBooking,
    onDismiss: () -> Unit,
    onViewTrips: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onViewTrips,
                colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View in My Trips", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Booking Confirmed! 🎉",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Booking Reference: ${booking.bookingId}",
                    fontWeight = FontWeight.SemiBold,
                    color = WLBluePrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = booking.destinationTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "${booking.duration} • Amount: ₹${booking.totalAmount}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "A confirmation e-voucher & instant ticket have been added to your Trips tab and synced with the cloud.",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

// =============================================================
// 1. VILLAS & HOMESTAYS BOOKING SCREEN
// =============================================================
@Composable
fun VillaBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }
    var locationQuery by remember { mutableStateOf("") }
    var selectedLocationChip by remember { mutableStateOf("All") }
    var selectedGuests by remember { mutableIntStateOf(2) }
    var stayDuration by remember { mutableStateOf("3 Nights (15 Oct - 18 Oct)") }
    var selectedFilter by remember { mutableStateOf("All Villas") }

    val locationChips = listOf("All", "Bhubaneswar", "Puri", "Goa", "Mumbai", "Delhi", "Dubai", "Bali", "Coorg")
    val filters = listOf("All Villas", "Private Pool", "Beachfront", "Heritage Estate", "Wi-Fi & Chef")

    val searchResults by viewModel.villaSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingVillas.collectAsState()

    // Trigger initial search if list is empty
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (searchResults.isEmpty()) {
            viewModel.searchVillas("")
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Villas & Luxury Homestays",
                subtitle = "Handcrafted private villas, pool estates & heritage stays",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Where do you want to stay? Search Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1E0284C7)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Where do you want to stay?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Search destination input
                            OutlinedTextField(
                                value = locationQuery,
                                onValueChange = {
                                    locationQuery = it
                                    selectedLocationChip = "Custom"
                                    viewModel.searchVillas(it, selectedGuests)
                                },
                                placeholder = { Text("Search city (e.g. Bhubaneswar, Puri, Goa, Bali)") },
                                leadingIcon = {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = WLBluePrimary)
                                },
                                trailingIcon = {
                                    if (locationQuery.isNotEmpty()) {
                                        IconButton(onClick = {
                                            locationQuery = ""
                                            selectedLocationChip = "All"
                                            viewModel.searchVillas("", selectedGuests)
                                        }) {
                                            Icon(Icons.Filled.Remove, contentDescription = "Clear", tint = Color(0xFF64748B))
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedIndicatorColor = WLBluePrimary,
                                    unfocusedIndicatorColor = Color(0xFFCBD5E1)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Location Chips (Bhubaneswar, Puri, Goa, Mumbai, Delhi, Dubai, Bali)
                            Text("Popular Destinations", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(locationChips) { loc ->
                                    val isSel = loc == selectedLocationChip
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSel) WLBluePrimary else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WLBluePrimary else Color(0xFFE2E8F0)),
                                        modifier = Modifier.clickable {
                                            selectedLocationChip = loc
                                            locationQuery = if (loc == "All") "" else loc
                                            viewModel.searchVillas(if (loc == "All") "" else loc, selectedGuests)
                                        }
                                    ) {
                                        Text(
                                            text = loc,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) Color.White else Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dates & Guests Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Duration
                                Card(
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Check-in / Check-out", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text(stayDuration, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    }
                                }

                                // Guests
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Guests", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (selectedGuests > 1) {
                                                        selectedGuests--
                                                        viewModel.searchVillas(locationQuery, selectedGuests)
                                                    }
                                                },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Remove, null, modifier = Modifier.size(14.dp))
                                            }
                                            Text("$selectedGuests Guests", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                            IconButton(
                                                onClick = {
                                                    if (selectedGuests < 10) {
                                                        selectedGuests++
                                                        viewModel.searchVillas(locationQuery, selectedGuests)
                                                    }
                                                },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Filters Row
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { f ->
                            val isSel = f == selectedFilter
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSel) WLBluePrimary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WLBluePrimary else Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable { selectedFilter = f }
                            ) {
                                Text(
                                    text = f,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // Search Results Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (locationQuery.isBlank()) "Available Luxury Villas (${searchResults.size})" else "Villas in '$locationQuery' (${searchResults.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = WLBluePrimary, strokeWidth = 2.dp)
                        }
                    }
                }

                // Empty State if no villas
                if (searchResults.isEmpty() && !isSearching) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.Pool, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(42.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No villas found matching '$locationQuery'", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                Text("Try selecting Bhubaneswar, Puri, Goa, Bali or Dubai from the destination list above.", fontSize = 12.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                // Dynamic Supabase Villa Cards
                items(searchResults) { villa ->
                    val villaImageRes = when {
                        villa.location.contains("Goa", ignoreCase = true) || villa.name.contains("Goa", ignoreCase = true) -> R.drawable.goa_hero
                        villa.location.contains("Puri", ignoreCase = true) || villa.location.contains("Bhubaneswar", ignoreCase = true) -> R.drawable.kerala_hero
                        villa.location.contains("Dubai", ignoreCase = true) -> R.drawable.hero_flight_service
                        villa.location.contains("Bali", ignoreCase = true) -> R.drawable.kerala_hero
                        villa.location.contains("Manali", ignoreCase = true) || villa.location.contains("Coorg", ignoreCase = true) -> R.drawable.manali_snow
                        villa.location.contains("Rajasthan", ignoreCase = true) || villa.location.contains("Udaipur", ignoreCase = true) -> R.drawable.rajasthan_hero
                        else -> R.drawable.goa_hero
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(165.dp)) {
                                Image(
                                    painter = painterResource(id = villaImageRes),
                                    contentDescription = villa.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                                ) {
                                    Text(
                                        text = "📍 ${villa.location}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(villa.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(
                                    "${villa.bedrooms} Bedrooms • Up to ${villa.guests} Guests • Full Private Property",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                // Amenities
                                if (villa.amenities.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                    ) {
                                        items(villa.amenities) { am ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFF1F5F9)
                                            ) {
                                                Text(am, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Price per night", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("₹${villa.pricePerNight}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WLBluePrimary)
                                    }
                                    Button(
                                        onClick = {
                                            val b = TripBooking(
                                                bookingId = "WL-VL-${(10000..99999).random()}",
                                                destinationId = "villa",
                                                destinationTitle = "Villa: ${villa.name} (${villa.location})",
                                                duration = "$stayDuration • ${villa.bedrooms} Beds",
                                                travellerName = "",
                                                email = "",
                                                phone = "",
                                                adults = selectedGuests,
                                                children = 0,
                                                paymentMethod = "Online UPI (Instant)",
                                                totalAmount = villa.pricePerNight * 3,
                                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                                status = "Confirmed",
                                                imageRes = villaImageRes
                                            )
                                            viewModel.createDirectBooking(b) { confirmedBooking = b }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Reserve Villa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 2. HOLIDAY PACKAGES BOOKING SCREEN
// =============================================================
@Composable
fun HolidayBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }

    val holidaysState by viewModel.holidays.collectAsState()
    val packages = remember(holidaysState) {
        if (holidaysState.isNotEmpty()) {
            holidaysState.map { h ->
                Triple(h.title.ifBlank { h.name }, "${h.duration} • ${h.inclusions.take(2).joinToString(" + ")}", h.price to h.imageRes)
            }
        } else {
            listOf(
                Triple("Kashmir Snow Paradise: Gulmarg & Dal Lake", "6 Days / 5 Nights • Houseboat + Gondola Pass Included", 24999 to R.drawable.kashmir_hero),
                Triple("Enchanting Kerala Backwaters & Munnar Hills", "5 Days / 4 Nights • Luxury Houseboat + Tea Gardens", 18999 to R.drawable.kerala_hero),
                Triple("Royal Rajasthan: Jaipur, Jodhpur & Udaipur", "7 Days / 6 Nights • Heritage Havelis + Desert Camp", 28500 to R.drawable.rajasthan_hero),
                Triple("Himachal Serenade: Manali & Solang Valley", "5 Days / 4 Nights • Riverside Cottage + Snow Gear", 19500 to R.drawable.manali_snow)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Holiday Packages",
                subtitle = "Handcrafted curated travel itineraries & all-inclusive tours",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(packages) { (title, desc, priceAndImage) ->
                    val (price, imageRes) = priceAndImage
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = title,
                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text("⭐ Best Seller All-Inclusive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Package Total", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("₹$price / person", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                    }
                                    Button(
                                        onClick = {
                                            val b = TripBooking(
                                                bookingId = "WL-HOL-${(10000..99999).random()}",
                                                destinationId = "package",
                                                destinationTitle = title,
                                                duration = desc.substringBefore(" •"),
                                                travellerName = "Verified Guest",
                                                email = "guest@wonderlight.com",
                                                phone = "+91 98765 43210",
                                                adults = 2,
                                                children = 0,
                                                paymentMethod = "Online UPI (Instant)",
                                                totalAmount = price * 2,
                                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                                status = "Confirmed",
                                                imageRes = imageRes
                                            )
                                            viewModel.createDirectBooking(b) { confirmedBooking = b }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Book Holiday", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 3. BUSES BOOKING SCREEN
// =============================================================
@Composable
fun BusBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }
    var originCity by remember { mutableStateOf("Bhubaneswar") }
    var destCity by remember { mutableStateOf("Puri") }
    var travelDate by remember { mutableStateOf("Tomorrow, 07:30 AM") }
    var passengers by remember { mutableIntStateOf(1) }
    var selectedFilter by remember { mutableStateOf("All Buses") }
    val busFilters = listOf("All Buses", "Volvo AC Sleeper", "Electric Intercity", "BharatBenz AC", "Multi-Axle")

    val quickBusRoutes = listOf(
        "Bhubaneswar" to "Puri",
        "Delhi" to "Manali",
        "Mumbai" to "Pune",
        "Kolkata" to "Bhubaneswar",
        "Bangalore" to "Goa"
    )

    val searchResults by viewModel.busSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingBuses.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (searchResults.isEmpty()) {
            viewModel.searchBuses("Bhubaneswar", "Puri")
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Intercity Buses & Volvo",
                subtitle = "Premium Volvo, AC Sleepers & Electric Zero-Emission Fleet",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Route Search Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1E0284C7)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Bus Route & Schedule", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(10.dp))

                            // From City Field
                            OutlinedTextField(
                                value = originCity,
                                onValueChange = { originCity = it },
                                label = { Text("From City (e.g. Bhubaneswar)") },
                                leadingIcon = { Icon(Icons.Filled.DirectionsBus, null, tint = WLBluePrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedIndicatorColor = WLBluePrimary,
                                    unfocusedIndicatorColor = Color(0xFFCBD5E1)
                                )
                            )

                            // Swap Icon Row
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 3.dp,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.size(36.dp).clickable {
                                        val temp = originCity
                                        originCity = destCity
                                        destCity = temp
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.SwapHoriz, "Swap", tint = WLBluePrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            // To City Field
                            OutlinedTextField(
                                value = destCity,
                                onValueChange = { destCity = it },
                                label = { Text("To Destination (e.g. Puri)") },
                                leadingIcon = { Icon(Icons.Filled.DirectionsBus, null, tint = Color(0xFF059669)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedIndicatorColor = WLBluePrimary,
                                    unfocusedIndicatorColor = Color(0xFFCBD5E1)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Route Chips
                            Text("Popular Bus Routes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(quickBusRoutes) { (from, to) ->
                                    val isMatch = originCity.contains(from, ignoreCase = true) && destCity.contains(to, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isMatch) WLBluePrimary else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isMatch) WLBluePrimary else Color(0xFFE2E8F0)),
                                        modifier = Modifier.clickable {
                                            originCity = from
                                            destCity = to
                                            viewModel.searchBuses(from, to)
                                        }
                                    ) {
                                        Text(
                                            text = "$from ➔ $to",
                                            fontSize = 11.sp,
                                            fontWeight = if (isMatch) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isMatch) Color.White else Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Date & Passengers Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Travel Date", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text(travelDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Seats", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = { if (passengers > 1) passengers-- },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Remove, null, modifier = Modifier.size(14.dp))
                                            }
                                            Text("$passengers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                            IconButton(
                                                onClick = { if (passengers < 6) passengers++ },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Search Buses Button
                            Button(
                                onClick = {
                                    viewModel.searchBuses(originCity, destCity)
                                    Toast.makeText(context, "Searching buses $originCity ➔ $destCity...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary)
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Searching Buses...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Filled.Search, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Search Buses", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Filter Row
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(busFilters) { f ->
                            val isSel = f == selectedFilter
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSel) WLBluePrimary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WLBluePrimary else Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable { selectedFilter = f }
                            ) {
                                Text(
                                    text = f,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // Results Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Buses: $originCity ➔ $destCity (${searchResults.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Dynamic Supabase Bus Cards
                items(searchResults) { bus ->
                    val totalPrice = bus.price * passengers
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = WLBluePrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(bus.operator, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                        Text(bus.busType, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Text("★ ${bus.rating} Rating", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(bus.departureTime, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(bus.origin, fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Direct", fontSize = 11.sp, color = WLBluePrimary, fontWeight = FontWeight.SemiBold)
                                    HorizontalDivider(modifier = Modifier.width(50.dp).padding(vertical = 2.dp), color = Color(0xFFCBD5E1))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(bus.arrivalTime, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(bus.destination, fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Fare for $passengers Seat(s)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text("₹$totalPrice", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WLBluePrimary)
                                }

                                Button(
                                    onClick = {
                                        val b = TripBooking(
                                            bookingId = "WL-BUS-${(10000..99999).random()}",
                                            destinationId = "bus",
                                            destinationTitle = "Bus: ${bus.operator} (${bus.origin} ➔ ${bus.destination})",
                                            duration = "${bus.departureTime} - ${bus.arrivalTime} • $passengers Sleeper Seat",
                                            travellerName = "",
                                            email = "",
                                            phone = "",
                                            adults = passengers,
                                            children = 0,
                                            paymentMethod = "Online UPI (Instant)",
                                            totalAmount = totalPrice,
                                            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            status = "Confirmed",
                                            imageRes = R.drawable.manali_snow
                                        )
                                        viewModel.createDirectBooking(b) { confirmedBooking = b }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Select Seat & Book", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 4. CABS & TRANSFERS BOOKING SCREEN
// =============================================================
@Composable
fun CabBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }
    var selectedType by remember { mutableStateOf("Airport Transfer") }
    val types = listOf("Airport Transfer", "Outstation", "Local")

    // Outstation state
    var outstationFrom by remember { mutableStateOf("Bhubaneswar") }
    var outstationTo by remember { mutableStateOf("Puri") }
    var outstationDate by remember { mutableStateOf("Tomorrow, 08:00 AM") }

    // Local state
    var localPickup by remember { mutableStateOf("Master Canteen, Bhubaneswar") }
    var localDrop by remember { mutableStateOf("KIIT Road, Patia") }
    var localPackage by remember { mutableStateOf("4 hrs / 40 km") }

    // Airport Transfer state
    var airportPickup by remember { mutableStateOf("Mayfair Lagoon Resort") }
    var airportSelected by remember { mutableStateOf("Bhubaneswar (BBI)") }
    val airports = listOf("Bhubaneswar (BBI)", "Delhi (DEL)", "Mumbai (BOM)", "Goa (GOI)", "Bengaluru (BLR)")

    val searchResults by viewModel.cabSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingCabs.collectAsState()

    androidx.compose.runtime.LaunchedEffect(selectedType) {
        viewModel.searchCabs(selectedType)
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Cabs & Chauffeur Drive",
                subtitle = "Guaranteed on-time airport transfers, local rides & outstation",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Service Type Segmented Tabs
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        types.forEach { t ->
                            val isSel = t == selectedType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) WLBluePrimary else Color.Transparent)
                                    .clickable {
                                        selectedType = t
                                        viewModel.searchCabs(t)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // 2. Tab-Specific Route Search Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1E0284C7)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            when (selectedType) {
                                "Outstation" -> {
                                    Text("Outstation One-Way & Round Trip", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = outstationFrom,
                                        onValueChange = { outstationFrom = it },
                                        label = { Text("From (Origin)") },
                                        leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = WLBluePrimary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = outstationTo,
                                        onValueChange = { outstationTo = it },
                                        label = { Text("To (Destination)") },
                                        leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF059669)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = outstationDate,
                                        onValueChange = { outstationDate = it },
                                        label = { Text("Departure Date & Time") },
                                        leadingIcon = { Icon(Icons.Filled.AccessTime, null, tint = WLBluePrimary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }
                                "Local" -> {
                                    Text("Local Hourly Rental & City Rides", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localPickup,
                                        onValueChange = { localPickup = it },
                                        label = { Text("Pickup Location") },
                                        leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = WLBluePrimary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localDrop,
                                        onValueChange = { localDrop = it },
                                        label = { Text("Drop Location") },
                                        leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF059669)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localPackage,
                                        onValueChange = { localPackage = it },
                                        label = { Text("Rental Package (e.g. 4 hrs / 40 km, 8 hrs / 80 km)") },
                                        leadingIcon = { Icon(Icons.Filled.AccessTime, null, tint = WLBluePrimary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                }
                                else -> { // Airport Transfer
                                    Text("Airport Pickup & Drop Transfers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = airportPickup,
                                        onValueChange = { airportPickup = it },
                                        label = { Text("Pickup / Drop Address") },
                                        leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = WLBluePrimary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Select Airport", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(airports) { ap ->
                                            val isSel = ap == airportSelected
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (isSel) WLBluePrimary else Color(0xFFF1F5F9),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WLBluePrimary else Color(0xFFE2E8F0)),
                                                modifier = Modifier.clickable { airportSelected = ap }
                                            ) {
                                                Text(
                                                    text = ap,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSel) Color.White else Color(0xFF334155),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.searchCabs(selectedType)
                                    Toast.makeText(context, "Searching available $selectedType cabs...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary)
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Searching Cabs...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Filled.Search, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Search Cabs", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 3. Dynamic Cab Cards from Supabase
                items(searchResults) { cab ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalTaxi, contentDescription = null, tint = WLBluePrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(cab.carModel, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                        Text("${cab.category} • ${cab.serviceType}", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEBF3FF)
                                ) {
                                    Text("Free Cancellation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WLBluePrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Chauffeur in Uniform • GPS Tracked • Tolls & Taxes Included", fontSize = 12.sp, color = Color(0xFF64748B))

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Base Fare", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text("₹${cab.baseFare} (₹${cab.perKm}/km)", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                }

                                Button(
                                    onClick = {
                                        val tripTitle = when (selectedType) {
                                            "Outstation" -> "Cab: ${cab.carModel} ($outstationFrom ➔ $outstationTo)"
                                            "Local" -> "Cab: ${cab.carModel} (Local: $localPickup)"
                                            else -> "Cab: ${cab.carModel} (Airport: $airportSelected)"
                                        }
                                        val b = TripBooking(
                                            bookingId = "WL-CAB-${(10000..99999).random()}",
                                            destinationId = "cab",
                                            destinationTitle = tripTitle,
                                            duration = "Immediate Pickup • Chauffeur Assigned",
                                            travellerName = "",
                                            email = "",
                                            phone = "",
                                            adults = 2,
                                            children = 0,
                                            paymentMethod = "Pay Driver / UPI",
                                            totalAmount = cab.baseFare,
                                            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            status = "Confirmed",
                                            imageRes = R.drawable.goa_hero
                                        )
                                        viewModel.createDirectBooking(b) { confirmedBooking = b }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Confirm Cab", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================
// 5. TOURS & ATTRACTIONS BOOKING SCREEN
// =============================================================
@Composable
fun ToursBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }

    val tours = listOf(
        Triple("Grand Shikara Sunset Cruise", "Dal Lake, Srinagar • 2.5 Hours • Kahwa Included", 899 to R.drawable.kashmir_hero),
        Triple("Scuba Diving & Coral Snorkeling", "Grande Island, Goa • Full Day • HD Video Included", 2499 to R.drawable.goa_hero),
        Triple("Sam Sand Dunes Jeep Safari & Camp", "Jaisalmer, Rajasthan • Overnight • Cultural Dance", 1999 to R.drawable.rajasthan_hero),
        Triple("Gulmarg Gondola Ride (Phase 1 & 2)", "Gulmarg, Kashmir • Snow Cable Car Experience", 1850 to R.drawable.gulmarg_gondola)
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Tours & Activities",
                subtitle = "Skip-the-line attraction passes & local adventures",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tours) { (title, desc, priceAndImage) ->
                    val (price, imageRes) = priceAndImage
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = title,
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Entry Pass", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("₹$price / person", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                    }

                                    Button(
                                        onClick = {
                                            val b = TripBooking(
                                                bookingId = "WL-ACT-${(10000..99999).random()}",
                                                destinationId = "tour",
                                                destinationTitle = "Pass: $title",
                                                duration = desc.substringBefore(" •"),
                                                travellerName = "Verified Guest",
                                                email = "guest@wonderlight.com",
                                                phone = "+91 98765 43210",
                                                adults = 2,
                                                children = 0,
                                                paymentMethod = "Online UPI (Instant)",
                                                totalAmount = price * 2,
                                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                                status = "Confirmed",
                                                imageRes = imageRes
                                            )
                                            viewModel.createDirectBooking(b) { confirmedBooking = b }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Book Pass", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 6. VISA ASSISTANCE BOOKING SCREEN
// =============================================================
@Composable
fun VisaBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }

    val visas = listOf(
        Triple("United Arab Emirates (Dubai)", "30 Days Tourist eVisa • 24 - 48 Hours Express", 6899),
        Triple("Thailand Tourist eVisa", "60 Days Entry • 3 Working Days • 99.5% Success Rate", 3200),
        Triple("Schengen Europe (27 Countries)", "Tourist Short-Stay • Appointment + Cover Letter + Verification", 11500),
        Triple("Singapore Tourist eVisa", "Multiple Entry 30 Days • 4 Working Days", 2999)
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Visa Assistance",
                subtitle = "99.8% Success rate with expert document verification",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(visas) { (country, details, price) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(country, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                                    Text("Verified Express", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            Text(details, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 6.dp))

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Visa Fee", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text("₹$price", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WLBluePrimary)
                                }

                                Button(
                                    onClick = {
                                        val b = TripBooking(
                                            bookingId = "WL-VISA-${(10000..99999).random()}",
                                            destinationId = "visa",
                                            destinationTitle = "Visa: $country",
                                            duration = details.substringBefore(" •"),
                                            travellerName = "Applicant User",
                                            email = "visa@wonderlight.com",
                                            phone = "+91 98765 43210",
                                            adults = 1,
                                            children = 0,
                                            paymentMethod = "Online UPI",
                                            totalAmount = price,
                                            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            status = "Confirmed",
                                            imageRes = R.drawable.hero_flight_service
                                        )
                                        viewModel.createDirectBooking(b) { confirmedBooking = b }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Apply Visa Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 7. LUXURY CRUISE BOOKING SCREEN
// =============================================================
@Composable
fun CruiseBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }
    var departurePort by remember { mutableStateOf("Mumbai") }
    var destinationRoute by remember { mutableStateOf("Goa") }
    var departureDate by remember { mutableStateOf("15 Nov 2025 (4 Nights)") }
    var passengers by remember { mutableIntStateOf(2) }

    val quickCruiseRoutes = listOf(
        "Mumbai" to "Goa",
        "Kochi" to "Lakshadweep",
        "Mumbai" to "High Seas",
        "Kolkata" to "Sundarbans",
        "Chennai" to "Sri Lanka"
    )

    val searchResults by viewModel.cruiseSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingCruises.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (searchResults.isEmpty()) {
            viewModel.searchCruises("", "")
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Luxury Cruises & Ocean Liners",
                subtitle = "Sail the open seas & tranquil rivers with 5-star hospitality",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Cruise Search Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1E0284C7)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cruise Sailing & Voyage Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Departure Port Field
                            OutlinedTextField(
                                value = departurePort,
                                onValueChange = { departurePort = it },
                                label = { Text("Departure Port (e.g. Mumbai, Goa, Kochi)") },
                                leadingIcon = { Icon(Icons.Filled.DirectionsBoat, null, tint = WLBluePrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Destination / Route Field
                            OutlinedTextField(
                                value = destinationRoute,
                                onValueChange = { destinationRoute = it },
                                label = { Text("Destination / Route (e.g. Goa, Lakshadweep)") },
                                leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF059669)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Popular Cruise Routes Chips
                            Text("Popular Sailing Voyages", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(quickCruiseRoutes) { (port, dest) ->
                                    val isMatch = departurePort.contains(port, ignoreCase = true) && destinationRoute.contains(dest, ignoreCase = true)
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isMatch) WLBluePrimary else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isMatch) WLBluePrimary else Color(0xFFE2E8F0)),
                                        modifier = Modifier.clickable {
                                            departurePort = port
                                            destinationRoute = dest
                                            viewModel.searchCruises(port, dest)
                                        }
                                    ) {
                                        Text(
                                            text = "$port ➔ $dest",
                                            fontSize = 11.sp,
                                            fontWeight = if (isMatch) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isMatch) Color.White else Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dates & Passengers Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Sailing Date & Duration", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Text(departureDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Guests", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = { if (passengers > 1) passengers-- },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Remove, null, modifier = Modifier.size(14.dp))
                                            }
                                            Text("$passengers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                            IconButton(
                                                onClick = { if (passengers < 8) passengers++ },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Search Cruises Button
                            Button(
                                onClick = {
                                    viewModel.searchCruises(departurePort, destinationRoute)
                                    Toast.makeText(context, "Searching cruises from $departurePort to $destinationRoute...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary)
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Searching Cruises...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Filled.Search, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Search Cruises", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Results Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Luxury Cruises (${searchResults.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Dynamic Supabase Cruise Cards
                items(searchResults) { cruise ->
                    val cruiseImageRes = when {
                        cruise.sailingRoute.contains("Goa", ignoreCase = true) -> R.drawable.goa_hero
                        cruise.sailingRoute.contains("Kochi", ignoreCase = true) || cruise.sailingRoute.contains("Kerala", ignoreCase = true) -> R.drawable.kerala_hero
                        cruise.sailingRoute.contains("Lakshadweep", ignoreCase = true) -> R.drawable.goa_hero
                        else -> R.drawable.kerala_hero
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                                Image(
                                    painter = painterResource(id = cruiseImageRes),
                                    contentDescription = cruise.cruiseName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                                ) {
                                    Text(
                                        text = "★ ${cruise.rating} Luxury Rated",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(cruise.cruiseName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Text(
                                    "${cruise.sailingRoute} • ${cruise.duration} • ${cruise.shipClass}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Starting from", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("₹${cruise.price} / cabin", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                    }

                                    Button(
                                        onClick = {
                                            val b = TripBooking(
                                                bookingId = "WL-CR-${(10000..99999).random()}",
                                                destinationId = "cruise",
                                                destinationTitle = "Cruise: ${cruise.cruiseName}",
                                                duration = "${cruise.sailingRoute} • ${cruise.duration}",
                                                travellerName = "",
                                                email = "",
                                                phone = "",
                                                adults = passengers,
                                                children = 0,
                                                paymentMethod = "Online Card / UPI",
                                                totalAmount = cruise.price,
                                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                                status = "Confirmed",
                                                imageRes = cruiseImageRes
                                            )
                                            viewModel.createDirectBooking(b) { confirmedBooking = b }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Reserve Cabin", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 8. FOREX CARD & CURRENCY BOOKING SCREEN
// =============================================================
@Composable
fun ForexBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }

    val forexItems = listOf(
        Triple("WonderLight Borderless Smart Card", "Zero Markup Card • USD, EUR, GBP, AED, THB • Instant App Reload", 0),
        Triple("Foreign Currency Banknotes Delivery", "Mint Bank Sealed Cash • Same-Day Doorstep Dispatch", 150)
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Forex Cards & Currency",
                subtitle = "Zero forex markup & genuine mint currency doorstep delivery",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(forexItems) { (name, desc, fee) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                                    Text("Zero Markup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            Text(desc, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 6.dp))

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Issuance Fee", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text(if (fee == 0) "FREE" else "₹$fee", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                }

                                Button(
                                    onClick = {
                                        val b = TripBooking(
                                            bookingId = "WL-FX-${(10000..99999).random()}",
                                            destinationId = "forex",
                                            destinationTitle = name,
                                            duration = "24h Doorstep Delivery",
                                            travellerName = "Verified Cardholder",
                                            email = "forex@wonderlight.com",
                                            phone = "+91 98765 43210",
                                            adults = 1,
                                            children = 0,
                                            paymentMethod = "Online UPI",
                                            totalAmount = fee,
                                            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            status = "Confirmed",
                                            imageRes = R.drawable.hero_flight_service
                                        )
                                        viewModel.createDirectBooking(b) { confirmedBooking = b }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Order Forex Card", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}

// =============================================================
// 9. TRAVEL INSURANCE BOOKING SCREEN
// =============================================================
@Composable
fun InsuranceBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }

    val plans = listOf(
        Triple("International Elite Shield", "Medical Cover $500,000 USD • Baggage & Flight Delay Covered", 180 to "TATA AIG & Digit"),
        Triple("Bharat Domestic Explorer", "Medical Cover ₹10,00,000 • Trekking & Sports Included", 39 to "ICICI Lombard")
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            UniversalServiceTopBar(
                title = "Travel Insurance",
                subtitle = "Instant cashless policy issuance with 24x7 global support",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(plans) { (name, details, priceAndUnderwriter) ->
                    val (pricePerDay, underwriter) = priceAndUnderwriter
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEBF3FF)) {
                                    Text(underwriter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WLBluePrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            Text(details, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 6.dp))

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Premium", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text("₹$pricePerDay / day", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WLBluePrimary)
                                }

                                Button(
                                    onClick = {
                                        val b = TripBooking(
                                            bookingId = "WL-INS-${(10000..99999).random()}",
                                            destinationId = "insurance",
                                            destinationTitle = "Policy: $name",
                                            duration = "7 Days Coverage • $underwriter",
                                            travellerName = "Insured Traveller",
                                            email = "insured@wonderlight.com",
                                            phone = "+91 98765 43210",
                                            adults = 1,
                                            children = 0,
                                            paymentMethod = "Online UPI",
                                            totalAmount = pricePerDay * 7,
                                            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                            status = "Confirmed",
                                            imageRes = R.drawable.hero_hotel_service
                                        )
                                        viewModel.createDirectBooking(b) { confirmedBooking = b }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WLBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Issue Policy", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onNavigateToTrips()
                }
            )
        }
    }
}
