package com.example.pinatlas.adapter

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.R
import com.example.pinatlas.model.Trip
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class TripAdapter (query: Query, private val mListener: OnTripSelectedListener): FirestoreAdapter<TripAdapter.ViewHolder>(query) {
    interface OnTripSelectedListener {
        fun onTripSelected(trip: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.traveldash_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }

    // Binds to the RecyclerView and converts the object into something useful
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.location)

        fun bind(snapshot: DocumentSnapshot, listener: OnTripSelectedListener) {
            val trip: Trip? = snapshot.toObject(Trip::class.java)
            trip!!.tripId = snapshot.id
            title.text = trip.name

            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View) {
                    listener.onTripSelected(snapshot)
                }
            })
        }
    }
}