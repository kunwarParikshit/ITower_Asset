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

public class MissingAdapter extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanReportsList data_list;
	public MissingAdapter(Context con , BeanReportsList list) {
		this.con = con;
		data_list= list;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data_list.getMissing_Asset_Details_list().size();
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
		vi = inflater.inflate(R.layout.adapter_missing_assets, null);
		final TextView tv_site_name=(TextView)vi.findViewById(R.id.tv_site_name);
		final TextView tv_audit_date=(TextView)vi.findViewById(R.id.tv_audit_date);
		final TextView tv_missing_asset=(TextView)vi.findViewById(R.id.tv_missing_asset);
		tv_site_name.setText(data_list.getMissing_Asset_Details_list().get(position).getSITE_NAME());
		tv_audit_date.setText(data_list.getMissing_Asset_Details_list().get(position).getAUDIT_DATE());
		tv_missing_asset.setText(data_list.getMissing_Asset_Details_list().get(position).getMISSING_ASSET());
		    tv_site_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getMissing_Asset_Details_list().get(position).getSITE_NAME(),tv_site_name);
			}
			});
			
		    tv_audit_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getMissing_Asset_Details_list().get(position).getAUDIT_DATE(),tv_audit_date);
			}
			});
			
		    tv_missing_asset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			popup(data_list.getMissing_Asset_Details_list().get(position).getMISSING_ASSET(),tv_missing_asset);
			}
			});
		if(position%2==0){
			tv_site_name.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_audit_date.setBackgroundColor(Color.parseColor("#DCDCDC"));
			tv_missing_asset.setBackgroundColor(Color.parseColor("#DCDCDC"));
		}else if(position%2==1){
			tv_site_name.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_audit_date.setBackgroundColor(Color.parseColor("#E6E6FA"));
			tv_missing_asset.setBackgroundColor(Color.parseColor("#E6E6FA"));
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
