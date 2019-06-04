package com.s.sdk;

public class SInitOption {
    private boolean isAutoLogin;


    private SInitOption(Builder builder) {
        this.isAutoLogin = builder.isAutoLogin;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }


    static class Builder {
        private boolean isAutoLogin;

        public Builder() {
        }

        public void setAutoLogin(boolean autoLogin) {
            isAutoLogin = autoLogin;
        }

        public SInitOption build() {
            return new SInitOption(this);
        }


    }

}
