package com.s.sdk.payment.model;

public class SPaymentResult {
    String orderId;

    public SPaymentResult(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
