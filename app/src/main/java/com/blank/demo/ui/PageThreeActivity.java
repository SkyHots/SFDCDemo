package com.blank.demo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blank.Constants;
import com.blank.demo.R;
import com.blank.demo.bean.PlayRecord;
import com.blank.demo.bean.TcConfig;
import com.blank.demo.bean.TcRecord;
import com.blank.demo.bean.TcWaveConfig;
import com.blank.demo.databinding.ActivityPageThreeBinding;
import com.blank.demo.ui.adapter.MyPageThreeAdapter;
import com.blank.demo.utils.CustomDialog;
import com.blank.demo.utils.F0Class;
import com.blank.demo.utils.FileUtil;
import com.blank.demo.utils.LoadingDialog;
import com.blank.demo.utils.SnackBarUtil;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PageThreeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PageThreeActivity";
    private ActivityPageThreeBinding mBinding;
    private MyPageThreeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPageThreeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();
    }

    private void initView() {
        ArrayList<TcConfig> dataList = new ArrayList<>();
        mAdapter = new MyPageThreeAdapter(dataList, this);
        mAdapter.setCallBack(new MyPageThreeAdapter.CallBack() {
            @Override
            public void clickEdit(String caseName) {
                Intent intent = new Intent(PageThreeActivity.this, PageFourActivity.class);
                intent.putExtra("caseName", caseName);
                startActivity(intent);
            }

            @Override
            public void clickDelete(String caseName, int index) {
                AlertDialog alertDialog = new AlertDialog.Builder(PageThreeActivity.this)
                        .setMessage("Delete " + caseName + " ?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            int result = F0Class.getInstance().sfdc_tc_delete(caseName);
                            if (result == 0) {
                                SnackBarUtil.showShort(mBinding.imBack, "Delete case success");
                                deleteCase(caseName, index);
                            } else {
                                SnackBarUtil.showShort(mBinding.imBack, "Delete case failure, result = " + result);
                            }
                        }).setNegativeButton("Cancel", (dialog, which) -> {}).create();
                alertDialog.show();
                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button.setAllCaps(false);
                buttonNegative.setAllCaps(false);
                button.setTextColor(getResources().getColor(R.color.green_text));
            }
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void deleteCase(String caseName, int index) {
        List<TcConfig> tcConfigs = getTcConfigs();
        tcConfigs.removeIf(tcConfig -> tcConfig.getTc_name().equals(caseName));
        String updatedAutoTestCase = new Gson().toJson(tcConfigs);
        MMKV.defaultMMKV().encode("autoTestCase", updatedAutoTestCase);
        refreshCase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCase();
    }

    private void refreshCase() {
        List<TcConfig> tcConfigs = getTcConfigs();
        if (mAdapter != null) {
            mAdapter.setDataList(tcConfigs);
        } else {
            mAdapter = new MyPageThreeAdapter(tcConfigs, PageThreeActivity.this);
            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mBinding.recyclerView.setAdapter(mAdapter);
        }
    }

    private void initListener() {
        mBinding.imBack.setOnClickListener(this);
        mBinding.btnAddCase.setOnClickListener(this);
        mBinding.btnTestCase.setOnClickListener(this);
        mBinding.btnLoadTestCase.setOnClickListener(this);
        mBinding.temperatureValue.setOnClickListener(v -> {
            String text = mBinding.temperatureValue.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                CustomDialog.show(PageThreeActivity.this, "temperatureValue", text);
            }
        });
        mBinding.BEMFValue.setOnClickListener(v -> {
            String text = mBinding.BEMFValue.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                CustomDialog.show(PageThreeActivity.this, "BEMFValue", text);
            }
        });
        mBinding.f0Value.setOnClickListener(v -> {
            String text = mBinding.f0Value.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                CustomDialog.show(PageThreeActivity.this, "f0Value", text);
            }
        });
    }

    private void showLineChart(List<Entry> mBEMFEntries, List<Entry> mF0Entries) {
        LineDataSet dataSetBEMF = new LineDataSet(mBEMFEntries, "BEMF");
        dataSetBEMF.setColor(getResources().getColor(R.color.chart1_color));
        dataSetBEMF.setValueTextColor(Color.BLACK);
        dataSetBEMF.setValueTextSize(10);
        dataSetBEMF.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet dataSetF0 = new LineDataSet(mF0Entries, "f0");
        dataSetF0.setColor(getResources().getColor(R.color.chart2_color));
        dataSetF0.setValueTextColor(Color.BLACK);
        dataSetF0.setValueTextSize(10);
        dataSetF0.setAxisDependency(YAxis.AxisDependency.RIGHT);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetBEMF);
        dataSets.add(dataSetF0);

        LineData lineData = new LineData(dataSets);
        lineData.setValueFormatter(new DefaultValueFormatter(1));

        mBinding.lineChat1.setData(lineData);
        mBinding.lineChat1.getDescription().setEnabled(false);
        mBinding.lineChat1.setDrawGridBackground(false);
        mBinding.lineChat1.getXAxis().setDrawGridLines(false);
        mBinding.lineChat1.getAxisLeft().setDrawGridLines(false);
        mBinding.lineChat1.getAxisRight().setDrawGridLines(false);
        mBinding.lineChat1.getAxisRight().setEnabled(true);
        mBinding.lineChat1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBinding.lineChat1.getXAxis().setValueFormatter(new IndexAxisValueFormatter());
        mBinding.lineChat1.setScaleEnabled(false);

        float minY = Math.min(getMinValue(mBEMFEntries), getMinValue(mF0Entries));
        float maxY = Math.max(getMaxValue(mBEMFEntries), getMaxValue(mF0Entries));
        float yRange = maxY - minY;
        float offset = yRange * 0.1f; // 间隔为总范围的 10%

        mBinding.lineChat1.getAxisLeft().setAxisMinimum(minY - offset);
        mBinding.lineChat1.getAxisLeft().setAxisMaximum(maxY + offset);
        mBinding.lineChat1.getAxisRight().setAxisMinimum(minY - offset);
        mBinding.lineChat1.getAxisRight().setAxisMaximum(maxY + offset);

        YAxis leftAxis = mBinding.lineChat1.getAxisLeft();
        leftAxis.setValueFormatter(new DefaultValueFormatter(1));
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = mBinding.lineChat1.getAxisRight();
        rightAxis.setValueFormatter(new DefaultValueFormatter(1));
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);

        mBinding.lineChat1.setVisibleXRangeMaximum(10);
        mBinding.lineChat1.moveViewToX(mF0Entries.size() - 1);
        mBinding.lineChat1.notifyDataSetChanged();
        mBinding.lineChat1.invalidate();
    }

    // 辅助方法：获取数据集中的最小值
    private float getMinValue(List<Entry> entries) {
        float minValue = Float.MAX_VALUE;
        for (Entry entry : entries) {
            if (entry.getY() < minValue) {
                minValue = entry.getY();
            }
        }
        return minValue;
    }

    // 辅助方法：获取数据集中的最大值
    private float getMaxValue(List<Entry> entries) {
        float maxValue = Float.MIN_VALUE;
        for (Entry entry : entries) {
            if (entry.getY() > maxValue) {
                maxValue = entry.getY();
            }
        }
        return maxValue;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imBack) {
            finish();
        } else if (id == R.id.btnAddCase) {
            addCase();
        } else if (id == R.id.btnTestCase) {
            sfdcTestCaseExecute();
        } else if (id == R.id.btnLoadTestCase) {
            loadTestCase();
        }
    }

    private void addCase() {
        Set<String> selectFile = MMKV.defaultMMKV().decodeStringSet("selectFile", null);
        if (selectFile == null) {
            SnackBarUtil.showShort(mBinding.btnAddCase, "Please choose waveform first");
            return;
        }
        Intent intent = new Intent(this, PageFourActivity.class);
        startActivity(intent);
    }

    private void sfdcTestCaseExecute() {
        if (mAdapter != null) {
            List<TcConfig> dataList = mAdapter.getDataList();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            LoadingDialog.get().show(this);
            executor.execute(() -> {
                for (int i = 0; i < dataList.size(); i++) {
                    TcConfig tcConfig = dataList.get(i);
                    String tcName = tcConfig.getTc_name();
                    int repeatLoop = tcConfig.getRepeat_loop();
                    TcWaveConfig[] waveConfig = tcConfig.getWave_config();
                    int count = Arrays.stream(waveConfig).mapToInt(TcWaveConfig::getPlayback_count).sum();
                    ArrayList<PlayRecord> playRecordList = new ArrayList<>();
                    for (int j = 0; j < tcConfig.getWave_config().length; j++) {
                        playRecordList.add(new PlayRecord((short) 0, 0, 0, 0));
                    }
                    TcRecord tcRecord = new TcRecord(playRecordList);

                    String recordPath = getFilesDir().getPath() + File.separator + tcName + ".txt";
                    File file = new File(recordPath);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            SnackBarUtil.showShort(mBinding.imBack, "CreateNewFile failure," + e.getMessage());
                        }
                    }
                    TcRecord record = F0Class.getInstance().sfdc_tc_execute(recordPath, tcName, tcRecord,
                            count * repeatLoop);
                    ArrayList<PlayRecord> recordList = record.getPlay_record_list();
                    runOnUiThread(() -> {
                        if (record.getResult() == 0 && !recordList.isEmpty()) {
                            SnackBarUtil.showShort(mBinding.imBack, "Execute " + tcName + " success");
                            mBinding.caseName.setText(tcName);
                            List<Entry> entriesBEMF = new ArrayList<>();
                            List<Entry> entriesF0 = new ArrayList<>();
                            List<Float> recordTemperature = record.getTemperature();

                            List<Float> recordBemf = record.getBemf();
                            List<Float> recordF0 = record.getF0();
                            for (int j = 0; j < recordList.size(); j++) {
                                entriesBEMF.add(new Entry(j, recordBemf.get(j)));
                                entriesF0.add(new Entry(j, recordF0.get(j)));
                            }
                            showLineChart(entriesBEMF, entriesF0);
                            mBinding.temperatureValue.setText(Arrays.toString(recordTemperature.toArray()));
                            mBinding.BEMFValue.setText(Arrays.toString(recordBemf.toArray()));
                            mBinding.f0Value.setText(Arrays.toString(recordF0.toArray()));
                        } else {
                            SnackBarUtil.showShort(mBinding.imBack, "Execute failure，result = " + record.getResult());
                        }
                    });
                }
                LoadingDialog.get().dismiss();
                executor.shutdown();
            });
        }
    }

    private List<String> getJsonFileList() {
        List<String> fileList = new ArrayList<>();
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.SFDC_CASE);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        fileList.add(file.getName());
                    }
                }
            }
        }
        return fileList;
    }

    private void loadTestCase() {
        List<String> fileList = getJsonFileList();
        fileList.sort(String::compareTo);
        if (fileList.isEmpty()) {
            SnackBarUtil.showShort(mBinding.btnLoadTestCase, "No file in the /sdcard/download/sfdcCase/");
        } else {
            String[] fileNames = fileList.toArray(new String[0]);
            boolean[] checkedItems = new boolean[fileNames.length];
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Load test case from json file")
                    .setMultiChoiceItems(fileNames, checkedItems,
                            (dialog, which, isChecked) -> checkedItems[which] = isChecked)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        List<String> selectedFileNames = new ArrayList<>();
                        IntStream.range(0, checkedItems.length)
                                .filter(i -> checkedItems[i])
                                .mapToObj(i -> fileNames[i])
                                .forEach(selectedFileNames::add);
                        selectedFileNames.sort(String::compareTo);
                        for (int i = 0; i < selectedFileNames.size(); i++) {
                            String name = selectedFileNames.get(i);
                            String jsonContent =
                                    FileUtil.readFileContent(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                                    + "/" + Constants.SFDC_CASE + "/" + name);
                            List<TcConfig> tcConfigs = getTcConfigs();
                            if (!TextUtils.isEmpty(jsonContent)) {
                                try {
                                    TcConfig tcConfig = new Gson().fromJson(jsonContent, TcConfig.class);
                                    Set<String> selectFile = MMKV.defaultMMKV().decodeStringSet("selectFile", null);
                                    if (selectFile != null && !selectFile.isEmpty()) {
                                        int size = selectFile.size();
                                        if (tcConfig.getWave_config().length == size) {
                                            List<String> tcNames = tcConfigs.stream().map(TcConfig::getTc_name).collect(Collectors.toList());
                                            String tcName = tcConfig.getTc_name();
                                            if (tcNames.contains(tcName)) {
                                                SnackBarUtil.showShort(mBinding.caseName, tcName + " already exists");
                                            } else if (tcConfig.getRepeat_loop() == 0) {
                                                SnackBarUtil.showShort(mBinding.caseName, "Repeat loop can not 0");
                                            } else {
                                                int result = F0Class.getInstance().sfdc_tc_add(tcConfig);
                                                if (result == 0) {
                                                    tcConfigs.add(tcConfig);
                                                    MMKV.defaultMMKV().encode("autoTestCase", new Gson().toJson(tcConfigs));
                                                    refreshCase();
                                                } else {
                                                    SnackBarUtil.showShort(mBinding.imBack, "Add " + tcName + " failure, result " +
                                                            "= " + result);
                                                }
                                            }
                                        } else {
                                            SnackBarUtil.showShort(mBinding.btnLoadTestCase, "Some file is invalid");
                                        }
                                    } else {
                                        SnackBarUtil.showShort(mBinding.btnLoadTestCase, "Please choose file first");
                                    }
                                } catch (Exception e) {
                                    SnackBarUtil.showShort(mBinding.btnLoadTestCase, "File" + name + "is invalid case json");
                                    break;
                                }
                            } else {
                                SnackBarUtil.showShort(mBinding.btnLoadTestCase, "File" + name + "is invalid case json");
                            }

                        }
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            button.setAllCaps(false);
            buttonNegative.setAllCaps(false);
            button.setTextColor(getResources().getColor(R.color.green_bar_bg));
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

}