package com.isl.incident;
import infozech.itower.R;

import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSearchTktRptList;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
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
import android.widget.TextView;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ExistingTicketsList extends Activity {
	ListView ticket_list;
	RelativeLayout textlayout;
	AppPreferences mAppPreferences;
	TextView tv_brand_logo,txt_no_ticket;
	Button bt_back;
	BeanSearchTktRptList response_tkt_rpt_list;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;
	String final_site_id,final_status,final_alarmDescId,final_severity,final_from,final_to,final_criteria,final_tktTypeId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(ExistingTicketsList.this);
		setContentView(R.layout.activity_searched_tickets_list);
		db = new DataBaseHelper(ExistingTicketsList.this);
		db.open();

		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			moduleUrl = db.getModuleIP("HealthSafty");
		}else{
			moduleUrl = db.getModuleIP("Incident");
		}
		db.close();
		final_site_id = getIntent().getExtras().getString("final_site_id");
		final_status = getIntent().getExtras().getString("final_status");
		final_alarmDescId = getIntent().getExtras().getString("final_alarmDescId");
		final_severity = getIntent().getExtras().getString("final_severity");
		final_from = getIntent().getExtras().getString("final_from");
		final_to = getIntent().getExtras().getString("final_to");
		final_criteria = getIntent().getExtras().getString("final_criteria");
		final_tktTypeId = getIntent().getExtras().getString("final_tktTypeId");

		tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
		txt_no_ticket=(TextView)findViewById(R.id.txt_no_ticket);
		bt_back = (Button) findViewById(R.id.button_back);
		textlayout = (RelativeLayout) findViewById(R.id.textlayout);
		ticket_list = (ListView) findViewById(R.id.lv_tickets);
		ticket_list.setDivider(null);
		setMsg();
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		ticket_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent i = new Intent(ExistingTicketsList.this,TicketDetailsTabs.class);
				mAppPreferences.SetBackModeNotifi123(2);
				i.putExtra("id", response_tkt_rpt_list.getTicket_list().get(arg2).getTktId());
				i.putExtra("sid", response_tkt_rpt_list.getTicket_list().get(arg2).getSid());
				mAppPreferences.setTTtabSelection("SearchTicket");
				startActivity(i);
			}
		});

		if (Utils.isNetworkAvailable(ExistingTicketsList.this)) {
			new SearchTicketTask( ExistingTicketsList.this).execute();
		} else{
			Utils.toast(ExistingTicketsList.this, "17");
		}

	}
	public void setMsg(){
		Utils.msgText(ExistingTicketsList.this, "145",tv_brand_logo); //set Text Fuel Filling
		Utils.msgText(ExistingTicketsList.this, "141",txt_no_ticket); //set Text Site Visibility		
	}

	public class SearchTicketTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String response;
		public SearchTicketTask(Context con) {
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
				nameValuePairs.add(new BasicNameValuePair("user_id", mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("site_id", final_site_id));
				nameValuePairs.add(new BasicNameValuePair("status", final_status));
				nameValuePairs.add(new BasicNameValuePair("alarmId", final_alarmDescId));
				nameValuePairs.add(new BasicNameValuePair("ticketType", final_severity));
				nameValuePairs.add(new BasicNameValuePair("fromDate", final_from));
				nameValuePairs.add(new BasicNameValuePair("toDate", final_to));
				nameValuePairs.add(new BasicNameValuePair("circleId",mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneId",mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterId",mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("alarmFlag",final_criteria));
				//mode 0 means get count and 1 means ticket for rpt
				nameValuePairs.add(new BasicNameValuePair("mode","1"));
				nameValuePairs.add(new BasicNameValuePair("tktType",final_tktTypeId));
				if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
					nameValuePairs.add(new BasicNameValuePair("module","H"));
				}else{
					nameValuePairs.add(new BasicNameValuePair("module","T"));
				}

				nameValuePairs.add( new BasicNameValuePair( "userCategory", mAppPreferences.getUserCategory()));      //1.0
				nameValuePairs.add( new BasicNameValuePair( "userSubCategory", mAppPreferences.getUserSubCategory()));

				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_get_tickets;
				}else{
					url=moduleUrl+ WebMethods.url_get_tickets;
				}
				response = Utils.httpPostRequest(con,url,nameValuePairs);
				Gson gson = new Gson();
				response_tkt_rpt_list = gson.fromJson( response, BeanSearchTktRptList.class );
		    	} catch (Exception e) {
				response_tkt_rpt_list=null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (response_tkt_rpt_list == null) {
				   ticket_list.setVisibility(View.GONE);
				   textlayout.setVisibility(View.VISIBLE);
				} else if (response_tkt_rpt_list.getTicket_list() != null && response_tkt_rpt_list.getTicket_list().size() > 0) {
				   ticket_list.setVisibility(View.VISIBLE);
				   textlayout.setVisibility(View.GONE);
				   ticket_list.setAdapter(new AdapterTickets(ExistingTicketsList.this,response,mAppPreferences.getTTModuleSelection()));
				} else {
				   ticket_list.setVisibility(View.GONE);
				   textlayout.setVisibility(View.VISIBLE);
				}
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onResume() {
		AppPreferences updateMAppPreferences = new AppPreferences(ExistingTicketsList.this);
		if(updateMAppPreferences.getTTupdateStatus()==1) {
			if (Utils.isNetworkAvailable(ExistingTicketsList.this)) {
				new SearchTicketTask( ExistingTicketsList.this).execute();
			} else{
				Utils.toast(ExistingTicketsList.this, "17");
			}
		}
		super.onResume();
	}
}
