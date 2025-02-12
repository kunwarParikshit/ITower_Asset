package com.isl.preventive;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.incident.AddTicket;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.itower.GPSTracker;
import com.isl.modal.AlarmDescList;
import com.isl.modal.BeanGetImage;
import com.isl.modal.ChecklistDetail;
import com.isl.modal.Response;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.sparepart.SparePart;
import com.isl.util.CustomRangeInputFilter;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import infozech.itower.R;
import retrofit2.Call;
import retrofit2.Callback;

public class PMChecklistApproval extends Activity implements OnClickListener, OnItemSelectedListener {
    public ImageLoader loader = ImageLoader.getInstance();
    DisplayImageOptions op;
    List<BeanGetImage> PreimageList, PostimageList;
    LinearLayout linear, linearRemarks, rl_submit, ll_pre_act_photo, ll_post_act_img;
    RelativeLayout linearNoDataFound;
    Button btnTakePhoto, bt_back, btnTakePrePhoto;
    TextView tvSubmit,tv_assetId,tv_asdt,tv_aedt,tv_arId,tv_arStatus,tv_reject, tvHeader, tv_no_data, tv_click_img, tv_siteId, tv_spare_parts, tv_click_pre_img,
            tv_add_corrective_tt,tv_rj_cat, tv_rj_rmk, tv_rj_date, tv_rj_region, tv_projectName, tv_productName, tv_soNumber, tv_WorkOrderNumber,
            tv_Region, tv_District, tv_City, tv_Latitude, tv_Longitude, tv_siteTowerType;
    EditText et_site_id,et_assetId,et_asdt,et_aedt,et_arId,et_arStatus, et_rj_cat, et_rj_rmk, et_rj_date, et_projectName, et_productName, et_soNumber, et_WorkOrderNumber,
            et_Region, et_District, et_City, et_Latitude, et_Longitude, et_siteTowerType;

    boolean txnStatus;
    int currFieldId;
    String str_reject_approval_remarks = "",scheduledDate, siteId, activityTypeId, dgType, paramName, activityStatus, latitude, longitude, actStaus, imgUploadFlag,
            sName, txnId, Status,str_rating="",str_photo_info="",
            str_quality_pm_task="",str_visual_check1="",str_visual_check2="",str_visual_check3="";;
    JSONObject savedDataJsonObj = null;
    JSONObject savedDataJsonObj1 = null;
    JSONObject savedDataJsonObjRemarks = null;
    JSONObject savedDataJsonObjRemarks1 = null;
    String moduleUrl = "";
    AppPreferences mAppPreferences;
    PackageInfo pInfo = null;//0.3
    private static LinkedHashMap<Integer, ChecklistDetail> hmPmCheklist = new LinkedHashMap<Integer, ChecklistDetail>();
    private static Map<String, TextView> grpList = new HashMap<String, TextView>();
    LayoutParams GalleryParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    ExpandableHeightGridView pre_grid = null, post_grid = null;
    JSONArray preImgInfoArray, postImgInfoArray, jsonArrStrImg;
    private IntentIntegrator qrScan;
    ScrollView scrollView;
    ProgressDialog loadChecklist;
    int scroolFlag = 0;
    String str_rj_remarks = "", str_rj_category = "";
    ArrayList<String> rj_cate;
    int alertCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadChecklist = new ProgressDialog(PMChecklistApproval.this);
        loadChecklist.setMessage("Please Wait Checklist is Loading....");
        loadChecklist.show();


        if(getIntent().getExtras()!=null
                && getIntent().getExtras().getString("assetCondition")!=null
                && getIntent().getExtras().getString("assetCondition").
                equalsIgnoreCase("Asset missing")){
            if (loadChecklist != null && loadChecklist.isShowing()) {
                loadChecklist.dismiss();
            }
        }


        try {

            op = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.no_media_default)
                    .showImageForEmptyUri(R.drawable.no_media_default)
                    .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                    .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                    .build();

            setContentView(R.layout.pm_checklist_form);

            scrollView = (ScrollView) findViewById(R.id.scrollView);

            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    scroolFlag = 1;
                }
            });

            mAppPreferences = new AppPreferences(PMChecklistApproval.this);
            mAppPreferences.setTrackMode(0);

            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                //e.printStackTrace();
            }

            GalleryParam.gravity = Gravity.CENTER_HORIZONTAL;
            GalleryParam.setMargins(10, 10, 10, 0);
            //Calling method to find IDs of TextView etc.
            init();
            if (imgUploadFlag.equalsIgnoreCase("1")) {
                setImageActivityLevel();
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addElementsToLayout('P', 0, null);
                }
            }, 100);
            if (getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")) {
                if (Utils.isNetworkAvailable(PMChecklistApproval.this)) {
                    callGetFildsValueApi(txnId);
                }
            }
            // on click of Submit Button
            tvSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //Validating all Mandatory Fields.
                    if(getIntent().getExtras().getString("rating").equalsIgnoreCase("1")){
                        approvedDialog();
                    }else{
                        str_rating = "";
                        str_photo_info = "";
                        str_quality_pm_task = "";
                        str_visual_check1 = "";
                        str_visual_check2 = "";
                        str_visual_check3 = "";
                        submitData("R", "", "", "");
                    }

                }
            });

            tv_reject.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //backScreen();
                    rejectDialog();
                }
            });

            bt_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFinishing()) {
                        //backButtonAlert( 1, "509", "27", "510" );
                        backButtonAlert(2, "291", "63", "64",
                                -1, -1, "", "");
                    }
                }
            });

            tv_spare_parts.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        Intent i = new Intent(PMChecklistApproval.this, SparePart.class);
                        i.putExtra("tranID", txnId);
                        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
                        i.putExtra("activityMode", "2");
                        i.putExtra("typeId", activityTypeId);
                        startActivity(i);
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tv_add_corrective_tt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        if (!alarmDescDataType().equalsIgnoreCase("")) {
                            if (Utils.isNetworkAvailable(PMChecklistApproval.this)) {
                                new AlarmDescTask(PMChecklistApproval.this).execute();
                            } else {
                                // Toast.makeText(IncidentManagement.this,"No internet connection.Please download meta data.",Toast.LENGTH_LONG).show();
                                Utils.toast(PMChecklistApproval.this, "17");
                            }
                        } else {
                            Intent i = new Intent(PMChecklistApproval.this, AddTicket.class);
                            i.putExtra("ticket_type",
                                    Utils.msg(PMChecklistApproval.this, "934"));
                            i.putExtra("site_id", siteId);
                            i.putExtra("ref_tkt_id", txnId);
                            i.putExtra("remarks",
                                    Utils.msg(PMChecklistApproval.this, "936")+txnId);
                            mAppPreferences.saveRefMode(3);
                            mAppPreferences.setTTModuleSelection("654");
                            startActivity(i);
                        }
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            String s = e.getMessage();
            Toast.makeText(PMChecklistApproval.this, "Please Contact iTower helpdesk", Toast.LENGTH_LONG).show();
        }
    }



    private void submitData(String oper, String RejectionRemarks, String Rejectioncategeroy, String RejTxt) {
        try {

            String addParams = "";
            boolean isValidate = false;
            if (oper.equalsIgnoreCase("J")) {
                isValidate = true;
            } else {
                isValidate = validate();
            }
            if (isValidate) {

                if (checklist().length() == 0) {
                    Toast.makeText(PMChecklistApproval.this, "Checklist not saved, Please resubmit it.", Toast.LENGTH_LONG).show();
                    return;
                }

                String current_time = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());
                JSONObject obj = new JSONObject();
                obj.put(AppConstants.PRE_IMG, "");
                obj.put(AppConstants.POST_IMG, "");
                obj.put(AppConstants.REJECTION_REMARKS, str_reject_approval_remarks);
                //approval rating
                obj.put("isRating", str_rating);
                obj.put("photoRating", str_photo_info);
                obj.put("pmRating", str_quality_pm_task);
                obj.put("visualCheck1", str_visual_check1);
                obj.put("visualCheck2", str_visual_check2);
                obj.put("visualCheck3", str_visual_check3);

                //obj.put(AppConstants.REJECTION_REMARKS, RejectionRemarks);
                obj.put(AppConstants.REJECTION_CATEGORY, Rejectioncategeroy);
                if (oper.equalsIgnoreCase("J")) {
                    obj.put(AppConstants.REJECTION_TXT, RejTxt);
                    obj.put(AppConstants.PRE_IMGS, "");
                    obj.put(AppConstants.POST_IMGS, "");
                }
                if (checklist().length() == 0) {
                    obj.put(AppConstants.CHECK_LIST, mAppPreferences.getPMchecklist());
                } else {
                    obj.put(AppConstants.CHECK_LIST, checklist());
                }
                obj.put(AppConstants.UPLOAD_TYPE, "PM");
                obj.put(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId());
                obj.put(AppConstants.ROLE_ID, mAppPreferences.getRoleId());
                obj.put(AppConstants.SITE_ID_ALIAS, siteId);
                obj.put(AppConstants.ACTIVITY_TYPE_ID, activityTypeId);
                obj.put(AppConstants.STATUS_ALIAS, activityStatus);
                obj.put(AppConstants.SCHEDULE_DATE, scheduledDate);
                obj.put(AppConstants.DG, dgType);

                obj.put("assetId",getIntent().getExtras().getString("assetId"));
                obj.put("equipmentId",getIntent().getExtras().getString("equipmentId"));

                obj.put(AppConstants.CURRENT_TIME, current_time);
                obj.put("lat", latitude);
                obj.put("long", longitude);
                obj.put(AppConstants.REMARKS, "");
                String module = "PM";
                String tktId = "";

                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")
                        || mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                    module = "H";
                    tktId = txnId;
                }
                addParams = "appversion=" + pInfo.versionName + "~oper=" + oper + "~moduleType=" + module + "~tktId=" + tktId;
                obj.put(AppConstants.ADD_PARAM, addParams);
                obj.put(AppConstants.LANGUAGE_CODE, mAppPreferences.getLanCode());

                if (Utils.isNetworkAvailable(PMChecklistApproval.this) && mAppPreferences.getSavePMBackgroundEnable() == 0) {
                    //Check if data submit in background is enabled or not.
                    AsynTaskService task = new AsynTaskService(obj.toString(),"R");
                    task.execute(moduleUrl + WebMethods.url_SaveAPI);
                } else {
                    //If Network is not available the store data in local database
                    DataBaseHelper db = new DataBaseHelper(PMChecklistApproval.this);
                    db.open();
                    db.insertTxnDataLocally(txnId+getIntent().getExtras().getString("assetId"), "2", obj.toString(), jsonArrStrImg.toString(), preImgInfoArray.toString(), postImgInfoArray.toString(), mAppPreferences.getUserId(), moduleUrl + WebMethods.url_SaveAPI);
                    db.close();
                    Utils.toast(PMChecklistApproval.this, "66");
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PMChecklistApproval.this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            //Toast.makeText(PMChecklist.this,"Please Contact iTower helpdesk",Toast.LENGTH_LONG).show();
        }
    }

    // Method to find IDs and getting previous screen Values
    private void init() {
        tv_rj_cat = (TextView) findViewById(R.id.tv_rj_cat);
        txnId = getIntent().getExtras().getString("txn");
        Status = getIntent().getExtras().getString("Status");
        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklistApproval.this);
        dbHelper.open();
        moduleUrl = dbHelper.getModuleIP("Preventive");
        Utils.textViewProperty(PMChecklistApproval.this, tv_rj_cat, Utils.msg(PMChecklistApproval.this, "469"));
        rj_cate = dbHelper.getInciParam1("117", tv_rj_cat, "654");
        //get Data which is auto saved from mobile db if any using txn id
        String savedDataJson = dbHelper.getAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
        String savedDataJsonRemarks = dbHelper.getAutoSaveReviewRemarks(txnId,getIntent().getExtras().getString("assetId"));
        //Check if transaction is present in pending data
        int count = dbHelper.getPendingTxnCount(txnId);
        dbHelper.close();

        txnStatus = count > 0 ? true : false;

        if (savedDataJson != null && savedDataJson.length() > 0) {
            try {
                savedDataJsonObj1 = new JSONObject(savedDataJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            txnStatus = false;
        }

        if (savedDataJsonRemarks != null && savedDataJsonRemarks.length() > 0) {
            try {
                savedDataJsonObjRemarks1 = new JSONObject(savedDataJsonRemarks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        preImgInfoArray = new JSONArray();
        postImgInfoArray = new JSONArray();
        jsonArrStrImg = new JSONArray();
        post_grid = (ExpandableHeightGridView) findViewById(R.id.post_grid);
        pre_grid = (ExpandableHeightGridView) findViewById(R.id.pre_grid);
        ll_pre_act_photo = (LinearLayout) findViewById(R.id.ll_pre_act_photo);
        ll_post_act_img = (LinearLayout) findViewById(R.id.ll_post_act_img);

        btnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        btnTakePhoto.setVisibility(View.GONE);
        btnTakePrePhoto = (Button) findViewById(R.id.btn_take_pre_photo);
        btnTakePrePhoto.setVisibility(View.GONE);
        PreimageList = new ArrayList<BeanGetImage>();
        PostimageList = new ArrayList<BeanGetImage>();

        hmPmCheklist.clear();

        paramName = getIntent().getExtras().getString("paramName");
        activityStatus = getIntent().getExtras().getString("Status");
        scheduledDate = getIntent().getExtras().getString("scheduledDate");
        siteId = getIntent().getExtras().getString("siteId");
        activityTypeId = getIntent().getExtras().getString("activityTypeId");
        dgType = getIntent().getExtras().getString("dgType");
        actStaus = getIntent().getExtras().getString("S");
        imgUploadFlag = getIntent().getExtras().getString("imgUploadFlag");


        tv_rj_rmk = (TextView) findViewById(R.id.tv_rj_rmk);
        tv_rj_date = (TextView) findViewById(R.id.tv_rj_date);
        tv_rj_region = (TextView) findViewById(R.id.tv_rj_region);

        Utils.groupTV(PMChecklistApproval.this, tv_rj_region, Utils.msg(PMChecklistApproval.this, "461"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_rj_rmk, Utils.msg(PMChecklistApproval.this, "470"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_rj_date, Utils.msg(PMChecklistApproval.this, "471"));

        et_rj_cat = (EditText) findViewById(R.id.et_rj_cat);
        Utils.editTextProperty(PMChecklistApproval.this, et_rj_cat);
        et_rj_cat.setEnabled(false);
        et_rj_rmk = (EditText) findViewById(R.id.et_rj_rmk);
        Utils.editTextProperty(PMChecklistApproval.this, et_rj_rmk);
        et_rj_rmk.setEnabled(false);
        et_rj_date = (EditText) findViewById(R.id.et_rj_date);
        Utils.editTextProperty(PMChecklistApproval.this, et_rj_date);
        et_rj_date.setEnabled(false);

        tv_rj_cat.setVisibility(View.GONE);
        tv_rj_rmk.setVisibility(View.GONE);
        tv_rj_date.setVisibility(View.GONE);
        et_rj_cat.setVisibility(View.GONE);
        et_rj_rmk.setVisibility(View.GONE);
        et_rj_date.setVisibility(View.GONE);
        tv_rj_region.setVisibility(View.GONE);

        if (dgType == null || dgType.isEmpty()) {
            dgType = "0";
        }

        linearNoDataFound = (RelativeLayout) findViewById(R.id.rl_no_data_found);
        linear = (LinearLayout) findViewById(R.id.ll_textview);
        linearRemarks = (LinearLayout) findViewById(R.id.ll_remarks);

        tv_spare_parts = (TextView) findViewById(R.id.tv_spare_parts);
        Utils.msgText(PMChecklistApproval.this, "279", tv_spare_parts);
        tv_spare_parts.setPaintFlags(tv_spare_parts.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_spare_parts.setVisibility(View.VISIBLE);

        tv_add_corrective_tt = (TextView) findViewById(R.id.tv_add_corrective_tt);
        Utils.msgText(PMChecklistApproval.this, "933", tv_add_corrective_tt);
        tv_add_corrective_tt.setPaintFlags(tv_add_corrective_tt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_add_corrective_tt.setVisibility(View.VISIBLE);

        if(getIntent().getExtras().getString("rating").equalsIgnoreCase("1")){
            tv_add_corrective_tt.setVisibility(View.VISIBLE);
        }else{
            tv_add_corrective_tt.setVisibility(View.GONE);
        }
        tv_add_corrective_tt.setVisibility(View.GONE);


        tv_click_img = (TextView) findViewById(R.id.tv_click_img);
        tv_click_pre_img = (TextView) findViewById(R.id.tv_click_pre_img);

        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        tv_reject = (TextView) findViewById(R.id.tv_reject);
        tv_siteId = (TextView) findViewById(R.id.tv_siteId);
        tv_projectName = (TextView) findViewById(R.id.tv_projectName);
        tv_productName = (TextView) findViewById(R.id.tv_productName);
        tv_soNumber = (TextView) findViewById(R.id.tv_soNumber);
        tv_WorkOrderNumber = (TextView) findViewById(R.id.tv_WorkOrderNumber);
        tv_Region = (TextView) findViewById(R.id.tv_Region);
        tv_District = (TextView) findViewById(R.id.tv_District);
        tv_City = (TextView) findViewById(R.id.tv_City);
        tv_Latitude = (TextView) findViewById(R.id.tv_Latitude);
        tv_Longitude = (TextView) findViewById(R.id.tv_Longitude);
        tv_siteTowerType = (TextView) findViewById(R.id.tv_siteTowerType);
        tv_assetId = (TextView) findViewById(R.id.tv_assetId);
        tv_asdt = (TextView) findViewById(R.id.tv_asdt);
        tv_aedt = (TextView) findViewById(R.id.tv_aedt);
        tv_arId = (TextView) findViewById(R.id.tv_arId);
        tv_arStatus = (TextView) findViewById(R.id.tv_arStatus);
        tv_asdt.setVisibility(View.VISIBLE);
        tv_aedt.setVisibility(View.VISIBLE);
        tv_arId.setVisibility(View.VISIBLE);
        tv_arStatus.setVisibility(View.VISIBLE);



        et_site_id = (EditText) findViewById(R.id.et_site_id);
        et_projectName = (EditText) findViewById(R.id.et_projectName);
        et_productName = (EditText) findViewById(R.id.et_productName);
        et_soNumber = (EditText) findViewById(R.id.et_soNumber);
        et_WorkOrderNumber = (EditText) findViewById(R.id.et_WorkOrderNumber);
        et_Region = (EditText) findViewById(R.id.et_Region);
        et_District = (EditText) findViewById(R.id.et_District);
        et_City = (EditText) findViewById(R.id.et_City);
        et_Latitude = (EditText) findViewById(R.id.et_Latitude);
        et_assetId = (EditText) findViewById(R.id.et_assetId);
        if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
            et_assetId.setText(getIntent().getExtras().getString("qrCode"));
        }

        et_asdt = (EditText) findViewById(R.id.et_asdt);
        et_aedt = (EditText) findViewById(R.id.et_aedt);
        et_arId = (EditText) findViewById(R.id.et_arId);
        et_arStatus = (EditText) findViewById(R.id.et_arStatus);
        et_asdt.setVisibility(View.VISIBLE);
        et_aedt.setVisibility(View.VISIBLE);
        et_arId.setVisibility(View.VISIBLE);
        et_arStatus.setVisibility(View.VISIBLE);

        Utils.editTextProperty(PMChecklistApproval.this,et_asdt);
        et_asdt.setEnabled( false );
        Utils.editTextProperty(PMChecklistApproval.this,et_aedt);
        et_aedt.setEnabled( false );
        Utils.editTextProperty(PMChecklistApproval.this,et_arId);
        et_arId.setEnabled( false );
        Utils.editTextProperty(PMChecklistApproval.this,et_arStatus);
        et_arStatus.setEnabled( false );

        et_asdt.setText(getIntent().getExtras().getString("asdt"));
        et_aedt.setText(getIntent().getExtras().getString("aedt"));
        et_arId.setText(getIntent().getExtras().getString("arId"));
        et_arStatus.setText(getIntent().getExtras().getString("amStatus"));




        et_Longitude = (EditText) findViewById(R.id.et_Longitude);
        et_siteTowerType = (EditText) findViewById(R.id.et_siteTowerType);

        et_site_id = (EditText) findViewById( R.id.et_site_id );
        Utils.editTextProperty(PMChecklistApproval.this,et_site_id);
        et_site_id.setEnabled( false );

        Utils.editTextProperty(PMChecklistApproval.this, et_projectName);
        et_projectName.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_productName);
        et_productName.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_soNumber);
        et_soNumber.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_WorkOrderNumber);
        et_WorkOrderNumber.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_Region);
        et_Region.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_District);
        et_District.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_City);
        et_City.setEnabled(false);

        Utils.editTextProperty(PMChecklistApproval.this, et_Latitude);
        et_Latitude.setEnabled(false);

        Utils.editTextProperty(PMChecklistApproval.this, et_assetId);
        et_assetId.setEnabled(false);


        Utils.editTextProperty(PMChecklistApproval.this, et_Longitude);
        et_Longitude.setEnabled(false);
        Utils.editTextProperty(PMChecklistApproval.this, et_siteTowerType);
        et_siteTowerType.setEnabled(false);

        //if transaction is present in pending transactions the disable submit button.
        if (txnStatus) {
            tvSubmit.setBackgroundResource(R.drawable.disable);
            tvSubmit.setEnabled(false);
        } else {
            tvSubmit.setEnabled(true);
        }

        if (mAppPreferences.getSiteNameEnable() == 1 && getIntent().getExtras().getString("siteName") != null) {
            sName = "(" + getIntent().getExtras().getString("siteName") + ")";
        } else {
            sName = "";
        }
        et_site_id.setText(siteId + sName);
        Utils.textViewProperty(PMChecklistApproval.this,tv_asdt,
                "Actual Start Date");
        Utils.textViewProperty(PMChecklistApproval.this,tv_aedt,
                "Actual End Date");
        Utils.textViewProperty(PMChecklistApproval.this,tv_arId,
                "Access ID");
        Utils.textViewProperty(PMChecklistApproval.this, tv_arStatus,
                "Access Request Status");

        Utils.textViewProperty(PMChecklistApproval.this,tv_siteId,Utils.msg(PMChecklistApproval.this,"77"));
        Utils.textViewProperty(PMChecklistApproval.this,tv_click_img,Utils.msg(PMChecklistApproval.this,"301"));
        Utils.textViewProperty(PMChecklistApproval.this,tv_click_pre_img,Utils.msg(PMChecklistApproval.this,"299"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_projectName, Utils.msg(PMChecklistApproval.this, "803"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_productName, Utils.msg(PMChecklistApproval.this, "802"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_soNumber, Utils.msg(PMChecklistApproval.this, "804"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_WorkOrderNumber, Utils.msg(PMChecklistApproval.this, "805"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_Region, Utils.msg(PMChecklistApproval.this, "806"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_District, Utils.msg(PMChecklistApproval.this, "807"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_City, Utils.msg(PMChecklistApproval.this, "808"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_Latitude, Utils.msg(PMChecklistApproval.this, "809"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_Longitude, Utils.msg(PMChecklistApproval.this, "810"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_siteTowerType, Utils.msg(PMChecklistApproval.this, "811"));
        Utils.textViewProperty(PMChecklistApproval.this, tv_assetId, Utils.msg(PMChecklistApproval.this, "880"));

        Utils.msgText(PMChecklistApproval.this, "465", tvSubmit); // set text Submit
        Utils.msgText(PMChecklistApproval.this, "466", tv_reject); // set text Submit
        tv_reject.setVisibility(View.VISIBLE);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        bt_back = (Button) findViewById(R.id.button_back);
        rl_submit = (LinearLayout) findViewById(R.id.rl_submit);
        Utils.msgButton(PMChecklistApproval.this, "71", bt_back);
        Utils.msgText(PMChecklistApproval.this, "226", tv_no_data); // set text General PM
        Utils.msgText(PMChecklistApproval.this, activityTypeId, tvHeader);
    }

    //Add elements to view.
    public void addElementsToLayout(char parentFlag, int prvfieldId, String fieldIdList) {
        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklistApproval.this);
        dbHelper.open();
        if (fieldIdList != null) {
            fieldIdList = fieldIdList.replace("[", "").replace("]", "");
        }

        Cursor cursor = dbHelper.getReviewerChecklist(activityTypeId,
                fieldIdList == null ? 0 : 2, fieldIdList);
        StringBuilder imgMsg = new StringBuilder();

        ChecklistDetail pmCheckList;

        TextView textView, tvRemarksLink, capturePrePhoto, capturePostPhoto, tv_divider;
        EditText etViRemark, etReviewRemarks;
        //Button bt_pre_pic,bt_post_pic;  //0.6
        RecyclerView pre_photo_gallery, post_photo_gallery;


        int prvFieldId = 0;
        int viewIndex = 0;
        String prvGroupName = "";
        int nextFieldId = 0;

        if (parentFlag == 'C') {
            prvFieldId = prvfieldId;
            prvGroupName = hmPmCheklist.get(prvfieldId).getGrpName();

            viewIndex = ((ViewGroup) linear).indexOfChild(hmPmCheklist.get(prvfieldId).getTvDivider());
            View view = ((ViewGroup) linear).getChildAt(viewIndex + 1);

            if (view != null) {
                nextFieldId = view.getId();
                //Check if next field is group header. Group header will have field id >=5000. Then get next view from layout
                if (nextFieldId >= 5000) {
                    view = ((ViewGroup) linear).getChildAt(viewIndex + 2);
                    nextFieldId = view.getId();
                }
            }
            viewIndex++;
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {

                imgMsg.setLength(0);

                //Add Header if groupName is R
                if (!prvGroupName.equals(cursor.getString(cursor.getColumnIndex("groupName")))) {

                    if (grpList.containsKey(cursor.getString(cursor.getColumnIndex("groupName")))) {
                        linear.removeView(grpList.get(cursor.getString(cursor.getColumnIndex("groupName"))));
                    }

                    TextView tvGroupName = new TextView(this);
                    tvGroupName.setId(cursor.getInt(cursor.getColumnIndex("fieldId")));
                    if(viewIndex<linear.getChildCount()){
                        linear.addView(Utils.groupTV(PMChecklistApproval.this, tvGroupName, (cursor.getString(cursor.getColumnIndex("groupName")))), viewIndex);
                        viewIndex++;
                    }
                    grpList.put(cursor.getString(cursor.getColumnIndex("groupName")), tvGroupName);
                    //continue;
                }

                pmCheckList = new ChecklistDetail();

                //in case first element avoid setting previous field id
                if (prvFieldId != 0) {
                    pmCheckList.setPrvId(prvFieldId);
                }

                pmCheckList.setRemarkMadatory(cursor.getString(cursor.getColumnIndex("rMandatory")).charAt(0));
                pmCheckList.setGrpName(cursor.getString(cursor.getColumnIndex("groupName")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setFieldName(cursor.getString(cursor.getColumnIndex("fieldName")));
                pmCheckList.setVisitorStatus(cursor.getString(cursor.getColumnIndex("status")));
                pmCheckList.setVisitorRemarks(cursor.getString(cursor.getColumnIndex("viRemark")));
                pmCheckList.setId(cursor.getInt(cursor.getColumnIndex("fieldId")));
                pmCheckList.setrFlag(cursor.getString(cursor.getColumnIndex("pFlag")).charAt(0));
                pmCheckList.setDropDownValue(cursor.getString(cursor.getColumnIndex("value")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setLength(cursor.getString(cursor.getColumnIndex("length")));
                pmCheckList.setDataType(cursor.getString(cursor.getColumnIndex("dataType")).charAt(0));
                pmCheckList.setMadatory(cursor.getString(cursor.getColumnIndex("mandatory")));
                pmCheckList.setRemarkMada(cursor.getString(cursor.getColumnIndex("rMandatory")));
                pmCheckList.setPrePhotoConfig(cursor.getString(cursor.getColumnIndex("preImgConfig"))); //0.6
                pmCheckList.setPrePhotoTag(cursor.getString(cursor.getColumnIndex("preImgTag"))); //0.6
                pmCheckList.setPostPhotoTag(cursor.getString(cursor.getColumnIndex("postImgTag")));//0.6
                pmCheckList.setPostPhotoConfig(cursor.getString(cursor.getColumnIndex("postImgConfig")));//0.6
                pmCheckList.setChildIteams(spiltChild(cursor.getString(cursor.getColumnIndex("pActivityId"))));

                prvFieldId = pmCheckList.getId();
                prvGroupName = pmCheckList.getGrpName();

                hmPmCheklist.put(pmCheckList.getId(), pmCheckList);

                if (imgUploadFlag.equalsIgnoreCase("2")) {

                    //Add Pre Photo extView if pre photo configured for checklist item
                    if (pmCheckList.getPreMinImg() > -1 && pmCheckList.getPreMaxImg() > -1) {
                        //0.6
                        //imgMsg.append("Capture (Minimum " + pmCheckList.getPreMinImg() + " & Maximum " + pmCheckList.getPreMaxImg() + ") pre photo");
                        imgMsg.append("pre photo");
                        capturePrePhoto = new TextView(this);
                        capturePrePhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(PMChecklistApproval.this, capturePrePhoto, imgMsg.toString());

                        //pre photo button //0.6
                        //bt_pre_pic = new Button(PMChecklistApproval.this);
                        //imageButtonProperty(bt_pre_pic, pmCheckList.getId(), "Pre");

                        //pre photo gallery
                        pre_photo_gallery = new RecyclerView(PMChecklistApproval.this);
                        recyclerViewProperty(pre_photo_gallery, pmCheckList.getId() + 100);

                        pmCheckList.setCapturePrePhoto(capturePrePhoto);
                        //pmCheckList.setPrePhoto(bt_pre_pic); //0.6
                        pmCheckList.setPre_grid(pre_photo_gallery);
                        pmCheckList.setPreImgCounter(1);
                        setImages(1, pmCheckList.getId());
                        //pmCheckList.getPrePhoto().setVisibility(View.VISIBLE); //0.6
                        //pmCheckList.getCapturePrePhoto().setVisibility(View.VISIBLE);//0.6
                    }

                    //Add Post Photo TextView if pre photo configured for checklist item
                    if (pmCheckList.getPostMinImg() > -1 && pmCheckList.getPostMaxImg() > -1) {
                        imgMsg.setLength(0);
                        //0.6
                        //imgMsg.append("Capture (Minimum " + pmCheckList.getPostMinImg() + " & Maximum " + pmCheckList.getPostMaxImg() + ") post photo");
                        imgMsg.append("post photo");

                        capturePostPhoto = new TextView(this);
                        capturePostPhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(PMChecklistApproval.this, capturePostPhoto, imgMsg.toString());

                        //post photo button  //0.6
                        //bt_post_pic = new Button(PMChecklistApproval.this);
                        //imageButtonProperty(bt_post_pic, pmCheckList.getId(), "Post");

                        //post photo gallery
                        post_photo_gallery = new RecyclerView(PMChecklistApproval.this);
                        recyclerViewProperty(post_photo_gallery, pmCheckList.getId());
                        pmCheckList.setCapturePostPhoto(capturePostPhoto);
                        //pmCheckList.setPostPhoto(bt_post_pic); //0.6
                        pmCheckList.setPost_grid(post_photo_gallery);
                        pmCheckList.setPostImgCounter(1);
                        setImages(2, pmCheckList.getId());
                        //pmCheckList.getPostPhoto().setVisibility(View.VISIBLE); //0.6
                        //pmCheckList.getCapturePostPhoto().setVisibility(View.VISIBLE); //0.6
                    }
                }

                //For text view use show checklist name
                textView = new TextView(this);
                textView.setId(pmCheckList.getId());
                Utils.textViewProperty(PMChecklistApproval.this, textView, pmCheckList.getFieldName().trim());
                pmCheckList.setTextView(textView);

                tv_divider = new TextView(this);
                Utils.textViewDivider(PMChecklistApproval.this, tv_divider);
                if (imgUploadFlag.equalsIgnoreCase("2")) {
                    tv_divider.setVisibility(View.VISIBLE);
                } else {
                    tv_divider.setVisibility(View.GONE);
                }

                pmCheckList.setTvDivider(tv_divider);

                // remarks
                tvRemarksLink = new TextView(this);
                etViRemark = new EditText(this);
                etViRemark.setText(getSavedRemarks("" + pmCheckList.getId()));
                remarklinkTV(tvRemarksLink, etViRemark, pmCheckList.getId());

                //etReviewRemarks = new EditText(this);// 0.5
                //etReviewRemarks.setId(pmCheckList.getId());
                //etReviewRemarks.setText(getSavedReviewRemarks(""+pmCheckList.getId()));
                //etReviewRemarks.setTextColor(Color.parseColor("#000000"));

                pmCheckList.setTvRemarksLink(tvRemarksLink);
                pmCheckList.setEtViRemarks(etViRemark);
                //pmCheckList.setEtReviewRemarks(etReviewRemarks);

                if (pmCheckList.getCapturePrePhoto() != null) {
                    linear.addView(pmCheckList.getCapturePrePhoto(), viewIndex);
                    viewIndex++;
                    //linear.addView(pmCheckList.getPrePhoto(),viewIndex); //0.6
                    //viewIndex++; //0.6
                    linear.addView(pmCheckList.getPre_grid(), viewIndex);
                    viewIndex++;
                }

                linear.addView(pmCheckList.getTextView(), viewIndex);
                viewIndex++;

                switch (pmCheckList.getFieldType()) {
                    case 'E':
                    case 'D':
                    case 'Q':
                    case 'R':
                    case 'T':
                        addEditText(pmCheckList, viewIndex);
                        viewIndex++;
                        break; // break is optional
                    case 'S':
                    case 'V':
                        addSpinner(pmCheckList, viewIndex);
                        viewIndex++;
                        break; // break is optional
                    default:
                }

                linear.addView(pmCheckList.getTvRemarksLink(), viewIndex);
                viewIndex++;

                if (pmCheckList.getCapturePostPhoto() != null) {
                    linear.addView(pmCheckList.getCapturePostPhoto(), viewIndex);
                    viewIndex++;
                    //linear.addView(pmCheckList.getPostPhoto(),viewIndex);
                    //viewIndex++;
                    linear.addView(pmCheckList.getPost_grid(), viewIndex);
                    viewIndex++;
                }

                linear.addView(pmCheckList.getTvDivider(), viewIndex);
                viewIndex++;

                //Show only parent items initially

                pmCheckList.getTextView().setVisibility(View.VISIBLE);
                pmCheckList.getTvDivider().setVisibility(View.VISIBLE);
                pmCheckList.getTvRemarksLink().setVisibility(View.VISIBLE);
                hmPmCheklist.put(pmCheckList.getId(), pmCheckList);

            }
        }

        if (nextFieldId != 0 && hmPmCheklist.containsKey(nextFieldId)) {
            hmPmCheklist.get(nextFieldId).setPrvId(prvFieldId);
        }
        dbHelper.close();
    }

    // Method to add EditText in form.
    private void addEditText(ChecklistDetail checkListDtl, int viewIndex) {

        final EditText editText = new EditText(this);
        Utils.editTextProperty(PMChecklistApproval.this, editText);
        editText.setText(getSavedValue("" + checkListDtl.getId()));
        checkListDtl.setFieldCat('E');
        editText.setEnabled(true);
        checkListDtl.getTvRemarksLink().setEnabled(true);
        checkListDtl.setEditText(editText);
        dataType(checkListDtl);

        linear.addView(checkListDtl.getEditText(), viewIndex);

        checkListDtl.getEditText().setVisibility(View.VISIBLE);

        if (checkListDtl.getFieldType() == 'D') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.icon_calender);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            dateDialog(checkListDtl.getEditText(), checkListDtl.getId(), checkListDtl.getTextView());
        } else if (checkListDtl.getFieldType() == 'T') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.icon_calender);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            timeDialog(checkListDtl.getEditText(), checkListDtl.getId(), checkListDtl.getTextView());
        }
        else if (checkListDtl.getFieldType() == 'Q') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.bar_code);
            checkListDtl.getEditText().setPadding(7, 0, 100, 0);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            qrCode(checkListDtl.getEditText(), checkListDtl.getId());
        } else if (checkListDtl.getFieldType() == 'R') {
            checkListDtl.getEditText().setKeyListener(null);
        }
        checkListDtl.getEditText().setId(checkListDtl.getId());
        checkListDtl.setVisible(editText.isEnabled());
        sharePrefence(checkListDtl.getEditText().getId(), checkListDtl.getEditText().getText().toString().trim());

        //register watcher to edittext
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (scroolFlag == 1 && imgUploadFlag.equalsIgnoreCase("2") && s.toString().length() > 0) {

                    int counter = hmPmCheklist.get(editText.getId()).getPreImgCounter() - 1;

                    if (hmPmCheklist.get(editText.getId()).getPreMinImg() > counter) {
                        String m = Utils.msg(PMChecklistApproval.this, "257") + " " + hmPmCheklist.get(editText.getId()).getPreMinImg()
                                + " " + Utils.msg(PMChecklistApproval.this, "258") + " "
                                + hmPmCheklist.get(editText.getId()).getPreMaxImg() + " " + Utils.msg(PMChecklistApproval.this, "299");
                        Utils.toastMsg(PMChecklistApproval.this, m.trim() + "\n" + hmPmCheklist.get(editText.getId()).getFieldName().toString());
                        s.replace(0, s.length(), "");
                        return;
                    }

                    if (!checkPreviousElement(editText.getId(), 0, 2)) {
                        s.replace(0, s.length(), "");
                        return;
                    }
                }
                //generate json and save in share preferences
                sharePrefence(editText.getId(), s.toString());
            }
        };
        checkListDtl.getEditText().addTextChangedListener(watcher);
    }

    // Method to add Spinner in form.
    private void addSpinner(ChecklistDetail checkListDtl, int viewIndex) {

        Spinner spinner;

        if (checkListDtl.getFieldType() == 'V') {
            spinner = new SearchableSpinner(this);
        } else {
            spinner = new Spinner(this);
        }
        addItemsOnSpinner(spinner, checkListDtl.getDropDownValue(), checkListDtl.getId());
        checkListDtl.setFieldCat('S');

        spinner.setId(checkListDtl.getId());
        Utils.spinnerProperty(PMChecklistApproval.this, spinner);
        spinner.setEnabled(true);
        checkListDtl.getTvRemarksLink().setEnabled(true);
        checkListDtl.setSpinner(spinner);
        checkListDtl.getSpinner().setVisibility(View.VISIBLE);

        linear.addView(checkListDtl.getSpinner(), viewIndex);
        sharePrefence(checkListDtl.getId(), spinner.getSelectedItem().toString().trim());
        checkListDtl.setVisible(spinner.isEnabled());
        spinner.setOnItemSelectedListener(this);

    }

    // Method to Add Values in DDL.
    private void addItemsOnSpinner(Spinner spinner, String values, int jsonKey) {

        ArrayList<String> arrList = new ArrayList<String>();
        arrList.add(AppConstants.DD_SELECT_OPTION);
        arrList.addAll(Arrays.asList(values.split(",")));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, arrList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
        int pos = 0;
        if (getSavedValue("" + jsonKey).length() > 0) {
            pos = arrList.indexOf(getSavedValue("" + jsonKey));
        }

        if (pos < arrList.size() && pos >= 0) {
            spinner.setSelection(pos);
        }
    }

    public void setupUI(Spinner view) {
        // Set up touch listener for non-text box views to hide keyboard.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void dateDialog(final EditText et, final int a, final TextView textView) {
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.datePicker(PMChecklistApproval.this, hmPmCheklist.get(a).getEditText(), textView);
            }
        });
    }

    public void timeDialog(final EditText et, final int a, final TextView textView) {
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.timePickerForPM(PMChecklistApproval.this, hmPmCheklist.get(a).getEditText(), textView);
            }
        });
    }



    public void qrCode(final EditText et, final int a) {
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //intializing scan object
                currFieldId = a;
                qrScan = new IntentIntegrator(PMChecklistApproval.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(PMChecklistApproval.this, "511"));
                qrScan.initiateScan();
            }
        });
    }

    // Method To set Validation based on DataType of field and Input Length
    // F means number decimal keypad open.
    // A means all keypad open
    // I means number keypad open
    private void dataType(ChecklistDetail chkListDtl) {

        switch (chkListDtl.getDataType()) {
            case 'F':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new CustomRangeInputFilter(chkListDtl.getLenBefore(), chkListDtl.getLenAfter())});
                //new EditTextLength( chkListDtl.getEditText(), chkListDtl.getLenBefore(), chkListDtl.getLenAfter());
                break;
            case 'A':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        chkListDtl.getLenBefore())});
                break;
            case 'I':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        chkListDtl.getLenBefore())});

                break;
            default:
                break;
        }

    }

    // Method to Validate Mandatory Field
    private boolean validate() {

        boolean status = true;
        GPSTracker gps = new GPSTracker(PMChecklistApproval.this);

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (!Utils.hasPermissions(PMChecklistApproval.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(PMChecklistApproval.this, "Permission denied for device's location.", Toast.LENGTH_LONG).show();
            return false;
        } else if (gps.canGetLocation() == true) {

            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());

            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(PMChecklistApproval.this, "252");
                return false;
            } else {
                latitude = String.valueOf(gps.getLatitude());
                longitude = String.valueOf(gps.getLongitude());
            }
        }

        for (ChecklistDetail checkListItem : hmPmCheklist.values()) {

            //If Checklist item is not visible/enable the continue to next item.
            if (!checkListItem.isVisible()) {
                continue;
            }

            //Check for mandatory field
            if (checkListItem.isMadatory().equalsIgnoreCase("Y")||
                    checkListItem.isMadatory().equalsIgnoreCase("S")) {
                if ((checkListItem.getFieldCat() == 'E' && checkListItem.getEditText().getText().toString().trim().length() == 0)
                        || (checkListItem.getFieldCat() == 'S' && checkListItem.getSpinner().getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    String s = Utils.msg(PMChecklistApproval.this, "256") + " " + checkListItem.getFieldName().toString().trim();
                    Utils.toastMsg(PMChecklistApproval.this, s);
                    return false;
                }
            }

            if (checkListItem.isRemarkMadatory()) {

				/*
					if Remarks is mandatory then validate following
					 - For Text Box - Remarks field is blank or not
					 - For Dropdown - Remark field is blank ot not + value selected in dropdwon is configured with reamsk as mandatory or not
				 */
                if (checkListItem.getEtViRemarks().getText().toString().length() == 0
                        && (checkListItem.getFieldCat() == 'E' || (checkListItem.getFieldCat() == 'S' && checkListItem.getRemarkMada().contains(checkListItem.getSpinner().getSelectedItem().toString())))) {

                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    String s1 = Utils.msg(PMChecklistApproval.this, "255");
                    String s2 = Utils.msg(PMChecklistApproval.this, "106");
                    s1 = s1 + " " + checkListItem.getFieldName().toString().trim() + " " + s2;
                    //Utils.toastMsg( PMChecklist.this, s1 );
                    Toast.makeText(PMChecklistApproval.this, s1.trim(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }

        }
        return true;
    }

    // key + "@~@" + value + "@,@"+remarks$~$+ key + "@~@" + value +
    // "@,@"+remarks;
    // Method to get Filled CheckList Values separated with '~'
    public String checklist() {
        String checkListData = "";

        String value = "";
        for (ChecklistDetail checkListObj : hmPmCheklist.values()) {

            String remarks = checkListObj.getEtViRemarks().getText().toString();

            if (checkListObj.getFieldCat() == 'E') {
                value = checkListObj.getEditText().getText().toString();
            } else if (checkListObj.getFieldCat() == 'S') {

                value = checkListObj.getSpinner().getSelectedItem().toString();

                if (value.equalsIgnoreCase("Select")) {
                    value = "";
                }
            }
            checkListData = checkListData + checkListObj.getId() + "$~$" + value + "$~$" + remarks + "@,@";

        }

        if (checkListData.length() > 0) {
            checkListData = checkListData.substring(0, checkListData.length() - 3);
        }

        if(checkListData.length()==0 && getIntent().getExtras()!=null
                && getIntent().getExtras().getString("assetCondition")!=null
                && getIntent().getExtras().getString("assetCondition").
                equalsIgnoreCase("Asset missing")){
            checkListData = "1";
        }

        return checkListData;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            //requestCode = 2 means image
            if (requestCode != 2) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                hmPmCheklist.get(currFieldId).getTextView().clearFocus();
                hmPmCheklist.get(currFieldId).getTextView().requestFocus();
                if (result != null) {
                    //if qrcode has nothing in it
                    if (result.getContents() == null) {
                        Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                    } else {
                        hmPmCheklist.get(currFieldId).getEditText().setText("" + result.getContents());
                        //Toast.makeText(this, "responce="+result.getContents(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
            //isImgActivityInProgress = false;
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    //Destroyed current screen then call this method
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppPreferences.setTrackMode(1);
    }

    //Call this method when hardware back button press
    @Override
    public void onBackPressed() {

        if (!isFinishing()) {
            //backButtonAlert( 1, "509", "27", "510" );
            backButtonAlert(2, "291", "63", "64",
                    -1, -1, "", "");
        }
    }

    //Open alert dialog confirmation for Do you want to exit in current screen
    public void backButtonAlert(final int mode, String confirmID, String primaryBt,
                                String secondaryBT, final int gridId, final int photoType, final String tag, final String path) {
			/*
		    mode - 3  alert box - for image delete Cofirmation
			mode - 2  click of back initially
			mode - 1 Second alert box - Cofirmation
		 */
        //If transaction is present in pending transaction list then no need to show alert.
        if ((mode == 1 || mode == 2) && txnStatus) {
            finish();
            return;
        }

        final Dialog actvity_dialog;

        actvity_dialog = new Dialog(PMChecklistApproval.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        // UI
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        positive.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        negative.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        title.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        title.setText(Utils.msg(PMChecklistApproval.this, confirmID) + " " + tag);
        positive.setText(Utils.msg(PMChecklistApproval.this, primaryBt));
        negative.setText(Utils.msg(PMChecklistApproval.this, secondaryBT));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mode == 2) {
                    if (actStaus.equalsIgnoreCase("RS")) {
                        if (!isFinishing()) {
                            DataBaseHelper db11 = new DataBaseHelper(PMChecklistApproval.this);
                            db11.open();
                            db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                            db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                            db11.close();
                            finish();
                        }
                    } else {
                        if (imgUploadFlag.equalsIgnoreCase("1")) {
                            DataBaseHelper db11 = new DataBaseHelper(PMChecklistApproval.this);
                            db11.open();
                            db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                            db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                            db11.close();
                            finish();
                        } else {
                            //backButtonAlert(1, "509", "27", "510",
                           //         -1, -1, "", "");
                            finish();
                        }
                    }

                }

                if (mode == 3) {
                    //for delete images
                    DataBaseHelper db = new DataBaseHelper(PMChecklistApproval.this);
                    db.open();
                    db.deleteImagesbyUser("" + gridId, getIntent().getExtras().getString("txn"),
                            "" + photoType,getIntent().getExtras().getString("assetId"), tag);
                    db.close();
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (photoType == 1) {
                        hmPmCheklist.get(gridId).getPrePhotoTag().remove(tag);
                        int a = hmPmCheklist.get(gridId).getPreImgCounter() - 2;
                        if (a <= hmPmCheklist.get(gridId).getPrePhotoTag().size()) {
                            hmPmCheklist.get(gridId).getPrePhotoTag().add(a, tag);
                        }
                    } else {
                        hmPmCheklist.get(gridId).getPostPhotoTag().remove(tag);
                        int a = hmPmCheklist.get(gridId).getPostImgCounter() - 2;
                        if (a <= hmPmCheklist.get(gridId).getPostPhotoTag().size()) {
                            hmPmCheklist.get(gridId).getPostPhotoTag().add(a, tag);
                        }
                    }
                    setImages(photoType, gridId);
                }

                actvity_dialog.cancel();
                if (mode == 1) {
                    finish();
                }
            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (mode == 1) {
                    if (!isFinishing()) {
                        DataBaseHelper db11 = new DataBaseHelper(PMChecklistApproval.this);
                        db11.open();
                        db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                        db11.close();
                        finish();
                    }
                }
            }
        });
    }

    /*
        This method is called from 3 places  - Pre Photo, Post Photo & Remarks
     */
    @Override
    public void onClick(final View arg0) {
        arg0.setClickable(false);
        arg0.postDelayed(new Runnable() {
            @Override
            public void run() {
                arg0.setClickable(true);
            }
        }, 200);

        if (arg0 instanceof TextView) {
            int a = ((TextView) arg0).getId();
            if (!checkPreviousElement(a, 0, 1)) {
                return;
            }
            RemarkPopup(a);
        }
    }

    public void RemarkPopup(final int id) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(PMChecklistApproval.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.remark_popup);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        Utils.msgText(PMChecklistApproval.this, "106", title);

        final EditText et_visitor_status = (EditText) actvity_dialog.findViewById(R.id.et_ip);
        et_visitor_status.setVisibility(View.VISIBLE);

        if (hmPmCheklist.get(id).getVisitorStatus() != null &&
                !hmPmCheklist.get(id).getVisitorStatus().equalsIgnoreCase("null")) {
            et_visitor_status.setText("Value:" + hmPmCheklist.get(id).getVisitorStatus());
        }

        if (hmPmCheklist.get(id).getVisitorRemarks() != null &&
                !hmPmCheklist.get(id).getVisitorRemarks().equalsIgnoreCase("null")) {
            et_visitor_status.setText(et_visitor_status.getText().toString() + "\n" + "Remarks:" + hmPmCheklist.get(id).getVisitorRemarks());
        }
        et_visitor_status.setEnabled(false);

        final EditText et_text = (EditText) actvity_dialog.findViewById(R.id.et_reviewer_remark);
        et_text.setVisibility(View.VISIBLE);
        et_text.setHint("Enter remarks");
        if (hmPmCheklist.get(id).getEtViRemarks().getText().toString() != null
                && !hmPmCheklist.get(id).getEtViRemarks().getText().toString().equalsIgnoreCase("null")) {
            et_text.setText(hmPmCheklist.get(id).getEtViRemarks().getText().toString().trim());
        } else {
            et_text.setText("");
        }

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        bt_cancel.setVisibility(View.GONE);
        Utils.msgButton(PMChecklistApproval.this, "7", positive);
        hmPmCheklist.get(id).getTextView().clearFocus();
        hmPmCheklist.get(id).getTextView().requestFocus();
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sharePrefenceRemarks(id, et_text.getText().toString().trim());
                hmPmCheklist.get(id).getEtViRemarks().setText(et_text.getText().toString().trim());
                actvity_dialog.cancel();

            }
        });
    }

    @Override
    public void onItemSelected(final AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        /* first level checklist handle at initialization time when dynamic form created.
         ** here we handle second, third, forth level checklist.*/

        final ChecklistDetail checklistDetailObj = hmPmCheklist.get(arg0.getId());
        final String selectValue = checklistDetailObj.getSpinner().getSelectedItem().toString();
        final String prvSelectedVal = checklistDetailObj.getSelectedVal();
        sharePrefence(arg0.getId(), selectValue);

        if (scroolFlag == 1 && imgUploadFlag.equalsIgnoreCase("2") && !selectValue.equals("Select")) {

            //Validate if pre min images are configured then validate the number of pre images taken
            if (checklistDetailObj.isMadatory().equalsIgnoreCase("Y")) {
                if (checklistDetailObj.getPreMinImg() > 0 && checklistDetailObj.getPreMinImg() > checklistDetailObj.getPreImgCounter() - 1) {

                    String m = Utils.msg(PMChecklistApproval.this, "257") + " " + checklistDetailObj.getPreMinImg()
                            + " " + Utils.msg(PMChecklistApproval.this, "258") + " "
                            + checklistDetailObj.getPreMaxImg() + " " + Utils.msg(PMChecklistApproval.this, "299");
                    Utils.toastMsg(PMChecklistApproval.this, m.trim() + "\n" + hmPmCheklist.get(arg0.getId()).getFieldName().toString());
                    checklistDetailObj.getSpinner().setSelection(0);
                    return;
                }
            }
            if (!checkPreviousElement(arg0.getId(), 0, 2)) {
                hmPmCheklist.get(arg0.getId()).getSpinner().setSelection(0);
                return;
            }
        }

        final ProgressDialog progressDialog = new ProgressDialog(PMChecklistApproval.this);
        progressDialog.setMessage("Please Wait....");
        if (!"Select".equals(selectValue)) {
            progressDialog.show();
        }
        progressDialog.setCanceledOnTouchOutside(false);
        try {

            // code runs in a thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (checklistDetailObj.getChildConditions() != null && checklistDetailObj.getChildConditions().size() > 0) {

                        if (prvSelectedVal != null && !prvSelectedVal.equals("Select")) {

                            //Hide child fields displayed on previous on selection event
                            if (checklistDetailObj.getChildConditions().containsKey(prvSelectedVal) && checklistDetailObj.getChildConditions().get(prvSelectedVal).size() > 0) {
                                for (Integer childFieldId : checklistDetailObj.getChildConditions().get(prvSelectedVal)) {
                                    hideField(childFieldId);
                                }
                            }
                        }

                        if (!"Select".equals(selectValue)) {
                            //Show the dependent child fields based on selected value
                            if (checklistDetailObj.getChildConditions().containsKey(selectValue) && checklistDetailObj.getChildConditions().get(selectValue).size() > 0) {
                                addElementsToLayout('C', arg0.getId(), checklistDetailObj.getChildConditions().get(selectValue).toString());
                                hmPmCheklist.get(arg0.getId()).getVisibleChildFields().addAll(checklistDetailObj.getChildConditions().get(selectValue));
                            }
                        }
                    }

                    hmPmCheklist.get(arg0.getId()).setSelectedVal(selectValue);

                    if (loadChecklist != null && loadChecklist.isShowing()) {
                        loadChecklist.dismiss();
                    }
                }
            });
        } catch (final Exception ex) {
            Log.i("---", "Exception in thread");
        }


        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadChecklist != null && loadChecklist.isShowing()) {
                    loadChecklist.dismiss();
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }, 300);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public Map<String, List<Integer>> spiltChild(String activityList) {
        Map<String, List<Integer>> childId = null;
        /*
         * point 1. multiple dropdown value has multiple or single child fields
         * as OK~122,123$NOT OK~124 point 2. single dropdown value has multiple
         * or single child fields as OK~122,123 or OK~122
         */
        if (activityList != null) {

            childId = new HashMap<String, List<Integer>>();

            String arr[] = activityList.split("\\$");
            for (int i = 0; i < arr.length; i++) {
                String arr1[] = arr[i].split("~");
                List<Integer> fieldList = new ArrayList<Integer>();
                if (arr1.length > 1) {
                    for (String fieldId : Arrays.asList(arr1[1].split(","))) {
                        fieldList.add(Integer.parseInt(fieldId));
                    }
                    if (fieldList.size() > 0) {
                        childId.put(arr1[0], fieldList);
                    }
                }
            }
        }

        return childId;
    }

    //all non dependent child field removed when choose value from dropdown from spinner
    public void hideField(int fieldId) {

        //if (hmPmCheklist.get( fieldId ).getrFlag() != ' ' && hmPmCheklist.get( fieldId ).getrFlag() == 'C') {
        if (hmPmCheklist.get(fieldId).getrFlag() == 'C') {

            linear.removeView(hmPmCheklist.get(fieldId).getTextView());
            linear.removeView(hmPmCheklist.get(fieldId).getTvDivider());
            linear.removeView(hmPmCheklist.get(fieldId).getTvRemarksLink());
            linear.removeView(hmPmCheklist.get(fieldId).getEtViRemarks());

            if (hmPmCheklist.get(fieldId).getCapturePrePhoto() != null) {
                linear.removeView(hmPmCheklist.get(fieldId).getCapturePrePhoto());
                linear.removeView(hmPmCheklist.get(fieldId).getPre_grid());
            }

            if (hmPmCheklist.get(fieldId).getCapturePostPhoto() != null) {
                linear.removeView(hmPmCheklist.get(fieldId).getCapturePostPhoto());
                linear.removeView(hmPmCheklist.get(fieldId).getPost_grid());
            }

            if (hmPmCheklist.get(fieldId).getFieldCat() == 'E') {
                linear.removeView(hmPmCheklist.get(fieldId).getEditText());
            } else if (hmPmCheklist.get(fieldId).getFieldCat() == 'S') {
                linear.removeView(hmPmCheklist.get(fieldId).getSpinner());
            }

        }

        //Check if child field have futhure visible childs then hide those as well;
        if (hmPmCheklist.get(fieldId).getVisibleChildFields().size() > 0) {
            for (Integer cFieldId : hmPmCheklist.get(fieldId).getVisibleChildFields()) {
                hideField(cFieldId);
            }
        }
        hmPmCheklist.remove(fieldId);
    }

    public class AsynTaskService extends AsyncTask<String, Void, String> {
        public String res = "", data, module,correctiveStatus = "R";
        public ProgressDialog progressDialog = null;
        Response response;

        public AsynTaskService(String data,String correctiveStatus) {
            this.data = data;
            this.correctiveStatus = correctiveStatus;
            progressDialog = new ProgressDialog(PMChecklistApproval.this);
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }

        protected String doInBackground(String... urls) {
            try {
                res = Utils.httpMultipartBackground(urls[0], "PM", jsonArrStrImg.toString(), data, preImgInfoArray.toString(), postImgInfoArray.toString(),
                        "", "", "", "", mAppPreferences.getLanCode(), "", "");
                res = res.replace("[", "").replace("]", "");
                Gson gson = new Gson();
                response = gson.fromJson(res, Response.class);
            } catch (Exception e) {
                response = null;
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (response != null) {
                Utils.toastMsg(PMChecklistApproval.this, response.getMessage());
                if (response.getSuccess().equals("true")) {
                    if(getIntent().getExtras().getString("rating")
                            .equalsIgnoreCase("1")
                            && correctiveStatus.equalsIgnoreCase("R")){
                        correctiveTTconfirmation();
                    }else{
                        backScreen();
                    }
                    /*if (mAppPreferences.getPMAssetBackTask()==0
                            && (mAppPreferences.getPMBackTask() == 0 || mAppPreferences.getPMBackTask() == 1
                            || mAppPreferences.getPMBackTask() == 2 || mAppPreferences.getPMBackTask() == 3
                            || mAppPreferences.getPMBackTask() == 4 || mAppPreferences.getPMBackTask() == 5
                            || mAppPreferences.getPMBackTask() == 6)) {
                        DataBaseHelper gridData = new DataBaseHelper(PMChecklistApproval.this);
                        gridData.open();
                        gridData.clearScheduleList(txnId);
                        gridData.clearMissedList(txnId);
                        gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        gridData.close();
                        Intent i = new Intent(PMChecklistApproval.this, PMTabs.class);
                        startActivity(i);
                        finish();
                    } else if(mAppPreferences.getPMAssetBackTask()==1){
                        mAppPreferences.setPMAssetBackTask(2);
                        DataBaseHelper gridData = new DataBaseHelper(PMChecklistApproval.this);
                        gridData.open();
                        gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        gridData.close();
                        finish();
                    }else {
                        finish();
                    }*/
                }
            } else {
                Utils.toast(PMChecklistApproval.this, "13");
            }
        }
    }

    public void sharePrefence(int id, String s) {
        if (savedDataJsonObj == null) {
            savedDataJsonObj = new JSONObject();
        }

        try {
            savedDataJsonObj.remove("" + id);
            savedDataJsonObj.put("" + id, s);
        } catch (JSONException e) {
        }

        mAppPreferences.setPMchecklistBackUp(savedDataJsonObj.toString());

        DataBaseHelper db10 = new DataBaseHelper(PMChecklistApproval.this);
        db10.open();
        db10.updateAutoSaveChkList(getIntent().getExtras().getString("txn"), "", "",
                mAppPreferences.getPMchecklistBackUp(),
                getIntent().getExtras().getString("assetId"));
        db10.close();
    }

    public void sharePrefenceRemarks(int id, String s) {
        if (savedDataJsonObjRemarks == null) {
            savedDataJsonObjRemarks = new JSONObject();
        }
        try {
            savedDataJsonObjRemarks.remove("" + id);
            savedDataJsonObjRemarks.put("" + id, s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(PMChecklistApproval.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId, savedDataJsonObjRemarks.toString(),getIntent().getExtras().getString("assetId"));
        db10.updateAutoSaveRemarks1(txnId, savedDataJsonObjRemarks.toString(),
                savedDataJsonObjRemarks.toString(),getIntent().getExtras().getString("assetId"));
        db10.close();
    }

    public String getSavedValue(String jsonKey) {

        String autoValue = "";

        if (savedDataJsonObj1 != null) {
            try {
                autoValue = savedDataJsonObj1.getString(jsonKey);
                if (autoValue == null || autoValue.length() == 0) {
                    autoValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return autoValue;
    }

    public String getSavedRemarks(String jsonKey) {

        String autoValue = "";

        if (savedDataJsonObjRemarks1 != null) {
            try {
                autoValue = savedDataJsonObjRemarks1.getString(jsonKey);
                if (autoValue == null || autoValue.length() == 0) {
                    autoValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return autoValue;
    }

    public void recyclerViewProperty(RecyclerView rv, int id) {
        rv.setLayoutParams(GalleryParam);
        rv.setBackgroundColor(getResources().getColor(R.color.bg_color_white));
        rv.setId(id);
    }

    public void remarklinkTV(TextView tv, EditText et, int id) {
        Utils.msgText(PMChecklistApproval.this, "106", tv);
        tv.setId(id);
        tv.setOnClickListener(this);
        Utils.remarksLink(PMChecklistApproval.this, tv);
        et.setTextColor(getResources().getColor(R.color.input_textcolor));
        et.setId(id);
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        List<ViewImage64> imageList = Collections.emptyList();
        Context context;
        int gridId, photoType;

        public HorizontalAdapter(List<ViewImage64> imageList, Context context, int gridId, int photoType) {
            this.imageList = imageList;
            this.context = context;
            this.gridId = gridId;
            this.photoType = photoType;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView delete, grid_image, play_video, doc_image;
            TextView tv_tag, tv_time_stamp, tv_lati, tv_longi;

            public MyViewHolder(View view) {
                super(view);
                grid_image = (ImageView) view.findViewById(R.id.grid_image);
                doc_image = (ImageView) view.findViewById(R.id.grid_image1);
                delete = (ImageView) view.findViewById(R.id.delete);
                play_video = (ImageView) view.findViewById(R.id.play_video);
                tv_lati = (TextView) view.findViewById(R.id.tv_lati);
                tv_longi = (TextView) view.findViewById(R.id.tv_longi);
                tv_tag = (TextView) view.findViewById(R.id.tv_tag);
                tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            }
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_img, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.delete.setVisibility(View.GONE);
            holder.delete.setImageResource(R.drawable.delete_icon);
            holder.grid_image.setVisibility(View.GONE);
            holder.doc_image.setVisibility(View.GONE);
            holder.play_video.setVisibility(View.GONE);
            holder.tv_lati.setText(" ");
            holder.tv_longi.setText(" ");
            holder.tv_time_stamp.setText(" ");
            holder.tv_tag.setText(" ");

            if (imageList != null && imageList.size() > 0 && imageList.get(position).getPath() != null) {
                if (imageList.get(position).getName() != null) {
                    holder.tv_tag.setText(Utils.msg(PMChecklistApproval.this, "473") + " " + imageList.get(position).getName());
                } else {
                    holder.tv_tag.setText(Utils.msg(PMChecklistApproval.this, "473") + " ");
                }

                if (imageList.get(position).getTimeStamp() != null) {
                    holder.tv_time_stamp.setText(Utils.msg(PMChecklistApproval.this, "474") + " " + imageList.get(position).getTimeStamp());
                } else {
                    holder.tv_time_stamp.setText(Utils.msg(PMChecklistApproval.this, "474") + " ");
                }

                if (imageList.get(position).getLati() != null) {
                    holder.tv_lati.setText(Utils.msg(PMChecklistApproval.this, "215") + " : " + imageList.get(position).getLati());
                } else {
                    holder.tv_lati.setText(Utils.msg(PMChecklistApproval.this, "215") + " : ");
                }

                if (imageList.get(position).getLongi() != null) {
                    holder.tv_longi.setText(Utils.msg(PMChecklistApproval.this, "216") + " : " + imageList.get(position).getLongi());
                } else {
                    holder.tv_longi.setText(Utils.msg(PMChecklistApproval.this, "216") + " : ");
                }

                Bitmap bm = null;
                String path = imageList.get(position).getPath();
                File isfile = new File(path);

                if (isfile.exists() && !path.contains("http")) {
                    if (path.contains(".jpeg") || path.contains(".JPEG")
                            || path.contains(".jpg") || path.contains(".JPG")
                            || path.contains(".png") || path.contains(".PNG")) {
                        bm = Utils.decodeFile(path);
                        holder.play_video.setTag("1");
                        holder.delete.setVisibility(View.GONE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        bm = Utils.createVideoThumbNail(path);
                        holder.play_video.setTag("2");
                        holder.delete.setVisibility(View.GONE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.stop_video);
                    } else if (path.contains(".doc") || path.contains(".DOC")
                            || path.contains(".txt") || path.contains(".TXT")
                            || path.contains(".pdf") || path.contains(".PDF")
                            || path.contains(".xlsx") || path.contains(".XLSX")
                            || path.contains(".pptx") || path.contains(".PPTX")
                            || path.contains(".xls") || path.contains(".XLS")
                            || path.contains(".ppt") || path.contains(".PPT")
                            || path.contains(".csv") || path.contains(".CSV")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.GONE);
                        holder.doc_image.setVisibility(View.VISIBLE);
                        holder.grid_image.setVisibility(View.GONE);
                        if (path.contains(".doc") || path.contains(".DOC")) {
                            holder.doc_image.setImageResource(R.drawable.image);
                        } else if (path.contains(".txt") || path.contains(".TXT")) {
                            holder.doc_image.setImageResource(R.drawable.image_txt);
                        } else if (path.contains(".pdf") || path.contains(".PDF")) {
                            holder.doc_image.setImageResource(R.drawable.image_pdf);
                        } else if (path.contains(".xlsx") || path.contains(".XLSX") || path.contains(".xls") || path.contains(".XLS")) {
                            holder.doc_image.setImageResource(R.drawable.image_xls);
                        } else if (path.contains(".pptx") || path.contains(".PPTX") || path.contains(".ppt") || path.contains(".PPT")) {
                            holder.doc_image.setImageResource(R.drawable.image_ppt);
                        } else if (path.contains(".csv") || path.contains(".CSV")) {
                            holder.doc_image.setImageResource(R.drawable.image_csv);
                        } else {
                            holder.doc_image.setImageResource(R.drawable.reports);
                        }
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                    }
                    if (bm != null) {
                        holder.grid_image.setImageBitmap(bm);
                    } else {
                        holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                    }
                } else if (path.contains("http")) {
                    if (path.contains(".jpeg") || path.contains(".JPEG")
                            || path.contains(".jpg") || path.contains(".JPG")
                            || path.contains(".png") || path.contains(".PNG")) {
                        holder.play_video.setTag("3");
                        holder.delete.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                        //loader.init(ImageLoaderConfiguration.createDefault(context));
                        //loader.displayImage(path, holder.grid_image, op, null);
                        Picasso.with(PMChecklistApproval.this).load(path).into(holder.grid_image);
                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.GONE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.stop_video);
                    } else if (path.contains(".doc") || path.contains(".DOC")
                            || path.contains(".txt") || path.contains(".TXT")
                            || path.contains(".pdf") || path.contains(".PDF")
                            || path.contains(".xlsx") || path.contains(".XLSX")
                            || path.contains(".pptx") || path.contains(".PPTX")
                            || path.contains(".xls") || path.contains(".XLS")
                            || path.contains(".ppt") || path.contains(".PPT")
                            || path.contains(".csv") || path.contains(".CSV")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.GONE);
                        holder.doc_image.setVisibility(View.VISIBLE);
                        holder.grid_image.setVisibility(View.GONE);
                        if (path.contains(".doc") || path.contains(".DOC")) {
                            holder.doc_image.setImageResource(R.drawable.image);
                        } else if (path.contains(".txt") || path.contains(".TXT")) {
                            holder.doc_image.setImageResource(R.drawable.image_txt);
                        } else if (path.contains(".pdf") || path.contains(".PDF")) {
                            holder.doc_image.setImageResource(R.drawable.image_pdf);
                        } else if (path.contains(".xlsx") || path.contains(".XLSX") || path.contains(".xls") || path.contains(".XLS")) {
                            holder.doc_image.setImageResource(R.drawable.image_xls);
                        } else if (path.contains(".pptx") || path.contains(".PPTX") || path.contains(".ppt") || path.contains(".PPT")) {
                            holder.doc_image.setImageResource(R.drawable.image_ppt);
                        } else if (path.contains(".csv") || path.contains(".CSV")) {
                            holder.doc_image.setImageResource(R.drawable.image_csv);
                        } else {
                            holder.doc_image.setImageResource(R.drawable.reports);
                        }
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                    }
                }
            }

            holder.play_video.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaView(holder.play_video.getTag().toString(), imageList.get(position).getPath());
                }
            });

            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    backButtonAlert(3, "512", "63", "64",
                            gridId, photoType, imageList.get(position).getName(), imageList.get(position).getPath());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    public boolean checkPreviousElement(int fieldId, int valFlag, int elementType) {

        int prvFieldId = 0;
        ChecklistDetail checkListDetail = null;

		/*
		 	valFlag - 0 means validate the previous checklist item
		 	valFlag - 1 means validate the previous element in same checklist items

		 	elementType - 1 means current element is pre photo of particular field
		 	elementType - 2 means current element is Editbox/Spinner of particular field
		 	elementType - 3 means current element is Remarks of particular field
		 	elementType - 4 means current element is post photo of particular field
		 */

        if (valFlag == 1) {
            checkListDetail = hmPmCheklist.get(fieldId);

			/*
				If element type is edit box or spinner type and check if pre min/max images are configured or not.
				then validate previous checklist item instead of validating previous element in same checklist item
			*/
            if (elementType == 2 && checkListDetail.getPreMinImg() == -1 && checkListDetail.getPreMinImg() == -1) {
                valFlag = 0;
            }
        }

        if (valFlag == 0) {

            //Get Visible Previous Checklist
            while (true) {
                prvFieldId = hmPmCheklist.get(fieldId).getPrvId();

                //0 indicate first element in list
                if (prvFieldId == 0) {
                    return true;
                }

                //Check visibility status of previous field, if field is visibile then exit loop
                checkListDetail = hmPmCheklist.get(prvFieldId);

                if (checkListDetail == null) {
                    return true;
                }

                if (checkListDetail.isVisible()) {
                    break;
                }
            }
        }

        //Validate Previous item for mandatory
        if ((elementType != 2 || valFlag == 0) && checkListDetail.isMadatory().equalsIgnoreCase("S")) {

            if ((checkListDetail.getFieldCat() == 'S' && checkListDetail.getSpinner().getSelectedItem().toString().equals(AppConstants.DD_SELECT_OPTION))
                    || (checkListDetail.getFieldCat() == 'E' && checkListDetail.getEditText().getText().toString().trim().length() == 0)) {

                checkListDetail.getTextView().clearFocus();
                checkListDetail.getTextView().requestFocus();
                String s = Utils.msg(PMChecklistApproval.this, "256") + " " + checkListDetail.getFieldName().toString().trim();
                Utils.toastMsg(PMChecklistApproval.this, s);
                return false;

            }
        }

        //Validate remarks when validating previous checklist item.
        if ((elementType != 3 || valFlag == 0)) {
            //Validate Previous item for mandatory remarks
            if (checkListDetail.getTvRemarksLink() != null && checkListDetail.getTvRemarksLink().getVisibility() == View.VISIBLE
                    && checkListDetail.isRemarkMadatory()) {

			/*
				if Reamrks field length is 0 then check following conditions
				1. if filed is editbox/date/qr code
				2. if it is spinner then also validate the selected value if remarks is configured as mandatory for selected value.
			 */
                if (checkListDetail.getEtViRemarks().getText().toString().length() == 0 &&
                        (checkListDetail.getFieldCat() == 'E' ||
                                (checkListDetail.getFieldCat() == 'S' && checkListDetail.getRemarkMada().contains(checkListDetail.getSpinner().getSelectedItem().toString())))) {

                    checkListDetail.getTextView().clearFocus();
                    checkListDetail.getTextView().requestFocus();
                    String s1 = Utils.msg(PMChecklistApproval.this, "255");
                    String s2 = Utils.msg(PMChecklistApproval.this, "106");
                    s1 = s1 + " " + checkListDetail.getFieldName().toString().trim() + " " + s2;
                    Toast.makeText(PMChecklistApproval.this, s1.trim(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;
    }

    public void setImages(int prePostType, int iddd) {
        DataBaseHelper db30 = new DataBaseHelper(PMChecklistApproval.this);
        db30.open();
        Cursor cursor = db30.getChecklistItemImages(prePostType, iddd, dgType, scheduledDate,
                activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));

        List<ViewImage64> imgViewList = null;

        if (hmPmCheklist.get(iddd).getCapturePrePhoto() != null) {
            hmPmCheklist.get(iddd).getCapturePrePhoto().setVisibility(View.GONE);
        }
        if (hmPmCheklist.get(iddd).getCapturePostPhoto() != null) {
            hmPmCheklist.get(iddd).getCapturePostPhoto().setVisibility(View.GONE);
        }

        if (cursor != null) {
            imgViewList = new ArrayList<ViewImage64>();
            hmPmCheklist.get(iddd).getPreImgName().clear();
            hmPmCheklist.get(iddd).getPostImgName().clear();

            int imgCounter = 1;

            int a = cursor.getCount();

            while (cursor.moveToNext()) {
                int inner = 0;
                String path = cursor.getString(cursor.getColumnIndex("IMAGE_PATH"));
                String tag = cursor.getString(cursor.getColumnIndex("IMG_TAG"));
                String time = cursor.getString(cursor.getColumnIndex("TIME"));
                String imgname = cursor.getString(cursor.getColumnIndex("IMG_NAME"));
                String lati = cursor.getString(cursor.getColumnIndex("LATI"));
                String longi = cursor.getString(cursor.getColumnIndex("LONGI"));
                ViewImage64 viewImg = null;

                if (path != null) {
                    viewImg = new ViewImage64();
                    viewImg.setTimeStamp(time);
                    viewImg.setName(tag);
                    viewImg.setPath(path);
                    viewImg.setLati(lati);
                    viewImg.setLongi(longi);
                    imgViewList.add(viewImg);
                    if (prePostType == 1) {
                        hmPmCheklist.get(iddd).getPreImgName().add(imgname);
                    } else {
                        hmPmCheklist.get(iddd).getPostImgName().add(imgname);
                    }
                    imgCounter++;

                }
            }

            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(imgViewList, PMChecklistApproval.this, iddd, prePostType);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(PMChecklistApproval.this, LinearLayoutManager.HORIZONTAL, false);

            if (prePostType == 1) {
                if (hmPmCheklist.get(iddd).getCapturePrePhoto() != null && imgViewList.size() > 0) {
                    //hmPmCheklist.get(iddd).getCapturePrePhoto().setVisibility(View.VISIBLE);
                }
                hmPmCheklist.get(iddd).getPre_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).getPre_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).setPreImgCounter(imgCounter);
            } else {
                if (hmPmCheklist.get(iddd).getCapturePostPhoto() != null && imgViewList.size() > 0) {
                    //hmPmCheklist.get(iddd).getCapturePostPhoto().setVisibility(View.VISIBLE);
                }
                hmPmCheklist.get(iddd).getPost_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).getPost_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).setPostImgCounter(imgCounter);
            }
        }
        db30.close();
    }

    private void setImageActivityLevel() {
        op = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.no_media_default)
                .showImageForEmptyUri(R.drawable.no_media_default)
                .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                .build();

        DataBaseHelper db30 = new DataBaseHelper(PMChecklistApproval.this);
        db30.open();
        Cursor preCursor = db30.getChecklistItemImages(1, 0,
                dgType, scheduledDate, activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));
        if (preCursor != null && preCursor.getCount() > 0) {
            while (preCursor.moveToNext()) {
                String path = preCursor.getString(preCursor.getColumnIndex("IMAGE_PATH"));
                String tag = preCursor.getString(preCursor.getColumnIndex("IMG_TAG"));
                String time = preCursor.getString(preCursor.getColumnIndex("TIME"));
                String imgname = preCursor.getString(preCursor.getColumnIndex("IMG_NAME"));
                String lati = preCursor.getString(preCursor.getColumnIndex("LATI"));
                String longi = preCursor.getString(preCursor.getColumnIndex("LONGI"));
                String displayName = "PM-" + System.currentTimeMillis() + ".jpg";

                BeanGetImage preimage = new BeanGetImage();
                preimage.setPreImgPath(path);
                preimage.setPreImgName(tag);
                preimage.setPreImgTimeStamp(time);
                preimage.setLATITUDE(lati);
                preimage.setLONGITUDE(longi);
                //preimage.setImageURL( imageList.getImageList().get( i ).getImageURL());
                PreimageList.add(preimage);
            }
            ll_pre_act_photo.setVisibility(View.VISIBLE);
            pre_grid.setFastScrollEnabled(true);
            //pre_grid
            pre_grid.setAdapter(new PreItemAdapter(PMChecklistApproval.this, PreimageList, op));
            pre_grid.setExpanded(true);
        }

        Cursor postCursor = db30.getChecklistItemImages(2, 0,
                dgType, scheduledDate, activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));
        if (postCursor != null && postCursor.getCount() > 0) {
            while (postCursor.moveToNext()) {
                String path = postCursor.getString(preCursor.getColumnIndex("IMAGE_PATH"));
                String tag = postCursor.getString(preCursor.getColumnIndex("IMG_TAG"));
                String time = postCursor.getString(preCursor.getColumnIndex("TIME"));
                String imgname = postCursor.getString(preCursor.getColumnIndex("IMG_NAME"));
                String lati = postCursor.getString(preCursor.getColumnIndex("LATI"));
                String longi = postCursor.getString(preCursor.getColumnIndex("LONGI"));
                String displayName = "PM-" + System.currentTimeMillis() + ".jpg";

                BeanGetImage postimage = new BeanGetImage();
                postimage.setIMAGE_PATH(path);
                postimage.setIMAGENAME(tag);
                postimage.setImgTimeStamp(time);
                postimage.setLATITUDE(lati);
                postimage.setLONGITUDE(longi);
                //postimage.setImageURL( imageList.getImageList().get( i ).getImageURL());
                PostimageList.add(postimage);
            }
            ll_post_act_img.setVisibility(View.VISIBLE);
            post_grid.setFastScrollEnabled(true);
            post_grid.setAdapter(new ItemAdapter(PMChecklistApproval.this, PostimageList, op));
            post_grid.setExpanded(true);
        }
        db30.close();
    }

    public void rejectDialog() {
        final Dialog RejectPMDialog = new Dialog(PMChecklistApproval.this);
        RejectPMDialog.setCanceledOnTouchOutside(false);
        RejectPMDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RejectPMDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        RejectPMDialog.setContentView(R.layout.reject_pm);
        final Window window_SignIn = RejectPMDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity(Gravity.CENTER);
        Drawable d = new ColorDrawable(Color.parseColor("#CC000000"));
        RejectPMDialog.getWindow().setBackgroundDrawable(d);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        RejectPMDialog.show();

        ImageView iv_cancel = (ImageView) RejectPMDialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                RejectPMDialog.cancel();
            }
        });

        TextView tv_rej_region = (TextView) RejectPMDialog.findViewById(R.id.tv_rej_region);
        Utils.msgText(PMChecklistApproval.this, "461", tv_rej_region);

        final TextView tv_category = (TextView) RejectPMDialog.findViewById(R.id.tv_category);
        Utils.msgText(PMChecklistApproval.this, "462", tv_category);

        final TextView tv_remarks = (TextView) RejectPMDialog.findViewById(R.id.tv_remarks);
        Utils.msgText(PMChecklistApproval.this, "463", tv_remarks);


        final Spinner sp_rmk_category = (Spinner) RejectPMDialog.findViewById(R.id.sp_rmk_category);
        sp_rmk_category.setBackgroundResource(R.drawable.doted);
        final EditText et_reject_remarks = (EditText) RejectPMDialog.findViewById(R.id.et_reject_remarks);
        InputFilter rmkfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.toString(source.charAt(i)).equals("!")
                            && !Character.toString(source.charAt(i)).equals("@")
                            && !Character.toString(source.charAt(i)).equals("^")
                            && !Character.toString(source.charAt(i)).equals("_")
                            && !Character.toString(source.charAt(i)).equals("-")
                            && !Character.toString(source.charAt(i)).equals("[")
                            && !Character.toString(source.charAt(i)).equals("]")
                            && !Character.toString(source.charAt(i)).equals(" ")
                            && !Character.toString(source.charAt(i)).equals("\\")) {
                        return "";
                    }
                }
                return null;
            }
        };
        et_reject_remarks.setFilters(new InputFilter[]{rmkfilter, new InputFilter.LengthFilter(200)});
        //et_reject_remarks.setText("" + str_rj_remarks);
        et_reject_remarks.setText("" + str_reject_approval_remarks);
        sp_rmk_category.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner1(sp_rmk_category, rj_cate);
        int status_pos = getCategoryPos(str_rj_category, rj_cate);
        sp_rmk_category.setSelection(status_pos);

        TextView tv_submit = (TextView) RejectPMDialog.findViewById(R.id.tv_submit);
        Utils.msgText(PMChecklistApproval.this, "464", tv_submit);
        tv_submit.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (mAppPreferences.getPMRejectMadatoryFields() == 1 && sp_rmk_category.getSelectedItem().toString().trim().equalsIgnoreCase("Select Category")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_category.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                } else if (mAppPreferences.getPMRejectMadatoryFields() == 2 && sp_rmk_category.getSelectedItem().toString().trim().equalsIgnoreCase("Select Category")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_category.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                } else if (mAppPreferences.getPMRejectMadatoryFields() == 1 && et_reject_remarks.getText().toString().length() == 0) {
                    Toast.makeText(PMChecklistApproval.this, "" + tv_remarks.getText().toString() + " can not be blank.",
                            Toast.LENGTH_LONG).show();
                } else if (mAppPreferences.getPMRejectMadatoryFields() == 3 && et_reject_remarks.getText().toString().length() == 0) {
                    Toast.makeText(PMChecklistApproval.this, "" + tv_remarks.getText().toString() + " can not be blank.",
                            Toast.LENGTH_LONG).show();
                } else {
                    str_rating = "";
                    str_photo_info = "";
                    str_quality_pm_task = "";
                    str_visual_check1 = "";
                    str_visual_check2 = "";
                    str_visual_check3 = "";
                    str_reject_approval_remarks = et_reject_remarks.getText().toString();
                    str_rj_remarks = et_reject_remarks.getText().toString();
                    str_rj_category = sp_rmk_category.getSelectedItem().toString();
                    RejectPMDialog.cancel();
                    alert(2, "468");

                }
            }
        });

    }

    public void alert(final int confirmation, String msgId) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(PMChecklistApproval.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list UI
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        positive.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        negative.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        title.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        title.setText(Utils.msg(PMChecklistApproval.this, msgId));
        // title.setText("Do you want to exit?");
        positive.setText(Utils.msg(PMChecklistApproval.this, "63"));
        negative.setText(Utils.msg(PMChecklistApproval.this, "64"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                DataBaseHelper db = new DataBaseHelper(PMChecklistApproval.this);
                db.open();
                actvity_dialog.cancel();
                if (confirmation == 1) {
                    finish();
                }

                if (confirmation == 2) {
                    actvity_dialog.cancel();
                    if (alertCount == 0) {
                        alert(2, "501");
                        alertCount = 1;
                        return;
                    }
                    alertCount = 0;
                    JSONObject obj = new JSONObject();
                    String rmkId = "";
                    if (validate()) {
                        try {
                            if (!str_rj_category.equalsIgnoreCase("Select Category")) {
                                rmkId = db.getInciParamId("117", str_rj_category, "654");
                            }

                            obj.put("chkImgPre", "");
                            obj.put("chkImgPost", "");
                            obj.put("rejRmk", str_rj_remarks);
                            obj.put("rejRmkTxt", str_rj_category);
                            obj.put("rejRmkCategory", rmkId);
                            obj.put(AppConstants.UPLOAD_TYPE, "PM");
                            obj.put(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId());
                            obj.put(AppConstants.ROLE_ID, mAppPreferences.getRoleId());
                            obj.put(AppConstants.SITE_ID_ALIAS, siteId);
                            obj.put(AppConstants.ACTIVITY_TYPE_ID, activityTypeId);
                            obj.put(AppConstants.STATUS_ALIAS, Status);
                            obj.put(AppConstants.SCHEDULE_DATE, scheduledDate);
                            obj.put(AppConstants.DG, dgType);
                            obj.put(AppConstants.CHECK_LIST, checklist());
                            obj.put(AppConstants.CURRENT_TIME, "");
                            obj.put(AppConstants.REMARKS, "");
                            obj.put("lat", latitude);
                            obj.put("long", longitude);
                            obj.put(AppConstants.PRE_IMGS, "");
                            obj.put(AppConstants.POST_IMGS, "");
                            obj.put("assetId",getIntent().getExtras().getString("assetId"));
                            obj.put("equipmentId",getIntent().getExtras().getString("equipmentId"));
                            String module = "PM";
                            String tktId = "";

                            //approval rating
                            obj.put("isRating", "");
                            obj.put("photoRating", "");
                            obj.put("pmRating", "");
                            obj.put("visualCheck1", "");
                            obj.put("visualCheck2", "");
                            obj.put("visualCheck3", "");

                            if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")
                                    || mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                                module = "H";
                                tktId = txnId;
                            }
                            String addParams = "appversion=" + pInfo.versionName + "~oper=J" + "~moduleType=" + module + "~tktId=" + tktId;
                            //String addParams = "appversion=" + pInfo.versionName+"~oper=J";
                            obj.put(AppConstants.ADD_PARAM, addParams);
                            obj.put(AppConstants.LANGUAGE_CODE, mAppPreferences.getLanCode());
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (Utils.isNetworkAvailable(PMChecklistApproval.this)) {
                        String url = "";
                        if (db.getModuleIP("Preventive").equalsIgnoreCase("0")) {
                            url = mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI;
                        } else {
                            url = db.getModuleIP("Preventive") + WebMethods.url_SaveAPI;
                        }
                        db.close();
                        AsynTaskService task = new AsynTaskService(obj.toString(),"J");
                        task.execute(url);
                    } else {
                        db.insertTxnDataLocally(txnId+getIntent().getExtras().getString("assetId"), "5", obj.toString(), "", "", "", mAppPreferences.getUserId(), db.getModuleIP("Preventive") + WebMethods.url_SaveAPI);
                        db.close();
                        //No internet connection.Data stored locally in the app;
                        Utils.toast(PMChecklistApproval.this, "66");
                        // return back to previous screen.
                        finish();
                    }
                }
            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                alertCount = 0;
            }
        });
    }

    public void addItemsOnSpinner1(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    private int getCategoryPos(String category, ArrayList<String> list) {
        return list.indexOf(category);
    }

    public void mediaView(String flag, String urlPath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (flag.equals("1")) {
            final Dialog nagDialog = new Dialog(PMChecklistApproval.this,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(true);
            nagDialog.setContentView(R.layout.image_zoom);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(nagDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes(lp);
            Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
            ImageView imageView = (ImageView) nagDialog.findViewById(R.id.imageView1);
            Bitmap bm = Utils.decodeFile(urlPath);
            if (bm != null) {
                imageView.setImageBitmap(bm);
            } else {
                imageView.setBackgroundColor(Color.parseColor("#000000"));
            }
            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        } else if (flag.equals("2") || flag.equals("4")) {
            Intent i = new Intent(PMChecklistApproval.this, ViewVideoWebView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        } else if (flag.equals("3")) {
            final Dialog nagDialog = new Dialog(PMChecklistApproval.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(true);
            nagDialog.setContentView(R.layout.image_zoom);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(nagDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes(lp);
            Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
            ImageView imageView = (ImageView) nagDialog.findViewById(R.id.imageView1);
            //loader.init(ImageLoaderConfiguration.createDefault(PMChecklistApproval.this));
            //loader.displayImage(urlPath, imageView, op, null);
            Picasso.with(PMChecklistApproval.this).load(urlPath).into(imageView);
            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        }
    }

    //Api for get Get PM Data with retrofit
    private void callGetFildsValueApi(String getTxnList) {
        ProgressDialog progressDialog = new ProgressDialog(PMChecklistApproval.this);
        progressDialog.setMessage("Please wait...");
        //progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        int txnId1 = Integer.parseInt(txnId);
        Call<GetTxnListResponce> call = request.GetPMFildsData(txnId1);
        //Call<GetTxnListResponce> call = request.GetPMFildsData(4035693);
        call.enqueue(new Callback<GetTxnListResponce>() {
            @Override
            public void onResponse(Call<GetTxnListResponce> call, retrofit2.Response<GetTxnListResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //  Toast.makeText(PMChecklist.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    //UAT 894 observation (hide below 5 field)
                    et_projectName.setVisibility(View.GONE);
                    et_productName.setVisibility(View.GONE);
                    et_soNumber.setVisibility(View.GONE);
                    et_WorkOrderNumber.setVisibility(View.GONE);
                    et_siteTowerType.setVisibility(View.GONE);

                    et_Region.setVisibility(View.VISIBLE);
                    et_District.setVisibility(View.VISIBLE);
                    et_City.setVisibility(View.VISIBLE);
                    et_Latitude.setVisibility(View.VISIBLE);
                    et_assetId.setVisibility(View.VISIBLE);
                    et_Longitude.setVisibility(View.VISIBLE);

                    et_Region.setText(response.body().getRegion());
                    et_District.setText(response.body().getDistrict());
                    et_City.setText(response.body().getCity());
                    et_Latitude.setText(response.body().getLatitude());
                    et_Longitude.setText(response.body().getLongitude());
                    et_siteTowerType.setText(response.body().getSiteTowerType());
                    et_assetId.setText(response.body().getAssetId());

                    if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                            || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
                        et_assetId.setText(getIntent().getExtras().getString("qrCode"));
                    }


                    if (response.body().getSONumber() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().getSONumber());
                            System.out.println("json to string"+json.toString());
                            Iterator<String> keys = json.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (key.equalsIgnoreCase("wONumber")){
                                    et_WorkOrderNumber.setText(json.getString("wONumber"));
                                }else if (key.equalsIgnoreCase("productName")) {
                                    et_productName.setText("" + json.getString("productName"));
                                }else if (key.equalsIgnoreCase("projectName")) {
                                    et_projectName.setText("" + json.getString("projectName"));
                                }else if (key.equalsIgnoreCase("sONumber")) {
                                    et_soNumber.setText("" + json.getString("sONumber"));
                                }
                                // Toast.makeText(PMChecklist.this, ""+key, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            System.out.println("Excaption SONumber"+e);
                            //  Toast.makeText(PMChecklist.this, ""+e, Toast.LENGTH_SHORT).show();
                        }

                    }

                    //UAT 894 observation (hide below 5 field)
                    tv_projectName.setVisibility(View.GONE);
                    tv_productName.setVisibility(View.GONE);
                    tv_soNumber.setVisibility(View.GONE);
                    tv_WorkOrderNumber.setVisibility(View.GONE);
                    tv_siteTowerType.setVisibility(View.GONE);

                    tv_Region.setVisibility(View.VISIBLE);
                    tv_District.setVisibility(View.VISIBLE);
                    tv_City.setVisibility(View.VISIBLE);
                    tv_Latitude.setVisibility(View.VISIBLE);
                    tv_Longitude.setVisibility(View.VISIBLE);
                    tv_assetId.setVisibility(View.VISIBLE);
                } else {

                    //UAT 894 observation (hide below 5 field)
                    et_projectName.setVisibility(View.GONE);
                    et_productName.setVisibility(View.GONE);
                    et_soNumber.setVisibility(View.GONE);
                    et_WorkOrderNumber.setVisibility(View.GONE);
                    et_siteTowerType.setVisibility(View.GONE);

                    et_Region.setVisibility(View.VISIBLE);
                    et_District.setVisibility(View.VISIBLE);
                    et_City.setVisibility(View.VISIBLE);
                    et_Latitude.setVisibility(View.VISIBLE);
                    et_assetId.setVisibility(View.VISIBLE);
                    et_Longitude.setVisibility(View.VISIBLE);


                    //UAT 894 observation (hide below 5 field)
                    tv_projectName.setVisibility(View.GONE);
                    tv_productName.setVisibility(View.GONE);
                    tv_soNumber.setVisibility(View.GONE);
                    tv_WorkOrderNumber.setVisibility(View.GONE);
                    tv_siteTowerType.setVisibility(View.GONE);

                    tv_Region.setVisibility(View.VISIBLE);
                    tv_District.setVisibility(View.VISIBLE);
                    tv_City.setVisibility(View.VISIBLE);
                    tv_Latitude.setVisibility(View.VISIBLE);
                    tv_Longitude.setVisibility(View.VISIBLE);
                    tv_assetId.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<GetTxnListResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //UAT 894 observation (hide below 5 field)
                et_projectName.setVisibility(View.GONE);
                et_productName.setVisibility(View.GONE);
                et_soNumber.setVisibility(View.GONE);
                et_WorkOrderNumber.setVisibility(View.GONE);
                et_siteTowerType.setVisibility(View.GONE);

                et_Region.setVisibility(View.VISIBLE);
                et_District.setVisibility(View.VISIBLE);
                et_City.setVisibility(View.VISIBLE);
                et_Latitude.setVisibility(View.VISIBLE);
                et_Longitude.setVisibility(View.VISIBLE);
                et_assetId.setVisibility(View.VISIBLE);

                //UAT 894 observation (hide below 5 field)
                tv_projectName.setVisibility(View.GONE);
                tv_productName.setVisibility(View.GONE);
                tv_soNumber.setVisibility(View.GONE);
                tv_WorkOrderNumber.setVisibility(View.GONE);
                tv_siteTowerType.setVisibility(View.GONE);

                tv_Region.setVisibility(View.VISIBLE);
                tv_District.setVisibility(View.VISIBLE);
                tv_City.setVisibility(View.VISIBLE);
                tv_Latitude.setVisibility(View.VISIBLE);
                tv_Longitude.setVisibility(View.VISIBLE);
                tv_assetId.setVisibility(View.VISIBLE);

                //Toast.makeText(PMChecklist.this, "" + t, Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    public void approvedDialog() {
        final Dialog approvedPMDialog = new Dialog(PMChecklistApproval.this);
        approvedPMDialog.setCanceledOnTouchOutside(false);
        approvedPMDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        approvedPMDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        approvedPMDialog.setContentView(R.layout.approval_pm_dialog);
        final Window window_SignIn = approvedPMDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity(Gravity.CENTER);
        Drawable d = new ColorDrawable(Color.parseColor("#CC000000"));
        approvedPMDialog.getWindow().setBackgroundDrawable(d);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        approvedPMDialog.show();

        ImageView iv_cancel = (ImageView) approvedPMDialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                approvedPMDialog.cancel();
            }
        });

        TextView tv_approval_region = (TextView) approvedPMDialog.findViewById(R.id.tv_approval_region);
        Utils.msgText(PMChecklistApproval.this, "926", tv_approval_region);

        final TextView tv_rating = (TextView) approvedPMDialog.findViewById(R.id.tv_rating);
        Utils.msgText(PMChecklistApproval.this, "927", tv_rating);

        final TextView tv_photo_information = (TextView) approvedPMDialog.findViewById(R.id.tv_photo_information);
        Utils.msgText(PMChecklistApproval.this, "928", tv_photo_information);

        final TextView tv_quality_pm_task = (TextView) approvedPMDialog.findViewById(R.id.tv_quality_pm_task);
        Utils.msgText(PMChecklistApproval.this, "929", tv_quality_pm_task);

        final TextView tv_visual_check1 = (TextView) approvedPMDialog.findViewById(R.id.tv_visual_check1);
        Utils.msgText(PMChecklistApproval.this, "930", tv_visual_check1);

        final TextView tv_visual_check2 = (TextView) approvedPMDialog.findViewById(R.id.tv_visual_check2);
        Utils.msgText(PMChecklistApproval.this, "931", tv_visual_check2);

        final TextView tv_visual_check3 = (TextView) approvedPMDialog.findViewById(R.id.tv_visual_check3);
        Utils.msgText(PMChecklistApproval.this, "932", tv_visual_check3);

        final TextView tv_approval_remarks = (TextView) approvedPMDialog.findViewById(R.id.tv_remarks);
        Utils.msgText(PMChecklistApproval.this, "463", tv_approval_remarks);


        final Spinner sp_rating = (Spinner) approvedPMDialog.findViewById(R.id.sp_rating);
        sp_rating.setBackgroundResource(R.drawable.doted);

        final Spinner sp_photo_information = (Spinner) approvedPMDialog.findViewById(R.id.sp_photo_information);
        sp_photo_information.setBackgroundResource(R.drawable.doted);

        final Spinner sp_quality_pm_task = (Spinner) approvedPMDialog.findViewById(R.id.sp_quality_pm_task);
        sp_quality_pm_task.setBackgroundResource(R.drawable.doted);

        final Spinner sp_visual_check1 = (Spinner) approvedPMDialog.findViewById(R.id.sp_visual_check1);
        sp_quality_pm_task.setBackgroundResource(R.drawable.doted);

        final Spinner sp_visual_check2 = (Spinner) approvedPMDialog.findViewById(R.id.sp_visual_check2);
        sp_quality_pm_task.setBackgroundResource(R.drawable.doted);

        final Spinner sp_visual_check3 = (Spinner) approvedPMDialog.findViewById(R.id.sp_visual_check3);
        sp_quality_pm_task.setBackgroundResource(R.drawable.doted);

        final EditText et_approval_remarks = (EditText) approvedPMDialog.findViewById(R.id.et_approval_remarks);

        et_approval_remarks.setText("" + str_reject_approval_remarks);
        et_approval_remarks.setBackgroundResource(R.drawable.doted);

        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklistApproval.this);
        dbHelper.open();
        ArrayList<String> arr_rating = dbHelper.getPMapproval("1282",
                "654","6");
        ArrayList<String> arr_photo_info = dbHelper.getPMapproval("1281",
                "654","1");
        ArrayList<String> arr_quality_pm_task = dbHelper.getPMapproval("1281",
                "654","2");
        ArrayList<String> arr_visual_check1 = dbHelper.getPMapproval("1281",
                "654","3");
        ArrayList<String> arr_visual_check2= dbHelper.getPMapproval("1281",
                "654","4");
        ArrayList<String> arr_visual_check3 = dbHelper.getPMapproval("1281",
                "654","5");

        dbHelper.close();
        addItemsOnSpinner1(sp_rating,arr_rating);
        addItemsOnSpinner1(sp_photo_information,arr_photo_info);
        addItemsOnSpinner1(sp_quality_pm_task,arr_quality_pm_task);
        addItemsOnSpinner1(sp_visual_check1,arr_visual_check1);
        addItemsOnSpinner1(sp_visual_check2,arr_visual_check2);
        addItemsOnSpinner1(sp_visual_check3,arr_visual_check3);
        int status_pos = getCategoryPos(mAppPreferences.getIsRating(), arr_rating);
        sp_rating.setSelection(status_pos);
        if(mAppPreferences.getIsRating().equalsIgnoreCase("Yes")){
            sp_rating.setEnabled(false);
        }

        sp_rating.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                tv_photo_information.setVisibility(View.GONE);
                tv_quality_pm_task.setVisibility(View.GONE);
                tv_visual_check1.setVisibility(View.GONE);
                tv_visual_check2.setVisibility(View.GONE);
                tv_visual_check3.setVisibility(View.GONE);
                sp_photo_information.setVisibility(View.GONE);
                sp_quality_pm_task.setVisibility(View.GONE);
                sp_visual_check1.setVisibility(View.GONE);
                sp_visual_check2.setVisibility(View.GONE);
                sp_visual_check3.setVisibility(View.GONE);
                et_approval_remarks.setText("");
                addItemsOnSpinner1(sp_photo_information,arr_photo_info);
                addItemsOnSpinner1(sp_quality_pm_task,arr_quality_pm_task);
                addItemsOnSpinner1(sp_visual_check1,arr_visual_check1);
                addItemsOnSpinner1(sp_visual_check2,arr_visual_check2);
                addItemsOnSpinner1(sp_visual_check3,arr_visual_check3);
                if(sp_rating.getSelectedItem().toString().equalsIgnoreCase("Yes")){
                    if(!mAppPreferences.getPMphotoInfoRating().equalsIgnoreCase("2")){
                        tv_photo_information.setVisibility(View.VISIBLE);
                        sp_photo_information.setVisibility(View.VISIBLE);
                    }

                    if(!mAppPreferences.getQualityPMtaskRating().equalsIgnoreCase("2")){
                        tv_quality_pm_task.setVisibility(View.VISIBLE);
                        sp_quality_pm_task.setVisibility(View.VISIBLE);
                    }

                    if(!mAppPreferences.getVisualCheck1Rating().equalsIgnoreCase("2")){
                        tv_visual_check1.setVisibility(View.VISIBLE);
                        sp_visual_check1.setVisibility(View.VISIBLE);
                    }

                    if(!mAppPreferences.getVisualCheck2Rating().equalsIgnoreCase("2")){
                        tv_visual_check2.setVisibility(View.VISIBLE);
                        sp_visual_check2.setVisibility(View.VISIBLE);
                    }

                    if(!mAppPreferences.getVisualCheck3Rating().equalsIgnoreCase("2")){
                        tv_visual_check3.setVisibility(View.VISIBLE);
                        sp_visual_check3.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        TextView tv_submit = (TextView) approvedPMDialog.findViewById(R.id.tv_submit);
        Utils.msgText(PMChecklistApproval.this, "464", tv_submit);
        tv_submit.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                if (mAppPreferences.getIsRating().equalsIgnoreCase("1") &&
                        sp_rating.getSelectedItem().toString().trim().equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_rating.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (tv_photo_information.getVisibility()== View.VISIBLE
                        && mAppPreferences.getPMphotoInfoRating().equalsIgnoreCase("1")
                        && sp_photo_information.getSelectedItem().toString().trim()
                        .equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_photo_information.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (tv_quality_pm_task.getVisibility()== View.VISIBLE
                        && mAppPreferences.getQualityPMtaskRating().equalsIgnoreCase("1")
                        && sp_quality_pm_task.getSelectedItem().toString().trim()
                        .equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_quality_pm_task.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (tv_visual_check1.getVisibility()== View.VISIBLE
                        && mAppPreferences.getVisualCheck1Rating().equalsIgnoreCase("1")
                        && sp_visual_check1.getSelectedItem().toString().trim()
                        .equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_visual_check1.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (tv_visual_check2.getVisibility()== View.VISIBLE
                        && mAppPreferences.getVisualCheck2Rating().equalsIgnoreCase("1")
                        && sp_visual_check2.getSelectedItem().toString().trim()
                        .equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_visual_check2.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (tv_visual_check3.getVisibility()== View.VISIBLE
                        && mAppPreferences.getVisualCheck3Rating().equalsIgnoreCase("1")
                        && sp_visual_check3.getSelectedItem().toString().trim()
                        .equalsIgnoreCase("Select")) {
                    Toast.makeText(PMChecklistApproval.this, "Select " + tv_visual_check3.getText().toString() + ".",
                            Toast.LENGTH_LONG).show();
                }else if (mAppPreferences.getApprovalRemarks().equalsIgnoreCase("1")&&
                        et_approval_remarks.getText().toString().length() == 0) {
                    Toast.makeText(PMChecklistApproval.this, "" + tv_approval_remarks.getText().toString() + " can not be blank.",
                            Toast.LENGTH_LONG).show();
                }else {
                    DataBaseHelper dbApproval = new DataBaseHelper(PMChecklistApproval.this);
                    dbApproval.open();
                    str_reject_approval_remarks = et_approval_remarks.getText().toString();
                    str_rj_category = "Select Category";

                    str_rating = "";
                    if (!sp_rating.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_rating = dbApproval.getApprvalCateId1("1248","654",sp_rating.getSelectedItem().toString());
                    }

                    str_photo_info = "";
                    if (!sp_photo_information.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_photo_info = sp_photo_information.getSelectedItem().toString();
                    }

                    str_quality_pm_task = "";
                    if (!sp_quality_pm_task.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_quality_pm_task = sp_quality_pm_task.getSelectedItem().toString();
                    }

                    str_visual_check1 = "";
                    if (!sp_visual_check1.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_visual_check1 = sp_visual_check1.getSelectedItem().toString();
                    }

                    str_visual_check2 = "";
                    if (!sp_visual_check2.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_visual_check2 = sp_visual_check2.getSelectedItem().toString();
                    }

                    str_visual_check3 = "";
                    if (!sp_visual_check3.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                        str_visual_check3 = sp_visual_check3.getSelectedItem().toString();
                    }

                    approvedPMDialog.cancel();
                    submitData("R", "", "", "");
                }
            }
        });
    }

    public class AlarmDescTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        AlarmDescList resposnse_alarm = null;
        DataBaseHelper dbHelper;
        public AlarmDescTask(Context con) {
            dbHelper = new DataBaseHelper(PMChecklistApproval.this);
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
                Gson gson = new Gson();
                nameValuePairs.add(new BasicNameValuePair("alarmId", ""));
                nameValuePairs.add(new BasicNameValuePair("equipId", ""));
                nameValuePairs.add(new BasicNameValuePair("severityId", ""));
                String url = "";
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_GetCompleteAlarmList;
                }else{
                    url=moduleUrl+ WebMethods.url_GetCompleteAlarmList;
                }
                String res = Utils.httpPostRequest(con,url, nameValuePairs);
                resposnse_alarm = gson.fromJson(res, AlarmDescList.class);
            } catch (Exception e) {
                e.printStackTrace();
                resposnse_alarm = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((resposnse_alarm == null)) {
                // Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
                Utils.toast(PMChecklistApproval.this, "70");
            } else if (resposnse_alarm.getAlarm_description()!=null && resposnse_alarm.getAlarm_description().size() > 0) {
                dbHelper.open();
                dbHelper.clearAlarmDescData("654");
                dbHelper.insertAlarmDescData(resposnse_alarm.getAlarm_description(),"654");
                dbHelper.dataTS(null, null, "19",
                        dbHelper.getLoginTimeStmp("19","654"), 2,"654");
                dbHelper.close();

                Intent i = new Intent(PMChecklistApproval.this, AddTicket.class);
                i.putExtra("ticket_type",
                        Utils.msg(PMChecklistApproval.this, "934"));
                i.putExtra("site_id", siteId);
                i.putExtra("ref_tkt_id", txnId);
                i.putExtra("remarks",
                        Utils.msg(PMChecklistApproval.this, "936")+txnId);

                mAppPreferences.saveRefMode(3);
                mAppPreferences.setTTModuleSelection("654");
                startActivity(i);
                finish();
            } else {
                // Toast.makeText(IncidentManagement.this,
                // "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(PMChecklistApproval.this, "13");
            }

            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    public String alarmDescDataType() {
        String DataType_Str = "1";
        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklistApproval.this);
        dbHelper.open();
        String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("19","654"),
                dbHelper.getLoginTimeStmp("19","654"),"19");
        dbHelper.close();
        if (i != "1") {
            DataType_Str = i;
        }
        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }

    //Open alert dialog confirmation for Do you want to create corrective ticket
    public void correctiveTTconfirmation() {
        final Dialog actvity_dialog;

        actvity_dialog = new Dialog(PMChecklistApproval.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        positive.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        negative.setTypeface(Utils.typeFace(PMChecklistApproval.this));
        title.setTypeface(Utils.typeFace(PMChecklistApproval.this));

        title.setText(Utils.msg(PMChecklistApproval.this,"933"));
        positive.setText(Utils.msg(PMChecklistApproval.this,"63"));
        negative.setText(Utils.msg(PMChecklistApproval.this,"64"));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                try {
                    if (!alarmDescDataType().equalsIgnoreCase("")) {
                        if (Utils.isNetworkAvailable(PMChecklistApproval.this)) {
                            new AlarmDescTask(PMChecklistApproval.this).execute();
                        } else {
                            // Toast.makeText(IncidentManagement.this,"No internet connection.Please download meta data.",Toast.LENGTH_LONG).show();
                            Utils.toast(PMChecklistApproval.this, "17");
                        }
                    } else {
                        Intent i = new Intent(PMChecklistApproval.this, AddTicket.class);
                        i.putExtra("ticket_type",
                                Utils.msg(PMChecklistApproval.this, "934"));
                        i.putExtra("site_id", siteId);
                        i.putExtra("ref_tkt_id", txnId);
                        i.putExtra("remarks",
                                Utils.msg(PMChecklistApproval.this, "936")+txnId);
                        mAppPreferences.saveRefMode(3);
                        mAppPreferences.setTTModuleSelection("654");
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                backScreen();

            }
        });
    }

    public void backScreen(){
      int s = mAppPreferences.getPMAssetBackTask();
      int s1 = mAppPreferences.getPMBackTask();

        if (mAppPreferences.getPMAssetBackTask()==0
                && (mAppPreferences.getPMBackTask() == 0 || mAppPreferences.getPMBackTask() == 1
                || mAppPreferences.getPMBackTask() == 2 || mAppPreferences.getPMBackTask() == 3
                || mAppPreferences.getPMBackTask() == 4 || mAppPreferences.getPMBackTask() == 5
                || mAppPreferences.getPMBackTask() == 6)) {
            DataBaseHelper gridData = new DataBaseHelper(PMChecklistApproval.this);
            gridData.open();
            gridData.clearScheduleList(txnId);
            gridData.clearMissedList(txnId);
            gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
            gridData.close();
            Intent i = new Intent(PMChecklistApproval.this, PMTabs.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else if(mAppPreferences.getPMAssetBackTask()==1){
            mAppPreferences.setPMAssetBackTask(2);
            DataBaseHelper gridData = new DataBaseHelper(PMChecklistApproval.this);
            gridData.open();
            gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
            gridData.close();
            finish();
        }else {
            finish();
        }
    }


}