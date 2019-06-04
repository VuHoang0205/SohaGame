package com.demo.sdksoha;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.s.sdk.LoginCallback;
import com.s.sdk.LogoutCallback;
import com.s.sdk.PaymentCallback;
import com.s.sdk.SSDK;
import com.s.sdk.dashboard.view.DashBoardPopup;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.payment.model.SPaymentResult;
import com.s.sdk.utils.SPopup;
import com.squareup.picasso.Picasso;

import vn.sohagame.sdkdemo.R;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnPayment;
    private Button btnLogout;
    private Button btnLevelUp;
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvUserId;
    private TextView tvUserCoin;
    private SLoginResult loginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(2);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserId = findViewById(R.id.tvUserId);
        tvUserCoin = findViewById(R.id.tvUserCoin);
        btnLogin = findViewById(R.id.btnLogin);
        btnLevelUp = findViewById(R.id.btnLevelUp);
        btnLevelUp.setVisibility(View.GONE);
        btnPayment = findViewById(R.id.btnPayment);
        btnPayment.setVisibility(View.GONE);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);
        findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // In landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    // In portrait
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        //init soha sdk
        LogoutCallback logoutCallback = new LogoutCallback() {
            @Override
            public void onLogout() {
                onLogin();
                btnLogin.setVisibility(View.VISIBLE);
                btnPayment.setVisibility(View.GONE);
                btnLevelUp.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
                tvUserId.setText("");
                tvUserName.setText("");
            }
        };
        SSDK.getInstance().init(MainActivity.this, logoutCallback);
        onLogin();


//        //for test
//        showDialogSelectVersion();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin();
            }
        });
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPayment();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogout();
            }
        });
        btnLevelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    /**
     * login
     */
    private void onLogin() {
        SSDK.getInstance().login(MainActivity.this, new LoginCallback() {
            @Override
            public void onSuccess(SLoginResult loginResult) {
                MainActivity.this.loginResult = loginResult;
                Log.d(TAG, "onSuccess: accessToken: " + loginResult.getAccessToken());
                Log.d(TAG, "onSuccess: userID: " + loginResult.getUserId());
                btnLogin.setVisibility(View.GONE);
                btnPayment.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(loginResult.getAvatar())) {
                    Picasso.get().load(loginResult.getAvatar()).into(ivUserAvatar);
                }

                tvUserId.setText("UserID: " + loginResult.getUserId());
                tvUserName.setText("UserName: " + loginResult.getUsername());
                new GetUserCoin(loginResult, new GetUserCoin.OnEventUserCoin() {
                    @Override
                    public void onCoinUser(String coinUser) {
                        tvUserCoin.setText(coinUser);
                    }
                }).execute();
                showDialogSelectServer();
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                MainActivity.this.finish();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private void hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * payment
     */
    private void onPayment() {
        SSDK.getInstance().pay(MainActivity.this, new PaymentCallback() {

            @Override
            public void onSuccess(SPaymentResult paymentResult) {
                Log.d("mytag", "Payment success: orderid = " + paymentResult.getOrderId());
                //[Option]use to get user coin, use if you need
                new GetUserCoin(loginResult, new GetUserCoin.OnEventUserCoin() {
                    @Override
                    public void onCoinUser(String coinUser) {
                        tvUserCoin.setText(coinUser);
                    }
                }).execute();
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * Logout
     */
    private void onLogout() {
        SSDK.getInstance().logout(MainActivity.this);
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        //for testing
//        DashBoardPopup.getInstance().updatePopupRotateScreen(MainActivity.this);
//        SPopup.getInstance().updatePopupRotateScreen(MainActivity.this);
//    }

    /**
     * demo dialog select server
     */
    private void showDialogSelectServer() {
        DialogSelectServer dialog = new DialogSelectServer(MainActivity.this);
        dialog.setCallbackSelectServer(new DialogSelectServer.ICallbackSelectServer() {
            @Override
            public void onSelectItem(DialogSelectServer.RolesObject character, int areaId) {
                SSDK.getInstance().mapUserGame(MainActivity.this, String.valueOf(areaId),
                        String.valueOf(character.getRoleId()),
                        character.getRoleName(), String.valueOf(character.getRoleLevel()));
            }

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //for testing
        DashBoardPopup.getInstance().updatePopupRotateScreen(MainActivity.this);
        SPopup.getInstance().updatePopupRotateScreen(MainActivity.this);
    }
//    private void showDialogSelectVersion() {
//        DialogSelectVersion selectVersion = new DialogSelectVersion(MainActivity.this);
//        selectVersion.setCancelable(false);
//        selectVersion.setCallbackSelectVersion(new DialogSelectVersion.ICallbackSelectVersion() {
//            @Override
//            public void onSelectItem(int version) {
//                switch (version) {
//                    case 0:
//                        SSDK.getInstance().changeDomain(0);
//                        SSDK.getInstance().init(MainActivity.this, logoutCallback);
//                        onLogin();
//                        break;
//                    case 1:
//                        SSDK.getInstance().changeDomain(1);
//                        SSDK.getInstance().init(MainActivity.this, logoutCallback);
//                        onLogin();
//                        break;
//                    case 2:
//                        SSDK.getInstance().changeDomain(2);
//                        SSDK.getInstance().init(MainActivity.this, logoutCallback);
//                        onLogin();
//                        break;
//                }
//            }
//        });
//        selectVersion.show();
//    }


}
