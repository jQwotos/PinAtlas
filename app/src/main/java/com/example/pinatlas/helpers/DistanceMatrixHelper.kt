package com.example.pinatlas.helpers

import com.example.pinatlas.helpers.Maps.context
import com.google.maps.DistanceMatrixApi
import com.google.maps.DistanceMatrixApiRequest
import com.google.maps.model.DistanceMatrix

object DistanceMatrixHelper {
    fun getDistanceMatrix(destinations: Array<String>) : DistanceMatrix {
        val apiRequest: DistanceMatrixApiRequest = DistanceMatrixApi.getDistanceMatrix(context, destinations, destinations)
        return apiRequest.await()
    }
}