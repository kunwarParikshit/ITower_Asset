package com.isl.approval;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.HomeActivity;
import com.isl.modal.EnergyMetaList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;
/**
 * Created by dhakan on 6/6/2019.
 */
public class Approval extends Activity {
    TextView tv_brand_logo, tv_spare_parts, tv_dfr;
    String dfrRight,sparePartRight,moduleUrl="";
    DataBaseHelper dbHelper;
    AppPreferences mAppPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_approval);
        mAppPreferences = new AppPreferences(Approval.this);
        Button bt_back = (Button) findViewById(R.id.bt_back);
        tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo);
        tv_spare_parts= (TextView) findViewById( R.id.tv_spare_parts);
        tv_dfr= (TextView) findViewById( R.id.tv_dfr);
        dbHelper = new DataBaseHelper(Approval.this);
        dbHelper.open();
        moduleUrl = dbHelper.getModuleIP("Approval");
        Utils.msgText(Approval.this, "401", tv_brand_logo);
        getMenuRights();
        dbHelper.close();

        if(!dataType().equalsIgnoreCase("")){
            if (Utils.isNetworkAvailable(Approval.this)) {
                new EnergyMetaDataTask(Approval.this).execute();
            }
        }

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                  Intent i = new Intent(Approval.this, HomeActivity.class);
                  startActivity(i);
                  finish();

            }
        });

        Utils.msgText(Approval.this, "402", tv_dfr);
        tv_dfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Approval.this, DFRApprovalCount.class);
                startActivity(i);
                //finish();

            }
        });
        Utils.msgText(Approval.this, "403", tv_spare_parts);
        tv_spare_parts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent i = new Intent(Approval.this, DFRApprovalCount.class);
                Intent i = new Intent(Approval.this, SparePartTabs.class);
                startActivity(i);
                //finish();

            }
        });
    }

    public void getMenuRights() {
        dfrRight = dbHelper.getSubMenuRight("DFR Approval", "Approval");
        sparePartRight = dbHelper.getSubMenuRight("Spare Part Approval", "Approval");
        if(dfrRight.contains("V") || dfrRight.contains("A")){
            tv_dfr.setVisibility(View.VISIBLE);
        }else{
            tv_dfr.setVisibility(View.GONE);
        }

        if(sparePartRight.contains( "V" ) || sparePartRight.contains( "A" )){
            tv_spare_parts.setVisibility(View.VISIBLE);
        }else{
            tv_spare_parts.setVisibility(View.GONE);
        }
     }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Approval.this, HomeActivity.class);
        startActivity(i);
        finish();
    }


    public String dataType(){
        dbHelper.open();
        String DataType_Str="1";
        String i=Utils.CompareDates(dbHelper.getSaveTimeStmp("16","0"),
                dbHelper.getLoginTimeStmp("16","0"),"16");
        String j = Utils.CompareDates(dbHelper.getSaveTimeStmp("22","0"),
                dbHelper.getLoginTimeStmp("22","0"), "22");
        dbHelper.close();
        if (i != "1") {
            DataType_Str = i;
        }
        if (j != "1") {
            if (DataType_Str == "1") {
                DataType_Str = j;
            } else {
                DataType_Str = DataType_Str + "," + j;
            }
        }
        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }

    public class EnergyMetaDataTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        EnergyMetaList dataResponse;
        public EnergyMetaDataTask(Context con) {
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
            String url = "";
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("module","Energy"));
            nameValuePairs.add(new BasicNameValuePair("datatype",dataType()));
            nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
            nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
            nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
            if(moduleUrl.equalsIgnoreCase("0")){
                url=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
            }else{
                url=moduleUrl+WebMethods.url_GetMetadata;
            }
            String response = Utils.httpPostRequest(con,url, nameValuePairs);
            Gson gson = new Gson();
            try {
                dataResponse = gson.fromJson(response, EnergyMetaList.class);
            } catch (Exception e) {
                e.printStackTrace();
                dataResponse = null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if ((dataResponse == null)) {
                //Toast.makeText(EnergyManagement.this,"Meta data not provided by server.",Toast.LENGTH_LONG).show();
                //WorkFlowUtils.toast(Approval.this, "70");
            }else if (dataResponse!=null){
                DataBaseHelper dbHelper = new DataBaseHelper(Approval.this);
                dbHelper.open();

                if(dataResponse.getVendors()!=null && dataResponse.getVendors().size()>0){
                    dbHelper.clearVender();
                    dbHelper.insertEnergyVender(dataResponse.getVendors());
                    dbHelper.dataTS(null, null,"16",dbHelper.getLoginTimeStmp("16","0"),2,"0");
                }
                if (dataResponse.getUser() !=null && dataResponse.getUser().size() > 0) {
                    dbHelper.clearUserContact("654");
                    dbHelper.insertUserContact(dataResponse.getUser(),"654");
                    dbHelper.dataTS(null, null, "22",
                            dbHelper.getLoginTimeStmp("22","0"), 2,"0");
                }
                dbHelper.close();
            }else{
                //Toast.makeText(EnergyManagement.this, "Server Not Available",Toast.LENGTH_LONG).show();
                //WorkFlowUtils.toast(EnergyManagement.this, "13");
            }
            super.onPostExecute(result);
        }
    }

}
