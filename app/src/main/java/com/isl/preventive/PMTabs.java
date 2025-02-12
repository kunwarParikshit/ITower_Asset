package com.isl.preventive;

import com.isl.constant.WebMethods;
import com.isl.itower.NotificationList;
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.modal.IncidentMetaList;
import com.isl.notification.ShortcutBadger;
import com.isl.itower.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.google.gson.Gson;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class PMTabs extends AppCompatActivity{
	private TabLayout tabLayout;
	private FragmentTabHost mTabHost;
	private ViewPager viewPager;
	AppPreferences mAppPreferences;
	DataBaseHelper db;
	String moduleUrl = "";
	String url = "";
	String schedule_tab_rights = "V",done_tab_rights = "V",missed_tab_rights = "V",pending_tab_rights = "V",
			review_tab_right = "V",reject_tab_right = "V",resubmit_tab_right = "V",add_pm_scheduling = "V";
	int fixedTab = 0;
	Button bt_back;
	TextView tv_brand_logo,tv_add_activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(PMTabs.this);
		mAppPreferences.setPMScheduleCountType("No action");
		mAppPreferences.setPMAssetBackTask(0);
		db = new DataBaseHelper(this);
		db.open();
		moduleUrl = db.getModuleIP("Preventive");
		PM_RIGHTS();

		if (Utils.isNetworkAvailable(PMTabs.this)) {
			if (!metaDataType().equalsIgnoreCase("")) {
				new IncidentMetaDataTask(PMTabs.this).execute();
			}
		}

		if (Utils.isNetworkAvailable( PMTabs.this )) {
			if (mAppPreferences.getTicketFrmNtBr().equalsIgnoreCase( "2" )) {
			} else {
				db.deleteNotification( mAppPreferences.getTicketFrmNtBr(),
						mAppPreferences.getUserId() );
			}
		} else {
			Utils.toast( PMTabs.this, "17" );
		}

		if(fixedTab>4) {
			setContentView( R.layout.scrool_tabs );
			Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
			tv_brand_logo = (TextView) toolbar.findViewById( R.id.tv_brand_logo );
			bt_back = (Button) toolbar.findViewById( R.id.bt_back );
			tv_add_activity = (TextView) toolbar.findViewById(R.id.tv_add_activity);
			setSupportActionBar( toolbar );
			getSupportActionBar().setDisplayShowTitleEnabled( false );
			viewPager = (ViewPager) findViewById( R.id.viewpager );
			Add_Fragement_Scrool( viewPager );
			viewPager.setCurrentItem(mAppPreferences.getPMBackTask());
			viewPager.setOffscreenPageLimit(1);
			tabLayout = (TabLayout) findViewById( R.id.tabs );
			tabLayout.setTabMode( MODE_SCROLLABLE );
			tabLayout.setupWithViewPager( viewPager );
			tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
				@Override
				public void onTabSelected(TabLayout.Tab tab) {
					//String a = tab.getTag().toString();
					viewPager.setCurrentItem( tab.getPosition() );
					mAppPreferences.setPMBackTask( tab.getPosition() );
				}
				@Override
				public void onTabUnselected(TabLayout.Tab tab) {
				}

				@Override
				public void onTabReselected(TabLayout.Tab tab) {

				}
			});
		}else{
			setContentView(R.layout.tab );
			tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo);
			bt_back = (Button)findViewById( R.id.bt_back);
			tv_add_activity = (TextView) findViewById(R.id.tv_add_activity);
			mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
			mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
			Add_Fragement_Fixed();
			mTabHost.setCurrentTab(mAppPreferences.getPMBackTask());
			mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());

			mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
				@Override
				public void onTabChanged(String tabId) {
					mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());
				}});
		}

		if(add_pm_scheduling.contains("A")) {
			tv_add_activity.setVisibility(View.VISIBLE);
		}else{
			tv_add_activity.setVisibility(View.GONE);
		}

		tv_add_activity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getApplicationContext().getPackageName()
						.equalsIgnoreCase("tawal.com.sa")){
					Intent myIntent = new Intent(PMTabs.this, ScheduleSiteActivity.class);
					startActivity(myIntent);
					finish();
				}else {
					Intent myIntent = new Intent(PMTabs.this, AddPMScheduling.class);
					startActivity(myIntent);
					finish();
				}
			}
		});

		Utils.msgText( PMTabs.this, "224", tv_brand_logo );
		bt_back.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mAppPreferences.getBackModeNotifi45() == 2) {
					Intent i = new Intent( PMTabs.this, NotificationList.class );
					i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK );
					startActivity( i );
					finish();
				} else {
					Intent i = new Intent( PMTabs.this, HomeActivity.class );
					startActivity( i );
					finish();
				}
			}
		} );

	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFrag(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ArrayList<String> notification_counter = new ArrayList<String>();
		notification_counter = db.getNotificationCount(
				mAppPreferences.getUserId(), "0");
		ShortcutBadger.removeCount(PMTabs.this);
		if (notification_counter.size() > 0) {
			ShortcutBadger.applyCount(PMTabs.this, notification_counter.size());
		}
	}

	@Override
	public void onBackPressed() {
		if (mAppPreferences.getBackModeNotifi45() == 2) {
			Intent i = new Intent(PMTabs.this, NotificationList.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		} else {
			Intent i = new Intent(PMTabs.this, HomeActivity.class);
			startActivity(i);
			finish();
		}
	}

	IncidentMetaList resposnse_Incident_meta = null;
	public class IncidentMetaDataTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;

		public IncidentMetaDataTask(Context con) {
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
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
				Gson gson = new Gson();
				nameValuePairs.add(new BasicNameValuePair("module", "Incident"));
				nameValuePairs.add(new BasicNameValuePair("datatype",	metaDataType()));
				nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
				nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
				}else{
					url=moduleUrl+ WebMethods.url_GetMetadata;
				}
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				resposnse_Incident_meta = gson.fromJson(res,
						IncidentMetaList.class);
			} catch (Exception e) {
				e.printStackTrace();
				resposnse_Incident_meta = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if ((resposnse_Incident_meta == null)) {
				// Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
				Utils.toast(PMTabs.this, "70");
			} else if (resposnse_Incident_meta != null) {
				if (resposnse_Incident_meta.getParam()!=null && resposnse_Incident_meta.getParam().size() > 0) {
					db.clearInciParamData("654");
					db.insertInciParamcData(resposnse_Incident_meta.getParam(),"654");
					db.dataTS(null, null, "10", db.getLoginTimeStmp("10","0"),2,"0");
				}

				if (resposnse_Incident_meta.getOpertors()!=null && resposnse_Incident_meta.getOpertors().size() > 0) {
					db.clearInciOpcoData("654");
					db.insertInciOpcoData(resposnse_Incident_meta.getOpertors(),"654");
					db.dataTS(null, null, "11",db.getLoginTimeStmp("11","654"),2,"654");
				}

				if (resposnse_Incident_meta.getEquipment() !=null && resposnse_Incident_meta.getEquipment().size() > 0) {
					db.clearInciEqpData("654");
					db.insertInciEqpcData(resposnse_Incident_meta.getEquipment(),"654");
					db.dataTS(null, null, "14",db.getLoginTimeStmp("14","654"),2,"654");
				}

				if (resposnse_Incident_meta.getUser() !=null && resposnse_Incident_meta.getUser().size() > 0) {
					db.clearUserContact("654");
					db.insertUserContact(resposnse_Incident_meta.getUser(),"654");
					db.dataTS(null, null, "22",db.getLoginTimeStmp("22","654"), 2,"654");
				}

				if (resposnse_Incident_meta.getGroups() !=null && resposnse_Incident_meta.getGroups().size() > 0) {
					db.clearIncigrpData("654");
					db.insertInciGrpData(resposnse_Incident_meta.getGroups(),"654");
					db.dataTS(null, null, "15",db.getLoginTimeStmp("15","0"), 2,"0");
				}


				if(resposnse_Incident_meta.getDgtype()!=null && resposnse_Incident_meta.getDgtype().size()>0){
					db.clearEnergyDg();
					db.insertEnergyDG(resposnse_Incident_meta.getDgtype());
					db.dataTS(null, null,"17",db.getLoginTimeStmp("17","0"),2,"0");
				}



			} else {
				// Toast.makeText(IncidentManagement.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				Utils.toast(PMTabs.this, "13");
			}
		}
	}

	public String metaDataType() {
		String DataType_Str = "1";
		String j = Utils.CompareDates(db.getSaveTimeStmp("10","0"),
				db.getLoginTimeStmp("10","0"), "10");

		// For froup
		String k = Utils.CompareDates(db.getSaveTimeStmp("15","0"),
				db.getLoginTimeStmp("15","0"), "15");

		String l=Utils.CompareDates(db.getSaveTimeStmp("17","0"),
				db.getLoginTimeStmp("17","0"),"17");

		String m=Utils.CompareDates(db.getSaveTimeStmp("11","0"),
				db.getLoginTimeStmp("11","0"),"11");

		String n=Utils.CompareDates(db.getSaveTimeStmp("14","0"),
				db.getLoginTimeStmp("14","0"),"14");

		String o=Utils.CompareDates(db.getSaveTimeStmp("22","0"),
				db.getLoginTimeStmp("22","0"),"22");

		if (j != "1") {
			DataType_Str = j;
		}

		if (k != "1") {
			if (DataType_Str == "1") {
				DataType_Str = k;
			} else {
				DataType_Str = DataType_Str + "," + k;
			}
		}

		if(l!="1"){
			if(DataType_Str =="1"){
				DataType_Str=l;
			}else{
				DataType_Str=DataType_Str+","+l;
			}
		}

		if(m!="1"){
			if(DataType_Str =="1"){
				DataType_Str=m;
			}else{
				DataType_Str=DataType_Str+","+m;
			}
		}

		if(n!="1"){
			if(DataType_Str =="1"){
				DataType_Str=n;
			}else{
				DataType_Str=DataType_Str+","+n;
			}
		}

		if(o!="1"){
			if(DataType_Str =="1"){
				DataType_Str=o;
			}else{
				DataType_Str=DataType_Str+","+o;
			}
		}

		if (DataType_Str == "1") {
			DataType_Str = "";
		}
		return DataType_Str;
	}

	public void PM_RIGHTS(){
		schedule_tab_rights = db.getSubMenuRight("Scheduled", "Preventive");
		done_tab_rights = db.getSubMenuRight("Done", "Preventive");
		missed_tab_rights = db.getSubMenuRight("Missed", "Preventive");
		pending_tab_rights = db.getSubMenuRight("Pending", "Preventive");
		review_tab_right = db.getSubMenuRight("Reviewed", "Preventive");
		reject_tab_right = db.getSubMenuRight("Rejected", "Preventive");
		resubmit_tab_right = db.getSubMenuRight("ReSubmit", "Preventive");
		add_pm_scheduling = db.getSubMenuRight("AddPMScheduling", "Preventive");

		if(schedule_tab_rights.contains("V") || schedule_tab_rights.contains("A")){
			fixedTab=fixedTab+1;
		}
		if(done_tab_rights.contains("V") || done_tab_rights.contains("A")){
			fixedTab=fixedTab+1;
		}

		if(missed_tab_rights.contains("V") || missed_tab_rights.contains("A")){
			fixedTab=fixedTab+1;
		}

		if(pending_tab_rights.contains("V") || pending_tab_rights.contains("A")){
			fixedTab=fixedTab+1;
		}

		if(review_tab_right.contains("V") || review_tab_right.contains("A")){
			fixedTab=fixedTab+1;
		}

		if(reject_tab_right.contains("V") || reject_tab_right.contains("A")){
			fixedTab=fixedTab+1;
		}

		if(resubmit_tab_right.contains("V") || resubmit_tab_right.contains("A")){
			fixedTab=fixedTab+1;
		}
	}

	public void Add_Fragement_Scrool(ViewPager viewPager){
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

		if(schedule_tab_rights.contains("V") || schedule_tab_rights.contains("A")){
			adapter.addFrag(new ScheduleFragement(), Utils.msg(PMTabs.this,"219"));

		}
		if(done_tab_rights.contains("V") || done_tab_rights.contains("A")){
			adapter.addFrag(new DoneFragement(), Utils.msg(PMTabs.this,"223"));
		}

		if(missed_tab_rights.contains("V") || missed_tab_rights.contains("A")){
			adapter.addFrag(new MissedFragement(), Utils.msg(PMTabs.this,"222"));
		}

		if(pending_tab_rights.contains("V") || pending_tab_rights.contains("A")){
			adapter.addFrag(new PendingFragement(), Utils.msg(PMTabs.this,"220"));
		}

		if(review_tab_right.contains("V") || review_tab_right.contains("A")){
			adapter.addFrag(new VerifyFragement(), Utils.msg(PMTabs.this,"221"));
		}

		if(reject_tab_right.contains("V") || reject_tab_right.contains("A")){
			adapter.addFrag(new RejectFragement(), Utils.msg(PMTabs.this,"467"));
		}

		if(resubmit_tab_right.contains("V") || resubmit_tab_right.contains("A")){
			adapter.addFrag(new ReSubmitFragment(), Utils.msg(PMTabs.this,"515"));
		}

		viewPager.setAdapter(adapter);
	}



	private View prepareTabView(String id) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);
		RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
		if(getApplicationContext().getPackageName().equalsIgnoreCase("infozech.tawal")
				|| getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")){
			relativelayout.setBackgroundResource(R.drawable.tab_bg_selector_tawal);
		}else{
			relativelayout.setBackgroundResource(R.drawable.tab_bg_selector);
		}
		TextView tv = (TextView) view.findViewById(R.id.TabTextView);
		tv.setTypeface(Utils.typeFace(PMTabs.this));
		String msg = Utils.msg(PMTabs.this,id);
		tv.setText(msg.toUpperCase());
		//tv.setText("Schedul/Resub");
		//Utils.msgText(PMTabs.this,id,tv);
		return view;
	}

	public void Add_Fragement_Fixed(){
		if(schedule_tab_rights.contains("V") || schedule_tab_rights.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Schedule" )
					.setIndicator( prepareTabView( "219" ) ), ScheduleFragement.class, null );
		}

		if(done_tab_rights.contains("V") || done_tab_rights.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Done" )
					.setIndicator( prepareTabView( "223" ) ), DoneFragement.class, null );
		}

		if(missed_tab_rights.contains("V") || missed_tab_rights.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Missed" )
					.setIndicator( prepareTabView( "222" ) ), MissedFragement.class, null );
		}

		if(pending_tab_rights.contains("V") || pending_tab_rights.contains("A")){
			//if(missed_tab_rights.contains("V") || missed_tab_rights.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Pending" )
					.setIndicator( prepareTabView( "220" ) ), PendingFragement.class, null );
		}

		if(review_tab_right.contains("V") || review_tab_right.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Verify" )
					.setIndicator( prepareTabView( "221" ) ), VerifyFragement.class, null );
		}

		if(reject_tab_right.contains("V") || reject_tab_right.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "Reject" )
					.setIndicator( prepareTabView( "467" ) ), RejectFragement.class, null );
		}

		if(resubmit_tab_right.contains("V") || resubmit_tab_right.contains("A")){
			mTabHost.addTab( mTabHost.newTabSpec( "ReSubmit" )
					.setIndicator( prepareTabView( "515" ) ), ReSubmitFragment.class, null );
		}

	}

}
