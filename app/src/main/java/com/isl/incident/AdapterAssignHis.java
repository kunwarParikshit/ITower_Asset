package com.isl.incident;

import com.isl.modal.ResponceTabList;

import infozech.itower.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterAssignHis extends BaseAdapter {
    Context con;
    private LayoutInflater inflater = null;
    ResponceTabList list;
    int flag = 0;
    public AdapterAssignHis(Context con, ResponceTabList data,int flag) {
        this.con = con;
        this.list = data;
        this.flag = flag;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int size = 0;
        if(flag == 0){
            size = list.getAssign().size();
        }
        if(flag == 1){
            size = list.getAlarm().size();
        }
        return size;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View arg1, ViewGroup parent) {
        View vi = arg1;
        if (arg1 == null)
        vi = inflater.inflate(R.layout.list_item_remarks, null);
        TextView tv_update_time = (TextView) vi.findViewById(R.id.tv_update_time);
        TextView tv_update_by= (TextView) vi.findViewById(R.id.tv_update_by);
        TextView tv_update_field = (TextView) vi.findViewById(R.id.tv_update_field);
        TextView tv_update_doc = (TextView) vi.findViewById(R.id.tv_update_doc);
        TextView tv_alarm_telly = (TextView) vi.findViewById(R.id.tv_alarm_telly);
        TextView tv_alarm_detail = (TextView) vi.findViewById(R.id.tv_alarm_detail);

        TextView et_update_time = (TextView) vi.findViewById(R.id.et_update_time);
        TextView et_update_by= (TextView) vi.findViewById(R.id.et_update_by);
        TextView et_update_field = (TextView) vi.findViewById(R.id.et_update_field);
        TextView et_update_doc = (TextView) vi.findViewById(R.id.et_update_doc);
        TextView et_alarm_telly = (TextView) vi.findViewById(R.id.et_alarm_telly);
        TextView et_alarm_detail = (TextView) vi.findViewById(R.id.et_alarm_detail);
        et_update_time.setVisibility(View.VISIBLE);
        et_update_by.setVisibility(View.VISIBLE);
        et_update_field.setVisibility(View.VISIBLE);
        et_update_doc.setVisibility(View.VISIBLE);
        et_alarm_telly.setVisibility(View.VISIBLE);
        et_alarm_detail.setVisibility(View.VISIBLE);


        // 0 for assign details 1 for alarm details
        if(flag == 0){
            tv_alarm_telly.setVisibility(View.GONE);
            et_alarm_telly.setVisibility(View.GONE);
            tv_alarm_detail.setVisibility(View.GONE);
            et_alarm_detail.setVisibility(View.GONE);

            tv_update_time.setText("Assigned To");
          if(list.getAssign().get(position).getASSIGNTO()!=null){
              //tv_update_time.setText(Html.fromHtml("<b>Assigned To : </b> "+list.getAssign().get(position).getASSIGNTO()));
              et_update_time.setText(list.getAssign().get(position).getASSIGNTO());
          }else{
              et_update_time.setText("");
              //tv_update_time.setText(Html.fromHtml("<b>Assigned To : </b> "));
          }

            tv_update_by.setText("Start Date Time");
            if(list.getAssign().get(position).getASSIGNED_DATE()!=null){
                et_update_by.setText(""+list.getAssign().get(position).getASSIGNED_DATE());
              //tv_update_by.setText(Html.fromHtml("<b>Start Date Time : </b> "+list.getAssign().get(position).getASSIGNED_DATE()));
          }else{
                et_update_by.setText("");
              //tv_update_by.setText(Html.fromHtml("<b>Start Date Time : </b> "));
          }

          tv_update_field.setText("End Date Time");
          if(list.getAssign().get(position).getASSIGNED_END_DATE()!=null){
              et_update_field.setText(list.getAssign().get(position).getASSIGNED_END_DATE());
          }else{
              et_update_field.setText("");
              //tv_update_field.setText(Html.fromHtml("<b>End Date Time : </b> "));
          }

              tv_update_doc.setText("Duration");
          if(list.getAssign().get(position).getDURATION()!=null){
              et_update_doc.setText(list.getAssign().get(position).getDURATION());
          }else{
              et_update_doc.setText("");
          }
       }

       //TT-20200129-04457138

        if(flag == 1){
            tv_alarm_telly.setVisibility(View.VISIBLE);
            et_alarm_telly.setVisibility(View.VISIBLE);


           if(list.getAlarm().get(position).getFLAG().equalsIgnoreCase("1")){
               tv_alarm_detail.setVisibility(View.VISIBLE);
               et_alarm_detail.setVisibility(View.VISIBLE);
           }else{
               tv_alarm_detail.setVisibility(View.GONE);
               et_alarm_detail.setVisibility(View.GONE);

           }

            tv_update_time.setText("Alarm Description");
            if(list.getAlarm().get(position).getALARM_DESC()!=null){
                et_update_time.setText(list.getAlarm().get(position).getALARM_DESC());
            }else{
                et_update_time.setText("");
            }

            tv_alarm_detail.setText("Alarm Detail");
            if(list.getAlarm().get(position).getALARM_DETAIL()!=null){
                et_alarm_detail.setText(list.getAlarm().get(position).getALARM_DETAIL());
            }else{
                et_alarm_detail.setText("");
            }

            tv_update_by.setText("Alarm Start Date Time");
            if(list.getAlarm().get(position).getALARM_START_DATE()!=null){
                et_update_by.setText(list.getAlarm().get(position).getALARM_START_DATE());
            }else{
                et_update_by.setText("");
            }

            tv_update_field.setText("Last Occurence Time");
            if(list.getAlarm().get(position).getLAST_OCCURRENCE_TIME()!=null){
                et_update_field.setText(list.getAlarm().get(position).getLAST_OCCURRENCE_TIME());
            }else{
                et_update_field.setText("");
            }

            tv_update_doc.setText("Alarm End Date Time");
            if(list.getAlarm().get(position).getALARM_CLOSE_TIME()!=null){
                et_update_doc.setText(list.getAlarm().get(position).getALARM_CLOSE_TIME());
            }else{
                et_update_doc.setText("");
            }

            tv_alarm_telly.setText("Alarm Tally");
            if(list.getAlarm().get(position).getALARM_TALLY()!=null){
                et_alarm_telly.setText(list.getAlarm().get(position).getALARM_TALLY());
            }else{
                et_alarm_telly.setText("");
            }
        }
       return vi;
    }
}