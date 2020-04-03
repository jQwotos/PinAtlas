package com.example.pinatlas

import android.app.AlertDialog
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
import com.example.pinatlas.constants.TransportationMethods
import com.example.pinatlas.constants.ViewModes
import com.example.pinatlas.databinding.CreationViewBinding
import com.example.pinatlas.model.Place
import com.example.pinatlas.utils.DateUtils
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
        /* Owner: AZ */
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
                        openingHours = if (gPlace.openingHours != null)
                            gPlace.openingHours?.weekdayText as ArrayList<String>
                            else null,
                        name = gPlace.name!!,
                        address = gPlace.address!!,
                        phoneNumber = gPlace.phoneNumber,
                        rating = gPlace.rating,
                        coordinates = GeoPoint(gPlace.latLng!!.latitude,gPlace.latLng!!.longitude)
                    )

                    viewModel.addPlace(place)
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

    /* Owner: MV */
    inner class OnCreateDateSetListener (private var datePicker: DatePicker)
        : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, day: Int) {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            this.datePicker.templateMethod(Timestamp(calendar.time))
        }
    }
    /*
    date sets the date to the button connected to the calendar and does the updating in trip (stored in Firebase)
     */
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

        fun setText(date: Timestamp) {
            button.setText(DateUtils.formatTimestamp(date))
        }

        fun templateMethod(date: Timestamp) {
            this.setDate(date)
            this.setText(date)
        }
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
    /* End of Owner: MV */

    fun deleteTrip(view: View) {
        viewModel.deleteTrip()
        finish()
    }

    fun buildTransportationPicker(view: View) {
        val options: Array<String> = TransportationMethods.values().map { it.type }.toTypedArray()
        val checked: BooleanArray = options.map { method -> viewModel.trip.value!!.transportationMethods.contains(method) }.toBooleanArray()

        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.transpo_picker_msg)
        builder.setMultiChoiceItems(options, checked) { _, which, isChecked ->
            checked[which] = isChecked
        }

        builder.setPositiveButton("Submit", null)

        builder.setNegativeButton("Cancel", null)

        var dialog = builder.create()

        dialog.setOnShowListener {
            var submitButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            submitButton.setOnClickListener { _ ->
                if (!checked.contains(true)) {
                    Toast.makeText(context, "Please select at least one method.", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.setTransportationMethods(
                        checked.foldIndexed(arrayListOf()) { index: Int, acc: ArrayList<String>, b: Boolean ->
                            if (b) acc.add(options[index])
                            acc
                        })
                    viewModel.saveTrip()
                    dialog.dismiss()
                } } }

        dialog.show()
    }

    fun changeToItineraryView(view: View? = null) {
        val intent = Intent(context, ItineraryView::class.java)
        intent.putExtra(Constants.TRIP_ID.type, tripId)
        startActivity(intent)
    }

    fun optimize(view: View) {
        if (viewModel.tripPlaces.value!!.size > 2) {
            loader.visibility = View.VISIBLE
            doAsync {
                MatrixifyUtil.optimizer(
                    viewModel.trip.value!!.places,
                    viewModel.trip.value!!.transportationMethods,
                    viewModel.trip.value!!.startDate,
                    viewModel.trip.value!!.endDate) { newOrderedPlaces: List<Place>? ->
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