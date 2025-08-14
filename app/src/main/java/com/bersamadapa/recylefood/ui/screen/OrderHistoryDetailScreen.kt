package com.bersamadapa.recylefood.ui.screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.CategoryOrder
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.viewmodel.GetOrderByIdState
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.UpdateRatingState
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryDetailScreen(
    navController: NavController? = null,
    orderId: String?
) {
    val orderRepository = RepositoryProvider.orderRepository
    val factoryOrder = ViewModelFactory { OrderViewModel(orderRepository) }
    val viewModelOrder: OrderViewModel = viewModel(factory = factoryOrder)

    // Provide the repository and create the ViewModel using the factory
    val restaurantRepository = RepositoryProvider.restaurantRepository
    val factoryRestaurant = ViewModelFactory { RestaurantViewModel(restaurantRepository) }
    val viewModelRestaurant: RestaurantViewModel = viewModel(factory = factoryRestaurant)

    var rating by remember { mutableStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasReviewed by remember { mutableStateOf(false) }  // New state to track if review is submitted

    val orderState by viewModelOrder.getOrderByIdState.collectAsState()

    LaunchedEffect(orderId) {
        if (!orderId.isNullOrEmpty()) {
            viewModelOrder.getOrderById(orderId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (orderState) {
                is GetOrderByIdState.Loading -> {
                    Text(text = "Loading order details...", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is GetOrderByIdState.Success -> {
                    val order = (orderState as GetOrderByIdState.Success).order
                    val restaurantId = order.mysteryBoxsData?.firstOrNull()?.restaurantData?.id
                    val selling = order.mysteryBoxsData?.firstOrNull()?.restaurantData?.selling

                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                        item {
                            order.mysteryBoxsData?.map { item ->
                                item.restaurantData?.let { restaurant ->
                                    item.price?.let { price ->
                                        formatCurrency(price.toDouble())?.let { formattedPrice ->
                                            ItemRow(
                                                imageRes = R.drawable.mystery_box,
                                                title = restaurant.name,
                                                price = formattedPrice
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    order.totalAmount?.toDouble()?.let { total ->
                        formatCurrency(total)?.let {
                            DetailsSectionOnGoingOrder(
                                createdAt = order.createdAt,
                                total = it
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (order.categoryOrder == CategoryOrder.Personal) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_save_bite),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .height(80.dp)
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Pembelian Anda Sudah DiSerahkan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Pengantaran Donasi Anda Sudah Selesai",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = order.proveDeliveryDonasi?.url,
                                        placeholder = painterResource(R.drawable.loading_placeholder),
                                        error = painterResource(R.drawable.logo_save_bite)
                                    ),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }
                    }

                    // Add StarRating Component
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Rate Your Experience",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            StarRating(
                                rating = rating,
                                onRatingChanged = { newRating -> rating = newRating }
                            )

                            Text(
                                text = if (rating == 0) "Tap to rate" else "You rated: $rating/5",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Button(
                                onClick = {
                                    if (restaurantId != null && rating > 0 && !hasReviewed) {
                                        // Set the state to loading
                                        isSubmitting = true
                                        errorMessage = null

                                        // Call the ViewModel function to update the rating
                                        if (selling != null) {
                                            viewModelRestaurant.updateRestaurantRating(restaurantId, rating.toFloat(),selling)
                                        }

                                        // Mark the review as submitted
                                        hasReviewed = true
                                    }
                                },
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6200EE)
                                ),
                                enabled = !hasReviewed  // Disable button if review is submitted
                            ) {
                                // Observe the rating update state and show the appropriate UI
                                when (val state = viewModelRestaurant.updateRatingState.collectAsState().value) {
                                    is UpdateRatingState.Loading -> {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    is UpdateRatingState.Success -> {
                                        isSubmitting = false
                                        Log.d("OrderHistoryDetail", "Rating updated successfully!")
                                        Text(
                                            text = "Submit Review",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    is UpdateRatingState.Error -> {
                                        isSubmitting = false
                                        errorMessage = state.message
                                        Text(
                                            text = "Submit Review",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Submit Review",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            errorMessage?.let {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
                is GetOrderByIdState.Error -> {
                    val errorMessage = (orderState as GetOrderByIdState.Error).message
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                GetOrderByIdState.Idle -> {
                    Text(text = "No order data available", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun StarRating(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..5) {
            var isHovered by remember { mutableStateOf(false) }
            IconButton(
                onClick = { onRatingChanged(i) },
                modifier = Modifier
                    .size(if (isHovered || i <= rating) 40.dp else 32.dp) // Enlarges hovered or selected stars
                    .animateContentSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { isHovered = true; awaitRelease(); isHovered = false }
                        )
                    }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (i <= rating) R.drawable.ic_star else R.drawable.ic_star_empty
                    ),
                    contentDescription = "Star $i",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color(0xFFA0A0A0), // Gold for selected, Gray for unselected
                    modifier = Modifier
                        .size(if (isHovered || i <= rating) 40.dp else 32.dp)
                        .animateContentSize()
                )
            }
        }
    }
}

