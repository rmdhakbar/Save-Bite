package com.bersamadapa.recylefood.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.CustomSearchField
import com.bersamadapa.recylefood.ui.component.bottomBar.BottomNavBar
import com.bersamadapa.recylefood.ui.component.dashboard.*
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.viewmodel.RestaurantDataState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@Composable
fun SearchWithStringScreen(
    selectedSearchText: String,
    navController: NavController
) {
    // Provide the repository and create the ViewModel using the factory
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factory = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModel: RestaurantViewModel = viewModel(factory = factory)

    // Collect the current state of restaurant data
    val restaurantDataState by viewModel.restaurantListState.collectAsState()

    // State variables for selected button and search text
    var searchText by remember { mutableStateOf(selectedSearchText) }



    // Main UI
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp, top = 10.dp)
        ) {
            // Search bar and button row
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp, top = 20.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = "Search Restaurants",
                    navController = navController
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Title text with the current search text
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Pencarian Terkait $searchText",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Handle the state of restaurant data
                when (val state = restaurantDataState) {
                    is RestaurantDataState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) }
                    }

                    is RestaurantDataState.Success -> {
                        if (state.restaurants.isNotEmpty()) {
                            items(state.restaurants) { restaurant ->
                                RestaurantItem(restaurant, navController)
                            }
                        } else {
                            item {
                                Text(
                                    text = "No restaurants found.",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                    is RestaurantDataState.Error -> {
                        item {
                            Text(
                                text = "Error: ${state.message}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }

    }
}
