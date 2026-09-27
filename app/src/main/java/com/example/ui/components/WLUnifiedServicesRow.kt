package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.WLTealPrimary

data class WLUnifiedServiceItem(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
)

val WL_ALL_SERVICES = listOf(
    WLUnifiedServiceItem("flights", "Flights", R.drawable.ic_svc_flights),
    WLUnifiedServiceItem("hotels", "Hotels", R.drawable.ic_svc_hotels),
    WLUnifiedServiceItem("villas", "Villas &\nHomestays", R.drawable.ic_svc_villas),
    WLUnifiedServiceItem("holidays", "Holiday\nPackages", R.drawable.ic_svc_holidays),
    WLUnifiedServiceItem("trains", "Trains", R.drawable.ic_svc_trains),
    WLUnifiedServiceItem("buses", "Buses", R.drawable.ic_svc_buses),
    WLUnifiedServiceItem("cabs", "Cabs", R.drawable.ic_svc_cabs),
    WLUnifiedServiceItem("tours", "Tours &\nAttractions", R.drawable.ic_svc_tours),
    WLUnifiedServiceItem("visa", "Visa", R.drawable.ic_svc_visa),
    WLUnifiedServiceItem("cruise", "Cruise", R.drawable.ic_svc_cruise),
    WLUnifiedServiceItem("forex", "Forex Card &\nCurrency", R.drawable.ic_svc_forex),
    WLUnifiedServiceItem("insurance", "Travel\nInsurance", R.drawable.ic_svc_insurance)
)

@Composable
fun WLUnifiedServicesRow(
    selectedServiceId: String = "flights",
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("unified_services_row"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        items(WL_ALL_SERVICES, key = { it.id }) { service ->
            val isSelected = service.id == selectedServiceId

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onServiceClick(service.id)
                    }
                    .padding(vertical = 4.dp)
                    .testTag("service_item_${service.id}")
            ) {
                // Circular 3D / Illustrated Icon
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(
                            elevation = if (isSelected) 6.dp else 2.dp,
                            shape = CircleShape,
                            spotColor = if (isSelected) WLTealPrimary.copy(alpha = 0.3f) else Color(0x18000000)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = service.iconRes),
                        contentDescription = service.title,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title Label (max 2 lines)
                Text(
                    text = service.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) WLTealPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Selected Blue / Teal Pill Indicator Line (as in Image 1)
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(36.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) WLTealPrimary else Color.Transparent)
                )
            }
        }
    }
}
