package com.isl.userTracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.isl.workflow.constant.Constants;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                    context.startService(new Intent(context, StartService.class));
                    // context.startService(new Intent(context, StartService.class));

                } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    context.stopService(new Intent(context, StartService.class));
                } else {
                    context.startService(new Intent(context, StartService.class));
                }
            }
        } else {
            context.startService(new Intent(context, StartService.class));
        }
    }
}