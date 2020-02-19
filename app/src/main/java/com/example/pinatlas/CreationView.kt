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
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Trip
import com.google.android.libraries.places.api.Places as GPlaces
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import android.util.Log
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.model.Place
import com.example.pinatlas.viewmodel.FirestoreViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place as GPlace
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import java.lang.Error
import kotlin.collections.ArrayList


class CreationView : AppCompatActivity() {
    private var TAG = CreationView::class.java.simpleName
    private lateinit var context: Context
    private lateinit var picker: DatePickerDialog
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button
    private lateinit var tripName: EditText
    private lateinit var firestoreViewModel: FirestoreViewModel

    private lateinit var tripId: String
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }
    private var trip: Trip = Trip(userId = currentUser!!.uid)
    private var places: ArrayList<Place> = arrayListOf()
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var tripDocument: LiveData<Trip>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_creation_view)
        firestoreViewModel = ViewModelProviders.of(this)
            .get(FirestoreViewModel::class.java)

        tripId = currentUser!!.uid
        val newTrip = Trip(tripId)
        firestoreViewModel.saveTrip(newTrip)

        context = this

        startDateButton = findViewById(R.id.editStartDate)

        // Set the endDateButton to the component
        endDateButton = findViewById(R.id.endDateButton)
        tripName = findViewById(R.id.tripName)
        tripName.setText(firestoreViewModel.newTrip.value?.name)

        GPlaces.initialize(applicationContext, BuildConfig.PLACES_API_KEY)

        val activityList: MultiSnapRecyclerView = findViewById(R.id.activityList)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ActivityListAdapter(places)

        activityList.adapter = adapter
        activityList.layoutManager = manager

        firestoreViewModel.fetchPlacesInTrip(tripId).observe(this, Observer { update ->
            Log.d(TAG, update.toString())
            if (update != null) {
                places.removeAll(places)
                places.addAll(update)
                activityList.adapter?.notifyDataSetChanged()
            }
        })

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
                    trip.places.add(place.id)
                    Log.d(TAG, place.id)
                    firestoreViewModel.saveTrip(trip)
                    adapter.notifyItemChanged(trip.places.size)
                }
            }

        })

    }

    inner class onCreateDateSetListener: DatePickerDialog.OnDateSetListener {
        // Create new variable in the onCreateDateSetListener to hold the button
        private var datePicker: datePicker

        // Create a constructor that takes the button and sets the classes button
        constructor(datePicker: datePicker) {
            this.datePicker = datePicker
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            // Now use the stored button instead
            this.datePicker.button.setText( day.toString() + "/" + (month + 1).toString() + "/" + year.toString());
            this.datePicker.setDate(Timestamp(Date(year, month, day)))
        }
    }

    inner class startDatePicker: datePicker {
        override var button: Button = startDateButton
        override fun setDate(date: Timestamp) {
            trip.startDate = date
            updateData()
        }

        constructor()
    }

    inner class endDatePicker: datePicker {
        override var button: Button = endDateButton
        override fun setDate(date: Timestamp) {
            trip.startDate = date
            updateData()
        }

        constructor()
    }

    abstract class datePicker {
        abstract var button: Button
        abstract fun setDate(date: Timestamp)
    }

    // Switch createDatePicker to accept a button
    fun createDatePicker(datePickerParam: datePicker) {
        val c = Calendar.getInstance()
        picker = DatePickerDialog(context, onCreateDateSetListener(datePickerParam), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        picker.show()
    }

    // Change the components onClick to createStartDatePicker or endDatePicker
    fun createStartDatePicker(view : View) {
        createDatePicker(startDatePicker())
    }

    fun createEndDatePicker(view : View) {
        createDatePicker(endDatePicker())
    }

    fun updateData() {
        firestoreViewModel.saveTrip(trip)
    }
}