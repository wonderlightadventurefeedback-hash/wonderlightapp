package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Surfing
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLOrangeLight
import com.example.ui.theme.WLTealLight
import com.example.ui.theme.WLTealPrimary

data class ServiceItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

data class InterestCategory(
    val title: String,
    val icon: ImageVector,
    val destinationId: String
)

/**
 * Primary Booking Services Row (Flights, Hotels, Holidays, Trains, Cabs)
 */
@Composable
fun WLPrimaryBookingRow(
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryServices = listOf(
        ServiceItem("flights", "Flights", Icons.Filled.Flight),
        ServiceItem("hotels", "Hotels", Icons.Filled.Hotel),
        ServiceItem("holidays", "Holidays", Icons.Filled.BeachAccess, isHighlighted = true),
        ServiceItem("trains", "Trains", Icons.Filled.Train),
        ServiceItem("cabs", "Cabs", Icons.Filled.DirectionsCar)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        primaryServices.forEach { service ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .testTag("service_item_${service.id}")
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onServiceClick(service.id) }
                    .padding(vertical = 4.dp)
            ) {
                val isHoliday = service.id == "holidays"
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape,
                            spotColor = Color(0x10084D5C)
                        )
                        .clip(CircleShape)
                        .background(if (isHoliday) WLOrangeLight else WLTealLight)
                        .border(
                            width = 1.dp,
                            color = if (isHoliday) WLOrangeAccent.copy(alpha = 0.3f) else WLTealPrimary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val iconTint = if (isHoliday) WLOrangeAccent else WLTealPrimary
                    Icon(
                        imageVector = service.icon,
                        contentDescription = service.title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = service.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/**
 * Explore by Interest Section (Nature, Beach, Culture, Adventure, Wildlife)
 * Styled with soft pale cyan circles (#E2F4F6) and deep teal icons (#084D5C)
 */
@Composable
fun WLExploreByInterestSection(
    onInterestClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interests = listOf(
        InterestCategory("Nature", Icons.Filled.Landscape, "kashmir"),
        InterestCategory("Beach", Icons.Filled.Surfing, "goa"),
        InterestCategory("Culture", Icons.Filled.NaturePeople, "rajasthan"),
        InterestCategory("Adventure", Icons.Filled.NearMe, "manali"),
        InterestCategory("Wildlife", Icons.Filled.Pets, "kerala")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Explore by Interest",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            interests.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .testTag("interest_${item.title.lowercase()}")
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onInterestClick(item.destinationId) }
                        .padding(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(
                                elevation = 1.dp,
                                shape = CircleShape,
                                spotColor = Color(0x0C084D5C)
                            )
                            .clip(CircleShape)
                            .background(Color(0xFFE2F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Color(0xFF084D5C),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

/**
 * Secondary Services Row (Villas, Buses, Visa, Forex, Insurance)
 */
@Composable
fun WLSecondaryServiceRow(
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val secondaryServices = listOf(
        ServiceItem("villas", "Villas", Icons.Filled.House),
        ServiceItem("buses", "Buses", Icons.Filled.DirectionsBus),
        ServiceItem("visa", "Visa", Icons.Filled.AssignmentInd),
        ServiceItem("forex", "Forex", Icons.Filled.CurrencyExchange),
        ServiceItem("insurance", "Insurance", Icons.Filled.Security)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(secondaryServices) { service ->
            Row(
                modifier = Modifier
                    .testTag("secondary_service_${service.id}")
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onServiceClick(service.id) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.title,
                    tint = WLOrangeAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = service.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/**
 * Combined Service Row for backward compatibility
 */
@Composable
fun WLServiceRow(
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WLPrimaryBookingRow(onServiceClick = onServiceClick)
        Spacer(modifier = Modifier.height(14.dp))
        WLSecondaryServiceRow(onServiceClick = onServiceClick)
    }
}
