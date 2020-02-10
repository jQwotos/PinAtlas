package com.example.pinatlas.model

import com.example.pinatlas.constants.TransportationMethods
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Trip {
    var trip_id: String = ""                                // Auto-generated ID by Firestore
    var name: String? = null                                // Name of the trip
    var start_date: LocalDateTime? = null                   // Start date of the trip
    var end_date: LocalDateTime? = null
    var place_ranking: ArrayList<String> = arrayListOf()
    var places: ArrayList<String> = arrayListOf()           // Array of places (not sorted in any way)

    var transportation_methods: ArrayList<TransportationMethods> = arrayListOf()

    constructor(
        trip_id: String,
        name: String?,
        start_date: LocalDateTime?,
        end_date: LocalDateTime?,
        place_ranking: ArrayList<String>,
        places: ArrayList<String>,
        transportation_methods: ArrayList<TransportationMethods>
    ) {
        this.trip_id = trip_id
        this.name = name
        this.start_date = start_date
        this.end_date = end_date
        this.place_ranking = place_ranking
        this.places = places
        this.transportation_methods = transportation_methods
    }
}