package com.isl.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.modal.BeanDashboardList;

import infozech.itower.R;

public class AdapterProblematic extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanDashboardList data_list;
	public AdapterProblematic(Context con, BeanDashboardList data_list) {
	this.con = con;
	this.data_list = data_list;
	inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
	return data_list.getProblematic_Site_list().size();
	//	return 5;
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
	public View getView(int position, View arg1, ViewGroup parent) {
		View vi = arg1;
		if (arg1 == null)
		vi = inflater.inflate( R.layout.adapter_problematic, null);
		TextView tv_site_id = (TextView) vi.findViewById( R.id.tv_site_id);
		TextView tv_site_type = (TextView) vi.findViewById( R.id.tv_site_type);
		TextView tv_site_class = (TextView) vi.findViewById( R.id.tv_site_class);
		tv_site_id.setText(data_list.getProblematic_Site_list().get(position).getSITE_ID());
		tv_site_type.setText(data_list.getProblematic_Site_list().get(position).getSITE_TYPE());
		tv_site_class.setText(data_list.getProblematic_Site_list().get(position).getSITE_CLASS());
		return vi;
	}
}
