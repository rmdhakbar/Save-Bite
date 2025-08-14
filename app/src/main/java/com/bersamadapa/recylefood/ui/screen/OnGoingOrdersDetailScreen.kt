package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.model.CategoryOrder
import com.bersamadapa.recylefood.data.model.Order
import com.bersamadapa.recylefood.utils.QRCode
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnGoingOrdersDetailScreen(
    navController : NavController? = null,
    order:Order
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bayar") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // List of Items
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    order.mysteryBoxsData?.map { it ->
                        it.restaurantData?.let { it1 ->
                            it.price?.let { it2 -> formatCurrency(it2.toDouble()) }?.let { it3 ->
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

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Details Section
            order.totalAmount?.toDouble()?.let { formatCurrency(it) }?.let {
                DetailsSectionOnGoingOrder(
                    createdAt = order.createdAt,
                    total = it
                )
            }
// Center QR Code

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (order.categoryOrder == CategoryOrder.Personal) {
                    order.id?.let {
                        QRCode(it)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_save_bite),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(80.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Donasi Sedang Dalam Pengantaran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Periksa pembaruan secara berkala.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


// Details Section
@Composable
fun DetailsSectionOnGoingOrder(total: String, createdAt: Timestamp?) {
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
            DetailRow(label = "Order Pada", value = formatTimestamp( createdAt), isBold = true)
            DetailRow(label = "Total Harga", value = total, isBold = true)
        }
    }
}

fun formatTimestamp(timestamp: Timestamp?): String {
    return timestamp?.let {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(it.toDate())
    } ?: "-"
}



