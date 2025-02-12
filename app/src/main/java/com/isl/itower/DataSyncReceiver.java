package com.isl.itower;
/*Created By : Dhakan Lal Sharma
  Modified On : 16-June-2016
  Version     : 0.1
  CR          : iMaintan 1.9.1.1

  Modified By : Dhakan Lal Sharma
  Modified On : 6-Sept-2016
  Version     : 0.2
  Purpose     : CR#iETS 2.8.2.11

  Modified By : Dhakan Lal Sharma
  Modified On : 15-Dec-2016
  Version     : 0.3
  Purpose     : CR#iETS 2.8.2.13*/
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanAddTicket;
import com.isl.modal.ResponeDfrSave;
import com.isl.util.Utils;
import com.isl.modal.Response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataSyncReceiver extends BroadcastReceiver {
	Context context;
	//Cursor resultset;
	AppPreferences mAppPreferences;
	boolean isWifiConnected = false;
	boolean isMobileConnected = false;
	boolean isFirstTime = true;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		mAppPreferences = new AppPreferences(context);
		callSyncMethod();
	}

	private void callSyncMethod() {
		try {
			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkInfo != null) {
				isWifiConnected = networkInfo.isConnected();
			}
			networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (networkInfo != null){
				isMobileConnected = networkInfo.isConnected();
			}

			if ((mAppPreferences.getIsNetworkConnection()==1 && isMobileConnected)
					|| (mAppPreferences.getIsNetworkConnection()==1 && isWifiConnected)) {
				mAppPreferences.setIsNetworkConnection(0);
				mAppPreferences.setNetworkConnectionTime(Utils.dateNotification());
				System.out.println("ONN Recived call");
				System.out.println("ONN Recived call"+isFirstTime);
				if (Utils.isNetworkAvailable(context) && mAppPreferences.getLoginState()==1){
					if(isFirstTime) {
						isFirstTime =false;
						postSubmitSync(context);
					}

				}
			}else {
				mAppPreferences.setIsNetworkConnection(1);
			}
		} catch (Exception e) {
			isFirstTime=true;
		}


	}


	public void postSubmitSync(Context context) {
		try {
			AppPreferences mAppPreferences = new AppPreferences(context);

			if (Utils.isNetworkAvailable(context)) {
				final String url = mAppPreferences.getConfigIP()+ WebMethods.url_SaveAPI;
				DataBaseHelper dbHelper = new DataBaseHelper(context);
				dbHelper.open();
				Cursor resultset = dbHelper.getLocalTranData(mAppPreferences.getUserId());
				Cursor cur = dbHelper.getChkImages();

				if (cur != null) {
					if (cur.getCount() > 0) {
						while (cur.moveToNext()) {
							try {
								final JSONObject obj = new JSONObject();
								obj.put("chkID", cur.getString(cur.getColumnIndex("CHECKLIST_ID")));
								obj.put("scheduledDate", cur.getString(cur.getColumnIndex("SCHEDULE_DATE")));
								obj.put("activityTypeId", cur.getString(cur.getColumnIndex("ACTIVITY_TYPE_ID")));
								obj.put("sid", cur.getString(cur.getColumnIndex("SITE_ID")));
								obj.put("dgType", cur.getString(cur.getColumnIndex("DG_TYPE")));
								obj.put("imgname", cur.getString(cur.getColumnIndex("IMG_NAME")));
								obj.put("type", cur.getString(cur.getColumnIndex("IMAGE_TYPE")));
								obj.put(AppConstants.UPLOAD_TYPE, "PM");

								JSONObject chkImgjsonObj = new JSONObject();
								chkImgjsonObj.put("path", cur.getString(cur.getColumnIndex("IMAGE_PATH")));
								final JSONArray jsonArrChkImg = new JSONArray();
								jsonArrChkImg.put(chkImgjsonObj);
								AsynTaskUploadIMG task = new AsynTaskUploadIMG(obj.toString(), jsonArrChkImg.toString());
								task.execute(cur.getString(cur.getColumnIndex("IP_ADDRESS")));
							} catch (JSONException e) {
							}

						}
					}
				}
				if (resultset != null) {
					//if (Utils.isNetworkAvailable(context)) {
						while (resultset.moveToNext()) {
							//String s = resultset.getString(resultset.getColumnIndex("IP_ADDRESS"));
							dbHelper.open();
							dbHelper.deleteLocalTxnData(resultset.getString(resultset.getColumnIndex("TXN_ID")));
							dbHelper.close();
						/*DataBaseHelper db = new DataBaseHelper(context);
						db.open();
						db.insertDummyTxnDataLocally(resultset.getString(resultset.getColumnIndex("TXN_ID")),
								resultset.getString(resultset.getColumnIndex("FLAG")),
								resultset.getString(resultset.getColumnIndex("USER_ID")),
								resultset.getString(resultset.getColumnIndex("DATE")),
								resultset.getString(resultset.getColumnIndex("PRE_IMG")),
								resultset.getString(resultset.getColumnIndex("POST_IMG")),
								resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
								resultset.getString(resultset.getColumnIndex("IP_ADDRESS")));
						db.close();*/

							if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("1")) {
								//Add Ticket
								AsynTaskService task = new AsynTaskService(0, context, "TT",
										resultset.getString(resultset.getColumnIndex("TXN_ID")),
										resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
										resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA")),
										"",
										"", "1");
								task.execute(resultset.getString(resultset.getColumnIndex("IP_ADDRESS")));
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("2")) {
								//PM Done
								final String s = resultset.getString(resultset.getColumnIndex("IP_ADDRESS"));
								final AsynTaskService task = new AsynTaskService(1, context, "PM",
										resultset.getString(resultset.getColumnIndex("TXN_ID")),
										resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
										resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA")),
										resultset.getString(resultset.getColumnIndex("PRE_IMG")),
										resultset.getString(resultset.getColumnIndex("POST_IMG")), "2");
								final Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										task.execute(s);
										//task.execute(resultset.getString(resultset.getColumnIndex("IP_ADDRESS")));
									}
								}, 10000);
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("5")) {
								//PM Review
								AsynTaskService task = new AsynTaskService(1, context, "PM",
										resultset.getString(resultset.getColumnIndex("TXN_ID")),
										resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
										resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA")), "", "", "2");
								task.execute(url);
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("FF")) {
								AsynTaskFuelPurchase task = new AsynTaskFuelPurchase(context,
										resultset.getString(resultset.getColumnIndex("FLAG")),
										resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
										resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA")),
										resultset.getString(resultset.getColumnIndex("PRE_IMG")),
										resultset.getString(resultset.getColumnIndex("POST_IMG")),
										resultset.getString(resultset.getColumnIndex("IP_ADDRESS")));
								task.execute(url);
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("FP")) {
								AsynTaskFuelPurchase task = new AsynTaskFuelPurchase(context,
										resultset.getString(resultset.getColumnIndex("FLAG")),
										resultset.getString(resultset.getColumnIndex("ALL_IMGS")),
										resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA")),
										resultset.getString(resultset.getColumnIndex("PRE_IMG")),
										resultset.getString(resultset.getColumnIndex("POST_IMG")),
										"A");
								task.execute(url);
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("SPARE")) {
								String bgData = resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA"));
								String urll = mAppPreferences.getConfigIP() + WebMethods.url_save_spare_part;
								SparePartApprovalTask task = new SparePartApprovalTask(context, urll, bgData);
								task.execute();
							} else if (resultset.getString(resultset.getColumnIndex("FLAG")).equalsIgnoreCase("DFR")) {
								String bgData = resultset.getString(resultset.getColumnIndex("ACTIVITY_DATA"));
								String urll = mAppPreferences.getConfigIP() + WebMethods.url_dfr_SaveDFRApproval;
								SaveDFRApproval task = new SaveDFRApproval(context, bgData, urll);
								task.execute();
							}
						}
					//}
				}
			}

			if(!isFirstTime) {
				isFirstTime=true;
			}
		} catch (Exception e) {
			isFirstTime=true;
		}
	}

	/*public void submitPMBackgroung(String txnId,String urls,String data,String allImgs,String preInfo,String postInfo,String userId){
		AsynTaskPM task = new AsynTaskPM(txnId,data,allImgs,preInfo,postInfo,userId);
		task.execute(urls);
	}*/

	public class AsynTaskService extends AsyncTask<String, Void, String> {

		Context context;
		String txnId,data, module, preImgInfo, postImgInfo, flag, res = "";
		String allImgs;
		Response response;
		int a;
		public AsynTaskService(int a,Context context, String module,String txnId, String allImgs,
							   String data, String preImgInfo, String postImgInfo, String flag) {

			this.context = context;
			this.module = module;
			this.allImgs = allImgs;
			this.data = data;
			this.preImgInfo = preImgInfo;
			this.postImgInfo = postImgInfo;
			this.flag = flag;
			this.txnId = txnId;
			this.a = a;
		}

		protected void onPreExecute() {

		}

		protected String doInBackground(String... urls) {
			try {
				res = Utils.httpMultipartBackground(urls[0], module, allImgs, data, preImgInfo, postImgInfo,
						"", "", "", "", "", "", "");
				Gson gson = new Gson();
				res = res.replace("[", "").replace("]", "");
				response = gson.fromJson(res, Response.class);
			} catch (Exception e) {
				isFirstTime =true;
				response = null;
			}
			return null;
		}

		public void onPostExecute(String result) {
			DataBaseHelper dbHelper = new DataBaseHelper(context);
			AppPreferences mAppPreferences = new AppPreferences(context);
			dbHelper.open();
			if (response != null) {
				dbHelper.deleteLocalTxnData(txnId);
				dbHelper.deleteActivity(data);
				if (response.getSuccess().equals("true")) {
					//Delete Auto Saved data
					//dbHelper.deleteLocalTxnData(txnId);
					//dbHelper.deleteLocalDummyTxnData(txnId);
					if(a==1){
						dbHelper.deleteAutoSaveChk(txnId,"");
					}
				} else {
					dbHelper.insertRejectTxnDataLocally(txnId,flag, data, mAppPreferences.getUserId(), response.getMessage());
				}
				isFirstTime =true;

			} else {

			}
			dbHelper.close();
		}
	}

	public class AsynTaskFuelPurchase extends AsyncTask<String, Void, String> {
		Context context;
		String data,module,res="",addParm="",formTask;
		String allImgs;
		Response response;
		String oper;
		public AsynTaskFuelPurchase(Context context, String module, String allImgs,
									String data, String addParm, String formTask, String oper) {
			this.context = context;
			this.module=module;
			this.allImgs=allImgs;
			this.data=data;
			this.addParm=addParm;
			this.formTask=formTask;
			this.oper=oper;
		}
		protected void onPreExecute() {
		}
		protected String doInBackground(String... urls) {
			try {
				res = Utils.httpMultipartBackground(urls[0],module,allImgs,data,"","",
						addParm,"M",mAppPreferences.getUserId(),formTask,mAppPreferences.getLanCode(),oper,"");
				Gson gson = new Gson();
				res = res.replace("[", "").replace("]", "");
				response = gson.fromJson(res,Response.class);
			} catch (Exception e) {
				response = null;
			}
			return null;
		}

		public void onPostExecute(String result) {
			DataBaseHelper dbHelper = new DataBaseHelper(context);
			AppPreferences mAppPreferences = new AppPreferences(context);
			dbHelper.open();
			if (response != null) {
				Utils.toastMsg(context,response.getMessage());
				dbHelper.deleteActivity(data);
				if (response.getSuccess().equals("true")) {
				} else {
					dbHelper.rejectLocalTxnData(module,data,mAppPreferences.getUserId(),response.getMessage());
				}
			}else {
			}
			dbHelper.close();
		}
	}

	/*public class AsynTaskPM extends AsyncTask<String, Void, String> {

		public String res="",data,allImgs,preInfo,postInfo,userId,txnId;
		Response response;
		Context context;

		public AsynTaskPM(String txnId,String data,String allImgs,String preInfo,String postInfo,String userId) {

			this.txnId=txnId;
			this.data=data;
			this.allImgs=allImgs;
			this.preInfo=preInfo;
			this.postInfo=postInfo;
			this.userId=userId;
			this.context = MyApp.getAppContext();
		}

		protected void onPreExecute() {
		}

		protected String doInBackground(String... urls) {
			try {
				res = WorkFlowUtils.httpMultipartBackground(urls[0],"PM",allImgs,data,preInfo,postInfo,"","","","","","","");
				res = res.replace("[", "").replace("]", "");
				Gson gson = new Gson();
				response = gson.fromJson(res,Response.class);
			} catch (Exception e) {
				response = null;
			}
			return null;
		}

		public void onPostExecute(String result) {
			if (response != null) {
				Toast.makeText(context,response.getMessage(),Toast.LENGTH_LONG  ).show();
				if (response.getSuccess().equals("true")) {
				}else{
					DataBaseHelper db = new DataBaseHelper(context);
					db.open();
					db.insertRejectTxnDataLocally(txnId,"2",data,userId,response.getMessage());
					db.close();
				}
			}else {
				DataBaseHelper db = new DataBaseHelper(context);
				db.open();
				db.insertTxnDataLocally(txnId, "2", data, allImgs, preInfo,postInfo,userId);
				db.close();
			}
		}
	}*/

	public class AsynTaskUploadIMG extends AsyncTask<String, Void, String> {
		public String res="",data,chkImages;
		Context context;

		public AsynTaskUploadIMG(String data,String chkImages) {
			this.data=data;
			this.chkImages = chkImages;
			this.context = MyApp.getAppContext();
		}

		protected void onPreExecute() {
		}

		protected String doInBackground(String... urls) {
			try {
				res = Utils.httpMultipartBackground(urls[0],"IMG",chkImages,data,"","",
						"","","","","","","");
				res = res.replace("[", "").replace("]", "");
			} catch (Exception e) {
				res = null;
			}
			return null;
		}

		public void onPostExecute(String result) {
			if (res != null) {
				if(res.contains("success") && res.contains("data")){
					try {
						JSONObject reader = new JSONObject(res);
						String success = reader.getString("success");
						String data = reader.getString( "data" );
						if(success.equalsIgnoreCase("S")) {
							JSONObject reader1 = new JSONObject( data );
							DataBaseHelper dbImg = new DataBaseHelper(context);
							dbImg.open();
							dbImg.updateImages(reader1.getString("sid"),reader1.getString("activityTypeId"),
									reader1.getString("chkID"),reader1.getString("scheduledDate"),
									reader1.getString("type"),reader1.getString("dgType"),
									reader1.getString("imgname"));
							dbImg.close();
						}
					}catch (JSONException e) {
					}
				}
			}else {
			}
		}
	}

	public class SparePartApprovalTask extends AsyncTask<Void, Void, Void> {
		Context con;
		String response,url,data="";
		BeanAddTicket ticket_data;
		public SparePartApprovalTask(Context con,String url,String data) {
			this.con = con;
			this.url = url;
			this.data = data;
			Gson gson = new Gson();
			ticket_data = gson.fromJson(data, BeanAddTicket.class);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("txnID",ticket_data.getTxnID()));
				nameValuePairs.add(new BasicNameValuePair("status",ticket_data.getStatus()));
				nameValuePairs.add(new BasicNameValuePair("spareID",ticket_data.getSpareID()));
				nameValuePairs.add(new BasicNameValuePair("approveQty",ticket_data.getQty()));
				nameValuePairs.add(new BasicNameValuePair("tranType",ticket_data.getTranType()));
				nameValuePairs.add(new BasicNameValuePair("userID",ticket_data.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("remarks",ticket_data.getRemarks()));
				response = Utils.httpPostRequest(con,url, nameValuePairs);
			} catch (Exception e) {
				e.printStackTrace();
				response = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (response == null) {

			} else if (response!=null) {

				try {
					DataBaseHelper db = new DataBaseHelper(context);
					db.open();
					JSONObject reader = new JSONObject( response );
					JSONArray objArray = reader.getJSONArray("GetSvApprovalSparePart");
					String message = objArray.getJSONObject(0).getString("errorDesc");
					String flag = objArray.getJSONObject(0).getString("respFlag");
					if(flag.equalsIgnoreCase("s"))
					{
					}else
					{
						db.rejectLocalTxnData("SPARE",data,ticket_data.getUserId(),message);
					}
					db.deleteActivity(data);
					db.close();
				}catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
			}
			super.onPostExecute(result);
		}
	}

	public class SaveDFRApproval extends AsyncTask<Void, Void, Void> {
		Context con;
		String response;
		String data="",url;
		ResponeDfrSave saveResponse;
		BeanAddTicket ticket_data;
		public SaveDFRApproval(Context con,String data,String url) {
			this.con = con;
			this.data = data;
			this.url = url;
			Gson gson = new Gson();
			ticket_data = gson.fromJson(data, BeanAddTicket.class);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
				nameValuePairs.add(new BasicNameValuePair("countryID",ticket_data.getCountryID()));
				nameValuePairs.add(new BasicNameValuePair("hubID",ticket_data.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("regionID",ticket_data.getRegionID()));
				nameValuePairs.add(new BasicNameValuePair("circleID",ticket_data.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneID",ticket_data.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterID",ticket_data.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("omeID",ticket_data.getOmeID()));
				nameValuePairs.add(new BasicNameValuePair("flag","2"));
				nameValuePairs.add(new BasicNameValuePair("userName",ticket_data.getUserId()));
				nameValuePairs.add(new BasicNameValuePair("xmlData",ticket_data.getXmlData()));
				response = Utils.httpPostRequest(con,url,nameValuePairs);
				Gson gson = new Gson();
				saveResponse = gson.fromJson(response,ResponeDfrSave.class);
			} catch (Exception e) {
				e.printStackTrace();
				saveResponse = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (saveResponse == null) {
			} else if (saveResponse.getSaveDFRApproval()!=null && saveResponse.getSaveDFRApproval().size() > 0) {
				DataBaseHelper db = new DataBaseHelper(context);
				db.open();
				if(saveResponse.getSaveDFRApproval().get(0).getResponse()!=null){
					db.rejectLocalTxnData("DFR",data,ticket_data.getUserId(),
							saveResponse.getSaveDFRApproval().get(0).getResponse());
				}
				db.deleteActivity(data);
				db.close();
			} else {

			}
			super.onPostExecute(result);
		}
	}

}
