package com.isl.itower;
import com.isl.modal.BeanAddNotification;
import infozech.itower.R;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
public class AdapterNotificationList extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanAddNotification data_list;
	ArrayList<String> list,flag;
	TextView txt_notification_type,tv_ticket_id,tv_tt_site_id,tv_alarm_description,tv_tt_assigned_to,tv_alarm_duration,tv_tt_escalation_level,
	         tv_pm_siteid,tv_pm_esca_level,tv_pm_activity_type,tv_pm_assigned_to,tv_pm_schedule_date,tv_pm_run_hour,tv_pm_current_run_hour,
	         tv_ff_siteid,tv_fill_qty,tv_ff_dgType,tv_fillDate,tv_gen,tv_time,
	         ut_site,ut_site_lat,ut_site_long,
	         fpr_site_id,fpr_suplier,fpr_request_date,fpr_approval_date,fpr_circle,fpr_zone,fpr_cluster,fpr_status,fpr_aqty;
	LinearLayout tt_linear,pm_linear,ff_linear,gen_linear,ll_parent,ut_linear,fpr_linear;
	public AdapterNotificationList(Context con,ArrayList<String> list,ArrayList<String> flag) {
		this.con = con;
		this.list=list;
		this.flag=flag;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View arg1, ViewGroup parent) {
		Gson gson = new Gson();
		data_list = gson.fromJson(list.get(position), BeanAddNotification.class);
		View vi = arg1;
		if (arg1 == null)
		vi = inflater.inflate(R.layout.list_notiifcation_detals, null);
		ll_parent=(LinearLayout)vi.findViewById(R.id.ll_parent);
		tt_linear=(LinearLayout)vi.findViewById(R.id.tt_linear);
		pm_linear=(LinearLayout)vi.findViewById(R.id.pm_linear);
		ut_linear=(LinearLayout)vi.findViewById(R.id.ut_linear);
		fpr_linear=(LinearLayout)vi.findViewById(R.id.fpr_linear);
		txt_notification_type=(TextView) vi.findViewById(R.id.txt_notification_type);
		tv_ticket_id=(TextView) vi.findViewById(R.id.tv_ticket_id);
		tv_tt_site_id=(TextView) vi.findViewById(R.id.tv_tt_site_id);
		tv_alarm_description=(TextView) vi.findViewById(R.id.tv_alarm_description);
		tv_tt_assigned_to=(TextView) vi.findViewById(R.id.tv_tt_assigned_to);
		tv_alarm_duration=(TextView) vi.findViewById(R.id.tv_alarm_duration);
		tv_tt_escalation_level=(TextView) vi.findViewById(R.id.tv_tt_escalation_level);
        tv_pm_siteid=(TextView) vi.findViewById(R.id.tv_pm_siteid);
        tv_pm_esca_level=(TextView) vi.findViewById(R.id.tv_pm_esca_level);
        tv_pm_activity_type=(TextView) vi.findViewById(R.id.tv_pm_activity_type);
        tv_pm_assigned_to=(TextView) vi.findViewById(R.id.tv_pm_assigned_to);
        tv_pm_schedule_date=(TextView) vi.findViewById(R.id.tv_pm_schedule_date);
        tv_pm_run_hour=(TextView) vi.findViewById(R.id.tv_pm_run_hour);
        tv_pm_current_run_hour=(TextView) vi.findViewById(R.id.tv_pm_current_run_hour);
        ff_linear=(LinearLayout)vi.findViewById(R.id.ff_linear); //0.3
		gen_linear=(LinearLayout)vi.findViewById(R.id.gen_linear);
        tv_ff_siteid =(TextView) vi.findViewById(R.id.tv_ff_siteid);
        tv_fill_qty =(TextView) vi.findViewById(R.id.tv_fill_qty);
        tv_ff_dgType =(TextView) vi.findViewById(R.id.tv_ff_dgType);
        tv_fillDate =(TextView) vi.findViewById(R.id.tv_fillDate);
        tv_gen =(TextView) vi.findViewById(R.id.tv_gen);
        tv_time =(TextView) vi.findViewById(R.id.tv_time);
        ut_site =(TextView) vi.findViewById(R.id.ut_site);
        ut_site_lat =(TextView) vi.findViewById(R.id.ut_site_lat);
        ut_site_long =(TextView) vi.findViewById(R.id.ut_site_long);

		fpr_site_id =(TextView) vi.findViewById(R.id.fpr_site_id);
		fpr_suplier =(TextView) vi.findViewById(R.id.fpr_suplier);
		fpr_request_date =(TextView) vi.findViewById(R.id.fpr_request_date);
		fpr_approval_date =(TextView) vi.findViewById(R.id.fpr_approval_date);
		fpr_circle =(TextView) vi.findViewById(R.id.fpr_circle);
		fpr_zone =(TextView) vi.findViewById(R.id.fpr_zone);
		fpr_cluster =(TextView) vi.findViewById(R.id.fpr_cluster);
		fpr_status=(TextView) vi.findViewById(R.id.fpr_status);
		fpr_aqty=(TextView) vi.findViewById(R.id.fpr_aqty);
        if(flag.get(position).equalsIgnoreCase("1")){
        	ll_parent.setBackgroundColor(Color.parseColor("#F2F3F4"));
        }else{
        	ll_parent.setBackgroundColor(con.getResources().getColor(R.color.bg_color_white));
        }
       
  		if(data_list.getNotification_type().equalsIgnoreCase("1")){
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			fpr_linear.setVisibility(View.GONE);
			txt_notification_type.setText(data_list.getNotification());
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.GONE);
			tv_alarm_duration.setVisibility(View.GONE);
			tv_tt_escalation_level.setVisibility(View.GONE);
			
		}else if(data_list.getNotification_type().equalsIgnoreCase("2")){
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			fpr_linear.setVisibility(View.GONE);
			txt_notification_type.setText(data_list.getNotification());
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.GONE);
			tv_alarm_duration.setVisibility(View.GONE);
			tv_tt_escalation_level.setVisibility(View.GONE);
		}else if(data_list.getNotification_type().equalsIgnoreCase("3")){
			tt_linear.setVisibility(View.VISIBLE);
			pm_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			ut_linear.setVisibility(View.GONE);
			fpr_linear.setVisibility(View.GONE);
			txt_notification_type.setText(data_list.getNotification());
			tv_ticket_id.setText("Ticket ID - "+data_list.getTkt_id());
			tv_tt_site_id.setText("Site ID - "+data_list.getSiteId());
			tv_alarm_description.setText("Alarm Description - "+data_list.getAlarmDescription());
			tv_tt_assigned_to.setVisibility(View.VISIBLE);
			tv_alarm_duration.setVisibility(View.VISIBLE);
			tv_tt_escalation_level.setVisibility(View.VISIBLE);

			tv_tt_assigned_to.setText("Assigned To - "+data_list.getAssignedTo());
			tv_alarm_duration.setText("Duration - "+data_list.getDuration());
			tv_tt_escalation_level.setText("Escalation Level - "+data_list.getEscalationLevel());
		}else if(data_list.getNotification_type().equalsIgnoreCase("4")
				|| data_list.getNotification_type().equalsIgnoreCase("14")
				||data_list.getNotification_type().equalsIgnoreCase("13")){
			tt_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.VISIBLE);
			ut_linear.setVisibility(View.GONE);
			fpr_linear.setVisibility(View.GONE);
			txt_notification_type.setText(data_list.getNotification());
			//txt_notification_type.setText("Site Activity Schedule Notification");
			//txt_notification_type.setText("Site Activity Missed Notification");
			tv_pm_siteid.setText("Site ID - "+data_list.getSiteId());
			tv_pm_esca_level.setVisibility(View.GONE);
			tv_pm_activity_type.setText("Activity Type - "+data_list.getActivityType());
			tv_pm_assigned_to.setVisibility(View.GONE);
			tv_pm_schedule_date.setText("Schedule Date - "+data_list.getScheduleDate());
			tv_pm_run_hour.setText("Status - "+data_list.getStatus());
			tv_pm_run_hour.setVisibility(View.GONE);
			tv_pm_current_run_hour.setVisibility(View.GONE);
			if(data_list.getNotification_type().equalsIgnoreCase("14")){
				tv_pm_run_hour.setVisibility(View.VISIBLE);
				tv_pm_run_hour.setText("Done Date - "+data_list.getDoneDate());
			}
			/*if(data_list.getActivityType().equalsIgnoreCase("General Inspection")){
			tv_pm_run_hour.setVisibility(View.GONE);
			tv_pm_current_run_hour.setVisibility(View.GONE);
			}else{
			tv_pm_run_hour.setVisibility(View.VISIBLE);
			tv_pm_current_run_hour.setVisibility(View.VISIBLE);	
			tv_pm_run_hour.setText("Run Hours - "+data_list.getRunHour());
			tv_pm_current_run_hour.setText("Current Run Hours - "+data_list.getCurrentRunHour());
			}*/

			}else if(data_list.getNotification_type().equalsIgnoreCase("5")	){
				tt_linear.setVisibility(View.GONE);
				ff_linear.setVisibility(View.GONE);
				gen_linear.setVisibility(View.GONE);
				pm_linear.setVisibility(View.VISIBLE);
				ut_linear.setVisibility(View.GONE);
			    fpr_linear.setVisibility(View.GONE);
				txt_notification_type.setText(data_list.getNotification());
				tv_pm_siteid.setText("Site ID - "+data_list.getSiteId());
				tv_pm_esca_level.setVisibility(View.VISIBLE);
				tv_pm_esca_level.setText("Escalation Level - "+data_list.getEscalationLevel());
				tv_pm_activity_type.setText("Activity Type - "+data_list.getActivityType());
				tv_pm_assigned_to.setVisibility(View.VISIBLE);
				tv_pm_assigned_to.setText("Assigned To - "+data_list.getAssignedTo());
				tv_pm_schedule_date.setText("Schedule Date - "+data_list.getScheduleDate());
			    tv_pm_run_hour.setText("Status - "+data_list.getStatus());
				/*if(data_list.getActivityType().equalsIgnoreCase("General Inspection")){
				tv_pm_run_hour.setVisibility(View.GONE);
				tv_pm_current_run_hour.setVisibility(View.GONE);
				}else{
				tv_pm_run_hour.setVisibility(View.VISIBLE);
				tv_pm_current_run_hour.setVisibility(View.VISIBLE);	
				tv_pm_run_hour.setText("Run Hour - "+data_list.getRunHour());
				tv_pm_current_run_hour.setText("Current Run Hour - "+data_list.getCurrentRunHour());
				}*/
		 }else if(data_list.getNotification_type().equalsIgnoreCase("6")){//0.3
			  txt_notification_type.setText(data_list.getNotification()); 
	    	  tt_linear.setVisibility(View.GONE);
			  pm_linear.setVisibility(View.GONE);
			  ff_linear.setVisibility(View.VISIBLE);
			  gen_linear.setVisibility(View.GONE);
			  ut_linear.setVisibility(View.GONE);
			  fpr_linear.setVisibility(View.GONE);
			  tv_ff_siteid.setText("Site ID - "+data_list.getSiteId());
		      tv_fill_qty.setText("Filled Qty. (Ltrs.) - "+data_list.getFillingQuantity());
		      tv_ff_dgType.setText("Genset No. - "+data_list.getDGType());
		      tv_fillDate.setText("Fill Date - "+data_list.getFillingDate());
		  }else if(data_list.getNotification_type().equalsIgnoreCase("7")){//0.3
	    	  tt_linear.setVisibility(View.GONE);
			  pm_linear.setVisibility(View.GONE);
			  ff_linear.setVisibility(View.GONE);
			  gen_linear.setVisibility(View.VISIBLE);
			  ut_linear.setVisibility(View.GONE);
			  fpr_linear.setVisibility(View.GONE);
			  txt_notification_type.setText(data_list.getNotification()); 
			  tv_gen.setText(""+data_list.getSiteId());
			  tv_time.setText(""+data_list.getDisplayTime());
	      }else if(data_list.getNotification_type().equalsIgnoreCase("8")){//0.3
	    	  tt_linear.setVisibility(View.GONE);
			  pm_linear.setVisibility(View.GONE);
			  ff_linear.setVisibility(View.GONE);
			  gen_linear.setVisibility(View.GONE);
			  ut_linear.setVisibility(View.VISIBLE);
			  fpr_linear.setVisibility(View.GONE);
			  txt_notification_type.setText(data_list.getNotification()); 
			  ut_site.setText("Site ID - "+data_list.getSiteId());
			  ut_site_lat.setText("Site Latitude - "+data_list.getLattitude());
			  ut_site_long.setText("Site Longitude - "+data_list.getLongitude());
		    }else if(data_list.getNotification_type().equalsIgnoreCase("11")){
	      	  tt_linear.setVisibility(View.GONE);
			  pm_linear.setVisibility(View.GONE);
			  ff_linear.setVisibility(View.GONE);
			  gen_linear.setVisibility(View.GONE);
			  ut_linear.setVisibility(View.GONE);
			  fpr_linear.setVisibility(View.VISIBLE);
			txt_notification_type.setText(data_list.getNotification());
			fpr_site_id.setText("Site ID - "+data_list.getSiteId());
			fpr_suplier.setText("Fuel Supplier - "+data_list.getSupplierName());
			fpr_request_date.setText("Request Date - "+data_list.getRequestDate());
			fpr_approval_date.setText("Approval Date - "+data_list.getApprovalDate());
			fpr_circle.setText("Region - "+data_list.getCircle());
			fpr_zone.setText("Sub Region - "+data_list.getZone());
			fpr_cluster.setText("Mini Cluster - "+data_list.getCluster());
			fpr_status.setText("Status - "+data_list.getTxnStatus());
			fpr_aqty.setText("Approve Quantity - "+data_list.getAqty());
		   }else if(data_list.getNotification_type().equalsIgnoreCase("12")){
			tt_linear.setVisibility(View.GONE);
			ff_linear.setVisibility(View.GONE);
			gen_linear.setVisibility(View.GONE);
			pm_linear.setVisibility(View.VISIBLE);
			ut_linear.setVisibility(View.GONE);
			fpr_linear.setVisibility(View.GONE);
			txt_notification_type.setText(data_list.getNotification());
			tv_pm_siteid.setText("Site ID - "+data_list.getSiteId());
			tv_pm_esca_level.setVisibility(View.GONE);
			tv_pm_activity_type.setText("Activity Type - "+data_list.getActivityType());
			tv_pm_assigned_to.setVisibility(View.GONE);
			tv_pm_schedule_date.setText("Schedule Date - "+data_list.getScheduleDate());
			tv_pm_run_hour.setText("Done Date - "+data_list.getDoneDate());
			//tv_pm_run_hour.setText("Done Date - 08-SEP-2019 "+data_list.getDoneDate());
			tv_pm_current_run_hour.setText("Rejected By - "+data_list.getRejectBy());

	    }
  	      return vi;
	    }
}