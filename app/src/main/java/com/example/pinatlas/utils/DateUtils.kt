package com.example.pinatlas.utils

import com.example.pinatlas.model.Trip
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

object DateUtils {
    val dateFormat: DateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)

    fun formatTimestamp(timestamp: Timestamp) : String {
        return dateFormat.format(timestamp.seconds * 1000)
    }

    fun formatTripDate(trip: Trip) : String {
        val startDate = formatTimestamp(trip.startDate)
        val endDate = formatTimestamp(trip.endDate)
        return "$startDate - $endDate"
    }
}
