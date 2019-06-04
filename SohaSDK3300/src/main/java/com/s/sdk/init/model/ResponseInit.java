package com.s.sdk.init.model;

import com.google.gson.annotations.SerializedName;
import com.s.sdk.base.BaseResponse;

public class ResponseInit extends BaseResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        String status;
        String image_age;
        String warning_time_message;
        int show_warning_ingame;
        String e_name;
        String device_token;

        String size_image_age_width;
        String size_image_age_height;
        String limit_reconnect_mqtt;
        String icon_db;
        int hidden_dashboard;
        int warning_time_connect;

        String active_mqtt;
        String domain_mqtt;
        String port_mqtt;
        String url_warning;
        String logo;

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDevice_token() {
            return device_token;
        }

        public String getE_name() {
            return e_name;
        }

        public String getStatus() {
            return status;
        }

        public String getImage_age() {
            return image_age;
        }

        public String getWarning_time_message() {
            return warning_time_message;
        }

        public int getShow_warning_ingame() {
            return show_warning_ingame;
        }

        public String getSize_image_age_width() {
            return size_image_age_width;
        }

        public String getSize_image_age_height() {
            return size_image_age_height;
        }

        public String getLimit_reconnect_mqtt() {
            return limit_reconnect_mqtt;
        }

        public String getIcon_db() {
            return icon_db;
        }

        public int getHidden_dashboard() {
            return hidden_dashboard;
        }

        public int getWarning_time_connect() {
            return warning_time_connect;
        }

        public String getActive_mqtt() {
            return active_mqtt;
        }

        public String getDomain_mqtt() {
            return domain_mqtt;
        }

        public String getPort_mqtt() {
            return port_mqtt;
        }

        public String getUrl_warning() {
            return url_warning;
        }
    }

//    public static class Update{
//    }

}
