package com.s.sdk.login.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.s.sdk.R;
import com.s.sdk.base.BaseResponse;
import com.s.sdk.base.Constants;
import com.s.sdk.login.model.ResponseLoginBig4;
import com.s.sdk.login.model.UserSdkInfo;
import com.s.sdk.network.RetrofitService;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseLoginPresenter implements BaseLoginContract.Presenter {
    private BaseLoginContract.View baseView;
    private Call<BaseResponse> callGetUserInfo;
    private Call<BaseResponse> callLoginBig4;


    @Override
    public void attachView(BaseLoginContract.View view) {
        baseView = view;
    }

    @Override
    public void detachView() {
        if (callGetUserInfo != null) {
            callGetUserInfo.cancel();
        }
        if (callLoginBig4 != null) {
            callLoginBig4.cancel();
        }
        baseView = null;
    }

    @Override
    public void getUserInfo(String bodyRequest, final String accessToken) {
        baseView.showLoading(true);
        LoginService loginApi = RetrofitService.create(LoginService.class);
        callGetUserInfo = loginApi.getUserInfo(bodyRequest);
        callGetUserInfo.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse res = response.body();
                if (baseView != null) {
                    baseView.showLoading(false);
                }
                if (res == null) {
                    showErrorGeneric();
                    return;
                }
//                String signedResponse = res.getSignedRequest();
//                String plantText = UserSdkInfo.decodeResponse(signedResponse);
//                Gson gson = new Gson();
//                UserSdkInfo.UserSdkInfo resUserInfo = gson.fromJson(plantText, UserSdkInfo.UserSdkInfo.class);

                // Thong tin UserSdkInfo get cac thong tin de phuc vu
                UserSdkInfo resUserInfo = res.decodeResponse(UserSdkInfo.class);
                if (resUserInfo == null) {
                    showErrorGeneric();
                    return;
                }

     // Set token
                baseView.updateAccessToken(accessToken);
                if (resUserInfo.getLoginResult() != null) {
                    resUserInfo.getLoginResult().setAccessToken(accessToken);
                }
                baseView.onResponseGetUserInfo(resUserInfo);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (baseView != null) {
                    baseView.showLoading(false);
                }
                showErrorGeneric();
            }
        });
    }

    private void showErrorGeneric() {
        if (baseView != null) {
            Utils.showToastError(baseView.getContext());
            ((Activity) baseView.getContext()).finish();
        }
    }


    @Override
    public void loginFacebook(CallbackManager callbackManager, final String bodyRequest) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            requestLoginFb(bodyRequest, accessToken.getToken());
        } else {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    requestLoginFb(bodyRequest, loginResult.getAccessToken().getToken());
                }

                @Override
                public void onCancel() {
                    Log.e("toannt", "onCancel: disFacebook");
                    baseView.showLoading(false);
                }

                @Override
                public void onError(FacebookException error) {
                    Utils.showToast(baseView.getContext(), baseView.getContext().getString(R.string.s_error_login_fb));
                }
            });
            LoginManager.getInstance().logInWithReadPermissions((Fragment) baseView, Arrays.asList("email", "public_profile", "user_friends"));
        }
    }


    private void requestLoginFb(final String bodyRequest, String tokenFb) {
        baseView.showLoading(true);
        JSONObject object = null;
        try {
            object = new JSONObject(bodyRequest);
            object.put("big4_access_token", tokenFb);
            object.put("big4_type", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object == null) return;
        LoginService loginApi = RetrofitService.create(LoginService.class);
        String signedResquest = EncryptorEngine.encryptDataNoURLEn(object.toString(), Constants.PUBLIC_KEY);
        callLoginBig4 = loginApi.loginBig4(signedResquest);
        callLoginBig4.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                //"{\"status\":\"success\",\"error_code\":0,\"type\":\"\",\"data\":
                // {\"access_token\":\"c29hcHRva2VuMC4xNDg4NzcwMCAxNDk1MjUwMTIwKzEzODIxNTUyNzY=\",
                // \"access_token_expired\":1497842960},\"message\":\"\"}";
                if (baseView != null) baseView.showLoading(false);
                BaseResponse res = response.body();
                if (res == null) {
                    baseView.showConnectfbError(baseView.getContext().getString(R.string.s_error_generic));
                    return;
                }
                ResponseLoginBig4 responseLoginBig4 = res.decodeResponse(ResponseLoginBig4.class);
                if (responseLoginBig4 == null) {
                    baseView.showConnectfbError(baseView.getContext().getString(R.string.s_error_generic));
                    return;
                }
                if (responseLoginBig4.getStatus().equalsIgnoreCase("success")) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(bodyRequest);
                        object.put("access_token", responseLoginBig4.getData().getAccess_token());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (object == null) return;
                    String request = EncryptorEngine.encryptDataNoURLEn(object.toString(), Constants.PUBLIC_KEY);
                    getUserInfo(request, responseLoginBig4.getData().getAccess_token());
                    return;
                }

                if (responseLoginBig4.getStatus().equalsIgnoreCase("confirm_otp")) {
                    baseView.gotoConfirmOTP(responseLoginBig4);
                    return;
                }

                //con lai la loi
                LoginManager.getInstance().logOut();
                baseView.showConnectfbError(responseLoginBig4.getMessage());

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (baseView != null) {
                    baseView.showLoading(false);
                    Utils.showToastError(baseView.getContext());
                }

            }
        });
    }
}
