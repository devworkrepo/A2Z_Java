package com.a2zsuvidhaa.`in`.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

    fun datePicker(
            context: Context?,
            disableFutureDate: Boolean = false,
            onDatePicker: (date: String) -> Unit
    ) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar[Calendar.YEAR]
        val month = myCalendar[Calendar.MONTH]
        val day = myCalendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
                context!!,
                OnDateSetListener { view: DatePicker?, year1: Int, mMonthOfYear: Int, dayOfMonth: Int ->
                    val monthOfYear = mMonthOfYear + 1
                    var m = monthOfYear.toString()
                    var d = dayOfMonth.toString()
                    if (monthOfYear < 10) m = "0$monthOfYear"
                    if (dayOfMonth < 10) d = "0$dayOfMonth"
                    onDatePicker("$d-$m-$year1")
                }, year, month, day
        )
        if (disableFutureDate)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    fun currentDate(): String {
        val cDate = Date()
        return SimpleDateFormat("dd-MM-yyyy").format(cDate)
    }

    fun previousDate(days : Int): String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal[Calendar.DAY_OF_MONTH] = cal[Calendar.DAY_OF_MONTH] - days
        val myDate:Date = cal.time
        return SimpleDateFormat("dd-MM-yyyy").format(myDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun currentDateInMinuteHourSecond(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
    }

    fun getTimeInMilliSeconds(addHours: Int? = null): Long {

        addHours?.let {
            val date = Date()
            val timeMilli = date.time
            return timeMilli + (3600 * (addHours * 1000))
        } ?: run {
            val date = Date()
            val timeMilli = date.time
            return timeMilli
        }

    }

    fun getTimeAfterMinute(minute: Int): Long {
        val date = Date()
        val timeMilli = date.time
        return timeMilli + (60000 * minute)
    }

    fun checkAfet8Hors(loginMilliSeconds: Long): Boolean {
        return if (loginMilliSeconds > 0L) {
            loginMilliSeconds > getTimeInMilliSeconds()
        } else false

    }
}