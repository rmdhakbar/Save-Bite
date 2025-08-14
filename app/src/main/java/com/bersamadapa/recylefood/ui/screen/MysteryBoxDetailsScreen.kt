package com.bersamadapa.recylefood.ui.screen
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.PaymentActivity
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.CartRequest
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.dashboard.FoodMysteryCard
import com.bersamadapa.recylefood.utils.LocationHelper
import com.bersamadapa.recylefood.viewmodel.CartViewModel
import com.bersamadapa.recylefood.viewmodel.MysteryBoxDetailState
import com.bersamadapa.recylefood.viewmodel.MysteryBoxListState
import com.bersamadapa.recylefood.viewmodel.MysteryBoxViewModel
import com.bersamadapa.recylefood.viewmodel.OrderState
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import com.google.gson.Gson
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MysteryBoxDetailScreen(
    idMysterBox: String, // Restaurant ID to pass the data
    navController: NavController
) {

    val mysteryBoxRepository = RepositoryProvider.mysteryBoxRepository
    val factoryMysteryBox = ViewModelFactory { MysteryBoxViewModel(mysteryBoxRepository) }
    val viewModelMysteryBox: MysteryBoxViewModel = viewModel(factory = factoryMysteryBox)


    val cartRepository = RepositoryProvider.cartRepository // Assume you have a Cart repository
    val factoryCart = ViewModelFactory { CartViewModel(cartRepository) }
    val viewModelCart: CartViewModel = viewModel(factory = factoryCart)



    // Collect the current state of restaurant data
    // Observe mystery box state
    val mysteryBoxState by viewModelMysteryBox.mysteryBoxDetailState.collectAsState()

    val context = LocalContext.current

    val dataStoreManager = DataStoreManager(context)
    val userId by dataStoreManager.userId.collectAsState("")

    var restaurantId:String? = null

    var mysterybox:MysteryBox? = null



    if (mysteryBoxState is MysteryBoxDetailState.Idle) {
        LaunchedEffect(Unit) {
            val location = LocationHelper(context = context).getUserLocation()
            Log.d("DashboardScreen", "Fetching restaurants for location: $location")
            if (location != null) {
                viewModelMysteryBox.fetchMysteryBoxDetails(idMysterBox,location)
            }

        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centers the image
        ) {

            item{

                Image(
                    painter = painterResource(id = R.drawable.mystery_box),
                    contentDescription = "Mystery Box",
                    modifier = Modifier
                        .size(350.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

            }


            item {
                // Text and details section
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start // Align text to the left
                ) {

                    when (val state = mysteryBoxState) {
                        is MysteryBoxDetailState.Loading -> {

                                CircularProgressIndicator()

                        }
                        is MysteryBoxDetailState.Success -> {
                            val mysteryBox = state.mysteryBox

                            restaurantId = mysteryBox.restaurantData?.id
                            mysterybox = mysteryBox


                            val formattedPrice = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(mysteryBox.price?.toDouble())


                                mysteryBox.restaurantData?.name?.let {
                                    Text(
                                        text = it,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.brown)
                                    )
                                }
                                Text(
                                    text = formattedPrice,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Start,
                                    color = colorResource(id = R.color.brown)
                                )
                                Text(
                                    text = "Anda bisa mendapatkan paket mystery box ${mysteryBox.name}",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Start,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_food),
                                        contentDescription = "Paket Tersedia",
                                        tint = Color.Gray
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("${((((mysteryBox.productsData?.size).toString())))} paket makanan", fontSize = 14.sp, color = Color.Gray)
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_location),
                                        contentDescription = "Jarak",
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(formatDistance(mysteryBox.restaurantData?.distance), fontSize = 14.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_star),
                                        contentDescription = "Rating",
                                        tint = colorResource(id = R.color.gold)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(mysteryBox.restaurantData?.rating.toString(), fontSize = 14.sp, color = Color.Gray)
                                }

                        }
                        is MysteryBoxDetailState.Error -> {


                                Text(text = "Error: ${state.message}")

                        }

                        else -> Unit
                    }



                }
                Spacer(modifier = Modifier.height(16.dp))

            }

            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally // Center the buttons
                ) {
                    Button(
                        onClick = {

                            navController.navigate("payment/${URLEncoder.encode(Gson().toJson(listOf(mysterybox)),"UTF-8") }")

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.brown) // Use saved color
                        ),
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .width(300.dp) // Custom width
                            .height(50.dp) // Custom height
                    ) {
                        Text("Bayar", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val user = userId
                            if (user != null) {
                                // Create cart request here
                                val cartRequest = restaurantId?.let {
                                    CartRequest(mysteryBox = listOf(idMysterBox),
                                        restaurantId = it,
                                        quantity = 1
                                    )
                                }
                                if (cartRequest != null) {
                                    viewModelCart.addItemToCart(user, cartRequest)

                                    // Show success toast notification
                                    Toast.makeText(context, "Item berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()

                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorResource(id = R.color.brown) // Use saved color
                        ),
                        shape = RoundedCornerShape(40.dp),
                        border = BorderStroke(2.dp, colorResource(id = R.color.brown)),
                        modifier = Modifier
                            .width(300.dp) // Custom width
                            .height(50.dp) // Custom height
                    ) {
                        Text("Keranjang", color = colorResource(id = R.color.brown))
                    }
                }

            }

            // Buttons section

        }


    }
}


private fun formatDistance(distance: Float?): String {
    if (distance == null) return "N/A"

    return if (distance < 1000) {
        // Format as meters
        "${distance.toInt()} m"
    } else {
        // Format as kilometers with one decimal place
        String.format("%.1f km", distance / 1000)
    }
}
