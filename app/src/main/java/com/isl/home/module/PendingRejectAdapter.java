package com.isl.home.module;
import infozech.itower.R;
import com.isl.modal.BeanAddTicket;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;

/*Created By : Dhakan Lal Sharma
Modified On : 16-June-2016
Version     : 0.1
CR          : iMaintan 1.9.1.1*/
public class PendingRejectAdapter extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	ArrayList<String> activity_list,flag_list,error_list;
	TextView txt_activity,txt_site_id,txt_ticket_status,txt_description,txt_date,txt_vendor,txt_vendor1,txt_error,
			site_id,spare_name,qty,spare_data,type,ticket_id,status,txt_error1,txt_rejected_rmk;
	BeanAddTicket ticket_data;
	AppPreferences mAppPreferences;
	RelativeLayout ticket_info1,ticket_info2;
	int mode;
	DataBaseHelper db;
	public PendingRejectAdapter(Context con,ArrayList<String> activity_list,ArrayList<String> flag_list,ArrayList<String> error_list,int mode) {
		this.con = con;
		this.activity_list=activity_list;
		this.flag_list=flag_list;
		this.error_list=error_list;
		this.mode=mode;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		db=new DataBaseHelper(con);
		db.open();
		mAppPreferences=new AppPreferences(con);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return activity_list.size();
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
		View vi = arg1;
		if (arg1 == null)
			vi = inflater.inflate(R.layout.pending_activity, null);
		    ticket_info1 = (RelativeLayout) vi.findViewById(R.id.ticket_info1);
		    ticket_info2 = (RelativeLayout) vi.findViewById(R.id.ticket_info2);
		    site_id = (TextView) vi.findViewById(R.id.txt_site);
		    spare_name = (TextView) vi.findViewById(R.id.txt_spare_name);
		    qty = (TextView) vi.findViewById(R.id.txt_spare_qty);
		    spare_data = (TextView) vi.findViewById(R.id.txt_spare_date);
		    type = (TextView) vi.findViewById(R.id.txt_checklist_type);
		    ticket_id = (TextView) vi.findViewById(R.id.txt_ticket_id);
		    status = (TextView) vi.findViewById(R.id.txt_status);
		    txt_error1=(TextView)vi.findViewById(R.id.txt_error1);
			txt_rejected_rmk=(TextView)vi.findViewById(R.id.txt_rejected_rmk);

		    txt_activity=(TextView)vi.findViewById(R.id.txt_activity);
		    txt_site_id=(TextView)vi.findViewById(R.id.txt_site_id);
		    txt_ticket_status=(TextView)vi.findViewById(R.id.txt_ticket_status);
		    txt_description=(TextView)vi.findViewById(R.id.txt_description);
		    txt_date=(TextView)vi.findViewById(R.id.txt_date);
		    txt_vendor=(TextView)vi.findViewById(R.id.txt_vendor);
		    txt_vendor1=(TextView)vi.findViewById(R.id.txt_vendor1);
		    txt_error=(TextView)vi.findViewById(R.id.txt_error);

		    
		    txt_activity.setTypeface(Utils.typeFace(con));
		    txt_site_id.setTypeface(Utils.typeFace(con));
		    txt_ticket_status.setTypeface(Utils.typeFace(con));
		    txt_description.setTypeface(Utils.typeFace(con));
		    txt_date.setTypeface(Utils.typeFace(con));
		    txt_vendor.setTypeface(Utils.typeFace(con));
		    txt_vendor1.setTypeface(Utils.typeFace(con));
		    txt_error.setTypeface(Utils.typeFace(con));
			txt_rejected_rmk.setTypeface(Utils.typeFace(con));
			txt_rejected_rmk.setVisibility(View.GONE);

		    if(flag_list.get(position).equalsIgnoreCase("1")){
				ticket_info2.setVisibility( View.GONE );
				txt_vendor.setVisibility( View.GONE );
		    	txt_description.setVisibility(View.VISIBLE);
		    	ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);	
		       	txt_activity.setText(Utils.msg(con, "73"));
		    	txt_site_id.setText(""+ticket_data.getSid());
		    	txt_ticket_status.setText(Utils.msg(con, "263"));
		    	txt_date.setText(""+ticket_data.getTtLogDt());
		    	//txt_description.setText(""+db.getDesc(ticket_data.getAldescId()));
				txt_description.setText(""+ticket_data.getAlarmDes());
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
		    }else if(flag_list.get(position).equalsIgnoreCase("2")){
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
				ticket_info2.setVisibility( View.GONE );
				txt_vendor.setVisibility( View.GONE );
		    	txt_description.setVisibility(View.VISIBLE);
		    	ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
		    	txt_activity.setText(Utils.msg(con, "224"));
		    	txt_site_id.setText(""+ticket_data.getSid());
		    	txt_date.setText(""+ticket_data.getSchDt());
		    	Utils.msgText(con, ticket_data.getActTypeId(), txt_ticket_status);
				if(ticket_data.getStatus().equalsIgnoreCase("Scheduled")){
				Utils.msgText(con, "219", txt_description);	
				}else if(ticket_data.getStatus().equalsIgnoreCase("Missed")	){
				Utils.msgText(con, "222", txt_description);	
				}else if(ticket_data.getStatus().equalsIgnoreCase("Done")){
				Utils.msgText(con, "223", txt_description);	
				}else if(ticket_data.getStatus().equalsIgnoreCase("Review")	){
				Utils.msgText(con, "221", txt_description);	
				}else if(ticket_data.getStatus().equalsIgnoreCase("Pending")){
				Utils.msgText(con, "220", txt_description);	
				}else{
				txt_description.setText(ticket_data.getStatus());	
				}
		    }else if(flag_list.get(position).equalsIgnoreCase("FF")){
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
				ticket_info2.setVisibility( View.GONE );
				txt_vendor.setVisibility( View.GONE );
		    	txt_description.setVisibility(View.GONE);
		    	ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
		    	txt_activity.setText(Utils.msg(con, "166"));
		    	txt_site_id.setText(""+ticket_data.getSid());
		    	txt_ticket_status.setText(""+db.getDgName(ticket_data.getDg()));
		    	txt_date.setText(""+ticket_data.getFdt());
		    }else if(flag_list.get(position).equalsIgnoreCase("FP")){
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
				ticket_info2.setVisibility( View.GONE );
				txt_vendor.setVisibility( View.GONE );
		    	txt_description.setVisibility(View.GONE);
		    	ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
		    	txt_activity.setText(Utils.msg(con, "194"));
		    	txt_site_id.setText(""+db.getCircleName(ticket_data.getCid()));
		    	txt_ticket_status.setText(""+db.getVenderName(ticket_data.getOid()));
		    	txt_date.setVisibility( View.GONE );
		    }else if(flag_list.get(position).equalsIgnoreCase("SPARE")){
				if(mode==0){
					txt_error1.setVisibility(View.GONE);
				}else{
					txt_error1.setVisibility(View.VISIBLE);
					txt_error1.setText(""+error_list.get(position));
				}
				ticket_info1.setVisibility( View.GONE);
				txt_vendor.setVisibility( View.GONE );
				ticket_info2.setVisibility( View.VISIBLE );
				ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
				site_id.setText(ticket_data.getSid());
				spare_name.setText(Utils.msg(con,"437" )+ticket_data.getSparePartName());
				qty.setText(Utils.msg(con,"438" )+ticket_data.getQty());
				spare_data.setText(ticket_data.getSchDt());
				type.setText(ticket_data.getActTypeId());
				ticket_id.setText(ticket_data.getTid());
				txt_vendor1.setText(Utils.msg(con,"439" )+ticket_data.getOmeName());
				status.setText("Pending");
			}else if(flag_list.get(position).equalsIgnoreCase("DFR")){
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
				ticket_info2.setVisibility( View.GONE );
				ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
				txt_activity.setText("DFR Approval");
				txt_site_id.setText(""+ticket_data.getSid());
				txt_date.setText(""+ticket_data.getSchDt());
				txt_ticket_status.setText(Utils.msg( con,"410")+ticket_data.getFiller());
				txt_description.setText(Utils.msg( con,"411")+ticket_data.getDg());
				txt_vendor.setText(Utils.msg(con,"439" )+ticket_data.getOmeName());
			}
			else if(flag_list.get(position).equalsIgnoreCase("5")){
				if(mode==0){
					txt_error.setVisibility(View.GONE);
				}else{
					txt_error.setVisibility(View.VISIBLE);
					txt_error.setText(""+error_list.get(position));
				}
				ticket_info2.setVisibility( View.GONE );
				txt_vendor.setVisibility( View.GONE );
				txt_description.setVisibility(View.VISIBLE);
				ticket_data = gson.fromJson(activity_list.get(position), BeanAddTicket.class);
				txt_activity.setText(Utils.msg(con, "224"));
				txt_site_id.setText(""+ticket_data.getSid());
				txt_date.setText(""+ticket_data.getSchDt());
				Utils.msgText(con, ticket_data.getActTypeId(), txt_ticket_status);
				if(ticket_data.getStatus().equalsIgnoreCase("Scheduled")){
					Utils.msgText(con, "219", txt_description);
				}else if(ticket_data.getStatus().equalsIgnoreCase("Missed")	){
					Utils.msgText(con, "222", txt_description);
				}else if(ticket_data.getStatus().equalsIgnoreCase("Done")){
					Utils.msgText(con, "223", txt_description);
				}else if(ticket_data.getStatus().equalsIgnoreCase("Review")	){
					Utils.msgText(con, "221", txt_description);
				}else if(ticket_data.getStatus().equalsIgnoreCase("Pending")){
					Utils.msgText(con, "220", txt_description);
				}else{
					txt_description.setText(ticket_data.getStatus());
				}
				if(ticket_data.getAddPrms() != null)
				{
					String addParam[]=ticket_data.getAddPrms().split("~")[1].split("=");
					if(addParam[1].equalsIgnoreCase("J"))
					{
						txt_error.setVisibility(View.VISIBLE);
						txt_error.setText("Category : "+ticket_data.getRejRmkTxt());

						txt_rejected_rmk.setVisibility(View.VISIBLE);
						txt_rejected_rmk.setText("Remarks : "+ticket_data.getRejRmk());
						Utils.msgText(con, "466", txt_description);
					}
				}
			}
			return vi;
	    }
	
}