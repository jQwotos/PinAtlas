package com.example.pinatlas.utils

import android.net.Uri
import android.util.Log
import com.example.pinatlas.BuildConfig
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.constants.TransportationMethods
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import java.io.Reader

/* Owner: JL  */
object DistanceMatrixProvider {
    var TAG = DistanceMatrixProvider::class.java.simpleName

    val DISTANCE_MATRIX_BASE_URL: String = "maps.googleapis.com"

    fun createDestinationsString(destinations: List<String>) : String {
        val appendedNames = destinations.map { destination -> "place_id:$destination" }
        return appendedNames.joinToString (separator = "|")
    }

    fun buildDistanceMatrixURI(destinations: List<Place>, mode: String = TransportationMethods.DRIVING.type) : String {
        var destinationIDs = destinations.map { dest -> dest.placeId }
        var destinationsString: String = createDestinationsString(destinationIDs)

        return Uri.Builder()
            .scheme("https")
            .authority(DISTANCE_MATRIX_BASE_URL)
            .appendPath("maps")
            .appendPath("api")
            .appendPath("distancematrix")
            .appendPath("json")
            .appendQueryParameter("units", "metrics")
            .appendQueryParameter("mode", mode)
            .appendQueryParameter("origins", destinationsString)
            .appendQueryParameter("destinations", destinationsString)
            .appendQueryParameter("key", BuildConfig.DISTANCE_MATRIX_API_KEY)
            .build()
            .toString()
    }

    /**
     * Fetches distance matrix from google api
     * we use the distance matrix to calculate the time it takes to get
     * between each point which will be used
     * for calculating the optimal route.
     *
     * @param destinations List of destinations for the trip
     * @param mode mode of transportation
     * @param responseHandler procedure that is invoked when query is finished
     */
    fun fetchDistanceMatrix(destinations: List<Place>, mode: String = TransportationMethods.DRIVING.type, responseHandler: (result: DistanceMatrixModel?) -> Any?){
        buildDistanceMatrixURI(destinations = destinations, mode = mode).httpGet().responseObject(DistanceMatrixDeserializer()) { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    Log.w(TAG, "Error when fetching distance matrix: " + result.getException())
                    responseHandler(null)

                }

                is Result.Success -> {
                    val (data, _) = result
                    val distanceMatrixModel = data as DistanceMatrixModel

                    if (distanceMatrixModel.status.equals(Constants.REQUEST_DENIED.type)) {
                        Log.w(TAG, "Warning: Distance Matrix query failed, is your API key out of uses or enabled for distance matrix?")
                    }
                    responseHandler(distanceMatrixModel)
                }
            }
        }
    }

    class DistanceMatrixDeserializer: ResponseDeserializable<DistanceMatrixModel> {
        override fun deserialize(reader: Reader): DistanceMatrixModel? {
            return Gson().fromJson(reader, DistanceMatrixModel::class.java)
        }
    }
}