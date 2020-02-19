package com.example.pinatlas

import androidx.constraintlayout.widget.Constraints
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser


    fun saveTrip(trip: Trip): Task<Void> {
        var documentReference = firestoreDB.collection(Constants.TRIPS_COLLECTION.type)
            .document(trip.tripId)

        return documentReference.set(trip)
    }

    fun fetchTrip(tripId: String): DocumentReference {
        var documentReference = firestoreDB.collection(Constants.TRIPS_COLLECTION.type).document(tripId)
        return documentReference
    }

    fun fetchTripsForUser(userId: String): Query {
        var collectionReference = firestoreDB.collection(Constants.TRIPS_COLLECTION.type)
            .whereEqualTo("userId", userId)

        return collectionReference
    }

    fun savePlace(place: Place): Task<Void> {
        var documentReference = firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .document(place.placeId)

        return documentReference.set(place)
    }

    fun fetchPlace(placeId: String): DocumentReference {
        var documentReference = firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .document(placeId)

        return documentReference
    }

    fun fetchPlacesInTrip(tripId: String): Query {

        var collectionReference = firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .whereArrayContains("tripId", tripId)

        return collectionReference
    }

}