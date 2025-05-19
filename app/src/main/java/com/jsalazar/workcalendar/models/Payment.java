package com.jsalazar.workcalendar.models;

import java.util.List;

public class Payment {

    public final int id;
    public final double netAmount;
    public final String dateCreated; // yyyy-MM-dd
    public final List<PaymentDetail> details;

    public Payment(int id, double netAmount, String dateCreated, List<PaymentDetail> details) {
        this.id = id;
        this.netAmount = netAmount;
        this.dateCreated = dateCreated;
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return this.id == payment.id;
    }
}
