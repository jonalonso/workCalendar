package com.jsalazar.workcalendar.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsalazar.workcalendar.database.WorkCalendarDbHelper;
import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;
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

        String query = "SELECT 1 FROM " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME +
                " WHERE NOT (? < " + WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE +
                " OR ? > " + WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE + ") LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{ newEnd, newStart });

        boolean hasOverlap = cursor.moveToFirst();
        cursor.close();
        db.close();

        return hasOverlap;
    }

    public void insertContract(String initialDate, String endDate, String startTime, String endTime, String serviceName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE, initialDate);
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE, endDate);
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_START_TIME, startTime);
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_TIME, endTime);
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_SERVICE_NAME, serviceName);
        values.put(WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_DESCRIPTION, description);

        db.insert(WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<Contract> getContractsForDateRange(String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Contract> contracts = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.ContractEntry._ID + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_START_TIME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_TIME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_SERVICE_NAME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_DESCRIPTION +
                " FROM " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME +
                " WHERE NOT (? < " + WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE +
                " OR ? > " + WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE + ")";

        Cursor cursor = db.rawQuery(query, new String[]{endDate,startDate});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String initialDate = cursor.getString(1);
            String endDateDb = cursor.getString(2);
            String startTime = cursor.getString(3);
            String endTime = cursor.getString(4);
            String serviceName = cursor.getString(5);
            String description = cursor.getString(6);

            contracts.add(new Contract(id,initialDate, endDateDb, startTime, endTime, serviceName, description));
        }
        cursor.close();
        db.close();

        return contracts;
    }

    public Contract getContractForDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.ContractEntry._ID + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_START_TIME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_TIME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_SERVICE_NAME + ", " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_DESCRIPTION +
                " FROM " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME +
                " WHERE ? BETWEEN " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE +
                " AND " +
                WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE +
                " LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        Contract contract = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String initialDate = cursor.getString(1);
            String endDateDb = cursor.getString(2);
            String startTime = cursor.getString(3);
            String endTime = cursor.getString(4);
            String serviceName = cursor.getString(5);
            String description = cursor.getString(6);

            contract = new Contract(id, initialDate, endDateDb, startTime, endTime, serviceName, description);
        }

        cursor.close();
        db.close();
        return contract;
    }

}
