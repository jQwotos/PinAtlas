package com.example.pinatlas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.viewmodel.DetailsViewModel
import com.example.pinatlas.databinding.DetailsViewBinding
import com.example.pinatlas.utils.PlaceThumbnailUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData

class DetailsView : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var imageView: ImageView
    private lateinit var busyTimesChart: BarChart
    private lateinit var busyTimesSpinner: Spinner
    private var daysOfWeek = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : DetailsViewBinding = DataBindingUtil.setContentView(this, R.layout.details_view)

        var tripId = intent.getStringExtra(Constants.TRIP_ID.type)!!
        var placeId = intent.getStringExtra(Constants.PLACE_ID.type)!!

        viewModel = DetailsViewModel(tripId, placeId)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        /* Setup image */
        imageView = findViewById(R.id.placeImage)
        PlaceThumbnailUtil.populateImageView(placeId, imageView, this)

        /* Setup Busy Times Spinner */
        busyTimesSpinner = findViewById(R.id.busyTimesSelector)
        var busyTimesSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, daysOfWeek)
        busyTimesSpinner.adapter = busyTimesSpinnerAdapter

        /* Setup busy times chart */
        busyTimesChart = findViewById(R.id.busyTimesChart)
        var description = Description()
        description.text = ""
        busyTimesChart.description = description

        busyTimesChart.axisLeft.setDrawLabels(false)
        busyTimesChart.axisRight.setDrawLabels(false)

        busyTimesChart.axisLeft.setDrawGridLines(false)
        busyTimesChart.axisRight.setDrawGridLines(false)
        busyTimesChart.xAxis.setDrawGridLines(false)

        busyTimesChart.legend.isEnabled = false

        viewModel.observeBusyData(this, Observer { data: BarData ->
            if (data.entryCount == 0) {
                busyTimesChart.visibility = View.GONE
            } else {
                busyTimesChart.visibility = View.VISIBLE
            }
            busyTimesChart.data = data
            busyTimesChart.invalidate()
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setBusyDay(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
