package com.bersamadapa.recylefood.ui.component.bottomBar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NavItem(
    icon: Any, // Accept either Painter or ImageVector
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val iconSize = 24.dp // Define a consistent size for icons
        when (icon) {
            is ImageVector -> Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFFFDB623) else Color.White,
                modifier = Modifier.size(iconSize)
            )
            is Painter -> Icon(
                painter = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFFFDB623) else Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}
