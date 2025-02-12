package com.isl.user.tracking;
import com.isl.constant.WebMethods;
import com.isl.itower.GPSTracker;
import com.isl.itower.PasswardExpire;
import com.isl.itower.PasswardExpired;
import com.isl.itower.SessionExpired;
import com.isl.itower.Version;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.ResponseGetUserInfo;
import com.isl.modal.ResponseUserInfoList;
import com.isl.util.Utils;
import com.isl.modal.ServiceResponce;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.content.PermissionChecker;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

public class GetUserInfoService extends Service {
	AppPreferences mAppPreferences;
	ResponseUserInfoList responselist;
	static int BatSta;
	int a;
	DataBaseHelper dbh1;
	public static boolean isServiceRunning = false;
	int flag=0;
	PackageInfo pInfo = null;
	String[] dataTypeID, timeStamp;
	String[] tmpDataTS = new String[2];
	public GetUserInfoService() {
	}


	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		//Toast.makeText(this, "The local Service was Created",Toast.LENGTH_LONG).show();
		//gps = new GPSTracker(GetUserInfoService.this);
		/*if (gps.canGetLocation() == false) {
			status = false;
			gps.showSettingsAlert();
		}*/
	}

	public static BroadcastReceiver battInfoRec = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			BatSta = level;
		}
	};
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		//Toast.makeText(this, "swiped app", Toast.LENGTH_LONG).show();
		//unregister listeners
		//do any other cleanup if required
		//System.out.println("===1111133335555");
		//stop service
		//stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(this, "local Service Started", Toast.LENGTH_LONG).show();
		mAppPreferences = new AppPreferences(GetUserInfoService.this);
		isServiceRunning = true;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		callWebService();
		/*if(WorkFlowUtils.isSchedularInfo(GetUserInfoService.this)==1){
			if(WorkFlowUtils.checkSetting(GetUserInfoService.this)==1){
				Intent userInfo = new Intent(GetUserInfoService.this,LocationOffOnService.class);
				GetUserInfoService.this.startService(userInfo);
			}else{
				if (WorkFlowUtils.isSchedularSave(GetUserInfoService.this)==1){
					callWebService();
				}
			}
		}*/
		if (Utils.isNetworkAvailable(GetUserInfoService.this)) {
			new Getversion(GetUserInfoService.this).execute();
		}
		stopSelf();
		return Service.START_STICKY;

	}

	public class saveMobileInfo extends AsyncTask<Void, Void, Void> {
		Context con;
		JSONObject objj;
		JSONObject dataObj;
		String res="";
		DataBaseHelper db2;
		public saveMobileInfo(Context con, JSONObject baseObj) {
			this.con = con;
			this.dataObj = baseObj;
			db2= new DataBaseHelper(GetUserInfoService.this);
			db2.open();
			//Toast.makeText(con, "service call=="+responselist.getDetails().size(), Toast.LENGTH_LONG).show();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				//String moduleUrl = db2.getModuleIP("UserTrackModule");
				//String url = (moduleUrl.isEmpty()) ? WebMethods.url_SaveMobileInfo : "http://" + moduleUrl + "/data-adaptor/bulkPushData";
				String moduleUrl = "101.53.139.74:9001";
				String url ="http://" + moduleUrl + "/data-adaptor/bulkPushData";
				res = Utils.postJson(url,dataObj.toString());
				objj = new JSONObject(res);
				if (objj.getString("status").equalsIgnoreCase("S")) {
					db2.deleteMobileInfo();
					db2.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
				//System.out.println("Exception : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(res.length()!=0){
				try {
					if (objj.getString("status").equalsIgnoreCase("S")) {
						//Toast.makeText(GetUserInfoService.this,
						//		"status="+objj.getString("status").toString(),
						//		Toast.LENGTH_LONG).show();
					} else {
						//Toast.makeText(GetUserInfoService.this,
						//		"message="+objj.getString("errorMessage").toString(),
						//		Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//Toast.makeText(GetUserInfoService.this,
				//		"message test",
				//		Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onDestroy() {
		isServiceRunning = false;

	}

	public void callWebService(){
		a=0;
		//insetValues();

		//Toast.makeText(this, "callWebService", Toast.LENGTH_LONG).show();
		//Calendar calendar = Calendar.getInstance();
		mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
		//mAppPreferences.setUserTrackUploadTime(calendar.getTimeInMillis());
		String latitude, longitude;
		String autoTime;
		String netType = "";
		GPSTracker gps;
		String iemi = "";
		String gpsStaus;
		final ResponseGetUserInfo response;
		//System.out.println("6666===");
		if (Utils.isAutoDateTime(this)) {
			autoTime = "false";
		} else {
			autoTime = "true";
		}
		this.registerReceiver(battInfoRec, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo Info = cm.getActiveNetworkInfo();
		if (Info == null || !Info.isConnectedOrConnecting()) {
			netType = "No Connection";
		} else {
			int netMode = Info.getType();
			if (netMode == ConnectivityManager.TYPE_WIFI) {
				netType = "Wifi Connection";
			} else if (netMode == ConnectivityManager.TYPE_MOBILE) {
				netType = "GPRS Connection";
			}
		}

		//TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		//iemi = telephonyManager.getDeviceId();


		int permission = PermissionChecker.checkSelfPermission(GetUserInfoService.this, Manifest.permission.READ_PHONE_STATE);
		if (permission == PermissionChecker.PERMISSION_GRANTED) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if(Build.VERSION.SDK_INT<28) {
				iemi = telephonyManager.getDeviceId();
			}else{
				iemi = "";
			}
		}

		gps = new GPSTracker(GetUserInfoService.this);
		if (gps.canGetLocation() == false) {
			gpsStaus = "Off";
		}else{
			gpsStaus = "On";
		}
		latitude = String.valueOf(gps.getLatitude());
		longitude = String.valueOf(gps.getLongitude());
		if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
				|| (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
			latitude="";
			longitude="";
		}
		response = new ResponseGetUserInfo();
		response.setLoginID(mAppPreferences.getLoginId());
		response.setTimeStamp(Utils.CurrentDateTime());
		response.setNetworkCheck(netType);//network type
		response.setLat(latitude);
		response.setLongt(longitude);
		response.setBatteryStatus(BatSta);
		response.setAutoTime(autoTime);
		response.setImei(iemi);
		response.setGps(gpsStaus);
		response.setMock(mAppPreferences.getAppNameMockLocation()+"~"+gps.isMockLocation());
		SignalChecker SC= new SignalChecker(GetUserInfoService.this,new SignalChecker.OnSignalReceiver() {
			@Override
			public void onRecived(int signal) {
				response.setSignal(signal);
				DataBaseHelper dbh = new DataBaseHelper(GetUserInfoService.this);
				dbh.open();
				dbh.insertMobileInfo(response);
				a=1;
				dbh.close();
				//System.out.println("7777===");

			}
		});
		SC.checkAndUpdate();
		if(a==0){
			DataBaseHelper dbhh = new DataBaseHelper(GetUserInfoService.this);
			dbhh.open();
			dbhh.insertMobileInfo(response);
			dbhh.close();
		}
		/*if(WorkFlowUtils.isNetworkAvailable(GetUserInfoService.this)){

		}else{
			DataBaseHelper dbhh = new DataBaseHelper(GetUserInfoService.this);
			dbhh.open();
			dbhh.insertMobileInfo(response);
			dbhh.close();
			//System.out.println("8888===");

		}*/
		DataBaseHelper db1 = new DataBaseHelper(GetUserInfoService.this);
		db1.open();
		responselist = db1.getMobileInfo();
		db1.close();
		try {
			JSONObject obj2 = new JSONObject();
			obj2.put("id", "1");
			obj2.put("baseId", "UserTracking");
			JSONArray myNewArray = new JSONArray();
			//Toast.makeText(this, "size=="+responselist.getDetails().size(), Toast.LENGTH_LONG).show();
			//System.out.println("==sizedhaaaa=="+responselist.getDetails().size());
			if(responselist.getDetails().size()>0 && Utils.isNetworkAvailable(GetUserInfoService.this)){
				for (int i = 0; i < responselist.getDetails().size(); i++) {
					JSONObject obj = new JSONObject();
					obj.put("trackID", responselist.getDetails().get(i).getID());
					obj.put("loginid", responselist.getDetails().get(i).getLoginID());
					obj.put("lat", responselist.getDetails().get(i).getLat());
					obj.put("long", responselist.getDetails().get(i).getLongt());
					obj.put("autoTime", responselist.getDetails().get(i).getAutoTime());
					obj.put("time", responselist.getDetails().get(i).getTimeStamp());
					obj.put("bat", responselist.getDetails().get(i).getBatteryStatus());
					obj.put("nw", responselist.getDetails().get(i).getNetworkCheck());
					obj.put("sig", responselist.getDetails().get(i).getSignal());
					obj.put("imei", responselist.getDetails().get(i).getImei());
					obj.put("gps", responselist.getDetails().get(i).getGps());
					obj.put("mock", responselist.getDetails().get(i).getMock());
					myNewArray.put(obj);
				}
				obj2.put("dataMapLst", myNewArray);
				//System.out.println("=dataObj dhakkkk=="+obj2.toString());
				new saveMobileInfo(GetUserInfoService.this, obj2).execute();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void insetValues(){
		//Toast.makeText(this, "insert value", Toast.LENGTH_LONG).show();
		Calendar calendar = Calendar.getInstance();
		mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
		//mAppPreferences.setUserTrackUploadTime(calendar.getTimeInMillis());
		String latitude, longitude;
		String autoTime;
		String netType = "";
		GPSTracker gps;
		String iemi = "";
		String gpsStaus;
		final ResponseGetUserInfo response;
		if (Utils.isAutoDateTime(this)) {
			autoTime = "false";
		} else {
			autoTime = "true";
		}
		this.registerReceiver(battInfoRec, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo Info = cm.getActiveNetworkInfo();
		if (Info == null || !Info.isConnectedOrConnecting()) {
			netType = "No Connection";
		} else {
			int netMode = Info.getType();
			if (netMode == ConnectivityManager.TYPE_WIFI) {
				netType = "Wifi Connection";
			} else if (netMode == ConnectivityManager.TYPE_MOBILE) {
				netType = "GPRS Connection";
			}
		}
		//TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		//iemi = telephonyManager.getDeviceId();
		int permission = PermissionChecker.checkSelfPermission(GetUserInfoService.this, Manifest.permission.READ_PHONE_STATE);
		if (permission == PermissionChecker.PERMISSION_GRANTED) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			iemi = telephonyManager.getDeviceId();
		}else{
			iemi ="";
		}
		gps = new GPSTracker(GetUserInfoService.this);
		if (gps.canGetLocation() == false) {
			gpsStaus = "Off";
		}else{
			gpsStaus = "On";
		}
		latitude = String.valueOf(gps.getLatitude());
		longitude = String.valueOf(gps.getLongitude());
		if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
				|| (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
			latitude="";
			longitude="";
		}
		response = new ResponseGetUserInfo();
		response.setLoginID(mAppPreferences.getLoginId());
		response.setTimeStamp(Utils.CurrentDateTime());
		response.setNetworkCheck(netType);//network type
		response.setLat(latitude);
		response.setLongt(longitude);
		response.setBatteryStatus(BatSta);
		response.setAutoTime(autoTime);
		response.setImei(iemi);
		response.setGps(gpsStaus);
		response.setMock(mAppPreferences.getAppNameMockLocation()+"~"+gps.isMockLocation());
		SignalChecker SC= new SignalChecker(GetUserInfoService.this,new SignalChecker.OnSignalReceiver() {
			@Override
			public void onRecived(int signal) {
				response.setSignal(signal);
				DataBaseHelper db = new DataBaseHelper(GetUserInfoService.this);
				db.open();
				db.insertMobileInfo(response);
				db.close();
			}
		});
		SC.checkAndUpdate();

		if(Utils.isNetworkAvailable(GetUserInfoService.this)){

		}else{
			DataBaseHelper db = new DataBaseHelper(GetUserInfoService.this);
			db.open();
			db.insertMobileInfo(response);
			db.close();
		}

	}


	public class Getversion extends AsyncTask<Void, Void, Void> {
		String res;
		Context con;
		ServiceResponce response;

		public Getversion(Context con) {
			this.con = con;
			//Toast.makeText(GetUserInfoService.this,"Meta data start.",Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);
			Gson gson = new Gson();
			nameValuePairs.add(new BasicNameValuePair("version",pInfo.versionName));
			nameValuePairs.add(new BasicNameValuePair("userID", mAppPreferences.getUserId()));
			nameValuePairs.add(new BasicNameValuePair("roleID", mAppPreferences.getRoleId()));
			nameValuePairs.add(new BasicNameValuePair("deviceID", mAppPreferences.getGCMRegistationId()));
			nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
			try {
				String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetAppVersion,nameValuePairs);
				String new_res = res.replace("[", "").replace("]", "");
				response = gson.fromJson(new_res, ServiceResponce.class);
			} catch (Exception e) {
				e.printStackTrace();
				res = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (response != null) {
				//Toast.makeText(GetUserInfoService.this,"Meta data starttted.",Toast.LENGTH_LONG).show();
				//get Tracking role
				DataBaseHelper dbHelperr = new DataBaseHelper(GetUserInfoService.this);
				dbHelperr.open();
				if(dbHelperr.trackingRoleRightCount("UserTrackOnOff", "UserTrackModule")>0){
					dbHelperr.updateTrackingRoleRight(response.getTrackingRole());
				}

				//get Tracking configuration
				if (response.getUserTracking().length() != 0) {
					mAppPreferences.setUserTracking(response.getUserTracking());
					String[] dataUserTrack = response.getUserTracking().split("\\~");
					mAppPreferences.setTrackingOnOff(dataUserTrack[0]);
					mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
					//Calendar calendar = Calendar.getInstance();
					//long saveuserTrackInterval=calendar.getTimeInMillis()+Integer.parseInt(dataUserTrack[2]);
					//mAppPreferences.setUserTrackUploadTime(saveuserTrackInterval);
				}

				/* get app config data (distance range,enable field,autodatetime flag)
				 * distance range for indicate show site in fuel filling given distance range
				 * enable disable field in fuel filling*/
				if(response.getDistanceRange().length()!=0){
					String[] dataTS = response.getDistanceRange().split("\\~");
					if (dataTS.length > 6) {
						mAppPreferences.setEnableFillingField(dataTS[0]);
						mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt(dataTS[2]) );
						mAppPreferences.setSiteNameEnable(Integer.parseInt(dataTS[3])  );
						mAppPreferences.setAutoDateTime(dataTS[4]);
						mAppPreferences.setSearchTTDateRange(Integer.parseInt(dataTS[5]));
						mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt(dataTS[6]));
					}
				}

				// TT Configuration
				mAppPreferences.setTTConfiguration(response.getTtconfiguration());
				String ttConfig = mAppPreferences.getTTConfiguration();
				if (ttConfig.length() != 0) {
					String baseArray[] = ttConfig.split("@");
					if (baseArray.length > 0) {
						mAppPreferences.setTTminimage(Integer.parseInt(baseArray[0]));
						mAppPreferences.setTTmaximage(Integer.parseInt(baseArray[1]));
						mAppPreferences.setTTimageMessage(baseArray[2]);
					}
				}

				// FF Configuration
				mAppPreferences.setFFConfiguration(response.getFfconfiguration());
				String ffConfig = mAppPreferences.getFFConfiguration();
				if (ffConfig.length() != 0) {
					String baseArray[] = ffConfig.split("@");
					if (baseArray.length > 0) {
						mAppPreferences.setFFminimage(Integer.parseInt(baseArray[0]));
						mAppPreferences.setFFmaximage(Integer.parseInt(baseArray[1]));
						mAppPreferences.setFFimageMessage(baseArray[2]);
					}
				}

				// PM Configuration
				mAppPreferences.setPmConfiguration(response.getPmconfiguration());
				String pmConfig = mAppPreferences.getPmConfiguration();
				if (pmConfig.length() != 0) {
					String baseArray[] = pmConfig.split("@");
					int activityID = 0, paramID = 0;
					String paramName = "";
					dbHelperr.clearpmconfig();
					if (baseArray.length > 0) {
						for (int i = 0; i < baseArray.length; i++) {
							String tmpArray[] = baseArray[i].split("\\$");
							activityID = Integer.parseInt(tmpArray[0].toString());
							tmpArray = tmpArray[1].split("\\#");
							paramID = Integer.parseInt(tmpArray[0].toString());
							paramName = tmpArray[1].toString();
							dbHelperr.insertPmConfiguration(activityID, paramID,paramName);
						}
					}
					dbHelperr.close();
				}

				//passward expired or
				//IMEI No.change or
				//passward change or
				//System Configuration setting has been changes.
				if (response.getSuccess().equalsIgnoreCase("A")) {
					DataBaseHelper dbHelper = new DataBaseHelper(GetUserInfoService.this);
					dbHelper.open();
					dbHelper.clearFormRights();
					dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
					PassIEMIChange(SessionExpired.class,response.getMessage().trim());
				}else if(response.getSuccess().equalsIgnoreCase("V")){ //use old version app
					mAppPreferences.setLoginState(2);
					PassIEMIChange(Version.class,response.getMessage().trim());
				}else if (response.getSuccess().equalsIgnoreCase("T") || response.getSuccess().equalsIgnoreCase("false")) {// licence expired
					DataBaseHelper dbHelper = new DataBaseHelper(GetUserInfoService.this);
					dbHelper.open();
					dbHelper.clearFormRights();
					dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
					PassIEMIChange(SessionExpired.class,response.getMessage().trim());
				}else if (response.getSuccess().equalsIgnoreCase("E")) {
					DataBaseHelper dbHelper = new DataBaseHelper(GetUserInfoService.this);
					dbHelper.open();
					dbHelper.clearFormRights();
					dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
					PassIEMIChange(PasswardExpired.class,response.getMessage().trim());
				}else if(response.getSuccess().equalsIgnoreCase("P")){
					ActivityManager activityManager = (ActivityManager) GetUserInfoService.this.getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
					if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(GetUserInfoService.this.getPackageName().toString())){
						Intent i = new Intent(GetUserInfoService.this, PasswardExpire.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("msg",response.getMessage());
						startActivity(i);
					}else{

					}
				}else if(response.getSuccess().equalsIgnoreCase("S") && response.getMessage().trim().length()!=0){ //successfully
					mAppPreferences.setDataTS(response.getMessage());
					successfull();
				}
			}else {
			}
			super.onPostExecute(result);
		}
	}

	public void PassIEMIChange(Class cls, String msg) {
		ActivityManager activityManager = (ActivityManager) GetUserInfoService.this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		if (services.get(0).topActivity
				.getPackageName()
				.toString()
				.equalsIgnoreCase(GetUserInfoService.this.getPackageName().toString())) {
			Intent i = new Intent(GetUserInfoService.this, cls);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.putExtra("msg", msg);
			startActivity(i);
		} else {

		}
	}

	public void successfull() {
		DataBaseHelper dbHelper = new DataBaseHelper(GetUserInfoService.this);
		dbHelper.open();
		String[] dataTS = mAppPreferences.getDataTS().split(",");
		dataTypeID = new String[dataTS.length];
		timeStamp = new String[dataTS.length];
		for (int i = 0; i < dataTS.length; i++) {
			tmpDataTS = dataTS[i].split("\\~");
			dataTypeID[i] = tmpDataTS[0];
			timeStamp[i] = tmpDataTS[1];
		}
		if (!mAppPreferences.getFirstTimeRunApp().equalsIgnoreCase("A")) {
			dbHelper.clearDataTS();
			dbHelper.dataTS(dataTypeID, timeStamp, "", "", 0,"0");
			mAppPreferences.setFirstTimeRunApp("A");
		} else {
			dbHelper.dataTS(dataTypeID, timeStamp, "", "", 1,"0");
		}
	}
}
