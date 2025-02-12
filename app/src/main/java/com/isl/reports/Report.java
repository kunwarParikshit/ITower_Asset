package com.isl.reports;
import infozech.itower.R;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity {
	TextView tv_site_audit_status, tv_missing_asset,tv_audit_summary;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.report);
	tv_site_audit_status = (TextView) findViewById(R.id.tv_site_audit_status);
	tv_missing_asset = (TextView) findViewById(R.id.tv_missing_asset);
	tv_audit_summary = (TextView) findViewById(R.id.tv_audit_summary);
	Button iv_back = (Button) findViewById(R.id.iv_back);
	getMenuRights();
	iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent i=new Intent(Report.this,HomeActivity.class);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		}
	});
	tv_site_audit_status.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent i = new Intent(Report.this, SiteAuditStatus.class);
			startActivity(i);
		}
	});

	tv_missing_asset.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
				Intent i = new Intent(Report.this, MissingAsset.class);
				startActivity(i);
	 }		
	});

	tv_audit_summary.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent i = new Intent(Report.this, SiteAuditSummary.class);
			startActivity(i);
		}
	});
	}
	public void getMenuRights() {
		String site_audit_status ,missing_asset,site_audit_summary ; 
		DataBaseHelper dbHelper = new DataBaseHelper(Report.this);
		dbHelper.open();
		site_audit_status = dbHelper.getSubMenuRight("SiteAuditStatus","Reports");
		missing_asset = dbHelper.getSubMenuRight("MissingAsset","Reports");
		site_audit_summary = dbHelper.getSubMenuRight("SiteAuditSummary","Reports");
		dbHelper.close();
		if(!site_audit_status.equalsIgnoreCase("V") && !missing_asset.equalsIgnoreCase("V") && !site_audit_summary.equalsIgnoreCase("V") ){
			Toast.makeText(Report.this, "You are not authorized for menus.",Toast.LENGTH_SHORT).show();	
			Intent j=new Intent(Report.this,HomeActivity.class);
		    j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(j);	
			finish();
		}else{
			if(site_audit_status.equalsIgnoreCase("V")){
				tv_site_audit_status.setVisibility(View.VISIBLE);
			}else{
				tv_site_audit_status.setVisibility(View.GONE);
			}
			
            if(missing_asset.equalsIgnoreCase("V")){
            	tv_missing_asset.setVisibility(View.VISIBLE);
			}else{
				tv_missing_asset.setVisibility(View.GONE);
			}
            
            if(site_audit_summary.equalsIgnoreCase("V")){
            	tv_audit_summary.setVisibility(View.VISIBLE);
    		}else{
    			tv_audit_summary.setVisibility(View.GONE);
    		}
		}
		
	 }
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(Report.this,HomeActivity.class);
		startActivity(i);
		finish();	
	}
}
