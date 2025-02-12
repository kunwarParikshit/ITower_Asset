package com.isl.audit.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.audit.util.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterAuditMenuItems extends RecyclerView.Adapter<AdapterAuditMenuItems.CustomViewHolder> {
    private List<String> menuItemsList;
    private Activity activity;
    private ItemClickListener itemClickListener;

    public AdapterAuditMenuItems(List<String> menuItemsList, Activity activity,ItemClickListener itemClickListener){
        this.activity=activity;
        this.menuItemsList=menuItemsList;
        this.itemClickListener=itemClickListener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!TextUtils.isEmpty(menuItemsList.get(position))){
            holder.tvItemName.setText(menuItemsList.get(position));
        }

        holder.tvItemName.setTypeface( null, Typeface.NORMAL );

        holder.cardView.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v,position);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_audit_menu_items;
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.card_view)
        CardView cardView;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
