package com.example.pinatlas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinatlas.repository.TripsRepository
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.PlacesRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

class ActivityCreationViewModel(tripId: String) : ViewModel() {
    val TAG = ActivityCreationViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()
    private val placesRepository = PlacesRepository()

    private val _trip = MutableLiveData<Trip>()
    private val _tripName = MutableLiveData<String>()
    private val _places = MutableLiveData<List<Place>>()

    init {
        tripsRepository.fetchTrip(tripId).addSnapshotListener { value, _ ->
            val trip = value!!.toObject(Trip::class.java)
            _trip.postValue(trip)
            _tripName.postValue(trip!!.name)
        }
        placesRepository.fetchPlacesInTrip(_trip.value!!.tripId).addSnapshotListener { value, _ ->
            _places.postValue(value!!.toObjects(Place::class.java))
        }
    }

    val tripName : LiveData<String>
        get() = _tripName

    val tripPlaces : LiveData<List<Place>>
        get() = _places

    fun saveTrip() {
        tripsRepository.saveTrip(_trip.value)?.addOnFailureListener {
            Log.e(TAG, "Failed to save trip")
        }
    }

    fun addPlace(place: Place) {
        val placeList = _places.value as ArrayList<Place>
        placesRepository.savePlace(place).addOnSuccessListener {
            placeList.add(place)
            _places.postValue(placeList)
        }
    }
}