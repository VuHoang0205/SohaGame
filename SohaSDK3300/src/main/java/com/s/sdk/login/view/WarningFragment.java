package com.s.sdk.login.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.s.sdk.R;
import com.s.sdk.base.BaseWebViewFragment;
import com.s.sdk.base.Constants;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.utils.PrefUtils;

public class WarningFragment extends BaseWebViewFragment {
    public static final String TAG = LoginFragment.class.getName();
    private ProgressBar mProgressLoading;
    private TextView mTxtTitle;
    private RelativeLayout mRlToolbar;
    private ImageButton ibtnClose;

    public static WarningFragment newInstance() {
        return new WarningFragment();
    }

    @Override
    protected void onActivityCreated() {

    }

    @Override
    protected String getURLRequest() {
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        return data.getUrl_warning();
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
        mRlToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onJavaScriptInteract(String method, String value) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.s_fragment_login;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mRlToolbar = view.findViewById(R.id.rl_toolbar);
        mProgressLoading = view.findViewById(R.id.progressLoading);
        mTxtTitle = view.findViewById(R.id.txt_title);
        ibtnClose = view.findViewById(R.id.ibtn_close);
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        if (loginResult != null && !TextUtils.isEmpty(loginResult.getUsername())) {
            mTxtTitle.setText(loginResult.getUsername());
        }
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
