package com.example.pinatlas.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.*
import java.util.ArrayList


// FireStore Adapter is the base adapter to add and listen for updates from firestore
abstract class FirestoreAdapter<VH: RecyclerView.ViewHolder>(private var mQuery: Query?): RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {
    private var mRegistration: ListenerRegistration? = null

    private val mSnapshots = ArrayList<DocumentSnapshot>()

    override fun getItemCount() = mSnapshots.size

    fun startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }

        mSnapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query) {
        stopListening()

        mSnapshots.clear()
        notifyDataSetChanged()

        mQuery = query
        startListening()
    }

    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshots.get(index)
    }

    protected fun onError(e: FirebaseFirestoreException) {}

    protected fun onDataChanged() {}

    private fun onDocumentAdded(change: DocumentChange) {
        mSnapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            mSnapshots.set(change.oldIndex, change.document)
            notifyItemChanged(change.oldIndex)
        } else {
            mSnapshots.removeAt(change.oldIndex)
            mSnapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        mSnapshots.removeAt(change.oldIndex)
        notifyItemChanged(change.oldIndex)
    }

    override fun onEvent(documentSnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e)
            return
        }

        for (change in documentSnapshot!!.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
            }
        }

        onDataChanged()
    }

    companion object {
        private val TAG = "Firestore Adapter"
    }
}