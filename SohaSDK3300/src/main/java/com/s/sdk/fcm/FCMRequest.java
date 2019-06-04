package com.s.sdk.fcm;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.s.sdk.base.BaseResponse;
import com.s.sdk.base.Constants;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.network.RetrofitService;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMRequest {
    public static void sendRegistrationToServer(final Context context, String token) {
        subscribeTopic();
        FCMService fcmService = RetrofitService.create(FCMService.class);
        JSONObject jsonObject = Utils.createDefaultParams(context);
        try {
            jsonObject.put("redirect_uri", "uri_login");
            jsonObject.put("device_token", token);
            jsonObject.put("type", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //send fcm
        Call<BaseResponse> callRegisterDevice = fcmService.registerDeviceFCM(EncryptorEngine.encryptDataNoURLEn(jsonObject.toString(), Constants.PUBLIC_KEY));
        callRegisterDevice.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                BaseResponse res = response.body();
                if (res != null) {
                    BaseResponse respon = res.decodeResponse(BaseResponse.class);
                    if (respon != null && respon.getStatus().equals("success")) {
                        PrefUtils.putBoolean(context, Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
            }
        });
        //send gcm
        Call<BaseResponse> callRegisterDeviceGCM = fcmService.registerDevice(EncryptorEngine.encryptDataNoURLEn(jsonObject.toString(), Constants.PUBLIC_KEY));
        callRegisterDeviceGCM.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
            }
        });
    }


    private static void subscribeTopic() {
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);
        if (initModel == null) return;
        FirebaseMessaging.getInstance().subscribeToTopic(initModel.getAppId())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Alog.e("subscribeToTopic: " + task.isSuccessful());
                    }
                });
    }

}
