package com.isl.itower;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import infozech.itower.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
public class NotificationDetails extends Activity {
	TextView tv_brand_logo,tv_details;
	Button bt_back;
	AppPreferences mAppPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_details);
		mAppPreferences = new AppPreferences(this);
		DataBaseHelper dbHelper = new DataBaseHelper(NotificationDetails.this);
		dbHelper.open();
		tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
		tv_details=(TextView)findViewById(R.id.tv_details);
		bt_back= (Button)findViewById(R.id.bt_back);
		String genMSG = getIntent().getExtras().getString("genMSG");
		String type = getIntent().getExtras().getString("type");
		String details = getIntent().getExtras().getString("details");
		dbHelper.updateReadNotificationFlag(mAppPreferences.getUserId(),details);
		dbHelper.close();
		tv_details.setText(Html.fromHtml(""+genMSG));
		tv_brand_logo.setText(""+type);
		
		bt_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		if(mAppPreferences.getBackModeNotifi7()==1)	{
		Intent i = new Intent(NotificationDetails.this, NotificationList.class);
		startActivity(i);	
		}		
		finish();	
		}
		});
	  }
	 @Override
	 public void onBackPressed() {
	 if(mAppPreferences.getBackModeNotifi7()==1)	{
	 Intent i = new Intent(NotificationDetails.this, NotificationList.class);
	 startActivity(i);	
	 }		
	 finish();	
	}
}
