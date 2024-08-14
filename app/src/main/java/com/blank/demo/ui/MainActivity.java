package com.blank.demo.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.blank.demo.R;
import com.blank.demo.databinding.ActivityMainBinding;
import com.blank.demo.utils.AppUtils;
import com.blank.demo.utils.F0Class;
import com.blank.demo.utils.SnackBarUtil;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.pika.sillyboy.DynamicSoLauncher;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        MMKV.defaultMMKV().clearAll();
        initListener();
        performFileOperations();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void performFileOperations() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @SuppressLint("SetWorldReadable")
                    @Override
                    public void onGranted(@NonNull List<String> list1, boolean b) {
                        try {
                            File privateLibraryFile = getPrivateLibraryFile();
                            DynamicSoLauncher.INSTANCE.loadSoDynamically(privateLibraryFile);
                            float result = F0Class.getInstance().sfdc_get_continuous_f0();
                            Log.e(TAG, "调用sfdc_get_continuous_f0，result = " + result);
                            SnackBarUtil.showShort(mBinding.toolBar, "调用sfdc_get_continuous_f0，result = " + result);
                        } catch (Exception e) {
                            SnackBarUtil.showShort(mBinding.toolBar, e.getMessage());
                            Log.e(TAG, "onGranted: " + e.getMessage());
                        }
                    }

                    @SuppressLint("SetWorldReadable")
                    @NonNull
                    private File getPrivateLibraryFile() throws IOException {
                        String sdcardPath = Environment.getRootDirectory().getAbsolutePath();
                        String externalLibraryPath = sdcardPath + "/data/libics_app.so";
                        String privateDirectoryPath = getFilesDir().getAbsolutePath();
                        File externalLibraryFile = new File(externalLibraryPath);
                        File privateLibraryFile = new File(privateDirectoryPath, "libics_app.so");
                        FileInputStream fis = new FileInputStream(externalLibraryFile);
                        FileOutputStream fos = new FileOutputStream(privateLibraryFile);
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                        fis.close();
                        fos.close();
                        privateLibraryFile.setReadable(true, false);
                        privateLibraryFile.setExecutable(true, false);
                        return privateLibraryFile;
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        performFileOperations();
                    }
                });
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_config) {
            toConfigUI();
        } else if (id == R.id.rl_config1) {
            toManualTestUI();
        } else if (id == R.id.rl_config2) {
            toAutoTestUI();
        } else if (id == R.id.clear) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Prompt")
                    .setMessage("Clear cache data ？")
                    .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MMKV.defaultMMKV().clearAll();
                            SnackBarUtil.showShort(mBinding.toolBar, "Clear cache success");
                        }
                    }).create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.green_bar_bg));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initListener() {
        mBinding.version.setText("Version: " + AppUtils.getAppVersion(this));
        mBinding.rlConfig.setOnClickListener(this);
        mBinding.rlConfig1.setOnClickListener(this);
        mBinding.rlConfig2.setOnClickListener(this);
        mBinding.clear.setOnClickListener(this);
    }

    private void toConfigUI() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> list, boolean b) {
                        startActivity(new Intent(MainActivity.this, PageOneActivity.class));
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        SnackBarUtil.showShort(mBinding.getRoot(), "Please agree permission first");
                    }
                });
    }

    private void toManualTestUI() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> list, boolean b) {
                        startActivity(new Intent(MainActivity.this, PageTwoActivity.class));
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        SnackBarUtil.showShort(mBinding.getRoot(), "Please agree permission first");
                    }
                });
    }

    private void toAutoTestUI() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> list, boolean b) {
                        startActivity(new Intent(MainActivity.this, PageThreeActivity.class));
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        SnackBarUtil.showShort(mBinding.getRoot(), "Please agree permission first");
                    }
                });
    }
}