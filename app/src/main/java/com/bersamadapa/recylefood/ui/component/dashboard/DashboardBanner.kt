package com.bersamadapa.recylefood.ui.component.dashboard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CarouselBannerAutoSlide(banners: List<BannerData>) {
    val listState = rememberLazyListState()
    val itemCount = banners.size - 1
    // Auto-scroll effect with looping
    LaunchedEffect(listState) {
        while (true) {
            delay(5000) // 5 seconds delay
            val currentIndex = listState.firstVisibleItemIndex
            val nextIndex = currentIndex + 1

            // Scroll to the next item
            listState.animateScrollToItem(nextIndex)

            // Check if we've reached the last item
            if (nextIndex == itemCount) {
                delay(5000) // 5 seconds delay
                // Reset to the first item without animation for a seamless loop
                listState.animateScrollToItem(0)
            }
        }
    }
    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp) // Remove spacing to avoid gaps
    ) {
        items(banners) { banner ->
            DashboardBanner(
                title = banner.title,
                subtitle = banner.subtitle,
                imageRes = banner.imageRes
            )
        }
    }
}

@Composable
fun DashboardBanner(title: String, subtitle: String, @DrawableRes imageRes: Int) {
    Box(
        modifier = Modifier
            .width(380.dp)
            .height(180.dp) // Adjust height as needed
    ) {
        // Background Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Banner Image",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop // Crop the image to fit without distortion
        )

    }
}

data class BannerData(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int
)
