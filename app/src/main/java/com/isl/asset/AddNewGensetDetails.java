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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
public class AddNewGensetDetails extends Activity {
	GPSTracker gps;
	String filePath = "",SiteId,EtsSiteId,DgType,DgCapacity,imageString,DgTankCapacity,DgMake,latitude = "", longitude = "";
	ArrayList<String> DgTypeList,DgMakeList,DgMakeId,tank_list;
	EditText et_site_id,dg_capacity,dg_tank_capacity;
	Spinner sp_dgtype,sp_dgmake;
	BeanAssetModuleList data_list1;
	Button send,take_photo;
	int pos1,pos2;
	AppPreferences mAppPreferences;
	ProgressDialog pd;
	ImageView iv_photo;
	File destination;
	String temp_dg_id;
	DataBaseHelper dbHelper=new DataBaseHelper(AddNewGensetDetails.this);
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(AddNewGensetDetails.this);
		SiteId=mAppPreferences.getSiteID();
		EtsSiteId=mAppPreferences.getEtsSiteID();
		setContentView(R.layout.add_new_dg);
		take_photo = (Button) findViewById(R.id.btn_take_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		take_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageCapture();
			}
		});
		gps = new GPSTracker(AddNewGensetDetails.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
		}
		DgMakeList = (ArrayList<String>) getIntent().getSerializableExtra("DgMakeList");
		DgMakeId = (ArrayList<String>) getIntent().getSerializableExtra("DgMakeId");
		String[] PassiveAssets=mAppPreferences.getSiteAuditRights().split("~");
		RelativeLayout RL_Send=(RelativeLayout) findViewById(R.id.rl_send);
		if(PassiveAssets.length==1){
			if(mAppPreferences.getSiteAuditRights().equalsIgnoreCase("V")){
				RL_Send.setVisibility(View.GONE);
				
			}
		}else{
				if(PassiveAssets[0].equalsIgnoreCase("M")){
					if(PassiveAssets[1].equalsIgnoreCase("V")){
						RL_Send.setVisibility(View.VISIBLE);
					}
		            }
		}
		send=(Button)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			DgType=tank_list.get(pos1);
			DgCapacity=dg_capacity.getText().toString();
			DgTankCapacity=dg_tank_capacity.getText().toString();
			imageString = Utils.convertImageToBase64(filePath); 
			if (sp_dgmake.getSelectedItem().toString().equalsIgnoreCase("Select Genset Make")) {
				Toast.makeText(AddNewGensetDetails.this,"Select Genset Make", Toast.LENGTH_SHORT).show();
		    }else if (dg_capacity.getText().toString().length()==0) {
				Toast.makeText(AddNewGensetDetails.this,"Enter Genset Capacity", Toast.LENGTH_SHORT).show();
		    }else if (dg_tank_capacity.getText().toString().length()==0) {
				Toast.makeText(AddNewGensetDetails.this,"Enter Genset Tank Capacity", Toast.LENGTH_SHORT).show();
		    }else if (Utils.isNetworkAvailable(AddNewGensetDetails.this)) {
				new AddDG(AddNewGensetDetails.this).execute();
			}else {
				Toast.makeText(AddNewGensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
			}					
		}
		});
		et_site_id=(EditText)findViewById(R.id.et_site_id);
		et_site_id.setText(""+SiteId);
		et_site_id=(EditText)findViewById(R.id.et_site_id);
		dg_capacity=(EditText)findViewById(R.id.dg_capacity);
		dg_tank_capacity=(EditText)findViewById(R.id.dg_tank_capacity);
		sp_dgtype=(Spinner)findViewById(R.id.sp_dgtype);
		sp_dgmake=(Spinner)findViewById(R.id.sp_dgmake);
		addItemsOnSpinner(sp_dgmake,DgMakeList);
		tank_list=new ArrayList();
		DataBaseHelper db = new DataBaseHelper(AddNewGensetDetails.this);
		db.open();
		tank_list.addAll(db.getEnergyDgDesc());
		addItemsOnSpinner(sp_dgtype, tank_list);
		Button iv_back = (Button) findViewById(R.id.button_back);
		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent i=new Intent(AddNewGensetDetails.this,GensetDetails.class);
			i.putExtra("noagaincallmode","no");
			startActivity(i);
			finish();	
		}
	    });
		sp_dgtype.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				dbHelper.open();
				temp_dg_id=dbHelper.getAssetDgId(sp_dgtype.getSelectedItem().toString());
				dbHelper.close();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			 }
			});
			
		 sp_dgmake.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					pos2=arg2;	
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {				
				 }
				});
		 }
	@Override
	public void onBackPressed() {
		Intent i=new Intent(AddNewGensetDetails.this,GensetDetails.class);
		i.putExtra("noagaincallmode","no");
		startActivity(i);
		finish();	
		
	}
	
	public class AddDG extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd1;
		Context con;
	
		public AddDG(Context con) {
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("ETS_SITE_ID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("SITE_ID",SiteId));
				nameValuePairs.add(new BasicNameValuePair("DG_TYPE",temp_dg_id));
				nameValuePairs.add(new BasicNameValuePair("DG_CAPACITY",DgCapacity));
				nameValuePairs.add(new BasicNameValuePair("DG_TANK_CAPACITY",DgTankCapacity));
				nameValuePairs.add(new BasicNameValuePair("DG_MAKE",DgMakeId.get(pos2)));
				nameValuePairs.add(new BasicNameValuePair("WEF_DATE",""));
				nameValuePairs.add(new BasicNameValuePair("WET_DATE",""));
				nameValuePairs.add(new BasicNameValuePair("OPER","A"));
				nameValuePairs.add(new BasicNameValuePair("USER",mAppPreferences.getLoginId()));
				nameValuePairs.add(new BasicNameValuePair("imagePath",imageString));
				nameValuePairs.add(new BasicNameValuePair("lat",latitude));
				nameValuePairs.add(new BasicNameValuePair("longt",longitude));
				nameValuePairs.add(new BasicNameValuePair("txnID",mAppPreferences.getTxnId()));
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_SaveDGDetails,nameValuePairs);
				System.out.println("+++++add new Dg++++"+temp_dg_id);
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
			if (pd1.isShowing()) {
				pd1.dismiss();
			}
			Message myMessage = new Message();
			myMessage.obj = "ApprovedList2";
			myHandler2.sendMessage(myMessage);
			super.onPostExecute(result);
		}
	}

	private Handler myHandler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj.toString().equalsIgnoreCase("ApprovedList2")) {
				if (!AddNewGensetDetails.this.isFinishing()) {
					if ((data_list1 == null)) {
					 } else if (data_list1.getDG_Detail_list_Saved().size() > 0) {
						 if(data_list1.getDG_Detail_list_Saved().get(0).getSuccess().equalsIgnoreCase("true")){
							 Toast.makeText(AddNewGensetDetails.this, ""+data_list1.getDG_Detail_list_Saved().get(0).getMessage(),Toast.LENGTH_LONG).show();
								Intent i=new Intent(AddNewGensetDetails.this,GensetDetails.class);
								i.putExtra("noagaincallmode","no");
								startActivity(i);
								finish();	
							}else{
							    Toast.makeText(AddNewGensetDetails.this, ""+data_list1.getDG_Detail_list_Saved().get(0).getMessage(),Toast.LENGTH_LONG).show();
							}
					   
					} else {
					}
				}
			}
		}
	};
	
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
				System.out.println("file path::" + filePath);
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
	
	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}
	
}
