package com.example.pinatlas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pinatlas.repository.TripsRepository
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.PlacesRepository
import com.example.pinatlas.utils.DateUtils


class ItineraryViewModel(tripId: String) : ViewModel() {
    val TAG = ItineraryViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()
    private val placesRepository = PlacesRepository()

    private val _trip = MutableLiveData<Trip>()
    private val _places = MutableLiveData<List<Place>>()

    val tripPlaces : LiveData<List<Place>>
        get() = _places

    val trip: LiveData<Trip>
        get() = _trip

    val startDateStr: LiveData<String> = Transformations.map(_trip) {trip ->
        if (trip != null) DateUtils.formatTimestamp(trip.startDate) else null
    }

    val endDateStr: LiveData<String> = Transformations.map(_trip) {trip ->
        if (trip != null) DateUtils.formatTimestamp(trip.endDate) else null
    }

    val tripName: LiveData<String> = Transformations.map(_trip) {trip ->
        trip!!.name
    }

    init {
        tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = Trip.fromFirestore(tripSnapshot!!)
            _trip.postValue(trip)

            if (trip!!.places.size > 0) {
                placesRepository.fetchPlaces(trip.places as ArrayList<String?>).get().addOnSuccessListener { placesSnapshot ->
                    _places.postValue(placesSnapshot.toObjects(Place::class.java))
                }.addOnFailureListener {
                    Log.e(TAG, "Couldn't fetch places for trip: ${it.message}")
                }
            }
        }
    }
}