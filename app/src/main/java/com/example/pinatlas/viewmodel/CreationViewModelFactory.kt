package com.example.pinatlas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreationViewModelFactory (private val tripId: String, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreationViewModel::class.java)) {
            return CreationViewModel(tripId, userId) as T
         }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}