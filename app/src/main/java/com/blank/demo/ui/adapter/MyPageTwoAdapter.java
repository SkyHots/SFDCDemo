package com.blank.demo.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blank.demo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPageTwoAdapter extends RecyclerView.Adapter<MyPageTwoAdapter.ViewHolder> {
    private List<String> dataList;
    private Context context;
    private CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public MyPageTwoAdapter(List<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manual_test, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = dataList.get(position);
        //截取掉后缀名
        String name = data.substring(0, data.lastIndexOf("."));
        holder.play.setText(data + " Playback");
        holder.play.setOnClickListener(v -> {
            if (mCallBack != null)
                mCallBack.clickPlay(data, position);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            play = itemView.findViewById(R.id.play);
        }
    }

    public interface CallBack {
        void clickPlay(String waveName, int position);
    }
}