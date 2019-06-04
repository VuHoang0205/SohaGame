package com.s.sdk.dashboard.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.s.sdk.R;
import com.s.sdk.SActivity;
import com.s.sdk.SSDK;
import com.s.sdk.base.BaseWebViewFragment;
import com.s.sdk.base.Constants;
import com.s.sdk.login.model.ResponseLoginBig4;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserSdkInfo;
import com.s.sdk.login.presenter.BaseLoginContract;
import com.s.sdk.login.presenter.BaseLoginPresenter;
import com.s.sdk.login.view.ConnectAccountPlayNowFragment;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.ImageFilePathUtils;
import com.s.sdk.utils.PermissionUtils;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashBoardDetailFragment extends BaseWebViewFragment implements BaseLoginContract.View, BaseLoginContract.Presenter {
    public static final String TAG = DashBoardDetailFragment.class.getSimpleName();
    private static final int REQUEST_CODE_READ_PERMISSION_PHOTO = 100;
    private static final int REQUEST_CODE_READ_PERMISSION_AVATAR = 101;
    private static final int REQUEST_CODE_SELECT_PHOTO = 102;
    private static final int REQUEST_CODE_SELECT_AVATAR = 103;
    private static final int SIZE_WEB_TAB = 720;
    private Gson gson = new Gson();
    private int screenWidth;
    private int screenHeight;
    private boolean isClickWeb;
    private View flParent;
    private RelativeLayout rlHeader;
    public static boolean isRefreshNotify = false;
    private BaseLoginContract.Presenter presenter;
    private CallbackManager callbackManager;

    public static DashBoardDetailFragment newInstance() {
        return new DashBoardDetailFragment();
    }

    boolean isFullScreen = true;

    @Override
    protected int getLayoutRes() {
        return R.layout.s_fragment_dashboard_detail;
    }

    @Override
    protected void onActivityCreated() {
        callbackManager = CallbackManager.Factory.create();
        presenter = new BaseLoginPresenter();
        presenter.attachView(this);
    }

//    protected void getUserInfo() {
//        getUserInfo(accessToken);
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        flParent = getView().findViewById(R.id.flParent);
        rlHeader = getView().findViewById(R.id.rl_header);
        callbackManager = CallbackManager.Factory.create();
//        updateSizeWebView();
    }


    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }

        super.onDestroy();
    }


    private void updateSizeWebView() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        final RelativeLayout.LayoutParams paramsWebview = (RelativeLayout.LayoutParams) flParent.getLayoutParams();
        if (!isCheckTab()) {
            setWHScreenWebViewPhone(paramsWebview);
        } else {
            setWHScreenWebViewTAB(paramsWebview);
        }
        //webView.setLayoutParams(paramsWebview);
    }

    //===========================>set Screen web in tablet
    private void loginFb(boolean isLogin) {
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email, public_profile, user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SLoginResult loginResult1 = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
                JSONObject jsonObject = createObjectRequestLogin(loginResult.getAccessToken().getToken());
                try {
                    jsonObject.put("connect_account", 1);
                    jsonObject.put("big4_access_token", loginResult.getAccessToken().getToken());
                    jsonObject.put("big4_type", "2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String bodyRequest = jsonObject.toString();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
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

    private void setWHScreenWebViewTAB(RelativeLayout.LayoutParams paramsWebview) {
        int widthWeb = (int) getResources().getDimension(R.dimen.s_login_width);
        int heightWeb = (int) getResources().getDimension(R.dimen.s_login_height);
        //Alog.e("isFullScreen: " + isFullScreen);
        if (heightWeb > screenHeight) {
            heightWeb = screenHeight;
        }
        if (isFullScreen) {
            isFullScreen = false;
            paramsWebview.width = widthWeb;
            paramsWebview.height = heightWeb;
            paramsWebview.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (!isClickWeb) {
                resize(0, widthWeb, heightWeb, heightWeb);
            } else {
                resize(widthWeb * 2, widthWeb, heightWeb, heightWeb);
            }
        } else {
            isFullScreen = true;
            paramsWebview.height = heightWeb;
            widthWeb = widthWeb * 2;
            if (screenWidth < widthWeb) {
                widthWeb = screenWidth;
            }

            if (screenWidth >= screenHeight) {//ngang
                resize(widthWeb / 2, widthWeb, heightWeb, heightWeb);
            } else {//doc
                paramsWebview.width = widthWeb;
                resize(widthWeb / 2, widthWeb, heightWeb, heightWeb);
            }
            isClickWeb = true;
        }
    }

    //===========================>Check if the device is a tablet???
    private boolean isCheckTab() {
        float scaleFactor = getResources().getDisplayMetrics().density;
        float widthDp = screenWidth / scaleFactor;
        float heightDp = screenHeight / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);

        return !(smallestWidth < SIZE_WEB_TAB);
    }

//===========================>set Screen web in phone

    private void setWHScreenWebViewPhone(RelativeLayout.LayoutParams paramsWebview) {
        if (isFullScreen) {
            isFullScreen = false;
            if (screenWidth >= screenHeight) {//ngang
                paramsWebview.width = screenWidth / 2;
                paramsWebview.height = ViewGroup.LayoutParams.MATCH_PARENT;
                resize(screenWidth, screenWidth / 2, screenHeight, screenHeight);
            } else {//doc
                paramsWebview.width = ViewGroup.LayoutParams.MATCH_PARENT;
                paramsWebview.height = screenHeight / 2;
                paramsWebview.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                resize(screenWidth, screenWidth, screenHeight, screenHeight / 2);
            }
        } else {
            isFullScreen = true;

            paramsWebview.width = ViewGroup.LayoutParams.MATCH_PARENT;
            paramsWebview.height = ViewGroup.LayoutParams.MATCH_PARENT;

            if (screenWidth >= screenHeight) {//ngang
                resize(screenWidth / 2, screenWidth, screenHeight, screenHeight);
            } else {//doc
                resize(screenWidth, screenWidth, screenHeight / 2, screenHeight);
            }
        }
    }

    private void resize(float fromWidth, float toWidth, float fromHeight, float toHeight) {
        Animation animation = new ResizeAnimation(flParent, fromWidth, fromHeight, toWidth, toHeight);
// this interpolator only speeds up as it keeps going
        animation.setInterpolator(new AccelerateInterpolator());
        //animation.setDuration(300);
        flParent.setAnimation(animation);
        flParent.startAnimation(animation);
    }

    @Override
    public void onResponseGetUserInfo(UserSdkInfo resUserInfo) {

    }

    @Override
    public void gotoConfirmOTP(ResponseLoginBig4 responseLoginBig4) {

    }

    @Override
    public void showLoading(boolean isShow) {

    }

    @Override
    public void updateAccessToken(String accessToken) {

    }

    @Override
    public void showConnectfbError(String message) {
        Utils.showToast(getActivity(), message);
    }

    @Override
    public void getUserInfo(String bodyRequest, String accessToken) {

    }

    @Override
    public void loginFacebook(CallbackManager callbackManager, String bodyRequest) {

    }

    @Override
    public void attachView(BaseLoginContract.View view) {

    }

    @Override
    public void detachView() {

    }

    public class ResizeAnimation extends Animation {
        private View mView;
        private float mToHeight;
        private float mFromHeight;

        private float mToWidth;
        private float mFromWidth;

        ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height =
                    (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            ViewGroup.LayoutParams p = mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            mView.requestLayout();
        }

    }

//    private void resizeWithAnimation(int currentHeight, int newHeight) {
//        // view we want to animate
//
//// set the values we want to animate between and how long it takes
//// to run
//        ValueAnimator slideAnimator = ValueAnimator
//                .ofInt(currentHeight, newHeight)
//                .setDuration(300);
//
//// we want to manually handle how each tick is handled so add a
//// listener
//        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                // get the value the interpolator is at
//                Integer value = (Integer) animation.getAnimatedValue();
//                // I'm going to set the layout's height 1:1 to the tick
//                webView.getLayoutParams().height = value.intValue();
//                // force all layouts to see which ones are affected by
//                // this layouts height change
//                webView.requestLayout();
//            }
//        });
//
//// create a new animationset
//        AnimatorSet set = new AnimatorSet();
//// since this is the only animation we are going to run we just use
//// play
//        set.play(slideAnimator);
//// this is how you set the parabola which controls acceleration
//        set.setInterpolator(new AccelerateDecelerateInterpolator());
//// start the animation
//        set.start();
//    }


    private void updateSizeWebViewRotate() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        final RelativeLayout.LayoutParams paramsWebview = (RelativeLayout.LayoutParams) flParent.getLayoutParams();

        if (!isCheckTab()) {
            if (isFullScreen) {
                paramsWebview.width = ViewGroup.LayoutParams.MATCH_PARENT;
                paramsWebview.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                if (screenWidth >= screenHeight) {//ngang
                    paramsWebview.width = screenWidth / 2;
                    paramsWebview.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {//doc
                    paramsWebview.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    paramsWebview.height = screenHeight / 2;
                    paramsWebview.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }
            }
            flParent.setLayoutParams(paramsWebview);
        } else {
            isFullScreen = !isFullScreen;
            setWHScreenWebViewTAB(paramsWebview);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateSizeWebViewRotate();
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            updateSizeWebViewRotate();
        }
    }

//    public void checkYorientationWeb() {
//        if (webView.getY() < 0) {
//            Alog.e("y rote: " + webView.getY());
//        }
//    }

    @Override
    protected String getURLRequest() {
        String urlIntent = getActivity().getIntent().getStringExtra(Constants.BUNDLE_EXTRA_DATA_2);
        String signedRes = EncryptorEngine.encryptDataNoURLEn(Utils.createDefaultParams(getActivity()).toString(),
                Constants.PUBLIC_KEY);

        try {
            return urlIntent + "?signed_request=" + URLEncoder.encode(signedRes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(String url) {
        return false;
    }

    @Override
    protected void onReceivedError(int errorCode, String description, String failingUrl) {

    }

    @Override
    protected void onPageStarted(String url) {

    }

    @Override
    protected void onPageFinished(String url) {
        rlHeader.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("localStorage.setItem('seen','" + gson.toJson(PrefUtils.getListSeenId()) + "');", null);
        } else {
            webView.loadUrl("javascript:localStorage.setItem('seen','" + gson.toJson(PrefUtils.getListSeenId()) + "');");
        }
    }

    @Override
    protected void onJavaScriptInteract(String method, String value) {
        if (method.equalsIgnoreCase("logout")) {
            finishActivity();
            SSDK.getInstance().logoutNoMessage();
            return;
        }
        if (method.equalsIgnoreCase("close_popup")) {
            finishActivity();
            return;
        }
        if (method.equalsIgnoreCase("select_photo")) {
            boolean permission = PermissionUtils.requestPermissionFragment(DashBoardDetailFragment.this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_READ_PERMISSION_PHOTO);
            if (permission) {
                selectPhoto();
            }

            return;
        }
        if (method.equalsIgnoreCase("select_avatar")) {
            boolean permission = PermissionUtils.requestPermissionFragment(DashBoardDetailFragment.this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_READ_PERMISSION_AVATAR);
            if (permission) {
                selectAvatar();
            }

            return;
        }
        if (method.equalsIgnoreCase("onclick_back")) {
            finishActivity();
            return;
        }
        if (method.equalsIgnoreCase("connect_account")) {
            gotoConnectAccountPlayNow();
            return;
        }
        if ("makeFullScreen".equalsIgnoreCase(method)) {
            updateSizeWebView();
            return;
        }
        if ("connect_account_facebook".equalsIgnoreCase(method)) {
//            loginFb(true);
            Intent i = new Intent(getActivity(), SActivity.class);
            i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_CONNECT_ACCOUNT_FB_FROM_POPUP);
            if (getActivity() != null) {
                getActivity().startActivity(i);
            }
            finishActivity();
            return;
        }
        if (method.equalsIgnoreCase("seen")) {
            List<String> listSeen = PrefUtils.getListSeenId();
            listSeen.add(value);
            PrefUtils.addSeenId(listSeen);
        }

    }

    //    private void gotoBrowser(String url) {
//        if (TextUtils.isEmpty(url)) return;
//        try {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//        } catch (Exception e) {
//        }
//    }
    private void connectFbAccount() {

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

    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_PHOTO);
    }

    private void selectAvatar() {
        Alog.e("selectAvatar()");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), REQUEST_CODE_SELECT_AVATAR);
    }

    @Override
    public void onBackPressed() {
//        webView.loadUrl("javascript:onclick_back()");
        finishActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_PHOTO:
                List<String> path = new ArrayList<>();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        String link = ImageFilePathUtils.getPath(getActivity(), imageUri);
                        path.add(link);
                        currentItem++;
                    }
                } else if (data.getData() != null) {
                    String imagePath = ImageFilePathUtils.getPath(getActivity(),
                            data.getData());
                    path.add(imagePath);
                }
                passPhotoToWebView(path);
                break;
            case REQUEST_CODE_SELECT_AVATAR:
                String pathAvatar = ImageFilePathUtils.getPath(getActivity(), data.getData());
                passAvatarToWebView(pathAvatar);
                break;
        }
    }

    private void passAvatarToWebView(String url) {
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        if (bitmap != null) {
            String imageString = imageToString(bitmap);
            String content = "javascript: returnSelectedAvatar(['data:image/png;base64,"
                    + imageString + "'])";
            webView.loadUrl(content);
        } else {
            Utils.showToast(getActivity(), getString(R.string.s_error_dashboard_detail_pick_image));
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        double ratio = (bitmap.getWidth() * 1.0) / bitmap.getHeight();
        if (bitmap.getWidth() > 480) {
            int width = 300;
            int height = (int) (300 / ratio);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    private void passPhotoToWebView(List<String> url) {
        String content = "javascript: returnSelectedPhoto([";
        for (int i = 0; i < url.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(url.get(i));
            String imageString;
            if (bitmap != null) {
                imageString = imageToString(bitmap);
            } else {
                imageString = "";
                Utils.showToast(getActivity(), getString(R.string.s_error_dashboard_detail_pick_image));
            }
            if (i != url.size() - 1) {
                content = content + "'data:image/png;base64," + imageString + "',";
            } else {
                content = content + "'data:image/png;base64," + imageString + "'])";
            }
        }
        webView.loadUrl(content);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_PERMISSION_PHOTO:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhoto();
                }

                break;
            case REQUEST_CODE_READ_PERMISSION_AVATAR:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectAvatar();
                }
                break;
        }
    }

    private void finishActivity() {
        //refresh notify db
        isRefreshNotify = true;
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
