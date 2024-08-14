package com.blank.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Created by    blank
 */
public class SPUtils {

    private static final String PREFERENCE_NAME = "my_preferences";
    private static SharedPreferences mSp;
    public static final String SAVED = "SAVED";
    public static final String SPACE = "SPACE";
    public static final String RANGE = "RANGE";
    public static final String OFF_SET = "OFF_SET";
    public static final String LRA_TYPE = "LRA_TYPE";
    public static final String FILE = "FILE";

    public static void init(Context context) {
        mSp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String defaultValue) {
        return mSp.getString(key, defaultValue);
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mSp.getBoolean(key, defaultValue);
    }
}