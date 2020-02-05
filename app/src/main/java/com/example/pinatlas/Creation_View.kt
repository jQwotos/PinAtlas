package com.example.pinatlas

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import java.time.Month

class Creation_View : AppCompatActivity() {
    private lateinit var context: Context
    private lateinit var picker: DatePickerDialog
    private lateinit var eText : EditText
    private lateinit var startDateButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this;
        setContentView(R.layout.activity_creation__view)
        startDateButton = findViewById(R.id.editStartDate)
    }

    inner class onCreateDateSetListener: DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            startDateButton.setText( day.toString() + "/" + (month + 1).toString() + "/" + year.toString());
        }
    }

    fun createDatePicker(view: View) {
        picker = DatePickerDialog(context, onCreateDateSetListener(), 2020, 3, 4)
        picker.show()
    }
}

