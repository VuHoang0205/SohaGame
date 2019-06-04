package com.s.sdk.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.view.LoginFragment;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.Utils;

import java.util.List;


/**
 * Created by LEGEND on 11/9/2017.
 */

public abstract class BaseActivity extends FragmentActivity {
    private IntentFilter mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION/*"android.net.conn.CONNECTIVITY_CHANGE"*/);
    private Dialog dialogNetwork;

    /**
     * @return id view
     */
    protected abstract @LayoutRes
    int getLayoutView();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        try {
            registerReceiver(mBroadcastReceiver, mIntentFilter);
        } catch (Exception e) {
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mBroadcastReceiver != null) {
            try {
                unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // activity transition animation
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Utils.isOnline(BaseActivity.this)) {
                showDialogNetworkError();
            }
        }
    };

    protected void showDialogNetworkError() {
        if (dialogNetwork == null) {
            dialogNetwork = SDialog.showDialog(BaseActivity.this, getString(R.string.please_check_connect_internet), getString(R.string.s_cancel),
                    getString(R.string.s_try_again), new SOnClickListener() {
                        @Override
                        public void onClick() {//click huy
                            BaseActivity.this.finish();
                            if (getCurrentFragment() != null && getCurrentFragment() instanceof LoginFragment && getCurrentFragment().isVisible()) {
                                callbackLoginCancel();
                            }
                        }
                    },
                    new SOnClickListener() {//click thu lai
                        @Override
                        public void onClick() {
                            clickConnectAgain();
                        }
                    });
            dialogNetwork.setCancelable(false);
        } else {
            //wait dialog dissmiss
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Log.e("mytag", "dialogNetwork !=null" + dialogNetwork.isShowing());
                    if (!dialogNetwork.isShowing()) {
                        dialogNetwork.show();
                    }
                }
            }, 500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void clickConnectAgain() {

    }

    private void callbackLoginCancel() {
        SCallback<SLoginResult> callback = CallbackManager.getLoginCallback();
        if (callback != null) {
            callback.onCancel();
        }
    }

    protected Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = null;
        if (fragmentManager.getBackStackEntryCount() > 1) {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        } else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                currentFragment = fragments.get(0);
            }
        }
        return currentFragment;
    }

}
