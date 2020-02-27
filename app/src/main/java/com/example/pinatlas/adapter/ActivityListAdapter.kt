package com.example.pinatlas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.R
import com.example.pinatlas.model.Place

class ActivityListAdapter ( private val places: LiveData<List<Place>>
) : RecyclerView.Adapter<ActivityListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.item_creation_list_tile, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.activity.text = places.value!![position].name
        holder.address.text = places.value!![position].address
        holder.priority.text = "#${position+1}"
    }

    override fun getItemCount(): Int {
        return places.value?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView) {
        val activity: TextView = itemView.findViewById(R.id.activityName) as TextView
        val address: TextView = itemView.findViewById(R.id.activityAddress) as TextView
        val priority: TextView = itemView.findViewById(R.id.activityPriority) as TextView
    }
}