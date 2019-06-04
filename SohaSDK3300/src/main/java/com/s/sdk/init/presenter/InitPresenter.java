package com.s.sdk.init.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.s.sdk.R;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;
import com.s.sdk.utils.Validate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InitPresenter {

    public static void readParamsFromAssets(Context context) {
        BufferedReader reader = null;
        StringBuilder returnString = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("client.txt"), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                returnString.append(mLine);
            }
        } catch (IOException e) {
            Utils.showToast(SContext.getApplicationContext(), SContext.getApplicationContext().getString(R.string.s_error_init));
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        //Log.d("mytag", returnString.toString());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(returnString.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Validate.notNull(jsonObject, "client.txt");

        InitModel initModel = PrefUtils.getObject(context, Constants.PREF_INIT_MODEL, InitModel.class);
        if (initModel == null) {
            initModel = new InitModel();
        }
        initModel.setAppId(jsonObject.optString("app_id"));
        initModel.setAppIdFacebook(jsonObject.optString("app_id_facebook"));
        initModel.setAppIdAppsflyer(jsonObject.optString("app_id_appsflyer"));
        initModel.setClientCode(jsonObject.optString("client_code"));

        //client name
        if (TextUtils.isEmpty(initModel.getClientName())) {
            initModel.setClientName(jsonObject.optString("client_name"));
        }
        PrefUtils.putObject(Constants.PREF_INIT_MODEL, initModel);
    }



}
