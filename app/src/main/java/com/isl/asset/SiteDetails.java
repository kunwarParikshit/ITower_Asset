package com.isl.asset;
import infozech.itower.R;

import com.isl.constant.WebMethods;
import com.isl.modal.BeanAssetModuleList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.itower.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SiteDetails extends Activity {
	AppPreferences mAppPreferences;
	BeanAssetModuleList data_list;
	ListView listView;
	RelativeLayout ll_blank;
	ImageButton bt_search;
	EditText et_search;
	String searchData,AuditMode;
	String site_details,general_details,passive_asset,active_asset; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_details);
		listView=(ListView)findViewById(R.id.listView1);
		ll_blank = (RelativeLayout)findViewById(R.id.rl_blank);
		TextView tv_result=(TextView)findViewById(R.id.tv_result);
		Button iv_back = (Button) findViewById(R.id.button_back);
		et_search=(EditText)findViewById(R.id.et_search);
		bt_search=(ImageButton)findViewById(R.id.bt_search);
		mAppPreferences = new AppPreferences(SiteDetails.this);
		AuditMode=mAppPreferences.getAuditMode();
		getMenuRights();
		if(AuditMode.equalsIgnoreCase("no")){
			tv_result.setVisibility(View.GONE);      
			}else if(AuditMode.equalsIgnoreCase("yes")){
			tv_result.setVisibility(View.VISIBLE);   
			tv_result.setPaintFlags(tv_result.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		}
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(SiteDetails.this,HomeActivity.class);
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();	
		}
		});
		bt_search.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		searchData=et_search.getText().toString();
		if (Utils.isNetworkAvailable(SiteDetails.this)) {
			new AssignedTicketsTask(SiteDetails.this).execute();
		} else {
			Toast.makeText(SiteDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		}
		});

	}
	@Override
	public void onBackPressed() {
		Intent i=new Intent(SiteDetails.this,HomeActivity.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();	
	}
	
	public void setAdapter(ListView list, BeanAssetModuleList data) {
		AdapterSiteDetails adapter = new AdapterSiteDetails(SiteDetails.this,data,general_details,passive_asset,active_asset);
		list.setDivider(null);
		list.setAdapter(adapter);
	}
	
	public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
		Context con;
		ProgressDialog pd; 
		public AssignedTicketsTask(Context con) {
			this.con = con;
			pd = ProgressDialog.show(con, null, "Loading...");
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("CounrtyID",mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("HubID",mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("RegionID",mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("CircleID",mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("ZoneID",mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("ClusterID",mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("SiteSearch",searchData));			
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetSiteDetails, nameValuePairs);
				Gson gson = new Gson();
				data_list = gson.fromJson(response,BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list = null;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!SiteDetails.this.isFinishing()) {
				if ((data_list == null)) {
					Toast.makeText(SiteDetails.this, "Server Not Available",Toast.LENGTH_LONG).show();
				} else if (data_list.getSite_Details_list().size() > 0) {
					listView.setVisibility(View.VISIBLE);
					ll_blank.setVisibility(View.GONE);
					setAdapter(listView, data_list);
				} else {
					Toast.makeText(SiteDetails.this, "No Data Found",Toast.LENGTH_LONG).show();
					listView.setVisibility(View.GONE);
					ll_blank.setVisibility(View.VISIBLE);
				}
			}
			super.onPostExecute(result);
		}
	}
	
	public void getMenuRights() {
		DataBaseHelper dbHelper = new DataBaseHelper(SiteDetails.this);
		dbHelper.open();
		site_details = dbHelper.getSubMenuRight("SiteDetails","Assets");
		general_details = dbHelper.getSubMenuRight("SiteGeneralDetails","Assets");
		passive_asset = dbHelper.getSubMenuRight("PassiveAssets","Assets");
		active_asset = dbHelper.getSubMenuRight("ActiveAsset","Assets");
		dbHelper.close();
		if(site_details.equalsIgnoreCase("V")){
			if (Utils.isNetworkAvailable(SiteDetails.this)) {
				new AssignedTicketsTask(SiteDetails.this).execute();
			} else {
				Toast.makeText(SiteDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
			}
			}
			else{	
			Toast.makeText(SiteDetails.this, "You are not authorised for Site Details. ",Toast.LENGTH_LONG).show();
			Intent i=new Intent(SiteDetails.this,HomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		}	
	 }
}

