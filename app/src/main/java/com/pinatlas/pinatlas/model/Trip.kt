package com.pinatlas.pinatlas.model

import com.pinatlas.pinatlas.constants.TransportationMethods
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList

class Trip {
    var userId: String = ""
    var tripId: String? = ""                                // Auto-generated ID by Firestore
    var name: String? = null                                // Name of the trip
    var startDate: Timestamp = Timestamp(Date())            // Start date of the trip
    var endDate: Timestamp = Timestamp(Date())
    var placeRanking: ArrayList<String> = arrayListOf()
    var places: ArrayList<Place> = ArrayList()          // Array of places (not sorted in any way)

    var transportationMethods: ArrayList<String> = arrayListOf()

    override fun toString(): String {
        return "User: $userId | Trip: $tripId | Name: $name | start_date: ${startDate.toString()} | end_date: ${endDate.toString()}"
    }

    constructor()

    constructor(
        userId: String,
        tripId: String? = null,
        name: String? = "",
        startDate: Timestamp = Timestamp(Date()),
        endDate: Timestamp = Timestamp(Date()),
        placeRanking: ArrayList<String> = arrayListOf(),
        places: ArrayList<Place> = arrayListOf(),
        transportationMethods: ArrayList<String> = arrayListOf(TransportationMethods.TRANSIT.type)
    ) {
        this.userId = userId
        this.tripId = tripId
        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.placeRanking = placeRanking
        this.places = places //Places ID
        this.transportationMethods = transportationMethods
    }

    // convert a firestore doc into an actual object
    companion object  {
        fun fromFirestore(document: DocumentSnapshot): Trip? {
            val trip =  document.toObject(Trip::class.java)
            trip?.tripId = document.id
            return trip
        }
    }
}