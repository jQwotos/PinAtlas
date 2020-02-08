package com.example.pinatlas

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.adapter.TripAdapter
import com.example.pinatlas.model.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


/*

    TODO: DELETE ME ONCE ACTUAL TRAVELBOARD IS IN PLACE
    ExampleActivity purely used to demo how to use the TripAdapter to fetch the users's trips

 */
class TravelBoard : AppCompatActivity(), TripAdapter.OnTripSelectedListener {
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val tripCollection: CollectionReference by lazy { mFirestore.collection("trips") }

    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    private lateinit var tripsQuery: Query

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputBox: EditText
    private lateinit var adapter: TripAdapter
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_board)

        // Bind everything
        context = this;
        recyclerView = findViewById(R.id.travel_board)
        inputBox = findViewById(R.id.createTripInput)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // Create the tripsQuery after user initialized
        tripsQuery = tripCollection.whereEqualTo("user_id", currentUser!!.uid)

        // Create adapter for trips
        adapter = TripAdapter(tripsQuery, this)

        // Bind adapter to recyclver view
        recyclerView.adapter = adapter
    }

    override fun onTripSelected(trip: DocumentSnapshot) {
        var snapshot = trip.toObject(Trip::class.java)
        Toast.makeText(context, "Clicked on " + snapshot!!.name, Toast.LENGTH_SHORT).show()
    }

    // When we click on the submit button
    fun onCreateNewTrip(view: View) {
        // Get the tripName from the input box
        var tripName = inputBox.text.toString()

        // Create a new Trip obj
        var trip = Trip(name = tripName, user_id = currentUser!!.uid)

        // Add to firestore
        tripCollection.add(trip)
    }

    // Starts listening to changes from firestore
    override fun onStart() {
        super.onStart()

        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (::adapter.isInitialized) {
            adapter.stopListening()
        }
    }
}
