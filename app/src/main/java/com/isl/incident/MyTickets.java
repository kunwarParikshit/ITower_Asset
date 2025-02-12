package com.isl.incident;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import infozech.itower.R;
/**
 * Created by dhakan on 6/7/2019.
 */

public class MyTickets  extends FragmentActivity {
	private FragmentTabHost mTabHost;
	String assigned_tab,raised_tab,resolved_tab,closed_tab;
	AppPreferences mAppPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab );
		mAppPreferences = new AppPreferences(MyTickets.this);
		TextView tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			Utils.msgText(MyTickets.this, "527", tv_brand_logo);
		}else{
			Utils.msgText(MyTickets.this, "74", tv_brand_logo);
		}
		Button bt_back = (Button)findViewById( R.id.bt_back);
		bt_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent( MyTickets.this, IncidentManagement.class );
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity( i );
				finish();
			}
		});
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		//getMenuRights();
		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			AddFragmentHS();
		}else{
			AddFragmentTT();
		}

		mTabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tag) {
				//tabSelectionPosition = mTabHost.getCurrentTab();
				mAppPreferences = new AppPreferences(MyTickets.this);
				mAppPreferences.setTTtabSelection(tag);
				//Toast.makeText(MyTickets.this,""+tabId+"=="+a,Toast.LENGTH_LONG).show();
			}
		});

	}

	private View prepareTabView(String id) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);
		RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);

		if(getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")){
			relativelayout.setBackgroundResource(R.drawable.tab_bg_selector_tawal);
		}else{
			relativelayout.setBackgroundResource(R.drawable.tab_bg_selector);
		}
		TextView tv = (TextView) view.findViewById(R.id.TabTextView);
		tv.setTypeface(Utils.typeFace(MyTickets.this));
		//tv.setText(text);
		Utils.msgText( MyTickets.this,id,tv);
		return view;
	}

	/*public void getMenuRights() {
		DataBaseHelper dbHelper = new DataBaseHelper( MyTickets.this);
		dbHelper.open();
		assigned_tab = dbHelper.getSubMenuRight("AssignedTab", "Incident");
		raised_tab = dbHelper.getSubMenuRight("RaisedTab", "Incident");
		resolved_tab = dbHelper.getSubMenuRight("ResolvedTab", "Incident");
		dbHelper.close();
	}*/

	public void AddFragmentTT(){
		//mTabHost.clearAllTabs();
		DataBaseHelper dbHelper = new DataBaseHelper( MyTickets.this);
		dbHelper.open();
		assigned_tab = dbHelper.getSubMenuRight("AssignedTab", "Incident");
		raised_tab = dbHelper.getSubMenuRight("RaisedTab", "Incident");
		resolved_tab = dbHelper.getSubMenuRight("ResolvedTab", "Incident");
		closed_tab = dbHelper.getSubMenuRight("ClosedTab", "Incident");
		dbHelper.close();


		String tabname = mAppPreferences.getTTtabSelection();
		int pos = 0;
		if(tabname.equalsIgnoreCase( "Raised" )){
			pos = 1;
		}else if(tabname.equalsIgnoreCase( "Resolved" )){
			pos = 2;
		}else if(tabname.equalsIgnoreCase( "Closed" )){
			pos = 3;
		}else{
			pos = 0;
		}

		if(assigned_tab.contains("V") || assigned_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Assigned" )
					.setIndicator( prepareTabView( "118" ) ), FragAssignedTicket.class, null );
		}


		if(raised_tab.contains("V")||raised_tab.contains("M")){
			mTabHost.addTab(mTabHost.newTabSpec("Raised")
					.setIndicator(prepareTabView("119")),FragRaisedTicket.class, null);
		}

		if(resolved_tab.contains("V") || resolved_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Resolved" )
					.setIndicator( prepareTabView( "472" ) ), FragResolvedTicket.class, null );
		}

		if(closed_tab.contains("V") || closed_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Closed" )
					.setIndicator( prepareTabView( "578" ) ), FragClosedTicket.class, null );
		}

		mTabHost.setCurrentTab(pos);
	}

	public void AddFragmentHS(){
		//mTabHost.clearAllTabs();
		DataBaseHelper dbHelper = new DataBaseHelper( MyTickets.this);
		dbHelper.open();
		assigned_tab = dbHelper.getSubMenuRight("AssignedTab", "HealthSafty");
		raised_tab = dbHelper.getSubMenuRight("RaisedTab", "HealthSafty");
		resolved_tab = dbHelper.getSubMenuRight("ResolvedTab", "HealthSafty");
		closed_tab = dbHelper.getSubMenuRight("ClosedTab", "HealthSafty");
		dbHelper.close();

		String tabname = mAppPreferences.getTTtabSelection();
		int pos = 0;
		if(tabname.equalsIgnoreCase( "Raised" )){
			pos = 1;
		}else if(tabname.equalsIgnoreCase( "Resolved" )){
			pos = 2;
		}else if(tabname.equalsIgnoreCase( "Closed" )){
			pos = 3;
		}else{
			pos = 0;
		}

		if(assigned_tab.contains("V") || assigned_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Assigned" )
					.setIndicator( prepareTabView( "575" ) ), FragAssignedTicket.class, null );
		}

		if(raised_tab.contains("V")||raised_tab.contains("M")){
			mTabHost.addTab(mTabHost.newTabSpec("Raised")
					.setIndicator(prepareTabView("576")),FragRaisedTicket.class, null);
		}

		if(resolved_tab.contains("V") || resolved_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Resolved" )
					.setIndicator( prepareTabView( "577" ) ), FragResolvedTicket.class, null );
		}

		if(closed_tab.contains("V") || closed_tab.contains("M")) {
			mTabHost.addTab( mTabHost.newTabSpec( "Closed" )
					.setIndicator( prepareTabView( "578" ) ), FragClosedTicket.class, null );
		}
		mTabHost.setCurrentTab(pos);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent( MyTickets.this, IncidentManagement.class );
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity( i );
		finish();
	}
}

