package com.bersamadapa.recylefood.ui.component.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RestaurantButtonRow(
    modifier: Modifier = Modifier,
    buttons: List<String>, // Pass the list of buttons as a parameter
    selectedButton: String, // Track the currently selected button
    onButtonClick: (String) -> Unit // Callback when a button is clicked
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .horizontalScroll(rememberScrollState()), // Allow horizontal scrolling
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { text ->
            Button(
                onClick = { onButtonClick(text) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (text == selectedButton) Color.DarkGray else Color.LightGray,
                    contentColor = if (text == selectedButton) Color.White else Color.Black
                ),
                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
            ) {
                Text(text = text, fontSize = 12.sp,
                    maxLines = 2, // Allow text to span two lines
                    overflow = TextOverflow.Ellipsis, // Ensure no overflow
                    textAlign = TextAlign.Center, // Center align the text
                    modifier = Modifier.width(70.dp) // Set a fixed width for the button
                )
            }
        }
    }
}

