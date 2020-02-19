package com.example.pinatlas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinatlas.FirestoreRepository
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

class FirestoreViewModel : ViewModel() {
    val TAG = "FIRESTORE_VIEW_MODEL"
    var firebaseRepository = FirestoreRepository()
    var userTrips : MutableLiveData<List<Trip>> = MutableLiveData()
    var tripPlaces : MutableLiveData<List<Place>> = MutableLiveData()
    var newTrip: MutableLiveData<Trip> = MutableLiveData()

    fun saveTrip(trip: Trip) {
        firebaseRepository.saveTrip(trip).addOnFailureListener {
            Log.e(TAG, "Failed to save trip")
        }
    }

    fun fetchTrip(tripId: String): LiveData<Trip> {
        firebaseRepository.fetchTrip(tripId).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                newTrip.value = null
                return@EventListener
            }
            newTrip.value = value?.toObject(Trip::class.java)
        })
        return newTrip
    }

    fun fetchTripsForUser(userId: String): LiveData<List<Trip>> {
        firebaseRepository.fetchTripsForUser(userId).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userTrips.value = null
                return@EventListener
            }
            var userTripsList: MutableList<Trip> = mutableListOf()
            for (doc in value!!) {
                var trip = doc.toObject(Trip::class.java)
                userTripsList.add(trip)
            }
            userTrips.value = userTripsList
        })

        return userTrips
    }


    fun savePlace(place: Place) {
        firebaseRepository.savePlace(place).addOnFailureListener {
            Log.e(TAG, "Failed to save place")
        }
    }

    fun fetchPlacesInTrip(tripId: String): LiveData<List<Place>> {
        firebaseRepository.fetchTripsForUser(tripId).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                tripPlaces.value = null
                return@EventListener
            }
            var tripPlacesList: MutableList<Place> = mutableListOf()
            for (doc in value!!) {
                var place = doc.toObject(Place::class.java)
                tripPlacesList.add(place)
            }
            tripPlaces.value = tripPlacesList
        })

        return tripPlaces
    }


}