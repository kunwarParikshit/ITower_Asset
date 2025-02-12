package com.isl.hsse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.LocationList;
import com.isl.util.Utils;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.DropdownValue;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import infozech.itower.R;

public class SearchHsseTicket extends Activity {
    AppPreferences mAppPreferences;

    EditText et_site_id,et_ticket_id;
    public Spinner sp_region,sp_sub_region,sp_mini_cluster,sp_tktType,sp_tkt_classification
            ,sp_tktStsatus,sp_type_incident,sp_force_entry,sp_group,sp_rca_category,sp_rca_sub_category,
            sp_alarm_notification,sp_request_date;

    TextView search,tv_header,tv_region,tv_sub_region,tv_mini_cluster,tv_tktType,tv_tkt_classification,
             tv_tktStsatus,tv_type_incident,tv_force_entry,
            tv_alarm_notification,tv_ticket_id,tv_site_id,tv_group,tv_rca_category,tv_rca_sub_category,
            tv_request_date,from,tv_from_Date,to,tv_to_Date;

    Button bt_back;
    List<String> tktType_id,tkt_classification_id,tkt_status_id,type_of_incident_id,forced_entry_id,
            alarm_notification_id,group_id,rca_cat_id,rca_subcat_id,request_date_id;
    JSONObject filterData =null;
    DataBaseHelper db;
    String url = "";
    private int pYear, pMonth, pDay;
    Date d1, d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences( SearchHsseTicket.this);
        db = new DataBaseHelper( SearchHsseTicket.this);
        db.open();
        setContentView( R.layout.hsse_search_ticket);

        getControllerId();
        setMsg();

        if(!locationDataType().equalsIgnoreCase("")){
            if (Utils.isNetworkAvailable( SearchHsseTicket.this)) {
                new LocationTask( SearchHsseTicket.this).execute();
            }else{
                //No internet connection.Please download meta data
                Utils.toast( SearchHsseTicket.this, "67");
                finish();
            }
        }else{
            init();
        }

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        tv_from_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fromDatePicker( SearchHsseTicket.this,tv_from_Date);
            }
        });

        tv_to_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ToDatePicker( SearchHsseTicket.this,tv_to_Date);
            }
        });




        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    JSONArray jsonArrChkImg = new JSONArray();
                    jsonArrChkImg.put( filterData );
                    Intent i = new Intent( SearchHsseTicket.this, MyReport.class);
                    i.putExtra("filterData",filterData.toString());
                    i.putExtra("report","Search");
                    startActivity(i);
                }
            }
        });
    }

    public void addItemsOnSpinner(Spinner spinner,int flag,String paramType,String column,List<String> id,
                                  String idCol, String valCol,String val2,String val3,String val15) {
        List<String> list= null;
        if(id!=null && id.size()>0){
            id.clear();
        }
        if(flag==1) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchHsseTicket.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select " + idCol + " as paramid ," + valCol + " as paramvalue from workflow_meta_data where type='" + paramType + "'ORDER BY id",
                    "paramid", "paramvalue", null );
            db2.close();
            list = new ArrayList<String>();

            if (ddValues != null && ddValues.size() > 0) {
                for (DropdownValue ddValue : ddValues) {
                    list.add( ddValue.getValue() );
                    id.add( ddValue.getId() );
                }
            }
        }else if(flag==2) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchHsseTicket.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select " + idCol + " as paramid ," + valCol + " as paramvalue from workflow_meta_data where type='" + paramType + "' AND val2='" + val2 + "' ORDER BY val1 ASC",
                    "paramid", "paramvalue", null );

            db2.close();
            list = new ArrayList<String>();
            if (ddValues != null && ddValues.size() > 0) {
                for (DropdownValue ddValue : ddValues) {
                    list.add( ddValue.getValue() );
                    id.add( ddValue.getId() );
                }
            }
        }else if(flag==3) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchHsseTicket.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select " + idCol + " as paramid ," + valCol + " as paramvalue from workflow_meta_data where type='" + paramType + "' AND val3='" + val3 + "' ORDER BY val1 ASC",
                    "paramid", "paramvalue", null );

            db2.close();
            list = new ArrayList<String>();
            if (ddValues != null && ddValues.size() > 0) {
                for (DropdownValue ddValue : ddValues) {
                    list.add( ddValue.getValue() );
                    id.add( ddValue.getId() );
                }
            }
        }else if(flag==4) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchHsseTicket.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select " + idCol + " as paramid ," + valCol + " as paramvalue from workflow_meta_data where type='" + paramType + "' AND val2='" + val2 + "' AND val15='" + val15 + "' ORDER BY val1 ASC",
                    "paramid", "paramvalue", null );

            db2.close();
            list = new ArrayList<String>();
            if (ddValues != null && ddValues.size() > 0) {
                for (DropdownValue ddValue : ddValues) {
                    list.add( ddValue.getValue() );
                    id.add( ddValue.getId() );
                }
            }
        }else if(flag==5) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchHsseTicket.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select " + idCol + " as paramid ," + valCol + " as paramvalue from workflow_meta_data where type='" + paramType + "' AND val15='" + val15 + "' ORDER BY val1 ASC",
                    "paramid", "paramvalue", null );

            db2.close();
            list = new ArrayList<String>();
            if (ddValues != null && ddValues.size() > 0) {
                for (DropdownValue ddValue : ddValues) {
                    list.add( ddValue.getValue() );
                    id.add( ddValue.getId() );
                }
            }
        } else{
            list = db.getLocation(paramType,column);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, list)
        {
        @Override
        public View getDropDownView ( final int position, View convertView, ViewGroup
        parent){
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            final TextView finalItem = tv;
            tv.post( new Runnable() {
                @Override
                public void run() {
                    finalItem.setSingleLine( false );
                }
            });
            return tv;
          }
       };
        dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void setMsg() {
        Utils.msgText( SearchHsseTicket.this, "144", search);
        Utils.msgButton( SearchHsseTicket.this, "71", bt_back);
        Utils.msgText( SearchHsseTicket.this, "77", tv_site_id);
        Utils.msgText( SearchHsseTicket.this, "195", tv_region);
        Utils.msgText( SearchHsseTicket.this, "196", tv_sub_region);
        Utils.msgText( SearchHsseTicket.this, "197", tv_mini_cluster);
        Utils.msgText( SearchHsseTicket.this, "701", tv_tktType);
        Utils.msgText( SearchHsseTicket.this, "702", tv_tkt_classification);
        Utils.msgText( SearchHsseTicket.this, "703", tv_tktStsatus);
        Utils.msgText( SearchHsseTicket.this, "704", tv_type_incident);
        Utils.msgText( SearchHsseTicket.this, "705", tv_force_entry);
        Utils.msgText( SearchHsseTicket.this, "706", tv_alarm_notification);
        Utils.msgText( SearchHsseTicket.this, "709", tv_ticket_id); //group,rca remaining

        Utils.msgText( SearchHsseTicket.this, "710", tv_group);
        Utils.msgText( SearchHsseTicket.this, "711", tv_rca_category);
        Utils.msgText( SearchHsseTicket.this, "712", tv_rca_sub_category);
        Utils.msgText( SearchHsseTicket.this, "713", tv_request_date);
        Utils.msgText( SearchHsseTicket.this, "714", from);
        Utils.msgText( SearchHsseTicket.this, "715", to);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getControllerId(){
        search = (TextView) findViewById( R.id.btn_search_ticket);
        bt_back = (Button) findViewById( R.id.bt_back);
        tv_header = (TextView) findViewById( R.id.tv_header);
        tv_site_id = (TextView) findViewById( R.id.tv_site_id);
        et_site_id = (EditText) findViewById( R.id.et_site_id);

        tv_region = (TextView) findViewById( R.id.tv_region);
        sp_region = (Spinner) findViewById( R.id.sp_region);

        tv_sub_region = (TextView) findViewById( R.id.tv_sub_region);
        sp_sub_region = (Spinner) findViewById( R.id.sp_sub_region);

        tv_mini_cluster = (TextView) findViewById( R.id.tv_mini_cluster);
        sp_mini_cluster = (Spinner) findViewById( R.id.sp_mini_cluster);

        tv_tktType = (TextView) findViewById( R.id.tv_tktType);
        sp_tktType = (Spinner) findViewById( R.id.sp_tktType);

        tv_group = (TextView) findViewById( R.id.tv_group);
        sp_group = (Spinner) findViewById( R.id.sp_group);

        tv_tkt_classification = (TextView) findViewById( R.id.tv_tkt_classification);
        sp_tkt_classification = (Spinner) findViewById( R.id.sp_tkt_classification);

        tv_tktStsatus = (TextView) findViewById( R.id.tv_tktStsatus);
        sp_tktStsatus = (Spinner) findViewById( R.id.sp_tktStsatus);

        tv_type_incident = (TextView) findViewById( R.id.tv_type_incident);
        sp_type_incident = (Spinner) findViewById( R.id.sp_type_incident);

        tv_force_entry = (TextView) findViewById( R.id.tv_force_entry);
        sp_force_entry = (Spinner) findViewById( R.id.sp_force_entry);

        tv_alarm_notification = (TextView) findViewById( R.id.tv_alarm_notification);
        sp_alarm_notification = (Spinner) findViewById( R.id.sp_alarm_notification);

        tv_ticket_id = (TextView) findViewById( R.id.tv_ticket_id);
        et_ticket_id = (EditText) findViewById( R.id.et_ticket_id);

        tv_rca_category = (TextView) findViewById( R.id.tv_rca_category);
        sp_rca_category = (Spinner) findViewById( R.id.sp_rca_category);

        tv_rca_sub_category = (TextView) findViewById( R.id.tv_rca_sub_category);
        sp_rca_sub_category = (Spinner) findViewById( R.id.sp_rca_sub_category);

        from = (TextView) findViewById( R.id.from);
        tv_from_Date = (TextView) findViewById( R.id.tv_from_Date);
        to = (TextView) findViewById( R.id.to);
        tv_to_Date = (TextView) findViewById( R.id.tv_to_Date);
        tv_request_date = (TextView) findViewById( R.id.tv_request_date);
        sp_request_date = (Spinner) findViewById( R.id.sp_request_date);
    }

    public void init(){
        final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        tktType_id = new ArrayList<String>();
        tkt_classification_id = new ArrayList<String>();
        tkt_status_id = new ArrayList<String>();
        group_id = new ArrayList<String>();
        type_of_incident_id = new ArrayList<String>();
        forced_entry_id = new ArrayList<String>();
        alarm_notification_id = new ArrayList<String>();
        rca_cat_id = new ArrayList<String>();
        rca_subcat_id = new ArrayList<String>();
        request_date_id = new ArrayList<String>();

        addItemsOnSpinner(sp_tktType,1,"-173","",tktType_id,"id",
                "val1","","","");
        addItemsOnSpinner(sp_tkt_classification,1, "-172","",tkt_classification_id,"id",
                "val1","","","");
        addItemsOnSpinner(sp_type_incident,1,"-162","",type_of_incident_id,
                "id","val1","","","");
        addItemsOnSpinner(sp_tktStsatus,1, "-169","",tkt_status_id,"id",
                "val1","","","");
        addItemsOnSpinner(sp_group,1, "15","",group_id,"id",
                "val1","","","");
        addItemsOnSpinner(sp_force_entry,1, "-163","",forced_entry_id,
                "id","val1","","","");
        addItemsOnSpinner(sp_alarm_notification,1, "-165","",alarm_notification_id,
                "id","val1","","","");
        addItemsOnSpinner(sp_rca_category,1, "21","",rca_cat_id,"id",
                "val1","","","");
        addItemsOnSpinner(sp_request_date,1, "-170","",request_date_id,"id",
                "val1","","","");
        refreshLocationData();;


        /*sp_tktType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {

                if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                     && !sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,4,"19","",type_of_incident_id,
                            "id","val1",tktType_id.get(sp_tktType.getSelectedItemPosition()),
                             "",
                             tkt_classification_id.get(sp_tkt_classification.getSelectedItemPosition()));
                }else if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                        && sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,2,"19","",type_of_incident_id,
                            "id","val1",tktType_id.get(sp_tktType.getSelectedItemPosition()),"",
                            "");
                }else if(sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                        && !sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,5,"19","",type_of_incident_id,
                            "id","val1","","",
                             tkt_classification_id.get(sp_tkt_classification.getSelectedItemPosition()));
                }else{
                    addItemsOnSpinner(sp_type_incident,1,"19","",type_of_incident_id,
                            "id","val1","","","");
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        sp_tkt_classification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                        && !sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,4,"19","",type_of_incident_id,
                            "id","val1",tktType_id.get(sp_tktType.getSelectedItemPosition()),
                            "",
                            tkt_classification_id.get(sp_tkt_classification.getSelectedItemPosition()));
                }else if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                        && sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,2,"19","",type_of_incident_id,
                            "id","val1",tktType_id.get(sp_tktType.getSelectedItemPosition()),"",
                            "");
                }else if(sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")
                        && !sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_type_incident,5,"19","",type_of_incident_id,
                            "id","val1","","",
                            tkt_classification_id.get(sp_tkt_classification.getSelectedItemPosition()));
                }else{
                    addItemsOnSpinner(sp_type_incident,1,"19","",type_of_incident_id,
                            "id","val1","","","");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/


        sp_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if(!sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_sub_region,0 ,
                            "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE='" +
                                    sp_region.getSelectedItem().toString() + "' ORDER BY UPPER(ZONE) ASC",
                            "ZONE",null,null,null,"","","");
                }else {
                    refreshSubRegionData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        sp_sub_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if(!sp_sub_region.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                    addItemsOnSpinner( sp_mini_cluster, 0,
                            "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE ZONE='" +
                                    sp_sub_region.getSelectedItem().toString() + "' ORDER BY UPPER(CLUSTER) ASC",
                            "CLUSTER",null,null,null,"","","");
                }else if(!sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")
                         ) {
                    addItemsOnSpinner(sp_mini_cluster,0 ,
                            "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE='" +
                                    sp_region.getSelectedItem().toString() + "' ORDER BY UPPER(CLUSTER) ASC",
                            "CLUSTER",null,null,null,"","","");
                }else if (sp_sub_region.getSelectedItem().toString().equalsIgnoreCase("Select")
                          && sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    refreshMiniClusterData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


       /* sp_rca_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if(!sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                    addItemsOnSpinner(sp_rca_sub_category,2, "22","",
                            rca_subcat_id,"id","val1",
                            rca_cat_id.get(sp_rca_category.getSelectedItemPosition()),
                            "","");

                }else{
                    addItemsOnSpinner(sp_rca_sub_category,1, "22","",
                            rca_subcat_id,"id","val1","","","");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/


    }

     private boolean validate() {
        boolean status = true;


        if (tv_from_Date.getText().toString().trim().length() != 0
                && tv_to_Date.getText().toString().trim().length() != 0) {

            String date1=tv_from_Date.getText().toString().toString().trim();

            if(date1.length()>0) {
                String[] arr = new String[3];
                arr = date1.split( "\\-" );
                int day = Integer.parseInt( arr[0] );
                int month = Utils.month( arr[1] );
                int year = Integer.parseInt( arr[2] );
                d1 = Utils.convertStringToDate(
                        new StringBuilder().append(month + 1).append("/")
                                .append(day).append("/").append(year).toString(),
                        "MM/dd/yyyy");
            }

            String date2=tv_to_Date.getText().toString().toString().trim();
            if(date2.length()>0){
                String[] arr = new String[3];
                arr = date2.split( "\\-" );
                int day=Integer.parseInt( arr[0] );
                int month= Utils.month(arr[1]);
                int year=Integer.parseInt( arr[2] );
                d2 = Utils.convertStringToDate(
                        new StringBuilder().append(month + 1).append("/")
                                .append(day).append("/").append(year).toString(),
                        "MM/dd/yyyy");
            }


            if (!Utils.checkDateCompare( d1, d2 )) {
                Toast.makeText( SearchHsseTicket.this,"Start Date cannot be greater than End Date",Toast.LENGTH_LONG).show();
                return false;
            }else if(Utils.diffDays(tv_from_Date.getText().toString(),tv_to_Date.getText().toString())>mAppPreferences.getSearchTTDateRange()){
                Toast.makeText( SearchHsseTicket.this,"Start Date and End Date Difference cannot be greater than "+mAppPreferences.getSearchTTDateRange()+" Days",Toast.LENGTH_LONG).show();
                return false;
            }
        }

        try {

            filterData = new JSONObject();
            //filterData.put("loginId",mAppPreferences.getUserId());
            //filterData.put("userGrpLvl",mAppPreferences.getUserGroup());
            filterData.put("rptType","1");
            if(!sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                filterData.put("cid",db.getLocationId("Select DISTINCT(CIRCLE_ID) from LOCATION_DDL WHERE CIRCLE='"
                                + sp_region.getSelectedItem().toString() + "'",
                        "CIRCLE_ID"));
            }

            if(!sp_sub_region.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("zid",db.getLocationId("Select DISTINCT(ZONE_ID) from LOCATION_DDL WHERE ZONE='"
                                + sp_sub_region.getSelectedItem().toString() + "'",
                        "ZONE_ID"));
            }
            if(!sp_mini_cluster.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("clid",db.getLocationId("Select DISTINCT(CLUSTER_ID) from LOCATION_DDL WHERE CLUSTER='"
                                + sp_mini_cluster.getSelectedItem().toString() + "'",
                        "CLUSTER_ID"));
            }

            if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("ticketType",tktType_id.get(sp_tktType.getSelectedItemPosition()));
            }

            if(!sp_group.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("assigntogrp",group_id.get(sp_group.getSelectedItemPosition()));
            }

            if(!sp_tkt_classification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("severity",tkt_classification_id.get(sp_tkt_classification.getSelectedItemPosition()));
            }

            if(!sp_tktStsatus.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("tktstatus",tkt_status_id.get(sp_tktStsatus.getSelectedItemPosition()));
            }

            if(!sp_type_incident.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("incidentType",type_of_incident_id.get(sp_type_incident.getSelectedItemPosition()));
            }

            if(!sp_force_entry.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("forcedEntry",forced_entry_id.get(sp_force_entry.getSelectedItemPosition()));
            }

            if(!sp_alarm_notification.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("AlarmNtficatn",alarm_notification_id.get(sp_alarm_notification.getSelectedItemPosition()));
            }

            if(!sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("rca_Cat_Id",rca_cat_id.get(sp_rca_category.getSelectedItemPosition()));
            }

            if(!sp_rca_sub_category.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("rca_SubCat_Id",rca_subcat_id.get(sp_rca_sub_category.getSelectedItemPosition()));
            }


            if(!sp_request_date.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("dateType",request_date_id.get(sp_request_date.getSelectedItemPosition()));
            }else{
                filterData.put("dateType","");
            }
            filterData.put("sdate",tv_from_Date.getText().toString());
            filterData.put("edate",tv_to_Date.getText().toString());
            filterData.put("sid",et_site_id.getText().toString().trim());
            filterData.put("ticketId",et_ticket_id.getText().toString().trim());
        }catch (Exception e){
        }
        return status;
    }

   public void fromDatePicker(Context contect, final TextView et){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String date=et.getText().toString().toString();


        if(date.length()>0){
            String[] arr = new String[3];
            arr = date.split( "\\-" );
            day=Integer.parseInt( arr[0] );
            month= Utils.month(arr[1]);
            year=Integer.parseInt( arr[2] );
        }

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(contect,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                d1 = Utils.convertStringToDate(
                        new StringBuilder().append( monthOfYear + 1 ).append( "/" )
                                .append( dayOfMonth ).append( "/" ).append( year ).toString(),
                        "MM/dd/yyyy" );
                if (sp_request_date.getSelectedItem().toString().equals("Select")){
                    Toast.makeText( SearchHsseTicket.this,
                            "Select "+tv_request_date.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (Utils.checkValidation(d1)) {
                    et.setText( Utils.changeDateFormat(new StringBuilder()
                                    .append(monthOfYear + 1).append("/").append(dayOfMonth)
                                    .append("/").append(year).toString(), "MM/dd/yyyy",
                            "dd-MMM-yyyy"));
                } else {
                    Toast.makeText( SearchHsseTicket.this,"Start Date cannot be greater than Current Date",
                            Toast.LENGTH_LONG).show();
                    // Utils.toast( SearchRequest.this, "206");
                }
            }
        }, year, month, day);
        picker.show();
    }

    public void ToDatePicker(Context contect,final TextView et){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String date=et.getText().toString().toString();


        if(date.length()>0){
            String[] arr = new String[3];
            arr = date.split( "\\-" );
            day=Integer.parseInt( arr[0] );
            month= Utils.month(arr[1]);
            year=Integer.parseInt( arr[2] );
        }

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(contect,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                d2 = Utils.convertStringToDate(
                        new StringBuilder().append(monthOfYear + 1).append("/")
                                .append(dayOfMonth).append("/").append(year).toString(),
                        "MM/dd/yyyy");
                if (sp_request_date.getSelectedItem().toString().equals("Select")){
                    Toast.makeText( SearchHsseTicket.this,
                            "Select "+tv_request_date.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (tv_from_Date.getText().toString().length()==0) {
                    Toast.makeText( SearchHsseTicket.this,
                            "Select "+from.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (Utils.checkValidation(d2)) {
                    et.setText( Utils.changeDateFormat(new StringBuilder()
                                    .append(monthOfYear + 1).append("/").append(dayOfMonth)
                                    .append("/").append(year).toString(), "MM/dd/yyyy",
                            "dd-MMM-yyyy"));
                } else {
                    Utils.toast( SearchHsseTicket.this, "207");
                    // To Date cannot be greater than Current Date;

                }
            }
        }, year, month, day);
        picker.show();
    }

    public class LocationTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        LocationList dataResponse;
        public LocationTask(Context con) {
            this.con = con;
        }
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            pd.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("countryId", ""));
            nameValuePairs.add(new BasicNameValuePair("hubId", ""));
            nameValuePairs.add(new BasicNameValuePair("regionId", ""));
            nameValuePairs.add(new BasicNameValuePair("circleId", ""));
            nameValuePairs.add(new BasicNameValuePair("zoneId", ""));
            nameValuePairs.add(new BasicNameValuePair("clusterId",""));
            url=mAppPreferences.getConfigIP()+ WebMethods.url_getlocation;
            String response = Utils.httpPostRequest(con,url, nameValuePairs);
            Gson gson = new Gson();
            try {
                dataResponse = gson.fromJson(response, LocationList.class);
            } catch (Exception e) {
                dataResponse = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if ((dataResponse==null)) {
                //Toast.makeText(EnergyManagement.this,"Meta data not provided by server.",Toast.LENGTH_LONG).show();
                Utils.toast( SearchHsseTicket.this, "70");
            }else if (dataResponse.getLocationList()!=null && dataResponse.getLocationList().size() > 0){
                db.clearLocationData();
                db.insertLocationData(dataResponse.getLocationList());
                db.dataTS(null, null,"18",
                        db.getLoginTimeStmp("18","0"),2,"0");
                init();
            }else{
                //Toast.makeText(EnergyManagement.this, "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast( SearchHsseTicket.this, "13");
            }
            super.onPostExecute(result);
        }
    }

    public String locationDataType(){
        String DataType_Str="1";
        String i= Utils.CompareDates(db.getSaveTimeStmp("18","0"),db.getLoginTimeStmp("18","0"),"18");
        if(i!="1"){
            DataType_Str=i;
        }if(DataType_Str=="1"){
            DataType_Str="";
        }
        return DataType_Str;
    }

    public void refreshLocationData(){
        if(mAppPreferences.getCircleID().equalsIgnoreCase( AppConstants.ZERO )){
            addItemsOnSpinner(sp_region,0 ,
                    "Select DISTINCT(CIRCLE) from LOCATION_DDL ORDER BY UPPER(CIRCLE) ASC",
                    "CIRCLE",null,null,null,"","","");

            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null,"","","");

           addItemsOnSpinner(sp_mini_cluster,0 ,
                    "Select DISTINCT(CLUSTER) from LOCATION_DDL ORDER BY UPPER(CLUSTER) ASC",
                    "CLUSTER",null,null,null,"","","");
        }else {
            addItemsOnSpinner(sp_region,0 ,
                    "Select DISTINCT(CIRCLE) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(CIRCLE) ASC",
                    "CIRCLE",null,null,null,"","","");

            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null,"","","");

            if(mAppPreferences.getZoneID().equalsIgnoreCase( AppConstants.ZERO )){
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null,"","","");
            }else{
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") AND ZONE_ID in ("+mAppPreferences.getZoneID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null,"","","");
            }
        }
    }

    public void refreshSubRegionData() {
        if(mAppPreferences.getCircleID().equalsIgnoreCase( AppConstants.ZERO )){
            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null,"","","");

        }else {
            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null,"","","");
        }
    }

    public void refreshMiniClusterData() {
        if(mAppPreferences.getCircleID().equalsIgnoreCase( AppConstants.ZERO )){
            addItemsOnSpinner(sp_mini_cluster,0 ,
                    "Select DISTINCT(CLUSTER) from LOCATION_DDL ORDER BY UPPER(CLUSTER) ASC",
                    "CLUSTER",null,null,null,"","","");
        }else {
             if(mAppPreferences.getZoneID().equalsIgnoreCase( AppConstants.ZERO )){
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null,"","","");
            }else{
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") AND ZONE_ID in ("+mAppPreferences.getZoneID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null,"","","");
            }
        }

    }
}

