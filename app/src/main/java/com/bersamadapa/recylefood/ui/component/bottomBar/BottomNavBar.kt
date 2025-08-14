package com.bersamadapa.recylefood.ui.component.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.bersamadapa.recylefood.MainActivity
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.ui.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController, selectedTab: String) {
    val backgroundColor = Color(0xFF402F2C) // Primary color for the bottom bar
    val fabBackgroundColor = Color(0xFFFBEDBB) // Background color for the FAB
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        // Bottom Navigation Bar
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            containerColor = backgroundColor,
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        label = "Home",
                        icon = painterResource(id = R.drawable.ic_home2),
                        isSelected = selectedTab == "Home",
                        onClick = { navController.navigate(Screen.Dashboard.route) }
                    )

                    Spacer(modifier = Modifier.weight(0.1f)) // Space for FAB
                    NavItem(
                        icon = painterResource(id = R.drawable.order),
                        label = "Order",
                        isSelected = selectedTab == "OrderHistory",
                        onClick = { navController.navigate("orderHistory") }
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Space for FAB

                    NavItem(
                        icon = painterResource(id = R.drawable.ic_cart),
                        label = "Cart",
                        isSelected = selectedTab == "Cart",
                        onClick = { navController.navigate(Screen.CartScreen.route) }
                    )

                    Spacer(modifier = Modifier.weight(0.1f)) // Space for FAB
                    NavItem(
                        icon = Icons.Filled.Person,
                        label = "Profile",
                        isSelected = selectedTab == "Profile",
                        onClick = { navController.navigate(Screen.Profile.route) }
                    )
                }
            }
        )

        // Place the FAB manually with an offset
        FloatingActionButton(
            onClick = {

                (context as? MainActivity)?.launchQrScanner() // Call the QR scanner method
            },
            containerColor = fabBackgroundColor,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp), // Adjust height offset for FAB placement
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_qr),
                contentDescription = "Scan",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
