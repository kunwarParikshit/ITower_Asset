package com.isl.incident;
import infozech.itower.R;

import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSearchTktList;
import com.isl.modal.BeanSearchTktRptList;
import com.isl.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.gson.Gson;

public class TicketSearch extends Activity {
	EditText et_site_id;
	AppPreferences mAppPreferences;
	Spinner sp_tkt_status, sp_sererity, sp_search_by;
	public SearchableSpinner sp_alarmDesc, sp_tktType;
	TextView search, tv_brand_logo, tv_site_id, tv_tkt_status, tv_tickettype;// tv_age;
	TextView tv_open_ticket, tv_open_count, tv_assign_ticket, tv_assign_count, tv_resolve_ticket, tv_resolve_count,
			tv_close_ticket, tv_close_count, from, tv_from_Date, to, tv_to_Date, tv_sererity,
			tv_alarm_desc, tv_search_by;
	Button bt_back;
	DataBaseHelper db = null;
	String moduleUrl = "";
	String url = "";
	private int pYear, pMonth, pDay;
	Date d1, d2;

	String final_site_id, final_alarmDescId, final_severity, final_tktTypeId, final_from, final_to, final_criteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(TicketSearch.this);
		db = new DataBaseHelper(TicketSearch.this);
		db.open();
		setContentView(R.layout.ticket_search);
		getControllerId();
		setMsg();
		init();

		if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
			moduleUrl = db.getModuleIP("HealthSafty");
		} else {
			moduleUrl = db.getModuleIP("Incident");
		}
		tv_open_count.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!tv_open_count.getText().toString().equalsIgnoreCase("0")) {
					nextActivity("5");
				} else {
					Utils.toast(TicketSearch.this, "141");
				}
			}
		});

		tv_assign_count.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!tv_assign_count.getText().toString().equalsIgnoreCase("0")) {
					nextActivity("6");
				} else {
					Utils.toast(TicketSearch.this, "141");
				}
			}
		});

		tv_resolve_count.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!tv_resolve_count.getText().toString().equalsIgnoreCase("0")) {
					nextActivity("7");
				} else {
					Utils.toast(TicketSearch.this, "141");
				}
			}
		});

		tv_close_count.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!tv_close_count.getText().toString().equalsIgnoreCase("0")) {
					nextActivity("8");
				} else {
					Utils.toast(TicketSearch.this, "141");
				}
			}
		});


		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});


		tv_from_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showFromDateTimeDialog(tv_from_Date);
			}
		});

		tv_to_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showToDateTimeDialog(tv_to_Date);
			}
		});

		searchTicket("0");
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				//inputManager.hideSoftInputFromWindow(getCurrentFocus()
				//		.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				if (validate()) {
					searchTicket("0");
				}
			}
		});

	}

	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_text, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
		spinner.setAdapter(dataAdapter);
	}

	public class SearchTicketTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String site_id, status, treatment, alarmDescId, severity, tktTypeId, criteria, response;
		BeanSearchTktList response_tkt_list;
		BeanSearchTktRptList response_tkt_rpt_list;

		public SearchTicketTask(Context con, String site_id, String status, String alarmDescId,
								String severity, String criteria, String tktTypeId) {
			this.con = con;
			this.site_id = site_id;
			this.status = status;
			this.alarmDescId = alarmDescId;
			this.severity = severity;
			this.criteria = criteria;
			this.tktTypeId = tktTypeId;
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
				final_site_id = site_id;
				final_alarmDescId = alarmDescId;
				final_severity = severity;
				final_tktTypeId = tktTypeId;
				final_from = tv_from_Date.getText().toString().trim();
				final_to = tv_to_Date.getText().toString().trim();
				final_criteria = criteria;
				nameValuePairs.add(new BasicNameValuePair("user_id", mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("site_id", site_id));
				nameValuePairs.add(new BasicNameValuePair("status", status));
				nameValuePairs.add(new BasicNameValuePair("alarmId", alarmDescId));
				nameValuePairs.add(new BasicNameValuePair("ticketType", severity));
				//nameValuePairs.add(new BasicNameValuePair("severity", tkt_type));
				nameValuePairs.add(new BasicNameValuePair("fromDate", tv_from_Date.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("toDate", tv_to_Date.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("circleId", mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneId", mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterId", mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("alarmFlag", criteria));
				//mode 0 means get count and 1 means ticket for rpt
				nameValuePairs.add(new BasicNameValuePair("mode", "0"));
				nameValuePairs.add(new BasicNameValuePair("tktType", tktTypeId));
				if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
					nameValuePairs.add(new BasicNameValuePair("module", "H"));
				} else {
					nameValuePairs.add(new BasicNameValuePair("module", "T"));
				}

				nameValuePairs.add(new BasicNameValuePair("userCategory", mAppPreferences.getUserCategory()));      //1.0
				nameValuePairs.add(new BasicNameValuePair("userSubCategory", mAppPreferences.getUserSubCategory()));


				if (moduleUrl.equalsIgnoreCase("0")) {
					url = mAppPreferences.getConfigIP() + WebMethods.url_Search_tickets;
				} else {
					url = moduleUrl + WebMethods.url_Search_tickets;
				}
				response = Utils.httpPostRequest(con, url, nameValuePairs);
				Gson gson = new Gson();
				response_tkt_list = gson.fromJson(response, BeanSearchTktList.class);
			} catch (Exception e) {
				response_tkt_list = null;
				response_tkt_rpt_list = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			if (response_tkt_list == null) {
				// Toast.makeText(TicketSearch.this,"Server Not Available",
				// Toast.LENGTH_LONG).show();
				Utils.toast(TicketSearch.this, "13");
			} else if (response_tkt_list.getTicket_list() != null && response_tkt_list.getTicket_list().size() > 0) {
				tv_open_count.setText(Html.fromHtml("0"));
				tv_assign_count.setText(Html.fromHtml("0"));
				tv_resolve_count.setText(Html.fromHtml("0"));
				tv_close_count.setText(Html.fromHtml("0"));
				for (int a = 0; a < response_tkt_list.getTicket_list().size(); a++) {
					if (response_tkt_list.getTicket_list().get(a).getTtStatusId().equalsIgnoreCase("5")) {
						//tv_open_ticket.setText( "" + response_tkt_list.getTicket_list().get( a ).getTtStatus() );
						tv_open_count.setText(Html.fromHtml("<u>" + response_tkt_list.getTicket_list().get(a).getTktCount() + "</u>"));
					} else if (response_tkt_list.getTicket_list().get(a).getTtStatusId().equalsIgnoreCase("6")) {
						//tv_assign_ticket.setText( "" + response_tkt_list.getTicket_list().get( a ).getTtStatus() );
						tv_assign_count.setText(Html.fromHtml("<u>" + response_tkt_list.getTicket_list().get(a).getTktCount() + "</u>"));
					} else if (response_tkt_list.getTicket_list().get(a).getTtStatusId().equalsIgnoreCase("7")) {
						//tv_resolve_ticket.setText( "" + response_tkt_list.getTicket_list().get( a ).getTtStatus() );
						tv_resolve_count.setText(Html.fromHtml("<u>" + response_tkt_list.getTicket_list().get(a).getTktCount() + "</u>"));
					} else if (response_tkt_list.getTicket_list().get(a).getTtStatusId().equalsIgnoreCase("8")) {
						//tv_close_ticket.setText( "" + response_tkt_list.getTicket_list().get( a ).getTtStatus() );
						tv_close_count.setText(Html.fromHtml("<u>" + response_tkt_list.getTicket_list().get(a).getTktCount() + "</u>"));
					}
				}
			} else {
				tktNotFound();
				// No Ticket Found
				Utils.toast(TicketSearch.this, "141");
			}
			super.onPostExecute(result);
		}
	}

	public void setMsg() {
		if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
			Utils.msgText(TicketSearch.this, "528", tv_brand_logo);
			Utils.msgText(TicketSearch.this, "77", tv_site_id);
			Utils.msgText(TicketSearch.this, "571", tv_tkt_status);
			Utils.msgText(TicketSearch.this, "245", from);
			Utils.msgText(TicketSearch.this, "246", to);
			Utils.msgText(TicketSearch.this, "531", tv_sererity);
			Utils.msgText(TicketSearch.this, "530", tv_alarm_desc);
			Utils.msgText(TicketSearch.this, "144", search);
			Utils.msgButton(TicketSearch.this, "71", bt_back);
			Utils.msgText(TicketSearch.this, "529", tv_tickettype);

		} else {
			Utils.msgText(TicketSearch.this, "75", tv_brand_logo);
			Utils.msgText(TicketSearch.this, "77", tv_site_id);
			Utils.msgText(TicketSearch.this, "98", tv_tkt_status);
			Utils.msgText(TicketSearch.this, "245", from);
			Utils.msgText(TicketSearch.this, "246", to);
			Utils.msgText(TicketSearch.this, "82", tv_sererity);
			Utils.msgText(TicketSearch.this, "83", tv_alarm_desc);
			Utils.msgText(TicketSearch.this, "144", search);
			Utils.msgButton(TicketSearch.this, "71", bt_back);
			Utils.msgText(TicketSearch.this, "95", tv_tickettype);
		}


	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public void getControllerId() {
		tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
		tv_open_ticket = (TextView) findViewById(R.id.tv_open_ticket);
		tv_open_count = (TextView) findViewById(R.id.tv_open_count);
		tv_tickettype = (TextView) findViewById(R.id.tv_tickettype);
		sp_tktType = (SearchableSpinner) findViewById(R.id.sp_tktType);
		sp_tktType.setBackgroundResource(R.drawable.doted);
		tv_assign_ticket = (TextView) findViewById(R.id.tv_assign_ticket);
		tv_assign_count = (TextView) findViewById(R.id.tv_assign_count);
		tv_resolve_ticket = (TextView) findViewById(R.id.tv_resolve_ticket);
		tv_resolve_count = (TextView) findViewById(R.id.tv_resolve_count);
		tv_close_ticket = (TextView) findViewById(R.id.tv_close_ticket);
		tv_close_count = (TextView) findViewById(R.id.tv_close_count);
		tv_site_id = (TextView) findViewById(R.id.tv_site_id);
		et_site_id = (EditText) findViewById(R.id.et_site_id);
		et_site_id.setTypeface(Utils.typeFace(TicketSearch.this));
		tv_tkt_status = (TextView) findViewById(R.id.tv_tkt_status);
		//	tv_tkt_treatment = (TextView) findViewById(R.id.tv_tkt_treatment);
		//	sp_tkt_treatment = (Spinner) findViewById(R.id.spinner_tratment);
		sp_tkt_status = (Spinner) findViewById(R.id.spinner_status);
		sp_tkt_status.setBackgroundResource(R.drawable.doted);
		//	sp_tkt_treatment.setBackgroundResource(R.drawable.doted );
		from = (TextView) findViewById(R.id.from);
		tv_from_Date = (TextView) findViewById(R.id.tv_from_Date);
		to = (TextView) findViewById(R.id.to);
		tv_to_Date = (TextView) findViewById(R.id.tv_to_Date);
		tv_sererity = (TextView) findViewById(R.id.tv_sererity);
		sp_sererity = (Spinner) findViewById(R.id.sp_sererity);
		sp_sererity.setBackgroundResource(R.drawable.doted);
		tv_alarm_desc = (TextView) findViewById(R.id.tv_alarm_desc);
		sp_alarmDesc = (SearchableSpinner) findViewById(R.id.sp_alarmDesc);
		sp_alarmDesc.setBackgroundResource(R.drawable.doted);
		tv_search_by = (TextView) findViewById(R.id.tv_search_by);
		sp_search_by = (Spinner) findViewById(R.id.sp_search_by);
		sp_search_by.setBackgroundResource(R.drawable.doted);
		bt_back = (Button) findViewById(R.id.button_back);
		search = (TextView) findViewById(R.id.btn_search_ticket);
	}

	public void init() {
		final Calendar cal = Calendar.getInstance();
		pYear = cal.get(Calendar.YEAR);
		pMonth = cal.get(Calendar.MONTH);
		pDay = cal.get(Calendar.DAY_OF_MONTH);

		ArrayList<String> list_tktType = db.getInciParam3("33", tv_tickettype, mAppPreferences.getTTModuleSelection());
		addItemsOnSpinner(sp_tktType, list_tktType);


		ArrayList<String> list_status = db.getInciParam1("62", tv_tkt_status, mAppPreferences.getTTModuleSelection());
		//list_status.add( 0,"Select "+tv_tkt_status.getText().toString().trim());
		addItemsOnSpinner(sp_tkt_status, list_status);


		//	ArrayList<String> list_treatment = db.getInciParam1("1192",tv_tkt_treatment,mAppPreferences.getTTModuleSelection());
		//list_status.add( 0,"Select "+tv_tkt_status.getText().toString().trim());
		//	addItemsOnSpinner(sp_tkt_treatment, list_treatment);


		ArrayList<String> list_alarmType = db.getInciParam1("61", tv_sererity, mAppPreferences.getTTModuleSelection());
		addItemsOnSpinner(sp_sererity, list_alarmType);


		ArrayList<String> list_alarmDesc = db.getAllAlarmDesc("R", "R", "R", "Y", tv_alarm_desc,
				mAppPreferences.getTTModuleSelection(),mAppPreferences.getUserCategory(),mAppPreferences.getUserSubCategory());
		addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);

		ArrayList<String> list_search_by = new ArrayList<String>();
		list_search_by.add("Assigned RequestReport");
		list_search_by.add("All RequestReport");
		addItemsOnSpinner(sp_search_by, list_search_by);


	}


	private boolean validate() {
		boolean status = true;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH);

		if (tv_from_Date.getText().toString().trim().length() != 0
				&& tv_to_Date.getText().toString().trim().length() != 0) {

			String date1=tv_from_Date.getText().toString().toString().trim();
			if(date1.length()>0) {
			//	 simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
				try {
					d1 = simpleDateFormat.parse(date1);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			String date2=tv_to_Date.getText().toString().toString().trim();
			if(date2.length()>0){


				try {
					d2 = simpleDateFormat.parse(date2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			if (d1.after(Calendar.getInstance().getTime())) {
				Toast.makeText(TicketSearch.this, "Future Timestamp not allowed", Toast.LENGTH_LONG).show();
				//isFailed = true;
				status = false;
			} else if (!Utils.checkDateCompare( d1, d2 )) {
					//Utils.toast( TicketSearch.this, "211" );
					Toast.makeText(TicketSearch.this,"From Date cannot be greater than To Date",Toast.LENGTH_LONG).show();
					status = false;
			} else if (d2.after(Calendar.getInstance().getTime())) {
				Toast.makeText(TicketSearch.this, "Future Timestamp not allowed", Toast.LENGTH_LONG).show();
				//isFailed = true;
				status = false;
			} else{

			}

		}
	return status;
}

	public void searchTicket(String status){
		//open/Assign/Resolves/Closed ticket id
		String tktTypeId="0";
		if(!sp_tktType.getSelectedItem().toString().equalsIgnoreCase( "Select "+tv_tickettype.getText().toString().trim())){
			tktTypeId=db.getInciParamDesc("33", sp_tktType.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());
		}else{
			tktTypeId="0";
		}

		String site="";
		if(et_site_id.getText().toString().trim().length()!=0){
			site=et_site_id.getText().toString().trim();
		}

		//open/Assign/Resolves/Closed ticket id
		String tkt_status="0";
		if(!sp_tkt_status.getSelectedItem().toString().equalsIgnoreCase( "Select "+tv_tkt_status.getText().toString().trim())){
			tkt_status=db.getInciParamId("62", sp_tkt_status.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());
		}else{
			tkt_status=status;
		}


	/*	String tkt_treatment="0";
		if(!sp_tkt_treatment.getSelectedItem().toString().equalsIgnoreCase( "Select "+tv_tkt_treatment.getText().toString().trim())){
			tkt_treatment=db.getInciParamId("1192", sp_tkt_treatment.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());
		}else{
			tkt_treatment=status;
		}*/

		//Critical/major/minor/info tkt type id
		String severity="0";
		if(!sp_sererity.getSelectedItem().toString().equalsIgnoreCase( "Select "+tv_sererity.getText().toString().trim())){
			severity=db.getInciParamId("61", sp_sererity.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());
		}

		String alarmDescId="0";
		if (!sp_sererity.getSelectedItem().toString().trim()
				.equalsIgnoreCase( "Select "+tv_sererity.getText().toString().trim())) {
			alarmDescId = db.getDescIDD(severity, sp_alarmDesc.getSelectedItem().toString(),mAppPreferences.getTTModuleSelection());
		}else if(sp_sererity.getSelectedItem().toString().trim()
				.equalsIgnoreCase( "Select "+tv_sererity.getText().toString().trim())) {
			alarmDescId = db.getDescID(sp_alarmDesc.getSelectedItem().toString(),mAppPreferences.getTTModuleSelection());
		}

		String criteria="0";
		if(sp_search_by.getSelectedItem().toString().trim().equalsIgnoreCase("Assigned RequestReport")){
			criteria="1";
		}

		if (Utils.isNetworkAvailable(TicketSearch.this)) {
			new SearchTicketTask( TicketSearch.this,site,tkt_status,alarmDescId,severity,criteria,tktTypeId).execute();
		} else{
			Utils.toast(TicketSearch.this, "17");
		}
	}



	private void showFromDateTimeDialog(View v) {

		Calendar calendar = Calendar.getInstance();
		DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

				calendar.set(Calendar.YEAR,year);
				//   DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
				calendar.set(Calendar.MONTH,month);
				calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);


				TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

						calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
						calendar.set(Calendar.MINUTE,minute);

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm",Locale.ENGLISH);
						tv_from_Date.setText(simpleDateFormat.format(calendar.getTime()));


					}
				};


				new TimePickerDialog(TicketSearch.this,timeSetListener,calendar.getActualMinimum(Calendar.HOUR_OF_DAY),calendar.getActualMinimum(Calendar.MINUTE),true).show();

			}
		};
		DatePickerDialog datePickerDialog = new  DatePickerDialog(TicketSearch.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),   calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		//datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
		datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
		datePickerDialog.show();

		//new DatePickerDialog(TicketSearch.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
	}




	private void showToDateTimeDialog(TextView tv_to_Date) {

		Calendar calendar = Calendar.getInstance();
		DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

				calendar.set(Calendar.YEAR,year);
				//   DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
				calendar.set(Calendar.MONTH,month);
				calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

				TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

						calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
						calendar.set(Calendar.MINUTE,minute);

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm",Locale.ENGLISH);
						tv_to_Date.setText(simpleDateFormat.format(calendar.getTime()));

					}
				};

				new TimePickerDialog(TicketSearch.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
			}
		};

		if (tv_from_Date.getText().toString().length()==0) {
			Toast.makeText(TicketSearch.this,
					"Select " + from.getText().toString(), Toast.LENGTH_LONG).show();

		}else {

			DatePickerDialog datePickerDialog1 = new DatePickerDialog(TicketSearch.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			datePickerDialog1.getDatePicker().setMaxDate(System.currentTimeMillis());
			datePickerDialog1.show();
		}

		//new DatePickerDialog(TicketSearch.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
	}



	public void tktNotFound(){
		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			Utils.msgText(TicketSearch.this, "579", tv_open_ticket);
			tv_open_count.setText( "0");
			Utils.msgText(TicketSearch.this, "575", tv_assign_ticket);
			tv_assign_count.setText( "0");
			Utils.msgText(TicketSearch.this, "577", tv_resolve_ticket);
			tv_resolve_count.setText( "0");
			Utils.msgText(TicketSearch.this, "578", tv_close_ticket);
			tv_close_count.setText( "0");
		}else{
			Utils.msgText(TicketSearch.this, "262", tv_open_ticket);
			tv_open_count.setText( "0");
			Utils.msgText(TicketSearch.this, "263", tv_assign_ticket);
			tv_assign_count.setText( "0");
			Utils.msgText(TicketSearch.this, "264", tv_resolve_ticket);
			tv_resolve_count.setText( "0");
			Utils.msgText(TicketSearch.this, "265", tv_close_ticket);
			tv_close_count.setText( "0");
			/*tv_open_ticket.setText( "Open");
			tv_open_count.setText( "0");
			tv_assign_ticket.setText( "Assigned");
			tv_assign_count.setText( "0");
			tv_resolve_ticket.setText( "Resolved");
			tv_resolve_count.setText( "0");
			tv_close_ticket.setText( "Closed");
			tv_close_count.setText( "0");*/
		}
	}

	public void nextActivity(String statusTkt){
		Intent i = new Intent(TicketSearch.this,ExistingTicketsList.class);
		i.putExtra("final_site_id",final_site_id);
		i.putExtra("final_status",statusTkt);
		i.putExtra("final_alarmDescId",final_alarmDescId);
		i.putExtra("final_severity",final_severity);
		i.putExtra("final_from",final_from);
		i.putExtra("final_to",final_to);
		i.putExtra("final_criteria",final_criteria);
		i.putExtra("final_tktTypeId",final_tktTypeId);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		AppPreferences updateMAppPreferences = new AppPreferences(TicketSearch.this);
		if(updateMAppPreferences.getTTupdateStatus()==1) {
			searchTicket("0");
		}
		updateMAppPreferences.setTTupdateStatus(0);
		super.onResume();
	}
}
