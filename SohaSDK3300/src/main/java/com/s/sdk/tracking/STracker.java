package com.s.sdk.tracking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mesglog.sdk.MesgLog;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class STracker {
    private STracker() {
    }

    public static final String ACTION_INSTALL = "install";
    public static final String ACTION_OPEN = "open_app";
    public static final String ACTION_LOGIN_OPEN = "login";
    public static final String ACTION_LOGIN_SUCCESS = "login_success";
    public static final String ACTION_NEW_USER = "new_user";
    public static final String ACTION_LOGOUT = "logout";
    public static final String ACTION_PAYMENT_OPEN = "open_pay";
    public static final String ACTION_PAYMENT_CLOSE = "close_pay";
    public static final String ACTION_PAYMENT_FINISH = "pay_finish";
    public static final String ACTION_OPEN_NOTIFI = "open_notifi";
    public static final String ACTION_KILL_APP = "kill_app";
    public static final String ACTION_SET_ROLE = "set_role";
    public static final String ACTION_IAP_START = "iap_start";
    public static final String ACTION_IAP_END = "iap_end";
    public static final String ACTION_OPEN_DB = "open_db";
    public static final String ACTION_CLOSE_DB = "close_db";
    public static final String ACTION_HIDE_APP = "hide_app";
    public static final String ACTION_RESUME_APP = "resume_app";
    public static final String ACTION_REMOVE_DB = "remove_db";

    //tracking game state (mysoha)
//    public static final String GAME_STATE_LOGOUT = "logout";
//    public static final String GAME_STATE_STARTGAME = "start_game";
//    public static final String GAME_STATE_ENDGAME = "end_game";

    /**
     * trackEvent
     *
     * @param category
     * @param action
     * @param label
     */
    public static void trackEvent(String category, @NonNull String action, String label) {
        if (ACTION_KILL_APP.equals(action)) {
            sendLogKillApp();
            return;
        }
        sendLogFacebook(action, label);
        sendLogGoogle(action, label);
        sendLogAppsflyer(action, label);
        MQTTTracker.getInstance().send(action, label);
//        if (!/*(action.equals(ACTION_OPEN) || action.equals(ACTION_INSTALL)
//                ||*/ action.equals(ACTION_KILL_APP) /*|| action.equals(ACTION_OPEN_NOTIFI))*/) {
//            //Alog.e("MQTTTracker not ACTION_KILL_APP ");
//            MQTTTracker.getInstance().send(action, label);
//        }
        switch (action) {
            case ACTION_OPEN:
                sendLogAdmicro(ACTION_OPEN, label);
                //sendLogMySoha(GAME_STATE_STARTGAME, label);
                break;
//            case ACTION_LOGOUT:
            //sendLogMySoha(GAME_STATE_LOGOUT, label);
//                break;
            case ACTION_LOGIN_OPEN:
                SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
                int versionPref = PrefUtils.getInt(Constants.PREF_VERSION_CODE_APP);
                int currentVersionCode = Utils.getAppVersionCode(SContext.getApplicationContext());

                if (loginResult == null && (versionPref == 0)) {
                    sendLogAdmicro(ACTION_LOGIN_OPEN, "");
                } else if (loginResult != null && (currentVersionCode != versionPref)) {
                    sendLogAdmicro(ACTION_LOGIN_OPEN, loginResult.getPuid());
                    PrefUtils.putInt(Constants.PREF_VERSION_CODE_APP, currentVersionCode);
                }
                break;
        }
    }

    public static void sendLogAppsflyer(final String action, final String ext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> eventValue = new HashMap();
                JSONObject jsonObject = MQTTTracker.getInstance().createParamsTracking(action, ext, 0);
                eventValue.put(AFInAppEventParameterName.CONTENT, jsonObject.toString());
                AppsFlyerLib.getInstance().trackEvent(SContext.getApplicationContext(), action, eventValue);
            }
        }).start();
    }

    public static void sendLogFacebook(String action, String ext) {
        AppEventsLogger logger = AppEventsLogger.newLogger(SContext.getApplicationContext());
        logger.logEvent(action, convertParamsMqttToFacebook(action, ext));
    }

    private static Bundle convertParamsMqttToFacebook(String action, String ext) {
        Bundle bundle = new Bundle();
        JSONObject jsonObject = MQTTTracker.getInstance().createParamsTracking(action, ext, 0);

        String keys = jsonObject.optString("k");
        String values = jsonObject.optString("v");
        String[] arrayKey = keys.split(",");
        String[] arrayValue = values.split(",");

        for (int i = 0, leng = arrayKey.length; i < leng; i++) {
            bundle.putString(arrayKey[i], arrayValue[i]);
        }
        return bundle;
    }

    public static void sendLogGoogle(String action, String ext) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, ext);
        FirebaseAnalytics.getInstance(SContext.getApplicationContext()).logEvent(action, bundle);
    }

//    public static void sendLogMySoha(Context context, String action, String ext) {
//        InitModel initModel = PrefUtils.getObject(context, Constants.PREF_INIT_MODEL, InitModel.class);
//        String appKey = "";
//        if (initModel != null) {
//            appKey = initModel.getAppId();
//        }
//        SLoginResult loginResult = PrefUtils.getObject(context, Constants.PREF_LOGIN_RESULT, SLoginResult.class);
//        String email = "";
//        if (loginResult != null) {
//            email = loginResult.getEmail();
//        }
//        sendLogMySoha(action, appKey, email);
//    }

//    public static void sendLogMySoha(String action, String ext) {
//        sendLogMySoha(SContext.getApplicationContext(), action, ext);
//    }


//    private static void sendLogMySoha(String action, String appKey, String email) {
//        STrackerService sohaTrackerService = RetrofitService.createRetrofitMySoha(STrackerService.class);
//        Map<String, String> map = new HashMap<>();
//        map.put("app_key", appKey);
//        map.put("device_type", "android");
//        map.put("user_email", email);
//        map.put("type", action);
//        sohaTrackerService.logStateGame(map).enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//
//            }
//        });
//    }

    private static void sendLogKillApp() {
        Intent intent = new Intent(SContext.getApplicationContext(), SService.class);
        intent.setAction(ACTION_KILL_APP);
        SContext.getApplicationContext().startService(intent);
    }

    public static void sendLogAdmicro(final Context context, final String action, final String ext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (action) {
                        case ACTION_OPEN:
                            MesgLog.sendLogInstall(SContext.getApplicationContext(), "");
                            MesgLog.sendLogAction(SContext.getApplicationContext(), "0", "");
                            break;
                        case ACTION_KILL_APP:
                            MesgLog.sendLogAction(context, "1", "");
                            break;
                        case ACTION_LOGIN_OPEN:
                            MesgLog.sendLogConfirm(SContext.getApplicationContext(), ext);
                            break;
                    }
                } catch (UnsatisfiedLinkError e) {
                } catch (NoClassDefFoundError e) {
                }

            }
        }).start();
    }

    public static void sendLogAdmicro(String action, final String ext) {
        sendLogAdmicro(SContext.getApplicationContext(), action, ext);
    }

//    public static void trackEvent(String action, String label) {
//
//    }

}
