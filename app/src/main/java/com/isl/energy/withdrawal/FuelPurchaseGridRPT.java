package com.isl.energy.withdrawal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.constant.WebMethods;
import com.isl.energy.AsyncTaskService;
import com.isl.energy.TaskCompleted;
import com.isl.modal.FuelWithdrawalDataList;
import com.isl.taskform.ActivityTaskForm;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import infozech.itower.R;

/**
 * Created by dhakan on 10/31/2018.
 */

public class FuelPurchaseGridRPT extends Activity implements TaskCompleted {

    TextView txt_no_ticket, tv_add;
    ListView rptGrid;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    FuelWithdrawalDataList rptData;
    RelativeLayout rl_no_list;
    String url = "", response, rights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.layout_fp_grid_rpt );
        init();

        if (!rights.contains( "R" )) {
            nextActivity( "0",null );
        } else {
            getPurchaseReport();
            tv_add.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nextActivity( "1",null );
                }
            } );

            rptGrid.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int posi, long arg3) {
                    selectGridData( posi );
                }
            } );

        }
    }

    public void getPurchaseReport() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 6 );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.CIRCLE_ID_ALIAS, mAppPreferences.getCircleID() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.ZONE_ID_ALIAS, mAppPreferences.getZoneID() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.CLUSTER_ID_ALIAS, mAppPreferences.getClusterID() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.OME_ID_ALIAS, mAppPreferences.getPIOMEID() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.STATUS_ALIAS, "0" ) );
        String moduleUrl = db.getModuleIP( "Energy" );

        if (moduleUrl.equalsIgnoreCase( "0" )) {
            url = mAppPreferences.getConfigIP() + WebMethods.urlPurchaseRpt;
        } else {
            url = moduleUrl + WebMethods.urlPurchaseRpt;
        }
        if (Utils.isNetworkAvailable( FuelPurchaseGridRPT.this )) {
            new AsyncTaskService( FuelPurchaseGridRPT.this, AppConstants.ENERGY_TASK_TYPE.ENERGY_PURCHASE_RPT, url, nameValuePairs ).execute();
        } else {
            Utils.toast( FuelPurchaseGridRPT.this, "17" );
        }
    }

    public void init() {

        mAppPreferences = new AppPreferences( this );
        db = new DataBaseHelper( this );
        db.open();
        rights = db.getSubMenuRight( "FuelPurchase", "Energy" );
        TextView tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo );
        rl_no_list = (RelativeLayout) findViewById( R.id.textlayout );
        txt_no_ticket = (TextView) findViewById( R.id.txt_no_ticket );
        rptGrid = (ListView) findViewById( R.id.lv_tickets );
        tv_add = (TextView) findViewById( R.id.tv_add );

        if (rights.contains( "A" )) {
            tv_add.setVisibility( View.VISIBLE );
        } else {
            tv_add.setVisibility( View.GONE );
        }

        Button bt_back = (Button) findViewById( R.id.bt_back );
        bt_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        } );
    }

    private void nextActivity(String mode,HashMap<String, String> tranData) {

        if(tranData==null){
            tranData = new HashMap<String, String>();
            tranData.put(AppConstants.TASK_STATE_ID_ALIAS,"11");
            tranData.put(AppConstants.IS_EDIT_ALIAS,"1");
        }

        tranData.put(AppConstants.FLAG_ALIAS,mode);
        tranData.put(AppConstants.HEADER_CAPTION_ID,"194");
        tranData.put(AppConstants.OPERATION,"A");
        tranData.put(AppConstants.MODULE,"FP");
        tranData.put(AppConstants.ClassModule,"FPG");
        if(tranData.containsKey(AppConstants.SITE_ID_ALIAS)){
            tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,tranData.get(AppConstants.SITE_ID_ALIAS)+"-FFR");
        } else{
            tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,"FFR");
        }

        Intent i = new Intent( FuelPurchaseGridRPT.this, ActivityTaskForm.class );
        i.putExtra("sel","FPG");
        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
        startActivity( i );
        finish();
    }

    public void selectGridData(int posi) {

        HashMap<String, String> tranData = new HashMap<String, String>();

        try {
            JSONObject reader = new JSONObject( response );
            JSONArray s = reader.getJSONArray( "DieselWithdrawl" );
            JSONObject c = s.getJSONObject( posi );
            Iterator<String> keys = c.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                tranData.put( key, c.get( key ).toString() );
            }
        } catch (JSONException e) {
        }
        nextActivity("1",tranData);
    }

    @Override
    public void onPurchaseGridRPTComplete(String result) {
        try {
            response = result;
            Gson gson = new Gson();
            rptData = gson.fromJson( response, FuelWithdrawalDataList.class );
        } catch (Exception e) {
            rptData = null;
        }

        if (rptData == null) {
            // Server Not Available
            Utils.toast( FuelPurchaseGridRPT.this, "13" );
        } else if (rptData.getDieselWithdrawl() != null && rptData.getDieselWithdrawl().size() > 0) {
            rptGrid.setAdapter( new AdapterGrid( FuelPurchaseGridRPT.this, response ) );
            rptGrid.setVisibility( View.VISIBLE );
            rl_no_list.setVisibility( View.INVISIBLE );
        } else {
            rl_no_list.setVisibility( View.VISIBLE );
            txt_no_ticket.setText( "No Data Found" );
            Toast.makeText( FuelPurchaseGridRPT.this, "No Data Found", Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void onFillingGridRPTComplete(String result) {

    }

    @Override
    public void onLocationTaskComplete(String result) {

    }

    @Override
    public void onEnergyParamTaskComplete(String result) {

    }
}
