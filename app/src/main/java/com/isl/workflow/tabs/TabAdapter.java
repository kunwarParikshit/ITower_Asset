package com.isl.workflow.tabs;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isl.util.Utils;
import com.isl.workflow.modal.AssigenmentHistory;
import com.isl.workflow.modal.AuditTrail;
import com.isl.workflow.modal.RequestHistory;

import java.util.List;

import infozech.itower.R;


/**
 * Created by dhakan on 6/27/2020.
 */

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.MyViewHolder> {

    RequestHistory requestHistory;
    Context context;
    String txnId;
    int flag = 0;

    public TabAdapter(Context context, List<AuditTrail> auditTrailLIst,List<AssigenmentHistory> assigenmentHistoryList,String txnId, int flag){

        if(this.requestHistory==null){
            this.requestHistory = new RequestHistory();
        }

        if(auditTrailLIst!=null){
            this.requestHistory.setAuditTrail(auditTrailLIst);
        }

        if(assigenmentHistoryList!=null){
            this.requestHistory.setAssigenmentHistory(assigenmentHistoryList);
        }

        this.context = context;
        this.flag = flag;
        this.txnId = txnId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvField1,tvField2,tvField3,tvField4;

        public MyViewHolder(View vi) {
            super(vi);

            tvField1 = (TextView) vi.findViewById(R.id.tv_change_id);
            tvField1.setTypeface( Utils.typeFace(context));

            tvField2 = (TextView) vi.findViewById(R.id.tv_updated_on);
            tvField2.setTypeface( Utils.typeFace(context));

            tvField3 = (TextView) vi.findViewById(R.id.tv_updated);
            tvField3.setTypeface( Utils.typeFace(context));

            tvField4 = (TextView) vi.findViewById(R.id.tv_update_by);
            tvField4.setTypeface( Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.am_tab_grid, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //sName = "";
        if(flag==1){

            AuditTrail auditTrail = requestHistory.getAuditTrail().get(position);
            holder.tvField4.setVisibility(View.GONE);
            //holder.tvField3.setVisibility(View.VISIBLE);
            if (auditTrail.getAudittime() != null) {
                holder.tvField1.setText( "Date : " +auditTrail.getAudittime());//+ sName );
            } else {
                holder.tvField1.setText( "Date : " );
            }

            if (auditTrail.getLoginid() != null) {
                holder.tvField2.setText( "User : " + auditTrail.getLoginid());
            } else {
                holder.tvField2.setText( "User : " );
            }


            if (auditTrail.getRemarks() != null) {
                holder.tvField3.setText( auditTrail.getRemarks());
            } else {
                holder.tvField3.setText("");
            }

        }else{

            AssigenmentHistory assigenmentHistory = requestHistory.getAssigenmentHistory().get(position);

            holder.tvField4.setVisibility(View.GONE);
            if (assigenmentHistory.getAssignedto() != null) {
              holder.tvField1.setText( "Assignee : " +assigenmentHistory.getAssignedto() + " (Type - "+assigenmentHistory.getAssigntype()+")");// + sName );
            } else {
              holder.tvField1.setText( "Assignee : " );
            }

            /*
            if (assigenmentHistory.getAssignedby() != null) {
              holder.tvField2.setText( "Created By : " + assigenmentHistory.getAssignedby());
            } else {
              holder.tvField2.setText( "Created By : " );
            }
            */

            if (assigenmentHistory.getStartdate() != null) {
              holder.tvField2.setText( "Start : " + assigenmentHistory.getStartdate());
            } else {
              holder.tvField2.setText( "Start : " );
            }


            if (assigenmentHistory.getEnddate() != null) {
              holder.tvField3.setText( "End : " + assigenmentHistory.getEnddate());
            } else {
              holder.tvField3.setText( "End : " );
            }

        }
    }

    @Override
    public int getItemCount()
    {
        if(flag==1){
            return requestHistory.getAuditTrail().size();
        }else{
            return requestHistory.getAssigenmentHistory().size();
        }
    }
}