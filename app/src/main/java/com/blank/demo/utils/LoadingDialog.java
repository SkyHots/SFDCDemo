package com.blank.demo.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.blank.demo.R;

public class LoadingDialog {
    private static volatile LoadingDialog instance;
    private Dialog mDialog;

    private LoadingDialog() {
        // 私有构造函数，防止外部实例化
    }

    public static LoadingDialog get() {
        if (instance == null) {
            synchronized (LoadingDialog.class) {
                if (instance == null) {
                    instance = new LoadingDialog();
                }
            }
        }
        return instance;
    }

    public void show(Context context) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            window.setDimAmount(0.2f);
        }
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}