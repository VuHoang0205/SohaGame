package com.s.sdk.dashboard.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.s.sdk.R;

public class ClosePopup {
    private static ClosePopup closePopup;
    private PopupWindow popupClose;

    private ClosePopup() {
    }

    public static synchronized ClosePopup getInstance() {
        if (closePopup == null) {
            closePopup = new ClosePopup();
        }

        return closePopup;
    }

    public void showPopup() {
        if (popupClose != null && popupClose.isShowing()) {
            popupClose.getContentView().setVisibility(View.VISIBLE);
        }
    }

    public void hidePopup() {
        if (popupClose != null) {
            popupClose.getContentView().setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView(Activity activity) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRoot = activity.findViewById(android.R.id.content);
        if (viewRoot == null) {
            viewRoot = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        FrameLayout popupView = (FrameLayout) layoutInflater.inflate(R.layout.close_dashboard, null);
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        int sizeCloseDB = dpToPx(50, activity);
        popupClose = new PopupWindow(
                popupView, sizeCloseDB, sizeCloseDB/*WindowManager.LayoutParams.WRAP_CONTENT*/);
        popupClose.showAtLocation(viewRoot, Gravity.NO_GRAVITY, screenWidth / 2 - sizeCloseDB / 2, screenHeight - 220);
    }

    private int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
