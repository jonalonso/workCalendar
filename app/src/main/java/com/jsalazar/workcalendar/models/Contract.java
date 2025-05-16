package com.jsalazar.workcalendar.models;

public class Contract {

    public final int id;
    public final String initialDate;
    public final String endDate;
    public final String startTime;
    public final String endTime;
    public final String serviceName;
    public final String description;

    public Contract(int id, String initialDate, String endDate, String startTime, String endTime, String serviceName, String description) {
        this.id = id;
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceName = serviceName;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return  this.id == contract.id;
    }
}