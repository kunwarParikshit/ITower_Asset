package com.isl.userTracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.isl.itower.HomeActivity;

import infozech.itower.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // we will use vibrator first
        Toast.makeText(context, "abc", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // PendingIntent pendingIntent = PendingIntent.getActivity(context,1001,i,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"location_notification_channel")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Reminder Check Out")
                .setContentText("Hi Please Check OUT in iTower App")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
               // .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(2001,builder.build());


    }


}
