package com.example.ui.screens.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Destination
import com.example.ui.components.WLDestinationCard
import com.example.ui.components.WLExploreByInterestSection
import com.example.ui.components.WLHeroCarousel
import com.example.ui.components.WLPrimaryBookingRow
import com.example.ui.components.WLSecondaryServiceRow
import com.example.ui.components.WLServiceRow
import com.example.ui.components.WLTopBar
import com.example.ui.components.WLUnifiedServicesRow
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.example.ui.viewmodel.TravelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TravelViewModel,
    onNavigateToExplore: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    onNavigateToService: (String) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit = onNavigateToProfile,
    onOpenSideMenu: () -> Unit = onNavigateToProfile,
    modifier: Modifier = Modifier
) {
    val destinations by viewModel.holidays.collectAsState()
    val trendingDestinations = remember(destinations) {
        destinations.filter { it.isTrending }
    }
    val weekendDestinations = remember(destinations) {
        val filtered = destinations.filter {
            it.id.contains("goa", ignoreCase = true) ||
            it.id.contains("rishikesh", ignoreCase = true) ||
            it.id.contains("kerala", ignoreCase = true) ||
            it.location.contains("Goa", ignoreCase = true) ||
            it.location.contains("Kerala", ignoreCase = true)
        }
        if (filtered.isNotEmpty()) filtered else destinations.take(3)
    }
    val savedIds by viewModel.savedDestinationIds.collectAsState()
    val isReloading by viewModel.isReloading.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Glowing moving border animation for search bar
    val infiniteTransition = rememberInfiniteTransition(label = "search_glow_border")
    val borderOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "border_offset"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val movingGlowBrush = Brush.linearGradient(
        colors = listOf(
            WLTealPrimary,
            Color(0xFF00E5FF), // Glowing Neon Cyan
            Color(0xFF14B8A6), // Radiant Teal
            WLOrangeAccent,    // Warm Orange Accent
            Color(0xFFFFB703), // Golden Glow
            Color(0xFF00E5FF), // Neon Cyan
            WLTealPrimary
        ),
        start = Offset(borderOffset, borderOffset * 0.4f),
        end = Offset(borderOffset + 400f, borderOffset * 0.4f + 300f),
        tileMode = TileMode.Repeated
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        viewModel.checkFirstLaunchGuestPrompt()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Constant App Top Bar (Stays fixed at the top, does NOT move or scroll away)
        WLTopBar(
            onVoiceClick = { viewModel.setVoiceActive(true) },
            onNotificationsClick = onNavigateToNotifications,
            onMenuClick = onOpenSideMenu
        )

        PullToRefreshBox(
            isRefreshing = isReloading,
            onRefresh = {
                viewModel.reloadAllData { success, msg ->
                    android.widget.Toast.makeText(context, "✨ $msg", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Greeting Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Hello, Traveller 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Where would you like to go today?",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Search Input Trigger with Glowing Moving Border
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = Color(0xFF00B4D8).copy(alpha = glowAlpha),
                        ambientColor = WLTealPrimary.copy(alpha = 0.2f)
                    )
                    .border(
                        width = 2.dp,
                        brush = movingGlowBrush,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onNavigateToSearch() }
                    .testTag("home_search_trigger_bar"),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search icon",
                        tint = WLTealPrimary,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Where do you want to go?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WLTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Filter",
                            tint = WLTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 1. All 12 Exact Illustrated Services Row (Flights, Hotels, Villas, Holidays, Trains, Buses, Cabs, Tours, Visa, Cruise, Forex, Insurance)
        item {
            WLUnifiedServicesRow(
                selectedServiceId = "flights",
                onServiceClick = onNavigateToService,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )
        }

        // 2. Explore by Interest Section (Nature, Beach, Culture, Adventure, Wildlife)
        item {
            WLExploreByInterestSection(
                onInterestClick = onNavigateToDestination,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // Hero Immersive Carousel
        item {
            WLHeroCarousel(
                onExploreClick = onNavigateToDestination,
                modifier = Modifier.padding(bottom = 22.dp)
            )
        }

        // Trending Destinations Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending Destinations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(
                    onClick = onNavigateToExplore,
                    modifier = Modifier.testTag("see_all_trending_button")
                ) {
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLTealPrimary
                    )
                }
            }
        }

        // Horizontal Carousel of Trending Destinations
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 22.dp)
            ) {
                items(trendingDestinations, key = { it.id }) { dest ->
                    val isSaved = savedIds.contains(dest.id)
                    WLDestinationCard(
                        destination = dest,
                        isSaved = isSaved,
                        onCardClick = { onNavigateToDestination(dest.id) },
                        onSaveToggle = { viewModel.toggleWishlist(dest.id) }
                    )
                }
            }
        }

        // Gemini AI Travel Intelligence Hero Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x3308766E))
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onNavigateToAI() }
                    .testTag("gemini_ai_hero_banner"),
                color = WLTealPrimary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(WLTealPrimary, Color(0xFF044E49))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WLOrangeAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "AI Sparkle",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "WonderLight AI Intelligence",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Live Voice • Google Maps Grounding • Deep Thinking",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Text(
                            text = "Get real-time high thinking itineraries, secret scenic stops, and voice recommendations for any Indian destination.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 10.dp, bottom = 12.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Ask AI Assistant →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Weekend Escapes Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Weekend Escapes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Quick 2-4 day getaways for spontaneous adventures",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Editorial Weekend Cards
                weekendDestinations.forEach { dest ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0x1A08766E))
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onNavigateToDestination(dest.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                AsyncImage(
                                    model = dest.imageRes,
                                    contentDescription = dest.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dest.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dest.duration,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "₹${dest.price} / person",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WLTealPrimary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WLOrangeAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "View →",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WLOrangeAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
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
}

