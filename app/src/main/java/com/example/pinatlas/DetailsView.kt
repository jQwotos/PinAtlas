package com.example.pinatlas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.example.pinatlas.constants.Constants
import com.example.pinatlas.viewmodel.DetailsViewModel
import com.example.pinatlas.databinding.DetailsViewBinding
import com.example.pinatlas.utils.PlaceThumbnailUtil

class DetailsView : AppCompatActivity() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : DetailsViewBinding = DataBindingUtil.setContentView(this, R.layout.details_view)

        var tripId = intent.getStringExtra(Constants.TRIP_ID.type)!!
        var placeId = intent.getStringExtra(Constants.PLACE_ID.type)!!

        viewModel = DetailsViewModel(tripId, placeId)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        imageView = findViewById(R.id.placeImage)

        PlaceThumbnailUtil.populateImageView(placeId, imageView)
    }
}
