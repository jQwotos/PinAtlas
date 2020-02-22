package com.example.pinatlas.utils

import com.example.pinatlas.model.Trip
import java.sql.Timestamp
import java.text.SimpleDateFormat

object DateUtils {
    fun formatTripDate(trip: Trip) : String {
        val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
        val startDate = dateFormat.format(Timestamp(trip.startDate.seconds * 1000))
        val endDate = dateFormat.format(Timestamp(trip.endDate.seconds * 1000))
        return "$startDate - $endDate"
    }
}
