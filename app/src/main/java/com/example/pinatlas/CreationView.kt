package com.example.pinatlas

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.libraries.places.api.Places as GPlaces
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import android.util.Log
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Place
import com.example.pinatlas.viewmodel.CreationViewModel
import com.example.pinatlas.viewmodel.CreationViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place as GPlace
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView


class CreationView : AppCompatActivity() {
    private var TAG = CreationView::class.java.simpleName

    private lateinit var context: Context
    private lateinit var viewModel: CreationViewModel

    private lateinit var picker: DatePickerDialog
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button
    private lateinit var tripName: EditText
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var tripId: String
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creation_view)
        context = this
        tripId = intent.getStringExtra(Constants.TRIP_ID.type)!!

        Log.d(TAG, "tripId: $tripId, uid: ${currentUser!!.uid}")
        val factory = CreationViewModelFactory(tripId, currentUser!!.uid)
        viewModel = ViewModelProviders.of(this, factory)
            .get(CreationViewModel::class.java)

        startDateButton = findViewById(R.id.editStartDate)
        endDateButton = findViewById(R.id.endDateButton)

        tripName = findViewById(R.id.tripName)
        tripName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(update: Editable?) {
                viewModel.setName(update.toString())
                updateData()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val adapter = ActivityListAdapter(viewModel.tripPlaces)

        viewModel.tripPlaces.observe(this, Observer { update ->
            if (update != null) {
               adapter.notifyDataSetChanged()
            }
        })

        GPlaces.initialize(applicationContext, BuildConfig.PLACES_API_KEY)

        val activityList: MultiSnapRecyclerView = findViewById(R.id.activityList)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        activityList.adapter = adapter
        activityList.layoutManager = manager

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.searchBar) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                GPlace.Field.ID,
                GPlace.Field.NAME,
                GPlace.Field.ADDRESS,
                GPlace.Field.PHONE_NUMBER,
                GPlace.Field.RATING,
                GPlace.Field.TYPES,
                GPlace.Field.OPENING_HOURS,
                GPlace.Field.LAT_LNG)
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred: $status")
            }

            override fun onPlaceSelected(place: GPlace) {
                if (place.id != null) {
                    val newPlace = Place(place.id!!, place.name!!,
                        place.address!!, null, place.phoneNumber, place.rating, null,
                        null, null, null, null)
                    viewModel.addPlace(newPlace)
                    updateData()
                }
            }
        })

    }

    inner class OnCreateDateSetListener: DatePickerDialog.OnDateSetListener {
        // Create new variable in the OnCreateDateSetListener to hold the button
        private var datePicker: DatePicker

        // Create a constructor that takes the button and sets the classes button
        constructor(datePicker: DatePicker) {
            this.datePicker = datePicker
        }

        override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, day: Int) {
            // Now use the stored button instead
            this.datePicker.button.setText(day.toString() + "/" + (month + 1).toString() + "/" + year.toString());
            this.datePicker.setDate(Timestamp(Date(year, month, day)))
        }
    }

    inner class StartDatePicker : DatePicker() {
        override var button: Button = startDateButton
        override fun setDate(date: Timestamp) {
             viewModel.setStartDate(date)
            updateData()
        }
    }

    inner class EndDatePicker : DatePicker() {
        override var button: Button = endDateButton
        override fun setDate(date: Timestamp) {
            viewModel.setEndDate(date)
            updateData()
        }
    }

    abstract class DatePicker {
        abstract var button: Button
        abstract fun setDate(date: Timestamp)
    }

    // Switch createDatePicker to accept a button
    fun createDatePicker(datePickerParam: DatePicker) {
        val c = Calendar.getInstance()
        picker = DatePickerDialog(context, OnCreateDateSetListener(datePickerParam), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        picker.show()
    }

    // Change the components onClick to createStartDatePicker or EndDatePicker
    fun createStartDatePicker(view : View) {
        createDatePicker(StartDatePicker())
    }

    fun createEndDatePicker(view : View) {
        createDatePicker(EndDatePicker())
    }

    fun updateData() {
        viewModel.saveTrip()
    }
}