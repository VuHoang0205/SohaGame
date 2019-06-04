package com.s.sdk.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.s.sdk.R;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public abstract class BaseWebViewFragment extends BaseFragment {
    protected WebView webView;
    protected ProgressBar progressLoading;
    private String accessToken;

    protected abstract void onActivityCreated();

    protected abstract String getURLRequest();

    protected abstract boolean onShouldOverrideUrlLoading(String url);

    protected abstract void onReceivedError(int errorCode,
                                            String description, String failingUrl);

    protected abstract void onPageStarted(String url);

    protected abstract void onPageFinished(String url);

    protected abstract void onJavaScriptInteract(String method, String value);

    protected abstract @LayoutRes
    int getLayoutRes();

    protected JSONObject createObjectRequestLogin(String accessToken) {
        JSONObject jsonObject = createObjectRequest();
        try {
            jsonObject.put("access_token", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onActivityCreated();
        webView = getView().findViewById(R.id.webView);
        progressLoading = getView().findViewById(R.id.progressLoading);
        //ensure device_id inited
        String device_id = PrefUtils.getString(Constants.PREF_ADS_ID_GG);
        if (TextUtils.isEmpty(device_id)) {
            getAdvertisingIdClient();
        } else {
            setUpWebView(getURLRequest());
        }
    }


    private void getAdvertisingIdClient() {
        final String deviceIdVcc = Utils.getDeviceIDVCC(SContext.getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                AdvertisingIdClient.Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(SContext.getApplicationContext());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (adInfo != null) {
                    String idAds = adInfo.getId();
                    PrefUtils.putString(Constants.PREF_ADS_ID_GG, idAds);
                } else {
                    PrefUtils.putString(Constants.PREF_ADS_ID_GG, deviceIdVcc);
                }
                setUpWebView(getURLRequest());
            }
        }).start();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void setUpWebView(String url) {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new DialogWebViewClient());
//        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
        //for test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.addJavascriptInterface(new JavaScriptInterface(), "JavaScriptInterface");
        webView.loadUrl(url);
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        if (url != null) {
            Log.e("URL_WEBVIEW", url + " ");
        }
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (!v.hasFocus()) {
//                    v.requestFocus();
//                }
//                return false;
//            }
//        });

    }

    protected JSONObject createObjectRequest() {
        return Utils.createDefaultParams(getActivity());
    }


    private class DialogWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Alog.e("shouldOverrideUrlLoading Redirect URL: " + url);
            return BaseWebViewFragment.this.onShouldOverrideUrlLoading(url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, final String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("mytag", "onReceivedError: " + failingUrl);
            Utils.showToastError(getActivity());
            BaseWebViewFragment.this.onReceivedError(errorCode, description, failingUrl);
            webView.loadUrl("");
        }

//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Alog.e("Webview onPageStarted: " + url);
            super.onPageStarted(view, url, favicon);
            BaseWebViewFragment.this.onPageStarted(url);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onPageFinished(WebView view, String url) {
            Alog.e("Webview onPageFinished: " + url);
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.GONE);
            webView.loadUrl("javascript:function AppSDKexecute(method,value) {JavaScriptInterface.javaScriptInteract(method,value);}");
            BaseWebViewFragment.this.onPageFinished(url);
        }
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void javaScriptInteract(final String method, final String value) {
            Alog.e("LoginJavaScriptInterface: " + method + "//" + value);
            //ensure run on uithread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseWebViewFragment.this.onJavaScriptInteract(method, value);
                }
            });
        }
    }
}
