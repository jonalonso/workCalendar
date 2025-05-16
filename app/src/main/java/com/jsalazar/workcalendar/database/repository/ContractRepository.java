package com.jsalazar.workcalendar.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsalazar.workcalendar.database.WorkCalendarDbHelper;
import com.jsalazar.workcalendar.database.models.WorkCalendarContract;
import com.jsalazar.workcalendar.models.Contract;

import java.util.ArrayList;
import java.util.List;

public class ContractRepository {

    private final WorkCalendarDbHelper dbHelper;

    public ContractRepository(Context context) {
        dbHelper = new WorkCalendarDbHelper(context);
    }

    public boolean isDateRangeOverlapping(String newStart, String newEnd) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT 1 FROM " + WorkCalendarContract.ContractEntry.TABLE_NAME +
                " WHERE NOT (? < " + WorkCalendarContract.ContractEntry.COLUMN_NAME_INITIAL_DATE +
                " OR ? > " + WorkCalendarContract.ContractEntry.COLUMN_NAME_END_DATE + ") LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{ newEnd, newStart });

        boolean hasOverlap = cursor.moveToFirst();
        cursor.close();
        db.close();

        return hasOverlap;
    }

    public void insertContract(String initialDate, String endDate, String startTime, String endTime, String serviceName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_INITIAL_DATE, initialDate);
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_END_DATE, endDate);
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_START_TIME, startTime);
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_END_TIME, endTime);
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_SERVICE_NAME, serviceName);
        values.put(WorkCalendarContract.ContractEntry.COLUMN_NAME_DESCRIPTION, description);

        db.insert(WorkCalendarContract.ContractEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<Contract> getContractsForDateRange(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Contract> contracts = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_INITIAL_DATE + ", " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_START_TIME + ", " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_END_TIME + ", " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_SERVICE_NAME + ", " +
                WorkCalendarContract.ContractEntry.COLUMN_NAME_DESCRIPTION +
                " FROM " + WorkCalendarContract.ContractEntry.TABLE_NAME +
                " WHERE NOT (? < " + WorkCalendarContract.ContractEntry.COLUMN_NAME_INITIAL_DATE +
                " OR ? > " + WorkCalendarContract.ContractEntry.COLUMN_NAME_END_DATE + ")";

        Cursor cursor = db.rawQuery(query, new String[]{endDate,startDate});

        while (cursor.moveToNext()) {
            String initialDate = cursor.getString(0);
            String endDateDb = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            String serviceName = cursor.getString(4);
            String description = cursor.getString(5);

            contracts.add(new Contract(initialDate, endDateDb, startTime, endTime, serviceName, description));
        }
        cursor.close();
        db.close();

        return contracts;
    }
}
