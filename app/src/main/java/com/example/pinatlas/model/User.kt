package com.example.pinatlas.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    var user_id: String = ""                                // Auto-generated id from firestore
    var trips: ArrayList<String> = arrayListOf<String>()    // Array of tripIDs

    constructor()

    constructor(user_id: String, trips: ArrayList<String>) {
        this.user_id = user_id
        this.trips = trips
    }

    fun addTrip(tripID: String) {
        trips.add(tripID)
    }

    fun deleteTrip(tripID: String) {
        trips.remove(tripID)
    }

    fun clearTrips() {
        trips.clear()
    }

    fun getAllTrips() : ArrayList<String> {
        return trips
    }
}