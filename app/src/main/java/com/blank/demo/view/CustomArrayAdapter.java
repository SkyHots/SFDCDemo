package com.blank.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blank.demo.R;

import androidx.core.content.ContextCompat;

public class CustomArrayAdapter extends ArrayAdapter<CharSequence> {

    private final Context mContext;
    private int mSelectedPosition = -1;

    public CustomArrayAdapter(Context context, int resource, CharSequence[] items) {
        super(context, resource, items);
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        if (position == mSelectedPosition) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_bar_bg));
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setTextColor(Color.WHITE);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }
}