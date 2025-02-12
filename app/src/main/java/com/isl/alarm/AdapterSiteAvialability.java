package com.isl.alarm;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.modal.BeanDashboardList;

import infozech.itower.R;

public class AdapterSiteAvialability extends BaseAdapter {
	Context con;
	int a, b, c;
	private LayoutInflater inflater = null;
	BeanDashboardList datalist;
	public AdapterSiteAvialability(Context con, BeanDashboardList datalist, int a, int b, int c) {
	this.con = con;
	this.datalist = datalist;
	this.a = a;
	this.b = b;
	this.c = c;
	inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
	return datalist.getSiteAvailabilitySummary_list().size();

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
		vi = inflater.inflate( R.layout.adapter_site_avialability, null);
		TextView tv_site_class = (TextView) vi.findViewById( R.id.tv_site_class);
		TextView tv_availability = (TextView) vi.findViewById( R.id.tv_availability);
		TextView Mttr = (TextView) vi.findViewById( R.id.tv_mttr);
		TextView Site_down = (TextView) vi.findViewById( R.id.tv_sitedown);
		tv_site_class.setText(datalist.getSiteAvailabilitySummary_list().get(position).getSITE_CLASS());
		tv_availability.setText(datalist.getSiteAvailabilitySummary_list().get(position).getAVAILABLE());
		Mttr.setText(datalist.getSiteAvailabilitySummary_list().get(position).getMTTR());
		Site_down.setText(datalist.getSiteAvailabilitySummary_list().get(position).getSITE_DOWN());
		if (a == 1) {
		tv_availability.setVisibility(View.VISIBLE);
		}else {
		tv_availability.setVisibility(View.GONE);
		}
		if (b == 1) {
		Mttr.setVisibility(View.VISIBLE);
		}else {
		Mttr.setVisibility(View.GONE);
		}
		if (c == 1) {
		Site_down.setVisibility(View.VISIBLE);
		}else {
		Site_down.setVisibility(View.GONE);
		}
		if (position == datalist.getSiteAvailabilitySummary_list().size() - 1) {
			tv_site_class.setTypeface(null, Typeface.BOLD);
			tv_availability.setTypeface(null, Typeface.BOLD);
			Mttr.setTypeface(null, Typeface.BOLD);
			Site_down.setTypeface(null, Typeface.BOLD);
		} else {
			tv_site_class.setTypeface(null, Typeface.NORMAL);
			tv_availability.setTypeface(null, Typeface.NORMAL);
			Mttr.setTypeface(null, Typeface.NORMAL);
			Site_down.setTypeface(null, Typeface.NORMAL);
		}
		return vi;
	}
}
