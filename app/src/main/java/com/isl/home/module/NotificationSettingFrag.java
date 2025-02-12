package com.isl.home.module;
/*Created By : Dhakan Lal Sharma
Modified On : 16-June-2016
Version     : 0.1
CR          : iMaintan 1.9.1.1*/
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;
public class NotificationSettingFrag extends Fragment {
	ToggleButton tb_tt_assign, tb_tt_update, tb_tt_escalate, tb_pm_schedule,tb_pm_escalate,tb_vibrate;
	TextView tv_general, tv_popup,tv_audio,tv_tone;
	RadioButton rb_no_popup, rb_always;
	AppPreferences mAppPreferences;
	LinearLayout ll_audio,ll_assignment,ll_updation,ll_escalation,ll_pm_schedule,ll_pm_escalate,ll_general;
	DataBaseHelper dbHelper;
	String assigned_tab,schedule_tab;
	View vw_aftr_general;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mAppPreferences = new AppPreferences(getActivity());
		dbHelper = new DataBaseHelper(getActivity());
		dbHelper.open();
		assigned_tab=dbHelper.getSubMenuRight("AssignedTab","Incident");
		schedule_tab=dbHelper.getSubMenuRight("Scheduled","Preventive");
		dbHelper.close();
		View view = inflater.inflate(R.layout.notification_sett_frag,container, false);
		tb_tt_assign = (ToggleButton) view.findViewById(R.id.tb_assign);
		tb_tt_update = (ToggleButton) view.findViewById(R.id.tb_update);
		tb_tt_escalate = (ToggleButton) view.findViewById(R.id.tb_escalate);
		tb_pm_schedule = (ToggleButton) view.findViewById(R.id.tb_schedule);
		tb_pm_escalate = (ToggleButton) view.findViewById(R.id.tb_pm_escalate);
		tb_vibrate = (ToggleButton) view.findViewById(R.id.tb_vibrate);
		tv_general = (TextView) view.findViewById(R.id.tv_general);
		tv_popup = (TextView) view.findViewById(R.id.tv_pop_up);
		tv_audio= (TextView) view.findViewById(R.id.tv_audio);
		tv_tone= (TextView) view.findViewById(R.id.tv_tone);
		rb_no_popup = (RadioButton) view.findViewById(R.id.rb_no_popup);
		rb_always = (RadioButton) view.findViewById(R.id.rb_always);
		ll_audio=(LinearLayout) view.findViewById(R.id.ll_audio);
		ll_assignment=(LinearLayout) view.findViewById(R.id.ll_assignment);
		ll_updation=(LinearLayout) view.findViewById(R.id.ll_updation);
		ll_escalation=(LinearLayout) view.findViewById(R.id.ll_escalation);
		ll_pm_schedule=(LinearLayout) view.findViewById(R.id.ll_pm_schedule);
		ll_pm_escalate=(LinearLayout) view.findViewById(R.id.ll_pm_escalate);
		ll_general=(LinearLayout) view.findViewById(R.id.ll_general);
		//vw_aftr_assign=(View) view.findViewById(R.id.vw_aftr_assign);
		//vw_after_update=(View) view.findViewById(R.id.vw_after_update);
		//vw_aftr_escalate=(View) view.findViewById(R.id.vw_aftr_escalate);
		vw_aftr_general=(View) view.findViewById(R.id.vw_aftr_general);
		//vw_aftr_pm_schedule=(View) view.findViewById(R.id.vw_aftr_pm_schedule);
		//vw_aftr_pm_escalate=(View) view.findViewById(R.id.vw_aftr_pm_escalate);
		
		TextView tv_general=(TextView)view.findViewById(R.id.tv_general); //
		TextView tv_tt_assign=(TextView)view.findViewById(R.id.tv_tt_assign);//
		TextView tv_tt_update=(TextView)view.findViewById(R.id.tv_tt_update);//
		TextView tv_tt_escalate=(TextView)view.findViewById(R.id.tv_tt_escalate);//
		TextView tv_pm_schedule=(TextView)view.findViewById(R.id.tv_pm_schedule);//
		TextView tv_pm_escalate=(TextView)view.findViewById(R.id.tv_pm_escalate);//
		TextView tv_pop_up=(TextView)view.findViewById(R.id.tv_pop_up);//
		TextView tv_no_popup=(TextView)view.findViewById(R.id.tv_no_popup);//
		TextView tv_show_popup=(TextView)view.findViewById(R.id.tv_show_popup);//
		TextView tv_audio=(TextView)view.findViewById(R.id.tv_audio); //
		TextView tv_noti_tone=(TextView)view.findViewById(R.id.tv_noti_tone);//
		TextView tv_vibrate=(TextView)view.findViewById(R.id.tv_vibrate);//
		
		Utils.msgText(getActivity(),"239",tv_general);    
		Utils.msgText(getActivity(),"49",tv_tt_assign);     
		Utils.msgText(getActivity(),"50",tv_tt_update);    
		Utils.msgText(getActivity(),"51",tv_tt_escalate);   
		Utils.msgText(getActivity(),"52",tv_pm_schedule);    
		Utils.msgText(getActivity(),"53",tv_pm_escalate);    
		Utils.msgText(getActivity(),"54",tv_pop_up);    
		Utils.msgText(getActivity(),"55",tv_no_popup);    
		Utils.msgText(getActivity(),"56",tv_show_popup);    
		Utils.msgText(getActivity(),"57",tv_audio);    
		Utils.msgText(getActivity(),"58",tv_noti_tone);   
		Utils.msgText(getActivity(),"60",tv_vibrate);    
		
		
		
		if(mAppPreferences.getPopUp().equalsIgnoreCase("no popup")){
			rb_no_popup.setChecked(true);	
			}else if(mAppPreferences.getPopUp().equalsIgnoreCase("show popup")){
			rb_always.setChecked(true);	
			}
			
			if(mAppPreferences.getVibrate().equalsIgnoreCase("vibrate on")){
			tb_vibrate.setChecked(true);	
			}else if (mAppPreferences.getVibrate().equalsIgnoreCase("vibrate off")){
			tb_vibrate.setChecked(false);	
			}
			
			tv_tone.setText(""+mAppPreferences.getNotificationToneName());
			
	        rb_no_popup.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
			if (rb_no_popup.isChecked()) {
				rb_always.setChecked(false);
				mAppPreferences.setPopUp("no popup");
			  } 
			 }
			});

			 rb_always.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View arg0) {
			 if (rb_always.isChecked()) {
				rb_no_popup.setChecked(false);
				mAppPreferences.setPopUp("show popup");
				}	
			  }
			  });
			 
			 ll_audio.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View v) {
			 Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		     //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications");
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, Utils.msg(getActivity(),"272"));
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,true);
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); //0.2
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
		     if(mAppPreferences.getNotificationTone().equalsIgnoreCase("default"))
		     {
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,Settings.System.DEFAULT_NOTIFICATION_URI); 
		     }else{
		     intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(mAppPreferences.getNotificationTone()));
		     }
		     startActivityForResult(intent,5);
			 }
			 });

			tb_tt_assign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			if (tb_tt_assign.isChecked()) {
				mAppPreferences.setTTAssignRb("on");
			} else {
				mAppPreferences.setTTAssignRb("off");
		    }
	     	}
			});

			tb_tt_update.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (tb_tt_update.isChecked()) {
						mAppPreferences.setTTUpdateRb("on");
					} else {
						mAppPreferences.setTTUpdateRb("off");
					}
			}
			});

			tb_tt_escalate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (tb_tt_escalate.isChecked()) {
						mAppPreferences.setTTEscalateRb("on");
					} else {
						mAppPreferences.setTTEscalateRb("off");
					}
				}
			});

			tb_pm_schedule.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (tb_pm_schedule.isChecked()) {
						mAppPreferences.setPMScheduleRb("on");
					} else {
						mAppPreferences.setPMScheduleRb("off");
					}
				}
			});

			tb_pm_escalate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (tb_pm_escalate.isChecked()) {
						mAppPreferences.setPMEscalateRb("on");
					} else {
						mAppPreferences.setPMEscalateRb("off");
					}
				}
			});
			
			tb_vibrate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (tb_vibrate.isChecked()) {
						mAppPreferences.setVibrate("vibrate on");
					} else {
						mAppPreferences.setVibrate("vibrate off");
				    }
				}
			});
		return view;
	}
	
	 @Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	 {
	     if (resultCode == Activity.RESULT_OK && requestCode == 5)
	          {
	          Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	          if (uri != null)
	          {
	          mAppPreferences.setNotificationTone(uri.toString());
	          mAppPreferences.setNotificationToneName(RingtoneManager.getRingtone(getActivity(), uri).getTitle(getActivity()));
	          tv_tone.setText(""+mAppPreferences.getNotificationToneName());
	          }	          
	      }            
	  }

	@Override
	public void onResume() {
		if(assigned_tab.toString().equalsIgnoreCase("R") || assigned_tab.toString().equalsIgnoreCase("M") ){
			ll_assignment.setVisibility(View.GONE);
			ll_updation.setVisibility(View.GONE);
			ll_escalation.setVisibility(View.GONE);
			//vw_aftr_assign.setVisibility(View.GONE);
			//vw_after_update.setVisibility(View.GONE);
			//vw_aftr_escalate.setVisibility(View.GONE);
		}
		if(schedule_tab.toString().equalsIgnoreCase("R")){
			ll_pm_schedule.setVisibility(View.GONE);
			ll_pm_escalate.setVisibility(View.GONE);
			//vw_aftr_pm_schedule.setVisibility(View.GONE);
			//vw_aftr_pm_escalate.setVisibility(View.GONE);
		}
		
		if(mAppPreferences.getTTAssignRb().equalsIgnoreCase("off")){
			tb_tt_assign.setChecked(false);
		}else{
			tb_tt_assign.setChecked(true);
		}
		if(mAppPreferences.getTTUpdateRb().equalsIgnoreCase("off")){
			tb_tt_update.setChecked(false);
		}else{
			tb_tt_update.setChecked(true);
		}
		if(mAppPreferences.getTTEscalateRb().equalsIgnoreCase("off")){
			tb_tt_escalate.setChecked(false);
		}else{
			tb_tt_escalate.setChecked(true);
		}
		if(mAppPreferences.getPMScheduleRb().equalsIgnoreCase("off")){
			tb_pm_schedule.setChecked(false);
		}else{
			tb_pm_schedule.setChecked(true);
		}
		if(mAppPreferences.getPMEscalateRb().equalsIgnoreCase("off")){
			tb_pm_escalate.setChecked(false);
		}else{
			tb_pm_escalate.setChecked(true);
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		mAppPreferences.setToggleButton("no");
		super.onPause();
	} 
}
