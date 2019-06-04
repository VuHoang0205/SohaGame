package com.s.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.s.sdk.CallbackManager;
import com.s.sdk.R;
import com.s.sdk.SCallback;
import com.s.sdk.base.Constants;
import com.s.sdk.base.SOnClickListener;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.squareup.picasso.Picasso;

public class SDialog {
    public static Dialog showDialog(Context context, String message,
                                    String textCancel, String textOk, final SOnClickListener onClickCancel, final SOnClickListener onClickOk) {
        final Dialog mDialog = new Dialog(context, R.style.sThemeDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.s_dialog_generic);
        mDialog.show();

        TextView tvDes = mDialog.findViewById(R.id.tvDes);
        if (!TextUtils.isEmpty(message)) {
            tvDes.setText(message);
        }

        Button btnOk = mDialog.findViewById(R.id.btnOk);
        if (!TextUtils.isEmpty(textOk)) {
            btnOk.setText(textOk);
        } else {
            btnOk.setVisibility(View.GONE);
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickOk != null) onClickOk.onClick();
                mDialog.dismiss();
            }
        });

        Button btnCancel = mDialog.findViewById(R.id.btnCancel);
        if (!TextUtils.isEmpty(textCancel)) {
            btnCancel.setText(textCancel);
        } else {
            btnCancel.setVisibility(View.GONE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onClickCancel != null) onClickCancel.onClick();
            }
        });
        return mDialog;
    }

    public static void showDialogUpdate(final Context context, boolean isForceUpdate, final String linkUpdate, final SOnClickListener onClickCancel) {

        final Dialog mDialog = new Dialog(context, R.style.sThemeDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.s_dialog_generic);
        mDialog.setCancelable(false);
        mDialog.show();

        TextView tvDes = mDialog.findViewById(R.id.tvDes);
        tvDes.setText(String.format(context.getString(R.string.update_des_update), Utils.getAppName(context)));

        Button btnOk = mDialog.findViewById(R.id.btnOk);
        btnOk.setText(context.getString(R.string.update_download_now));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(linkUpdate)));
                } catch (Exception e) {
                }
                ((Activity) context).finish();
                SCallback<SLoginResult> callback = CallbackManager.getLoginCallback();
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });

        Button btnCancel = mDialog.findViewById(R.id.btnCancel);
        btnCancel.setText(context.getString(R.string.update_continue));
        View vDivider2 = mDialog.findViewById(R.id.vDivider2);
        if (isForceUpdate) {
            btnCancel.setVisibility(View.GONE);
            vDivider2.setVisibility(View.GONE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onClickCancel != null) {
                    onClickCancel.onClick();
                }

            }
        });

//        final Dialog mDialog = new Dialog(context);
//        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.setContentView(R.layout.soha_dialog_update);
//        mDialog.setCancelable(false);
//        if (isForceUpdate) {
//            mDialog.findViewById(R.id.btContinue).setVisibility(View.GONE);
//        }
//        mDialog.show();
//
//        ImageView ivGameIcon = mDialog.findViewById(R.id.ivGameIcon);
//        TextView tvFormatUpdate = mDialog.findViewById(R.id.tvFormatUpdate);
//        ivGameIcon.setImageResource(Utils.getAppIcon(context));
//
//        tvFormatUpdate.setText(String.format(context.getString(R.string.update_des_update), Utils.getAppName(context)));
//
//        mDialog.findViewById(R.id.btDownloadNow).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse(linkUpdate)));
//                    ((Activity) context).finish();
//                } catch (Exception e) {
//                }
//
//            }
//        });
//        mDialog.findViewById(R.id.btContinue).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDialog.dismiss();
//                if (onClickContinueListener != null) {
//                    onClickContinueListener.onClick();
//                }
//            }
//        });
    }


    public static void showDialogWarning(Context context) {
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        final Dialog mDialog = new Dialog(context, R.style.sThemeDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.s_dialog_warning_image);
        mDialog.show();
        ImageView imageView = mDialog.findViewById(R.id.ivWarning);
        String url = data.getImage_age();
        if (!TextUtils.isEmpty(url)) {
            Picasso.get().load(url).fit().into(imageView);
        }

        TextView tvWarning = mDialog.findViewById(R.id.tvWarning);
        String message = data.getWarning_time_message();
        if (!TextUtils.isEmpty(message)) {
            tvWarning.setText(message);
        }

        ImageView ibClose = mDialog.findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        //return mDialog;
    }
}
