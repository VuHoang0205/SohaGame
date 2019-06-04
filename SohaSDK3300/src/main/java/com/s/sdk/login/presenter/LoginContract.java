package com.s.sdk.login.presenter;

import com.s.sdk.base.BasePresenter;
import com.s.sdk.base.BaseView;

public interface LoginContract {
    interface View extends BaseView {

        void onSuccessGetAppInfo();
    }

    interface Presenter extends BasePresenter<View> {
        void getAppInfo();
    }

}
