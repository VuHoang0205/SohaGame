package com.s.sdk.login.presenter;

import com.facebook.CallbackManager;
import com.s.sdk.base.BasePresenter;
import com.s.sdk.base.BaseView;
import com.s.sdk.login.model.ResponseLoginBig4;
import com.s.sdk.login.model.UserSdkInfo;

public interface BaseLoginContract {
    interface View extends BaseView {

        void onResponseGetUserInfo(UserSdkInfo resUserInfo);

        void gotoConfirmOTP(ResponseLoginBig4 responseLoginBig4);

        void showLoading(boolean isShow);

        void updateAccessToken(String accessToken);

        void showConnectfbError(String message);
    }

    interface Presenter extends BasePresenter<View> {
        void getUserInfo(String bodyRequest, String accessToken);

        void loginFacebook(CallbackManager callbackManager, String bodyRequest);

    }
}
