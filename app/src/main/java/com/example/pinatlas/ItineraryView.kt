package com.example.pinatlas

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.constants.ViewModes
import com.example.pinatlas.viewmodel.CreationViewModel
import com.example.pinatlas.viewmodel.CreationViewModelFactory
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
import com.mapbox.mapboxsdk.plugins.annotation.Fill
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView

class ItineraryView : AppCompatActivity() , OnMapReadyCallback, PermissionsListener{
    private val TAG = ItineraryView::class.java

    private val context: Context = this

    private lateinit var mapView : MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var tripName: TextView
    private lateinit var viewModel: CreationViewModel
    private lateinit var tripId: String
    private lateinit var markerViewManager : MarkerViewManager
    private lateinit var adapter: ActivityListAdapter
    private lateinit var marker: MarkerView

    private lateinit var  fill : Fill

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }


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

        val factory = CreationViewModelFactory(tripId, currentUser!!.uid)
        viewModel = ViewModelProviders.of(this, factory).get(CreationViewModel::class.java)
        viewModel.tripName.observe(this,  Observer {
            tripName.text = it
        })

        //Local the tiles for past/upcoming trips
        adapter = ActivityListAdapter(viewModel, ViewModes.ITINERARY_MODE)
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
        mapView = findViewById(R.id.mapView)
        viewModel.tripPlaces.observe(this, Observer { placesList ->
            if (placesList != null) {
                val inLatLngs : ArrayList<Feature> = ArrayList<Feature>()

                for (n in 0 until placesList.size) {
                    inLatLngs.add(  Feature.fromGeometry(
                        Point.fromLngLat(placesList[n].coordinates!!.latitude,placesList[n].coordinates!!.longitude))
                    )
                }



                mapboxMap.setStyle(
                    Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                        // Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                            this.getResources(), R.drawable.common_full_open_on_phone))
                        // Adding a GeoJson source for the SymbolLayer icons.
                        .withSource(
                            GeoJsonSource(
                                SOURCE_ID,
                                FeatureCollection.fromFeatures(inLatLngs)
                            )
                        )

                        // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                        // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                        // the coordinate point. This is offset is not always needed and is dependent on the image
                        // that you use for the SymbolLayer icon.
                        .withLayer(
                            SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                    PropertyFactory.iconAllowOverlap(true),
                                    PropertyFactory.iconOffset(arrayOf(0f, -9f)),
                                    PropertyFactory.iconSize(100f)
                                )
                        )
                ) {
                    enableLocationComponent(it)
                }




            }
        })

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
        markerViewManager?.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    fun onClickEditButton(view: View) {
        val intent = Intent(this, CreationView::class.java)
        intent.putExtra(Constants.TRIP_ID.type, tripId)
        startActivity(intent)
    }
    companion object {

        // Create names for the map's source, icon, and layer IDs.
        private val SOURCE_ID = "SOURCE_ID"
        private val ICON_ID = "ICON_ID"
        private val LAYER_ID = "LAYER_ID"
    }

}




