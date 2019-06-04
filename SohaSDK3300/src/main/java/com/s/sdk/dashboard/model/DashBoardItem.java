package com.s.sdk.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class DashBoardItem {
    int tab;
    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId_page(String id_page) {
        this.id_page = id_page;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setMessageActive(String messageActive) {
        this.messageActive = messageActive;
    }

    public DashBoardItem(String title, String type, String icon, String url, int tab, int active) {
        super();
        this.title = title;
        this.type = type;
        this.icon = icon;
        this.url = url;
        this.tab = tab;
        this.active = active;
    }
//    public static final String TYPE_MORE_ITEM = "more_item";
    String title;
    String type;
    String icon;
    String url;
    String id_page;

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    int notify;
    public String getId_page() {
        return id_page;
    }


//    String notify;
    int active  = -1;
    @SerializedName("mess_active")
    String messageActive;

//    public String getNotify() {
//        return notify;
//    }

    public int getActive() {
        return active;
    }

    public String getMessageActive() {
        return messageActive;
    }



    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }
}
