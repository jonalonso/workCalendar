package com.jsalazar.workcalendar.models;

public class TimeOff {

    public final int id;
    public final String initialDate;
    public final String endDate;
    public final String type;
    public final String description;

    public TimeOff(int id, String initialDate, String endDate, String type, String description) {
        this.id = id;
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.type = type;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOff timeOff = (TimeOff) o;
        return this.id == timeOff.id;
    }
}