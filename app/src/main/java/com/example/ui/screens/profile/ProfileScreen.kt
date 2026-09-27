package com.example.ui.screens.profile

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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

@Composable
fun ProfileScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToWallet: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val bookings by viewModel.allBookings.collectAsState()
    val savedIds by viewModel.savedDestinationIds.collectAsState()
    val formState by viewModel.bookingForm.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val userAvatarUri by viewModel.userAvatarUri.collectAsState()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadUserProfilePicture(context, it)
        }
    }

    val displayName = currentUser?.fullName ?: (if (isAuthenticated) "Verified Traveller" else "WonderLight Explorer")
    val displayEmail = currentUser?.email ?: "Sign in to manage bookings & sync tickets"
    val initials = if (displayName.length >= 2) displayName.substring(0, 2).uppercase() else "WL"

    var notificationsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WLBackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Screen Title: "Profile" with Back Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(2.dp, CircleShape, spotColor = Color(0x20084D5C))
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, WLTealPrimary.copy(alpha = 0.2f), CircleShape)
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = WLTealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLTextPrimaryLight
                )
            }
        }

        // User Profile Header Card (Screen 11)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x22084D5C))
                    .testTag("profile_user_card"),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar with user photo / initials & camera upload button & verified badge
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(WLOrangeAccent, Color(0xFFE65100))
                                    )
                                )
                                .border(3.dp, WLTealPrimary.copy(alpha = 0.35f), CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") }
                                .testTag("profile_avatar_box"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!userAvatarUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = userAvatarUri,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = initials,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Camera icon action button on bottom-right to select/change picture
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(WLTealPrimary)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") }
                                .testTag("btn_change_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Upload Profile Photo",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Verified tick badge on top-right if authenticated
                        if (isAuthenticated) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(WLTealPrimary)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = "Verified User",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick "Change Photo" / "Remove Photo" actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (userAvatarUri.isNullOrBlank()) "📷 Add Profile Photo" else "📷 Change Photo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WLTealPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(WLTealLight)
                                .clickable { photoPickerLauncher.launch("image/*") }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                        if (!userAvatarUri.isNullOrBlank()) {
                            Text(
                                text = "Remove",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFDC2626),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .clickable { viewModel.removeUserProfilePicture(context) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLTextPrimaryLight
                    )

                    Text(
                        text = displayEmail,
                        fontSize = 13.sp,
                        color = WLTextSecondaryLight,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Supabase Sign In / Sign Out Action
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAuthenticated) Color(0xFFFEF2F2) else WLTealLight,
                        modifier = Modifier.clickable {
                            if (isAuthenticated) {
                                viewModel.signOutFromSupabase()
                                Toast.makeText(context, "Signed out of Supabase", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.openAuthSheet("Sign in or create your Supabase account to manage bookings.")
                            }
                        }
                    ) {
                        Text(
                            text = if (isAuthenticated) "Sign Out" else "⚡ Sign In / Register with Supabase",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAuthenticated) Color(0xFFDC2626) else WLTealPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Stats: Bookings, Saved, Tier
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WLTealLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onNavigateToTrips() }
                        ) {
                            Text(
                                text = if (isAuthenticated) "${bookings.size}" else "0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLTealPrimary
                            )
                            Text(
                                text = "My Trips",
                                fontSize = 11.sp,
                                color = WLTextSecondaryLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(26.dp)
                                .background(WLTealPrimary.copy(alpha = 0.2f))
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onNavigateToExplore() }
                        ) {
                            Text(
                                text = "${savedIds.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = WLOrangeAccent
                            )
                            Text(
                                text = "Saved",
                                fontSize = 11.sp,
                                color = WLTextSecondaryLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(26.dp)
                                .background(WLTealPrimary.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isAuthenticated) "Silver" else "Guest",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAuthenticated) WLTealPrimary else WLTextSecondaryLight
                            )
                            Text(
                                text = "Member Tier",
                                fontSize = 11.sp,
                                color = WLTextSecondaryLight
                            )
                        }
                    }
                }
            }
        }

        // Screen 11 Core Menu Options (5 Clean Cards with Chevron)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // 1. My Bookings
                Screen11MenuItem(
                    icon = Icons.Filled.Luggage,
                    iconBgColor = WLTealLight,
                    iconColor = WLTealPrimary,
                    title = "My Bookings",
                    subtitle = "Active, upcoming & completed trips",
                    onClick = onNavigateToTrips
                )

                // 2. Saved Destinations
                Screen11MenuItem(
                    icon = Icons.Filled.Favorite,
                    iconBgColor = WLOrangeLight,
                    iconColor = WLOrangeAccent,
                    title = "Saved Destinations",
                    subtitle = "Your curated bucket list wishlist",
                    onClick = onNavigateToExplore
                )

                // 3. Wallet & Payments
                Screen11MenuItem(
                    icon = Icons.Filled.AccountBalanceWallet,
                    iconBgColor = WLTealLight,
                    iconColor = WLTealPrimary,
                    title = "Wallet & Payments",
                    subtitle = if (isAuthenticated) "Balance ₹${"%,d".format(walletBalance)} • WonderCash" else "Sign in to activate WonderCash wallet",
                    onClick = {
                        if (isAuthenticated) {
                            onNavigateToWallet()
                        } else {
                            viewModel.openAuthSheet("Sign in to access your WonderCash wallet.")
                        }
                    }
                )

                // 4. Support
                Screen11MenuItem(
                    icon = Icons.Filled.HeadsetMic,
                    iconBgColor = WLOrangeLight,
                    iconColor = WLOrangeAccent,
                    title = "Customer Support",
                    subtitle = "24x7 Customer Care & Live Chat",
                    onClick = onNavigateToSupport
                )

                // 5. Settings
                Screen11MenuItem(
                    icon = Icons.Filled.Settings,
                    iconBgColor = WLTealLight,
                    iconColor = WLTealPrimary,
                    title = "Settings",
                    subtitle = "AI Engine, notifications & preferences",
                    onClick = onNavigateToSettings
                )
            }
        }

        // Gemini AI Smart Companion Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clickable { onNavigateToAI() }
                    .testTag("profile_ai_features_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WLTealPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = WLOrangeAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gemini Travel Companion",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Live AI planning, recommendations & voice chat",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Notification Toggle & Sign Out
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Notifications switch
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1A084D5C))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = WLTealPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Travel Alerts & Push Notifications",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = WLTextPrimaryLight
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WLOrangeAccent,
                                checkedTrackColor = WLTealPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // App Footnote
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WonderLight Adventure v1.0.0",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLTextSecondaryLight
                    )
                    Text(
                        text = "Collect Moments, Not Things",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = WLTealPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Screen11MenuItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x15084D5C))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x0F084D5C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded Icon Container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WLTextPrimaryLight
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = WLTextSecondaryLight
                )
            }

            // Right Chevron Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

