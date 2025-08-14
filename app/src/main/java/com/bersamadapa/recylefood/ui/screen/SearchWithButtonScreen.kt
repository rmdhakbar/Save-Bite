package com.bersamadapa.recylefood.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.bersamadapa.recylefood.viewmodel.MysteryBoxFilter
import com.bersamadapa.recylefood.viewmodel.MysteryBoxListState
import com.bersamadapa.recylefood.viewmodel.MysteryBoxViewModel
import com.bersamadapa.recylefood.viewmodel.RestaurantDataState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@Composable
fun SearchWithButtonScreen(
    selectedSearchButton: String,
    navController: NavController
) {
    // Provide the repository and create the ViewModel using the factory
//

    val mysteryBoxRepository = RepositoryProvider.mysteryBoxRepository
    val factoryMysteryBox = ViewModelFactory { MysteryBoxViewModel(mysteryBoxRepository) }
    val viewModelMysteryBox: MysteryBoxViewModel = viewModel(factory = factoryMysteryBox)

    // Collect the current state of restaurant data
    // Observe mystery box state
    val mysteryBoxState by viewModelMysteryBox.mysteryBoxListState.collectAsState()


    // Collect the current state of restaurant data
    val context = LocalContext.current

    // State variables for selected button and search text
    var selectedButton by remember { mutableStateOf(selectedSearchButton) }
    var searchText by remember { mutableStateOf(selectedSearchButton) }

    // Trigger the restaurant fetching when the selected button changes
    // Trigger the mystery box fetching when the selected button changes
    LaunchedEffect(selectedButton) {
        val location = LocationHelper(context).getUserLocation()
        if (location != null) {
            viewModelMysteryBox.fetchAllMysteryBoxes(
                selectedFilter = when (selectedButton) {
                    "Resto Terdekat" -> MysteryBoxFilter.Nearest
                    "Resto Terbaik" -> MysteryBoxFilter.BestRated
                    "Mystery Murah" -> MysteryBoxFilter.Cheapest
                    "The Best Seller" -> MysteryBoxFilter.New
                    else -> MysteryBoxFilter.Nearest // Default filter
                },
                userLocation = location
            )
        }
    }

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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Set to 2 columns
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between columns
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between rows
            ) {
                // Horizontal buttons as a single grid item
                item(span = { GridItemSpan(2) }) { // Span the entire width (2 columns)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .offset(x = (-10).dp)
                    ) {
                        RestaurantButtonRow(
                            modifier = Modifier.padding(horizontal = 0.dp),
                            buttons = listOf(
                                selectedButton
                            ),
                            selectedButton = selectedButton,
                            onButtonClick = { selected ->
                                selectedButton = selected
                                searchText = selected
                            }
                        )
                    }
                }

                // Handle the state of mystery box data
                when (val state = mysteryBoxState) {
                    is MysteryBoxListState.Loading -> {
                        item(span = { GridItemSpan(2) }) { // Span the entire width for loading
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    is MysteryBoxListState.Success -> {
                        if (state.mysteryBoxes.isNotEmpty()) {
                            items(state.mysteryBoxes) { mysteryBox ->
                                FoodMysteryCard(mysteryBox, R.drawable.mysterybox, navController)
                            }
                        } else {
                            item(span = { GridItemSpan(2) }) { // Span the entire width for no data
                                Text(
                                    text = "No mystery boxes found.",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                    is MysteryBoxListState.Error -> {
                        item(span = { GridItemSpan(2) }) { // Span the entire width for error
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
