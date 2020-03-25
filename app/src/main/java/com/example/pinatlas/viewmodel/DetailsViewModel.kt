package com.example.pinatlas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.TripsRepository
import com.google.firebase.firestore.ListenerRegistration

class DetailsViewModel(tripId: String, placeId: String) : ViewModel() {
    private val tripsRepository = TripsRepository()

    private val _place = MutableLiveData<Place>()

    var tripListener: ListenerRegistration

    val place: LiveData<Place>
        get() = _place

    val rating: LiveData<String> = Transformations.map(_place) { place ->
        if (place != null) "${place.rating} / 5" else null
    }

    init {
        tripListener = tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = Trip.fromFirestore(tripSnapshot!!)

            if (trip != null && trip.places.size > 0) {
                val place = trip.places.find { p -> p.placeId == placeId }
                _place.postValue(place)
            }
        }
    }
}