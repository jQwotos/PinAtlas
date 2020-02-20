package com.example.pinatlas.utils

import com.example.pinatlas.model.Trip

object DateUtils {
    fun formatTripDate(trip: Trip) : String {
        return "${trip.startDate} - ${trip.endDate}"
    }
}