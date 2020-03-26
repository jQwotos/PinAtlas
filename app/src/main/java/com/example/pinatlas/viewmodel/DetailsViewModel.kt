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

    private val _busyData = MediatorLiveData<BarData>()

    var busyDay = MutableLiveData(0)

    var tripListener: ListenerRegistration

    fun updateBusyData(place: Place?, day: Int?) {
        if (place?.busyData?.busyTimes != null) {
            var barEntries = place.busyData!!.busyTimes!!.get(day!!).data!!.mapIndexed { index, level ->
                BarEntry(index.toFloat(), level.toFloat())
            }
            var barDataSet = BarDataSet(barEntries, day.toString())
            barDataSet.color = R.color.quantum_grey
            _busyData.postValue(BarData(barDataSet))
        }
    }

    val place: LiveData<Place>
        get() = _place

    fun observeBusyData(owner: LifecycleOwner, observer: Observer<BarData>) {
        _busyData.observe(owner, observer)

        _busyData.addSource(_place) { place ->
            updateBusyData(place, busyDay.value)
        }

        _busyData.addSource(busyDay) { day ->
            updateBusyData(place.value, day)
        }
    }

    val rating: LiveData<String> = Transformations.map(_place) { place ->
        if (place!!.rating != null) "${place.rating} / 5" else ""
    }

    val timeSpent: LiveData<String> = Transformations.map(_place) { place ->
        if (place.busyData!!.avgSpentTimes != null)
            "People spend between ${place.busyData!!.avgSpentTimes!![0]} min to ${place.busyData!!.avgSpentTimes!![1]} min here."
        else ""
    }

    fun setBusyDay(day: Int) {
        busyDay.postValue(day)
    }

    fun callPlace(view: View) {
        var intent = Intent(Intent.ACTION_CALL, Uri.parse("tel: ${place.value!!.phoneNumber}"))

        if (ActivityCompat.checkSelfPermission(view.context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                view.context.startActivity(intent)
        }
    }

    fun openNavigation(view: View) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${place.value!!.address}"))
        intent.setPackage("com.google.android.apps.maps");

        view.context.startActivity(intent)
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