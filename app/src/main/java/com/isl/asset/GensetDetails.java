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
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class GensetDetails extends Activity {
	GPSTracker gps;
	String SiteId,EtsSiteId,DgType,DgCapacity,imageString,DgTankCapacity,DgMake,latitude = "", longitude = "";
	ArrayList<String> DgTypeList,DgMakeList,DgMakeId,tank_list;
	EditText et_site_id,dg_capacity,dg_tank_capacity;
	Spinner sp_dgtype,sp_dgmake;
	Button previous,next;
	String response_assigned_tickets;
	BeanAssetModuleList data_list;
	BeanAssetModuleList data_list2;
	BeanAssetModuleList data_list3;
	BeanAssetModuleList data_list1;
	Button send,take_photo;
	int a=0,pos1,pos2;
	AppPreferences mAppPreferences;
	ProgressDialog pd;
	ImageView iv_photo;
	File destination;
	String filePath = "",WEF,WET;
	LinearLayout NPBackground;
	String z="",temp_dg_id;
	TextView tv_add_new_dg;
	DataBaseHelper dbHelper=new DataBaseHelper(GensetDetails.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		z=i.getStringExtra("noagaincallmode");
		mAppPreferences = new AppPreferences(GensetDetails.this);
		SiteId=mAppPreferences.getSiteID();
		EtsSiteId=mAppPreferences.getEtsSiteID();
		setContentView(R.layout.dgdetails);
		tank_list= new ArrayList<String>();
		tv_add_new_dg=(TextView)findViewById(R.id.tv_add_new_dg);
		tv_add_new_dg.setPaintFlags(tv_add_new_dg.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		tv_add_new_dg.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		Intent i=new Intent(GensetDetails.this,AddNewGensetDetails.class);
		i.putExtra("DgTypeList", DgTypeList);
		i.putExtra("DgMakeList", DgMakeList);
		i.putExtra("DgMakeId", DgMakeId);
		startActivity(i);
		finish();
		}
		});
		take_photo = (Button) findViewById(R.id.btn_take_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		take_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageCapture();
			}
		});
		gps = new GPSTracker(GensetDetails.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
		} else {
			gps.showSettingsAlert();
		}
		
		DgTypeList=new ArrayList<String>();
		DgMakeList=new ArrayList<String>();
		DgMakeId=new ArrayList<String>();
		String[] PassiveAssets=mAppPreferences.getSiteAuditRights().split("~");
		RelativeLayout RL_Send=(RelativeLayout) findViewById(R.id.rl_send);
		if(PassiveAssets.length==1){
			if(mAppPreferences.getSiteAuditRights().equalsIgnoreCase("V")){
				RL_Send.setVisibility(View.GONE);
				tv_add_new_dg.setVisibility(View.GONE);
			}
		}else{
				if(PassiveAssets[0].equalsIgnoreCase("M")){
					if(PassiveAssets[1].equalsIgnoreCase("V")){
						RL_Send.setVisibility(View.VISIBLE);
						tv_add_new_dg.setVisibility(View.VISIBLE);
						tv_add_new_dg.setPaintFlags(tv_add_new_dg.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
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
			//By Dheeraj
            if (sp_dgmake.getSelectedItem().toString().equalsIgnoreCase("Select Genset Make")) {
				Toast.makeText(GensetDetails.this,"Select Genset Make", Toast.LENGTH_SHORT).show();
		    }else if (dg_capacity.getText().toString().length()==0) {
				Toast.makeText(GensetDetails.this,"Enter Genset Capacity", Toast.LENGTH_SHORT).show();
		    }else if (dg_tank_capacity.getText().toString().length()==0) {
				Toast.makeText(GensetDetails.this,"Enter Genset Tank Capacity", Toast.LENGTH_SHORT).show();
		    }else if (Utils.isNetworkAvailable(GensetDetails.this)) {
				new AssignedTicketsTask2(GensetDetails.this).execute();
			} else {
				Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
			}					
		}
		});
		NPBackground=(LinearLayout)findViewById(R.id.NPBackground);
		et_site_id=(EditText)findViewById(R.id.et_site_id);
		et_site_id.setText(""+SiteId);
		et_site_id=(EditText)findViewById(R.id.et_site_id);
		dg_capacity=(EditText)findViewById(R.id.dg_capacity);
		dg_tank_capacity=(EditText)findViewById(R.id.dg_tank_capacity);
		sp_dgtype=(Spinner)findViewById(R.id.sp_dgtype);
		sp_dgmake=(Spinner)findViewById(R.id.sp_dgmake);
		next=(Button) findViewById(R.id.next);
		previous=(Button) findViewById(R.id.previous);
		Button iv_back = (Button) findViewById(R.id.button_back);
		iv_back.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		finish();
		}
	    });
		if (Utils.isNetworkAvailable(GensetDetails.this)) {
			new DGType(GensetDetails.this).execute();
		} else {
			Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}	
		
		next.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
	    if(a<data_list.getDG_Details_list().size()-1){
			 a++;
			 dg_capacity.setText(""+data_list.getDG_Details_list().get(a).getDG_CAPACITY());
			 dg_tank_capacity.setText(""+data_list.getDG_Details_list().get(a).getDG_TANK_CAPACITY());
			 WEF=data_list.getDG_Details_list().get(a).getWEF_DATE();
			 WET=data_list.getDG_Details_list().get(a).getWET_DATE();
			    pos1=getCategoryPos(data_list.getDG_Details_list().get(a).getDG_TYPE(),DgTypeList);
				pos2=getCategoryPos(data_list.getDG_Details_list().get(a).getMAKE_DESC(),DgMakeList);
				if(pos1<=0){
					pos1=0;	
					}if(pos2<=0){
						pos2=0;	
				}
					sp_dgtype.setSelection(pos1);
					sp_dgmake.setSelection(pos2);
					previous.setBackgroundResource(R.drawable.input_box );
					previous.setEnabled(true);
		 }else{
			Toast.makeText(GensetDetails.this," No More Record Found",Toast.LENGTH_LONG).show(); 
			 next.setBackgroundResource(R.drawable.input_box );
			 next.setEnabled(false);
		 }
		 }
		});	
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			 if(a<data_list.getDG_Details_list().size()){
				 if(a>0){
					 a--;
					 dg_capacity.setText(""+data_list.getDG_Details_list().get(a).getDG_CAPACITY());
					 dg_tank_capacity.setText(""+data_list.getDG_Details_list().get(a).getDG_TANK_CAPACITY()); 
					 WEF=data_list.getDG_Details_list().get(a).getWEF_DATE();
					 WET=data_list.getDG_Details_list().get(a).getWET_DATE();
					    pos1=getCategoryPos(data_list.getDG_Details_list().get(a).getDG_TYPE(),DgTypeList);
						pos2=getCategoryPos(data_list.getDG_Details_list().get(a).getMAKE_DESC(),DgMakeList);
						if(pos1<=0){
							pos1=0;	
							}if(pos2<=0){
								pos2=0;	
						}
							sp_dgtype.setSelection(pos1);
							sp_dgmake.setSelection(pos2);
							next.setBackgroundResource(R.drawable.input_box );
							next.setEnabled(true);
				 }	else{
					 Toast.makeText(GensetDetails.this, "First Record",Toast.LENGTH_LONG).show();
					 previous.setBackgroundResource(R.drawable.input_box );
					 previous.setEnabled(false);
				 }
		 }
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
		finish();
	}
	
	public class DGType extends AsyncTask<Void, Void, Void> {
		Context con;
		public DGType(Context con) {
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
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_PopulateDGNo,nameValuePairs);
				Gson gson = new Gson();
				data_list2 = gson.fromJson(response,BeanAssetModuleList.class);
				} catch (Exception e) {
				e.printStackTrace();
				data_list2 = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//DgTypeList.add("Select Genset No");
			Message myMessage = new Message();
			myMessage.obj = "ApprovedList2";
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);
		}
	}
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj.toString().equalsIgnoreCase("ApprovedList2")) {
				if (!GensetDetails.this.isFinishing()) {
					if ((data_list2 == null)) {
						if (Utils.isNetworkAvailable(GensetDetails.this)) {
							new AssetOwner(GensetDetails.this).execute();
						} else {
							Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
						}	
					} else if (data_list2.getDG_NO_list().size() > 0) {
						for (int i = 0; i < data_list2.getDG_NO_list().size(); i++) {
						DgTypeList.add(data_list2.getDG_NO_list().get(i).getDG_TYPE());	
						}
						//addItemsOnSpinner(sp_dgtype,DgTypeList);
						DataBaseHelper db = new DataBaseHelper(GensetDetails.this);
						db.open();
						tank_list.addAll(db.getEnergyDgDesc());
						addItemsOnSpinner(sp_dgtype, tank_list);
						if (Utils.isNetworkAvailable(GensetDetails.this)) {
							new AssetOwner(GensetDetails.this).execute();
						} else {
							Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
						}	
					} else {
						if (Utils.isNetworkAvailable(GensetDetails.this)) {
							new AssetOwner(GensetDetails.this).execute();
						} else {
							Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
						}					
					}
				}
			}
		}
	};
	
	
	public class AssetOwner extends AsyncTask<Void, Void, Void> {
		Context con;
		public AssetOwner(Context con) {
			this.con = con;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_PopulateDGMake,nameValuePairs);
				Gson gson = new Gson();
				data_list3 = gson.fromJson(response,BeanAssetModuleList.class);
				
			} catch (Exception e) {
				e.printStackTrace();
				data_list3 = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			DgMakeList.add("Select Genset Make");
			DgMakeId.add("0");
			Message myMessage = new Message();
			myMessage.obj = "ApprovedList3";
			myHandler3.sendMessage(myMessage);
			super.onPostExecute(result);
		}
	}
	private Handler myHandler3 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj.toString().equalsIgnoreCase("ApprovedList3")) {
				if (!GensetDetails.this.isFinishing()) {
					if ((data_list3 == null)) {
						
					if (Utils.isNetworkAvailable(GensetDetails.this)) {
						new AssignedTicketsTask1(GensetDetails.this).execute();
					} else {
						Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
					}	
						
					} else if (data_list3.getDG_Make_list().size() > 0) {
						for (int i = 0; i < data_list3.getDG_Make_list().size(); i++) {
							DgMakeList.add(data_list3.getDG_Make_list().get(i).getMAKE_DESC());	
							DgMakeId.add(data_list3.getDG_Make_list().get(i).getMAKE_ID());	
						}
						addItemsOnSpinner(sp_dgmake,DgMakeList);
					if (Utils.isNetworkAvailable(GensetDetails.this)) {
						new AssignedTicketsTask1(GensetDetails.this).execute();
					} else {
						Toast.makeText(GensetDetails.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
					}	
						
					} else {
										
					}
				}
			}
		}
	};
	
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("ETSSiteID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("SiteID",SiteId));
				nameValuePairs.add(new BasicNameValuePair("DGType",""));
				nameValuePairs.add(new BasicNameValuePair("Date",""));
				response_assigned_tickets = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetDGDetail,nameValuePairs);
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
			Message myMessage = new Message();
			myMessage.obj = "ApprovedList1";
			myHandler1.sendMessage(myMessage);
			super.onPostExecute(result);
		}
	}

	private Handler myHandler1 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj.toString().equalsIgnoreCase("ApprovedList1")) {
				if (!GensetDetails.this.isFinishing()) {
					if ((data_list == null)) {
						} else if (data_list.getDG_Details_list().size() > 0) {
						dg_capacity.setText(""+data_list.getDG_Details_list().get(0).getDG_CAPACITY());
						dg_tank_capacity.setText(""+data_list.getDG_Details_list().get(0).getDG_TANK_CAPACITY());
						WEF=data_list.getDG_Details_list().get(0).getWEF_DATE();
						WET=data_list.getDG_Details_list().get(0).getWET_DATE();
						pos1=getCategoryPos(data_list.getDG_Details_list().get(0).getDG_TYPE(),DgTypeList);
						pos2=getCategoryPos(data_list.getDG_Details_list().get(0).getMAKE_DESC(),DgMakeList);
						if(data_list.getDG_Details_list().get(0).getDG_TYPE()!=null && !data_list.getDG_Details_list().get(0).getDG_TYPE().isEmpty()){
							sp_dgtype.setClickable(false);
						}else{
							sp_dgtype.setClickable(true);
						}
                        if(data_list.getDG_Details_list().get(0).getMAKE_DESC()!=null && !data_list.getDG_Details_list().get(0).getMAKE_DESC().isEmpty()){
                        	sp_dgmake.setClickable(false);
						}else{
							sp_dgmake.setClickable(true);
						}
						if(pos1<=0){
							pos1=0;	
							}if(pos2<=0){
								pos2=0;	
						}
							sp_dgtype.setSelection(pos1);
							sp_dgmake.setSelection(pos2);
							if(data_list.getDG_Details_list().size()==1){
								NPBackground.setVisibility(View.GONE);	
							}else{
								NPBackground.setVisibility(View.VISIBLE);
							}
						}else if (data_list.getDG_Details_list().size()==0) {
							if(z.equalsIgnoreCase("no")){
								finish();
							}else{
								Intent i=new Intent(GensetDetails.this,AddNewGensetDetails.class);
								i.putExtra("DgTypeList", DgTypeList);
								i.putExtra("DgMakeList", DgMakeList);
								i.putExtra("DgMakeId", DgMakeId);
								startActivity(i);
								finish();
							}
							
					  }else {
					}
				}
			}
		}
	};
	
	
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("ETS_SITE_ID",EtsSiteId));
				nameValuePairs.add(new BasicNameValuePair("SITE_ID",SiteId));
				nameValuePairs.add(new BasicNameValuePair("DG_TYPE",temp_dg_id));
				nameValuePairs.add(new BasicNameValuePair("DG_CAPACITY",DgCapacity));
				nameValuePairs.add(new BasicNameValuePair("DG_TANK_CAPACITY",DgTankCapacity));
				nameValuePairs.add(new BasicNameValuePair("DG_MAKE",DgMakeId.get(pos2)));
				nameValuePairs.add(new BasicNameValuePair("WEF_DATE",WEF));
				nameValuePairs.add(new BasicNameValuePair("WET_DATE",WET));
				nameValuePairs.add(new BasicNameValuePair("OPER","E"));
				nameValuePairs.add(new BasicNameValuePair("USER",mAppPreferences.getLoginId()));
				nameValuePairs.add(new BasicNameValuePair("imagePath",imageString));
				nameValuePairs.add(new BasicNameValuePair("lat",latitude));
				nameValuePairs.add(new BasicNameValuePair("longt",longitude));
				nameValuePairs.add(new BasicNameValuePair("txnID",mAppPreferences.getTxnId()));
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_SaveDGDetails,nameValuePairs);
				Gson gson = new Gson();
				data_list1 = gson.fromJson(response,BeanAssetModuleList.class);
				System.out.println("+++++adg Details++++"+temp_dg_id);
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
				if (!GensetDetails.this.isFinishing()) {
					if ((data_list1 == null)) {
					 } else if (data_list1.getDG_Detail_list_Saved().size() > 0) {
						Toast.makeText(GensetDetails.this, ""+data_list1.getDG_Detail_list_Saved().get(0).getMessage(),Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(GensetDetails.this, ""+data_list1.getDG_Detail_list_Saved().get(0).getMessage(),Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};
	
	protected void imageCapture() {
		String name = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ENGLISH).format(new Date());
		destination = new File(Environment.getExternalStorageDirectory(), name + System.currentTimeMillis() + ".jpg");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Add the captured image in the path
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
		// Start result method - Method handles the output
		// of the camera activity
		startActivityForResult(intent, 2);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
			try {
				// imageList.add(Uri.fromFile(destination));
				filePath = destination.getAbsolutePath();
				System.out.println("file path::" + filePath);
				Bitmap bm = decodeFile(destination);
				// create output stream
				OutputStream outputStream = null;
				// create file
				try {
					outputStream = new FileOutputStream(destination);
					bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
					iv_photo.setImageBitmap(bm);
					iv_photo.setVisibility(View.VISIBLE);
					// picture.recycle();
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
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 200;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE	&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;
			// Decode with inSampleSize
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
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}
	
}
