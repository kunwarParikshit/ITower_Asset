package com.isl.sparepart.schedule;
import infozech.itower.R;

import com.isl.constant.WebMethods;
import com.isl.modal.BeanFillingSiteList;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.EnergyMetaList;
import com.isl.taskform.ActivityTaskForm;
import com.isl.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;

public class FillingSchedules extends Activity {
	ListView lv_schedule;
	BeanFillingSiteList response_site_list;
	String get_type;
	AppPreferences mAppPreferences;
	DataBaseHelper Localdb;
	int clickItemPosition;
	TextView txt_no_schedule;
	String moduleUrl = "";
	String serUrl = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filling_schedule_list);
		Localdb = new DataBaseHelper(FillingSchedules.this);
		Localdb.open();
		moduleUrl = Localdb.getModuleIP("Schedule");
		mAppPreferences = new AppPreferences(FillingSchedules.this);
		TextView title = (TextView) findViewById(R.id.tv_brand_logo);
		txt_no_schedule = (TextView) findViewById(R.id.txt_no_schedule);
		title.setTypeface(Utils.typeFace(FillingSchedules.this));
		String type = getIntent().getExtras().getString("type");  
		title.setText(type);
		if (type.equals("Scheduled")) {
			get_type = "SCHEDULED";
		} else if (type.equals("Missed")) {
			get_type = "MISSED";
		} else if (type.equals("Done")) {
			get_type = "DONE";
		}
		Button bt_back = (Button) findViewById(R.id.button_back);
		Utils.msgButton(FillingSchedules.this,"71",bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		lv_schedule = (ListView) findViewById(R.id.lv_schedule);
		if (Utils.isNetworkAvailable(FillingSchedules.this)) {
			new MyFillingSchedulesTask(FillingSchedules.this).execute();
		} else {
			Utils.toast( FillingSchedules.this, "17" );
		}

		DataBaseHelper dbHelper = new DataBaseHelper(FillingSchedules.this);
		dbHelper.open();
		String filling_schedule = dbHelper.getSubMenuRight("MyFillingSchedules", "Schedule");

		lv_schedule.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				clickItemPosition=arg2;
				if(filling_schedule.contains("V")){
					if (get_type.equals("SCHEDULED") || get_type.equals("MISSED")) {
						if(!dataType().equalsIgnoreCase("")
								&& Utils.isNetworkAvailable(FillingSchedules.this)){
							new EnergyMetaDataTask(FillingSchedules.this).execute();
						}else {
							nextActivity( "0", null,
									response_site_list.getSite_list().get( clickItemPosition ).getSITE_ID(),
									response_site_list.getSite_list().get( arg2 ).getSCHEDULE_DATE(),
									response_site_list.getSite_list().get( arg2 ).getDg(),
									response_site_list.getSite_list().get( arg2 ).getPdt(),
									response_site_list.getSite_list().get( arg2 ).getPqty(),
									response_site_list.getSite_list().get( arg2 ).getTktid(),
									response_site_list.getSite_list().get( arg2 ).getTrantyp(),
									response_site_list.getSite_list().get( arg2 ).getTranid(),
									response_site_list.getSite_list().get( arg2 ).getTranname(),
									response_site_list.getSite_list().get( arg2 ).getDgname(),
									response_site_list.getSite_list().get( arg2 ).getTranidnew());
						}
					}
				}
			}
		});
	}

	public class MyFillingSchedulesTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		public MyFillingSchedulesTask(Context con) {
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
				String url = "";
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("userId",mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("roleId",mAppPreferences.getRoleId()));
				nameValuePairs.add(new BasicNameValuePair("status", get_type));

				if(moduleUrl.equalsIgnoreCase("0")){
					serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduledFillingSites;
				}else{
					serUrl=moduleUrl+ WebMethods.url_getScheduledFillingSites;
				}
				String response_site = Utils.httpPostRequest(con,serUrl, nameValuePairs);
				Gson gson = new Gson();
				response_site_list = gson.fromJson(response_site,BeanFillingSiteList.class);
			} catch (Exception e) {
				e.printStackTrace();
				response_site_list = null;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (response_site_list == null)
				{
					Utils.toast(FillingSchedules.this, "13");
					//Toast.makeText(MyFillingSchedules.this,"Server Not Available", Toast.LENGTH_LONG).show();
				}else if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
					lv_schedule.setAdapter(new AdapterFillingSchedule(FillingSchedules.this, response_site_list));
					lv_schedule.setVisibility(View.VISIBLE);
					RelativeLayout rl_no_list = (RelativeLayout) findViewById(R.id.textlayout);
					rl_no_list.setVisibility(View.INVISIBLE);
				}else {
					finish();
					Utils.toast(FillingSchedules.this, "225");
					//Toast.makeText(MyFillingSchedules.this,"No Activity Found", Toast.LENGTH_LONG).show();
					lv_schedule.setVisibility(View.INVISIBLE);
					RelativeLayout rl_no_list = (RelativeLayout) findViewById(R.id.textlayout);
					rl_no_list.setVisibility(View.VISIBLE);
					Utils.msgText(FillingSchedules.this,"225",txt_no_schedule);
				}
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}
	
	public class EnergyMetaDataTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		EnergyMetaList dataResponse;
		public EnergyMetaDataTask(Context con) {
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
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("module","Energy"));
		nameValuePairs.add(new BasicNameValuePair("datatype",dataType()));
		nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
		nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
		nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
		if(moduleUrl.equalsIgnoreCase("0")){
				serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
		}else{
		        serUrl=moduleUrl+ WebMethods.url_GetMetadata;
		}
		/*serUrl = (moduleUrl.isEmpty()) ? Constants.url_GetMetadata
				:  "http://" + moduleUrl + "/Service.asmx/GetMetadata";*/
		String response = Utils.httpPostRequest(con,serUrl, nameValuePairs);
		Gson gson = new Gson();
		try {
		dataResponse = gson.fromJson(response, EnergyMetaList.class);
		} catch (Exception e) {
		e.printStackTrace();
		dataResponse = null;
		}
		return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		if (dataResponse == null) {
		Utils.toast(FillingSchedules.this, "70");
		//Toast.makeText(MyFillingSchedules.this,"Energy meta data not provided by server",Toast.LENGTH_LONG).show();	
		}else if (dataResponse!=null){
			DataBaseHelper dbHelper = new DataBaseHelper(FillingSchedules.this);
			dbHelper.open();
			if(dataResponse.getParam()!=null && dataResponse.getParam().size()>0){
			dbHelper.clearEnergyParams();	
			dbHelper.insertEnergyParam(dataResponse.getParam());
			dbHelper.dataTS(null, null,"10",dbHelper.getLoginTimeStmp("10","0"),3,"0");
			}
			if(dataResponse.getFuelsuppliers()!=null && dataResponse.getFuelsuppliers().size()>0){
            dbHelper.clearSupplier();	
            dbHelper.insertIntoSupplier(dataResponse.getFuelsuppliers());
            dbHelper.dataTS(null, null,"13",dbHelper.getLoginTimeStmp("13","0"),2,"0");
			}
			if(dataResponse.getVendors()!=null && dataResponse.getVendors().size()>0){
            dbHelper.clearVender();	
           	dbHelper.insertEnergyVender(dataResponse.getVendors());
           	dbHelper.dataTS(null, null,"16",dbHelper.getLoginTimeStmp("16","0"),2,"0");
			}
            if(dataResponse.getDgtype()!=null && dataResponse.getDgtype().size()>0){
            dbHelper.clearEnergyDg();	
            dbHelper.insertEnergyDG(dataResponse.getDgtype());	
            dbHelper.dataTS(null, null,"17",dbHelper.getLoginTimeStmp("17","0"),2,"0");
			}
            dbHelper.close();
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}

			nextActivity( "0",null,
					response_site_list.getSite_list().get(clickItemPosition).getSITE_ID(),
					response_site_list.getSite_list().get(clickItemPosition).getSCHEDULE_DATE(),
					response_site_list.getSite_list().get(clickItemPosition).getDg(),
					response_site_list.getSite_list().get(clickItemPosition).getPdt(),
					response_site_list.getSite_list().get(clickItemPosition).getPqty(),
					response_site_list.getSite_list().get(clickItemPosition).getTktid(),
					response_site_list.getSite_list().get(clickItemPosition).getTrantyp(),
					response_site_list.getSite_list().get(clickItemPosition).getTranid(),
					response_site_list.getSite_list().get(clickItemPosition).getTranname(),
					response_site_list.getSite_list().get(clickItemPosition).getDgname(),
					response_site_list.getSite_list().get(clickItemPosition).getTranidnew());
		}else{
			  Utils.toast(FillingSchedules.this, "13");
			//Toast.makeText(MyFillingSchedules.this, "Server Not Available",Toast.LENGTH_LONG).show();	
		}
		super.onPostExecute(result);
		}
	    }
	
	public String dataType(){
		//for Param	 
			 String DataType_Str="1";
		 	 String i=Utils.CompareDates(Localdb.getSaveTimeStmpEner("10"),Localdb.getLoginTimeStmp("10","0"),"10");
		//for Operator		 
		 	String j=Utils.CompareDates(Localdb.getSaveTimeStmp("13","0"),Localdb.getLoginTimeStmp("13","0"),"13");
		//for RCA		 
		 	String k=Utils.CompareDates(Localdb.getSaveTimeStmp("16","0"),Localdb.getLoginTimeStmp("16","0"),"16");
		//For Equipment		 
		 	String l=Utils.CompareDates(Localdb.getSaveTimeStmp("17","0"),Localdb.getLoginTimeStmp("17","0"),"17");
		     if(i!="1"){
				 DataType_Str=i;
			 }
			 if(j!="1"){
				 if(DataType_Str =="1"){
					 DataType_Str=j;
				 }else{
					 DataType_Str=DataType_Str+","+j;
				 }
			 }
			 if(k!="1"){
				 if(DataType_Str ==""){
					 DataType_Str=k;
				 }else{
					 DataType_Str=DataType_Str+","+k; 
				 }
			 }
			 
			 if(l!="1"){
				 if(DataType_Str =="1"){
				 DataType_Str=l;
			 }else{
				 DataType_Str=DataType_Str+","+l;
			    }
			 }
			 		 
			 if(DataType_Str=="1"){
				  DataType_Str="";
		     }
			 return DataType_Str;
		  }

	@Override
	protected void onResume() {
		super.onResume();
		if (Utils.isNetworkAvailable(FillingSchedules.this)) {
			new MyFillingSchedulesTask(FillingSchedules.this).execute();
		} else {
			Utils.toast( FillingSchedules.this, "17" );
		}
	}

	private void nextActivity(String mode, HashMap<String, String> tranData, String sid,
							  String date, String dg, String pdt, String pqty,
							  String tktid, String trantyp, String trainid, String tranname, String dgname, String tranidnew) {

		if(tranData==null){
			tranData = new HashMap<String, String>();
			tranData.put(AppConstants.TASK_STATE_ID_ALIAS,"21");
			tranData.put(AppConstants.IS_EDIT_ALIAS,"1");
		}

		tranData.put(AppConstants.FLAG_ALIAS,mode);
		tranData.put(AppConstants.HEADER_CAPTION_ID,"166");
		tranData.put(AppConstants.OPERATION,"E");
		tranData.put(AppConstants.MODULE,"FF");
		tranData.put(AppConstants.SITE_ID_ALIAS,sid);
		tranData.put(AppConstants.FILLING_DATE_KEY ,Utils.dateMMM());

		tranData.put(AppConstants.DG ,dg);
		tranData.put(AppConstants.PDT ,pdt);
		tranData.put(AppConstants.PQTY ,pqty);
		tranData.put(AppConstants.TKTID ,tktid);
		tranData.put(AppConstants.TRANTYPE ,trantyp);
		tranData.put(AppConstants.TRANSID ,tranidnew);
		tranData.put(AppConstants.FWO ,trainid);
		tranData.put(AppConstants.TRANNAME ,tranname);
		tranData.put(AppConstants.trantypeFF ,trantyp);
		tranData.put(AppConstants.ClassModule ,"FS");


		if(tranData.containsKey(AppConstants.SITE_ID_ALIAS)){
			tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,tranData.get(AppConstants.SITE_ID_ALIAS)+"-FF");
		} else{
			tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,"FF");
		}

		Intent i = new Intent( FillingSchedules.this, ActivityTaskForm.class );
		i.putExtra("sel","FS");
		i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
		startActivity( i );
	}
	
}
