package com.isl.audit.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.isl.audit.model.AssetListResult;
import com.isl.audit.util.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterChildAssetsList extends RecyclerView.Adapter<AdapterChildAssetsList.CustomViewHolder> {
    private List<AssetListResult> assetList;
    private Activity activity;
    private ItemClickListener itemClickListener;

    public AdapterChildAssetsList(List<AssetListResult> assetList, Activity activity,ItemClickListener itemClickListener){
        this.activity=activity;
        this.assetList=assetList;
        this.itemClickListener=itemClickListener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!TextUtils.isEmpty(assetList.get(position).getName())){
            holder.tvAssetName.setText(assetList.get(position).getName());
        }

        holder.tvAssetName.setTypeface( null, Typeface.NORMAL );

        holder.ivEdit.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v,position);
        });
        holder.ivDelete.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v,position);
        });

    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_child_assets_list;
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_asset_name)
        TextView tvAssetName;
        @BindView(R.id.iv_edit)
        ImageView ivEdit;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
