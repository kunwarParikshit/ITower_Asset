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

public class BootCompleteReceiver extends BroadcastReceiver {
	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
			this.context = context;
		    AppPreferences mAppPreferences =new AppPreferences(context);
		    mAppPreferences.setLoginState(0);
			//GetUserInfoScheduler.schedule(context);
			//SaveUserInfoScheduler.schedule(context);
		    /*WorkFlowUtils.stop1(context);
		    WorkFlowUtils.stop2(context);
		    WorkFlowUtils.stop3(context);
			WorkFlowUtils.scheduleAlarmAppVersion(context);
		 	WorkFlowUtils.scheduleAlarmSaveMobileInfo(context);
		 	WorkFlowUtils.scheduleAlarmMobileInfo(context);	*/
	}	
}
