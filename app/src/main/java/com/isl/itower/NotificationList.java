package com.isl.itower;
import com.isl.incident.TicketDetailsTabs;
import com.isl.modal.BeanAddNotification;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.modal.TTChecklist;
import com.isl.notification.ShortcutBadger;
import infozech.itower.R;

import com.isl.preventive.PMTabs;
import com.isl.sparepart.schedule.Schedule;
import com.isl.energy.withdrawal.FuelPurchaseGridRPT;

import java.util.ArrayList;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

public class NotificationList extends Activity {
	ArrayList<String> notification_list,notification_flag,delete_list;
	ListView notification;
	RelativeLayout rl_no_list;
	AppPreferences mAppPreferences;
	TTChecklist ttchecklist =null;
	TextView tv_brand_logo,txt_no_ticket;
	BeanAddNotification data_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_list);
		mAppPreferences = new AppPreferences(this);
		Utils.deleteNotification(this);
		tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
		txt_no_ticket=(TextView)findViewById(R.id.txt_no_ticket);
		Utils.msgText(NotificationList.this,"48",txt_no_ticket);
		Utils.msgText(NotificationList.this,"42",tv_brand_logo); //Notification List
		Button back=(Button)findViewById(R.id.button_back);
		Utils.msgButton(NotificationList.this,"71",back);
		back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		Intent i = new Intent(NotificationList.this, HomeActivity.class);
		startActivity(i);
		finish();
		}
		});
		
		}
	  @Override
	  protected void onResume() {
	    super.onResume();
	    mAppPreferences.setNotificationListFlag(0);
        notification_list = new ArrayList<String>();
        notification_flag = new ArrayList<String>();
		final DataBaseHelper dbHelper = new DataBaseHelper(NotificationList.this);
		dbHelper.open();
		notification_list = dbHelper.getAllNotification(mAppPreferences.getUserId());
		notification_flag = dbHelper.getNotificationFlag(mAppPreferences.getUserId());
		rl_no_list= (RelativeLayout) findViewById(R.id.rl_textlayout);
		notification=(ListView)findViewById(R.id.lv_notification);
		if(mAppPreferences.getToastFlag().equalsIgnoreCase("Yes")){
		Toast.makeText(this,""+mAppPreferences.getListToast(), Toast.LENGTH_LONG).show();
		}
		mAppPreferences.setToastFlag("No");
		if(notification_list!=null && notification_list.size()>0){
			notification.setAdapter(new AdapterNotificationList(NotificationList.this,notification_list,notification_flag));
			notification.setVisibility(View.VISIBLE);
			rl_no_list.setVisibility(View.GONE);
		}else{
			rl_no_list.setVisibility(View.VISIBLE);
			notification.setVisibility(View.GONE);
		}
		notification.setOnItemClickListener(new OnItemClickListener() {
		int a=0;	
	    @Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
	    Gson gson = new Gson();
	    BeanAddNotification data_list = gson.fromJson(notification_list.get(position), BeanAddNotification.class);
	    if(data_list.getNotification_type().equalsIgnoreCase("1") || 
	       data_list.getNotification_type().equalsIgnoreCase("2") ||
	       data_list.getNotification_type().equalsIgnoreCase("3")){
	    a=1;
	    Intent i = new Intent(NotificationList.this, TicketDetailsTabs.class);
		if(data_list.getTkt_mode()!=null && data_list.getTkt_mode().equalsIgnoreCase("3")){
				mAppPreferences.setTTModuleSelection("955");
		}else{
				mAppPreferences.setTTModuleSelection("654");
		}
	    mAppPreferences.SetBackModeNotifi123(2);
	    i.putExtra("id",data_list.getTkt_id()); 
	    startActivity(i);
		}else if(data_list.getNotification_type().equalsIgnoreCase("4")){
		a=1;	
		Intent i = new Intent(NotificationList.this, PMTabs.class);
		mAppPreferences.SetBackModeNotifi45(2);
		startActivity(i);	
		}else if(data_list.getNotification_type().equalsIgnoreCase("5")){
		a=1;	
		Intent i = new Intent(NotificationList.this, PMTabs.class);
		mAppPreferences.SetBackModeNotifi45(2);
		mAppPreferences.setPMTabs("N"); //default open Miss tab
		startActivity(i);	//useCase 1.8.1	
		}else if(data_list.getNotification_type().equalsIgnoreCase("6")
				|| data_list.getNotification_type().equalsIgnoreCase("13")){
		a=1;
		Intent i = new Intent(NotificationList.this, Schedule.class);
		mAppPreferences.SetBackModeNotifi6(2);
		startActivity(i);
		}else if(data_list.getNotification_type().equalsIgnoreCase("7")){
		a=0;
		Intent i = new Intent(NotificationList.this, NotificationDetails.class);
		mAppPreferences.SetBackModeNotifi7(1);
		i.putExtra("genMSG",data_list.getGenMessage());
		i.putExtra("type",data_list.getNotification());
		i.putExtra("details", notification_list.get(position));
		startActivity(i);
		finish();
		}else if(data_list.getNotification_type().equalsIgnoreCase("12")){
			a=1;
			Intent i = new Intent(NotificationList.this, PMTabs.class);
			mAppPreferences.SetBackModeNotifi45(2);
			startActivity(i);
		}else if(data_list.getNotification_type().equalsIgnoreCase("8")){
			a=0;
			GPSTracker gps = new GPSTracker(NotificationList.this);
			if(gps.canGetLocation()==false){
				gps.showSettingsAlert();		
			}else {
				if ((String.valueOf(gps.getLatitude()) == null || String.valueOf(gps.getLatitude()).equalsIgnoreCase("0.0") || String.valueOf(gps.getLatitude()).isEmpty())
					|| (String.valueOf(gps.getLongitude()) == null || String.valueOf(gps.getLongitude()).equalsIgnoreCase("0.0") || String.valueOf(gps.getLongitude()).isEmpty())) {
					// Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
					Utils.toast(NotificationList.this, "252");
				} else{
					Intent i=new Intent(Intent.ACTION_VIEW);
					dbHelper.updateReadNotificationFlag(mAppPreferences.getUserId(),notification_list.get(position));
					String saddr=String.valueOf(gps.getLatitude())+","+String.valueOf(gps.getLongitude());
					String daddr=data_list.getLattitude()+","+data_list.getLongitude();		
		        	i.setData(Uri.parse("https://maps.google.com/maps?saddr="+saddr+"&daddr="+daddr));
		        	i.setPackage("com.google.android.apps.maps");
		        	startActivity(i);
				}			
			}		
		}else if(data_list.getNotification_type().equalsIgnoreCase("11")){
			a=0;
			Intent i = new Intent(NotificationList.this, FuelPurchaseGridRPT.class);
			dbHelper.updateReadNotificationFlag(mAppPreferences.getUserId(),notification_list.get(position));
			dbHelper.close();
			startActivity(i);
		}
	    if(a==1){
	    dbHelper.deleteNotification(notification_list.get(position),mAppPreferences.getUserId());
		dbHelper.close();
		NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancelAll();
	    }
		}
		});
	   }
	  @Override
	  protected void onPause() {
		 super.onPause();
		 mAppPreferences.setNotificationListFlag(1);
		 ArrayList<String> notification_counter = new ArrayList<String>();
		 final DataBaseHelper dbHelper = new DataBaseHelper(NotificationList.this);
		 dbHelper.open();
		 notification_counter = dbHelper.getNotificationCount(mAppPreferences.getUserId(),"0");
		 ShortcutBadger.removeCount(NotificationList.this);
		 if(notification_counter.size()>0){
		 ShortcutBadger.applyCount(NotificationList.this,notification_counter.size());
		 }
	  }
	 @Override
		public void onBackPressed() {
		 Intent i = new Intent(NotificationList.this, HomeActivity.class);
		 startActivity(i);
		 finish();	
		}
     }
