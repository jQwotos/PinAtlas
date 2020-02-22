package com.example.pinatlas.model

import com.example.pinatlas.constants.TransportationMethods
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList

class Trip(var userId:String, var tripId:String?, var name:String, var startDate:Timestamp,
           var endDate:Timestamp, var placeRanking:ArrayList<String>, var places:ArrayList<String?>,
           var transportationMethods:ArrayList<TransportationMethods>) {

    constructor():this("",null,"",Timestamp(Date()), Timestamp(Date()),
        ArrayList<String>(), ArrayList<String?>(), ArrayList<TransportationMethods>())

    constructor(userId:String):this(userId,null,"",Timestamp(Date()), Timestamp(Date()),
        ArrayList<String>(), ArrayList<String?>(), ArrayList<TransportationMethods>())

    override fun toString(): String {
        return "User: $userId | Trip: $tripId | Name: $name | start_date: ${startDate.toString()} | end_date: ${endDate.toString()}"
    }

    companion object  {
        fun fromFirestore(document: DocumentSnapshot): Trip? {
            val trip =  document.toObject(Trip::class.java)
            trip?.tripId = document.id

            return trip
        }
    }
}