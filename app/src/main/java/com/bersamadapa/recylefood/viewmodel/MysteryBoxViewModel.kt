
package com.bersamadapa.recylefood.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.repository.MysteryBoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MysteryBoxViewModel(private val mysteryBoxRepository: MysteryBoxRepository) : ViewModel() {

    val TAG = "MysteryBoxViewModel"

    // State to manage the list of mystery boxes
    private val _mysteryBoxListState = MutableStateFlow<MysteryBoxListState>(MysteryBoxListState.Idle)
    val mysteryBoxListState: StateFlow<MysteryBoxListState> get() = _mysteryBoxListState

    // State to manage the details of a single mystery box
    private val _mysteryBoxDetailState = MutableStateFlow<MysteryBoxDetailState>(MysteryBoxDetailState.Idle)
    val mysteryBoxDetailState: StateFlow<MysteryBoxDetailState> get() = _mysteryBoxDetailState


    private val _filterState = MutableStateFlow<MysteryBoxFilter>(MysteryBoxFilter.Nearest)
    val filterState: StateFlow<MysteryBoxFilter> get() = _filterState

    fun fetchAllMysteryBoxes(selectedFilter: MysteryBoxFilter, userLocation: Location) {
        viewModelScope.launch {
            _mysteryBoxListState.value = MysteryBoxListState.Loading

            val result = mysteryBoxRepository.getAllMysteryBoxes(userLocation)
            result.fold(
                onSuccess = { mysteryBoxes ->
                    val filteredMysteryBoxes = when (selectedFilter) {
                        MysteryBoxFilter.Nearest -> mysteryBoxes.sortedBy { it.restaurantData?.distance }
                        MysteryBoxFilter.BestRated -> mysteryBoxes.sortedByDescending { it.restaurantData?.rating }
                        MysteryBoxFilter.Cheapest -> mysteryBoxes.sortedBy { it.price }
                        MysteryBoxFilter.BestSeller -> mysteryBoxes.sortedBy { it.restaurantData?.selling }
                        MysteryBoxFilter.New -> mysteryBoxes.sortedByDescending { it.createdAt }


                    }
                    _mysteryBoxListState.value = MysteryBoxListState.Success(filteredMysteryBoxes)
                },
                onFailure = { exception ->
                    _mysteryBoxListState.value = MysteryBoxListState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }



    fun updateFilter(filter: MysteryBoxFilter) {
        _filterState.value = filter
    }

    // Function to fetch mystery box details
    fun fetchMysteryBoxDetails(mysteryBoxId: String, userLocation: Location) {
        viewModelScope.launch {
            _mysteryBoxDetailState.value = MysteryBoxDetailState.Loading
            Log.d(TAG, "Fetching mystery box details for ID: $mysteryBoxId with user location: $userLocation")

            val result = mysteryBoxRepository.getMysteryBoxDetails(mysteryBoxId, userLocation)
            result.fold(
                onSuccess = { mysteryBox ->
                    Log.d(TAG, "Successfully fetched mystery box details: $mysteryBox")
                    _mysteryBoxDetailState.value = mysteryBox?.let {
                        MysteryBoxDetailState.Success(it)
                    } ?: MysteryBoxDetailState.Error("Mystery box not found")
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to fetch mystery box details: ${exception.message}")
                    _mysteryBoxDetailState.value = MysteryBoxDetailState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

}

// Sealed class to represent the state of the mystery box list
sealed class MysteryBoxListState {
    object Idle : MysteryBoxListState() // No action taken yet
    object Loading : MysteryBoxListState() // Fetching data from the repository
    data class Success(val mysteryBoxes: List<MysteryBox>) : MysteryBoxListState() // Successfully fetched data
    data class Error(val message: String) : MysteryBoxListState() // Error during the data fetch
}

// Sealed class to represent the state of a single mystery box detail
sealed class MysteryBoxDetailState {
    object Idle : MysteryBoxDetailState() // No action taken yet
    object Loading : MysteryBoxDetailState() // Fetching data from the repository
    data class Success(val mysteryBox: MysteryBox) : MysteryBoxDetailState() // Successfully fetched mystery box detail
    data class Error(val message: String) : MysteryBoxDetailState() // Error during the data fetch
}

enum class MysteryBoxFilter {
    Nearest, BestRated, Cheapest, BestSeller, New
}
