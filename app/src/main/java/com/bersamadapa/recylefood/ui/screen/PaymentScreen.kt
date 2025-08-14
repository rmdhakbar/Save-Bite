package com.bersamadapa.recylefood.ui.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.PaymentActivity
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.Voucher
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.ui.component.textinput.TextFieldCustom
import com.bersamadapa.recylefood.ui.navigation.Screen
import com.bersamadapa.recylefood.viewmodel.CartViewModel
import com.bersamadapa.recylefood.viewmodel.OrderState
import com.bersamadapa.recylefood.viewmodel.OrderViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

// Main PaymentScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController, // Nullable for preview
    mysteryBoxs: List<MysteryBox>,
) {

    val adminFee = 4999
    val totalPriceBeforeDiscount = mysteryBoxs.sumOf { it.price?.toDouble() ?: 0.0 } + adminFee

    var voucherCode by remember { mutableStateOf<Voucher?>(null) }
    var isDonation by remember { mutableStateOf(false) }  // State to track if it's for donation
    var phoneNumber by remember { mutableStateOf("") }  // State to store receiver's phone number
    var address by remember { mutableStateOf("") }  // State to store receiver's address

    val selectedVoucher = navController.currentBackStackEntry?.savedStateHandle?.get<Voucher>("selectedVoucher")
    if (selectedVoucher != null) {
        voucherCode = selectedVoucher // Update the voucher code
    }

    // Apply voucher discount, ensuring no discount exceeds the maximum discount
    // Apply voucher discount if available (percentage-based discount)
    val voucherDiscountPercentage = voucherCode?.discount ?: 0 // Discount percentage (0 to 100)
    val maxDiscount = voucherCode?.maximumDiscount ?: 0.0  // Maximum discount (in currency)

    // Calculate discount amount based on percentage
    val discountAmount = totalPriceBeforeDiscount * (voucherDiscountPercentage / 100.0)

    // Ensure the discount does not exceed the maximum allowed discount
    val actualDiscount = minOf(discountAmount.toInt(), maxDiscount.toInt())

    // Total price after applying discount
    val totalPrice = totalPriceBeforeDiscount - actualDiscount
    // Dynamically set category based on donation or personal choice
    val category = if (isDonation) "Donation" else "Personal"

    val boxPrice = mysteryBoxs.firstOrNull()?.price

    // OrderRequest with category and mystery box IDs, add address and phone number for donation
    val orderRequest = OrderRequest(
        mysteryBox = mysteryBoxs.map { it.id },
        category = category, // Add the correct category here
        phoneNumberReceiver = if (isDonation) phoneNumber else null,
        addressReceiver = if (isDonation) address else null,
        voucherId = voucherCode?.id
    )

    Log.d("PaymentScreen", "Mystery Box IDs: ${mysteryBoxs.toString()}")
    Log.d("PaymentScreen", "Category: $category")

    val orderRepository = RepositoryProvider.orderRepository
    val factoryOrder = ViewModelFactory { OrderViewModel(orderRepository) }
    val viewModelOrder: OrderViewModel = viewModel(factory = factoryOrder)

    // Observe the state of order creation (you can use this to show loading, success, or error)
    val createOrderState by viewModelOrder.createOrderState.collectAsState()

    val context = LocalContext.current

    val dataStoreManager = DataStoreManager(context)
    val userId by dataStoreManager.userId.collectAsState("")

    LaunchedEffect(createOrderState) {
        if (createOrderState is OrderState.Success) {
            val token = (createOrderState as OrderState.Success).orderResponse.token_midtrans.token
            startPaymentActivity(context, token)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bayar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        bottomBar = {
            userId?.let { BottomCartBar2(totalPrice, it, orderRequest, viewModelOrder, navController) }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // List of Items
            LazyColumn() {
                item {
                    mysteryBoxs.map { it ->
                        it.restaurantData?.let { it1 ->
                            it.price?.let { it2 ->
                                formatCurrency(it2.toDouble()).let { it3 ->
                                    ItemRow(
                                        imageRes = R.drawable.mystery_box,
                                        title = it1.name,
                                        price = it3
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Donation / Personal Choice
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("For: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row() {
                        Text("Personal")
                        RadioButton(
                            selected = !isDonation,
                            onClick = { isDonation = false }
                        )
                    }
                    Row() {
                        Text("Donation")
                        RadioButton(
                            selected = isDonation,
                            onClick = { isDonation = true }
                        )
                    }
                }
            }

            // If Donation is selected, show additional input fields
            if (isDonation) {
                Spacer(modifier = Modifier.height(16.dp))
                TextFieldCustom(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Nomor Handphone Penerima") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextFieldCustom(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat Penerima") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Payment Details Section
            if (boxPrice != null) {
                DetailsSection(
                    boxPrice = formatCurrency((boxPrice.toDouble())),
                    adminFee = formatCurrency(4999.0),
                    discount = formatCurrency(actualDiscount.toDouble()),
                    total = formatCurrency(totalPrice.toDouble())
                )
            }
        }
    }
}

// ItemRow Composable
@Composable
fun ItemRow(imageRes: Int, title: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Item Image",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = price,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

// Details Section
@Composable
fun DetailsSection(boxPrice: String, adminFee: String, discount: String, total: String) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Details",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp, start = 10.dp, top = 5.dp),
            textAlign = TextAlign.Start
        )
        Column(modifier = Modifier.padding(8.dp)) {
            DetailRow(label = "Harga Mystery Box", value = boxPrice)
            DetailRow(label = "Layanan Admin", value = adminFee)
            DetailRow(label = "Voucher Discount", value = "-${discount}")
            Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color.Black)
            DetailRow(label = "Total Harga", value = total, isBold = true)
        }
    }
}

// Single Row in Details Section
@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Bottom Cart Bar
@Composable
fun BottomCartBar2(totalPrice: Double, userId:String, orderRequest:OrderRequest,viewModelOrder: OrderViewModel, navController: NavController) {
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
                color = Color.Gray,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.VoucherPaymentScreen.route)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Observe createOrderState from the ViewModel
        val createOrderState by viewModelOrder.createOrderState.collectAsState()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total: ${formatCurrency(totalPrice)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // Check if the state is loading
            if (createOrderState is OrderState.Loading) {
                // Show a loading spinner when the state is Loading
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Show the button when not loading
                Button(
                    onClick = {
                        viewModelOrder.createOrder(userId, orderRequest)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.cream))
                ) {
                    Text(text = "Bayar", color = Color.Black)
                }
            }
        }
    }
}

// Utility Function for Formatting Currency
fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
}

fun startPaymentActivity(context: Context, token: String) {
    val intent = Intent(context, PaymentActivity::class.java).apply {
        putExtra("PAYMENT_TOKEN", token)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(intent)
}
