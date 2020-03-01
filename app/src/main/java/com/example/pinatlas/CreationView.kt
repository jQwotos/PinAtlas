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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.constants.ViewModes
import com.example.pinatlas.databinding.CreationViewBinding
import com.example.pinatlas.model.Place
import com.example.pinatlas.viewmodel.CreationViewModel
import com.example.pinatlas.viewmodel.CreationViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place as GPlace
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView

class CreationView : AppCompatActivity() {
    private val TAG = CreationView::class.java.simpleName
    private val context: Context = this

    private lateinit var viewModel: CreationViewModel
    private lateinit var picker: DatePickerDialog
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button
    private lateinit var tripNameText: EditText
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button

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

        val adapter = ActivityListAdapter(viewModel, ViewModes.EDIT_MODE)
        val activityList: MultiSnapRecyclerView = findViewById(R.id.activityList)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val touchCallback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(touchCallback)

        touchHelper.attachToRecyclerView(activityList)

        activityList.adapter = adapter
        activityList.layoutManager = manager

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.searchBar) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(PLACE_FIELDS)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                val msg = "An error occurred: $status"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Log.e(TAG, msg)
            }

            override fun onPlaceSelected(gPlace: GPlace) {
                if (gPlace.id != null) {
                    val place = Place(gPlace.id!!, gPlace.name!!,
                        gPlace.address!!, null, gPlace.phoneNumber, gPlace.rating, null,
                        null, null, null, null)

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

        submitButton = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            val intent = Intent(this, ItineraryView::class.java)
            intent.putExtra(Constants.TRIP_ID.type, tripId)
            startActivity(intent)
        }

        deleteButton = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            viewModel.deleteTrip()
            finish()
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
}