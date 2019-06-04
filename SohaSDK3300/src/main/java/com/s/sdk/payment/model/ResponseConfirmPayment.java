package com.s.sdk.payment.model;

import com.s.sdk.base.BaseResponse;

public class ResponseConfirmPayment extends BaseResponse {
    String order_id;

    public String getOrder_id() {
        return order_id;
    }
}
