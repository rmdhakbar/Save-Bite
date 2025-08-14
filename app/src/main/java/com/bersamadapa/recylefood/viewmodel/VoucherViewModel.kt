package com.bersamadapa.recylefood.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.Voucher
import com.bersamadapa.recylefood.data.repository.VoucherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoucherViewModel(private val voucherRepository: VoucherRepository) : ViewModel() {

    companion object {
        private const val TAG = "VoucherViewModel"
    }

    // State for fetching all vouchers
    private val _getAllVouchersState = MutableStateFlow<GetAllVouchersState>(GetAllVouchersState.Idle)
    val getAllVouchersState: StateFlow<GetAllVouchersState> get() = _getAllVouchersState

    // State for claiming a voucher
    private val _claimVoucherState = MutableStateFlow<ClaimVoucherState>(ClaimVoucherState.Idle)
    val claimVoucherState: StateFlow<ClaimVoucherState> get() = _claimVoucherState

    // State for fetching user vouchers
    private val _getUserVouchersState = MutableStateFlow<GetUserVouchersState>(GetUserVouchersState.Idle)
    val getUserVouchersState: StateFlow<GetUserVouchersState> get() = _getUserVouchersState

    // Fetch all vouchers
    fun getAllVouchers() {
        viewModelScope.launch {
            _getAllVouchersState.value = GetAllVouchersState.Loading
            Log.d(TAG, "Fetching all vouchers")

            try {
                val result = voucherRepository.getAllVouchers()
                result.fold(
                    onSuccess = { vouchers ->
                        Log.d(TAG, "Fetched vouchers: $vouchers")
                        _getAllVouchersState.value = GetAllVouchersState.Success(vouchers)
                    },
                    onFailure = { exception ->
                        handleError(GetAllVouchersState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetAllVouchersState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    // Claim a voucher
    fun claimVoucher(userId: String, voucherId: String) {
        viewModelScope.launch {
            _claimVoucherState.value = ClaimVoucherState.Loading
            Log.d(TAG, "Claiming voucher $voucherId for user $userId")

            try {
                val result = voucherRepository.claimVoucher(userId, voucherId)
                result.fold(
                    onSuccess = {
                        Log.d(TAG, "Voucher $voucherId successfully claimed")
                        _claimVoucherState.value = ClaimVoucherState.Success
                    },
                    onFailure = { exception ->
                        handleError(ClaimVoucherState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(ClaimVoucherState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    // Fetch user's claimed vouchers
    fun getUserVouchers(userId: String) {
        viewModelScope.launch {
            _getUserVouchersState.value = GetUserVouchersState.Loading
            Log.d(TAG, "Fetching vouchers for user $userId")

            try {
                val result = voucherRepository.getUserVouchers(userId)
                result.fold(
                    onSuccess = { vouchers ->
                        Log.d(TAG, "Fetched user vouchers: $vouchers")
                        _getUserVouchersState.value = GetUserVouchersState.Success(vouchers)
                    },
                    onFailure = { exception ->
                        handleError(GetUserVouchersState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetUserVouchersState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    private fun <T> handleError(state: T, tag: String) where T : Any {
        Log.e(tag, "Error: ${(state as? GetAllVouchersState.Error)?.message ?: (state as? ClaimVoucherState.Error)?.message}")
        when (state) {
            is GetAllVouchersState -> _getAllVouchersState.value = state
            is ClaimVoucherState -> _claimVoucherState.value = state
            is GetUserVouchersState -> _getUserVouchersState.value = state
        }
    }
}

// State classes for fetching all vouchers
sealed class GetAllVouchersState {
    object Idle : GetAllVouchersState()
    object Loading : GetAllVouchersState()
    data class Success(val vouchers: List<Voucher>) : GetAllVouchersState()
    data class Error(val message: String) : GetAllVouchersState()
}

// State classes for claiming a voucher
sealed class ClaimVoucherState {
    object Idle : ClaimVoucherState()
    object Loading : ClaimVoucherState()
    object Success : ClaimVoucherState()
    data class Error(val message: String) : ClaimVoucherState()
}

// State classes for fetching user vouchers
sealed class GetUserVouchersState {
    object Idle : GetUserVouchersState()
    object Loading : GetUserVouchersState()
    data class Success(val vouchers: List<Voucher>) : GetUserVouchersState()
    data class Error(val message: String) : GetUserVouchersState()
}
