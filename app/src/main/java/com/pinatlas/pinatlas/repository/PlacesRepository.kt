package com.pinatlas.pinatlas.repository

import com.pinatlas.pinatlas.constants.Constants
import com.pinatlas.pinatlas.model.Place
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

/**
 * Publish-Subscribe design pattern
 * PlacesRepository acts both as a Publisher and Subscriber depending on it's usage
 */
/* Owner: JL */
class PlacesRepository {

    // TAG helps us do a log as it typically asks us for the name of the class (to know your log came from)
    val TAG = PlacesRepository::class.java.simpleName
    var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Publish-Subscribe design pattern
     * savePlace is a Publish Event
     */
    fun savePlace(place: Place): Task<Void> {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type).document(place.placeId).set(place)
    }

    /**
     * Publish-Subscribe design pattern
     * @return DocumentReference which a subscriber can be attached using addSnapshotListener
     */
    fun fetchPlace(placeId: String): DocumentReference {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .document(placeId)
    }

    /**
     * Warning!! This function does not guarantee the order of the ids
     * Publish-Subscribe design pattern
     * @return Query which can be subscribed to using addSnapshotListener
     */
    fun fetchPlaces(placeIds: ArrayList<String>) : Query {
        return firestoreDB.collection(Constants.PLACES_COLLECTION.type)
            .whereIn("placeId", placeIds)
    }
}