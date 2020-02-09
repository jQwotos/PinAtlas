package com.example.pinatlas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pinatlas.R

class Submitted_List_Adapter ( private val titles: Array<String>
) : androidx.recyclerview.widget.RecyclerView.Adapter<Submitted_List_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.item_submit_list_tile, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = titles[position]
        val date = "Hello world"
        holder.location.text = location
        holder.date.text = date
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        itemView) {
        val location: TextView = itemView.findViewById(R.id.location) as TextView
        val date: TextView = itemView.findViewById(R.id.date) as TextView
    }
}