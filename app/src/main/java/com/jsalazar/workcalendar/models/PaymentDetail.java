package com.jsalazar.workcalendar.models;

public class PaymentDetail {

    public final int id;
    public final int paymentId;
    public final String type;
    public final String startDate;
    public final String endDate;
    public final double amount;

    public PaymentDetail(int id, int paymentId, String type, String startDate, String endDate, double amount) {
        this.id = id;
        this.paymentId = paymentId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDetail detail = (PaymentDetail) o;
        return this.id == detail.id;
    }
}
