package com.s.sdk.login.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.SSDK;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.dashboard.view.DashBoardPopup;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserSdkInfo;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.SPopup;
import com.s.sdk.utils.Utils;

public class ConnectAccountPlayNowFragment extends BaseLoginFragment {
    public static final String TAG = ConnectAccountPlayNowFragment.class.getName();
    private static final String KEY_IS_CONNECT_FB = "KEY_IS_CONNECT_FB";
    private ConnectAccountPlayNowListener connectAccountPlayNowListener;


    public static ConnectAccountPlayNowFragment newInstance() {
        Bundle args = new Bundle();
        ConnectAccountPlayNowFragment fragment = new ConnectAccountPlayNowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ConnectAccountPlayNowFragment newInstance(boolean isConnectFb, int style) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_CONNECT_FB, isConnectFb);
        args.putInt(Constants.KEY_STYLE_LOGIN, style);
        ConnectAccountPlayNowFragment fragment = new ConnectAccountPlayNowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getURLRequest() {
        boolean isConnectFb = getArguments().getBoolean(KEY_IS_CONNECT_FB, false);
        if (isConnectFb) {
            return null;
        }
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        String accessToken = "";
        if (loginResult != null) {
            accessToken = loginResult.getAccessToken();
        }
        return Constants.URL_LOGIN + signRequest(accessToken) + "&connect_account=1";
    }

    private String signRequest(String accessToken) {
        return "signed_request=" + EncryptorEngine.encryptData(createObjectRequestLogin(accessToken).toString(), Constants.PUBLIC_KEY);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DashBoardPopup.getInstance().hidePopup();
        SPopup.getInstance().hidePopupWarning();
        SPopup.getInstance().hidePopupConnectAccount();
        boolean isConnectFb = getArguments().getBoolean(KEY_IS_CONNECT_FB, false);
        int styleLogin = getArguments().getInt(Constants.KEY_STYLE_LOGIN);
        if (isConnectFb) {
            loginFb(false);
            setStyleLogin(styleLogin);
        }
    }

    @Override
    public void onDestroy() {
        SPopup.getInstance().showPopupConnectAccount();
        super.onDestroy();
    }

    @Override
    public void onResponseGetUserInfo(final UserSdkInfo resUserInfo) {
        Utils.showToast(getActivity(), getString(R.string.s_login_connect_account_success));
        SPopup.getInstance().clearPopupConnectAccount();
        if (resUserInfo.getStatus().equals("success")) {
            //check update
            UserSdkInfo.Update update = resUserInfo.getUpdate();
            if (update != null && update.getStatus().equals("1")) {
                if (update.getForce().equals("0")) {//don't need force update
                    SDialog.showDialogUpdate(getActivity(), false, update.getLink(), new SOnClickListener() {
                        @Override
                        public void onClick() {
                            handleGetUserSuccess(resUserInfo);
                        }
                    });
                } else { //force update
                    SDialog.showDialogUpdate(getActivity(), true, update.getLink(), null);
                }
                return;
            }


            handleGetUserSuccess(resUserInfo);
            return;
        }

        if (resUserInfo.getStatus().equals("notice")) {
            showDialogNotice(resUserInfo);
            return;
        }

        //con lai la loi
        Utils.showToast(getActivity(), resUserInfo.getMessage());
    }

    private void showDialogNotice(UserSdkInfo resUserInfo) {
        String messCancel = null;
        String messOk = null;

        if (resUserInfo.getRetry().equals("1")) {
            messOk = getString(R.string.s_try_again);
        }
        if (resUserInfo.getLogout().equals("1")) {
            messCancel = getString(R.string.s_exit);
        }


        SDialog.showDialog(getActivity(), resUserInfo.getMessage(), messCancel, messOk,
                new SOnClickListener() {//click cancel
                    @Override
                    public void onClick() {
                        finishActivity();
                        SSDK.getInstance().logoutNoMessageNotCallback();
                        SCallback<SLoginResult> callback = CallbackManager.getLoginCallback();
                        if (callback != null) {
                            callback.onCancel();
                        }
                    }
                }, new SOnClickListener() {//click retry
                    @Override
                    public void onClick() {
                        getUserInfo();
                    }
                }).setCancelable(false);
    }

    private void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void showConnectfbError(String message) {
        Utils.showToast(getActivity(), message);
        if (getArguments() != null && getArguments().getBoolean(KEY_IS_CONNECT_FB, false)) {
            finishActivity();
        }
    }

    private void handleGetUserSuccess(UserSdkInfo resUserInfo) {
        SLoginResult loginResult = resUserInfo.getLoginResult();
        PrefUtils.putObject(Constants.PREF_LOGIN_RESULT, loginResult);
        if ("1".equals(loginResult.getNew_user())) {
            STracker.trackEvent("sdk", STracker.ACTION_NEW_USER, "");
        }
        //TODO show hello dialog (ignore)
        if (connectAccountPlayNowListener != null) {
            connectAccountPlayNowListener.onConnectSuccess();
        }
        finishActivity();
    }

//    private void callbackLogin(SLoginResult loginResult) {
//        SCallback callback = CallbackManager.getLoginCallback();
//        if (callback != null) {
//            callback.onSuccess(loginResult);
//        }
//         finishActivity();
//    }

    //setter
    public void setConnectAccountPlayNowListener(ConnectAccountPlayNowListener connectAccountPlayNowListener) {
        this.connectAccountPlayNowListener = connectAccountPlayNowListener;
    }


    public interface ConnectAccountPlayNowListener {
        void onConnectSuccess();
    }
}
