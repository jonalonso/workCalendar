package com.jsalazar.workcalendar.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsalazar.workcalendar.database.WorkCalendarDbHelper;
import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;
import com.jsalazar.workcalendar.models.OverTime;

import java.util.ArrayList;
import java.util.List;

public class OverTimeRepository {

    private final WorkCalendarDbHelper dbHelper;

    public OverTimeRepository(Context context) {
        dbHelper = new WorkCalendarDbHelper(context);
    }

    public void insertOverTime(String initialDate, String startTime, String endTime, String serviceName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_INITIAL_DATE, initialDate);
        values.put(WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_START_TIME, startTime);
        values.put(WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_END_TIME, endTime);
        values.put(WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_SERVICE_NAME, serviceName);
        values.put(WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_DESCRIPTION, description);

        db.insert(WorkCalendarDataBaseModel.OverTimeEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<OverTime> getOverTimeForDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<OverTime> overTimeList = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.OverTimeEntry._ID + ", " +
                WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_INITIAL_DATE + ", " +
                WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_START_TIME + ", " +
                WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_END_TIME + ", " +
                WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_SERVICE_NAME + ", " +
                WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_DESCRIPTION +
                " FROM " + WorkCalendarDataBaseModel.OverTimeEntry.TABLE_NAME +
                " WHERE " + WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_INITIAL_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String initialDate = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            String serviceName = cursor.getString(4);
            String description = cursor.getString(5);

            overTimeList.add(new OverTime(id, initialDate, startTime, endTime, serviceName, description));
        }

        cursor.close();
        db.close();

        return overTimeList;
    }
}