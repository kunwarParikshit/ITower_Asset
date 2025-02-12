package com.isl.home.module;
/*Created By : Dhakan Lal Sharma
Modified On : 16-June-2016
Version     : 0.1
CR          : iMaintan 1.9.1.1*/

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.isl.api.IApiRequest;
import com.isl.common.activities.Home;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.ValidateUDetails;
import com.isl.itower.CustomGrid;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.itower.GPSTracker;
import com.isl.modal.HomeModule;
import com.isl.modal.ResponceLoginList;
import com.isl.modal.ResponseGetUserInfo;
import com.isl.modal.ResponseUserInfoList;
import com.isl.modal.ServiceResponce;
import com.isl.modal.TTChecklist;
import com.isl.user.tracking.SignalChecker;
import com.isl.userTracking.DailyReceiver;
import com.isl.userTracking.PunchInNotificationService;
import com.isl.userTracking.UserTrackingService;
import com.isl.userTracking.userttracking.BatteryUtil;
import com.isl.userTracking.userttracking.LocationService;
import com.isl.userTracking.userttracking.RequestModel.ChildUserTrackingModel;
import com.isl.userTracking.userttracking.RequestModel.ParentModel;
import com.isl.userTracking.userttracking.RequestModel.ResponceUserTracking;
import com.isl.userTracking.userttracking.Util;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import infozech.itower.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragement extends Fragment {
    AppPreferences mAppPreferences;
    DataBaseHelper dbHelper;
    ExpandableHeightGridView grid = null;
    TTChecklist ttchecklist = null;
    Switch switchButton;
    String moduleUrl = "", appBundleId = "", appLink = "", url = "", hsUrl = "",netType = "",autoTime = "";
    ArrayList<String> notification_list, txnDate, rejectTxnDate, error_list;
    Button checkIn, checkOut;
    private static final int REQUEST_CODE_Permission = 1020;
    private RelativeLayout checkInOut;
    private String UTRoleRights;
    private static int MY_FINE_LOCATION_REQUEST = 99;
    private static int MY_BACKGROUND_LOCATION_REQUEST = 100;
    boolean firstTime = false;
    boolean flag = true;

    LocationService mLocationService = new LocationService();
    Intent mServiceIntent;
    private static final int  REQUEST_LOCATION_PERMISSION = 100;
    LocationManager manager;
    private FusedLocationProviderClient fusedLocationClient;
    public int signalStrengthValue;
    ResponseGetUserInfo response;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_frag, container, false);
        switchButton = view.findViewById(R.id.switch_button);
        mAppPreferences = new AppPreferences(getActivity());
        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


        try {
            dbHelper = new DataBaseHelper(getActivity());
            dbHelper.open();

            moduleUrl = dbHelper.getModuleIP("Incident");
            appBundleId = dbHelper.getModuleIP("KPLCApp");
            hsUrl = dbHelper.getModuleIP("HealthSafty");

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (Utils.isNetworkAvailable(getActivity())) {
            if (!ttCheckListDataType().equalsIgnoreCase("")) {
                new TTCheckListTask(getActivity()).execute();
            }
            new Getversion(getActivity()).execute();

        }

        checkInOut = view.findViewById(R.id.checkInOut);
        checkIn = view.findViewById(R.id.punch);
        checkOut = view.findViewById(R.id.punchout);
        if (mAppPreferences.getUserTracking() != null && mAppPreferences.getUserTracking().length() != 3) {
            String[] dataUserTrack = mAppPreferences.getUserTracking().split("\\~");
            if (dataUserTrack.length != 3) {
                Log.d("dataUserTrack", dataUserTrack.length + "-" + dataUserTrack[0] + "-" + dataUserTrack[1] + "-" + dataUserTrack[2] + "-" + dataUserTrack[3]);
                //   mAppPreferences.setTrackingOnOff(dataUserTrack[0]);
                mAppPreferences.setCheckInTime(dataUserTrack[1]);
                mAppPreferences.setCheckOutTime(dataUserTrack[2]);
                mAppPreferences.setTimeInterval(dataUserTrack[3]);
            }
        }
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        dbHelper.open();
        UTRoleRights = dbHelper.getSubMenuRight("UserTrackOnOff", "UserTrackModule");
        if (mAppPreferences.getTrackingOnOff().equalsIgnoreCase("ON") &&
                UTRoleRights.equalsIgnoreCase("1")) {
            checkInOut.setVisibility(View.VISIBLE);
            if(mAppPreferences.getCheckIn().equalsIgnoreCase("Y"))
            {
                //Issue 3177
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getContext(), "Please enable your GPS!!", Toast.LENGTH_SHORT).show();
                }
                //3177
                switchButton.setChecked(true);
            }
            else
            {
                 switchButton.setChecked(false);
            }
        }
        else {
            checkInOut.setVisibility(View.GONE);
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked())
                {
                    switchButton.setChecked(false);
                    if(areNotificationsEnabled(getContext())) {
                        permission();
                    }
                    else
                    {
                        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE,getActivity().getPackageName());
                               // .putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID);
                        startActivity(settingsIntent);
                     //   Toast.makeText(getContext(),"Please turn on notification!!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(gpsProvider()) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            if (Util.isMyServiceRunning(UserTrackingService.class, getActivity())) {
                                stopService();
                            }
                            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Please Wait...");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getLocation();
                                    Toast.makeText(getContext(),"CheckedOut successfully!!",Toast.LENGTH_SHORT).show();
                                    mAppPreferences.setCheckIn("N");
                                    progressDialog.dismiss();
                                }
                            }, 4000);


                        }

                        else
                        {
                            switchButton.setChecked(true);
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1001);

                        }


                    }
                    else
                    {
                        switchButton.setChecked(true);
                    }
                }
            }
        });


        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areNotificationsEnabled(getContext())) {
                    permission();
                }
                else
                {
                    Toast.makeText(getContext(),"Please turn on notification!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsProvider()) {

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (Util.isMyServiceRunning(UserTrackingService.class, getActivity())) {
                            stopService();
                        }
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getLocation();
                                checkIn.setVisibility(View.VISIBLE);
                                checkOut.setVisibility(View.GONE);
                                mAppPreferences.setCheckIn("N");
                                progressDialog.dismiss();
                            }
                        }, 4000);


                    }

                    else
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1001);
                    }


                }
            }
        });

        grid = (ExpandableHeightGridView) view.findViewById(R.id.grid);
        getLocalData();
        init();

        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                HomeModule module = AppConstants.moduleList.get(position);
                if (module.getModuleId() == 964) {
                    if (appBundleId.equalsIgnoreCase("0")) {
                        appLink = "com.kokava.apps.safkplc";
                    } else {
                        appLink = appBundleId;
                    }
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(appLink);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    } else {
                        appNotInstall(appLink);
                    }
                }

                else if(module.getModuleId() == 654)
                {

                    if(mAppPreferences.getCheckIn().equalsIgnoreCase("Y"))
                    {
                        Intent i = new Intent(getActivity(), module.getModuleClass());
                        mAppPreferences.setTTModuleSelection("" + module.getModuleId());
                        mAppPreferences.setModuleName(module.getModuleName());
                        mAppPreferences.setModuleIndex(position);
                        mAppPreferences.setPMBackTask(0);
                        mAppPreferences.SetBackModeNotifi45(1);
                        mAppPreferences.SetBackModeNotifi6(1);
                        i.putExtra("moduleIndex", position);
                        i.putExtra("moduleId", module.getModuleId());
                        i.putExtra("moduleName", module.getModuleName());
                        startActivity(i);
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("CheckIn");
                        alertDialog.setCancelable(false
                        );
                        alertDialog.setMessage("Please Check In to Access Incident Management");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else {
                    Intent i = new Intent(getActivity(), module.getModuleClass());
                    mAppPreferences.setTTModuleSelection("" + module.getModuleId());
                    mAppPreferences.setModuleName(module.getModuleName());
                    mAppPreferences.setModuleIndex(position);
                    mAppPreferences.setPMBackTask(0);
                    mAppPreferences.SetBackModeNotifi45(1);
                    mAppPreferences.SetBackModeNotifi6(1);
                    i.putExtra("moduleIndex", position);
                    i.putExtra("moduleId", module.getModuleId());
                    i.putExtra("moduleName", module.getModuleName());
                    startActivity(i);
                }
            }
        });
        return view;
    }


    private void stopLocationService() {
        checkIn.setVisibility(View.VISIBLE);
        checkOut.setVisibility(View.GONE);
        mAppPreferences.setUserPunchInOut("false");
        LocationService mYourService = new LocationService();
        Intent intent = new Intent(getContext(), mYourService.getClass());
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        getContext().stopService(intent);
    }




    private void stopPunchNotification() {
        PunchInNotificationService mYourService = new PunchInNotificationService();
        Intent mServiceIntent = new Intent(getContext(), mYourService.getClass());
        mServiceIntent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        getContext().stopService(mServiceIntent);
    }












    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


       if (requestCode ==  REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    checkBackgroundPermission();
                }
                else
                {

                    if (BatteryUtil.isBatteryOptimizationEnabled(getContext())) {
                      //  BatteryUtil.requestBatteryOptimizationPermission(getActivity());
                        BatteryUtil.getUnrestrictedBAttery(getContext(),"Please enable auto start functionality from app settings Change Battery setting of App to Unrestricted to get location updates in the background even when not using the app");
                    } else {

                     Toast.makeText(getContext(),"CheckedIn successfully!!",Toast.LENGTH_SHORT).show();
                        switchButton.setChecked(true);

                        mAppPreferences.setCheckIn("Y");
                        //
                        mAppPreferences.setCheckInStatus("First Time");
                        startService();
                    }
                }

            }
        }



    }



    public void getLocalData() {

        if (AppConstants.moduleList.size() > 0) {
            return;
        }

        try {
            DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
            dbHelper.open();
            dbHelper.initializeModuleList(getResources());

            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {

        if (AppConstants.moduleList.size() > 0) {
            CustomGrid adapter = new CustomGrid(getActivity());
            grid.setFastScrollEnabled(true);
            grid.setAdapter(adapter);
            grid.setExpanded(true);
        } else {
            //Toast.makeText(getActivity(),"You are not authorized to access modules.",Toast.LENGTH_SHORT).show();
            Utils.toast(getActivity(), "21");
            new LogoutTask(getActivity()).execute();
            mAppPreferences.setTTAssignRb("off");
            mAppPreferences.setTTUpdateRb("off");
            mAppPreferences.setTTEscalateRb("off");
            mAppPreferences.setPMScheduleRb("off");
            mAppPreferences.setPMEscalateRb("off");
        }
    }

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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("userId", mAppPreferences.getUserId()));
                res = Utils.httpPostRequest(con, mAppPreferences.getConfigIP() + WebMethods.url_logout, nameValuePairs);
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
            Utils.toast(getActivity(), "36");
            stopLocationService();
            stopPunchNotification();
            Intent i = new Intent(getActivity(), ValidateUDetails.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            try {
                DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                dbHelper.open();
                dbHelper.clearFormRights();
                dbHelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAppPreferences.setLoginState(0);
            mAppPreferences.saveSyncState(0);
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            dbHelper = new DataBaseHelper(getActivity());
            dbHelper.open();
            txnDate = dbHelper.getTxnDate(1);
            rejectTxnDate = dbHelper.getTxnDate(2);
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            Date d1 = null;
            Date d2 = null;
            Date d3 = null;
            if (txnDate.size() > 0) {
                for (int i = 0; i < txnDate.size(); i++) {
                    try {
                        d1 = format.parse(txnDate.get(i));
                        d2 = format.parse(Utils.date());
                        //in milliseconds
                        long diff = d2.getTime() - d1.getTime();
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        if (diffDays > 6) {
                            dbHelper.deleteTxnData(txnDate.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (rejectTxnDate.size() > 0) {
                for (int i = 0; i < rejectTxnDate.size(); i++) {
                    try {
                        d3 = format.parse(rejectTxnDate.get(i));
                        d2 = format.parse(Utils.date());
                        //in milliseconds
                        long diff = d2.getTime() - d3.getTime();
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        if (diffDays > 6) {
                            dbHelper.deleteTxnRejectData(rejectTxnDate.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            notification_list = new ArrayList<String>();//Start 0.1
            notification_list = dbHelper.getAllNotification(mAppPreferences.getUserId());
            dbHelper.close();
            mAppPreferences.setNotificationListFlag(1);
            mAppPreferences.setHomeScreenFlag(0);
            mAppPreferences.setToastFlag("No");
            mAppPreferences.setPMTabs("M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mAppPreferences.setHomeScreenFlag(1);
    }

    public class TTCheckListTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;

        public TTCheckListTask(Context con) {
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
                nameValuePairs.add(new BasicNameValuePair("addparam", ""));
                url = mAppPreferences.getConfigIP() + WebMethods.url_getTTChecklist;
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                ttchecklist = gson.fromJson(res, TTChecklist.class);
            } catch (Exception e) {
                e.printStackTrace();
                ttchecklist = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if ((ttchecklist == null)) {
                //Toast.makeText(getActivity(),"TT checklist not provided by server",Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(), "37");
            } else if (ttchecklist != null) {
                if (ttchecklist.getTTCheckList() != null && ttchecklist.getTTCheckList().size() > 0) {
                    try {
                        dbHelper.open();
                        dbHelper.clearTTChecklist();
                        dbHelper.insertTTCheckList(ttchecklist.getTTCheckList());
                        dbHelper.dataTS(null, null, "24", dbHelper.getLoginTimeStmp("24", "0"), 2, "0");
                        dbHelper.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //Toast.makeText(getActivity(), "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(), "13");
            }
        }
    }



    public String ttCheckListDataType() {
        String DataType_Str = "1";
        try {
            dbHelper = new DataBaseHelper(getActivity());
            dbHelper.open();
            String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("24", "0"),
                    dbHelper.getLoginTimeStmp("24", "0"), "24");
            dbHelper.close();
            if (i != "1") {
                DataType_Str = i;
            }
            if (DataType_Str == "1") {
                DataType_Str = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DataType_Str;
    }




    public void appNotInstall(final String appBundleId) {
        final Dialog actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(getActivity()));
        positive.setTypeface(Utils.typeFace(getActivity()));
        negative.setTypeface(Utils.typeFace(getActivity()));
        title.setTypeface(Utils.typeFace(getActivity()));
        title.setText(Utils.msg(getActivity(), "581"));
        positive.setText("YES");
        negative.setText("NO");

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appBundleId)));


            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }


    public class Getversion extends AsyncTask<Void, Void, Void> {
        String res;
        Context con;
        ServiceResponce response;
        ProgressDialog pd;
        PackageInfo pInfo = null;

        public Getversion(Context con) {
            this.con = con;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);
            Gson gson = new Gson();
            nameValuePairs.add(new BasicNameValuePair("version", pInfo.versionName));
            nameValuePairs.add(new BasicNameValuePair("userID", mAppPreferences.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("roleID", mAppPreferences.getRoleId()));
            nameValuePairs.add(new BasicNameValuePair("deviceID", mAppPreferences.getGCMRegistationId()));
            nameValuePairs.add(new BasicNameValuePair("languageCode", "" + mAppPreferences.getLanCode()));
            try {
                //String res = WorkFlowUtils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_GetAppVersion, nameValuePairs );
                String res = Utils.httpPostRequest(con, hsUrl + WebMethods.url_GetAppVersion, nameValuePairs);
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
                if (response.getSuccess().equalsIgnoreCase("S") && response.getMessage().trim().length() != 0) { //successfully
                    successfull(response.getMessage());
                }
                if (response.getSuccess().equalsIgnoreCase("S") && response.getTtconfiguration().trim().length() != 0) { //successfully
                    ttConfiguration(response.getTtconfiguration());
                }

            } else {
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    public void successfull(String res) {
        String[] dataTypeID, timeStamp;
        String[] tmpDataTS = new String[2];
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
            dbHelper.open();
            String[] dataTS = res.split(",");
            dataTypeID = new String[dataTS.length];
            timeStamp = new String[dataTS.length];
            for (int i = 0; i < dataTS.length; i++) {
                tmpDataTS = dataTS[i].split("\\~");
                dataTypeID[i] = tmpDataTS[0];
                timeStamp[i] = tmpDataTS[1];
            }
            if (!mAppPreferences.getFirstTimeHS().equalsIgnoreCase("A")) {
                dbHelper.clearDataTSHS();
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 0, "955");
                mAppPreferences.setFirstTimeHS("A");
            } else {
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 1, "955");
            }
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void ttConfiguration(String res) {
        // TT Configuration
        mAppPreferences.setTTConfiguration(res);
        String ttConfig = mAppPreferences.getTTConfiguration();
        if (ttConfig.length() != 0) {
            String baseArray[] = ttConfig.split("@");
            if (baseArray.length > 3) {
                mAppPreferences.setTTminimageHS(Integer.parseInt(baseArray[0]));
                mAppPreferences.setTTmaximageHS(Integer.parseInt(baseArray[1]));
                mAppPreferences.setTTimageMessageHS(baseArray[2]);
                mAppPreferences.setTTMediaFileTypeHS(baseArray[3]);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        mAppPreferences.setTrackMode(1);
    }

    public void createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory() + fname;
        File f = new File(myfolder);
        if (!f.exists())
            if (!f.mkdir()) {
            }
    }

    public void permission()
    {
        if(gpsProvider()) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Check pemission for android 10 for background location.

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    checkBackgroundPermission();
                }
                else
                {

                    if (BatteryUtil.isBatteryOptimizationEnabled(getContext())) {
                         BatteryUtil.getUnrestrictedBAttery(getContext(),"Please enable auto start functionality from app settings Change Battery setting of App to Unrestricted to get location updates in the background even when not using the app");

                    } else {

                        Toast.makeText(getContext(),"CheckedIn successfully!!",Toast.LENGTH_SHORT).show();
                        switchButton.setChecked(true);

                        mAppPreferences.setCheckIn("Y");
                        //
                        mAppPreferences.setCheckInStatus("First Time");
                        startService();
                    }

                }

            }

            else
            {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION_PERMISSION);

            }


        }

    }



    public boolean gpsProvider()
    {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are disabled, prompt the user to enable them
            Toast.makeText(getContext(), "Please enable location services", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return false;
        }

        else {
            return true;
        }
    }

    public void checkBackgroundPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)  {
          //  boolean value = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

                if (BatteryUtil.isBatteryOptimizationEnabled(getContext())) {
                  //  BatteryUtil.requestBatteryOptimizationPermission(getActivity());
                    BatteryUtil.getUnrestrictedBAttery(getContext(),"Please enable auto start functionality from app settings Change Battery setting of App to Unrestricted to get location updates in the background even when not using the app");

                } else {
//                    checkOut.setBackground(null);
//                    checkOut.setTextColor(getResources().getColor(R.color.black));
//                    checkIn.setBackground(getResources().getDrawable(R.drawable.button_orange));
//                    checkIn.setTextColor(getResources().getColor(R.color.white));
                    Toast.makeText(getContext(),"CheckedIn successfully!!",Toast.LENGTH_SHORT).show();
                    switchButton.setChecked(true);
                   // checkIn.setVisibility(View.GONE);
                   // checkOut.setVisibility(View.VISIBLE);
                    mAppPreferences.setCheckIn("Y");
                    mAppPreferences.setCheckInStatus("First Time");
                    startService();
                }
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Background permission");
                alertDialog.setMessage(getString(R.string.background_location_permission_message));
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Grant background Permission",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                        REQUEST_LOCATION_PERMISSION);
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
            }

        }

    }

    public void startService()
    {
        Intent intent = new Intent(getContext(), UserTrackingService.class);
        getActivity(). startService(intent);
    }

    public void stopService()
    {
        Intent intent = new Intent(getContext(),UserTrackingService.class);
        getActivity().stopService(intent);
    }


    ///////////////////////////CheckOut//////////////////////////


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {

                    saveData(location.getLatitude(),location.getLongitude());
                    //  Toast.makeText(UserTrackingService.this,"Location Update: " + location.getLatitude() + ", " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Location Update: " + location.getLatitude() + ", " + location.getLongitude());
                }


            }
        });
        fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Fused Location Failure!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }

    public void saveData(double latitude,double longitude) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        int battery = getBatteryPercentage(getContext());
        if (Utils.isAutoDateTime(getContext())) {
            autoTime = "false";
        } else {
            autoTime = "true";
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            //UI THREAD CODE HERE
            // getLatLong();
            //   getUpdatedLatLong();
            //getLocation();
            //check for Signal strength
            SignalChecker SC = new SignalChecker(getContext(), new SignalChecker.OnSignalReceiver() {
                @Override
                public void onRecived(int signal) {
                    signalStrengthValue = signal;
                }
            });
            SC.checkAndUpdate();
        });

        response = new ResponseGetUserInfo();
        response.setLoginID(mAppPreferences.getLoginId());
        response.setTimeStamp(Utils.CurrentDateTime());
        response.setNetworkCheck(netType);
        response.setLat(String.valueOf(latitude));
        response.setLongt(String.valueOf(longitude));
        response.setBatteryStatus(battery);
        response.setAutoTime(autoTime);
        response.setImei("");
        response.setGps(String.valueOf(1));
        response.setMock("" + "~" + true);
        response.setCheckInStatus("OUT");



        //Insert to database
        DataBaseHelper dbhh = new DataBaseHelper(getContext());
        dbhh.open();
        dbhh.insertMobileInfo(response);
        dbhh.close();

        if (Utils.isNetworkAvailable(getContext())) {
            DataBaseHelper db1 = new DataBaseHelper(getContext());
            db1.open();
            ResponseUserInfoList responselist = db1.getMobileInfo();
            db1.close();

            List<ChildUserTrackingModel> saveUserTrackingRequest1 = new ArrayList<>();
            if (responselist.getDetails().size() > 0) {
                for (int i = 0; i < responselist.getDetails().size(); i++) {
                    ChildUserTrackingModel saveUserTrackingRequest = new ChildUserTrackingModel();
                    saveUserTrackingRequest.setUserId(Integer.valueOf(mAppPreferences.getUserId()));
                    saveUserTrackingRequest.setLocationTime(responselist.getDetails().get(i).getTimeStamp());
                    saveUserTrackingRequest.setLatitude(responselist.getDetails().get(i).getLat());
                    saveUserTrackingRequest.setLongitude(responselist.getDetails().get(i).getLongt());
                    saveUserTrackingRequest.setBatteryPercent(Integer.valueOf(responselist.getDetails().get(i).getBatteryStatus()));
                    saveUserTrackingRequest.setGpsValue(1);
                    saveUserTrackingRequest.setNetworkSignalStatus(signalStrengthValue);
                    saveUserTrackingRequest.setMockValue(responselist.getDetails().get(i).getMock());
                    saveUserTrackingRequest.setPermission("Allways Allow");
                    saveUserTrackingRequest.setCheckInStatus(responselist.getDetails().get(i).getCheckInStatus());
                    saveUserTrackingRequest1.add(saveUserTrackingRequest);


                }
                apiSaveMobileInfo(saveUserTrackingRequest1);

            }

        }
    }

    private void apiSaveMobileInfo(List<ChildUserTrackingModel> responselist) {
        ParentModel parentModel = new ParentModel();
        parentModel.setIngester("UserTracking");
        parentModel.setMapping("");
        parentModel.setSiteId("");
        parentModel.setStatus(0);
        parentModel.setData(responselist);

        DataBaseHelper db2 = new DataBaseHelper(getContext());
        db2.open();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
        String BaseUrl=Utils.msg(getContext(), "831");

        Retrofit builder = new Retrofit.Builder().baseUrl(BaseUrl).
                addConverterFactory(GsonConverterFactory.create()).client(okclient.build()).build();
        IApiRequest request = builder.create(IApiRequest.class);
        //IApiRequest request = RetrofitApiClient.getRequest();
        Call<ResponceUserTracking> call = request.getApiSaveMobileInfo1(parentModel);
        call.enqueue(new Callback<ResponceUserTracking>() {
            @Override
            public void onResponse(Call<ResponceUserTracking> call, retrofit2.Response<ResponceUserTracking> response) {
                Log.d("response.body", response.body().getErrorMessage());
                //Toast.makeText(LocationService.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("S")) {
                        db2.deleteMobileInfo();
                        db2.close();

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponceUserTracking> call, Throwable t) {
                Log.d("response.body", "" + t);
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });

    }

    public boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

}
