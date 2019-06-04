package com.s.sdk.dashboard.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.s.sdk.R;
import com.s.sdk.dashboard.model.DashBoardItem;
import com.s.sdk.dashboard.presenter.InteractConfirmOpenFanpage;

import java.util.List;


public class DialogDashboardConfirmOpenFanpage extends Dialog {
    private Context context;

    InteractConfirmOpenFanpage interactice;

    TextView btnAccept, btnCancel;

    List<DashBoardItem> listItem;
    private String mContent;

    public DialogDashboardConfirmOpenFanpage(Context context, String mContent, InteractConfirmOpenFanpage interactice) {
        super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        this.mContent = mContent;
        this.interactice = interactice;
    }

//    public DialogDashboardConfirmOpenFanpage(String content, InteractConfirmOpenFanpage interactice) {
//        super();
//        // TODO Auto-generated constructor stub
//        mActivity = activity;
//        this.interactice = interactice;
//        this.mContent = content;
//        initDialog();
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    public void initDialog() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_dashboard_confirm_open_fanpage);

        ((TextView) findViewById(R.id.tvContent)).setText(mContent);

        btnAccept = findViewById(R.id.tvAccept);
        btnCancel = findViewById(R.id.tvCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                interactice.onCancel();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                interactice.onAccept();
            }
        });
        setCancelable(false);

    }


}
