package com.blank.demo.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blank.demo.R;
import com.blank.demo.bean.TcWaveConfig;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPageFourAdapter extends RecyclerView.Adapter<MyPageFourAdapter.ViewHolder> {
    private static final String TAG = "MyPageFourAdapter";
    private ArrayList<TcWaveConfig> dataList;
    private final Context context;

    public ArrayList<TcWaveConfig> getEditedDataList() {
        return dataList;
    }

    public MyPageFourAdapter(ArrayList<TcWaveConfig> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_case, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TcWaveConfig data = dataList.get(position);
        holder.wave_id.setText("Waveform " + data.getWave_index());
        holder.playback_count.setValue(data.getPlayback_count());
        holder.f0_drift.setValue(data.getF0_drift());
        holder.f0_drift.setOnValueChangedListener((picker, oldVal, newVal) -> {
            data.setF0_drift((short) newVal);
        });
        holder.playback_count.setOnValueChangedListener((picker, oldVal, newVal) -> {
            data.setPlayback_count((short) newVal);
        });
        //显示 playbackInterval
        String[] displayedValues = {"0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "10"};
        float playbackInterval = data.getPlayback_interval();
        int index = 11;
        for (int i = 0; i < displayedValues.length; i++) {
            if (playbackInterval == Float.parseFloat(displayedValues[i])) {
                index = i;
                break;
            }
        }
        holder.playback_interval.setValue(index);
        holder.playback_interval.setOnValueChangedListener((picker, oldVal, newVal) -> {
            Log.e(TAG, "setOnValueChangedListener: " + newVal);
            data.setPlayback_interval(Float.parseFloat(displayedValues[newVal]));
        });
        NumberPicker numberPicker = holder.playback_interval;
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length - 1);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wave_id;
        NumberPicker f0_drift, playback_count, playback_interval;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wave_id = itemView.findViewById(R.id.wave_id);
            f0_drift = itemView.findViewById(R.id.f0_drift);
            playback_count = itemView.findViewById(R.id.playback_count);
            playback_interval = itemView.findViewById(R.id.playback_interval);
        }
    }
}