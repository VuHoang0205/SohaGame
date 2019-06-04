package com.s.sdk.init.model;

public class InitModel {
    private String appId;
    private String appIdFacebook;
    private String appIdAppsflyer;
    private String clientName;
    private String clientCode;


    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppIdFacebook(String appIdFacebook) {
        this.appIdFacebook = appIdFacebook;
    }

    public void setAppIdAppsflyer(String appIdAppsflyer) {
        this.appIdAppsflyer = appIdAppsflyer;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppIdFacebook() {
        return appIdFacebook;
    }

    public String getClientCode() {
        return clientCode;
    }

    public String getClientName() {
        return clientName;
    }

    public String getAppIdAppsflyer() {
        return appIdAppsflyer;
    }
}
