package com.example.pinatlas.model

import android.location.Location
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDateTime
import kotlin.collections.ArrayList

@IgnoreExtraProperties
data class Place (
    var placeId: String,                       // Auto populate ID
    // Properties of a place for firestore
    var name: String,                        // Name of the location
    var address: String,                     // Address of location
    var busyTimes: ArrayList<String>,       // Array of busy times
    var phoneNumber: String,                // Phone number as string
    var rating: Float,                       // Google Maps rating
    var types: ArrayList<String>,            // Type (museum, hotel, etc...)
    var openingHours: ArrayList<String>,    // Google Maps array of hours of operation
    var permanentlyClosed: Boolean,         // True if location is permanently closed
    var photos: ArrayList<String>,           // URL of photos from google maps
    var coordinates: Location               // Coordinates of location
)