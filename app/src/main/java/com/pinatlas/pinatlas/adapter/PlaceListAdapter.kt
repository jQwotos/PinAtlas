package com.pinatlas.pinatlas.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pinatlas.pinatlas.DetailsView
import com.pinatlas.pinatlas.ItemMoveCallback
import com.pinatlas.pinatlas.R
import com.pinatlas.pinatlas.constants.Constants
import com.pinatlas.pinatlas.constants.ViewModes
import com.pinatlas.pinatlas.utils.PlaceThumbnailUtil
import com.pinatlas.pinatlas.viewmodel.CreationViewModel

// Whenever we create an PlaceListAdapter, we specify the mode, the view model, context
/* Owner: AZ */
class PlaceListAdapter (private val viewModel: CreationViewModel, private val mode: ViewModes, private val context: Context
) : RecyclerView.Adapter<PlaceListAdapter.ViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    private val places = viewModel.tripPlaces

    /*
    internal Android Logic (Cmd + Click; if asked to override, then it's created by Android)

    when Android creates it, it's trying to figure out which layout to use
    */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.item_creation_list_tile, viewGroup, false)
        return ViewHolder(view)
    }


    /* internal Android Logic
    value: returns actual object stored within LiveData Object
    ViewModes: constant (set to ITINERARY MODE)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place =  places.value!![position]

        /* in itinerary mode, we hide the delete button in itinerary
        *
        * We do this to prevent page duplication since the only difference (for now) between itinerary mode and creation mode
        * is that itinerary mode does not allow users to delete items (in our system)
        *  */
        PlaceThumbnailUtil.populateImageView(place.placeId, holder.thumbnail, context)
        holder.activity.text = place.name
        holder.address.text = place.address

        if (mode == ViewModes.ITINERARY_MODE) {
            holder.deleteButton.visibility = View.GONE
            if(!place.canvisit){
                holder.address.text = "Unable to visit this place!! Maybe Next time!!"
            }else{
                holder.address.text = place.starttime.toDate().toString()
            }


            // this allows us to zoom in to the place when we click on the "card" in itinerary view
            holder.itemView.setOnClickListener {
                viewModel.latLng.postValue(place.coordinates)
            }
        }

        // updates info in Firebase
        holder.deleteButton.setOnClickListener {
            viewModel.deletePlace(position)
            viewModel.saveTrip()
        }

        holder.infoButton.setOnClickListener {
            val intent = Intent(it.context, DetailsView::class.java)
            intent.putExtra(Constants.TRIP_ID.type, viewModel.trip.value!!.tripId)
            intent.putExtra(Constants.PLACE_ID.type, place.placeId)
            it.context.startActivity(intent)
        }
    }

    // internal Android Logic -- check if list has any items
    override fun getItemCount(): Int {
        return places.value?.size ?: 0
    }

    // public -> gets used throughout our code
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView) {
        val activity: TextView = itemView.findViewById(R.id.activityName) as TextView
        val address: TextView = itemView.findViewById(R.id.activityAddress) as TextView
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteSymbol) as ImageView
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail) as ImageView
        val infoButton: ImageView = itemView.findViewById(R.id.infoSymbol)
        val card: CardView = itemView as CardView
    }

    // when the user clicks on the card, it'll change the background colour to show what the user is selecting

    /* Owner: MV */
    // todo: change to XML colours (MONICA)
    override fun onRowClear(viewHolder: ViewHolder) {
        viewHolder.card.setCardBackgroundColor(Color.parseColor("#EA3F60"))
    }

    // when we move the row, we'll update our Firestore the new priority
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        viewModel.updatePlacePriority(fromPosition, toPosition)
        viewModel.saveTrip()
    }

    // change the background back to the original colour
    override fun onRowSelected(viewHolder: ViewHolder) {
        viewHolder.card.setCardBackgroundColor(Color.parseColor("#e87289"))
    }
}