package com.s.sdk.fcm;

import com.s.sdk.base.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FCMService {
    @FormUrlEncoded
    @POST("api/POST/Push/RegisterDevice")
    Call<BaseResponse> registerDevice(@Field("signed_request") String bodyRequest);

    @FormUrlEncoded
    @POST("api/POST/Push/RegisterDeviceFCM")
    Call<BaseResponse> registerDeviceFCM(@Field("signed_request") String bodyRequest);
}
