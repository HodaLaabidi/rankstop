package com.steeringit.rankstop.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.steeringit.rankstop.R;
import com.steeringit.rankstop.customviews.RSTVMedium;

public class SpinnerGenderAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] genderArray;

    public SpinnerGenderAdapter(@NonNull Context context, @NonNull String[] genderArray) {
        super(context, 0, genderArray);
        this.mContext = context;
        this.genderArray = genderArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_category,parent,false);
        }
        RSTVMedium categoryNameTV = convertView.findViewById(R.id.text_view);

        String gender = genderArray[position];

        if (gender != null)
            categoryNameTV.setText(gender);

        return convertView;

    }

}
