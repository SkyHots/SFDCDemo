package com.blank.demo;

import android.app.Application;
import android.util.Log;

import com.blank.demo.utils.SPUtils;
import com.pika.sillyboy.DynamicSoLauncher;
import com.tencent.mmkv.MMKV;

import java.io.File;

public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtils.init(this);
        MMKV.initialize(this);
        dynamicInstallSo();
    }

    private void dynamicInstallSo() {
        String path = getFilesDir().getAbsolutePath() + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        DynamicSoLauncher.INSTANCE.initDynamicSoConfig(this, path, s -> {
            Log.e(TAG, "initDynamicSoConfig: Start！！！");
            return true;
        });
    }

}
