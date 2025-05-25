package com.jsalazar.workcalendar.models;

import java.util.List;

public class DayEvents {

    public final Contract contract;
    public final OverTime overTime;
    public final List<PaymentDetail> paymentDetails;
    public final List<TimeOff> timeOffs;

    public DayEvents(Contract contract, OverTime overTime, List<PaymentDetail> paymentDetails, List<TimeOff> timeOffs) {
        this.contract = contract;
        this.overTime = overTime;
        this.paymentDetails = paymentDetails;
        this.timeOffs = timeOffs;
    }
}
