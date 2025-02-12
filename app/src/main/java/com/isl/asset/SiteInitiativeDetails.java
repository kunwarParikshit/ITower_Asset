package com.isl.asset;
import com.isl.constant.WebMethods;
import com.isl.itower.GPSTracker;
import infozech.itower.R;
import com.isl.modal.BeanAssetModuleList;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SiteInitiativeDetails extends Activity implements OnClickListener {
	GPSTracker gps;
	AppPreferences mAppPreferences;
	BeanAssetModuleList data_list;
	BeanAssetModuleList data_list1;
	TextView tv_brand_logo,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12,tv13;
	RadioButton rb_y1,rb_n1,rb_missing1,rb_y2,rb_n2,rb_missing2,rb_y3,rb_n3,rb_missing3,rb_y4,rb_n4,rb_missing4
	,rb_y5,rb_n5,rb_missing5,rb_y6,rb_n6,rb_missing6,rb_y7,rb_n7,rb_missing7,rb_y8,rb_n8,rb_missing8,rb_y9,rb_n9,rb_missing9
	,rb_y10,rb_n10,rb_missing10,rb_y11,rb_n11,rb_missing11,rb_y12,rb_n12,rb_missing12,rb_y13,rb_n13,rb_missing13;
	String filePath = "",imageString,latitude = "", longitude = "",s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13;
	Button submit,take_photo;
	EditText remarks;
	ImageView iv_photo;
	File destination;
	LinearLayout ll1,ll2,ll3,ll4,ll5,ll6,ll7,ll8,ll9,ll10,ll11,ll12,ll13;
	String z="x";
	RelativeLayout Rl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		setContentView(R.layout.passive_asset);
		mAppPreferences = new AppPreferences(SiteInitiativeDetails.this);
		String AuditRight = mAppPreferences.getSiteAuditRights();
		String[] PassiveAssets=AuditRight.split("~");
		Rl=(RelativeLayout)findViewById(R.id.Rl);
		ll1=(LinearLayout)findViewById(R.id.ll1);
		ll2=(LinearLayout)findViewById(R.id.ll2);
		ll3=(LinearLayout)findViewById(R.id.ll3);
		ll4=(LinearLayout)findViewById(R.id.ll4);
		ll5=(LinearLayout)findViewById(R.id.ll5);
		ll6=(LinearLayout)findViewById(R.id.ll6);
		ll7=(LinearLayout)findViewById(R.id.ll7);
		ll8=(LinearLayout)findViewById(R.id.ll8);
		ll9=(LinearLayout)findViewById(R.id.ll9);
		ll10=(LinearLayout)findViewById(R.id.ll10);
		ll11=(LinearLayout)findViewById(R.id.ll11);
		ll12=(LinearLayout)findViewById(R.id.ll12);
		ll13=(LinearLayout)findViewById(R.id.ll13);
		take_photo = (Button) findViewById(R.id.btn_take_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		take_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageCapture();
			}
		});
		gps = new GPSTracker(SiteInitiativeDetails.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
		}
		
		tv_brand_logo=(TextView)findViewById(R.id.tv_brand_logo);
		remarks=(EditText)findViewById(R.id.et_remarks);
		submit=(Button)findViewById(R.id.button_submit);

		if(PassiveAssets.length==1){
			if(mAppPreferences.getSiteAuditRights().equalsIgnoreCase("V")){
				Rl.setVisibility(View.GONE);
			}
		}else{
			
				if(PassiveAssets[0].equalsIgnoreCase("M")){
					if(PassiveAssets[1].equalsIgnoreCase("V")){
						Rl.setVisibility(View.VISIBLE);
					}
			     	
			   }
			}
	  	tv1=(TextView)findViewById(R.id.tv_dg1);
	  	tv1.setPaintFlags(tv1.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv1.setOnClickListener(this);
		rb_y1=(RadioButton)findViewById(R.id.rb_y1);
		rb_n1=(RadioButton)findViewById(R.id.rb_n1);
		rb_missing1=(RadioButton)findViewById(R.id.rb_missing1);
		
		tv2=(TextView)findViewById(R.id.tv_dg2);
		tv2.setPaintFlags(tv2.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv2.setOnClickListener(this);
		rb_y2=(RadioButton)findViewById(R.id.rb_y2);
		rb_n2=(RadioButton)findViewById(R.id.rb_n2);
		rb_missing2=(RadioButton)findViewById(R.id.rb_missing2);
		
		tv3=(TextView)findViewById(R.id.tv_dg3);
		tv3.setPaintFlags(tv3.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv3.setOnClickListener(this);
		rb_y3=(RadioButton)findViewById(R.id.rb_y3);
		rb_n3=(RadioButton)findViewById(R.id.rb_n3);
		rb_missing3=(RadioButton)findViewById(R.id.rb_missing3);
		
		tv4=(TextView)findViewById(R.id.tv_dg4);
		tv4.setPaintFlags(tv4.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv4.setOnClickListener(this);
		rb_y4=(RadioButton)findViewById(R.id.rb_y4);
		rb_n4=(RadioButton)findViewById(R.id.rb_n4);
		rb_missing4=(RadioButton)findViewById(R.id.rb_missing4);
		
		tv5=(TextView)findViewById(R.id.tv_dg5);
		tv5.setPaintFlags(tv5.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv5.setOnClickListener(this);
		rb_y5=(RadioButton)findViewById(R.id.rb_y5);
		rb_n5=(RadioButton)findViewById(R.id.rb_n5);
		rb_missing5=(RadioButton)findViewById(R.id.rb_missing5);
		
		tv6=(TextView)findViewById(R.id.tv_dg6);
		tv6.setPaintFlags(tv6.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv6.setOnClickListener(this);
		rb_y6=(RadioButton)findViewById(R.id.rb_y6);
		rb_n6=(RadioButton)findViewById(R.id.rb_n6);
		rb_missing6=(RadioButton)findViewById(R.id.rb_missing6);
		
		tv7=(TextView)findViewById(R.id.tv_dg7);
		tv7.setPaintFlags(tv7.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv7.setOnClickListener(this);
		rb_y7=(RadioButton)findViewById(R.id.rb_y7);
		rb_n7=(RadioButton)findViewById(R.id.rb_n7);
		rb_missing7=(RadioButton)findViewById(R.id.rb_missing7);
		
		tv8=(TextView)findViewById(R.id.tv_dg8);
		tv8.setPaintFlags(tv8.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv8.setOnClickListener(this);
		rb_y8=(RadioButton)findViewById(R.id.rb_y8);
		rb_n8=(RadioButton)findViewById(R.id.rb_n8);
		rb_missing8=(RadioButton)findViewById(R.id.rb_missing8);
		
		
		tv9=(TextView)findViewById(R.id.tv_dg9);
		tv9.setPaintFlags(tv9.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv9.setOnClickListener(this);
		rb_y9=(RadioButton)findViewById(R.id.rb_y9);
		rb_n9=(RadioButton)findViewById(R.id.rb_n9);
		rb_missing9=(RadioButton)findViewById(R.id.rb_missing9);
		
		tv10=(TextView)findViewById(R.id.tv_dg10);
		tv10.setPaintFlags(tv10.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv10.setOnClickListener(this);
		rb_y10=(RadioButton)findViewById(R.id.rb_y10);
		rb_n10=(RadioButton)findViewById(R.id.rb_n10);
		rb_missing10=(RadioButton)findViewById(R.id.rb_missing10);
		
		tv11=(TextView)findViewById(R.id.tv_dg11);
		tv11.setPaintFlags(tv11.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv11.setOnClickListener(this);
		rb_y11=(RadioButton)findViewById(R.id.rb_y11);
		rb_n11=(RadioButton)findViewById(R.id.rb_n11);
		rb_missing11=(RadioButton)findViewById(R.id.rb_missing11);
		
		tv12=(TextView)findViewById(R.id.tv_dg12);
		tv12.setPaintFlags(tv12.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv12.setOnClickListener(this);
		rb_y12=(RadioButton)findViewById(R.id.rb_y12);
		rb_n12=(RadioButton)findViewById(R.id.rb_n12);
		rb_missing12=(RadioButton)findViewById(R.id.rb_missing12);
		
		tv13=(TextView)findViewById(R.id.tv_dg13);
		tv13.setPaintFlags(tv13.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv13.setOnClickListener(this);
		rb_y13=(RadioButton)findViewById(R.id.rb_y13);
		rb_n13=(RadioButton)findViewById(R.id.rb_n13);
		rb_missing13=(RadioButton)findViewById(R.id.rb_missing13);
		tv_brand_logo.setText(""+mAppPreferences.getSiteID());
		Button iv_back = (Button) findViewById(R.id.button_back);
		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		    if(z.equalsIgnoreCase("y")){
				z="x";
			}else{
			Intent i=new Intent(SiteInitiativeDetails.this, AssetsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
			}
		}
	    });
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					if (Utils.isNetworkAvailable(SiteInitiativeDetails.this)) {
					imageString = Utils.convertImageToBase64(filePath); 
					check();
					new AssignedTicketsTask1(SiteInitiativeDetails.this).execute();
				} else {
					Toast.makeText(SiteInitiativeDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
				}	
		 }
		});
		
		
		rb_y1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			if(data_list.getSite_Initiative_list().size()>0){	
			z="y";	
			data_list.getSite_Initiative_list().get(0).setSTATUS("1");
			s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~Y";	
				 }
		 }
		});
		rb_n1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(data_list.getSite_Initiative_list().size()>0){
					 z="y";
				 data_list.getSite_Initiative_list().get(0).setSTATUS("0");
				 s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~N";	
				 }	
		 }
		});
		rb_missing1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(data_list.getSite_Initiative_list().size()>0){
					 z="y";
				data_list.getSite_Initiative_list().get(0).setSTATUS("2");
				s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~M";	
				 }
		 }
		});
		
		
		rb_y2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(data_list.getSite_Initiative_list().size()>1){
					 z="y";
			data_list.getSite_Initiative_list().get(1).setSTATUS("1");
			s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~Y";	
				 }
		 }
		});
		rb_n2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>1){
					z="y";
				data_list.getSite_Initiative_list().get(1).setSTATUS("0");
				s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~N";	
				}	
		 }
		});
		rb_missing2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>1){
					z="y";
				data_list.getSite_Initiative_list().get(1).setSTATUS("2");	
				s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~M";	
				}	
		 }
		});
		
		rb_y3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>2){
					z="y";
			data_list.getSite_Initiative_list().get(2).setSTATUS("1");		
			s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>2){
					z="y";
					data_list.getSite_Initiative_list().get(2).setSTATUS("0");
				s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~N";	
				}
		 }
		});
		rb_missing3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>2){
					z="y";
					data_list.getSite_Initiative_list().get(2).setSTATUS("2");
				s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		
		
		rb_y4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>3){
					z="y";
			data_list.getSite_Initiative_list().get(3).setSTATUS("1");		
			s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>3){
					z="y";
					data_list.getSite_Initiative_list().get(3).setSTATUS("0");
				s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>3){
					z="y";
					data_list.getSite_Initiative_list().get(3).setSTATUS("2");
				s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		
		
		rb_y5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>4){
					z="y";
			data_list.getSite_Initiative_list().get(4).setSTATUS("1");		
			s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~Y";
				}
		 }
		});
		rb_n5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>4){
					z="y";
					data_list.getSite_Initiative_list().get(4).setSTATUS("0");		
				s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~N";	
				}
		 }
		});
		rb_missing5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>4){
					z="y";
				data_list.getSite_Initiative_list().get(4).setSTATUS("2");		
				s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~M";	
				}
		 }
		});
		
		
		rb_y6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>5){
					z="y";
			data_list.getSite_Initiative_list().get(5).setSTATUS("1");		
			s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>5){
					z="y";
					data_list.getSite_Initiative_list().get(5).setSTATUS("0");	
				s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>5){
					z="y";
					data_list.getSite_Initiative_list().get(5).setSTATUS("2");	
				s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		
		
		rb_y7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>6){
					z="y";
			data_list.getSite_Initiative_list().get(6).setSTATUS("1");		
			s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>6){
					z="y";
				data_list.getSite_Initiative_list().get(6).setSTATUS("0");
				s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~N";	
				}
		 }
		});
		rb_missing7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>6){
					z="y";
					data_list.getSite_Initiative_list().get(6).setSTATUS("2");
				s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~M";	
				}
		 }
		});
		
		
		rb_y8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>7){
					z="y";
			data_list.getSite_Initiative_list().get(7).setSTATUS("1");		
			s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>7){
					z="y";
					data_list.getSite_Initiative_list().get(7).setSTATUS("0");	
				s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>7){
					z="y";
					data_list.getSite_Initiative_list().get(7).setSTATUS("2");	
				s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~M";	
				}
		 }
		});
		
		
		rb_y9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>8){
					z="y";
			data_list.getSite_Initiative_list().get(8).setSTATUS("1");		
			s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~Y";
				}
		 }
		});
		rb_n9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>8){
					z="y";
					data_list.getSite_Initiative_list().get(8).setSTATUS("0");		
				s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>8){
					z="y";
				data_list.getSite_Initiative_list().get(8).setSTATUS("2");		
				s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
	
		rb_y10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>9){
					z="y";
			data_list.getSite_Initiative_list().get(9).setSTATUS("1");
			s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>9){
					z="y";
					data_list.getSite_Initiative_list().get(9).setSTATUS("0");
				s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~N";	
				}
		 }
		});
		rb_missing10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>9){
					z="y";
					data_list.getSite_Initiative_list().get(9).setSTATUS("2");
				s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~M";	
				}
		 }
		});
		
		
		rb_y11.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>10){
					z="y";
			data_list.getSite_Initiative_list().get(10).setSTATUS("1");		
			s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~Y";
				}
		 }
		});
		rb_n11.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>10){
					z="y";
					data_list.getSite_Initiative_list().get(10).setSTATUS("0");	
				s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing11.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>10){
					z="y";
					data_list.getSite_Initiative_list().get(10).setSTATUS("2");	
				s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		rb_y12.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>11){
					z="y";
			data_list.getSite_Initiative_list().get(11).setSTATUS("1");		
			s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~Y";	
				}
		 }
		});
		rb_n12.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>11){
					z="y";
					data_list.getSite_Initiative_list().get(10).setSTATUS("0");
				s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing12.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>11){
					z="y";
					data_list.getSite_Initiative_list().get(10).setSTATUS("2");
				s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		
		rb_y13.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>12){
					z="y";
			data_list.getSite_Initiative_list().get(12).setSTATUS("1");		
			s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~Y";
				}
		 }
		});
		rb_n13.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>12){
					z="y";
					data_list.getSite_Initiative_list().get(12).setSTATUS("0");
				s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~N";
				}
		 }
		});
		rb_missing13.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data_list.getSite_Initiative_list().size()>12){
					z="y";
					data_list.getSite_Initiative_list().get(12).setSTATUS("2");
				s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~M";
				}
		 }
		});
		if (Utils.isNetworkAvailable(SiteInitiativeDetails.this)) {
			new AssignedTicketsTask(SiteInitiativeDetails.this).execute();
		} else {
			Toast.makeText(SiteInitiativeDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(z.equalsIgnoreCase("y")){
			z="x";
			//Toast.makeText(SiteInitiativeDetails.this, "Data will not be saved. Please Click Submit",9000000).show();
			}else{
			Intent i=new Intent(SiteInitiativeDetails.this, AssetsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
			finish();
		}
		
	}
	
	public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
	public AssignedTicketsTask(Context con) {
			this.con = con;
		}
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("CounrtyID",mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("HubID",mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("RegionID",mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("CircleID",mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("ZoneID",mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("ClusterID",mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("EtsSiteID",mAppPreferences.getEtsSiteID()));
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetSiteInitiativeDetails, nameValuePairs);
				Gson gson = new Gson();
				data_list = gson.fromJson(response,BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list = null;
			}
			return null;
		    }
		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!SiteInitiativeDetails.this.isFinishing()) {
				if ((data_list == null)) {
					Toast.makeText(SiteInitiativeDetails.this, "Server Not Available",Toast.LENGTH_LONG).show();
				} else if (data_list.getSite_Initiative_list().size() > 0) {
					valueSet();
				} else{
					Toast.makeText(SiteInitiativeDetails.this, "Details Not Available",Toast.LENGTH_LONG).show();
				}
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View v) {
		 if(v.getId()==R.id.tv_dg1)
		 {
			 if(data_list.getSite_Initiative_list().size()>0){
			 if(data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(0).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(0).getSTATUS().equalsIgnoreCase("0")){
					other(data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(0).getSTATUS(),data_list.getSite_Initiative_list().get(0).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		
		
		 if(v.getId()==R.id.tv_dg2)
		 {
			 if(data_list.getSite_Initiative_list().size()>1){
			 if(data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(1).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show(); }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(1).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(1).getSTATUS(),data_list.getSite_Initiative_list().get(1).getEQUIPMENT_NAME());
			  }else{
				  Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
               }
			 }
		 }
		
		
		 if(v.getId()==R.id.tv_dg3)
		 {
			 if(data_list.getSite_Initiative_list().size()>2){
			 if(data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(2).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(2).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(2).getSTATUS(),data_list.getSite_Initiative_list().get(2).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 if(v.getId()==R.id.tv_dg4)
		 {
			 if(data_list.getSite_Initiative_list().size()>3){
			 if(data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(3).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(3).getSTATUS().equalsIgnoreCase("0")){
			  other(data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(3).getSTATUS(),data_list.getSite_Initiative_list().get(3).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg5)
		 {
			 if(data_list.getSite_Initiative_list().size()>4){
			 if(data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(4).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(4).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(4).getSTATUS(),data_list.getSite_Initiative_list().get(4).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg6)
		 {
			 if(data_list.getSite_Initiative_list().size()>5){
			 if(data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(5).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(5).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(5).getSTATUS(),data_list.getSite_Initiative_list().get(5).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg7)
		 {
			 if(data_list.getSite_Initiative_list().size()>6){
			 if(data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(6).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }else{
					 if(!data_list.getSite_Initiative_list().get(6).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(6).getSTATUS(),data_list.getSite_Initiative_list().get(6).getEQUIPMENT_NAME());
					 }else{
						 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
					 }
					 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg8)
		 {
			 if(data_list.getSite_Initiative_list().size()>7){
			 if(data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(7).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(7).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(7).getSTATUS(),data_list.getSite_Initiative_list().get(7).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg9)
		 {
			 if(data_list.getSite_Initiative_list().size()>8){
			 if(data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(8).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
			 }else{
				 if(!data_list.getSite_Initiative_list().get(8).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(8).getSTATUS(),data_list.getSite_Initiative_list().get(8).getEQUIPMENT_NAME());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg10)
		 {
			 if(data_list.getSite_Initiative_list().size()>9){
			 if(data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(9).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }else{
					 if(!data_list.getSite_Initiative_list().get(9).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(9).getSTATUS(),data_list.getSite_Initiative_list().get(9).getEQUIPMENT_NAME());
					 }else{
						 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
					 }
					 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg11)
		 {
			 if(data_list.getSite_Initiative_list().size()>10){
			 if(data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(10).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }else{
					 if(!data_list.getSite_Initiative_list().get(10).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(10).getSTATUS(),data_list.getSite_Initiative_list().get(10).getEQUIPMENT_NAME());
					 }else{
						 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
					 }
					 }
			 }
		 }
		 
		 
		 
		 if(v.getId()==R.id.tv_dg12)
		 {
			 if(data_list.getSite_Initiative_list().size()>11){
			 if(data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(11).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID());
				 }else{
					 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
				 }
				 }else{
					 if(!data_list.getSite_Initiative_list().get(11).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(11).getSTATUS(),data_list.getSite_Initiative_list().get(11).getEQUIPMENT_NAME());
					 }else{
						 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
					 }
					 }
			 }
		 }
		 
		 
		 if(v.getId()==R.id.tv_dg13)
		 {
			 if(data_list.getSite_Initiative_list().size()>12){
			 if(data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID().equals("115")){
				 if(!data_list.getSite_Initiative_list().get(12).getSTATUS().equalsIgnoreCase("0")){
				 dg(data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID());
			 }else{
				 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
			 }
				 }else{
					 if(!data_list.getSite_Initiative_list().get(12).getSTATUS().equalsIgnoreCase("0")){
				 other(data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID(),data_list.getSite_Initiative_list().get(12).getSTATUS(),data_list.getSite_Initiative_list().get(12).getEQUIPMENT_NAME());
					 }else{
						 Toast.makeText(SiteInitiativeDetails.this, "Asset is not available on site.Please select Y to add the details.", Toast.LENGTH_LONG).show();
					 }
					 }
			 }
		 }
		}
	
	public void valueSet(){
	 //item1
		if(data_list.getSite_Initiative_list().size()>0){
		    tv1.setText(data_list.getSite_Initiative_list().get(0).getEQUIPMENT_NAME().replace("DG","Genset"));
		 		if(data_list.getSite_Initiative_list().get(0).getSTATUS().equals("0")){
				rb_y1.setChecked(false);
				rb_n1.setChecked(true);
				rb_missing1.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(0).getSTATUS().equals("1")){
				rb_y1.setChecked(true);
				rb_n1.setChecked(false);
				rb_missing1.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(0).getSTATUS().equals("2")){
				rb_y1.setChecked(false);
				rb_n1.setChecked(false);
				rb_missing1.setChecked(true);
				
			}
		  }else{
			  ll1.setVisibility(View.GONE);
			  ll2.setVisibility(View.GONE);
			  ll3.setVisibility(View.GONE);
			  ll4.setVisibility(View.GONE);
			  ll5.setVisibility(View.GONE);
			  ll6.setVisibility(View.GONE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
			  }
		
		if(data_list.getSite_Initiative_list().size()>1){
		    tv2.setText(data_list.getSite_Initiative_list().get(1).getEQUIPMENT_NAME().replace("DG","Genset"));
		  //item2
			if(data_list.getSite_Initiative_list().get(1).getSTATUS().equals("0")){
				rb_y2.setChecked(false);
				rb_n2.setChecked(true);
				rb_missing2.setChecked(false);
				System.out.println("=data_list.getSite_Initiative_list()==="+data_list.getSite_Initiative_list().size());
			}else if(data_list.getSite_Initiative_list().get(1).getSTATUS().equals("1")){
				rb_y2.setChecked(true);
				rb_n2.setChecked(false);
				rb_missing2.setChecked(false);
				System.out.println("=data_list.getSite_Initiative_list()==="+data_list.getSite_Initiative_list().size());
			}else if(data_list.getSite_Initiative_list().get(1).getSTATUS().equals("2")){
				rb_y2.setChecked(false);
				rb_n2.setChecked(false);
				rb_missing2.setChecked(true);
				System.out.println("=data_list.getSite_Initiative_list()==="+data_list.getSite_Initiative_list().size());
			}
		 }else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.GONE);
			  ll3.setVisibility(View.GONE);
			  ll4.setVisibility(View.GONE);
			  ll5.setVisibility(View.GONE);
			  ll6.setVisibility(View.GONE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
			 
		  }
		
		 if(data_list.getSite_Initiative_list().size()>2){
			tv3.setText(data_list.getSite_Initiative_list().get(2).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item3
			if(data_list.getSite_Initiative_list().get(2).getSTATUS().equals("0")){
				rb_y3.setChecked(false);
				rb_n3.setChecked(true);
				rb_missing3.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(2).getSTATUS().equals("1")){
				rb_y3.setChecked(true);
				rb_n3.setChecked(false);
				rb_missing3.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(2).getSTATUS().equals("2")){
				rb_y3.setChecked(false);
				rb_n3.setChecked(false);
				rb_missing3.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.GONE);
			  ll4.setVisibility(View.GONE);
			  ll5.setVisibility(View.GONE);
			  ll6.setVisibility(View.GONE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
			  }
		 
		 
		 if(data_list.getSite_Initiative_list().size()>3){
			tv4.setText(data_list.getSite_Initiative_list().get(3).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item4
			if(data_list.getSite_Initiative_list().get(3).getSTATUS().equals("0")){
				rb_y4.setChecked(false);
				rb_n4.setChecked(true);
				rb_missing4.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(3).getSTATUS().equals("1")){
				rb_y4.setChecked(true);
				rb_n4.setChecked(false);
				rb_missing4.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(3).getSTATUS().equals("2")){
				rb_y4.setChecked(false);
				rb_n4.setChecked(false);
				rb_missing4.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.GONE);
			  ll5.setVisibility(View.GONE);
			  ll6.setVisibility(View.GONE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
			  }
		 
		 
		 
		 if(data_list.getSite_Initiative_list().size()>4){
			tv5.setText(data_list.getSite_Initiative_list().get(4).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item5
			if(data_list.getSite_Initiative_list().get(4).getSTATUS().equals("0")){
				rb_y5.setChecked(false);
				rb_n5.setChecked(true);
				rb_missing5.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(4).getSTATUS().equals("1")){
				rb_y5.setChecked(true);
				rb_n5.setChecked(false);
				rb_missing5.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(4).getSTATUS().equals("2")){
				rb_y5.setChecked(false);
				rb_n5.setChecked(false);
				rb_missing5.setChecked(true);
			}
			}else{
				  ll1.setVisibility(View.VISIBLE);
				  ll2.setVisibility(View.VISIBLE);
				  ll3.setVisibility(View.VISIBLE);
				  ll4.setVisibility(View.VISIBLE);
				  ll5.setVisibility(View.GONE);
				  ll6.setVisibility(View.GONE);
				  ll7.setVisibility(View.GONE);
				  ll8.setVisibility(View.GONE);
				  ll9.setVisibility(View.GONE);
				  ll10.setVisibility(View.GONE);
				  ll11.setVisibility(View.GONE);
				  ll12.setVisibility(View.GONE);
				  ll13.setVisibility(View.GONE);
				  }
		 
		 if(data_list.getSite_Initiative_list().size()>5){
			tv6.setText(data_list.getSite_Initiative_list().get(5).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item6
			if(data_list.getSite_Initiative_list().get(5).getSTATUS().equals("0")){
				rb_y6.setChecked(false);
				rb_n6.setChecked(true);
				rb_missing6.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(5).getSTATUS().equals("1")){
				rb_y6.setChecked(true);
				rb_n6.setChecked(false);
				rb_missing6.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(5).getSTATUS().equals("2")){
				rb_y6.setChecked(false);
				rb_n6.setChecked(false);
				rb_missing6.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.GONE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
			  }
		 
		 
		 if(data_list.getSite_Initiative_list().size()>6){
			tv7.setText(data_list.getSite_Initiative_list().get(6).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item7
			if(data_list.getSite_Initiative_list().get(6).getSTATUS().equals("0")){
				rb_y7.setChecked(false);
				rb_n7.setChecked(true);
				rb_missing7.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(6).getSTATUS().equals("1")){
				rb_y7.setChecked(true);
				rb_n7.setChecked(false);
				rb_missing7.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(6).getSTATUS().equals("2")){
				rb_y7.setChecked(false);
				rb_n7.setChecked(false);
				rb_missing7.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.GONE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
		  }
		 
		 
		 if(data_list.getSite_Initiative_list().size()>7){
			tv8.setText(data_list.getSite_Initiative_list().get(7).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item8
			if(data_list.getSite_Initiative_list().get(7).getSTATUS().equals("0")){
				rb_y8.setChecked(false);
				rb_n8.setChecked(true);
				rb_missing8.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(7).getSTATUS().equals("1")){
				rb_y8.setChecked(true);
				rb_n8.setChecked(false);
				rb_missing8.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(7).getSTATUS().equals("2")){
				rb_y8.setChecked(false);
				rb_n8.setChecked(false);
				rb_missing8.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.VISIBLE);
			  ll8.setVisibility(View.GONE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
		  }
		 if(data_list.getSite_Initiative_list().size()>8){
			tv9.setText(data_list.getSite_Initiative_list().get(8).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item9
			if(data_list.getSite_Initiative_list().get(8).getSTATUS().equals("0")){
				rb_y9.setChecked(false);
				rb_n9.setChecked(true);
				rb_missing9.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(8).getSTATUS().equals("1")){
				rb_y9.setChecked(true);
				rb_n9.setChecked(false);
				rb_missing9.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(8).getSTATUS().equals("2")){
				rb_y9.setChecked(false);
				rb_n9.setChecked(false);
				rb_missing9.setChecked(true);
		}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.VISIBLE);
			  ll8.setVisibility(View.VISIBLE);
			  ll9.setVisibility(View.GONE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
		  }
		 
		 
		 
		 if(data_list.getSite_Initiative_list().size()>9){
			tv10.setText(data_list.getSite_Initiative_list().get(9).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item10
			if(data_list.getSite_Initiative_list().get(9).getSTATUS().equals("0")){
				rb_y10.setChecked(false);
				rb_n10.setChecked(true);
				rb_missing10.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(9).getSTATUS().equals("1")){
				rb_y10.setChecked(true);
				rb_n10.setChecked(false);
				rb_missing10.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(9).getSTATUS().equals("2")){
				rb_y10.setChecked(false);
				rb_n10.setChecked(false);
				rb_missing10.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.VISIBLE);
			  ll8.setVisibility(View.VISIBLE);
			  ll9.setVisibility(View.VISIBLE);
			  ll10.setVisibility(View.GONE);
			  ll11.setVisibility(View.GONE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
		  }
		 
		 
		 
		 if(data_list.getSite_Initiative_list().size()>10){
			tv11.setText(data_list.getSite_Initiative_list().get(10).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item11
			if(data_list.getSite_Initiative_list().get(10).getSTATUS().equals("0")){
				rb_y11.setChecked(false);
				rb_n11.setChecked(true);
				rb_missing11.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(10).getSTATUS().equals("1")){
				rb_y11.setChecked(true);
				rb_n11.setChecked(false);
				rb_missing11.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(10).getSTATUS().equals("2")){
				rb_y11.setChecked(false);
				rb_n11.setChecked(false);
				rb_missing11.setChecked(true);
			}
			}else{
				  ll1.setVisibility(View.VISIBLE);
				  ll2.setVisibility(View.VISIBLE);
				  ll3.setVisibility(View.VISIBLE);
				  ll4.setVisibility(View.VISIBLE);
				  ll5.setVisibility(View.VISIBLE);
				  ll6.setVisibility(View.VISIBLE);
				  ll7.setVisibility(View.VISIBLE);
				  ll8.setVisibility(View.VISIBLE);
				  ll9.setVisibility(View.VISIBLE);
				  ll10.setVisibility(View.VISIBLE);
				  ll11.setVisibility(View.GONE);
				  ll12.setVisibility(View.GONE);
				  ll13.setVisibility(View.GONE);
			  }
		 
		 
		 if(data_list.getSite_Initiative_list().size()>11){
			tv12.setText(data_list.getSite_Initiative_list().get(11).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item12
			if(data_list.getSite_Initiative_list().get(11).getSTATUS().equals("0")){
				rb_y12.setChecked(false);
				rb_n12.setChecked(true);
				rb_missing12.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(11).getSTATUS().equals("1")){
				rb_y12.setChecked(true);
				rb_n12.setChecked(false);
				rb_missing12.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(11).getSTATUS().equals("2")){
				rb_y12.setChecked(false);
				rb_n12.setChecked(false);
				rb_missing12.setChecked(true);
			}
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.VISIBLE);
			  ll8.setVisibility(View.VISIBLE);
			  ll9.setVisibility(View.VISIBLE);
			  ll10.setVisibility(View.VISIBLE);
			  ll11.setVisibility(View.VISIBLE);
			  ll12.setVisibility(View.GONE);
			  ll13.setVisibility(View.GONE);
		  }
		 
		 
		 if(data_list.getSite_Initiative_list().size()>12){
			tv13.setText(data_list.getSite_Initiative_list().get(12).getEQUIPMENT_NAME().replace("DG","Genset"));
			//item13
			if(data_list.getSite_Initiative_list().get(12).getSTATUS().equals("0")){
				rb_y13.setChecked(false);
				rb_n13.setChecked(true);
				rb_missing13.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(12).getSTATUS().equals("1")){
				rb_y13.setChecked(true);
				rb_n13.setChecked(false);
				rb_missing13.setChecked(false);
				
			}else if(data_list.getSite_Initiative_list().get(12).getSTATUS().equals("2")){
				rb_y13.setChecked(false);
				rb_n13.setChecked(false);
				rb_missing13.setChecked(true);
			}				
		}else{
			  ll1.setVisibility(View.VISIBLE);
			  ll2.setVisibility(View.VISIBLE);
			  ll3.setVisibility(View.VISIBLE);
			  ll4.setVisibility(View.VISIBLE);
			  ll5.setVisibility(View.VISIBLE);
			  ll6.setVisibility(View.VISIBLE);
			  ll7.setVisibility(View.VISIBLE);
			  ll8.setVisibility(View.VISIBLE);
			  ll9.setVisibility(View.VISIBLE);
			  ll10.setVisibility(View.VISIBLE);
			  ll11.setVisibility(View.VISIBLE);
			  ll12.setVisibility(View.VISIBLE);
			  ll13.setVisibility(View.GONE);
		  }
	
	}
	
	public void dg(String EquipmentId){
		 Intent intent = new Intent(SiteInitiativeDetails.this,GensetDetails.class);
		 intent.putExtra("EquipmentId",EquipmentId);
		 intent.putExtra("noagaincallmode","yes");
		 startActivity(intent);	
		 // finish();
	}
	
	public void other(String EquipmentId,String status,String Equipment){
		 Intent intent = new Intent(SiteInitiativeDetails.this,EquipmentDetails.class);
		 intent.putExtra("EquipmentId",EquipmentId);
		 intent.putExtra("status",status);
		 intent.putExtra("Equipment",Equipment);
		 intent.putExtra("z",z);
		 startActivity(intent);	
		 
		// finish();
	}
	
	public void check(){
		if(data_list.getSite_Initiative_list().size()>0){
		if(rb_y1.isChecked()){
			s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~Y";
			}else if(rb_n1.isChecked()){
			s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~N";	
			}else if(rb_missing1.isChecked()){
			s1=data_list.getSite_Initiative_list().get(0).getEQUIPMENT_ID()+"~M";	
			}
		}
			
		if(data_list.getSite_Initiative_list().size()>1){	
		if(rb_y2.isChecked()){
			s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~Y";
			}else if(rb_n2.isChecked()){
			s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~N";	
			}else if(rb_missing2.isChecked()){
			s2=data_list.getSite_Initiative_list().get(1).getEQUIPMENT_ID()+"~M";	
			}
		}
			
		
		if(data_list.getSite_Initiative_list().size()>2){
		if(rb_y3.isChecked()){
				s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n3.isChecked()){
				s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing3.isChecked()){
				s3=data_list.getSite_Initiative_list().get(2).getEQUIPMENT_ID()+"~M";			
				}
		}
			
			
		if(data_list.getSite_Initiative_list().size()>3){
		if(rb_y4.isChecked()){
				s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n4.isChecked()){
				s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing4.isChecked()){
				s4=data_list.getSite_Initiative_list().get(3).getEQUIPMENT_ID()+"~M";			
				}
		}
			
		
		if(data_list.getSite_Initiative_list().size()>4){
		if(rb_y5.isChecked()){
				s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n5.isChecked()){
				s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing5.isChecked()){
				s5=data_list.getSite_Initiative_list().get(4).getEQUIPMENT_ID()+"~M";			
				}
		}
		
			
		if(data_list.getSite_Initiative_list().size()>5){
			if(rb_y6.isChecked()){
				s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n6.isChecked()){
				s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing6.isChecked()){
				s6=data_list.getSite_Initiative_list().get(5).getEQUIPMENT_ID()+"~M";			
				}
		}
			
		if(data_list.getSite_Initiative_list().size()>6){
			if(rb_y7.isChecked()){
				s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n7.isChecked()){
				s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing7.isChecked()){
				s7=data_list.getSite_Initiative_list().get(6).getEQUIPMENT_ID()+"~M";			
				}
		}
				
			
		if(data_list.getSite_Initiative_list().size()>7){
			if(rb_y8.isChecked()){
				s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n8.isChecked()){
				s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing8.isChecked()){
				s8=data_list.getSite_Initiative_list().get(7).getEQUIPMENT_ID()+"~M";			
				}
		     }
			
		if(data_list.getSite_Initiative_list().size()>8){	
		if(rb_y9.isChecked()){
				s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n9.isChecked()){
				s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing9.isChecked()){
				s9=data_list.getSite_Initiative_list().get(8).getEQUIPMENT_ID()+"~M";			
				}
		}
			
		
		if(data_list.getSite_Initiative_list().size()>9){
		if(rb_y10.isChecked()){
				s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n10.isChecked()){
				s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing10.isChecked()){
				s10=data_list.getSite_Initiative_list().get(9).getEQUIPMENT_ID()+"~M";			
				}	
		}
			
		
		if(data_list.getSite_Initiative_list().size()>10){
		if(rb_y11.isChecked()){
				s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n11.isChecked()){
				s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing11.isChecked()){
				s11=data_list.getSite_Initiative_list().get(10).getEQUIPMENT_ID()+"~M";			
				}
		}
			
		
		if(data_list.getSite_Initiative_list().size()>11){
		if(rb_y12.isChecked()){
				s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n12.isChecked()){
				s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing12.isChecked()){
				s12=data_list.getSite_Initiative_list().get(11).getEQUIPMENT_ID()+"~M";			
				}
		}
		
		 if(data_list.getSite_Initiative_list().size()>12){
			if(rb_y13.isChecked()){
				s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~Y";
				}else if(rb_n13.isChecked()){
				s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~N";	
				}else if(rb_missing13.isChecked()){
				s13=data_list.getSite_Initiative_list().get(12).getEQUIPMENT_ID()+"~M";			
				}
		 }
	}
	
	public class AssignedTicketsTask1 extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
	
		public AssignedTicketsTask1(Context con) {
			this.con = con;

		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("EtsSiteID",mAppPreferences.getEtsSiteID()));
				nameValuePairs.add(new BasicNameValuePair("collection",s1+","+s2+","+s3+","+s4+","+s5+","+s6+","+s7+","+s8
						                          						+","+s9+","+s10+","+s11+","+s12+","+s13));
				nameValuePairs.add(new BasicNameValuePair("Remarks",remarks.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("UserName",mAppPreferences.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("Oper","A"));
				nameValuePairs.add(new BasicNameValuePair("ImgEncoded",imageString));
				nameValuePairs.add(new BasicNameValuePair("lat",latitude));
				nameValuePairs.add(new BasicNameValuePair("longt",longitude));
				nameValuePairs.add(new BasicNameValuePair("txnID",mAppPreferences.getTxnId()));
				String response= Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_SaveSiteInitiativeDetails, nameValuePairs);
				Gson gson = new Gson();
				data_list1 = gson.fromJson(response,BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list1 = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!SiteInitiativeDetails.this.isFinishing()) {
				if ((data_list1 == null)) {
				} else if (data_list1.getSite_Initiative_list_Saved().size() >= 0) {
					if(data_list1.getSite_Initiative_list_Saved().get(0).getSuccess().equalsIgnoreCase("true")){
						Toast.makeText(SiteInitiativeDetails.this, "Audit report Submitted for site no. "+mAppPreferences.getSiteID()+" and notification is forwarded.",Toast.LENGTH_LONG).show();
						Intent i=new Intent(SiteInitiativeDetails.this,SiteDetails.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						mAppPreferences.setAuditMode("yes");
						startActivity(i);
						finish();	
					}else{
					    Toast.makeText(SiteInitiativeDetails.this, ""+data_list1.getSite_Initiative_list_Saved().get(0).getMessage(),Toast.LENGTH_LONG).show();
					}
					} else {
				   }
			      }
			super.onPostExecute(result);
		}
	}

	protected void imageCapture() {
		String name = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ENGLISH).format(new Date());
		destination = new File(Environment.getExternalStorageDirectory(), name + System.currentTimeMillis() + ".jpg");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
		startActivityForResult(intent, 2);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
			try {
				filePath = destination.getAbsolutePath();
				Bitmap bm = decodeFile(destination);
				OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(destination);
					bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
					iv_photo.setImageBitmap(bm);
					iv_photo.setVisibility(View.VISIBLE);
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
				}
			} catch (Exception th) {
				th.printStackTrace();
			}
		}
	}
	private Bitmap decodeFile(File f) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			final int REQUIRED_SIZE = 200;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE	&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	}
