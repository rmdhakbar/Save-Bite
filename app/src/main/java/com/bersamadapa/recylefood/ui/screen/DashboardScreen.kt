package com.bersamadapa.recylefood.ui.screen

import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.CustomSearchField
import com.bersamadapa.recylefood.ui.component.dashboard.RestaurantItem
import com.bersamadapa.recylefood.ui.component.bottomBar.BottomNavBar
import com.bersamadapa.recylefood.ui.component.dashboard.BannerData
import com.bersamadapa.recylefood.ui.component.dashboard.CarouselBannerAutoSlide
import com.bersamadapa.recylefood.ui.component.dashboard.DashboardBanner
import com.bersamadapa.recylefood.ui.component.dashboard.FloatingButton
import com.bersamadapa.recylefood.ui.component.dashboard.SmallCard
import com.bersamadapa.recylefood.ui.component.dashboard.FoodMysteryCard
import com.bersamadapa.recylefood.ui.component.dashboard.RestaurantButtonRow
import com.bersamadapa.recylefood.ui.component.profile.ProfileMenuItem
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.viewmodel.MysteryBoxFilter
import com.bersamadapa.recylefood.viewmodel.MysteryBoxListState
import com.bersamadapa.recylefood.viewmodel.MysteryBoxViewModel
import com.bersamadapa.recylefood.viewmodel.RestaurantDataState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.UserDataState
import com.bersamadapa.recylefood.viewmodel.UserViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(navController: NavController) {

    // Provide the repository and create the ViewModel using the factory
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factory = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModel: RestaurantViewModel = viewModel(factory = factory)

    val mysteryBoxRepository = RepositoryProvider.mysteryBoxRepository
    val factoryMysteryBox = ViewModelFactory { MysteryBoxViewModel(mysteryBoxRepository) }
    val viewModelMysteryBox: MysteryBoxViewModel = viewModel(factory = factoryMysteryBox)

    // Collect the current state of restaurant data
    val restaurantDataState by viewModel.restaurantListState.collectAsState()
    // Observe mystery box state
    val mysteryBoxState by viewModelMysteryBox.mysteryBoxListState.collectAsState()

    val selectedTab = "Home"

    val userRepository = RepositoryProvider.userRepository
    val factoryUser = ViewModelFactory { UserViewModel(userRepository) }
    val viewModelUser: UserViewModel = viewModel(factory = factoryUser)
    val userDataState by viewModelUser.userDataState.collectAsState()
    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    if (userDataState is UserDataState.Idle && userId?.isNotEmpty() == true) {
        userId?.let { viewModelUser.fetchUserById(it) }
    }

    // Trigger the data fetching only when the state is Id

    val banners = listOf(
        BannerData("Welcome to Recycle Food", "Save food, save the planet!", R.drawable.banner_dashboard),
        BannerData("Welcome to Recycle Food", "Save food, save the planet!", R.drawable.banner2),
        BannerData("Get the Best Deals", "Discounts on every meal!", R.drawable.baked_goods_2),
        BannerData("Support Sustainability", "Join us in reducing waste!", R.drawable.baked_goods_3)
    )

    val context = LocalContext.current

    var selectedButton by remember { mutableStateOf("Terdekat") } // Default selected button
    var searchText by remember { mutableStateOf("") }



    LaunchedEffect(selectedButton) {
            val location = LocationHelper(context = context).getUserLocation()
            Log.d("DashboardScreen", "Fetching restaurants for location: $location")
            viewModel.fetchRestaurants(selectedButton, location)
    }

    if (restaurantDataState is RestaurantDataState.Idle) {
        LaunchedEffect(Unit) {
            val location = LocationHelper(context = context).getUserLocation()
            Log.d("DashboardScreen", "Fetching restaurants for location: $location")
            viewModel.fetchRestaurants(selectedButton, location)
        }

    }

    if (mysteryBoxState is MysteryBoxListState.Idle) {
        LaunchedEffect(Unit) {
            val location = LocationHelper(context = context).getUserLocation()
            Log.d("DashboardScreen", "Fetching restaurants for location: $location")
            if (location != null) {
                viewModelMysteryBox.fetchAllMysteryBoxes(MysteryBoxFilter.New,location)
            }

        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()


    ) {
        // Search Bar UI outside of the LazyColumn to keep it fixed
        // Box for handling content and positioning
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp, top = 10.dp)
        ) {

            Row (
                modifier = Modifier
                    .padding(bottom = 20.dp, end = 21.dp, start = 10.dp, top = 20.dp)
                    .align(Alignment.Start),
                verticalAlignment = Alignment.CenterVertically

            ){
                // Profile Picture
                Image(
                    painter = painterResource(id = R.drawable.logo_save_bite),
                    contentDescription = "Logo Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(16.dp))

                CustomSearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = "Search Restaurants",
                    navController = navController
                )

            }

            // LazyColumn for the content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {

                    Box(
                        modifier = Modifier
                            .offset(x = (-10).dp) // Use offset instead of padding
                            .height(150.dp) // Adjust height as needed
                            .fillMaxWidth() // Optional: Make the Box full-width
                            .clip(RoundedCornerShape(16.dp)) // Apply rounded corners with a 16.dp radius
                    ) {
                        CarouselBannerAutoSlide(banners)
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Add spacing below the banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),

                    ){

                        when (val state = userDataState) {
                            is UserDataState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is UserDataState.Success -> {
                                val user = state.user

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    ) {

                                    user.points?.let { FloatingButton(it, onLeaderboardClick = {navController.navigate(Screen.LeaderBoardScreen.route)}) }
                                }


                            }

                            is UserDataState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Error: ${state.message}")
                                }
                            }

                            else -> Unit
                        }
                    }




                }
                // Small Cards (Terdekat, Termurah, Terbaik)
                item {
                    Text(
                        text = "Menu Pilihan", // Optional title for the row of cards
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
//                            .align(Alignment.Start)
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .offset(x = (-12).dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        SmallCard(
                            title = "Resto Terdekat",
                            picture = R.drawable.loc,
                            onClick = {
                                // Handle click for "Resto Terdekat"
                                navController.navigate("dashboard/Resto Terdekat")
                            }
                        )

                        SmallCard(
                            title = "Resto Terbaik",
                            picture = R.drawable.ic_restaurant,
                            onClick = {
                                // Handle click for "Resto Terbaik"
                                navController.navigate("dashboard/Resto Terbaik")
                            }
                        )

                        SmallCard(
                            title = "The Best Seller",
                            picture = R.drawable.ic_top_button,
                            onClick = {
                                // Handle click for "The Best Seller"
                                navController.navigate("dashboard/The Best Seller")
                            }
                        )

                        SmallCard(
                            title = "Mystery Murah",
                            picture = R.drawable.ic_voucher,
                            onClick = {
                                // Handle click for "Mystery Murah"
                                navController.navigate("dashboard/Mystery Murah")
                            }
                        )

                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically // Align icon and text vertically
                    ) {
                        // Icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save_your_food), // Replace with your actual icon resource
                            contentDescription = "Save Your Food Icon",
                            modifier = Modifier
                                .size(30.dp) // Adjust icon size
                                .padding(end = 8.dp), // Add spacing between icon and text
                            tint = MaterialTheme.colorScheme.primary // Optional: set icon color
                        )

                        // Text
                        Text(
                            text = "Save Your Food",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                }


                when (val state = mysteryBoxState) {
                    is MysteryBoxListState.Loading -> {
                        item{
                            CircularProgressIndicator()
                        }
                    }
                    is MysteryBoxListState.Success -> {
                        item {


                            LazyRow {
                                items(state.mysteryBoxes) { mysteryBox ->
                                    FoodMysteryCard(
                                        mysteryBox = mysteryBox,
                                        picture = R.drawable.mysterybox,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                    is MysteryBoxListState.Error -> {
                        item {

                            Text(text = "Error: ${state.message}")
                        }
                    }

                    else -> Unit
                }

                item {

                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically // Align icon and text vertically
                        ) {
                            // Icon
                            Icon(
                                painter = painterResource(id = R.drawable.ic_restaurant), // Replace with your actual icon resource
                                contentDescription = "Restaurant Icon",
                                modifier = Modifier
                                    .size(30.dp) // Adjust icon size
                                    .padding(end = 8.dp), // Add spacing between icon and text
                                tint = MaterialTheme.colorScheme.primary // Optional: set icon color
                            )

                            // Text
                            Text(
                                text = "Restaurant",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )

                            )
                        }


                        RestaurantButtonRow(
                            modifier = Modifier.padding(start = 0.dp, end = 0.dp),
                            buttons = listOf("Terdekat", "Terbaik", "Termewah", "Termurah"), // Pass button labels
                            selectedButton = selectedButton, // Pass the current selected button
                            onButtonClick = { selected ->
                                selectedButton = selected // Update selected button when clicked
                            }
                        )


                    }

                }

                // Handle the UI based on the current state
                when (val state = restaurantDataState) {


                    is RestaurantDataState.Loading -> {
                        item {
                            CircularProgressIndicator()
                        }
                    }

                    is RestaurantDataState.Success -> {
                        items(state.restaurants) { restaurant ->
                            RestaurantItem(restaurant, navController)
                        }
                    }

                    is RestaurantDataState.Error -> {
                        item {
                            Text(text = "Error: ${state.message}")
                        }
                    }

                    else -> Unit
                }

            }

            // Ensure the BottomNavBar is always at the botto
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 10.dp)
        ) {
            BottomNavBar(navController = navController, selectedTab = selectedTab)
        }
    }
}
