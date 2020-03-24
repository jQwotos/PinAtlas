package com.example.pinatlas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pinatlas.model.BusyData
import com.example.pinatlas.repository.TripsRepository
import com.example.pinatlas.model.Place
import com.example.pinatlas.model.Trip
import com.example.pinatlas.utils.BusyTimesUtil
import com.example.pinatlas.utils.DateUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration

/* Owner: AZ */
class CreationViewModel(tripId: String, userId: String) : ViewModel() {
    val TAG = CreationViewModel::class.java.simpleName

    private val tripsRepository = TripsRepository()

    private val _trip = MutableLiveData<Trip>()
    private val _places = MutableLiveData<List<Place>>()

    var tripListener: ListenerRegistration

    val tripPlaces : LiveData<List<Place>>
        get() = _places

    val trip: LiveData<Trip>
        get() = _trip

    val startDateStr: LiveData<String> = Transformations.map(_trip) {trip ->
        if (trip != null) DateUtils.formatTimestamp(trip.startDate) else null
    }

    val endDateStr: LiveData<String> = Transformations.map(_trip) {trip ->
        if (trip != null) DateUtils.formatTimestamp(trip.endDate) else null
    }

    val tripName: LiveData<String> = Transformations.map(_trip) {trip ->
        trip?.name
    }

    val latLng: MutableLiveData<GeoPoint> = MutableLiveData<GeoPoint>()

    init {
        tripListener =  tripsRepository.fetchTrip(tripId).addSnapshotListener { tripSnapshot, _ ->
            val trip = Trip.fromFirestore(tripSnapshot!!)
            trip?.userId = userId
            _trip.postValue(trip)

            if (trip != null && trip.places.size > 0) {
                var lat = 0.0
                var lgt = 0.0

                trip.places.forEach { place ->
                    lat += place.coordinates!!.latitude / trip.places.size
                    lgt += place.coordinates!!.longitude / trip.places.size
                    latLng.postValue(GeoPoint(lat, lgt))
                }

                _places.postValue(trip.places)
            } else {
                _places.postValue(listOf())
            }
        }
    }

    fun updatePlacePriority(fromPos: Int, toPos: Int) {
        val arrayOfPlaces = _places.value as ArrayList
        arrayOfPlaces.add(toPos, arrayOfPlaces.removeAt(fromPos))
        _trip.value!!.places = arrayOfPlaces
        _trip.postValue(_trip.value)
        _places.postValue(arrayOfPlaces)
    }

    fun setName(name: String) {
        _trip.value?.name = name
        _trip.postValue(_trip.value)
    }

    fun setStartDate(date: Timestamp) {
        _trip.value?.startDate = date
        _trip.postValue(_trip.value)
    }

    fun setEndDate(date: Timestamp) {
        _trip.value?.endDate = date
        _trip.postValue(_trip.value)
    }

    fun saveTrip(): Task<DocumentReference>? {
        return tripsRepository.saveTrip(_trip.value)?.addOnFailureListener {
            Log.e(TAG, "Failed to save trip")
        }
    }

//    fun toggleTransportationMethod(method: String): Task<Void>? {
//        if (_trip.value?.transportationMethods!!.contains(method)) {
//            _trip.value?.transportationMethods?.remove(method)
//        } else {
//            _trip.value?.transportationMethods?.add(method)
//        }
//        _trip.postValue(_trip.value)
//    }

    fun deleteTrip(): Task<Void>? {
        tripListener.remove()
        return tripsRepository.deleteTrip(_trip.value)
    }

    fun addPlace(place: Place) {
        BusyTimesUtil.fetchBusyTimesData(place.placeId) { result: BusyData? ->
            if (result != null) {
                place.busyData = result
            }
            _trip.value?.places?.add(place)
            _trip.postValue(_trip.value)
            saveTrip()
        }
    }

    fun reorderPlaces(places: List<Place>) {
        _trip.value?.places?.clear()
        _trip.value?.places?.addAll(places)
        _trip.postValue(_trip.value)
    }

    fun deletePlace(i: Int) {
        _trip.value?.places?.removeAt(i)
        _trip.postValue(_trip.value)
    }
}