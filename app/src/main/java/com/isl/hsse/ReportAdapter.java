package com.isl.hsse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isl.constant.AppConstants;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import infozech.itower.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    List<HashMap<String, Object>> requestList;
    Context context;
    String source,editRight;

    public ReportAdapter(Context context, List<HashMap<String, Object>> requestList, String source,String editRight) {
        this.requestList = requestList;
        this.context = context;
        this.source = source;
        this.editRight = editRight;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_date,txt_ticket_id,txt_site_id,txt_ticket_status,txt_description;
          public MyViewHolder(View vi) {
            super(vi);

            txt_date = (TextView) vi.findViewById(R.id.txt_date);
            txt_date.setTypeface(Utils.typeFace(context));

            txt_ticket_id = (TextView) vi.findViewById(R.id.txt_ticket_id);
            txt_ticket_id.setTypeface(Utils.typeFace(context));

            txt_site_id = (TextView) vi.findViewById(R.id.txt_site_id);
            txt_site_id.setTypeface(Utils.typeFace(context));

            txt_ticket_status = (TextView) vi.findViewById(R.id.txt_ticket_status);
            txt_ticket_status.setTypeface(Utils.typeFace(context));

            txt_description = (TextView) vi.findViewById(R.id.txt_description);
            txt_description.setTypeface(Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ticket,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

       Map<String,Object> txnData = requestList.get(position);

        for(Map.Entry<String, Object> entry : txnData.entrySet()){

            switch(entry.getKey()){
                case AppConstants.SITE_ID_ALIAS:
                    if(entry.getValue() != null){
                        holder.txt_site_id.setText((String)entry.getValue());
                    }else{
                        holder.txt_site_id.setText("");
                    }
                    break;
                case AppConstants.CREATED_DATE:
                    if(entry.getValue() != null){
                        holder.txt_date.setText((String)entry.getValue());
                    }else{
                        holder.txt_date.setText("");
                    }
                    break;
                case "ticketid":
                    if(entry.getValue() != null){
                        holder.txt_ticket_id.setText((String)entry.getValue());
                    }else{
                        holder.txt_ticket_id.setText("");
                    }
                    break;
                case AppConstants.INCIDENT_TYPE:
                    if(entry.getValue() != null){
                        holder.txt_description.setText((String)entry.getValue());
                    }else{
                        holder.txt_description.setText("");
                    }
                    break;
                case AppConstants.TICKET_STATUS:
                    if(entry.getValue() != null){
                        holder.txt_ticket_status.setText((String)entry.getValue());
                    }else{
                        holder.txt_ticket_status.setText("");
                    }
                    break;
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity(requestList.get(position));
            }
        });

    }
    @Override
    public int getItemCount()
    {
         return requestList.size();
    }

    private void nextActivity(HashMap<String,Object> selected) {
         selected.put(Constants.PROCESS_INSTANCE_ID,selected.get(Constants.PROCESS_INSTANCE_ID));
         selected.put(Constants.TASK_ID,selected.get("ticketid"));
         selected.put(Constants.NEXT_TAB_SELECT,"HSSE");
         selected.put(Constants.OPERATION,"E");
         selected.put(Constants.EDIT_RIGHTS,editRight);
         selected.put(Constants.FORM_KEY,"EditHSSERequest");
         selected.put(Constants.TXN_SOURCE,source);
         selected.put(HsseConstant.OLD_TKT_STATUS,selected.get("tktstatus"));
         selected.put(HsseConstant.OLD_GRP,selected.get("assigntogrp"));
         Intent i = new Intent( context, HsseFrame.class );
         i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, selected);
         context.startActivity(i);

     }
}