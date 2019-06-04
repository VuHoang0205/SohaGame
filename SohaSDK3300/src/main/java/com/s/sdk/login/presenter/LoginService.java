package com.s.sdk.login.presenter;

import com.s.sdk.base.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by LEGEND on 11/10/2017.
 */

public interface LoginService {
    @FormUrlEncoded
    @POST("api/GET/Me/Userinfo")
    Call<BaseResponse> getUserInfo(@Field("signed_request") String bodyRequest);


    @FormUrlEncoded
    @POST("api/GET/Auth/LoginBig4?")
    Call<BaseResponse> loginBig4(@Field("signed_request") String bodyRequest);


    @FormUrlEncoded
    @POST("api/GET/Mobile/LogPlayUser")
    Call<BaseResponse> mapUserGame(@Field("signed_request") String bodyRequest);


}
