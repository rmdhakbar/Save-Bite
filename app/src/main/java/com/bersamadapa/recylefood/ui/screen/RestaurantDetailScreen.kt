package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.dashboard.FoodMysteryCard
import com.bersamadapa.recylefood.ui.component.product.ProductCardDetail
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.viewmodel.RestaurantDetailState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    idRestaurant: String,
    navController: NavController
) {
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factory = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModel: RestaurantViewModel = viewModel(factory = factory)

    val restaurantDataState by viewModel.restaurantDetailState.collectAsState()

    if (restaurantDataState is RestaurantDetailState.Idle) {
        viewModel.fetchRestaurantDetails(idRestaurant)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Restoran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Make entire screen scrollable
        ) {
            when (val state = restaurantDataState) {
                is RestaurantDetailState.Loading -> {
                    LoadingScreen(message = "Fetching restaurant details...")
                }
                is RestaurantDetailState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is RestaurantDetailState.Success -> {
                    val restaurant = state.restaurant

                    // Banner Image
                    Image(
                        painter = rememberAsyncImagePainter(restaurant.profilePicture?.url),
                        contentDescription = "Restaurant Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Restaurant Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = restaurant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
//                        Text(
//                            text = restaurant.description ?: "No description available",
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = "Location",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = restaurant.address,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val rating = restaurant.rating?.toFloat() ?: 0f // Handle null rating, default to 0 if null
                            val fullStars = rating.toInt() // Number of full stars
                            val halfStar = (rating % 1 >= 0.5f) // Check if there is a half star
                            val emptyStars = 5 - fullStars - if (halfStar) 1 else 0 // Remaining empty stars

                            // Render full stars
                            repeat(fullStars) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = "Full star",
                                    tint = Color(0xFFFFC107), // Yellow color for full stars
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Render half star if necessary
                            if (halfStar) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star_half),
                                    contentDescription = "Half star",
                                    tint = Color(0xFFFFC107), // Yellow color for half star
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Render empty stars
                            repeat(emptyStars) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star_empty),
                                    contentDescription = "Empty star",
                                    tint = Color.Gray, // Gray color for empty stars
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Display rating text
                            Text(
                                text = rating.toString(),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                    }

//                     Horizontal Scroll for Offers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        restaurant.mysteryBox?.forEach { offer ->
                            FoodMysteryCard(
                                offer,
                                picture = R.drawable.mysterybox,
                                navController = navController)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Menu Section
                    Text(
                        text = "Menu Items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        restaurant.products?.forEach { product ->
                            MenuItem(
                                imageUrl = product.productPicture?.url, // Replace with product image
                                name = product.name,
                                description = product.description
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}



@Composable
fun MenuItem(imageUrl: String?, name: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Use Coil to load the image from URL or fallback to local placeholder
        val painter = // Show while loading
            rememberAsyncImagePainter(ImageRequest.Builder // Show if failed to load
                (LocalContext.current).data(data = imageUrl ?: R.drawable.mysterybox)
                .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.mysterybox) // Show while loading
                    error(R.drawable.mysterybox) // Show if failed to load
                }).build()
            )
        Image(
            painter = painter,
            contentDescription = name,
            modifier = Modifier
                .size(64.dp)
                .background(Color.DarkGray, shape = CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = description, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
