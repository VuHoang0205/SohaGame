package com.s.sdk.login.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.SSDK;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.fcm.FCMRequest;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserSdkInfo;
import com.s.sdk.login.presenter.LoginContract;
import com.s.sdk.login.presenter.LoginPresenter;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.Utils;

public class LoginFragment extends BaseLoginFragment implements LoginContract.View {
    public static final String TAG = LoginFragment.class.getName();
    private boolean isAutoLogin = false;

    private LoginContract.Presenter presenter;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getURLRequest() {
        //test return "https://soap.soha.vn/dialog/webview/loginv2";

        // Check Accset token
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        //String accessToken = "";
        if (loginResult != null) {//auto login
            isAutoLogin = true;
            //accessToken = loginResult.getAccessToken();
            return null;
        }
        Log.e(">>>>",Constants.URL_LOGIN + signRequest());
        return Constants.URL_LOGIN + signRequest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isAutoLogin) {
            finishActivity();
        }
    }

    private void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    private String signRequest() {
        return "signed_request=" + EncryptorEngine.encryptData(createObjectRequestLogin("").toString(), Constants.PUBLIC_KEY);
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MQTTTracker.pendingActionOpenApp = true;
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Alog.d("LoginFragment onActivityCreated");
        STracker.trackEvent("sdk", STracker.ACTION_LOGIN_OPEN, "");
        presenter = new LoginPresenter();
        presenter.attachView(this);

        // 1. Api get appInfo
        presenter.getAppInfo();

//        //test firebase
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "iddd");
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "nameee");
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onDestroy() {
//        MQTTTracker.pendingActionOpenApp = true;
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }

    // Get UserSdkInfo phục vị SDK

    @Override
    public void onResponseGetUserInfo(final UserSdkInfo resUserInfo) {
        if (resUserInfo.getStatus().equals("success")) {
            //send tracking
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    STracker.trackEvent("sdk", STracker.ACTION_LOGIN_SUCCESS, "");
                }
            }, 200);

            //check update ban cap nhat
            UserSdkInfo.Update update = resUserInfo.getUpdate();
            if (update != null && update.getStatus().equals("1")) {
                if (update.getForce().equals("0")) {//don't need force update
                    SDialog.showDialogUpdate(getActivity(), false, update.getLink(), new SOnClickListener() {
                        @Override
                        public void onClick() {
                            handleLoginSuccess(resUserInfo);
                        }
                    });
                } else { //force update
                    SDialog.showDialogUpdate(getActivity(), true, update.getLink(), null);
                }
                return;
            }
            handleLoginSuccess(resUserInfo);
            return;
        }

        if (resUserInfo.getStatus().equals("notice")) {
            showDialogNotice(resUserInfo);
            return;
        }

        //con lai la loi
        PrefUtils.putObject(Constants.PREF_LOGIN_RESULT, null);
        Utils.showToast(getActivity(), resUserInfo.getMessage());
        if (isAutoLogin) {
            finishActivity();
            //calback result login
            callbackLoginError();
        }
    }

    @Override
    public void showConnectfbError(String message) {
        Utils.showToast(getActivity(), message);
    }

    private void showDialogNotice(UserSdkInfo resUserInfo) {
        String messCancel = null;
        String messOk = null;

        if (resUserInfo.getLogout().equals("1")) {
            messCancel = getString(R.string.s_exit);
        }
        if (resUserInfo.getRetry().equals("1")) {
            messOk = getString(R.string.s_try_again);
        }


        SDialog.showDialog(getActivity(), resUserInfo.getMessage(), messCancel, messOk,
                new SOnClickListener() {//click cancel
                    @Override
                    public void onClick() {
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

    private void handleLoginSuccess(UserSdkInfo resUserInfo) {
        SLoginResult loginResult = resUserInfo.getLoginResult();
        if (loginResult.getNew_user().equals("1")) {
            STracker.trackEvent("sdk", STracker.ACTION_NEW_USER, "");
        }

        //show dialog connect account play now
        if (loginResult.getType_user().equalsIgnoreCase("play_now")) {
            SLoginResult loginResultPref = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
            if (loginResultPref != null && loginResult.getAccessToken() != null) {

                // Save data oject to sharea
                PrefUtils.putObject(Constants.PREF_LOGIN_RESULT, loginResult);
                showDialogConnectAccount(loginResult);
                return;
            }

        }
        //calback result login
        PrefUtils.putObject(Constants.PREF_LOGIN_RESULT, loginResult);
        finishActivity();
        callbackLoginSuccess(loginResult);
        String oldUserId = PrefUtils.getString(Constants.PREF_USER_ID_OLD);
        if (!oldUserId.equals(loginResult.getUserId())) {
            PrefUtils.putString(Constants.PREF_USER_ID_OLD, loginResult.getUserId());
            PrefUtils.putBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
   // Check UserId rồi gọi Api register device to server de push noti
            sendTokenFCM(getContext());
        } else {
            boolean isSuccess = PrefUtils.getBoolean(Constants.PREF_IS_SEND_PUSH_NOTIFY_SUCCESS, false);
            if (!isSuccess) {
                sendTokenFCM(getContext());
            }
        }
    }

    // Send token to server register
    private void sendTokenFCM(final Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        Alog.e("getInstanceId isSuccessful " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            //Log.w("mytag", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Alog.e("token_fcm 0: " + token);
                        FCMRequest.sendRegistrationToServer(context, token);
                    }
                });


    }

    private void showDialogConnectAccount(final SLoginResult loginResult) {
        SDialog.showDialog(getActivity(), getString(R.string.s_login_des_connect_account),
                getString(R.string.s_login_giveup),
                getString(R.string.s_login_connect_account),
                new SOnClickListener() {
                    @Override
                    public void onClick() {
                        finishActivity();
                        callbackLoginSuccess(loginResult);
                    }
                }, new SOnClickListener() {
                    @Override
                    public void onClick() {
                        gotoConnectAccountPlayNow();
                        callbackLoginSuccess(loginResult);
                    }
                }
        ).setCancelable(false);
    }

    private void gotoConnectAccountPlayNow() {
        Fragment fragment = ConnectAccountPlayNowFragment.newInstance();
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.sContainer, fragment, ConnectAccountPlayNowFragment.TAG)
                .addToBackStack(ConnectAccountPlayNowFragment.TAG)
                .commit();
    }

    private void callbackLoginSuccess(SLoginResult loginResult) {
        SCallback<SLoginResult> callback = CallbackManager.getLoginCallback();
        if (callback != null) {
            callback.onSuccess(loginResult);
        }
        showToastHello(loginResult.getUsername());
    }

    private void callbackLoginError() {
        SCallback<SLoginResult> callback = CallbackManager.getLoginCallback();
        if (callback != null) {
            callback.onError();
        }
    }

    private void showToastHello(String text) {
        if (getActivity() == null) return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return;
        final View layout = inflater.inflate(R.layout.s_login_toast_hello, null);
        TextView tvHello = layout.findViewById(R.id.tvHello);
        tvHello.setText(String.format(getString(R.string.s_login_hello), text));
        Toast toast = new Toast(SContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    // Get cac thong tin phuc vu sdk ham nay dc goi ben BaseLoginFragment
    @Override
    public void onSuccessGetAppInfo() {
        if (isAutoLogin) {
            SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
            getUserInfo(loginResult.getAccessToken());
        }
    }

}
