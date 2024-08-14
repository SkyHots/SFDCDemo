package com.blank.demo.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blank.demo.R;
import com.blank.demo.bean.TcConfig;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPageThreeAdapter extends RecyclerView.Adapter<MyPageThreeAdapter.ViewHolder> {
    private List<TcConfig> dataList;
    private Context context;
    private CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public List<TcConfig> getDataList() {
        return dataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<TcConfig> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public MyPageThreeAdapter(List<TcConfig> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_auto_test, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TcConfig data = dataList.get(position);
        holder.name.setText(data.getTc_name());
        holder.edit.setOnClickListener(v -> {
            if (mCallBack != null)
                mCallBack.clickEdit(data.getTc_name());
        });
        holder.delete.setOnClickListener(v -> {
            if (mCallBack != null)
                mCallBack.clickDelete(data.getTc_name(), position);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface CallBack {
        void clickEdit(String caseName);

        void clickDelete(String caseName, int index);
    }
}