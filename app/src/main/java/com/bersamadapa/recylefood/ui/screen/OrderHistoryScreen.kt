package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bersamadapa.recylefood.ui.component.bottomBar.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Tabs with rounded edges
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 8.dp,
                        containerColor = Color(0xFFF1F1F1),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .padding(horizontal = 20.dp),
                                color = Color(0xFF4CAF50)
                            )
                        }
                    ) {
                        TabItem("Pending Order", selectedTab == 0) { selectedTab = 0 }
                        TabItem("Ongoing", selectedTab == 1) { selectedTab = 1 }
                        TabItem("History Order", selectedTab == 2) { selectedTab = 2 }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display content based on selected tab
                    when (selectedTab) {
                        0 -> {
                            // Ensure PendingOrders gets recomposed
                            androidx.compose.runtime.key(selectedTab) {
                                PendingOrders()
                            }
                        }
                        1 -> {
                            // Ensure OngoingOrders gets recomposed
                            androidx.compose.runtime.key(selectedTab) {
                                OngoingOrders(navController)
                            }
                        }
                        2 -> {
                            // Ensure OrderHistory gets recomposed
                            androidx.compose.runtime.key(selectedTab) {
                                OrderHistory(navController)
                            }
                        }
                    }
                }

                // Bottom Navigation Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    BottomNavBar(navController = navController, selectedTab = "OrderHistory")
                }
            }
        }
    )
}

@Composable
fun TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = {
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                color = if (selected) Color(0xFF4CAF50) else Color.Gray
            )
        },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                if (selected) Color(0xFFE8F5E9) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    )
}

@Composable
fun PlaceholderContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
