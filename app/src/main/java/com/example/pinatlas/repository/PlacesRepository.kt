package com.example.pinatlas.repository

import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Place
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class PlacesRepository {

    // helps us do a log as it typically asks us for the name of the class (to know your log came from)
    val TAG = PlacesRepository::class.java.simpleName
    var firestoreDB = FirebaseFirestore.getInstance()

    fun savePlace(place: Place): Task<Void> {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type).document(place.placeId).set(place)
    }

    fun fetchPlace(placeId: String): DocumentReference {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .document(placeId)
    }

    /**
     * Warning!! This function does not guarantee the order of the ids
     */
    fun fetchPlaces(placeIds: ArrayList<String>) : Query {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .whereIn("placeId", placeIds)
    }
}