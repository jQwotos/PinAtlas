package com.example.pinatlas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityCreationViewModel : ViewModel() {
    private val _tripName = MutableLiveData<String>()
    val tripName: LiveData<String> = _tripName

    init {
        _tripName.value = ""
    }

    fun setTripName(triplName: String) {
        _tripName.value = triplName
    }
}