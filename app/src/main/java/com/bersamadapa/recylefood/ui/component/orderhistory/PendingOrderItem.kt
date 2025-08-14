package com.bersamadapa.recylefood.ui.component.orderhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.Order
import com.bersamadapa.recylefood.ui.screen.formatCurrency
import com.bersamadapa.recylefood.ui.screen.startPaymentActivity

@Composable
fun PendingOrderItem(
    order: Order,
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Optional: Handle row click */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFF6F6F6), Color(0xFFFFFFFF))
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restaurant logo or fallback image
            val mysteryBoxData = order.mysteryBoxsData?.firstOrNull()
            val restaurantData = mysteryBoxData?.restaurantData

            Image(
                painter = rememberAsyncImagePainter(
                    model = restaurantData?.profilePicture?.url,
                    placeholder = painterResource(R.drawable.loading_placeholder),
                    error = painterResource(R.drawable.mystrey_box)
                ),
                contentDescription = restaurantData?.name ?: "Restaurant Logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Restaurant name
                Text(
                    text = restaurantData?.name ?: "Unknown Restaurant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price with fallback
                Text(
                    text = order.mysteryBoxsData?.firstOrNull()?.price?.let { formatCurrency( it.toDouble()) } ?: "N/A",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Payment button
            Button(
                onClick = { order.tokenMidtrans?.let { startPaymentActivity(context, it) } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Bayar Sekarang",
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
