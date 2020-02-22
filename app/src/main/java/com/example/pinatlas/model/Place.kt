package com.example.pinatlas.model

import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties;
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Place {
    var placeId: String = ""                       // Auto populate ID
    // Properties of a place for firestore
    var name: String? = null                        // Name of the location
    var address: String? = null                     // Address of location
    var busyTimes: ArrayList<String>? = null       // Array of busy times
    var phoneNumber: String? = null                // Phone number as string
    var rating: Double? = null                       // Google Maps rating
    var types: List<Place.Type>? = null            // Type (museum, hotel, etc...)
    var openingHours: List<String>? = null    // Google Maps array of hours of operation
    var permanentlyClosed: Boolean? = null         // True if location is permanently closed
    var photos: ArrayList<String>? = null           // URL of photos from google maps
    var coordinates: GeoPoint? = null               // Coordinates of location

    constructor()
    constructor(
        placeId: String,
        name: String?,
        address: String?,
        busyTimes: ArrayList<String>?,
        phoneNumber: String?,
        rating: Double?,
        types: List<Place.Type>?,
        openingHours: List<String>?,
        permanentlyClosed: Boolean?,
        photos: ArrayList<String>?,
        coordinates: GeoPoint?
    ) {
        this.placeId = placeId
        this.name = name
        this.address = address
        this.busyTimes = busyTimes
        this.phoneNumber = phoneNumber
        this.rating = rating
        this.types = types
        this.openingHours = openingHours
        this.permanentlyClosed = permanentlyClosed
        this.photos = photos
        this.coordinates = coordinates
    }


}