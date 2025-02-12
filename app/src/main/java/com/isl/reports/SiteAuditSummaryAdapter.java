package com.isl.reports;
import infozech.itower.R;
import com.isl.modal.BeanReportsList;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import androidx.viewpager.widget.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SiteAuditSummaryAdapter extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanReportsList data_list;
	public SiteAuditSummaryAdapter(Context con , BeanReportsList list) {
		this.con = con;
		data_list= list;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data_list.getSite_Equipment_Audit_Summary().size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(final int position, View arg1, ViewGroup parent) {
		View vi = arg1;
		if (arg1 == null)
		vi = inflater.inflate(R.layout.adapter_site_audit_summary, null);
		final TextView tv_site_name=(TextView)vi.findViewById(R.id.tv_site_name1);
		final TextView tv_last_audit_dt=(TextView)vi.findViewById(R.id.tv_last_audit_dt1);
		final TextView tv_by_whom=(TextView)vi.findViewById(R.id.tv_by_whom1);
		TextView tv_dg=(TextView)vi.findViewById(R.id.tv_dg);
		TextView tv_battery=(TextView)vi.findViewById(R.id.tv_battery);
		TextView tv_ac_meter=(TextView)vi.findViewById(R.id.tv_ac_meter);
		TextView tv_aircon=(TextView)vi.findViewById(R.id.tv_aircon);
		TextView tv_dc_meter=(TextView)vi.findViewById(R.id.tv_dc_meter);
		TextView tv_fire=(TextView)vi.findViewById(R.id.tv_fire);
		TextView tv_fuel_tank=(TextView)vi.findViewById(R.id.tv_fuel_tank);
		TextView tv_piu=(TextView)vi.findViewById(R.id.tv_piu);
		TextView tv_rms=(TextView)vi.findViewById(R.id.tv_rms);
		TextView tv_smps=(TextView)vi.findViewById(R.id.tv_smps);
		TextView tv_security=(TextView)vi.findViewById(R.id.tv_security);
		TextView tv_shelter=(TextView)vi.findViewById(R.id.tv_shelter);
		TextView tv_solar=(TextView)vi.findViewById(R.id.tv_solar);
		final TextView tv_remarks=(TextView)vi.findViewById(R.id.tv_remarks);
		tv_site_name.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getSITE_NAME());
		tv_last_audit_dt.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getAUDIT_DATE());
		tv_by_whom.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getAUDIT_BY());
		tv_dg.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getDG());
		tv_battery.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getBATTERY());
		tv_aircon.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getAIRCON());
		tv_dc_meter.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getDC_METER());
		tv_fire.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getFIRE_EXT());
		tv_fuel_tank.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getFUEL_TANK());
		tv_piu.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getPIU());
		tv_rms.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getRMS());
		tv_smps.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getSMPS());
		tv_security.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getSECURITY_EQP());
		tv_shelter.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getSHELTER());
		tv_solar.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getSOLAR());
		tv_ac_meter.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getAC_METER());
		if(data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS()!=null && !data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS().isEmpty()){
			tv_remarks.setText(data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS());
		}else{
			tv_remarks.setText("No remarks added");
		}
		tv_remarks.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		if(data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS()!=null && !data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS().isEmpty()){
			popup(data_list.getSite_Equipment_Audit_Summary().get(position).getREMARKS(),tv_remarks);
		}else{
			popup("No remarks added",tv_remarks);
		}	
		}
		});
		
		tv_site_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getSite_Equipment_Audit_Summary().get(position).getSITE_NAME(),tv_site_name);
			
			}
		});
		
		tv_last_audit_dt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getSite_Equipment_Audit_Summary().get(position).getAUDIT_DATE(),tv_last_audit_dt);
			
			}
		});
		
		tv_by_whom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getSite_Equipment_Audit_Summary().get(position).getAUDIT_BY(),tv_by_whom);
			
			}
		});
		
		if(position%2==0){
			tv_site_name.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_last_audit_dt.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_by_whom.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_dg.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_battery.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_ac_meter.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_aircon.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_dc_meter.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_fire.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_fuel_tank.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_piu.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_rms.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_smps.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_security.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_shelter.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_solar.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_remarks.setBackgroundColor(Color.parseColor("#DCDCDC"));
		}else if(position%2==1){
			tv_site_name.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_last_audit_dt.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_by_whom.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_dg.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_battery.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_ac_meter.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_aircon.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_dc_meter.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_fire.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_fuel_tank.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_piu.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_rms.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_smps.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_security.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_shelter.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_solar.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_remarks.setBackgroundColor(Color.parseColor("#E6E6FA"));
			}
		return vi;
	}
	public void popup(String str,TextView tv){
		LayoutInflater headerinflate = (LayoutInflater) con.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View coloumwindow = (View) headerinflate.inflate(R.layout.popup_details, null);
		final PopupWindow popupMessage = new PopupWindow(coloumwindow,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,false);
		popupMessage.setOutsideTouchable(true);
		popupMessage.setContentView(coloumwindow);
		popupMessage.setBackgroundDrawable(new BitmapDrawable());
		popupMessage.setOutsideTouchable(true);
		popupMessage.setFocusable(true);
		popupMessage.showAsDropDown(tv, 0, 0);
		final TextView tv_details = (TextView) coloumwindow.findViewById(R.id.tv_details);
		tv_details.setText(""+str);
	}
}
