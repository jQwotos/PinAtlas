package com.example.pinatlas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinatlas.repository.TripsRepository
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.PlacesRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class CreationViewModel(tripId: String, userId: String) : ViewModel() {
    val TAG = CreationViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()
    private val placesRepository = PlacesRepository()

    private val _trip = MutableLiveData<Trip>()
    private val _places = MutableLiveData<List<Place>>()

    val tripPlaces : LiveData<List<Place>>
        get() = _places

    init {
        tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = tripSnapshot!!.toObject(Trip::class.java)
            trip?.userId = userId
            trip?.tripId = tripSnapshot.id
            _trip.postValue(trip)

            if (trip!!.places.size > 0) {
                placesRepository.fetchPlaces(trip!!.places).get().addOnSuccessListener { placesSnapshot ->
                    val t = trip
                    val p = placesSnapshot!!.toObjects(Place::class.java)
                    _places.postValue(placesSnapshot!!.toObjects(Place::class.java))
                }
            }
        }
    }

    fun setName(name: String) {
        _trip.value?.name = name
    }

    fun setStartDate(date: Timestamp) {
        _trip.value?.startDate = date
    }

    fun setEndDate(date: Timestamp) {
        val trip = _trip.value
        trip?.endDate = date
        _trip.postValue(trip)
    }

    fun saveTrip(): Task<DocumentReference>? {
        return tripsRepository.saveTrip(_trip.value)?.addOnFailureListener {
            Log.e(TAG, "Failed to save trip")
        }
    }

    fun addPlace(place: Place) {
        placesRepository.savePlace(place).addOnSuccessListener {
            _trip.value?.places?.add(place.placeId)
            _trip.postValue(_trip.value)
        }
    }
}