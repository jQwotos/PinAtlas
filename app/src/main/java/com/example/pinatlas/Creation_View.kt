package com.example.pinatlas

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
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
    private lateinit var startDateButton : Button
    private lateinit var endDateButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_creation__view)

            context = this;

            startDateButton = findViewById(R.id.editStartDate)

            // Set the endDateButton to the component
            endDateButton = findViewById(R.id.endDateButton)
        }

    inner class onCreateDateSetListener: DatePickerDialog.OnDateSetListener {
        // Create new variable in the onCreateDateSetListener to hold the button
        private var button: Button

        // Create a constructor that takes the button and sets the classes button
        constructor(button: Button) {
            this.button = button
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            // Now use the stored button instead
            button.setText( day.toString() + "/" + (month + 1).toString() + "/" + year.toString());
        }
    }

    // Switch createDatePicker to accept a button
    fun createDatePicker(button: Button) {
        val c = Calendar.getInstance()
        picker = DatePickerDialog(context, onCreateDateSetListener(button), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        picker.show()
    }

    // Change the components onClick to createStartDatePicker or endDatePicker
    fun createStartDatePicker(view : View) {
        createDatePicker(startDateButton)
    }

    fun createEndDatePicker(view : View) {
        createDatePicker(endDateButton)
    }
}

