package com.example.pinatlas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItineraryViewModelFactory (private val tripId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItineraryViewModel::class.java)) {
            return ItineraryViewModel(tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}