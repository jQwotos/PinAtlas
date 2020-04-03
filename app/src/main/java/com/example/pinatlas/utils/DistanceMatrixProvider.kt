package com.example.pinatlas.utils

import android.net.Uri
import android.util.Log
import com.example.pinatlas.BuildConfig
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.constants.TransportationMethods
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.model.matrix.Element
import com.example.pinatlas.model.matrix.Row
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import java.io.Reader
import java.lang.Exception

/* Owner: JL  */
object DistanceMatrixProvider {
    var TAG = DistanceMatrixProvider::class.java.simpleName

    val DISTANCE_MATRIX_BASE_URL: String = "maps.googleapis.com"

    fun createDestinationsString(destinations: ArrayList<Place>) : String {
        val appendedNames = destinations.map { destination -> "place_id:${destination.placeId}" }
        return appendedNames.joinToString (separator = "|")
    }

    fun buildDistanceMatrixURI(destinations: ArrayList<Place>, mode: String = TransportationMethods.DRIVING.type) : String {
        var destinationsString: String = createDestinationsString(destinations)

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
    fun optimizeMatrices(matrixes: List<DistanceMatrixModel>): DistanceMatrixModel? {
        return matrixes[0].rows?.foldIndexed(DistanceMatrixModel(
            status = matrixes[0].status,
            origin_addresses = matrixes[0].origin_addresses,
            destination_addresses = matrixes[0].destination_addresses
        )) { rowIndex, acc, row ->
            acc.rows?.add(Row(
                elements = ArrayList(row.elements.mapIndexedNotNull { elemenIndex, _ ->
                    var allElements = matrixes.mapNotNull {
                        var element: Element? = it.rows?.get(rowIndex)?.elements?.get(elemenIndex)
                        if (element?.duration?.value != null) element else null
                    }
                    allElements.minBy { it.duration!!.value }
                })
            ))
            acc
        }
    }

    fun fetchAllDistanceMatrixes(destinations: ArrayList<Place>, modes: List<String>, responseHandler: (result: DistanceMatrixModel?) -> Any?) {
        try {
            var results = modes.map {mode ->
                buildDistanceMatrixURI(destinations = destinations, mode = mode).httpGet().responseObject(DistanceMatrixDeserializer())
            }
            var matrices = results.map { result ->
                result.third.get()
            }
            responseHandler(optimizeMatrices(matrices))
        } catch(e: Exception) {
            responseHandler(null)
        }
    }

    class DistanceMatrixDeserializer: ResponseDeserializable<DistanceMatrixModel> {
        override fun deserialize(reader: Reader): DistanceMatrixModel? {
            return Gson().fromJson(reader, DistanceMatrixModel::class.java)
        }
    }
}