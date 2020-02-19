package com.example.pinatlas.repository

import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Place
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class PlacesRepository {

    val TAG = PlacesRepository::class.java.simpleName
    var firestoreDB = FirebaseFirestore.getInstance()


    fun savePlace(place: Place): Task<DocumentReference> {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type).add(place)
    }

    fun fetchPlace(placeId: String): DocumentReference {

        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .document(placeId)
    }

    fun fetchPlacesInTrip(tripId: String): Query {

        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .whereArrayContains("tripId", tripId)
    }

}