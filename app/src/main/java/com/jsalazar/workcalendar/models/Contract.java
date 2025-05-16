package com.jsalazar.workcalendar.models;

public class Contract {
    public final String initialDate;
    public final String endDate;
    public final String startTime;
    public final String endTime;
    public final String serviceName;
    public final String description;

    public Contract(String initialDate, String endDate, String startTime, String endTime, String serviceName, String description) {
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceName = serviceName;
        this.description = description;
    }
}