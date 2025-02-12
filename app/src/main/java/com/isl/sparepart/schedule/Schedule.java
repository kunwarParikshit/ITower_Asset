package com.isl.sparepart.schedule;

import com.isl.constant.WebMethods;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.IncidentMetaList;
import infozech.itower.R;
import com.isl.modal.BeanSiteList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.preventive.ScheduleModuleTab;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;
import com.isl.modal.ResponseTechnician;
import com.isl.itower.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Schedule extends Activity {
	BeanSiteList scheduleCount = null, doneCount = null, missCount = null,
			pendingCount = null, reviewCount = null, rejectCount = null,resubmitCount = null;
	RelativeLayout rl_schedule, rl_missed, rl_done,rl_resubmit,rl_pending, rl_approve,rl_reject,
			rl_filling_schedule, rl_filling_missed, rl_filling_done;
	LinearLayout ll_filling_schedule;

	TextView txt_pm_schedule, txt_pm_review, txt_filling, txt_pm_scheduled,
			txt_pm_schedule_cnt, txt_pm_missed, txt_pm_missed_cnt, txt_pm_done,txt_pm_resubmit,txt_pm_resubmit_cnt,
			txt_pm_done_cnt, txt_pm_pending, txt_pm_pending_cnt,
			txt_pm_approved, txt_pm_approved_cnt, txt_pm_reject,txt_pm_reject_cnt,txt_filling_schedule,
			txt_filling_schedule_cnt, txt_filling_missed,
			txt_filling_missed_cnt, txt_filling_done, txt_filling_done_cnt,
			tv_brand_logo;
	String response1,response2,response3,response4,response5,response6,response7;
	ResponseTechnician res_filling;
	AppPreferences mAppPreferences;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;
	private NetworkManager networkManager;//108

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DataBaseHelper(this);
		db.open();
		moduleUrl = db.getModuleIP("Schedule");
		setContentView(R.layout.activity_my_schedule);
		mAppPreferences = new AppPreferences(Schedule.this);
		tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
		ll_filling_schedule = (LinearLayout) findViewById(R.id.ll_filling_schedule);

		rl_schedule = (RelativeLayout) findViewById(R.id.rl_schedule);
		rl_missed = (RelativeLayout) findViewById(R.id.rl_missed);
		rl_done = (RelativeLayout) findViewById(R.id.rl_done);
		rl_resubmit = (RelativeLayout) findViewById(R.id.rl_resubmit);
		rl_pending = (RelativeLayout) findViewById(R.id.rl_pending);
		rl_approve = (RelativeLayout) findViewById(R.id.rl_approve);
		rl_reject = (RelativeLayout) findViewById(R.id.rl_reject);

		rl_filling_schedule = (RelativeLayout) findViewById(R.id.rl_filling_schedule);
		rl_filling_missed = (RelativeLayout) findViewById(R.id.rl_filling_missed);
		rl_filling_done = (RelativeLayout) findViewById(R.id.rl_filling_done);

		txt_pm_schedule = (TextView) findViewById(R.id.txt_pm_schedule);
		txt_pm_review = (TextView) findViewById(R.id.txt_pm_review);
		txt_filling = (TextView) findViewById(R.id.txt_filling);

		txt_pm_scheduled = (TextView) findViewById(R.id.txt_pm_scheduled);
		txt_pm_scheduled.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_schedule_cnt = (TextView) findViewById(R.id.txt_pm_schedule_cnt);
		txt_pm_schedule_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_pm_missed = (TextView) findViewById(R.id.txt_pm_missed);
		txt_pm_missed.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_missed_cnt = (TextView) findViewById(R.id.txt_pm_missed_cnt);
		txt_pm_missed_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_pm_done = (TextView) findViewById(R.id.txt_pm_done);
		txt_pm_done.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_done_cnt = (TextView) findViewById(R.id.txt_pm_done_cnt);
		txt_pm_done_cnt.setTypeface(Utils.typeFace(Schedule.this));


		txt_pm_resubmit = (TextView) findViewById(R.id.txt_pm_resubmit);
		txt_pm_resubmit.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_resubmit_cnt = (TextView) findViewById(R.id.txt_pm_resubmit_cnt);
		txt_pm_resubmit_cnt.setTypeface(Utils.typeFace(Schedule.this));



		txt_pm_pending = (TextView) findViewById(R.id.txt_pm_pending);
		txt_pm_pending.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_pending_cnt = (TextView) findViewById(R.id.txt_pm_pending_cnt);
		txt_pm_pending_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_pm_approved = (TextView) findViewById(R.id.txt_pm_approved);
		txt_pm_approved.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_approved_cnt = (TextView) findViewById(R.id.txt_pm_approved_cnt);
		txt_pm_approved_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_pm_reject = (TextView) findViewById(R.id.txt_pm_reject);
		txt_pm_reject.setTypeface(Utils.typeFace(Schedule.this));
		txt_pm_reject_cnt = (TextView) findViewById(R.id.txt_pm_reject_cnt);
		txt_pm_reject_cnt.setTypeface(Utils.typeFace(Schedule.this));


		txt_filling_schedule = (TextView) findViewById(R.id.txt_filling_schedule);
		txt_filling_schedule.setTypeface(Utils.typeFace(Schedule.this));
		txt_filling_schedule_cnt = (TextView) findViewById(R.id.txt_filling_schedule_cnt);
		txt_filling_schedule_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_filling_missed = (TextView) findViewById(R.id.txt_filling_missed);
		txt_filling_missed.setTypeface(Utils.typeFace(Schedule.this));
		txt_filling_missed_cnt = (TextView) findViewById(R.id.txt_filling_missed_cnt);
		txt_filling_missed_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_filling_done = (TextView) findViewById(R.id.txt_filling_done);
		txt_filling_done.setTypeface(Utils.typeFace(Schedule.this));
		txt_filling_done_cnt = (TextView) findViewById(R.id.txt_filling_done_cnt);
		txt_filling_done_cnt.setTypeface(Utils.typeFace(Schedule.this));

		txt_pm_schedule_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_missed_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_done_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_resubmit_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_pending_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_approved_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_pm_reject_cnt.setText("0" + " " + Utils.msg(Schedule.this, "237"));
		txt_filling_schedule_cnt.setText("0" + " "+ Utils.msg(Schedule.this, "237"));
		txt_filling_missed_cnt.setText("0" + " "+ Utils.msg(Schedule.this, "237"));
		txt_filling_done_cnt.setText("0" + " "+ Utils.msg(Schedule.this, "237"));

		Utils.msgText(Schedule.this, "234", tv_brand_logo); // Schedule
		Utils.msgText(Schedule.this, "236", txt_pm_schedule); // PM Schedule
		Utils.msgText(Schedule.this, "235", txt_filling); // Filling Schedule
		Utils.msgText(Schedule.this, "278", txt_pm_review); // PM Review

		Utils.msgText(Schedule.this, "219", txt_pm_scheduled); // Scheduled
		Utils.msgText(Schedule.this, "222", txt_pm_missed); // Missed
		Utils.msgText(Schedule.this, "223", txt_pm_done); // done
		Utils.msgText(Schedule.this, "515", txt_pm_resubmit); // done
		Utils.msgText(Schedule.this, "220", txt_pm_pending); // Pending
		Utils.msgText(Schedule.this, "221", txt_pm_approved); // Reviewed
		Utils.msgText(Schedule.this, "467", txt_pm_reject); // Reject
		Utils.msgText(Schedule.this, "219", txt_filling_schedule); // Scheduled
		Utils.msgText(Schedule.this, "222", txt_filling_missed); // Missed
		Utils.msgText(Schedule.this, "223", txt_filling_done); // done
		getMenuRights();

		rl_schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("NEXT_7_DAYS");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "219");// Scheduled
				i.putExtra("type", "NEXT_7_DAYS");// scheduled
				startActivity(i);

			}
		});

		rl_missed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "222");// Missed
				i.putExtra("type", "TILL_YESTERDAY");// missed
				startActivity(i);

			}
		});

		rl_resubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("RESUBMITTED");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "515");// Missed
				i.putExtra("type", "RESUBMITTED");// missed
				startActivity(i);
			}
		});

		rl_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("TILL_TODAY");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "223");// Done
				i.putExtra("type", "TILL_TODAY"); // done
			    startActivity(i);
			}
		});

		rl_pending.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("APPROVAL_PENDING");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "220");// Pending
				i.putExtra("type", "APPROVAL_PENDING");// pending
				//i.putExtra("data", response4);
				startActivity(i);
			}
		});

		rl_approve.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("TILL_TODAY_APPROVED");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "221");// Reviewed
				i.putExtra("type", "TILL_TODAY_APPROVED");// approve
				//i.putExtra("data", response5);
				startActivity(i);
			}
		});

		rl_reject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.setPMScheduleCountType("REJECTED");
				Intent i = new Intent(Schedule.this, ScheduleModuleTab.class);
				//i.putExtra("hdTextId", "467");// Rejected
				i.putExtra("type", "REJECTED");// approve
				//i.putExtra("data", response5);
				startActivity(i);
			}
		});

		rl_filling_schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Schedule.this, FillingSchedules.class);
				i.putExtra("type", "Scheduled");
				startActivity(i);
			}
		});

		rl_filling_missed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Schedule.this, FillingSchedules.class);
				i.putExtra("type", "Missed");
				startActivity(i);
			}
		});

		rl_filling_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Schedule.this, FillingSchedules.class);
				i.putExtra("type", "Done");
				startActivity(i);
			}
		});

		Button bt_back = (Button) findViewById(R.id.button_back);
		Utils.msgButton(Schedule.this, "71", bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mAppPreferences.getBackModeNotifi6() == 2) {
					finish();
				} else {
					Intent i = new Intent(Schedule.this, HomeActivity.class);
					startActivity(i);
					finish();
				}
			}
		});
		//new GetSchedules(this).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		networkManager = new NetworkManager();//108
		if (Utils.isNetworkAvailable(this)) {
			//108
			networkManager.getToken(new NetworkManager.TokenCallback() {
				@Override
				public void onTokenReceived(String token) {
					new GetSchedules(Schedule.this,token).execute();
				}

				@Override
				public void onTokenError(String error) {
					Toast.makeText(Schedule.this, error, Toast.LENGTH_SHORT).show();
				}
			});
			//108
			//new GetSchedules(this).execute();
			new GetFillingSchedules(this).execute();
			if (!metaDataType().equalsIgnoreCase("")) {
				new IncidentMetaDataTask(Schedule.this).execute();
			}
		} else {
            txt_pm_schedule_cnt.setText(db.scheduleSiteCount()+ " "
                    + Utils.msg(Schedule.this, "237"));

            txt_pm_missed_cnt.setText(db.missSiteCount() + " "
                    + Utils.msg(Schedule.this, "237"));
			// Toast.makeText(MySchedule1.this,
			// "No Internet Connection",Toast.LENGTH_SHORT).show();
			Utils.toast(Schedule.this, "17");
		}
	}

	public void getMenuRights() {
		String filling_schedule="",pm_review="",pm_schecdule="",pm_done="",pm_resubmit="",pm_missed="",pm_pending="",pm_reject="";
		DataBaseHelper dbHelper = new DataBaseHelper(Schedule.this);
		dbHelper.open();
		filling_schedule = dbHelper.getSubMenuRight("MyFillingSchedules", "Schedule");
		pm_schecdule = dbHelper.getSubMenuRight("PMScheduled", "Schedule");
		pm_done = dbHelper.getSubMenuRight("PMDone", "Schedule");
		pm_missed = dbHelper.getSubMenuRight("PMMissed", "Schedule");
		pm_pending = dbHelper.getSubMenuRight("PMPending", "Schedule");
		pm_review = dbHelper.getSubMenuRight("PMReviewed", "Schedule");
		pm_reject = dbHelper.getSubMenuRight("PMReject", "Schedule");
		pm_resubmit = dbHelper.getSubMenuRight("PMReSubmit", "Schedule");

		dbHelper.close();
		if (!filling_schedule.contains( "V" ) && !pm_schecdule.contains("V") && !pm_done.contains("V")
				&& !pm_missed.contains( "V" ) && !pm_pending.contains( "V" ) && !pm_review.contains( "V" )
				&& !pm_reject.contains( "V" ) && !pm_resubmit.contains("V")) {
			//You are not authorized for menus
			Intent i = new Intent(Schedule.this, HomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		} else {
            int a=0,b=0;
			if (pm_schecdule.contains("V")) {
				rl_schedule.setVisibility(View.VISIBLE);
				a=1;
			} else {
				rl_schedule.setVisibility(View.GONE);
			}

			if (pm_done.contains("V")) {
				rl_done.setVisibility(View.VISIBLE);
				a=1;
			} else {
				rl_done.setVisibility(View.GONE);
			}

			if (pm_missed.contains("V")) {
				rl_missed.setVisibility(View.VISIBLE);
				a=1;
			} else {
				rl_missed.setVisibility(View.GONE);
			}

			if (pm_resubmit.contains("V")) {
				rl_resubmit.setVisibility(View.VISIBLE);
				a=1;
			} else {
				rl_resubmit.setVisibility(View.GONE);
			}

			if (pm_pending.contains("V")) {
				rl_pending.setVisibility(View.VISIBLE);
				b=1;
			} else {
				rl_pending.setVisibility(View.GONE);
			}

			if (pm_review.contains("V")) {
				rl_approve.setVisibility(View.VISIBLE);
				b=1;
			} else {
				rl_approve.setVisibility(View.GONE);
			}

			if (pm_reject.contains("V")) {
				rl_reject.setVisibility(View.VISIBLE);
				b=1;
			} else {
				rl_reject.setVisibility(View.GONE);
			}

           if(a==1){
			   txt_pm_schedule.setVisibility(View.VISIBLE);
		   }else{
			   txt_pm_schedule.setVisibility(View.GONE);
		   }

		   if(b==1){
			   txt_pm_review.setVisibility( View.VISIBLE );
		   }else {
			   txt_pm_review.setVisibility( View.GONE );
		   }

			if (filling_schedule.contains("V")) {
				ll_filling_schedule.setVisibility(View.VISIBLE);
			} else {
				ll_filling_schedule.setVisibility(View.GONE);
			}
		}
	}

	public class GetSchedules extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String token; //108

		public GetSchedules(Context con,String token) {
			this.con = con;
			this.token = token; //108
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
				Gson gson = new Gson();
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
				}else{
					url=moduleUrl+ WebMethods.url_getScheduled_Sites;
				}

			try {
				List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(4);
				nameValuePairs1.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs1.add(new BasicNameValuePair("type","NEXT_7_DAYS"));
				nameValuePairs1.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs1.add(new BasicNameValuePair("activityTypeFlag","1"));
				response1 = Utils.httpPostRequest1(con,url, nameValuePairs1,token);
				scheduleCount = gson.fromJson(response1, BeanSiteList.class);
			 } catch (Exception e) {
				e.printStackTrace();
				scheduleCount=null;
			 }

			try {
				List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(4);
				nameValuePairs2.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs2.add(new BasicNameValuePair("type", "TILL_TODAY"));
				nameValuePairs2.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs2.add(new BasicNameValuePair("activityTypeFlag","1"));
				response2 = Utils.httpPostRequest1(con,url, nameValuePairs2,token);
				doneCount = gson.fromJson(response2, BeanSiteList.class);
		     } catch (Exception e) {
			  e.printStackTrace();
				doneCount=null;
		    }

			try {
				List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(4);
				nameValuePairs3.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs3.add(new BasicNameValuePair("type","TILL_YESTERDAY"));
				nameValuePairs3.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs3.add(new BasicNameValuePair("activityTypeFlag","1"));
				response3 = Utils.httpPostRequest1(con,url, nameValuePairs3,token);
				missCount = gson.fromJson(response3, BeanSiteList.class);
	        } catch (Exception e) {
		    e.printStackTrace();
				missCount=null;
	        }

			try {
				List<NameValuePair> nameValuePairs4 = new ArrayList<NameValuePair>(4);
				nameValuePairs4.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs4.add(new BasicNameValuePair("type","APPROVAL_PENDING"));
				nameValuePairs4.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs4.add(new BasicNameValuePair("activityTypeFlag","1"));
				response4 = Utils.httpPostRequest1(con,url, nameValuePairs4,token);
				pendingCount = gson.fromJson(response4, BeanSiteList.class);
            } catch (Exception e) {
		    e.printStackTrace();
				pendingCount=null;
		   }

			try {
				List<NameValuePair> nameValuePairs5 = new ArrayList<NameValuePair>(4);
				nameValuePairs5.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs5.add(new BasicNameValuePair("type","TILL_TODAY_APPROVED"));
				nameValuePairs5.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs5.add(new BasicNameValuePair("activityTypeFlag","1"));
				response5 = Utils.httpPostRequest1(con,url, nameValuePairs5,token);
				reviewCount = gson.fromJson(response5, BeanSiteList.class);
			} catch (Exception e) {
				e.printStackTrace();
				reviewCount=null;
			}

			try {
				List<NameValuePair> nameValuePairs6 = new ArrayList<NameValuePair>(4);
				nameValuePairs6.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs6.add(new BasicNameValuePair("type","REJECTED"));
				nameValuePairs6.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs6.add(new BasicNameValuePair("activityTypeFlag","1"));
				response6 = Utils.httpPostRequest1(con,url, nameValuePairs6,token);
				rejectCount = gson.fromJson(response6, BeanSiteList.class);
			} catch (Exception e) {
				e.printStackTrace();
				rejectCount=null;
			}

			try {
				List<NameValuePair> nameValuePairs7 = new ArrayList<NameValuePair>(4);
				nameValuePairs7.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs7.add(new BasicNameValuePair("type","RESUBMITTED"));
				nameValuePairs7.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs7.add(new BasicNameValuePair("activityTypeFlag","1"));
				response7 = Utils.httpPostRequest1(con,url, nameValuePairs7,token);
				resubmitCount = gson.fromJson(response7, BeanSiteList.class);
			} catch (Exception e) {
				e.printStackTrace();
				resubmitCount=null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			if (scheduleCount != null && scheduleCount.getSite_list()!=null) {
				txt_pm_schedule_cnt.setText(scheduleCount.getSite_list().size()
						+ " " + Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_schedule_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (missCount != null && missCount.getSite_list()!=null) {
				txt_pm_missed_cnt.setText(missCount.getSite_list().size() + " "
						+ Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_missed_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (doneCount != null && doneCount.getSite_list()!=null) {
				txt_pm_done_cnt.setText(doneCount.getSite_list().size() + " "
						+ Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_done_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (pendingCount != null && pendingCount.getSite_list()!=null) {
				txt_pm_pending_cnt.setText(pendingCount.getSite_list().size()
						+ " " + Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_pending_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (reviewCount != null && reviewCount.getSite_list()!=null) {
				txt_pm_approved_cnt.setText(reviewCount.getSite_list().size()
						+ " " + Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_approved_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (rejectCount != null && rejectCount.getSite_list()!=null) {
				txt_pm_reject_cnt.setText(rejectCount.getSite_list().size()
						+ " " + Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_reject_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			if (resubmitCount != null && resubmitCount.getSite_list()!=null) {
				txt_pm_resubmit_cnt.setText(resubmitCount.getSite_list().size()
						+ " " + Utils.msg(Schedule.this, "237"));
			} else {
				txt_pm_resubmit_cnt.setText("0" + " "
						+ Utils.msg(Schedule.this, "237"));
			}

			super.onPostExecute(result);
		}
	};

	public class GetFillingSchedules extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;

		public GetFillingSchedules(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Gson g = new Gson();
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduledFilling;
				}else{
					url=moduleUrl+ WebMethods.url_getScheduledFilling;
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("roleId",mAppPreferences.getRoleId()));
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				String schedules_data_filling = res.replace("[", "").replace("]", "");
				res_filling = g.fromJson( schedules_data_filling, ResponseTechnician.class );
			} catch (Exception e) {
				e.printStackTrace();
				res_filling=null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(res_filling!=null){
				txt_filling_schedule_cnt.setText( res_filling.getNEXT_7_DAYS()
						+ " "
						+ Utils.msg( Schedule.this, "237" ) );
				txt_filling_missed_cnt.setText( res_filling
						.getTILL_YESTERDAY()
						+ " "
						+ Utils.msg( Schedule.this, "237" ) );
				txt_filling_done_cnt.setText( res_filling.getTILL_TODAY()
						+ " " + Utils.msg( Schedule.this, "237" ) );
			} else {
				// Toast.makeText(MyPMSchedules.this,"Server Not Available",
				// Toast.LENGTH_LONG).show();
				Utils.toast(Schedule.this, "13");
			}
			super.onPostExecute(result);
		}
	};

	@Override
	public void onBackPressed() {
		if (mAppPreferences.getBackModeNotifi6() == 2) {
			finish();
		} else {
			Intent i = new Intent(Schedule.this, HomeActivity.class);
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
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
				resposnse_Incident_meta = gson.fromJson(res,IncidentMetaList.class);
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
				Utils.toast(Schedule.this, "70");
			} else if (resposnse_Incident_meta != null) {
				if (resposnse_Incident_meta.getParam()!=null && resposnse_Incident_meta.getParam().size() > 0) {
					db.clearInciParamData("654");
					db.insertInciParamcData(resposnse_Incident_meta.getParam(),"654");
					db.dataTS(null, null, "10", db.getLoginTimeStmp("10","0"), 2,"0");

				}
			} else {
				// Toast.makeText(IncidentManagement.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				Utils.toast(Schedule.this, "13");
			}
		}
	}

	public String metaDataType() {
		String DataType_Str = "1";
		String j = Utils.CompareDates(db.getSaveTimeStmp("10","0"),
				db.getLoginTimeStmp("10","0"), "10");

		if (j != "1") {
			DataType_Str = j;
		}
		if (DataType_Str == "1") {
			DataType_Str = "";
		}

		return DataType_Str;
	}


	// Class to call Web Service to get PM CheckList to draw form.
	private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		BeanCheckListDetails PMCheckList;
        String nextType="",headerId="";
		private GetPMCheckList(Context con,String nextType,String headerId) {
			this.con = con;
			this.nextType = nextType;
			this.headerId = headerId;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show( con, null, "Loading..." );
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
			Gson gson = new Gson();
			nameValuePairs.add( new BasicNameValuePair( "siteId","" ) );
			nameValuePairs.add( new BasicNameValuePair( "checkListType","0")); // 0 means all checklist(20001,20002,20005...) data download
			nameValuePairs.add( new BasicNameValuePair( "checkListDate","" ) );
			nameValuePairs.add( new BasicNameValuePair( "status", "S")); //S or M get blank checklistdata
			nameValuePairs.add( new BasicNameValuePair( "dgType", "" ) );
			nameValuePairs.add( new BasicNameValuePair( "languageCode", mAppPreferences.getLanCode() ) );
			try {
				if (moduleUrl.equalsIgnoreCase( "0" )) {
					url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
				} else {
					url = moduleUrl + WebMethods.url_getCheckListDetails;
				}
				String res = Utils.httpPostRequest( con, url, nameValuePairs );
				PMCheckList = gson.fromJson( res, BeanCheckListDetails.class );
			} catch (Exception e) {
				//e.printStackTrace();
				PMCheckList = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (PMCheckList == null) {
				// Toast.makeText(PMChecklist.this,"PM CheckList not available.Pls contact system admin.",Toast.LENGTH_LONG).show();
				Utils.toast( Schedule.this, "226" );
			} else if (PMCheckList != null) {
				if (PMCheckList.getPMCheckListDetail()!=null && PMCheckList.getPMCheckListDetail().size() > 0) {
					DataBaseHelper dbHelper = new DataBaseHelper(Schedule.this);
					dbHelper.open();
					dbHelper.clearCheckListPMForm("655");
					dbHelper.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),
							"655",0,"",Schedule.this,"0","0");
					dbHelper.dataTS( null, null, "20",
							dbHelper.getLoginTimeStmp( "20","0" ), 2 ,"0");
					dbHelper.close();
				}
			} else {
				Utils.toast( Schedule.this, "13" );
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}

			Intent i = new Intent(Schedule.this, PMSchedules.class);
			i.putExtra("hdTextId", headerId);// Scheduled
			i.putExtra("type",nextType);// scheduled
			startActivity(i);

			super.onPostExecute( result );
		}
	}

	private String timeStamp() {
		String dataTypeID = null;
		DataBaseHelper dbHelper = new DataBaseHelper( Schedule.this );
		dbHelper.open();
		String dataType = "1";
		dataTypeID = Utils.CompareDates( dbHelper.getSaveTimeStmp( "20","0" ),
				dbHelper.getLoginTimeStmp( "20","0" ), "20" );

		dbHelper.close();
		if (dataTypeID != "1") {
			dataType = dataTypeID;
		}
		if (dataType == "1") {
			dataType = "";
		}
		return dataType;
	}

}
