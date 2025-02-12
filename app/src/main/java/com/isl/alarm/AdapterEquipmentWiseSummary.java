package com.isl.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.modal.BeanDashboardList;

import infozech.itower.R;

public class AdapterEquipmentWiseSummary extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanDashboardList datalist;
	int a, b, c, d;
	public AdapterEquipmentWiseSummary(Context con, BeanDashboardList datalist, int a, int b, int c, int d) {
		this.con = con;
		this.datalist = datalist;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return datalist.getEquipmentwiseSummary_list().size();
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
		vi = inflater.inflate( R.layout.adapter_equipmentwise_summary, null);
		TextView tv_equipment = (TextView) vi.findViewById( R.id.tv_equipment);
		TextView tv_critical = (TextView) vi.findViewById( R.id.tv_critical);
		TextView tv_major = (TextView) vi.findViewById( R.id.tv_major);
		TextView tv_minor = (TextView) vi.findViewById( R.id.tv_minor);
		TextView tv_info = (TextView) vi.findViewById( R.id.tv_info);
		tv_equipment.setText(datalist.getEquipmentwiseSummary_list().get(position).getEQUIPMENT_NAME());
		tv_critical.setText(datalist.getEquipmentwiseSummary_list().get(position).getCRITICAL());
		tv_major.setText(datalist.getEquipmentwiseSummary_list().get(position).getMAJOR());
		tv_minor.setText(datalist.getEquipmentwiseSummary_list().get(position).getMINOR());
		tv_info.setText(datalist.getEquipmentwiseSummary_list().get(position).getINFORMATION());
		if (a == 1) {
			tv_critical.setVisibility(View.VISIBLE);
		} else {
			tv_critical.setVisibility(View.GONE);
		}
		if (b == 1) {
			tv_major.setVisibility(View.VISIBLE);
		} else {
			tv_major.setVisibility(View.GONE);
		}
		if (c == 1) {
			tv_minor.setVisibility(View.VISIBLE);
		} else {
			tv_minor.setVisibility(View.GONE);
		}
		if (d == 1) {
			tv_info.setVisibility(View.VISIBLE);
		} else {
			tv_info.setVisibility(View.GONE);
		}
		return vi;
	}

}
