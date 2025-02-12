package com.isl.userTracking;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.ResponseGetUserInfo;
import com.isl.modal.ResponseUserInfoList;
import com.isl.user.tracking.SignalChecker;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import infozech.itower.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

public class StartService extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30; // 30 meters
    // The minimum time between updates in millisecondsAA
    private static final long MIN_TIME_BW_UPDATES = 300000; // 1 minute
    private static final int REQUEST_CODE = 102;
    private int TIME_INTERVAL;//time interval for 15 min
    Timer timer = new Timer();
    double latitude, LATE;
    double longitude, Long;
    String address;
    LocationManager locationManager;
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    int gpsStaus;
    private LocationListener locationListener;
    private Location location;
    public int signalStrengthValue;
    String appName = "";
    String action;
    boolean status;
    private final static String TAG = StartService.class.getName();
    private String permission;
    AppPreferences mAppPreferences;


    //Get Address
    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  countary" + country);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);
                // addres.setText(address+"  &  "+city+"  & "+state+"  &  "+country+"  $   "+postalCode+"  &  "+knownName);
                //   Toast.makeText(context, address+"&"+city+"&"+state+"&"+country+"$"+postalCode+"&"+knownName, Toast.LENGTH_LONG).show();
                ///    Toast.makeText( this, ""+address, Toast.LENGTH_SHORT ).show();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        getLatLong();
        doTimerThings();
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mAppPreferences = new AppPreferences(this);
        if (mAppPreferences.getTimeInterval().equalsIgnoreCase("")){
            TIME_INTERVAL=300000;
        }else {
            TIME_INTERVAL = Integer.parseInt(mAppPreferences.getTimeInterval());
        }
        getLatLong();
        //getLocation();
        // callWebService();

    }

    // Get Latitude and Longitude
    private void getLatLong() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //latLng = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //   mMap.addMarker( new MarkerOptions().position( latLng ).title( "My Location" ) );
                // mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ) );
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                gpsStaus=0;
                //  showSettingsAlert();
            }
        };


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        try {
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled) {
                this.canGetLocation = false;
                // no network provider is enabled
                // showDialogGPS();
                // Toast.makeText(this, "Please Open GPS or Location Provider From Your Phone Settings", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }
                        }
                    }
                }
                getAddress(this, latitude, longitude);
                // callWebService();
            }


        } catch (SecurityException e) {
            e.printStackTrace();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, flags, startId);
        //for start service for interval
        // doTimerThings();

        if (intent != null) {
            action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                    getLatLong();
                    doTimerThings();
                    // Toast.makeText(this, "cfghk", Toast.LENGTH_SHORT).show();
                } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopTimerTask();
                    // Toast.makeText(this, "gg", Toast.LENGTH_SHORT).show();

                }
            }
        }

        // callWebService();
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

   /* public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }*/

   /* @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (intent != null) {
            action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                    getLatLong();
                    doTimerThings();
                    // Toast.makeText(this, "cfghk", Toast.LENGTH_SHORT).show();
                } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopTimerTask();
                    // Toast.makeText(this, "gg", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
*/
    // Run Service for specific time
    private void doTimerThings() {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                // you get the lat and lng , do your server stuff here-----
                //AppPreferences mAppPreferences = new AppPreferences(StartService.this);

                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int Minutes = calendar.get(Calendar.MINUTE);
                int sec = calendar.get(Calendar.SECOND);
                int CheckHours = 0,Checkminuts = 0;
                if (mAppPreferences.getCheckInTime().length() != 1) {
                    String[] datatime = mAppPreferences.getCheckInTime().split(":");
                    if (datatime.length !=1) {
                        CheckHours = Integer.parseInt(datatime[0]);
                        Checkminuts = Integer.parseInt(datatime[1]);
                    }
                }
                if (hours == CheckHours) {
                    if (Minutes >= Checkminuts) {
                        createNotification();
                        //   stopTimerTask();
                    }
                }
                if (ActivityCompat.checkSelfPermission(StartService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                    mAppPreferences.setUserPermission("Allow once");
                }else if (ActivityCompat.checkSelfPermission(StartService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    mAppPreferences.setUserPermission("Allow while using App");
                }else if (ActivityCompat.checkSelfPermission(StartService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    mAppPreferences.setUserPermission("Always Allow");
                } else {
                    mAppPreferences.setUserPermission("Always Allow");
                }

                callWebService();
            }

        }, 0, TIME_INTERVAL);

    }

    // Get Details
    public void callWebService() {
        final int[] a = {0};

        mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
        String autoTime;
        String netType = "";
        // GPSTracker gps;
        String iemi = "";
        final ResponseGetUserInfo response;
        //check for Battery
        int battery = getBatteryPercentage(StartService.this);
        Log.d("battery", "" + battery);
        //check for AutoTime
        if (Utils.isAutoDateTime(this)) {
            autoTime = "false";
        } else {
            autoTime = "true";
        }

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
        //check for GPS
        if (canGetLocation() == false) {
            Log.d("gps", "0");
            gpsStaus = 0;
        } else {
            gpsStaus = 1;
            Log.d("gps", "1");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            //UI THREAD CODE HERE
            // getLatLong();
            getUpdatedLatLong();
            //getLocation();
            //check for Signal strength
            SignalChecker SC = new SignalChecker(StartService.this, new SignalChecker.OnSignalReceiver() {
                @Override
                public void onRecived(int signal) {
                    signalStrengthValue = signal;
                }
            });
            SC.checkAndUpdate();
        });
        //check for mock
        if (isMockLocation() == true) {
            status = true;
        } else {
            status = false;
        }
        if (mAppPreferences.getUserPermission() == null || mAppPreferences.getUserPermission().equalsIgnoreCase("")) {
            permission = "Allow While Using App";
        } else {
            permission = mAppPreferences.getUserPermission();
        }
        // getLocation();

        //getUpdatedLatLong();
       /* GPSTracker gpsTracker = new GPSTracker(StartService.this);
        latitude =  gpsTracker.getLatitude() ;
        longitude = gpsTracker.getLongitude();F
     */
        response = new ResponseGetUserInfo();
        response.setLoginID(mAppPreferences.getLoginId());
        response.setTimeStamp(Utils.CurrentDateTime());
        response.setNetworkCheck(netType);//network type
        response.setLat("" + latitude);
        response.setLongt("" + longitude);
        response.setBatteryStatus(battery);
        response.setAutoTime(autoTime);
        response.setImei(iemi);
        response.setGps("" + gpsStaus);
        response.setMock(appName + "~" + status);

        //Insert to database
        DataBaseHelper dbhh = new DataBaseHelper(StartService.this);
        dbhh.open();
        dbhh.insertMobileInfo(response);
        dbhh.close();

        //Fetch from database
        if (Utils.isNetworkAvailable(StartService.this)) {
            DataBaseHelper db1 = new DataBaseHelper(StartService.this);
            db1.open();
            ResponseUserInfoList responselist = db1.getMobileInfo();
            db1.close();
            try {
                JSONObject obj2 = new JSONObject();
                JSONArray myNewArray = new JSONArray();
                if (responselist.getDetails().size() > 0 && Utils.isNetworkAvailable(StartService.this)) {
                    for (int i = 0; i < responselist.getDetails().size(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("userId", Integer.parseInt(mAppPreferences.getUserId()));
                        obj.put("locationTime", responselist.getDetails().get(i).getTimeStamp());
                        obj.put("latitude", responselist.getDetails().get(i).getLat());
                        obj.put("longitude", responselist.getDetails().get(i).getLongt());
                        obj.put("batteryPercent",responselist.getDetails().get(i).getBatteryStatus());
                        obj.put("gpsValue", gpsStaus);
                        obj.put("networkSignalStatus", signalStrengthValue);
                        obj.put("mockValue", responselist.getDetails().get(i).getMock());
                        obj.put("permission", permission);
                        myNewArray.put(obj);
                    }
                    obj2.put("formData", myNewArray);

                    apiSaveMobileInfo(obj2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //send to server object bys
          /*   List<SaveUserTrackingRequest> saveUserTrackingRequest1 = new ArrayList<>();
            UserTrackingRequest userTrackingRequest = new UserTrackingRequest();

          if (responselist.getDetails().size() > 0) {
                for (int i = 0; i < responselist.getDetails().size(); i++) {
                    SaveUserTrackingRequest saveUserTrackingRequest = new SaveUserTrackingRequest();
                    saveUserTrackingRequest.setUserId(Integer.valueOf(mAppPreferences.getUserId()));
                    saveUserTrackingRequest.setLocationTime(responselist.getDetails().get(i).getTimeStamp());
                    saveUserTrackingRequest.setLatitude(responselist.getDetails().get(i).getLat());
                    saveUserTrackingRequest.setLongitude(responselist.getDetails().get(i).getLongt());
                    saveUserTrackingRequest.setBatteryPercent(Integer.valueOf(responselist.getDetails().get(i).getBatteryStatus()));
                    saveUserTrackingRequest.setGpsValue(gpsStaus);
                    saveUserTrackingRequest.setNetworkSignalStatus(signalStrengthValue);
                    saveUserTrackingRequest.setMockValue(responselist.getDetails().get(i).getMock());
                    saveUserTrackingRequest.setPermission(permission);
                    saveUserTrackingRequest1.add(saveUserTrackingRequest);
                    userTrackingRequest.setFormData(saveUserTrackingRequest1);

                }
                saveUserTrackingRequest1.toString();
                userTrackingRequest.getFormData().toString();
             //   apiSaveMobileInfo(userTrackingRequest);
            }
*/

        }
    }

    private void getUpdatedLatLong() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());

        }

        try {
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                this.canGetLocation = false;
                // no network provider is enabled
                //showDialogGPS();
                //  Toast.makeText(this, "Please Open GPS or Location Provider From Your Phone Settings", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                /*if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        } else {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }*/
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }
                        }
                    }
                }
                getAddress(this, latitude, longitude);

            }


        } catch (SecurityException e) {
            e.printStackTrace();

        }

    }

    //Api for  retrofit
    private void apiSaveMobileInfo(JSONObject responselist) {
        DataBaseHelper db2 = new DataBaseHelper(StartService.this);
        db2.open();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
       /* Retrofit builder = new Retrofit.Builder().baseUrl("http://203.122.7.134:6100/").
                addConverterFactory(GsonConverterFactory.create()).client(okclient.build()).build();
       IApiRequest request = builder.create(IApiRequest.class);
      */IApiRequest request = RetrofitApiClient.getRequest();
        Call<String> call = request.getApiSaveMobileInfo(responselist.toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("response.body", response.body());
                if (response.body() != null) {
                    if (response.body().equalsIgnoreCase("S")) {
                        db2.deleteMobileInfo();
                        db2.close();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("response.body", "" + t);
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (action != null) {
            if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                getLatLong();
                doTimerThings();
                // Toast.makeText(this, "cfghk", Toast.LENGTH_SHORT).show();
            } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                stopTimerTask();
                // Toast.makeText(this, "gg", Toast.LENGTH_SHORT).show();

            }
        }
        // Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        // sendBroadcast(broadcastIntent);
        //stoptimertask();
    }

    //Stop Tracking Service
    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stopSelf();
    }

  /*  @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        broadcastIntent.setClass(this, SensorRestarterBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }*/

    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle("Gps is running in background")
                //.setContentText("iTower is using your Gps")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void startMyOwnForeground1() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder
                .setSmallIcon(R.drawable.tawal_icon)
                .setContentTitle("Reminder For Check Out")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("Please Check Out From iTower App")
                //.setContentText("iTower is using your Gps")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(3, notification);
    }

    //Check Battery Percentage
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("jfbjks", " " + latitude + "  " + longitude);

    }

    public boolean isMockLocation() {
        if (location != null) {
            LATE = location.getLatitude();
            Long = location.getLongitude();
        }
        return Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            //locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            // isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled) {
                this.canGetLocation = false;
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        //		|| ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        //{
                        //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        //   MIN_TIME_BW_UPDATES,
                        //   MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        //}

                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {

                            //if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            //		|| ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            //{
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            //}

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    private void createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startMyOwnForeground1();
        } else {
            startForeground(1, new Notification());

        }
       /* String channelID = "location_notification_channel";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent =  new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.tawal_icon);
        builder.setContentTitle("Reminder For Punch Out");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Please Punch Out From iTower App");
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This Channel is used by Location Service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    */
    }
  /*  private void showDialogGPS() {
        locationRequest = new LocationRequest()
                .setFastestInterval(300)
                .setInterval(300)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> responseTask = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        responseTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(StartService.this,
                                REQUEST_CODE);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(Sthis, "Open Location From Settings", Toast.LENGTH_SHORT).show();
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Toast.makeText(getContext(), location.getLongitude() + "  \\" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            }
        };

    }*/

}