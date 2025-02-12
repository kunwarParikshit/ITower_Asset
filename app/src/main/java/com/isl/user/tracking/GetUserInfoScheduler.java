package com.isl.user.tracking;

import com.isl.dao.cache.AppPreferences;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class GetUserInfoScheduler {
	private static final int REQUEST_CODE = 1001;
	
	public static void schedule(Context ctx) {
		AppPreferences mAppPreferences=new AppPreferences(ctx);
		long startat=01;
		String[] dataUserTrack = mAppPreferences.getUserTracking().split("\\~");
		Calendar calendar = Calendar.getInstance();
		startat=calendar.getTimeInMillis()+Integer.parseInt(dataUserTrack[3]);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
				REQUEST_CODE, new Intent(ctx, GetUserInfoAlarmReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		stop(ctx);
		AlarmManager alm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alm.set(AlarmManager.RTC_WAKEUP, startat, pendingIntent);	
	 }

	/**
	 * Stop running alarm
	 * 
	 * @param ctx
	 */
	public static void stop(Context ctx) {
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
				REQUEST_CODE, new Intent(ctx, GetUserInfoAlarmReceiver.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alm.cancel(pendingIntent);
	}
}
