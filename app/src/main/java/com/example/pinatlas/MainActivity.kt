package com.example.pinatlas

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    internal var TAG = MainActivity::class.java.simpleName

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            context = this;
            recyclerView = findViewById<RecyclerView>(R.id.itineraryRecyclerView) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

            val models = ArrayList<DataModel>()
            models.add(DataModel("Item Title 1", "01 Jan, 2018"))
            models.add(DataModel("Item Title 2", "02 Jan, 2018"))
            models.add(DataModel("Item Title 3", "03 Jan, 2018"))
            models.add(DataModel("Item Title 4", "04 Jan, 2018"))
            models.add(DataModel("Item Title 5", "05 Jan, 2018"))
            models.add(DataModel("Item Title 6", "06 Jan, 2018"))
            models.add(DataModel("Item Title 7", "07 Jan, 2018"))
            models.add(DataModel("Item Title 8", "08 Jan, 2018"))
            models.add(DataModel("Item Title 9", "09 Jan, 2018"))
            models.add(DataModel("Item Title 10", "10 Jan, 2018"))
            models.add(DataModel("Item Title 11", "11 Jan, 2018"))
            models.add(DataModel("Item Title 12", "12 Jan, 2018"))
            models.add(DataModel("Item Title 13", "13 Jan, 2018"))
            models.add(DataModel("Item Title 14", "14 Jan, 2018"))
            models.add(DataModel("Item Title 15", "15 Jan, 2018"))

            adapter = ItemAdapter(models, context)
            recyclerView.adapter = adapter
            recyclerView.addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    Toast.makeText(context, "clicked on " + models.get(position).title, Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }
}

class DataModel(var title: String, var date: String){

}

class ItemAdapter(val models: ArrayList<DataModel>, val context: Context) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recyclerview, parent, false)
        return ItemViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ItemViewHolder, position: Int) {
        holder.bindItems(models[position])
    }

    override fun getItemCount(): Int {
        return models.size
    }

    class ItemViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(model: DataModel) {
            val txtTitle = itemView.findViewById<TextView>(R.id.txt_title) as TextView
            val txtDate = itemView.findViewById<TextView>(R.id.txt_date) as TextView

            txtTitle.text = model.title
            txtDate.text = model.date
            txtTitle.setOnClickListener {
                Toast.makeText(context,"clicked on "+txtTitle.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}