package com.bersamadapa.recylefood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Generic ViewModel Factory
class ViewModelFactory<T : ViewModel>(
    private val creator: (String) -> T // Creator function that takes parameters needed for ViewModel constructor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the model class is the one we want to instantiate
        @Suppress("UNCHECKED_CAST")
        return creator(modelClass.simpleName) as T
    }
}
