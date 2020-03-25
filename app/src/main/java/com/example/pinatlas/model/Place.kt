package com.example.pinatlas.model

import android.graphics.Bitmap
import android.location.Location
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Place {
    var placeId: String = ""                       // Auto populate ID
    // Properties of a place for firestore
    var name: String? = null                       // Name of the location
    var address: String? = null                    // Address of location
    var phoneNumber: String? = null                // Phone number as string
    var rating: Double? = null                     // Google Maps rating
    var types: ArrayList<String>? = null           // Type (museum, hotel, etc...)
    var openingHours: ArrayList<String>? = null    // Google Maps array of hours of operation
    var permanentlyClosed: Boolean? = null         // True if location is permanently closed
    var photos: ArrayList<String>? = null          // URL of photos from google maps
    var coordinates: GeoPoint? = null              // Coordinates of location
    var busyData: BusyData? = null
    var thumbnail: Bitmap? = null
    var startTime: Timestamp = Timestamp(Date())
    var endTime: Timestamp = Timestamp(Date())

    fun openingHoursString() : String {
        var combined = ""
        openingHours?.forEach { combined += it }
        return combined
    }

    // here because it needs to deserialize. If it doesn't find a constructor, it'll break
    constructor()

    constructor(
        placeId: String,
        name: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        rating: Double? = null,
        types: ArrayList<String>? = null,
        openingHours: ArrayList<String>? = null,
        permanentlyClosed: Boolean? = null,
        photos: ArrayList<String>? = null,
        coordinates: GeoPoint? = null,
        busyData: BusyData? = null,
        thumbnail: Bitmap? = null,
        startTime : Timestamp = Timestamp(Date()),
        endTime : Timestamp = Timestamp(Date())
    ) {
        this.placeId = placeId
        this.name = name
        this.address = address
        this.phoneNumber = phoneNumber
        this.rating = rating
        this.types = types
        this.openingHours = openingHours
        this.permanentlyClosed = permanentlyClosed
        this.photos = photos
        this.coordinates = coordinates
        this.busyData = busyData
        this.thumbnail = thumbnail
        this.startTime = startTime
        this.endTime = endTime
    }
}