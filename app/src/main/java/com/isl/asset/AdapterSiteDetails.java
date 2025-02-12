package com.isl.asset;
import infozech.itower.R;
import com.isl.modal.BeanAssetModuleList;
import com.isl.dao.cache.AppPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class AdapterSiteDetails extends BaseAdapter {
	Context con;
	AppPreferences mAppPreferences;
	private LayoutInflater inflater = null;
	BeanAssetModuleList data_list;
	String general_details,passive_asset,active_asset;
	public AdapterSiteDetails(Context con, BeanAssetModuleList data_list,String general_details,String passive_asset,String active_asset) {
		this.con = con;
		this.data_list = data_list;
		this.general_details=general_details;
		this.passive_asset=passive_asset;
		this.active_asset=active_asset;
		mAppPreferences = new AppPreferences(con);
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data_list.getSite_Details_list().size();
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
		vi = inflater.inflate(R.layout.adapter_site_details, null);
		TextView tv_site_id = (TextView) vi.findViewById(R.id.tv_site_id);
		TextView tv_site_name = (TextView) vi.findViewById(R.id.tv_site_name);
		TextView tv_site_status = (TextView) vi.findViewById(R.id.tv_site_status);
		tv_site_id.setText(data_list.getSite_Details_list().get(position).getSITE_ID());
		tv_site_name.setText(data_list.getSite_Details_list().get(position).getSITE_NAME());
		tv_site_status.setText(data_list.getSite_Details_list().get(position).getSTATUS());
		tv_site_id.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(active_asset.equalsIgnoreCase("R")
						&& passive_asset.equalsIgnoreCase("R") && general_details.equalsIgnoreCase("R") 
						|| active_asset.equalsIgnoreCase("R")&& passive_asset.equalsIgnoreCase("M")	&& general_details.equalsIgnoreCase("R") 
						|| active_asset.equalsIgnoreCase("M")&& passive_asset.equalsIgnoreCase("M")	&& general_details.equalsIgnoreCase("M") 
						|| active_asset.equalsIgnoreCase("M") && passive_asset.equalsIgnoreCase("R") && general_details.equalsIgnoreCase("R") 
						|| active_asset.equalsIgnoreCase("R") && passive_asset.equalsIgnoreCase("R") && general_details.equalsIgnoreCase("M") 
						|| active_asset.equalsIgnoreCase("M") && passive_asset.equalsIgnoreCase("M") && general_details.equalsIgnoreCase("R") 
						|| active_asset.equalsIgnoreCase("R") && passive_asset.equalsIgnoreCase("M") && general_details.equalsIgnoreCase("M") 
						|| active_asset.equalsIgnoreCase("M") && passive_asset.equalsIgnoreCase("R") && general_details.equalsIgnoreCase("M")){
					Toast.makeText(con,"You are not authorized for menus.",Toast.LENGTH_SHORT).show();
					}else{
					Intent intent = new Intent(con.getApplicationContext(),AuditScheduleList.class);
					mAppPreferences.setSiteID(data_list.getSite_Details_list().get(position).getSITE_ID());
					mAppPreferences.setEtsSiteID(data_list.getSite_Details_list().get(position).getETS_SITE_ID());
					mAppPreferences.setSiteName(data_list.getSite_Details_list().get(position).getSITE_NAME());
					con.startActivity(intent);
					((Activity)con).finish();
			}
			}});
		    return vi;
	}
}
