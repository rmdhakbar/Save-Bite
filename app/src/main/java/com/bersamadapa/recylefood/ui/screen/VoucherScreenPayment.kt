package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bersamadapa.recylefood.R
import com.bersamadapa.recylefood.data.datastore.DataStoreManager
import com.bersamadapa.recylefood.data.model.Voucher
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.viewmodel.GetUserVouchersState
import com.bersamadapa.recylefood.viewmodel.VoucherViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreenPayment(
    navController: NavController
) {
    val voucherRepository = RepositoryProvider.voucherRepository
    val factory = ViewModelFactory { VoucherViewModel(voucherRepository) }
    val viewModel: VoucherViewModel = viewModel(factory = factory)

    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    val getUserVouchersState by viewModel.getUserVouchersState.collectAsState()

    val selectedVoucher = remember { mutableStateOf<Voucher?>(null) }

    LaunchedEffect(userId) {
        if (userId?.isNotEmpty() == true) {
            viewModel.getUserVouchers(userId!!)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Voucher Pembayaran") },
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
        when (getUserVouchersState) {
            is GetUserVouchersState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GetUserVouchersState.Success -> {
                val claimedVouchers = (getUserVouchersState as GetUserVouchersState.Success).vouchers

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    LazyColumn {
                        itemsIndexed(claimedVouchers) { _, voucher ->
                            VoucherItemPayment(
                                voucher = voucher.name ?: "",
                                isSelected = selectedVoucher.value == voucher,
                                isClaimed = false,
                                onSelect = {
                                    selectedVoucher.value = voucher
                                }
                            )
                            if (claimedVouchers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selectedVoucher", selectedVoucher.value
                            )
                            navController.popBackStack() // Go back to PaymentScreen
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.brown)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Gunakan Voucher", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
            is GetUserVouchersState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gagal memuat voucher Anda",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            GetUserVouchersState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gagal memuat voucher Anda",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun VoucherItemPayment(
    voucher: String,
    isSelected: Boolean,
    isClaimed: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(enabled = !isSelected) { onSelect() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_voucher),
            contentDescription = "Voucher Icon",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = voucher,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isClaimed) Color.Gray else colorResource(id = R.color.brown)
            )
            Text(
                text = "Metode pembayaran Save Wallet",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = { if (!isClaimed) onSelect() },
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(id = R.color.brown),
                unselectedColor = if (isClaimed) Color.Gray else colorResource(id = R.color.brown)
            ),
            enabled = !isClaimed
        )
    }
}