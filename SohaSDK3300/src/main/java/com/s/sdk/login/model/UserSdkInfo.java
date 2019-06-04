package com.s.sdk.login.model;

import com.google.gson.annotations.SerializedName;
import com.s.sdk.base.BaseResponse;

public class UserSdkInfo extends BaseResponse {

    @SerializedName("user_info")
    SLoginResult loginResult;

    @SerializedName("update")
    Update update;

    String retry;

    String logout;

    public String getLogout() {
        return logout;
    }

    public String getRetry() {
        return retry;
    }

    public SLoginResult getLoginResult() {
        return loginResult;
    }



    public Update getUpdate() {
        return update;
    }

    public static class Update {
        String status;
        String force;
        String link;


        public String getStatus() {
            return status;
        }

        public String getForce() {
            return force;
        }

        public String getLink() {
            return link;
        }
    }

}
