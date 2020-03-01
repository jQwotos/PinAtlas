package com.example.pinatlas.utils

import android.content.Context
import android.widget.ImageView
import com.example.pinatlas.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlaceThumbnailUtil(context: Context) {
    private var  placesClient: PlacesClient

    init {
        Places.initialize(context, BuildConfig.PLACES_API_KEY)
        this.placesClient = Places.createClient(context)
    }

    fun populateImageView(placeId: String, view: ImageView) {
        val placeRequest = FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.PHOTO_METADATAS))
        placesClient.fetchPlace(placeRequest).addOnSuccessListener {
            if (it.place.photoMetadatas != null) {
                // TODO: add photoMetadata to Trip.thumbnail to reduce amount of requests
                val photoRequest = FetchPhotoRequest.newInstance(it.place.photoMetadatas!![0])
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photo ->
                    view.setImageBitmap(photo.bitmap)
                }
            }
        }
    }
}