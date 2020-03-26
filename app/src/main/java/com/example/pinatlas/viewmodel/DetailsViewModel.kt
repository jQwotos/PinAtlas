package com.example.pinatlas.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pinatlas.R
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.TripsRepository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.ListenerRegistration
import java.util.jar.Manifest

class DetailsViewModel(tripId: String, placeId: String) : ViewModel() {
    private val tripsRepository = TripsRepository()

    private val _place = MutableLiveData<Place>()

    var busyDay = 0

    var tripListener: ListenerRegistration

    val place: LiveData<Place>
        get() = _place

    val rating: LiveData<String> = Transformations.map(_place) { place ->
        if (place != null) "${place.rating} / 5" else null
    }

    val timeSpent: LiveData<String> = Transformations.map(_place) { place ->
        if (place != null) "People spend between ${place.busyData!!.avgSpentTimes!![0]} min to ${place.busyData!!.avgSpentTimes!![1]} min here." else null
    }

    val busyTimesEntries: LiveData<List<BarEntry>> = Transformations.map(_place) { place ->
        if (place != null) {
            place.busyData!!.busyTimes!!.get(busyDay).data!!.mapIndexed { index, level ->
                BarEntry(index.toFloat(), level.toFloat())
            }
        } else null
    }

    val busyTimesBarData: LiveData<BarData> = Transformations.map(busyTimesEntries) { busyTimesEntries ->
        if (busyTimesEntries != null) {
            var barDataSet = BarDataSet(busyTimesEntries, busyDay.toString())
            barDataSet.color = R.color.lightGrey
            BarData(barDataSet)
        } else null
    }

    fun callPlace(view: View) {
        var intent: Intent = Intent(Intent.ACTION_CALL, Uri.parse("tel: ${place.value!!.phoneNumber}"))

        if (ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                view.context.startActivity(intent)
        }
    }

    init {
        tripListener = tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = Trip.fromFirestore(tripSnapshot!!)

            if (trip != null && trip.places.size > 0) {
                val place = trip.places.find { p -> p.placeId == placeId }
                _place.postValue(place)
            }
        }
    }
}