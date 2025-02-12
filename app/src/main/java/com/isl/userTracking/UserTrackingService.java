package com.isl.userTracking;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import infozech.itower.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserTrackingService extends Service {
    private static final String TAG = "LocationForegroundServ";
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "LocationForegroundService";
    private static long LOCATION_INTERVAL = 1 * 60 * 1000; // 7 sec
    private Handler handler;
    private Runnable runnable;
    private FusedLocationProviderClient fusedLocationClient;
    AppPreferences mAppPreferences;
    ResponseGetUserInfo response;
    String netType = "",autoTime = "";
    boolean status;
    public int signalStrengthValue;
    private PowerManager.WakeLock wakeLock;
    @Override
    public void onCreate() {
        super.onCreate();
        mAppPreferences = new AppPreferences(this);
        handler = new Handler();
       LOCATION_INTERVAL = Integer.parseInt(mAppPreferences.getTimeInterval());
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, createNotification("Service has been started"), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        }
        else {
            startForeground(NOTIFICATION_ID, createNotification("Service has been started"));
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        acquireWakeLock();
        startLocationUpdates();
//        if(mAppPreferences.getCheckIn().equalsIgnoreCase("N")) {

//        }

    }


    private void startLocationUpdates() {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                getLocation();
                handler.postDelayed(this, LOCATION_INTERVAL);
            }
        }, LOCATION_INTERVAL);
    }




    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {

                    saveData(location.getLatitude(),location.getLongitude());
                  //  Toast.makeText(UserTrackingService.this,"Location Update: " + location.getLatitude() + ", " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Location Update: " + location.getLatitude() + ", " + location.getLongitude());
                }


            }
        });
        fusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserTrackingService.this,"Fused Location Failure!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void saveData(double latitude,double longitude) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
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
        int battery = getBatteryPercentage(UserTrackingService.this);
        if (Utils.isAutoDateTime(this)) {
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
            SignalChecker SC = new SignalChecker(UserTrackingService.this, new SignalChecker.OnSignalReceiver() {
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
        if(mAppPreferences.getCheckInSataus().equalsIgnoreCase("First Time"))
        {
            response.setCheckInStatus("IN");
            mAppPreferences.setCheckInStatus("");
        }
//        else if(mAppPreferences.getCheckInSataus().equalsIgnoreCase("OUT"))
//        {
//            response.setCheckInStatus("OUT");
//        }
        else
        {
            response.setCheckInStatus("");
        }

        //Insert to database
        DataBaseHelper dbhh = new DataBaseHelper(UserTrackingService.this);
        dbhh.open();
        dbhh.insertMobileInfo(response);
        dbhh.close();

        if (Utils.isNetworkAvailable(UserTrackingService.this)) {
            DataBaseHelper db1 = new DataBaseHelper(UserTrackingService.this);
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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       releaseWakeLock();
        // Stop the foreground service and remove the notification
        createNotification("Service has been stopped");
        stopForeground(true);
        handler.removeCallbacks(runnable);

    }

    private Notification createNotification(String msg) {
        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.tawal_icon)
                .setContentTitle("Location Service")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create a notification channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        return builder.build();
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

    private void apiSaveMobileInfo(List<ChildUserTrackingModel> responselist) {

        ParentModel parentModel = new ParentModel();
        parentModel.setIngester("UserTracking");
        parentModel.setMapping("");
        parentModel.setSiteId("");
        parentModel.setStatus(0);
        parentModel.setData(responselist);

        DataBaseHelper db2 = new DataBaseHelper(UserTrackingService.this);
        db2.open();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
        String BaseUrl=Utils.msg(UserTrackingService.this, "831");

        Retrofit builder = new Retrofit.Builder().baseUrl(BaseUrl).
                addConverterFactory(GsonConverterFactory.create()).client(okclient.build()).build();
        IApiRequest request = builder.create(IApiRequest.class);
        //IApiRequest request = RetrofitApiClient.getRequest();
        Call<ResponceUserTracking> call = request.getApiSaveMobileInfo1(parentModel);
        call.enqueue(new Callback<ResponceUserTracking>() {
            @Override
            public void onResponse(Call<ResponceUserTracking> call, retrofit2.Response<ResponceUserTracking> response) {
                //Log.d("response.body", response.body().getErrorMessage());
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

    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationForegroundServ:WakeLock");
            wakeLock.acquire();
        }
    }

    // Release the WakeLock
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

}

