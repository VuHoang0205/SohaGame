package com.mesglog.sdk;

import android.content.Context;

public class MesgLog {
    static {
        System.loadLibrary("MesgLogSdk");
    }

    public static native boolean sendLogInstall(Context var0, String var1);

    public static native boolean sendLogConfirm(Context var0, String var1);

    public static native boolean sendLogAction(Context var0, String var1, String var2);
}
