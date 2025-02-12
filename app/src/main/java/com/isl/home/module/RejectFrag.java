package com.isl.home.module;
/*Created By : Dhakan Lal Sharma
Modified On : 16-June-2015
Version     : 0.1
CR          : iMaintan 1.9.1.1*/

/* Modified By : Dhakan Lal Sharma
Modified On : 14-July-2016
Version     : 0.2
Purpose     : QC Bugs 24820 */

import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RejectFrag extends Fragment {
	 ListView lv_activity;
	 TextView txt_no_activity;
	 ArrayList<String> flag_list, activity_list,error_list;
	 RelativeLayout rl_no_list; 
	 ArrayList<String> txnDate,rejectTxnDate;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pending_local_list,container, false);
		txt_no_activity =(TextView)view.findViewById(R.id.txt_no_activity);
		lv_activity =(ListView) view.findViewById(R.id.lv_activity);
		rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
		AppPreferences mAppPreferences = new AppPreferences(getActivity());
		DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
		dbHelper.open();
		flag_list = new ArrayList<String>();
		activity_list = new ArrayList<String>();
		error_list = new ArrayList<String>();
		flag_list = dbHelper.getRejectLocalTxnData(1,mAppPreferences.getUserId());
		activity_list = dbHelper.getRejectLocalTxnData(2,mAppPreferences.getUserId());
		error_list= dbHelper.getRejectLocalTxnData(3,mAppPreferences.getUserId());
		dbHelper.close();
		if(activity_list.size()>0){
			lv_activity.setAdapter(new PendingRejectAdapter(getActivity(),activity_list,flag_list,error_list,1));
			lv_activity.setVisibility(View.VISIBLE);
			rl_no_list.setVisibility(View.GONE);
		}else{
			lv_activity.setVisibility(View.GONE);
			rl_no_list.setVisibility(View.VISIBLE);
			Utils.msgText(getActivity(),"47",txt_no_activity);    //No Transaction Found
			//txt_no_activity.setText("No Transaction Found");  //0.2
		}	
		return view;
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
		dbHelper.open();
		txnDate=dbHelper.getTxnDate(1);
		rejectTxnDate=dbHelper.getTxnDate(2);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date d2 = null;
		Date d3 = null;
		if(rejectTxnDate.size()>0){
			 for(int i=0;i<rejectTxnDate.size();i++){
			 try {
					d3 = format.parse(rejectTxnDate.get(i));
					d2 = format.parse(Utils.date());
				    //in milliseconds
					long diff = d2.getTime() - d3.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					if(diffDays>6){
					dbHelper.deleteTxnRejectData(rejectTxnDate.get(i));	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
     		 }
		}
		
	}
}
