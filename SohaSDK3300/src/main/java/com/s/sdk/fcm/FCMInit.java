package com.s.sdk.fcm;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.s.sdk.utils.Alog;

public class FCMInit {
    public static void initFirebase(Context context) {
        Alog.e( "initFirebase");
        FirebaseApp.initializeApp(context.getApplicationContext());
    }

}
