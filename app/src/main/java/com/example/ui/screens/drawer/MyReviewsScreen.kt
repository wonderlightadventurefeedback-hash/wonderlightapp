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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WLBackgroundLight
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLStarGold
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.theme.WLTextPrimaryLight
import com.example.ui.theme.WLTextSecondaryLight
import com.example.ui.viewmodel.TravelViewModel

@Composable
fun MyReviewsScreen(
    viewModel: TravelViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    val reviews by viewModel.userReviews.collectAsState()
    var showWriteDialog by remember { mutableStateOf(false) }

    var newTripTitle by remember { mutableStateOf("") }
    var newRating by remember { mutableStateOf(5) }
    var newComment by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My Reviews & Ratings",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLTextPrimaryLight
                    )
                    Text(
                        text = "Verified traveller feedback & experiences",
                        fontSize = 11.sp,
                        color = WLTextSecondaryLight
                    )
                }

                Button(
                    onClick = { showWriteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Write", fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("4.9", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WLTextPrimaryLight)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Filled.Star, contentDescription = null, tint = WLStarGold, modifier = Modifier.size(24.dp))
                            }
                            Text("Overall Traveller Score", fontSize = 12.sp, color = WLTextSecondaryLight)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = WLTealLight
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("${reviews.size}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WLTealPrimary)
                                Text("Trip Reviews", fontSize = 10.sp, color = WLTealPrimary)
                            }
                        }
                    }
                }
            }

            // Reviews List
            items(reviews, key = { it.id }) { rev ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rev.destinationTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WLTextPrimaryLight)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Booking • ${rev.userName}", fontSize = 11.sp, color = WLTextSecondaryLight)
                                }
                            }

                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (i <= rev.rating) WLStarGold else Color.LightGray,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = rev.comment,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Reviewed on ${rev.date}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showWriteDialog) {
        AlertDialog(
            onDismissRequest = { showWriteDialog = false },
            title = { Text("Write a Trip Review", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select your star rating:", fontSize = 12.sp, color = WLTextSecondaryLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { newRating = i }) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= newRating) WLStarGold else Color.LightGray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newTripTitle,
                        onValueChange = { newTripTitle = it },
                        label = { Text("Trip / Destination Name") },
                        placeholder = { Text("e.g. Goa Beachfront or Delhi Flight") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        label = { Text("Your Experience") },
                        placeholder = { Text("How was the stay, food, transport, or service?") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTripTitle.isNotBlank() && newComment.isNotBlank()) {
                            viewModel.submitReview(newTripTitle, newRating, newComment)
                            Toast.makeText(context, "Review submitted! Thank you for sharing your experience. ⭐", Toast.LENGTH_LONG).show()
                            showWriteDialog = false
                            newTripTitle = ""
                            newComment = ""
                            newRating = 5
                        } else {
                            Toast.makeText(context, "Please fill in Destination and Review details", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary)
                ) {
                    Text("Publish Review", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWriteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
