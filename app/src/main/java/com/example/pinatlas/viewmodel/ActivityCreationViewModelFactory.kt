package com.example.pinatlas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActivityCreationViewModelFactory (private val tripId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityCreationViewModel::class.java)) {
            return ActivityCreationViewModel(tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}