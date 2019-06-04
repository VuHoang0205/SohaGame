package com.s.sdk.base;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    String status; //success, notice,fail
    String message;
    int error_code;

    @SerializedName("signed_request")
    String signedRequest;

    public <T> T decodeResponse(Class<T> tClass) {
        if (TextUtils.isEmpty(signedRequest)) return null;
        String sDecode = new String(Base64.decode(signedRequest, Base64.DEFAULT));
        Gson gson = new Gson();
        try {
            return gson.fromJson(sDecode, tClass);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }


    public int getError_code() {
        return error_code;
    }
}
