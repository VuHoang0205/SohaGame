package com.s.sdk;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.fcm.FCMInit;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.init.presenter.InitPresenter;
import com.s.sdk.tracking.ProcessLifecycleOwnerTracking;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import java.io.IOException;
import java.util.Map;


public class SApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Alog.e("onCreate SApplication");

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        init(this);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }


    public void init(Context context) {
        SContext.setApplicationContext(context.getApplicationContext());
        getAdvertisingIdClient();
        InitPresenter.readParamsFromAssets(SContext.getApplicationContext());
        //initServiceTrackingSoha();
        initFirebase(SContext.getApplicationContext());
        initFacebook();
        ProcessLifecycleOwnerTracking.init();
        initAppsflyer();
        //initFCM(SContext.getApplicationContext());
    }

    private void initAppsflyer() {
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);
        AppsFlyerConversionListener conversionDataListener =
                new AppsFlyerConversionListener() {
                    @Override
                    public void onInstallConversionDataLoaded(Map<String, String> map) {

                    }

                    @Override
                    public void onInstallConversionFailure(String s) {

                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> conversionData) {
                        for (String attrName : conversionData.keySet()) {
                            Alog.e("attribute: appsflyer" + attrName + " = " + conversionData.get(attrName));
                        }
                    }

                    @Override
                    public void onAttributionFailure(String s) {

                    }
                };
        //final String AF_DEV_KEY = "4MDuMgiUXFLJVRYirfWar3"; //real:4MDuMgiUXFLJVRYirfWar3, test: oTT2FbZ5bpSHrtEamqAEwb
            String appId = initModel.getAppIdAppsflyer();
            AppsFlyerLib.getInstance().init(appId, conversionDataListener, getApplicationContext());
            AppsFlyerLib.getInstance().startTracking(this);
    }


    private void getAdvertisingIdClient() {
        String idAds = PrefUtils.getString(Constants.PREF_ADS_ID_GG);
        final String deviceIdVcc = Utils.getDeviceIDVCC(SContext.getApplicationContext());
        if (TextUtils.isEmpty(idAds) || idAds.equals(deviceIdVcc)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AdvertisingIdClient.Info adInfo = null;
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(SContext.getApplicationContext());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (adInfo != null) {
                        String idAds = adInfo.getId();
                        PrefUtils.putString(Constants.PREF_ADS_ID_GG, idAds);
                    } else {
                        PrefUtils.putString(Constants.PREF_ADS_ID_GG, deviceIdVcc);
                    }
                }
            }).start();
        }
    }



    private void initFirebase(Context context) {
        FCMInit.initFirebase(context);
    }


    private void initFacebook() {
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);
        if (initModel == null) {
            Utils.showToast(SContext.getApplicationContext(), SContext.getApplicationContext().getString(R.string.s_error_init));
            return;
        }
        FacebookSdk.setApplicationId(initModel.getAppIdFacebook());
        FacebookSdk.sdkInitialize(SContext.getApplicationContext());

    }

//    private void initFCM(final Context context) {
//        //avoid duplicate send 2 times request registerDevice
//        Alog.e("initFCM");
//        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
//        if (loginResult == null) return;
//
//        //neu chua success thi luon luon send
//        boolean isSuccess = PrefUtils.getBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
//        Alog.e("PREF_IS_SEND_PUSH_NOTIFY_SUCCESS: "+isSuccess);
//        if (!isSuccess) {
//            sendTokenFCM(context);
//            return;
//        }
//
//        //neu success roi, kiem tra login = account khac thi send tiep
//        String oldUserId = PrefUtils.getString(Constants.PREF_USER_ID_OLD);
//        if (!oldUserId.equals(loginResult.getUserId())) {
//            PrefUtils.putString(Constants.PREF_USER_ID_OLD, loginResult.getUserId());
//            PrefUtils.putBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
//            sendTokenFCM(context);
//        }
//    }

//    private void sendTokenFCM(final Context context) {
//////        FirebaseInstanceId.getInstance().getInstanceId()
//////                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//////                    @Override
//////                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//////                        Alog.e("getInstanceId isSuccessful " + task.isSuccessful());
//////                        if (!task.isSuccessful()) {
//////                            //Log.w("mytag", "getInstanceId failed", task.getException());
//////                            return;
//////                        }
//////                        // Get new Instance ID token
//////                        String token = task.getResult().getToken();
//////                        Alog.e("token fcm: " + token);
//////                        FCMRequest.sendRegistrationToServer(context, token);
//////                    }
//////                });
////
////
////        if (checkPlayServices()) {
////            // Start IntentService to register this application with GCM.
////            Alog.e("Start IntentService to register this application with GCM.");
////            Intent intent = new Intent(this, RegistrationIntentService.class);
////            startService(intent);
////        }
////    }
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
////            if (apiAvailability.isUserResolvableError(resultCode)) {
////                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
////                        .show();
////            } else {
////                //Log.i(TAG, "This device is not supported.");
////                //finish();
////            }
//            return false;
//        }
//        return true;
//    }

}
