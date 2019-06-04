package com.s.sdk.payment.presenter;

import com.android.billingclient.api.Purchase;
import com.s.sdk.base.BasePresenter;
import com.s.sdk.base.BaseView;

import org.json.JSONObject;

public class PaymentContract {
    public interface Presenter extends BasePresenter<View>{
        void onClickIAP(JSONObject jsonObjectRequest);

        void handlePurchase(JSONObject jsonObjectRequest,Purchase purchase);

        void sendTrackingPaymentFinish(String dataSend);

        void sendTrackingIAPEnd(String dataSend);
    }

    public interface View extends BaseView {

        void showLoading(boolean b);

        void showDialogTokenExpired(String message);

        void initIAPGG(String skuId);
    }
}
