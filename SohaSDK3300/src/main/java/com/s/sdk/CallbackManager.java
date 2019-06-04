package com.s.sdk;

import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.payment.model.SPaymentResult;

import java.util.HashMap;
import java.util.Map;

public class CallbackManager {
    private static final int ID_CALLBACK_LOGIN = 1;
    private static final int ID_CALLBACK_PAYMENT = 2;
    private static final int ID_CALLBACK_LOGOUT = 3;

    private static Map<Integer, SCallback> staticCallback = new HashMap<>();
    private static Map<Integer, LogoutCallback> staticCallback2 = new HashMap<>();

    //login
    public static void setLoginCallback(SCallback<SLoginResult> callback) {
        staticCallback.put(ID_CALLBACK_LOGIN, callback);
    }

    public static SCallback getLoginCallback() {
        return staticCallback.get(ID_CALLBACK_LOGIN);
    }

    //payment
    public static void setPaymentCallback(SCallback<SPaymentResult> callback) {
        staticCallback.put(ID_CALLBACK_PAYMENT, callback);
    }

    public static SCallback getPaymentCallback() {
        return staticCallback.get(ID_CALLBACK_PAYMENT);
    }

    //logoutNoMessage
    public static void setLogoutCallback(LogoutCallback callback) {
        staticCallback2.put(ID_CALLBACK_LOGOUT, callback);
    }

    public static LogoutCallback getLogoutCallback() {
        return staticCallback2.get(ID_CALLBACK_LOGOUT);
    }


    public static void clearCallback() {
        if (staticCallback != null) staticCallback.clear();
        if (staticCallback2 != null) staticCallback2.clear();
    }
// Đây là comment
}
