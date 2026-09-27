package com.example.ui.screens.services

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.viewmodel.TravelViewModel

data class TopHotelPick(
    val id: String,
    val name: String,
    val rating: String,
    val reviews: String,
    val location: String,
    val price: Int,
    val imageUrl: String
)

data class HotelCategoryItem(
    val title: String,
    val imageUrl: String
)

@Composable
fun HotelBookingScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToHotelDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Stays") }
    var destination by remember { mutableStateOf("Goa, India") }
    var checkInDate by remember { mutableStateOf("15 Oct 2025") }
    var checkOutDate by remember { mutableStateOf("18 Oct 2025") }
    var guestRoom by remember { mutableStateOf("2 Adults • 1 Room") }

    val liveHotelsState by viewModel.hotels.collectAsState()
    val hotelPicks = remember(liveHotelsState) {
        val liveList = liveHotelsState.map { h ->
            TopHotelPick(
                id = h.id,
                name = h.name,
                rating = "${h.rating}",
                reviews = "Verified",
                location = h.location,
                price = h.pricePerNight,
                imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600"
            )
        }
        liveList + listOf(
            TopHotelPick("h1", "Taj Holiday Village Resort & Spa", "4.5", "2.8k reviews", "Goa", 8499, "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600"),
            TopHotelPick("h2", "ITC Grand Chola", "4.7", "1.9k reviews", "Chennai", 7999, "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=600"),
            TopHotelPick("h3", "The Leela Palace", "4.8", "3.2k reviews", "Bengaluru", 12499, "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600"),
            TopHotelPick("h4", "Waterfront Resort & Spa", "4.9", "4.1k reviews", "Srinagar", 9999, "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=600")
        )
    }

    val categories = remember {
        listOf(
            HotelCategoryItem("Luxury", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=300"),
            HotelCategoryItem("Budget", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=300"),
            HotelCategoryItem("Resorts", "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=300"),
            HotelCategoryItem("Business", "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=300")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header (Image 2 Right Panel)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_hotel_service),
                    contentDescription = "Hotel Booking Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x8805263B), Color(0x33000000), Color(0xAA084D5C))
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
                        text = "Stay Somewhere Special",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Book Hotels Worldwide",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "From budget stays to luxury retreats",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Tabs: Stays, Homestays, Apartments
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Pair("Stays", Icons.Filled.Hotel),
                    Pair("Homestays", Icons.Filled.HolidayVillage),
                    Pair("Apartments", Icons.Filled.Apartment)
                ).forEach { (type, icon) ->
                    val isSelected = selectedTab == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedTab = type },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF1E88E5) else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, type, tint = if (isSelected) Color.White else Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }

        // Search Form Card (Image 2 Right Panel)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1E1E88E5)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Destination Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                destination = if (destination.contains("Goa")) "Manali, Himachal" else "Goa, India"
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, "Location", tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Destination", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(destination, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                        Icon(Icons.Filled.MyLocation, "Locate Me", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Check In / Check Out
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Check In
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CalendarMonth, "Check In", tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Check In", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(checkInDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }

                        VerticalDivider(modifier = Modifier.height(36.dp).padding(horizontal = 8.dp), color = Color(0xFFE2E8F0))

                        // Check Out
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CalendarMonth, "Check Out", tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Check Out", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(checkOutDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Guests & Rooms
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                guestRoom = if (guestRoom.contains("2 Adults")) "3 Adults • 2 Rooms" else "2 Adults • 1 Room"
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, "Guests", tint = Color(0xFF1E88E5), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Guests & Rooms", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(guestRoom, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                        Icon(Icons.Filled.ChevronRight, "Edit", tint = Color(0xFF94A3B8))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Searching hotels in $destination...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = Color(0x331E88E5)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Icon(Icons.Filled.Search, "Search", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Hotels", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Top Hotel Picks
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Top Hotel Picks", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("See All", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E88E5))
                }
            }
        }

        items(hotelPicks) { hotel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { onNavigateToHotelDetail() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = hotel.imageUrl,
                        contentDescription = hotel.name,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(hotel.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Filled.Star, "Rating", tint = WLOrangeAccent, modifier = Modifier.size(14.dp))
                            Text(" ${hotel.rating} (${hotel.reviews})", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text("📍 ${hotel.location}", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("₹${hotel.price} / night", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                    }
                    Button(
                        onClick = { onNavigateToHotelDetail() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Hotel Categories
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text("Hotel Categories", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories) { cat ->
                        Card(
                            modifier = Modifier
                                .width(96.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    Toast.makeText(context, "Selected ${cat.title} category", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = cat.imageUrl,
                                    contentDescription = cat.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = cat.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
