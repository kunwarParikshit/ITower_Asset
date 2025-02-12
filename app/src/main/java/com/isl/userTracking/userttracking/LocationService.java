package com.isl.userTracking.userttracking;

import android.Manifest;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.isl.api.IApiRequest;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.ResponseGetUserInfo;
import com.isl.modal.ResponseUserInfoList;
import com.isl.user.tracking.SignalChecker;
import com.isl.userTracking.userttracking.RequestModel.ChildUserTrackingModel;
import com.isl.userTracking.userttracking.RequestModel.ParentModel;
import com.isl.userTracking.userttracking.RequestModel.ResponceUserTracking;
import com.isl.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LocationService extends Service {
    AppPreferences mAppPreferences;
    private int TIME_INTERVAL = 900000;//time interval for 15 min
    public static ArrayList<LatLng> locationArrayList = new ArrayList<LatLng>();
    double latitude, LATE, NewLate;
    double longitude, Long, NewLong;
    String address;
    LocationManager locationManager;
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    private LocationListener locationListener;
    private Location location;
    public int signalStrengthValue;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String appName = "";
    String action;
    boolean status,punchInStatus=true;
    private final static String TAG = LocationService.class.getName();
    private String permission;


    private void startLocationUpdates() {
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
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    protected void createLocationRequest() {
        TIME_INTERVAL = Integer.parseInt(mAppPreferences.getTimeInterval());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(TIME_INTERVAL);
        locationRequest.setFastestInterval(TIME_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppPreferences = new AppPreferences(this);
        TIME_INTERVAL = Integer.parseInt(mAppPreferences.getTimeInterval());

        new Notification();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel();
        else startForeground(
                1,
                new Notification()
        );
        if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mAppPreferences.setUserPermission("Allow once");
        } else if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mAppPreferences.setUserPermission("Allow while using App");
        } else if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mAppPreferences.setUserPermission("Always Allow");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    mAppPreferences.setUserPermission("Allow while using App");
                } else {
                    mAppPreferences.setUserPermission("Always Allow");
                }
            } else {
                mAppPreferences.setUserPermission("Always Allow");

            }

        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(TIME_INTERVAL);
        locationRequest.setFastestInterval(TIME_INTERVAL);
        locationRequest.setMaxWaitTime(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                NewLate = location.getLatitude();
                NewLong = location.getLongitude();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
               /* isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
               */
                if (isGPSEnabled) {
                    canGetLocation = true;
                }

                if(mAppPreferences.getCheckIn().equalsIgnoreCase("Y")) {
                   mAppPreferences.setCheckIn("N");

                }
                else
                {
                    callWebService();
                }
               /* Toast.makeText(getApplicationContext(),
                        "Lat: " + Double.toString(location.getLatitude()) + '\n' +
                                "Long: " + Double.toString(location.getLongitude()), Toast.LENGTH_LONG).show();
              */
            }
        };
        startLocationUpdates();
    }

    // Get Latitude and Longitude
    private void getLatLong() {
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
                  /*  locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);*/
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
                       /* locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);*/
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChanel() {
        String notificationChannelId = "Location channel id";
        String channelName = "Background Service";

        NotificationChannel chan = new NotificationChannel(
                notificationChannelId,
                channelName,
                NotificationManager.IMPORTANCE_NONE
        );
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = getSystemService(NotificationManager.class);

        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, notificationChannelId);

        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Location updates:")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callWebService();
        Log.d("addd","OnDistory");
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Get Details
    public void callWebService() {
        final int[] a = {0};

        mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
        String autoTime;
        String netType = "";
        // GPSTracker gps;
        String iemi = "";
        int gpsStaus;
        final ResponseGetUserInfo response;
        //check for Battery
        int battery = getBatteryPercentage(LocationService.this);
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
            //   getUpdatedLatLong();
            //getLocation();
            //check for Signal strength
            SignalChecker SC = new SignalChecker(LocationService.this, new SignalChecker.OnSignalReceiver() {
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
        response.setLat("" + NewLate);
        response.setLongt("" + NewLong);
        response.setBatteryStatus(battery);
        response.setAutoTime(autoTime);
        response.setImei(iemi);
        response.setGps("" + gpsStaus);
        response.setMock(appName + "~" + status);

        //Insert to database
        DataBaseHelper dbhh = new DataBaseHelper(LocationService.this);
        dbhh.open();
        dbhh.insertMobileInfo(response);
        dbhh.close();

//        if(mAppPreferences.getCheckIn().equalsIgnoreCase("Y"))
//        {
//            punchInStatus = false;
//           // saveUserTrackingRequest.setCheckInStatus("IN");
//            mAppPreferences.setCheckIn("N");
//        }
      //  else {

            //Fetch from database
            if (Utils.isNetworkAvailable(LocationService.this)) {
                DataBaseHelper db1 = new DataBaseHelper(LocationService.this);
                db1.open();
                ResponseUserInfoList responselist = db1.getMobileInfo();
                db1.close();
      /*      try {
                List<ChildUserTrackingModel> childUserTrackingModels = new ArrayList<>();
                JSONObject obj2 = new JSONObject();
                JSONArray myNewArray = new JSONArray();
                if (responselist.getDetails().size() > 0 && Utils.isNetworkAvailable(LocationService.this)) {
                    for (int i = 0; i < responselist.getDetails().size(); i++) {
                        ChildUserTrackingModel childUserTrackingModel = new ChildUserTrackingModel();
                        JSONObject obj = new JSONObject();
                        obj.put("userId", Integer.parseInt(mAppPreferences.getUserId()));
                        obj.put("locationTime", responselist.getDetails().get(i).getTimeStamp());
                        obj.put("latitude", responselist.getDetails().get(i).getLat());
                        obj.put("longitude", responselist.getDetails().get(i).getLongt());
                        obj.put("batteryPercent", responselist.getDetails().get(i).getBatteryStatus());
                        obj.put("gpsValue", gpsStaus);
                        obj.put("networkSignalStatus", signalStrengthValue);
                        obj.put("mockValue", responselist.getDetails().get(i).getMock());
                        obj.put("permission", permission);
                        obj.put("checkInStatus", "IN");
                        myNewArray.put(obj);
                    }
                    obj2.put("formData", myNewArray);
                    childUserTrackingModels.add()
                    apiSaveMobileInfo(obj2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
*/
                List<ChildUserTrackingModel> saveUserTrackingRequest1 = new ArrayList<>();
                if (responselist.getDetails().size() > 0) {
                    for (int i = 0; i < responselist.getDetails().size(); i++) {
                        ChildUserTrackingModel saveUserTrackingRequest = new ChildUserTrackingModel();
                        saveUserTrackingRequest.setUserId(Integer.valueOf(mAppPreferences.getUserId()));
                        saveUserTrackingRequest.setLocationTime(responselist.getDetails().get(i).getTimeStamp());
                        saveUserTrackingRequest.setLatitude(responselist.getDetails().get(i).getLat());
                        saveUserTrackingRequest.setLongitude(responselist.getDetails().get(i).getLongt());
                        saveUserTrackingRequest.setBatteryPercent(Integer.valueOf(responselist.getDetails().get(i).getBatteryStatus()));
                        saveUserTrackingRequest.setGpsValue(gpsStaus);
                        saveUserTrackingRequest.setNetworkSignalStatus(signalStrengthValue);
                        saveUserTrackingRequest.setMockValue(responselist.getDetails().get(i).getMock());
                        saveUserTrackingRequest.setPermission(permission);
                        if (mAppPreferences.getUserPunchInOut().equalsIgnoreCase("false")) {
                            saveUserTrackingRequest.setCheckInStatus("OUT");
                            punchInStatus = true;
                        } else {
                            if (punchInStatus) {
                                saveUserTrackingRequest.setCheckInStatus("IN");
                            } else {
                                saveUserTrackingRequest.setCheckInStatus("");
                            }
                            punchInStatus = false;
                        }
                        saveUserTrackingRequest1.add(saveUserTrackingRequest);
                    }
                    apiSaveMobileInfo(saveUserTrackingRequest1);
                }


            }

    }

    private boolean canGetLocation() {
        return this.canGetLocation;
    }

    // To Check Mock Location
    public boolean isMockLocation() {
        if (location != null) {
            LATE = location.getLatitude();
            Long = location.getLongitude();
        }
        return Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider();
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

    //Api for  retrofit
    private void apiSaveMobileInfo(List<ChildUserTrackingModel> responselist) {
       //List<ParentModel> list = new ArrayList<>();
        ParentModel parentModel = new ParentModel();
        parentModel.setIngester("UserTracking");
        parentModel.setMapping("");
        parentModel.setSiteId("");
        parentModel.setStatus(0);
        parentModel.setData(responselist);
       //list.add(parentModel);
        DataBaseHelper db2 = new DataBaseHelper(LocationService.this);
        db2.open();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
        String BaseUrl=Utils.msg(LocationService.this, "831");
       // Toast.makeText(this, ""+BaseUrl, Toast.LENGTH_SHORT).show();
       // String BaseUrl= "http://101.53.139.74:9020/";
        Log.d("Avi",""+TIME_INTERVAL);
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


}
