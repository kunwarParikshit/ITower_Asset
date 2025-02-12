package com.isl.itower;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.app.auth.AuthStateManager;
import com.isl.app.auth.BrowserSelectionAdapter;
import com.isl.app.auth.Configuration;
import com.isl.app.auth.TokenActivity;
import com.isl.constant.WebMethods;
import com.isl.modal.MultiLanguageList;
import com.isl.modal.ResponceLoginList;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.util.HttpUtils;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;
import com.isl.notification.ShortcutBadger;
import com.isl.modal.ServerResponse;
import com.isl.modal.ResponseForgotPassword;
import infozech.itower.R;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.AnyThread;
import androidx.annotation.ColorRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.browser.customtabs.CustomTabsIntent;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.scottyab.rootbeer.RootBeer;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;
import net.openid.appauth.browser.ExactBrowserMatcher;

/*
Modified By : Dhakan Lal on 27 Sep, 2021
Purpose     : hide login id and pass
Version     : 1.0
 */

public class ValidateUDetails extends Activity {
	private static final String TAG = "LoginActivity";
	private static final String EXTRA_FAILED = "failed";
	private static final int RC_AUTH = 100;
	private AuthorizationService mAuthService;
	private AuthStateManager mAuthStateManager;
	private Configuration mConfiguration;

	private final AtomicReference<String> mClientId = new AtomicReference<>();
	private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();
	private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();
	private CountDownLatch mAuthIntentLatch = new CountDownLatch(1);
	private ExecutorService mExecutor;
	private boolean mUsePendingIntents;

	@NonNull
	private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;


	int retryCnt = 0;
	Dialog changePassPop;
	ResponceLoginList response = null;
	TextInputEditText et_inputip;
	TextInputLayout language_layout;
	ImageView client_logo,infozech_logo;
	TextInputEditText et_username, et_password;
	TextView tv_ip_config,tv_display_msg,tv_login_text_content4;
	TextView Login;
	Spinner sp_lang_name;
	AppPreferences mAppPreferences;
	Dialog dialog_forgetPassword;
	PackageInfo pInfo = null;
	String iemi, code = "en",mobileVersion;
	String[] dataTypeID, timeStamp;
	String[] tmpDataTS = new String[3];
	ArrayList<String> lan_name = new ArrayList<>();
	ArrayList<String> lan_code = new ArrayList<>();
	Boolean time = null;
	private static final int REQUEST_CODE =100 ;
	public static final String TAG_MY_WORK = "mywork";
	private NetworkManager networkManager; //108
	LinearLayout.LayoutParams imageParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );

	Date d1,d2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkManager = new NetworkManager();//108
		///108//

		if (isEmulator() || runningOnAndroidStudioEmulator()) {
			// Display a message or take appropriate action
			Toast.makeText(this, "This app will not run on emulator and virtual device!!", Toast.LENGTH_SHORT).show();
			finish();
			return;
			// You can also disable certain features, log an event, etc.
		}
		//////108//////////
		RootBeer rootBeer = new RootBeer(ValidateUDetails.this);
		if (rootBeer.isRooted()) {
			Utils.toastMsg(ValidateUDetails.this,"This app will not work correctly on rooted devices. If you would like to use this app you must unroot this device.");
			SharedPreferences preferences =getSharedPreferences("com.isl",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.apply();
			finish();
		}
		mExecutor = Executors.newSingleThreadExecutor();
		mAuthStateManager = AuthStateManager.getInstance(this);
		mConfiguration = Configuration.getInstance(this);

		//createFolder(AppConstants.MEDIA_TEMP_PATH);
		//createFolder(AppConstants.DOC_PATH);
		//createFolder(AppConstants.PIC_PATH);

		//String s = "#21sadfs23$&%^(!9@!-";
		//String temp = s.replaceAll("\\D", "");

		//String androidId = Settings.Secure.getString(getContentResolver(),
		//		Settings.Secure.ANDROID_ID);

		//String s = Utils.currentDateTimePlusMint("dd.MM.yyyy HH:mm","yyyy-MM-dd'T'HH:mm","30.8.2021 16:40");


		mAppPreferences = new AppPreferences(ValidateUDetails.this);

		//mAppPreferences.setUserTracking("OFF~06:00~22:00~60000~120000");
		//mAppPreferences.setTrackingOnOff("OFF");
		if( FirebaseMessaging.getInstance().getToken().toString()!=null) {
			//mAppPreferences.setGCMRegistationId(  FirebaseMessaging.getInstance().getToken().toString() );
			FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
				@Override
				public void onComplete(@NonNull Task<String> task) {
					String deviceToken = task.getResult();
					mAppPreferences.setGCMRegistationId(deviceToken);
				}
			});
		}
		mobileVersion = Build.VERSION.RELEASE;
		ShortcutBadger.removeCount(ValidateUDetails.this);
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		lan_name.add("English");
		lan_name.add("Burmese");
		lan_code.add("en");
		lan_code.add("my");
		if (mAppPreferences.getLoginState() == 1) {
			Intent i = new Intent(ValidateUDetails.this, HomeActivity.class);
			startActivity(i);
			finish();
		} else if (mAppPreferences.getLoginState() == 2) {
			Intent i = new Intent(ValidateUDetails.this, Version.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		} else {
			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if(!Utils.hasPermissions(ValidateUDetails.this, AppConstants.ALL_PERMISSIONS)){
					ActivityCompat.requestPermissions(this, AppConstants.ALL_PERMISSIONS,REQUEST_CODE);
				}
			}*/

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if (!Utils.hasPermissions(this, FOREGROUND_PERMISSIONS)) {
					// Show Prominent Disclosure for Foreground Permissions
					showProminentDisclosureForForeground();
				}
			}

			setContentView(R.layout.activity_login);

			/********free space in mobile app***************/
			/*StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			double avail_sd_space = (double)stat_fs.getAvailableBlocks() *(double)stat_fs.getBlockSize();
			double GB_Available = (avail_sd_space / 1073741824);
			System.out.println("Available GB : " + GB_Available);

			*//********total bytes of heap your app is allowed to use***************//*
			Runtime rt = Runtime.getRuntime();
			long maxMemory = rt.maxMemory();
			Log.v("onCreate", "maxMemory:" + Long.toString(maxMemory));

			*//********get used byte of heap memory in my app***************//*
			long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			Log.v("onCreate", "usedMemory:" + Long.toString(usedMemory));

			*//********used heap memory in my app***************//*
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			int memoryClass = am.getMemoryClass();
			Log.v("onCreate", "memoryClass:" + Integer.toString(memoryClass));*/

			init();
			setDefaultTxt();
			//changePassPopup();
			if (!rootBeer.isRooted()) {
				clientIcon(getApplicationContext().getPackageName());
			}
			findViewById(R.id.retry).setOnClickListener((View view) ->
					mExecutor.submit(this::initializeAppAuth));
			findViewById(R.id.start_auth).setOnClickListener((View view) -> startAuth());
			((EditText)findViewById(R.id.login_hint_value)).addTextChangedListener(
					new LoginHintChangeHandler());
			if (!mConfiguration.isValid()) {
				displayError(mConfiguration.getConfigurationError(), false);
				return;
			}
			configureBrowserSelector();
			if (mConfiguration.hasConfigurationChanged()) {
				// discard any existing authorization state due to the change of configuration
				Log.i(TAG, "Configuration change detected, discarding old state");
				mAuthStateManager.replace(new AuthState());
				mConfiguration.acceptConfiguration();
			}
			if (getIntent().getBooleanExtra(EXTRA_FAILED, false)) {
				displayAuthCancelled();
			}
			displayLoading(""); //Initializing
			mExecutor.submit(this::initializeAppAuth);
			tv_ip_config.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final Dialog ip_config_dialog;
					ip_config_dialog = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
					ip_config_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
					ip_config_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
					ip_config_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					ip_config_dialog.setContentView(R.layout.custom_alert);
					final Window window_SignIn = ip_config_dialog.getWindow();
					window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
							WindowManager.LayoutParams.MATCH_PARENT);
					window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//					android.view.WindowLeaked: Activity com.isl.itower.ValidateUDetails has leaked window DecorView@c78e5ee[ValidateUDetails] that was originally added here
//					at android.view.ViewRootImpl.<init>(ViewRootImpl.java:769)
//					at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:95)
//					at android.app.Dialog.show(Dialog.java:473)
//					at com.isl.itower.ValidateUDetails$2.onClick(ValidateUDetails.java:312)
					ip_config_dialog.show();
					TextInputLayout lay = (TextInputLayout) ip_config_dialog.findViewById(R.id.ip_input);
					lay.setVisibility(View.VISIBLE);
					Button positive = (Button) ip_config_dialog.findViewById(R.id.bt_ok);
					Button negative = (Button) ip_config_dialog.findViewById(R.id.bt_cancel);
					TextView title = (TextView) ip_config_dialog.findViewById(R.id.tv_title);
					et_inputip=(TextInputEditText)ip_config_dialog.findViewById(R.id.edit1);
					et_inputip.setVisibility(View.VISIBLE);
					et_inputip.setTypeface(Utils.typeFace(ValidateUDetails.this));
					et_inputip.setText("" + AppPreferences.getConfigIP());
					et_inputip.setSelection(et_inputip.getText().length());
					positive.setTypeface(Utils.typeFace(ValidateUDetails.this));
					negative.setTypeface(Utils.typeFace(ValidateUDetails.this));
					title.setTypeface(Utils.typeFace(ValidateUDetails.this));
					title.setText("Configure IP");
					positive.setText("OK");
					negative.setText("CANCEL");
					positive.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							mAppPreferences.setConfigIP(et_inputip.getText().toString());
							if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
									new MultiLanguage(ValidateUDetails.this, "en").execute();
								ip_config_dialog.hide();
							} else {
								//Toast.makeText( ValidateUDetails.this,"No internet connection",Toast.LENGTH_LONG ).show();
								//WorkFlowUtils.toast(ValidateUDetails.this, "17");
								Utils.toastMsg(ValidateUDetails.this,"No internet connection");
							}
						}
					});
					negative.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ip_config_dialog.hide();
						}
					});
				}
			});
			/*login_sso = (TextView) findViewById(R.id.login_sso);
			login_sso.setBackgroundResource(R.drawable.button_9_blue);
			login_sso.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mAppPreferences.getConfigIP().toString().length() == 0) {
						WorkFlowUtils.toastMsg(ValidateUDetails.this,"Configure IP");
						tv_ip_config.clearFocus();
						tv_ip_config.requestFocus();
					}else{
						Intent i  = new Intent(ValidateUDetails.this, LoginActivity.class);
						startActivity(i);
						finish();
					}
				}
			});*/
			// for forgot password
			/*tv_forgot_password.setVisibility(View.INVISIBLE);
			tv_forgot_password.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					forgotPassword();
				}
			}); // end forgot password
           */
			// *********************for user login
			sp_lang_name.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0,View arg1, int pos, long arg3) {
					code = lan_code.get(pos);
					if (mAppPreferences.getConfigIP().toString().length() == 0) {
						Utils.toastMsg(ValidateUDetails.this, "First Server Configuration IP.");
						sp_lang_name.setSelection(0);
					} else {
						if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
							new MultiLanguage(ValidateUDetails.this,code).execute();
							new LogoutTask(ValidateUDetails.this).execute();
						} else {
							Utils.toastMsg(ValidateUDetails.this, "No internet connection");
						}
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			Login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Toast.makeText(ValidateUDetails.this, "1111", Toast.LENGTH_SHORT).show();
					FirebaseMessaging.getInstance().getToken()
							.addOnCompleteListener(new OnCompleteListener<String>() {
						@Override
						public void onComplete(@NonNull Task<String> task) {
							String deviceToken = task.getResult();
							mAppPreferences.setGCMRegistationId(deviceToken);
							//Toast.makeText(ValidateUDetails.this, "2222", Toast.LENGTH_SHORT).show();
						}
					});
					iemi = "";

					if (validatedefault(0)) {
						//Toast.makeText(ValidateUDetails.this, "3333", Toast.LENGTH_SHORT).show();
						if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
							//Toast.makeText(ValidateUDetails.this, "4444", Toast.LENGTH_SHORT).show();
							//108
							RetrofitApiClient.init(IApiRequest.class,getApplicationContext());
							//Toast.makeText(ValidateUDetails.this, "5555", Toast.LENGTH_SHORT).show();
							networkManager.getToken(new NetworkManager.TokenCallback() {
								@Override
								public void onTokenReceived(String token) {
									//Toast.makeText(ValidateUDetails.this, "6666", Toast.LENGTH_SHORT).show();
									new LoginTask(ValidateUDetails.this, 0,token).execute();
								}

								@Override
								public void onTokenError(String error) {
									Toast.makeText(ValidateUDetails.this, error, Toast.LENGTH_SHORT).show();
								}
							});
							//108

						} else {
							Utils.toastMsg(ValidateUDetails.this,"No internet connection");
						}
					}
				}
			});
		}
	}

//	@androidx.annotation.RequiresApi(api = Build.VERSION_CODES.O)
//	@SuppressLint("MissingPermission")
//	@Override
//	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult( requestCode, permissions, grantResults );
//
//		if(requestCode==REQUEST_CODE) {
//			for (int i = 0; i < permissions.length; i++) {
//				String permission = permissions[i];
//				int grantResult = grantResults[i];
//				if (grantResult == PackageManager.PERMISSION_DENIED) {
//					boolean showRationale = shouldShowRequestPermissionRationale( permission );
//					if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)){
//						mAppPreferences.setUserPermission("Not Allow");
//					}
//					if (! showRationale) {
//						// user also CHECKED "never ask again"
//						// you can either enable some fall back,
//						// disable features of your app
//						// or open another dialog explaining
//						// again the permission and directing to
//						// the app setting
//						//Toast.makeText( this,"is show rational false",Toast.LENGTH_SHORT ).show();
//						if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
//							((ActivityManager)this.getSystemService(ACTIVITY_SERVICE))
//									.clearApplicationUserData();
//						}
//						//ActivityCompat.requestPermissions(this, Constants.PERMISSIONS,REQUEST_CODE);
//					} else if (Manifest.permission.CAMERA.equals(permission)
//							|| Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)
//							|| Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)
//							|| Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)
//							|| Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
//						// user did NOT check "never ask again"
//						// this is a good    place to explain the user
//						// why you need the permission and ask if he wants
//						// to accept it (the rationale)
//						//Toast.makeText( this,"persmission reason",Toast.LENGTH_SHORT ).show();
//						ActivityCompat.requestPermissions(this, AppConstants.ALL_PERMISSIONS,REQUEST_CODE);
//					} else  {
//						//Toast.makeText( this,"other111",Toast.LENGTH_SHORT ).show();
//					}
//				}else{
//					if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)){
//						if (grantResults[0] == PermissionChecker.PERMISSION_DENIED && grantResults[1] == PermissionChecker.PERMISSION_DENIED) {
//							// user denies
//							mAppPreferences.setUserPermission("Not Allow");
//						}
//						if (grantResults[0] == PermissionChecker.PERMISSION_DENIED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
//							// user allow while using
//							mAppPreferences.setUserPermission("Allow Once");
//						}
//						if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
//							// user allow all the time
//							mAppPreferences.setUserPermission("Allow while using App");
//						}
//						//	mAppPreferences.setUserPermission("Allow");
//					}else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)){
//						if (grantResults[0] == PermissionChecker.PERMISSION_DENIED && grantResults[1] == PermissionChecker.PERMISSION_DENIED) {
//							// user denies
//							mAppPreferences.setUserPermission("Not Allow");
//						}
//						if (grantResults[0] == PermissionChecker.PERMISSION_DENIED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
//							// user allow while using
//							mAppPreferences.setUserPermission("Allow Once");
//						}
//						if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
//							// user allow all the time
//							mAppPreferences.setUserPermission("Allow while using App");
//						}
//					}
//					//String s = Build.getSerial();
//					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//					//iemi = telephonyManager.getDeviceId();
//
//
//				}
//			}
//		}
//	}

	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(), 0);
					return false;
				}
			});
		}
	}

	public class LoginTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		AppPreferences mAppPref;
		int login=0;
		String res,token;
		public LoginTask(Context con,int login,String token) {
			this.con = con;
			mAppPref = new AppPreferences(ValidateUDetails.this);
			this.login = login;
			this.token = token;
			//Toast.makeText(ValidateUDetails.this, "6666", Toast.LENGTH_SHORT).show();

		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (!mAppPreferences.getMaxAttendUser().equalsIgnoreCase(
						et_username.getText().toString().trim())) {
					retryCnt = 0;
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				/*nameValuePairs.add(new BasicNameValuePair("loginId",et_username.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("pwd", "11@"));
				nameValuePairs.add(new BasicNameValuePair("mId", "40"));
				nameValuePairs.add(new BasicNameValuePair("version",pInfo.versionName));
				String addParams = "deviceId=" + mAppPref.getGCMRegistationId()
						+ "~" + "emiNo=" + iemi + "~login="+login + "~ip="+getIPAddress()
						+"~language="+mAppPreferences.getLanCode()
						+"~mobileVersion="+mobileVersion
						+"~authStatus=SSO"
						+"~appId="+getApplicationContext().getPackageName();

				nameValuePairs.add(new BasicNameValuePair("addParams",addParams));
				nameValuePairs.add(new BasicNameValuePair("retryCnt", "0"));
				nameValuePairs.add(new BasicNameValuePair("languageCode", "en"));*/



				nameValuePairs.add(new BasicNameValuePair("loginId",et_username.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("pwd", et_password.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("mId", "40"));
				nameValuePairs.add(new BasicNameValuePair("version",pInfo.versionName));
				String addParams = "deviceId=" + mAppPref.getGCMRegistationId()
						+ "~" + "emiNo=" + iemi + "~login="+login + "~ip="+getIPAddress()
						+"~language="+mAppPreferences.getLanCode()
						+"~mobileVersion="+mobileVersion
						+"~authStatus=O"
						//+"~appId=tawal.com.sa";
						+"~appId="+getApplicationContext().getPackageName();
				nameValuePairs.add(new BasicNameValuePair("addParams",addParams));
				nameValuePairs.add(new BasicNameValuePair("retryCnt", ""+ retryCnt));
				nameValuePairs.add(new BasicNameValuePair("languageCode", "en"));
				res = Utils.httpPostRequest1(con,mAppPreferences.getConfigIP()+ WebMethods.url_Authenticate, nameValuePairs,token);
				//res = res.replaceAll("192.168.0.35:6008","49.205.178.202:6007");
				//System.out.println("login res = ==="+res);
				response = new Gson().fromJson(res, ResponceLoginList.class);
			} catch (Exception e) {
				e.printStackTrace();
				response = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("S")
					&& response.getDetails().get(0).getMessage().length() == 0) {
				// start 0.5
				setLoginResponce(response, 0);
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("S")
					&& response.getDetails().get(0).getMessage().length() != 0) {
				Utils.toastMsg(ValidateUDetails.this, response.getDetails()
						.get(0).getMessage());
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("V")) {
				setLoginResponce(response, 0);
				version();
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("E")) {
				Utils.toastMsg(ValidateUDetails.this, response.getDetails()
						.get(0).getMessage());
				mAppPreferences.saveUserId(String.valueOf(response.getDetails().get(0).getuId()));
				pwdExpired(response.getDetails().get(0).getMessage());
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("P")) {
				pwdExpire(response.getDetails().get(0).getMessage());
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("I")) {
				if (mAppPreferences.getMaxAttendUser().equalsIgnoreCase(
						et_username.getText().toString().trim())
						|| retryCnt == 0) {
					retryCnt = retryCnt + 1;
				} else {
					retryCnt = 0;
				}
				mAppPreferences.setMaxAttendUser(et_username.getText()
						.toString().trim());
				Utils.toastMsg(ValidateUDetails.this, response.getDetails()
						.get(0).getMessage());
			} else if (response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("F")
					|| response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("A")
					|| response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("L")
					|| response != null
					&& response.getDetails().get(0).getSuccess()
					.equalsIgnoreCase("false")) {
				Utils.toastMsg(ValidateUDetails.this, response.getDetails()
						.get(0).getMessage());
			}else if (response != null	&& response.getDetails().get(0).getSuccess().equalsIgnoreCase("M")) {
				//WorkFlowUtils.toastMsg(ValidateUDetails.this, response.getDetails().get(0).getMessage());
				alreadyLogin(response.getDetails().get(0).getMessage(),"M");
			}else if (response != null	&& response.getDetails().get(0).getSuccess().equalsIgnoreCase("N")) {
				//WorkFlowUtils.toastMsg(ValidateUDetails.this, response.getDetails().get(0).getMessage());
				alreadyLogin1(response.getDetails().get(0).getMessage(),"N",(response.getDetails().get(0).getuId()));
			}else {
				// WorkFlowUtils.ToastMessage(ValidateUDetails.this,"Server Not Available");
				Utils.toastMsg(ValidateUDetails.this, "Server Not Available");
			}
			super.onPostExecute(result);
		}
	}

	// end 0.5
	private void forgotPassword() {
		dialog_forgetPassword = new Dialog(ValidateUDetails.this,R.style.FullHeightDialog);
		dialog_forgetPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_forgetPassword.getWindow().setBackgroundDrawableResource(
				R.color.nevermind_bg_color);
		dialog_forgetPassword.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog_forgetPassword.setContentView(R.layout.forgot_password);
		final Window window_SignIn = dialog_forgetPassword.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog_forgetPassword.show();
		final EditText EditText_login_id = (EditText) dialog_forgetPassword
				.findViewById(R.id.EditText_Login);
		final EditText EditText_email = (EditText) dialog_forgetPassword
				.findViewById(R.id.EditText_email);
		final EditText EditText_mobile = (EditText) dialog_forgetPassword
				.findViewById(R.id.EditText_mobile);
		EditText_email.setTypeface(Utils.typeFace(ValidateUDetails.this));
		EditText_login_id.setTypeface(Utils.typeFace(ValidateUDetails.this));
		EditText_mobile.setTypeface(Utils.typeFace(ValidateUDetails.this));
		Button Button_submit = (Button) dialog_forgetPassword
				.findViewById(R.id.Button_submit);
		Button Button_cancel = (Button) dialog_forgetPassword
				.findViewById(R.id.Button_clear);
		TextView tv_brand_logo = (TextView) dialog_forgetPassword
				.findViewById(R.id.tv_brand_logo);
		TextView tv_login = (TextView) dialog_forgetPassword
				.findViewById(R.id.tv_login);
		TextView tv_email = (TextView) dialog_forgetPassword
				.findViewById(R.id.tv_email);
		TextView tv_or = (TextView) dialog_forgetPassword
				.findViewById(R.id.TextView_or);
		TextView tv_mobile = (TextView) dialog_forgetPassword
				.findViewById(R.id.tv_mobile);

		Utils.msgText(ValidateUDetails.this, "261", tv_brand_logo);
		Utils.msgText(ValidateUDetails.this, "1", tv_login);
		Utils.msgText(ValidateUDetails.this, "268", tv_email);
		Utils.msgText(ValidateUDetails.this, "269", tv_or);
		Utils.msgText(ValidateUDetails.this, "270", tv_mobile);
		Utils.msgButton(ValidateUDetails.this, "115", Button_submit);
		Utils.msgButton(ValidateUDetails.this, "271", Button_cancel);
		EditText_login_id.setHint(Utils.msg(ValidateUDetails.this, "1"));
		EditText_email.setHint(Utils.msg(ValidateUDetails.this, "276"));
		EditText_mobile.setHint(Utils.msg(ValidateUDetails.this, "277"));

		Button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText_email.setText("");
				EditText_login_id.setText("");
				EditText_mobile.setText("");
				EditText_login_id.clearFocus();
				EditText_login_id.requestFocus();
				EditText_login_id.setHint(Utils.msg(ValidateUDetails.this, "1"));
				EditText_email.setHint(Utils.msg(ValidateUDetails.this, "276"));
				EditText_mobile.setHint(Utils.msg(ValidateUDetails.this, "277"));
			}
		});
		Button_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mAppPreferences.getConfigIP().toString().length() == 0) {
					// Toast.makeText(ValidateUDetails.this,"Configure IP",
					// Toast.LENGTH_SHORT).show();
					Utils.toast(ValidateUDetails.this, "12");
				} else if (EditText_login_id.getText().toString().trim()
						.length() == 0) {
					// Toast.makeText(ValidateUDetails.this,
					// "Enter Login Id",Toast.LENGTH_LONG).show();
					Utils.toast(ValidateUDetails.this, "10");
				} else if (EditText_email.getText().toString().trim().length() == 0
						&& EditText_mobile.getText().toString().trim().length() == 0) {
					//

					Utils.toast(ValidateUDetails.this, "14");
				} else if (!(EditText_email.getText().toString().trim()
						.length() == 0)
						&& !Utils.isValidEmail(EditText_email.getText()
						.toString().trim())) {
					// Toast.makeText(ValidateUDetails.this,"Enter valid email address",
					// Toast.LENGTH_LONG).show();
					Utils.toast(ValidateUDetails.this, "15");
				} else if (!(EditText_mobile.getText().toString().trim()
						.length() == 0)
						&& !(EditText_mobile.getText().toString().trim()
						.length() >= 8)) {
					// Toast.makeText(ValidateUDetails.this,"Enter valid mobile Number",
					// Toast.LENGTH_LONG).show();
					Utils.toast(ValidateUDetails.this, "16");
				} else {
					if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
						new ForgotPasswordTask(ValidateUDetails.this,
								EditText_login_id.getText().toString().trim(),
								EditText_email.getText().toString().trim(),
								EditText_mobile.getText().toString().trim())
								.execute();
					} else {
						// WorkFlowUtils.ToastMessage(ValidateUDetails.this,Constants.netConnection);
						Utils.toastMsg(ValidateUDetails.this, "No internet connection");
					}
				}
			}
		});
	}

	// Beneficiary Bank not available. Amount if debited, will be reversed.
	public class ForgotPasswordTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String user_id;
		String email;
		String mobile;
		ResponseForgotPassword response_forgot = null;

		public ForgotPasswordTask(Context con, String user_id, String email,
								  String mobile) {
			this.con = con;
			this.user_id = user_id;
			this.email = email;
			this.mobile = mobile;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("userID", user_id));
				nameValuePairs.add(new BasicNameValuePair("email", email));
				nameValuePairs.add(new BasicNameValuePair("mobileNo", mobile));
				nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
				String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_forgotPWD, nameValuePairs);
				String new_res = res.replace("[", "").replace("]", "");
				response_forgot = new Gson().fromJson(new_res,
						ResponseForgotPassword.class);
			} catch (Exception e) {
				e.printStackTrace();
				response_forgot = null;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (response_forgot != null) {
				if (response_forgot.getStatus().equals("true")) {
					Utils.toastMsg(ValidateUDetails.this,
							response_forgot.getMessage());
					dialog_forgetPassword.dismiss();
				} else {
					Utils.toastMsg(ValidateUDetails.this,
							response_forgot.getMessage());
				}
			} else {
				// WorkFlowUtils.ToastMessage(ValidateUDetails.this,"Server Not Available");
				Utils.toastMsg(ValidateUDetails.this, "Server Not Available");
			}
			super.onPostExecute(result);
		}
	};

	// Start 0.1
	public class LogoutTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String res;
		ResponceLoginList response = null;

		public LogoutTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Logging Out...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("userId",
						mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
				res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_logout,nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			DataBaseHelper dbHelper = new DataBaseHelper(ValidateUDetails.this);
			dbHelper.open();
			dbHelper.clearFormRights();
			dbHelper.close();
			mAppPreferences.setLoginState(0);
			mAppPreferences.saveSyncState(0);
			//mAppPreferences.setGCMRegistationId("");
		}
	}

	public void setLoginResponce(ResponceLoginList response, int r) { // pass expired
		DataBaseHelper dbHelper = new DataBaseHelper(ValidateUDetails.this);
		dbHelper.open();
		mAppPreferences.setLoginId(et_username.getText().toString().trim());
		mAppPreferences.setPassword(et_password.getText().toString());
		mAppPreferences.setPopUp("show popup"); // 0.2
		mAppPreferences.setNotificationTone("default");// 0.2
		mAppPreferences.setNotificationToneName("Default Tone");// 0.2
		mAppPreferences.setTTAssignRb("off");
		mAppPreferences.setTTUpdateRb("off");
		mAppPreferences.setTTEscalateRb("off");
		mAppPreferences.setPMScheduleRb("off");
		mAppPreferences.setPMEscalateRb("off");
		mAppPreferences.setRegionId(response.getDetails().get(0).getrId());
		mAppPreferences.setRoleId(response.getDetails().get(0).getRoleId());
		//mAppPreferences.setName(response.getDetails().get(0).getName());

		if(response.getDetails().get(0).getName().contains("~")){
			//System.out.println("************** Name - "+response.getDetails().get(0).getName());
			String [] arr = response.getDetails().get(0).getName().split("~");
			mAppPreferences.setName(arr[0]);
			mAppPreferences.setUserMailid(arr[1]);
			mAppPreferences.setUserNumber(arr[2]);
			mAppPreferences.setUserGroup(arr[3]);
			mAppPreferences.setUserGroupName(arr[4]);
		}

		mAppPreferences.setLastLogin(response.getDetails().get(0).getLastLogin());
		mAppPreferences.saveUserId(String.valueOf(response.getDetails().get(0).getuId()));
		mAppPreferences.setCounrtyID(response.getDetails().get(0).getCnId());
		mAppPreferences.setHubID(response.getDetails().get(0).gethId());
		mAppPreferences.setCircleID(response.getDetails().get(0).getCrId());
		mAppPreferences.setZoneID(response.getDetails().get(0).getzId());
		mAppPreferences.setClusterID(response.getDetails().get(0).getClId());
		mAppPreferences.setPIOMEID(response.getDetails().get(0).getOmId());
		mAppPreferences.setDataTS(response.getDetails().get(0).getDataTS());
		mAppPreferences.setUserCategory(response.getDetails().get(0).getUserCategory());  //1.0
		mAppPreferences.setUserSubCategory(response.getDetails().get(0).getUserSubCategory());	//1.0

		// PM images Configuration
		mAppPreferences.setPmConfiguration(response.getDetails().get(0).getPMConfiguration());
		String pmConfig = mAppPreferences.getPmConfiguration();
		if (pmConfig.length() != 0) {
			String baseArray[] = pmConfig.split("@");
			int activityID = 0, paramID = 0;
			String paramName = "";
			dbHelper.clearpmconfig();
			if (baseArray.length > 0) {
				for (int i = 0; i < baseArray.length; i++) {
					String tmpArray[] = baseArray[i].split("\\$");
					activityID = Integer.parseInt(tmpArray[0].toString());
					tmpArray = tmpArray[1].split("\\#");
					paramID = Integer.parseInt(tmpArray[0].toString());
					paramName = tmpArray[1].toString();
					dbHelper.insertPmConfiguration(activityID, paramID,paramName);
				}
			}
		}

		// TT Configuration
		mAppPreferences.setTTConfiguration(response.getDetails().get(0).getTtconfiguration());
		String ttConfig = mAppPreferences.getTTConfiguration();
		if (ttConfig.length() != 0) {
			String baseArray[] = ttConfig.split("@");
			if (baseArray.length > 2) {
				mAppPreferences.setTTminimage(Integer.parseInt(baseArray[0]));
				mAppPreferences.setTTmaximage(Integer.parseInt(baseArray[1]));
				mAppPreferences.setTTimageMessage(baseArray[2]);
				mAppPreferences.setTTMediaFileType(baseArray[3]);
				//mAppPreferences.setTTMediaFileType("jpg,mp4,jpg,mp4,jpg");
			}
		}

		// FF Configuration
		mAppPreferences.setFFConfiguration(response.getDetails().get(0).getFfconfiguration());
		String ffConfig = mAppPreferences.getFFConfiguration();
		/*if (ffConfig.length() != 0) {
			String baseArray[] = ffConfig.split("@");
			if (baseArray.length > 2) {
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

		if (response.getDetails().get(0).getUserTracking().length() != 0) {
			mAppPreferences.setUserTracking(response.getDetails().get(0).getUserTracking());
			String[] dataUserTrack = response.getDetails().get(0).getUserTracking().split("\\~");
			mAppPreferences.setTrackingOnOff(dataUserTrack[0]);
			mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
		}

		try {
			if (response.getDetails().get( 0 ).getDistanceRange().length() != 0) {
				String[] dataTS = response.getDetails().get( 0 ).getDistanceRange().split( "\\~" );
				if (dataTS.length > 11) {
					mAppPreferences.setEnableFillingField( dataTS[0] );
					mAppPreferences.setSiteMotorable( Integer.parseInt( dataTS[1] ) );
					mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt( dataTS[2] ) );
					mAppPreferences.setSiteNameEnable( Integer.parseInt( dataTS[3] ) );
					mAppPreferences.setAutoDateTime( dataTS[4] );
					mAppPreferences.setSearchTTDateRange( Integer.parseInt( dataTS[5] ) );
					mAppPreferences.setEnablePrePopulateSitesTT( Integer.parseInt( dataTS[6] ) );
					mAppPreferences.setPMImageUploadType( Integer.parseInt( dataTS[7] ) );
					mAppPreferences.setPMRejectMadatoryFields( Integer.parseInt( dataTS[8]));
					mAppPreferences.setPMReviewPlanDate( Integer.parseInt(dataTS[9] ));
					mAppPreferences.setVideoUploadMaxSize( Integer.parseInt(dataTS[10]));
					mAppPreferences.setIsVideoCompress(Integer.parseInt(dataTS[11]));
					mAppPreferences.setOperatorWiseUserField(dataTS[12]);
					mAppPreferences.setHyperLinkPM(dataTS[13]);
					if (dataTS.length > 14) {
						mAppPreferences.setCalendarMonth(Integer.parseInt(dataTS[14]));
					}
					if (dataTS.length > 15) {
						Log.d("documentize",dataTS[15]);
						mAppPreferences.setDocumentUploadMaxSize(Integer.parseInt(dataTS[15]));
					}
					if(dataTS.length>16)
					{
						mAppPreferences.setCheckIn(String.valueOf(dataTS[16]));
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
			mAppPreferences.setVideoUploadMaxSize(Integer.parseInt("5"));
			mAppPreferences.setDocumentUploadMaxSize(1);
			mAppPreferences.setIsVideoCompress(Integer.parseInt("0"));
			mAppPreferences.setOperatorWiseUserField("N");
			mAppPreferences.setHyperLinkPM("0");
		}

		mAppPreferences.setSrvcTime(response.getDetails().get(0).getSrvcTime());
		// mAppPreferences.setSrvcTime(120000);
		mAppPreferences.setToggleButton("yes");
		if (response.getDetails().get(0).getDataTS().length() != 0) {
			String[] dataTS = mAppPreferences.getDataTS().split(",");
			dataTypeID = new String[dataTS.length];
			timeStamp = new String[dataTS.length];
			for (int i = 0; i < dataTS.length; i++) {
				tmpDataTS = dataTS[i].split("\\~");
				dataTypeID[i] = tmpDataTS[0];
				timeStamp[i] = tmpDataTS[1];

				//System.out.println("Data Type Id - "+tmpDataTS[0]);
			}
			if (!mAppPreferences.getFirstTimeRunApp().equalsIgnoreCase("A")) {
				dbHelper.clearDataTS();
				dbHelper.dataTS(dataTypeID, timeStamp, "", "", 0,"0");
				dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),0);
				mAppPreferences.setFirstTimeRunApp("A");
			} else {
				dbHelper.dataTS(dataTypeID, timeStamp, "", "", 1,"0");
				dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),1);
			}
		}
		if (response.getForm()!=null && response.getForm().size() > 0) {
			//scheduleAlarm(); // call service for check app version
			AppConstants.moduleList.clear();
			dbHelper.clearFormRights();
			dbHelper.insertFormRight(response.getForm());
			if ((response.getDetails().get(0).getPassResetDate().length() != 0 && response
					.getDetails().get(0).getMessage().length() == 0)
					|| (response.getDetails().get(0).getPassResetDate()
					.length() != 0 && r == 1)) {
				mAppPreferences.setLoginState(1);
				Utils.toastMsg(ValidateUDetails.this, "Login Successfully");
				Intent i = new Intent(ValidateUDetails.this, HomeActivity.class);
				startActivity(i);
				finish();
			} else {
				changePassPopup();
			}
		} else {
			Utils.toastMsg(ValidateUDetails.this, "You are not authorized to access modules.");
			new LogoutTask(ValidateUDetails.this).execute();
		}
		dbHelper.close();
		scheduleWork(TAG_MY_WORK,mAppPreferences.getSrvcTime());
	}

	public void pwdExpire(String msg) { // open pop up for passward expire next day
		final Dialog actvity_dialog = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();
		Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
		Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
		tv_header.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		positive.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		negative.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setText(msg);
		positive.setText("YES");
		negative.setText("NO");


		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				changePassPopup();

			}
		});

		negative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				setLoginResponce(response, 1);
			}
		});




		/*AlertDialog.Builder alert = new AlertDialog.Builder(
				ValidateUDetails.this);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.custom_alert, null);
		Button positive = (Button) view.findViewById(R.id.bt_ok);
		Button negative = (Button) view.findViewById(R.id.bt_cancel);
		TextView title = (TextView) view.findViewById(R.id.tv_title);
		EditText et = (EditText) view.findViewById(R.id.et_ip);
		et.setVisibility(View.GONE);
		positive.setTypeface(Utils.typeFace(ValidateUDetails.this));
		negative.setTypeface(Utils.typeFace(ValidateUDetails.this));
		title.setTypeface(Utils.typeFace(ValidateUDetails.this));
		title.setText(msg);
		positive.setText("OK");
		negative.setText("CANCEL");
		alert.setView(view);
		final AlertDialog alertDialog = alert.create();
		alertDialog.show();

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
				changePassPopup();
			}
		});

		negative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
				setLoginResponce(response, 1);
			}
		});*/
	}

	public void pwdExpired(String msg) { // open pop up for passward expired
		final Dialog actvity_dialog = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();
		Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
		Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
		tv_header.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		positive.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		negative.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setText(msg);
		positive.setVisibility(View.GONE);
		negative.setText("OK");


		negative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				changePassPopup();

			}
		});

	}

	public void alreadyLogin(String msg,String flag) { // open pop up for passward expired
		final Dialog actvity_dialog = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();
		Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
		Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
		tv_header.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		positive.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		negative.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setText(msg);
		positive.setText("YES");
		negative.setText("NO");

		if(flag.equalsIgnoreCase("N")){
			positive.setVisibility(View.GONE);
			negative.setText("OK");
		}

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				//108
				networkManager.getToken(new NetworkManager.TokenCallback() {
					@Override
					public void onTokenReceived(String token) {
						new LoginTask(ValidateUDetails.this, 0,token).execute();
					}

					@Override
					public void onTokenError(String error) {
						Toast.makeText(ValidateUDetails.this, error, Toast.LENGTH_SHORT).show();
					}
				});
				//108
				//new LoginTask(ValidateUDetails.this,1).execute();

			}
		});

		negative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();

			}
		});
	}

	public void alreadyLogin1(String msg,String flag,String userId) { // open pop up for passward expired
		final Dialog actvity_dialog = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.back_confirmation_alert);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();
		Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
		Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
		TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
		tv_header.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		positive.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		negative.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setTypeface( Utils.typeFace( ValidateUDetails.this ) );
		title.setText(msg);
		positive.setText("YES");
		negative.setText("NO");

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
						new LogoutAuth2(ValidateUDetails.this,userId).execute();
						//new LoginTask(ValidateUDetails.this, 0,token).execute();
				//new LoginTask(ValidateUDetails.this,1).execute();

			}
		});

		negative.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();

			}
		});
	}

	public void version() { // check app version
		mAppPreferences.setLoginState(2);
		Intent i = new Intent(ValidateUDetails.this, Version.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}

	// open pop up for change passward
	public void changePassPopup() {
		changePassPop = new Dialog(ValidateUDetails.this, R.style.FullHeightDialog);
		changePassPop.requestWindowFeature(Window.FEATURE_NO_TITLE);
		changePassPop.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		changePassPop.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		changePassPop.setContentView(R.layout.change_passward);
		final Window window_SignIn = changePassPop.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		changePassPop.show();
		final TextInputEditText et_old_pwd = (TextInputEditText) changePassPop.findViewById(R.id.et_old_pwd);
		et_old_pwd.setFocusableInTouchMode(true);
		final EditText et_new_pwd = (EditText) changePassPop.findViewById(R.id.et_new_pwd);
		final EditText et_retry_pwd = (EditText) changePassPop.findViewById(R.id.et_retry_pwd);
		Button bt_save = (Button) changePassPop.findViewById(R.id.bt_save);
		Button bt_cancel = (Button) changePassPop.findViewById(R.id.bt_cancel);
		TextView tv_brand_logo = (TextView) changePassPop.findViewById(R.id.tv_brand_logo);
		tv_brand_logo.setText("Change Password");
		bt_cancel.setText("Cancel");
		bt_save.setText("Save");

		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changePassPop.dismiss();
				mAppPreferences.setTTAssignRb("off");
				mAppPreferences.setTTUpdateRb("off");
				mAppPreferences.setTTEscalateRb("off");
				mAppPreferences.setPMScheduleRb("off");
				mAppPreferences.setPMEscalateRb("off");
				DataBaseHelper db = new DataBaseHelper(ValidateUDetails.this);
				db.open();
				db.clearFormRights();
				db.close();
				mAppPreferences.setLoginState(0);
				mAppPreferences.saveSyncState(0);
				//mAppPreferences.setGCMRegistationId("");
			}
		});
		bt_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (et_old_pwd.getText().toString().trim().length() == 0) {
					//Toast.makeText(ValidateUDetails.this,
					// "Enter Old Password.",Toast.LENGTH_LONG).show();
					Utils.toastMsg(ValidateUDetails.this, "Enter Old Password.");
				} else if (et_new_pwd.getText().toString().trim().length() == 0) {
					//Toast.makeText(ValidateUDetails.this,
					//"Enter New Password.",Toast.LENGTH_LONG).show();
					Utils.toastMsg(ValidateUDetails.this, "Enter New Password.");
				} else if (et_retry_pwd.getText().toString().trim().length() == 0) {
					// Toast.makeText(ValidateUDetails.this,"Enter Retype Password.",Toast.LENGTH_LONG).show();
					Utils.toastMsg(ValidateUDetails.this, "Enter Retype Password.");
				} else if (!(et_new_pwd.getText().toString().trim()
						.equals(et_retry_pwd.getText().toString().trim()))) {
					// Toast.makeText(ValidateUDetails.this,"Password does not match with Retype Password.",Toast.LENGTH_LONG).show();
					Utils.toastMsg(ValidateUDetails.this, "Password does not match with Retype Password.");
				} else if (et_old_pwd.getText().toString().trim()
						.equals(et_new_pwd.getText().toString().trim())) {
					// Toast.makeText(ValidateUDetails.this,"Enter New Password different from Old Password.",Toast.LENGTH_LONG).show();
					Utils.toastMsg(ValidateUDetails.this, "Enter New Password different from Old Password.");
				} else {
					if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
						new ChangePassward(ValidateUDetails.this,
								mAppPreferences.getUserId(), et_old_pwd
								.getText().toString().trim(),
								et_new_pwd.getText().toString().trim())
								.execute();
					} else {
						// WorkFlowUtils.ToastMessage(ValidateUDetails.this,Constants.netConnection);
						Utils.toastMsg(ValidateUDetails.this, "No internet connection");
					}
				}
			}
		});
	}

	// call webservice for change passward
	public class ChangePassward extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String user_id;
		String pwd;
		String newpwd;
		ServerResponse serverResponse;

		public ChangePassward(Context con, String user_id, String pwd,
							  String newpwd) {
			this.con = con;
			this.user_id = user_id;
			this.pwd = pwd;
			this.newpwd = newpwd;
		}

		@Override		protected void onPreExecute() {

			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("userID", user_id));
				nameValuePairs.add(new BasicNameValuePair("pwd", pwd));
				nameValuePairs.add(new BasicNameValuePair("newPwd", newpwd));
				nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
				nameValuePairs.add(new BasicNameValuePair("loginId", et_username.getText().toString().trim()));
				String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_chngPWD,nameValuePairs);
				String new_res = res.replace("[", "").replace("]", "");
				serverResponse = new Gson().fromJson(new_res, ServerResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				serverResponse = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (serverResponse != null) {
				if (serverResponse.getSuccess().equals("true")) {
					Utils.toastMsg(ValidateUDetails.this, serverResponse.getMessage());
					changePassPop.dismiss();
				} else {
					Utils.toastMsg(ValidateUDetails.this, serverResponse.getMessage());
				}
			} else {
				// WorkFlowUtils.ToastMessage(ValidateUDetails.this,"Server Not Available");
				Utils.toastMsg(ValidateUDetails.this, "Server Not Available");
			}
			super.onPostExecute(result);
		}
	};

	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_text, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
		spinner.setAdapter(dataAdapter);
	}

	public class MultiLanguage extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String res, code;
		MultiLanguageList multiLanguageList = null;

		public MultiLanguage(Context con, String code) {
			this.con = con;
			this.code = code;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}
		//The 'label' start tag on line 1692 position 2 does not match the end tag of 'labels'. Line 1801, position 5.

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("languageCode", code));
				res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetBaseData,nameValuePairs);
				Gson gson = new Gson();
				multiLanguageList = gson.fromJson(res, MultiLanguageList.class);
			} catch (Exception e) {
				e.printStackTrace();
				multiLanguageList = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			DataBaseHelper dbHelper = new DataBaseHelper(ValidateUDetails.this);
			dbHelper.open();
			dbHelper.clearMultiLanguage();
			if ((multiLanguageList == null)) {
				// Toast.makeText(ValidateUDetails.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
				Utils.toastMsg(ValidateUDetails.this, "Caption Level not provided by server, Please check Server Configuration IP.");
			} else if (multiLanguageList.getLanguageList()!=null && multiLanguageList.getLanguageList().size() > 0) {
				mAppPreferences.setLanCode(code);
				dbHelper.insertMultiLanguage(multiLanguageList.getLanguageList());
				setDefaultTxt();
			} else {
				// Toast.makeText(ValidateUDetails.this,
				// "Server Not Available",Toast.LENGTH_LONG).show();
				Utils.toastMsg(ValidateUDetails.this, "Caption Level not provided by server, Please check Server Configuration IP.");

			}
			dbHelper.close();
		}
	}
	public void init() {
		client_logo = (ImageView) findViewById( R.id.client_logo);
		infozech_logo = (ImageView) findViewById( R.id.infozech_logo);
		//infozech_logo.setBackgroundResource(R.drawable.infozech_logo);
		language_layout = (TextInputLayout) findViewById( R.id.language_layout);
		tv_display_msg = (TextView)findViewById(R.id.tv_display_msg);
		tv_ip_config = (TextView) findViewById(R.id.tv_ip_config);
		tv_ip_config.setPaintFlags(tv_ip_config.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);

		tv_login_text_content4 = (TextView) findViewById(R.id.tv_login_text_content4);
		et_username = (TextInputEditText) findViewById(R.id.username);
		et_username.setText(mAppPreferences.getLoginId());
		et_username.setSelection(et_username.getText().length());
		et_username.setTypeface(Utils.typeFace(ValidateUDetails.this));
		et_password = (TextInputEditText) findViewById(R.id.password);
		et_password.setText(mAppPreferences.getPassword());
		et_password.setSelection(et_password.getText().length());
		et_password.setTypeface(Utils.typeFace(ValidateUDetails.this));
		sp_lang_name = (Spinner) findViewById(R.id.sp_lang_name);
		sp_lang_name.setBackgroundResource(R.drawable.input_box );
		addItemsOnSpinner(sp_lang_name, lan_name);
		int pos = lan_code.indexOf(mAppPreferences.getLanCode());
		sp_lang_name.setSelection(pos);
		Login = (TextView) findViewById(R.id.login);
		//Login.setBackgroundResource(R.drawable.button_secondary);
	}

	public void setDefaultTxt() {

		if (mAppPreferences.getLanCode().length() != 0) {
			et_username.setTypeface(Utils.typeFace(ValidateUDetails.this));
			et_password.setTypeface(Utils.typeFace(ValidateUDetails.this));
			Login.setTypeface(Utils.typeFace(ValidateUDetails.this));
			tv_ip_config.setText("Server Configuration");
			Login.setText("Login");
			tv_display_msg.setTypeface(Utils.typeFace(ValidateUDetails.this));
			Utils.msgText(ValidateUDetails.this, "318", tv_display_msg);
		}
	}

	private boolean validatedefault(int loginType) { //0 means normal login and 1 mean azure login
		DataBaseHelper db = new DataBaseHelper(ValidateUDetails.this);
		db.open();
		int isCaptionLevel = db.isLevel();
		db.close();
		if (mAppPreferences.getConfigIP().toString().length() == 0) {
			//Toast.makeText(ValidateUDetails.this, "First Configure IP",
			//		Toast.LENGTH_SHORT).show();
			Utils.toastMsg(ValidateUDetails.this,"First Server Configuration IP.");
			tv_ip_config.clearFocus();
			tv_ip_config.requestFocus();
			return false;
		}else if (et_username.getText().toString().trim().equals("") && loginType==0) {
			//Toast.makeText(ValidateUDetails.this, "Enter Login ID",
			//		Toast.LENGTH_SHORT).show();
			Utils.toastMsg(ValidateUDetails.this,"Enter Login ID");
			et_username.clearFocus();
			et_username.requestFocus();
			return false;
		} else if (et_password.getText().toString().equals("") && loginType == 0 ) {
			//Toast.makeText(ValidateUDetails.this, "Enter Password",
			//		Toast.LENGTH_SHORT).show();
			Utils.toastMsg(ValidateUDetails.this,"Enter Password");
			et_password.clearFocus();
			et_password.requestFocus();
			return false;
		} else if(isCaptionLevel==0){
			Utils.toastMsg(ValidateUDetails.this,"Caption level reload please wait, retry login.");
			new MultiLanguage(ValidateUDetails.this,code).execute();
			return false;
		}
		return  true;
	}

	public void FackApp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ValidateUDetails.this);
		builder.setMessage("Uninstall "+mAppPreferences.getAppNameMockLocation()+" app/Remove Fack Location  in your mobile handset.");
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public String getIPAddress()
	{
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()) ) {
						return  inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {

		}
		return null;
	}

	public static void scheduleWork(String tag, int interval) {
		PeriodicWorkRequest.Builder periodicRequest = new PeriodicWorkRequest.Builder( BackGroundWork.class, interval, TimeUnit.MINUTES, 5,
				TimeUnit.MINUTES );
		PeriodicWorkRequest myWork = periodicRequest.build();
		WorkManager.getInstance().enqueue( myWork );
		WorkManager instance = WorkManager.getInstance();
		instance.cancelAllWorkByTag( tag );
		instance.enqueueUniquePeriodicWork( tag, ExistingPeriodicWorkPolicy.REPLACE, myWork );
	}

	public void clientIcon(String appId){
		switch (appId) {
			case "tawal.com.sa" :
				language_layout.setVisibility(View.GONE);
				//start 1.0
            	tv_login_text_content4.setVisibility(View.GONE);
				//infozech_logo.setBackgroundResource(R.drawable.mast_login_img);
				infozech_logo.setBackgroundResource(R.drawable.tawal_login_img);
				infozech_logo.setLayoutParams(imageParam);
				client_logo.setBackgroundResource(R.drawable.tawal);
				client_logo.setVisibility(View.GONE);
				Login.setBackgroundResource(R.drawable.button_secondary);

				////Login visibilty for uat make visible and for product make invisible

				Login.setVisibility(View.VISIBLE);
				et_username.setVisibility(View.VISIBLE);
				et_password.setVisibility(View.VISIBLE);

				/*Login.setVisibility(View.GONE);
				et_username.setVisibility(View.GONE);
				et_password.setVisibility(View.GONE);*/

				findViewById(R.id.start_auth).setVisibility(View.VISIBLE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				//end 1.0
				if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
					new MultiLanguage(ValidateUDetails.this, "en").execute();
				}

				break;
			case "infozech.tawal" :
			/*	language_layout.setVisibility(View.VISIBLE);
				infozech_logo.setBackgroundResource(R.drawable.midc_logo);
				client_logo.setBackgroundResource(R.drawable.tawal);
				client_logo.setVisibility(View.GONE);
				Login.setBackgroundResource(R.drawable.button_secondary);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				*/
				/*if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
					new MultiLanguage(ValidateUDetails.this, "en").execute();
				}*//*
				break;*/

				int width = LinearLayout.LayoutParams.MATCH_PARENT;
				int hieght = LinearLayout.LayoutParams.WRAP_CONTENT;
				LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(width,hieght);
				tv_ip_config.setLayoutParams(ip);
				tv_ip_config.setGravity(Gravity.CENTER);
				language_layout.setVisibility(View.GONE);
				tv_login_text_content4.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.midc_logo);
				infozech_logo.setLayoutParams(imageParam);
				client_logo.setBackgroundResource(R.drawable.tawal);
				client_logo.setVisibility(View.GONE);
				Login.setBackgroundResource(R.drawable.button_secondary);

				Login.setVisibility(View.GONE);
				et_username.setVisibility(View.GONE);
				et_password.setVisibility(View.GONE);

				findViewById(R.id.start_auth).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.start_auth)).setText("    Login    ");
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				//end 1.0
				if (Utils.isNetworkAvailable(ValidateUDetails.this)) {
					new MultiLanguage(ValidateUDetails.this, "en").execute();
				}
				break;
			case "infozech.safari" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.infozech_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
			case "apollo.com.sa" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.appollo_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
			case "voltalia.com.sa" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.voltalia_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
			case "infozech.zamil" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.voltalia_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
			case "ock.com.sa" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.ock_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
			case "eft.com.sa" :
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.eft_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);
				findViewById(R.id.start_auth).setVisibility(View.GONE);
				findViewById(R.id.tv_other).setVisibility(View.GONE);
				break;
		}
	}
	/*public void thawal(){
			if(getApplicationContext().getPackageName().equalsIgnoreCase("infozech.tawal")
					||getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa"))	{
				language_layout.setVisibility(View.GONE);
				infozech_logo.setBackgroundResource(R.drawable.infozech_logo);
				client_logo.setBackgroundResource(R.drawable.tawal);

				Login.setBackgroundResource(R.drawable.button_secondary);
				if (WorkFlowUtils.isNetworkAvailable(ValidateUDetails.this)) {
					new MultiLanguage(ValidateUDetails.this, "en").execute();
				}
			}else{
				language_layout.setVisibility(View.VISIBLE);
				client_logo.setVisibility(View.GONE);
				//client_logo.setBackgroundResource(R.drawable.appollo);
				infozech_logo.setBackgroundResource(R.drawable.appollo_logo);
				Login.setBackgroundResource(R.drawable.button_9_blue);

			}
		}*/

	public void createFolder(String fname) {
		String myfolder = Environment.getExternalStorageDirectory() + fname;
		File f = new File( myfolder );
		if (!f.exists())
			if (!f.mkdir()) {
			}
	}



	@Override
	protected void onStart() {
		super.onStart();

		if (mExecutor.isShutdown()) {
			mExecutor = Executors.newSingleThreadExecutor();
		}

	}


	@Override
	protected void onStop() {
		super.onStop();
		mExecutor.shutdownNow();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mAuthService != null) {
			mAuthService.dispose();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		displayAuthOptions();
		if (resultCode == RESULT_CANCELED) {
			displayAuthCancelled();
		} else {
			Intent intent = new Intent(this, TokenActivity.class);
			intent.putExtras(data.getExtras());
			startActivity(intent);
		}
	}

	@MainThread
	void startAuth() {

		if (!validatedefault(1)) {
			return;
		}



		displayLoading("Loading..."); //Making authorization request

		mUsePendingIntents = ((CheckBox) findViewById(R.id.pending_intents_checkbox)).isChecked();

		// WrongThread inference is incorrect for lambdas
		// noinspection WrongThread
		mExecutor.submit(this::doAuth);
	}

	/**
	 * Initializes the authorization service configuration if necessary, either from the local
	 * static values or by retrieving an OpenID discovery document.
	 */
	@WorkerThread
	private void initializeAppAuth() {
		Log.i(TAG, "Initializing AppAuth");
		recreateAuthorizationService();

		if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
			// configuration is already created, skip to client initialization
			Log.i(TAG, "auth config already established");
			initializeClient();
			return;
		}

		// if we are not using discovery, build the authorization service configuration directly
		// from the static configuration values.
		if (mConfiguration.getDiscoveryUri() == null) {
			Log.i(TAG, "Creating auth config from res/raw/auth_config.json");
			AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
					mConfiguration.getAuthEndpointUri(),
					mConfiguration.getTokenEndpointUri(),
					mConfiguration.getRegistrationEndpointUri());

			mAuthStateManager.replace(new AuthState(config));
			initializeClient();
			return;
		}

		// WrongThread inference is incorrect for lambdas
		// noinspection WrongThread

		runOnUiThread(() -> displayLoading("Retrieving discovery document"));
		Log.i(TAG, "Retrieving OpenID discovery doc");
		AuthorizationServiceConfiguration.fetchFromUrl(
				mConfiguration.getDiscoveryUri(),
				this::handleConfigurationRetrievalResult,
				mConfiguration.getConnectionBuilder());
	}

	@MainThread
	private void handleConfigurationRetrievalResult(
			AuthorizationServiceConfiguration config,
			AuthorizationException ex) {
		if (config == null) {
			Log.i(TAG, "Failed to retrieve discovery document", ex);
			displayError("Failed to retrieve discovery document: " + ex.getMessage(), true);
			return;
		}

		Log.i(TAG, "Discovery document retrieved");
		mAuthStateManager.replace(new AuthState(config));
		mExecutor.submit(this::initializeClient);
	}

	/**
	 * Initiates a dynamic registration request if a client ID is not provided by the static
	 * configuration.
	 */
	@WorkerThread
	private void initializeClient() {
		if (mConfiguration.getClientId() != null) {
			Log.i(TAG, "Using static client ID: " + mConfiguration.getClientId());
			// use a statically configured client ID
			mClientId.set(mConfiguration.getClientId());
			runOnUiThread(this::initializeAuthRequest);
			return;
		}

		RegistrationResponse lastResponse =
				mAuthStateManager.getCurrent().getLastRegistrationResponse();
		if (lastResponse != null) {
			Log.i(TAG, "Using dynamic client ID: " + lastResponse.clientId);
			// already dynamically registered a client ID
			mClientId.set(lastResponse.clientId);
			runOnUiThread(this::initializeAuthRequest);
			return;
		}

		// WrongThread inference is incorrect for lambdas
		// noinspection WrongThread
		runOnUiThread(() -> displayLoading("Dynamically registering client"));
		Log.i(TAG, "Dynamically registering client");

		RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
				mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
				Collections.singletonList(mConfiguration.getRedirectUri()))
				.setTokenEndpointAuthenticationMethod( ClientSecretBasic.NAME)
				.build();

		mAuthService.performRegistrationRequest(
				registrationRequest,
				this::handleRegistrationResponse);
	}

	@MainThread
	private void handleRegistrationResponse(
			RegistrationResponse response,
			AuthorizationException ex) {
		mAuthStateManager.updateAfterRegistration(response, ex);
		if (response == null) {
			Log.i(TAG, "Failed to dynamically register client", ex);
			displayErrorLater("Failed to register client: " + ex.getMessage(), true);
			return;
		}

		Log.i(TAG, "Dynamically registered client: " + response.clientId);
		mClientId.set(response.clientId);
		initializeAuthRequest();
	}

	/**
	 * Enumerates the browsers installed on the device and populates a spinner, allowing the
	 * demo user to easily test the authorization flow against different browser and custom
	 * tab configurations.
	 */
	@MainThread
	private void configureBrowserSelector() {
		Spinner spinner = (Spinner) findViewById(R.id.browser_selector);
		final BrowserSelectionAdapter adapter = new BrowserSelectionAdapter(this);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				BrowserSelectionAdapter.BrowserInfo info = adapter.getItem(position);
				if (info == null) {
					mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
					return;
				} else {
					mBrowserMatcher = new ExactBrowserMatcher(info.mDescriptor);
				}

				recreateAuthorizationService();
				createAuthRequest(getLoginHint());
				warmUpBrowser();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
			}
		});
	}

	/**
	 * Performs the authorization request, using the browser selected in the spinner,
	 * and a user-provided `login_hint` if available.
	 */
	@WorkerThread
	private void doAuth() {
		try {
			mAuthIntentLatch.await();
		} catch (InterruptedException ex) {
			Log.w(TAG, "Interrupted while waiting for auth intent");
		}

		if (mUsePendingIntents) {
			Intent completionIntent = new Intent(this, TokenActivity.class);
			Intent cancelIntent = new Intent(this, ValidateUDetails.class);
			cancelIntent.putExtra(EXTRA_FAILED, true);
			cancelIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);

			mAuthService.performAuthorizationRequest(
					mAuthRequest.get(),
					PendingIntent.getActivity(this, 0, completionIntent, 0),
					PendingIntent.getActivity(this, 0, cancelIntent, 0),
					mAuthIntent.get());
		} else {
			Intent intent = mAuthService.getAuthorizationRequestIntent(
					mAuthRequest.get(),
					mAuthIntent.get());
			startActivityForResult(intent, RC_AUTH);
		}
	}

	private void recreateAuthorizationService() {
		if (mAuthService != null) {
			Log.i(TAG, "Discarding existing AuthService instance");
			mAuthService.dispose();
		}
		mAuthService = createAuthorizationService();
		mAuthRequest.set(null);
		mAuthIntent.set(null);
	}

	private AuthorizationService createAuthorizationService() {
		Log.i(TAG, "Creating authorization service");
		AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
		builder.setBrowserMatcher(mBrowserMatcher);
		builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

		return new AuthorizationService(this, builder.build());
	}

	@MainThread
	private void displayLoading(String loadingMessage) {
		findViewById(R.id.loading_container).setVisibility( View.VISIBLE);
		findViewById(R.id.auth_container).setVisibility( View.GONE);
		findViewById(R.id.error_container).setVisibility( View.GONE);

		((TextView)findViewById(R.id.loading_description)).setText(loadingMessage);
	}

	@MainThread
	private void displayError(String error, boolean recoverable) {
		findViewById(R.id.error_container).setVisibility( View.VISIBLE);
		findViewById(R.id.loading_container).setVisibility( View.GONE);
		findViewById(R.id.auth_container).setVisibility( View.GONE);

		((TextView)findViewById(R.id.error_description)).setText(error);
		findViewById(R.id.retry).setVisibility(recoverable ? View.VISIBLE : View.GONE);
	}

	// WrongThread inference is incorrect in this case
	@SuppressWarnings("WrongThread")
	@AnyThread
	private void displayErrorLater(final String error, final boolean recoverable) {
		runOnUiThread(() -> displayError(error, recoverable));
	}

	@MainThread
	private void initializeAuthRequest() {
		createAuthRequest(getLoginHint());
		warmUpBrowser();
		displayAuthOptions();
	}

	@MainThread
	private void displayAuthOptions() {
		findViewById(R.id.auth_container).setVisibility( View.VISIBLE);
		findViewById(R.id.loading_container).setVisibility( View.GONE);
		findViewById(R.id.error_container).setVisibility( View.GONE);

		AuthState state = mAuthStateManager.getCurrent();
		AuthorizationServiceConfiguration config = state.getAuthorizationServiceConfiguration();

		String authEndpointStr;
		if (config.discoveryDoc != null) {
			authEndpointStr = "Discovered auth endpoint: \n";
		} else {
			authEndpointStr = "Static auth endpoint: \n";
		}
		authEndpointStr += config.authorizationEndpoint;
		((TextView)findViewById(R.id.auth_endpoint)).setText(authEndpointStr);

		String clientIdStr;
		if (state.getLastRegistrationResponse() != null) {
			clientIdStr = "Dynamic client ID: \n";
		} else {
			clientIdStr = "Static client ID: \n";
		}
		clientIdStr += mClientId;
		((TextView)findViewById(R.id.client_id)).setText(clientIdStr);
	}

	private void displayAuthCancelled() {
		Snackbar.make(findViewById(R.id.coordinator),
				"Authorization canceled",
				Snackbar.LENGTH_SHORT)
				.show();
	}

	private void warmUpBrowser() {
		mAuthIntentLatch = new CountDownLatch(1);
		mExecutor.execute(() -> {
			Log.i(TAG, "Warming up browser instance for auth request");
			CustomTabsIntent.Builder intentBuilder =
					mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
			intentBuilder.setToolbarColor(getColorCompat(R.color.colorPrimary));
			mAuthIntent.set(intentBuilder.build());
			mAuthIntentLatch.countDown();
		});
	}

	private void createAuthRequest(@Nullable String loginHint) {
		Log.i(TAG, "Creating auth request for login hint: " + loginHint);
		AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
				mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
				mClientId.get(),
				ResponseTypeValues.CODE,
				mConfiguration.getRedirectUri())
				.setScope(mConfiguration.getScope());

		if (!TextUtils.isEmpty(loginHint)) {
			authRequestBuilder.setLoginHint(loginHint);
		}

		mAuthRequest.set(authRequestBuilder.build());
	}

	private String getLoginHint() {
		return ((EditText)findViewById(R.id.login_hint_value))
				.getText()
				.toString()
				.trim();
	}

	@TargetApi(Build.VERSION_CODES.M)
	@SuppressWarnings("deprecation")
	private int getColorCompat(@ColorRes int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return getColor(color);
		} else {
			return getResources().getColor(color);
		}
	}

	/**
	 * Responds to changes in the login hint. After a "debounce" delay, warms up the browser
	 * for a request with the new login hint; this avoids constantly re-initializing the
	 * browser while the user is typing.
	 */
	private final class LoginHintChangeHandler implements TextWatcher {

		private static final int DEBOUNCE_DELAY_MS = 500;

		private Handler mHandler;
		private RecreateAuthRequestTask mTask;

		LoginHintChangeHandler() {
			mHandler = new Handler( Looper.getMainLooper());
			mTask = new RecreateAuthRequestTask();
		}

		@Override
		public void beforeTextChanged(CharSequence cs, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence cs, int start, int before, int count) {
			mTask.cancel();
			mTask = new RecreateAuthRequestTask();
			mHandler.postDelayed(mTask, DEBOUNCE_DELAY_MS);
		}

		@Override
		public void afterTextChanged(Editable ed) {}
	}

	private final class RecreateAuthRequestTask implements Runnable {

		private final AtomicBoolean mCanceled = new AtomicBoolean();

		@Override
		public void run() {
			if (mCanceled.get()) {
				return;
			}

			createAuthRequest(getLoginHint());
			warmUpBrowser();
		}

		public void cancel() {
			mCanceled.set(true);
		}
	}
////108///////////
		private boolean isEmulator() {
			String build = Build.FINGERPRINT;
			return build.contains("generic")
					|| build.contains("google_sdk")
					|| build.contains("vbox")
					|| build.contains("emu")
					|| build.contains("sdk")
					|| build.contains("test-keys")
					|| Build.MODEL.contains("Emulator")
					|| Build.MODEL.contains("Android SDK built for x86")
					|| Build.MANUFACTURER.contains("Genymotion")
					|| Build.PRODUCT.contains("sdk")
					|| Build.BRAND.startsWith("generic")
					|| Build.DEVICE.startsWith("generic")
					|| "google_sdk".equals(Build.PRODUCT)
					|| Build.BOARD.equals("QC_Reference_Phone")
			        || Build.FINGERPRINT.startsWith("generic")
					|| Build.MODEL.contains("google_sdk")
					|| Build.PRODUCT.contains("sdk_google")
					|| Build.PRODUCT.contains("google_sdk")
					|| Build.PRODUCT.contains("sdk_x86")
					|| Build.PRODUCT.contains("vbox86p")
					|| Build.PRODUCT.contains("emulator")
					|| Build.PRODUCT.contains("simulator")
					|| Build.FINGERPRINT.startsWith("unknown")
					|| Build.MODEL.contains("VirtualBox")
					//bluestacks
					|| Build.HOST == "Build2" //MSI App Player
					|| Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
					|| Build.MODEL.toLowerCase().contains("droid4x")
					|| Build.MODEL.contains("Emulator")
					|| Build.MODEL.contains("Android SDK built for x86")
					|| Build.HARDWARE == "goldfish"
					|| Build.HARDWARE == "vbox86"
					|| Build.HARDWARE.toLowerCase().contains("nox")
					|| Build.FINGERPRINT.startsWith("generic")
					|| Build.PRODUCT.toLowerCase().contains("nox")
					|| Build.BOARD.toLowerCase().contains("nox")
					|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
					// another Android SDK emulator check
					|| System.getProperties().getProperty("ro.kernel.qemu") == "1";
		}

	private boolean runningOnAndroidStudioEmulator(){
		return Build.FINGERPRINT.startsWith("google/sdk_gphone")
				&& Build.FINGERPRINT.endsWith(":user/release-keys")
				&& Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone") && Build.BRAND == "google"
				&& Build.MODEL.startsWith("sdk_gphone");
	}

	public class LogoutAuth2 extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String userId;
		public LogoutAuth2(Context con,String userId) {
			this.con = con;
			this.userId = userId;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String response = HttpUtils.httpGetRequest(WebMethods.url_logout_ad);
			} catch (Exception e) {
				e.printStackTrace();
				//auditTrailList = null;
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			//Toast.mak+eText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
			//  Utils.toast( HomeActivity.this, "36" );
//            stopLocationService();
//            stopPunchInnotificatioService();
//            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
//            i.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//            startActivity( i );
//            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
//            dbHelper.open();
//            dbHelper.clearFormRights();
//            dbHelper.close();
//            mAppPreferences.setLoginState( 0 );
//            mAppPreferences.saveSyncState( 0 );
//            //mAppPreferences.setGCMRegistationId("");
//            finish();
			new LogoutTask1(ValidateUDetails.this,userId).execute();
		}
	}

	public class LogoutTask1 extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String res,userId;
		ResponceLoginList response = null;


		public LogoutTask1(Context con,String userId) {
			this.con = con;
			this.userId = userId;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show( con, null, "Logging Out..." );

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
				nameValuePairs.add( new BasicNameValuePair( "userId",userId ) );
				res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_logout, nameValuePairs );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			//Toast.makeText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
//			stopLocationService();
//			stopPunchInnotificatioService();
			Utils.toast( ValidateUDetails.this, "36" );
//			Intent i = new Intent( ValidateUDetails.this, ValidateUDetails.class );
//			i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//			startActivity( i );
//			DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
//			dbHelper.open();
//			dbHelper.clearFormRights();
//			dbHelper.close();
//			mAppPreferences.setLoginState( 0 );
//			mAppPreferences.saveSyncState( 0 );
			//mAppPreferences.setGCMRegistationId("");
		//	finish();
		}
	}

	private void showProminentDisclosure() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Permissions Required");
		builder.setMessage("This app requires access to your location, camera, and storage to provide core features, such as [explain specific use cases]. Please grant these permissions to continue.");
		builder.setPositiveButton("Continue", (dialog, which) -> {
			// Request Permissions
			ActivityCompat.requestPermissions(this, AppConstants.ALL_PERMISSIONS, REQUEST_CODE);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			dialog.dismiss();
			Toast.makeText(this, "Permissions are required to use the app.", Toast.LENGTH_SHORT).show();
		});
		builder.setCancelable(false); // Prevent dismissal by tapping outside
		builder.show();
	}


	private static final int REQUEST_CODE_FOREGROUND = 100;
	private static final int REQUEST_CODE_BACKGROUND = 101;

	// Foreground permissions
	public static String[] FOREGROUND_PERMISSIONS = {
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.POST_NOTIFICATIONS
	};

	// Background location permission
	public static String BACKGROUND_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;


	private void showProminentDisclosureForForeground() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Permissions Required");
		builder.setMessage("This app requires access to your camera, storage, and location to provide its core functionality, such as [specific feature explanation]. Please grant these permissions to continue.");
		builder.setPositiveButton("Continue", (dialog, which) -> {
			// Request Foreground Permissions
			ActivityCompat.requestPermissions(this, FOREGROUND_PERMISSIONS, REQUEST_CODE_FOREGROUND);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			dialog.dismiss();
			Toast.makeText(this, "Permissions are required to use the app.", Toast.LENGTH_SHORT).show();
		});
		builder.setCancelable(false);
		builder.show();
	}

	private void showProminentDisclosureForBackground() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Background Location Permission Required");
		builder.setMessage("This app requires background location access to provide features such as [need to explan user tracking functionality]. Your location data will not be shared with third parties.");
		builder.setPositiveButton("Continue", (dialog, which) -> {
			// Request Background Location Permission
			ActivityCompat.requestPermissions(this, new String[]{BACKGROUND_PERMISSION}, REQUEST_CODE_BACKGROUND);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			dialog.dismiss();
			Toast.makeText(this, "Background location permission is required for full functionality.", Toast.LENGTH_SHORT).show();
		});
		builder.setCancelable(false);
		builder.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == REQUEST_CODE_FOREGROUND) {
			if (Utils.hasPermissions(this, FOREGROUND_PERMISSIONS)) {
				// Foreground permissions granted, now request background permission
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Utils.hasPermissions(this, new String[]{BACKGROUND_PERMISSION})) {
					showProminentDisclosureForBackground();
				} else {
					Toast.makeText(this, "All necessary permissions granted!", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "Foreground permissions are required to proceed.", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == REQUEST_CODE_BACKGROUND) {
			if (Utils.hasPermissions(this, new String[]{BACKGROUND_PERMISSION})) {
				Toast.makeText(this, "Background location permission granted!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Background location permission denied.", Toast.LENGTH_SHORT).show();
			}
		}
	}


}
