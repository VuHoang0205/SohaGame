package com.s.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.s.sdk.base.SContext;
import com.s.sdk.dashboard.model.DashBoardItem;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LEGEND on 06-Apr-18.
 */

public class PrefUtils {
    private static final String KEY_PREF = "SF";

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
    }

    /************************************
     ***put Object
     ***************************************/
    public static void putObject(String key, Object value) {
        putObject(SContext.getApplicationContext(), key, value);
    }

    public static void putObject(Context context, String key, Object value) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreference(context);
        sharedPreferences.edit().putString(key, gson.toJson(value)).apply();
    }

    public static <T> T getObject(String key, Class<T> tClass) {
        return getObject(SContext.getApplicationContext(), key, tClass);
    }

//    public static <T> List<T> getListObject(String key, Class<T[]> clazz) {
//        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
//        String dataRaw = sharedPreferences.getString(key, "");
//        Type listType = new TypeToken<List<T>>() {}.getType();
//        return new Gson().fromJson(dataRaw, listType);
//    }

    public static List<DashBoardItem> getListObjectDB(String key) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        String dataRaw = sharedPreferences.getString(key, "");
        Type listType = new TypeToken<List<DashBoardItem>>() {
        }.getType();
        return new Gson().fromJson(dataRaw, listType);
    }

    public static <T> T getObject(Context context, String key, Class<T> tClass) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        String dataRaw = sharedPreferences.getString(key, "");
        Gson gson = new Gson();
        return gson.fromJson(dataRaw, tClass);
    }


    /************************************
     ***put String
     ***************************************/
    public static void putString(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        return sharedPreferences.getString(key, "");
    }

    /************************************
     ***put Long
     ***************************************/
    public static void putLong(String key, long value) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        return sharedPreferences.getLong(key, 0l);
    }

    public static void addSeenId(List<String> list) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        sharedPreferences.edit().putString("SeenId", gson.toJson(list)).apply();
    }

    public static List<String> getListSeenId() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        String s = sharedPreferences.getString("SeenId", "[]");
        String[] strings = gson.fromJson(s, String[].class);
        return Arrays.asList(strings);
    }

    /************************************
     ***put Int
     ***************************************/
    public static void putInt(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        return sharedPreferences.getInt(key, 0);
    }

    /************************************
     ***put Float
     ***************************************/
    public static void putFloat(String key, float value) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public static Float getFloat(String key) {
        SharedPreferences sharedPreferences = getSharedPreference(SContext.getApplicationContext());
        return sharedPreferences.getFloat(key, 0);
    }

    /************************************
     ***put boolean
     ***************************************/
    public static void putBoolean(String key, boolean value) {
        putBoolean(SContext.getApplicationContext(), key, value);
    }

    /************************************************
     * use when context of getSharedPreference null
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }


    public static boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(SContext.getApplicationContext(), key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

}
