package com.example.pinatlas.utils

import android.util.Log
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.model.matrix.Salesman
import com.example.pinatlas.model.matrix.SalesmanGenome
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.jetbrains.anko.doAsync


/* Owner: SS  */
object MatrixifyUtil {

    private val TAG = MatrixifyUtil::class.java.simpleName

    //Maps the algorithm's index to places
    fun repositionPlaces(places: List<Place>, optimizedIndex: List<Int>) : Task<List<Place>> {
        return Tasks.forResult(optimizedIndex.map { index -> places[index] })
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
        //Optimization is done here
        val geneticAlgorithm =
            Salesman(distanceMatrixModel.rows!!.size, travelDurations, 0, 0)
        return Tasks.forResult(geneticAlgorithm.optimize())
    }

    /* Optimize merges DistanceMatrixProvider fetchDistanceMatrix and pipes it into generateGenome */
    fun optimize(places: List<Place>, responseHandler: (result: List<Place>?) -> Unit?) {
        doAsync {
            DistanceMatrixProvider.fetchDistanceMatrix(destinations = places){ result: DistanceMatrixModel? ->
                if (result != null ) {
                    generateGenome(distanceMatrixModel = result).continueWithTask { genome: Task<SalesmanGenome> ->
                        repositionPlaces(places, genome.result!!.optimizedRoute)
                    }.addOnSuccessListener { optimizedRoute: List<Place> ->
                        responseHandler(optimizedRoute)
                    }
                } else {
                    responseHandler(null)
                }
            }
        }
    }
}