package com.automatodev.loa.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.automatodev.loa.R;
import com.automatodev.loa.model.entity.CategoryEntity;

import java.util.List;

public class SpinnerCustomAdapter extends ArrayAdapter {

    private List<CategoryEntity> categoryEntities;

    public SpinnerCustomAdapter(@NonNull Context context, @NonNull List<CategoryEntity> spinnerList) {
        super(context, 0, spinnerList);
        this.categoryEntities = spinnerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_spinner, parent, false);
        }


        ImageView imgCategory_layout = convertView.findViewById(R.id.imgCategory_layout);
        TextView txtCategory_layout = convertView.findViewById(R.id.txtCategory_layout);
        CategoryEntity spinner = categoryEntities.get(position);
        imgCategory_layout.setImageResource(spinner.getImage());
        txtCategory_layout.setText(spinner.getOption());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_spinner_dropdown, parent, false);
        }
        ImageView imgCategory_drop = convertView.findViewById(R.id.imgCategory_drop);
        TextView txtCategory_drop = convertView.findViewById(R.id.txtCategory_drop);
        CategoryEntity spinner = categoryEntities.get(position);
        imgCategory_drop.setImageResource(spinner.getImage());
        txtCategory_drop.setText(spinner.getOption());

        return convertView;
    }

}
