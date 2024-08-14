package com.blank.demo.utils;

import android.app.AlertDialog;
import android.content.Context;

public class CustomDialog {
    public static void show(Context context,String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}