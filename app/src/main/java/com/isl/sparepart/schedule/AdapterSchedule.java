package com.isl.sparepart.schedule;


import infozech.itower.R;


import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSiteList;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
import com.isl.workflow.WorkflowImpl;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterSchedule extends BaseAdapter {
	Context con;
	private LayoutInflater inflater = null;
	BeanSiteList data_list;
	AppPreferences mAppPreferences;
	String sName,flag;
	DataBaseHelper db;
	public AdapterSchedule(Context con, BeanSiteList list,String flag) {
		this.con = con;
		this.data_list = list;
		this.flag = flag;
		inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppPreferences=new AppPreferences(con);
		db = new DataBaseHelper(con);

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

		if(mAppPreferences.getSiteNameEnable()==1 && data_list.getSite_list().get(position).getSidName()!=null){
			sName="("+data_list.getSite_list().get(position).getSidName()+")";
		}else{
			sName="";
		}

		TextView site_id = (TextView) vi.findViewById(R.id.txt_site_id);
		site_id.setTypeface(Utils.typeFace(con));
		site_id.setText(data_list.getSite_list().get(position).getSITE_ID()+sName);


		TextView txt_checklist_type = (TextView) vi.findViewById(R.id.txt_checklist_type);
		txt_checklist_type.setTypeface(Utils.typeFace(con));
		txt_checklist_type.setText(data_list.getSite_list().get(position).getPARAM_NAME());
		//Utils.msgText(con,data_list.getSite_list().get(position).getPARAM_ID(),txt_checklist_type);
		if (data_list.getSite_list().get(position).getDgDesc() == null || data_list.getSite_list().get(position).getDgDesc().isEmpty()) {

		}else{
			txt_checklist_type.setText(txt_checklist_type.getText().toString()+" ("+Utils.msg(con, "168") + " : "	+data_list.getSite_list().get(position).getDgDesc()+")");
		}

		TextView txt_status = (TextView) vi.findViewById(R.id.txt_status);
		txt_status.setTypeface(Utils.typeFace(con));
		if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Scheduled")){
			Utils.msgText(con, "219", txt_status);
			RelativeLayout rl = vi.findViewById(R.id.relativelayout);
			rl.setBackground(con.getResources().getDrawable(R.drawable.list_bg));
			if(data_list.getSite_list().get(position).getPmFlag()!= null && data_list.getSite_list().get(position).getPmNote() != null)
			{
				TextView txt_pmNote = vi.findViewById(R.id.txt_pmNote);
				txt_pmNote.setVisibility(View.VISIBLE);
				txt_pmNote.setText(data_list.getSite_list().get(position).getPmNote());
				if(data_list.getSite_list().get(position).getPmFlag().equalsIgnoreCase("Yes")){
					rl.setBackgroundColor(con.getResources().getColor(R.color.light_orange));
				}
				else
				{
					rl.setBackground(con.getResources().getDrawable(R.drawable.list_bg));
				}


			}

		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Missed")	){
			Utils.msgText(con, "222", txt_status);
		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Done")){
			Utils.msgText(con, "223", txt_status);
		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Review")	){
			Utils.msgText(con, "221", txt_status);
		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Pending")){
			Utils.msgText(con, "220", txt_status);
		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Verify")){
			Utils.msgText(con, "221", txt_status);
		}else if(data_list.getSite_list().get(position).getACTIVITY_STATUS().equalsIgnoreCase("Rejected")){
			Utils.msgText(con, "466", txt_status);
		}else{
			txt_status.setText(data_list.getSite_list().get(position).getACTIVITY_STATUS());
		}

		TextView txt_date = (TextView) vi.findViewById(R.id.txt_date);
		txt_date.setTypeface(Utils.typeFace(con));

		TextView txt_rp_date=(TextView)vi.findViewById(R.id.txt_rp_date);
		txt_rp_date.setTypeface(Utils.typeFace(con));

		TextView txt_generator_status=(TextView)vi.findViewById(R.id.txt_generator_status);
		txt_generator_status.setTypeface(Utils.typeFace(con));

		TextView txt_txn_id=(TextView)vi.findViewById(R.id.txt_txn_id);
		txt_txn_id.setTypeface(Utils.typeFace(con));
		txt_txn_id.setVisibility(View.VISIBLE);
		txt_txn_id.setText(data_list.getSite_list().get(position).getPmTktId());

		TextView txt_link=(TextView)vi.findViewById(R.id.txt_link);
		txt_link.setTypeface(Utils.typeFace(con));
		txt_link.setPaintFlags(txt_link.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);


		if(data_list.getSite_list().get( position).getGenStatus()!=null &&
				data_list.getSite_list().get( position).getGenStatus().length()>0){
			txt_generator_status.setVisibility(View.VISIBLE);
			db.open();
			ArrayList<String> list = db.getPreventiveParamName("1195","654",
					data_list.getSite_list().get( position).getGenStatus());
			db.close();
			if(list.size()>0){
				txt_generator_status.setText(""+list.get(0));
			}
		}else{
			txt_generator_status.setVisibility(View.GONE);
		}

		txt_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int mode = 0;
				Intent i = null;
				//Toast.makeText( con,"hhh",Toast.LENGTH_LONG ).show();
				for(int index = 0; index<AppConstants.moduleList.size(); index++){
					int moduleId = AppConstants.moduleList.get(index).getModuleId();

					if(moduleId==1001){
						mode = 1;
						i = new Intent(con, WorkflowImpl.class);
						mAppPreferences.setSite("");
						mAppPreferences.setPEDT("");
						mAppPreferences.setPSDT("");
						mAppPreferences.setTTModuleSelection(""+moduleId);
						mAppPreferences.setModuleName(AppConstants.moduleList.get(index).getModuleName());
						mAppPreferences.setModuleIndex(index);
						mAppPreferences.setPMBackTask(0);
						mAppPreferences.SetBackModeNotifi45(1);
						mAppPreferences.SetBackModeNotifi6(1);
						i.putExtra("moduleIndex",index);
						i.putExtra("moduleId",moduleId);
						i.putExtra("moduleName",AppConstants.moduleList.get(index).getModuleName());
						break;
					 }
					}
				if(mode == 1){
					i.putExtra("sid",data_list.getSite_list().get(position).getSITE_ID());
					i.putExtra("acttype",data_list.getSite_list().get(position).getPARAM_ID());
					i.putExtra("tranId",data_list.getSite_list().get(position).getPmTktId());
					i.putExtra("esid",data_list.getSite_list().get(position).getEtsSid());
					if(flag.equalsIgnoreCase("P")){
						i.putExtra("ppr",Utils.msg(con,"738"));
					}else{
						i.putExtra("ppr",Utils.msg(con,"737"));
					}

					i.putExtra("link","pmlink");
					con.startActivity(i);
				}
				}
		});

		if(mAppPreferences.getHyperLinkPM().equalsIgnoreCase("1") &&
				(flag.equalsIgnoreCase("M") || flag.equalsIgnoreCase("S") ||
				 flag.equalsIgnoreCase("P"))){
			txt_link.setVisibility(View.VISIBLE);
		}else{
			txt_link.setVisibility(View.GONE);
		}


		if(flag.equalsIgnoreCase("D")){
			txt_date.setText(data_list.getSite_list().get(position).getdDate());
			txt_rp_date.setVisibility(View.GONE);
		}else if(flag.equalsIgnoreCase("P")) {
			if(data_list.getSite_list().get(position).getFuncheck()!=null && data_list.getSite_list().get(position).getCreplandate()!=null
					&& data_list.getSite_list().get(position).getFuncheck().equalsIgnoreCase("1"))
			{
				txt_rp_date.setVisibility(View.VISIBLE);
				txt_date.setText(Utils.msg(con,"514")+" "+data_list.getSite_list().get(position).getdDate());
				txt_rp_date.setText(Utils.msg(con,"513")+" "+data_list.getSite_list().get(position).getCreplandate());
			}else {
				txt_date.setText(data_list.getSite_list().get(position).getdDate());
				txt_rp_date.setVisibility(View.GONE);
			}
		}else if(flag.equalsIgnoreCase("V") || flag.equalsIgnoreCase("R")){
			txt_date.setText(data_list.getSite_list().get(position).getRvDate());
			txt_rp_date.setVisibility(View.GONE);
		}else {
			txt_date.setText( data_list.getSite_list().get( position ).getSCHEDULE_DATE() );
			txt_rp_date.setVisibility(View.GONE);

		}
		return vi;
	}
}
