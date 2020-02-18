package com.example.pinatlas.model

import com.example.pinatlas.constants.TransportationMethods
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.Timestamp
import kotlin.collections.ArrayList

class Trip {
    var user_id: String = ""
    var trip_id: String? = ""                                // Auto-generated ID by Firestore
    var name: String? = null                                // Name of the trip
    var start_date: Timestamp? = null                   // Start date of the trip
    var end_date: Timestamp? = null
    var place_ranking: ArrayList<String> = arrayListOf()
    var places: ArrayList<String> = ArrayList()          // Array of places (not sorted in any way)

    var transportation_methods: ArrayList<TransportationMethods> = arrayListOf()

    override fun toString(): String {
        return "User: $user_id | Trip: $trip_id | Name: $name | start_date: ${start_date.toString()} | end_date: ${end_date.toString()}"
    }

    constructor()

    constructor(
        user_id: String,
        trip_id: String? = "",
        name: String? = "",
        start_date: Timestamp? = null,
        end_date: Timestamp? = null,
        place_ranking: ArrayList<String> = arrayListOf(),
        places: ArrayList<String> = arrayListOf(),
        transportation_methods: ArrayList<TransportationMethods> = arrayListOf()
    ) {
        this.user_id = user_id
        this.trip_id = trip_id
        this.name = name
        this.start_date = start_date
        this.end_date = end_date
        this.place_ranking = place_ranking
        this.places = places
        this.transportation_methods = transportation_methods
    }
}