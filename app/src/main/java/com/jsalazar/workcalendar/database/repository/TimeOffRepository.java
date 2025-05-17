package com.jsalazar.workcalendar.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsalazar.workcalendar.database.WorkCalendarDbHelper;
import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;
import com.jsalazar.workcalendar.models.TimeOff;

import java.util.ArrayList;
import java.util.List;

public class TimeOffRepository {

    private final WorkCalendarDbHelper dbHelper;

    public TimeOffRepository(Context context) {
        dbHelper = new WorkCalendarDbHelper(context);
    }

    public void insertTimeOff(String initialDate, String endDate, String type, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_INITIAL_DATE, initialDate);
        values.put(WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_END_DATE, endDate);
        values.put(WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_TYPE, type);
        values.put(WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_DESCRIPTION, description);

        db.insert(WorkCalendarDataBaseModel.TimeOffEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<TimeOff> getTimeOffForDateRange(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TimeOff> timeOffList = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.TimeOffEntry._ID + ", " +
                WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_INITIAL_DATE + ", " +
                WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_TYPE + ", " +
                WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_DESCRIPTION +
                " FROM " + WorkCalendarDataBaseModel.TimeOffEntry.TABLE_NAME +
                " WHERE NOT (? < " + WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_INITIAL_DATE +
                " OR ? > " + WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_END_DATE + ")";

        Cursor cursor = db.rawQuery(query, new String[]{endDate, startDate});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String initialDate = cursor.getString(1);
            String endDateDb = cursor.getString(2);
            String type = cursor.getString(3);
            String description = cursor.getString(4);

            timeOffList.add(new TimeOff(id, initialDate, endDateDb, type, description));
        }

        cursor.close();
        db.close();

        return timeOffList;
    }
}
