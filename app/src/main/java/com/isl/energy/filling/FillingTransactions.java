package com.isl.energy.filling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.modal.BeanLastFillingTransList;
import com.isl.util.Utils;

import infozech.itower.R;

public class FillingTransactions extends Activity {
	Button bt_back;
	ListView filling_list;
	BeanLastFillingTransList fillingReportList;
	TextView txt_no_data;
	String flag;
	RelativeLayout rl_no_list;
	String res;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.filling_transaction);
		TextView tv_brand_logo=(TextView)findViewById( R.id.tv_brand_logo);
		Utils.msgText( FillingTransactions.this, "218",tv_brand_logo); //set Text Filling Transaction Report
		rl_no_list=(RelativeLayout)findViewById( R.id.rl_no_list);
		bt_back =(Button)findViewById( R.id.bt_back);
		Utils.msgButton( FillingTransactions.this,"71",bt_back);
		filling_list=(ListView)findViewById( R.id.filling_list);
		txt_no_data=(TextView)findViewById( R.id.txt_no_data);
		res = getIntent().getExtras().getString("res");
		flag = getIntent().getExtras().getString("flag");
		Gson gson = new Gson();
		fillingReportList = gson.fromJson(res, BeanLastFillingTransList.class);
		//fillingReportList.getFillingReportList().remove(fillingReportList.getFillingReportList().size()-1);
		
		filling_list.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {
			Intent i=new Intent( FillingTransactions.this, FillingTransactionDetails.class);
			i.putExtra("res", res);
			i.putExtra("flag", flag);
			i.putExtra("pos", pos);
			startActivity(i);				
		 }
		});
		
		bt_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		finish();	
		}
		});

		if (fillingReportList.getFillingReportList()!=null && fillingReportList.getFillingReportList().size() > 0) {
			filling_list.setVisibility(View.VISIBLE);
			rl_no_list.setVisibility(View.GONE);
			filling_list.setAdapter(new FillingTransAdapter( FillingTransactions.this,fillingReportList));
		}else {
			filling_list.setVisibility(View.GONE);
			rl_no_list.setVisibility(View.VISIBLE);
			Utils.msgText( FillingTransactions.this, "267", txt_no_data);
		}
	}
}