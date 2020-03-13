package com.example.pinatlas

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import android.text.Editable
import android.text.TextWatcher
import com.google.android.libraries.places.api.Places as GPlaces
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.PlaceListAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.constants.ViewModes
import com.example.pinatlas.databinding.CreationViewBinding
import com.example.pinatlas.model.Place
import com.example.pinatlas.utils.MatrixifyUtil
import com.example.pinatlas.viewmodel.CreationViewModel
import com.example.pinatlas.viewmodel.CreationViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.firebase.firestore.GeoPoint
import com.google.android.libraries.places.api.model.Place as GPlace
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import org.jetbrains.anko.doAsync

class CreationView : AppCompatActivity() {
    private val TAG = CreationView::class.java.simpleName
    private val context: Context = this

    private lateinit var viewModel: CreationViewModel
    private lateinit var picker: DatePickerDialog
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button
    private lateinit var tripNameText: EditText
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var deleteButton: Button
    private lateinit var loader: ConstraintLayout

    private lateinit var tripId: String
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }
    private val PLACE_FIELDS = listOf(
        GPlace.Field.ID,
        GPlace.Field.NAME,
        GPlace.Field.ADDRESS,
        GPlace.Field.PHONE_NUMBER,
        GPlace.Field.RATING,
        GPlace.Field.TYPES,
        GPlace.Field.OPENING_HOURS,
        GPlace.Field.LAT_LNG,
        GPlace.Field.PHOTO_METADATAS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : CreationViewBinding = DataBindingUtil.setContentView(this, R.layout.creation_view)
        GPlaces.initialize(applicationContext, BuildConfig.PLACES_API_KEY)

        tripId = intent.getStringExtra(Constants.TRIP_ID.type)!!

        val factory = CreationViewModelFactory(tripId, currentUser!!.uid)
        viewModel = ViewModelProviders.of(this, factory).get(CreationViewModel::class.java)

        // Bind to viewModel
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        startDateButton = findViewById(R.id.editStartDate)
        endDateButton = findViewById(R.id.endDateButton)

        tripNameText = findViewById(R.id.tripName)
        tripNameText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(update: Editable?) {
                viewModel.setName(update.toString())
                viewModel.saveTrip()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val adapter = PlaceListAdapter(viewModel, ViewModes.EDIT_MODE, this)
        val placeList: MultiSnapRecyclerView = findViewById(R.id.placeList)
        loader = findViewById(R.id.loader)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val touchCallback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(touchCallback)

        touchHelper.attachToRecyclerView(placeList)

        placeList.adapter = adapter
        placeList.layoutManager = manager

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.searchBar) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(PLACE_FIELDS)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                if (!status.isCanceled) {
                    val msg = "An error occurred: $status"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                }
            }

            override fun onPlaceSelected(gPlace: GPlace) {
                if (gPlace.id != null) {
                    val place = Place(
                        placeId = gPlace.id!!,
                        name = gPlace.name!!,
                        address = gPlace.address!!,
                        phoneNumber = gPlace.phoneNumber,
                        rating = gPlace.rating,
                        coordinates = GeoPoint(gPlace.latLng!!.latitude,gPlace.latLng!!.longitude)
                    )

                    viewModel.addPlace(place).addOnSuccessListener {
                        viewModel.saveTrip()
                    }
                }
            }
        })

        // update view when tripPlaces changes
        viewModel.tripPlaces.observe(this, Observer { update ->
            if (update != null) {
                adapter.notifyDataSetChanged()
            }
        })

        deleteButton = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            viewModel.deleteTrip()
            // send to travel dash while clearing activity stack
            intent = Intent(this, TravelDash::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    inner class OnCreateDateSetListener (private var datePicker: DatePicker)
        : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, day: Int) {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            this.datePicker.setDate(Timestamp(calendar.time))
        }
    }

    inner class StartDatePicker : DatePicker() {
        override var button: Button = startDateButton
        override fun setDate(date: Timestamp) {
            viewModel.setStartDate(date)
            viewModel.saveTrip()
        }
    }

    inner class EndDatePicker : DatePicker() {
        override var button: Button = endDateButton
        override fun setDate(date: Timestamp) {
            viewModel.setEndDate(date)
            viewModel.saveTrip()
        }
    }

    abstract class DatePicker {
        abstract var button: Button
        abstract fun setDate(date: Timestamp)
    }

    // Switch createDatePicker to accept a button
    private fun createDatePicker(datePickerParam: DatePicker) {
        val c = Calendar.getInstance()
        picker = DatePickerDialog(context, OnCreateDateSetListener(datePickerParam),
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        picker.show()
    }

    // Change the components onClick to createStartDatePicker or EndDatePicker
    fun createStartDatePicker(view : View) {
        createDatePicker(StartDatePicker())
    }

    fun createEndDatePicker(view : View) {
        createDatePicker(EndDatePicker())
    }

    fun deleteTrip(view: View) {
        viewModel.deleteTrip()
        finish()
    }

    fun changeToItineraryView(view: View? = null) {
        val intent = Intent(context, ItineraryView::class.java)
        intent.putExtra(Constants.TRIP_ID.type, tripId)
        startActivity(intent)
    }
    /*
        Shubham Sharan
        * Facade design pattern used.
        * Client is the UI: creation_view.xml : It contains the submit button. which when clicked launches the genetic algorithm
        * Facade class : CreationView : This where they optimize method is called which initiates the matrixifyUtil's optimize algorithmn ->
        * My complex classes are : Salesman, SalesmanGenome, MatrixifyUtil, Distance Matrix Provider
            * SalesmanGenome : A candidate optimal solution. This class to stores the random generation, fitness function, the fitness itself, etc.
            * Salesman : This class will improve our model, and functions contained within it allow it to enhance the model to provide a viable solution
            * MatrixifyUtil : Calls the Saleman class to return the optimized solution
            * DistanceMatrixProvider : Fetches distance matrix from google api we use the distance matrix to calculate the time it takes to get between each point.
        * Helper Classes: All the classes inside the model.matrix : Duration, Element, Row
            * Building of the data structure utilised in the complex classes
            * Not specifically part of Facade Design Pattern
        * */

    fun optimize(view: View) {
        if (viewModel.tripPlaces.value!!.size > 2) {
            loader.visibility = View.VISIBLE
            doAsync {
                MatrixifyUtil.optimize(viewModel.trip.value!!.places) { newOrderedPlaces ->
                    if (newOrderedPlaces != null) {
                        viewModel.reorderPlaces(newOrderedPlaces)
                        viewModel.saveTrip()
                    }
                    changeToItineraryView()
                }
            }
        } else {
            Toast.makeText(context, "You must add more than 2 locations to provide an optimal route", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.tripListener.remove()
    }
}