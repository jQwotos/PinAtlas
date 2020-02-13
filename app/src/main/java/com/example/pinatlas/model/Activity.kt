package com.example.pinatlas.model

import com.example.pinatlas.constants.TransportationMethods
import java.time.LocalDateTime

class Activity {
    var place_id: String = ""
    var trip_id: String = ""
    var start_time: LocalDateTime? = null
    var end_time: LocalDateTime? = null
    var transportation_mode: TransportationMethods? = null

    constructor(
        place_id: String,
        trip_id: String,
        start_time: LocalDateTime?,
        end_time: LocalDateTime?,
        transportation_mode: TransportationMethods?
    ) {
        this.place_id = place_id
        this.trip_id = trip_id
        this.start_time = start_time
        this.end_time = end_time
        this.transportation_mode = transportation_mode
    }
}