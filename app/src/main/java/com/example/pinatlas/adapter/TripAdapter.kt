package com.example.pinatlas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.R
import com.example.pinatlas.model.Trip
import com.example.pinatlas.utils.DateUtils

class TripAdapter (private val pastTrips: LiveData<List<Trip>>, onTripSelectedListener: OnTripSelectedListener): RecyclerView.Adapter<TripAdapter.ViewHolder>() {
    private var listener: OnTripSelectedListener = onTripSelectedListener

    interface OnTripSelectedListener {
        fun onTripSelected(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.traveldash_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pastTrips.value!![position], listener)
    }

    override fun getItemCount(): Int = pastTrips.value?.size ?: 0

    // Binds to the RecyclerView and converts the object into something useful
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.location)
        var date: TextView = itemView.findViewById(R.id.dates)

        fun bind(trip: Trip, listener: OnTripSelectedListener) {
            title.setText(trip.name)
            date.setText(DateUtils.formatTripDate(trip))

            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View) {
                    listener.onTripSelected(trip)
                }
            })
        }
    }
}