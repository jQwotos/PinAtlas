package com.pinatlas.pinatlas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pinatlas.pinatlas.model.Trip
import com.pinatlas.pinatlas.repository.TripsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

/* Owner: AZ */
class TripsViewModel(userId: String) : ViewModel() {
    val TAG = TripsViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()
    var tripsListener: ListenerRegistration

    private val _trips = MutableLiveData<List<Trip>>()
    private val _previousTrips = MutableLiveData<List<Trip>>()
    private val _upcomingTrips = MutableLiveData<List<Trip>>()

    init {
        tripsListener = tripsRepository.fetchTripsForUser(userId).addSnapshotListener { value, _ ->
            val trips = arrayListOf<Trip>()
            val previous = arrayListOf<Trip>()
            val upcoming = arrayListOf<Trip>()
            for (t in value!!.iterator()) {
                val trip = Trip.fromFirestore(t)!!
                val now = Timestamp(Date())
                trips.add(trip)
                if (trip.endDate.seconds < now.seconds) {
                    previous.add(trip)
                } else {
                    upcoming.add(trip)
                }
            }
            _trips.postValue(trips)
            _previousTrips.postValue(previous)
            _upcomingTrips.postValue(upcoming)
        }
    }

    fun addTrip(trip: Trip) : Task<DocumentReference>? {
       return tripsRepository.saveTrip(trip)
    }

    val previousTrips : LiveData<List<Trip>>
        get() = _previousTrips

    val upcomingTrips : LiveData<List<Trip>>
        get() = _upcomingTrips
}