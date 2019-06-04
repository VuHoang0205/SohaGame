package com.s.sdk;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.s.sdk.base.BaseActivity;
import com.s.sdk.base.BaseFragment;
import com.s.sdk.base.Constants;
import com.s.sdk.dashboard.view.DashBoardDetailFragment;
import com.s.sdk.dashboard.view.DashBoardDialog;
import com.s.sdk.dashboard.view.DashBoardPopup;
import com.s.sdk.login.view.BaseLoginFragment;
import com.s.sdk.login.view.ConnectAccountPlayNowFragment;
import com.s.sdk.login.view.LoginFragment;
import com.s.sdk.login.view.WarningFragment;
import com.s.sdk.payment.view.PaymentFragment;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.SPopup;
import com.s.sdk.utils.Utils;

public class SActivity extends BaseActivity {
    public static final int ACTION_LOGIN = 1;
    public static final int ACTION_PAYMENT = 2;

    public static final int ACTION_SHOW_WARNING = 3;
    public static final int ACTION_CONNECT_ACCOUNT_PLAYNOW = 4;
    public static final int ACTION_SHOW_DETAIL_DASHBOARD = 5;
    public static final int ACTION_CONNECT_ACCOUNT_FB_FROM_POPUP = 6;


    @Override
    protected int getLayoutView() {
        return R.layout.s_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.s_transparent_status));
        }

//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //onChangeConfig(getResources().getConfiguration());
        onCreateActivity();
    }

    private void onCreateActivity() {
        if (!Utils.isOnline(SActivity.this)) {
            showDialogNetworkError();
            return;
        }
        int action = getIntent().getIntExtra(Constants.BUNDLE_EXTRA_DATA, ACTION_LOGIN);
        int styleLogin = getIntent().getIntExtra(Constants.KEY_STYLE_LOGIN, 0);
        switch (action) {
            case ACTION_LOGIN:
                gotoLogin();
                break;
            case ACTION_PAYMENT:
                gotoPayment();
                break;
            case ACTION_SHOW_WARNING:
                gotoShowWarning();
                break;
            case ACTION_CONNECT_ACCOUNT_PLAYNOW:
                gotoConnectAccountPlayNow(false, styleLogin);
                break;
            case ACTION_SHOW_DETAIL_DASHBOARD:
                gotoDetailDashBoard();
                break;
            case ACTION_CONNECT_ACCOUNT_FB_FROM_POPUP:
                gotoConnectAccountPlayNow(true, styleLogin);
                break;

        }
    }


    private void gotoLogin() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(LoginFragment.TAG);

        if (fragment == null) {
            fragment = LoginFragment.newInstance();
            fragment.setRetainInstance(true);
            manager.beginTransaction()
                    .add(R.id.sContainer, fragment, LoginFragment.TAG)
                    //.addToBackStack(LoginFragment.TAG)
                    .commit();
        }
        //fragmentLogin = fragment;
    }


    private void gotoPayment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(PaymentFragment.TAG);

        if (fragment == null) {
            fragment = PaymentFragment.newInstance();
            fragment.setRetainInstance(true);
            manager.beginTransaction()
                    .add(R.id.sContainer, fragment, PaymentFragment.TAG)
                    //.addToBackStack(PaymentFragment.TAG)
                    .commit();
        }
    }

    private void gotoShowWarning() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(WarningFragment.TAG);

        if (fragment == null) {
            fragment = WarningFragment.newInstance();
            fragment.setRetainInstance(true);
            manager.beginTransaction()
                    .add(R.id.sContainer, fragment, WarningFragment.TAG)
                    //.addToBackStack(LoginFragment.TAG)
                    .commit();
        }
    }

    private void gotoConnectAccountPlayNow(boolean isConnectFb, int styleLogin) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(ConnectAccountPlayNowFragment.TAG);
        if (fragment == null) {
            fragment = ConnectAccountPlayNowFragment.newInstance(isConnectFb, styleLogin);
//            ((ConnectAccountPlayNowFragment) fragment).setConnectAccountPlayNowListener(new ConnectAccountPlayNowFragment.ConnectAccountPlayNowListener() {
//                @Override
//                public void onConnectSuccess() {
//                    SCallback<Object> callback = CallbackManager.getLogoutCallback();
//                    if (callback != null) {
//                        callback.onSuccess(null);
//                    }
//                }
//            });
            fragment.setRetainInstance(true);
            manager.beginTransaction()
                    .add(R.id.sContainer, fragment, ConnectAccountPlayNowFragment.TAG)
                    //.addToBackStack(LoginFragment.TAG)
                    .commit();
        }
    }

    private void gotoDetailDashBoard() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(DashBoardDetailFragment.TAG);

        if (fragment == null) {
            fragment = DashBoardDetailFragment.newInstance();
            fragment.setRetainInstance(true);
            manager.beginTransaction()
                    .add(R.id.sContainer, fragment, DashBoardDetailFragment.TAG)
                    //.addToBackStack(LoginFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        DashBoardPopup.getInstance().showPopup();
        SPopup.getInstance().showPopupWarning();
        if (DashBoardDialog.isClickGotoDetailDB) {
            DashBoardDialog.isClickGotoDetailDB = false;
            STracker.trackEvent("sdk", STracker.ACTION_CLOSE_DB, "");
        }
        super.onDestroy();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        onChangeConfig(newConfig);
//    }
//    private void onChangeConfig(Configuration newConfig){
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        }
//    }

    @Override
    protected void clickConnectAgain() {
        super.clickConnectAgain();
        onCreateActivity();
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() != null && (getCurrentFragment() instanceof BaseLoginFragment || getCurrentFragment() instanceof DashBoardDetailFragment) && getCurrentFragment().isVisible()) {
            ((BaseFragment) getCurrentFragment()).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
