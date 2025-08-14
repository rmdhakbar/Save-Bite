package com.bersamadapa.recylefood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.User
import com.bersamadapa.recylefood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // State for fetching user data
    private val _userDataState = MutableStateFlow<UserDataState>(UserDataState.Idle)
    val userDataState: StateFlow<UserDataState> get() = _userDataState

    // State for updates
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> get() = _updateState

    // State to hold the current user
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    // Fetch user by ID
    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            _userDataState.value = UserDataState.Loading
            try {
                val result = userRepository.getUserById(userId)
                result.onSuccess { user ->
                    _user.value = user
                    _userDataState.value = UserDataState.Success(user)
                }.onFailure { exception ->
                    _userDataState.value = UserDataState.Error(exception.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _userDataState.value = UserDataState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Update user data with profile picture
    fun updateUser(userId: String, username: String, profilePicture: MultipartBody.Part?) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            try {
                val result = userRepository.updateUser(userId, username, profilePicture)
                result.onSuccess {
                    _updateState.value = UpdateState.Success
                    fetchUserById(userId) // Refresh user data after update
                }.onFailure {
                    _updateState.value = UpdateState.Error(it.message ?: "Update failed")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.message ?: "Unknown error")
            }
        }
    }
}


// Sealed class to manage fetching user states
sealed class UserDataState {
    object Idle : UserDataState()
    object Loading : UserDataState()
    data class Success(val user: User) : UserDataState()
    data class Error(val message: String) : UserDataState()
}

// Sealed class to manage update user states
sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}
