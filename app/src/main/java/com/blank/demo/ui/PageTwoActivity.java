package com.blank.demo.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;

import com.blank.Constants;
import com.blank.demo.R;
import com.blank.demo.bean.PlayRecord;
import com.blank.demo.databinding.ActivityPageTwoBinding;
import com.blank.demo.ui.adapter.MyPageTwoAdapter;
import com.blank.demo.utils.F0Class;
import com.blank.demo.utils.SnackBarUtil;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.rcl.lib.inputnumber.InputNumberView;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

public class PageTwoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PageTwoActivity";
    private ActivityPageTwoBinding mBinding;
    private Set<String> mSelectFile;
    private boolean mIsChecked = true;
    private short mValueOffset = 1;
    private List<Entry> mBEMFEntries, mF0Entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPageTwoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();
    }

    private void initListener() {
        mBinding.imBack.setOnClickListener(this);
        mBinding.btnApplyF0Drift.setOnClickListener(this);
        mBinding.swSFDC.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int result = F0Class.getInstance().sfdc_enable();
                if (result == 0) {
                    SnackBarUtil.showShort(mBinding.swSFDC, "Enabled");
                    mIsChecked = true;
                } else {
                    SnackBarUtil.showShort(mBinding.swSFDC, "Enabled failure");
                }
            } else {
                int result = F0Class.getInstance().sfdc_disable();
                if (result == 0) {
                    SnackBarUtil.showShort(mBinding.swSFDC, "Disabled");
                    mIsChecked = false;
                } else {
                    SnackBarUtil.showShort(mBinding.swSFDC, "Disabled failure");
                }
            }
            MMKV.defaultMMKV().encode("isEnabled", mIsChecked);
        });
        mBinding.longWaveformPlayback.setOnClickListener(v -> {
            int result = F0Class.getInstance().sfdc_long_waveform_playback(Constants.WAVEFORM_PATH + "sin_30T.txt");
            if (result == 0) {
                SnackBarUtil.showShort(mBinding.swSFDC, "Long waveform playback success");
            } else {
                SnackBarUtil.showShort(mBinding.swSFDC, "Long waveform playback failure");
            }
        });
    }

    private void showLineChart(float BEMFValue, float f0Value) {
        if (mBEMFEntries == null) {
            mBEMFEntries = new ArrayList<>();
        }
        if (mF0Entries == null) {
            mF0Entries = new ArrayList<>();
        }
        Entry bemfEntry = new Entry(mBEMFEntries.size(), BEMFValue);
        mBEMFEntries.add(bemfEntry);
        Entry f0Entry = new Entry(mF0Entries.size(), f0Value);
        mF0Entries.add(f0Entry);

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

        mBinding.lineChat1.setVisibleXRangeMaximum(5);
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

    @SuppressLint("SetTextI18n")
    private void initView() {
        mIsChecked = MMKV.defaultMMKV().decodeBool("isEnabled", mIsChecked);
        mBinding.swSFDC.setChecked(mIsChecked);
        mValueOffset = (short) MMKV.defaultMMKV().decodeInt("mValueOffset", 1);
        mBinding.mInputNumberDrift.setDefaultNumber(mValueOffset);
        mBinding.mInputNumberDrift.setInputNumListener((InputNumberView.InputNumListener) number -> {
            mValueOffset = (short) number;
            MMKV.defaultMMKV().encode("mValueOffset", number);
        });
        SpannableString spannableString = new SpannableString("");
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.green_bar_bg)), 0, spannableString.length(), 0);
        mBinding.temperatureValue.setText(spannableString);
        mBinding.BEMFValue.setText(spannableString);
        mBinding.f0Value.setText(spannableString);
        mSelectFile = MMKV.defaultMMKV().decodeStringSet("selectFile", null);
        Log.e(TAG, "initView: " + mSelectFile);
        if (mSelectFile == null)
            mSelectFile = new TreeSet<>();
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<String> dataList = new ArrayList<>(mSelectFile);
        Collections.sort(dataList);
        MyPageTwoAdapter adapter = new MyPageTwoAdapter(dataList, this);
        adapter.setCallBack((waveName, position) -> {
            mBinding.waveName.setText(waveName);
            Log.e(TAG, "clickPlay: position = " + position);
            PlayRecord record = new PlayRecord();
            record.setWave_index((short) position);
            PlayRecord playRecord = F0Class.getInstance().sfdc_wave_play(record);
            if (playRecord.getResult() == 0) {
                SnackBarUtil.showShort(mBinding.imBack, "Play success");
                mBinding.temperatureValue.setText(playRecord.getTemperature() + "");
                mBinding.BEMFValue.setText(playRecord.getRelative_bemf() + "");
                mBinding.f0Value.setText(playRecord.getOutput_f0() + "");
                showLineChart(playRecord.getRelative_bemf(), playRecord.getOutput_f0());
                Log.e(TAG, "playRecord.getRelative_bemf(): " + playRecord.getRelative_bemf());
                Log.e(TAG, "playRecord.getOutput_f0(): " + playRecord.getOutput_f0());
            } else {
                SnackBarUtil.showShort(mBinding.imBack, "Play failure");
            }
        });
        mBinding.recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imBack) {
            finish();
        } else if (id == R.id.btn_apply_f0_drift) {
            int result = F0Class.getInstance().sfdc_drift_apply(mValueOffset);
            if (result == 0) {
                MMKV.defaultMMKV().encode("mValueOffset", mValueOffset);
                SnackBarUtil.showShort(mBinding.imBack, "Apply success");
            } else {
                SnackBarUtil.showShort(mBinding.imBack, "Apply failure");
            }
        }
    }
}