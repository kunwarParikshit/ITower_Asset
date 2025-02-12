package com.isl.workflow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.isl.workflow.tabs.RequestReport;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 6/24/2020.
 */

public class SearchRequest extends Activity {
    AppPreferences mAppPreferences;

    EditText et_site_id,et_change_id,et_requester_name;

    public Spinner sp_region,sp_sub_region,sp_mini_cluster,sp_domain,sp_change_type,sp_product_type,
            sp_purpose,sp_activity_status,sp_request_status,sp_request_date;

    TextView search,tv_header,tv_region,tv_sub_region,tv_mini_cluster,tv_domain,
            tv_change_type,tv_product_type,tv_purpose,tv_activity_status,tv_change_id,tv_site_id,
            from,tv_from_Date,to,tv_to_Date,tv_request_status,tv_requester_name,tv_request_date;

    Button bt_back;
    List<String> domain_id,purpose_id,change_type_id,product_type_id,activity_status_id,request_status_id,request_date_id;
    JSONObject filterData =null;

    DataBaseHelper db;
    String url = "";
    private int pYear, pMonth, pDay;
    Date d1, d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences( SearchRequest.this);
        db = new DataBaseHelper( SearchRequest.this);
        db.open();
        setContentView( R.layout.workflow_search_ticket);

        getControllerId();
        setMsg();
       /* if (Utils.isNetworkAvailable(SearchRequest.this)) {
            new GroupId(SearchRequest.this).execute();
        }*/

        if(!locationDataType().equalsIgnoreCase("")){
            if (Utils.isNetworkAvailable(SearchRequest.this)) {
                new LocationTask(SearchRequest.this).execute();
            }else{
                //No internet connection.Please download meta data
                Utils.toast(SearchRequest.this, "67");
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
                fromDatePicker( SearchRequest.this,tv_from_Date);
            }
        });

        tv_to_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ToDatePicker( SearchRequest.this,tv_to_Date);
            }
        });




        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    JSONArray jsonArrChkImg = new JSONArray();
                    jsonArrChkImg.put( filterData );
                    Intent i = new Intent( SearchRequest.this, RequestReport.class);
                    i.putExtra("filterData",filterData.toString());
                    startActivity(i);
                }
            }
        });
    }

    public void addItemsOnSpinner(Spinner spinner,int flag,String paramType,String column,List<String> id, String idCol, String valCol) {
        List<String> list= null;

        if(flag==1) {
            List<DropdownValue> ddValues = null;
            WorkFlowDatabaseHelper db2 = new WorkFlowDatabaseHelper( SearchRequest.this );
            db2.open();
            ddValues = db2.getDropdownList( "Select "+idCol+" as paramid ,"+valCol+" as paramvalue from workflow_meta_data where type='" + paramType + "'ORDER BY val1 ASC",
                    "paramid", "paramvalue",null );
            db2.close();
            list = new ArrayList<String>();
            if(ddValues != null && ddValues.size()>0){
                for(DropdownValue ddValue : ddValues){
                    list.add(ddValue.getValue());
                    id.add(ddValue.getId());
                }
            }
        }else if(flag == 2){
            list = new ArrayList<String>();
            list.add(Utils.msg(SearchRequest.this,"1005"));
            list.add(Utils.msg(SearchRequest.this,"1006"));
        } else{
            list = db.getLocation(paramType,column);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void setMsg() {
        //Utils.msgText( SearchRequest.this, "604", tv_reuest);
        Utils.msgText( SearchRequest.this, "605", tv_requester_name);
        Utils.msgText( SearchRequest.this, "606", tv_request_date);
        Utils.msgText( SearchRequest.this, "195", tv_region);
        Utils.msgText( SearchRequest.this, "196", tv_sub_region);
        Utils.msgText( SearchRequest.this, "197", tv_mini_cluster);
        Utils.msgText( SearchRequest.this, "595", tv_domain);
        Utils.msgText( SearchRequest.this, "596", tv_change_type);
        Utils.msgText( SearchRequest.this, "597", tv_product_type);
        Utils.msgText( SearchRequest.this, "598", tv_purpose);
        Utils.msgText( SearchRequest.this, "599", tv_activity_status);
        Utils.msgText( SearchRequest.this, "600", tv_change_id);
        Utils.msgText( SearchRequest.this, "601", from);
        Utils.msgText( SearchRequest.this, "602", to);
        Utils.msgText( SearchRequest.this, "603", tv_request_status);
        Utils.msgText( SearchRequest.this, "144", search);
        Utils.msgButton( SearchRequest.this, "71", bt_back);
        Utils.msgText( SearchRequest.this, "77", tv_site_id);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getControllerId(){
        tv_header = (TextView) findViewById( R.id.tv_header);
        tv_request_date = (TextView) findViewById( R.id.tv_request_date);
        tv_requester_name = (TextView) findViewById( R.id.tv_requester_name);
        tv_region = (TextView) findViewById( R.id.tv_region);
        tv_sub_region = (TextView) findViewById( R.id.tv_sub_region);
        tv_mini_cluster = (TextView) findViewById( R.id.tv_mini_cluster);
        tv_domain = (TextView) findViewById( R.id.tv_domain);
        tv_change_type = (TextView) findViewById( R.id.tv_change_type);
        tv_product_type = (TextView) findViewById( R.id.tv_product_type);
        tv_purpose = (TextView) findViewById( R.id.tv_purpose);
        tv_activity_status = (TextView) findViewById( R.id.tv_activity_status);
        tv_change_id = (TextView) findViewById( R.id.tv_change_id);
        tv_site_id = (TextView) findViewById( R.id.tv_site_id);
        from = (TextView) findViewById( R.id.from);
        tv_from_Date = (TextView) findViewById( R.id.tv_from_Date);
        to = (TextView) findViewById( R.id.to);
        tv_to_Date = (TextView) findViewById( R.id.tv_to_Date);
        tv_request_status = (TextView) findViewById( R.id.tv_request_status);

        et_change_id = (EditText) findViewById( R.id.et_change_id);
        et_site_id = (EditText) findViewById( R.id.et_site_id);
        et_requester_name = (EditText) findViewById( R.id.et_requester_name);

        sp_region = (Spinner) findViewById( R.id.sp_region);
        sp_sub_region = (Spinner) findViewById( R.id.sp_sub_region);
        sp_mini_cluster = (Spinner) findViewById( R.id.sp_mini_cluster);
        sp_domain = (Spinner) findViewById( R.id.sp_domain);
        sp_change_type = (Spinner) findViewById( R.id.sp_change_type);
        sp_product_type = (Spinner) findViewById( R.id.sp_product_type);
        sp_purpose = (Spinner) findViewById( R.id.sp_purpose);
        sp_activity_status = (Spinner) findViewById( R.id.sp_activity_status);
        sp_request_status = (Spinner) findViewById( R.id.sp_request_status);
        sp_request_date = (Spinner) findViewById( R.id.sp_request_date);

        search = (TextView) findViewById( R.id.btn_search_ticket);
        bt_back = (Button) findViewById( R.id.bt_back);

    }

    public void init(){
        final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        domain_id = new ArrayList<String>();
        purpose_id = new ArrayList<String>();
        change_type_id = new ArrayList<String>();
        product_type_id = new ArrayList<String>();
        activity_status_id = new ArrayList<String>();
        request_status_id = new ArrayList<String>();
        request_date_id = new ArrayList<String>();
        addItemsOnSpinner(sp_domain,1,"-131","",domain_id,"id","val1");
        addItemsOnSpinner(sp_purpose,1, "-132","",purpose_id,"id","val1");
        addItemsOnSpinner(sp_change_type,1, "-133","",change_type_id,"id","val1");
        addItemsOnSpinner(sp_product_type,1, "-134","",product_type_id,"id","val1");
        addItemsOnSpinner(sp_activity_status,1, "-135","",activity_status_id,"val2","val1");
        addItemsOnSpinner(sp_request_status,1, "-136","",request_status_id,"val2","val1");
        addItemsOnSpinner(sp_request_date,1, "-137","",request_date_id,"id","val1");

        if(mAppPreferences.getCircleID().equalsIgnoreCase( AppConstants.ZERO )){
            addItemsOnSpinner(sp_region,0 ,
                    "Select DISTINCT(CIRCLE) from LOCATION_DDL ORDER BY UPPER(CIRCLE) ASC",
                    "CIRCLE",null,null,null);

            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null);

            addItemsOnSpinner(sp_mini_cluster,0 ,
                    "Select DISTINCT(CLUSTER) from LOCATION_DDL ORDER BY UPPER(CLUSTER) ASC",
                    "CLUSTER",null,null,null);
        }else {
            addItemsOnSpinner(sp_region,0 ,
                    "Select DISTINCT(CIRCLE) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(CIRCLE) ASC",
                    "CIRCLE",null,null,null);

            addItemsOnSpinner(sp_sub_region,0 ,
                    "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(ZONE) ASC",
                    "ZONE",null,null,null);

            if(mAppPreferences.getZoneID().equalsIgnoreCase( AppConstants.ZERO )){
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null);
            }else{
                addItemsOnSpinner(sp_mini_cluster,0 ,
                        "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE_ID in ("+mAppPreferences.getCircleID()+") AND ZONE_ID in ("+mAppPreferences.getZoneID()+") ORDER BY UPPER(CLUSTER) ASC",
                        "CLUSTER",null,null,null);
            }
        }

        sp_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if(!sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")){
                    addItemsOnSpinner(sp_sub_region,0 ,
                            "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE='" +
                                    sp_region.getSelectedItem().toString() + "' ORDER BY UPPER(ZONE) ASC",
                            "ZONE",null,null,null);

                    addItemsOnSpinner(sp_mini_cluster,0 ,
                            "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE='" +
                                    sp_region.getSelectedItem().toString() + "' ORDER BY UPPER(CLUSTER) ASC",
                            "CLUSTER",null,null,null);
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
                            "CLUSTER",null,null,null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
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
                Toast.makeText(SearchRequest.this,"Start Date cannot be greater than End Date",Toast.LENGTH_LONG).show();
                return false;
            }else if(Utils.diffDays(tv_from_Date.getText().toString(),tv_to_Date.getText().toString())>mAppPreferences.getSearchTTDateRange()){
                Toast.makeText( SearchRequest.this,"Start Date and End Date Difference cannot be greater than "+mAppPreferences.getSearchTTDateRange()+" Days",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        try {

            filterData = new JSONObject();
            //filterData.put("loginId",mAppPreferences.getUserId());
            //filterData.put("userGrpLvl",mAppPreferences.getUserGroup());
            filterData.put("rptType","1");
            filterData.put("src","M");

            if(!sp_region.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                filterData.put("cid",db.getLocationId("Select DISTINCT(CIRCLE_ID) from LOCATION_DDL WHERE CIRCLE='"
                                + sp_region.getSelectedItem().toString() + "'",
                        "CIRCLE_ID"));
            }else{
                filterData.put("cid",mAppPreferences.getCircleID());
            }

            if(!sp_sub_region.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("zid",db.getLocationId("Select DISTINCT(ZONE_ID) from LOCATION_DDL WHERE ZONE='"
                                + sp_sub_region.getSelectedItem().toString() + "'",
                        "ZONE_ID"));
            }else{
                filterData.put("zid",mAppPreferences.getZoneID());
            }

            if(!sp_mini_cluster.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("clid",db.getLocationId("Select DISTINCT(CLUSTER_ID) from LOCATION_DDL WHERE CLUSTER='"
                                + sp_mini_cluster.getSelectedItem().toString() + "'",
                        "CLUSTER_ID"));
            }else{
                filterData.put("clid",mAppPreferences.getClusterID());
            }

            if(!sp_domain.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("dmn",domain_id.get(sp_domain.getSelectedItemPosition()));
            }else{
                filterData.put("dmn","");
            }

            if(!sp_change_type.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("cngtype",change_type_id.get(sp_change_type.getSelectedItemPosition()));
            }else{
                filterData.put("cngtype","");
            }


            if(!sp_product_type.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("prdtype",product_type_id.get(sp_product_type.getSelectedItemPosition()));
            }else{
                filterData.put("prdtype","");
            }

            if(!sp_purpose.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("ppr",purpose_id.get(sp_purpose.getSelectedItemPosition()));
            }else{
                filterData.put("ppr","");
            }

            if(et_site_id.getText().toString().length()>0){
                filterData.put("sid",et_site_id.getText().toString().trim());
            }else{
                filterData.put("sid","");
            }

            if(!sp_activity_status.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("asts",activity_status_id.get(sp_activity_status.getSelectedItemPosition()));
            }else{
                filterData.put("asts","");
            }

            if(!sp_request_status.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("rqsts",request_status_id.get(sp_request_status.getSelectedItemPosition()));
            }else{
                filterData.put("rqsts","");
            }

            if(!sp_request_date.getSelectedItem().toString().equalsIgnoreCase("Select")){
                filterData.put("dateType",request_date_id.get(sp_request_date.getSelectedItemPosition()));
            }else{
                filterData.put("dateType","");
            }
            filterData.put("sdate",tv_from_Date.getText().toString());
            filterData.put("edate",tv_to_Date.getText().toString());
            filterData.put("txnid",et_change_id.getText().toString().trim());
            filterData.put("sid",et_site_id.getText().toString().trim());
            filterData.put("rqname",et_requester_name.getText().toString().trim());
        }catch (Exception e){
        }
        return status;
    }

    public void fromDatePicker(Context contect,final TextView et){
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
                    Toast.makeText(SearchRequest.this,
                            "Select "+tv_request_date.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (Utils.checkValidation(d1)) {
                    et.setText( Utils.changeDateFormat(new StringBuilder()
                                    .append(monthOfYear + 1).append("/").append(dayOfMonth)
                                    .append("/").append(year).toString(), "MM/dd/yyyy",
                            "dd-MMM-yyyy"));
                } else {
                    Toast.makeText(SearchRequest.this,"Start Date cannot be greater than Current Date",
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
                    Toast.makeText(SearchRequest.this,
                            "Select "+tv_request_date.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (tv_from_Date.getText().toString().length()==0) {
                    Toast.makeText(SearchRequest.this,
                            "Select "+from.getText().toString(),Toast.LENGTH_LONG).show();
                    return;
                }else if (Utils.checkValidation(d2)) {
                    et.setText( Utils.changeDateFormat(new StringBuilder()
                                    .append(monthOfYear + 1).append("/").append(dayOfMonth)
                                    .append("/").append(year).toString(), "MM/dd/yyyy",
                            "dd-MMM-yyyy"));
                } else {
                    Utils.toast( SearchRequest.this, "207");
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
                Utils.toast( SearchRequest.this, "70");
            }else if (dataResponse.getLocationList()!=null && dataResponse.getLocationList().size() > 0){
                db.clearLocationData();
                db.insertLocationData(dataResponse.getLocationList());
                db.dataTS(null, null,"18",
                        db.getLoginTimeStmp("18","0"),2,"0");
                init();
            }else{
                //Toast.makeText(EnergyManagement.this, "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(SearchRequest.this, "13");
            }
            super.onPostExecute(result);
        }
    }

    public String locationDataType(){
        String DataType_Str="1";
        String i=Utils.CompareDates(db.getSaveTimeStmp("18","0"),db.getLoginTimeStmp("18","0"),"18");
        if(i!="1"){
            DataType_Str=i;
        }if(DataType_Str=="1"){
            DataType_Str="";
        }
        return DataType_Str;
    }
}
