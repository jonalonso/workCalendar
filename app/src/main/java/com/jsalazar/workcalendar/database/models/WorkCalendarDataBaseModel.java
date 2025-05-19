package com.jsalazar.workcalendar.database.models;
import android.provider.BaseColumns;

public final class WorkCalendarDataBaseModel {

    private WorkCalendarDataBaseModel() {}

    public static class ContractEntry implements BaseColumns {
        public static final String TABLE_NAME = "contract";
        public static final String COLUMN_NAME_INITIAL_DATE = "initialDate";   // yyyy-MM-dd
        public static final String COLUMN_NAME_END_DATE = "endDate";           // yyyy-MM-dd
        public static final String COLUMN_NAME_START_TIME = "startTime";       // HH:mm
        public static final String COLUMN_NAME_END_TIME = "endTime";           // HH:mm
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_SERVICE_NAME = "serviceName";
    }

    public static class TimeOffEntry implements BaseColumns {
        public static final String TABLE_NAME = "time_off";
        public static final String COLUMN_NAME_INITIAL_DATE = "initialDate";   // yyyy-MM-dd
        public static final String COLUMN_NAME_END_DATE = "endDate";           // yyyy-MM-dd
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public static class OverTimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "overtime";
        public static final String COLUMN_NAME_INITIAL_DATE = "initialDate";   // yyyy-MM-dd
        public static final String COLUMN_NAME_START_TIME = "startTime";       // HH:mm
        public static final String COLUMN_NAME_END_TIME = "endTime";           // HH:mm
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_SERVICE_NAME = "serviceName";
    }

    public static class PaymentEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment";
        public static final String COLUMN_NAME_NET_AMOUNT = "netAmount";       // real
        public static final String COLUMN_NAME_DATE_CREATED = "dateCreated";   // yyyy-MM-dd HH:mm:ss
    }

    public static class PaymentDetailEntry implements BaseColumns {
        public static final String TABLE_NAME = "payment_detail";
        public static final String COLUMN_NAME_PAYMENT_ID = "paymentId";       // foreign key
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_START_DATE = "startDate";       // yyyy-MM-dd
        public static final String COLUMN_NAME_END_DATE = "endDate";           // yyyy-MM-dd
        public static final String COLUMN_NAME_AMOUNT = "amount";              // real
    }
}