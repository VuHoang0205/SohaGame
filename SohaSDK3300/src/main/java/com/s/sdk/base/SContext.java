package com.s.sdk.base;

import android.content.Context;

public class SContext {
    private SContext(){
    }
    private static Context applicationContext;

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context applicationContext) {
        SContext.applicationContext = applicationContext;
    }
}
