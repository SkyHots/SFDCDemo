package com.blank.demo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.blank.Constants;
import com.blank.demo.R;
import com.blank.demo.bean.PageOneData;
import com.blank.demo.bean.ScanConfig;
import com.blank.demo.bean.SfdcParameter;
import com.blank.demo.databinding.ActivityPageOneBinding;
import com.blank.demo.ui.adapter.MyPageOneAdapter;
import com.blank.demo.utils.CharSequence2String;
import com.blank.demo.utils.F0Class;
import com.blank.demo.utils.FileReaderLineUtil;
import com.blank.demo.utils.LoadingDialog;
import com.blank.demo.utils.SnackBarUtil;
import com.blank.demo.utils.WaveFormUtil;
import com.blank.demo.view.CustomArrayAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PageOneActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PageOneActivity";
    private String[] mAllFile = {};
    private TreeSet<String> mSelectedFileList = new TreeSet<>();
    private ActivityPageOneBinding mBinding;
    private Spinner mSpinnerLraType;
    private String[] mLraTypes;
    private List<SfdcParameter> mParamList = new ArrayList<>();
    private float mNumberStep = 1;
    private float mNumberWindow = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPageOneBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();
    }

    private void initListener() {
        mBinding.imBack.setOnClickListener(this);
        mBinding.btnSelectFile.setOnClickListener(this);
        mBinding.run.setOnClickListener(this);
        mBinding.apply.setOnClickListener(this);
    }

    private void initView() {
        mBinding.mInputNumberStep.setInputNumListener(number -> {
            mNumberStep = number;
        });
        mBinding.mInputNumberWindow.setInputNumListener(number -> {
            mNumberWindow = number;
        });
        String pageOneResult = MMKV.defaultMMKV().decodeString("pageOneResult", null);
        if (pageOneResult != null) {
            List<SfdcParameter> data = new Gson().fromJson(pageOneResult, new TypeToken<List<SfdcParameter>>() {
            }.getType());
            if (!data.isEmpty()) {
                mParamList = data;
                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mBinding.recyclerView.setAdapter(new MyPageOneAdapter(data, this));
            } else {
                mParamList = new ArrayList<>();
            }
        }
        getCheckedItems();
        mBinding.apply.setEnabled(false);
        mSpinnerLraType = mBinding.spinnerLraType;
        mLraTypes = CharSequence2String.convertToStringArray(getResources().getTextArray(R.array.lraTypeItems));
        CustomArrayAdapter adapter = new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, mLraTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLraType.setAdapter(adapter);
        mSpinnerLraType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinnerLraType.setSelection(0);
        PageOneData pageOneData = MMKV.defaultMMKV().decodeParcelable("pageOneData", PageOneData.class);
        if (pageOneData != null) {
            mBinding.mInputNumberStep.setDefaultNumber(Float.parseFloat(pageOneData.getTvStep()));
            mBinding.mInputNumberWindow.setDefaultNumber(Float.parseFloat(pageOneData.getTvWindow()));
            String lraType = pageOneData.getLraType();
            int indexOfLraType = -1;
            for (int i = 0; i < mLraTypes.length; i++) {
                if (lraType.equals(mLraTypes[i])) {
                    indexOfLraType = i;
                    break;
                }
            }
            if (indexOfLraType != -1) {
                mSpinnerLraType.setSelection(indexOfLraType);
            } else {
                mSpinnerLraType.setSelection(0);
            }
        }
    }

    private void showFileSelectionDialog() {
        WaveFormUtil waveFormUtil = new WaveFormUtil(this);
        XXPermissions with = XXPermissions.with(this);
        with.permission(Permission.MANAGE_EXTERNAL_STORAGE);
        with.request((list, b) -> {
            List<File> files = waveFormUtil.listFiles(Constants.WAVEFORM_DIC);
            files.sort(Comparator.comparing(File::getName));
            mAllFile = new String[files.size()];
            for (int i = 0; i < files.size(); i++) {
                mAllFile[i] = files.get(i).getName();
            }
            boolean[] checkedItems = getCheckedItems();
            AlertDialog.Builder builder = new AlertDialog.Builder(PageOneActivity.this);
            builder.setPositiveButton("Confirm", null);
            builder.setTitle("Choose File")
                    .setCancelable(true)
                    .setMultiChoiceItems(mAllFile, checkedItems, (dialog, which, isChecked) -> {
                        if (mAllFile != null && which < mAllFile.length) {
                            String fileName = mAllFile[which];
                            if (isChecked) {
                                mSelectedFileList.add(fileName);
                            } else {
                                mSelectedFileList.remove(fileName);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            button.setAllCaps(false);
            buttonNegative.setAllCaps(false);
            button.setTextColor(getResources().getColor(R.color.green_bar_bg));
            button.setOnClickListener(v -> addWaveform(dialog));
        });
    }

    private void addWaveform(AlertDialog dialog) {
        int size = mSelectedFileList.size();
        if (size == 0) {
            SnackBarUtil.showShort(mSpinnerLraType, "Please select file");
        } else {
            mParamList = new ArrayList<>();
            int resultClear = F0Class.getInstance().sfdc_wave_clear();
            if (resultClear == 1) {
                SnackBarUtil.showShort(mBinding.spinnerLraType, "Clear wave failure");
            } else {
                for (String name : mSelectedFileList) {
                    String filePath = Constants.WAVEFORM_DIC + name;
                    short fileLines = FileReaderLineUtil.readLinesLength(filePath);
                    byte[] content = FileReaderLineUtil.readLinesToByteArray(filePath);
                    Log.e(TAG, "name: " + name + ",length=" + fileLines + ",content=" + Arrays.toString(content));
                    if (content.length == 0) {
                        SnackBarUtil.showShort(mBinding.spinnerLraType, "File is null");
                        return;
                    }
                    int result = F0Class.getInstance().sfdc_wave_add(Constants.WAVEFORM_PATH + name, fileLines, content);
                    if (result == 0) {
                        mParamList.add(new SfdcParameter(name, 0, 0, 0, 0, 0));
                    } else {
                        SnackBarUtil.showShort(mSpinnerLraType, "Add wave failure");
                        return;
                    }
                }
                SnackBarUtil.showShort(mSpinnerLraType, "Add wave success");
                MyPageOneAdapter adapter = new MyPageOneAdapter(mParamList, PageOneActivity.this);
                mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(PageOneActivity.this, LinearLayoutManager.VERTICAL,
                        false));
                mBinding.recyclerView.setAdapter(adapter);
                dialog.dismiss();
                MMKV.defaultMMKV().encode("pageOneResult", new Gson().toJson(mParamList));
                MMKV.defaultMMKV().encode("selectFile", mSelectedFileList);
                MMKV.defaultMMKV().remove("autoTestCase");
            }
        }
    }

    @NonNull
    private boolean[] getCheckedItems() {
        mSelectedFileList.clear();
        Set<String> selectFile = MMKV.defaultMMKV().decodeStringSet("selectFile", null);
        boolean[] checkedItems = new boolean[mAllFile.length];
        if (selectFile != null) {
            for (int i = 0; i < mAllFile.length; i++) {
                String fileName = mAllFile[i];
                boolean contains = selectFile.contains(fileName);
                checkedItems[i] = contains;
                if (contains) {
                    mSelectedFileList.add(fileName);
                }
            }
        } else {
            mSelectedFileList = new TreeSet<>(Arrays.asList(mAllFile));
            Arrays.fill(checkedItems, true);
        }
        return checkedItems;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imBack) {
            finish();
        } else if (id == R.id.run) {
            mBinding.apply.setEnabled(false);
            ScanConfig config = new ScanConfig();
            config.setFreq_step(mNumberStep);
            config.setFreq_window(mNumberWindow);
            if (mParamList != null && !mParamList.isEmpty()) {
                mBinding.run.setEnabled(false);
                LoadingDialog.get().show(this);
                SfdcParameter[] array = mParamList.toArray(new SfdcParameter[0]);
                String lraType = mLraTypes[mSpinnerLraType.getSelectedItemPosition()];
                PageOneActivity context = this;
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    SfdcParameter[] result = F0Class.getInstance().sfdc_param_cali_run(lraType, config, array);
                    MMKV.defaultMMKV().encode("pageOneResult", new Gson().toJson(Arrays.asList(array)));
                    runOnUiThread(() -> {
                        LoadingDialog.get().dismiss();
                        if (result != null && result.length != 0 && result[0].getResult() == 0) {
                            mBinding.run.setEnabled(true);
                            mBinding.apply.setEnabled(true);
                            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                                    false));
                            mBinding.recyclerView.setAdapter(new MyPageOneAdapter(Arrays.asList(array), context));
                            SnackBarUtil.showShort(mSpinnerLraType, "Run success");
                        } else {
                            SnackBarUtil.showShort(mSpinnerLraType, "Run failure");
                        }
                        executor.shutdown();
                    });
                });
            } else {
                SnackBarUtil.showShort(mSpinnerLraType, "Please choose file first");
            }
        } else if (id == R.id.apply) {
            /*int result = F0Class.getInstance().sfdc_param_cali_apply();
            if (result == 0) {
                SnackBarUtil.showShort(mSpinnerLraType, "Apply success");
            } else {
                SnackBarUtil.showShort(mSpinnerLraType, "Apply failure");
            }*/
        } else if (id == R.id.btnSelectFile) {
            showFileSelectionDialog();
        }
    }

    @Override
    protected void onDestroy() {
        PageOneData pageOneData = new PageOneData(String.valueOf(mNumberStep), String.valueOf(mNumberWindow),
                mLraTypes[mSpinnerLraType.getSelectedItemPosition()]);
        MMKV.defaultMMKV().encode("pageOneData", pageOneData);
        super.onDestroy();
    }
}