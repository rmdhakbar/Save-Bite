package com.bersamadapa.recylefood.ui.component.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.Restaurant
import com.bersamadapa.recylefood.ui.screen.RestaurantDetailScreen

@Composable
fun RestaurantItem(restaurant: Restaurant, navController: NavController) {
    Card(
        onClick = {
            // Navigate to the RestaurantDetailScreen
            navController.navigate("restaurant_detail/${restaurant.id}")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 7.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)), // Increased rounded corners
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF594A42)) // Brown background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restaurant Logo/Icon
            // Restaurant Logo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(restaurant.profilePicture?.url)
                    .crossfade(true)
                    .placeholder(R.drawable.loading_placeholder) // Replace with your placeholder resource
                    .error(R.drawable.baked_goods_3) // Replace with your error resource
                    .build(),
                contentDescription = "Restaurant Logo",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(24.dp)), // Circular shape for the image
                contentScale = ContentScale.Crop
            )


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Restaurant Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDistance(restaurant.distance),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }

                // Restaurant Address/Description
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Rating Section
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star Icon",
                    tint = Color.Yellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = restaurant.rating?.toString() ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

private fun formatDistance(distance: Float?): String {
    if (distance == null) return "N/A"

    return if (distance < 1000) {
        // Format as meters
        "${distance.toInt()} m"
    } else {
        // Format as kilometers with one decimal place
        String.format("%.1f km", distance / 1000)
    }
}

