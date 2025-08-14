package com.bersamadapa.recylefood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.CartItem
import com.bersamadapa.recylefood.data.model.CartRequest
import com.bersamadapa.recylefood.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Fetch cart items for a user
    fun fetchCartItems(userId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = cartRepository.getCartItems(userId)
            _isLoading.value = false

            result.fold(
                onSuccess = { items ->
                    _cartItems.value = items
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "An error occurred"
                }
            )
        }
    }

    // Add or update an item in the cart
    fun addItemToCart(userId: String, cartRequest: CartRequest) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = cartRepository.addItemToCart(userId, cartRequest)
            _isLoading.value = false

            result.fold(
                onSuccess = {
                    fetchCartItems(userId) // Refresh cart items after adding/updating
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "An error occurred"
                }
            )
        }
    }

    // Delete an item from the cart
    fun deleteItemFromCart(userId: String, cartId:String,cartItemId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = cartRepository.deleteItemFromCart(userId, cartId, cartItemId)
            _isLoading.value = false

            result.fold(
                onSuccess = {
                    fetchCartItems(userId) // Refresh cart items after deletion
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "An error occurred"
                }
            )
        }
    }
}
