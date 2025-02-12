package com.isl.userTracking;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.isl.dao.cache.AppPreferences;
import com.isl.workflow.constant.Constants;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import infozech.itower.R;

public class PunchInNotificationService extends Service {

    private final static String TAG = PunchInNotificationService.class.getName();
    private static final long TIME_INTERVAL = 18000000;
    private String permission;
    AppPreferences mAppPreferences;
    Timer timer = new Timer();
    String action;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
       
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mAppPreferences = new AppPreferences(this);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, flags, startId);
        //for start service for interval
        if (intent != null) {
            action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                    doTimerThings();
                    // Toast.makeText(this, "cfghk", Toast.LENGTH_SHORT).show();
                } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopTimerTask();
                    // Toast.makeText(this, "gg", Toast.LENGTH_SHORT).show();

                }
            }
        }
        // doTimerThings();
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

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
              //  Log.d("CheckHours",CheckHours+""+Checkminuts);
                //Log.d("CheckHours1",hours+""+Minutes);

                if (hours == CheckHours) {
                    if (Minutes >= Checkminuts) {
                        createNotification();
                        //   stopTimerTask();
                    }
                }
            }

        }, 0, TIME_INTERVAL);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerTask();

        Intent broadcastIntent = new Intent();
       // broadcastIntent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        broadcastIntent.setClass(this, DailyReceiver.class);
        this.sendBroadcast(broadcastIntent);
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

    private void startMyOwnForeground1() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                .setContentTitle("Reminder For Punch Out")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("Please Punch Out From iTower App")
                //.setContentText("iTower is using your Gps")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(3, notification);
    }


    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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