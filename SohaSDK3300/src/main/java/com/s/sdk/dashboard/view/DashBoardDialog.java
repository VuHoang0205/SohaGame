package com.s.sdk.dashboard.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.s.sdk.R;
import com.s.sdk.SActivity;
import com.s.sdk.base.Constants;
import com.s.sdk.base.InkPageIndicator;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.dashboard.model.DashBoardItem;
import com.s.sdk.dashboard.presenter.InteractConfirmOpenFanpage;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.tracking.STracker;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.SDialog;
import com.s.sdk.utils.SPopup;
import com.s.sdk.utils.Utils;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class DashBoardDialog extends Dialog {
    private final String FACEBOOK_URL = "https://www.facebook.com/";
    private final String TWITTER_URL = "https://twitter.com/";
    private final String PACKAGE_S = "com.soha.sohacustomerservices";
    private static final String DB_BACK = "back";

    private List<DashBoardItem> listDataOrigin;
    private List<DashBoardItem> listDash;
    private Activity activity;
    private final int sizePage = 9;
    private OnEventDashBoard onEventDashBoard;
    public static boolean isClickGotoDetailDB = false;
    private boolean isRemoveDB = false;
    private LinearLayout mLinearLayoutBodyDB8;
    private LinearLayout mLinearLayoutBodyDB6;
    private GridView mGridView;
    private DashBoardAdapter mDashBoardAdapter;
    private boolean mIsMoreItems = false;
    private DialogDashboardConfirmOpenFanpage mDialogConfirm;

    public DashBoardDialog(@NonNull Activity context) {
        super(context);
        this.listDataOrigin = PrefUtils.getListObjectDB(Constants.PREF_LIST_DB_CONFIG);
        this.activity = context;
        Log.e("toannt", new Gson().toJson(listDataOrigin));

//        listDataOrigin.remove(9);
//        listDataOrigin.remove(8);
//        listDataOrigin.add(new DashBoardItem("Tin Tức", "news",
//                "https://beta.soap.soha.vn/dialog/dashboard/icon_new/news.png",
//                "https://beta.soap.soha.vn/dialog/webview/dashboardn/news", 1, 1));
//        listDataOrigin.add(new DashBoardItem("Tin Tức", "news",
//                "https://beta.soap.soha.vn/dialog/dashboard/icon_new/news.png",
//                "https://beta.soap.soha.vn/dialog/webview/dashboardn/news", 1, 1));
//        listDataOrigin.add(new DashBoardItem("Tin Tức", "news",
//                "https://beta.soap.soha.vn/dialog/dashboard/icon_new/news.png",
//                "https://beta.soap.soha.vn/dialog/webview/dashboardn/news", 1, 1));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_dialog_dashboard);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onEventDashBoard.onDismitDialog();
                if (!isClickGotoDetailDB) {
                    if (isRemoveDB) {
                        isRemoveDB = false;
                    } else {
                        STracker.trackEvent("sdk", STracker.ACTION_CLOSE_DB, "");
                    }
                }
            }
        });
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DashBoardPopup.getInstance().showPopup();
                SPopup.getInstance().showPopupWarning();
            }
        });
        vInitData();
        STracker.trackEvent("sdk", STracker.ACTION_OPEN_DB, "");


    }

    private void vInitData() {
        mLinearLayoutBodyDB6 = findViewById(R.id.ln_body_db_6);
        mLinearLayoutBodyDB8 = findViewById(R.id.ln_body_db_8);
        mGridView = findViewById(R.id.gridview_db);
        if (listDataOrigin.size() == 6) {
            // 6 items in DB
            mLinearLayoutBodyDB8.setVisibility(View.GONE);
            mLinearLayoutBodyDB6.setVisibility(View.VISIBLE);

            // fill data
            initDataTextViewTitle(listDataOrigin.get(0).getTitle(), R.id.tv1);
            initDataImageViewIcon(listDataOrigin.get(0).getIcon(), R.id.iv1);
            initDataTextViewNotify(listDataOrigin.get(0).getNotify(), R.id.tvNotify1);
            initDataOnClickItem(listDataOrigin.get(0), R.id.linear_layout_1);

            initDataTextViewTitle(listDataOrigin.get(1).getTitle(), R.id.tv2);
            initDataImageViewIcon(listDataOrigin.get(1).getIcon(), R.id.iv2);
            initDataTextViewNotify(listDataOrigin.get(1).getNotify(), R.id.tvNotify2);
            initDataOnClickItem(listDataOrigin.get(1), R.id.linear_layout_2);

            initDataTextViewTitle(listDataOrigin.get(2).getTitle(), R.id.tv3);
            initDataImageViewIcon(listDataOrigin.get(2).getIcon(), R.id.iv3);
            initDataTextViewNotify(listDataOrigin.get(2).getNotify(), R.id.tvNotify3);
            initDataOnClickItem(listDataOrigin.get(2), R.id.linear_layout_3);

            initDataTextViewTitle(listDataOrigin.get(3).getTitle(), R.id.tv4);
            initDataImageViewIcon(listDataOrigin.get(3).getIcon(), R.id.iv4);
            initDataTextViewNotify(listDataOrigin.get(3).getNotify(), R.id.tvNotify4);
            initDataOnClickItem(listDataOrigin.get(3), R.id.linear_layout_4);

            initDataTextViewTitle(listDataOrigin.get(4).getTitle(), R.id.tv5);
            initDataImageViewIcon(listDataOrigin.get(4).getIcon(), R.id.iv5);
            initDataTextViewNotify(listDataOrigin.get(4).getNotify(), R.id.tvNotify5);
            initDataOnClickItem(listDataOrigin.get(4), R.id.linear_layout_5);

            initDataTextViewTitle(listDataOrigin.get(5).getTitle(), R.id.tv6);
            initDataImageViewIcon(listDataOrigin.get(5).getIcon(), R.id.iv6);
            initDataTextViewNotify(listDataOrigin.get(5).getNotify(), R.id.tvNotify6);
            initDataOnClickItem(listDataOrigin.get(5), R.id.linear_layout_6);
        } else {
            // 8 items in DB or more than
            mLinearLayoutBodyDB6.setVisibility(View.GONE);
            mLinearLayoutBodyDB8.setVisibility(View.VISIBLE);

            int sizeOrigin = listDataOrigin.size();
            if (sizeOrigin > 8) {
                listDataOrigin.add(4, new DashBoardItem("blank", "blank", "blank", "", -1, -1));
                listDataOrigin.add(8, new DashBoardItem(getContext().getString(R.string.textviewMore), "more",
                        String.valueOf(R.drawable.more), "blank", 1, 1));
                int pager = 1;
                int deltaOrigin = sizeOrigin - (pager * 9);
                boolean isLoadData = deltaOrigin >= 0 || deltaOrigin > -9;
                while (isLoadData) {
                    if (pager == 1) {
                        listDataOrigin.add(new DashBoardItem("blank", "blank", "blank", "", -1, -1));
                    } else {
                        int itemPage = 9 * pager - 5;
//                        Log.e("toannt", "itemPage: " + itemPage + " - " + sizeOrigin);
                        if (itemPage > sizeOrigin) {
                            int sizeDelta = 9 * pager - sizeOrigin - 2;
                            for (int i = 0; i < sizeDelta; i++) {
//                                Log.e("toannt", "hehehe");
                                listDataOrigin.add(new DashBoardItem("blank", "blank", "blank", "", -1, -1));
                            }
                        }
                        listDataOrigin.add(itemPage, new DashBoardItem("", DB_BACK,
                                String.valueOf(R.drawable.back), "", 1, 1));

                    }
                    pager = pager + 1;
                    deltaOrigin = sizeOrigin - (pager * 9);
//                    Log.e("toannt", "deltaOrigin: " + deltaOrigin);
                    isLoadData = deltaOrigin >= 0 || deltaOrigin > -9;
                }
            } else {
                for (int i = sizeOrigin - 1; i < 9; i++) {
                    listDataOrigin.add(new DashBoardItem("blank", "blank", "blank", "", -1, -1));
                }
                listDataOrigin.remove(8);
                listDataOrigin.add(4, new DashBoardItem("blank", "blank", "blank", "", -1, -1));
            }
            listDash = new ArrayList<>();
            addListData(0, 9);
            mDashBoardAdapter = new DashBoardAdapter(getContext(), listDash, false);
            mGridView.setAdapter(mDashBoardAdapter);
//
//            if (listDataOrigin.size() == 8) {
//                listDataOrigin.add(4, new DashBoardItem("blank", "blank", "blank", "blank", -1, -1));
//                mDashBoardAdapter = new DashBoardAdapter(getContext(), listDataOrigin, false);
//            } else if (listDataOrigin.size() > 8) {
//                mIsMoreItems = true;
//                listDataOrigin.add(4, new DashBoardItem("blank", "blank", "blank", "blank", -1, -1));
//                listDataOrigin.add(8, new DashBoardItem(getContext().getResources().getString(R.string.textviewMore), "Thêm", "blank", "blank", -1, -1));
//                if (listDataOrigin.size() >= 13)
//                    listDataOrigin.add(13, new DashBoardItem("blank", "blank", "blank", "blank", -1, -1));
//
//                while (listDataOrigin.size() % 9 != 0) {
//                    listDataOrigin.add(new DashBoardItem("blank", "blank", "blank", "blank", -1, -1));
//                }
//
//                List<DashBoardItem> dbItems = new ArrayList<DashBoardItem>();
//                for (int i = 0; i <= 8; i++) {
//                    dbItems.add(listDataOrigin.get(i));
//                }
//                mDashBoardAdapter = new DashBoardAdapter(getContext(), dbItems, true);
//            } else {
//                mDashBoardAdapter = new DashBoardAdapter(getContext(), listDataOrigin, false);
//            }
//
//            mGridView.setAdapter(mDashBoardAdapter);
        }
    }


    private void initDataTextViewNotify(int totalNotify, int resId) {
        TextView tvNotify = findViewById(resId);
        if (totalNotify > 0) {
            tvNotify.setText(String.valueOf(totalNotify));
            tvNotify.setVisibility(View.VISIBLE);
        } else {
            tvNotify.setVisibility(View.GONE);
        }
    }

    private void initDataTextViewTitle(String title, int resId) {
        ((TextView) findViewById(resId)).setText(title);
    }

    private void initDataImageViewIcon(String url, int resId) {
        Picasso.get().load(url).into((ImageView) findViewById(resId));
    }

    private void initDataOnClickItem(DashBoardItem dbItem, int resId) {
        (findViewById(resId)).setOnClickListener(new OnClickCheckDataListener(dbItem));
    }

    public class OnClickCheckDataListener implements View.OnClickListener {

        DashBoardItem mDbItem;

        public OnClickCheckDataListener(DashBoardItem dbItem) {
            this.mDbItem = dbItem;
        }

        @Override
        public void onClick(View v) {
            checkDataToClick(mDbItem);
        }

    }

    private void checkDataToClick(final DashBoardItem mDashBoard) {
        if (mDashBoard.getActive() == 1) {
            if (mDashBoard.getType().contains("page_facebook")) {
//                startFacebookPage(getContext(), mDashBoard.getId_page());
                mDialogConfirm = new DialogDashboardConfirmOpenFanpage(activity, getContext().getResources().getString(R.string.contentOpenFanpageFB), new InteractConfirmOpenFanpage() {

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onAccept() {
//                        dismiss();
                        startFacebookPage(getContext(), mDashBoard.getId_page());
                        STracker.trackEvent("sdk", STracker.ACTION_CLOSE_DB, "");
                    }
                });
                mDialogConfirm.show();
            } else if (mDashBoard.getType().contains("page_twitter")) {
//                startTwitterPage(getContext(), mDashBoard.getId_page());
                mDialogConfirm = new DialogDashboardConfirmOpenFanpage(activity, getContext().getResources().getString(R.string.contentOpenFanpageTwitter), new InteractConfirmOpenFanpage() {

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onAccept() {
//                        dismiss();
                        startTwitterPage(activity, mDashBoard.getId_page());
                        STracker.trackEvent("sdk", STracker.ACTION_CLOSE_DB, "");
                    }
                });
                mDialogConfirm.show();
            } else if (mDashBoard.getType().contains("sohacare")) {
//                startSohaCare(getContext(), "com.soha.sohacustomerservices");
                mDialogConfirm = new DialogDashboardConfirmOpenFanpage(activity, getContext().getResources().getString(R.string.contentOpenFanpageSohaCare), new InteractConfirmOpenFanpage() {

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onAccept() {
//                        dismiss();
                        startSohaCare(activity, "com.soha.sohacustomerservices");
                        STracker.trackEvent("sdk", STracker.ACTION_CLOSE_DB, "");
                    }
                });
                mDialogConfirm.show();
            } else {
                isClickGotoDetailDB = true;
                Intent i = new Intent(activity, SActivity.class);
                i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_SHOW_DETAIL_DASHBOARD);
                i.putExtra(Constants.BUNDLE_EXTRA_DATA_2, mDashBoard.getUrl());
                dismiss();
                activity.startActivity(i);
            }
        } else if (mDashBoard.getActive() == 0) {
            Toast.makeText(getContext(), mDashBoard.getMessageActive(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setOnEventDashBoard(OnEventDashBoard onEventDashBoard) {
        this.onEventDashBoard = onEventDashBoard;
    }

    public interface OnEventDashBoard {
        void onDismitDialog();
    }

    public void startFacebookPage(Context context, String pageId) {
        Intent facebookIntent = null;
        try {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
        } catch (Exception e) {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + pageId));
        }
        if (facebookIntent.resolveActivity(getContext().getPackageManager()) != null) {
            context.startActivity(facebookIntent);
        } else {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + pageId));
            context.startActivity(facebookIntent);
        }
    }

    public void startTwitterPage(Context context, String pageId) {
        Intent twitterintent = null;
        try {
            twitterintent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + pageId));
            twitterintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            twitterintent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL + pageId));
        }
        context.startActivity(twitterintent);
    }

    public void startSohaCare(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("token", PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class).getAccessToken());
        context.startActivity(intent);
    }

//    private class CustomPagerAdapter extends PagerAdapter {
//        LayoutInflater mLayoutInflater;
//        int numberPages;
//
//        private CustomPagerAdapter(Context context, int numberPages) {
//            mLayoutInflater = LayoutInflater.from(context);
//            this.numberPages = numberPages;
//        }
//
//        @Override
//        public int getCount() {
//            return numberPages;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @NonNull
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View itemView = mLayoutInflater.inflate(R.layout.s_dialog_dashboard_grid, container, false);
//            GridView gridView = itemView.findViewById(R.id.gridView);
//            List<DashBoardItem> listDataConvert;
//            if (position == numberPages - 1) {
//                listDataConvert = listDataOrigin.subList(position * sizePage, listDataOrigin.size());
//            } else {
//                listDataConvert = listDataOrigin.subList(position * sizePage, (position + 1) * sizePage);
//            }
//            final DashBoardAdapter adapter = new DashBoardAdapter(activity, listDataConvert);
//            gridView.setAdapter(adapter);
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    DashBoardItem item = (DashBoardItem) adapter.getItem(position);
//                    if (item.getType().equals("sohacare")) {
//                        clickSCare(PACKAGE_S, item);
//                        return;
//                    }
//
//                    if (item.getActive() == 0) {
//                        Utils.showToast(activity, item.getMessageActive());
//                        return;
//                    }
//                    if (item.getActive() == -1) {
//                        return;
//                    }
//                    switch (item.getType()) {
//                        case "page_facebook":
//                            clickPageFb(activity, item.getId_page());
//                            break;
//                        case "page_twitter":
//                            clickPageTwitter(activity, item.getId_page());
//                            break;
//                        case "exit_dashboard":
//                            showDBDialogExit(activity);
//                            break;
//                        case "demo":
//                            Toast.makeText(getContext(), item.getMessageActive(), Toast.LENGTH_SHORT).show();
//                            break;
//                        default:
//                            isClickGotoDetailDB = true;
//                            Intent i = new Intent(activity, SActivity.class);
//                            i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_SHOW_DETAIL_DASHBOARD);
//                            i.putExtra(Constants.BUNDLE_EXTRA_DATA_2, item.getUrl());
//                            dismiss();
//                            activity.startActivity(i);
//                            break;
//                    }
//                }
//            });
//            container.addView(itemView);
//            return itemView;
//        }
//
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//    }

    private void clickSCare(final String packageName, DashBoardItem item) {
        final SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        if (loginResult == null) return;
        if ("play_now".equalsIgnoreCase(loginResult.getType_user())) {
            if (item.getActive() == 0) {
                Utils.showToast(activity, item.getMessageActive());
            }
            return;
        }
        SDialog.showDialog(activity, activity.getString(R.string.s_open_app_s_care), activity.getString(R.string.s_cancel), activity.getString(R.string.s_ok),
                new SOnClickListener() {
                    @Override
                    public void onClick() {//
                    }
                }, new SOnClickListener() {
                    @Override
                    public void onClick() {
                        String accessToken = "";
                        if (loginResult != null) {
                            accessToken = loginResult.getAccessToken();
                        }
                        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
                        if (intent == null) {
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + packageName));
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("token", accessToken);
                        activity.startActivity(intent);
                        //dismiss();
                    }
                });
    }

    private void clickPageTwitter(final Context context, final String pageId) {
        SDialog.showDialog(context, context.getString(R.string.s_open_app_twitter), context.getString(R.string.s_cancel), context.getString(R.string.s_ok),
                new SOnClickListener() {
                    @Override
                    public void onClick() {//
                    }
                }, new SOnClickListener() {
                    @Override
                    public void onClick() {
                        Intent twitterintent;
                        try {
                            twitterintent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + pageId));
                            twitterintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {
                            twitterintent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL + pageId));
                        }
                        context.startActivity(twitterintent);

                        //dismiss();
                    }
                });
    }

    private void clickPageFb(final Context context, final String pageId) {
        SDialog.showDialog(context, context.getString(R.string.s_open_app_fb), context.getString(R.string.s_cancel), context.getString(R.string.s_ok),
                new SOnClickListener() {
                    @Override
                    public void onClick() {//
                    }
                }, new SOnClickListener() {
                    @Override
                    public void onClick() {
//                        DashBoardPopup.getInstance().showPopup();
//                        SPopup.getInstance().showPopupWarning();
                        Intent facebookIntent;
                        try {
                            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
                        } catch (Exception e) {
                            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + pageId));
                        }
                        if (facebookIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(facebookIntent);
                        } else {
                            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + pageId));
                            context.startActivity(facebookIntent);
                        }
                        //dismiss();
                    }
                });
    }

    private void showDBDialogExit(Context context) {
        SDialog.showDialog(context, context.getString(R.string.s_dashboard_exit), context.getString(R.string.s_cancel), context.getString(R.string.s_ok),
                new SOnClickListener() {
                    @Override
                    public void onClick() {//
//                        DashBoardPopup.getInstance().showPopup();
//                        SPopup.getInstance().showPopupWarning();
                    }
                }, new SOnClickListener() {
                    @Override
                    public void onClick() {
                        SPopup.getInstance().showPopupWarning();
                        DashBoardPopup.getInstance().clearPopup();
                        STracker.trackEvent("sdk", STracker.ACTION_REMOVE_DB, "");
                        isRemoveDB = true;
                        dismiss();
                    }
                });
    }


    private class DashBoardAdapter extends BaseAdapter {
        private final Context mContext;
        private List<DashBoardItem> listData;
        private boolean isMore;

        private DashBoardAdapter(Context context, List<DashBoardItem> dashBoards, boolean isMore) {
            this.mContext = context;
            this.listData = dashBoards;
            this.isMore = isMore;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            GridView grid = (GridView) parent;
            int size = grid.getColumnWidth();
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.s_dashboard_item, parent, false);
                convertView.setLayoutParams(new GridView.LayoutParams(size, size));
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final DashBoardItem mDashBoard = listData.get(position);

            viewHolder.tvText.setText(mDashBoard.getTitle());
            convertView.setVisibility(View.VISIBLE);
            if (!mDashBoard.getTitle().equals("blank")) {
                if (mDashBoard.getTitle().equals(mContext.getString(R.string.textviewMore))) {
//                    viewHolder.ivIcon.setVisibility(View.VISIBLE);
//                    Picasso.get().load(R.drawable.more).into(viewHolder.ivIcon);
                    viewHolder.tvText.setText("more");
                }
            } else {
                if (position != 9) {
                    convertView.setVisibility(View.INVISIBLE);
                }
            }
            if (!TextUtils.isEmpty(mDashBoard.getIcon())) {
                String regexStr = "^[0-9]*$";
                if (mDashBoard.getIcon().matches(regexStr)) {
                    try {
                        Picasso.get().load(Integer.parseInt(mDashBoard.getIcon())).into(viewHolder.ivIcon);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Picasso.get().load(mDashBoard.getIcon()).into(viewHolder.ivIcon);
                    // write code for failure
                }
            }
            if (!TextUtils.isEmpty(String.valueOf(mDashBoard.getNotify()))) {
                int notify;
                try {
                    notify = Integer.parseInt(String.valueOf(mDashBoard.getNotify()));
                } catch (NumberFormatException e) {
                    notify = 0;
                }
                if (notify > 0) {
                    viewHolder.ivNotify.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivNotify.setVisibility(View.GONE);
                }
            } else {
                viewHolder.ivNotify.setVisibility(View.GONE);
            }
            final View finalConvertView = convertView;
            viewHolder.llViewDash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (mDashBoard.getType()) {
                        case "page_facebook":
                            clickPageFb(mContext, mDashBoard.getId_page());
                            break;
                        case "page_twitter":
                            clickPageTwitter(mContext, mDashBoard.getId_page());
                            break;
                        case "exit_dashboard":
                            showDBDialogExit(mContext);
                            break;
                        case "demo":
                            Toast.makeText(getContext(), mDashBoard.getMessageActive(), Toast.LENGTH_SHORT).show();
                            break;
                        case "more":
                            addListData(9, 18);
                            notifyDataSetChanged();
//                            Toast.makeText(getContext(), "Clicked Add", Toast.LENGTH_SHORT).show();
                            break;
                        case DB_BACK:
                            addListData(0, 9);
                            notifyDataSetChanged();
                            break;
                        case "blank":

                            break;
                        default:
                            if (!TextUtils.isEmpty(mDashBoard.getUrl())) {
                                isClickGotoDetailDB = true;
                                Intent i = new Intent(mContext, SActivity.class);
                                i.putExtra(Constants.BUNDLE_EXTRA_DATA, SActivity.ACTION_SHOW_DETAIL_DASHBOARD);
                                i.putExtra(Constants.BUNDLE_EXTRA_DATA_2, mDashBoard.getUrl());
                                dismiss();
                                mContext.startActivity(i);
                            } else {
                                dismiss();
                            }
                            break;
                    }
                }
            });
            return convertView;
        }


    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvText;
        ImageView ivNotify;
        LinearLayout llViewDash;

        private ViewHolder(View view) {
            ivIcon = view.findViewById(R.id.ivIcon);
            tvText = view.findViewById(R.id.tvText);
            ivNotify = view.findViewById(R.id.ivNotify);
            llViewDash = view.findViewById(R.id.ll_view_dash);
        }
    }

    private void addListData(int j, int sizeData) {
        if (listDataOrigin != null && listDataOrigin.size() > 0) {
            listDash.clear();
            for (int i = j; i < sizeData; i++) {
                listDash.add(listDataOrigin.get(i));
            }
        }
    }

    public void setOnClickItem(List<DashBoardItem> list, int pos) {
        DashBoardItem item = list.get(pos);

    }
}

