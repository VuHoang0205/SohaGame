package com.s.sdk.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MQTTUtils {

    public static String getMF() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getDB() {
        return android.os.Build.BRAND;
    }

    public static String getDM() {
        return android.os.Build.MODEL;
    }

    public static String getDP() {
        return android.os.Build.PRODUCT;
    }

    public static String getMBN() {
        return Build.ID + " " + Build.VERSION.INCREMENTAL + " " + Build.TAGS;
    }

    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static float getBatteryLevel(Context mContext) {
        Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public static String getPhoneName() {
        try {
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            String deviceName = myDevice.getName();
            return removeComma(deviceName);
        } catch (Exception e) {
            return "default_phone_name";
        }
    }

    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    public static String getDeviceLang() {
        return Locale.getDefault().getLanguage();
    }

    public static String getNetworkType(Context mContext) {
        ConnectivityManager cm = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        String result = "";
        try {
            if (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager mTelephonyManager = (TelephonyManager) mContext
                        .getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = mTelephonyManager.getNetworkType();
                Log.e("NETWORK_TYPE", " IS " + networkType);
                if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
                        || networkType == TelephonyManager.NETWORK_TYPE_EDGE
                        || networkType == TelephonyManager.NETWORK_TYPE_CDMA
                        || networkType == TelephonyManager.NETWORK_TYPE_1xRTT
                        || networkType == TelephonyManager.NETWORK_TYPE_IDEN) {
                    result = "2G";
                } else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
                        || networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || networkType == TelephonyManager.NETWORK_TYPE_EVDO_A
                        || networkType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || networkType == TelephonyManager.NETWORK_TYPE_HSUPA
                        || networkType == TelephonyManager.NETWORK_TYPE_HSPA
                        || networkType == TelephonyManager.NETWORK_TYPE_EVDO_B
                        || networkType == TelephonyManager.NETWORK_TYPE_EHRPD
                        || networkType == TelephonyManager.NETWORK_TYPE_HSPAP

                        ) {
                    result = "3G";

                } else if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
                    result = "4G";
                } else {
                    result = "mobile" + networkType;
                }

            } else {
                result = "wifi";
            }
        } catch (Exception e) {
        }

        return result;
    }

    public static String getIPLan(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        // boolean isIPv4 =
                        // InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone
                                // suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static String isEmulator() {
        // String finger = Build.FINGERPRINT;
        if (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("vbox") || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)) {
            return "0";
        }
        return "1";
    }

    public static String removeComma(String phoneName) {
        String result = "";
        if (phoneName == null || phoneName.equals("")) {
            result = phoneName;
        } else if (phoneName.contains(",")) {
            result = phoneName.replace(",", " ");
        } else {
            result = phoneName;
        }
        return result;
    }
}
