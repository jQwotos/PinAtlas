package com.example.pinatlas

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pinatlas.databinding.DetailsItemViewBinding

class ActionItemView(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    private var textView: TextView
    private var imageView: ImageView

    init {
//        ActionItemViewBinding.inflate(LayoutInflater.from(context), this, true)

        imageView = findViewById(R.id.actionBtnImage)
        textView = findViewById(R.id.actionBtnTxt)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionItemView)

        imageView.setImageDrawable(attributes.getDrawable(R.styleable.ActionItemView_actionImg))
        attributes.recycle()
    }

    /* Data Binding, do not remove */
    fun getActionText() : String {
        return textView.text.toString()
    }

    fun setActionText(text: String?) {
        textView.text = text
    }
}