package com.jsalazar.workcalendar.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsalazar.workcalendar.database.WorkCalendarDbHelper;
import com.jsalazar.workcalendar.database.models.WorkCalendarDataBaseModel;
import com.jsalazar.workcalendar.models.Payment;
import com.jsalazar.workcalendar.models.PaymentDetail;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private final WorkCalendarDbHelper dbHelper;

    public PaymentRepository(Context context) {
        this.dbHelper = new WorkCalendarDbHelper(context);
    }

    public void insertPayment(Payment payment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues paymentValues = new ContentValues();
            paymentValues.put(WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_NET_AMOUNT, payment.netAmount);
            paymentValues.put(WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_DATE_CREATED, payment.dateCreated);

            long paymentId = db.insert(WorkCalendarDataBaseModel.PaymentEntry.TABLE_NAME, null, paymentValues);

            if (payment.details != null) {
                for (PaymentDetail detail : payment.details) {
                    ContentValues detailValues = new ContentValues();
                    detailValues.put(WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_PAYMENT_ID, paymentId);
                    detailValues.put(WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_TYPE, detail.type);
                    detailValues.put(WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_START_DATE, detail.startDate);
                    detailValues.put(WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_END_DATE, detail.endDate);
                    detailValues.put(WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_AMOUNT, detail.amount);

                    db.insert(WorkCalendarDataBaseModel.PaymentDetailEntry.TABLE_NAME, null, detailValues);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<PaymentDetail> getPaymentDetailsForDate(String targetDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PaymentDetail> details = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.PaymentDetailEntry._ID + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_PAYMENT_ID + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_TYPE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_START_DATE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_AMOUNT +
                " FROM " + WorkCalendarDataBaseModel.PaymentDetailEntry.TABLE_NAME +
                " WHERE ? BETWEEN " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_START_DATE +
                " AND " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_END_DATE;

        Cursor cursor = db.rawQuery(query, new String[]{targetDate});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int paymentId = cursor.getInt(1);
            String type = cursor.getString(2);
            String startDate = cursor.getString(3);
            String endDate = cursor.getString(4);
            double amount = cursor.getDouble(5);

            details.add(new PaymentDetail(id, paymentId, type, startDate, endDate, amount));
        }

        cursor.close();
        db.close();

        return details;
    }

    public List<Payment> getAllPayments(boolean loadDetails) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Payment> payments = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.PaymentEntry._ID + ", " +
                WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_NET_AMOUNT + ", " +
                WorkCalendarDataBaseModel.PaymentEntry.COLUMN_NAME_DATE_CREATED +
                " FROM " + WorkCalendarDataBaseModel.PaymentEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double netAmount = cursor.getDouble(1);
            String dateCreated = cursor.getString(2);

            List<PaymentDetail> details = loadDetails ? getDetailsForPayment(db, id) : null;
            payments.add(new Payment(id, netAmount, dateCreated, details));
        }

        cursor.close();
        db.close();

        return payments;
    }

    private List<PaymentDetail> getDetailsForPayment(SQLiteDatabase db, int paymentId) {
        List<PaymentDetail> details = new ArrayList<>();

        String query = "SELECT " +
                WorkCalendarDataBaseModel.PaymentDetailEntry._ID + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_TYPE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_START_DATE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_END_DATE + ", " +
                WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_AMOUNT +
                " FROM " + WorkCalendarDataBaseModel.PaymentDetailEntry.TABLE_NAME +
                " WHERE " + WorkCalendarDataBaseModel.PaymentDetailEntry.COLUMN_NAME_PAYMENT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(paymentId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            double amount = cursor.getDouble(4);

            details.add(new PaymentDetail(id, paymentId, type, startDate, endDate, amount));
        }

        cursor.close();
        return details;
    }
}
