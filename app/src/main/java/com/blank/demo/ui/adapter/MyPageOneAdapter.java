package com.blank.demo.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blank.demo.R;
import com.blank.demo.bean.SfdcParameter;
import com.blank.demo.utils.Number2String;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPageOneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<SfdcParameter> dataList;
    private Context context;

    public MyPageOneAdapter(List<SfdcParameter> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_header, parent, false);
            return new ViewHolderHeader(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_page_one_layout, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
            holderHeader.f0.setText(Number2String.float2String(dataList.get(0).getLra_f0()));
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            SfdcParameter data = dataList.get(position - 1);
            viewHolder.kValue.setText(Number2String.float2String(data.getAvg_slope()));
            viewHolder.Hz.setText(Number2String.float2String(data.getWave_fc()));
            viewHolder.mv.setText(Number2String.float2String(data.getBemf_offset()));
            viewHolder.waveName.setText(data.getWave_name());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView waveName, kValue, Hz, mv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.waveName);
            kValue = itemView.findViewById(R.id.kValue);
            Hz = itemView.findViewById(R.id.Hz);
            mv = itemView.findViewById(R.id.mv);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView f0;

        public ViewHolderHeader(@NonNull View itemView) {
            super(itemView);
            f0 = itemView.findViewById(R.id.f0);
        }
    }
}