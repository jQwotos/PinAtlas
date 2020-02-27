package com.example.pinatlas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.TripsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

class TripsViewModel(userId: String) : ViewModel() {
    val TAG = TripsViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()

    private val _previousTrips = MutableLiveData<List<Trip>>()

    init {
        tripsRepository.fetchTripsForUser(userId).addSnapshotListener { value, _ ->
            val trips = arrayListOf<Trip>()
            for (t in value!!.iterator()) {
                trips.add(Trip.fromFirestore(t)!!)
            }
            _previousTrips.postValue(trips)
        }
    }

    fun addTrip(trip: Trip) : Task<DocumentReference>? {
       return tripsRepository.saveTrip(trip)
    }

    val previousTrips : LiveData<List<Trip>>
        get() = _previousTrips
}