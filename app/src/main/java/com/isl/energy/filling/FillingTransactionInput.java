package com.isl.energy.filling;
/*
 Modified By : Dheeraj Paliwal
 Modified On : 23-Dec-2016
 Version     : 0.1
 Purpose     : Enh# 26279
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanLastFillingTransList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import infozech.itower.R;

public class FillingTransactionInput extends Activity {
	Button bt_back, bt_submit;
	EditText et_site_id;
	TextView tv_site_id, tv_tank, from, to, tv_from_Date, tv_to_Date, tv_age,
			tv_brand_logo;
	Spinner spinner_tank, spinner_age;
	LinearLayout ll_age;
	RelativeLayout rl_age;
	AppPreferences mAppPreferences;
	String flag, dgType, pre_siteId, pre_dgType, age;
	BeanLastFillingTransList fillingReportList;
	DataBaseHelper db;
	static final int FROM_DATE_DIALOG_ID = 0;
	static final int TO_DATE_DIALOG_ID = 1;
	private int pYear, pMonth, pDay;
	Date d1, d2;
	String moduleUrl = "";
	String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.last_filling_transaction);
		getId();
		init();
		db.open();
		moduleUrl = db.getModuleIP("Energy");
		addItemsOnSpinner(spinner_tank, db.getEnergyDgDesc());
		//setupUI(spinner_tank);
		int pos = getCategoryPos(pre_dgType, db.getEnergyDgDesc());
		spinner_tank.setSelection(pos);
		spinner_tank.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				dgType = db.getAssetDgId(spinner_tank.getSelectedItem()
						.toString().trim());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		spinner_age.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (spinner_age.getSelectedItem().toString()
						.equalsIgnoreCase("Date")) {
					ll_age.setVisibility(View.VISIBLE);
				} else {
					ll_age.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		tv_from_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(FROM_DATE_DIALOG_ID);
			}
		});

		tv_to_Date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(TO_DATE_DIALOG_ID);
			}
		});

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (flag.equalsIgnoreCase("A")) {
					age = "Last Transaction";
				} else {
					age = spinner_age.getSelectedItem().toString().trim();
				}
				if (Utils.isNetworkAvailable( FillingTransactionInput.this)) {
					if (validate()) {
						new FillingReport( FillingTransactionInput.this,
								et_site_id.getText().toString().trim(), dgType,
								age, tv_from_Date.getText().toString().trim(),
								tv_to_Date.getText().toString().trim()).execute();
					}
				} else {
					// Toast.makeText(LastFillingTrans.this,
					// "No Internet Connection",Toast.LENGTH_SHORT).show();
					Utils.toast( FillingTransactionInput.this, "17");
				}
			}
		});
	}

	public void init() {
		final Calendar cal = Calendar.getInstance();
		pYear = cal.get(Calendar.YEAR);
		pMonth = cal.get(Calendar.MONTH);
		pDay = cal.get(Calendar.DAY_OF_MONTH);
		db = new DataBaseHelper( FillingTransactionInput.this);
		mAppPreferences = new AppPreferences( FillingTransactionInput.this);
		flag = getIntent().getExtras().getString("flag");
		pre_siteId = getIntent().getExtras().getString("sid");
		pre_dgType = getIntent().getExtras().getString("dg");
		et_site_id.setText(pre_siteId);
		if (flag.equalsIgnoreCase("A")) {
			rl_age.setVisibility(View.GONE);
			tv_age.setVisibility(View.GONE);
			// tv_brand_logo.setText("    Last Filling Transaction");
			Utils.msgText( FillingTransactionInput.this, "167", tv_brand_logo);
		} else {
			rl_age.setVisibility(View.VISIBLE);
			tv_age.setVisibility(View.VISIBLE);
			// tv_brand_logo.setText("    Fuel Filling Report");
			Utils.msgText( FillingTransactionInput.this, "150", tv_brand_logo); // set Fuel Filling Report
		}
		ArrayList<String> list_age = new ArrayList<String>();
		list_age.add("Last Transaction");
		list_age.add("Transaction in Last 5 days");
		list_age.add("Transaction in Last 10 days");
		list_age.add("Date");
		addItemsOnSpinner(spinner_age, list_age);
	}

	public void getId() {
		ll_age = (LinearLayout) findViewById( R.id.ll_age);
		bt_back = (Button) findViewById( R.id.bt_back);
		Utils.msgButton( FillingTransactionInput.this, "71", bt_back);
		bt_submit = (Button) findViewById( R.id.bt_submit);
		et_site_id = (EditText) findViewById( R.id.et_site_id);
		spinner_tank = (Spinner) findViewById( R.id.spinner_tank);
		spinner_age = (Spinner) findViewById( R.id.spinner_age);
		spinner_tank.setBackgroundResource( R.drawable.input_box );
		spinner_age.setBackgroundResource( R.drawable.input_box );
		tv_from_Date = (TextView) findViewById( R.id.tv_from_Date);
		tv_to_Date = (TextView) findViewById( R.id.tv_to_Date);
		rl_age = (RelativeLayout) findViewById( R.id.rl_age);
		tv_age = (TextView) findViewById( R.id.tv_age);
		tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo);
		tv_site_id = (TextView) findViewById( R.id.tv_site_id);
		tv_tank = (TextView) findViewById( R.id.tv_tank);
		from = (TextView) findViewById( R.id.from);
		to = (TextView) findViewById( R.id.to);

		Utils.msgButton( FillingTransactionInput.this, "115", bt_submit);
		Utils.msgText( FillingTransactionInput.this, "77", tv_site_id);
		Utils.msgText( FillingTransactionInput.this, "168", tv_tank);
		Utils.msgText( FillingTransactionInput.this, "143", tv_age);
		Utils.msgText( FillingTransactionInput.this, "245", from);
		Utils.msgText( FillingTransactionInput.this, "246", to);
	}

	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_text, list);
		dataAdapter.setDropDownViewResource( R.layout.spinner_dropdown);
		spinner.setAdapter(dataAdapter);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FROM_DATE_DIALOG_ID:
			return new DatePickerDialog(this, fromDateSetListener, pYear,
					pMonth, pDay);
		case TO_DATE_DIALOG_ID:
			return new DatePickerDialog(this, toDateSetListener, pYear, pMonth,
					pDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			pYear = year;
			pMonth = monthOfYear;
			pDay = dayOfMonth;
			d1 = Utils.convertStringToDate(
					new StringBuilder().append(pMonth + 1).append("/")
							.append(pDay).append("/").append(pYear).toString(),
					"MM/dd/yyyy");
			if (Utils.checkValidation(d1)) {
				tv_from_Date.setText( Utils.changeDateFormat(new StringBuilder()
						.append(pMonth + 1).append("/").append(pDay)
						.append("/").append(pYear).toString(), "MM/dd/yyyy",
						"dd-MMM-yyyy"));
			} else {
				Utils.toast( FillingTransactionInput.this, "206");
				// Toast.makeText(LastFillingTrans.this,"From Date cannot be greater than Current Date",Toast.LENGTH_LONG).show();
			}
		}
	};

	private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			pYear = year;
			pMonth = monthOfYear;
			pDay = dayOfMonth;
			d2 = Utils.convertStringToDate(
					new StringBuilder().append(pMonth + 1).append("/")
							.append(pDay).append("/").append(pYear).toString(),
					"MM/dd/yyyy");
			if (Utils.checkValidation(d2)) {
				tv_to_Date.setText( Utils.changeDateFormat(new StringBuilder()
						.append(pMonth + 1).append("/").append(pDay)
						.append("/").append(pYear).toString(), "MM/dd/yyyy",
						"dd-MMM-yyyy"));
			} else {
				Utils.toast( FillingTransactionInput.this, "207");
				// Toast.makeText(LastFillingTrans.this,"To Date cannot be greater than Current Date",Toast.LENGTH_LONG).show();
			}
		}
	};

	private int getCategoryPos(String category, ArrayList<String> list) {
		return list.indexOf(category);
	}

	private boolean validate() {
		boolean status = true;
		if (et_site_id.getText().toString().trim().length() == 0) {
			et_site_id.clearFocus();
			et_site_id.requestFocus();
			status = false;
			Utils.toast( FillingTransactionInput.this, "152");
			// Toast.makeText(LastFillingTrans.this,"Site Id cannot be blank.",
			// Toast.LENGTH_SHORT).show();
		} else if (spinner_tank.getSelectedItem().toString().length() == 0) {
			Utils.toast( FillingTransactionInput.this, "259");
			// Toast.makeText(LastFillingTrans.this,"Select Genset No.",Toast.LENGTH_SHORT).show();
		} else if (spinner_age.getSelectedItem().toString()
				.equalsIgnoreCase("Date")
				&& tv_from_Date.getText().toString().trim().length() == 0) {
			status = false;
			Utils.toast( FillingTransactionInput.this, "209");
			// Toast.makeText(LastFillingTrans.this,"From Date cannot be blank.",Toast.LENGTH_SHORT).show();
		} else if (spinner_age.getSelectedItem().toString()
				.equalsIgnoreCase("Date")
				&& tv_to_Date.getText().toString().trim().length() == 0) {
			status = false;
			Utils.toast( FillingTransactionInput.this, "210");
			// Toast.makeText(LastFillingTrans.this,"To Date cannot be blank.",Toast.LENGTH_SHORT).show();
		} else if (spinner_age.getSelectedItem().toString()
				.equalsIgnoreCase("Date")
				&& tv_from_Date.getText().toString().trim().length() != 0
				&& tv_to_Date.getText().toString().trim().length() != 0) {
			if (Utils.checkDateCompare(d1, d2)) {
				status = true;
			} else {
				Utils.toast( FillingTransactionInput.this, "211");
				// Toast.makeText(LastFillingTrans.this,"From Date cannot be greater than To Date",Toast.LENGTH_LONG).show();
				status = false;
			}
		}
		return status;
	}

	public class FillingReport extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		String res;
		Context con;
		String site_id;
		String dg;
		String age;
		String fDate;
		String tDate;

		public FillingReport(Context con, String site_id, String dgType,
				String age, String fDate, String tDate) {
			this.con = con;
			this.site_id = site_id;
			this.dg = dgType;
			this.age = age;
			this.fDate = fDate;
			this.tDate = tDate;

		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("userID",	mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("siteID", site_id));
				nameValuePairs.add(new BasicNameValuePair("dgType", dg));
				nameValuePairs.add(new BasicNameValuePair("age", age));
				nameValuePairs.add(new BasicNameValuePair("fromDate", fDate));
				nameValuePairs.add(new BasicNameValuePair("toDate", tDate));
				nameValuePairs.add(new BasicNameValuePair("addParam", ""));
				nameValuePairs.add(new BasicNameValuePair("flag", "S"));
				//nameValuePairs.add(new BasicNameValuePair("flag", flag));
				nameValuePairs.add(new BasicNameValuePair("languageCode", "" + mAppPreferences.getLanCode()));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetFillingReport;
				}else{
					url=moduleUrl+ WebMethods.url_GetFillingReport;
				}
				res = Utils.httpPostRequest(con,url, nameValuePairs);
				Gson gson = new Gson();
				fillingReportList = gson.fromJson(res, BeanLastFillingTransList.class);
			} catch (Exception e) {
				e.printStackTrace();
				fillingReportList = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}

			if ((fillingReportList == null)) {
				Utils.toast( FillingTransactionInput.this, "13");
			} else if (fillingReportList.getFillingReportList()!=null && fillingReportList.getFillingReportList().size() > 0
					&& fillingReportList.getFillingReportList().get(0)
							.getFLAG().equalsIgnoreCase("S")) {
				if (flag.equalsIgnoreCase("A")) {
					Intent i = new Intent( FillingTransactionInput.this, FillingTransactionDetails.class);
					i.putExtra("res", res);
					i.putExtra("flag", flag);
					i.putExtra("pos", 0);
					startActivity(i);
				} else {
					Intent i = new Intent( FillingTransactionInput.this, FillingTransactions.class);
					i.putExtra("res", res);
					i.putExtra("flag", flag);
					startActivity(i);
				}

			} else if (fillingReportList.getFillingReportList()!=null && fillingReportList.getFillingReportList().size() > 0
					&& fillingReportList.getFillingReportList().get(0)
							.getFLAG().equalsIgnoreCase("F")) {
				Utils.toastMsg( FillingTransactionInput.this, fillingReportList
						.getFillingReportList().get(0).getMSG());
			} else {
				// Toast.makeText(LastFillingTrans.this,"Report Not Found",Toast.LENGTH_LONG).show();
				Utils.toast( FillingTransactionInput.this, "212");
			}
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}