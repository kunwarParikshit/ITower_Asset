package com.isl.energy;
/* Modified By : Dhakan Lal Sharma
   Modified On : 09-Nov-2016
   Version     : 0.1
   Purpose     : Enh 25693*/
import infozech.itower.R;
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
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.energy.filling.FillingTransactionInput;
import com.isl.energy.withdrawal.FuelPurchaseGridRPT;
import com.isl.itower.GPSTracker;
import com.isl.itower.HomeActivity;
import com.isl.modal.BeanAddNotification;
import com.isl.modal.EnergyMetaList;
import com.isl.modal.LocationList;
import com.isl.taskform.ActivityTaskForm;
import com.isl.util.Utils;

public class EnergyManagement extends Activity {
	TextView tv_brand_logo,tv_fuel_purchase,tv_fuel_filling,tv_last_trans;//0.1
	AppPreferences mAppPreferences;
	GPSTracker gps;
	String latitude,longitude;
	DataBaseHelper dbHelper;
	BeanAddNotification temp;
	String moduleUrl = "";
	String url = "";
	Button bt_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.energy_management);
		init();

		if(!dataType().equalsIgnoreCase("")){
		  if (Utils.isNetworkAvailable(EnergyManagement.this)) {
			    new EnergyMetaDataTask(EnergyManagement.this).execute();
		   }else{
				//No internet connection.Please download meta data
				Utils.toast(EnergyManagement.this, "67");
			    Intent i=new Intent(EnergyManagement.this,HomeActivity.class);
				startActivity(i);
				finish();
			}
		} 
		getMenuRights();
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(EnergyManagement.this,HomeActivity.class);
				startActivity(i);
				finish();
			}
		});


		tv_fuel_purchase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Intent i = new Intent(EnergyManagement.this,FuelFillingActivity.class);
				//startActivity(i);
				if(validate()){
					if(!locationDataType().equalsIgnoreCase("")){
						if (Utils.isNetworkAvailable(EnergyManagement.this)) {
							new LocationTask(EnergyManagement.this).execute();
						}else{
							//No internet connection.Please download meta data
							Utils.toast(EnergyManagement.this, "67");
						}
					}else{
						Intent i=new Intent(EnergyManagement.this,FuelPurchaseGridRPT.class);
						startActivity(i);
					}
				}
			}
		});
		
		tv_fuel_filling.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(validate()){
				//Intent i = new Intent(EnergyManagement.this,FuelFillingActivity.class);
				//startActivity(i);
				nextActivity( "0",null );

			 }
			}
		});
		
		tv_last_trans.setOnClickListener(new OnClickListener() { //0.1
		@Override
		public void onClick(View arg0) {
			Intent i=new Intent(EnergyManagement.this,FillingTransactionInput.class);
		   i.putExtra("flag", "S");
		   startActivity(i);

		 }
		});
	   }



	public void getMenuRights() {
		String purchase_right,filling_right,last_filling_right; 
		DataBaseHelper dbHelper = new DataBaseHelper(EnergyManagement.this);
		dbHelper.open();
		purchase_right = dbHelper.getSubMenuRight("FuelPurchase","Energy");
		filling_right = dbHelper.getSubMenuRight("FuelFilling","Energy");
		last_filling_right = dbHelper.getSubMenuRight("LastFillingTrans","Energy"); //0.1
		dbHelper.close();
		if(!purchase_right.contains( "V" ) && !filling_right.contains( "V" ) && !last_filling_right.contains( "V" )){
			//Toast.makeText(EnergyManagement.this, "You are not authorized for menus.",Toast.LENGTH_SHORT).show();	
			Utils.toast(EnergyManagement.this, "69");
			Intent j=new Intent(EnergyManagement.this,HomeActivity.class);
			j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(j);
			finish();
		}else{
			if(purchase_right.contains( "V" )){
				 tv_fuel_purchase.setVisibility(View.VISIBLE);
			}else{
				 tv_fuel_purchase.setVisibility(View.GONE);
			}
			
            if(filling_right.contains( "V" )){
            	tv_fuel_filling.setVisibility(View.VISIBLE);
			}else{
				tv_fuel_filling.setVisibility(View.GONE);
			}
            
            if(last_filling_right.contains( "V" )){ //0.1
            	tv_last_trans.setVisibility(View.VISIBLE);
            }else{
            	tv_last_trans.setVisibility(View.GONE);
            }
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
			url=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
		}else{
			url=moduleUrl+WebMethods.url_GetMetadata;
		}
		String response = Utils.httpPostRequest(con,url, nameValuePairs);
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
		if (pd !=null && pd.isShowing()) {
		pd.dismiss();
		}
		if ((dataResponse == null)) {
		//Toast.makeText(EnergyManagement.this,"Meta data not provided by server.",Toast.LENGTH_LONG).show();	
		Utils.toast(EnergyManagement.this, "70");
	    }else if (dataResponse!=null){
			DataBaseHelper dbHelper = new DataBaseHelper(EnergyManagement.this);
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
		}else{
		//Toast.makeText(EnergyManagement.this, "Server Not Available",Toast.LENGTH_LONG).show();
		Utils.toast(EnergyManagement.this, "13");
		}
		super.onPostExecute(result);
		}
	    }
		
	public class LocationTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		LocationList dataResponse;
		public LocationTask(Context con) {
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
		nameValuePairs.add(new BasicNameValuePair("countryId", ""));
		nameValuePairs.add(new BasicNameValuePair("hubId", ""));
		nameValuePairs.add(new BasicNameValuePair("regionId", ""));
		nameValuePairs.add(new BasicNameValuePair("circleId", ""));
		nameValuePairs.add(new BasicNameValuePair("zoneId", ""));
		nameValuePairs.add(new BasicNameValuePair("clusterId",""));
		if(moduleUrl.equalsIgnoreCase("0")){
			url=mAppPreferences.getConfigIP()+WebMethods.url_getlocation;
		}else{
			url=moduleUrl+WebMethods.url_getlocation;
		}
		/*url = (moduleUrl.isEmpty()) ? Constants.url_getlocation
				:  "http://" + moduleUrl + "/Service.asmx/GetLocationData";*/
		String response = Utils.httpPostRequest(con,url, nameValuePairs);
		Gson gson = new Gson();
		try {
		dataResponse = gson.fromJson(response, LocationList.class);
		} catch (Exception e) {
			String s=e.getMessage();
		   dataResponse = null;
		}
		return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		if (pd !=null && pd.isShowing()) {
		pd.dismiss();
		}
		if ((dataResponse==null)) {
		//Toast.makeText(EnergyManagement.this,"Meta data not provided by server.",Toast.LENGTH_LONG).show();
		Utils.toast(EnergyManagement.this, "70");
		}else if (dataResponse.getLocationList()!=null && dataResponse.getLocationList().size() > 0){
			dbHelper.clearLocationData();
			dbHelper.insertLocationData(dataResponse.getLocationList());
			dbHelper.dataTS(null, null,"18",dbHelper.getLoginTimeStmp("18","0"),2,"0");
			//Intent i = new Intent(EnergyManagement.this,FuelPurchaseActivity.class);
			//startActivity(i);
			Intent i = new Intent( EnergyManagement.this, FuelPurchaseGridRPT.class );
			startActivity(i);
		}else{
		//Toast.makeText(EnergyManagement.this, "Server Not Available",Toast.LENGTH_LONG).show();
		  Utils.toast(EnergyManagement.this, "13");
		}
		super.onPostExecute(result);
		}
	   }
	
	public String dataType(){
		//for Param	 
			 String DataType_Str="1";
		 	 String i=Utils.CompareDates(dbHelper.getSaveTimeStmpEner("10"),dbHelper.getLoginTimeStmp("10","0"),"10");
		//for Operator		 
		 	String j=Utils.CompareDates(dbHelper.getSaveTimeStmp("13","0"),dbHelper.getLoginTimeStmp("13","0"),"13");
		//for RCA		 
		 	String k=Utils.CompareDates(dbHelper.getSaveTimeStmp("16","0"),dbHelper.getLoginTimeStmp("16","0"),"16");
		//for Equipment		 
		 	String l=Utils.CompareDates(dbHelper.getSaveTimeStmp("17","0"),dbHelper.getLoginTimeStmp("17","0"),"17");
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
		
		public String locationDataType(){
			     String DataType_Str="1";
			 	 String i=Utils.CompareDates(dbHelper.getSaveTimeStmp("18","0"),dbHelper.getLoginTimeStmp("18","0"),"18");
			     if(i!="1"){
					 DataType_Str=i;
				 }if(DataType_Str=="1"){
					 DataType_Str="";
			     }
				 return DataType_Str;
			  }
		
		@Override
		public void onBackPressed() {
			Intent i=new Intent(EnergyManagement.this,HomeActivity.class);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();	
		}

		public void init(){
			mAppPreferences = new AppPreferences(EnergyManagement.this);
			dbHelper = new DataBaseHelper(EnergyManagement.this);
			dbHelper.open();
			moduleUrl = dbHelper.getModuleIP("Energy");

			tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
			tv_fuel_purchase = (TextView) findViewById(R.id.tv_fuel_Purchase);
			tv_fuel_filling = (TextView) findViewById(R.id.tv_fuel_filling);
			tv_last_trans = (TextView) findViewById(R.id.tv_last_trans);
			Utils.msgText(EnergyManagement.this, "147",tv_brand_logo); //set Text Energy Management
			Utils.msgText(EnergyManagement.this, "148",tv_fuel_purchase); //set Text My Fuel Purchase
			Utils.msgText(EnergyManagement.this, "149",tv_fuel_filling); //set Text My Fuel Filling
			Utils.msgText(EnergyManagement.this, "150",tv_last_trans); //set Text Fuel Filling Report

			bt_back = (Button) findViewById(R.id.iv_back);
			Utils.msgButton(EnergyManagement.this,"71",bt_back);
		}

	  private boolean validate(){
		  boolean status = true;
		  gps = new GPSTracker(EnergyManagement.this);
		  if (gps.canGetLocation()) {
			  latitude = String.valueOf(gps.getLatitude());
			  longitude = String.valueOf(gps.getLongitude());
			  if((latitude==null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
					  ||(longitude==null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())){
				  //Wait,Latitude & Longitude is Capturing
				  Utils.toast(EnergyManagement.this, "252");
				  status = false;
			  }
		  }else{
			  gps.showSettingsAlert();
			  status = false;
		  }
		  return status;
		}

	private void nextActivity(String mode,HashMap<String, String> tranData) {

		if(tranData==null){
			tranData = new HashMap<String, String>();
			tranData.put(AppConstants.TASK_STATE_ID_ALIAS,"21");
			tranData.put(AppConstants.IS_EDIT_ALIAS,"1");
		}

		tranData.put(AppConstants.FLAG_ALIAS,mode);
		tranData.put(AppConstants.HEADER_CAPTION_ID,"166");
		tranData.put(AppConstants.OPERATION,"A");
		tranData.put(AppConstants.MODULE,"FF");
		tranData.put(AppConstants.ClassModule,"EM");
		if(tranData.containsKey(AppConstants.SITE_ID_ALIAS)){
			tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,tranData.get(AppConstants.SITE_ID_ALIAS)+"-FF");
		} else{
			tranData.put(AppConstants.IMG_NAME_TEMP_ALIAS,"FF");
		}
		Intent i = new Intent( EnergyManagement.this, ActivityTaskForm.class );
		i.putExtra("sel","EM");
		i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
		startActivity( i );
	  }
    }
