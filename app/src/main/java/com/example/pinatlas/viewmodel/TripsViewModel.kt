package com.example.pinatlas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.TripsRepository

class TripsViewModel(userId: String) : ViewModel() {
    val TAG = TripsViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()

    private val _previousTrips = MutableLiveData<List<Trip>>()

    init {
        tripsRepository.fetchTripsForUser(userId).addSnapshotListener { value, _ ->
            _previousTrips.postValue(value!!.toObjects(Trip::class.java))
        }
    }

    val previousTrips : LiveData<List<Trip>>
        get() = _previousTrips
}