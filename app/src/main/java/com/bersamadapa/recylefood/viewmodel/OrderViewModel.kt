package com.bersamadapa.recylefood.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.Order
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.OrderResponse
import com.bersamadapa.recylefood.data.model.OrderStatus
import com.bersamadapa.recylefood.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    companion object {
        private const val TAG = "OrderViewModel"
    }


    // State to manage the status of fetching pending orders
    private val _getPendingOrdersState = MutableStateFlow<GetAllOrdersState>(GetAllOrdersState.Idle)
    val getPendingOrdersState: StateFlow<GetAllOrdersState> get() = _getPendingOrdersState

    // State to manage the status of fetching ongoing orders
    private val _getOngoingOrdersState = MutableStateFlow<GetAllOrdersState>(GetAllOrdersState.Idle)
    val getOngoingOrdersState: StateFlow<GetAllOrdersState> get() = _getOngoingOrdersState

    // State to manage the status of fetching order history (completed orders)
    private val _getHistoryOrdersState = MutableStateFlow<GetAllOrdersState>(GetAllOrdersState.Idle)
    val getHistoryOrdersState: StateFlow<GetAllOrdersState> get() = _getHistoryOrdersState


    // State to manage the status of order creation
    private val _createOrderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val createOrderState: StateFlow<OrderState> get() = _createOrderState

    // Add a state to manage the status of fetching an order by ID
    private val _getOrderByIdState = MutableStateFlow<GetOrderByIdState>(GetOrderByIdState.Idle)
    val getOrderByIdState: StateFlow<GetOrderByIdState> get() = _getOrderByIdState

    // Function to fetch an order by its ID
    fun getOrderById(orderId: String) {
        viewModelScope.launch {
            _getOrderByIdState.value = GetOrderByIdState.Loading
            Log.d(TAG, "Fetching order by ID: $orderId")

            try {
                val result = orderRepository.getOrderById(orderId)
                result.fold(
                    onSuccess = { order ->
                        Log.d(TAG, "Order fetched successfully: $order")
                        _getOrderByIdState.value = GetOrderByIdState.Success(order)
                    },
                    onFailure = { exception ->
                        handleError(GetOrderByIdState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetOrderByIdState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    // Function to create an order
    fun createOrder(userId: String, orderRequest: OrderRequest) {
        viewModelScope.launch {
            _createOrderState.value = OrderState.Loading
            Log.d(TAG, "Creating order for userId: $userId with order request: $orderRequest")

            try {
                val result = orderRepository.createOrder(userId, orderRequest)
                result.fold(
                    onSuccess = { orderResponse ->
                        Log.d(TAG, "Successfully created order: $orderResponse")
                        _createOrderState.value = OrderState.Success(orderResponse)
                    },
                    onFailure = { exception ->
                        handleError(OrderState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(OrderState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    // Function to fetch pending orders
    fun getPendingOrders(userId: String, categoryFilter: String) {
        viewModelScope.launch {
            _getPendingOrdersState.value = GetAllOrdersState.Loading
            Log.d(TAG, "Fetching pending orders for userId: $userId with categoryFilter: $categoryFilter")

            try {
                val result = orderRepository.getAllOrders(userId, OrderStatus.Pending)

                result.fold(
                    onSuccess = { orders ->
                        Log.d(TAG, "Pending orders: $orders")

                        // Filter orders based on the category
                        val filteredOrders = if (categoryFilter != "All") {
                            orders.filter { it.category == categoryFilter }
                        } else {
                            orders
                        }

                        val sortedOrders = filteredOrders.sortedByDescending { it.createdAt }

                        _getPendingOrdersState.value = GetAllOrdersState.Success(sortedOrders)
                    },
                    onFailure = { exception ->
                        handleError(GetAllOrdersState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetAllOrdersState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }


    // Function to fetch ongoing orders
    fun getOngoingOrders(userId: String, categoryFilter: String) {
        viewModelScope.launch {
            _getOngoingOrdersState.value = GetAllOrdersState.Loading
            Log.d(TAG, "Fetching ongoing orders for userId: $userId")

            try {
                val result = orderRepository.getAllOrders(userId, OrderStatus.OnGoing)
                result.fold(
                    onSuccess = { orders ->
                        Log.d(TAG, "Ongoing orders: $orders")

                        val filteredOrders = if (categoryFilter != "All") {
                            orders.filter { it.category == categoryFilter }
                        } else {
                            orders
                        }

                        val sortedOrders = filteredOrders.sortedByDescending { it.createdAt }

                        _getOngoingOrdersState.value = GetAllOrdersState.Success(sortedOrders)
                    },
                    onFailure = { exception ->
                        handleError(GetAllOrdersState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetAllOrdersState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    // Function to fetch completed order history
    fun getHistoryOrders(userId: String, categoryFilter: String) {
        viewModelScope.launch {
            _getHistoryOrdersState.value = GetAllOrdersState.Loading
            Log.d(TAG, "Fetching completed orders for userId: $userId")

            try {
                val result = orderRepository.getAllOrders(userId, OrderStatus.Done)
                result.fold(
                    onSuccess = { orders ->
                        Log.d(TAG, "Completed orders: $orders")

                        val filteredOrders = if (categoryFilter != "All") {
                            orders.filter { it.category == categoryFilter }
                        } else {
                            orders
                        }

                        val sortedOrders = filteredOrders.sortedByDescending { it.createdAt }
                        _getHistoryOrdersState.value = GetAllOrdersState.Success(sortedOrders)
                    },
                    onFailure = { exception ->
                        handleError(GetAllOrdersState.Error(exception.message ?: "Unknown error"), TAG)
                    }
                )
            } catch (e: Exception) {
                handleError(GetAllOrdersState.Error(e.message ?: "Unknown error"), TAG)
            }
        }
    }

    private fun <T> handleError(state: T, tag: String) where T : Any {
        Log.e(tag, "Error: ${(state as? GetAllOrdersState.Error)?.message}")
        when (state) {
            is GetAllOrdersState -> {
                when (state) {
                    is GetAllOrdersState.Success -> {
                        // No action required; success is already handled.
                    }
                    is GetAllOrdersState.Error -> {
                        // Assign the error state to appropriate flow
                        _getPendingOrdersState.value = state
                        _getOngoingOrdersState.value = state
                        _getHistoryOrdersState.value = state
                    }
                    GetAllOrdersState.Idle -> {
                        // Reset state to Idle if necessary
                        _getPendingOrdersState.value = GetAllOrdersState.Idle
                        _getOngoingOrdersState.value = GetAllOrdersState.Idle
                        _getHistoryOrdersState.value = GetAllOrdersState.Idle
                    }
                    GetAllOrdersState.Loading -> {
                        // Log unexpected loading state
                        Log.w(tag, "Unexpected loading state during error handling")
                    }
                }
            }
        }
    }

}

// Sealed class to represent the state of order creation
sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    data class Success(val orderResponse: OrderResponse) : OrderState()
    data class Error(val message: String) : OrderState()
}

// Sealed class to represent the state of fetching all orders
sealed class GetAllOrdersState {
    object Idle : GetAllOrdersState()
    object Loading : GetAllOrdersState()
    data class Success(val orders: List<Order>) : GetAllOrdersState()
    data class Error(val message: String) : GetAllOrdersState()
}

sealed class GetOrderByIdState {
    object Idle : GetOrderByIdState()
    object Loading : GetOrderByIdState()
    data class Success(val order: Order) : GetOrderByIdState()
    data class Error(val message: String) : GetOrderByIdState()
}



