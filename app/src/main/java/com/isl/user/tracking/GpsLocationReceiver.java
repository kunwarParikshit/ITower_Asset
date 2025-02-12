package com.isl.user.tracking;
import com.isl.itower.GPSTracker;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsLocationReceiver  extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		AppPreferences mAppPreferences = new AppPreferences(context);
		if(mAppPreferences.getTrackingOnOff().equalsIgnoreCase( "ON" ) && mAppPreferences.getTrackMode()==1 && mAppPreferences.getLoginState()==1){
			GPSTracker gps = new GPSTracker(context);
			if (Utils.isAutoDateTime(context) || gps.canGetLocation() == false) {
				Intent userInfo = new Intent(context,LocationOffOnService.class);
				context.startService(userInfo);
			}
		}
	}
}

