package com.jsalazar.workcalendar.database.models;
import android.provider.BaseColumns;

public final class WorkCalendarContract {

    private WorkCalendarContract() {}

    public static class ContractEntry implements BaseColumns {
        public static final String TABLE_NAME = "contract";

        public static final String COLUMN_NAME_INITIAL_DATE = "initialDate";   // yyyy-MM-dd
        public static final String COLUMN_NAME_END_DATE = "endDate";           // yyyy-MM-dd
        public static final String COLUMN_NAME_START_TIME = "startTime";       // HH:mm
        public static final String COLUMN_NAME_END_TIME = "endTime";           // HH:mm
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_SERVICE_NAME = "serviceName";   // Área del hospital
    }
}