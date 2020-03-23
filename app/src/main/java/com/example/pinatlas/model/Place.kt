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
    var busyTimes: ArrayList<Timings>? = null      // How busy a place is on a Day of the week
    var waitTimes: ArrayList<Timings>? = null      // How long are wait times in minutes
    var avgSpentTimes: ArrayList<Int>? = null      // Range of average time spent in minutes, between [0] to [1] minutes spent
    var thumbnail: Bitmap? = null
    var startTime: Timestamp = Timestamp(Date())
    var endTime: Timestamp = Timestamp(Date())

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
        busyTimes: ArrayList<Timings>? = null,
        waitTimes: ArrayList<Timings>? = null,
        avgSpentTimes: ArrayList<Int>? = null,
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
        this.busyTimes = busyTimes
        this.waitTimes = waitTimes
        this.avgSpentTimes = avgSpentTimes
        this.thumbnail = thumbnail
        this.startTime = startTime
        this.endTime = endTime
    }

    class Timings {
        var name: String? = null
        var data: ArrayList<Int>? = arrayListOf() // an array of the data starting from hour 0 to hour 24
    }
}