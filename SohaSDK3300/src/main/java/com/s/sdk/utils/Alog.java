package com.s.sdk.utils;

import android.util.Log;

public class Alog {
    private static final boolean ENABLE_DEBUG = true;
    private static final String TAG = "stag";

    public static void e(String message) {
        if (message == null) message = "empty message log";
        if (ENABLE_DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void d(String message) {
        if (message == null) message = "empty message log";
        if (ENABLE_DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if (message == null) message = "empty message log";
        if (ENABLE_DEBUG) {
            Log.i(TAG, message);
        }
    }

}
