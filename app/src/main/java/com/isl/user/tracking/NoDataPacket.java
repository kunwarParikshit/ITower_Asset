package com.isl.user.tracking;
import com.isl.constant.WebMethods;
import com.isl.itower.GPSTracker;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
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
import android.os.IBinder;
import androidx.core.content.PermissionChecker;
import android.telephony.TelephonyManager;

public class NoDataPacket extends Service {
	AppPreferences mAppPreferences;
	static String BatSta;
	DataBaseHelper dbh; 
	PackageInfo pInfo = null;
	public NoDataPacket() {
	}
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
	    //Toast.makeText(this, "No data packet Service was Created", Toast.LENGTH_LONG).show();
		//gps = new GPSTracker(GetUserInfoService.this);
		}

	public static BroadcastReceiver battInfoRec = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			BatSta = String.valueOf(level) + "%";
		}
	};
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		//Toast.makeText(this, "swiped app", Toast.LENGTH_LONG).show();
	    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(this, "The No data packet Service started", Toast.LENGTH_LONG).show();
		mAppPreferences = new AppPreferences(NoDataPacket.this);
		dbh = new DataBaseHelper(NoDataPacket.this);
		dbh.open();
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(mAppPreferences.getTrackingOnOff().equalsIgnoreCase( "ON" )) {
			callWebService();
		}
		return Service.START_STICKY;
		
	}
	
	public class saveMobileInfo extends AsyncTask<Void, Void, Void> {
		Context con;
		JSONObject objj;
		JSONObject dataObj;
		String res="";
		public saveMobileInfo(Context con, JSONObject baseObj) {
			this.con = con;
			this.dataObj = baseObj;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String moduleUrl = dbh.getModuleIP("UserTrackModule");
				String url = (moduleUrl.isEmpty()) ? WebMethods.url_SaveMobileInfo : "http://" + moduleUrl + "/data-adaptor/bulkPushData";
				res = Utils.postJson(url,dataObj.toString());
				objj = new JSONObject(res); 		
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
					//Toast.makeText(LocationOffOnService.this,
					//		"status="+objj.getString("status").toString(),
					//		Toast.LENGTH_LONG).show();
				} else {
					//Toast.makeText(LocationOffOnService.this,
					//		"message="+objj.getString("errorMessage").toString(),
					//		Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			
		  }else{
			 // Toast.makeText(GetUserInfoService.this,
			//			"message test",
			//			Toast.LENGTH_LONG).show();
		  }
		 stopSelf();
		 super.onPostExecute(result);
		}
	}

	public void callWebService(){
		String latitude, longitude;
		String autoTime="";
		GPSTracker gps;
		String iemi = "";
		String gpsStaus="";
		String netType = "";
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
		int permission = PermissionChecker.checkSelfPermission(NoDataPacket.this, Manifest.permission.READ_PHONE_STATE);
		if (permission == PermissionChecker.PERMISSION_GRANTED) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			iemi = telephonyManager.getDeviceId();
		}
		gps = new GPSTracker(NoDataPacket.this);
		if (gps.canGetLocation() == false) {
			gpsStaus = "Off";
		}else{
			gpsStaus = "On";
		}
		
		latitude = String.valueOf(gps.getLatitude());
		longitude = String.valueOf(gps.getLongitude());
		if (Utils.isAutoDateTime(this)) {
			autoTime = "false";
		} else {
			autoTime = "true";
		}
			
		if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
				|| (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
			latitude="";
			longitude="";
		}
		try {
			JSONObject obj2 = new JSONObject();
			obj2.put("id", "1");
			obj2.put("baseId", "UserTracking");
			final JSONArray myNewArray = new JSONArray();
			if(Utils.isNetworkAvailable(NoDataPacket.this)){
				final JSONObject obj = new JSONObject();
				obj.put("trackID", "");
				obj.put("loginid", mAppPreferences.getLoginId());
				obj.put("lat", latitude);
				obj.put("long", longitude);
				obj.put("autoTime", autoTime);
				obj.put("time", Utils.CurrentDateTime());
				obj.put("bat", BatSta);
				obj.put("nw", netType);
				obj.put("imei",iemi);
				obj.put("gps", gpsStaus);
				obj.put("mock", mAppPreferences.getAppNameMockLocation()+"~"+gps.isMockLocation()+"~push data from API");
				SignalChecker SC= new SignalChecker(NoDataPacket.this,new SignalChecker.OnSignalReceiver() {
					@Override
					public void onRecived(int signal) {
						try {
							obj.put("sig",signal);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myNewArray.put(obj);
					}
				});
				SC.checkAndUpdate();
				if(Utils.isNetworkAvailable(NoDataPacket.this)){
				}else{
					obj.put("sig","");
					myNewArray.put(obj);
				}
			
			obj2.put("dataMapLst", myNewArray);
			if (Utils.isNetworkAvailable(NoDataPacket.this)) {
			new saveMobileInfo(NoDataPacket.this, obj2).execute();
		   }else{
			   stopSelf();
		   }
		  }
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
