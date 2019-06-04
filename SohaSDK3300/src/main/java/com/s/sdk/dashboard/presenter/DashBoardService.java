package com.s.sdk.dashboard.presenter;

import com.s.sdk.base.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DashBoardService {
    @FormUrlEncoded
    @POST("api/POST/App/DB")
    Call<BaseResponse> getDashBoardConfig(@Field("signed_request") String bodyRequest);

}
