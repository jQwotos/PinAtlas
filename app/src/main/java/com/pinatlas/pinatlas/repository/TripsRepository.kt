package com.pinatlas.pinatlas.repository

import com.pinatlas.pinatlas.constants.Constants
import com.pinatlas.pinatlas.model.Trip
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

/* Owner: JL */
class TripsRepository {

    val TAG = TripsRepository::class.java.simpleName
    var tripsCollection = FirebaseFirestore.getInstance().collection(Constants.TRIPS_COLLECTION.type)

    fun saveTrip(trip: Trip?): Task<DocumentReference>? {
        if (trip != null && trip.tripId == null) {
            // create trip
            return tripsCollection.add(trip)
        } else if (trip != null){
            // update trip
            val tripDocument = fetchTrip(trip.tripId!!)
            tripDocument.set(trip)
        }
        return null
    }

    fun deleteTrip(trip: Trip?): Task<Void>? {
        return fetchTrip(trip!!.tripId!!).delete()
    }

    fun fetchTrip(tripId: String): DocumentReference {
        return tripsCollection.document(tripId)
    }

    fun fetchTripsForUser(userId: String): Query {
        return tripsCollection.whereEqualTo("userId", userId)
    }
}