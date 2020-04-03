package com.example.pinatlas.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.example.pinatlas.R
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.PlaceDetails
import com.example.pinatlas.model.Trip
import com.example.pinatlas.repository.TripsRepository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.ListenerRegistration
import java.util.jar.Manifest

class DetailsViewModel(tripId: String, placeId: String) : ViewModel() {
    private val tripsRepository = TripsRepository()

    private val _placeDetails = MutableLiveData(PlaceDetails())

    var tripListener: ListenerRegistration

    val busyData: LiveData<BarData> = Transformations.map(_placeDetails) {placeDetails: PlaceDetails? ->
        var barEntries = placeDetails?.Place?.busyData?.busyTimes?.get(placeDetails.busyDay)?.data?.mapIndexed { index, level ->
            BarEntry(index.toFloat(), level.toFloat())
        } ?: arrayListOf()
        var barDataSet = BarDataSet(barEntries, placeDetails?.busyDay.toString())
        barDataSet.color = R.color.quantum_grey
        BarData(barDataSet)
    }

    val place: LiveData<PlaceDetails>
        get() = _placeDetails

    val rating: LiveData<String> = Transformations.map(_placeDetails) { placeDetails ->
        if (placeDetails?.Place?.rating != null) "${placeDetails?.Place?.rating} / 5" else ""
    }

    val timeSpent: LiveData<String> = Transformations.map(_placeDetails) { placeDetails ->
        if (placeDetails.Place?.busyData?.avgSpentTimes != null)
            "People spend between ${placeDetails.Place?.busyData!!.avgSpentTimes!![0]} min to ${placeDetails.Place?.busyData!!.avgSpentTimes!![1]} min here."
        else ""
    }

    fun setBusyDay(day: Int) {
        _placeDetails.value!!.busyDay = day
        _placeDetails.postValue(_placeDetails.value)
    }

    fun callPlace(view: View) {
        var intent = Intent(Intent.ACTION_CALL, Uri.parse("tel: ${place.value!!.Place?.phoneNumber}"))

        if (ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                view.context.startActivity(intent)
        }
    }

    fun openNavigation(view: View) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${place.value!!.Place?.address}"))
        intent.setPackage("com.google.android.apps.maps");

        view.context.startActivity(intent)
    }

    init {
        tripListener = tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = Trip.fromFirestore(tripSnapshot!!)

            if (trip != null && trip.places.size > 0) {
                val place = trip.places.find { p -> p.placeId == placeId }
                _placeDetails.value!!.Place = place
                _placeDetails.postValue(_placeDetails.value)
            }
        }
    }
}