package com.jsalazar.workcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jsalazar.workcalendar.database.models.WorkCalendarContract;

public class WorkCalendarDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WorkCalendar.db";
    private static final int DATABASE_VERSION = 1;

    public WorkCalendarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_EVENTS =
            "CREATE TABLE " + WorkCalendarContract.ContractEntry.TABLE_NAME + " (" +
                    WorkCalendarContract.ContractEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_INITIAL_DATE + " TEXT NOT NULL, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_END_DATE + " TEXT NOT NULL, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_START_TIME + " TEXT, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_END_TIME + " TEXT, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    WorkCalendarContract.ContractEntry.COLUMN_NAME_SERVICE_NAME + " TEXT" +
                    ")";

    private static final String SQL_DELETE_EVENTS =
            "DROP TABLE IF EXISTS " + WorkCalendarContract.ContractEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EVENTS);
        onCreate(db);
    }

}
