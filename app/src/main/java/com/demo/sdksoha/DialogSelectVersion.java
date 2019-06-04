package com.demo.sdksoha;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

import vn.sohagame.sdkdemo.R;


/**
 * demo dialog select server, don't use in your project
 */
public class DialogSelectVersion extends Dialog {
    private ICallbackSelectVersion callbackSelectVersion;
    int version = 2;

    public DialogSelectVersion(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_select_version);
        RadioGroup rdServer = findViewById(R.id.rdServer);
        rdServer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rbS1) {
                    version = 0;
                } else if (checkedId == R.id.rbS2) {
                    version = 1;
                } else if (checkedId == R.id.rbS3) {
                    version = 2;
                }
            }
        });
        Button button = findViewById(R.id.btnOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callbackSelectVersion.onSelectItem(version);
            }
        });

    }


    public interface ICallbackSelectVersion {
        void onSelectItem(int version);
    }

    public void setCallbackSelectVersion(ICallbackSelectVersion callbackSelectVersion) {
        this.callbackSelectVersion = callbackSelectVersion;
    }
}
