package com.isl.itower;

/*Created By : Dhakan lal Sharma
 * Version   : 0.1
 * CR        : iMaintain 1.8.1*/

/*Modify By  : Dhakan lal Sharma
 * Version   : 0.2
 * Bugs      : bugs#23745,23746

* Modified By : Dhakan lal sharma
* Modified On : 17-June-2017
* Version     : 0.3
* Purpose     : notification for beat plan and general notification
*/
import com.isl.modal.BeanAddNotification;
import com.isl.dao.cache.AppPreferences;

import infozech.itower.R;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AlwaysPopUP extends Activity {
	ArrayList<String> notification_list;
	BeanAddNotification data_list= null;
	Button bt_view,bt_close,bt_previous,bt_next;
	LinearLayout tt_linear,pm_linear,ff_linear,gen_linear,ut_linear;
	TextView tv_count,textView1,tv_ticket_id,tv_tt_site_id,tv_alarm_description,tv_tt_assigned_to,tv_alarm_duration,tv_tt_escalation_level,
			tv_pm_siteid,tv_pm_esca_level,tv_pm_activity_type,tv_pm_assigned_to,tv_pm_schedule_date,tv_pm_run_hour,tv_pm_current_run_hour,
			tv_ff_siteid,tv_fill_qty,tv_ff_dgType,tv_fillDate,tv_gen,tv_time,
			ut_site,ut_site_lat,ut_site_long;
	private float x1,x2;
	static final int MIN_DISTANCE = 150;
	int totalcounter=0,counter=0,counter1=1;
	LinearLayout rl_touch;
	ScrollView scrollView;
	AppPreferences mAppPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.always_show_pop_up);
		/*Utils.deleteNotification(this);
		bt_view=(Button)findViewById(R.id.bt_view);
		bt_close=(Button)findViewById(R.id.bt_close);
		bt_previous=(Button)findViewById(R.id.bt_previous);
		bt_next=(Button)findViewById(R.id.bt_next);
		tv_count=(TextView)findViewById(R.id.tv_count);
		textView1=(TextView) findViewById(R.id.textView1);
		tt_linear=(LinearLayout)findViewById(R.id.tt_linear);
		pm_linear=(LinearLayout)findViewById(R.id.pm_linear);
		tv_ticket_id=(TextView) findViewById(R.id.tv_ticket_id);
		tv_tt_site_id=(TextView) findViewById(R.id.tv_tt_site_id);
		tv_alarm_description=(TextView) findViewById(R.id.tv_alarm_description);
		tv_tt_assigned_to=(TextView) findViewById(R.id.tv_tt_assigned_to);
		tv_alarm_duration=(TextView) findViewById(R.id.tv_alarm_duration);
		tv_tt_escalation_level=(TextView) findViewById(R.id.tv_tt_escalation_level);
		tv_pm_siteid=(TextView) findViewById(R.id.tv_pm_siteid);
		tv_pm_esca_level=(TextView) findViewById(R.id.tv_pm_esca_level);
		tv_pm_activity_type=(TextView) findViewById(R.id.tv_pm_activity_type);
		tv_pm_assigned_to=(TextView) findViewById(R.id.tv_pm_assigned_to);
		tv_pm_schedule_date=(TextView) findViewById(R.id.tv_pm_schedule_date);
		tv_pm_run_hour =(TextView) findViewById(R.id.tv_pm_run_hour);
		tv_pm_current_run_hour=(TextView) findViewById(R.id.tv_pm_current_run_hour);
		ut_linear=(LinearLayout)findViewById(R.id.ut_linear);
		ut_site =(TextView) findViewById(R.id.ut_site);
		ut_site_lat =(TextView) findViewById(R.id.ut_site_lat);
		ut_site_long =(TextView) findViewById(R.id.ut_site_long);


		ff_linear=(LinearLayout)findViewById(R.id.ff_linear);//0.3
		gen_linear=(LinearLayout)findViewById(R.id.gen_linear);
		tv_ff_siteid =(TextView) findViewById(R.id.tv_ff_siteid);
		tv_fill_qty =(TextView) findViewById(R.id.tv_fill_qty);
		tv_ff_dgType =(TextView) findViewById(R.id.tv_ff_dgType);
		tv_fillDate =(TextView) findViewById(R.id.tv_fillDate);
		tv_gen =(TextView) findViewById(R.id.tv_gen);
		tv_time =(TextView) findViewById(R.id.tv_time);

		scrollView=(ScrollView)findViewById(R.id.scrollView);
		rl_touch=(LinearLayout)findViewById(R.id.rl_touch);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK ,"My_App");
		wl.acquire();
		bt_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl_touch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean status=false;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						x1 = event.getX();
						status=true;
						break;
					case MotionEvent.ACTION_UP:
						x2 = event.getX();
						status=true;
						float deltaX = x2 - x1;
						if (Math.abs(deltaX) > MIN_DISTANCE) {
							// Left/Right swipe action
							displayData(x1,x2);
						} else {
						}
						break;
				}
				return status;
			}
		});*/
	}

	/*private void displayData(float a,float b){
		if (b > a) {
			if(counter<=totalcounter && counter>0){
				counter--;
				counter1=counter+1;
			}else if(counter==0){
				counter=totalcounter-1;
				counter1=totalcounter;
			}
		}else {
			if(counter<=totalcounter-2 && counter>=0){
				counter++;
				counter1=counter+1;
			}else if(counter==totalcounter-1){
				counter=0;
				counter1=counter+1;
			}
		}
		Gson gson = new Gson();//call next/previous button
		data_list= null;
		data_list = gson.fromJson(notification_list.get(counter), BeanAddNotification.class);
		tv_count.setText(+counter1+" of "+totalcounter);
		setData();
	}
	@Override
	protected void onResume() {
		super.onResume();
		mAppPreferences = new AppPreferences(this);
		mAppPreferences.setCheckPopup("yes");
		notification_list = new ArrayList<String>();
		final DataBaseHelper dbHelper = new DataBaseHelper(AlwaysPopUP.this);
		dbHelper.open();
		notification_list = dbHelper.getAllNotification(mAppPreferences.getUserId());
		totalcounter=notification_list.size();
		bt_previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(notification_list!=null && notification_list.size()>0){
					displayData(1,2);
				}
			}
		});

		bt_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(notification_list!=null && notification_list.size()>0){
					displayData(2,1);
				}
			}
		});
		if(notification_list!=null && notification_list.size()>0){
			Gson gson = new Gson();//call initial
			data_list = gson.fromJson(notification_list.get(counter), BeanAddNotification.class);
			tv_count.setText(+counter1+" of "+totalcounter);
			setData();
		}

		//open notification list
		bt_view.setOnClickListener(new OnClickListener() {
			int a=0;
			@Override
			public void onClick(View v) {
				Gson gson = new Gson();
				BeanAddNotification data_list = gson.fromJson(notification_list.get(counter), BeanAddNotification.class);
				if(data_list.getNotification_type().equalsIgnoreCase("1") ||
						data_list.getNotification_type().equalsIgnoreCase("2") ||
						data_list.getNotification_type().equalsIgnoreCase("3")){
					a=1;
					Intent i = new Intent(AlwaysPopUP.this, TicketDetailsTabs.class);
					mAppPreferences.SetBackModeNotifi123(1);
					i.putExtra("id",data_list.getTkt_id());
					i.putExtra("rights",dbHelper.getSubMenuRight("AssignedTab","Incident"));
					startActivity(i);
					finish();
				}else if(data_list.getNotification_type().equalsIgnoreCase("4")){
					a=1;
					Intent i = new Intent(AlwaysPopUP.this, PMTabs.class);
					mAppPreferences.SetBackModeNotifi45(1);
					startActivity(i);
					finish();
				}else if(data_list.getNotification_type().equalsIgnoreCase("5")){
					a=1;
					Intent i = new Intent(AlwaysPopUP.this, PMTabs.class);
					mAppPreferences.SetBackModeNotifi45(2);
					mAppPreferences.setPMTabs("N");
					startActivity(i);
				}else if(data_list.getNotification_type().equalsIgnoreCase("6")){//0.3
					a=1;
					Intent i = new Intent(AlwaysPopUP.this, Schedule.class);
					startActivity(i);
					finish();
				}else if(data_list.getNotification_type().equalsIgnoreCase("7")){
					a=0;
					Intent i = new Intent(AlwaysPopUP.this, NotificationDetails.class);
					mAppPreferences.SetBackModeNotifi7(1);
					i.putExtra("genMSG",data_list.getGenMessage());
					i.putExtra("type",data_list.getNotification());
					i.putExtra("details", notification_list.get(counter));
					startActivity(i);
					finish();
				}else if(data_list.getNotification_type().equalsIgnoreCase("8")){
					a=0;
					GPSTracker gps = new GPSTracker(AlwaysPopUP.this);
					if(gps.canGetLocation()==false){
						gps.showSettingsAlert();
					}else {
						if ((String.valueOf(gps.getLatitude()) == null || String.valueOf(gps.getLatitude()).equalsIgnoreCase("0.0") || String.valueOf(gps.getLatitude()).isEmpty())
								|| (String.valueOf(gps.getLongitude()) == null || String.valueOf(gps.getLongitude()).equalsIgnoreCase("0.0") || String.valueOf(gps.getLongitude()).isEmpty())) {
							// Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
							Utils.toast(AlwaysPopUP.this, "252");
						} else{
							Intent i=new Intent(Intent.ACTION_VIEW);
							dbHelper.updateReadNotificationFlag(mAppPreferences.getUserId(),notification_list.get(counter));
							String saddr=String.valueOf(gps.getLatitude())+","+String.valueOf(gps.getLongitude());
							String daddr=data_list.getLattitude()+","+data_list.getLongitude();
							i.setData(Uri.parse("https://maps.google.com/maps?saddr="+saddr+"&daddr="+daddr));
							i.setPackage("com.google.android.apps.maps");
							startActivity(i);
						}


					}
				}
				if(a==1){
					dbHelper.deleteNotification(notification_list.get(counter),mAppPreferences.getUserId());
					dbHelper.close();
					NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					notifManager.cancelAll();
				}
			}
		});
	}
	public void setData(){
		if(data_list.getNotification_type().equalsIgnoreCase("1")){
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.GONE);
			tv_alarm_duration.setVisibility(View.GONE);
			tv_tt_escalation_level.setVisibility(View.GONE);
		}else if(data_list.getNotification_type().equalsIgnoreCase("2")){
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.GONE);
			tv_alarm_duration.setVisibility(View.GONE);//hQWO
			tv_tt_escalation_level.setVisibility(View.GONE);
		}else if(data_list.getNotification_type().equalsIgnoreCase("3")){
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.VISIBLE);
			tv_alarm_duration.setVisibility(View.VISIBLE);
			tv_tt_escalation_level.setVisibility(View.VISIBLE);
			tv_tt_assigned_to.setText("Assigned To - "+data_list.getAssignedTo());
			tv_alarm_duration.setText("Duration - "+data_list.getDuration());
			tv_tt_escalation_level.setText("Escalation Level - "+data_list.getEscalationLevel());
		}else if(data_list.getNotification_type().equalsIgnoreCase("4")){
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.VISIBLE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_pm_siteid.setText("Site ID - "+data_list.getSiteId());
			tv_pm_esca_level.setVisibility(View.GONE);
			tv_pm_activity_type.setText("Activity Type - "+data_list.getActivityType());
			tv_pm_assigned_to.setVisibility(View.GONE);
			tv_pm_schedule_date.setText("Schedule Date - "+data_list.getScheduleDate());
			if(data_list.getActivityType().equalsIgnoreCase("General Inspection")){
				tv_pm_run_hour.setVisibility(View.GONE);
				tv_pm_current_run_hour.setVisibility(View.GONE);
			}else{
				tv_pm_run_hour.setVisibility(View.VISIBLE);
				tv_pm_current_run_hour.setVisibility(View.VISIBLE);
				tv_pm_run_hour.setText("Run Hour - "+data_list.getRunHour());
				tv_pm_current_run_hour.setText("Current Run Hours - "+data_list.getCurrentRunHour());
			}
		}else if(data_list.getNotification_type().equalsIgnoreCase("5")){
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.VISIBLE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_pm_siteid.setText("Site ID - "+data_list.getSiteId());
			tv_pm_esca_level.setVisibility(View.VISIBLE);
			tv_pm_esca_level.setText("Escalation Level - "+data_list.getEscalationLevel());
			tv_pm_activity_type.setText("Activity Type - "+data_list.getActivityType());
			tv_pm_assigned_to.setVisibility(View.VISIBLE);
			tv_pm_assigned_to.setText("Assigned To - "+data_list.getAssignedTo());
			tv_pm_schedule_date.setText("Schedule Date - "+data_list.getScheduleDate());
			if(data_list.getActivityType().equalsIgnoreCase("General Inspection")){
				tv_pm_run_hour.setVisibility(View.GONE);
				tv_pm_current_run_hour.setVisibility(View.GONE);
			}else{
				tv_pm_run_hour.setVisibility(View.VISIBLE);
				tv_pm_current_run_hour.setVisibility(View.VISIBLE);
				tv_pm_run_hour.setText("Run Hour - "+data_list.getRunHour());
				tv_pm_current_run_hour.setText("Current Run Hour - "+data_list.getCurrentRunHour());
			}
		}else if(data_list.getNotification_type().equalsIgnoreCase("6")){ //0.3
			textView1.setText(data_list.getNotification());
			tt_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.VISIBLE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			tv_ff_siteid.setText("Site ID - "+data_list.getSiteId());
			tv_fill_qty.setText("Filled Qty. (Ltrs.) - "+data_list.getFillingQuantity());
			tv_ff_dgType.setText("Genset No. - "+data_list.getDGType());
			tv_fillDate.setText("Fill Date - "+data_list.getFillingDate());
		}else if(data_list.getNotification_type().equalsIgnoreCase("7")){ //0.3
			tt_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.VISIBLE);
			textView1.setText(data_list.getNotification());
			tv_gen.setText(""+data_list.getSiteId());
			tv_time.setText(""+data_list.getDisplayTime());
		}else if(data_list.getNotification_type().equalsIgnoreCase("8")){//0.3
			tt_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.VISIBLE);
			textView1.setText(data_list.getNotification());
			ut_site.setText("Site ID - "+data_list.getSiteId());
			ut_site_lat.setText("Site Latitude - "+data_list.getLattitude());
			ut_site_long.setText("Site Longitude - "+data_list.getLongitude());
		}
	}
	*//** Called when another activity is taking focus. *//*
	@Override
	protected void onPause() {
		mAppPreferences.setCheckPopup("no");
		super.onPause();
		ArrayList<String> notification_counter = new ArrayList<String>();
		final DataBaseHelper dbHelper = new DataBaseHelper(AlwaysPopUP.this);
		dbHelper.open();
		notification_counter = dbHelper.getNotificationCount(mAppPreferences.getUserId(),"0");
		ShortcutBadger.removeCount(AlwaysPopUP.this);
		if(notification_counter!=null && notification_counter.size()>0){
			ShortcutBadger.applyCount(AlwaysPopUP.this,notification_counter.size());
		}
	}*/
}
