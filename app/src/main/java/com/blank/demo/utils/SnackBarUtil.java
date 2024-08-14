package com.blank.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtil {

    public static void showShort(View view, String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Snackbar snackbar = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_SHORT);
            setSnackbarTextAlignment(snackbar);
            snackbar.show();
            new Handler().postDelayed(snackbar::dismiss, 400);
        });
    }

    public static void showLong(View view, String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Snackbar snackbar = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_LONG);
            setSnackbarTextAlignment(snackbar);
            snackbar.show();
        });
    }

    private static void setSnackbarTextAlignment(Snackbar snackbar) {
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private static void centerSnackbar(View view, Snackbar snackbar) {
        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        params.bottomMargin = view.getHeight() / 2; // Adjust this value according to your needs
        snackbarView.setLayoutParams(params);
    }
}