package com.s.sdk.tracking;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;

import com.s.sdk.utils.Alog;

public class ProcessLifecycleOwnerTracking {
    public static void init() {
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(new LifecycleObserver() {
                    @OnLifecycleEvent(Lifecycle.Event.ON_START)
                    void onForeground() {
                        Alog.d("Lifecycle.Event.ON_START");
                        if (MQTTTracker.pendingActionOpenApp) {
                            MQTTTracker.pendingActionOpenApp = false;
                        } else {
                            STracker.trackEvent("sdk", STracker.ACTION_RESUME_APP, "");
                        }
                        // App goes to foreground
                    }


                    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                    void onBackground() {
                        //Alog.d("Lifecycle.Event.ON_STOP");
                        STracker.trackEvent("sdk", STracker.ACTION_HIDE_APP, "");
                        // App goes to background
                    }
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    void onDestroy() {
                        //Alog.d("Lifecycle.Event.ON_DESTROY");
                    }
                });
    }
}
