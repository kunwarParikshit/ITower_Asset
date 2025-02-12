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

public class EquipmentDetails extends Activity {
	GPSTracker gps;
	String SiteId, EtsSiteId, Status, EquipmentId, DgType, DgCapacity,
			DgTankCapacity, DgMake, assetlist;
	EditText et_other_site_id, et_other_asset,et_other_asset_measure,et_other_unit_measurement;
	Spinner sp_other_asset_make, sp_other_asset_owner;
	String response_assigned_tickets, Equipment, response_assigned_tickets1,
			populateEquipmentMake, imageString, latitude = "",
			longitude = "";
	BeanAssetModuleList data_list;
	BeanAssetModuleList data_list1;
	BeanAssetModuleList data_list2;
	Button send, take_photo;
	ImageView iv_photo;
	int a = 0, pos1, pos2;
	String s;
	AppPreferences mAppPreferences;
	ArrayList<String> populate, populateValue, powerVenderList;
	ProgressDialog pd;
	File destination;
	String filePath = "", temp_param_id;
	DataBaseHelper dbHelper = new DataBaseHelper(EquipmentDetails.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(EquipmentDetails.this);
		SiteId = mAppPreferences.getSiteID();
		EtsSiteId = mAppPreferences.getEtsSiteID();
		Intent intent = getIntent();
		EquipmentId = intent.getStringExtra("EquipmentId");
		Equipment = intent.getStringExtra("Equipment");
		Status = intent.getStringExtra("status");
		setContentView(R.layout.other_assets);
		take_photo = (Button) findViewById(R.id.btn_take_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		take_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageCapture();
			}
		});
		gps = new GPSTracker(EquipmentDetails.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
		}

		populate = new ArrayList<String>();
		populateValue = new ArrayList<String>();
		populate.add("Select Equipment Make");
		populateValue.add("0");
		Button iv_back = (Button) findViewById(R.id.button_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		et_other_site_id = (EditText) findViewById(R.id.et_other_site_id);
		et_other_site_id.setText("" + SiteId);
		et_other_asset = (EditText) findViewById(R.id.et_other_asset);
		et_other_asset.setText(Equipment);
		sp_other_asset_make = (Spinner) findViewById(R.id.sp_other_asset_make);
		sp_other_asset_owner = (Spinner) findViewById(R.id.sp_other_asset_owner);
		et_other_asset_measure = (EditText) findViewById(R.id.et_other_asset_measure);
		et_other_unit_measurement = (EditText) findViewById(R.id.et_other_unit_measurement);
		String[] PassiveAssets = mAppPreferences.getSiteAuditRights().split("~");
		RelativeLayout RL_Send = (RelativeLayout) findViewById(R.id.rl_other_send);
		if (PassiveAssets.length == 1) {
			if (mAppPreferences.getSiteAuditRights().equalsIgnoreCase("V")) {
				RL_Send.setVisibility(View.GONE);
			}
		} else {
			if (PassiveAssets[0].equalsIgnoreCase("M")) {
				if (PassiveAssets[1].equalsIgnoreCase("V")) {
					RL_Send.setVisibility(View.VISIBLE);
				}
			}
		}
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et_other_asset.getText().toString().length() == 0)
				{Toast.makeText(EquipmentDetails.this, "Enter Equipment",Toast.LENGTH_SHORT).show();
				} else if (et_other_unit_measurement.getText().toString().length() == 0) {
					Toast.makeText(EquipmentDetails.this,"Unit of Measurement cannot be blank",Toast.LENGTH_SHORT).show();
				} else if (sp_other_asset_owner.getSelectedItem().toString().equalsIgnoreCase("Select Power Vendor")) {
					Toast.makeText(EquipmentDetails.this,"Select Power Vendor", Toast.LENGTH_SHORT).show();
				} else if (Utils.isNetworkAvailable(EquipmentDetails.this)) {
					imageString = Utils.convertImageToBase64(filePath);
					new AssignedTicketsTask2(EquipmentDetails.this).execute();
				} else {
					Toast.makeText(EquipmentDetails.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dbHelper.open();
		powerVenderList = dbHelper.getAssetParamName(68);
		addItemsOnSpinner(sp_other_asset_owner, powerVenderList);
		dbHelper.close();

		if (Utils.isNetworkAvailable(EquipmentDetails.this)) {
			new PopulateEquipmentMake(EquipmentDetails.this).execute();
		} else {
			Toast.makeText(EquipmentDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}

		sp_other_asset_make.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						pos1 = arg2;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public class PopulateEquipmentMake extends AsyncTask<Void, Void, Void> {
		Context con;

		public PopulateEquipmentMake(Context con) {
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
				nameValuePairs.add(new BasicNameValuePair("initiativeid",EquipmentId));
				nameValuePairs.add(new BasicNameValuePair("initiativename", ""));
				nameValuePairs.add(new BasicNameValuePair("initiativemakeid","0"));
				nameValuePairs.add(new BasicNameValuePair("initiativemanename",""));
				nameValuePairs.add(new BasicNameValuePair("flag", "0"));
				populateEquipmentMake = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_PopulateEquipmentMake, nameValuePairs);
				Gson gson = new Gson();
				data_list2 = gson.fromJson(populateEquipmentMake,BeanAssetModuleList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list2 = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (data_list2.getEquipment_Make_list().size() > 0) {
				for (int i = 0; i < data_list2.getEquipment_Make_list().size(); i++) {
					populate.add(data_list2.getEquipment_Make_list().get(i).getINITIATIVE_MAKE_NAME());
					populateValue.add(data_list2.getEquipment_Make_list().get(i).getINITIATIVE_MAKE_ID());
				}
				if (Utils.isNetworkAvailable(EquipmentDetails.this)) {
					new AssignedTicketsTask1(EquipmentDetails.this).execute();
				} else {
					Toast.makeText(EquipmentDetails.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
					
				}
			} else {
				new AssignedTicketsTask1(EquipmentDetails.this).execute();
			}
			addItemsOnSpinner(sp_other_asset_make, populate);
		}
	}

	public class AssignedTicketsTask1 extends AsyncTask<Void, Void, Void> {
		Context con;

		public AssignedTicketsTask1(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						7);
				nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("omeID", ""));
				nameValuePairs.add(new BasicNameValuePair("initiativeid",EquipmentId));
				nameValuePairs.add(new BasicNameValuePair("etssiteID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("date", ""));
				response_assigned_tickets = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetEquipmentDetail, nameValuePairs);
				Gson gson = new Gson();
				data_list = gson.fromJson(response_assigned_tickets,BeanAssetModuleList.class);

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

			if ((data_list == null)) {
				s = "A";
				et_other_site_id.setText("" + SiteId);
			} else if (data_list.getEquipment_Details_list().size() > 0) {
				s = "E";
				sp_other_asset_make.setClickable(false);
				et_other_site_id.setText("" + SiteId);
				et_other_asset.setText(""+ data_list.getEquipment_Details_list().get(0).getINITIATIVE_NAME());
				pos1 = getCategoryPos(data_list.getEquipment_Details_list().get(0).getMAKE_DESC(), populate);
				pos2 = getCategoryPos(data_list.getEquipment_Details_list().get(0).getOWNER_NAME(), powerVenderList);
				if (pos1 <= 0) {
					pos1 = 0;
				}
				if (pos2 <= 0) {
					pos2 = 0;
				}
				sp_other_asset_owner.setSelection(pos2);
				sp_other_asset_make.setSelection(pos1);
				
				
				
				if (data_list.getEquipment_Details_list().get(0).getINITIATIVE_BASE_VALUE() != null && !data_list.getEquipment_Details_list().get(0).getINITIATIVE_BASE_VALUE().isEmpty()) {
					et_other_asset_measure.setText(""+ data_list.getEquipment_Details_list().get(0).getINITIATIVE_BASE_VALUE());
				} else {
					et_other_asset_measure.setText("");
				}
				if (data_list.getEquipment_Details_list().get(0).getOWNER_NAME() != null && !data_list.getEquipment_Details_list().get(0).getOWNER_NAME().isEmpty()) {
					sp_other_asset_owner.setClickable(false);
				} else {
					sp_other_asset_owner.setClickable(true);
				}
				
				if(!data_list.getEquipment_Details_list().get(0).getUNIT_OF_MEASURE().equalsIgnoreCase("null") &&
					data_list.getEquipment_Details_list().get(0).getUNIT_OF_MEASURE()!=null){
					et_other_unit_measurement.setText(""+ data_list.getEquipment_Details_list().get(0).getUNIT_OF_MEASURE());						
				}else{
					et_other_unit_measurement.setText("");
				}
				} else if (data_list.getEquipment_Details_list().size() == 0) {
				sp_other_asset_make.setClickable(true);
				s = "A";

			} else {
			}

		}
	}

	public class AssignedTicketsTask2 extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd1;
		Context con;

		public AssignedTicketsTask2(Context con) {
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						17);
				nameValuePairs.add(new BasicNameValuePair("etssiteid",
						EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("siteid", SiteId));
				nameValuePairs.add(new BasicNameValuePair("equipmentId",
						EquipmentId));
				if (sp_other_asset_make.getSelectedItem().toString()
						.equalsIgnoreCase("Select Equipment Make")) {
					nameValuePairs.add(new BasicNameValuePair("equipmentMake",
							""));
				} else {
					nameValuePairs.add(new BasicNameValuePair("equipmentMake",
							populateValue.get(pos1)));
				}
				nameValuePairs.add(new BasicNameValuePair("equipmentValue",
						et_other_asset_measure.getText().toString()));
				dbHelper.open();
				String str = dbHelper.getAssetParamId("68",
						sp_other_asset_owner.getSelectedItem().toString());
				nameValuePairs.add(new BasicNameValuePair("owner", str));
				nameValuePairs.add(new BasicNameValuePair("uom",
						et_other_unit_measurement.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("imagePath",
						imageString));
				nameValuePairs.add(new BasicNameValuePair("lat", latitude));
				nameValuePairs.add(new BasicNameValuePair("longt", longitude));
				nameValuePairs.add(new BasicNameValuePair("status", Status));
				nameValuePairs.add(new BasicNameValuePair("wef", ""));
				nameValuePairs.add(new BasicNameValuePair("wet", ""));
				nameValuePairs.add(new BasicNameValuePair("oper", s));
				nameValuePairs.add(new BasicNameValuePair("user",
						mAppPreferences.getLoginId()));
				nameValuePairs.add(new BasicNameValuePair("assetid",
						EquipmentId));
				nameValuePairs.add(new BasicNameValuePair("txnID",
						mAppPreferences.getTxnId()));

				response_assigned_tickets1 = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+
						WebMethods.url_SaveEquipmentDetails, nameValuePairs);
				Gson gson = new Gson();
				data_list1 = gson.fromJson(response_assigned_tickets1,
						BeanAssetModuleList.class);
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
				if (!EquipmentDetails.this.isFinishing()) {
					if ((data_list1 == null)) {
					} else if (data_list1.getEquipment_Detail_list_Saved()
							.size() >= 0) {
						Toast.makeText(
								EquipmentDetails.this,
								""
										+ data_list1
												.getEquipment_Detail_list_Saved()
												.get(0).getMessage(),Toast.LENGTH_LONG)
								.show();
						if (data_list1.getEquipment_Detail_list_Saved().get(0).success
								.equalsIgnoreCase("true")) {
							finish();
						}
					} else {
					}
				}
			}
		}
	};

	protected void imageCapture() {
		String name = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ENGLISH)
				.format(new Date());
		destination = new File(Environment.getExternalStorageDirectory(), name
				+ System.currentTimeMillis() + ".jpg");
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
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	private int getCategoryPos(String category, ArrayList<String> list) {
		return list.indexOf(category);
	}

	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}
}
