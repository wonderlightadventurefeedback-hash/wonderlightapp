package com.example.ui.screens.drawer

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLOrangeLight
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary
import com.example.ui.theme.WLTextPrimaryLight
import com.example.ui.theme.WLTextSecondaryLight

@Composable
fun CustomerSupportScreen(
    onBack: () -> Unit,
    onNavigateToAIChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var subject by remember { mutableStateOf("") }
    var bookingId by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            FaqItem(
                question = "How do I cancel my booking and get a refund?",
                answer = "You can cancel bookings under the 'Trips' tab in the app. Instant refunds are credited to your WonderCash Wallet in seconds, or to original payment method within 3-5 business days."
            ),
            FaqItem(
                question = "What is the baggage allowance on domestic flights?",
                answer = "Standard economy domestic flights include 15 kg check-in baggage and 7 kg cabin baggage per passenger unless otherwise specified in your ticket details."
            ),
            FaqItem(
                question = "Can I reschedule my hotel stay or villa booking?",
                answer = "Yes! You can reschedule hotel or villa stays up to 24 hours prior to check-in by contacting our 24/7 support helpline or submitting a request with your booking reference."
            ),
            FaqItem(
                question = "How do I get my boarding pass and hotel vouchers?",
                answer = "Boarding passes and hotel vouchers are automatically available in the app under 'Trips' → 'View Voucher', and sent directly to your registered email upon booking confirmation."
            )
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

                Column {
                    Text(
                        text = "Customer Support",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = WLTextPrimaryLight
                    )
                    Text(
                        text = "24/7 Dedicated Travel Assistance & Helpline",
                        fontSize = 11.sp,
                        color = WLTextSecondaryLight
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Channels Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Call Helpline
                    SupportChannelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Call,
                        title = "Call Helpline",
                        subtitle = "1800-425-8899",
                        badge = "Toll-Free",
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18004258899"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Helpline: 1800-425-8899", Toast.LENGTH_LONG).show()
                            }
                        }
                    )

                    // WhatsApp Support
                    SupportChannelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Chat,
                        title = "WhatsApp Chat",
                        subtitle = "+91 98765 43210",
                        badge = "Instant",
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919876543210?text=Hello%20WonderLight%20Support"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp: +91 98765 43210", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // AI Travel Concierge Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onNavigateToAIChat() }
                        .border(1.2.dp, WLTealPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    color = WLTealLight.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(WLTealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Travel Intelligence", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WLTealPrimary)
                            Text("Ask our smart assistant for itinerary changes, destination tips or instant answers.", fontSize = 11.sp, color = WLTextSecondaryLight)
                        }
                        Text("Chat →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = WLTealPrimary)
                    }
                }
            }

            // Submit Support Ticket Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = WLTealPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit a Support Ticket", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WLTextPrimaryLight)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Need personalized assistance? Send your issue directly to our priority support team.", fontSize = 11.sp, color = WLTextSecondaryLight)

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject / Issue Type") },
                            placeholder = { Text("e.g. Flight Rescheduling, Refund Status") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = bookingId,
                            onValueChange = { bookingId = it },
                            label = { Text("Booking Reference (Optional)") },
                            placeholder = { Text("e.g. WL-8245") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Detailed Description") },
                            placeholder = { Text("Tell us what happened so we can assist quickly...") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (subject.isNotBlank() && message.isNotBlank()) {
                                    val ticketId = "TKT-${(10000..99999).random()}"
                                    Toast.makeText(context, "Support Ticket #$ticketId submitted! An agent will respond in under 15 mins.", Toast.LENGTH_LONG).show()
                                    subject = ""
                                    bookingId = ""
                                    message = ""
                                    isSubmitted = true
                                } else {
                                    Toast.makeText(context, "Please fill in Subject and Description", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WLTealPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Support Request", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Frequently Asked Questions
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.QuestionAnswer, contentDescription = null, tint = WLOrangeAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WLTextPrimaryLight)
                }
            }

            items(faqs.size) { idx ->
                val faq = faqs[idx]
                var expanded by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { expanded = !expanded }
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = WLTextPrimaryLight,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = WLTealPrimary
                            )
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = faq.answer,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = WLTextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
private fun SupportChannelCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WLTealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = WLTealPrimary, modifier = Modifier.size(18.dp))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WLOrangeLight
                ) {
                    Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WLOrangeAccent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WLTextPrimaryLight)
            Text(subtitle, fontSize = 11.sp, color = WLTextSecondaryLight)
        }
    }
}
