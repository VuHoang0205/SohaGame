package com.s.sdk.payment.presenter;

import android.app.Activity;
import android.util.Base64;

import com.android.billingclient.api.Purchase;
import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.base.BaseResponse;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.network.RetrofitService;
import com.s.sdk.payment.model.ResponseConfirmPayment;
import com.s.sdk.payment.model.ResponseCreatepayment;
import com.s.sdk.payment.model.SPaymentResult;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentPresenter implements PaymentContract.Presenter {
    private PaymentContract.View baseView;
    private Call<BaseResponse> callCreatePayment;
    private Call<BaseResponse> callConfirmPayment;
    private String trans_id;

    @Override
    public void attachView(PaymentContract.View view) {
        baseView = view;
    }

    @Override
    public void detachView() {
        if (callCreatePayment != null) {
            callCreatePayment.cancel();
        }
        if (callConfirmPayment != null) {
            callConfirmPayment.cancel();
        }

        baseView = null;
    }

    @Override
    public void onClickIAP(JSONObject jsonObjectRequest) {
        createOrderPayment(jsonObjectRequest);
    }

    //{"status":"success","error_code":0,"message":"create order success","data":"245651539402267"}
    private void createOrderPayment(final JSONObject jsonObjectRequest) {
        baseView.showLoading(true);
        String signRequest = EncryptorEngine.encryptDataNoURLEn(jsonObjectRequest.toString(), Constants.PUBLIC_KEY);
        PaymentService paymentService = RetrofitService.create(PaymentService.class);
        callCreatePayment = paymentService.createPayment(signRequest);
        callCreatePayment.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                baseView.showLoading(false);
                BaseResponse res = response.body();
                if (res == null) {
                    Utils.showToastError(baseView.getContext());
                    return;
                }

                ResponseCreatepayment responseCreatePayment = res.decodeResponse(ResponseCreatepayment.class);
                if (responseCreatePayment == null) {
                    Utils.showToastError(baseView.getContext());
                    return;
                }
                if (responseCreatePayment.getStatus().equalsIgnoreCase("success")) {
                    trans_id = responseCreatePayment.getData();
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("trans_id", trans_id);
                        obj.put("item_id", jsonObjectRequest.optString("order_info"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendTrackingIAPStart(obj.toString());

                    baseView.initIAPGG(jsonObjectRequest.optString("order_info"));
                    return;
                }

                if (responseCreatePayment.getError_code() == 1002) {//token expired
                    baseView.showDialogTokenExpired(responseCreatePayment.getMessage());
                    return;
                }

                //con lai la loi
                Utils.showToast(baseView.getContext(), responseCreatePayment.getMessage());

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (baseView != null) {
                    baseView.showLoading(false);
                }
            }
        });
    }

    private void sendTrackingIAPStart(String data) {
        STracker.trackEvent("sdk", STracker.ACTION_IAP_START,
                Base64.encodeToString(data.getBytes(), Base64.DEFAULT));
    }

    @Override
    public void handlePurchase(final JSONObject jsonObjectRequest, final Purchase purchase) {
        baseView.showLoading(true);
        try {
            jsonObjectRequest.put("platform", "android");
            jsonObjectRequest.put("trans_id", trans_id);
            jsonObjectRequest.put("receipt", signDataIap(purchase.getOriginalJson(), purchase.getSignature()));
            //Log.e("CHECK_IAP111", jsonObjectRequest.toString() + " ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String signRequest = EncryptorEngine.encryptDataNoURLEn(jsonObjectRequest.toString(), Constants.PUBLIC_KEY);
        PaymentService paymentService = RetrofitService.create(PaymentService.class);
        callConfirmPayment = paymentService.confirmPayment(signRequest);
        callConfirmPayment.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (baseView == null) return;
                baseView.showLoading(false);
                BaseResponse res = response.body();
                if (res == null) {
                    Utils.showToastError(baseView.getContext());
                    return;
                }

                ResponseConfirmPayment responseConfirmPayment = res.decodeResponse(ResponseConfirmPayment.class);
                if (responseConfirmPayment == null) {
                    Utils.showToastError(baseView.getContext());
                    return;
                }

                if (responseConfirmPayment.getStatus().equalsIgnoreCase("success")) {
                    String orderId = responseConfirmPayment.getOrder_id();
                    sendTrackingFinish(trans_id, purchase.getOriginalJson());
                    ((Activity) baseView.getContext()).finish();
                    SCallback<SPaymentResult> callback = CallbackManager.getPaymentCallback();
                    if (callback != null) {
                        callback.onSuccess(new SPaymentResult(orderId));
                    }
                    Utils.showToast(SContext.getApplicationContext(), baseView.getContext().getString(R.string.s_fragment_payment_success));
                    return;
                }

                //con lai loi
                //send tracking error iap
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status", "fail");
                    jsonObject.put("message", responseConfirmPayment.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendTrackingPaymentFinish(jsonObject.toString());

                Utils.showToast(baseView.getContext(), responseConfirmPayment.getMessage());
                ((Activity) baseView.getContext()).finish();
                SCallback callback = CallbackManager.getPaymentCallback();
                if (callback != null) {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (baseView != null) {
                    baseView.showLoading(false);
                    Utils.showToastError(baseView.getContext());
                }
            }
        });
    }

    private void sendTrackingFinish(String trans_id, String purchaseData) {
        String itemId = "";
        try {
            JSONObject jsPurchaseData = new JSONObject(purchaseData);
            itemId = jsPurchaseData.optString("productId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("trans_id", trans_id);
            obj.put("item_id", itemId);
            obj.put("status", "success");
            obj.put("receipt", "");
            obj.put("message", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendTrackingPaymentFinish(obj.toString());
    }

    @Override
    public void sendTrackingIAPEnd(String dataSend) {
        STracker.trackEvent("sdk", STracker.ACTION_IAP_END,
                Base64.encodeToString(dataSend.getBytes(), Base64.DEFAULT));
    }

    @Override
    public void sendTrackingPaymentFinish(String dataSend) {
        STracker.trackEvent("sdk", STracker.ACTION_PAYMENT_FINISH,
                Base64.encodeToString(dataSend.getBytes(), Base64.DEFAULT));
    }

    private String signDataIap(String purchaseData, String signature) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("order_data", purchaseData);
            obj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dataEncodedBase64 = Base64.encodeToString(obj.toString().getBytes(), Base64.DEFAULT).replaceAll("\n", "");
        return dataEncodedBase64;
    }

}
