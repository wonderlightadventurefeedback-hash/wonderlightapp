package com.example.ui.screens.drawer

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WLBackgroundLight
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.theme.WLTextPrimaryLight
import com.example.ui.theme.WLTextSecondaryLight

data class CountryOption(val name: String, val flag: String, val dialCode: String, val currencyCode: String)
data class CurrencyOption(val code: String, val symbol: String, val name: String, val rate: String)

@Composable
fun CountryCurrencyScreen(
    currentCountry: String = "India 🇮🇳",
    currentCurrency: String = "INR (₹)",
    onCountrySelected: (String) -> Unit = {},
    onCurrencySelected: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCountry by remember { mutableStateOf(currentCountry) }
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }

    val countries = remember {
        listOf(
            CountryOption("India", "🇮🇳", "+91", "INR (₹)"),
            CountryOption("United Arab Emirates", "🇦🇪", "+971", "AED (د.إ)"),
            CountryOption("Singapore", "🇸🇬", "+65", "SGD ($)"),
            CountryOption("United Kingdom", "🇬🇧", "+44", "GBP (£)"),
            CountryOption("United States", "🇺🇸", "+1", "USD ($)"),
            CountryOption("Malaysia", "🇲🇾", "+60", "MYR (RM)"),
            CountryOption("Thailand", "🇹🇭", "+66", "THB (฿)"),
            CountryOption("Australia", "🇦🇺", "+61", "AUD ($)"),
            CountryOption("Canada", "🇨🇦", "+1", "CAD ($)"),
            CountryOption("Germany", "🇩🇪", "+49", "EUR (€)")
        )
    }

    val currencies = remember {
        listOf(
            CurrencyOption("INR", "₹", "Indian Rupee", "Base Currency"),
            CurrencyOption("USD", "$", "United States Dollar", "1 USD ≈ ₹83.50"),
            CurrencyOption("EUR", "€", "Euro", "1 EUR ≈ ₹91.20"),
            CurrencyOption("AED", "د.إ", "UAE Dirham", "1 AED ≈ ₹22.75"),
            CurrencyOption("GBP", "£", "British Pound Sterling", "1 GBP ≈ ₹108.40"),
            CurrencyOption("SGD", "$", "Singapore Dollar", "1 SGD ≈ ₹64.10"),
            CurrencyOption("AUD", "$", "Australian Dollar", "1 AUD ≈ ₹56.80"),
            CurrencyOption("CAD", "$", "Canadian Dollar", "1 CAD ≈ ₹61.30")
        )
    }

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
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, WLTealPrimary.copy(alpha = 0.2f), CircleShape)
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
                            text = "Regional Settings",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLTextPrimaryLight
                        )
                        Text(
                            text = "Set your local country and payment currency",
                            fontSize = 11.sp,
                            color = WLTextSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = WLTealPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = WLTealPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Country / Region", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Currency", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }
            }
        }

        // List Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTabIndex == 0) {
                items(countries, key = { it.name }) { item ->
                    val isSelected = selectedCountry.contains(item.name, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedCountry = "${item.name} ${item.flag}"
                                onCountrySelected(selectedCountry)
                                Toast.makeText(context, "Country set to ${item.name}", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, if (isSelected) WLTealPrimary else Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                        color = if (isSelected) WLTealLight.copy(alpha = 0.5f) else Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WLTextPrimaryLight)
                                    Text("${item.dialCode} • ${item.currencyCode}", fontSize = 11.sp, color = WLTextSecondaryLight)
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(WLTealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                items(currencies, key = { it.code }) { cur ->
                    val isSelected = selectedCurrency.contains(cur.code, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedCurrency = "${cur.code} (${cur.symbol})"
                                onCurrencySelected(selectedCurrency)
                                Toast.makeText(context, "Currency set to ${cur.code}", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, if (isSelected) WLTealPrimary else Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                        color = if (isSelected) WLTealLight.copy(alpha = 0.5f) else Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = WLTealLight,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(cur.symbol, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WLTealPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("${cur.code} - ${cur.name}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WLTextPrimaryLight)
                                    Text(cur.rate, fontSize = 11.sp, color = WLTextSecondaryLight)
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(WLTealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        onCountrySelected(selectedCountry)
                        onCurrencySelected(selectedCurrency)
                        Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Save & Apply Settings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
