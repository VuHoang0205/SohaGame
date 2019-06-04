package com.s.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.s.sdk.R;
import com.s.sdk.SActivity;
import com.s.sdk.base.Constants;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.squareup.picasso.Picasso;

public class SPopup {
    private PopupWindow popupWarning;
    private PopupWindow popupConnectAccount;
    private Handler handlerConnectAccount;
    private Runnable runnableConnectAccount;

    private Handler handlerHideConnectAccount;
    private Runnable runnableHideConnectAccount;

    private int mCurrentX;
    private int mCurrentY;
    private int screenHeight;
    private int screenWidth;
    private int heightImage;
    private int delta;
    private PopupWindow popupConnectFb;

    private SPopup() {

    }

    private static SPopup instance;

    public static synchronized SPopup getInstance() {
        if (instance == null) {
            instance = new SPopup();
        }
        return instance;
    }

    public void showPopupWarning() {
        if (popupWarning != null && popupWarning.isShowing()) {
            //Alog.d("showPopupWarning");
            popupWarning.getContentView().setVisibility(View.VISIBLE);
        }
    }

    public void hidePopupWarning() {
        if (popupWarning != null) {
            //Alog.d("hidePopupWarning");
            popupWarning.getContentView().setVisibility(View.INVISIBLE);
        }
    }


    /**
     * initAndShowPopupWarning
     *
     * @param activity
     */
    public void initAndShowPopupWarning(final Activity activity) {
        if (popupWarning != null && popupWarning.isShowing()) return;
        View viewRoot = activity.findViewById(android.R.id.content);
        if (viewRoot == null) {
            viewRoot = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        }

        final GestureDetector gestureDetector = new GestureDetector(activity, new SingleTapConfirm());

        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        Alog.e("show popup warning");
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ImageView popupView = (ImageView) layoutInflater.inflate(R.layout.s_popup_warning, null);
        //ImageView imgView = popupView.findViewById(R.id.imgView);


//        final int statusBarHeight = getStatusBarHeight(activity);
        screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        heightImage = /*popupView.getLayoutParams().height*/activity.getResources().getDimensionPixelSize(R.dimen.s_height_popup_warning);
        delta = screenHeight - heightImage;

        String url = data.getImage_age();
        if (TextUtils.isEmpty(url)) return;
        popupWarning = new PopupWindow(
                popupView, LinearLayout.LayoutParams.WRAP_CONTENT, heightImage);
        mCurrentX = 0;
        mCurrentY = 2 * activity.getResources().getDimensionPixelSize(R.dimen.s_size_popup_dashboard);
        popupWarning.showAtLocation(viewRoot, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
        popupView.setOnTouchListener(new View.OnTouchListener() {
//            int orgX, orgY;
//            int offsetX, offsetY;

            private float mDx;
            private float mDy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    Alog.e("onClick popupWarning");
                    onClickWarning(activity);
                    return true;
                }
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        orgX = (int) event.getX();
//                        orgY = (int) event.getY();
//                        Log.e("mytag", "orgX:" + orgX + "//orgY:" + orgY);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        offsetX = (int) event.getRawX() - orgX;
//                        offsetY = (int) event.getRawY() - orgY;
//                        if (offsetY > delta) offsetY = delta;
//                        if (offsetY < 1) offsetY = 1;
//                        popupWarning.update(offsetX, offsetY, -1, -1, true);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDx = mCurrentX - event.getRawX();
                        mDy = mCurrentY - event.getRawY();
                        Alog.e("ACTION_DOWN " + "mDx:" + mDx + "//mDy:" + mDy);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX = (int) (event.getRawX() + mDx);
                        mCurrentY = (int) (event.getRawY() + mDy);
                        if (mCurrentY > delta) mCurrentY = delta;
                        if (mCurrentY < 1) mCurrentY = 1;

                        popupWarning.update(mCurrentX, mCurrentY, -1, -1, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (popupWarning != null && popupWarning.isShowing()) {
                            if (mCurrentX <= (screenWidth - heightImage) / 2) {
                                mCurrentX = 1;
                            } else {
                                mCurrentX = screenWidth - heightImage;
                            }
                            popupWarning.update(mCurrentX, mCurrentY, -1, -1, true);
                        }

                        break;
                }
                return true;
            }
        });

        Picasso.get().load(url).into(popupView);

    }

    public void updatePopupRotateScreen(Activity activity) {
        if (popupWarning != null && popupWarning.isShowing()) {
            screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            delta = screenHeight - heightImage;
            //Alog.e("screenHeight: " + screenHeight + "//screenWidth: " + screenWidth);

            if (mCurrentY - heightImage > screenHeight) {
                mCurrentY = delta;
            }
            if (mCurrentX <= (screenWidth - heightImage) / 2) {
                mCurrentX = 1;
            } else {
                mCurrentX = screenWidth - heightImage;
            }
            popupWarning.update(mCurrentX, mCurrentY, -1, -1, true);
        }
    }

    private void clearPopupWarning() {
        if (popupWarning != null) {
            if (popupWarning.isShowing()) {
                popupWarning.dismiss();
            }
            popupWarning = null;
        }
    }

    public void clearAllPopup() {
        clearPopupWarning();
        clearPopupConnectAccount();
        clearPopupConnectFb();
    }

    private void clearPopupConnectFb() {
        if (popupConnectFb != null) {
            if (popupConnectFb.isShowing()) {
                popupConnectFb.dismiss();
            }
            popupConnectFb = null;
        }
    }


    public void clearPopupConnectAccount() {
        if (popupConnectAccount != null) {
            if (popupConnectAccount.isShowing()) {
                popupConnectAccount.dismiss();
            }
            popupConnectAccount = null;
        }
        if (handlerConnectAccount != null) {
            handlerConnectAccount.removeCallbacks(runnableConnectAccount);
            handlerConnectAccount = null;
            runnableConnectAccount = null;
        }

        if (handlerHideConnectAccount != null) {
            handlerHideConnectAccount.removeCallbacks(runnableHideConnectAccount);
            handlerHideConnectAccount = null;
            runnableHideConnectAccount = null;
        }
    }


    private static class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    private void onClickWarning(Activity activity) {
        Intent i = new Intent(activity, SActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_SHOW_WARNING);
        activity.startActivity(i);
    }

//    private int getStatusBarHeight(Activity activity) {
//        Rect rectangle = new Rect();
//        Window window = activity.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
//        int statusBarHeight = rectangle.top;
////        int contentViewTop =
////                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
////        int titleBarHeight = contentViewTop - statusBarHeight;
//        return statusBarHeight;
//    }

    private void removeHandlerConnectAccount() {
        if (handlerConnectAccount != null && runnableConnectAccount != null) {
            handlerConnectAccount.removeCallbacks(runnableConnectAccount);
        }
    }

    private int timePost;
    private boolean isShowConnectAccount = true;

    private boolean enableConnectAccount() {
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        if (loginResult == null) {
            removeHandlerConnectAccount();
            return false;
        }
        if (!"play_now".equalsIgnoreCase(loginResult.getType_user())) {
            removeHandlerConnectAccount();
            return false;
        }
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) {
            removeHandlerConnectAccount();
            return false;
        }
        timePost = data.getWarning_time_connect();
        if (timePost <= 0) {
            removeHandlerConnectAccount();
            return false;
        }
        return true;
    }

    public void initAndShowPopupConnectAccountPlayNow(final Activity activity) {
        final SpannableString spannable = new SpannableString(activity.getString(R.string.s_popup_connect_account_playnow_2) + activity.getString(R.string.s_popup_connect_account_playnow3));
//        spannable.setSpan(new UnderlineSpan(), spannable.length() - 12, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spannable.setSpan();
        if (!enableConnectAccount()) return;
        if (handlerConnectAccount != null) return;
        Alog.d("initAndShowPopupConnectAccountPlayNow");

        //if (handlerConnectAccount == null) {
        handlerConnectAccount = new Handler();
        //}
        if (runnableConnectAccount == null) {
            runnableConnectAccount = new Runnable() {
                @Override
                public void run() {
                    if (!enableConnectAccount()) return;
                    View viewRoot = activity.findViewById(android.R.id.content);
                    if (viewRoot == null) {
                        viewRoot = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                    }
                    if (popupConnectAccount != null && !popupConnectAccount.isShowing()) {
                        popupConnectAccount.showAtLocation(viewRoot, Gravity.NO_GRAVITY, 0, 0);
                    } else {
                        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View popupView = layoutInflater.inflate(R.layout.s_popup_connect_account, null);
                        popupConnectAccount = new PopupWindow(
                                popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        popupConnectAccount.showAtLocation(viewRoot, Gravity.NO_GRAVITY, 0, 0);
                        final ImageView imgClose = popupView.findViewById(R.id.imgClose);
                        imgClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupConnectAccount.dismiss();
                            }
                        });
                        final TextView tvAction = popupView.findViewById(R.id.tvAction);
                        tvAction.setMovementMethod(LinkMovementMethod.getInstance());
                        final View finalViewRoot = viewRoot;
                        spannable.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                popupConnectAccount.dismiss();
                                showPopupConnectAccountFb(activity, finalViewRoot, tvAction.getLayoutParams().height);
                            }
                        }, spannable.length() - 13, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        tvAction.setText(spannable);
                    }

                    //check hide connect account
                    if (isShowConnectAccount) {
                        showPopupConnectAccount();
                    } else {
                        hidePopupConnectAccount();
                    }

                    //auto hide popup connectaccount
                    if (handlerHideConnectAccount == null) {
                        handlerHideConnectAccount = new Handler();
                    }
                    if (runnableHideConnectAccount == null) {
                        runnableHideConnectAccount = new Runnable() {
                            @Override
                            public void run() {

                                if (popupConnectAccount != null && popupConnectAccount.isShowing()) {
                                    popupConnectAccount.dismiss();
                                }
                            }
                        };
                    }
                    handlerHideConnectAccount.postDelayed(runnableHideConnectAccount, 6000);

                    //schedule
                    handlerConnectAccount.postDelayed(runnableConnectAccount, timePost * 1000);
                }
            };
        }
        handlerConnectAccount.postDelayed(runnableConnectAccount, timePost * 1000);
    }


    public void showPopupConnectAccount() {
        isShowConnectAccount = true;
        if (popupConnectAccount != null /*&& popupConnectAccount.isShowing()*/) {
            popupConnectAccount.getContentView().setVisibility(View.VISIBLE);
        }

    }

    public void hidePopupConnectAccount() {
        isShowConnectAccount = false;
        if (popupConnectAccount != null /*&& popupConnectAccount.isShowing()*/) {
            popupConnectAccount.getContentView().setVisibility(View.GONE);
        }
    }

    private void showPopupConnectAccountFb(final Activity activity, View viewRoot, int height) {
        Alog.d("height: " + height);
        LayoutInflater layoutInflater = LayoutInflater.from(viewRoot.getContext());
        final ViewGroup popupView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_show_connect_account, null);
        popupConnectFb = new PopupWindow(
                popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupConnectFb.setOutsideTouchable(true);
        popupConnectFb.showAtLocation(viewRoot, Gravity.RIGHT | Gravity.TOP, 0, activity.getResources().getDimensionPixelSize(R.dimen.s_60dp));
        final View mLnLoginMobile = popupView.findViewById(R.id.lnLoginMobile);
        final View lnLoginfaceBook = popupView.findViewById(R.id.lnLoginfaceBook);

        mLnLoginMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupConnectFb.dismiss();
                Intent i = new Intent(activity, SActivity.class);
                i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_CONNECT_ACCOUNT_PLAYNOW);
                activity.startActivity(i);
            }
        });
        lnLoginfaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupConnectFb.dismiss();
                Intent i = new Intent(activity, SActivity.class);
                i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_CONNECT_ACCOUNT_FB_FROM_POPUP);
                i.putExtra(Constants.KEY_STYLE_LOGIN, Constants.LOGIN_POPUP);
                activity.startActivity(i);
            }
        });
    }
}
