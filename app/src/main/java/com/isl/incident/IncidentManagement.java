package com.isl.incident;

/*Modified by :  Dhakan lal
 Modified On :  18-May-2016.
 Purpose     :  CR# 1.9.1(configurable tt field)
 Version     :  0.1 */
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanCheckListDetails;
import com.isl.util.Utils;
import com.isl.modal.AlarmDescList;
import com.isl.modal.IncidentMetaList;
import com.isl.itower.HomeActivity;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;

public class IncidentManagement extends Activity {
	TextView tv_raise_ticket, tv_my_tickets, tv_search_tickets, tv_brand_logo;
	AppPreferences mAppPreferences;
	String raise_ticket, my_ticket, my_search;// assigned_tab, raised_tab,resolved_tab;
	DataBaseHelper dbHelper;
	String moduleUrl = "";
	//String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trouble_tickets);
		dbHelper = new DataBaseHelper(IncidentManagement.this);
		dbHelper.open();

		mAppPreferences = new AppPreferences(IncidentManagement.this);
		mAppPreferences.setTTChklistMadatory(0);
		tv_raise_ticket = (TextView) findViewById(R.id.tv_raise_ticket);
		tv_my_tickets = (TextView) findViewById(R.id.tv_my_ticket);
		tv_search_tickets = (TextView) findViewById(R.id.tv_search_ticket);
		tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);

		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			moduleUrl = dbHelper.getModuleIP("HealthSafty");
			Utils.msgText(IncidentManagement.this, "526", tv_raise_ticket);
			Utils.msgText(IncidentManagement.this, "527", tv_my_tickets);
			Utils.msgText(IncidentManagement.this, "528", tv_search_tickets);
			Utils.msgText(IncidentManagement.this, "955", tv_brand_logo);
		}else{
			moduleUrl = dbHelper.getModuleIP("Incident");
			Utils.msgText(IncidentManagement.this, "73", tv_raise_ticket);
			Utils.msgText(IncidentManagement.this, "74", tv_my_tickets);
			Utils.msgText(IncidentManagement.this, "75", tv_search_tickets);
			Utils.msgText(IncidentManagement.this, "72", tv_brand_logo);
		}


		if (Utils.isNetworkAvailable(IncidentManagement.this)) {
			//new IncidentMetaDataTask(IncidentManagement.this).execute();
			if (!metaDataType().equalsIgnoreCase("")) {
				new IncidentMetaDataTask(IncidentManagement.this).execute();
			}
			if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")
					&& !pmCheckListDataType().equalsIgnoreCase("")) {
				new GetPMCheckList(IncidentManagement.this).execute();
			}
		}else{
			Utils.toast(IncidentManagement.this, "17");
			Intent i = new Intent(IncidentManagement.this,HomeActivity.class);
			startActivity(i);
			finish();
		}

		/*if (!metaDataType().equalsIgnoreCase("")) {
			if (WorkFlowUtils.isNetworkAvailable(IncidentManagement.this)) {
				new IncidentMetaDataTask(IncidentManagement.this).execute();

			} else {
				// Toast.makeText(IncidentManagement.this,"No internet connection.Please download Meta data.",Toast.LENGTH_LONG).show();
				WorkFlowUtils.toast(IncidentManagement.this, "17");
				Intent i = new Intent(IncidentManagement.this,HomeActivity.class);
				startActivity(i);
				finish();
			}
		}*/



		Button iv_back = (Button) findViewById(R.id.iv_back);
		Utils.msgButton(IncidentManagement.this, "71", iv_back);
		if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
			getMenuRights("HealthSafty","New Request",
					"Health and Safty Ticket","Health and Safty Search");
		}else{
			getMenuRights("Incident","RaisedTicket","MyTicket","MySearch");
		}

		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(IncidentManagement.this,HomeActivity.class);
				startActivity(i);
				finish();
			}
		});

		tv_raise_ticket.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAppPreferences.saveRefMode(1);
				if (!alarmDescDataType().equalsIgnoreCase("")) {
					if (Utils.isNetworkAvailable(IncidentManagement.this)) {
						new AlarmDescTask(IncidentManagement.this).execute();
					} else {
						// Toast.makeText(IncidentManagement.this,"No internet connection.Please download meta data.",Toast.LENGTH_LONG).show();
						Utils.toast(IncidentManagement.this, "17");
					}
				} else {
					Intent i = new Intent(IncidentManagement.this,AddTicket.class);
					mAppPreferences.saveRefMode(1);
					startActivity(i);
				}
			}
		});

		tv_search_tickets.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(IncidentManagement.this,TicketSearch.class);
				startActivity(i);
			}
		});

		tv_my_tickets.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Utils.isNetworkAvailable(IncidentManagement.this)) {
						Intent i = new Intent(IncidentManagement.this,MyTickets.class);
						startActivity(i);
				} else
					Utils.toast(IncidentManagement.this, "17");
			}
		});
	}

	public void getMenuRights(String module,String addTT,String tickets,String ttSearch) {
		int flag =0;
		raise_ticket = dbHelper.getSubMenuRight(addTT, module);
		my_ticket = dbHelper.getSubMenuRight(tickets, module);
		my_search = dbHelper.getSubMenuRight(ttSearch, module);

		if(raise_ticket.contains("A")){
			tv_raise_ticket.setVisibility(View.VISIBLE);
			flag = 1;
		}

		if(my_ticket.contains("V")||my_ticket.contains("M")){
			tv_my_tickets.setVisibility(View.VISIBLE);
			flag = 1;
		}
		if(my_search.contains("V")||my_search.contains("M")){
			tv_search_tickets.setVisibility(View.VISIBLE);
			flag = 1;
		}

		if(flag == 0){
			Utils.toast(IncidentManagement.this, "69");
			Intent j = new Intent(IncidentManagement.this, HomeActivity.class);
			j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(j);
			finish();
		}
	}

	public class IncidentMetaDataTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		IncidentMetaList resposnse_Incident_meta = null;
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
				nameValuePairs.add(new BasicNameValuePair("datatype",metaDataType()));
//				nameValuePairs.add(new BasicNameValuePair("datatype","10"));
				nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
				nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
				String url = "";
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
			if ((resposnse_Incident_meta == null)) {
				// Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
				Utils.toast(IncidentManagement.this, "70");
			} else if (resposnse_Incident_meta != null) {
				if (resposnse_Incident_meta.getParam()!=null && resposnse_Incident_meta.getParam().size() > 0) {
					dbHelper.clearInciParamData(mAppPreferences.getTTModuleSelection());
					dbHelper.insertInciParamcData(resposnse_Incident_meta.getParam(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null,null,"10",
							dbHelper.getLoginTimeStmp("10",mAppPreferences.getTTModuleSelection()),2,
							mAppPreferences.getTTModuleSelection());
				}
				if (resposnse_Incident_meta.getOpertors()!=null && resposnse_Incident_meta.getOpertors().size() > 0) {
					dbHelper.clearInciOpcoData(mAppPreferences.getTTModuleSelection());
					dbHelper.insertInciOpcoData(resposnse_Incident_meta.getOpertors(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null, null, "11",dbHelper.getLoginTimeStmp("11",mAppPreferences.getTTModuleSelection()),2,mAppPreferences.getTTModuleSelection());
				}
				if (resposnse_Incident_meta.getRcaCategory()!=null && resposnse_Incident_meta.getRcaCategory().size() > 0) {
					dbHelper.clearInciRcaData(mAppPreferences.getTTModuleSelection());
					dbHelper.insertInciRcaData(resposnse_Incident_meta.getRcaCategory(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null, null, "12",dbHelper.getLoginTimeStmp("12",mAppPreferences.getTTModuleSelection()),2,mAppPreferences.getTTModuleSelection());
				}
				if (resposnse_Incident_meta.getEquipment() !=null && resposnse_Incident_meta.getEquipment().size() > 0) {
					dbHelper.clearInciEqpData(mAppPreferences.getTTModuleSelection());
					dbHelper.insertInciEqpcData(resposnse_Incident_meta.getEquipment(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null, null, "14",dbHelper.getLoginTimeStmp("14",mAppPreferences.getTTModuleSelection()),2,mAppPreferences.getTTModuleSelection());
				}
				if (resposnse_Incident_meta.getGroups() !=null && resposnse_Incident_meta.getGroups().size() > 0) {
					dbHelper.clearIncigrpData(mAppPreferences.getTTModuleSelection());
					dbHelper.insertInciGrpData(resposnse_Incident_meta.getGroups(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null, null, "15",dbHelper.getLoginTimeStmp("15",mAppPreferences.getTTModuleSelection()),2,mAppPreferences.getTTModuleSelection());
				}
				if (resposnse_Incident_meta.getSpareParts() !=null && resposnse_Incident_meta.getSpareParts().size() > 0) {
					dbHelper.clearSparePart();
					dbHelper.insertSparePart(resposnse_Incident_meta.getSpareParts());
					dbHelper.dataTS(null, null, "26",dbHelper.getLoginTimeStmp("26",mAppPreferences.getTTModuleSelection()),2,mAppPreferences.getTTModuleSelection());
				}
				
				if (resposnse_Incident_meta.getUser() !=null && resposnse_Incident_meta.getUser().size() > 0) {
					dbHelper.clearUserContact(mAppPreferences.getTTModuleSelection());
					dbHelper.insertUserContact(resposnse_Incident_meta.getUser(),mAppPreferences.getTTModuleSelection());
					dbHelper.dataTS(null, null, "22",dbHelper.getLoginTimeStmp("22",mAppPreferences.getTTModuleSelection()), 2,mAppPreferences.getTTModuleSelection());
				}
			} else {
				Utils.toast(IncidentManagement.this, "13");
			}

			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	}


	public class AlarmDescTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		AlarmDescList resposnse_alarm = null;
		public AlarmDescTask(Context con) {
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
				nameValuePairs.add(new BasicNameValuePair("alarmId", ""));
				nameValuePairs.add(new BasicNameValuePair("equipId", ""));
				nameValuePairs.add(new BasicNameValuePair("severityId", ""));
				String url = "";
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetCompleteAlarmList;
				}else{
					url=moduleUrl+ WebMethods.url_GetCompleteAlarmList;
				}
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				resposnse_alarm = gson.fromJson(res, AlarmDescList.class);
			} catch (Exception e) {
				e.printStackTrace();
				resposnse_alarm = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if ((resposnse_alarm == null)) {
				// Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
				Utils.toast(IncidentManagement.this, "70");
			} else if (resposnse_alarm.getAlarm_description()!=null && resposnse_alarm.getAlarm_description().size() > 0) {
				dbHelper.clearAlarmDescData(mAppPreferences.getTTModuleSelection());
				dbHelper.insertAlarmDescData(resposnse_alarm.getAlarm_description(),mAppPreferences.getTTModuleSelection());
				dbHelper.dataTS(null, null, "19",
						dbHelper.getLoginTimeStmp("19",mAppPreferences.getTTModuleSelection()), 2,mAppPreferences.getTTModuleSelection());
				Intent i = new Intent(IncidentManagement.this, AddTicket.class);
				mAppPreferences.saveRefMode(1);
				startActivity(i);
			} else {
				// Toast.makeText(IncidentManagement.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				Utils.toast(IncidentManagement.this, "13");
			}

			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

	public String metaDataType() {
		String DataType_Str = "1";
		// for Param
		String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("10",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("10",mAppPreferences.getTTModuleSelection()),"10");
		// for Operator
		String j = Utils.CompareDates(dbHelper.getSaveTimeStmp("11",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("11",mAppPreferences.getTTModuleSelection()),"11");
		// for RCA
		String k = Utils.CompareDates(dbHelper.getSaveTimeStmp("12",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("12",mAppPreferences.getTTModuleSelection()),"12");
		// For Equipment
		String l = Utils.CompareDates(dbHelper.getSaveTimeStmp("14",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("14",mAppPreferences.getTTModuleSelection()),"14");
		// For froup
		String m = Utils.CompareDates(dbHelper.getSaveTimeStmp("15",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("15",mAppPreferences.getTTModuleSelection()),"15");
		// For Spare Part
		String n = Utils.CompareDates(dbHelper.getSaveTimeStmp("26",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("26",mAppPreferences.getTTModuleSelection()),"26");
		// For User Primary Contact
		String o = Utils.CompareDates(dbHelper.getSaveTimeStmp("22",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("22",mAppPreferences.getTTModuleSelection()),"22");
		
		if (i != "1") {
			DataType_Str = i;
		}
		if (j != "1") {
			if (DataType_Str == "1") {
				DataType_Str = j;
			} else {
				DataType_Str = DataType_Str + "," + j;
			}
		}
		if (k != "1") {
			if (DataType_Str == "1") {
				DataType_Str = k;
			} else {
				DataType_Str = DataType_Str + "," + k;
			}
		}

		if (l != "1") {
			if (DataType_Str == "1") {
				DataType_Str = l;
			} else {
				DataType_Str = DataType_Str + "," + l;
			}
		}

		if (m != "1") {
			if (DataType_Str == "1") {
				DataType_Str = m;
			} else {
				DataType_Str = DataType_Str + "," + m;
			}
		}

		if (n != "1") {
			if (DataType_Str == "1") {
				DataType_Str = n;
			} else {
				DataType_Str = DataType_Str + "," + n;
			}
		}

		if (o != "1") {
			if (DataType_Str == "1") {
				DataType_Str = o;
			} else {
				DataType_Str = DataType_Str + "," + o;
			}
		}
		
		if (DataType_Str == "1") {
			DataType_Str = "";
		}
		return DataType_Str;
	}

	public String alarmDescDataType() {
		String DataType_Str = "1";
		String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("19",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("19",mAppPreferences.getTTModuleSelection()),"19");
		if (i != "1") {
			DataType_Str = i;
		}
		if (DataType_Str == "1") {
			DataType_Str = "";
		}
		return DataType_Str;
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(IncidentManagement.this, HomeActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	protected void onResume() {
		mAppPreferences.setTTtabSelection("Assigned");
		super.onResume();
	}

	// Class to call Web Service to get PM CheckList to draw form.
	private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		BeanCheckListDetails PMCheckList;

		private GetPMCheckList(Context con) {
			this.con = con;
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
			String url = "";
			try {
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_getCheckListDetails;
				}else{
					url=moduleUrl+ WebMethods.url_getCheckListDetails;
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
				Utils.toast(IncidentManagement.this, "226" );
			} else if (PMCheckList != null) {
				if (PMCheckList.getPMCheckListDetail()!=null && PMCheckList.getPMCheckListDetail().size() > 0) {
					//DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
					dbHelper.clearCheckListPMForm(mAppPreferences.getTTModuleSelection());
					dbHelper.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),mAppPreferences.getTTModuleSelection(),
							0,"",IncidentManagement.this,"0","0");
					dbHelper.dataTS( null, null, "20",
							dbHelper.getLoginTimeStmp( "20",mAppPreferences.getTTModuleSelection()), 2,mAppPreferences.getTTModuleSelection());
				}
			} else {
				Utils.toast(IncidentManagement.this, "13" );
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			super.onPostExecute( result );
		}
	}

	private String pmCheckListDataType() {
		String DataType_Str="1";
		String i=Utils.CompareDates(dbHelper.getSaveTimeStmp("20",mAppPreferences.getTTModuleSelection()),
				dbHelper.getLoginTimeStmp("20",mAppPreferences.getTTModuleSelection()),"20");
		if(i!="1"){
			DataType_Str=i;
		}if(DataType_Str=="1"){
			DataType_Str="";
		}
		return DataType_Str;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
}