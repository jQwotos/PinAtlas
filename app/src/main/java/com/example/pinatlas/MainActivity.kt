package com.example.pinatlas

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.adapter.PlaceAdapter
import com.example.pinatlas.model.Place
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), PlaceAdapter.OnPlaceSelectedListener {
    private var TAG = MainActivity::class.java.simpleName

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val mQuery: Query by lazy { mFirestore.collection("places") }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            context = this;
            recyclerView = findViewById(R.id.itineraryRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

            adapter = PlaceAdapter(mQuery, this)

            recyclerView.adapter = adapter

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override fun onPlaceSelected(place: DocumentSnapshot) {
        var snapshot = place.toObject(Place::class.java)
        Toast.makeText(context, "Clicked on " + snapshot!!.name, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        if (adapter != null) {
            adapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter.stopListening()
        }
    }
}