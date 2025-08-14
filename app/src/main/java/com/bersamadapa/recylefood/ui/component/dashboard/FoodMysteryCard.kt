package com.bersamadapa.recylefood.ui.component.dashboard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.MysteryBox
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FoodMysteryCard(
    mysteryBox:MysteryBox,
    @DrawableRes picture: Int,
    navController: NavController

) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(mysteryBox.price?.toDouble())

    Card(
        modifier = Modifier
            .size(200.dp)
            .clickable { navController.navigate("mysteryBox/${mysteryBox.id}") }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Remove default background
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top section with light beige background
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFFFF5CC)) // Light beige color
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Image
                Image(
                    painter = painterResource(id = picture),
                    contentDescription = "Menu Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDED4A1)), // Light icon background
                    contentScale = ContentScale.Fit
                )
            }

            // Bottom section with dark brown background
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF4D372A)) // Dark brown color
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = mysteryBox.name?: "Mystery Box",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                // Restaurant name
                Text(
                    text = mysteryBox.restaurantData?.name ?: "name",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                // Rating and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating with star icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star), // Replace with star icon resource
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = mysteryBox.restaurantData?.rating?.toString()?:"N/A",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        )
                    }

                    // Price
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
