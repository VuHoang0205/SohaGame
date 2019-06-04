package com.s.sdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.s.sdk.R;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserGameInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Utils {

    public static String getAppVersionName(Context mContext) {
        String versionName = "";
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAppVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getDeviceIDVCC(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getAppName(Context mContext) {
        String appName = "Sgame";
        try {
            int stringId = mContext.getApplicationInfo().labelRes;
            appName = mContext.getString(stringId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static int getAppIcon(Context mContext) {
        int appIcon = android.R.drawable.ic_notification_overlay;
        try {
            appIcon = mContext.getApplicationInfo().icon;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    public static String getSDKVersion(Context context) {
        return context.getApplicationContext().getString(R.string.s_sdk_version);
    }

    public static void showToastError(Context context) {
        showToast(context, context.getString(R.string.s_error_generic));
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

//        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected()))
//            return true;
//        else return false;
    }


    public static JSONObject createDefaultParams(Context context) {
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);

        SLoginResult sLoginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        String accessToken = "";
        if (sLoginResult != null) {
            accessToken = sLoginResult.getAccessToken();
        }

        String area_id = "";
        String role_id = "";
        String role_name = "";
        String role_level = "";

        UserGameInfo userGameInfo = PrefUtils.getObject(Constants.PREF_USER_GAME_INFO, UserGameInfo.class);
        if (userGameInfo != null) {
            area_id = userGameInfo.getAreaId();
            role_id = userGameInfo.getRoleId();
            role_name = Base64.encodeToString(userGameInfo.getRoleName().getBytes(), Base64.DEFAULT);
            role_level = userGameInfo.getRoleLevel();
        }

        JSONObject obj = new JSONObject();
        String clientIdMqtt = getClientIdMQTT(initModel);
        try {
            obj.put("app_id", initModel.getAppId());
            obj.put("area_id", area_id);
            obj.put("role_id", role_id);
            obj.put("role_name", role_name);
            obj.put("role_level", role_level);
            obj.put("gver", getAppVersionName(context));
            obj.put("sdkver", Utils.getSDKVersion(context));
            obj.put("clientname", initModel.getClientName());
            obj.put("access_token", accessToken);
            obj.put("device_id_vcc", getDeviceIDVCC(context));
            obj.put("bundle_id", context.getApplicationContext().getPackageName());
            obj.put("device_id", PrefUtils.getString(Constants.PREF_ADS_ID_GG));
            obj.put("redirect_uri", "uri_login");
            obj.put("client_id", clientIdMqtt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Alog.e( "createParams: " + obj.toString());
        return obj;
    }

    public static String getClientIdMQTT(InitModel initModel) {
        String clientId = PrefUtils.getString(Constants.PREF_CLIENTID_MQTT);
        if (TextUtils.isEmpty(clientId)) {
            clientId = String.format("%s_%s_%s_%s_%s", initModel.getClientCode(), initModel.getAppId(),
                    Utils.getDeviceIDVCC(SContext.getApplicationContext()), System.currentTimeMillis(), UUID.randomUUID().toString().substring(0, 5));
            PrefUtils.putString(Constants.PREF_CLIENTID_MQTT, clientId);
        }
        return clientId;
    }


    public static void getKeyhash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Alog.e("key_hash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
