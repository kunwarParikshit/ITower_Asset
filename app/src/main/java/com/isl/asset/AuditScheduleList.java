package com.isl.asset;

import infozech.itower.R;

import com.isl.constant.WebMethods;
import com.isl.modal.BeanSiteList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;
import com.isl.modal.AssetMetaList;
import com.isl.sparepart.schedule.AdapterSchedule;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

public class AuditScheduleList extends Activity {
	ListView schedule_list;
	AppPreferences mAppPreferences;
	RelativeLayout RL;
	BeanSiteList response_ticket_list = null;
	AssetMetaList resposnse_asset_meta = null; 
	int a=0;
	private NetworkManager networkManager;//108
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audit_schedule_tab_list);
		networkManager = new NetworkManager();//108
		mAppPreferences = new AppPreferences(AuditScheduleList.this);
		Button iv_back = (Button) findViewById(R.id.button_back);
		RL = (RelativeLayout) findViewById(R.id.rl_textlayout);
		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i = new Intent(AuditScheduleList.this, SiteDetails.class);
		startActivity(i);
		finish();
		}
		});
		schedule_list = (ListView) findViewById(R.id.lv_schedule);
		if (Utils.isNetworkAvailable(AuditScheduleList.this)) {
			//108
			networkManager.getToken(new NetworkManager.TokenCallback() {
				@Override
				public void onTokenReceived(String token) {
					new ScheduleTask(AuditScheduleList.this,token).execute();
				}

				@Override
				public void onTokenError(String error) {
					Toast.makeText(AuditScheduleList.this, error, Toast.LENGTH_SHORT).show();
				}
			});
			//108
		//new ScheduleTask(AuditScheduleList.this).execute();
		} else {
		Toast.makeText(AuditScheduleList.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		schedule_list.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			a=arg2;
		if (Utils.isNetworkAvailable(AuditScheduleList.this)) {
			  if(!dataType().equalsIgnoreCase("")){
				  new AssetMetaDataTask(AuditScheduleList.this).execute();
				}else{
					mAppPreferences.setTxnId(response_ticket_list.getSite_list().get(arg2).getTXN_ID());
					Intent i = new Intent(AuditScheduleList.this, AssetsActivity.class);
					startActivity(i);
				}
			}else {
				mAppPreferences.setTxnId(response_ticket_list.getSite_list().get(arg2).getTXN_ID());
				Intent i = new Intent(AuditScheduleList.this, AssetsActivity.class);
				startActivity(i);
			}
		}
		});
	    }

	    public class ScheduleTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String token; //108
		public ScheduleTask(Context con,String token) {
			this.con = con;
			this.token = token;//108
		}
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
		try {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
		nameValuePairs.add(new BasicNameValuePair("type",""));
		nameValuePairs.add(new BasicNameValuePair("siteID",mAppPreferences.getSiteID()));
		nameValuePairs.add(new BasicNameValuePair("activityTypeFlag","2"));
		String response = Utils.httpPostRequest1(con,mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites,nameValuePairs,token);//108
		Gson gson = new Gson();
		response_ticket_list = gson.fromJson(response,BeanSiteList.class);
		}catch (Exception e) {
		e.printStackTrace();
		response_ticket_list = null;
		}
		return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!isFinishing()) {
				if ((response_ticket_list == null)) {
					Toast.makeText(AuditScheduleList.this,"Server Not Available", Toast.LENGTH_LONG).show();
					schedule_list.setVisibility(View.GONE);
					RL.setVisibility(View.GONE);
					Intent i = new Intent(AuditScheduleList.this, SiteDetails.class);
					startActivity(i);
					finish();
				} else if (response_ticket_list.getSite_list().size() > 0) {
					schedule_list.setAdapter(new AdapterSchedule(AuditScheduleList.this, response_ticket_list,"S"));
					schedule_list.setVisibility(View.VISIBLE);
					RL.setVisibility(View.GONE);
				} else {
					Toast.makeText(AuditScheduleList.this, "No Data Found",Toast.LENGTH_LONG).show();
					schedule_list.setVisibility(View.GONE);
					RL.setVisibility(View.VISIBLE);
					Intent i = new Intent(AuditScheduleList.this, SiteDetails.class);
					startActivity(i);
					finish();
				}
			}
		}
	    }
	    
	    public class AssetMetaDataTask extends AsyncTask<Void, Void, Void> {
			ProgressDialog pd;
			Context con;

			public AssetMetaDataTask(Context con) {
				this.con = con;
			}
			@Override
			protected void onPreExecute() {
				pd = ProgressDialog.show(con, null, "Loading...");
				super.onPreExecute();
			}
			@Override
			protected Void doInBackground(Void... params) {
				try{
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
					Gson gson = new Gson();
					nameValuePairs.add(new BasicNameValuePair("module", "Assets"));
					nameValuePairs.add(new BasicNameValuePair("datatype",dataType()));
					nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
					nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
					nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
					String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata, nameValuePairs);
					resposnse_asset_meta = gson.fromJson(res,AssetMetaList.class);
				  } catch (Exception e) {
					e.printStackTrace();
					resposnse_asset_meta = null;
				 }
				return null;
			}
		@Override
			protected void onPostExecute(Void result) {
			     if (pd.isShowing()) {
				    pd.dismiss();
			     }
				DataBaseHelper dbHelper = new DataBaseHelper(AuditScheduleList.this);
				dbHelper.open();
				if ((response_ticket_list == null)) {
				Toast.makeText(AuditScheduleList.this,"Server Not Available", Toast.LENGTH_LONG).show();
				}else if (resposnse_asset_meta!=null){
					if(resposnse_asset_meta.getParam().size()>0){
					dbHelper.clearAssetParamData();
					dbHelper.insertAssetParamData(resposnse_asset_meta.getParam());
					dbHelper.dataTS(null, null,"10",dbHelper.getLoginTimeStmp("10","0"),4,"0");
					}
					if(resposnse_asset_meta.getVendors().size()>0){
		            dbHelper.clearVender();	
		           	dbHelper.insertEnergyVender(resposnse_asset_meta.getVendors());
		           	dbHelper.dataTS(null, null,"16",dbHelper.getLoginTimeStmp("16","0"),2,"0");
					}
		            if(resposnse_asset_meta.getDgtype().size()>0){
		            dbHelper.clearEnergyDg();	
		            dbHelper.insertEnergyDG(resposnse_asset_meta.getDgtype());	
		            dbHelper.dataTS(null, null,"17",dbHelper.getLoginTimeStmp("17","0"),2,"0");
					}
		            dbHelper.close();
				}
				else{			
				}
				mAppPreferences.setTxnId(response_ticket_list.getSite_list().get(a).getTXN_ID());
				Intent i = new Intent(AuditScheduleList.this, AssetsActivity.class);
				startActivity(i);
				}
		}
	    @Override
	    public void onBackPressed() {
		Intent i = new Intent(AuditScheduleList.this, SiteDetails.class);
		startActivity(i);
		finish();
	    }
	    
	    public String dataType(){
	    	DataBaseHelper dbHelper = new DataBaseHelper(AuditScheduleList.this);
	    	dbHelper.open();
			//for Param	 
				 String DataType_Str="1";
			 	 String i=Utils.CompareDates(dbHelper.getSaveTimeStmpAsset("10"),dbHelper.getLoginTimeStmp("10","0"),"10");
			//for vender		 
			 	String k=Utils.CompareDates(dbHelper.getSaveTimeStmp("16","0"),dbHelper.getLoginTimeStmp("16","0"),"16");
			//For Equipment		 
			 	String l=Utils.CompareDates(dbHelper.getSaveTimeStmp("17","0"),dbHelper.getLoginTimeStmp("17","0"),"17");
			     if(i!="1"){
					 DataType_Str=i;
				 }
				 
				 if(k!="1"){
					 if(DataType_Str ==""){
						 DataType_Str=k;
					 }else{
						 DataType_Str=DataType_Str+","+k; 
					 }
				 }
				 
				 if(l!="1"){
					 if(DataType_Str =="1"){
					 DataType_Str=l;
				 }else{
					 DataType_Str=DataType_Str+","+l;
				    }
				 }
				 		 
				 if(DataType_Str=="1"){
					  DataType_Str="";
			     }
				 return DataType_Str;
			  }
  }