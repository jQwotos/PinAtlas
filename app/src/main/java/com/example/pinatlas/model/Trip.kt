package com.example.pinatlas.model

import com.example.pinatlas.constants.TransportationMethods
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
    var places: ArrayList<String?> = ArrayList()          // Array of places (not sorted in any way)
    // var ArrayList<String>

    var transportationMethods: ArrayList<TransportationMethods> = arrayListOf()

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
        places: ArrayList<String?> = arrayListOf(),
        transportationMethods: ArrayList<TransportationMethods> = arrayListOf()
    ) {
        this.userId = userId
        this.tripId = tripId
        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.placeRanking = placeRanking
        this.places = places
        this.transportationMethods = transportationMethods
    }


    companion object  {
        fun fromFirestore(document: DocumentSnapshot): Trip? {
            val trip =  document.toObject(Trip::class.java)
            trip?.tripId = document.id

            return trip
        }
    }
}