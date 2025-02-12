package com.isl.hsse;

import android.app.Activity;
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

public class SubTabAdapter extends RecyclerView.Adapter<SubTabAdapter.MyViewHolder> {

    List<HashMap<String, Object>> requestList;
    HashMap<String,String> tranData = null;
    Context context;
    String report,editRight;

    public SubTabAdapter(Context context, List<HashMap<String, Object>> requestList,
                         HashMap<String,String> tranData, String report,String editRight) {
        this.requestList = requestList;
        this.tranData = tranData;
        this.context = context;
        this.report = report;
        this.editRight = editRight;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_tkt_id,tvField1,tvField2,tvField3,tvField4,tvEdit;
        public MyViewHolder(View vi) {
            super(vi);

            tv_tkt_id = (TextView) vi.findViewById( R.id.txt_tkt_id);
            tv_tkt_id.setTypeface( Utils.typeFace(context));

            tvField1 = (TextView) vi.findViewById( R.id.txt_name);
            tvField1.setTypeface( Utils.typeFace(context));

            tvField2 = (TextView) vi.findViewById(R.id.txt_job);
            tvField2.setTypeface(Utils.typeFace(context));

            tvField3 = (TextView) vi.findViewById(R.id.txt_company);
            tvField3.setTypeface(Utils.typeFace(context));

            tvField4 = (TextView) vi.findViewById(R.id.txt_contact);
            tvField4.setTypeface(Utils.typeFace(context));

            tvEdit = (TextView) vi.findViewById(R.id.tv_edit);
            tvEdit.setTypeface(Utils.typeFace(context));
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hsse_personal, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(tranData.get(HsseConstant.OLD_TKT_STATUS).equalsIgnoreCase("1693")){
            holder.tvEdit.setText("Details");
        }

        holder.tvEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(context, SubFormActivity.class );
                tranData.put(Constants.OPERATION,"E");
                tranData.put(Constants.EDIT_RIGHTS,editRight);
                tranData.put(Constants.TXN_SOURCE,"EditRequest");
                tranData.put(Constants.REQUEST_FLAG,"");
                tranData.put(Constants.TASK_ID,""+requestList.get(position).get(HsseConstant.TKT_ID));
                switch(report){
                    case "PersonDetails":
                        tranData.put( Constants.FORM_KEY,"EditHSSEPersonalDetail");
                        tranData.put( Constants.NEXT_TAB_SELECT,"Personal Details");
                        tranData.put(Constants.TXN_ID,""+requestList.get(position).get("pid"));
                        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
                        context.startActivity( i );
                        break;
                    case "LogBook":
                        tranData.put(Constants.FORM_KEY,"EditHSSELogBook");
                        tranData.put(Constants.TXN_ID,""+requestList.get(position).get("logid"));
                        tranData.put(Constants.NEXT_TAB_SELECT,"Log Book");
                        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
                        context.startActivity( i );
                        break;
                    case "PreventiveActions":
                        tranData.put( Constants.FORM_KEY,"EditHSSEPreventiveActions");
                        tranData.put(Constants.TXN_ID,""+requestList.get(position).get("cpid"));
                        tranData.put(Constants.NEXT_TAB_SELECT,"Pre Action");
                        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
                        context.startActivity( i );
                        break;
                }
            }
        });


        Map<String,Object> txnData = requestList.get(position);
        for(Map.Entry<String, Object> entry : txnData.entrySet()){

            switch(entry.getKey()){
                case HsseConstant.TKT_ID:
                    if(entry.getValue() != null){
                        holder.tv_tkt_id.setText(""+entry.getValue());
                    }else{
                        holder.tv_tkt_id.setText("");
                    }
                    break;
                case HsseConstant.PERSONAL_NAME:
                    if(entry.getValue() != null){
                        holder.tvField1.setText(""+entry.getValue());
                    }else{
                        holder.tvField1.setText("");
                    }
                    break;
                case HsseConstant.NAME:
                    if(entry.getValue() != null){
                        holder.tvField1.setText(""+entry.getValue());
                    }else{
                        holder.tvField1.setText("");
                    }
                    break;
                case HsseConstant.OWNER_NAME:
                    if(entry.getValue() != null){
                        holder.tvField1.setText(""+entry.getValue());
                    }else{
                        holder.tvField1.setText("");
                    }
                    break;

                case HsseConstant.CONTACT_NO:
                    if(entry.getValue() != null){
                        holder.tvField4.setText("Contact No :"+entry.getValue());
                    }else{
                        holder.tvField4.setText("");
                    }
                    break;
                case HsseConstant.DATE:
                    if(entry.getValue() != null){
                        holder.tvField4.setText("Date :"+entry.getValue());
                    }else{
                        holder.tvField4.setText("");
                    }
                    break;
                case HsseConstant.DUE_DATE:
                    if(entry.getValue() != null){
                        holder.tvField4.setText("Due Date : "+entry.getValue());
                    }else{
                        holder.tvField4.setText("");
                    }
                    break;

                case HsseConstant.COMPANY_NAME:
                    if(entry.getValue() != null){
                        holder.tvField3.setText(""+entry.getValue());
                    }else{
                        holder.tvField3.setText("");
                    }
                    break;

                case HsseConstant.DEPARTMENT  :
                    if(entry.getValue() != null){
                        holder.tvField3.setText(""+entry.getValue());
                    }else{
                        holder.tvField3.setText("");
                    }
                    break;


                case HsseConstant.JOB_TITLE:
                    if(entry.getValue() != null){
                        holder.tvField2.setText("Job Title : "+entry.getValue());
                    }else{
                        holder.tvField2.setText("Job Title : ");
                    }
                    break;
                case HsseConstant.INTERVENTION:
                    if(entry.getValue() != null){
                        holder.tvField2.setText("Intervention Details : "+entry.getValue());
                    }else{
                        holder.tvField2.setText("Intervention Details : ");
                    }
                    break;
                case HsseConstant.STATUS:
                    if(entry.getValue() != null){
                        holder.tvField2.setText((String)entry.getValue());
                    }else{
                        holder.tvField2.setText("");
                    }
                    break;
            }

        }

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity(requestList.get(position));
            }
        });*/

    }
    @Override
    public int getItemCount()
    {
        return requestList.size();
    }
}
