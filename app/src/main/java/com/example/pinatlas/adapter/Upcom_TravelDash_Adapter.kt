package com.example.pinatlas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pinatlas.R

class Upcom_TravelDash_Adapter (
    private val titles: Array<String>
) : androidx.recyclerview.widget.RecyclerView.Adapter<Upcom_TravelDash_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.traveldash_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = titles[position]

        holder.title.text = title
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        itemView) {
        val title: TextView = itemView.findViewById(R.id.location) as TextView
    }
}