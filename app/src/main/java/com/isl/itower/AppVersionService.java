package com.isl.itower;

/*Created By : Dhakan Lal Sharma
 Modified On : 4-Aug-2016
 Version     : 0.1
 Purpose     : cr# 1.9.1.3

 Modified By : Dhakan Lal Sharma
 Version     : 0.2
 Modified On : 23-Nov-2016
 Purpose     : pwd complexcity(expired)
 */
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.modal.ServiceResponce;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.gson.Gson;
import com.isl.modal.EnergyMetaList;

public class AppVersionService extends Service {
	AppPreferences mAppPreferences;
	PackageInfo pInfo = null;
	String[] dataTypeID, timeStamp;
	String[] tmpDataTS = new String[2];
	DataBaseHelper db = null;
	public static boolean isServiceRunning = false;  
	public AppVersionService() {
	}
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Override
	public void onCreate() {
		// Toast.makeText(this, "The new Service was Created",
		// Toast.LENGTH_LONG).show();
		db = new DataBaseHelper(this);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		//WorkFlowUtils.stop1(AppVersionService.this);
		//WorkFlowUtils.scheduleAlarmAppVersion(AppVersionService.this);
		mAppPreferences = new AppPreferences(AppVersionService.this);
		//mAppPreferences.setServiceVersion("start");
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (Utils.isNetworkAvailable(AppVersionService.this)) {
			new Getversion(AppVersionService.this).execute();
			new SiteDetailsTask(AppVersionService.this).execute();
		} else {
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		//mAppPreferences.setServiceVersion("stop");
		//WorkFlowUtils.scheduleAlarmAppVersion(AppVersionService.this);
	}

	public class SiteDetailsTask extends AsyncTask<Void, Void, Void> {
		Context con;
		EnergyMetaList dataResponse;

		public SiteDetailsTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("module", "Energy"));
			nameValuePairs.add(new BasicNameValuePair("datatype", "27"));
			nameValuePairs.add(new BasicNameValuePair("userID", mAppPreferences.getUserId()));
			nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
			nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
			String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata,nameValuePairs);
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
			if (dataResponse != null) {
				if (dataResponse.getSites()==null || dataResponse.getSites().size() == 0) {
					// Toast.makeText(EnergyManagement.this,"Meta data not provided by server.",Toast.LENGTH_LONG).show();
					// WorkFlowUtils.toast(EnergyManagement.this, "70");
				} else if (dataResponse.getSites()!=null && dataResponse.getSites().size() > 0) {
					DataBaseHelper dbHelper = new DataBaseHelper(AppVersionService.this);
					dbHelper.open();
					dbHelper.clearSites();
					dbHelper.insertSites(dataResponse.getSites());
					dbHelper.close();

				}
			} else {
				// Toast.makeText(EnergyManagement.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				// WorkFlowUtils.toast(EnergyManagement.this, "13");
			}
			super.onPostExecute(result);
		}
	}

	public class Getversion extends AsyncTask<Void, Void, Void> {
		String res;
		Context con;
		ServiceResponce response;

		public Getversion(Context con) {
			this.con = con;
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
				//mAppPreferences.setPMimageMessage(response.getPmImageMessage());
				// PM images Configuration
				mAppPreferences.setPmConfiguration(response.getPmconfiguration());
				String pmConfig = mAppPreferences.getPmConfiguration();
				if (pmConfig.length() != 0) {
					String baseArray[] = pmConfig.split("@");
					int activityID = 0, paramID = 0;
					String paramName = "";
					db.open();
					db.clearpmconfig();
					//db.updateTrackingRoleRight("UserTrackOnOff", response.getTrackingRole());
					if (baseArray.length > 0) {
						for (int i = 0; i < baseArray.length; i++) {
							String tmpArray[] = baseArray[i].split("\\$");
							activityID = Integer.parseInt(tmpArray[0].toString());
							tmpArray = tmpArray[1].split("\\#");
							paramID = Integer.parseInt(tmpArray[0].toString());
							paramName = tmpArray[1].toString();
							db.insertPmConfiguration(activityID, paramID,paramName);
						}
					}
					db.close();
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
				/*if (ffConfig.length() != 0) {
					String baseArray[] = ffConfig.split("@");
					if (baseArray.length > 0) {
						mAppPreferences.setFFminimage(Integer.parseInt(baseArray[0]));
						mAppPreferences.setFFmaximage(Integer.parseInt(baseArray[1]));
						mAppPreferences.setFFimageMessage(baseArray[2]);
					}
				}*/
				if (ffConfig.length() != 0) {
					String baseArray[] = ffConfig.split("@");
					if (baseArray.length > 6) {
						mAppPreferences.setApprovalRemarks(baseArray[0]);
						mAppPreferences.setIsRating(baseArray[1]);
						mAppPreferences.setPMphotoInfoRating(baseArray[2]);
						mAppPreferences.setQualityPMtaskRating(baseArray[3]);
						mAppPreferences.setVisualCheck1Rating(baseArray[4]);
						mAppPreferences.setVisualCheck2Rating(baseArray[5]);
						mAppPreferences.setVisualCheck3Rating(baseArray[6]);
					}
				}

				if(response.getDistanceRange().length()!=0){
					String[] dataTS = response.getDistanceRange().split("\\~");
					if (dataTS.length > 7) {
						mAppPreferences.setEnableFillingField(dataTS[0]);
						mAppPreferences.setSiteMotorable(Integer.parseInt(dataTS[1]));
						mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt(dataTS[2]) );
						mAppPreferences.setSiteNameEnable(Integer.parseInt(dataTS[3])  );
						mAppPreferences.setAutoDateTime(dataTS[4]);
						mAppPreferences.setSearchTTDateRange(Integer.parseInt(dataTS[5]));
						mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt(dataTS[6]));
						mAppPreferences.setPMImageUploadType(Integer.parseInt(dataTS[7]));
						if (dataTS.length > 8){
							mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt(dataTS[8]));
						}
					}
				}


				try {
					if (response.getDistanceRange().length() != 0) {
						String[] dataTS = response.getDistanceRange().split( "\\~" );
						if (dataTS.length > 9) {
							mAppPreferences.setEnableFillingField( dataTS[0] );
							mAppPreferences.setSiteMotorable( Integer.parseInt( dataTS[1] ) );
							mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt( dataTS[2] ) );
							mAppPreferences.setSiteNameEnable( Integer.parseInt( dataTS[3] ) );
							mAppPreferences.setAutoDateTime( dataTS[4] );
							mAppPreferences.setSearchTTDateRange( Integer.parseInt( dataTS[5] ) );
							mAppPreferences.setEnablePrePopulateSitesTT( Integer.parseInt( dataTS[6] ) );
							mAppPreferences.setPMImageUploadType( Integer.parseInt( dataTS[7] ) );
							mAppPreferences.setPMRejectMadatoryFields( Integer.parseInt( dataTS[8] ) );
							mAppPreferences.setPMReviewPlanDate( Integer.parseInt( dataTS[9] ) );
							if (dataTS.length > 10) {
								mAppPreferences.setVideoUploadMaxSize( Integer.parseInt( dataTS[10] ) );
							}
						}
					}
				}catch (Exception e){
					mAppPreferences.setEnableFillingField("0");
					mAppPreferences.setSiteMotorable(0);
					mAppPreferences.setSavePMBackgroundEnable(0);
					mAppPreferences.setSiteNameEnable(Integer.parseInt("0"));
					mAppPreferences.setAutoDateTime("0");
					mAppPreferences.setSearchTTDateRange(Integer.parseInt("30"));
					mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt("0"));
					mAppPreferences.setPMImageUploadType(Integer.parseInt("2"));
					mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt("0"));
					mAppPreferences.setPMReviewPlanDate(Integer.parseInt("0"));
					mAppPreferences.setVideoUploadMaxSize(Integer.parseInt("2"));
				}


				if (response.getUserTracking().length() != 0) {
					mAppPreferences.setUserTracking(response.getUserTracking());
					//String[] dataUserTrack = response.getUserTracking().split("\\~");
					//Calendar calendar = Calendar.getInstance();
					//long saveuserTrackInterval=calendar.getTimeInMillis()+Integer.parseInt(dataUserTrack[2]);
					//mAppPreferences.setUserTrackUploadTime(saveuserTrackInterval);
				}
				
				
				
				//start 0.2
				//passward expired or  
				//IMEI No.change or 
				//passward change or 
				//System Configuration setting has been changes.
				if (response.getSuccess().equalsIgnoreCase("A")) {
					DataBaseHelper dbHelper = new DataBaseHelper(AppVersionService.this);
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
				}else if (response.getSuccess().equalsIgnoreCase("T")) {// licence expired
					DataBaseHelper dbHelper = new DataBaseHelper(AppVersionService.this);
	     	        dbHelper.open();
			        dbHelper.clearFormRights();
			        dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
			        PassIEMIChange(SessionExpired.class,response.getMessage().trim());
		        }else if (response.getSuccess().equalsIgnoreCase("E")) {
					DataBaseHelper dbHelper = new DataBaseHelper(AppVersionService.this);
	     	        dbHelper.open();
			        dbHelper.clearFormRights();
			        dbHelper.close();
					mAppPreferences.setLoginState(0);
					mAppPreferences.saveSyncState(0);
					mAppPreferences.setGCMRegistationId("");
			        PassIEMIChange(PasswardExpired.class,response.getMessage().trim());
		        }else if(response.getSuccess().equalsIgnoreCase("P")){
		          ActivityManager activityManager = (ActivityManager) AppVersionService.this.getSystemService(Context.ACTIVITY_SERVICE);
		  		  List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
		  		  if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(AppVersionService.this.getPackageName().toString())){
		  			  Intent i = new Intent(AppVersionService.this, PasswardExpire.class);
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
		ActivityManager activityManager = (ActivityManager) AppVersionService.this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		if (services.get(0).topActivity
				.getPackageName()
				.toString()
				.equalsIgnoreCase(
						AppVersionService.this.getPackageName().toString())) {
			Intent i = new Intent(AppVersionService.this, cls);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.putExtra("msg", msg);
			startActivity(i);
		} else {

		}
	}

	// start 0.2
	public void successfull() {
		DataBaseHelper dbHelper = new DataBaseHelper(AppVersionService.this);
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
