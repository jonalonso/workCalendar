package com.jsalazar.workcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;

public class WorkCalendarDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WorkCalendar.db";
    private static final int DATABASE_VERSION = 2;

    public WorkCalendarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_EVENTS =
            "CREATE TABLE " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME + " (" +
                    WorkCalendarDataBaseModel.ContractEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_INITIAL_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_START_TIME + " TEXT, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_END_TIME + " TEXT, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    WorkCalendarDataBaseModel.ContractEntry.COLUMN_NAME_SERVICE_NAME + " TEXT" +
                    ")";

    private static final String SQL_CREATE_TIME_OFF =
            "CREATE TABLE " + WorkCalendarDataBaseModel.TimeOffEntry.TABLE_NAME + " (" +
                    WorkCalendarDataBaseModel.TimeOffEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_INITIAL_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_END_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_TYPE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.TimeOffEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                    ")";

    private static final String SQL_DELETE_EVENTS =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME;

    private static final String SQL_DELETE_TIME_OFF =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.TimeOffEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENTS);
        db.execSQL(SQL_CREATE_TIME_OFF);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EVENTS);
        db.execSQL(SQL_DELETE_TIME_OFF);
        onCreate(db);
    }

}
