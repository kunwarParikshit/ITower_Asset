package com.isl.itower;
/*Created By : Dhakan Lal Sharma
Modified On : 4-Aug-2016
Version     : 0.1
Purpose     : cr# 1.9.1.3
*/
import com.isl.dao.cache.AppPreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class InstallAppReceiver extends BroadcastReceiver {   
	    Context context;
	    AppPreferences mAppPreferences;
	    @Override  
	    public void onReceive(Context context, Intent intent) { 
	    mAppPreferences = new AppPreferences(context);
	    this.context=context;
	    //context.stopService(new Intent(context, AppVersionService.class));
	   // scheduleAlarm();
	    loginState();
	    } 
	    public void loginState(){
		    mAppPreferences.setTTAssignRb("off");
		    mAppPreferences.setTTUpdateRb("off");
		    mAppPreferences.setTTEscalateRb("off");
		    mAppPreferences.setPMScheduleRb("off");
		    mAppPreferences.setPMEscalateRb("off");
			//DataBaseHelper dbHelper = new DataBaseHelper(context);
			//dbHelper.open();
			//dbHelper.clearFormRights();
			//dbHelper.close();
			mAppPreferences.setLoginState(0);
			mAppPreferences.saveSyncState(0);
			//mAppPreferences.setGCMRegistationId("");
	  }
	  /*public void scheduleAlarm() {
			WorkFlowUtils.stop1(context);
		    WorkFlowUtils.stop2(context);
		    WorkFlowUtils.stop3(context);
		    WorkFlowUtils.scheduleAlarmAppVersion(context);
		    WorkFlowUtils.scheduleAlarmSaveMobileInfo(context);
		    WorkFlowUtils.scheduleAlarmMobileInfo(context);
	  }*/
}
