package com.s.sdk.payment.view;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.SSDK;
import com.s.sdk.base.BaseWebViewFragment;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.dashboard.view.DashBoardPopup;
import com.s.sdk.login.view.ConnectAccountPlayNowFragment;
import com.s.sdk.payment.model.SPaymentResult;
import com.s.sdk.payment.presenter.PaymentContract;
import com.s.sdk.payment.presenter.PaymentPresenter;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.SPopup;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaymentFragment extends BaseWebViewFragment implements PaymentContract.View, PurchasesUpdatedListener {
    private PaymentContract.Presenter presenter;
    private JSONObject jsonObjectRequest;


    private BillingClient mBillingClient;


    public static final String TAG = PaymentFragment.class.getName();

    public static PaymentFragment newInstance() {
        Bundle args = new Bundle();
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.s_fragment_login;
    }

    @Override
    public void onDestroy() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
        if (webView != null) {
            webView.stopLoading();
        }
        if (presenter!=null){
            presenter.detachView();
        }
        //tracking close payment
        STracker.trackEvent("sdk", STracker.ACTION_PAYMENT_CLOSE, "");
        super.onDestroy();
    }

    @Override
    protected void onActivityCreated() {
        //send tracking open payment
        STracker.trackEvent("sdk", STracker.ACTION_PAYMENT_OPEN, "");
        DashBoardPopup.getInstance().hidePopup();
        SPopup.getInstance().hidePopupWarning();
        //init presenter
        presenter = new PaymentPresenter();
        presenter.attachView(this);

        mBillingClient = BillingClient.newBuilder(getActivity()).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                if (getActivity() == null) return;
                Utils.showToast(getActivity(), getString(R.string.s_payment_error_init_iap));
                finishActivity();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected String getURLRequest() {
        return Constants.URL_PAYMENT + signRequest();
    }

    private String signRequest() {
        jsonObjectRequest = createObjectRequest();
        try {
            jsonObjectRequest.put("redirect_uri", "uri_payment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "signed_request="
                + EncryptorEngine.encryptData(jsonObjectRequest.toString(), Constants.PUBLIC_KEY);
    }

    @Override
    protected boolean onShouldOverrideUrlLoading(String url) {
        if (url.contains("http://close")) {
            finishActivity();
            return true;
        }
        if (url.contains("uri_payment") && url.contains("status")) {
            Uri uri = Uri.parse(url);
            String status = uri.getQueryParameter("status");
            if (status.equals("success")) {
                String orderId = uri.getQueryParameter("order_id");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("trans_id", orderId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                presenter.sendTrackingPaymentFinish(jsonObject.toString());
                SCallback<SPaymentResult> callback = CallbackManager.getPaymentCallback();
                if (callback != null) {
                    callback.onSuccess(new SPaymentResult(orderId));
                }
                Utils.showToast(SContext.getApplicationContext(), getString(R.string.s_fragment_payment_success));

            } else {
                //send tracking
                String message = uri.getQueryParameter("message");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                presenter.sendTrackingPaymentFinish(jsonObject.toString());

                SDialog.showDialog(getActivity(), message, null, getString(R.string.s_ok), null, new SOnClickListener() {
                    @Override
                    public void onClick() {
                        SCallback callback = CallbackManager.getPaymentCallback();
                        if (callback != null) {
                            callback.onError();
                        }
                        finishActivity();
                    }
                }).setCancelable(false);
            }
            return true;
        }

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
        //webView.loadUrl("javascript:function AppSDKexecute(method, orderInfo) {var orderText = JSON.stringify(orderInfo); PaymentInterface.interactPay(method,orderText);}");
        webView.loadUrl("javascript:function AppSDKexecute(method,orderInfo) {var orderText = JSON.stringify(orderInfo); JavaScriptInterface.javaScriptInteract(method,orderText);}");
    }

    @Override
    protected void onJavaScriptInteract(String method, String value) {
        switch (method) {
            case "logout":
                finishActivity();
                SSDK.getInstance().logoutNoMessage();
                break;
            case "connect_account":
                gotoConnectAccountPlayNow();
                break;
            case "iappay":
                try {
                    String orderInfo = new JSONObject(value).getString("package_id");
                    jsonObjectRequest.put("order_info", orderInfo /*"android.test.purchased"*/);
                } catch (JSONException e) {
                    Utils.showToastError(getActivity());
                    e.printStackTrace();
                    return;
                }
                presenter.onClickIAP(jsonObjectRequest);
                break;
            case "close_popup":
                finishActivity();
                break;
            case "onclick_back":
                getActivity().onBackPressed();
                break;

        }
    }

    private void finishActivity() {
        Activity activity = getActivity();
        if (activity != null)
            activity.finish();
    }

    private void gotoConnectAccountPlayNow() {
        Fragment fragment = ConnectAccountPlayNowFragment.newInstance();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.sContainer, fragment, ConnectAccountPlayNowFragment.TAG)
                .addToBackStack(ConnectAccountPlayNowFragment.TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * onPurchasesUpdated iap
     *
     * @param responseCode
     * @param purchases
     */
    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            int count = 0;
            for (Purchase purchase : purchases) {
                Alog.e("purchase " + count + ": " + purchase.getOriginalJson() + "//" + purchase.getSignature());
                count++;
                consumeAsync(purchase.getPurchaseToken());
                presenter.handlePurchase(jsonObjectRequest, purchase);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("status", "success");
                    obj.put("message", "success");
                    obj.put("receipt", purchase.getOriginalJson());
                } catch (JSONException e) {
                }
                presenter.sendTrackingIAPEnd(obj.toString());
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Alog.e("BillingClient.BillingResponse.USER_CANCELED");

            JSONObject obj = new JSONObject();
            try {
                obj.put("status", "fail");
                obj.put("message", "user cancel");
            } catch (JSONException e) {
                //e.printStackTrace();
            }
            presenter.sendTrackingIAPEnd(obj.toString());

        } else {
            // Handle any other error codes.
            Alog.e("BillingClient.BillingResponse.OTHER_CODE " + responseCode);
            JSONObject obj = new JSONObject();
            try {
                obj.put("status", "fail");
                obj.put("message", "error code = " + responseCode);
            } catch (JSONException e) {
                //e.printStackTrace();
            }
            presenter.sendTrackingIAPEnd(obj.toString());

        }
    }

    private void consumeAsync(final String purchaseToken) {
        final ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    Alog.e("onConsumeResponse ok");
                    // Handle the success of the consume operation.
                    // For example, increase the number of coins inside the user&#39;s basket.
                }
            }
        };
        mBillingClient.consumeAsync(purchaseToken, listener);
    }

    @Override
    public void initIAPGG(String skuId) {
//        Alog.e("skuId: " + skuId);
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(skuId)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        mBillingClient.launchBillingFlow(getActivity(), flowParams);
    }

    @Override
    public void showLoading(boolean isShowLoading) {
        if (isShowLoading) {
            progressLoading.setVisibility(View.VISIBLE);
        } else {
            progressLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDialogTokenExpired(String message) {
        SDialog.showDialog(getActivity(), message, null, getString(R.string.s_ok), null, new SOnClickListener() {
            @Override
            public void onClick() {
                finishActivity();
                SSDK.getInstance().logoutNoMessage();
            }
        }).setCancelable(false);
    }
}
