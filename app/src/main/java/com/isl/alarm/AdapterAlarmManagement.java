package com.isl.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.modal.BeansAlarmList;

import infozech.itower.R;

@SuppressLint("InflateParams")
public class AdapterAlarmManagement extends BaseAdapter {
	BeansAlarmList list_des;
	Context con;
	private LayoutInflater inflater = null;

	public AdapterAlarmManagement(Context con, String data) {
		this.con = con;
		Gson g = new Gson();
		this.list_des = g.fromJson(data, BeansAlarmList.class);
		inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list_des.getActiveAlarm_list().size();

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
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi = inflater.inflate( R.layout.custom_alarm, null);
		TextView Descriptions = (TextView) vi.findViewById( R.id.txt_descriptions);
		TextView Date = (TextView) vi.findViewById( R.id.txt_date);
		TextView Since = (TextView) vi.findViewById( R.id.txt_since);
		TextView AlamType = (TextView) vi.findViewById( R.id.txt_alamType);
		Descriptions.setText(list_des.getActiveAlarm_list().get(position).get_DESCRIPTION());
		Date.setText(list_des.getActiveAlarm_list().get(position).getDATE());
		Since.setText(list_des.getActiveAlarm_list().get(position).getSINCE());
		AlamType.setText(list_des.getActiveAlarm_list().get(position).getAlamType());
		return vi;
	}
}
