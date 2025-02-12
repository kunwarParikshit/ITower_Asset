package com.isl.user.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.isl.dao.cache.AppPreferences;

public class GetUserInfoAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		AppPreferences mAppPreferences = new AppPreferences(context);
		if(mAppPreferences.getTrackingOnOff().equalsIgnoreCase( "ON" )) {
			GetUserInfoScheduler.schedule( context ); // Next scheduling
			Intent userInfo = new Intent( context, GetUserInfoService.class );
		    /*if (GetUserInfoService.isServiceRunning) {
			  context.stopService(userInfo);
		     }*/
			context.startService( userInfo );
			//Toast.makeText(context, "get user broadcast", Toast.LENGTH_LONG).show();
		}
	}
}
