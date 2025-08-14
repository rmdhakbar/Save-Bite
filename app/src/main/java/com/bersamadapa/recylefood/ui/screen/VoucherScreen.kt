package com.bersamadapa.recylefood.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.bersamadapa.recylefood.data.repository.RepositoryProvider
import com.bersamadapa.recylefood.viewmodel.GetAllVouchersState
import com.bersamadapa.recylefood.viewmodel.GetUserVouchersState
import com.bersamadapa.recylefood.viewmodel.RestaurantViewModel
import com.bersamadapa.recylefood.viewmodel.ViewModelFactory
import com.bersamadapa.recylefood.viewmodel.VoucherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    navController: NavController
) {
    val voucherRepository = RepositoryProvider.voucherRepository
    val factory = ViewModelFactory { VoucherViewModel(voucherRepository) }
    val viewModel: VoucherViewModel = viewModel(factory = factory)

    val dataStoreManager = DataStoreManager(LocalContext.current)
    val userId by dataStoreManager.userId.collectAsState("")

    val getAllVouchersState by viewModel.getAllVouchersState.collectAsState()
    val getUserVouchersState by viewModel.getUserVouchersState.collectAsState()
    val selectedVoucherId = remember { mutableStateOf<String?>(null) }  // Store voucher ID

    LaunchedEffect(userId) {
        if (userId?.isNotEmpty() == true) {
            // Fetch all vouchers and user's claimed vouchers
            viewModel.getAllVouchers()
            viewModel.getUserVouchers(userId!!)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Voucher") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar {
                // Trigger claim voucher action if a voucher is selected
                selectedVoucherId.value?.let { voucherId ->
                    userId?.let { viewModel.claimVoucher(it, voucherId) }
                }
            }
        }
    ) { innerPadding ->
        when (getAllVouchersState) {
            is GetAllVouchersState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GetAllVouchersState.Success -> {
                val vouchers = (getAllVouchersState as GetAllVouchersState.Success).vouchers

                // Filter out claimed vouchers by comparing them with the user's claimed vouchers
                val claimedVouchers = (getUserVouchersState as? GetUserVouchersState.Success)?.vouchers ?: emptyList()
                val claimedVoucherIds = claimedVouchers.map { it.id }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* Handle text input */ },
                        placeholder = { Text("Masukkan kode voucher kamu") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        trailingIcon = {
                            IconButton(onClick = { /* Clear text */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        item {
                            Text(
                                text = "Voucher yang Dimiliki",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Claimed Vouchers
                        itemsIndexed(vouchers) { index, voucher ->
                            val isClaimed = claimedVoucherIds.contains(voucher.id)
                            if (isClaimed) {
                                VoucherItem(
                                    voucher = voucher.name ?: "",
                                    isSelected = false,
                                    isClaimed = true,
                                    onSelect = { /* No action */ }
                                )
                                if (index < vouchers.size - 1) {
                                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Voucher yang Belum Diklaim",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Not Claimed Vouchers
                        itemsIndexed(vouchers) { _, voucher ->
                            val isClaimed = claimedVoucherIds.contains(voucher.id)
                            if (!isClaimed) {
                                VoucherItem(
                                    voucher = voucher.name ?: "",
                                    isSelected = selectedVoucherId.value == voucher.id,
                                    isClaimed = false,
                                    onSelect = {
                                        selectedVoucherId.value = voucher.id // Store the voucher ID
                                    }
                                )
                            }
                        }
                    }
                }
            }
            is GetAllVouchersState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gagal memuat data voucher",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            GetAllVouchersState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }
        }
    }
}


@Composable
fun VoucherItem(
    voucher: String,
    isSelected: Boolean,
    isClaimed: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(enabled = !isClaimed) { onSelect() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_voucher), // Replace with your voucher icon resource
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


@Composable
fun BottomBar(onClaim: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.brown)),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClaim,
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.cream)),
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                text = "Claim",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}
