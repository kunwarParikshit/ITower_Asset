package com.isl.home.module;
/*Created By : Dhakan Lal Sharma
Modified On : 16-June-2016
Version     : 0.1
CR          : iMaintan 1.9.1.1*/
import com.isl.incident.TicketDetailsTabs;
import com.isl.itower.AdapterNotificationList;
import com.isl.itower.NotificationDetails;
import infozech.itower.R;
import com.isl.modal.BeanAddNotification;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.notification.ShortcutBadger;
import com.isl.preventive.PMTabs;
import com.isl.sparepart.schedule.Schedule;

import java.util.ArrayList;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class NotificationListFrag extends Fragment {
	ArrayList<String> notification_list,notification_flag;
	ListView notification;
	RelativeLayout rl_no_list;
	TextView txt_no_ticket;
	AppPreferences mAppPreferences;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.notification_frag,container, false);
		Utils.deleteNotification(getActivity());
		rl_no_list= (RelativeLayout) view.findViewById(R.id.rl_textlayout);
		notification=(ListView) view.findViewById(R.id.lv_notification);
		txt_no_ticket=(TextView)view.findViewById(R.id.txt_no_ticket);
		mAppPreferences = new AppPreferences(getActivity());
		return view;
	}	
	
	 @Override
	public void onResume() {
	    super.onResume();
        mAppPreferences.setNotificationListFlag(0);
        notification_list = new ArrayList<String>();
		final DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
		dbHelper.open();
		notification_list = dbHelper.getAllNotification(mAppPreferences.getUserId());
		notification_flag = dbHelper.getNotificationFlag(mAppPreferences.getUserId());
		if(mAppPreferences.getToastFlag().equalsIgnoreCase("Yes")){
		Utils.toastMsg(getActivity(),""+mAppPreferences.getListToast());
		}
		mAppPreferences.setToastFlag("No");
		if(notification_list.size()>0){
			notification.setAdapter(new AdapterNotificationList(getActivity(),notification_list,notification_flag));
			notification.setVisibility(View.VISIBLE);
			rl_no_list.setVisibility(View.GONE);
		}else{
			rl_no_list.setVisibility(View.VISIBLE);
			Utils.msgText(getActivity(),"48",txt_no_ticket);    //No Notification Found
			notification.setVisibility(View.GONE);
		}
		notification.setOnItemClickListener(new OnItemClickListener() {
	    @Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
	    Gson gson = new Gson();
	    int a=0;
	    BeanAddNotification data_list = gson.fromJson(notification_list.get(position), BeanAddNotification.class);
	    if(data_list.getNotification_type().equalsIgnoreCase("1") || 
	       data_list.getNotification_type().equalsIgnoreCase("2") ||
	       data_list.getNotification_type().equalsIgnoreCase("3")){
	    a=1;	
	    Intent i = new Intent(getActivity(), TicketDetailsTabs.class);
		  if(data_list.getTkt_mode()!=null && data_list.getTkt_mode().equalsIgnoreCase("3")){
				mAppPreferences.setTTModuleSelection("955");
		  }else{
				mAppPreferences.setTTModuleSelection("654");
		  }
	    mAppPreferences.SetBackModeNotifi123(2);
	    i.putExtra("id",data_list.getTkt_id()); 
	    startActivity(i);
		}else if(data_list.getNotification_type().equalsIgnoreCase("5")){ 
		a=1;
		Intent i = new Intent(getActivity(), PMTabs.class);
		mAppPreferences.SetBackModeNotifi45(2);
		mAppPreferences.setPMTabs("N"); //default open Miss tab
		startActivity(i);	//useCase 1.8.1	
		}else if(data_list.getNotification_type().equalsIgnoreCase("6")
				||data_list.getNotification_type().equalsIgnoreCase("13")){
		a=1;
		Intent i = new Intent(getActivity(), Schedule.class);
		mAppPreferences.SetBackModeNotifi6(2);
		startActivity(i);
		}
		else if(data_list.getNotification_type().equalsIgnoreCase("7")){
		a=0;
		Intent i = new Intent(getActivity(), NotificationDetails.class);
		mAppPreferences.SetBackModeNotifi7(2);
		i.putExtra("genMSG",data_list.getGenMessage());
		i.putExtra("type",data_list.getNotification());
		i.putExtra("details", notification_list.get(position));
		startActivity(i);
		}else{
		a=1;	
	    Intent i = new Intent(getActivity(), PMTabs.class);
	    mAppPreferences.SetBackModeNotifi45(2);
	 	startActivity(i);	
	    }
	    if(a==1){
	    dbHelper.deleteNotification(notification_list.get(position),mAppPreferences.getUserId());
		dbHelper.close();
		NotificationManager notifManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancelAll();
	    }
		}
		});
	   }
	  @Override
	public void onPause() {
		 super.onPause();
		 mAppPreferences.setNotificationListFlag(1);
		 ArrayList<String> notification_counter = new ArrayList<String>();
		 final DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
		 dbHelper.open();
		 notification_counter = dbHelper.getNotificationCount(mAppPreferences.getUserId(),"0");
		 ShortcutBadger.removeCount(getActivity());
		 if(notification_counter.size()>0){
		 ShortcutBadger.applyCount(getActivity(),notification_counter.size());
		 }
	  }
}
