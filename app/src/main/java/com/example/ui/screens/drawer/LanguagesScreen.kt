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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

data class LanguageOption(
    val code: String,
    val nativeName: String,
    val englishName: String
)

@Composable
fun LanguagesScreen(
    currentLanguage: String = "English (IN)",
    onLanguageSelected: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var selectedLang by remember { mutableStateOf(currentLanguage) }
    var searchQuery by remember { mutableStateOf("") }

    val languages = remember {
        listOf(
            LanguageOption("en", "English (India)", "English"),
            LanguageOption("hi", "हिन्दी", "Hindi"),
            LanguageOption("or", "ଓଡ଼ିଆ", "Odia"),
            LanguageOption("bn", "বাংলা", "Bengali"),
            LanguageOption("ta", "தமிழ்", "Tamil"),
            LanguageOption("te", "తెలుగు", "Telugu"),
            LanguageOption("mr", "मराठी", "Marathi"),
            LanguageOption("gu", "ગુજરાતી", "Gujarati"),
            LanguageOption("kn", "ಕನ್ನಡ", "Kannada"),
            LanguageOption("ml", "മലയാളം", "Malayalam"),
            LanguageOption("es", "Español", "Spanish"),
            LanguageOption("fr", "Français", "French"),
            LanguageOption("de", "Deutsch", "German"),
            LanguageOption("ar", "العربية", "Arabic")
        )
    }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) languages
        else languages.filter {
            it.englishName.contains(searchQuery, ignoreCase = true) ||
                    it.nativeName.contains(searchQuery, ignoreCase = true)
        }
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
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Select Language",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = WLTextPrimaryLight
                        )
                        Text(
                            text = "Choose your preferred interface language",
                            fontSize = 11.sp,
                            color = WLTextSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search language...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.code }) { lang ->
                val isSelected = selectedLang.contains(lang.englishName, ignoreCase = true) ||
                        selectedLang.contains(lang.nativeName, ignoreCase = true)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            selectedLang = "${lang.englishName} (${lang.nativeName})"
                            onLanguageSelected(selectedLang)
                            Toast.makeText(context, "Language set to ${lang.englishName} (${lang.nativeName})", Toast.LENGTH_SHORT).show()
                        }
                        .border(
                            1.dp,
                            if (isSelected) WLTealPrimary else Color(0xFFE2E8F0),
                            RoundedCornerShape(14.dp)
                        ),
                    color = if (isSelected) WLTealLight.copy(alpha = 0.5f) else Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lang.nativeName,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) WLTealPrimary else WLTextPrimaryLight
                            )
                            Text(
                                text = lang.englishName,
                                fontSize = 12.sp,
                                color = WLTextSecondaryLight
                            )
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

        // Apply Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        onLanguageSelected(selectedLang)
                        Toast.makeText(context, "Applied $selectedLang", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Apply Language", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
