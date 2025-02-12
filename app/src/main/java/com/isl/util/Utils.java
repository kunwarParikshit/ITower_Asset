/*Modified by :  Dhakan Lal Sharma
Version     :  0.1
Date        :  21-June-2017
Purpose     :  To fix Qc bug# 22655 on 11-Jan-2016.

Modified by :  Dhakan Lal Sharma
Version     :  0.2
Date        :  21-June-2017
Purpose     :  Alert message for go to Automatically date & time screen.
 */
package com.isl.util;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

import com.isl.approval.Approval;

import android.app.DownloadManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.LinearLayout.LayoutParams;

import com.isl.audit.activity.AuditManagementActivity;
import com.isl.common.activities.Home;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.energy.EnergyManagement;
import com.isl.hsse.Hsse;
import com.isl.itower.ValidateUDetails;
import com.isl.itower.GPSTracker;
import infozech.itower.R;

import com.isl.modal.BeanAddNotification;
import com.isl.alarm.AlarmManagement;
import com.isl.asset.SiteDetails;
import com.isl.incident.IncidentManagement;
import com.isl.modal.HomeModule;
import com.isl.preventive.PMTabs;
import com.isl.reports.Report;
import com.isl.sparepart.schedule.Schedule;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.isl.workflow.WorkflowImpl;

public class Utils {
	private int available;

	/*
	public static void initializeHashmap(Resources resources) {
		AppConstants.mainModule.put("Incident","654");
		AppConstants.mainModule.put("Preventive","655");
		AppConstants.mainModule.put("Energy","656");
		AppConstants.mainModule.put("Approval","921");
		AppConstants.mainModule.put("Schedule","657");
		AppConstants.mainModule.put("Alarm","658");
		AppConstants.mainModule.put("Assets","659");
		AppConstants.mainModule.put("Reports","660");
		AppConstants.mainModule.put("HealthSafty","955");
		AppConstants.mainModule.put("KPLCApp","964");
		AppConstants.mainModule.put("WorkflowImpl","1001");


		AppConstants.hashMap.put("Incident",R.drawable.incident);
		AppConstants.hashMap.put("Preventive",R.drawable.preventive);
		AppConstants.hashMap.put("Energy",R.drawable.energy);
		AppConstants.hashMap.put("Approval",R.drawable.approval);
		AppConstants.hashMap.put("Schedule",R.drawable.schedule);
		AppConstants.hashMap.put("Alarm",R.drawable.alarm_management);
		AppConstants.hashMap.put("Assets",R.drawable.asset);
		AppConstants.hashMap.put("Reports",R.drawable.reports);
		AppConstants.hashMap.put("HealthSafty",R.drawable.health_safty);
		AppConstants.hashMap.put("KPLCApp",R.drawable.kplc_app);
		AppConstants.hashMap.put("WorkflowImpl",R.drawable.access_management);

		AppConstants.classMap.put("Incident", IncidentManagement.class);
		AppConstants.classMap.put("Preventive", PMTabs.class);
		AppConstants.classMap.put("Energy", EnergyManagement.class);
		AppConstants.classMap.put("Approval", Approval.class);
		AppConstants.classMap.put("Schedule", Schedule.class);
		AppConstants.classMap.put("Alarm", AlarmManagement.class);
		AppConstants.classMap.put("Assets", SiteDetails.class);
		AppConstants.classMap.put("Reports", Report.class);
		AppConstants.classMap.put("HealthSafty",IncidentManagement.class);
		AppConstants.classMap.put("KPLCApp",IncidentManagement.class);
		AppConstants.classMap.put("WorkflowImpl",WorkflowImpl.class);
	}
	*/

	public static Map<String, HomeModule> initializeModuleMap(Resources resources) {

		Map<String, HomeModule> moduleMap = new HashMap<String, HomeModule>();

		HomeModule module = new HomeModule("Incident",654,R.drawable.incident,IncidentManagement.class,false,null,null,"TT");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Preventive",655,R.drawable.preventive,Home.class,false,null,null,"PM");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Energy",656,R.drawable.energy,EnergyManagement.class,false,null,null,"FF");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Approval",921,R.drawable.approval,Approval.class,false,null,null,"APP");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Schedule",657,R.drawable.schedule,AlarmManagement.class,false,null,null,"PM");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Alarm",658,R.drawable.alarm_management,AlarmManagement.class,false,null,null,"ALM");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("iAsset",659,R.drawable.asset, Home.class,false,null,null,"AST");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("Reports",660,R.drawable.reports,Report.class,false,null,null,"RPT");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("HealthSafty",955,R.drawable.health_safty,IncidentManagement.class,false,null,null,"HNS");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("KPLCApp",964,R.drawable.kplc_app,IncidentManagement.class,false,null,null,"KPL");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("AccessManagement",1001,R.drawable.access_management, WorkflowImpl.class,false,null,null,"AM");
		moduleMap.put(module.getModuleName(),module);

		module = new HomeModule("HSSE",1012,R.drawable.hsse, Hsse.class,false,null,null,"HSSE");
		moduleMap.put(module.getModuleName(),module);


		module = new HomeModule("Audits",3000,R.drawable.asset, AuditManagementActivity.class,false,null,null,"ADT");
		moduleMap.put(module.getModuleName(),module);

		/*module = new HomeModule("Asset Movement",3005,R.drawable.asset, AssetMovementActivity.class,false,null,null,"ADT");
		moduleMap.put(module.getModuleName(),module);*/

		return moduleMap;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr.getActiveNetworkInfo() != null
				&& connMgr.getActiveNetworkInfo().isAvailable()
				&& connMgr.getActiveNetworkInfo().isConnected())
			return true;
		else {
			return false;
		}
	}

//	public static boolean hasPermissions(Context context,String... permissions) {
//		for (String permission : permissions) {
//			if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//				return false;
//			}
//		}
//		return true;
//	}
	public static boolean hasPermissions(Context context,String... permissions) {
		for (String permission : permissions) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
					&& permission.equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")){
				return true;
			}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
					&& permission.equalsIgnoreCase("android.permission.READ_EXTERNAL_STORAGE")){
				return true;
			}else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
					&& permission.equalsIgnoreCase("android.permission.POST_NOTIFICATIONS")){
				return true;
			}else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
					&& permission.equalsIgnoreCase("android.permission.READ_MEDIA_IMAGES")){
				return true;
			}else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
					&& permission.equalsIgnoreCase("android.permission.READ_MEDIA_VIDEO")){
				return true;
			}else {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public static String changeDateFormat(String time, String fromFormat,String toFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(fromFormat, Locale.ENGLISH);
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(toFormat,Locale.ENGLISH);
		String s = sdf1.format(date.getTime());
		return s;
	}

	public static int month(String mon) {
		int month=0;
		if (mon.equalsIgnoreCase( "Jan" )) {
			month=0;
		}else if (mon.equalsIgnoreCase( "Feb" )) {
			month=1;
		}else if (mon.equalsIgnoreCase( "Mar" )) {
			month=2;
		}else if (mon.equalsIgnoreCase( "Apr" )) {
			month=3;
		}else if (mon.equalsIgnoreCase( "May" )) {
			month=4;
		}else if (mon.equalsIgnoreCase( "Jun" )) {
			month=5;
		}else if (mon.equalsIgnoreCase( "Jul" )) {
			month=6;
		}else if (mon.equalsIgnoreCase( "Aug" )) {
			month=7;
		}else if (mon.equalsIgnoreCase( "Sep" )) {
			month=8;
		}else if (mon.equalsIgnoreCase( "Oct" )) {
			month=9;
		}else if (mon.equalsIgnoreCase( "Nov" )) {
			month=10;
		}else if (mon.equalsIgnoreCase( "Dec" )) {
			month=11;
		}
		return month;
	}

	public static String CurrentDateTime() {
		String Mon = "";
		String currentdate;
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int a = cal.get(Calendar.HOUR_OF_DAY);
		int b = cal.get(Calendar.MINUTE);
		int c = cal.get(Calendar.SECOND);
		String cur_day = "";
		if (day < 10)
			cur_day = "0" + day;
		else
			cur_day = String.valueOf(day);
		if (month == 0) {
			Mon = "Jan";
		} else if (month == 1) {
			Mon = "Feb";
		} else if (month == 2) {
			Mon = "Mar";
		} else if (month == 3) {
			Mon = "Apr";
		} else if (month == 4) {
			Mon = "May";
		} else if (month == 5) {
			Mon = "Jun";
		} else if (month == 6) {
			Mon = "Jul";
		} else if (month == 7) {
			Mon = "Aug";
		} else if (month == 8) {
			Mon = "Sep";
		} else if (month == 9) {
			Mon = "Oct";
		} else if (month == 10) {
			Mon = "Nov";
		} else if (month == 11) {
			Mon = "Dec";
		}
		String cur_hour = "";
		if (a < 10)
			cur_hour = "0" + a;
		else
			cur_hour = String.valueOf(a);
		String cur_min = "";
		if (b < 10)
			cur_min = "0" + b;
		else
			cur_min = String.valueOf(b);
		String cur_sec = "";
		if (c < 10)
			cur_sec = "0" + c;
		else
			cur_sec = String.valueOf(c);
		currentdate = cur_day + "-" + Mon + "-" + year + " " + cur_hour + ":"
				+ cur_min + ":" + cur_sec;
		return currentdate;
	}

	public static String DateTime() {
		String currentdate;
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int a = cal.get(Calendar.HOUR_OF_DAY);
		int b = cal.get(Calendar.MINUTE);
		int c = cal.get(Calendar.SECOND);
		String cur_day = "";
		String cur_month = "";
		if (day < 10)
			cur_day = "0" + day;
		else
			cur_day = String.valueOf(day);

		month = month+1;
		if(month<10){
			cur_month = "0"+ month;
		}


		String cur_hour = "";
		if (a < 10)
			cur_hour = "0" + a;
		else
			cur_hour = String.valueOf(a);
		String cur_min = "";
		if (b < 10)
			cur_min = "0" + b;
		else
			cur_min = String.valueOf(b);
		String cur_sec = "";
		if (c < 10)
			cur_sec = "0" + c;
		else
			cur_sec = String.valueOf(c);
		currentdate = cur_day + "." + cur_month + "." + year + " " + cur_hour + ":"
				+ cur_min;
		return currentdate;
	}

	public static String DateTimeStamp() {
		String Mon = "";
		String currentdate;
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int a = cal.get(Calendar.HOUR_OF_DAY);
		int b = cal.get(Calendar.MINUTE);
		int c = cal.get(Calendar.SECOND);
		String cur_day = "";
		if (day < 10)
			cur_day = "0" + day;
		else
			cur_day = String.valueOf(day);
		if (month == 0) {
			Mon = "Jan";
		} else if (month == 1) {
			Mon = "Feb";
		} else if (month == 2) {
			Mon = "Mar";
		} else if (month == 3) {
			Mon = "Apr";
		} else if (month == 4) {
			Mon = "May";
		} else if (month == 5) {
			Mon = "Jun";
		} else if (month == 6) {
			Mon = "Jul";
		} else if (month == 7) {
			Mon = "Aug";
		} else if (month == 8) {
			Mon = "Sep";
		} else if (month == 9) {
			Mon = "Oct";
		} else if (month == 10) {
			Mon = "Nov";
		} else if (month == 11) {
			Mon = "Dec";
		}
		String cur_hour = "";
		if (a < 10)
			cur_hour = "0" + a;
		else
			cur_hour = String.valueOf(a);
		String cur_min = "";
		if (b < 10)
			cur_min = "0" + b;
		else
			cur_min = String.valueOf(b);
		String cur_sec = "";
		if (c < 10)
			cur_sec = "0" + c;
		else
			cur_sec = String.valueOf(c);
		currentdate = cur_day + Mon + year + cur_hour + cur_min + cur_sec;
		return currentdate;
	}

	public static String CurrentDate(int flag) {
		String Mon = "";
		String currdate;
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String cur_day = "";
		if (day < 10)
			cur_day = "0" + day;
		else
			cur_day = String.valueOf(day);
		if (month == 0) {
			Mon = "Jan";
		} else if (month == 1) {
			Mon = "Feb";
		} else if (month == 2) {
			Mon = "Mar";
		} else if (month == 3) {
			Mon = "Apr";
		} else if (month == 4) {
			Mon = "May";
		} else if (month == 5) {
			Mon = "Jun";
		} else if (month == 6) {
			Mon = "Jul";
		} else if (month == 7) {
			Mon = "Aug";
		} else if (month == 8) {
			Mon = "Sep";
		} else if (month == 9) {
			Mon = "Oct";
		} else if (month == 10) {
			Mon = "Nov";
		} else if (month == 11) {
			Mon = "Dec";
		}
		if(flag==1) {
			month = month + 1;
			if (month < 10){
				Mon = "0" + month;
			}else{
				Mon = ""+month;
			}
			currdate = year + Mon + cur_day;
		}else{
			currdate = cur_day + "-" + Mon + "-" + year;
		}

		return currdate;
	}

	public static String updateDate(int month,int year) {
		String Mon = "";
		String currdate;
		final Calendar cal = Calendar.getInstance();

		if (month == 0) {
			Mon = "Jan";
		} else if (month == 1) {
			Mon = "Feb";
		} else if (month == 2) {
			Mon = "Mar";
		} else if (month == 3) {
			Mon = "Apr";
		} else if (month == 4) {
			Mon = "May";
		} else if (month == 5) {
			Mon = "Jun";
		} else if (month == 6) {
			Mon = "Jul";
		} else if (month == 7) {
			Mon = "Aug";
		} else if (month == 8) {
			Mon = "Sep";
		} else if (month == 9) {
			Mon = "Oct";
		} else if (month == 10) {
			Mon = "Nov";
		} else if (month == 11) {
			Mon = "Dec";
		}
		currdate =  Mon + "-" + year;


		return currdate;
	}

	public static String CurrentTime() {
		String currTime;
		final Calendar cal = Calendar.getInstance();
		int a = cal.get(Calendar.HOUR_OF_DAY);
		int b = cal.get(Calendar.MINUTE);
		int c = cal.get(Calendar.SECOND);

		String cur_hour = "";
		if (a < 10)
			cur_hour = "0" + a;
		else
			cur_hour = String.valueOf(a);
		String cur_min = "";
		if (b < 10)
			cur_min = "0" + b;
		else
			cur_min = String.valueOf(b);
		String cur_sec = "";
		if (c < 10)
			cur_sec = "0" + c;
		else
			cur_sec = String.valueOf(c);
		currTime = cur_hour + ":" + cur_min + ":" + cur_sec;
		return currTime;
	}

	public static Date convertStringToDate(String dateString, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format,Locale.ENGLISH);
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return convertedDate;
	}

	public static String httpPostRequest(Context context,String url,List<NameValuePair> parameterList) {
		// System.out.println("url:: " + url);
		String str = "";
		InputStream is = null;
		try {
	//	HttpsTrustManager.allowAllSSL();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;

		if(url.contains("https") || url.contains("http")){
			httppost = new HttpPost(url);
		}else{
			httppost = new HttpPost("http://"+url);
		}

			httppost.setEntity(new UrlEncodedFormEntity(parameterList));
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			str = sb.toString();
			//System.out.println("response::" + str);
		} catch (Exception e) {
			//e.printStackTrace();
			e.getMessage();
			//e.getMessage();
			//DataBaseHelper dbHelper = new DataBaseHelper(context);
			//AppPreferences mAppPreferences = new AppPreferences(context);
			//dbHelper.open();
			//dbHelper.insertLog("errorlog="+e.getMessage()+"-date="+date()+"-userId="+mAppPreferences.getUserId());
			//dbHelper.close();
		}
		return str;
	}
  // 108
	public static String httpPostRequest1(Context context,String url,List<NameValuePair> parameterList,String header) {
		// System.out.println("url:: " + url);
		String str = "";
		InputStream is = null;
		//	HttpsTrustManager.allowAllSSL();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;

		if(url.contains("https") || url.contains("http")){
			httppost = new HttpPost(url);
		}else{
			httppost = new HttpPost("http://"+url);
		}
		String value = "Bearer "+ header;
	//	httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",value);

		try {
			httppost.setEntity(new UrlEncodedFormEntity(parameterList));
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			str = sb.toString();
			//System.out.println("response::" + str);
		} catch (Exception e) {
			//e.printStackTrace();
			e.getMessage();
			//e.getMessage();
			//DataBaseHelper dbHelper = new DataBaseHelper(context);
			//AppPreferences mAppPreferences = new AppPreferences(context);
			//dbHelper.open();
			//dbHelper.insertLog("errorlog="+e.getMessage()+"-date="+date()+"-userId="+mAppPreferences.getUserId());
			//dbHelper.close();
		}
		return str;
	}
	//108

	public static String httpMultipartBackground(String url,String module,String allImgs,String data,String preImgsInfo,String postImgsInfo,
												 String addParam,String src,String userId,String taskState,String language,String opr,String flag) {
		//res = WorkFlowUtils.httpMultipartBackground(urls[0],"PM",jsonArrStrImg.toString(),data,preImgInfoArray.toString(),postImgInfoArray.toString(),
		//		"","","","",mAppPreferences.getLanCode(),"","");
		String str = "";
		InputStream is = null;
		//HttpsTrustManager.allowAllSSL();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;
		if (url.contains( "https" ) || url.contains( "http" )) {
			httppost = new HttpPost( url );
		} else {
			httppost = new HttpPost( "http://" + url );
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray( allImgs );
		} catch (Exception e) {

		}

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		try {
			if (jsonArray.length()>0 && jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					File file = null;
					file = new File((String)jsonArray.getJSONObject( i ).get( "path" ));
					builder.addBinaryBody( "images", file );
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			builder.addTextBody( "preImgInfo", preImgsInfo);
			builder.addTextBody( "postImgInfo", postImgsInfo);
			builder.addTextBody( AppConstants.MODULE, module);
			builder.addTextBody( "data", data);
			builder.addTextBody( AppConstants.ADD_PARAMS_ALIAS, addParam);
			builder.addTextBody( AppConstants.TXN_SOURSE, src);
			builder.addTextBody( AppConstants.USER_ID_ALIAS, userId);
			builder.addTextBody( AppConstants.TASK_STATE_ID_ALIAS, taskState);
			builder.addTextBody( AppConstants.LANGUAGE_CODE_ALIAS, language);
			builder.addTextBody( AppConstants.OPERATION, opr);
			builder.addTextBody( AppConstants.CPH_FALG, flag);
			httppost.setEntity(builder.build());

			HttpResponse httpResponse = httpclient.execute( httppost );
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader( is ) );
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append( line + "\n" );
			}
			is.close();
			str = sb.toString();
		} catch (Exception e) {
			//e.printStackTrace();
			str = e.getMessage();
		}
		System.out.println("ecex  ttt = ===="+str);
		return str;
	}

	public static String postJson(String url, String json) throws Exception {
		StringBuffer responseBfr = new StringBuffer();
		//HttpsTrustManager.allowAllSSL();
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpPost postRequest;
		if(url.contains("https") || url.contains("http")){
			postRequest = new HttpPost(url);
		}else{
			postRequest = new HttpPost("http://"+url);
		}

		try {
			//HttpPost postRequest = new HttpPost(url);
			StringEntity input = new StringEntity(json);
			input.setContentType("application/json");

			postRequest.setEntity(input);
			HttpResponse response = httpClient.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 201
					&& response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String output;
			while ((output = br.readLine()) != null) {
				responseBfr.append(output);
			}
			return responseBfr.toString();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public static String convertImageToBase64(String path) {
		String encodedImage = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			Matrix matrix = new Matrix();
			Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return encodedImage;
	}

	public static Bitmap convertBase64toBitmap(String base64) {
		Bitmap decodedByte = null;
		try {
			byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
			decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return decodedByte;
	}

	public static boolean checkValidation(Date date) {
		Date current_date = new Date();
		if (date.after(current_date))
			return false;
		else
			return true;
	}

	// date compare
	public static boolean checkDateCompare(Date fromDate, Date toDate) {
		if (fromDate.after(toDate))
			return false;
		else
			return true;
	}

	// Method to decode Image
	public static Bitmap decodeFile(String filePath) {
		Bitmap bitmap = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(filePath), null, o);
			//int origWidth = o.outWidth;
			//int origHeight = o.outHeight;
			// The new size we want to scale to
			final int REQUIRED_SIZE = 300;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			//int origWidth1 = o2.outWidth;
			//int origHeight2 = o2.outHeight;
			return BitmapFactory.decodeStream(new FileInputStream(filePath), null, o2);
		} catch (FileNotFoundException e) {
			bitmap = null;
		}finally {
			bitmap = null;
		}
		return bitmap;
	}

	//Method to Render Image with caption by Avdhesh
	public static Bitmap mark(Bitmap src, String watermark) {
		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(src, 0, 0, null);
		TextPaint myTextPaint = new TextPaint();
		Typeface tf = Typeface.create("sans-serif", Typeface.BOLD);
		myTextPaint.setColor(Color.WHITE);
		myTextPaint.setTypeface(tf);
		myTextPaint.setTextSize(15f);
		myTextPaint.setTextAlign(Paint.Align.RIGHT);
		Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
		float spacingMultiplier = 1;
		float spacingAddition = 0;
		boolean includePadding = false;
		//Calculate the positions
		int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset
		//"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
		int yPos = (int) ((canvas.getHeight()) - ((myTextPaint.descent() + myTextPaint.ascent()) / 2)) ;

		StaticLayout myStaticLayout = new StaticLayout(watermark, myTextPaint,
				yPos, alignment, spacingMultiplier, spacingAddition, includePadding);
		canvas.save();
		canvas.translate( canvas.getWidth()-10,canvas.getHeight() - myStaticLayout.getHeight() - 0.0f);
		myStaticLayout.draw(canvas);
		canvas.restore();
		return result;
	}

	public static Bitmap createVideoThumbNail(String filePath){
		Bitmap bitmap = null;
		try{
			return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
		}catch(Exception e){
			bitmap = null;
		}finally {
			bitmap = null;
		}
		return bitmap;
	}

	public static boolean checkTodaysValidation(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
		Calendar cal = Calendar.getInstance();
		String time_str = dateFormat.format(cal.getTime());
		Date d = convertStringToDate(time_str, "dd-MMM-yyyy");
		if (date.equals(d)) {
			return true;
		} else
			return false;
	}

	public static String CompareDates(String saved, String currentUpdate,String dataType) {
		String datatype = dataType;
		if(saved!=null && currentUpdate!=null) {
			try {
				String pattern = "dd-MMM-yyyy HH:mm:ss";
				SimpleDateFormat formatter = new SimpleDateFormat( pattern,Locale.ENGLISH );
				Date saveDate = formatter.parse( saved );
				Date updateDate = formatter.parse( currentUpdate );
				if (saveDate != null && updateDate != null) {
					if (saveDate.equals( updateDate )) {
						datatype = "1";
					}
					if (saveDate.before( updateDate )) {
						datatype = dataType;

					}
					if (saveDate.after( updateDate )) {
						// datatype= "1";
						datatype = dataType; // 0.1
					}
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return datatype;
	}

	public static String date() {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy",Locale.ENGLISH);
		final Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String s = format.format(date);
		return s;
	}

	public static String dateMMM() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
		final Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String s = format.format(date);
		return s;
	}

	public static String dateNotification() {
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int a = cal.get(Calendar.HOUR_OF_DAY);
		int b = cal.get(Calendar.MINUTE);
		int c = cal.get(Calendar.SECOND);

		String s = "" + month + "/" + day + "/" + year + " " + a + ":" + b
				+ ":" + c;
		return s;
	}

	public static String updateTime(int hours, int mins) {
		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);
		String hour = "";
		if (hours < 10)
			hour = "0" + hours;
		else
			hour = String.valueOf(hours);
		String aTime = new StringBuilder().append(hour).append(':')
				.append(minutes).toString();
		return aTime;
		}

	public static TextView msgText(Context context, String id, TextView tv) {
		DataBaseHelper dbHelper = new DataBaseHelper(context);
		AppPreferences mAppPreferences = new AppPreferences(context);
		dbHelper.open();
		String message = dbHelper.getMessage(id);
		tv.setTypeface(dbHelper.typeface(mAppPreferences.getLanCode()));
		tv.setText(message);
		dbHelper.close();
		return tv;
	}

	public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

		ExifInterface ei = new ExifInterface(selectedImage.getPath());
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);

		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return rotateImage(img, 90);
			case ExifInterface.ORIENTATION_ROTATE_180:
				return rotateImage(img, 180);
			case ExifInterface.ORIENTATION_ROTATE_270:
				return rotateImage(img, 270);
			default:
				//return rotateImage(img, 90);
				return img;
		}
	}

	public static Button msgButton(Context context, String id, Button bt) {
		DataBaseHelper dbHelper = new DataBaseHelper(context);
		AppPreferences mAppPreferences = new AppPreferences(context);
		dbHelper.open();
		String message = dbHelper.getMessage(id);
		bt.setTypeface(dbHelper.typeface(mAppPreferences.getLanCode()));
		bt.setText(message);
		dbHelper.close();
		return bt;
	}

	public static Typeface typeFace(Context context) {
		DataBaseHelper dbHelper = new DataBaseHelper(context);
		AppPreferences mAppPreferences = new AppPreferences(context);
		dbHelper.open();
		Typeface typeFace = dbHelper.typeface(mAppPreferences.getLanCode());
		dbHelper.close();
		return typeFace;
	}

	public static String msg(Context context, String id) {
		DataBaseHelper dbHelper = new DataBaseHelper(context);
		dbHelper.open();
		String message = dbHelper.getMessage(id);
		dbHelper.close();
		return message;
	}

	public static Toast toast(Context context, String id) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) vi.findViewById(R.id.text);
		tv.setTypeface(typeFace(context));
		tv.setText(msg(context, id));
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(vi);
		toast.show();
		return toast;
	}

	/*Added by Anshul Sharma*/
	public static Toast toastMsgCenter(Context context, String msg) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) vi.findViewById(R.id.text);
		tv.setTypeface(typeFace(context));
		tv.setText(msg);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(vi);
		toast.show();
		return toast;
	}

	public static Toast toastMsg(Context context, String msg) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) vi.findViewById(R.id.text);
		tv.setTypeface(typeFace(context));
		tv.setText(msg);
		Toast toast = new Toast(context.getApplicationContext());
		//toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(vi);
		toast.show();
		return toast;
	}

	public static Toast noToastShow(Context context, String id) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) vi.findViewById(R.id.text);
		tv.setTypeface(typeFace(context));
		tv.setText(msg(context, id));
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(vi);
		return toast;
	}

	public static boolean timeValidate(String time) {
		boolean state = true;
		int count = 0;
		String Time = time.toString();
		if (Time.length() < 5) {
			state = false;
		} else {
			for (int i = 0; i < Time.length(); i++) {
				if (Time.charAt(i) == ':') {
					count++;
				}
			}
			int pos1 = Time.indexOf(":");
			int pos2 = Time.lastIndexOf(":");
			if (count == 2) {
				if (pos1 == 0) {
					state = false;
				} else if (pos1 == pos2) {
					state = false;
				} else {
					String hr = Time.substring(0, pos1);
					if (hr.toString().length() == 0) {
						// alert("invalid time. Hour can not be greater that 12.");
						state = false;
					} else if (Integer.parseInt(hr.toString()) > 23) {
						// alert("Invalid time. Hour can not be hours less than 0.");
						state = false;
					}
					String minval = Time.substring(pos1 + 1, pos2);
					if (minval.toString().length() == 0) {
						// alert("Invalid time. Minute can not be more than 59.");
						state = false;
					} else if (Integer.parseInt(minval.toString()) > 59) {
						// alert("Invalid time. Minute can not be less than 0.");
						state = false;
					}
					String secondVal = Time.substring(pos2 + 1, Time.length());
					if (secondVal.toString().length() == 0) {
						// alert("Invalid time. Seconds can not be more than 59.");
						state = false;

					} else if (Integer.parseInt(secondVal.toString()) > 59) {
						// alert("Invalid time. Seconds can not be less than 0.");
						state = false;
					}
				}
			} else {
				state = false;
			}
		}
		return state;
	}


	/*public static boolean dateValidate(String time) {
		boolean state = true;
		int count = 0;
		String date = time.toString();
		if (date.length() >11 || date.length()<11) {
			state = false;
		} else {
			for (int i = 0; i < date.length(); i++) {
				if (date.charAt(i) == '-') {
					count++;
				}
			}
			int pos1 = date.indexOf("-");
			int pos2 = date.lastIndexOf("-");
			if (count == 2) {
				if (pos1 == 0) {
					state = false;
				} else if (pos1 == pos2) {
					state = false;
				} else {
					String hr = date.substring(0, pos1);
					if (hr.toString().length() == 0) {
						// alert("invalid time. Hour can not be greater that 12.");
						state = false;
					} else if (Integer.parseInt(hr.toString()) > 23) {
						// alert("Invalid time. Hour can not be hours less than 0.");
						state = false;
					}
					String minval = Time.substring(pos1 + 1, pos2);
					if (minval.toString().length() == 0) {
						// alert("Invalid time. Minute can not be more than 59.");
						state = false;
					} else if (Integer.parseInt(minval.toString()) > 59) {
						// alert("Invalid time. Minute can not be less than 0.");
						state = false;
					}
					String secondVal = Time.substring(pos2 + 1, Time.length());
					if (secondVal.toString().length() == 0) {
						// alert("Invalid time. Seconds can not be more than 59.");
						state = false;

					} else if (Integer.parseInt(secondVal.toString()) > 59) {
						// alert("Invalid time. Seconds can not be less than 0.");
						state = false;
					}
				}
			} else {
				state = false;
			}
		}
		return state;
	}*/

	public static void autoDateTimeSettingsAlert(final Context mContext) { // 0.2
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		// Setting Dialog Title
		alertDialog.setTitle("Date and time Settings");
		// Setting Dialog Message
		alertDialog
				.setMessage("Automatic date and time is not enabled. Please enable to continue.");
		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_DATE_SETTINGS);
						mContext.startActivity(intent);
					}
				});
		alertDialog.setCancelable(false);
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						// ((Activity) mContext).finish();
					}
				});

		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					((Activity) mContext).finish();
				}
				return false;
			}
		});
		// Showing Alert Message
		alertDialog.show();
	}

	/*
	 * check auto date and time is check or uncheck in mobile setting screen.
	 */
	public static boolean isAutoDateTime(Context context) {
		boolean status = false;
		AppPreferences mAppPreferences = new AppPreferences(context);
		String s=mAppPreferences.getAutoDateTime();
		try {
			if (Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.AUTO_TIME) == 0
					&& mAppPreferences.getAutoDateTime().equalsIgnoreCase("1")) {
				status = true;
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return status;
	}

	public static boolean isAutoDatetime(Context context) {
		boolean status = false;
		AppPreferences mAppPreferences = new AppPreferences(context);
		try {
			if (Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.AUTO_TIME) == 0) {
				status = true;
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return status;
	}

	public static void deleteNotification(final Context mContext) {
		AppPreferences mAppPreferences = new AppPreferences(mContext);
		DataBaseHelper dbHelper = new DataBaseHelper(mContext);
		dbHelper.open();
		ArrayList<String> delete_list;
		delete_list = dbHelper.getAllNotification(mAppPreferences.getUserId());
		Date d1 = null;
		Date d2 = null;
		int displayDuration = 0;
		long totalMin = 0;
		BeanAddNotification data_list;
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
		if (delete_list.size() > 0) {
			for (int i = 0; i < delete_list.size(); i++) {
				try {
					Gson gson = new Gson();
					data_list = gson.fromJson(delete_list.get(i),
							BeanAddNotification.class);
					if (data_list.getNotification_type().equalsIgnoreCase("7")||
							data_list.getNotification_type().equalsIgnoreCase("8")) {
						displayDuration = Integer.parseInt(data_list.getDisplayDuration());
						d1 = format.parse(data_list.getDropTime());
						d2 = format.parse(Utils.dateNotification());
						long diff = d2.getTime() - d1.getTime();
						long diffMinutes = diff / (60 * 1000) % 60;
						long diffHours = diff / (60 * 60 * 1000) % 24;
						long diffDays = diff / (24 * 60 * 60 * 1000);
						totalMin = (diffDays * 24 * 60) + (diffHours * 60)
								+ diffMinutes;
						if (totalMin > displayDuration) {
							dbHelper.deleteNotification(delete_list.get(i),
									mAppPreferences.getUserId());
							dbHelper.close();
						}
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}

	public static int convertMin(String time) {
		String[] split = time.split(":");
		int a = Integer.parseInt(split[0]);
		int b = Integer.parseInt(split[1]);
		int minutes = a * 60 + b;
		return minutes;

	}

	public static void allowMockSettingsAlert(final Context mContext) { // 0.2
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		// Setting Dialog Title
		alertDialog.setTitle("Developer option Settings");
		// Setting Dialog Message
		alertDialog.setMessage("Allow mock locations is enabled. Please disable to continue.");
		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
						mContext.startActivity(intent);
					}
				});
		alertDialog.setCancelable(false);
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						// ((Activity) mContext).finish();
					}
				});

		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					((Activity) mContext).finish();
				}
				return false;
			}
		});
		// Showing Alert Message
		alertDialog.show();
	}

	public static boolean isMockSettingsON(Context context) {
		if (Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
			return false;
		else
			return true;
	}

	public static void sendNotification(Context context) {
		AppPreferences mAppPreferences = new AppPreferences(context);
		Intent i = new Intent(context, ValidateUDetails.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		//DataBaseHelper dbHelper = new DataBaseHelper(context);
		//dbHelper.open();
		//dbHelper.clearFormRights();
		//dbHelper.close();
		mAppPreferences.setLoginState(0);
		mAppPreferences.saveSyncState(0);
		//mAppPreferences.setGCMRegistationId("");
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0,i, PendingIntent.FLAG_ONE_SHOT);
		NotificationCompat.Builder mNotifyBuilder;
		NotificationManager mNotificationManager;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyBuilder = new NotificationCompat.Builder(context).setContentTitle("Logout iTower App").setContentText("You've received new message.")
				.setSmallIcon(R.drawable.ic_launcher);
		//Set pending intent
		mNotifyBuilder.setContentIntent(resultPendingIntent);
		mNotifyBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
		mNotifyBuilder.setVibrate(new long[] { 1000, 1000});
		//default light
		int defaults = 0;
		defaults = defaults | Notification.DEFAULT_LIGHTS;
		mNotifyBuilder.setDefaults(defaults);
		//Spanned htmlAsSpanned = Html.fromHtml("Automatically  &quot;date time/GPS&quot;  should be enable or  &quot;mock locations&quot;  should be disable"); // used by TextView
		mNotifyBuilder.setContentText(mAppPreferences.getAppSettingNotification());
		// Set autocancel
		mNotifyBuilder.setAutoCancel(true);
		// Post a notification
		mNotificationManager.notify(9003, mNotifyBuilder.build());
		context.startActivity(i);
	}

	public static int isSchedularInfo(Context context){
		int a=0;
		String userRoleTrack="0";
		int currentTime = 0,startTime = 0,endTime = 0;
		DataBaseHelper dbHelper = new DataBaseHelper(context);
		dbHelper.open();
		userRoleTrack = dbHelper.getSubMenuRight("UserTrackOnOff", "UserTrackModule");
		dbHelper.close();
		AppPreferences mAppPreferences = new AppPreferences(context);
		String[] dataUserTrack = mAppPreferences.getUserTracking().split("\\~");
		String[] scheduleTime = dataUserTrack[1].split("\\-");
		final Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		//convertMin
		currentTime=convertMin(Utils.updateTime(hour, minute));
		startTime=convertMin(scheduleTime[0]);
		endTime=convertMin(scheduleTime[1]);
		if(dataUserTrack[0].equalsIgnoreCase("ON") && userRoleTrack.equalsIgnoreCase("1") && currentTime>startTime && currentTime<endTime && mAppPreferences.getLoginState()==1){
			a=1;
		}
		return a;
	}

	public static int isSchedularSave(Context context){
		int b=0;
		long interval=01;
		Date d1 = null;
		Date d2 = null;
		long totalMin = 0;
		long uploadMin =0;
		AppPreferences mAppPreferences = new AppPreferences(context);
		String[] dataUserTrack = mAppPreferences.getUserTracking().split("\\~");
		interval=Integer.parseInt(dataUserTrack[2]);
		//interval=120000;
		uploadMin = interval/(60*1000);

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
		try {
			d1 = format.parse(mAppPreferences.getUserTrackUploadTime());
			d2 = format.parse(Utils.dateNotification());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		long diff = d2.getTime() - d1.getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		totalMin = (diffDays * 24 * 60) + (diffHours * 60)
				+ diffMinutes;
		if (totalMin > uploadMin) {
			b=1;
		}
		return b;
	}

	public static int getIntervalInMin(Context context){
		int b=0;
		//long interval=05;
		Date d1 = null;
		Date d2 = null;
		long totalMin = 0;
		long uploadMin =03;
		AppPreferences mAppPreferences = new AppPreferences(context);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
		try {
			d1 = format.parse(mAppPreferences.getNetworkConnectionTime());
			d2 = format.parse(Utils.dateNotification());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		long diff = d2.getTime() - d1.getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		totalMin = (diffDays * 24 * 60) + (diffHours * 60)
				+ diffMinutes;
		if (totalMin > uploadMin) {
			b=1;
		}
		return b;

	}

	public static long diffDays(String fromDate,String toDate){
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = convertStringToDate(fromDate, "dd-MMM-yyyy");
			d2 = convertStringToDate(toDate, "dd-MMM-yyyy");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		long diff = d2.getTime() - d1.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		diffDays = diffDays+1;
		return  diffDays;
	}

	public static int checkSetting(Context con){
		int a=0;
		GPSTracker gps = new GPSTracker(con);
		AppPreferences mAppPreferences = new AppPreferences(con);
		areThereMockPermissionApps(con);
		if(mAppPreferences.getTrackMode()==1){
			if(gps.isMockLocation()==true){
				a=1;
				mAppPreferences.setAppSettingNotification("Uninstall "+mAppPreferences.getAppNameMockLocation()+" app/Remove Fack Location  in your mobile handset.");
			}else if(isMockSettingsON(con)==true) {
				a=1;
				mAppPreferences.setAppSettingNotification("Mock locations should be disabled.");
			}else if(isAutoDatetime(con)){
				a=1;
				mAppPreferences.setAppSettingNotification("Automatically date time should be enable");
			}else if(gps.canGetLocation()==false){
				a=1;
				mAppPreferences.setAppSettingNotification("GPS should be enable");
			}
		}
		return a;

	}

	public static String getdateTime(){
		String date="";
		final Calendar cldr = Calendar.getInstance();
		int day = cldr.get(Calendar.DAY_OF_MONTH);
		int month = cldr.get(Calendar.MONTH);
		int year = cldr.get(Calendar.YEAR);
		int hour = cldr.get(Calendar.HOUR_OF_DAY);
		int min = cldr.get(Calendar.MINUTE);
		int second = cldr.get(Calendar.SECOND);
		int mseconds = cldr.get(Calendar.MILLISECOND);
		String minutes = "";
		if (min < 10)
			minutes = "0" + min;
		else
			minutes = String.valueOf(min);

		String hours = "";
		if (hour < 10)
			hours = "0" + hour;
		else
			hours = String.valueOf(hour);

		String seconds = "";
		if (second < 10)
			seconds = "0" + second;
		else
			seconds = String.valueOf(second);



		String aTime = new StringBuilder().append(hours).append(minutes).append(seconds).append( mseconds ).toString();

		date = Utils.changeDateFormat(new StringBuilder().append(month + 1).append("/")
				.append(day).append("/").append(year)
				.toString(), "MM/dd/yyyy", "dd-MMM-yyyy");
		date=date.replaceAll( "-" ,"");
		date=date+aTime;
		return date;
	}


	public static int areThereMockPermissionApps(Context context) {
		int count = 0;
		String appName="";
		AppPreferences mAppPreferences = new AppPreferences(context);
		StringBuilder sb = new StringBuilder();
		sb.delete(0, sb.length());
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo applicationInfo : packages) {
			try {
				PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,PackageManager.GET_PERMISSIONS);
				// Get Permissions
				String[] requestedPermissions = packageInfo.requestedPermissions;
				if (requestedPermissions != null) {
					for (int i = 0; i < requestedPermissions.length; i++) {
						if (requestedPermissions[i].equals("android.permission.ACCESS_MOCK_LOCATION")
								&& !applicationInfo.packageName.equals(context.getPackageName())) {
							sb.append(packageInfo.applicationInfo.loadLabel(pm));
							sb.append(",");
							//if(packageInfo.applicationInfo.loadLabel(pm).e)
							count++;
						}
					}
				}
			} catch (NameNotFoundException e) {
				Log.e("Got exception " , e.getMessage());
			}
		}
		appName = sb.toString();
		if (appName.length() > 0) {
			appName = appName.substring(0,
					appName.length() - 1);
		}
		if(count>0){
			mAppPreferences.setAppNameMockLocation(""+appName);
		}else{
			mAppPreferences.setAppNameMockLocation("0");
		}
		return count;
	}

	public static TextView groupTV(Context con,TextView tv,String text){
		final float scale = con.getResources().getDisplayMetrics().density;
		int height = (int)(35 * scale);
		int margin = (int)(5 * scale);
		LayoutParams GNameParam = new LayoutParams( LayoutParams.MATCH_PARENT,
				height);
		GNameParam.setMargins( 0, margin, 0, margin );
		tv.setText(text);
		tv.setLayoutParams( GNameParam );
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(con.getResources().getColor(R.color.over_textcolor ));
		tv.setTextSize(15);
		tv.setBackgroundColor(con.getResources().getColor(R.color.pm_chklist_bg_group ));
		tv.setTypeface( Utils.typeFace(con ));
		return  tv;
	}

	public static TextView textViewProperty(Context con,TextView tv,String chkName){
		final float scale = con.getResources().getDisplayMetrics().density;
		int margin = (int)(10 * scale);
		LayoutParams tvPrePhotoParam = new LayoutParams( LayoutParams.MATCH_PARENT,	LayoutParams.WRAP_CONTENT );
		LayoutParams tvPostPhotoParam = new LayoutParams( LayoutParams.MATCH_PARENT,	LayoutParams.WRAP_CONTENT );
		LayoutParams TVParam = new LayoutParams( LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT );
		tvPrePhotoParam.setMargins( 10, 20, 10, 0 );
		tvPostPhotoParam.setMargins( 10, -20, 10, 0 );
		TVParam.setMargins( margin, margin, margin, 0 );
		tv.setText( "" + chkName );
		tv.setTextColor(con.getResources().getColor(R.color.textcolor));
		tv.setTextSize(15);

		if(chkName.equalsIgnoreCase( "Capture Pre Photo" )){
			tv.setLayoutParams( tvPrePhotoParam );
		}else if(chkName.equalsIgnoreCase( "Capture Post Photo" )) {
			tv.setLayoutParams( tvPostPhotoParam );
		}else{
			tv.setLayoutParams( TVParam );
		}
		tv.setPadding(0,0,0,0 );
		tv.setFocusable( true );
		tv.setFocusableInTouchMode( true );
		tv.setTypeface( Utils.typeFace(con ));
		return  tv;
	}


	public static TextView defaulttextViewProperty(Context con,TextView tv){
		final float scale = con.getResources().getDisplayMetrics().density;
		int margin = (int)(2 * scale);
		LayoutParams TVParam = new LayoutParams( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT );
		TVParam.setMargins( margin, margin, margin, 0 );
		tv.setTextColor(con.getResources().getColor(R.color.disable_textcolor));
		tv.setTextSize(15);
		tv.setLayoutParams( TVParam );
		tv.setPadding(3,0,3,0 );
		tv.setFocusable( true );
		tv.setFocusableInTouchMode( true );
		tv.setTypeface( Utils.typeFace(con ));
		return  tv;
	}

	public static TextView textViewDivider(Context con,TextView tv){
		LayoutParams TVDividerParam = new LayoutParams( LayoutParams.MATCH_PARENT,	LayoutParams.WRAP_CONTENT );
		TVDividerParam.setMargins( 0, 70, 0, 0 );
		tv.setLayoutParams(TVDividerParam);
		tv.setHeight(2);
		tv.setBackgroundColor(con.getResources().getColor(R.color.divider));
		return  tv;
	}

	public static EditText editTextProperty(Context con,EditText et){
		final float scale = con.getResources().getDisplayMetrics().density;
		int margin = (int)(10 * scale);
		int marginTop = (int)(5 * scale);
		LayoutParams InputParam = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		InputParam.setMargins( margin, marginTop, margin, 0 );
		et.setBackgroundResource( R.drawable.input_box );
		et.setPadding( margin, 0, margin, 0 );
		et.setLayoutParams( InputParam );
		et.setMinimumHeight( (int)(40 * scale) );
		et.setTypeface(Utils.typeFace(con));
		et.setTextSize(15);
		return  et;
	}

	public static Spinner spinnerProperty(Context con,Spinner spinner){
		final float scale = con.getResources().getDisplayMetrics().density;
		int margin = (int)(10 * scale);
		int marginTop = (int)(5 * scale);
		LayoutParams InputParam = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		InputParam.setMargins( margin, marginTop, margin, 0 );
		spinner.setBackgroundResource( R.drawable.doted );
		spinner.setLayoutParams( InputParam );
		spinner.setMinimumHeight( (int)(40 * scale) );
		return  spinner;
	}

	public static TextView remarksLink(Context con,TextView remark){
		final float scale = con.getResources().getDisplayMetrics().density;
		int margin = (int)(10 * scale);
		int marginTop = (int)(5 * scale);
		LayoutParams RemarkParam = new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT );
		RemarkParam.gravity = Gravity.RIGHT;
		RemarkParam.setMargins( margin, marginTop, margin, 0 );
		remark.setTextColor(con.getResources().getColor(R.color.textcolor));
		remark.setTextSize(15);
		remark.setPaintFlags( remark.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );
		remark.setLayoutParams( RemarkParam );
		return  remark;
	}

	/*public static boolean checkBeforeValidation(Date date) {
		Date current_date = new Date();
		if (date.before(current_date))
			return false;
		else
			return true;
	}
  */

	public static boolean checkBeforeValidation(Date date2) {
		final Calendar cal = Calendar.getInstance();
		int pYear = cal.get(Calendar.YEAR);
		int pMonth = cal.get(Calendar.MONTH);
		int pDay = cal.get(Calendar.DAY_OF_MONTH);

		Date date1 = Utils.convertStringToDate(new StringBuilder()
				.append(pMonth + 1).append("/").append(pDay).append("/")
				.append(pYear).toString(), "MM/dd/yyyy");

		if (date1.compareTo(date2) > 0)
			return  false;
		else
			return true;


	}

	//Validation for Next Month
	public static String checkAfterValidation(int selectedMonth,int selectedYear, Context context) {
		final Calendar cal = Calendar.getInstance();
		AppPreferences mAppPreferences = new AppPreferences(context);
		int mon = 0;

		if(mAppPreferences.getCalendarMonth()>0) {
			mon = mAppPreferences.getCalendarMonth() - 1;
		}
		cal.add(Calendar.MONTH, mon);
		int updatedYear = cal.get(Calendar.YEAR);
		int updatedMonth = cal.get(Calendar.MONTH);

		System.out.println("updatedYear=="+updatedYear);
		System.out.println("updatedMonth=="+updatedMonth);

		System.out.println("selectedYear=="+selectedYear);
		System.out.println("selectedMonth=="+selectedMonth);

		if(selectedYear>updatedYear){
			return  ""+updateDate(updatedMonth,updatedYear);
		}else if(selectedYear == updatedYear && selectedMonth>updatedMonth){
			return  ""+updateDate(updatedMonth,updatedYear);
		}else{
			return  "0";
		}
	}

	/*public static void createFolder(String fname) {
		String myfolder = Environment.getExternalStorageDirectory() + fname;
		File f = new File( myfolder );
		if (!f.exists()) {
			f.mkdirs();
		}
	}*/

	public static Button buttonUI(Context ctx,Button bt){
		final float scale = ctx.getResources().getDisplayMetrics().density;
		int height = (int)(40 * scale);
		int margin = (int)(5 * scale);
		LayoutParams GNameParam = new LayoutParams
				( LayoutParams.MATCH_PARENT, height);
		GNameParam.setMargins( 130, margin, 130, margin );
		bt.setLayoutParams( GNameParam );
		bt.setGravity(Gravity.CENTER);
		bt.setTextColor(ctx.getResources().getColor(R.color.over_textcolor ));
		bt.setTextSize(15);
		bt.setBackgroundResource( R.drawable.button_9_blue);
		bt.setTypeface( Typeface.DEFAULT_BOLD);
		bt.setAllCaps(false);
		return bt;
	}

	public static String dateTimeConversion(String sDate,Context ctx){
		try{
			AppPreferences mAppPreferences = new AppPreferences(ctx);
			String sFormat = "dd.MM.yyyy HH:mm";
			String dFormat = "yyyy-MM-dd'T'HH:mm";

			DateFormat sFormatter = new SimpleDateFormat(sFormat,Locale.ENGLISH);
			Date dt= sFormatter.parse(sDate);
			Calendar sCal = Calendar.getInstance();
			sCal.setTime(dt);
			DateFormat dFormatter = new SimpleDateFormat(dFormat,Locale.ENGLISH);
			return dFormatter.format(sCal.getTime());
		} catch(Exception exp){
			exp.printStackTrace();
			return Utils.CurrentDateTime();
		}
	}

	// To download document
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static void downloadByFileManager(Context context, String url) {
		DownloadManager downloadmanager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);

		File isAvailable =  new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + uri.getLastPathSegment())));
		// File isAvailable =  new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
		if(isAvailable.exists()){
			Toast.makeText(context, "File already downloaded ", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "Start downloading !", Toast.LENGTH_SHORT).show();
			DownloadManager.Request request = new DownloadManager.Request(uri);
			request.setTitle("File : "+uri.getLastPathSegment());
			request.setDescription("Downloading");
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			downloadmanager.enqueue(request);
		}


	}

}
