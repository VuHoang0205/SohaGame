package com.s.sdk.payment.presenter;

import com.s.sdk.base.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PaymentService {

    @FormUrlEncoded
    @POST("api/POST/pay/create")
    Call<BaseResponse> createPayment(@Field("signed_request") String bodyRequest);


    @FormUrlEncoded
    @POST("api/POST/pay/appstore")
    Call<BaseResponse> confirmPayment(@Field("signed_request") String bodyRequest);
}
