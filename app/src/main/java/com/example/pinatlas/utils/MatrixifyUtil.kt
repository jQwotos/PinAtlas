package com.example.pinatlas.utils

import android.util.Log
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.model.matrix.Salesman
import com.example.pinatlas.model.matrix.SalesmanGenome
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.jetbrains.anko.doAsync

object MatrixifyUtil {

    private val TAG = MatrixifyUtil::class.java.simpleName

    fun repositionPlaces(places: List<String>, optimizedIndex: List<Int>) : Task<List<String>> {
        return Tasks.forResult(optimizedIndex.map { index -> places.get(index) })
    }

    fun generateGenome(distanceMatrixModel: DistanceMatrixModel): Task<SalesmanGenome> {
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
        return Tasks.forResult(geneticAlgorithm.optimize())
    }

    fun optimize(places: List<String>, responseHandler: (result: List<String>?) -> Unit?) {
        doAsync {
            DistanceMatrixProvider.fetchDistanceMatrix(destinations = places as ArrayList<String>){ result: DistanceMatrixModel? ->
                if (result != null ) {
                    generateGenome(distanceMatrixModel = result).continueWithTask { genome: Task<SalesmanGenome> ->
                        repositionPlaces(places, genome.result!!.optimizedRoute)
                    }.addOnSuccessListener { optimizedRoute: List<String> ->
                        responseHandler(optimizedRoute)
                    }
                } else {
                    responseHandler(null)
                }
            }
        }
    }
}