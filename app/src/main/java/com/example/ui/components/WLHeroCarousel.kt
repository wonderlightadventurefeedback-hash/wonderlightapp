package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.WLOrangeAccent
import com.example.ui.theme.WLTealPrimary
import kotlinx.coroutines.delay

data class HeroSlide(
    val destinationId: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val badge: String = "Popular"
)

@Composable
fun WLHeroCarousel(
    onExploreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = remember {
        listOf(
            HeroSlide("kashmir", "Explore Kashmir", "Snowy peaks, serene lakes, unforgettable moments", R.drawable.kashmir_hero, "Featured"),
            HeroSlide("goa", "Discover Goa", "Sunsets, beaches, coastal serenity", R.drawable.goa_hero, "Trending"),
            HeroSlide("kerala", "Misty Kerala", "Canals, tea gardens, backwaters", R.drawable.kerala_hero, "Best Seller"),
            HeroSlide("rajasthan", "Royal Rajasthan", "Forts, palaces, desert royalty", R.drawable.rajasthan_hero, "Heritage"),
            HeroSlide("manali", "Snowy Manali", "Pine forests, high altitude passes", R.drawable.manali_snow, "Adventure")
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto rotate every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % slides.size
        }
    }

    val currentSlide = slides[currentIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(210.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color(0x3308766E))
            .clip(RoundedCornerShape(26.dp))
            .clickable { onExploreClick(currentSlide.destinationId) }
            .testTag("hero_carousel_card")
    ) {
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "hero_slide_transition"
        ) { slide ->
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = slide.imageRes,
                    contentDescription = slide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark cinematic gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x55000000),
                                    Color(0xEE071B1B)
                                )
                            )
                        )
                )
            }
        }

        // Top right slide counter
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            color = Color(0x77000000),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "${currentIndex + 1}/${slides.size}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Bottom Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp)
        ) {
            Text(
                text = currentSlide.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = currentSlide.subtitle,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Explore Now button
                Button(
                    onClick = { onExploreClick(currentSlide.destinationId) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = WLTealPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier.testTag("hero_explore_button")
                ) {
                    Text(
                        text = "Explore Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow",
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Page indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.indices.forEach { index ->
                        val isActive = index == currentIndex
                        Box(
                            modifier = Modifier
                                .size(if (isActive) 16.dp to 6.dp else 6.dp to 6.dp)
                                .clip(CircleShape)
                                .background(if (isActive) WLOrangeAccent else Color.White.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

// Extension helper for size
private fun Modifier.size(size: Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp>): Modifier =
    this.width(size.first).height(size.second)
