package com.bersamadapa.recylefood.ui.component.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bersamadapa.recylefood.R

@Composable
fun FloatingButton(
    coinCount: Int,
    onLeaderboardClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(0xFF4D372A), shape = CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coin Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_coin), // Replace with your coin icon
            contentDescription = "Coin Icon",
            tint = Color.Yellow,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Coin Count
        Text(
            text = "$coinCount",
            style = TextStyle(fontSize = 16.sp, color = Color.White)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Divider
        HorizontalDivider(
            color = Color.Gray,
            modifier = Modifier
                .height(24.dp)
                .width(1.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Leaderboard Button


        Icon(
            painter = painterResource(id = R.drawable.ic_leaderboard), // Replace with your leaderboard icon
            contentDescription = "Leaderboard Icon",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Leaderboard",
            style = TextStyle(fontSize = 16.sp, color = Color.White),
            modifier = Modifier.clickable { onLeaderboardClick() } // Make text clickable
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FloatingButtonPreview() {
    FloatingButton(
        coinCount = 123,
        onLeaderboardClick = { /* Handle click */ }
    )
}