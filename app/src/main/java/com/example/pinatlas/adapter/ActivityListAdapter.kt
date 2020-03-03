package com.example.pinatlas.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pinatlas.ItemMoveCallback
import com.example.pinatlas.R
import com.example.pinatlas.constants.ViewModes
import com.example.pinatlas.utils.PlaceThumbnailUtil
import com.example.pinatlas.viewmodel.CreationViewModel

class ActivityListAdapter ( private val viewModel: CreationViewModel, private val mode: ViewModes, private val context: Context
) : RecyclerView.Adapter<ActivityListAdapter.ViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    private val places = viewModel.tripPlaces

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.item_creation_list_tile, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place =  places.value!![position]

        if (mode == ViewModes.ITINERARY_MODE) {
            holder.deleteButton.visibility = View.GONE

            holder.itemView.setOnClickListener {
                viewModel.latLng.postValue(place.coordinates)
            }
        }

        PlaceThumbnailUtil(context).populateImageView(place.placeId, holder.thumbnail)
        holder.activity.text = place.name
        holder.address.text = place.address
        holder.priority.text = "#${position+1}"
        holder.deleteButton.setOnClickListener {
            viewModel.deletePlace(position)
            viewModel.saveTrip()
        }
    }

    override fun getItemCount(): Int {
        return places.value?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView) {
        val activity: TextView = itemView.findViewById(R.id.activityName) as TextView
        val address: TextView = itemView.findViewById(R.id.activityAddress) as TextView
        val priority: TextView = itemView.findViewById(R.id.activityPriority) as TextView
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteSymbol) as ImageView
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail) as ImageView
        val card: CardView = itemView as CardView
    }

    override fun onRowClear(viewHolder: ViewHolder) {
        viewHolder.card.setCardBackgroundColor(Color.parseColor("#EA3F60"))
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        viewModel.updatePlacePriority(fromPosition, toPosition)
        viewModel.saveTrip()
    }

    override fun onRowSelected(viewHolder: ViewHolder) {
        viewHolder.card.setCardBackgroundColor(Color.parseColor("#e87289"))
    }
}