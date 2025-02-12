package com.isl.asset;
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.LocationList;
import com.isl.util.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class AssetsActivity extends Activity {
	TextView tv_site_general_details,tv_active_assets,tv_passive_assets;
	AppPreferences mAppPreferences;
	String[] SiteGeneralDetails;
	String[] PassiveAssets;
	String general_details,passive_asset,active_asset; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assets);
		tv_site_general_details=(TextView)findViewById(R.id.tv_site_general_detail);
		mAppPreferences = new AppPreferences( AssetsActivity.this);
		TextView Tv_SiteId=(TextView)findViewById(R.id.tv_site_id);
		Tv_SiteId.setText(""+mAppPreferences.getSiteID());
		Button iv_back = (Button) findViewById(R.id.button_back);
		tv_passive_assets=(TextView)findViewById(R.id.tv_passive_assets);
		tv_active_assets=(TextView)findViewById(R.id.tv_active_assets);
		getMenuRights();
		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i=new Intent( AssetsActivity.this,AuditScheduleList.class);
		startActivity(i);
		finish();
		}
		});
		
		tv_site_general_details.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (Utils.isNetworkAvailable( AssetsActivity.this)) {
				if(!locationDataType().equalsIgnoreCase("")){
					new LocationTask( AssetsActivity.this).execute();
				}else{
					Intent intent = new Intent( AssetsActivity.this,SiteGeneralDetails.class);
					intent.putExtra("siteGenDetails",general_details);
					startActivity(intent);	
					finish();
				}
			} else {
				Intent intent = new Intent( AssetsActivity.this,SiteGeneralDetails.class);
				intent.putExtra("siteGenDetails",general_details);
				startActivity(intent);	
				finish();
			}		
			
		}
		});
		
		tv_active_assets.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText( AssetsActivity.this, "Comming Soon", Toast.LENGTH_LONG).show();
		}
		});
		
		tv_passive_assets.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent( AssetsActivity.this,SiteInitiativeDetails.class);
			startActivity(intent);
			finish();
		}
		});
	    }
	
	@Override
	public void onBackPressed() {
	Intent i=new Intent( AssetsActivity.this,AuditScheduleList.class);
	startActivity(i);
	finish();
	}
	
	public void getMenuRights() {
		DataBaseHelper dbHelper = new DataBaseHelper( AssetsActivity.this);
		dbHelper.open();
		general_details = dbHelper.getSubMenuRight("SiteGeneralDetails","Assets");
		passive_asset = dbHelper.getSubMenuRight("PassiveAssets", "Assets");
		active_asset = dbHelper.getSubMenuRight("ActiveAsset", "Assets");
		dbHelper.close();
		SiteGeneralDetails = general_details.split("~");
		PassiveAssets = passive_asset.split("~");

		if (SiteGeneralDetails.length == 1) {
			if (general_details.equalsIgnoreCase("V")) {
				tv_site_general_details.setVisibility(View.VISIBLE);
			} else {
				tv_site_general_details.setVisibility(View.GONE);
			}
		} else {
			if (SiteGeneralDetails[0].equalsIgnoreCase("M")) {
				if (SiteGeneralDetails[1].equalsIgnoreCase("V")) {
					tv_site_general_details.setVisibility(View.VISIBLE);
				} else {
					tv_site_general_details.setVisibility(View.GONE);
				}
			} else {
				tv_site_general_details.setVisibility(View.GONE);
			}
		}

		if (active_asset.equalsIgnoreCase("V")) {
			tv_active_assets.setVisibility(View.VISIBLE);
		} else {
			tv_active_assets.setVisibility(View.GONE);
		}

		if (PassiveAssets.length == 1) {
			if (passive_asset.equalsIgnoreCase("V")) {
				tv_passive_assets.setVisibility(View.VISIBLE);
			} else {
				tv_passive_assets.setVisibility(View.GONE);
			}
		} else {
			if (PassiveAssets[0].equalsIgnoreCase("M")) {
				if (PassiveAssets[1].equalsIgnoreCase("V")) {
					tv_passive_assets.setVisibility(View.VISIBLE);
				} else {
					tv_passive_assets.setVisibility(View.GONE);
				}
			} else {
				tv_passive_assets.setVisibility(View.GONE);
			}
		}
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
		nameValuePairs.add(new BasicNameValuePair("countryId", mAppPreferences.getCounrtyID()));
		nameValuePairs.add(new BasicNameValuePair("hubId", mAppPreferences.getHubID()));
		nameValuePairs.add(new BasicNameValuePair("regionId", mAppPreferences.getRegionId()));
		nameValuePairs.add(new BasicNameValuePair("circleId", mAppPreferences.getCircleID()));
		nameValuePairs.add(new BasicNameValuePair("zoneId", mAppPreferences.getZoneID()));
		nameValuePairs.add(new BasicNameValuePair("clusterId", mAppPreferences.getClusterID()));
		String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_getlocation, nameValuePairs);
		Gson gson = new Gson();
		try {
		dataResponse = gson.fromJson(response, LocationList.class);
		} catch (Exception e) {
		e.printStackTrace();
		dataResponse = null;
		}
		return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		if (pd.isShowing()) {
		pd.dismiss();
		}
		if ((dataResponse.getLocationList().size() == 0)) {
		Toast.makeText( AssetsActivity.this,"Location Data not Provided by Server",Toast.LENGTH_LONG).show();
		}else if (dataResponse.getLocationList().size() > 0){
			DataBaseHelper dbHelper = new DataBaseHelper( AssetsActivity.this);
			dbHelper.open();
			dbHelper.clearLocationData();
			dbHelper.insertLocationData(dataResponse.getLocationList());
			dbHelper.dataTS(null, null,"18",dbHelper.getLoginTimeStmp("18","0"),2,"0");
			dbHelper.close();
			Intent intent = new Intent( AssetsActivity.this,SiteGeneralDetails.class);
			intent.putExtra("siteGenDetails",general_details);
			startActivity(intent);	
			finish();
		}else{
		Toast.makeText( AssetsActivity.this, "Server Not Available",Toast.LENGTH_LONG).show();
		}
		super.onPostExecute(result);
		}
	   }
	
	public String locationDataType(){
		 DataBaseHelper dbHelper = new DataBaseHelper( AssetsActivity.this);
		 dbHelper.open();
	     String DataType_Str="1";
	 	 String i=Utils.CompareDates(dbHelper.getSaveTimeStmp("18","0"),dbHelper.getLoginTimeStmp("18","0"),"18");
	     if(i!="1"){
			 DataType_Str=i;
		 }if(DataType_Str=="1"){
			 DataType_Str="";
	     }
		 return DataType_Str;
	  }
    }
