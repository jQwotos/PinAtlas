package com.example.pinatlas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.R
import com.example.pinatlas.model.Trip
import com.example.pinatlas.utils.DateUtils
import com.example.pinatlas.utils.PlaceThumbnailUtil

/* Owner: AZ */
class TripAdapter (private val pastTrips: LiveData<List<Trip>>, context: Context,
                   onTripSelectedListener: OnTripSelectedListener): RecyclerView.Adapter<TripAdapter.ViewHolder>() {

    // event listener for when a trip gets tapped
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
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.location)
        val date: TextView = itemView.findViewById(R.id.dates)
        val thumbnail: ImageView = itemView.findViewById(R.id.locationThumbnail)

        fun bind(trip: Trip, listener: OnTripSelectedListener) {
            title.text = trip.name
            date.text = DateUtils.formatTripDate(trip)
            if (trip.places.size > 0) {
                PlaceThumbnailUtil.populateImageView(trip.places[0]!!.placeId, thumbnail)
            }

            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View) {
                    listener.onTripSelected(trip)
                }
            })
        }
    }
}