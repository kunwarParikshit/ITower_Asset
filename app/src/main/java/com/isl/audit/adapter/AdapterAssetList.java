package com.isl.audit.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.audit.model.AssetListResult;
import com.isl.audit.util.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterAssetList extends RecyclerView.Adapter<AdapterAssetList.CustomViewHolder> {
    private List<AssetListResult> assetList;
    private Activity activity;
    private int type;
    private ItemClickListener itemClickListener;

    public AdapterAssetList(List<AssetListResult> assetList, Activity activity, ItemClickListener itemClickListener, int type) {
        this.activity = activity;
        this.type = type;
        this.assetList = assetList;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (!TextUtils.isEmpty(assetList.get(position).getName())) {
            holder.tvAssetName.setText(assetList.get(position).getName()+ " (" + assetList.get(position).getQr_code() + ")");
        }

        if (assetList.get(position).getParentTypes() != null && assetList.get(position).getParentTypes().size() > 0) {
            holder.tvAddParent.setVisibility(View.VISIBLE);
            holder.tvAddChild.setVisibility(View.GONE);
        } else {
            holder.tvAddChild.setVisibility(View.VISIBLE);
            holder.tvAddParent.setVisibility(View.GONE);
        }

        if(assetList.get(position).isShowError()){
            holder.ivWarning.setVisibility(View.VISIBLE);
        }else{
            holder.ivWarning.setVisibility(View.GONE);
        }

        /*if(assetList.get(position).getChildAssetsResultList()!=null && assetList.get(position).getChildAssetsResultList().size()>0){
            holder.rvChildAssets.setLayoutManager(new LinearLayoutManager(activity));
            AdapterChildAssetsList adapterChildAssetsList=new AdapterChildAssetsList(assetList.get(position).getChildAssetsResultList(),activity,null);
            holder.rvChildAssets.setAdapter(adapterChildAssetsList);
            holder.rvChildAssets.setVisibility(View.VISIBLE);
        }else{
            holder.rvChildAssets.setVisibility(View.GONE);
        }*/

        if (!TextUtils.isEmpty(assetList.get(position).getParent())) {
            holder.tvAddChild.setVisibility(View.GONE);
            holder.tvAddParent.setVisibility(View.GONE);
            holder.tvParent.setVisibility(View.VISIBLE);
            holder.tvParentValue.setVisibility(View.VISIBLE);
            holder.tvParentValue.setText(assetList.get(position).getParentName() + " (" + assetList.get(position).getParent() + ")");
            holder.cvMain.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray_light));
        } else {
            holder.tvParent.setVisibility(View.GONE);
            holder.tvParentValue.setVisibility(View.GONE);
            holder.cvMain.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
        }

        if (TextUtils.isEmpty(assetList.get(position).getParent()) && assetList.size()>1) {
                holder.viewLine.setVisibility(View.VISIBLE);
        }else{
            holder.viewLine.setVisibility(View.GONE);
        }

        if(type==1){
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
            holder.tvAddChild.setVisibility(View.GONE);
            holder.tvAddParent.setVisibility(View.GONE);
        }else{
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
        }

        holder.tvAssetName.setTypeface(null, Typeface.NORMAL);

        holder.ivEdit.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
        holder.cvMain.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
        holder.ivDelete.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
        holder.tvAddChild.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
        holder.tvAddParent.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
        holder.ivWarning.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v, position);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_assets_list;
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_add_parent)
        TextView tvAddParent;
        @BindView(R.id.tv_parent)
        TextView tvParent;
        @BindView(R.id.tv_parent_value)
        TextView tvParentValue;
        @BindView(R.id.tv_add_child)
        TextView tvAddChild;
        @BindView(R.id.tv_asset_name)
        TextView tvAssetName;
        @BindView(R.id.iv_edit)
        ImageView ivEdit;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.iv_warning)
        ImageView ivWarning;
        @BindView(R.id.rv_child_assets)
        RecyclerView rvChildAssets;
        @BindView(R.id.cv_main)
        CardView cvMain;
        @BindView(R.id.view_line)
        View viewLine;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
