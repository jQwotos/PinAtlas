package com.example.pinatlas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.TripAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Trip
import com.example.pinatlas.viewmodel.TripsViewModel
import com.example.pinatlas.viewmodel.TripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView

class TravelDash : AppCompatActivity() , OnMapReadyCallback, PermissionsListener, TripAdapter.OnTripSelectedListener {

    private val TAG = TravelDash::class.java.simpleName

    private lateinit var mapView : MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var context: Context
    private lateinit var addTripBtn: Button
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    private lateinit var viewModel: TripsViewModel

    private lateinit var pastTripsAdapter: TripAdapter
    private lateinit var upcommingTripsAdapter: TripAdapter

    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    override fun onTripSelected(trip: Trip) {
        val intent = Intent(this, CreationView::class.java)
        intent.putExtra(Constants.TRIP_ID.type, trip.tripId)
        intent.putExtra(Constants.TRIP_NAME.type, trip.name)
        intent.putExtra(Constants.TRIP_START_DATE.type, trip.startDate)
        intent.putExtra(Constants.TRIP_END_DATE.type, trip.endDate)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.traveldash)

        context = this

        //For the Mapbox Implementation
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        addTripBtn = findViewById(R.id.addTripBtn)

        //Local the tiles for past/upcoming trips

        // Factory Pattern: We can create different ViewModels based on the interface
        viewModel = ViewModelProviders.of(
            this,
            TripsViewModelFactory(userId = this.currentUser!!.uid))
            .get(TripsViewModel::class.java)

        pastTripsAdapter = TripAdapter(viewModel.previousTrips, this)

        // Factory Pattern: we modify the ViewModel to do what we need
        // Observer Pattern: we watch when the trips change
        viewModel.previousTrips.observe(this, Observer { update ->
            if (update != null) {
                pastTripsAdapter.notifyDataSetChanged()
            }
        })

        // TODO: Change pastTripsQuery to find past trips instead of all trips
        val pastRecyclerView = findViewById<MultiSnapRecyclerView>(R.id.PTview)
        val pastManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pastRecyclerView.layoutManager = pastManager
        pastRecyclerView.adapter = pastTripsAdapter

        // TODO: Change upcommingTripsQuery to find past trips instead of all trips
//        val upcomRecyclerView = findViewById<MultiSnapRecyclerView>(R.id.UTView)
//        val upcomManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        upcomRecyclerView.layoutManager = upcomManager
//        upcomRecyclerView.adapter = upcommingTripsAdapter
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
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
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show() //Need this is to be removed
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show() //Need this is to be removed
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
    }

    fun createNewTrip(view: View) {
        val intent = Intent(this, CreationView::class.java)

        viewModel.addTrip(Trip(userId = currentUser!!.uid))!!.addOnSuccessListener {
            intent.putExtra(Constants.TRIP_ID.type, it.id)
            startActivity(intent)
        }.addOnFailureListener {
            Log.e(TAG, "Failed to create trip with error $it")
            Toast.makeText(context, "Failed to make trip", Toast.LENGTH_LONG).show()
        }
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




