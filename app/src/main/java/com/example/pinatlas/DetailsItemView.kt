package com.example.pinatlas

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import com.example.pinatlas.databinding.DetailsItemViewBinding

class DetailsItemView(context: Context, attributeSet: AttributeSet): TableRow(context, attributeSet) {

    private var textView: TextView

    init {
        DetailsItemViewBinding.inflate(LayoutInflater.from(context), this, true)

        val imageView: ImageView = findViewById(R.id.detailsImage)
        textView = findViewById(R.id.detailsTextView)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.DetailsItemView)

        imageView.setImageDrawable(attributes.getDrawable(R.styleable.DetailsItemView_imageSrc))
        attributes.recycle()
    }

    /* Data Binding, do not remove */
    fun getText() : String {
        return textView.text.toString()
    }

    fun setText(text: String?) {
        textView.text = text
    }
}
