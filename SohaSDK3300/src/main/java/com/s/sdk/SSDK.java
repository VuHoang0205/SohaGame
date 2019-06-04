package com.s.sdk;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.s.sdk.base.BaseResponse;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.dashboard.model.DashBoardItem;
import com.s.sdk.dashboard.model.ResponseDashConfig;
import com.s.sdk.dashboard.presenter.DashBoardService;
import com.s.sdk.dashboard.view.DashBoardDetailFragment;
import com.s.sdk.dashboard.view.DashBoardDialog;
import com.s.sdk.dashboard.view.DashBoardPopup;
import com.s.sdk.fcm.FCMRequest;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserGameInfo;
import com.s.sdk.login.presenter.LoginService;
import com.s.sdk.network.RetrofitService;
import com.s.sdk.tracking.MQTTTracker;
import com.s.sdk.tracking.SService;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.SPopup;
import com.s.sdk.utils.Utils;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SSDK {
    //    public static String VERSION;
    private static volatile SSDK instance;
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    private SSDK() {
    }

    public static SSDK getInstance() {
        if (instance == null) {
            synchronized (SSDK.class) {
                if (instance == null) {
                    instance = new SSDK();
                }
            }
        }
        return instance;
    }

    public void init(Activity activity, LogoutCallback logoutCallback) {
        CallbackManager.setLogoutCallback(logoutCallback);
        Utils.getKeyhash(activity);
        trackingApp(activity);
        initFCM(SContext.getApplicationContext());
        DashBoardPopup.isInitedDashBoard = false;
    }

    private void initFCM(final Context context) {
        //kiem tra login = account khac thi send
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        if (loginResult == null) return;
        String oldUserId = PrefUtils.getString(Constants.PREF_USER_ID_OLD);
        if (!oldUserId.equals(loginResult.getUserId())) {
            PrefUtils.putString(Constants.PREF_USER_ID_OLD, loginResult.getUserId());
            PrefUtils.putBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
            sendTokenFCM(context);
        } else {
            boolean isSuccess = PrefUtils.getBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
            if (!isSuccess) {
                sendTokenFCM(context);
            }
        }
    }

    private void sendTokenFCM(final Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        Alog.e("getInstanceId isSuccessful " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            //Log.w("mytag", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Alog.e("token_fcm 0: " + token);
                        FCMRequest.sendRegistrationToServer(context, token);
                    }
                });


//        if (checkPlayServices(context)) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(context, RegistrationIntentService.class);
//            context.startService(intent);
//        }
    }

//    private boolean checkPlayServices(Context context) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//        return resultCode == ConnectionResult.SUCCESS;
//    }


    private void initServiceTrackingS() {
        Intent intent = new Intent(SContext.getApplicationContext(), SService.class);
        SContext.getApplicationContext().startService(intent);
    }

    private void trackingApp(final Activity activity) {
        initServiceTrackingS();
        Intent intent = activity.getIntent();
        if (intent != null) {
            boolean isActionOpen = intent.getBooleanExtra(STracker.ACTION_OPEN_NOTIFI, false);
            if (isActionOpen) {
                STracker.trackEvent("sdk", STracker.ACTION_OPEN_NOTIFI, "");
            }
        }

        //activateApp facebook
        AppEventsLogger.activateApp(activity.getApplication());

        boolean isInstalled = PrefUtils.getBoolean(STracker.ACTION_INSTALL, false);
        if (!isInstalled) {
            STracker.trackEvent("sdk", STracker.ACTION_INSTALL, "");
        }
        STracker.trackEvent("sdk", STracker.ACTION_OPEN, "");


        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activityy, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activityy) {
                if (activityy.equals(activity) && DashBoardDetailFragment.isRefreshNotify) {
                    //Alog.d("onActivityResumed activityy main");
                    DashBoardDetailFragment.isRefreshNotify = false;
                    updateDashBoardConfig(activityy);
                }
            }

            @Override
            public void onActivityPaused(Activity activityy) {
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activityy) {
                if (activityy.equals(activity)) {
                    MQTTTracker.pendingActionOpenApp = true;
                    Alog.e("MainActivity onActivityDestroyed");
                    STracker.trackEvent("sdk", STracker.ACTION_KILL_APP, "");
                    SPopup.getInstance().clearAllPopup();
                    DashBoardPopup.getInstance().clearPopup();
                    if (activityLifecycleCallbacks != null) {
                        activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                        activityLifecycleCallbacks = null;
                    }
                    CallbackManager.clearCallback();
                }
            }
        };
        activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

    }

    public void login(Activity activity, LoginCallback loginCallback) {
        CallbackManager.setLoginCallback(loginCallback);
        startLogin(activity);
    }

    private void startLogin(Activity activity) {
        Intent i = new Intent(activity, SActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_LOGIN);
        activity.startActivity(i);
    }


    public void pay(Activity activity, PaymentCallback paymentCallback) {
        CallbackManager.setPaymentCallback(paymentCallback);
        startPayment(activity);
    }

    private void startPayment(Activity activity) {
        Intent i = new Intent(activity, SActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_PAYMENT);
        activity.startActivity(i);
    }

    public void mapUserGame(final Activity activity, final String areaId, final String roleId, final String roleName,
                            final String roleLevel) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrefUtils.putObject(Constants.PREF_USER_GAME_INFO, new UserGameInfo(areaId, roleId, roleName, roleLevel));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        STracker.trackEvent("sdk", STracker.ACTION_SET_ROLE, "");
                    }
                }, 200);
                mapUserGame(activity);
                SPopup.getInstance().initAndShowPopupConnectAccountPlayNow(activity);

                ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
                if (data == null) return;
                if (data.getShow_warning_ingame() == 1) {
                    SPopup.getInstance().initAndShowPopupWarning(activity);
                }
                getDashBoardConfig(activity);
            }
        });
    }

    private void mapUserGame(Activity activity) {
        LoginService loginService = RetrofitService.create(LoginService.class);
        JSONObject jsonObject = Utils.createDefaultParams(activity);
        String signResquest = EncryptorEngine.encryptDataNoURLEn(jsonObject.toString(), Constants.PUBLIC_KEY);
        loginService.mapUserGame(signResquest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private void updateDashBoardConfig(final Activity activity) {
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) return;
        if (data.getHidden_dashboard() != 0) return;
        DashBoardService dashBoardService = RetrofitService.create(DashBoardService.class);
        final JSONObject jsonObject = Utils.createDefaultParams(activity);
        final String signResquest = EncryptorEngine.encryptDataNoURLEn(jsonObject.toString(), Constants.PUBLIC_KEY);
        dashBoardService.getDashBoardConfig(signResquest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body == null) return;
                final ResponseDashConfig res = body.decodeResponse(ResponseDashConfig.class);
                if (res == null) return;
                if ("success".equalsIgnoreCase(res.getStatus())) {
                    if (!activity.isFinishing() && res.getListData() != null && res.getListData().size() > 0) {
                        PrefUtils.putObject(Constants.PREF_LIST_DB_CONFIG, res.getListData());
                        checkShowNotify(res.getListData());
                        DashBoardPopup.getInstance().showImageNotify();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
            }
        });
    }

    private void getDashBoardConfig(final Activity activity) {
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) return;
        if (data.getHidden_dashboard() != 0) return;
        final DashBoardPopup dashBoardPopup = DashBoardPopup.getInstance();
        final boolean isShowPopup = dashBoardPopup.isShowPopup();
        Alog.e("isShowPopup: " + isShowPopup);
        if (isShowPopup) return;

        DashBoardService dashBoardService = RetrofitService.create(DashBoardService.class);
        final JSONObject jsonObject = Utils.createDefaultParams(activity);
        final String signResquest = EncryptorEngine.encryptDataNoURLEn(jsonObject.toString(), Constants.PUBLIC_KEY);
        dashBoardService.getDashBoardConfig(signResquest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body == null) return;
                final ResponseDashConfig res = body.decodeResponse(ResponseDashConfig.class);
//                Log.e("Respone_SDK", "api/POST/App/DB \n " + res.signedRequest);
//                Log.e("Param_SDK", "api/POST/App/DB \n " + signResquest);
                if (res == null) return;
                if ("success".equalsIgnoreCase(res.getStatus())) {
                    if (!activity.isFinishing() && res.getListData() != null && res.getListData().size() > 0) {
                        PrefUtils.putObject(Constants.PREF_LIST_DB_CONFIG, res.getListData());
                        checkShowNotify(res.getListData());
                        //if (!isShowPopup) {
                        dashBoardPopup.initAndShowPopup(activity);
                        DashBoardPopup.getInstance().setOnClickDashBoard(new DashBoardPopup.OnClickDashBoard() {
                            @Override
                            public void onClickDashBoard() {
                                DashBoardPopup.getInstance().hidePopup();
                                SPopup.getInstance().hidePopupWarning();
                                DashBoardDialog dashBoardDialog = new DashBoardDialog(activity);
                                dashBoardDialog.setOnEventDashBoard(new DashBoardDialog.OnEventDashBoard() {
                                    @Override
                                    public void onDismitDialog() {
                                        DashBoardPopup.getInstance().setIsMove(false);
                                        DashBoardPopup.getInstance().hideToEdge();
                                    }
                                });
                                dashBoardDialog.show();
                            }
                        });
                        // }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
            }
        });
    }

    private void checkShowNotify(List<DashBoardItem> listData) {
        for (int i = 0, leng = listData.size(); i < leng; i++) {
            DashBoardItem item = listData.get(i);
            int noti;
            try {
                noti = (item.getNotify());
            } catch (NumberFormatException e) {
                noti = 0;
            }
            if (noti > 0) {
                DashBoardPopup.showNotify = true;
                return;
            }
        }
        DashBoardPopup.showNotify = false;
    }

    public void logoutNoMessage() {
        logoutNoMessageNotCallback();
        LogoutCallback logoutCallback = CallbackManager.getLogoutCallback();
        if (logoutCallback != null) {
            logoutCallback.onLogout();
        }
    }

    public void logoutNoMessageNotCallback() {
        DashBoardPopup.isInitedDashBoard = false;
        STracker.trackEvent("sdk", STracker.ACTION_LOGOUT, "");
        LoginManager.getInstance().logOut();
        PrefUtils.putObject(Constants.PREF_LOGIN_RESULT, null);
        PrefUtils.putObject(Constants.PREF_USER_GAME_INFO, null);
        SPopup.getInstance().clearAllPopup();
        DashBoardPopup.getInstance().clearPopup();
    }

    private boolean isDissmissDialogConnectAccount = true;

    public void logout(final Activity activity /*, final LogoutCallback logoutCallback*/) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
                if (loginResult != null && "play_now".equalsIgnoreCase(loginResult.getType_user())) {
                    if (isDissmissDialogConnectAccount) {
                        Dialog dialog = SDialog.showDialog(activity, activity.getString(R.string.s_login_des_connect_account),
                                activity.getString(R.string.s_login_giveup),
                                activity.getString(R.string.s_login_connect_account),
                                new SOnClickListener() {
                                    @Override
                                    public void onClick() {
                                        logoutNoMessage();

                                    }
                                }, new SOnClickListener() {
                                    @Override
                                    public void onClick() {
//                            CallbackManager.setLogoutCallback(logoutCallback);
                                        //SPopup.getInstance().clearAllPopup();
                                        gotoConnectAccountPlayNow(activity);
                                    }
                                }
                        );
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                isDissmissDialogConnectAccount = true;
                            }
                        });
                        dialog.setCancelable(false);
                        isDissmissDialogConnectAccount = false;
                    }
                } else {
                    logoutNoMessage();
                }
            }
        });
    }

    private void gotoConnectAccountPlayNow(Activity activity) {
        Intent i = new Intent(activity, SActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_CONNECT_ACCOUNT_PLAYNOW);
        activity.startActivity(i);
    }

    public void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
