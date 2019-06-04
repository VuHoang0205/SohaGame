package com.s.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.s.sdk.base.Constants;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.PrefUtils;


public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String referrerValue = extras.getString("referrer");
        Alog.e("InstallReceiver : referrerValue= " + referrerValue);

        if (!TextUtils.isEmpty(referrerValue)) {
            String[] mang = referrerValue.split("=");
            if (mang.length > 1) {
                String clientName = referrerValue.split("=")[1];
                if (TextUtils.isEmpty(clientName)) return;
                InitModel initModel = PrefUtils.getObject(context, Constants.PREF_INIT_MODEL, InitModel.class);
                if (initModel == null) {
                    initModel = new InitModel();
                }
                initModel.setClientName(clientName);
                PrefUtils.putObject(context, Constants.PREF_INIT_MODEL, initModel);
            }
        }
    }
}
