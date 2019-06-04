package com.s.sdk.tracking;

import com.s.sdk.base.BaseResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface STrackerService {
    @FormUrlEncoded
    @POST("tracking")
    Call<BaseResponse> logStateGame(@FieldMap Map<String, String> maps);
}
