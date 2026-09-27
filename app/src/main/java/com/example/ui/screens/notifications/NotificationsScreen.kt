package com.example.ui.screens.notifications

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WLBackgroundLight
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLOrangeLight
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.theme.WLTextPrimaryLight
import com.example.ui.theme.WLTextSecondaryLight
import com.example.ui.viewmodel.TravelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String, // "Bookings", "Offers", "Alerts", "Updates"
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val isRead: Boolean = false
)

@Composable
fun NotificationsScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit = {},
    onNavigateToOffers: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    // Load live notifications from Supabase whenever this screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    val liveNotifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isNotificationsLoading.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Bookings", "Offers", "Alerts", "Updates")

    // Map Supabase notifications to UI NotificationItem
    val displayItems = remember(liveNotifications) {
        liveNotifications.map { notif ->
            val (category, icon, bgCol, tintCol) = when (notif.type.lowercase()) {
                "promotion", "offer" -> Quad(
                    "Offers",
                    Icons.Filled.CardGiftcard,
                    WLOrangeLight,
                    WLOrangeAccent
                )
                "booking_update", "booking" -> Quad(
                    "Bookings",
                    Icons.Filled.Flight,
                    WLTealLight,
                    WLTealPrimary
                )
                "alert", "urgent" -> Quad(
                    "Alerts",
                    Icons.Filled.NotificationsActive,
                    Color(0xFFFFECEB),
                    Color(0xFFE53935)
                )
                "welcome" -> Quad(
                    "Updates",
                    Icons.Filled.AutoAwesome,
                    Color(0xFFEDE7F6),
                    Color(0xFF673AB7)
                )
                else -> Quad(
                    "Updates",
                    Icons.Filled.Notifications,
                    WLTealLight,
                    WLTealPrimary
                )
            }

            val friendlyTime = formatTimestamp(notif.sentAt)

            NotificationItem(
                id = notif.id,
                title = notif.title,
                message = notif.body,
                timestamp = friendlyTime,
                category = category,
                icon = icon,
                iconBgColor = bgCol,
                iconTint = tintCol,
                isRead = notif.isRead
            )
        }
    }

    val filteredNotifications = remember(selectedFilter, displayItems) {
        if (selectedFilter == "All") displayItems
        else displayItems.filter { it.category == selectedFilter }
    }

    val unreadCount = displayItems.count { !it.isRead }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WLBackgroundLight)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(2.dp, CircleShape, spotColor = Color(0x20084D5C))
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, WLTealPrimary.copy(alpha = 0.2f), CircleShape)
                                .testTag("notification_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = WLTealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Notifications",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLTextPrimaryLight
                            )
                            if (unreadCount > 0) {
                                Text(
                                    text = "$unreadCount new alerts",
                                    fontSize = 11.sp,
                                    color = WLOrangeAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "All up to date",
                                    fontSize = 11.sp,
                                    color = WLTextSecondaryLight
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.loadNotifications()
                                Toast.makeText(context, "Checking for notifications...", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                tint = WLTealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        if (unreadCount > 0) {
                            TextButton(
                                onClick = {
                                    viewModel.markAllNotificationsAsRead()
                                    Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.DoneAll,
                                        contentDescription = null,
                                        tint = WLTealPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Mark read",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = WLTealPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WLTealPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = WLTextPrimaryLight
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = null
                        )
                    }
                }
            }
        }

        // Notification List Content
        if (isLoading && displayItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = WLTealPrimary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Loading real-time notifications...",
                        fontSize = 13.sp,
                        color = WLTextSecondaryLight
                    )
                }
            }
        } else if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsOff,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No notifications yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLTextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You're all caught up! Booking updates, promotions, and alerts sent by WonderLight will appear here.",
                        fontSize = 13.sp,
                        color = WLTextSecondaryLight,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { viewModel.loadNotifications() },
                        colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Refresh Feed", fontSize = 13.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x10084D5C))
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                viewModel.markNotificationAsRead(item.id)
                                when (item.category) {
                                    "Bookings" -> onNavigateToTrips()
                                    "Offers" -> onNavigateToOffers()
                                    else -> Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("notification_item_${item.id}"),
                        color = if (item.isRead) Color.White else Color(0xFFF9FCFC),
                        shape = RoundedCornerShape(18.dp),
                        border = if (item.isRead) {
                            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        } else {
                            androidx.compose.foundation.BorderStroke(1.2.dp, WLTealPrimary.copy(alpha = 0.35f))
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Category Icon Container
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(item.iconBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.category,
                                    tint = item.iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Content Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                        color = WLTextPrimaryLight,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (!item.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(WLOrangeAccent)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.message,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = if (item.isRead) WLTextSecondaryLight else Color(0xFF334155)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = item.iconBgColor.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = item.iconTint,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = item.timestamp,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
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

private data class Quad(
    val category: String,
    val icon: ImageVector,
    val bgCol: Color,
    val tintCol: Color
)

private fun formatTimestamp(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "Just now"
    return try {
        val clean = isoString.replace("Z", "+0000")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(clean) ?: Date()
        val diffMs = System.currentTimeMillis() - date.time
        val minutes = diffMs / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24
        when {
            minutes < 2 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
        }
    } catch (_: Exception) {
        "Recent"
    }
}
