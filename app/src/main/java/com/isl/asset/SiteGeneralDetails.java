package com.isl.asset;
import com.isl.constant.WebMethods;
import com.isl.itower.GPSTracker;
import infozech.itower.R;
import com.isl.modal.BeanAssetModuleList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
public class SiteGeneralDetails extends Activity {
	private int hour, minute, pYear, pMonth, pDay;
	AppPreferences mAppPreferences;
	String EtsSiteId,SiteId,SiteName,siteGenDetails,Wef_date,latitude = "", longitude = "",filePath = "";
	EditText et_siteID,et_site_name,et_opco_siteID,et_site_address,et_latitude,et_longitude,et_last_auditedby1,et_site_load,
    et_remark;
	Button submit;
	Spinner  sp_region,sp_sub_region,sp_mini_cluster,sp_vender,sp_power_vender,sp_site_type,
    sp_site_location,sp_site_area_type,sp_owner,sp_site_class,sp_power_model;
	RelativeLayout RL;
	TextView et_rfs_date;
	ArrayList<String> region_list,sub_region_list,mini_cluster_list,list_vender,power_vender_list,
	site_type_list,area_type_list,owner_list,site_location_list,site_class_list,power_model_list;
	String   miniClusterId,venderId,powerVenderId,siteTypeId,areaTypeId,ownerId,locationId,siteClassId,powerModelId;
	DataBaseHelper db;
	ImageView iv_photo;
	File destination;
	ProgressDialog pd;
	int s_mode=0;
	Date d;
	BeanAssetModuleList SaveResponce;
	BeanAssetModuleList SiteGeneralDetailsResponce;
	private DatePickerDialog.OnDateSetListener pDateSetListener3 = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			pYear = year;
			pMonth = monthOfYear;
			pDay = dayOfMonth;
			d = Utils.convertStringToDate(new StringBuilder().append(pMonth + 1).append("/").append(pDay).append("/").append(pYear).toString(),"MM/dd/yyyy");
			if (Utils.checkValidation(d)) {
				et_rfs_date.setText(Utils.changeDateFormat(new StringBuilder().append(pMonth + 1).append("/").append(pDay).append("/").append(pYear).toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
			} else {
				Toast.makeText(SiteGeneralDetails.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_general_details);
		db = new DataBaseHelper(SiteGeneralDetails.this);
		db.open();
		init();
		
		
		
		et_rfs_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			showDialog(1);
			}
			});  
		submit.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		validation();
		}
		});
		Wef_date=Utils.changeDateFormat(new StringBuilder().append(pMonth + 1).append("/").append(pDay).append("/").append(pYear).toString(), "MM/dd/yyyy", "dd-MMM-yyyy");
	   	Button take_photo = (Button) findViewById(R.id.btn_take_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		take_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageCapture();
			}
		});
		GPSTracker gps = new GPSTracker(SiteGeneralDetails.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
		}
		sp_sub_region.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
			if(s_mode==1){	
			addItemsOnSpinner(sp_mini_cluster,db.getAllMini(sp_region.getSelectedItem().toString(),sp_sub_region.getSelectedItem().toString()));
			}
			s_mode=1;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (Utils.isNetworkAvailable(SiteGeneralDetails.this)) {
		new GetDetailsTask(SiteGeneralDetails.this).execute();
		}else{
		Toast.makeText(SiteGeneralDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		
		
		
		Button back=(Button)findViewById(R.id.button_back);
		back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		Intent i=new Intent(SiteGeneralDetails.this, AssetsActivity.class);
		startActivity(i);
		finish();
		}
		});
		
		}
	public void init(){
		final Calendar cal = Calendar.getInstance();
		pYear = cal.get(Calendar.YEAR);
		pMonth = cal.get(Calendar.MONTH);
		pDay = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		mAppPreferences = new AppPreferences(SiteGeneralDetails.this);
		EtsSiteId=mAppPreferences.getEtsSiteID();
		SiteId=mAppPreferences.getSiteID();
		SiteName=mAppPreferences.getSiteName();
		et_siteID=(EditText) findViewById(R.id.site);
		submit=(Button)findViewById(R.id.submit);
		RL=(RelativeLayout)findViewById(R.id.Rl);
		et_siteID.setText(""+SiteId);
		et_site_name=(EditText) findViewById(R.id.et_site_name);
		et_opco_siteID=(EditText) findViewById(R.id.et_opco);
		et_site_address=(EditText) findViewById(R.id.SAddr);
		et_latitude=(EditText) findViewById(R.id.et_latitude);
		et_longitude=(EditText) findViewById(R.id.et_longitude);
		et_rfs_date=(TextView) findViewById(R.id.et_rfs_rate);
		et_site_load=(EditText) findViewById(R.id.et_site_load);
		et_remark=(EditText) findViewById(R.id.gbt_rtt);
		sp_region=(Spinner) findViewById(R.id.sp_region);
		sp_sub_region=(Spinner) findViewById(R.id.sp_sub_region);
		sp_mini_cluster=(Spinner) findViewById(R.id.sp_mini_cluster);
		sp_vender=(Spinner) findViewById(R.id.sp_vendor);
		sp_power_vender=(Spinner) findViewById(R.id.sp_power_vendor);
		sp_site_type=(Spinner) findViewById(R.id.sp_site_type);
		sp_site_area_type=(Spinner) findViewById(R.id.sp_site__area_type);
		sp_owner=(Spinner) findViewById(R.id.sp_owner);
		sp_site_location=(Spinner) findViewById(R.id.sp_site_location);
		sp_site_class=(Spinner) findViewById(R.id.sp_site_class);
		sp_power_model=(Spinner) findViewById(R.id.sp_power_model);
		if(et_siteID.getText().toString().length()==0){
			et_siteID.setEnabled(true);
		}else{
			et_siteID.setEnabled(false);
		}
		siteGenDetails=getIntent().getStringExtra("siteGenDetails");
		String[] SiteGeneralDetails=siteGenDetails.split("~");
		if(SiteGeneralDetails.length==1){
			if(siteGenDetails.equalsIgnoreCase("V")){
				RL.setVisibility(View.GONE);
			}
		}else{
				if(SiteGeneralDetails[0].equalsIgnoreCase("M")){
					if(SiteGeneralDetails[1].equalsIgnoreCase("V")){
						RL.setVisibility(View.VISIBLE);
					}else{
						RL.setVisibility(View.GONE);
			     }
				}else {
					RL.setVisibility(View.GONE);
				}
		}
		}
	
	public void bindValue(){
		addItemsOnSpinner(sp_region,db.getRegion(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_ID(),1));
		addItemsOnSpinner(sp_sub_region,db.getAllSubRegions(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_NAME()));
		addItemsOnSpinner(sp_mini_cluster,db.getAllMini(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_NAME(),
				                                        SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSUB_REGOIN_NAME()));
		addItemsOnSpinner(sp_vender,db.getVender(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_ID()));
		addItemsOnSpinner(sp_power_vender,db.getAssetParamName(68));
		addItemsOnSpinner(sp_site_type,db.getAssetParamName(5));
		addItemsOnSpinner(sp_site_area_type,db.getAssetParamName(67));
		addItemsOnSpinner(sp_owner,db.getAssetParamName(3));
		addItemsOnSpinner(sp_site_location,db.getAssetParamName(2));
		addItemsOnSpinner(sp_site_class,db.getAssetParamName(71));
		addItemsOnSpinner(sp_power_model,db.getAssetParamName(69));
		
		sp_sub_region.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSUB_REGOIN_NAME(),
				                                   db.getAllSubRegions(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_NAME())));
		sp_mini_cluster.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCLUSTER_NAME(),
				                                    db.getAllMini(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_NAME(),
                                                    SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSUB_REGOIN_NAME())));
		sp_vender.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getOME_NAME(),
				                                    db.getVender(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_ID())));
		sp_power_vender.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getPOWER_VENDOR(),db.getAssetParamName(68)));
		sp_site_class.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSITE_CLASS_NAME(),db.getAssetParamName(71)));
		sp_power_model.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getPOWER_MODEL_NAME(), db.getAssetParamName(69)));
		sp_owner.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getOWNERSHIP(),db.getAssetParamName(3)));
		sp_site_area_type.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSITE_AREA_NAME(),db.getAssetParamName(67)));
		sp_site_type.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getSITE_CRITICALITY(),db.getAssetParamName(5)));
		sp_site_location.setSelection(getCategoryPos(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getGBT_RTT_RTP_COW(),db.getAssetParamName(2)));
		}
	
	private int getCategoryPos(String name,ArrayList<String> list) {
		 return list.indexOf(name);
			
	}
		
	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}
	
	
	public void validation(){
		   getId();
		   float a = 0,b = 0;
		   String s1=et_latitude.getText().toString().trim();
		   String s2=et_longitude.getText().toString().trim();
		   if(et_latitude.getText().toString()!=null && !et_latitude.getText().toString().isEmpty()){
		   a=Float.valueOf(s1);
		   }
		   if(et_longitude.getText().toString()!=null && !et_longitude.getText().toString().isEmpty()){
		   b=Float.valueOf(s2);
		   }
			if(et_siteID.getText().toString().trim().length()== 0){
				Toast.makeText(SiteGeneralDetails.this, "Enter Site ID",Toast.LENGTH_SHORT).show();
			}else if(et_site_name.getText().toString().trim().length()==0){
				Toast.makeText(SiteGeneralDetails.this, "Enter Site Name",Toast.LENGTH_SHORT).show();
			}else if(sp_region.getSelectedItem().toString().equalsIgnoreCase("Select Region")){
				 Toast.makeText(SiteGeneralDetails.this, "Select Region",Toast.LENGTH_SHORT).show();
			}else if(sp_sub_region.getSelectedItem().toString().equalsIgnoreCase("Select Sub Region")){
				 Toast.makeText(SiteGeneralDetails.this, "Select Sub Region",Toast.LENGTH_SHORT).show();
			}else if(sp_mini_cluster.getSelectedItem().toString().equalsIgnoreCase("Select Mini Cluster")){
				 Toast.makeText(SiteGeneralDetails.this, "Select Mini Cluster",Toast.LENGTH_SHORT).show();
			}else if(sp_vender.getSelectedItem().toString().equalsIgnoreCase("Select Vendor")){
				Toast.makeText(SiteGeneralDetails.this, "Select Vendor",Toast.LENGTH_SHORT).show();
			}else if(et_latitude.getText().toString().trim().length()==0){
				Toast.makeText(SiteGeneralDetails.this, "Enter Latitude",Toast.LENGTH_SHORT).show();
			}else if(a<-90 || a>90){
				Toast.makeText(SiteGeneralDetails.this, "Latitude must be between -90 and 90.",Toast.LENGTH_SHORT).show();
			}else if(et_longitude.getText().toString().trim().length()==0){
				Toast.makeText(SiteGeneralDetails.this, "Enter Longitude",Toast.LENGTH_SHORT).show();
			}else if(b<-180 || b>180){
				Toast.makeText(SiteGeneralDetails.this, "Longitude must be between -180 and 180.",Toast.LENGTH_SHORT).show();
			}else if(sp_power_vender.getSelectedItem().toString().equalsIgnoreCase("Select Power Vendor")){
				Toast.makeText(SiteGeneralDetails.this, "Select Power Vendor",Toast.LENGTH_SHORT).show();
			}else if(sp_site_type.getSelectedItem().toString().equalsIgnoreCase("Select Site Type")){
				Toast.makeText(SiteGeneralDetails.this, "Select Site Type",Toast.LENGTH_SHORT).show();
			}else if(sp_owner.getSelectedItem().toString().equalsIgnoreCase("Select Owner")){
				Toast.makeText(SiteGeneralDetails.this, "Select Owner",Toast.LENGTH_SHORT).show();
			}else if(sp_site_location.getSelectedItem().toString().equalsIgnoreCase("Select Site Location")){
				Toast.makeText(SiteGeneralDetails.this, "Select Site Location",Toast.LENGTH_SHORT).show();
			}else if(sp_site_class.getSelectedItem().toString().equalsIgnoreCase("Select Site Class")){
				Toast.makeText(SiteGeneralDetails.this, "Select Site Class",Toast.LENGTH_SHORT).show();
			}else if(sp_power_model.getSelectedItem().toString().equalsIgnoreCase("Select Power Model")){
				Toast.makeText(SiteGeneralDetails.this, "Select Power Model",Toast.LENGTH_SHORT).show();
			}else if (Utils.isNetworkAvailable(SiteGeneralDetails.this)) {
				new SaveDetailsTask(SiteGeneralDetails.this).execute();
		    }else {
				Toast.makeText(SiteGeneralDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		   }
		   }
	
	public void getId(){
	 miniClusterId=db.getMiniId(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_NAME(),
			                    sp_mini_cluster.getSelectedItem().toString());
	 venderId=db.getVenderId(SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_ID(),
			                 sp_vender.getSelectedItem().toString());	
	 powerVenderId=db.getAssetParamId("68",sp_power_vender.getSelectedItem().toString());
	 siteTypeId=db.getAssetParamId("5",sp_site_type.getSelectedItem().toString());
	 areaTypeId=db.getAssetParamId("67",sp_site_area_type.getSelectedItem().toString());
	 ownerId=db.getAssetParamId("3",sp_owner.getSelectedItem().toString());
	 locationId=db.getAssetParamId("2",sp_site_location.getSelectedItem().toString());
	 siteClassId=db.getAssetParamId("71",sp_site_class.getSelectedItem().toString());
	 powerModelId=db.getAssetParamId("69",sp_power_model.getSelectedItem().toString());
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
	 
	@Override
    protected Dialog onCreateDialog(int id) {
	     switch (id) {
	     case 1:
	     return new DatePickerDialog(this, pDateSetListener3, pYear, pMonth,pDay);
         }
	     return null;
      }
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(SiteGeneralDetails.this, AssetsActivity.class);
		startActivity(i);
		finish();
	 }
	
	public class GetDetailsTask extends AsyncTask<Void, Void, Void> {
		Context con;
		public GetDetailsTask(Context con) {
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("etsSiteID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("siteID",SiteId));
				nameValuePairs.add(new BasicNameValuePair("date",""));                          
			    String res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetSiteDetailGeneralInfo,nameValuePairs);
			    Gson gson = new Gson();
			    SiteGeneralDetailsResponce = gson.fromJson(res, BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			     if (pd.isShowing()) {
				 pd.dismiss();
			     }
				 if ((SiteGeneralDetailsResponce == null)) {
					Intent i=new Intent(SiteGeneralDetails.this,SiteDetails.class);
					Toast.makeText(SiteGeneralDetails.this, "General Details not Available in Server",Toast.LENGTH_LONG).show();
					startActivity(i);
					finish();
					
				 } else if (SiteGeneralDetailsResponce.getSite_Detail_General_list().size() > 0) {
					if(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getSITE_NAME()!=null && !SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getSITE_NAME().isEmpty()){
						et_site_name.setText(""+SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getSITE_NAME());	
					}else{
						et_site_name.setText(""+SiteName);
					}
					et_opco_siteID.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getTELENOR_SITE_ID());
					et_site_address.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getSITE_ADDRESS());
					et_latitude.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getLATITUDE());
					et_longitude.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getLONGITUDE());
					et_rfs_date.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getRFS_DATE());
					et_site_load.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getSITE_LOAD());
					et_remark.setText(SiteGeneralDetailsResponce.getSite_Detail_General_list().get(0).getREMARKS());
					bindValue();
				 }
			    super.onPostExecute(result);
		       }
	        }
	
	  public class SaveDetailsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd1;
		Context con;
	
		public SaveDetailsTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pd1 = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(38);
				nameValuePairs.add(new BasicNameValuePair("siteID",SiteId));
				nameValuePairs.add(new BasicNameValuePair("etsSiteID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("siteName",et_site_name.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("siteAddress",et_site_address.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("districtID",""));
				nameValuePairs.add(new BasicNameValuePair("omeID",venderId));
				nameValuePairs.add(new BasicNameValuePair("clusterID",miniClusterId));
				nameValuePairs.add(new BasicNameValuePair("ownerID",ownerId));
				nameValuePairs.add(new BasicNameValuePair("gbtrttID",locationId));
				nameValuePairs.add(new BasicNameValuePair("criticalityID",siteTypeId));
				nameValuePairs.add(new BasicNameValuePair("ebdgStatus",""));
				nameValuePairs.add(new BasicNameValuePair("dgRate",""));
				nameValuePairs.add(new BasicNameValuePair("ebRate",""));
				nameValuePairs.add(new BasicNameValuePair("rfsDate",et_rfs_date.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("ac_capacity",""));
				nameValuePairs.add(new BasicNameValuePair("ac_load",""));
				nameValuePairs.add(new BasicNameValuePair("site_load",""));
				nameValuePairs.add(new BasicNameValuePair("remarks",et_remark.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("operation","E"));
				nameValuePairs.add(new BasicNameValuePair("user",mAppPreferences.getName()));
				nameValuePairs.add(new BasicNameValuePair("circleID",SiteGeneralDetailsResponce.Site_Detail_General_list.get(0).getCIRCLE_ID()));
				nameValuePairs.add(new BasicNameValuePair("wef",Wef_date));
				nameValuePairs.add(new BasicNameValuePair("wet",""));
				nameValuePairs.add(new BasicNameValuePair("windFactor",""));
				nameValuePairs.add(new BasicNameValuePair("acmake",""));
				nameValuePairs.add(new BasicNameValuePair("cityname",""));
				nameValuePairs.add(new BasicNameValuePair("buildheight",""));
				nameValuePairs.add(new BasicNameValuePair("latitude",et_latitude.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("longitude",et_longitude.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("telenSiteID",et_opco_siteID.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("keyPerson",""));
				nameValuePairs.add(new BasicNameValuePair("siteArea",areaTypeId));
				nameValuePairs.add(new BasicNameValuePair("powerModel",powerModelId));
				nameValuePairs.add(new BasicNameValuePair("powerVendor",powerVenderId));
				nameValuePairs.add(new BasicNameValuePair("siteClass",siteClassId));
				nameValuePairs.add(new BasicNameValuePair("imagePath",Utils.convertImageToBase64(filePath)));
				nameValuePairs.add(new BasicNameValuePair("mobLat",latitude));
				nameValuePairs.add(new BasicNameValuePair("mobLong",longitude));
				nameValuePairs.add(new BasicNameValuePair("txnID",mAppPreferences.getTxnId()));
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_SaveSiteGeneralInfo,nameValuePairs);
				Gson gson = new Gson();
				SaveResponce = gson.fromJson(response,BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
				SaveResponce = null;
			}
			return null;
		   }
		@Override
		protected void onPostExecute(Void result) {
			if (pd1.isShowing()) {
				pd1.dismiss();
			}
			  if ((SaveResponce == null)) {
			  } else if (SaveResponce.getSite_General_Info_list().size() > 0) {
			  Toast.makeText(SiteGeneralDetails.this, ""+SaveResponce.getSite_General_Info_list().get(0).getMessage(),Toast.LENGTH_LONG).show();
			  if(SaveResponce.getSite_General_Info_list().get(0).success.equalsIgnoreCase("true")){
			  Intent i=new Intent(SiteGeneralDetails.this, AssetsActivity.class);
			  startActivity(i);
			  finish();
			  }
			  } else {
			  }
			  
			super.onPostExecute(result);
		     }
	       } 
	}

