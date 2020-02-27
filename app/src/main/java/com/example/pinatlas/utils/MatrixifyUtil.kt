package com.example.pinatlas.utils

import android.util.Log
import android.view.View
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.model.matrix.Salesman

class MatrixifyUtil(private val places: List<Place>) {
    private val TAG = MatrixifyUtil::class.java.simpleName

    fun finishFetchingDistanceMatrix(distanceMatrixModel: DistanceMatrixModel) {
        Log.d(TAG, distanceMatrixModel.rows!!.indices.toString())
        Log.d(TAG, distanceMatrixModel.status)

        val travelDurations = Array(distanceMatrixModel.rows!!.size) { IntArray(distanceMatrixModel.rows!!.size) }
        for(n in 0 until (distanceMatrixModel.rows!!.size - 1)) {
            for(k in 0 until (distanceMatrixModel.rows!!.size - 1)) {
                travelDurations[n][k] = distanceMatrixModel.rows!!.get(n).elements!!.get(k).duration!!.value.toInt()
                Log.d(TAG,distanceMatrixModel.rows!!.get(n).elements!!.get(k).duration!!.value.toString())
            }
            Log.d(TAG,"\n\n")
        }

        val geneticAlgorithm =
            Salesman(distanceMatrixModel.rows!!.size, travelDurations, 0, 0)
        val result = geneticAlgorithm.optimize()
        Log.d(TAG,result.toString())
    }

    fun createMatrix(view: View) {
        // TODO: Use the new get trip Places function
        var placesIdArray: ArrayList<String> = arrayListOf()
        for (place in places) {
            placesIdArray.add(place.name!!)
        }

        DistanceMatrixProvider.fetchDistanceMatrix(placesIdArray) {
                result: DistanceMatrixModel ->
            finishFetchingDistanceMatrix(result) // After we fetched invoke function
        }
        // TODO: SHUBHAM LOOK HERE
    }
}