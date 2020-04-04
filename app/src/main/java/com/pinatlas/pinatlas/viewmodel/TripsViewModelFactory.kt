package com.pinatlas.pinatlas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/* Owner: AZ */
class TripsViewModelFactory (private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripsViewModel::class.java)) {
            return TripsViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}