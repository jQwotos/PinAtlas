package com.example.pinatlas

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.viewmodel.ItineraryViewModel
import com.example.pinatlas.viewmodel.ItineraryViewModelFactory
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions
import com.mapbox.mapboxsdk.plugins.annotation.FillManager
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView



class ItineraryView : AppCompatActivity() , OnMapReadyCallback, PermissionsListener {
    private val TAG = ItineraryView::class.java
    private val context: Context = this

    private lateinit var mapView : MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var tripName: TextView
    private lateinit var viewModel: ItineraryViewModel
    private lateinit var tripId: String
    private lateinit var fillManager: FillManager

    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.itinerary_view)

        //For the Mapbox Implementation
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        tripId = intent.getStringExtra(Constants.TRIP_ID.type)!!
        tripName = findViewById(R.id.tripName)

        val factory = ItineraryViewModelFactory(tripId)
        viewModel = ViewModelProviders.of(this, factory).get(ItineraryViewModel::class.java)
        viewModel.tripName.observe(this,  Observer {
            tripName.text = it
        })

        //Local the tiles for past/upcoming trips
        val adapter = ActivityListAdapter(viewModel.tripPlaces)
        val submitListView = findViewById<MultiSnapRecyclerView>(R.id.sublist_recycler_view)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        submitListView.layoutManager = manager
        submitListView.adapter = adapter

        viewModel.tripPlaces.observe(this, Observer { update ->
            if (update != null) {
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
//            val placesList = viewModel.tripPlaces.value
//            val inLatLngs = ArrayList<LatLng>()
//
//            for(n in 0 until placesList!!.size){
//                inLatLngs.add(LatLng(placesList[n].coordinates!!.latitude, placesList[n].coordinates!!.longitude))
//            }
//            val latLngs = ArrayList<List<LatLng>>()
//            latLngs.add(inLatLngs)
//
//            val fillOptions = FillOptions()
//                .withLatLngs(latLngs)
//            fillManager?.create(fillOptions)
//
//            // random add fills across the globe
//            val fillOptionsList = ArrayList<FillOptions>()
//            fillManager?.create(fillOptionsList)

        }
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapbox_blue))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        //Need this is to be removed
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            //Need this is to be removed
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}




