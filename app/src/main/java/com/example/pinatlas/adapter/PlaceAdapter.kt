package com.example.pinatlas.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.R
import com.example.pinatlas.model.Place
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class PlaceAdapter(query: Query, private val mListener: OnPlaceSelectedListener): FirestoreAdapter<PlaceAdapter.ViewHolder>(query) {

    interface OnPlaceSelectedListener {
        fun onPlaceSelected(place: DocumentSnapshot)
    }

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.adapter_recyclerview, parent, false))
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        var txtDate: TextView = itemView.findViewById(R.id.txt_date)

        fun bind(snapshot: DocumentSnapshot, listener: OnPlaceSelectedListener) {
            val place = snapshot.toObject(Place::class.java)
            Log.w("PlaceAdapter", "Adding item" + place!!.name )
            txtTitle.setText(place.name)
            txtDate.setText(place.place_id)

            itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View) {
                    listener.onPlaceSelected(snapshot)
                }
            })
        }
    }

}