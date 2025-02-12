package com.isl.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.modal.BeanDashboardList;

import infozech.itower.R;

public class AdapterFrequentlyAlarm extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanDashboardList data_list;
	public AdapterFrequentlyAlarm(Context con,BeanDashboardList data_list) {
	this.con = con;
	this.data_list = data_list;
	inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
	return data_list.getMos_Frequent_Alarms_list().size();
	//return 5;
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
	vi = inflater.inflate( R.layout.adapter_frequency_alarm, null);
	TextView tv_description = (TextView) vi.findViewById( R.id.tv_description);
	TextView tv_count = (TextView) vi.findViewById( R.id.tv_count);
	tv_description.setText(data_list.getMos_Frequent_Alarms_list().get(position).getALARM_DESC());
	tv_count.setText(data_list.getMos_Frequent_Alarms_list().get(position).getCNT());
	return vi;
	}

}
