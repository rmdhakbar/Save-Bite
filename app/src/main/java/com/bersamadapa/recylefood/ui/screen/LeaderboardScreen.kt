package com.bersamadapa.recylefood.ui.screen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.viewmodel.RestaurantDataState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardPage(
    navController: NavController
) {
    val context = LocalContext.current

    // Provide the repository and create the ViewModel using the factory
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factory = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModel: RestaurantViewModel = viewModel(factory = factory)

    val restaurantDataState by viewModel.restaurantListState.collectAsState()

    if (restaurantDataState is RestaurantDataState.Idle) {
        LaunchedEffect(Unit) {
            val location = LocationHelper(context = context).getUserLocation()
            Log.d("LeaderboardPage", "Fetching restaurants for location: $location")
            viewModel.fetchRestaurants("Terpopuler", location)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Leaderboard Restoran") },
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
                .padding(top = 10.dp)
        ) {
            when (restaurantDataState) {
                is RestaurantDataState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RestaurantDataState.Success -> {
                    val restaurants = (restaurantDataState as RestaurantDataState.Success).restaurants
                    val top3 = restaurants.take(3)
                    val others = restaurants.drop(3)

                    // Top 3 Leaderboard Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Assign ranks explicitly to ensure correct display
                        val ranks = listOf(2, 1, 3) // Mapping of ranks to their respective positions

                        val reorderedTopThree = listOf(
                            top3[1], // 2nd place at index 0
                            top3[0], // 1st place at index 1
                            top3[2]  // 3rd place at index 2
                        )

// Iterate with matching index == rank
                        reorderedTopThree.forEachIndexed { index, restaurant ->
                            restaurant.profilePicture?.let {
                                TopThreeItem(
                                    rank = ranks[index], // Rank matches index
                                    imageRes = it.url, // Replace with actual image field
                                    name = restaurant.name,
                                    sales = "${restaurant.selling} Terjual",
                                    isHighlighted = index == 0 // Highlight the 2nd place
                                )
                            }
                        }


                    }

                    // Other Restaurants List
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F9FA))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            others.forEachIndexed { index, restaurant ->
                                OtherRestaurantItem(
                                    rank = index + 4,
                                    name = restaurant.name,
                                    sales = "${restaurant.selling} Terjual"
                                )
                            }
                        }
                    }
                }
                is RestaurantDataState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${(restaurantDataState as RestaurantDataState.Error).message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun TopThreeItem(
    rank: Int,
    imageRes: String,
    name: String,
    sales: String,
    isHighlighted: Boolean
) {
    Column(
        modifier = Modifier
            .width(when (rank) {
                1 -> 140.dp // First place
                2 -> 120.dp // Second place
                else -> 100.dp // Third place and others
            })
            .widthIn(min = 150.dp)
            .height(
                when (rank) {
                    1 -> 300.dp // First place
                    2 -> 250.dp // Second place
                    else -> 220.dp // Third place and others
                }
            )
            .background(
                color = Color(0xFFFFE082), // Yellow background
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .border(1.dp,Color.Black , RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(0.dp, top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rank Star
        RankStar(rank)

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant Image
        Image(
            painter = rememberAsyncImagePainter(
                model = imageRes,
                placeholder = painterResource(R.drawable.loading_placeholder),
                error = painterResource(R.drawable.default_profile_picture)
            ),
            contentDescription = name,
            modifier = Modifier
                .size(
                    when (rank) {
                        1 -> 64.dp // First place
                        2 -> 54.dp // Second place
                        else -> 44.dp // Third place and others
                    }
                ),
            contentScale = ContentScale.Crop
        )


        // Name and Sales
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.brown)
        )

        Text(
            text = sales,
            fontSize = 12.sp,
            color = colorResource(id = R.color.brown),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun RankStar(rank: Int) {
    val size = when (rank) {
        1 -> 80.dp // First place
        2 -> 60.dp // Second place
        else -> 40.dp // Third place and others
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700), // Gold
                        Color(0xFFFFA500) // Orange
                    )
                ),
                shape = CircleShape
            )
            .shadow(
                elevation = if (rank == 1) 8.dp else 4.dp,
                shape = CircleShape
            )
            .padding(4.dp) // Adds some space around the star
    ) {
        // Animated star size
        Image(
            painter = painterResource(id = R.drawable.ic_round_star),
            contentDescription = "Rank $rank",
            modifier = Modifier
                .size(size - 10.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape
                )
        )

        // Centered rank number with dynamic font size
        Text(
            text = rank.toString(),
            fontSize = when (rank) {
                1 -> 18.sp // Bigger for first place
                2 -> 16.sp
                else -> 14.sp
            },
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(2.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun RankStar2(rank: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp) // Adjust size as needed
    ) {
        // Background star image
        Image(
            painter = painterResource(id = R.drawable.ic_round_star2),
            contentDescription = "Rank $rank",
            modifier = Modifier.size(40.dp) // Size of the star
        )

        // Number displayed in the center of the star
        Text(
            text = rank.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // Make sure it contrasts with the star's color
        )
    }
}


@Composable
fun OtherRestaurantItem(rank: Int, name: String, sales: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(12.dp)) // Clip the shape for rounded corners
            .background(
                color = Color(0xFFF8F9FA) // Subtle off-white background for contrast
            )
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)) // Add a light border
            .padding(16.dp), // Internal padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Badge
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = Color(0xFF6C63FF)), // Accessible, vibrant accent color
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White // Contrast against the badge background
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        // Name and Address
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333) // Dark gray for readability on white
            )
            Text(
                text = "Jalan Kaliurang KM 12", // Replace with a dynamic value if needed
                fontSize = 12.sp,
                color = Color(0xFF888888), // Medium gray for less emphasis
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Sales Section
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = sales,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF333333) // Dark gray for strong contrast
            )
            Text(
                text = "Sales Today",
                fontSize = 12.sp,
                color = Color(0xFF888888), // Medium gray for a lighter tone
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
