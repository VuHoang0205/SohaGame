package com.s.sdk.init.presenter;

import com.s.sdk.base.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface InitService {
    @FormUrlEncoded
    @POST("api/GET/App/Oinfo")
    Call<BaseResponse> getAppInfo(@Field("signed_request") String bodyRequest);
}
