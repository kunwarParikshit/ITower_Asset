package com.isl.energy.filling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.modal.BeanLastFillingTransList;
import com.isl.util.Utils;

import infozech.itower.R;

@SuppressLint("InflateParams")
public class FillingTransAdapter extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanLastFillingTransList fillingReportList;
	AppPreferences mAppPreferences;
	String sName;
	public FillingTransAdapter(Context con, BeanLastFillingTransList data) {
		this.con = con;
		this.fillingReportList = data;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppPreferences=new AppPreferences(con);
	}

	@Override
	public int getCount() {
		return fillingReportList.getFillingReportList().size();
		// return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup parent) {
		View vi = arg1;
		if (arg1 == null)
			vi = inflater.inflate( R.layout.filling_adapter, null);
		if(mAppPreferences.getSiteNameEnable()==1 && fillingReportList.getFillingReportList().get(position).getsName()!=null){
			sName="("+fillingReportList.getFillingReportList().get(position).getsName()+")";
		}else{
			sName="";
		}

		TextView tv_site_id = (TextView) vi.findViewById( R.id.tv_site_id);
		tv_site_id.setPaintFlags(tv_site_id.getPaintFlags()	| Paint.UNDERLINE_TEXT_FLAG);
		tv_site_id.setTypeface( Utils.typeFace(con));
		tv_site_id.setText( Utils.msg(con, "77")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getSITE_ID()+sName);

		TextView tv_Genset = (TextView) vi.findViewById( R.id.tv_dg_type);
		tv_Genset.setTypeface( Utils.typeFace(con));
		tv_Genset.setText( Utils.msg(con, "168")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDG_TYPE());

		TextView tv_fill_date = (TextView) vi.findViewById( R.id.tv_fill_date);
		tv_fill_date.setTypeface( Utils.typeFace(con));
		tv_fill_date.setText( Utils.msg(con, "214")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getTRAN_DATE());

		TextView tv_filled_qty = (TextView) vi.findViewById( R.id.tv_filled_qty);
		tv_filled_qty.setTypeface( Utils.typeFace(con));
		tv_filled_qty.setText( Utils.msg(con, "172")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
						.getDIESEL_QTY());

		TextView tv_tran_id = (TextView) vi.findViewById( R.id.tv_tran_id);
		tv_tran_id.setTypeface( Utils.typeFace(con));
		tv_tran_id.setText( Utils.msg(con, "746")
				+ " : "
				+ fillingReportList.getFillingReportList().get(position)
				.getTranId());
		return vi;
	}
}
