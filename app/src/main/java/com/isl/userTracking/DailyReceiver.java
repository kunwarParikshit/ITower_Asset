package com.isl.userTracking;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.isl.itower.HomeActivity;
import com.isl.workflow.constant.Constants;

import infozech.itower.R;

public class DailyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                    sendNotification(context);
                } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    context.stopService(new Intent(context, PunchInNotificationService.class));
                }
            }
        }

    }

    private void sendNotification(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,10001,i,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notify")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Reminder Check In")
                .setContentText("Hi Please Check IN in iTower App")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,builder.build());
    }

}
