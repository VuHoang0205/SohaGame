package com.s.sdk.login.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.base.BaseWebViewFragment;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.login.model.ResponseLoginBig4;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.presenter.BaseLoginContract;
import com.s.sdk.login.presenter.BaseLoginPresenter;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseLoginFragment extends BaseWebViewFragment implements BaseLoginContract.View {
    private BaseLoginContract.Presenter presenter;
    private CallbackManager callbackManager;

    //use when getuserinfo fail, need to retry request
    private String accessToken;
    private int styleLogin;

    @Override
    protected int getLayoutRes() {
        return R.layout.s_fragment_login;
    }

    @Override
    protected void onActivityCreated() {
        callbackManager = CallbackManager.Factory.create();
        presenter = new BaseLoginPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) presenter.detachView();
        if (webView != null) {
            webView.stopLoading();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Alog.e("click back");
        webView.loadUrl("javascript:onclick_back()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onChangeConfig(getResources().getConfiguration());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onChangeConfig(newConfig);
    }

    private void onChangeConfig(Configuration newConfig) {
        Activity activity = getActivity();
        if (activity == null) return;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void showLoading(boolean isShow) {
        if (isShow) {
            progressLoading.setVisibility(View.VISIBLE);
        } else {
            progressLoading.setVisibility(View.GONE);
            if (styleLogin == Constants.LOGIN_POPUP) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void getUserInfo() {
        getUserInfo(accessToken);
    }

    protected void getUserInfo(String mAccessToken) {
        String plantextRequest = createObjectRequestLogin(mAccessToken).toString();
        String request = EncryptorEngine.encryptDataNoURLEn(plantextRequest, Constants.PUBLIC_KEY);
        presenter.getUserInfo(request, mAccessToken);
    }

    protected JSONObject createObjectRequestLogin(String accessToken) {
        JSONObject jsonObject = createObjectRequest();
        try {
            jsonObject.put("access_token", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(String url) {
        if (url.contains("access_token")) {
            Uri uri = Uri.parse(url);
            updateAccessToken(uri.getQueryParameter("access_token"));
            getUserInfo();
            return true;
        }
        return false;
    }

    @Override
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected void onReceivedError(int errorCode, String description, final String failingUrl) {
        Dialog dialog = SDialog.showDialog(getActivity(), getString(R.string.s_error_generic), getString(R.string.s_cancel), getString(R.string.s_try_again),
                new SOnClickListener() {
                    @Override
                    public void onClick() {//
                        finishActivity();
                    }
                }, new SOnClickListener() {//retry
                    @Override
                    public void onClick() {
                        webView.loadUrl(failingUrl);
                    }
                });
        dialog.setCancelable(false);
    }

    private void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    protected void onPageStarted(String url) {

    }

    @Override
    protected void onPageFinished(String url) {
        // webView.loadUrl("javascript:document.getElementsByName('email')[0].focus();");
    }

    @Override
    protected void onJavaScriptInteract(String method, String value) {
        if (method.equalsIgnoreCase("LoginFB")) {
            loginFb(true);
        } else if (method.equalsIgnoreCase("ConnectLoginFB")) {
            loginFb(false);
        } else if (method.equalsIgnoreCase("close_popup")) {
            onClose();
        } else if (method.equals("onclick_back")) {
            if (value.equalsIgnoreCase("0")) {
                if (this instanceof LoginFragment) {
                    SDialog.showDialog(getActivity(), getString(R.string.s_dialog_exit_game), getString(R.string.s_ok), getString(R.string.s_cancel),
                            new SOnClickListener() {
                                @Override
                                public void onClick() {//
                                    finishActivity();
                                    SCallback callback = com.s.sdk.CallbackManager.getLoginCallback();
                                    if (callback != null) {
                                        callback.onCancel();
                                    }
                                }
                            }, new SOnClickListener() {
                                @Override
                                public void onClick() {
                                }
                            });

                } else {
                    finishActivity();
                }
            }
        }
//        else if (method.equals("onclick_clear_text")) {
//            Alog.d("onclick_clear_text");
//            webView.loadUrl("javascript:document.getElementsByName('email')[0].focus();");
//            webView.loadUrl("javascript:document.getElementsByName('password')[0].focus();");
//        }
    }

    protected void loginFb(boolean isLogin) {
        Log.e("toannt", "isLogin: " + isLogin);
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        String accessToken = "";
        if (loginResult != null) {
            accessToken = loginResult.getAccessToken();
        }
        JSONObject jsonObject = createObjectRequestLogin(accessToken);
        if (!isLogin) {
            LoginManager.getInstance().logOut();
            try {
                jsonObject.put("connect_account", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String bodyRequest = jsonObject.toString();
        presenter.loginFacebook(callbackManager, bodyRequest);
    }

    public void setStyleLogin(int styleLogin) {
        this.styleLogin = styleLogin;
    }

    private void onClose() {
        if (getActivity() == null) return;
//        FragmentManager manager = getActivity().getSupportFragmentManager();
//        if (manager.getBackStackEntryCount() > 0) {
//            manager.popBackStack();
//        } else {
//            finishActivity();
//        }
        finishActivity();
    }

    @Override
    public void gotoConfirmOTP(ResponseLoginBig4 responseLoginBig4) {
        webView.loadUrl(Constants.URL_LOGIN + createParamsLoginOTP(responseLoginBig4));
    }

    private String createParamsLoginOTP(ResponseLoginBig4 responseLoginBig4) {
        JSONObject objectRequest = createObjectRequestLogin("");
        try {
            objectRequest.put("confirm_otp", "1");
            objectRequest.put("otp_token", responseLoginBig4.getOtp_token());
            objectRequest.put("message", responseLoginBig4.getMessage());
            objectRequest.put("syntax", responseLoginBig4.getSyntax());
            objectRequest.put("phone_number", responseLoginBig4.getPhone_number());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "signed_request=" + EncryptorEngine.encryptData(objectRequest.toString(), Constants.PUBLIC_KEY);
    }


}
