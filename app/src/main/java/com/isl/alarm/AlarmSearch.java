package com.isl.alarm;

//for Current Status iMaintain CR#1.5.2.2 in Alarm Management

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanCurrentStatusList;
import com.isl.modal.BeansAlarmList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class AlarmSearch extends Activity {
	EditText et_edit_Text;
	ListView openalarm, currentStatus;
	String response_site_description;
	BeansAlarmList site_alarm_list = null;
	BeanCurrentStatusList current_status = null;
	RadioButton open, current; // 1.5.2.2
	AdapterAlarmStatus adapter1; // 1.5.2.2
	LinearLayout ll_open_alarm, ll_current_status; // 1.5.2.2
	int o = 0; // 1.5.2.2
	RelativeLayout RL;
	AppPreferences mAppPreferences;
	String response;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DataBaseHelper(this);
		db.open();
		moduleUrl = db.getModuleIP("Alarm");
		setContentView( R.layout.activity_alarm_search);
		mAppPreferences = new AppPreferences(AlarmSearch.this);
		et_edit_Text = (EditText) findViewById( R.id.et_site_id);
		openalarm = (ListView) findViewById( R.id.open_alarm);
		currentStatus = (ListView) findViewById( R.id.current_status);
		ImageButton bt_search = (ImageButton) findViewById( R.id.bt_search);
		Button button_back = (Button) findViewById( R.id.button_back);
		open = (RadioButton) findViewById( R.id.rb_alarm_open); // 1.5.2.2
		current = (RadioButton) findViewById( R.id.rb_alarm_current); // 1.5.2.2
		RL = (RelativeLayout) findViewById( R.id.rl_blank_alarm);
		ll_open_alarm = (LinearLayout) findViewById( R.id.ll_open_alarm); // 1.5.2.2
		ll_current_status = (LinearLayout) findViewById( R.id.ll_current_status); // 1.5.2.2

		button_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				o = 1;
				ll_open_alarm.setVisibility(View.GONE);
				ll_current_status.setVisibility(View.GONE);
				et_edit_Text.setText("");
			}
		});

		current.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				o = 2;
				ll_open_alarm.setVisibility(View.GONE);
				ll_current_status.setVisibility(View.GONE);
				et_edit_Text.setText("");
			}
		});

		bt_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (o == 1 || o == 0) {
					if (Utils.isNetworkAvailable(AlarmSearch.this)) {
						if (et_edit_Text.getText().toString().length() == 0) {
							Utils.toastMsg(AlarmSearch.this, "Enter Site Id");
							ll_open_alarm.setVisibility(View.GONE);
							ll_current_status.setVisibility(View.GONE);
							RL.setVisibility(View.GONE);
						} else {
							new AssignedTicketsTask(AlarmSearch.this).execute();
						}
					} else {
					Utils.toast(AlarmSearch.this, "17"); // No internet connection
					}
				} else {
					if (Utils.isNetworkAvailable(AlarmSearch.this)) {
						if (et_edit_Text.getText().toString().length() == 0) {
							Utils.toastMsg(AlarmSearch.this, "Enter Site Id");
							ll_open_alarm.setVisibility(View.GONE);
							ll_current_status.setVisibility(View.GONE);
							RL.setVisibility(View.GONE);
						} else {
							new CurrentStatusTask(AlarmSearch.this).execute();
						}
					} else {
						Utils.toast(AlarmSearch.this, "17");  //No internet connection
					}
				}
			}
		});
	}

	public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;

		public AssignedTicketsTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("siteID",
						et_edit_Text.getText().toString()));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetSiteAlarms;
				}else{
					url=moduleUrl+ WebMethods.url_GetSiteAlarms;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_GetSiteAlarms
						:  "http://" + moduleUrl + "/Service.asmx/GetSiteAlarms";*/
				response_site_description = Utils.httpPostRequest(con,url,
						nameValuePairs);
				Gson gson = new Gson();
				site_alarm_list = gson.fromJson(response_site_description,
						BeansAlarmList.class);
			} catch (Exception e) {
				e.printStackTrace();
				site_alarm_list = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!isFinishing()) {
				if ((site_alarm_list == null)) {
					Utils.toastMsg(AlarmSearch.this, "No Data Found");
					ll_open_alarm.setVisibility(View.GONE);
					ll_current_status.setVisibility(View.GONE);
					RL.setVisibility(View.VISIBLE);

				} else if (site_alarm_list.getActiveAlarm_list()!=null && site_alarm_list.getActiveAlarm_list().size() > 0) {
					AdapterAlarmManagement adapter = new AdapterAlarmManagement(
							AlarmSearch.this, response_site_description);
					adapter.notifyDataSetChanged();
					openalarm.setAdapter(adapter);
					ll_open_alarm.setVisibility(View.VISIBLE);
					ll_current_status.setVisibility(View.GONE);
					RL.setVisibility(View.GONE);
				} else {
					Utils.toastMsg(AlarmSearch.this, "No Data Found");
					ll_open_alarm.setVisibility(View.GONE);
					ll_current_status.setVisibility(View.GONE);
					RL.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	// 1.5.2.2
	public class CurrentStatusTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;

		public CurrentStatusTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("countryID",
						mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("hubID",
						mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("regionID",
						mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("circleID",
						mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneID",
						mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterID",
						mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("siteID",
						et_edit_Text.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("alarmType", ""));
				nameValuePairs.add(new BasicNameValuePair("pIOMEID",
						mAppPreferences.getPIOMEID()));
				nameValuePairs.add(new BasicNameValuePair("status", ""));
				nameValuePairs.add(new BasicNameValuePair("siteType", ""));
				nameValuePairs.add(new BasicNameValuePair("userID",
						mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("powerModel", ""));
				nameValuePairs.add(new BasicNameValuePair("source", "40"));
				nameValuePairs.add(new BasicNameValuePair("formID", "1"));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetAlarmStatus;
				}else{
					url=moduleUrl+ WebMethods.url_GetAlarmStatus;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_GetAlarmStatus
						:  "http://" + moduleUrl + "/Service.asmx/GetAlarmStatus";*/
				response = Utils.httpPostRequest(con,url, nameValuePairs);
				Gson gson = new Gson();
				current_status = gson.fromJson(response,
						BeanCurrentStatusList.class);

			} catch (Exception e) {
				e.printStackTrace();
				current_status = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!isFinishing()) {
				if ((current_status == null)) {
					//Toast.makeText(AlarmSearch.this, "Server Not Available",
					//		Toast.LENGTH_LONG).show();
					//ll_open_alarm.setVisibility(View.GONE);
					//ll_current_status.setVisibility(View.GONE);
					//RL.setVisibility(View.GONE);
					Utils.toastMsg(AlarmSearch.this, "No Data Found");
					ll_open_alarm.setVisibility(View.GONE);
					ll_current_status.setVisibility(View.GONE);
					RL.setVisibility(View.VISIBLE);


				} else if (current_status.getGetAlarmStatus_list()!=null && current_status.getGetAlarmStatus_list().size() > 0) {
					adapter1 = new AdapterAlarmStatus(AlarmSearch.this,response);
					adapter1.notifyDataSetChanged();
					currentStatus.setAdapter(adapter1);
					currentStatus.setDivider(null);
					ll_open_alarm.setVisibility(View.GONE);
					ll_current_status.setVisibility(View.VISIBLE);
					ll_current_status.setBackgroundResource( R.drawable.input_box );
					RL.setVisibility(View.GONE);
				} else {
					Utils.toastMsg(AlarmSearch.this, "No Data Found");
					ll_open_alarm.setVisibility(View.GONE);
					ll_current_status.setVisibility(View.GONE);
					RL.setVisibility(View.VISIBLE);


				}
			}
		}
	}

}
