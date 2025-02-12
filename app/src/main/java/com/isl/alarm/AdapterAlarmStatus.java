package com.isl.alarm;

// for Current Status iMaintain CR#1.5.2.2 in Alarm Management

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.modal.BeanCurrentStatusList;

import infozech.itower.R;

@SuppressLint("InflateParams")
public class AdapterAlarmStatus extends BaseAdapter {
	
	Context con;
	private LayoutInflater inflater = null;
	TextView tv_fields,tv_col;
	BeanCurrentStatusList data_list;
	public AdapterAlarmStatus(Context con, String response_site_description) {
		this.con = con;
		Gson g = new Gson();
		this.data_list = g.fromJson(response_site_description, BeanCurrentStatusList.class);
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data_list.getGetAlarmStatus_list().size();
		//return 8;

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
		vi = inflater.inflate( R.layout.adapter_alarm_status, null);
		tv_fields = (TextView) vi.findViewById( R.id.textView13);
		tv_col = (TextView) vi.findViewById( R.id.textView2);
		tv_fields.setText(data_list.getGetAlarmStatus_list().get(position).getFIELDS());
		tv_col.setText(data_list.getGetAlarmStatus_list().get(position).getCOL());
		return vi;
	}
}
