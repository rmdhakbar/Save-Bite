package com.bersamadapa.recylefood.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.orderhistory.OrderItem
import com.bersamadapa.recylefood.viewmodel.GetAllOrdersState
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun OrderHistory(navController: NavController, modifier: Modifier = Modifier) {
    val orderRepository = RepositoryProvider.orderRepository
    val factoryOrder = ViewModelFactory { OrderViewModel(orderRepository) }
    val viewModelOrder: OrderViewModel = viewModel(factory = factoryOrder)

    // Observe the state of fetching all orders
    val listOrderState by viewModelOrder.getHistoryOrdersState.collectAsState()

    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    // State for selected category and dropdown menu expansion state
    var selectedCategory by remember { mutableStateOf("All") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Trigger fetching all orders with a status filter (Done in this case)
    LaunchedEffect(userId, selectedCategory) {
        if (userId?.isNotEmpty() == true) {
            userId?.let { viewModelOrder.getHistoryOrders(userId = it, categoryFilter = selectedCategory) }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp, bottom = 75.dp)) {
        // Header
        Text(
            text = "History Order",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Category Chooser (Dropdown Menu)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
        ) {
            // Category Button
            TextButton(
                onClick = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { isDropdownExpanded = !isDropdownExpanded }
                    .padding(5.dp)
            ) {
                Text(
                    text = "Category: $selectedCategory",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            ) {
                // Dropdown Menu Item for All Categories
                DropdownMenuItem(
                    text = { Text("All Categories") },
                    onClick = {
                        selectedCategory = "All"
                        isDropdownExpanded = false
                    }
                )

                // Dropdown Menu Item for Donation
                DropdownMenuItem(
                    text = { Text("Donation") },
                    onClick = {
                        selectedCategory = "Donation"
                        isDropdownExpanded = false
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            selectedCategory = "Donation"
                            isDropdownExpanded = false
                        }
                )

                // Dropdown Menu Item for Personal
                DropdownMenuItem(
                    text = { Text("Personal") },
                    onClick = {
                        selectedCategory = "Personal"
                        isDropdownExpanded = false
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            selectedCategory = "Personal"
                            isDropdownExpanded = false
                        }
                )
                // Add more DropdownMenuItems for other categories if needed
            }
        }

        // Fetch and display orders based on the selected category
        when (listOrderState) {
            is GetAllOrdersState.Loading -> {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is GetAllOrdersState.Success -> {
                val orders = (listOrderState as GetAllOrdersState.Success).orders
                    .filter { order ->
                        // Filter orders based on category
                        selectedCategory == "All" || order.category == selectedCategory
                    }
                if (orders.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logo_save_bite),
                                contentDescription = "Empty",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No history orders found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    // Orders List
                    LazyColumn {
                        items(orders.size) { index ->
                            OrderItem(
                                order = orders[index],
                                onClick = { order ->
                                    // Navigate to the order detail screen, passing the order data

                                    navController.navigate("orderDetails/history/${order.id}")
                                }
                            )
                        }
                    }
                }
            }
            is GetAllOrdersState.Error -> {
                // Error State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: ${(listOrderState as GetAllOrdersState.Error).message}")
                }
            }
            is GetAllOrdersState.Idle -> {
                // Idle State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No history orders yet.")
                }
            }
        }
    }
}
