package com.s.sdk.login.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Window;

import com.s.sdk.R;
import com.s.sdk.base.BaseResponse;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.init.presenter.InitService;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.network.RetrofitService;
import com.s.sdk.tracking.MQTTTracker;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View baseView;
    private Call<BaseResponse> callInit;

    @Override
    public void attachView(LoginContract.View view) {
        baseView = view;
    }

    @Override
    public void detachView() {
        if (callInit != null) callInit.cancel();
        baseView = null;
    }

    // SDK goi ngam Api appInfor de lay thoong tin va api register
    @Override
    public void getAppInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(baseView.getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Stops people from accidently cancelling the login flow
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //cancel();
            }
        });
        progressDialog.show();

        String request = Utils.createDefaultParams(SContext.getApplicationContext()).toString();
        String signRequest = EncryptorEngine.encryptDataNoURLEn(request, Constants.PUBLIC_KEY);
        InitService initService = RetrofitService.create(InitService.class);
        callInit = initService.getAppInfo(signRequest);
        callInit.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                BaseResponse res = response.body();
                if (res == null) {
                    //Utils.showToastError(SContext.getApplicationContext());
                    showDialogError(baseView.getContext().getString(R.string.s_error_generic));
                    return;
                }

                // Ket qua tra ve ResponseInit
                ResponseInit resDecode = res.decodeResponse(ResponseInit.class);
                if (resDecode == null) {
                    //Utils.showToastError(SContext.getApplicationContext());
                    showDialogError(baseView.getContext().getString(R.string.s_error_generic));
                    return;
                }
                if (resDecode.getStatus().equals("success") && resDecode.getData() != null) {
                    PrefUtils.putObject(Constants.PREF_RESPONSE_INIT_DATA, resDecode.getData());
                    SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);

                    // Check show Popup canh baodo tuoi game
                    if (loginResult == null && resDecode.getData().getShow_warning_ingame() == 1) {
                        SDialog.showDialogWarning(baseView.getContext());
                    }
                    if (resDecode.getData().getActive_mqtt().equals("1")) {
                        MQTTTracker.getInstance().initMQTT();
                    }

                    // Api appinfo success
                    baseView.onSuccessGetAppInfo();
                    return;
                }

                //con lai loi
                // Api appinfo error loi
                showDialogError(resDecode.getMessage());
            }

            // Api appinfo error loi
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Alog.e("onFailure : "+t.getMessage());
                if (baseView == null) return;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                showDialogError(baseView.getContext().getString(R.string.s_error_generic));
            }
        });
    }

    private void showDialogError(String message) {
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) {
            SDialog.showDialog(baseView.getContext(), message, null, baseView.getContext().getString(R.string.s_ok), null, new SOnClickListener() {
                @Override
                public void onClick() {
                    ((Activity) baseView.getContext()).finish();
                }
            }).setCancelable(false);
        } else {
            if (data.getActive_mqtt().equals("1")) {
                MQTTTracker.getInstance().initMQTT();
            }
            baseView.onSuccessGetAppInfo();
        }
    }
}
