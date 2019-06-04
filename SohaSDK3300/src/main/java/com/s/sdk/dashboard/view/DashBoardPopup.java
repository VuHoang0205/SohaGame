package com.s.sdk.dashboard.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.s.sdk.R;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.dashboard.model.LocationDashboard;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.squareup.picasso.Picasso;


public class DashBoardPopup {
    public static boolean showNotify = false;
    private static DashBoardPopup dashBoardPopup;

    private PopupWindow popupDashBoard;
    private OnClickDashBoard onClickDashBoard;

    private int mCurrentX;
    private int mCurrentY;

    private int screenHeight;
    private int screenWidth;
    private int delta;
    private int heightImage;
    private LocationDashboard locationDashboard;
    private Handler handler;
    private boolean isMove = false;
    private boolean isLeft;
    private Runnable runnable;
    private ImageView imgNotify;
    private ClosePopup closePopup;
    public static boolean isInitedDashBoard = false;
    private int sizeClose;

    private DashBoardPopup() {
    }

    public static synchronized DashBoardPopup getInstance() {
        if (dashBoardPopup == null) {
            dashBoardPopup = new DashBoardPopup();
        }

        return dashBoardPopup;
    }

    public void showPopup() {
        if (popupDashBoard != null && popupDashBoard.isShowing()) {
            popupDashBoard.getContentView().setVisibility(View.VISIBLE);
        }
    }

    public void hidePopup() {
        if (popupDashBoard != null) {
            popupDashBoard.getContentView().setVisibility(View.GONE);
        }
    }

    public void showImageNotify() {
        if (imgNotify == null) return;
        if (showNotify) {
            imgNotify.setVisibility(View.VISIBLE);
        } else {
            imgNotify.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initAndShowPopup(final Activity activity) {
//        View decorView = activity.getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) return;
        String urlDashBoard = data.getIcon_db();
        if (TextUtils.isEmpty(urlDashBoard)) return;
        if (DashBoardPopup.isInitedDashBoard) return;
        DashBoardPopup.isInitedDashBoard = true;
        closePopup = ClosePopup.getInstance();
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRoot = activity.findViewById(android.R.id.content);
        if (viewRoot == null) {
            viewRoot = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        final GestureDetector gestureDetector = new GestureDetector(activity, new SingleTapConfirm());

        FrameLayout popupView = (FrameLayout) layoutInflater.inflate(R.layout.s_popup_dashboard, null);
        final ImageView imageView = popupView.findViewById(R.id.imgView);
        imgNotify = popupView.findViewById(R.id.imgNotify);
        final RelativeLayout rlDb = popupView.findViewById(R.id.rl_db);

        showImageNotify();

        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (isLeft) {
                        rlDb.setScrollX(heightImage / 2);
                    } else {
                        rlDb.setScrollX(-heightImage / 2);
                    }
                }
            };
        }

        heightImage = activity.getResources().getDimensionPixelSize(R.dimen.s_size_popup_dashboard);
        sizeClose = dpToPx(50, activity);
        screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        popupDashBoard = new PopupWindow(
                popupView, heightImage, heightImage/*WindowManager.LayoutParams.WRAP_CONTENT*/);
        locationDashboard = PrefUtils.getObject(Constants.PREF_LOCATION_DB, LocationDashboard.class);
        if (locationDashboard == null) {
            locationDashboard = new LocationDashboard(0, 0);
        }
        mCurrentX = locationDashboard.getCurrentX();
        mCurrentY = locationDashboard.getCurrentY();
        isLeft = mCurrentX < screenWidth / 2;
        delta = screenHeight - heightImage;
        popupDashBoard.showAtLocation(viewRoot, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);

        hideToEdge();
        popupView.setOnTouchListener(new View.OnTouchListener() {
            private float mDx;
            private float mDy;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    Alog.e("onClick popupDashBoard");
                    if (onClickDashBoard != null) onClickDashBoard.onClickDashBoard();
                    // return true;
                }


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        closePopup.initView(activity);
                        handler.removeCallbacks(runnable);
                        isMove = true;
//                        imgNotify.setVisibility(View.VISIBLE);
                        rlDb.setScrollX(0);
                        mDx = mCurrentX - event.getRawX();
                        mDy = mCurrentY - event.getRawY();

                        //Alog.e("ACTION_DOWN " + "mDx:" + mDx + "//mDy:" + mDy);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        mCurrentX = (int) (event.getRawX() + mDx);
                        mCurrentY = (int) (event.getRawY() + mDy);
                        if (mCurrentY > delta) mCurrentY = delta;
                        if (mCurrentY < 1) mCurrentY = 1;
                        popupDashBoard.update(mCurrentX, mCurrentY, -1, -1, true);
                        break;
                    case MotionEvent.ACTION_UP:
                        closePopup.hidePopup();
                        isMove = false;
                        Log.e("aaaaaa", (screenWidth / 2 - sizeClose / 2) + " " + mCurrentX + " " + (screenWidth / 2 + sizeClose / 2) + "\n"
                                + (screenHeight - 220) + " " + mCurrentY + " " + (screenHeight - 220 + sizeClose) + " _____");
                        //Alog.e("ACTION_UP:" + mDx + "//mDy:" + mDy);
                        if (popupDashBoard != null && popupDashBoard.isShowing()) {
                            if (screenWidth / 2 - sizeClose / 2 - heightImage < mCurrentX && mCurrentX < screenWidth / 2 + sizeClose / 2 + heightImage
                                    && screenHeight - 220 + heightImage > mCurrentY && screenHeight - 220 - sizeClose - heightImage < mCurrentY) {
                                SDialog.showDialog(activity, activity.getString(R.string.cf_close_dashboard), activity.getString(R.string.s_cancel),
                                        activity.getString(R.string.s_ok), new SOnClickListener() {
                                            @Override
                                            public void onClick() {

                                            }
                                        }, new SOnClickListener() {
                                            @Override
                                            public void onClick() {
                                                hidePopup();
                                                STracker.trackEvent("sdk", STracker.ACTION_REMOVE_DB, "");
                                            }
                                        });

                            }
                            if (mCurrentX <= (screenWidth - heightImage) / 2) {
                                mCurrentX = 1;
                                hideToEdge();
                                isLeft = true;
                            } else {
                                mCurrentX = screenWidth /*- heightImage*/;
                                hideToEdge();
                                isLeft = false;
                            }

                            popupDashBoard.update(mCurrentX, mCurrentY, -1, -1, true);
                            locationDashboard.setCurrentX(mCurrentX);
                            locationDashboard.setCurrentY(mCurrentY);
                            PrefUtils.putObject(Constants.PREF_LOCATION_DB, locationDashboard);
                            //PrefUtils.setXDashboard(activity, mCurrentX);
                            //PrefUtils.setYDashboard(activity, mCurrentY);
                            //hideHalfDB();
                        }
                        break;
                }
                return true;

            }
        });

        Picasso.get().load(urlDashBoard).into(imageView);
    }

    private int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public boolean isShowPopup() {
        return popupDashBoard != null && popupDashBoard.isShowing();
    }

    public void updatePopupRotateScreen(Activity activity) {
        if (popupDashBoard != null && popupDashBoard.isShowing()) {
            screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            delta = screenHeight - heightImage;
            //Alog.e("screenHeight: " + screenHeight + "//screenWidth: " + screenWidth);

            if (mCurrentY - heightImage > screenHeight) {
                mCurrentY = delta;
            }
            if (mCurrentX <= (screenWidth - heightImage) / 2) {
                mCurrentX = 1;
                isLeft = true;
            } else {
                mCurrentX = screenWidth /*- heightImage*/;
                isLeft = false;
            }
            popupDashBoard.update(mCurrentX, mCurrentY, -1, -1, true);
        }
    }


    /**
     * clearPopup
     */
    public void clearPopup() {
        if (popupDashBoard != null) {
            popupDashBoard.dismiss();
            popupDashBoard = null;
        }
        if (onClickDashBoard != null) onClickDashBoard = null;
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        if (imgNotify != null) imgNotify = null;


    }


    public void setOnClickDashBoard(OnClickDashBoard onClickDashBoard) {
        this.onClickDashBoard = onClickDashBoard;
    }

    public interface OnClickDashBoard {
        void onClickDashBoard();
    }

    private static class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    //    private void hideHalfDB(){
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isLeft){
//                    Log.e("CHECK_RIGHT", "LEFT");
//                    popupDashBoard.showAtLocation(viewRoot, Gravity.NO_GRAVITY, -450, mCurrentY);
//                } else {
//                    Log.e("CHECK_RIGHT", "RIGHT");
//                    popupDashBoard.showAtLocation(viewRoot, Gravity.NO_GRAVITY, screenWidth+ 4500, mCurrentY);
//                }
//            }
//        },3000);
//    }


    public void setIsMove(boolean isMove) {
        this.isMove = isMove;
    }

    public void hideToEdge() {
        if (!isMove && handler != null && runnable != null) {
            handler.postDelayed(runnable, 3000);
        }
    }
}
