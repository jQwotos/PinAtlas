package com.example.pinatlas.utils

import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.concurrent.TimeUnit

/* Owner: JL  */
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

    fun formatPlaceTime(place: Place) : String {
        val sfd = SimpleDateFormat("HH:mm:ss")
        val start = sfd.parse(place.starttime.toDate().time.plus(5600).toString())
        val end = start.time.plus(3600).toString()
        return "${start} - $end"
    }
}
