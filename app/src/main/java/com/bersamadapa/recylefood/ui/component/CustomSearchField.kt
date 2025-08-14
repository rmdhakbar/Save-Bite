package com.bersamadapa.recylefood.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController

@Composable
fun CustomSearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search",
    navController: NavController // Add NavController for navigation
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // Adjust height to match the design
            .clip(RoundedCornerShape(25.dp)) // Fully rounded corners
            .background(Color(0xFFE0E0E0)), // Light gray background
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray // Icon color
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search // Set the "Search" button on the keyboard
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                // Handle the search action when the search button is pressed
                keyboardController?.hide() // Hide the keyboard after search
                navController.navigate("dashboard/search/$value") // Replace with your search screen route
            }
        ),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent, // Remove default background when focused
            unfocusedContainerColor = Color.Transparent, // Remove default background when unfocused
            focusedIndicatorColor = Color.Transparent, // Remove underline when focused
            unfocusedIndicatorColor = Color.Transparent, // Remove underline when unfocused
            cursorColor = Color.Black, // Cursor color
            focusedTextColor = Color.Black, // Input text color
            unfocusedTextColor = Color.Black, // Input text color when unfocused
            focusedPlaceholderColor = Color.Gray, // Placeholder color when focused
            unfocusedPlaceholderColor = Color.Gray // Placeholder color when unfocused
        ),
        textStyle = MaterialTheme.typography.bodySmall
    )
}
