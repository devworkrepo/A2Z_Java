package com.a2zsuvidhaa.in.util;

import android.app.DatePickerDialog;
import android.content.Context;

import com.a2zsuvidhaa.in.listener.OnDatePicker;

import java.util.Calendar;

public class DatePicker {


    public static void datePicker(Context context) {
        Calendar myCalendar = Calendar.getInstance();
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, monthOfYear, dayOfMonth) -> {

                    monthOfYear = monthOfYear + 1;

                    String m = String.valueOf(monthOfYear);
                    String d = String.valueOf(dayOfMonth);

                    if (monthOfYear < 10) {

                        m = ("0" + (monthOfYear));
                    }
                    if (dayOfMonth < 10) {

                        d = ("0" + (dayOfMonth));
                    }

                    listener.onDatePick(d + "-" + (m) + "-" + year1);
                }, year, month, day);
        datePickerDialog.show();

    }

    private static OnDatePicker listener;
    public static void setupOnDatePicker(OnDatePicker onDatePicker){
        listener = onDatePicker;
    }
}
