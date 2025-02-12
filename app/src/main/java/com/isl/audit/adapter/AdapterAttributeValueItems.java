package com.isl.audit.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterAttributeValueItems extends RecyclerView.Adapter<AdapterAttributeValueItems.CustomViewHolder> {
    private List<String> valueList;
    private Activity activity;

    public AdapterAttributeValueItems(List<String> valueList, Activity activity) {
        this.activity = activity;
        this.valueList = valueList;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!TextUtils.isEmpty(valueList.get(position))){
            holder.tvAttrValue.setText(valueList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_attribute_value_item;
    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_attr_value)
        TextView tvAttrValue;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}