package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.ui.viewmodel.TravelViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.WLBackgroundLight
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLOrangeLight
import com.example.ui.theme.WLStarGold
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.theme.WLTextPrimaryLight
import com.example.ui.theme.WLTextSecondaryLight

data class DrawerMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val badge: String? = null,
    val subtitle: String? = null,
    val isHighlighted: Boolean = false
)

@Composable
fun WLSideDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToLanguages: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToReviews: () -> Unit = {},
    onNavigateToRefer: () -> Unit = {},
    onNavigateToCountryCurrency: () -> Unit = {},
    viewModel: TravelViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser = viewModel?.currentUser?.collectAsState()?.value
    val isAuthenticated = viewModel?.isAuthenticated?.collectAsState()?.value ?: (currentUser != null)
    val userAvatarUri = viewModel?.userAvatarUri?.collectAsState()?.value

    val displayName = currentUser?.fullName?.takeIf { it.isNotBlank() } ?: (if (isAuthenticated) "Explorer" else "Guest Traveller")
    val displayEmail = currentUser?.email?.takeIf { it.isNotBlank() } ?: (if (isAuthenticated) "account@wonderlight.com" else "Sign in to book & save")
    val initials = if (displayName.length >= 2) displayName.substring(0, 2).uppercase() else "WL"
    val userReferralCode = "WONDER-" + (currentUser?.fullName?.split(" ")?.firstOrNull()?.uppercase()?.filter { it.isLetter() }?.takeIf { it.isNotBlank() } ?: "TRAVEL")

    val unreadNotifCount = viewModel?.unreadNotificationsCount?.collectAsState()?.value ?: 0
    val walletBal = viewModel?.walletBalance?.collectAsState()?.value ?: 0

    // Dialog states for interactive sidebar features
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showRateUsDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var showReviewsDialog by remember { mutableStateOf(false) }
    var showReferDialog by remember { mutableStateOf(false) }

    // User selected preferences
    var selectedLanguage by remember { mutableStateOf("English (IN)") }
    var selectedCountry by remember { mutableStateOf("India 🇮🇳") }
    var selectedCurrency by remember { mutableStateOf("INR (₹)") }
    var userRating by remember { mutableIntStateOf(5) }

    BackHandler(enabled = isOpen) {
        onDismiss()
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
                .testTag("side_drawer_scrim")
        ) {
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(320.dp)
                    .clickable(enabled = false) {} // Prevent clicking through drawer
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    ) {
                        // Top Header: App Branding + Close Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.wonderlight_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "WonderLight",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = WLTextPrimaryLight
                                    )
                                    Text(
                                        text = "ADVENTURE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = WLOrangeAccent,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WLBackgroundLight)
                                    .testTag("close_drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close Menu",
                                    tint = WLTextPrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // User Profile Header Card (Clickable to go to full Profile)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onDismiss()
                                    onNavigateToProfile()
                                }
                                .testTag("drawer_profile_header"),
                            color = WLTealLight.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(WLOrangeAccent, Color(0xFFE05A12)))),
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
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WLTextPrimaryLight
                                    )
                                    Text(
                                        text = displayEmail,
                                        fontSize = 11.sp,
                                        color = WLTextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAuthenticated) "View Profile →" else "Sign In / Register →",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = WLTealPrimary
                                    )
                                }
                            }
                        }

                        // Drawer Menu Items
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            // 1. Profile
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.Person,
                                    title = "Profile",
                                    subtitle = "Personal info, bookings & preferences",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToProfile()
                                    }
                                )
                            }

                            // 2. Notifications
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.Notifications,
                                    title = "Notifications",
                                    subtitle = "Live alerts, booking updates & deals",
                                    badge = if (unreadNotifCount > 0) "$unreadNotifCount new" else null,
                                    badgeColor = WLOrangeAccent,
                                    onClick = {
                                        onDismiss()
                                        onNavigateToNotifications()
                                    }
                                )
                            }

                            // 3. Wallet
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.AccountBalanceWallet,
                                    title = "Wallet",
                                    badge = if (isAuthenticated) "₹$walletBal" else null,
                                    badgeColor = WLTealPrimary,
                                    subtitle = if (isAuthenticated) "WonderCash balance & transactions" else "Sign in to activate WonderCash",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToWallet()
                                    }
                                )
                            }

                            // 4. Settings
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.Settings,
                                    title = "Settings",
                                    subtitle = "App preferences & security",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToSettings()
                                    }
                                )
                            }

                            // 5. Languages
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.Translate,
                                    title = "Languages",
                                    subtitle = "10+ native & global languages",
                                    badge = selectedLanguage,
                                    onClick = {
                                        onDismiss()
                                        onNavigateToLanguages()
                                    }
                                )
                            }

                            // 6. Customer Support
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.HeadsetMic,
                                    title = "Customer Support",
                                    subtitle = "24/7 Helpline, WhatsApp & AI Chat",
                                    badge = "24/7",
                                    badgeColor = WLTealPrimary,
                                    onClick = {
                                        onDismiss()
                                        onNavigateToSupport()
                                    }
                                )
                            }

                            // 7. My Reviews
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.RateReview,
                                    title = "My Reviews",
                                    badge = "4.9 ★",
                                    badgeColor = WLStarGold,
                                    subtitle = "Verified trip reviews & feedback",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToReviews()
                                    }
                                )
                            }

                            // 8. Refer & Earn
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.CardGiftcard,
                                    title = "Refer & Earn",
                                    badge = "Earn ₹500",
                                    badgeColor = WLOrangeAccent,
                                    subtitle = "Invite friends & get WonderCash",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToRefer()
                                    }
                                )
                            }

                            // 9. Country & Currency
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.Public,
                                    title = "Country & Currency",
                                    badge = selectedCountry,
                                    subtitle = "Regional currency & rates",
                                    onClick = {
                                        onDismiss()
                                        onNavigateToCountryCurrency()
                                    }
                                )
                            }

                            // 10. Rate Us
                            item {
                                DrawerItemRow(
                                    icon = Icons.Filled.StarRate,
                                    title = "Rate Us",
                                    subtitle = "Love WonderLight? Give us 5 stars",
                                    onClick = { showRateUsDialog = true }
                                )
                            }
                        }

                        // Drawer Footer
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "WonderLight Adventure v1.0.0",
                                fontSize = 11.sp,
                                color = WLTextSecondaryLight
                            )
                        }
                    }
                }
            }
        }
    }

    // --- INTERACTIVE DIALOGS ---

    // 1. Language Dialog
    if (showLanguageDialog) {
        val languages = listOf("English (IN)", "Hindi (हिंदी)", "Odia (ଓଡ଼ିଆ)", "Bengali (বাংলা)", "Tamil (தமிழ்)", "Telugu (తెలుగు)", "Marathi (मराठी)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Language updated to $lang", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lang, fontSize = 14.sp)
                            if (selectedLanguage == lang) {
                                Text("✓", color = WLTealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = WLTealPrimary)
                }
            }
        )
    }

    // 2. Country Dialog
    if (showCountryDialog) {
        val countries = listOf("India 🇮🇳", "UAE 🇦🇪", "Singapore 🇸🇬", "United Kingdom 🇬🇧", "United States 🇺🇸", "Malaysia 🇲🇾", "Thailand 🇹🇭")
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text("Select Country", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    countries.forEach { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCountry = country
                                    showCountryDialog = false
                                    Toast.makeText(context, "Country set to $country", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = country, fontSize = 14.sp)
                            if (selectedCountry == country) {
                                Text("✓", color = WLTealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialog = false }) {
                    Text("Close", color = WLTealPrimary)
                }
            }
        )
    }

    // 3. Currency Dialog
    if (showCurrencyDialog) {
        val currencies = listOf("INR (₹)", "USD ($)", "EUR (€)", "AED (د.إ)", "GBP (£)", "SGD ($)")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    currencies.forEach { cur ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCurrency = cur
                                    showCurrencyDialog = false
                                    Toast.makeText(context, "Currency set to $cur", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cur, fontSize = 14.sp)
                            if (selectedCurrency == cur) {
                                Text("✓", color = WLTealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close", color = WLTealPrimary)
                }
            }
        )
    }

    // 4. Rate Us Dialog
    if (showRateUsDialog) {
        AlertDialog(
            onDismissRequest = { showRateUsDialog = false },
            title = { Text("Rate WonderLight", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How was your travel booking experience?", fontSize = 13.sp, color = WLTextSecondaryLight)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { userRating = i }) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= userRating) WLStarGold else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = when (userRating) {
                            5 -> "⭐⭐⭐⭐⭐ Outstanding!"
                            4 -> "⭐⭐⭐⭐ Great experience"
                            3 -> "⭐⭐⭐ Good"
                            else -> "We'll do better!"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WLTealPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRateUsDialog = false
                        Toast.makeText(context, "Thank you for giving us $userRating stars! ⭐", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary)
                ) {
                    Text("Submit Rating", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateUsDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // 5. Customer Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Customer Support", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("We're here 24/7 to assist with your travel bookings and inquiries.", fontSize = 13.sp, color = WLTextSecondaryLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("📞 Toll-Free Helpline: 1800-425-8899", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = WLTealPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✉️ Email: support@wonderlight.com", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("💬 WhatsApp: +91 98765 43210", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSupportDialog = false
                        Toast.makeText(context, "Connecting to live travel agent...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary)
                ) {
                    Text("Call Now", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // 6. Wallet Dialog
    if (showWalletDialog) {
        AlertDialog(
            onDismissRequest = { showWalletDialog = false },
            title = { Text("WonderCash Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Surface(
                        color = WLTealLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Available Balance", fontSize = 11.sp, color = WLTextSecondaryLight)
                            Text("₹2,450.00", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WLTealPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• ₹1,000 Sign-up Welcome Bonus", fontSize = 12.sp, color = Color.DarkGray)
                    Text("• ₹1,450 Kashmir Trip Booking Cashback", fontSize = 12.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWalletDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary)
                ) {
                    Text("Use on Next Trip", color = Color.White)
                }
            }
        )
    }

    // 7. My Reviews Dialog
    if (showReviewsDialog) {
        AlertDialog(
            onDismissRequest = { showReviewsDialog = false },
            title = { Text("My Reviews (4)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("★ 5.0 - Dal Lake Houseboat Stay (Kashmir)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("\"Mesmerizing view and top hospitality!\"", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("★ 4.8 - Goa Sunset Cruise (Goa)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("\"Beautiful sunset with great music.\"", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showReviewsDialog = false }) {
                    Text("Close", color = WLTealPrimary)
                }
            }
        )
    }

    // 8. Refer & Earn Dialog
    if (showReferDialog) {
        AlertDialog(
            onDismissRequest = { showReferDialog = false },
            title = { Text("Refer & Earn ₹500", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Share your invite code with friends. When they book their first trip, you both get ₹500 WonderCash!", fontSize = 13.sp, color = WLTextSecondaryLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WLOrangeLight,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = userReferralCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLOrangeAccent,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReferDialog = false
                        Toast.makeText(context, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLOrangeAccent)
                ) {
                    Text("Copy Code", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReferDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun DrawerItemRow(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    badgeColor: Color = WLTealPrimary,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .testTag("drawer_item_${title.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WLTealLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = WLTealPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = WLTextPrimaryLight
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = WLTextSecondaryLight
                )
            }
        }

        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
        )
    }
}
