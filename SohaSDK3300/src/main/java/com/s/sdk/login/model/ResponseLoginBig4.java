package com.s.sdk.login.model;

import com.s.sdk.base.BaseResponse;

public class ResponseLoginBig4 extends BaseResponse {
    Data data;
    //confirm otp
    String otp_token;
    String syntax;
    String phone_number;


    public Data getData() {
        return data;
    }


    public String getOtp_token() {
        return otp_token;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public static class Data{
        String access_token;

        public String getAccess_token() {
            return access_token;
        }
    }
}
