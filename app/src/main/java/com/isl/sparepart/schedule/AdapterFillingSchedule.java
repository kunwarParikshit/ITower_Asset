package com.isl.sparepart.schedule;

import infozech.itower.R;
import com.isl.modal.BeanFillingSiteList;
import com.isl.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterFillingSchedule extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanFillingSiteList data_list;

	public AdapterFillingSchedule(Context con, BeanFillingSiteList list) {
		this.con = con;
		this.data_list = list;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data_list.getSite_list().size();
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
		vi = inflater.inflate(R.layout.list_item_schedule, null);

		TextView txt_rp_date = (TextView) vi.findViewById(R.id.txt_rp_date);
		txt_rp_date.setVisibility(View.VISIBLE);
		txt_rp_date.setTypeface(Utils.typeFace(con));
		if (data_list.getSite_list().get(position).getPqty()!=null){
			txt_rp_date.setText("Fuel Quantity to be filled : "+data_list.getSite_list().get(position).getPqty());
		}else {
			txt_rp_date.setText("Fuel Quantity to be filled : ");
		}

		TextView txt_dgtype = (TextView) vi.findViewById(R.id.txt_dgtype);
		txt_dgtype.setVisibility(View.VISIBLE);
		txt_dgtype.setTypeface(Utils.typeFace(con));
		txt_dgtype.setText("DG Type : "+data_list.getSite_list().get(position).getDgname());


		TextView site_id = (TextView) vi.findViewById(R.id.txt_site_id);
		site_id.setTypeface( Utils.typeFace(con));
		site_id.setText(data_list.getSite_list().get(position).getSITE_ID());

		TextView txt_checklist_type = (TextView) vi.findViewById(R.id.txt_checklist_type);
		txt_checklist_type.setTypeface(Utils.typeFace(con));
		txt_checklist_type.setText("Filling");
		txt_checklist_type.setVisibility(View.GONE);

		TextView txt_status = (TextView) vi.findViewById(R.id.txt_status);
		txt_status.setTypeface(Utils.typeFace(con));
		txt_status.setText("Status : "+data_list.getSite_list().get(position).getACTIVITY_STATUS());

		TextView txt_date = (TextView) vi.findViewById(R.id.txt_date);
		txt_date.setTypeface(Utils.typeFace(con));
		txt_date.setText("Planned Date : "+data_list.getSite_list().get(position).getSCHEDULE_DATE());


		TextView txt_ttikd = (TextView) vi.findViewById(R.id.txt_ttikd);
		txt_ttikd.setVisibility(View.VISIBLE);
		txt_ttikd.setTypeface(Utils.typeFace(con));
		if (data_list.getSite_list().get(position).getTranid()!=null){
			txt_ttikd.setText("FWO ID : "+data_list.getSite_list().get(position).getTranid());
		}else {
			txt_ttikd.setText("FWO ID : ");
		}

		return vi;
	}

}
