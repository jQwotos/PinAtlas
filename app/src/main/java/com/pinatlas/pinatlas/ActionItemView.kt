package com.pinatlas.pinatlas

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pinatlas.pinatlas.databinding.ActionItemViewBinding

class ActionItemView(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    private var textView: TextView
    private var imageView: ImageView

    init {
        ActionItemViewBinding.inflate(LayoutInflater.from(context), this, true)

        imageView = findViewById(R.id.actionBtnImage)
        textView = findViewById(R.id.actionBtnTxt)

        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionItemView)

        imageView.setImageDrawable(attributes.getDrawable(R.styleable.ActionItemView_actionImg))
        textView.text = attributes.getText(R.styleable.ActionItemView_actionText)
        attributes.recycle()
    }
}