package com.bersamadapa.recylefood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authRepository.login(email, password) // Result<String>
            _loginState.value = if (result.isSuccess) {
                val userId = result.getOrNull() ?: "" // Get the userId from Result
                LoginState.Success(userId) // Pass userId to the Success state
            } else {
                LoginState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val userId: String) : LoginState() // Add userId
    data class Error(val message: String) : LoginState()
    object Idle : LoginState() // Optional, to reset state
}

