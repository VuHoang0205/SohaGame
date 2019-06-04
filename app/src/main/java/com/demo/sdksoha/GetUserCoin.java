package com.demo.sdksoha;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.s.sdk.login.model.SLoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetUserCoin extends AsyncTask<Void, Void, String> {

    private SLoginResult loginResult;
    private OnEventUserCoin onEventUserCoin;
    private static final String COIN_API = "http://soap.soha.vn/api/a/GET/util/Infosdkdemo?&user_id=";

    GetUserCoin(SLoginResult loginResult, OnEventUserCoin onEventUserCoin) {
        this.loginResult = loginResult;
        this.onEventUserCoin = onEventUserCoin;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder stringBuilder = null;
        try {
            URL url = new URL(COIN_API + loginResult.getUserId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (stringBuilder != null) {
            return stringBuilder.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        Log.d("mytag", "GetUserCoin: " + string);
        if (TextUtils.isEmpty(string)) return;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(string);
            String point = jsonObject.getJSONObject("data").getString("point");
            onEventUserCoin.onCoinUser("UserCoin: " + point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnEventUserCoin {

        void onCoinUser(String coinUser);

    }

}
