package com.s.sdk.dashboard.model;

public class LocationDashboard {

    public LocationDashboard(int mCurrentX, int mCurrentY) {
        this.mCurrentX = mCurrentX;
        this.mCurrentY = mCurrentY;
    }

    private int mCurrentX;
    private int mCurrentY;


    public int getCurrentX() {
        return mCurrentX;
    }

    public int getCurrentY() {
        return mCurrentY;
    }

    public void setCurrentX(int mCurrentX) {
        this.mCurrentX = mCurrentX;
    }

    public void setCurrentY(int mCurrentY) {
        this.mCurrentY = mCurrentY;
    }
}
