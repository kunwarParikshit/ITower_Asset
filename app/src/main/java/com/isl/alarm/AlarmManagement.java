package com.isl.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.HomeActivity;

import infozech.itower.R;

public class AlarmManagement extends Activity {
	TextView tv_dashboard, tv_my_search;
	AppPreferences mAppPreferences;
	String[] formCaption,rights;
	String[] tmpFrmNameRights=new String[2];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_alarm_management);
		tv_dashboard = (TextView) findViewById( R.id.tv_dashboard);
		tv_my_search = (TextView) findViewById( R.id.tv_alarm_search);
		Button iv_back = (Button) findViewById( R.id.iv_back);
		getMenuRights();

		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i=new Intent(AlarmManagement.this,HomeActivity.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);	
		finish();
		}
		});
  		
		tv_dashboard.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i = new Intent(AlarmManagement.this,DashboardActivity.class);
		startActivity(i);
		}
		});

		tv_my_search.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i = new Intent(AlarmManagement.this, AlarmSearch.class);
		startActivity(i);
		}
		});
	  }
	
	public void getMenuRights() {
		String dashboard,my_search; 
		DataBaseHelper dbHelper = new DataBaseHelper(AlarmManagement.this);
		dbHelper.open();
		dashboard = dbHelper.getSubMenuRight("DashBoard","Alarm");
		my_search = dbHelper.getSubMenuRight("MySearch","Alarm");
		dbHelper.close();
//		if((1==0)&&!dashboard.equalsIgnoreCase("V") && !my_search.equalsIgnoreCase("V")){
//			Toast.makeText(AlarmManagement.this, "You are not authorized for menus.",Toast.LENGTH_SHORT).show();
//			Intent j=new Intent(AlarmManagement.this,HomeActivity.class);
//			j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			startActivity(j);
//			finish();
//		}else{
//			if(dashboard.equalsIgnoreCase("V")){
//				tv_dashboard.setVisibility(View.VISIBLE);
//			}else{
//				tv_dashboard.setVisibility(View.GONE);
//			}
//            if(my_search.equalsIgnoreCase("V")){
//            	tv_my_search.setVisibility(View.VISIBLE);
//			}else{
//				tv_my_search.setVisibility(View.GONE);
//			}
//		 }
		tv_dashboard.setVisibility(View.VISIBLE);
		tv_my_search.setVisibility(View.VISIBLE);


//		if(dashboard.equalsIgnoreCase("V")){
//			tv_dashboard.setVisibility(View.VISIBLE);
//		}else{
//			tv_dashboard.setVisibility(View.GONE);
//		}
//		if(my_search.equalsIgnoreCase("V")){
//			tv_my_search.setVisibility(View.VISIBLE);
//		}else{
//			tv_my_search.setVisibility(View.GONE);
//		}
	 }
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(AlarmManagement.this,HomeActivity.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();	
	}
    }
