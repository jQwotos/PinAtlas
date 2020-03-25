package com.example.pinatlas

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.PlaceListAdapter
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
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Fill
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView

class ItineraryView : AppCompatActivity() , OnMapReadyCallback, PermissionsListener{
    private val context: Context = this
    private lateinit var mapView : MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var tripName: TextView
    private lateinit var viewModel: CreationViewModel
    private lateinit var tripId: String
    private lateinit var adapter: PlaceListAdapter

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

        tripId = intent.getStringExtra("TRIP_ID")!!
        tripName = findViewById(R.id.tripName)

        val factory = CreationViewModelFactory(tripId, currentUser!!.uid)
        viewModel = ViewModelProviders.of(this, factory).get(CreationViewModel::class.java)
        viewModel.tripName.observe(this,  Observer {
            tripName.text = it
        })

        //Local the tiles for past/upcoming trips
        adapter = PlaceListAdapter(viewModel, ViewModes.ITINERARY_MODE, this)
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
        var isInit = false
        viewModel.tripPlaces.observe(this, Observer { placesList ->
            if (placesList != null) {
                val inLatLngs : ArrayList<Feature> = ArrayList<Feature>()
                val markers : MutableList<Point> = ArrayList()
                for (n in 0 until placesList.size) {
                    inLatLngs.add(  Feature.fromGeometry(
                        Point.fromLngLat(placesList[n].coordinates!!.longitude, placesList[n].coordinates!!.latitude))
                    )
                    markers.add(Point.fromLngLat(placesList[n].coordinates!!.longitude, placesList[n].coordinates!!.latitude))
                }
                if (placesList.isNotEmpty()) {
                    viewModel.latLng.observe(this, Observer { coordinates ->
                        val lat = coordinates.latitude
                        val lng = coordinates.longitude
                        val zoom = when (isInit) {
                            true -> 12.5
                            else -> 11.0
                        }
                        this.mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
                        isInit = true
                    })
                }
                mapboxMap.setStyle(Style.MAPBOX_STREETS){style ->
                    // Add the SymbolLayer icon im
                    // age to the map style
                    style.addImage(ICON_ID, BitmapFactory.decodeResource(
                        this.getResources(), R.drawable.mapbox_compass_icon))
                    // Adding a GeoJson source for the SymbolLayer icons.
                    style.addSource(
                        GeoJsonSource(
                            SOURCE_ID,
                            FeatureCollection.fromFeatures(inLatLngs)
                        )
                    )
                    style.addLayer(
                        SymbolLayer(LAYER_ID, SOURCE_ID)
                            .withProperties(
                                PropertyFactory.iconImage(ICON_ID),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(1f),
                                PropertyFactory.iconAllowOverlap(true)
                            )
                    )

                    style.addSource(
                        GeoJsonSource(
                            "line-source",
                            FeatureCollection.fromFeatures(
                                arrayOf(
                                    Feature.fromGeometry(
                                        LineString.fromLngLats(markers!!)
                                    )
                                )
                            )
                        )
                    )

                    style.addLayer(
                        LineLayer("linelayer", "line-source").withProperties(
                            PropertyFactory.lineDasharray(arrayOf(0.21f, 2f)),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineWidth(5f),
                            PropertyFactory.lineColor(Color.parseColor("#e55e5e"))))
                    enableLocationComponent(style)
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
                cameraMode = CameraMode.NONE

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

    fun changeToTravelBoard(view: View? = null) {
        val intent = Intent(context, TravelDash::class.java)
        intent.putExtra(Constants.TRIP_ID.type, tripId)
        startActivity(intent)
    }
}




