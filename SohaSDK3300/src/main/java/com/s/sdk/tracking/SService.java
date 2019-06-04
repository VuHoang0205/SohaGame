package com.s.sdk.tracking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.s.sdk.utils.Alog;

public class SService extends Service {
//    private boolean isSentLogKillApp = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if (intent == null && PrefUtils.getBoolean(this, STracker.ACTION_KILL_APP, false)) {
//            Alog.e("ACTION_KILL_APP 1 " + isSentLogKillApp);
//            sendLogKillApp();
//            PrefUtils.putBoolean(this, STracker.ACTION_KILL_APP, false);
//            this.stopSelf();
//        } else
            if (intent != null && intent.getAction() != null && intent.getAction().equals(STracker.ACTION_KILL_APP)) {
            //Alog.e("ACTION_KILL_APP 2 " + isSentLogKillApp);
            sendLogKillApp();
            //isSentLogKillApp = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Alog.e("service destroy");
        super.onDestroy();
    }

    private void sendLogKillApp() {
        sendLogKillAppAdmicro();
        //sendLogKillAppMySoha();
        sendLogKillAppMqtt();
        sendLogKillAppFb();
        senLogKillAppGg();
        sendLogKillAppAppsflyer();
    }

    private void sendLogKillAppMqtt() {
        Alog.e("sendLogKillAppMqtt");
        MQTTTracker.getInstance().send(STracker.ACTION_KILL_APP, "");
    }

    private void sendLogKillAppFb() {
        STracker.sendLogFacebook(STracker.ACTION_KILL_APP, "");
    }

    private void senLogKillAppGg() {
        STracker.sendLogGoogle(STracker.ACTION_KILL_APP, "");
    }

    private void sendLogKillAppAppsflyer(){
        STracker.sendLogAppsflyer(STracker.ACTION_KILL_APP, "");
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Alog.e("onTaskRemoved: " + isSentLogKillApp);
//        if (!isSentLogKillApp) {
//            PrefUtils.putBoolean(SService.this, STracker.ACTION_KILL_APP, true);
//        }
//        super.onTaskRemoved(rootIntent);
//    }

    private void sendLogKillAppAdmicro() {
        STracker.sendLogAdmicro(SService.this, STracker.ACTION_KILL_APP, "");
    }

//    private void sendLogKillAppMySoha() {
//        STracker.sendLogMySoha(SService.this, STracker.GAME_STATE_ENDGAME, "");
//    }

}
