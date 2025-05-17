package com.jsalazar.workcalendar.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class DatePicker {

    public static void showDatePicker(TextInputEditText targetEditText, Context context) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    targetEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public static void showTimePicker(TextInputEditText targetEditText, Context context) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    targetEditText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                0,//calendar.get(Calendar.MINUTE)
                false
        );
        timePickerDialog.show();
    }
}
