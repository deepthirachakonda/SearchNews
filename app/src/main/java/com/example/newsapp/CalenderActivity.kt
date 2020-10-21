package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CalenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_layout)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val calendar = Calendar.getInstance()
        check=1;

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
            Log.i("Date selected", date.toString())
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("date", date)
            intent.putExtra("check",1);


            val currDate:String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val current : List<String>  = currDate.split('-')
            val selected : List<String>  = date.toString().split('-')
            for (idx in selected.indices){
                if (selected[idx].toInt() > current[idx].toInt()){
                    date = currDate
                    Toast.makeText(this, "Cannot be a future date, defaulting to TODAY", Toast.LENGTH_LONG).show()
                }
            }
            startActivity(intent);
        }
    }

    companion object {
        @JvmField
        var date: String? = null
        var check:Int=0;
    }
}