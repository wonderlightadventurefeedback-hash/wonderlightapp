package com.example.ui.screens.services

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TripBooking
import com.example.ui.theme.WLBluePrimary
import com.example.ui.theme.WLTealPrimary
import com.example.ui.viewmodel.TravelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrainBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onBookTrain: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("One Way") }
    var fromStation by remember { mutableStateOf("Bhubaneswar (BBS)") }
    var toStation by remember { mutableStateOf("Kolkata (HWH)") }
    var travelDate by remember { mutableStateOf("Tomorrow, 10:00 AM") }
    var trainClass by remember { mutableStateOf("All Classes") }
    var passengers by remember { mutableIntStateOf(1) }
    var hasSearched by remember { mutableStateOf(false) }

    var confirmedBooking by remember { mutableStateOf<TripBooking?>(null) }
    val searchResults by viewModel.trainSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingTrains.collectAsState()

    val quickRoutes = listOf(
        "Bhubaneswar" to "Kolkata",
        "Puri" to "Bhubaneswar",
        "Delhi" to "Jaipur",
        "Mumbai" to "Pune",
        "Bengaluru" to "Chennai",
        "Ahmedabad" to "Mumbai"
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_train_service),
                        contentDescription = "Train Booking Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x8805263B), Color(0x33000000), Color(0xCC084D5C))
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = "INDIAN RAILWAYS & EXPRESS TRANSIT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Book Train Tickets",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Direct Supabase live routes, IRCTC confirmed seats & instant reservation",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Quick Route Chips
            item {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Text(
                        text = "Popular Direct Routes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickRoutes) { (from, to) ->
                            val isMatch = fromStation.contains(from, ignoreCase = true) && toStation.contains(to, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isMatch) Color(0xFF1E88E5) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isMatch) Color(0xFF1E88E5) else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable {
                                    fromStation = "$from Station"
                                    toStation = "$to Junction"
                                    viewModel.searchTrains(from, to)
                                    hasSearched = true
                                }
                            ) {
                                Text(
                                    text = "$from ➔ $to",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isMatch) Color.White else Color(0xFF0F172A),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Train Search Form Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1E1E88E5)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Where do you want to travel?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // From Station Editable Field
                        OutlinedTextField(
                            value = fromStation,
                            onValueChange = { fromStation = it },
                            label = { Text("From Station / City") },
                            leadingIcon = {
                                Icon(Icons.Filled.Train, contentDescription = null, tint = Color(0xFF1E88E5))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedIndicatorColor = Color(0xFF1E88E5),
                                unfocusedIndicatorColor = Color(0xFFCBD5E1)
                            )
                        )

                        // Swap Button Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 3.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        val temp = fromStation
                                        fromStation = toStation
                                        toStation = temp
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.SwapVert, "Swap", tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        // To Station Editable Field
                        OutlinedTextField(
                            value = toStation,
                            onValueChange = { toStation = it },
                            label = { Text("To Station / Destination") },
                            leadingIcon = {
                                Icon(Icons.Filled.Train, contentDescription = null, tint = Color(0xFF059669))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedIndicatorColor = Color(0xFF1E88E5),
                                unfocusedIndicatorColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Travel Date & Passengers Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Date
                            Card(
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CalendarMonth, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Travel Date", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(travelDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }
                            }

                            // Passengers Counter
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Group, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Passengers", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { if (passengers > 1) passengers-- },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.Remove, null, modifier = Modifier.size(16.dp))
                                        }
                                        Text("$passengers", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        IconButton(
                                            onClick = { if (passengers < 6) passengers++ },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Search Button
                        Button(
                            onClick = {
                                hasSearched = true
                                viewModel.searchTrains(fromStation, toStation)
                                Toast.makeText(context, "Searching trains $fromStation ➔ $toStation...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = Color(0x331E88E5)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Searching Live Trains...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Icon(Icons.Filled.Search, "Search", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Search Trains", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Results Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (hasSearched) "Matching Trains (${searchResults.size})" else "Featured Express Routes",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "IRCTC Certified Live Schedule & Instant Confirmation",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Dynamic Train Results from Supabase
            if (searchResults.isEmpty() && !isSearching) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Train, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No trains found for '$fromStation' to '$toStation'", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text("Try searching popular routes like Bhubaneswar to Kolkata or Delhi to Jaipur.", fontSize = 12.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            } else {
                items(searchResults) { train ->
                    val totalPrice = train.price * passengers
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Train Name & Type Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Train, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(train.trainName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                                        Text("#${train.trainNumber} • Runs Daily", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Text("✓ Available", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Timings & Stations Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(train.departureTime, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(train.origin, fontSize = 12.sp, color = Color(0xFF64748B))
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(train.duration, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E88E5))
                                    HorizontalDivider(
                                        modifier = Modifier.width(60.dp).padding(vertical = 4.dp),
                                        thickness = 1.5.dp,
                                        color = Color(0xFF90CAF9)
                                    )
                                    Text("Direct", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(train.arrivalTime, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(train.destination, fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Available Classes Row
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(train.classes) { cl ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Text(cl, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Price & Book Button Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Fare for $passengers passenger(s)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text("₹$totalPrice", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E88E5))
                                }

                                Button(
                                    onClick = {
                                        val b = TripBooking(
                                            bookingId = "WL-TR-${(10000..99999).random()}",
                                            destinationId = "train",
                                            destinationTitle = "Train: ${train.trainName} (#${train.trainNumber})",
                                            duration = "${train.origin} ➔ ${train.destination} • ${train.departureTime} - ${train.arrivalTime}",
                                            travellerName = "Verified Passenger",
                                            email = "passenger@wonderlight.com",
                                            phone = "+91 98765 43210",
                                            adults = passengers,
                                            children = 0,
                                            paymentMethod = "Online UPI / IRCTC",
                                            totalAmount = totalPrice,
                                            bookingDate = travelDate,
                                            status = "Confirmed",
                                            imageRes = R.drawable.hero_train_service
                                        )
                                        viewModel.createDirectBooking(b) {
                                            confirmedBooking = b
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Book Ticket", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Why Book With Us
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Why Book Train Tickets on WonderLight?", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val perks = listOf(
                            Pair(Icons.Filled.AccessTime, "Real-time\nSeats"),
                            Pair(Icons.Filled.Lock, "Secure\nPayments"),
                            Pair(Icons.Filled.ElectricBolt, "Instant\nTicket"),
                            Pair(Icons.Filled.Headphones, "24/7\nSupport")
                        )

                        perks.forEach { (icon, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEBF5FF),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(icon, label, tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = Color(0xFF334155),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Confirmation Dialog
        confirmedBooking?.let { b ->
            UniversalBookingConfirmationDialog(
                booking = b,
                onDismiss = { confirmedBooking = null },
                onViewTrips = {
                    confirmedBooking = null
                    onBookTrain()
                }
            )
        }
    }
}
