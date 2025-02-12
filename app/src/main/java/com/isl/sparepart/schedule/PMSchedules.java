package com.isl.sparepart.schedule;
import infozech.itower.R;
import com.isl.constant.WebMethods;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.modal.BeanSiteList;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.Operator;
import com.isl.preventive.PMChecklistApproval;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;
import com.isl.preventive.PMChecklist;
import com.isl.preventive.ViewPMCheckList;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PMSchedules extends Activity {
	ListView lv_schedule;
	BeanSiteList response_site_list;
	String type;
	AppPreferences mAppPreferences;
	TextView txt_no_schedule;
	DataBaseHelper db;
	Spinner sp_search_by;
	EditText et_searchby_site, et_sch_date,et_txn_id;
	Calendar myCalendar = Calendar.getInstance();
	AdapterSchedule adapterrr = null;
	TextView tv_operator, tv_search_by_logo;
	Dialog actvity_dialog;
	ArrayList<String> list_operator_name, list_operator_id;
	ArrayList<Operator> operatorList;
	StringBuffer All_Operator_Id;
	MyCustomAdapter dataAdapter = null;
	int optCounter = 0;
	String moduleUrl = "",url="";
	String oper = "S",pm_schecdule="V",pm_missed="V",pm_pending="V",pm_resubmit = "V";
	ArrayList<String> searchDDLlist;
	SwipeRefreshLayout pullToRefresh;
	JSONObject savedDataJsonObjRemarks = null;
	JSONObject savedDataJsonObj = null;
	int temp_flag=0;
	private NetworkManager networkManager;//108
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_site_list);
		networkManager = new NetworkManager();//108
		searchDDLlist = new ArrayList<String>();
		searchDDLlist.add("Site Id");
		searchDDLlist.add("Activity Type");
		tv_search_by_logo = (TextView) findViewById(R.id.tv_search_by_logo);
		Utils.msgText(PMSchedules.this, "296", tv_search_by_logo);
		db = new DataBaseHelper(this);
		db.open();
		moduleUrl = db.getModuleIP("Schedule");
		pm_schecdule = db.getSubMenuRight("PMScheduled", "Schedule");
		pm_missed = db.getSubMenuRight("PMMissed", "Schedule");
		pm_pending = db.getSubMenuRight("PMPending", "Schedule");
		pm_resubmit = db.getSubMenuRight("PMReSubmit", "Schedule");
		//db.close();
		mAppPreferences = new AppPreferences(PMSchedules.this);
		url=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
		et_txn_id = (EditText) findViewById(R.id.et_txn_id);
		TextView title = (TextView) findViewById(R.id.tv_brand_logo);
		txt_no_schedule = (TextView) findViewById(R.id.txt_no_ticket);
		title.setTypeface(Utils.typeFace(PMSchedules.this));
		type = getIntent().getExtras().getString("type");
		String hdTextId = getIntent().getExtras().getString("hdTextId");
		title.setText(Utils.msg(PMSchedules.this, hdTextId));
		Button bt_back = (Button) findViewById(R.id.button_back);
		Utils.msgButton(PMSchedules.this, "71", bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		if(type.equals("NEXT_7_DAYS")){
			oper = "S";
			searchDDLlist.add("Schedule Date");
		}else if (type.equals("TILL_YESTERDAY")){
			oper = "M";
			searchDDLlist.add("Schedule Date");
		}else if (type.equals("TILL_TODAY")){
			oper = "D";
			searchDDLlist.add("Done Date");
		}else if (type.equals("APPROVAL_PENDING")){
			oper = "P";
			searchDDLlist.add("Done Date");
		}else if (type.equals("TILL_TODAY_APPROVED")){
			oper = "V";
			searchDDLlist.add("Verify Date");
		}else if (type.equals("REJECTED")){    //need to J
			oper = "R";
			searchDDLlist.add("Rejected Date");
		}else if (type.equals("RESUBMITTED")){
			oper = "RS";
			searchDDLlist.add("Schedule Date");
		}
		searchDDLlist.add("Activity ID");
		tv_operator = (TextView) findViewById(R.id.tv_operator);
		tv_operator.setBackgroundResource(R.drawable.input_box );
		tv_operator.setTypeface(Utils.typeFace(PMSchedules.this));
		tv_operator.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ActivityPopup();
			}
		});
		lv_schedule = (ListView) findViewById(R.id.lv_sites);
		pullToRefresh = findViewById(R.id.pullToRefresh);
		db.close();

		pullToRefresh.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
			public void onRefresh() {
				if (Utils.isNetworkAvailable( PMSchedules.this )) {
					//108
					networkManager.getToken(new NetworkManager.TokenCallback() {
						@Override
						public void onTokenReceived(String token) {
							new GetSchedules( PMSchedules.this,token).execute();
						}

						@Override
						public void onTokenError(String error) {
							Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
						}
					});
					//108
				//	new GetSchedules( PMSchedules.this ).execute();
					searchFunction();
				} else {
					Toast.makeText( PMSchedules.this, "No internet connection,Try again.", Toast.LENGTH_SHORT ).show();
				}
				pullToRefresh.setRefreshing( false );
			}
		} );


		lv_schedule.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				int alreadyJson = 0;
				mAppPreferences.setPMchecklist("");
				mAppPreferences.setPMchecklistBackUp("");
				if(pm_schecdule.contains("A") && type.equals("NEXT_7_DAYS")){
					DataBaseHelper db = new DataBaseHelper(PMSchedules.this);
					db.open();
					if(db.isAlreadyAutoSaveChk(response_site_list.getSite_list().get(arg2).getTXN_ID(),"0")==0){
						db.insertAutoSaveChkList(response_site_list.getSite_list().get(arg2).getTXN_ID(),
								"","","","0");
						alreadyJson = 1;
					}
					activity(arg2,alreadyJson);

				}else if(pm_resubmit.contains("A") && type.equals("RESUBMITTED")){
					DataBaseHelper db = new DataBaseHelper(PMSchedules.this);
					db.open();
					if(db.isAlreadyAutoSaveChk(response_site_list.getSite_list().get(arg2).getTXN_ID(),"0")==0){
						db.insertAutoSaveChkList(response_site_list.getSite_list().get(arg2).getTXN_ID(),
								"","","","0");
					}
					activity(arg2,alreadyJson);

				}else if(pm_missed.contains("A") && type.equals("TILL_YESTERDAY")){
					DataBaseHelper db = new DataBaseHelper(PMSchedules.this);
					db.open();
					if(db.isAlreadyAutoSaveChk(response_site_list.getSite_list().get(arg2).getTXN_ID(),"0")==0){
						db.insertAutoSaveChkList(response_site_list.getSite_list().get(arg2).getTXN_ID(),
								"","","","");
						alreadyJson = 1;
					}
					activity(arg2,alreadyJson);

				}else if (pm_pending.contains("A") && type.equals("APPROVAL_PENDING")){
					DataBaseHelper db = new DataBaseHelper(PMSchedules.this);
					db.open();
					Intent i = new Intent(PMSchedules.this,PMChecklistApproval.class);
					i.putExtra("S","R");
					i.putExtra("scheduledDate", response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE());
					i.putExtra("siteId", response_site_list.getSite_list().get(arg2).getSITE_ID());
					i.putExtra("siteName", response_site_list.getSite_list().get(arg2).getSidName());
					i.putExtra("activityTypeId", response_site_list.getSite_list().get(arg2).getPARAM_ID());
					i.putExtra("Status", response_site_list.getSite_list().get(arg2).getACTIVITY_STATUS());
					i.putExtra("paramName", response_site_list.getSite_list().get(arg2).getPARAM_NAME());
					i.putExtra("dgType", response_site_list.getSite_list().get(arg2).getDG_TYPE());
					i.putExtra("txn", response_site_list.getSite_list().get(arg2).getTXN_ID());
					i.putExtra("etsSid", response_site_list.getSite_list().get(arg2).getEtsSid());
					i.putExtra("imgUploadFlag",response_site_list.getSite_list().get(arg2).getImgUploadflag());

					if(db.isAlreadyAutoSaveChk(response_site_list.getSite_list().get(arg2).getTXN_ID(),"0")== 0){
						db.insertAutoSaveChkList(response_site_list.getSite_list().get(arg2).getTXN_ID(),
								"","","","");
						temp_flag =1;
					}else{
						temp_flag=0;
					}
					db.close();
					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						new GetImage(PMSchedules.this,i,response_site_list.getSite_list().get(arg2).getTXN_ID(),
								response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
								response_site_list.getSite_list().get(arg2).getPARAM_ID(),
								response_site_list.getSite_list().get(arg2).getSITE_ID(),
								response_site_list.getSite_list().get(arg2).getEtsSid(),
								response_site_list.getSite_list().get(arg2).getDG_TYPE(),
								response_site_list.getSite_list().get(arg2).getImgUploadflag(),1,arg2).execute();
					} else {
						//No Internet Connection;
						Utils.toast(PMSchedules.this, "17");
					}

                }else if (type.equals("TILL_TODAY")) { //done details
					Intent i = new Intent(PMSchedules.this,ViewPMCheckList.class);
					i.putExtra("S","D");
					i.putExtra("scheduledDate", response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE());
					i.putExtra("siteId", response_site_list.getSite_list().get(arg2).getSITE_ID());
					i.putExtra("siteName", response_site_list.getSite_list().get(arg2).getSidName());
					i.putExtra("activityTypeId", response_site_list.getSite_list().get(arg2).getPARAM_ID());
					i.putExtra("Status", response_site_list.getSite_list().get(arg2).getACTIVITY_STATUS());
					i.putExtra("paramName", response_site_list.getSite_list().get(arg2).getPARAM_NAME());
					i.putExtra("dgType", response_site_list.getSite_list().get(arg2).getDG_TYPE());
					i.putExtra("txn", response_site_list.getSite_list().get(arg2).getTXN_ID());
					i.putExtra("etsSid", response_site_list.getSite_list().get(arg2).getEtsSid());
					i.putExtra("imgUploadFlag",response_site_list.getSite_list().get(arg2).getImgUploadflag());
					i.putExtra( "rCat","");
					i.putExtra( "rejRmks","");
					i.putExtra( "rvDate","");

					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						new GetImage(PMSchedules.this,i,response_site_list.getSite_list().get(arg2).getTXN_ID(),
								response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
								response_site_list.getSite_list().get(arg2).getPARAM_ID(),
								response_site_list.getSite_list().get(arg2).getSITE_ID(),
								response_site_list.getSite_list().get(arg2).getEtsSid(),
								response_site_list.getSite_list().get(arg2).getDG_TYPE(),
								response_site_list.getSite_list().get(arg2).getImgUploadflag(),3,arg2).execute();
					} else {
						//No Internet Connection;
						Utils.toast(PMSchedules.this, "17");
					}

				}else if (type.equals("TILL_TODAY_APPROVED")){
					Intent i = new Intent(PMSchedules.this,ViewPMCheckList.class);
					i.putExtra("S","R");
					i.putExtra("scheduledDate", response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE());
					i.putExtra("siteId", response_site_list.getSite_list().get(arg2).getSITE_ID());
					i.putExtra("siteName", response_site_list.getSite_list().get(arg2).getSidName());
					i.putExtra("activityTypeId", response_site_list.getSite_list().get(arg2).getPARAM_ID());
					i.putExtra("Status", response_site_list.getSite_list().get(arg2).getACTIVITY_STATUS());
					i.putExtra("paramName", response_site_list.getSite_list().get(arg2).getPARAM_NAME());
					i.putExtra("dgType", response_site_list.getSite_list().get(arg2).getDG_TYPE());
					i.putExtra("txn", response_site_list.getSite_list().get(arg2).getTXN_ID());
					i.putExtra("etsSid", response_site_list.getSite_list().get(arg2).getEtsSid());
					i.putExtra("imgUploadFlag",response_site_list.getSite_list().get(arg2).getImgUploadflag());
					i.putExtra( "rCat","");
					i.putExtra( "rejRmks","");
					i.putExtra( "rvDate","");

					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						new GetImage(PMSchedules.this,i,response_site_list.getSite_list().get(arg2).getTXN_ID(),
								response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
								response_site_list.getSite_list().get(arg2).getPARAM_ID(),
								response_site_list.getSite_list().get(arg2).getSITE_ID(),
								response_site_list.getSite_list().get(arg2).getEtsSid(),
								response_site_list.getSite_list().get(arg2).getDG_TYPE(),
								response_site_list.getSite_list().get(arg2).getImgUploadflag(),4,arg2).execute();
					} else {
						//No Internet Connection;
						Utils.toast(PMSchedules.this, "17");
					}
				}else if (type.equals("REJECTED")){
					Intent i = new Intent(PMSchedules.this,ViewPMCheckList.class);
					i.putExtra("S","J");
					i.putExtra("scheduledDate", response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE());
					i.putExtra("siteId", response_site_list.getSite_list().get(arg2).getSITE_ID());
					i.putExtra("siteName", response_site_list.getSite_list().get(arg2).getSidName());
					i.putExtra("activityTypeId", response_site_list.getSite_list().get(arg2).getPARAM_ID());
					i.putExtra("Status", response_site_list.getSite_list().get(arg2).getACTIVITY_STATUS());
					i.putExtra("paramName", response_site_list.getSite_list().get(arg2).getPARAM_NAME());
					i.putExtra("dgType", response_site_list.getSite_list().get(arg2).getDG_TYPE());
					i.putExtra("txn", response_site_list.getSite_list().get(arg2).getTXN_ID());
					i.putExtra("etsSid", response_site_list.getSite_list().get(arg2).getEtsSid());
					i.putExtra("imgUploadFlag",response_site_list.getSite_list().get(arg2).getImgUploadflag());
					i.putExtra( "rvDate", response_site_list.getSite_list().get( arg2 ).getRvDate() );
					i.putExtra( "rejRmks", response_site_list.getSite_list().get( arg2 ).getRejRmks() );
					i.putExtra( "rCat", response_site_list.getSite_list().get( arg2 ).getrCat() );

					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						new GetImage(PMSchedules.this,i,response_site_list.getSite_list().get(arg2).getTXN_ID(),
								response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
								response_site_list.getSite_list().get(arg2).getPARAM_ID(),
								response_site_list.getSite_list().get(arg2).getSITE_ID(),
								response_site_list.getSite_list().get(arg2).getEtsSid(),
								response_site_list.getSite_list().get(arg2).getDG_TYPE(),
								response_site_list.getSite_list().get(arg2).getImgUploadflag(),5,arg2).execute();
					} else {
						//No Internet Connection;
						Utils.toast(PMSchedules.this, "17");
					}

				}
			}
		});
	}

	public void searchData(String flag) {
		db.open();
		String cs = "";

		if(flag.equalsIgnoreCase("T")){
			cs = et_txn_id.getText().toString().toLowerCase( Locale.getDefault());
		}else{
			cs = et_searchby_site.getText().toString().toLowerCase(Locale.getDefault());
		}

		if(oper.equalsIgnoreCase( "S" )){
			response_site_list = db.ScheduleSite(cs, flag);
		}else if(oper.equalsIgnoreCase( "D" )
				|| oper.equalsIgnoreCase( "P" )){
			response_site_list = db.doneSite(cs, flag);
		}else if(oper.equalsIgnoreCase( "M" )){
			response_site_list = db.missedSite(cs, flag);
		}else if(oper.equalsIgnoreCase( "V" )){
			response_site_list = db.verifySite(cs, flag);
		}else if(oper.equalsIgnoreCase( "R" )){
			response_site_list = db.rejectSite(cs, flag);
		}else if(oper.equalsIgnoreCase( "RS" )){
			if(flag.equalsIgnoreCase("T")){
				response_site_list = db.reSubmitSite(cs,4);
			}else {
				response_site_list = db.reSubmitSite( cs, 1 );
			}

		}



		if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
			adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
			lv_schedule.setAdapter(adapterrr);
			txt_no_schedule.setVisibility(View.GONE);
		} else {
			// Toast.makeText(ScheduledList.this,
			// "No Activity Found",Toast.LENGTH_LONG).show();
			if (et_searchby_site.getText().toString().trim().length() != 0) {
				Utils.toast(PMSchedules.this, "225");
			}
			txt_no_schedule.setVisibility(View.VISIBLE);
			lv_schedule.setAdapter(null);
		}
		db.close();
	}

	//public void activity(final int arg2, final String preMin,final String preMax, final String postMin,
	//					 final String postMax,String imageName,int alreadyJson) {
	public void activity(final int arg2,int alreadyJson) {
		HashMap<String, String> valData = new HashMap<String, String>();
		HashMap<String, String> readingData = new HashMap<String, String>();
		if(response_site_list.getSite_list().get(arg2).getLtstRdng()!=null){
			String[] split1 = response_site_list.getSite_list().get(arg2).getLtstRdng().split( "\\," );
			if(split1.length>0) {
				for (int i = 0; i < split1.length; i++) {
					String[] split2 = split1[i].split( "\\~" );
					if(split2.length>1) {
						readingData.put( split2[0], split2[1] );
					}
				}
			}
		}

		if(response_site_list.getSite_list().get(arg2).getVal()!=null) {
			String[] split = response_site_list.getSite_list().get( arg2 ).getVal().split( "\\~" );
			if(split.length>0) {
				for(int i = 0; i<split.length; i++){
					valData.put(split[i],"");
				}
			}
		}
		Intent i = new Intent(PMSchedules.this, PMChecklist.class);
		i.putExtra("scheduledDate", response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE());
		i.putExtra("siteId", response_site_list.getSite_list().get(arg2).getSITE_ID());
		i.putExtra("siteName", response_site_list.getSite_list().get(arg2).getSidName());
		i.putExtra("activityTypeId", response_site_list.getSite_list().get(arg2).getPARAM_ID());
		i.putExtra("paramName", response_site_list.getSite_list().get(arg2).getPARAM_NAME());
		i.putExtra("Status", response_site_list.getSite_list().get(arg2).getACTIVITY_STATUS());
		i.putExtra("dgType", response_site_list.getSite_list().get(arg2).getDG_TYPE());
		i.putExtra("txn", response_site_list.getSite_list().get(arg2).getTXN_ID());
		i.putExtra("etsSid", response_site_list.getSite_list().get(arg2).getEtsSid());
		i.putExtra("preMinImage", "0");
		i.putExtra("preMaxImage", "0");
		i.putExtra("postMinImage", "0");
		i.putExtra("postMaxImage", "0");
		i.putExtra("imageName", "");
		i.putExtra("valData", valData);
		i.putExtra("readingData", readingData);
		i.putExtra("S",oper);


		if(type.equals("RESUBMITTED")){
			i.putExtra( "rvDate", response_site_list.getSite_list().get( arg2 ).getRvDate() );
			i.putExtra( "rejRmks", response_site_list.getSite_list().get( arg2 ).getRejRmks() );
			i.putExtra( "rCat", response_site_list.getSite_list().get( arg2 ).getrCat() );

			if (Utils.isNetworkAvailable(PMSchedules.this)) {
				new GetImage(PMSchedules.this,i,response_site_list.getSite_list().get(arg2).getTXN_ID(),
						response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
						response_site_list.getSite_list().get(arg2).getPARAM_ID(),
						response_site_list.getSite_list().get(arg2).getSITE_ID(),
						response_site_list.getSite_list().get(arg2).getEtsSid(),
						response_site_list.getSite_list().get(arg2).getDG_TYPE(),
						response_site_list.getSite_list().get(arg2).getImgUploadflag(),2,arg2).execute();
			} else {
				//No Internet Connection;
				Utils.toast(PMSchedules.this, "17");
			}
		}else{
			i.putExtra( "rvDate", "" );
			i.putExtra( "rejRmks", "");
			i.putExtra( "rCat", "");
			//startActivity(i);

			if (Utils.isNetworkAvailable(PMSchedules.this)) {
				new GetPMCheckList(PMSchedules.this,alreadyJson,i,
						response_site_list.getSite_list().get(arg2).getSITE_ID(),
						response_site_list.getSite_list().get(arg2).getPARAM_ID(),
						response_site_list.getSite_list().get(arg2).getTXN_ID(),2,arg2,
						response_site_list.getSite_list().get(arg2).getSCHEDULE_DATE()).execute();
			} else if(response_site_list.getSite_list().get( arg2).getPopupFlag()!=null
					&& response_site_list.getSite_list().get( arg2).getPopupFlag().length()>0
					&& response_site_list.getSite_list().get( arg2).getPopupFlag()
					.equalsIgnoreCase("1"))
			{
				if(response_site_list.getSite_list().get( arg2).getPullDate()!=null &&
						response_site_list.getSite_list().get( arg2).getPullDate().length()>0){
					overallDate(0,response_site_list.getSite_list().get( arg2).getPullDate()
							,arg2,i);
				}else{
					pullDate(0,arg2,i,"");
				}
			} else {
				startActivity(i);
			}
		}
	}


	private void pullDate(int flag,int selectedPos,Intent i,String msg) {
		final Dialog actvity_dialog;
		actvity_dialog = new Dialog(PMSchedules.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.generator_status_popup);
		Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();

		TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		tv_title.setTypeface( Utils.typeFace(PMSchedules.this));
		tv_title.setText(Utils.msg(PMSchedules.this, "739"));

		Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
		iv_cross.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				if (Utils.isNetworkAvailable(PMSchedules.this)) {
					//108
					networkManager.getToken(new NetworkManager.TokenCallback() {
						@Override
						public void onTokenReceived(String token) {
							new GetSchedules( PMSchedules.this,token).execute();
						}

						@Override
						public void onTokenError(String error) {
							Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
						}
					});
					//108
					//new GetSchedules( PMSchedules.this ).execute();
					searchFunction();

				}
			}
		});

		TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
		Utils.textViewProperty(PMSchedules.this,tv_sid,Utils.msg(PMSchedules.this,"77"));

		EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
		Utils.editTextProperty(PMSchedules.this,et_sid);
		et_sid.setEnabled( false );
		et_sid.setText(response_site_list.getSite_list().get(selectedPos).getSITE_ID());

		TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
		Utils.textViewProperty(PMSchedules.this,tv_aid,Utils.msg(PMSchedules.this,"740"));

		EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
		Utils.editTextProperty(PMSchedules.this,et_aid);
		et_aid.setEnabled( false );
		et_aid.setText(response_site_list.getSite_list().get(selectedPos).getPARAM_NAME());

		TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
		Utils.textViewProperty(PMSchedules.this,tv_pull_date,Utils.msg(PMSchedules.this,"743"));
		tv_pull_date.setVisibility(View.GONE);
		EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
		Utils.editTextProperty(PMSchedules.this,et_pull_date);
		et_pull_date.setEnabled( false );
		et_pull_date.setText("");
		et_pull_date.setVisibility(View.GONE);

		TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
		Utils.textViewProperty(PMSchedules.this,tv_overall_date,Utils.msg(PMSchedules.this,"744"));
		tv_overall_date.setVisibility(View.GONE);
		EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
		Utils.editTextProperty(PMSchedules.this,et_overall_date);
		et_overall_date.setEnabled( false );
		et_overall_date.setText("");
		et_overall_date.setVisibility(View.GONE);


		Button next = (Button) actvity_dialog.findViewById( R.id.bt_next );
		next.setTypeface( Utils.typeFace(PMSchedules.this));
		next.setText( Utils.msg(PMSchedules.this, "742" ) );
		next.setVisibility(View.GONE);

		TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
		Utils.textViewProperty(PMSchedules.this,tv_checklist,"");
		tv_checklist.setVisibility(View.GONE);

		Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
		add.setTypeface( Utils.typeFace(PMSchedules.this) );
		add.setText( Utils.msg(PMSchedules.this, "204" ) );

		TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
		Utils.textViewProperty(PMSchedules.this,tv_gen_status,Utils.msg(PMSchedules.this,"741"));

		Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
		sp_gen_status.setBackgroundResource(R.drawable.doted);
		Utils.spinnerProperty(PMSchedules.this,sp_gen_status);
		db.open();
		ArrayList<String> list = db.getPreventiveParamName("1195","654","1");
		addItemsOnSpinner(sp_gen_status,list);
		db.close();

		add.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Utils.isNetworkAvailable(PMSchedules.this)) {
					actvity_dialog.cancel();
					new UpdateGeneratorStatusTask(PMSchedules.this,Utils.CurrentDate(2),""
							,selectedPos,1,i).execute();
				} else {
					Toast.makeText(PMSchedules.this,"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		next.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				overallDate(0,Utils.CurrentDate(2),selectedPos,i);
			}
		});

		if(flag == 1){
			tv_checklist.setVisibility(View.VISIBLE);
			tv_checklist.setText(""+msg);
			next.setVisibility(View.VISIBLE);
			tv_pull_date.setVisibility(View.VISIBLE);
			et_pull_date.setVisibility(View.VISIBLE);
			et_pull_date.setText(""+Utils.CurrentDate(2));
			add.setVisibility(View.GONE);
		}

	}

	private void overallDate(int flag,String pullDate, int selectedPos,Intent i) {
		final Dialog actvity_dialog;
		actvity_dialog = new Dialog(PMSchedules.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.generator_status_popup);
		Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();

		TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
		tv_title.setTypeface( Utils.typeFace(PMSchedules.this));
		tv_title.setText(Utils.msg(PMSchedules.this, "739"));

		TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
		Utils.textViewProperty(PMSchedules.this,tv_checklist,Utils.msg(PMSchedules.this,"745"));


		Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
		iv_cross.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				if (Utils.isNetworkAvailable(PMSchedules.this)) {
					//108
					networkManager.getToken(new NetworkManager.TokenCallback() {
						@Override
						public void onTokenReceived(String token) {
							new GetSchedules( PMSchedules.this,token).execute();
						}

						@Override
						public void onTokenError(String error) {
							Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
						}
					});
					//108
				//	new GetSchedules( PMSchedules.this ).execute();
					searchFunction();
				}
			}
		});
		TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
		Utils.textViewProperty(PMSchedules.this,tv_sid,Utils.msg(PMSchedules.this,"77"));
		EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
		Utils.editTextProperty(PMSchedules.this,et_sid);
		et_sid.setEnabled( false );
		et_sid.setText(response_site_list.getSite_list().get(selectedPos).getSITE_ID());

		TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
		Utils.textViewProperty(PMSchedules.this,tv_aid,Utils.msg(PMSchedules.this,"740"));

		EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
		Utils.editTextProperty(PMSchedules.this,et_aid);
		et_aid.setEnabled( false );
		et_aid.setText(response_site_list.getSite_list().get(selectedPos).getPARAM_NAME());

		TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
		Utils.textViewProperty(PMSchedules.this,tv_pull_date,Utils.msg(PMSchedules.this,"743"));
		EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
		Utils.editTextProperty(PMSchedules.this,et_pull_date);
		et_pull_date.setEnabled( false );
		et_pull_date.setText(pullDate);

		TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
		Utils.textViewProperty(PMSchedules.this,tv_overall_date,Utils.msg(PMSchedules.this,"744"));
		tv_overall_date.setVisibility(View.GONE);

		EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
		Utils.editTextProperty(PMSchedules.this,et_overall_date);
		et_overall_date.setEnabled( false );
		et_overall_date.setText("");
		et_overall_date.setVisibility(View.GONE);

		Button no = (Button) actvity_dialog.findViewById( R.id.bt_next );
		no.setTypeface( Utils.typeFace(PMSchedules.this) );
		no.setText( Utils.msg(PMSchedules.this, "742" ) );
		no.setVisibility(View.GONE);

		Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
		add.setTypeface( Utils.typeFace(PMSchedules.this) );
		add.setText( Utils.msg( PMSchedules.this, "284" ) );

		TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
		Utils.textViewProperty(PMSchedules.this,tv_gen_status,Utils.msg(PMSchedules.this,"741"));

		Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
		sp_gen_status.setBackgroundResource(R.drawable.doted);
		Utils.spinnerProperty(PMSchedules.this,sp_gen_status);
		db.open();
		ArrayList<String> list = db.getPreventiveParamName("1195","654","2");
		addItemsOnSpinner(sp_gen_status,list);
		db.close();

		add.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(flag==1){
					actvity_dialog.cancel();
					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						//108
						networkManager.getToken(new NetworkManager.TokenCallback() {
							@Override
							public void onTokenReceived(String token) {
								new GetSchedules( PMSchedules.this,token).execute();
							}

							@Override
							public void onTokenError(String error) {
								Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
							}
						});
						//108
						//new GetSchedules( PMSchedules.this ).execute();
						searchFunction();
					}
					startActivity(i);
				}else{
					if (Utils.isNetworkAvailable(PMSchedules.this)) {
						actvity_dialog.cancel();
						new UpdateGeneratorStatusTask(PMSchedules.this,pullDate,Utils.CurrentDate(2),
								selectedPos,2,i).execute();
					} else {
						Toast.makeText(PMSchedules.this,"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		if(flag == 1){
			tv_overall_date.setVisibility(View.VISIBLE);
			et_overall_date.setVisibility(View.VISIBLE);
			no.setVisibility(View.VISIBLE);
			//tv_gen_status.setVisibility(View.GONE);
			//sp_gen_status.setVisibility(View.GONE);
			et_overall_date.setText(""+Utils.CurrentDate(2));
			add.setText( Utils.msg( PMSchedules.this, "63" ));
			no.setText( Utils.msg( PMSchedules.this, "64" ));
			tv_checklist.setVisibility(View.VISIBLE);
		}

		no.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actvity_dialog.cancel();
				if (Utils.isNetworkAvailable(PMSchedules.this)) {
					//108
					networkManager.getToken(new NetworkManager.TokenCallback() {
						@Override
						public void onTokenReceived(String token) {
							new GetSchedules( PMSchedules.this,token).execute();
						}

						@Override
						public void onTokenError(String error) {
							Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
						}
					});
					//108
				//	new GetSchedules( PMSchedules.this ).execute();
					searchFunction();
				}
			}
		});
	}

	/*private void alert(final int arg2, final String preMin,final String preMax, final String postMin,
					   final String postMax,final String imageName,final int alreadyJson) {
		AlertDialog.Builder alert = new AlertDialog.Builder(PMSchedules.this);
		LayoutInflater inflater = (PMSchedules.this).getLayoutInflater();
		View view = inflater.inflate(R.layout.custom_alert, null);
		Button positive = (Button) view.findViewById(R.id.bt_ok);
		Button negative = (Button) view.findViewById(R.id.bt_cancel);
		TextView title = (TextView) view.findViewById(R.id.tv_title);
		EditText et = (EditText) view.findViewById(R.id.et_ip);
		et.setVisibility(View.GONE);
		negative.setVisibility(View.GONE);
		positive.setTypeface(Utils.typeFace(PMSchedules.this));
		negative.setTypeface(Utils.typeFace(PMSchedules.this));
		title.setTypeface(Utils.typeFace(PMSchedules.this));
		title.setText(Utils.msg(PMSchedules.this, "163") + " " + preMin + " "
				+ Utils.msg(PMSchedules.this, "300"));
		positive.setText(Utils.msg(PMSchedules.this, "7"));
		negative.setText(Utils.msg(PMSchedules.this, "64"));
		alert.setView(view);
		final AlertDialog alertDialog = alert.create();
		alertDialog.show();
		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
				activity(arg2, preMin, preMax, postMin, postMax, imageName,alreadyJson);
			}
		});
	}*/

	// 0.1 Start
	public void searchFunction() {
		sp_search_by = (Spinner) findViewById(R.id.sp_search_by);
		addItemsOnSpinner(sp_search_by, searchDDLlist);
		sp_search_by.setBackgroundResource(R.drawable.input_box );
		et_sch_date = (EditText) findViewById(R.id.et_sch_date);
		et_sch_date.setTypeface(Utils.typeFace(PMSchedules.this));
		et_searchby_site = (EditText) findViewById(R.id.et_searchby_site);
		et_searchby_site.setBackgroundResource(R.drawable.input_box );
		et_searchby_site.setTypeface(Utils.typeFace(PMSchedules.this));
		sp_search_by.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
				et_searchby_site.setText("");
				et_sch_date.setText("");
				optCounter = 0;
				tv_operator.setText("Select Activity Type");
				hideKeyBoardEdt(sp_search_by);
				if (sp_search_by.getSelectedItem().toString()
						.equalsIgnoreCase("Site Id")) {
					tv_operator.setVisibility(View.GONE);
					et_searchby_site.setVisibility(View.VISIBLE);
					et_sch_date.setVisibility(View.GONE);
					et_txn_id.setVisibility(View.GONE);
				} else if (sp_search_by.getSelectedItem().toString()
						.equalsIgnoreCase("Activity Type")) {
					tv_operator.setVisibility(View.VISIBLE);
					et_searchby_site.setVisibility(View.GONE);
					et_sch_date.setVisibility(View.GONE);
					et_txn_id.setVisibility(View.GONE);
				} else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Schedule Date")
						||sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Done Date")
						||sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Verify Date")
						||sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Rejected Date")) {
					tv_operator.setVisibility(View.GONE);
					et_searchby_site.setVisibility(View.GONE);
					et_sch_date.setVisibility(View.VISIBLE);
					et_txn_id.setVisibility(View.GONE);
				}else if (sp_search_by.getSelectedItem().toString()
						.equalsIgnoreCase("Activity ID")) {
					tv_operator.setVisibility(View.GONE);
					et_searchby_site.setVisibility(View.GONE);
					et_sch_date.setVisibility(View.GONE);
					et_txn_id.setVisibility(View.VISIBLE);
				}
				adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
				lv_schedule.setAdapter(adapterrr);
				operatorList = new ArrayList<Operator>();
				All_Operator_Id = new StringBuffer();

				db.open();
				list_operator_id = new ArrayList<String>();
				list_operator_name = new ArrayList<String>();
				list_operator_name = db.getInciParam1("20",tv_operator,"654");

				for (int i = 0; i < list_operator_name.size(); i++) {
					if (!list_operator_name.get(i).equalsIgnoreCase(
							"Please Select")) {
						Operator operater = new Operator(db.getInciParamId("20",
								list_operator_name.get(i),"654"), list_operator_name
								.get(i), false);
						operatorList.add(operater);
					}
				}
				db.close();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		et_searchby_site.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
									  int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				searchData("I");
			}
		});


		et_txn_id.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}
			@Override
			public void afterTextChanged(Editable arg0) {
				searchData("T");
			}
		});

		et_sch_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new DatePickerDialog(PMSchedules.this, date, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		et_sch_date.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// String text = et_sch_date.getText().toString();
				// adapterrr.filterbydate(text);
			}
		});
	}

	DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			// TODO Auto-generated method stub
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.YEAR, year);
			updateLabel();
		}
	};

	private void updateLabel() {
		String myFormat = "dd-MMM-yyyy-hh-mm-ss";
//		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
		et_sch_date.setText(sdf.format(myCalendar.getTime()));
		db.open();
		String cs = et_sch_date.getText().toString().toLowerCase(Locale.getDefault());
		if(oper.equalsIgnoreCase( "S" )){
			response_site_list = db.ScheduleSite(cs, "D");
		}else if(oper.equalsIgnoreCase( "D" ) || oper.equalsIgnoreCase( "P" )){
			response_site_list = db.doneSite(cs, "D");
		}else if(oper.equalsIgnoreCase( "M" )){
			response_site_list = db.missedSite(cs, "D");
		}else if(oper.equalsIgnoreCase( "V" )){
			response_site_list = db.verifySite(cs, "D");
		}else if(oper.equalsIgnoreCase( "R" )){
			response_site_list = db.rejectSite(cs, "D");
		}else if(oper.equalsIgnoreCase( "RS" )){
			response_site_list = db.reSubmitSite(cs, 3);
		}

		if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
			adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
			lv_schedule.setAdapter(adapterrr);
			txt_no_schedule.setVisibility(View.GONE);
		} else {
			// Toast.makeText(ScheduledList.this,
			// "No Activity Found",Toast.LENGTH_LONG).show();
			Utils.toast(PMSchedules.this, "225");
			txt_no_schedule.setVisibility(View.VISIBLE);
			lv_schedule.setAdapter(null);
		}
		db.close();
	}

	// end 0.1
	public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_text, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
		spinner.setAdapter(dataAdapter);
	}

	public void ActivityPopup() {
		optCounter = 0;
		All_Operator_Id.setLength(0);
		actvity_dialog = new Dialog(PMSchedules.this, R.style.FullHeightDialog);
		actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
		actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		actvity_dialog.setContentView(R.layout.activity_type_popup);
		final Window window_SignIn = actvity_dialog.getWindow();
		window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		actvity_dialog.show();

		final ListView list_view = (ListView) actvity_dialog.findViewById(R.id.list_view);
		TextView tv_apply = (TextView) actvity_dialog.findViewById(R.id.tv_apply);
		tv_apply.setTypeface(Utils.typeFace(PMSchedules.this));
		TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
		tv_header.setTypeface(Utils.typeFace(PMSchedules.this));
		tv_header.setText("Actvity Type");

		ImageView iv_cancel = (ImageView) actvity_dialog.findViewById(R.id.iv_cancel);
		iv_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				actvity_dialog.cancel();
			}
		});

		dataAdapter = new MyCustomAdapter(PMSchedules.this,
				R.layout.custom_operator, operatorList);
		list_view.setAdapter(dataAdapter);
		tv_apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ArrayList<Operator> OperatorList = dataAdapter.countryList;
				for (int i = 0; i < OperatorList.size(); i++) {
					Operator operator = OperatorList.get(i);
					if (operator.isSelected()) {
						optCounter++;
						if (optCounter > 1) {
							All_Operator_Id.append(",");
						}
						All_Operator_Id.append(operatorList.get(i).getCode());

					}
				}

				if (optCounter == 0) {
					tv_operator.setText("Select Activity Type");
					searchData("I");
				} else {
					tv_operator.setText("" + optCounter + " Activity Selected");
				}
				actvity_dialog.dismiss();// dismiss dialog box for operator
				if (All_Operator_Id.length() > 0) {
					db.open();
					if(oper.equalsIgnoreCase( "S" )){
						response_site_list = db.ScheduleSite(All_Operator_Id.toString(), "aID");
					}else if(oper.equalsIgnoreCase( "D" ) || oper.equalsIgnoreCase( "P" )){
						response_site_list = db.doneSite(All_Operator_Id.toString(), "aID");
					}else if(oper.equalsIgnoreCase( "M" )){
						response_site_list = db.missedSite(All_Operator_Id.toString(), "aID");
					}else if(oper.equalsIgnoreCase( "V" )){
						response_site_list = db.verifySite(All_Operator_Id.toString(), "aID");
					}else if(oper.equalsIgnoreCase( "R" )){
						response_site_list = db.rejectSite(All_Operator_Id.toString(), "aID");
					}else if(oper.equalsIgnoreCase( "RS" )){
						response_site_list = db.reSubmitSite(All_Operator_Id.toString(), 2);
					}

					if (response_site_list.getSite_list().size() > 0) {
						adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
						lv_schedule.setAdapter(adapterrr);
						txt_no_schedule.setVisibility(View.GONE);
					} else {
						// Toast.makeText(ScheduledList.this,
						// "No Activity Found",Toast.LENGTH_LONG).show();
						Utils.toast(PMSchedules.this, "225");
						txt_no_schedule.setVisibility(View.VISIBLE);
						lv_schedule.setAdapter(null);
					}
					db.close();
				}
			}
		});
	}

	private class MyCustomAdapter extends ArrayAdapter<Operator> {
		private ArrayList<Operator> countryList;
		public MyCustomAdapter(Context context, int textViewResourceId,ArrayList<Operator> countryList) {
			super(context, textViewResourceId, countryList);
			this.countryList = new ArrayList<Operator>();
			this.countryList.addAll(countryList);
		}

		private class ViewHolder {
			TextView code;
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.custom_operator, null);
				holder = new ViewHolder();
				holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
				holder.name.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Operator country = (Operator) cb.getTag();
						country.setSelected(cb.isChecked());
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Operator country = countryList.get(position);
			holder.code.setText("");
			holder.name.setText(country.getName());
			holder.name.setChecked(country.isSelected());
			holder.name.setTag(country);
			return convertView;
		}
	}

	public class GetSchedules extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		String response,token; //108
		public GetSchedules(Context con,String token) {
			this.con = con;
			this.token = token; //108
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Gson gson = new Gson();
			if(moduleUrl.equalsIgnoreCase("0")){
				url=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
			}else{
				url=moduleUrl+ WebMethods.url_getScheduled_Sites;
			}

			try {
				List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(4);
				nameValuePairs1.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
				nameValuePairs1.add(new BasicNameValuePair("type",type));
				nameValuePairs1.add(new BasicNameValuePair("siteID", ""));
				nameValuePairs1.add(new BasicNameValuePair("activityTypeFlag","1"));
				response = Utils.httpPostRequest1(con,url, nameValuePairs1,token); //108
				response_site_list = gson.fromJson(response, BeanSiteList.class);
			} catch (Exception e) {
				e.printStackTrace();
				response_site_list=null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (response_site_list == null) {
				// Toast.makeText(MyPMSchedules.this,"Server Not Available",
				// Toast.LENGTH_LONG).show();
				Utils.toast(PMSchedules.this, "13");
			} else if (response_site_list.getSite_list().size() > 0) {
				db.open();
				if(oper.equalsIgnoreCase( "S" )){
					db.clearScheduleList();
					db.insertScheduleList(response_site_list);
				}else if(oper.equalsIgnoreCase( "D" ) || oper.equalsIgnoreCase( "P" )){
					db.clearDoneList();
					db.insertDoneList(response_site_list);
				}else if(oper.equalsIgnoreCase( "M" )){
					db.clearMissedList();
					db.insertMissedList(response_site_list);
				}else if(oper.equalsIgnoreCase( "V" )){
					db.clearVerifyList();
					db.insertVerifyList(response_site_list);
				}else if(oper.equalsIgnoreCase( "R" )){
					db.clearRejectList();
					db.insertRejectList(response_site_list);
				}else if(oper.equalsIgnoreCase( "RS" )){
					db.clearResubmitPMList();
					db.insertResubmitList(response_site_list);
				}
				db.close();
				lv_schedule.setAdapter(new AdapterSchedule(PMSchedules.this,response_site_list,oper));
				txt_no_schedule.setVisibility(View.GONE);
			} else {

				db.open();
				if(oper.equalsIgnoreCase( "S" )){
					db.clearScheduleList();
				}else if(oper.equalsIgnoreCase( "D" ) || oper.equalsIgnoreCase( "P" )){
					db.clearDoneList();
				}else if(oper.equalsIgnoreCase( "M" )){
					db.clearMissedList();
				}else if(oper.equalsIgnoreCase( "V" )){
					db.clearVerifyList();
				}else if(oper.equalsIgnoreCase( "R" )){
					db.clearRejectList();
				}else if(oper.equalsIgnoreCase( "RS" )){
					db.clearResubmitPMList();
				}
				db.close();
				finish();
				Utils.toast(PMSchedules.this, "225");
				txt_no_schedule.setVisibility(View.VISIBLE);
				lv_schedule.setAdapter(null);
				Utils.msgText(PMSchedules.this, "225", txt_no_schedule); // No Activity Found
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	public void  hideKeyBoardEdt(Spinner edt) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.
				INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		searchFunction();
		db.open();
		if (Utils.isNetworkAvailable(PMSchedules.this)) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					//108
					networkManager.getToken(new NetworkManager.TokenCallback() {
						@Override
						public void onTokenReceived(String token) {
							new GetSchedules( PMSchedules.this,token).execute();
						}

						@Override
						public void onTokenError(String error) {
							Toast.makeText(PMSchedules.this, error, Toast.LENGTH_SHORT).show();
						}
					});
					//108
					//new GetSchedules( PMSchedules.this ).execute();
				}
			},100);
		}
	   else {
			if(oper.equalsIgnoreCase( "S" ) && db.scheduleSiteCount()>0){
				response_site_list = db.ScheduleSite("", "A");
				if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
					adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
					lv_schedule.setAdapter(adapterrr);
					txt_no_schedule.setVisibility(View.GONE);
				}
			}else if(oper.equalsIgnoreCase( "M" ) && db.missSiteCount()>0){
				response_site_list = db.missedSite("", "A");
				if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
					adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
					lv_schedule.setAdapter(adapterrr);
					txt_no_schedule.setVisibility(View.GONE);
				}
			}else if(oper.equalsIgnoreCase( "RS" ) && db.reSubmitSiteCount()>0){
				response_site_list = db.reSubmitSite("", 0);
				if (response_site_list.getSite_list()!=null && response_site_list.getSite_list().size() > 0) {
					adapterrr = new AdapterSchedule(PMSchedules.this,response_site_list,oper);
					lv_schedule.setAdapter(adapterrr);
					txt_no_schedule.setVisibility(View.GONE);
				}
			}
			else{
				txt_no_schedule.setVisibility(View.VISIBLE);
				lv_schedule.setAdapter(null);
			}
		}
		db.close();
	}


	private class GetImage extends AsyncTask<Void, Void, Void> {
		Context con;
		ProgressDialog pd;
		BeanGetImageList imageList;
		String txnId,scDate,activityId,sId,dgType,etsSid,imguploadflag;
		int flag,selectedPos = 0;
		Intent i;
		private GetImage(Context con,Intent i,String txnId,String scDate,String activityId,String sId,
						 String etsSid,String dgType,String imguploadflag,int flag,int selectedPos) {
			this.con = con;
			this.txnId = txnId;
			this.scDate = scDate;
			this.activityId = activityId;
			this.sId = sId;
			this.etsSid = etsSid;
			this.dgType = dgType;
			this.imguploadflag = imguploadflag;
			this.i = i;
			this.flag = flag; //flag 1 means pending tranaction for approval , 2 menas resubmited transaction
			this.selectedPos = selectedPos;
		}
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("siteId", etsSid));
				nameValuePairs.add(new BasicNameValuePair("activityType", activityId));
				nameValuePairs.add(new BasicNameValuePair("scheduledDate",scDate));
				nameValuePairs.add(new BasicNameValuePair("dgType", dgType));
				nameValuePairs.add(new BasicNameValuePair("assetID","-10"));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_GetPmImage;
				}else{
					url=moduleUrl+ WebMethods.url_GetPmImage;
				}

				String response = Utils.httpPostRequest(con,url, nameValuePairs);
				Gson gson = new Gson();
				imageList = gson.fromJson(response, BeanGetImageList.class);
			} catch (Exception e) {
				e.printStackTrace();
				imageList = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			DataBaseHelper dataBaseHelper = new DataBaseHelper(PMSchedules.this);
			dataBaseHelper.open();
			dataBaseHelper.deleteActivityImages(txnId,"0");
			if (imageList == null) {
			}else if (imageList.getImageList().size() > 0) {
				for (int i = 0; i < imageList.getImageList().size(); i++) {
					if (imageList.getImageList().get( i ).getPreImgPath() != null &&
							imageList.getImageList().get( i ).getPreImgPath() != "") {

						String[] imgpathArr = null;
						String[] timeArr = null;
						String[] nameArr = null;
						String[] latArr = null;
						String[] longArr = null;

						imgpathArr = new String[1000];
						imgpathArr = imageList.getImageList().get( i ).getPreImgPath().split( "\\," );

						if(imageList.getImageList().get(i).getPreImgName()!=null){
							nameArr = new String[1000];
							nameArr = imageList.getImageList().get( i ).getPreImgName().split( "\\," );
						}

						if(imageList.getImageList().get(i).getPreLat()!=null){
							latArr = new String[1000];
							latArr = imageList.getImageList().get( i ).getPreLat().split( "\\," );
						}

						if(imageList.getImageList().get(i).getPreLongt()!=null){
							longArr = new String[1000];
							longArr = imageList.getImageList().get( i ).getPreLongt().split( "\\," );
						}

						if(imageList.getImageList().get(i).getPreImgTimeStamp()!=null){
							timeArr = new String[1000];
							timeArr = imageList.getImageList().get( i ).getPreImgTimeStamp().split( "\\," );
						}

						if(imgpathArr!=null && imgpathArr.length>0) {
							for(int j=0; j<imgpathArr.length; j++){

								String name = " ";
								String time = " ";
								String lat = " ";
								String longi = " ";
								String clId = "0";
								if(timeArr!=null && imgpathArr.length<=timeArr.length){
									time = timeArr[j];
								}

								if(nameArr!=null && imgpathArr.length<=nameArr.length){
									name = nameArr[j];
								}

								if(latArr!=null && imgpathArr.length<=latArr.length){
									lat = latArr[j];
								}else if(lat.length()==1 && imageList.getImageList().get( i ).getLATITUDE()!=null){
									lat = imageList.getImageList().get( i ).getLATITUDE();
								}


								if(longArr!=null && imgpathArr.length<=longArr.length){
									longi = longArr[j];
								}else if(longi.length()==1 && imageList.getImageList().get( i ).getLONGITUDE()!=null){
									longi = imageList.getImageList().get( i ).getLONGITUDE();
								}

								if(imguploadflag.equalsIgnoreCase("2")){
									clId = imageList.getImageList().get(i).getClID();
								}

								dataBaseHelper.insertImages(
										txnId,clId,imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
										name,lat,longi,Utils.DateTimeStamp(),time,1,3,
										scDate,activityId,sId,dgType,imgpathArr[j],
										mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,
										"0"
								);
							}
						}
					}

					if (imageList.getImageList().get( i ).getIMAGE_PATH() != null &&
							imageList.getImageList().get( i ).getIMAGE_PATH() != "") {
						String[] imgpathArr = null;
						String[] timeArr = null;
						String[] nameArr = null;
						String[] latArr = null;
						String[] longArr = null;

						imgpathArr =new String[1000];
						imgpathArr = imageList.getImageList().get(i).getIMAGE_PATH().split( "\\," );

						if(imageList.getImageList().get(i).getIMAGENAME()!=null){
							nameArr = new String[1000];
							nameArr = imageList.getImageList().get(i).getIMAGENAME().split( "\\," );
						}

						if(imageList.getImageList().get(i).getLATITUDE()!=null){
							latArr = new String[1000];
							latArr = imageList.getImageList().get( i ).getLATITUDE().split( "\\," );
						}

						if(imageList.getImageList().get(i).getLONGITUDE()!=null){
							longArr = new String[1000];
							longArr = imageList.getImageList().get( i ).getLONGITUDE().split( "\\," );
						}

						if(imageList.getImageList().get(i).getImgTimeStamp()!=null){
							timeArr = new String[1000];
							timeArr = imageList.getImageList().get(i).getImgTimeStamp().split( "\\," );
						}

						if(imgpathArr!=null && imgpathArr.length>0) {
							for (int j = 0; j < imgpathArr.length; j++) {

								String name = " ";
								String time = " ";
								String lat = " ";
								String longi = " ";
								String clId = "0";
								if(timeArr!=null && imgpathArr.length<=timeArr.length){
									time = timeArr[j];
								}

								if(nameArr!=null && imgpathArr.length<=nameArr.length){
									name = nameArr[j];
								}

								if(latArr!=null && imgpathArr.length<=latArr.length){
									lat = latArr[j];
								}else if(lat.length()==1 && imageList.getImageList().get( i ).getLATITUDE()!=null){
									lat = imageList.getImageList().get( i ).getLATITUDE();
								}


								if(longArr!=null && imgpathArr.length<=longArr.length){
									longi = longArr[j];
								}else if(longi.length()==1 && imageList.getImageList().get( i ).getLONGITUDE()!=null){
									longi = imageList.getImageList().get( i ).getLONGITUDE();
								}

								if(imguploadflag.equalsIgnoreCase("2")){
									clId = imageList.getImageList().get(i).getClID();
								}
		                                dataBaseHelper.insertImages(
										txnId,clId,imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
										name,lat,longi,Utils.DateTimeStamp(),time,2,3,
										scDate,activityId,sId,dgType,imgpathArr[j],
												mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,
												"0"
								);
							}
						}
					}
				}
			}
			dataBaseHelper.close();

			if (Utils.isNetworkAvailable(PMSchedules.this)) {
				new CheckListDetailsTask(PMSchedules.this,i,txnId,sId,scDate,dgType,activityId,flag,selectedPos).execute();
			} else {
				Utils.toast(PMSchedules.this, "17");
			}
			super.onPostExecute(result);
		}
	}

	private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		Intent i;
		String txnId,scDate,activityId,sId,dgType;
		int flag,selectedPos = 0;
		BeanCheckListDetails PMCheckListDetails;

		private CheckListDetailsTask(Context con,Intent i,String txnId,String sId,String scDate,String dgType,
									 String activityId, int flag,int selectedPos) {
			this.con = con;
			this.txnId = txnId;
			this.scDate = scDate;
			this.activityId = activityId;
			this.sId = sId;
			this.dgType = dgType;
			this.i = i;
			this.flag = flag;
			this.selectedPos = selectedPos;

		}
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("siteId", sId));
				nameValuePairs.add(new BasicNameValuePair("checkListType",activityId));
				nameValuePairs.add(new BasicNameValuePair("checkListDate",scDate));
				String activity_status = "D";
				if(flag == 4){
					activity_status = "R"; //get verify details
				}
				nameValuePairs.add(new BasicNameValuePair("status", activity_status));
				nameValuePairs.add(new BasicNameValuePair("languageCode",mAppPreferences.getLanCode()));
				nameValuePairs.add(new BasicNameValuePair("dgType",dgType));
				nameValuePairs.add(new BasicNameValuePair("assetID","0"));
				nameValuePairs.add(new BasicNameValuePair("equipID",""));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_getCheckListDetails;
				}else{
					url=moduleUrl+ WebMethods.url_getCheckListDetails;
				}
				String res = Utils.httpPostRequest(con,url, nameValuePairs);
				Gson gson = new Gson();
				PMCheckListDetails = gson.fromJson(res,BeanCheckListDetails.class);
			} catch (Exception e) {
				e.printStackTrace();
				PMCheckListDetails = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (PMCheckListDetails == null) {
				Utils.toast(PMSchedules.this, "13");
			}else if (PMCheckListDetails.getPMCheckListDetail()!=null && PMCheckListDetails.getPMCheckListDetail().size() > 0){
				DataBaseHelper dbHelper = new DataBaseHelper(PMSchedules.this);
				dbHelper.open();
				if(flag==1){ //pending for approval
					dbHelper.clearReviewerCheclist();
					dbHelper.insertReviewerCheckList(PMCheckListDetails.getPMCheckListDetail(),activityId,txnId,PMSchedules.this,temp_flag,"0");
					dbHelper.close();
				}else if(flag==3 || flag==4 || flag==5){  //done details,verify details,reject details
					dbHelper.clearReviewerCheclist();
					dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(),activityId);
					dbHelper.close();
				}else{
					for(int a = 0; a<PMCheckListDetails.getPMCheckListDetail().size();a++){
						sharePrefence(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
								PMCheckListDetails.getPMCheckListDetail().get(a).getStatus(),txnId);

						sharePrefenceRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
								PMCheckListDetails.getPMCheckListDetail().get(a).getViRemark(),txnId);
					}
				}
			}else {

			}

			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (Utils.isNetworkAvailable(PMSchedules.this)) {
				new GetPMCheckList(PMSchedules.this,0,i,sId,activityId,txnId,flag,
						selectedPos,scDate).execute();
			} else {
				startActivity(i);
			}
			super.onPostExecute(result);
		}
	}


	public void sharePrefence(String id,String s,String txnId){

		if(savedDataJsonObj==null){
			savedDataJsonObj = new JSONObject();
		}

		try {
			savedDataJsonObj.remove( "" + id);
			savedDataJsonObj.put( "" +id,s);
		} catch (JSONException e) {}

		DataBaseHelper db10 = new DataBaseHelper(PMSchedules.this);
		db10.open();
		db10.updateAutoSaveChkList(txnId,"","",savedDataJsonObj.toString(),"0");
		db10.close();

	}

	public void sharePrefenceRemarks(String id,String s,String txnId){

		if(savedDataJsonObjRemarks==null){
			savedDataJsonObjRemarks = new JSONObject();
		}

		try {
			savedDataJsonObjRemarks.remove( "" + id);
			savedDataJsonObjRemarks.put( "" +id,s);
		} catch (JSONException e) {

		}
		DataBaseHelper db10 = new DataBaseHelper(PMSchedules.this);
		db10.open();
		db10.updateAutoSaveRemarks(txnId,savedDataJsonObjRemarks.toString(),"0");
		db10.close();
	}

	// Class to call Web Service to get PM CheckList to draw form.
	private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		Intent i;
		String siteId = "",chklistType = "0",txnId="";
		BeanCheckListDetails PMCheckList;
		int alreadyJson = 0,flag,selectedPos = 0;
        String scDate  = "";
		private GetPMCheckList(Context con,int alreadyJson,Intent i,String siteId,String chklistType,
							   String txnId,int flag,int selectedPos,String scDate) {
			this.con = con;
			this.i = i;
			this.siteId = siteId;
			this.chklistType = chklistType;
			this.alreadyJson = alreadyJson;
			this.txnId = txnId;
			this.flag = flag;
			this.selectedPos = selectedPos;
			this.scDate = scDate;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show( con, null, "Loading..." );
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
			Gson gson = new Gson();
			nameValuePairs.add( new BasicNameValuePair( "siteId",siteId));//
			nameValuePairs.add( new BasicNameValuePair( "checkListType",chklistType)); // 0 means all checklist(20001,20002,20005...) data download
			nameValuePairs.add( new BasicNameValuePair( "checkListDate",scDate ) );
			nameValuePairs.add( new BasicNameValuePair( "status", "S")); //S or M get blank checklistdata
			nameValuePairs.add( new BasicNameValuePair( "dgType", "" ) );
			nameValuePairs.add( new BasicNameValuePair( "languageCode", mAppPreferences.getLanCode() ) );
			try {
				String url = "";
				if (moduleUrl.equalsIgnoreCase( "0" )) {
					url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
				} else {
					url = moduleUrl + WebMethods.url_getCheckListDetails;
				}
				String res = Utils.httpPostRequest( con, url, nameValuePairs );
				PMCheckList = gson.fromJson( res, BeanCheckListDetails.class );
			} catch (Exception e) {
				//e.printStackTrace();
				PMCheckList = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (PMCheckList == null) {
				// Toast.makeText(PMChecklist.this,"PM CheckList not available.Pls contact system admin.",Toast.LENGTH_LONG).show();
				Utils.toast(PMSchedules.this,"226");
			} else if (PMCheckList != null) {
				if (PMCheckList.getPMCheckListDetail()!=null && PMCheckList.getPMCheckListDetail().size() > 0) {
					//DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
					db.open();
					db.clearCheckList("655",chklistType,"0");
					db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),"655",
							alreadyJson,txnId,PMSchedules.this,"0","0");
					db.close();
				}
			} else {
				Utils.toast(PMSchedules.this,"13");
			}
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			mAppPreferences.setTTModuleSelection("655");
			mAppPreferences.setPMBackTask(657);

			if(response_site_list.getSite_list().get( selectedPos).getPopupFlag()!=null
					&& response_site_list.getSite_list().get( selectedPos).getPopupFlag().length()>0
					&& response_site_list.getSite_list().get( selectedPos).getPopupFlag().equalsIgnoreCase("1")
			        && flag==2)
			{
				if(response_site_list.getSite_list().get( selectedPos).getPullDate()!=null &&
						response_site_list.getSite_list().get( selectedPos).getPullDate().length()>0){
					overallDate(0,response_site_list.getSite_list().get( selectedPos).getPullDate()
							,selectedPos,i);
				}else{
					pullDate(0,selectedPos,i,"");
				}
			}
			else{
				startActivity(i);
			}
			super.onPostExecute( result );
		}
	}

	public class UpdateGeneratorStatusTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		int selectPos =0;
		String res;
		int pDsec = 1;
		String pullDate,overallDate;
		Intent i;
		public UpdateGeneratorStatusTask(Context con,String pullDate,String overallDate,int selectPos,int pDsec,Intent i) {
			this.con = con;
			this.selectPos = selectPos;
			this.pDsec = pDsec;
			this.i = i;
			this.pullDate = pullDate;
			this.overallDate = overallDate;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);
				nameValuePairs.add( new BasicNameValuePair("tranId",
						response_site_list.getSite_list().get(selectPos).getTXN_ID()));
				nameValuePairs.add( new BasicNameValuePair("siteId",
						response_site_list.getSite_list().get(selectPos).getSITE_ID()));
				nameValuePairs.add( new BasicNameValuePair("actType",
						response_site_list.getSite_list().get(selectPos).getPARAM_ID()));
				nameValuePairs.add( new BasicNameValuePair("scheduleDate",
						response_site_list.getSite_list().get(selectPos).getSCHEDULE_DATE()));
				nameValuePairs.add( new BasicNameValuePair("assgnUser",""));
				nameValuePairs.add( new BasicNameValuePair("assgnGrp",""));
				nameValuePairs.add( new BasicNameValuePair("reviewUser",""));
				nameValuePairs.add( new BasicNameValuePair("reviewGrp",""));
				nameValuePairs.add( new BasicNameValuePair("flag","G" ) );
				nameValuePairs.add( new BasicNameValuePair( "dgType", "0"));
				nameValuePairs.add( new BasicNameValuePair( "userId", mAppPreferences.getUserId()));
				nameValuePairs.add( new BasicNameValuePair( "pmStatus", "S"));
				nameValuePairs.add( new BasicNameValuePair( "reviewDate", ""));
				nameValuePairs.add( new BasicNameValuePair( "maxAccessDuration",""));
				nameValuePairs.add( new BasicNameValuePair( "no_of_pm_visit",""));
				nameValuePairs.add( new BasicNameValuePair( "siteAutoAccess", ""));
				nameValuePairs.add( new BasicNameValuePair( "maxAccessDuration_audit",""));
				nameValuePairs.add( new BasicNameValuePair( "no_of_pm_visit_audit",""));
				nameValuePairs.add( new BasicNameValuePair( "siteAutoAccess_audit", ""));
				nameValuePairs.add( new BasicNameValuePair( "generator_status",""+pDsec));
				nameValuePairs.add( new BasicNameValuePair( "pull_date",pullDate));
				nameValuePairs.add( new BasicNameValuePair( "overall_date",overallDate));
				String url = "";
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_add_activity;
				}else{
					url=moduleUrl+ WebMethods.url_add_activity;
				}
				res = Utils.httpPostRequest(con , url , nameValuePairs);
				res="{response :"+res+"}";
			} catch (Exception e) {
				e.printStackTrace();
				res = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			String success = "";
			String msg = "";
			if (pd !=null && pd.isShowing()) {
				pd.dismiss();
			}
			if (res != null && res.length()>0) {
				try {
					JSONObject jsonObject = new JSONObject(res);
					JSONArray subArray = jsonObject.getJSONArray("response");
					for(int i= 0;i<subArray.length();i++){
						success = subArray.getJSONObject(i).getString("success");
						msg = subArray.getJSONObject(i).getString("message");
					}
					Toast.makeText(con,msg,Toast.LENGTH_SHORT).show();
					if (success.equals("true") && pDsec == 1) {
						pullDate(1,selectPos,i,msg);
					}else if (success.equals("true") && pDsec == 2){
						overallDate(1,pullDate,selectPos,i);
					}else{
						if (pDsec == 1) {
							pullDate(0,selectPos,i,"");
						}else if (pDsec == 2){
							overallDate(0,pullDate,selectPos,i);
						}
					}
				}catch(Exception e){
					Toast.makeText(con,e.getMessage(),Toast.LENGTH_SHORT).show();
					if (pDsec == 1) {
						pullDate(0,selectPos,i,"");
					}else if (pDsec == 2){
						overallDate(0,pullDate,selectPos,i);
					}
				}
			}else {
				//if no responce then reopen popup
				Utils.toast(PMSchedules.this, "13");
				if (pDsec == 1) {
					pullDate(0,selectPos,i,"");
				}else if (pDsec == 2){
					overallDate(0,pullDate,selectPos,i);
				}

			}
			super.onPostExecute(result);
		}
	}
}