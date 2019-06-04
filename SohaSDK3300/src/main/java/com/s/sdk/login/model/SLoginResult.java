package com.s.sdk.login.model;

public class SLoginResult {
    String accessToken;
    String id;
    String puid;
    String username;
    String email;
    String type_user;
    String new_user;
    String avatar;


    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return id;
    }

    public String getPuid() {
        return puid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getType_user() {
        return type_user;
    }

    public String getNew_user() {
        return new_user;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
