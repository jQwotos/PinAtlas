/*
Sources:
https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback
 */

package com.pinatlas.pinatlas

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pinatlas.pinatlas.adapter.PlaceListAdapter

/*
Allows us to control touch behaviors in the ViewHolder & receive callbacks when user performs these actions
 */
class ItemMoveCallback : ItemTouchHelper.Callback {
    private var mAdapter: ItemTouchHelperContract

    constructor(adapter: ItemTouchHelperContract) {
        mAdapter = adapter
    }

    // allows long press
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    // don't allow swipe
    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    // we can control which actions user can take on each view by overriding this function
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    // need to override this method to allow any changes during dragging
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /*
    onSelectedChanged gets called when a "row" in ViewHolder is dragged by the ItemTouchHelper is changed.
    This calls "onRowSelected" (implemented by the user) & determines what happens after

    Source: https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback#onSelectedChanged(android.support.v7.widget.RecyclerView.ViewHolder,%20int)
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is PlaceListAdapter.ViewHolder) {
                var myViewHolder: PlaceListAdapter.ViewHolder = viewHolder
                mAdapter.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    /*
    clearView Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation.
    This calls "onRowClear" (implemented by the user) & determines what happens when the user completes their action
     */

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        if (viewHolder is PlaceListAdapter.ViewHolder) {
            var myViewHolder: PlaceListAdapter.ViewHolder = viewHolder
            mAdapter.onRowClear(myViewHolder)
        }
    }

    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition : Int, toPosition : Int)
        fun onRowSelected(viewHolder : PlaceListAdapter.ViewHolder)
        fun onRowClear(viewHolder : PlaceListAdapter.ViewHolder)
    }
}