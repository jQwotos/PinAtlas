package com.example.pinatlas

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.model.Trip
import com.google.android.libraries.places.api.Places
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinatlas.adapter.ActivityListAdapter
import com.example.pinatlas.model.matrix.DistanceMatrixModel
import com.example.pinatlas.utils.DistanceMatrixProvider
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView


class CreationView : AppCompatActivity() {
    private var TAG = CreationView::class.java.simpleName
    private lateinit var context: Context
    private lateinit var picker: DatePickerDialog
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button
    private lateinit var tripName: EditText

    private lateinit var tripID: String
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }
    private var trip: Trip = Trip(user_id = currentUser!!.uid)
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var tripDocument: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_creation_view)

        tripID = intent.getStringExtra(Constants.TRIP_ID.type)
        trip.trip_id = tripID
        tripDocument = mFirestore.collection("trips").document(tripID)

        context = this

        startDateButton = findViewById(R.id.editStartDate)

        // Set the endDateButton to the component
        endDateButton = findViewById(R.id.endDateButton)
        tripName = findViewById(R.id.tripName)
        tripName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                trip.name = s.toString()
                updateData()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.searchBar) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.TYPES,
                Place.Field.OPENING_HOURS,
                Place.Field.LAT_LNG)
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred: $status")
            }

            override fun onPlaceSelected(place: Place) {
                trip.places.add(place)
                updateData()
            }

        })

        val activityList: MultiSnapRecyclerView = findViewById(R.id.activityList)
        val placeAdapter = ActivityListAdapter(trip.places)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        activityList.adapter = placeAdapter
        activityList.layoutManager = manager

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
            trip.start_date = date
            updateData()
        }

        constructor()
    }

    inner class endDatePicker: datePicker {
        override var button: Button = endDateButton
        override fun setDate(date: Timestamp) {
            trip.end_date = date
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
        tripDocument.set(trip)
    }

    fun finishFetchingDistanceMatrix(distanceMatrixModel: DistanceMatrixModel) {
        Log.d(TAG, distanceMatrixModel.rows!!.size.toString())
        Log.d(TAG, distanceMatrixModel.status)
    }

    fun submit(view: View) {
        // TODO: Use the new get trip Places function
        var HARD_CODED_PLACES_REMOVE: ArrayList<String> = arrayListOf("Parliament Hill", "3 Brothers Rideau", "Carleton University", "The Caf Carleton", "1375 Prince of Wales")
        DistanceMatrixProvider.fetchDistanceMatrix(HARD_CODED_PLACES_REMOVE) {
            result: DistanceMatrixModel ->
                finishFetchingDistanceMatrix(result) // After we fetched invoke function
        }
        // TODO: SHUBHAM LOOK HERE
    }
}