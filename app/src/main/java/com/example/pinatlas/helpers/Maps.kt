package com.example.pinatlas.helpers

import com.example.pinatlas.BuildConfig
import com.google.maps.GeoApiContext

object Maps {
    val context: GeoApiContext = GeoApiContext.Builder()
        .apiKey(BuildConfig.PLACES_API_KEY).build()
}