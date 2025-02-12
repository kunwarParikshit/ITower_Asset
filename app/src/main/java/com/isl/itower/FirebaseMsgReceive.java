package com.isl.itower;
/**
 * Created by dhakan on 7/4/2018.
 */
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.util.ArrayList;

import com.isl.home.module.NotificationUpdate;
import com.isl.incident.TicketDetailsTabs;
import com.isl.modal.BeanAddNotification;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.notification.ShortcutBadger;
import infozech.itower.R;
import com.isl.preventive.PMTabs;
import com.isl.energy.withdrawal.FuelPurchaseGridRPT;
import com.isl.user.tracking.GetUserInfoService;
import com.isl.user.tracking.NoDataPacket;

public class FirebaseMsgReceive extends FirebaseMessagingService {
    public static final String NOTIFICATION_CHANNEL_ID = "9001" ;
    private final static String default_notification_channel_id = "iTower" ;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    BeanAddNotification temp;

    String notification_type,tkt_id,SiteId,AlarmDescription,UpdatedFields,AssignedTo,Duration,EscalationLevel,ActivityType,
            Status,ScheduleDate,RunHour,CurrentRunHour,data,fillingQty,dgType,fillDate,genMessage,subject,DoneDate,RejectBy;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mAppPreferences = new AppPreferences(this);
        temp=new BeanAddNotification();

        if(remoteMessage.getData()!=null
                && remoteMessage.getData().get( "message" ).toString().length()!=0
                && mAppPreferences.getLoginState() == 1){
            createNotification(remoteMessage.getData().get( "message" ).toString());
        }
    }

    public void createNotification( String messageBody) {
        String[] dataTS = messageBody.split("@@@");
        for(int counter=0;counter<dataTS.length;counter++){
            if(dataTS[counter].contains("NotificationType")){
                int position=dataTS[counter].indexOf("~");
                notification_type=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setNotification_type(notification_type);
            }else if (dataTS[counter].contains("DisplayTime")){
                int position=dataTS[counter].indexOf("~");
                String display=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setDisplayDuration(display);
            }else if (dataTS[counter].contains("NotificationSubject")){
                int position=dataTS[counter].indexOf("~");
                subject=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setNotification(subject);
            }else if (dataTS[counter].contains("GenMSG")){ //0.2
                int position=dataTS[counter].indexOf("~");
                genMessage=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setGenMessage(genMessage);
            }else if(dataTS[counter].contains("TicketId")){
                int position=dataTS[counter].indexOf("~");
                tkt_id=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setTkt_id(tkt_id);
            }else if(dataTS[counter].contains("SiteId")){
                int position=dataTS[counter].indexOf("~");
                SiteId=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setSiteId(SiteId);
            }else if(dataTS[counter].contains("AlarmDescription")){
                int position=dataTS[counter].indexOf("~");
                AlarmDescription=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setAlarmDescription(AlarmDescription);
            }else if(dataTS[counter].contains("UpdatedFields")){
                int position=dataTS[counter].indexOf("~");
                UpdatedFields=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setUpdatedFields(UpdatedFields);
            }else if(dataTS[counter].contains("AssignedTo")){
                int position=dataTS[counter].indexOf("~");
                AssignedTo=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setAssignedTo(AssignedTo);
            }else if(dataTS[counter].contains("Duration")){
                int position=dataTS[counter].indexOf("~");
                Duration=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setDuration(Duration);
            }else if(dataTS[counter].contains("EscalationLevel")){
                int position=dataTS[counter].indexOf("~");
                EscalationLevel=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setEscalationLevel(EscalationLevel);
            }else if(dataTS[counter].contains("ActivityType")){
                int position=dataTS[counter].indexOf("~");
                ActivityType=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setActivityType(ActivityType);
            }else if(dataTS[counter].contains("ScheduleDate")){
                int position=dataTS[counter].indexOf("~");
                ScheduleDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setScheduleDate(ScheduleDate);
            }else if(dataTS[counter].contains("Status")){
                int position=dataTS[counter].indexOf("~");
                Status=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setStatus(Status);
            }else if(dataTS[counter].contains("DoneDate")){
                int position=dataTS[counter].indexOf("~");
                DoneDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setDoneDate(DoneDate);
            }else if(dataTS[counter].contains("RejectBy")){
                int position=dataTS[counter].indexOf("~");
                RejectBy=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setRejectBy(RejectBy);
            }else if (dataTS[counter].contains("CurrentRunHour")){
                int position=dataTS[counter].indexOf("~");
                CurrentRunHour=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setCurrentRunHour(CurrentRunHour);
            }else if (dataTS[counter].contains("RunHours")){
                int position=dataTS[counter].indexOf("~");
                RunHour=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setRunHour(RunHour);
            }else if (dataTS[counter].contains("FillingQuantity")){
                int position=dataTS[counter].indexOf("~");
                fillingQty=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setFillingQuantity(fillingQty);
            }else if (dataTS[counter].contains("DGType")){
                int position=dataTS[counter].indexOf("~");
                dgType=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setDGType(dgType);
            }else if (dataTS[counter].contains("FillingDate")){//0.2
                int position=dataTS[counter].indexOf("~");
                fillDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setFillingDate(fillDate);
            }else if (dataTS[counter].contains("lattitude")){
                int position=dataTS[counter].indexOf("~");
                String lat=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setLattitude(lat);
            }else if (dataTS[counter].contains("longitude")){
                int position=dataTS[counter].indexOf("~");
                String log=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setLongitude(log);
            }else if(dataTS[counter].contains("SupplierName")){
                int position=dataTS[counter].indexOf("~");
                String supplier=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setSupplierName( supplier );
            }else if(dataTS[counter].contains("RequestDate")){
                int position=dataTS[counter].indexOf("~");
                String requestDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setRequestDate( requestDate );
            }else if(dataTS[counter].contains("ApprovalDate")){
                int position=dataTS[counter].indexOf("~");
                String approvalDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setApprovalDate( approvalDate );
            }else if(dataTS[counter].contains("Circle")){
                int position=dataTS[counter].indexOf("~");
                String Circle=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setCircle( Circle );
            }else if(dataTS[counter].contains("Zone")){
                int position=dataTS[counter].indexOf("~");
                String Zone=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setZone( Zone );
            }else if(dataTS[counter].contains("Cluster")){
                int position=dataTS[counter].indexOf("~");
                String Cluster=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setCluster( Cluster );
            }else if(dataTS[counter].contains("txnStatus")){
                int position=dataTS[counter].indexOf("~");
                String txnStatus=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setTxnStatus( txnStatus );
            }else if(dataTS[counter].contains("aqty")){
                int position=dataTS[counter].indexOf("~");
                String aqty=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setAqty( aqty );
            }else if(dataTS[counter].contains("DoneDate")){
                int position=dataTS[counter].indexOf("~");
                String DoneDate=dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setDoneDate(DoneDate);
            } else if(dataTS[counter].contains("TICKET_MODE")){
                int position=dataTS[counter].indexOf("~");
                String tkt_mode = dataTS[counter].substring(position+1, dataTS[counter].length());
                temp.setTkt_mode(tkt_mode);
            }
        }

        if(notification_type.equalsIgnoreCase("1")){
            temp.setNotification("TT Assignment Notification");
        }else if(notification_type.equalsIgnoreCase("2")){
            temp.setNotification("TT Update Notification");
        }else if(notification_type.equalsIgnoreCase("3")){
            temp.setNotification("TT Escalation Notification");
        }else if(notification_type.equalsIgnoreCase("4")){
            temp.setNotification("Site Activity Schedule Notification");
        }else if(notification_type.equalsIgnoreCase("5")){
            temp.setNotification("Site Activity Escalation Notification");
        }else if(notification_type.equalsIgnoreCase("6")){
            temp.setNotification("Filling Beat Plan Notification");
        }else if(notification_type.equalsIgnoreCase("7")){//0.2
            temp.setNotification(subject);
         }else if(notification_type.equalsIgnoreCase("8")){
            temp.setNotification(subject);
        }else if(notification_type.equalsIgnoreCase("9")){
            Intent userInfo = new Intent(this,NoDataPacket.class);
            this.startService(userInfo);
        }else if(notification_type.equalsIgnoreCase("10")){
            Intent userInfo = new Intent(this,GetUserInfoService.class);
            this.startService(userInfo);
        }else if(notification_type.equalsIgnoreCase("11")){
            temp.setNotification(""+subject);
        }else if(notification_type.equalsIgnoreCase("12")){
            temp.setNotification("Site Activity Rejected Notification");
         }else if(notification_type.equalsIgnoreCase("13")){
            temp.setNotification("Site Activity Done Notification");
        }

        if(!(notification_type.equalsIgnoreCase("9")
            || notification_type.equalsIgnoreCase("10"))){
            onNotificationSetting();
        }
    }

    private void onNotificationSetting() {
        db = new DataBaseHelper(this);
        db.open();
        temp.setDropTime(Utils.dateNotification());
        temp.setDisplayTime(Utils.CurrentDateTime());
        data = new Gson().toJson(temp);
        db.insertNotificationData(mAppPreferences.getUserId(), data);
        ArrayList<String> list = db.getNotificationCount(mAppPreferences.getUserId(), "0");
        String msg = "You've received " + list.size() + " " + temp.getNotification();
        int flag = 0;
        if (list != null && list.size() > 1) {
            mAppPreferences.setTicketFrmNtBr("1");
            msg = "You've received " + list.size() + " iTower notification.";
            flag  =1;
        }
            Intent resultIntent = null;
          if (flag==0 && (notification_type.equalsIgnoreCase("1")
                    || notification_type.equalsIgnoreCase("2")
                    || notification_type.equalsIgnoreCase("3"))) {
                mAppPreferences.setTicketFrmNtBr(list.get(0).toString());
                mAppPreferences.SetBackModeNotifi123(1);
                resultIntent = new Intent(this, TicketDetailsTabs.class);
                resultIntent.putExtra("id", tkt_id);
                resultIntent.putExtra("rights",
                        db.getSubMenuRight("AssignedTab", "Incident"));
            } else if (flag==0 && (notification_type.equalsIgnoreCase("4")
                    || notification_type.equalsIgnoreCase("12")
                    || notification_type.equalsIgnoreCase("13"))) {
                mAppPreferences.setTicketFrmNtBr(list.get(0).toString());
                mAppPreferences.SetBackModeNotifi45(1);
                resultIntent = new Intent(this, PMTabs.class);
            } else if (flag==0 && (notification_type.equalsIgnoreCase("5"))) {
                mAppPreferences.setTicketFrmNtBr(list.get(0).toString());
                mAppPreferences.SetBackModeNotifi45(1);
                mAppPreferences.setPMTabs("N"); //default open Miss tab
                resultIntent = new Intent(this, PMTabs.class);
            } else if (flag==0 && (notification_type.equalsIgnoreCase("6")
                    || notification_type.equalsIgnoreCase("7")
                    || notification_type.equalsIgnoreCase("14"))) {
                resultIntent = new Intent(this, NotificationList.class);
            } else if (flag==1) {
              resultIntent = new Intent(this, NotificationList.class);
            } else if (notification_type.equalsIgnoreCase("8")) {
                resultIntent = new Intent(Intent.ACTION_VIEW);
                resultIntent.setData(Uri.parse("https://maps.google.com/maps?saddr=28.5335,77.2109&daddr=28.5857,77.311&z=17"));
                resultIntent.setPackage("com.google.android.apps.maps");
            } else if (notification_type.equalsIgnoreCase("11")) {
                resultIntent = new Intent(this, FuelPurchaseGridRPT.class);
            }
            db.close();
            PendingIntent resultPendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resultPendingIntent = PendingIntent.getActivity
                        (this, 0, resultIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                resultPendingIntent = PendingIntent.getActivity
                        (this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
            }
            int icon = clientIcon(getApplicationContext().getPackageName());
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                    default_notification_channel_id)
                    .setSmallIcon(icon)
                    .setContentTitle("iTower")
                    .setSound(sound)
                    .setContentText(msg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(icon);
            mBuilder.setColor(getResources().getColor(R.color.bg_color_white));
        } else {
            mBuilder.setSmallIcon(icon);
        }

        NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
            mBuilder.setContentIntent(resultPendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new
                        NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.setSound(sound, audioAttributes);

                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
            ShortcutBadger.removeCount(FirebaseMsgReceive.this);
            ShortcutBadger.applyCount(FirebaseMsgReceive.this, list.size());
            startService(new Intent(this, NotificationUpdate.class));

    }

    public int clientIcon(String appId){
        switch (appId) {
            case "tawal.com.sa" :
                return (R.drawable.tawal_icon);
            case "infozech.tawal" :
                return (R.drawable.midc_logo);
            case "infozech.safari" :
                return (R.drawable.infozech_logo);
            case "apollo.com.sa" :
                return (R.drawable.appollo_icon);
            case "voltalia.com.sa" :
                return (R.drawable.voltalia_logo);
            case "ock.com.sa" :
                return (R.drawable.ock_logo);
            case "eft.com.sa" :
                return (R.drawable.eft_logo);
        }
        return 0;
    }


}
