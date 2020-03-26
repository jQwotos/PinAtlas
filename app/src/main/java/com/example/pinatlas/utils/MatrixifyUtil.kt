package com.example.pinatlas.utils

import android.text.format.DateUtils
import android.util.Log
import android.widget.Button
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.model.matrix.Salesman
import com.example.pinatlas.model.matrix.SalesmanGenome
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import org.jetbrains.anko.doAsync
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/* Owner: SS  */
object MatrixifyUtil {

    private val TAG = MatrixifyUtil::class.java.simpleName

    //Maps the algorithm's index to places
    fun repositionPlaces(places: List<Place>, optimizedIndex: List<Int>, distanceMatrixModel: DistanceMatrixModel) : Task<List<Place>> {
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
        //Optimization is done here
        val geneticAlgorithm =
            Salesman(distanceMatrixModel.rows!!.size, travelDurations, 0, 0)
        return Tasks.forResult(geneticAlgorithm.optimize())
    }



    fun setPlaceTimes(places : List<Place>, distanceMatrixModel: DistanceMatrixModel, tripstart: Timestamp, tripend: Timestamp): List<Place> {
        var placesout : List<Place> = places

        var start : Calendar = Calendar.getInstance()
        start.setTime(tripstart.toDate())
        start.set(Calendar.HOUR,9)
        start.set(Calendar.MINUTE,30)
        start.set(Calendar.SECOND,0)
        start.set(Calendar.MILLISECOND,0)

        var endday : Calendar = Calendar.getInstance()
        endday.setTime(tripstart.toDate())
        endday.set(Calendar.HOUR,22)
        endday.set(Calendar.MINUTE,30)
        endday.set(Calendar.SECOND,0)
        endday.set(Calendar.MILLISECOND,0)

        var end : Calendar = Calendar.getInstance()
        end.setTime(tripend.toDate())
        end.set(Calendar.HOUR,22)
        end.set(Calendar.MINUTE,30)
        end.set(Calendar.SECOND,0)
        end.set(Calendar.MILLISECOND,0)


        for(place in placesout){
            if(placesout.indexOf(place) == placesout.size - 1) {
                place.traveltime = 0
            }else{
                val placeOneIndex : Int = distanceMatrixModel.origin_addresses!!.indexOf(place.address)
                val placeTwoIndex : Int = distanceMatrixModel.destination_addresses!!.indexOf(placesout.get(placesout.indexOf(place)+1).address)
                val duration : Long? = distanceMatrixModel.rows!!.get(placeOneIndex).elements.get(placeTwoIndex).duration!!.value
                place.traveltime = duration
            }
        }

        for(place in placesout){
            //if(start)
            if(placesout.indexOf(place) == 0) {
                start.set(Calendar.HOUR,start.get(Calendar.HOUR)+2)
                place.starttime = Timestamp(start.time)
            }
            else{
                if(start.compareTo(endday)>=0){ // Figure out comparisonL
                    start.set(Calendar.HOUR,11)
                    start.set(Calendar.MINUTE,30)
                    start.set(Calendar.SECOND,0)
                    start.set(Calendar.DATE,start.get(Calendar.DATE)+1)
                    endday.set(Calendar.DATE,endday.get(Calendar.DATE)+1)
                    place.starttime = Timestamp(start.time)
                }
                else{
                    start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + Integer.parseInt(placesout.get(placesout.indexOf(place) - 1).traveltime.toString()))
                    place.starttime = Timestamp(start.time)
                }
            }
        }
        return placesout

    }



    /* Optimize merges DistanceMatrixProvider fetchDistanceMatrix and pipes it into generateGenome */
    fun optimizer(places: List<Place>, tripstart: Timestamp, tripend: Timestamp, responseHandler: (result: List<Place>?) -> Unit?) {
        doAsync {
            DistanceMatrixProvider.fetchDistanceMatrix(destinations = places as ArrayList<Place>){ result: DistanceMatrixModel? ->
                if (result != null ) {
                    generateGenome(distanceMatrixModel = result).continueWithTask { genome: Task<SalesmanGenome> ->
                        repositionPlaces(places, genome.result!!.optimizedRoute, result)
                    }.addOnSuccessListener { optimizedRoute: List<Place> ->
                        responseHandler(setPlaceTimes(optimizedRoute,result, tripstart, tripend))
                    }
                } else {
                    responseHandler(null)
                }
            }
        }
    }
}