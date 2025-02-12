package com.isl.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.modal.BeanDashboardList;

import infozech.itower.R;

public class AdapterOpenAlarm extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanDashboardList data_list;
	int a, b, c, d, e;
	public AdapterOpenAlarm(Context con, BeanDashboardList data_list, int a, int b, int c, int d, int e) {
	this.con = con;
	this.data_list = data_list;
	this.a = a;
	this.b = b;
	this.c = c;
	this.d = d;
	this.e = e;
	inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
	return data_list.getOpenAlarmSummary_list().size();
	//return 6;
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
		vi = inflater.inflate( R.layout.adapter_open_alarm, null);
		TextView tv_interval = (TextView) vi.findViewById( R.id.tv_interval);
		TextView tv_sitedown = (TextView) vi.findViewById( R.id.tv_sitedown);
		TextView tv_critical = (TextView) vi.findViewById( R.id.tv_critical);
		TextView tv_major = (TextView) vi.findViewById( R.id.tv_major);
		TextView tv_minor = (TextView) vi.findViewById( R.id.tv_minor);
		TextView tv_info = (TextView) vi.findViewById( R.id.tv_info);
		tv_interval.setText(data_list.getOpenAlarmSummary_list().get(position).getDIFF_MINUTES());
		tv_sitedown.setText(data_list.getOpenAlarmSummary_list().get(position).getSIT_DOWN_CNT());
		tv_critical.setText(data_list.getOpenAlarmSummary_list().get(position).getCRITICAL());
		tv_major.setText(data_list.getOpenAlarmSummary_list().get(position).getMAJOR());
		tv_minor.setText(data_list.getOpenAlarmSummary_list().get(position).getMINOR());
		tv_info.setText(data_list.getOpenAlarmSummary_list().get(position).getINFORMATION());
		if (a == 1) {
			tv_sitedown.setVisibility(View.VISIBLE);
		} else {
			tv_sitedown.setVisibility(View.GONE);
		}
		if (b == 1) {
			tv_critical.setVisibility(View.VISIBLE);
		} else {
			tv_critical.setVisibility(View.GONE);
		}
		if (c == 1) {
			tv_major.setVisibility(View.VISIBLE);
		} else {
			tv_major.setVisibility(View.GONE);
		}
		if (d == 1) {
			tv_minor.setVisibility(View.VISIBLE);
		} else {
			tv_minor.setVisibility(View.GONE);
		}
		if (e == 1) {
			tv_info.setVisibility(View.VISIBLE);
		} else {
			tv_info.setVisibility(View.GONE);
		}

		//tv_interval.setBackgroundColor(Color.parseColor("#830300"));
		/*if (position == data_list.getOpenAlarmSummary_list().size() - 5) {
			tv_interval.setBackgroundColor(Color.parseColor("#a0d080"));
		}
		if (position == data_list.getOpenAlarmSummary_list().size() - 4) {
			tv_interval.setBackgroundColor(Color.parseColor("#80b060"));
		}
		if (position == data_list.getOpenAlarmSummary_list().size() - 3) {
			tv_interval.setBackgroundColor(Color.parseColor("#70a050"));
		}
		if (position == data_list.getOpenAlarmSummary_list().size() - 2) {
			tv_interval.setBackgroundColor(Color.parseColor("#609040"));
		}*/
		/*if (position == data_list.getOpenAlarmSummary_list().size() - 1) {
			tv_interval.setBackgroundColor(Color.parseColor("#830300"));
			tv_interval.setTypeface(null, Typeface.BOLD);
			tv_sitedown.setTypeface(null, Typeface.BOLD);
			tv_critical.setTypeface(null, Typeface.BOLD);
			tv_major.setTypeface(null, Typeface.BOLD);
			tv_minor.setTypeface(null, Typeface.BOLD);
			tv_info.setTypeface(null, Typeface.BOLD);
		} else {
			tv_interval.setTypeface(null, Typeface.NORMAL);
			tv_sitedown.setTypeface(null, Typeface.NORMAL);
			tv_critical.setTypeface(null, Typeface.NORMAL);
			tv_major.setTypeface(null, Typeface.NORMAL);
			tv_minor.setTypeface(null, Typeface.NORMAL);
			tv_info.setTypeface(null, Typeface.NORMAL);
		}*/
		return vi;
	}
}