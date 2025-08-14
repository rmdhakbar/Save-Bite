package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.viewmodel.CartViewModel
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val cartRepository = RepositoryProvider.cartRepository
    val factoryCart = ViewModelFactory { CartViewModel(cartRepository) }
    val viewModelCart: CartViewModel = viewModel(factory = factoryCart)

    val listOrderState by viewModelCart.cartItems.collectAsState()
    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    // State for tracking checked items, selected restaurant, and total price
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }
    val selectedRestaurant = remember { mutableStateOf<String?>(null) }
    val selectedItemIds = remember { mutableStateOf(setOf<String>()) }
    val totalPrice = remember { mutableStateOf(0.0) }

    // Fetch cart items when userId changes
    LaunchedEffect(userId) {
        if (userId?.isNotEmpty() == true) {
            userId?.let { viewModelCart.fetchCartItems(it) }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Keranjang") },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomCartBar(totalPrice.value)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (viewModelCart.isLoading.collectAsState().value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                val errorMessage by viewModelCart.errorMessage.collectAsState()
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Display cart items
                var currentRestaurant = ""
                listOrderState.forEachIndexed { index, cartItem ->
                    val isFirstItemOfRestaurant = cartItem.restaurant?.name != currentRestaurant

                    val cartId = cartItem.id

                    if (isFirstItemOfRestaurant) {
                        if (index != 0) {
                            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        }
                        currentRestaurant = cartItem.restaurant?.name.orEmpty()
                    }

                    if (cartId != null) {
                        CartItem(
                            cartId = cartId,
                            restaurantName = if (isFirstItemOfRestaurant) cartItem.restaurant?.name else null,
                            mysteryBox = cartItem.mysteryBoxData ?: emptyList(),
                            quantity = cartItem.quantity,
                            checkedItems = checkedItems,
                            selectedRestaurant = selectedRestaurant,
                            onCheckChange = { itemId, isChecked, price ->
                                checkedItems[itemId] = isChecked
                                totalPrice.value = if (isChecked) {
                                    totalPrice.value + price
                                } else {
                                    totalPrice.value - price
                                }

                                // Update selectedItemIds state
                                selectedItemIds.value = if (isChecked) {
                                    selectedItemIds.value + itemId
                                } else {
                                    selectedItemIds.value - itemId
                                }
                            },
                            onRestaurantCheckChange = { restaurantName, restaurantItems, isChecked ->
                                // Only allow selecting one restaurant at a time
                                selectedRestaurant.value = if (isChecked) restaurantName else null
                                // Uncheck items from other restaurants
                                checkedItems.clear()
                                totalPrice.value = 0.0

                                // Update selected items for this restaurant
                                restaurantItems.forEach { item ->
                                    checkedItems[item.id] = isChecked
                                    if (isChecked) totalPrice.value += item.price?.toDouble() ?: 0.0
                                }
                            } ,
                            onDelete = { mysteryBoxId, cartItemId ->  // Make sure to pass correct parameters
                                userId?.let { viewModelCart.deleteItemFromCart(it, cartItemId, mysteryBoxId) }
                            }

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItem(
    cartId: String,
    restaurantName: String?,
    mysteryBox: List<MysteryBox>,
    quantity: Int,
    checkedItems: MutableMap<String, Boolean>,
    selectedRestaurant: MutableState<String?>,
    onCheckChange: (String, Boolean, Int) -> Unit,
    onRestaurantCheckChange: (String, List<MysteryBox>, Boolean) -> Unit,
    onDelete: (String, String) -> Unit // Corrected onDelete callback
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Restaurant Name Section with checkbox
        restaurantName?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Checkbox(
                    checked = selectedRestaurant.value == it,
                    onCheckedChange = { isChecked -> onRestaurantCheckChange(it, mysteryBox, isChecked) }
                )
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.brown)
                )
            }
        }

        // Display MysteryBox items with checkboxes
        mysteryBox.forEach { box ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedItems[box.id] == true,
                    onCheckedChange = { isChecked -> box.price?.let { onCheckChange(box.id, isChecked, it) } }
                )
                Image(
                    painter = painterResource(id = R.drawable.mystery_box),
                    contentDescription = "Mystery Box Image",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    box.name?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.brown)
                        )
                    }
                    Text(
                        text = "Rp${box.price}",
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.brown)
                    )
                }
                IconButton(onClick = { onDelete(box.id, cartId) }) { // Corrected the parameters
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete Item",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun BottomCartBar(totalPrice: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.brown))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Voucher SaveBite",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Masukkan atau pilih voucher mu",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total: Rp$totalPrice",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { /* Handle Payment */ },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.cream))
            ) {
                Text(text = "Bayar", color = Color.Black)
            }
        }
    }
}
