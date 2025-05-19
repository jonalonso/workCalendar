package com.jsalazar.workcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;

public class WorkCalendarDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WorkCalendar.db";
    private static final int DATABASE_VERSION = 4;

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

    private static final String SQL_CREATE_OVERTIME =
            "CREATE TABLE " + WorkCalendarDataBaseModel.OverTimeEntry.TABLE_NAME + " (" +
                    WorkCalendarDataBaseModel.OverTimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_INITIAL_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_START_TIME + " TEXT, " +
                    WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_END_TIME + " TEXT, " +
                    WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    WorkCalendarDataBaseModel.OverTimeEntry.COLUMN_NAME_SERVICE_NAME + " TEXT" +
                    ")";
    private static final String SQL_CREATE_PAYMENT =
            "CREATE TABLE " + WorkCalendarDataBaseModel.PaymentEntry.TABLE_NAME + " (" +
                    WorkCalendarDataBaseModel.PaymentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_NET_AMOUNT + " REAL NOT NULL, " +
                    WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_DATE_CREATED + " TEXT NOT NULL" +
                    ")";

    private static final String SQL_CREATE_PAYMENT_DETAIL =
            "CREATE TABLE " + WorkCalendarDataBaseModel.PaymentDetailEntry.TABLE_NAME + " (" +
                    WorkCalendarDataBaseModel.PaymentDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_PAYMENT_ID + " INTEGER NOT NULL, " +
                    WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_TYPE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_START_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_END_DATE + " TEXT NOT NULL, " +
                    WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_AMOUNT + " REAL NOT NULL, " +
                    "FOREIGN KEY(" + WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_PAYMENT_ID + ") REFERENCES " +
                    WorkCalendarDataBaseModel.PaymentEntry.TABLE_NAME + "(" + WorkCalendarDataBaseModel.PaymentEntry._ID + ") ON DELETE CASCADE" +
                    ")";
    private static final String SQL_DELETE_OVERTIME =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.OverTimeEntry.TABLE_NAME;


    private static final String SQL_DELETE_EVENTS =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.ContractEntry.TABLE_NAME;

    private static final String SQL_DELETE_TIME_OFF =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.TimeOffEntry.TABLE_NAME;

    private static final String SQL_DELETE_PAYMENT =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.PaymentEntry.TABLE_NAME;

    private static final String SQL_DELETE_PAYMENT_DETAIL =
            "DROP TABLE IF EXISTS " + WorkCalendarDataBaseModel.PaymentDetailEntry.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENTS);
        db.execSQL(SQL_CREATE_TIME_OFF);
        db.execSQL(SQL_CREATE_OVERTIME);
        db.execSQL(SQL_CREATE_PAYMENT);
        db.execSQL(SQL_CREATE_PAYMENT_DETAIL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PAYMENT_DETAIL);
        db.execSQL(SQL_DELETE_PAYMENT);
        db.execSQL(SQL_DELETE_EVENTS);
        db.execSQL(SQL_DELETE_TIME_OFF);
        db.execSQL(SQL_DELETE_OVERTIME);
        onCreate(db);
    }

}
