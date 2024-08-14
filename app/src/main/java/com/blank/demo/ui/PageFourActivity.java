package com.blank.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blank.Constants;
import com.blank.demo.R;
import com.blank.demo.bean.TcConfig;
import com.blank.demo.bean.TcWaveConfig;
import com.blank.demo.databinding.ActivityPageFourBinding;
import com.blank.demo.ui.adapter.MyPageFourAdapter;
import com.blank.demo.utils.F0Class;
import com.blank.demo.utils.FileUtil;
import com.blank.demo.utils.SnackBarUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PageFourActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PageFourActivity";
    private ActivityPageFourBinding mBinding;
    private boolean mIsModify;
    private MyPageFourAdapter mAdapter;
    private TcConfig mTcConfig;
    private String mCaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPageFourBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();
    }

    private void setTcConfig(String caseName) {
        String autoTestCase = MMKV.defaultMMKV().decodeString("autoTestCase");
        List<TcConfig> tcConfigs = new Gson().fromJson(autoTestCase, new TypeToken<List<TcConfig>>() {
        }.getType());
        for (int result = 0; result < tcConfigs.size(); result++) {
            TcConfig tcConfig = tcConfigs.get(result);
            String tcName = tcConfig.getTc_name();
            if (tcName.equals(caseName)) {
                mTcConfig = tcConfig;
                break;
            }
        }
    }

    private void initView() {
        Intent intent = getIntent();
        mCaseName = intent.getStringExtra("caseName");
        if (mCaseName != null) {
            mBinding.caseName.setEnabled(false);
            mIsModify = true;
            mBinding.caseName.setText(mCaseName);
            setTcConfig(mCaseName);
        }
        ArrayList<TcWaveConfig> dataList = new ArrayList<>();
        if (mIsModify) {
            TcWaveConfig[] waveConfig = mTcConfig.getWave_config();
            dataList.addAll(Arrays.asList(waveConfig));
            mBinding.repeat.setValue(mTcConfig.getRepeat_loop());
            mBinding.peer.setValue(mTcConfig.getRecord_interval());
        } else {
            Set<String> selectFileNames = MMKV.defaultMMKV().decodeStringSet("selectFile", null);
            if (selectFileNames != null && !selectFileNames.isEmpty()) {
                short i = 0;
                for (String fileName : selectFileNames) {
                    TcWaveConfig tcWaveConfig = new TcWaveConfig();
                    tcWaveConfig.setWave_index(i);
                    dataList.add(tcWaveConfig);
                    i++;
                }
            }
        }
        mAdapter = new MyPageFourAdapter(dataList, PageFourActivity.this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mBinding.imBack.setOnClickListener(this);
        mBinding.btnSaveCase.setOnClickListener(this);
    }

    private List<String> getLoadPathFileNames() {
        return FileUtil.listFilesInDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + "/" + Constants.SFDC_CASE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imBack) {
            finish();
        } else if (id == R.id.btnSaveCase) {
            short repeatValue = (short) mBinding.repeat.getValue();
            if (repeatValue == 0) {
                SnackBarUtil.showShort(mBinding.caseName, "Repeat loops cannot be 0");
            } else if (mIsModify) {
                if (mTcConfig != null) {
                    ArrayList<TcWaveConfig> dataList = mAdapter.getEditedDataList();
                    mTcConfig.setWave_config(dataList.toArray(new TcWaveConfig[0]));
                    mTcConfig.setRecord_interval((short) mBinding.peer.getValue());
                    mTcConfig.setTc_name(mCaseName);
                    mTcConfig.setRepeat_loop(repeatValue);
                    mTcConfig.setValid_wave_count((short) dataList.size());
                    int result = F0Class.getInstance().sfdc_tc_edit(mTcConfig);
                    String jsonTestCase = new Gson().toJson(mTcConfig);
                    if (result == 0) {
                        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                                + "/" + Constants.SFDC_CASE + "/" + mCaseName + ".json";
                        File file = new File(filePath);
                        try {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        } catch (IOException e) {
                            SnackBarUtil.showShort(mBinding.imBack, "create file FAILURE");
                        }
                        FileUtil.writeJsonToFile(jsonTestCase, filePath);
                        updateCase(mTcConfig);
                        SnackBarUtil.showShort(mBinding.caseName, "Update case success");
                        finish();
                    } else {
                        SnackBarUtil.showShort(mBinding.caseName, "Update case failure, result = " + result);
                    }
                }
            } else {
                // 新增
                if (mAdapter != null) {
                    String caseName = mBinding.caseName.getText().toString();
                    List<TcConfig> tcConfigs = getTcConfigs();
                    List<String> tcNames = tcConfigs.stream().map(TcConfig::getTc_name).collect(Collectors.toList());
                    if (TextUtils.isEmpty(caseName)) {
                        SnackBarUtil.showShort(mBinding.caseName, "Please input test caseName");
                    } else if (tcNames.contains(caseName)) {
                        SnackBarUtil.showShort(mBinding.caseName, "CaseName already exists");
                    } else {
                        mTcConfig = new TcConfig();
                        ArrayList<TcWaveConfig> dataList = mAdapter.getEditedDataList();
                        mTcConfig.setWave_config(dataList.toArray(new TcWaveConfig[0]));
                        mTcConfig.setRecord_interval((short) mBinding.peer.getValue());
                        mTcConfig.setTc_name(caseName);
                        mTcConfig.setRepeat_loop(repeatValue);
                        mTcConfig.setValid_wave_count((short) dataList.size());

                        List<String> loadPathFileNames = getLoadPathFileNames();
                        if (loadPathFileNames.contains(caseName + ".json")) {
                            SnackBarUtil.showShort(mBinding.imBack, caseName + " exist load path!");
                        } else {
                            String jsonTestCase = new Gson().toJson(mTcConfig);
                            Log.e(TAG, "sfdc_tc_add: " + jsonTestCase);
                            int result = F0Class.getInstance().sfdc_tc_add(mTcConfig);
                            if (result == 0) {
                                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                                        + "/" + Constants.SFDC_CASE + "/" + caseName + ".json";
                                File file = new File(filePath);
                                try {
                                    file.getParentFile().mkdirs();
                                    file.createNewFile();
                                } catch (IOException e) {
                                    SnackBarUtil.showShort(mBinding.imBack, "create file FAILURE");
                                }
                                FileUtil.writeJsonToFile(jsonTestCase, filePath);
                                tcConfigs.add(mTcConfig);
                                MMKV.defaultMMKV().encode("autoTestCase", new Gson().toJson(tcConfigs));
                                SnackBarUtil.showShort(mBinding.imBack, "Add success");
                                finish();
                            } else {
                                SnackBarUtil.showShort(mBinding.imBack, "Add failure");
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<TcConfig> getTcConfigs() {
        String autoTestCase = MMKV.defaultMMKV().decodeString("autoTestCase");
        List<TcConfig> tcConfigs;
        if (autoTestCase == null) {
            tcConfigs = new ArrayList<>();
        } else {
            tcConfigs = new Gson().fromJson(autoTestCase, new TypeToken<List<TcConfig>>() {
            }.getType());
        }
        return tcConfigs;
    }

    private void updateCase(TcConfig data) {
        String autoTestCase = MMKV.defaultMMKV().decodeString("autoTestCase");
        List<TcConfig> tcConfigs = new Gson().fromJson(autoTestCase, new TypeToken<List<TcConfig>>() {
        }.getType());
        if (tcConfigs != null) {
            List<TcConfig> configList = tcConfigs.stream()
                    .map(tcConfig -> {
                        if (tcConfig.getTc_name().equals(data.getTc_name())) {
                            tcConfig = data;
                        }
                        return tcConfig;
                    })
                    .collect(Collectors.toList());
            String updatedAutoTestCase = new Gson().toJson(configList);
            Log.e(TAG, "updateCase: " + updatedAutoTestCase);
            MMKV.defaultMMKV().encode("autoTestCase", updatedAutoTestCase);
        }
    }
}