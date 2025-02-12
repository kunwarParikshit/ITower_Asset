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

import com.isl.audit.model.AuditListResult;
import com.isl.audit.util.DateUtil;
import com.isl.audit.util.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import infozech.itower.R;

public class AdapterAuditListItem extends RecyclerView.Adapter<AdapterAuditListItem.CustomViewHolder> {
    private List<AuditListResult> auditList;
    private Activity activity;
    private ItemClickListener itemClickListener;

    public AdapterAuditListItem(List<AuditListResult> auditList, Activity activity,ItemClickListener itemClickListener){
        this.activity=activity;
        this.auditList=auditList;
        this.itemClickListener=itemClickListener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(!TextUtils.isEmpty(auditList.get(position).getScheduleDate())){
            String date= DateUtil.formatDate(DateUtil.DATE_FORMAT1,DateUtil.DATE_FORMAT2,auditList.get(position).getScheduleDate());
            holder.tvAuditDate.setText(date);
        }
        if(!TextUtils.isEmpty(auditList.get(position).getOwnerSiteId())){
            holder.tvSiteId.setText(""+auditList.get(position).getOwnerSiteId());
        }

        if(!TextUtils.isEmpty(auditList.get(position).getAuditName())){
            holder.tvAuditId.setText(auditList.get(position).getAuditName());
        }

        if(!TextUtils.isEmpty(auditList.get(position).getAuditType())){
            holder.tvAuditStatus.setText(auditList.get(position).getAuditType());
        }
        if(auditList.get(position).isNeedSync()){
            holder.tvSync.setVisibility(View.VISIBLE);
        }else{
            holder.tvSync.setVisibility(View.GONE);
        }

        holder.tvSiteId.setTypeface( null, Typeface.BOLD );
        holder.tvAuditId.setTypeface( null, Typeface.NORMAL );
        holder.tvAuditDate.setTypeface( null, Typeface.NORMAL );
        holder.tvAuditStatus.setTypeface( null, Typeface.NORMAL );

        holder.cardView.setOnClickListener(v -> {
            itemClickListener.onItemClickListener(v,position);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.inflater_audit_list_item;
    }

    @Override
    public int getItemCount() {
        return auditList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_site_id)
        TextView tvSiteId;
        @BindView(R.id.tv_audit_id)
        TextView tvAuditId;
        @BindView(R.id.tv_audit_date)
        TextView tvAuditDate;
        @BindView(R.id.tv_audit_status)
        TextView tvAuditStatus;
        @BindView(R.id.tv_sync)
        TextView tvSync;
        @BindView(R.id.card_view)
        CardView cardView;


        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
