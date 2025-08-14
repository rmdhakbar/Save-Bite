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
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmallCard(
    title: String,
    @DrawableRes picture: Int,
    onClick: () -> Unit // Accepts a lambda function for click handling
) {
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clickable { onClick() }, // Make the entire card clickable
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = picture), // Replace with your profile image
            contentDescription = "Menu Picture",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape) // Makes the image circular
                .background(Color.Transparent) // Background color for the profile picture
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = title, // Example: "Resto Terdekat"
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            fontSize = 12.sp,
            maxLines = 2, // Allow text to span two lines
            overflow = TextOverflow.Ellipsis, // Ensure no overflow
            textAlign = TextAlign.Center, // Center align the text
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(80.dp) // Set a fixed width for consistent wrapping
        )
    }
}
