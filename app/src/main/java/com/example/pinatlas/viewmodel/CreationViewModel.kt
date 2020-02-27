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
            trip?.userId = userId
            _trip.postValue(trip)

            if (trip!!.places.size > 0) {
                placesRepository.fetchPlaces(trip.places).get().addOnSuccessListener { placesSnapshot ->
                    _places.postValue(placesSnapshot.toObjects(Place::class.java))
                }.addOnFailureListener {
                    Log.e(TAG, "Couldn't fetch places for trip: ${it.message}")
                }
            } else {
                _places.postValue(listOf())
            }
        }
    }

    fun updatePlacePriority(fromPos: Int, toPos: Int) {
        val arrayOfPlaces = _places.value as ArrayList
        arrayOfPlaces.add(toPos, arrayOfPlaces.removeAt(fromPos))

        _places.postValue(arrayOfPlaces)
    }

    fun setName(name: String) {
        _trip.value?.name = name
        _trip.postValue(_trip.value)
    }

    fun setStartDate(date: Timestamp) {
        _trip.value?.startDate = date
        _trip.postValue(_trip.value)
    }

    fun setEndDate(date: Timestamp) {
        _trip.value?.endDate = date
        _trip.postValue(_trip.value)
    }

    fun saveTrip(): Task<DocumentReference>? {
        return tripsRepository.saveTrip(_trip.value)?.addOnFailureListener {
            Log.e(TAG, "Failed to save trip")
        }
    }

    fun addPlace(place: Place): Task<Void> {
        return placesRepository.savePlace(place).addOnSuccessListener {
            _trip.value?.places?.add(place.placeId)
            _trip.postValue(_trip.value)
        }
    }

    fun deletePlace(i: Int) {
        _trip.value?.places?.removeAt(i)
        _trip.postValue(_trip.value)
    }
}