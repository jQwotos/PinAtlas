package com.pinatlas.pinatlas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.pinatlas.pinatlas.adapter.TripAdapter
import com.pinatlas.pinatlas.constants.Constants
import com.pinatlas.pinatlas.model.Trip
import com.pinatlas.pinatlas.viewmodel.TripsViewModel
import com.pinatlas.pinatlas.viewmodel.TripsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView

class TravelDash : AppCompatActivity() , OnMapReadyCallback, PermissionsListener, TripAdapter.OnTripSelectedListener {

    private val TAG = TravelDash::class.java.simpleName

    //Mapbox
    private lateinit var mapView : MapView
    private lateinit var mapboxMap: MapboxMap

    //Items
    private lateinit var addTripBtn: Button

    // FIREBASE
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }
    private lateinit var viewModel: TripsViewModel
    private lateinit var pastTripsAdapter: TripAdapter
    private lateinit var upcommingTripsAdapter: TripAdapter

    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    override fun onTripSelected(trip: Trip) {
        val intent = Intent(this, ItineraryView::class.java)
        intent.putExtra(Constants.TRIP_ID.type, trip.tripId)
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

        pastTripsAdapter = TripAdapter(viewModel.previousTrips, context, this)

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

        // upcoming trips
        upcommingTripsAdapter = TripAdapter(viewModel.upcomingTrips, this, this)
        viewModel.upcomingTrips.observe(this, Observer { update ->
            if (update != null) {
                upcommingTripsAdapter.notifyDataSetChanged()
            }
        })

        val upcomingRecyclerView = findViewById<MultiSnapRecyclerView>(R.id.UTView)
        val upcomingManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        upcomingRecyclerView.layoutManager = upcomingManager
        upcomingRecyclerView.adapter = upcommingTripsAdapter
    }

    fun updateStyle(style: Style) {
        var upcommingTrips = viewModel.upcomingTrips.value
        var pastTrips = viewModel.previousTrips.value

        if (upcommingTrips != null) {
            this.drawPlacesFromTrips(
                Constants.UPCOMING_PLACES_LAYER_ID.type,
                Constants.UPCOMING_PLACES_ICON_ID.type, style, upcommingTrips
            )
        }

        if (pastTrips != null) {
            this.drawPlacesFromTrips(
                Constants.PREVIOUS_PLACES_LAYER_ID.type,
                Constants.PREVIOUS_PLACES_ICON_ID.type, style, pastTrips
            )
        }
    }

    fun setMapStyle(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.Builder()
            .fromUri("mapbox://styles/davidchopin/cjtz90km70tkk1fo6oxifkd67")) { style ->
            style.addImage(
                Constants.UPCOMING_PLACES_ICON_ID.type,
                BitmapFactory.decodeResource(resources, R.drawable.blue_pin))
            style.addImage(
                Constants.PREVIOUS_PLACES_ICON_ID.type,
                BitmapFactory.decodeResource(resources, R.drawable.pink_pin))
            updateStyle(style)
            enableLocationComponent(style)
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        viewModel.previousTrips.observe(this, Observer {
            setMapStyle(this.mapboxMap)
        })

        viewModel.upcomingTrips.observe(this, Observer {
            setMapStyle(this.mapboxMap)
        })

    }

    fun drawPlacesFromTrips(layerId: String, iconId: String, style: Style, trips: List<Trip>) {
        val sourceId = "${layerId}_SOURCE"

        style.removeSource(sourceId)
        style.removeLayer(layerId)

        val latLngs = arrayListOf<Feature>()
        for (trip in trips) {
            latLngs.addAll(trip.places.map { place ->
                Feature.fromGeometry(Point.fromLngLat(
                    place.coordinates!!.longitude,
                    place.coordinates!!.latitude
                ))
            })
        }

        style.addSource(GeoJsonSource(sourceId, FeatureCollection.fromFeatures(latLngs)))
        style.addLayer(
            SymbolLayer(layerId, sourceId)
                .withProperties(
                    PropertyFactory.iconImage(iconId),
                    PropertyFactory.iconIgnorePlacement(true),
                    PropertyFactory.iconSize(0.5f),
                    PropertyFactory.iconAllowOverlap(true)
                )
        )
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
        }
        else {
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
        viewModel.tripsListener.remove()
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

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        intent = Intent(this, GoogleLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    companion object {
        lateinit var context: Context
    }

}

