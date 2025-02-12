package com.isl.incident;

/*
Modified By : Avishek Singh
 Modified On : 27-jan-2021
 Version     : 1.1
 Purpose     : iMaintain cr# 746

 Modified By : Avishek Singh
 Modified On : 02-mar-2021
 Version     : 1.2
 Purpose     : iMaintain cr# 821

 Modified By : Dhakan Lal Sharma
 Modified On : 14-Feb-2022
 Version     : 1.3
 Purpose     : hide Reference checkbox

 Modified By : Dhakan Lal Sharma
 Modified On : 14-Feb-2024
 Version     : 1.4
 Purpose     : DM-1178

 Modified By : Dhakan Lal Sharma
 Modified On : 21-Mar-2024
 Version     : 1.5
 Purpose     : DM-1158

 Version     : 1.6
 Purpose     : DM-1158


 Modified By : Dhakan Lal Sharma
 Modified On : 30-Oct-2024
 Version     : 1.7
 Purpose     : MAST

 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.GPSTracker;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.modal.Operator;
import com.isl.modal.ResponseUpdateTicket;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.preventive.ViewPMCheckList;
import com.isl.sparepart.SparePart;
import com.isl.util.DecimalDigitsInputFilter;
import com.isl.util.FilePathFinder;
import com.isl.util.Utils;
import com.isl.videocompressor.MediaController;
import com.isl.workflow.utils.DateTimeUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import infozech.itower.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTicket extends Activity {
    boolean check = false,spinnerFirstTime = true;
    String eta, etr, rca, category_name, assingTo = "", RCAcate = "",
            RCASubcate = "", RCA = "", PROBLEM_START_DATE, PROBLEM_START_TIME,
            PROBLEM_END_DATE, PROBLEM_END_TIME, TICKET_RCA, Effective_Site,
            Operator, OperatorExempt, Site_Id, ticket_id, status,
            ticket_type, problem_description, alarm_detail, equipment,
            alarm_type, alarm_description, tkt_log_time, equipment_id,
            severity_Id, alarm_descId, ticket_typeId, alarmTxnId, site_status,
            rca_sub_category, rca_sub_category_na, batt_disc_date,
            batt_disc_time, trvlDistnc = "", noOfTech = "", wrkgNights = "", asgnToUid, etsid, offLineFlag,
            dgReading = "", gridReading = "", fuelLevel = "", actionTaken = "", str_siteId = "",
            ttCreaterUserCat = "", ttCreaterUserSubCat = "", resMethod, fouArea,
            fouAreaDetails, staResion, serviceImacted, serviceImpactStart,
            serviceAffecetd,serviceImpactStartTime,isUserSatisfied,loggedBy,serviceImapactID, assetId = "",toc_fo_user = "";//1.4 //1.5; //1.4

    String hide_tt_status, hide_group, hide_site_visibility,
            hide_problem_start_date_time, hide_disc_bat_date_time, hide_eta,
            hide_etr, hide_rca_category, hide_rca_sub_category, hide_rca,
            hide_operator, hide_exempted_oper, hide_effective_site,
            hide_remarks, hide_problem_end_date_time, discharge_date_time,
            latitude = "", longitude = "", MSGTag, filePath = "", rca_id,
            spareVisable, assign_user = "", hide_dg = "", hide_grid = "", hide_fuel = "",
            hide_action = "", approvalNeed = "", Imglatitude = "DefaultLatitude",
            Imglongitude = "DefaultLongitude", fileType = "jpg", hide_tt_treatement = "N", hide_hub_siteId = "N";

    String submissionData = "", ticketType = "", pShortName, pDescription, tt_flag = "1", tt_rev_flag = "1", statusResion, resoulationMethod, faultarea, faultareadetails, spstatusapprove = "", prNodetails = "", approvelStatus = "";
    Spinner sp_status, sp_site_visibility, sp_action_taken, sp_approval1, sp_approval2,
            sp_reject_category, sp_ticket_treatment, sp_status_reason, sp_resolution_method,
            sp_status_approve, sp_serviceImapact,sp_confirmation,sp_delay_reason;
    public SearchableSpinner sp_group, sp_rca_category, sp_rca_category_sub, sp_fault_area_details, sp_fault_area;
    TextView tv_chk_link, txt_problem_start_date, txt_problem_start_time,
            txt_problem_end_date, txt_problem_end_time,txt_ticket_log_date,txt_ticket_log_time, tv_dis_bttry_Date,
            tv_dis_bttry_time, sp_operator, sp_operator_exempted,
            tv_brand_logo, tv_status, tv_group, tv_site_visibility,
            tv_problem_start_date, tv_start_time, tv_problem_end_date,
            tv_problem_end_time, tv_dis_Date, tv_dis_time, tv_eta, tv_etr,
            tv_rca_category, tv_rca_category_sub, tv_rca, tv_operator,
            tv_operator_exempted, tv_dependent_site, tv_remarks, tv_click_img,
            tv_spare_parts, tv_grp_user, tv_dg_reading, tv_grid_reading, tv_fuel_level,
            tv_action_taken, tv_ticket_treatment, tv_approval1, tv_approval2, tv_reject_category, tv_reject_remarks,
    sp_priority, tv_status_reason, tv_status_approve, tv_resolution_method, tv_serviceImapact,
            tv_serviceImpactStart,tv_serviceImpactStartTime,tv_isSatisfy,tv_asset,tv_delay_reason;//1.5;
    int minImgCounter = 0, maxImgCounter = 0, userCounter = 0, adapterCounter = 1,
            uploadMax = 0, uploadMin = 0;
    EditText et_eta, et_etr, et_rca, et_dependent_site, et_remarks, et_trvlDistnc, et_noOfTech, et_wrkgNights, et_hubSiteId,
            et_grid_reading, et_fuel_level, et_dg_reading, et_reject_remarks, et_servicesAffected,et_asset;//1.5;
    CheckBox cb_add_ref_ticket;
    Button bt_back, btnTakePhoto;
    TextView btn_update_tt;
    ArrayList<String> list_status, list_status1, rca_category_name, rca_sub_category_name, list_action,
            list_approval1, list_approval2, list_rejection_category, list_tt_treatement,
            list_group, list_operator_name, list_operator_id, siteStatusList,
            list_user_id, list_userGRP, list_status_resion, list_service_impact,
            list_method_resolation, list_status_aaprove,list_confirmation,list_delay_reason;;
    StringBuffer All_Operator_Id, all_exempt_opt_id, All_user_Id;
    DataBaseHelper db;
    LinearLayout ll_assigned, ll_site_visible, ll_str_date_time,
            ll_end_date_time, ll_bttry_dschrg_date, ll_eta, ll_etr,
            ll_rca_category, ll_rca_sub_category, ll_rca, ll_operator,
            ll_effected_sites, ll_remarks, ll_operator_exempted, ll_user,
            ll_trvlDistnc, ll_noOfTech, ll_wrkgNights, ll_hub_SiteId,
            ll_dg_reading, ll_grid_reading, ll_fuel_level, ll_action_taken, ll_approval1, ll_approval2,
            ll_reject_category, ll_ticket_treatment, ll_status_reason, ll_serviceImapact_add, ll_resolution_method, ll_fault_area,
            ll_fault_area_details, ll_status_approve,ll_confirmation,ll_asset,ll_delay_reason,ll_ticket_log_date;
    private int hour, minute, pYear, pMonth, pDay;
    AppPreferences mAppPreferences;
    LinearLayout ll_update;
    List<GetRttsFoultAreaList> rttsFoultAreaList = new ArrayList();
    List<GetRttsFoultAreaList> rttsFoultAreaListdetails = new ArrayList();
    ArrayList<String> list_faoult_area = new ArrayList();
    ArrayList<String> list_fault_area_details = new ArrayList();
    ArrayList<Operator> operatorList, exemptOperatorList, tempOptExemptList, userList;
    MyCustomAdapter dataAdapter = null, dataAdapterExempt = null, dataAdapterUser = null;
    String[] words, words1, ttmsg, assignUserIdTT, ttMedia;
    Dialog dialogOperator, dialogOperatorExempt, dialogUser;
    Date d;
    int flagA = 0, flagB = 0, counter = 0, a = 0, exemptOptCounter = 0,
            imgCounter = 1, gridCounter = 1;
    GPSTracker gps;
    PackageInfo pInfo = null;
    static final int TIME_DIALOG_ID = 1111;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID1 = 2222;
    static final int DATE_DIALOG_ID1 = 1;
    static final int TIME_DIALOG_ID2 = 3333;
    static final int DATE_DIALOG_ID2 = 2;
    static final int DATE_DIALOG_ID4 = 4;
    static final int TIME_DIALOG_ID5 = 55;
    String moduleUrl = "";
    String url = "";
    int trvlDis = 0, noOfTec = 0, wrkgNight = 0;
    JSONArray imgInfoArray, jsonArrStrImg;
    RecyclerView grid = null;
    //ExpandableHeightGridView grid = null;
    //LinkedHashMap<String, ViewImage64> lhmImages = new LinkedHashMap<String, ViewImage64>();
    List<ViewImage64> lhmImages = new ArrayList<ViewImage64>();
    //MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
    private Uri imageUri;
    boolean disableSupperUserField = false;
    String priorityId = "";
    String priorityName = "";
    String usergrpId = "";
    String[] alarm_in_time;//110
    int delay_value = 0;//110

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.layout_update_ticket);
            sp_priority = findViewById(R.id.sp_priority);
            list_delay_reason = new ArrayList<String>();//110

            //Utils.createFolder(AppConstants.MEDIA_TEMP_PATH);
            //Utils.createFolder(AppConstants.DOC_PATH);
            //Utils.createFolder(AppConstants.PIC_PATH);
            mAppPreferences = new AppPreferences(UpdateTicket.this);
            gps = new GPSTracker(UpdateTicket.this);
            if (gps.canGetLocation()) {
                latitude = String.valueOf(gps.getLatitude());
                longitude = String.valueOf(gps.getLongitude());
            } else {
                gps.showSettingsAlert();
            }
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                //e.printStackTrace();
            }
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                callGetRttsFaultAreaApi("");
                callGetRttsFaultAreaApi1("" + getIntent().getExtras().getString("fouAreaID"));
            } else {
                Utils.toast(UpdateTicket.this, "17");
            }
            ticketType = Utils.msg(this, "836");
            getControllerId();
            setMsg(); // set multilanguage text with font supported
            getPreRequiredValue();// getPreRequiredValue for display and update
            getDataFromLocal(); // get data from local
            init();// get id from design view
            previousBindValue(); // show previous value in view
            showHideValue(); // show hide field status
            superUser();


            tv_chk_link.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(UpdateTicket.this, ViewPMCheckList.class);
                    i.putExtra("S", "D");
                    i.putExtra("scheduledDate", "01-JAN-2020");
                    i.putExtra("siteId", Site_Id);
                    String Sname = null;
                    i.putExtra("siteName", Sname);
                    i.putExtra("activityTypeId", alarm_descId);
                    i.putExtra("paramName", "");
                    i.putExtra("Status", "D");
                    i.putExtra("dgType", "0");
                    i.putExtra("txn", ticket_id);
                    i.putExtra("etsSid", etsid);
                    i.putExtra("imgUploadFlag", "2");
                    i.putExtra("rvDate", "");
                    i.putExtra("rejRmks", "");
                    i.putExtra("rCat", "");
                    //startActivity(i);

                    if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                        new GetImage(UpdateTicket.this, i, ticket_id, "01-JAN-2020",
                                alarm_descId, str_siteId, etsid, "0", "2").execute();
                    } else {
                        //No Internet Connection;
                        Utils.toast(UpdateTicket.this, "17");
                    }

                }
            });

            tv_spare_parts.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //SparePartPopup();
                    Intent i = new Intent(UpdateTicket.this, SparePart.class);
                    i.putExtra("tranID", ticket_id);
                    i.putExtra("etsSid", etsid);
                    i.putExtra("activityMode", "1");
                    if (ticket_typeId == null || ticket_typeId.isEmpty()
                            || ticket_typeId.length() == 0) {
                        ticket_typeId = "-1";
                    }
                    i.putExtra("typeId", ticket_typeId);
                    startActivity(i);
                }
            });



            sp_operator.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OperatorpopUp();
                }
            });

            sp_operator_exempted.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OperatorExemptPopUp(1);
                }
            });

            tv_dis_bttry_Date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID2);
                }
            });

            tv_dis_bttry_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_dis_bttry_Date.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID2);
                    else
                        // Toast.makeText(UpdateTicket.this,"Select Discarge Start Date first",Toast.LENGTH_SHORT).show();
                        Utils.toast(UpdateTicket.this, "125");
                }
            });

            txt_problem_start_date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID);
                }
            });
            tv_serviceImpactStart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID);
                }
            });

             txt_ticket_log_date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID4);
                }
            });




            tv_serviceImpactStartTime.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_serviceImpactStart.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID);
                    else
                         Toast.makeText(UpdateTicket.this,"Select Impact Start Date first",Toast.LENGTH_SHORT).show();
//                        Utils.toast(UpdateTicket.this, "126");
                }
            });
            txt_problem_start_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_problem_start_date.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID);
                    else
                        // Toast.makeText(UpdateTicket.this,"Select Problem Start Date first",Toast.LENGTH_SHORT).show();
                        Utils.toast(UpdateTicket.this, "126");
                }
            });

            txt_problem_end_date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID1);
                }
            });

            txt_problem_end_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_problem_end_date.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID1);
                    else
                        // Toast.makeText(UpdateTicket.this,"Select Problem end Date first",
                        // Toast.LENGTH_SHORT).show();
                        Utils.toast(UpdateTicket.this, "127"); // Select Problem end
                    // Date first
                }
            });

            txt_ticket_log_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_ticket_log_date.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID5);
                    else
                        Toast.makeText(UpdateTicket.this,"Select Ticket Log Date First",Toast.LENGTH_SHORT).show();

                }
            });


            bt_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    backButtonAlert("291", "63", "64");
                }
            });

            tv_grp_user.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (!db.getUpdateTTField("Assigned To", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
                        if (sp_group.getSelectedItem().toString().trim().equals("Select " + tv_group.getText().toString().trim())) {
                            Toast.makeText(UpdateTicket.this, "Select " + tv_group.getText().toString().trim(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            grpUser();
                        }
                    }
                }
            });

            sp_approval2.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject")) {
                        ll_reject_category.setVisibility(View.VISIBLE);
                    } else {
                        ll_reject_category.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            sp_rca_category.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (sp_rca_category.getSelectedItem().toString()
                            .equalsIgnoreCase("Select RCA Category")) {
                        et_rca.setText("");
                        et_rca.setEnabled(false);
                        ll_asset.setVisibility(View.GONE); //1.5
                    } else {
                        et_rca.setEnabled(true);
                    }
                    rca_id = db.getRcaId(sp_rca_category.getSelectedItem().toString(), "N", mAppPreferences.getTTModuleSelection());
                    rca_sub_category_name = db.getRcaName(rca_id, 2, mAppPreferences.getTTModuleSelection(),
                            0, "0");
					/*if(mAppPreferences.getUserCategory().equalsIgnoreCase("2")){
						rca_sub_category_name = db.getRcaName(rca_id, 2,mAppPreferences.getTTModuleSelection(),
								2,mAppPreferences.getUserSubCategory());
					}else{
						rca_sub_category_name = db.getRcaName(rca_id, 2,mAppPreferences.getTTModuleSelection(),
								0,"0");
					}*/

                    //1.5 start
                    LinkedHashMap<String, String> RcaCatIdHashMap = new LinkedHashMap<String, String>();
                    if(Utils.msg(UpdateTicket.this, "876").contains(",")){
                        String [] arr = Utils.msg(UpdateTicket.this, "876").split(",");
                        if(arr.length>0) {
                            for (int a = 0; a < arr.length; a++) {
                                RcaCatIdHashMap.put(arr[a], arr[a]);
                            }
                        }
                    }else{
                        RcaCatIdHashMap.put(Utils.msg(UpdateTicket.this, "876")
                                , Utils.msg(UpdateTicket.this, "876"));
                    }

                    if(RcaCatIdHashMap.containsKey(rca_id)){
                        ll_asset.setVisibility(View.VISIBLE);
                        //et_asset.setText(getIntent().getExtras().getString("assetId"));
                    }else{
                        ll_asset.setVisibility(View.GONE);
                        //et_asset.setText(getIntent().getExtras().getString("assetId"));
                    }

                    if(getIntent().getExtras().getString("assetId")!=null){
                        if (getIntent().getExtras().getString("assetId")
                                .equalsIgnoreCase("null")){
                            et_asset.setText("");
                        }else{
                            et_asset.setText(getIntent().getExtras().getString("assetId"));
                        }
                    }else{
                        et_asset.setText("");
                    }


                    if (!rca_sub_category_na.equalsIgnoreCase("NO")
                            && !rca_sub_category_name.contains(rca_sub_category_na)
                            && category_name != null
                            && sp_rca_category.getSelectedItem().toString()
                            .equalsIgnoreCase(category_name)) {
                        rca_sub_category_name.add(rca_sub_category_na);
                    }
                    addItemsOnSpinner(sp_rca_category_sub, rca_sub_category_name);
                    if (a == 0) {
                        int pos22 = getCategoryPos(rca_sub_category_na,
                                rca_sub_category_name);
                        if (pos22 == -1) {
                            sp_rca_category_sub.setSelection(0);
                        } else {
                            sp_rca_category_sub.setSelection(pos22);
                        }
                        a = 1;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_status_reason.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    statusResion = sp_status_reason.getSelectedItem().toString().trim();
                    if (statusResion.equalsIgnoreCase("Required Permanent Solution")) {
                        ll_assigned.setVisibility(View.VISIBLE);
                        ll_user.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_serviceImapact.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    serviceImapactID = db.getInciParamDesc("1221", sp_serviceImapact.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    if(spinnerFirstTime) {
                        spinnerFirstTime = false;

                    }
                    else
                    {
                        if (!sp_serviceImapact.getSelectedItem().toString().equalsIgnoreCase("Select Service Impact")) {
                            callPriorityService(str_siteId, severity_Id, serviceImapactID);
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_resolution_method.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    resoulationMethod = sp_resolution_method.getSelectedItem().toString().trim();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_fault_area.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    faultarea = sp_fault_area.getSelectedItem().toString().trim();
                    int id = sp_fault_area.getSelectedItemPosition();
                    String p;
                    if (id == 0) {
                        p = "";
                    } else {
                        p = rttsFoultAreaList.get(id - 1).getFaultid();
                    }

                    if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                        callGetRttsFaultAreaApi1(p);
                    } else {
                        Utils.toast(UpdateTicket.this, "17");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_fault_area_details.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    faultareadetails = sp_fault_area_details.getSelectedItem().toString().trim();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            sp_status_approve.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    spstatusapprove = sp_status_approve.getSelectedItem().toString().trim();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            //110
            sp_delay_reason.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    delay_value = Integer.parseInt(db.getInciParamId("1275", sp_delay_reason.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //110

            sp_status.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    //3175
                    ll_fault_area.setVisibility(View.GONE);
                    ll_fault_area_details.setVisibility(View.GONE);
                    ll_resolution_method.setVisibility(View.GONE);
                    //3175

                    //int tempStatus = Integer.parseInt(getIntent().getExtras().getString("status"));
                    int tempStatus = Integer.parseInt(db.getInciParamId("62", getIntent().getExtras().getString("status"), mAppPreferences.getTTModuleSelection()));  //1.1
                    int currentStatus = Integer.parseInt(db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()));  //1.1

                    //1.4
                    LinkedHashMap<String, String> groupHashMap = new LinkedHashMap<String, String>();
                    if(mAppPreferences.getUserGroup().contains(",")){
                        String [] arr = mAppPreferences.getUserGroup().split(",");
                        if(arr.length>0) {
                            for (int a = 0; a < arr.length; a++) {
                                groupHashMap.put(arr[a], arr[a]);
                            }
                        }
                    }else{
                        groupHashMap.put(mAppPreferences.getUserGroup(), mAppPreferences.getUserGroup());
                    }

                    if(loggedBy.equalsIgnoreCase("Resolution Ticket for Repeated Outage")
                            && Utils.msg(UpdateTicket.this, "874").equalsIgnoreCase(ticket_typeId)
                            && tempStatus==7 && currentStatus==7
                            && groupHashMap.containsKey(getIntent().getExtras().getString("grpId"))
                            && !(isUserSatisfied.equalsIgnoreCase("Yes")
                            || isUserSatisfied.equalsIgnoreCase("No"))){
                        sp_confirmation.setEnabled(true);
                        ll_confirmation.setVisibility(View.VISIBLE);
                    } else if(loggedBy.equalsIgnoreCase("Resolution Ticket for Repeated Outage")
                            && Utils.msg(UpdateTicket.this, "874").equalsIgnoreCase(ticket_typeId)
                            && tempStatus==7 && currentStatus==8
                            && groupHashMap.containsKey(getIntent().getExtras().getString("grpId"))){
                        addItemsOnSpinner(sp_confirmation, list_confirmation);
                        int poa25 = getCategoryPos(isUserSatisfied, list_confirmation);
                        sp_confirmation.setSelection(poa25);
                        sp_confirmation.setEnabled(false);
                        ll_confirmation.setVisibility(View.VISIBLE);
                    }else{
                        ll_confirmation.setVisibility(View.GONE);
                    }
                    //end


                    if (ticket_typeId.equalsIgnoreCase(ticketType)) {
                        et_servicesAffected.setEnabled(false);
                        tv_serviceImpactStart.setEnabled(false);
                        sp_serviceImapact.setEnabled(false);
                        sp_operator.setEnabled(false);
                        sp_operator_exempted.setEnabled(false);
                        if (tempStatus == 2011794 && tempStatus != currentStatus) {
                            if (!prNodetails.equalsIgnoreCase("") && !prNodetails.equalsIgnoreCase("null") && currentStatus != 6) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Assign", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (tempStatus == 2011794 && tempStatus != currentStatus) {
                            if (currentStatus != 2011799 && currentStatus != 6) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Assign or Cancel", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (tempStatus == 6 && tempStatus != currentStatus) {
                            if (currentStatus != 2011799 && currentStatus != 2011794 && currentStatus != 2011795) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Refereed ,In Progress or Cancel", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (tempStatus == 2011795 && tempStatus != currentStatus) {
                            if (currentStatus != 2011796 && currentStatus != 2011799 && currentStatus != 7 && currentStatus != 6) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Restoration ,Resolve or Cancel", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (currentStatus == 6) {
                                if (!prNodetails.equalsIgnoreCase("") && !prNodetails.equalsIgnoreCase("null") && currentStatus != 6) {
                                    addItemsOnSpinner(sp_status, list_status);
                                    int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                    sp_status.setSelection(status_pos);
                                    Toast.makeText(UpdateTicket.this, "Ticket status can be Assign", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }

                       /* if (tempStatus==2011795 && tempStatus != currentStatus) {
                            if (currentStatus != 2011796 && currentStatus != 2011799 && currentStatus != 7) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Restoration ,Resolve or Cancel", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }*/
                        if (tempStatus == 2011796 && tempStatus != currentStatus) {
                            //  if (currentStatus != 2011796 && currentStatus != 2011799 && currentStatus != 7) {
                            addItemsOnSpinner(sp_status, list_status);
                            int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                            sp_status.setSelection(status_pos);
                            Toast.makeText(UpdateTicket.this, "Ticket status can be changed", Toast.LENGTH_LONG).show();
                            return;
                            //  }
                        }
                        if (tempStatus == 7 && tempStatus != currentStatus) {
                            if (currentStatus != 6 && currentStatus != 8) {
                                addItemsOnSpinner(sp_status, list_status);
                                int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                                sp_status.setSelection(status_pos);
                                Toast.makeText(UpdateTicket.this, "Ticket status can be Assign and closed", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    } else {
                        if (((tt_rev_flag.equalsIgnoreCase("0") && currentStatus > currentStatus))
                                || (tt_rev_flag.equalsIgnoreCase("1") && tempStatus > currentStatus && currentStatus == 5
                                || arg2 == 0)) {
                            addItemsOnSpinner(sp_status, list_status);
                            int status_pos = getCategoryPos(getIntent().getExtras().getString("status"), list_status);
                            sp_status.setSelection(status_pos);
                            Toast.makeText(UpdateTicket.this, " Ticket status can not be changed to lower level", Toast.LENGTH_LONG).show();
                            return;
                        }
                       // return;
                    }

                    if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
                        ll_status_reason.setVisibility(View.VISIBLE);
                        ll_fault_area.setVisibility(View.VISIBLE);
                        ll_fault_area_details.setVisibility(View.VISIBLE);
                        ll_resolution_method.setVisibility(View.VISIBLE);
                    }
                     // Issue 3175
                    if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7") && ticket_typeId.equalsIgnoreCase("23")) {
                       // ll_status_reason.setVisibility(View.VISIBLE);
                        ll_fault_area.setVisibility(View.VISIBLE);
                        ll_fault_area_details.setVisibility(View.VISIBLE);
                        ll_resolution_method.setVisibility(View.VISIBLE);

                    }
                    // 3175
                    pShortName = db.getParmName("4", alarm_descId, mAppPreferences.getTTModuleSelection());
                    String ttStatusID = db.getInciParamId("62", sp_status.getSelectedItem()
                            .toString().trim(), mAppPreferences.getTTModuleSelection());
                    if (pDescription.contains("trvlDistnc") && pShortName.contains(ttStatusID) && db.getUpdateTTField("Distance Travelled", mAppPreferences.getTTModuleSelection())
                            .equalsIgnoreCase("Y")) {
                        trvlDis = 1;
                    } else {
                        trvlDis = 0;
                    }

                    if (pDescription.contains("noOfTech") && pShortName.contains(ttStatusID) && db.getUpdateTTField("No. Of Technician", mAppPreferences.getTTModuleSelection())
                            .equalsIgnoreCase("Y")) {
                        noOfTec = 1;
                    } else {
                        noOfTec = 0;
                    }

                    if (pDescription.contains("wrkgNights") && pShortName.contains(ttStatusID) && db.getUpdateTTField("Working Nights", mAppPreferences.getTTModuleSelection())
                            .equalsIgnoreCase("Y")) {
                        wrkgNight = 1;
                    } else {
                        wrkgNight = 0;
                    }

                    if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("5")
                            || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")
                            || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011794")
                            || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011795")) {
                        ll_dg_reading.setVisibility(View.GONE);
                        ll_grid_reading.setVisibility(View.GONE);
                        ll_fuel_level.setVisibility(View.GONE);
                        ll_action_taken.setVisibility(View.GONE);
                        tv_spare_parts.setVisibility(View.GONE);

                        ll_end_date_time.setVisibility(View.GONE);
                        ll_approval2.setVisibility(View.GONE);
                        ll_reject_category.setVisibility(View.GONE);

                        if (offLineFlag.equalsIgnoreCase("1") && hide_rca_category.equalsIgnoreCase("Y")) {
                            ll_rca_category.setVisibility(View.VISIBLE);
                        } else {
                            ll_rca_category.setVisibility(View.GONE);
                        }

                        if (offLineFlag.equalsIgnoreCase("1") && hide_rca_sub_category.equalsIgnoreCase("Y")) {
                            ll_rca_sub_category.setVisibility(View.VISIBLE);// 0.1
                        } else {
                            ll_rca_sub_category.setVisibility(View.GONE);// 0.1
                        }

                        if (offLineFlag.equalsIgnoreCase("1") && hide_rca.equalsIgnoreCase("Y")) {
                            ll_rca.setVisibility(View.VISIBLE);
                        } else {
                            ll_rca.setVisibility(View.GONE);
                        }

                        cb_add_ref_ticket.setVisibility(View.GONE);
                        cb_add_ref_ticket.setChecked(false);
                        if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")
                                && !hide_group.equalsIgnoreCase("N")) {
                            ll_assigned.setVisibility(View.VISIBLE);
                            ll_user.setVisibility(View.VISIBLE);

                            if (mAppPreferences.getUserCategory().equalsIgnoreCase("2") &&
                                    (mAppPreferences.getOperatorWiseUserField().equalsIgnoreCase("0") ||
                                            mAppPreferences.getOperatorWiseUserField().equalsIgnoreCase("3"))) {//1.0
                                ll_user.setVisibility(View.GONE);
                            }

                            if (db.getUpdateTTField("First Level Approval",
                                    mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
                                ll_approval1.setVisibility(View.GONE);
                            } else {
                                if (approvalNeed.equalsIgnoreCase("1")
                                        || approvalNeed.equalsIgnoreCase("3")) {
                                    ll_approval1.setVisibility(View.VISIBLE);
                                    addItemsOnSpinner(sp_approval1, list_approval1);
                                    int poa14 = getCategoryPos(getIntent().getExtras().getString("firstLevel"), list_approval1);
                                    sp_approval1.setSelection(poa14);
                                } else {
                                    ll_approval1.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            ll_assigned.setVisibility(View.GONE);
                            ll_user.setVisibility(View.GONE);
                            ll_approval1.setVisibility(View.GONE);
                        }
                    } else { // block for resolved or closed
                        if (hide_dg != null && hide_dg.length() > 0 && (hide_dg.equalsIgnoreCase("Y")
                                || hide_dg.equalsIgnoreCase("M"))) {
                            ll_dg_reading.setVisibility(View.VISIBLE);
                        } else {
                            ll_dg_reading.setVisibility(View.GONE);
                        }

                        if (hide_grid != null && hide_grid.length() > 0 && (hide_grid.equalsIgnoreCase("Y")
                                || hide_grid.equalsIgnoreCase("M"))) {
                            ll_grid_reading.setVisibility(View.VISIBLE);
                        } else {
                            ll_grid_reading.setVisibility(View.GONE);
                        }

                        if (hide_fuel != null && hide_fuel.length() > 0 && (hide_fuel.equalsIgnoreCase("Y")
                                || hide_fuel.equalsIgnoreCase("M"))) {
                            ll_fuel_level.setVisibility(View.VISIBLE);
                        } else {
                            ll_fuel_level.setVisibility(View.GONE);
                        }

                        if (hide_action != null && hide_action.length() > 0 && (hide_action.equalsIgnoreCase("Y")
                                || hide_action.equalsIgnoreCase("M"))) {
                            ll_action_taken.setVisibility(View.VISIBLE);
                        } else {
                            ll_action_taken.setVisibility(View.GONE);
                        }

                        if (spareVisable.equalsIgnoreCase("Y")) {
                            tv_spare_parts.setVisibility(View.VISIBLE);
                        } else {
                            tv_spare_parts.setVisibility(View.GONE);
                        }


                        if (hide_problem_end_date_time.equalsIgnoreCase("Y")) {
                            ll_end_date_time.setVisibility(View.VISIBLE);
                        } else {
                            ll_end_date_time.setVisibility(View.GONE);
                        }

                        if (hide_rca_category.equalsIgnoreCase("Y")) {
                            ll_rca_category.setVisibility(View.VISIBLE);
                        } else {
                            ll_rca_category.setVisibility(View.GONE);
                        }

                        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
                            ll_rca_sub_category.setVisibility(View.VISIBLE);
                        } else {
                            ll_rca_sub_category.setVisibility(View.GONE);
                        }

                        if (hide_rca.equalsIgnoreCase("Y")) {
                            ll_rca.setVisibility(View.VISIBLE);
                        } else {
                            ll_rca.setVisibility(View.GONE);
                        }
                        ll_assigned.setVisibility(View.GONE);
                        ll_user.setVisibility(View.GONE);

                        if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(),
                                        mAppPreferences.getTTModuleSelection())
                                .equalsIgnoreCase("8")) {
                            ll_approval1.setVisibility(View.GONE);
                            ll_approval2.setVisibility(View.GONE);
                            ll_reject_category.setVisibility(View.GONE);
                            if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")) {
                                //cb_add_ref_ticket.setVisibility(View.VISIBLE); //1.3
                                cb_add_ref_ticket.setVisibility(View.GONE);
                                cb_add_ref_ticket.setChecked(true);
                            } else {
                                cb_add_ref_ticket.setVisibility(View.GONE);
                                cb_add_ref_ticket.setChecked(false);
                            }
                        } else {
                            cb_add_ref_ticket.setVisibility(View.GONE);
                            cb_add_ref_ticket.setChecked(false);
                            if (db.getUpdateTTField("First Level Approval",
                                    mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
                                ll_approval1.setVisibility(View.GONE);
                            } else {
                                if (approvalNeed.equalsIgnoreCase("1")
                                        || approvalNeed.equalsIgnoreCase("3")) {
                                    ll_approval1.setVisibility(View.VISIBLE);
                                    addItemsOnSpinner(sp_approval1, list_approval1);
                                    int poa14 = getCategoryPos(getIntent().getExtras().getString("firstLevel"), list_approval1);
                                    sp_approval1.setSelection(poa14);
                                } else {
                                    ll_approval1.setVisibility(View.GONE);
                                }
                            }

                            if (db.getUpdateTTField("Second Level Approval",
                                    mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
                                ll_approval2.setVisibility(View.GONE);
                            } else {
                                if (approvalNeed.equalsIgnoreCase("2")
                                        || approvalNeed.equalsIgnoreCase("3")) {
                                    ll_approval2.setVisibility(View.VISIBLE);
                                    addItemsOnSpinner(sp_approval2, list_approval2);
                                    int poa15 = getCategoryPos(getIntent().getExtras().getString("secondLevel"),
                                            list_approval2);
                                    sp_approval2.setSelection(poa15);
                                    addItemsOnSpinner(sp_reject_category, list_rejection_category);
                                } else {
                                    ll_approval2.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            btnTakePhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    GPSTracker gps = new GPSTracker(UpdateTicket.this);
                    String Imglatitude1 = "DefaultLatitude", Imglongitude1 = "DefaultLongitude";
                    if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.PERMISSIONS)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //ActivityCompat.requestPermissions(FuelFillingActivity.this, Constants.PERMISSIONS,100);
                        Toast.makeText(UpdateTicket.this, "Permission denied for take pictures or access photos,media,files. Please Re-login", Toast.LENGTH_LONG).show();
                    } else {
                        if (imgCounter <= uploadMax) {
                            if (gps.canGetLocation() == true) {
                                Imglatitude1 = String.valueOf(gps.getLatitude());
                                Imglongitude1 = String.valueOf(gps.getLongitude());
                                if ((Imglatitude1 == null || Imglatitude1.equalsIgnoreCase("0.0") || Imglatitude1.isEmpty())
                                        || (Imglongitude1 == null || Imglongitude1.equalsIgnoreCase("0.0") || Imglongitude1.isEmpty())) {
                                } else {
                                    Imglatitude1 = String.valueOf(gps.getLatitude());
                                    Imglongitude1 = String.valueOf(gps.getLongitude());
                                }
                                //location = Imglatitude+"$"+Imglongitude;
                            }

                            //private void imageCapture(String tag,String Imglatitude1,String Imglongitude1,String fileType)

                            String type = "jpg";
                            if (ttMedia.length >= imgCounter) {
                                int a = imgCounter - 1;
                                type = ttMedia[a];
                            }


                            if (ttmsg.length >= imgCounter) {
                                int a = imgCounter - 1;
                                ImageName(ttmsg[a], Imglatitude1, Imglongitude1, type, 0, "");
                            } else {
                                imageCapture(" ", Imglatitude1, Imglongitude1, type);
                            }
                        } else {
                            // Toast.makeText(PMChecklist.this,"Maximum " +
                            // mAppPreferences.getPMmaximage()+
                            // " Images can be uploaded.",Toast.LENGTH_SHORT).show();
                            String s = Utils.msg(UpdateTicket.this, "253");
                            s = s + " " + uploadMax;
                            s = s + " " + Utils.msg(UpdateTicket.this, "254");
                            Utils.toastMsg(UpdateTicket.this, s);
                        }
                    }
                }
            });

            btn_update_tt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flagA == 0) {
                        All_Operator_Id.setLength(0);
                        All_Operator_Id.append(Operator);
                    }
                    if (flagB == 0) {
                        all_exempt_opt_id.setLength(0);
                        all_exempt_opt_id.append(OperatorExempt);
                    }
                    //3175
                    if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7") && ticket_typeId.equalsIgnoreCase("23")) {
                         if(sp_fault_area.getSelectedItemPosition() == 0)
                        {
                            Toast.makeText(UpdateTicket.this,"Please select fault area",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(sp_fault_area_details.getSelectedItemPosition() ==0)
                        {
                            Toast.makeText(UpdateTicket.this,"Please select fault area details",Toast.LENGTH_SHORT).show();
                            return;
                        }
                       else if(sp_resolution_method.getSelectedItemPosition()==0)
                        {
                            Toast.makeText(UpdateTicket.this,"Please select resolution method",Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                    //3175
                   //115
                    String rmk = et_remarks.getText().toString().trim();
                    if (rmk.isEmpty()) {
                        et_remarks.setError("Remarks Mandatory");
                        et_remarks.setFocusable(true);
                        return;
                    }
                      //115
                    if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
                       rmk = et_remarks.getText().toString().trim();
                        //  String farea = sp_fault_area.getSelectedItem().toString().trim();
                        //  String fareadetails
                        //  = sp_fault_area_details.getSelectedItem().toString().trim();;
                        String resulation = sp_resolution_method.getSelectedItem().toString().trim();
                        if (rmk.isEmpty()) {
                            et_remarks.setError("Remarks Mandatory");
                            et_remarks.setFocusable(true);
                            return;
                        }
                        if (resulation.isEmpty()) {
                            Toast.makeText(UpdateTicket.this, "Please Select Resolution Method", Toast.LENGTH_SHORT).show();
                            sp_resolution_method.setFocusable(true);
                            return;
                        }
                    }
                    if (validateAlarmDescription() == 0) {
                        if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")
                                || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")) {
                            Resolved();
                        } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")) {
                            Assign();
                        } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011794")) {
                            Assign();
                        } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011795")) {
                            Assign();
                        } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
                            // Assign();
                            if (statusResion.equalsIgnoreCase("Required Permanent Solution")) {

                                if (spstatusapprove.equalsIgnoreCase("Rejected")) {
                                    InProgress();
                                } else {
                                    Resostration();
                                }
                            } else {
                                Resolved();
                            }
                        } else {
                            Open();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public void getPreRequiredValue() {
        minImgCounter = getIntent().getExtras().getInt("minImgCounter", 0);
        All_Operator_Id = new StringBuffer();
        All_user_Id = new StringBuffer();
        all_exempt_opt_id = new StringBuffer();
        final Calendar cal = Calendar.getInstance();
        pYear = cal.get(Calendar.YEAR);
        pMonth = cal.get(Calendar.MONTH);
        pDay = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        toc_fo_user = getIntent().getExtras().getString("toc_fo_user");//110
        alarm_in_time = getIntent().getExtras().getString("alarm_in_time").split(" ");
        ttCreaterUserCat = getIntent().getExtras().getString("userCat");
        ttCreaterUserSubCat = getIntent().getExtras().getString("userSubcat");

        offLineFlag = getIntent().getExtras().getString("offLineFlag");
        etsid = getIntent().getExtras().getString("etsid");
        site_status = getIntent().getExtras().getString("site_status");
        equipment = getIntent().getExtras().getString("equipment");
        serviceAffecetd = getIntent().getExtras().getString("serviceAffected");
        serviceImpactStart = getIntent().getExtras().getString("serviceImpactStart");
        serviceImpactStartTime = getIntent().getExtras().getString("serviceImpactStartTime");
        serviceImacted = getIntent().getExtras().getString("serviceImpacted");
        str_siteId = getIntent().getExtras().getString("str_siteId");
        alarm_type = getIntent().getExtras().getString("str_severity");
        alarm_description = getIntent().getExtras().getString("alarm_description");
        equipment_id = getIntent().getExtras().getString("equipment_id");
        severity_Id = getIntent().getExtras().getString("severity_Id");
        priorityName = getIntent().getExtras().getString("priorityName");
        priorityId = getIntent().getExtras().getString("priorityId");
        alarm_descId = getIntent().getExtras().getString("alarm_descId");
        ticket_typeId = getIntent().getExtras().getString("ticket_typeId");
        tkt_log_time = getIntent().getExtras().getString("tkt_log_time");
        alarm_detail = getIntent().getExtras().getString("alarm_detail");
        problem_description = getIntent().getExtras().getString("problem_description");
        ticket_type = getIntent().getExtras().getString("ticket_type");
        ticket_id = getIntent().getExtras().getString("id");
        status = getIntent().getExtras().getString("status");
        resMethod = getIntent().getExtras().getString("resMethod");
        fouArea = getIntent().getExtras().getString("fouArea");
        fouAreaDetails = getIntent().getExtras().getString("fouAreaDetails");
        staResion = getIntent().getExtras().getString("staResion");
        prNodetails = getIntent().getExtras().getString("prNo");
        approvelStatus = getIntent().getExtras().getString("approvelStatus");
        assingTo = getIntent().getExtras().getString("assingTo");
        eta = getIntent().getExtras().getString("eta");
        etr = getIntent().getExtras().getString("etr");
        rca = getIntent().getExtras().getString("rca");
        Site_Id = getIntent().getExtras().getString("Site_Id");
        alarmTxnId = getIntent().getExtras().getString("alarmTxnId");
        if (alarmTxnId == null || alarmTxnId.isEmpty()
                || alarmTxnId.length() == 0) {
            alarmTxnId = "";
        }
        Effective_Site = getIntent().getExtras().getString("EffectiveSites");
        category_name = getIntent().getExtras().getString("rca_category_name");
        PROBLEM_START_DATE = getIntent().getExtras().getString(
                "PROBLEM_START_DATE");
        PROBLEM_START_TIME = getIntent().getExtras().getString(
                "PROBLEM_START_TIME");
        TICKET_RCA = getIntent().getExtras().getString("TICKET_RCA");
        if (TICKET_RCA == null) {
            TICKET_RCA = "NO";
        }
        PROBLEM_END_DATE = getIntent().getExtras()
                .getString("PROBLEM_END_DATE");
        PROBLEM_END_TIME = getIntent().getExtras()
                .getString("PROBLEM_END_TIME");
        // 0.1
        rca_sub_category = getIntent().getExtras()
                .getString("rca_sub_category");
        rca_sub_category_na = getIntent().getExtras().getString(
                "rca_sub_category_name");
        if (rca_sub_category_na == null) {
            rca_sub_category_na = "NO";
        }
        batt_disc_date = getIntent().getExtras().getString("batt_disc_date");
        batt_disc_time = getIntent().getExtras().getString("batt_disc_time");
        disableSupperUserField = getIntent().getExtras().getBoolean("enableField");

        trvlDistnc = getIntent().getExtras().getString("trvlDistnc");
        noOfTech = getIntent().getExtras().getString("noOfTech");
        wrkgNights = getIntent().getExtras().getString("wrkgNights");
        tt_flag = getIntent().getExtras().getString("tt_flag");
        tt_rev_flag = getIntent().getExtras().getString("tt_rev_flag");
        if (trvlDistnc != null && !trvlDistnc.equalsIgnoreCase("null")) {
            trvlDistnc = trvlDistnc;
        } else {
            trvlDistnc = "";
        }

        if (noOfTech != null && !noOfTech.equalsIgnoreCase("null")) {
            noOfTech = noOfTech;
        } else {
            noOfTech = "";
        }

        if (wrkgNights != null && !wrkgNights.equalsIgnoreCase("null")) {
            wrkgNights = wrkgNights;
        } else {
            wrkgNights = "";
        }

        isUserSatisfied = getIntent().getExtras().getString("isUserSatisfied");
        loggedBy = getIntent().getExtras().getString("loggedBy");
    }

    public void superUser() {
        if (disableSupperUserField == true
                && db.getInciParamId("62", status.trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")) {
            sp_status.setEnabled(false);
            sp_site_visibility.setEnabled(false);
            et_eta.setEnabled(false);
            et_etr.setEnabled(false);
            sp_operator_exempted.setEnabled(false);
            et_remarks.setEnabled(false);
            cb_add_ref_ticket.setEnabled(false);
            btnTakePhoto.setEnabled(false);
            tv_dis_bttry_Date.setEnabled(false);
            tv_dis_bttry_time.setEnabled(false);
            sp_ticket_treatment.setEnabled(false);
        }
       //110
       else if(status.equalsIgnoreCase("Closed") && toc_fo_user.equalsIgnoreCase("Yes") && ticket_typeId.equalsIgnoreCase("23"))
        {
          //  usergrpId = Utils.msg(UpdateTicket.this, "906");
            sp_status.setEnabled(false);
            sp_group.setEnabled(false);
            tv_grp_user.setEnabled(false);
            sp_status_reason.setEnabled(false);
            sp_fault_area.setEnabled(false);
            sp_fault_area_details.setEnabled(false);
            sp_resolution_method.setEnabled(false);
            sp_status_approve.setEnabled(false);
            sp_site_visibility.setEnabled(false);
            sp_serviceImapact.setEnabled(false);
            sp_priority.setEnabled(false);
            tv_serviceImpactStart.setEnabled(false);
            tv_serviceImpactStartTime.setEnabled(false);
            et_servicesAffected.setEnabled(false);
            et_servicesAffected.setFocusable(false);
            txt_problem_start_date.setEnabled(false);
            txt_problem_start_time.setEnabled(false);
            txt_problem_end_date.setEnabled(false);
            txt_problem_end_time.setEnabled(false);
            tv_dis_bttry_Date.setEnabled(false);
            tv_dis_bttry_time.setEnabled(false);
            et_trvlDistnc.setEnabled(false);
            et_trvlDistnc.setFocusable(false);
            et_noOfTech.setEnabled(false);
            et_noOfTech.setFocusable(false);
            et_wrkgNights.setEnabled(false);
            et_wrkgNights.setFocusable(false);
            et_eta.setEnabled(false);
            et_eta.setFocusable(false);
            et_etr.setEnabled(false);
            et_etr.setFocusable(false);
            sp_rca_category.setEnabled(false);
            sp_rca_category_sub.setEnabled(false);
            et_rca.setEnabled(false);
            et_rca.setFocusable(false);
            et_asset.setEnabled(false);
            et_asset.setFocusable(false);
            sp_operator.setEnabled(false);
            sp_operator_exempted.setEnabled(false);
            et_dependent_site.setEnabled(false);
            et_dependent_site.setFocusable(false);
            et_dg_reading.setEnabled(false);
            et_dg_reading.setFocusable(false);
            et_grid_reading.setEnabled(false);
            et_grid_reading.setFocusable(false);
            et_fuel_level.setEnabled(false);
            et_fuel_level.setFocusable(false);
            sp_action_taken.setEnabled(false);
            sp_ticket_treatment.setEnabled(false);
            sp_delay_reason.setEnabled(true);
            et_hubSiteId.setEnabled(false);
            et_hubSiteId.setFocusable(false);
            sp_approval1.setEnabled(false);
            sp_approval2.setEnabled(false);
            sp_reject_category.setEnabled(false);
            et_reject_remarks.setEnabled(false);
            et_reject_remarks.setFocusable(false);
            sp_confirmation.setEnabled(false);
            et_remarks.setEnabled(false);
            et_remarks.setFocusable(false);
            cb_add_ref_ticket.setEnabled(false);
            btnTakePhoto.setEnabled(false);
            tv_spare_parts.setEnabled(false);

        }
        else if(toc_fo_user.equalsIgnoreCase("Yes") && ticket_typeId.equalsIgnoreCase("23") && (checkUser() || checkAssignedUser()))
        {
            sp_delay_reason.setEnabled(true);
            txt_ticket_log_date.setEnabled(true);
            txt_ticket_log_time.setEnabled(true);
        }
        else if(!status.equalsIgnoreCase("Closed") && toc_fo_user.equalsIgnoreCase("Yes") && ticket_typeId.equalsIgnoreCase("23"))
        {
            //  usergrpId = Utils.msg(UpdateTicket.this, "906");
            sp_status.setEnabled(false);
            sp_group.setEnabled(false);
            tv_grp_user.setEnabled(false);
            sp_status_reason.setEnabled(false);
            sp_fault_area.setEnabled(false);
            sp_fault_area_details.setEnabled(false);
            sp_resolution_method.setEnabled(false);
            sp_status_approve.setEnabled(false);
            sp_site_visibility.setEnabled(false);
            sp_serviceImapact.setEnabled(false);
            sp_priority.setEnabled(false);
            tv_serviceImpactStart.setEnabled(false);
            tv_serviceImpactStartTime.setEnabled(false);
            et_servicesAffected.setEnabled(false);
            et_servicesAffected.setFocusable(false);
            txt_problem_start_date.setEnabled(false);
            txt_problem_start_time.setEnabled(false);
            txt_problem_end_date.setEnabled(false);
            txt_problem_end_time.setEnabled(false);
            tv_dis_bttry_Date.setEnabled(false);
            tv_dis_bttry_time.setEnabled(false);
            et_trvlDistnc.setEnabled(false);
            et_trvlDistnc.setFocusable(false);
            et_noOfTech.setEnabled(false);
            et_noOfTech.setFocusable(false);
            et_wrkgNights.setEnabled(false);
            et_wrkgNights.setFocusable(false);
            et_eta.setEnabled(false);
            et_eta.setFocusable(false);
            et_etr.setEnabled(false);
            et_etr.setFocusable(false);
            sp_rca_category.setEnabled(false);
            sp_rca_category_sub.setEnabled(false);
            et_rca.setEnabled(false);
            et_rca.setFocusable(false);
            et_asset.setEnabled(false);
            et_asset.setFocusable(false);
            sp_operator.setEnabled(false);
            sp_operator_exempted.setEnabled(false);
            et_dependent_site.setEnabled(false);
            et_dependent_site.setFocusable(false);
            et_dg_reading.setEnabled(false);
            et_dg_reading.setFocusable(false);
            et_grid_reading.setEnabled(false);
            et_grid_reading.setFocusable(false);
            et_fuel_level.setEnabled(false);
            et_fuel_level.setFocusable(false);
            sp_action_taken.setEnabled(false);
            sp_ticket_treatment.setEnabled(false);
            sp_delay_reason.setEnabled(true);
            txt_ticket_log_date.setEnabled(true);
            txt_ticket_log_time.setEnabled(true);
            et_hubSiteId.setEnabled(false);
            et_hubSiteId.setFocusable(false);
            sp_approval1.setEnabled(false);
            sp_approval2.setEnabled(false);
            sp_reject_category.setEnabled(false);
            et_reject_remarks.setEnabled(false);
            et_reject_remarks.setFocusable(false);
            sp_confirmation.setEnabled(false);
            et_remarks.setEnabled(false);
            et_remarks.setFocusable(false);
            cb_add_ref_ticket.setEnabled(false);
            btnTakePhoto.setEnabled(false);
            tv_spare_parts.setEnabled(false);

        }


       //110
        else if (db.getInciParamId("62", status.trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011794")) {
            sp_site_visibility.setEnabled(false);
            ll_assigned.setVisibility(View.VISIBLE);
            tv_spare_parts.setEnabled(false);

            ll_user.setVisibility(View.VISIBLE);
            sp_group.setEnabled(false);
            txt_problem_start_date.setEnabled(false);
            txt_problem_start_time.setEnabled(false);
            txt_problem_end_date.setEnabled(false);
            txt_problem_end_time.setEnabled(false);
            sp_rca_category.setEnabled(false);
            sp_rca_category_sub.setEnabled(false);
            // sp_operator.setEnabled(false);
            et_dependent_site.setEnabled(false);
            et_dg_reading.setEnabled(false);
            et_grid_reading.setEnabled(false);
            et_fuel_level.setEnabled(false);
            sp_action_taken.setEnabled(false);
            et_eta.setEnabled(false);
            et_etr.setEnabled(false);
            //sp_operator_exempted.setEnabled(false);
            et_remarks.setEnabled(false);
            cb_add_ref_ticket.setEnabled(false);
            btnTakePhoto.setEnabled(false);
            tv_dis_bttry_Date.setEnabled(false);
            tv_dis_bttry_time.setEnabled(false);
            sp_ticket_treatment.setEnabled(false);
            et_hubSiteId.setEnabled(false);
        } else if (ticket_typeId.equalsIgnoreCase(ticketType) && db.getInciParamId("62", status.trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")) {
            sp_site_visibility.setEnabled(false);
            ll_assigned.setVisibility(View.VISIBLE);
            sp_group.setEnabled(true);
            tv_spare_parts.setEnabled(false);

            ll_user.setVisibility(View.VISIBLE);
            txt_problem_start_date.setEnabled(false);
            txt_problem_start_time.setEnabled(false);
            txt_problem_end_date.setEnabled(false);
            txt_problem_end_time.setEnabled(false);
            sp_rca_category.setEnabled(false);
            sp_rca_category_sub.setEnabled(false);
            //sp_operator.setEnabled(false);
            et_dependent_site.setEnabled(false);
            et_dg_reading.setEnabled(false);
            et_grid_reading.setEnabled(false);
            et_fuel_level.setEnabled(false);
            sp_action_taken.setEnabled(false);
            et_eta.setEnabled(false);
            et_etr.setEnabled(false);
            // sp_operator_exempted.setEnabled(false);
            et_remarks.setEnabled(false);
            cb_add_ref_ticket.setEnabled(false);
            btnTakePhoto.setEnabled(false);
            tv_dis_bttry_Date.setEnabled(false);
            tv_dis_bttry_time.setEnabled(false);
            sp_ticket_treatment.setEnabled(false);
            et_hubSiteId.setEnabled(false);
        } else {

        }
    }

    public void getDataFromLocal() {
        mAppPreferences.setSpareSerialFlagScreen(0);
        mAppPreferences.setTrackMode(0);
        db = new DataBaseHelper(UpdateTicket.this);
        db.open();
        approvalNeed = db.getParmShortName("61", severity_Id, mAppPreferences.getTTModuleSelection());
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            moduleUrl = db.getModuleIP("HealthSafty");
            try {
                uploadMax = mAppPreferences.getTTmaximageHS();
                uploadMin = mAppPreferences.getTTminimageHS();
                if (mAppPreferences.getTTimageMessageHS().length() != 0) {
                    ttmsg = mAppPreferences.getTTimageMessageHS().split("\\~");
                } else {
                    ttmsg = "image1~image2~image3~image4~image5~image6".split("\\~");
                }

                if (mAppPreferences.getTTMediaFileTypeHS().length() != 0) {
                    ttMedia = mAppPreferences.getTTMediaFileTypeHS().split(",");
                } else {
                    ttMedia = "jpg,jpg,jpg,jpg,jpg,jpg".split(",");
                }
            } catch (Exception e) {
                uploadMax = 0;
                uploadMin = 0;
                ttmsg = "image1~image2~image3~image4~image5~image6".split("\\~");
                ttMedia = "jpg,jpg,jpg,jpg,jpg,jpg".split(",");
            }

            maxImgCounter = uploadMax - minImgCounter;
            if (maxImgCounter <= 0) {
                imgCounter = uploadMax + 1;
            } else {
                imgCounter = minImgCounter + 1;
            }


        } else {
            moduleUrl = db.getModuleIP("Incident");
            try {
                uploadMax = mAppPreferences.getTTmaximage();
                uploadMin = mAppPreferences.getTTminimage();
                if (mAppPreferences.getTTimageMessage().length() != 0) {
                    ttmsg = mAppPreferences.getTTimageMessage().split("\\~");
                } else {
                    ttmsg = "image1~image2~image3~image4~image5~image6".split("\\~");
                }

                if (mAppPreferences.getTTMediaFileType().length() != 0) {
                    ttMedia = mAppPreferences.getTTMediaFileType().split(",");
                } else {
                    ttMedia = "jpg,jpg,jpg,jpg,jpg,jpg".split(",");
                }

            } catch (Exception e) {
                uploadMax = 0;
                uploadMin = 0;
                ttmsg = "image1~image2~image3~image4~image5~image6".split("\\~");
                ttMedia = "jpg,jpg,jpg,jpg,jpg,jpg".split(",");
            }

            maxImgCounter = uploadMax - minImgCounter;
            if (maxImgCounter <= 0) {
                imgCounter = uploadMax + 1;
            } else {
                imgCounter = minImgCounter + 1;
            }
        }
        list_status = db.getInciParam1("62", tv_status, mAppPreferences.getTTModuleSelection());
        list_delay_reason = db.getInciParam1("1275",tv_delay_reason, mAppPreferences.getTTModuleSelection());//110
        if (ticket_typeId.equalsIgnoreCase(ticketType)) {

        } else {
            for (int i = 0; i < list_status.size(); i++) {

                if (list_status.get(i).equalsIgnoreCase("Refered") ||
                        list_status.get(i).equalsIgnoreCase("Cancel") ||
                        list_status.get(i).equalsIgnoreCase("Restoration") ||
                        list_status.get(i).equalsIgnoreCase("In Progress")) {

                    list_status = removeElements(list_status, list_status.get(i));

                } else {

                }
            }
        }
        // list_faoult_area = getfoutlArealist("");
        //list_fault_area_details = getfoutlAreaDetailslist(getIntent().getExtras().getString("fouAreaDetailsID"));
        list_status_resion = db.getInciParam1("1218", tv_status_reason, mAppPreferences.getTTModuleSelection());
        list_service_impact = db.getInciParam1("1221", tv_serviceImapact, mAppPreferences.getTTModuleSelection());
        list_status_aaprove = db.getInciParam1("1219", tv_status_approve, mAppPreferences.getTTModuleSelection());
        list_method_resolation = db.getInciParam1("1222", tv_resolution_method, mAppPreferences.getTTModuleSelection());
        list_action = db.getInciParam1("1174", tv_action_taken, mAppPreferences.getTTModuleSelection());
        list_approval1 = db.getInciParam1("100", tv_approval1, mAppPreferences.getTTModuleSelection());
        list_approval2 = db.getInciParam1("100", tv_approval2, mAppPreferences.getTTModuleSelection());
        list_tt_treatement = db.getInciParam1("1192", tv_ticket_treatment, mAppPreferences.getTTModuleSelection());
        list_rejection_category = db.getInciParam1("117", tv_reject_category, mAppPreferences.getTTModuleSelection());
        list_confirmation = db.getInciParam1("1208",
                tv_isSatisfy, mAppPreferences.getTTModuleSelection());
        if (ttCreaterUserCat.equalsIgnoreCase("2")) {
            rca_category_name = db.getRcaName("N", 1, mAppPreferences.getTTModuleSelection(),
                    2, ttCreaterUserSubCat);
        } else {
            rca_category_name = db.getRcaName("N", 1, mAppPreferences.getTTModuleSelection(),
                    0, "0");
        }


        if (category_name != null && !rca_category_name.contains(category_name)) {
            rca_category_name.add(category_name);
        }
        //list_group = db.getInciGrp(tv_group);
        list_operator_name = db.getOperatorName(mAppPreferences.getTTModuleSelection());
        list_operator_id = db.getOperatorId(mAppPreferences.getTTModuleSelection());
        siteStatusList = new ArrayList<String>();
        siteStatusList.add("No");
        siteStatusList.add("Yes");

        if (Utils.isNetworkAvailable(UpdateTicket.this)) {
            GetSiteGroupTask task = new GetSiteGroupTask(UpdateTicket.this);
            task.execute();
        } else {
            //list_group.clear();
            list_group = db.getInciGrp(tv_group, mAppPreferences.getTTModuleSelection());
            addItemsOnSpinner(sp_group, list_group);
            int group_pos = getCategoryPos(assingTo, list_group);
            sp_group.setSelection(group_pos);
            //group();
        }

    }


    public static ArrayList<String> removeElements(ArrayList<String> input, String deleteMe) {
        ArrayList result = new ArrayList();
        input.subList(1, 5).clear();
        for (String item : input) {
            // if (!deleteMe.equals(item))
            result.add(item);
        }
        return result;
    }

    public void getControllerId() {
        //1.5 start
        ll_asset = (LinearLayout) findViewById(R.id.ll_asset);
        tv_asset = (TextView) findViewById(R.id.tv_asset);
        et_asset = (EditText) findViewById(R.id.et_asset);
        et_asset.setBackgroundResource(R.drawable.input_box);
        //end
        ll_confirmation = (LinearLayout) findViewById(R.id.ll_confirmation);
        tv_isSatisfy = (TextView) findViewById(R.id.tv_isSatisfy);
        sp_confirmation = (Spinner) findViewById(R.id.sp_confirmation);

        sp_ticket_treatment = (Spinner) findViewById(R.id.sp_ticket_treatment);
        ll_ticket_treatment = (LinearLayout) findViewById(R.id.ll_ticket_treatment);
        ll_reject_category = (LinearLayout) findViewById(R.id.ll_reject_category);
        tv_reject_category = (TextView) findViewById(R.id.tv_reject_category);
        sp_reject_category = (Spinner) findViewById(R.id.sp_reject_category);
        tv_reject_remarks = (TextView) findViewById(R.id.tv_reject_remarks);
        et_reject_remarks = (EditText) findViewById(R.id.et_reject_remarks);

        ll_approval1 = (LinearLayout) findViewById(R.id.ll_approval1);
        ll_approval2 = (LinearLayout) findViewById(R.id.ll_approval2);
        tv_approval1 = (TextView) findViewById(R.id.tv_approval1);
        tv_approval2 = (TextView) findViewById(R.id.tv_approval2);
        sp_approval1 = (Spinner) findViewById(R.id.sp_approval1);
        sp_approval2 = (Spinner) findViewById(R.id.sp_approval2);
        tv_chk_link = (TextView) findViewById(R.id.tv_chk_link);
        tv_chk_link.setPaintFlags(tv_chk_link.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        grid = (RecyclerView) findViewById(R.id.grid);
        grid.setNestedScrollingEnabled(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(UpdateTicket.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        grid.setLayoutManager(layoutManager);

        btnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        tv_spare_parts = (TextView) findViewById(R.id.tv_spare_parts);
        Utils.msgText(UpdateTicket.this, "279", tv_spare_parts);
        tv_spare_parts.setPaintFlags(tv_spare_parts.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);


        tv_click_img = (TextView) findViewById(R.id.tv_click_img);
        Utils.msgText(UpdateTicket.this, "240", tv_click_img);
        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_group = (TextView) findViewById(R.id.tv_group);
        tv_site_visibility = (TextView) findViewById(R.id.tv_site_visibility);
        tv_problem_start_date = (TextView) findViewById(R.id.tv_problem_start_date);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_problem_end_date = (TextView) findViewById(R.id.tv_problem_end_date);
        tv_problem_end_time = (TextView) findViewById(R.id.tv_problem_end_time);
        tv_dis_Date = (TextView) findViewById(R.id.tv_dis_Date);
        tv_dis_time = (TextView) findViewById(R.id.tv_dis_time);
        tv_eta = (TextView) findViewById(R.id.tv_eta);
        tv_etr = (TextView) findViewById(R.id.tv_etr);
        tv_rca_category = (TextView) findViewById(R.id.tv_rca_category);
        tv_rca_category_sub = (TextView) findViewById(R.id.tv_rca_category_sub);
        tv_rca = (TextView) findViewById(R.id.tv_rca);
        tv_operator = (TextView) findViewById(R.id.tv_operator);
        tv_operator_exempted = (TextView) findViewById(R.id.tv_operator_exempted);
        tv_dependent_site = (TextView) findViewById(R.id.tv_dependent_site);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        ll_assigned = (LinearLayout) findViewById(R.id.ll_assigned);
        ll_user = (LinearLayout) findViewById(R.id.ll_user);
        ll_site_visible = (LinearLayout) findViewById(R.id.ll_site_visible);
        ll_str_date_time = (LinearLayout) findViewById(R.id.ll_str_date_time);
        ll_end_date_time = (LinearLayout) findViewById(R.id.ll_end_date_time);
        ll_bttry_dschrg_date = (LinearLayout) findViewById(R.id.ll_bttry_dschrg_date);
        ll_eta = (LinearLayout) findViewById(R.id.ll_eta);
        ll_etr = (LinearLayout) findViewById(R.id.ll_etr);
        ll_rca_category = (LinearLayout) findViewById(R.id.ll_rca_category);
        ll_rca_sub_category = (LinearLayout) findViewById(R.id.ll_rca_sub_category);
        ll_rca = (LinearLayout) findViewById(R.id.ll_rca);
        ll_operator = (LinearLayout) findViewById(R.id.ll_operator);
        ll_operator_exempted = (LinearLayout) findViewById(R.id.ll_operator_exempted);
        ll_operator_exempted = (LinearLayout) findViewById(R.id.ll_operator_exempted);
        ll_effected_sites = (LinearLayout) findViewById(R.id.ll_effected_sites);
        ll_dg_reading = (LinearLayout) findViewById(R.id.ll_dg_reading);
        ll_grid_reading = (LinearLayout) findViewById(R.id.ll_grid_reading);
        ll_fuel_level = (LinearLayout) findViewById(R.id.ll_fuel_level);
        ll_action_taken = (LinearLayout) findViewById(R.id.ll_action_taken);
        tv_dg_reading = (TextView) findViewById(R.id.tv_dg_reading);
        tv_grid_reading = (TextView) findViewById(R.id.tv_grid_reading);
        tv_fuel_level = (TextView) findViewById(R.id.tv_fuel_level);
        tv_action_taken = (TextView) findViewById(R.id.tv_action_taken);
        tv_ticket_treatment = (TextView) findViewById(R.id.tv_ticket_treatment);
        sp_action_taken = (Spinner) findViewById(R.id.sp_action_taken);
        et_grid_reading = (EditText) findViewById(R.id.et_grid_reading);
        et_fuel_level = (EditText) findViewById(R.id.et_fuel_level);
        et_dg_reading = (EditText) findViewById(R.id.et_dg_reading);
        sp_action_taken.setBackgroundResource(R.drawable.input_box);
        et_grid_reading.setBackgroundResource(R.drawable.input_box);
        et_fuel_level.setBackgroundResource(R.drawable.input_box);
        et_dg_reading.setBackgroundResource(R.drawable.input_box);
        ll_remarks = (LinearLayout) findViewById(R.id.ll_remarks);
        tv_grp_user = (TextView) findViewById(R.id.tv_grp_user);
        tv_grp_user.setBackgroundResource(R.drawable.input_box);
        sp_status = (Spinner) findViewById(R.id.sp_status);
        sp_status.setBackgroundResource(R.drawable.input_box);
        sp_group = (SearchableSpinner) findViewById(R.id.sp_group);
        sp_group.setBackgroundResource(R.drawable.doted);
        sp_site_visibility = (Spinner) findViewById(R.id.sp_site_visibility);
        sp_site_visibility.setBackgroundResource(R.drawable.input_box);
        txt_problem_start_date = (TextView) findViewById(R.id.txt_problem_start_date);
        txt_problem_start_date.setVisibility(View.VISIBLE);
        txt_problem_start_date.setTypeface(Utils.typeFace(UpdateTicket.this));

        tv_serviceImpactStart = (TextView) findViewById(R.id.tv_serviceImpactStart);
        tv_serviceImpactStart.setVisibility(View.VISIBLE);
        tv_serviceImpactStart.setTypeface(Utils.typeFace(UpdateTicket.this));

        tv_serviceImpactStartTime = (TextView) findViewById(R.id.tv_serviceImpactStartTime);
        tv_serviceImpactStartTime.setVisibility(View.VISIBLE);
        tv_serviceImpactStartTime.setTypeface(Utils.typeFace(UpdateTicket.this));
        //110
        ll_ticket_log_date = findViewById(R.id.ll_ticket_log_date);
        txt_ticket_log_date = findViewById(R.id.txt_ticket_log_date);
        txt_ticket_log_date.setVisibility(View.VISIBLE);
        txt_ticket_log_date.setTypeface(Utils.typeFace(UpdateTicket.this));
        txt_ticket_log_time = findViewById(R.id.txt_ticket_log_time);
        txt_ticket_log_time.setVisibility(View.VISIBLE);
        txt_ticket_log_time.setTypeface(Utils.typeFace(UpdateTicket.this));
        //110
        txt_problem_start_time = (TextView) findViewById(R.id.txt_problem_start_time);
        txt_problem_start_time.setVisibility(View.VISIBLE);
        txt_problem_start_time.setTypeface(Utils.typeFace(UpdateTicket.this));
        txt_problem_end_date = (TextView) findViewById(R.id.txt_problem_end_date);
        txt_problem_end_date.setVisibility(View.VISIBLE);
        txt_problem_end_date.setTypeface(Utils.typeFace(UpdateTicket.this));
        txt_problem_end_time = (TextView) findViewById(R.id.txt_problem_end_time);
        txt_problem_end_time.setVisibility(View.VISIBLE);
        txt_problem_end_time.setTypeface(Utils.typeFace(UpdateTicket.this));
        tv_dis_bttry_Date = (TextView) findViewById(R.id.tv_dis_bttry_Date);
        tv_dis_bttry_Date.setVisibility(View.VISIBLE);
        tv_dis_bttry_Date.setTypeface(Utils.typeFace(UpdateTicket.this));
        tv_dis_bttry_time = (TextView) findViewById(R.id.tv_dis_bttry_time);
        tv_dis_bttry_time.setVisibility(View.VISIBLE);
        tv_dis_bttry_time.setTypeface(Utils.typeFace(UpdateTicket.this));
        et_eta = (EditText) findViewById(R.id.et_eta);
        et_eta.setBackgroundResource(R.drawable.input_box);
        et_servicesAffected = (EditText) findViewById(R.id.et_servicesAffected);
        et_servicesAffected.setBackgroundResource(R.drawable.input_box);

        et_etr = (EditText) findViewById(R.id.et_etr);
        et_etr.setBackgroundResource(R.drawable.input_box);
        sp_rca_category = (SearchableSpinner) findViewById(R.id.sp_rca_category);
        sp_rca_category.setBackgroundResource(R.drawable.doted);
        sp_rca_category_sub = (SearchableSpinner) findViewById(R.id.sp_rca_category_sub);
        sp_rca_category_sub.setBackgroundResource(R.drawable.doted);
        et_rca = (EditText) findViewById(R.id.et_rca);
        et_rca.setBackgroundResource(R.drawable.input_box);
        sp_operator = (TextView) findViewById(R.id.sp_operator);
        sp_operator.setBackgroundResource(R.drawable.input_box);
        sp_operator_exempted = (TextView) findViewById(R.id.sp_operator_exempted);
        sp_operator_exempted.setBackgroundResource(R.drawable.input_box);
        et_dependent_site = (EditText) findViewById(R.id.et_dependent_site);
        et_dependent_site.setBackgroundResource(R.drawable.input_box);
        et_remarks = (EditText) findViewById(R.id.et_remarks);
        et_remarks.setBackgroundResource(R.drawable.input_box);
        cb_add_ref_ticket = (CheckBox) findViewById(R.id.cb_add_ref_ticket);
        btn_update_tt = (TextView) findViewById(R.id.btn_update_tt);
        bt_back = (Button) findViewById(R.id.bt_back);
        Utils.msgButton(UpdateTicket.this, "71", bt_back);
        ll_trvlDistnc = (LinearLayout) findViewById(R.id.ll_trvlDistnc);
        ll_noOfTech = (LinearLayout) findViewById(R.id.ll_noOfTech);
        ll_wrkgNights = (LinearLayout) findViewById(R.id.ll_wrkgNights);
        ll_hub_SiteId = (LinearLayout) findViewById(R.id.ll_hub_siteId);
        et_trvlDistnc = (EditText) findViewById(R.id.et_trvlDistnc);
        et_noOfTech = (EditText) findViewById(R.id.et_noOfTech);
        et_wrkgNights = (EditText) findViewById(R.id.et_wrkgNights);
        et_hubSiteId = (EditText) findViewById(R.id.et_hubSiteId);
        et_hubSiteId.setBackgroundResource(R.drawable.input_box);

        ll_status_reason = (LinearLayout) findViewById(R.id.ll_status_reason);
        sp_status_reason = (SearchableSpinner) findViewById(R.id.sp_status_reason);
        sp_status_reason.setBackgroundResource(R.drawable.doted);
        tv_status_reason = (TextView) findViewById(R.id.tv_status_reason);

        ll_serviceImapact_add = (LinearLayout) findViewById(R.id.ll_serviceImapact_add);
        sp_serviceImapact = (Spinner) findViewById(R.id.sp_serviceImapact);
        sp_serviceImapact.setBackgroundResource(R.drawable.doted);
        tv_serviceImapact = (TextView) findViewById(R.id.tv_serviceImapact);


        ll_resolution_method = (LinearLayout) findViewById(R.id.ll_resolution_method);
        sp_resolution_method = (SearchableSpinner) findViewById(R.id.sp_resolution_method);
        sp_resolution_method.setBackgroundResource(R.drawable.doted);
        tv_resolution_method = (TextView) findViewById(R.id.tv_resolution_method);

        ll_fault_area = (LinearLayout) findViewById(R.id.ll_fault_area);
        ll_fault_area_details = (LinearLayout) findViewById(R.id.ll_fault_area_details);

        sp_fault_area = (SearchableSpinner) findViewById(R.id.sp_fault_area);
        sp_fault_area_details = (SearchableSpinner) findViewById(R.id.sp_fault_area_details);
        sp_fault_area_details.setBackgroundResource(R.drawable.doted);
        sp_fault_area.setBackgroundResource(R.drawable.doted);
        tv_status_approve = (TextView) findViewById(R.id.tv_status_approve);

        ll_status_approve = (LinearLayout) findViewById(R.id.ll_status_approve);
        sp_status_approve = (SearchableSpinner) findViewById(R.id.sp_status_approve);
        sp_status_approve.setBackgroundResource(R.drawable.doted);
        ll_delay_reason = (LinearLayout) findViewById(R.id.ll_delay_reason);//110
        tv_delay_reason = (TextView) findViewById(R.id.tv_delay_reason);//110
        sp_delay_reason = (Spinner) findViewById(R.id.sp_delay_reason);//110


    }

    public void init() {
        imgInfoArray = new JSONArray();
        jsonArrStrImg = new JSONArray();

        sp_group.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String groupId = db.getGroupId(sp_group.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                All_user_Id.setLength(0);
                userList.clear();
                userCounter = 0;
                tv_grp_user.setText("Select User");
                list_user_id = new ArrayList<String>();
                list_userGRP = new ArrayList<String>();
                list_user_id.clear();
                list_userGRP.clear();
                if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                    GetAssignUserTask task = new GetAssignUserTask(UpdateTicket.this, Site_Id, groupId);
                    task.execute();
                } else {
                    list_user_id.clear();
                    list_userGRP.clear();
                    Cursor c = db.getGrpUser(groupId, mAppPreferences.getTTModuleSelection());
                    if (c != null) {
                        while (c.moveToNext()) {
                            list_user_id.add(c.getString(c.getColumnIndex("U_ID")));
                            list_userGRP.add(c.getString(c.getColumnIndex("U_DETAIL")));
                        }
                    }
                    defaultUser();
                }
                //defaultUser();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        et_trvlDistnc.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        et_trvlDistnc.setText(trvlDistnc);
        et_noOfTech.setText(noOfTech);
        et_wrkgNights.setText(wrkgNights);
        pDescription = db.getPDesc("4", alarm_descId, mAppPreferences.getTTModuleSelection());
        pShortName = db.getParmName("4", alarm_descId, mAppPreferences.getTTModuleSelection());

        if (pDescription.contains("trvlDistnc") && db.getUpdateTTField("Distance Travelled", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_trvlDistnc.setVisibility(View.VISIBLE);
        } else {
            ll_trvlDistnc.setVisibility(View.GONE);
        }

        if (pDescription.contains("noOfTech") && db.getUpdateTTField("No. Of Technician", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_noOfTech.setVisibility(View.VISIBLE);
        } else {
            ll_noOfTech.setVisibility(View.GONE);
        }

        if (pDescription.contains("wrkgNights") && db.getUpdateTTField("Working Nights", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_wrkgNights.setVisibility(View.VISIBLE);
        } else {
            ll_wrkgNights.setVisibility(View.GONE);
        }

    }

    public void previousBindValue() {
        addItemsOnSpinner(sp_delay_reason,list_delay_reason);//110
        int poa26 = getCategoryPos(getIntent().getExtras().getString("delay_reason"),list_delay_reason);
        sp_delay_reason.setSelection(poa26);
        //110
        addItemsOnSpinner(sp_confirmation, list_confirmation);
        int poa25 = getCategoryPos(isUserSatisfied, list_confirmation);
        sp_confirmation.setSelection(poa25);

        addItemsOnSpinner(sp_ticket_treatment, list_tt_treatement);
        int poa24 = getCategoryPos(getIntent().getExtras().getString("tktTretmnt"), list_tt_treatement);
        sp_ticket_treatment.setSelection(poa24);

        addItemsOnSpinner(sp_reject_category, list_rejection_category);
        //int poa14 = getCategoryPos(getIntent().getExtras().getString("firstLevel"),list_approval1);
        //sp_approval1.setSelection(poa14);

        addItemsOnSpinner(sp_approval1, list_approval1);
        int poa14 = getCategoryPos(getIntent().getExtras().getString("firstLevel"), list_approval1);
        sp_approval1.setSelection(poa14);

        addItemsOnSpinner(sp_approval2, list_approval2);
        int poa15 = getCategoryPos(getIntent().getExtras().getString("secondLevel"), list_approval2);
        sp_approval2.setSelection(poa15);


        addItemsOnSpinner(sp_action_taken, list_action);
        int poa12 = getCategoryPos(getIntent().getExtras().getString("actionTaken"), list_action);
        sp_action_taken.setSelection(poa12);

        addItemsOnSpinner(sp_site_visibility, siteStatusList);
        int poa11 = getCategoryPos(site_status, siteStatusList);
        sp_site_visibility.setSelection(poa11);

        addItemsOnSpinner(sp_status, list_status);
        int status_pos = getCategoryPos(status, list_status);
        sp_status.setSelection(status_pos);

		/*addItemsOnSpinner(sp_group, list_group);
		int group_pos = getCategoryPos(assingTo, list_group);
		sp_group.setSelection(group_pos);*/

        addItemsOnSpinner(sp_rca_category, rca_category_name);
        int pos1 = getCategoryPos(category_name, rca_category_name);
        sp_rca_category.setSelection(pos1);

        addItemsOnSpinner(sp_status_reason, list_status_resion);
        int status_res_pos = getCategoryPos(staResion, list_status_resion);
        sp_status_reason.setSelection(status_res_pos);

        addItemsOnSpinner(sp_serviceImapact, list_service_impact);
        int serviceImpact = getCategoryPos(serviceImacted, list_service_impact);
        sp_serviceImapact.setSelection(serviceImpact);

        addItemsOnSpinner(sp_resolution_method, list_method_resolation);
        int status_rsoulation = getCategoryPos(resMethod, list_method_resolation);
        sp_resolution_method.setSelection(status_rsoulation);

        addItemsOnSpinner(sp_status_approve, list_status_aaprove);
        int status_rso = getCategoryPos(resoulationMethod, list_status_aaprove);
        sp_status_approve.setSelection(status_rso);

        addItemsOnSpinner(sp_fault_area, list_faoult_area);
        int faoult_Area = getCategoryPos(fouArea, list_faoult_area);
        sp_fault_area.setSelection(faoult_Area);

        addItemsOnSpinner(sp_fault_area_details, list_fault_area_details);
        int faolt_area_deatils = getCategoryPos(fouAreaDetails, list_fault_area_details);
        sp_fault_area_details.setSelection(faolt_area_deatils);

        txt_problem_start_date.setText(PROBLEM_START_DATE);
        txt_problem_start_time.setText(PROBLEM_START_TIME);
        txt_problem_end_date.setText(PROBLEM_END_DATE);
        txt_problem_end_time.setText(PROBLEM_END_TIME);
        tv_serviceImpactStart.setText(serviceImpactStart);
        tv_serviceImpactStartTime.setText(serviceImpactStartTime);
        tv_dis_bttry_Date.setText(batt_disc_date);
        tv_dis_bttry_time.setText(batt_disc_time);
        sp_priority.setText(priorityName);
        txt_ticket_log_date.setText(alarm_in_time[0]);//110
        txt_ticket_log_time.setText(alarm_in_time[1]);//110
		/*if(eta!=null && eta.length()>0){
			et_eta.setEnabled( false );
			et_eta.setTextColor( Color.parseColor( "#A4A0A0" ));
			et_eta.setBackgroundResource( R.drawable.et_data);
		}else{
			et_eta.setEnabled( true );
		}*/
        et_eta.setText(eta);
        et_servicesAffected.setText(serviceAffecetd);
        et_etr.setText(etr);
        et_rca.setText(rca);
        //1.5
        if(getIntent().getExtras().getString("assetId")!=null){
            if (getIntent().getExtras().getString("assetId")
                    .equalsIgnoreCase("null")){
                et_asset.setText("");
            }else{
                et_asset.setText(getIntent().getExtras().getString("assetId"));
            }
        }else{
            et_asset.setText("");
        }
        et_dependent_site.setText(Effective_Site);
        et_dg_reading.setText(getIntent().getExtras().getString("dgReading"));
        et_grid_reading.setText(getIntent().getExtras().getString("gridReading"));

        et_hubSiteId.setText(getIntent().getExtras().getString("hubSiteId"));

        et_fuel_level.setText(getIntent().getExtras().getString("fuelLevel"));
        exemptOperatorList = new ArrayList<Operator>();
        OperatorExempt = getIntent().getExtras().getString("OperatorExempt");
        asgnToUid = getIntent().getExtras().getString("asgnToUid");
        operatorList = new ArrayList<Operator>();
        userList = new ArrayList<Operator>();
        Operator = getIntent().getExtras().getString("Operator");
        if (Operator != null && !Operator.isEmpty()) {
            words = Operator.split(",");
            boolean isSelected = false;
            boolean isExempSelected = false;
            sp_operator.setText("" + words.length + " " + "Operator Selected");
            for (int i = 0; i < list_operator_id.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    if (Operator.contains(list_operator_id.get(i))) {
                        isSelected = true;
                        if (OperatorExempt != null && !OperatorExempt.isEmpty()) {
                            words1 = OperatorExempt.split(",");
                            isExempSelected = true;
                            sp_operator_exempted.setText("" + words1.length
                                    + " " + "Operator Exempted Selected");
                            if (OperatorExempt
                                    .contains(list_operator_id.get(i))) {
                                isExempSelected = true;
                            } else {
                                isExempSelected = false;
                            }
                        }
                        Operator operExemp = new Operator(
                                list_operator_id.get(i),
                                list_operator_name.get(i), isExempSelected);
                        exemptOperatorList.add(operExemp);
                    } else {
                        isSelected = false;
                    }
                    Operator operator = new Operator(list_operator_id.get(i),
                            list_operator_name.get(i), isSelected);
                    operatorList.add(operator);
                }
            }
        } else {
            for (int i = 0; i < list_operator_name.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    Operator country = new Operator(list_operator_id.get(i),
                            list_operator_name.get(i), false);
                    operatorList.add(country);
                }
            }
        }
    }

    // start 0.1
    public void showHideValue() {
        hide_tt_status = db.getUpdateTTField("Ticket Status", mAppPreferences.getTTModuleSelection());// Ticket Status
        hide_group = db.getUpdateTTField("Assigned To", mAppPreferences.getTTModuleSelection());// Assigned To
        hide_site_visibility = db.getUpdateTTField("Site Visibility", mAppPreferences.getTTModuleSelection());// Site Visibility
        hide_problem_start_date_time = db.getUpdateTTField("Problem Start Date Time", mAppPreferences.getTTModuleSelection());// Problem Start
        hide_disc_bat_date_time = db.getUpdateTTField("Battery Discharge Start Date Time", mAppPreferences.getTTModuleSelection());// Battery Discharge
        hide_problem_end_date_time = db.getUpdateTTField("Problem End Date Time", mAppPreferences.getTTModuleSelection());// Problem End
        hide_eta = db.getUpdateTTField("ETA", mAppPreferences.getTTModuleSelection());// ETA
        hide_etr = db.getUpdateTTField("ETR", mAppPreferences.getTTModuleSelection()); // ETR
        hide_dg = db.getUpdateTTField("DG Meter Reading", mAppPreferences.getTTModuleSelection()); // dg reading
        hide_grid = db.getUpdateTTField("Grid Meter Reading", mAppPreferences.getTTModuleSelection()); // grid reading
        hide_fuel = db.getUpdateTTField("Fuel Level", mAppPreferences.getTTModuleSelection()); // fuel level
        hide_action = db.getUpdateTTField("Action Taken", mAppPreferences.getTTModuleSelection()); // action taken
        hide_tt_treatement = db.getUpdateTTField("Ticket_treatment", mAppPreferences.getTTModuleSelection()); // ticket treatment
        hide_hub_siteId = db.getUpdateTTField("Hub Site Id", mAppPreferences.getTTModuleSelection()); // Hub Site Id      1.2
        if (hide_dg != null && hide_dg.length() > 0 && (hide_dg.equalsIgnoreCase("Y")
                || hide_dg.equalsIgnoreCase("M"))) {
            ll_dg_reading.setVisibility(View.VISIBLE);
        } else {
            ll_dg_reading.setVisibility(View.GONE);
        }

        if (hide_grid != null && hide_grid.length() > 0 && (hide_grid.equalsIgnoreCase("Y")
                || hide_grid.equalsIgnoreCase("M"))) {
            ll_grid_reading.setVisibility(View.VISIBLE);
        } else {
            ll_grid_reading.setVisibility(View.GONE);
        }

        if (hide_fuel != null && hide_fuel.length() > 0 && (hide_fuel.equalsIgnoreCase("Y")
                || hide_fuel.equalsIgnoreCase("M"))) {
            ll_fuel_level.setVisibility(View.VISIBLE);
        } else {
            ll_fuel_level.setVisibility(View.GONE);
        }

        if (hide_action != null && hide_action.length() > 0 && (hide_action.equalsIgnoreCase("Y")
                || hide_action.equalsIgnoreCase("M"))) {
            ll_action_taken.setVisibility(View.VISIBLE);
        } else {
            ll_action_taken.setVisibility(View.GONE);
        }
        if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
            ll_status_approve.setVisibility(View.VISIBLE);
        } else {
            ll_status_approve.setVisibility(View.GONE);
        }

        if (getIntent().getExtras().getBoolean("enableRca") == true) {//resolution link ticket rca functionality disable
            hide_rca_category = "N";
            hide_rca_sub_category = "N";
            hide_rca = "N";
        } else {
            hide_rca_category = db.getUpdateTTField("RCA Category", mAppPreferences.getTTModuleSelection());// RCA Category
            hide_rca_sub_category = db.getUpdateTTField("RCA Sub Category", mAppPreferences.getTTModuleSelection());// RCA Sub Category
            hide_rca = db.getUpdateTTField("RCA", mAppPreferences.getTTModuleSelection()); // RCA
        }
        hide_operator = db.getUpdateTTField("Operator", mAppPreferences.getTTModuleSelection());// Operator
        hide_exempted_oper = db.getUpdateTTField("Exempted Operator", mAppPreferences.getTTModuleSelection());//
        hide_effective_site = db.getUpdateTTField("Effected Sites", mAppPreferences.getTTModuleSelection());// Effected Sites
        hide_remarks = db.getUpdateTTField("Remarks", mAppPreferences.getTTModuleSelection());// Remarks
        spareVisable = db.getUpdateTTField("Spare Parts", mAppPreferences.getTTModuleSelection());// spare parts
        if (spareVisable.equalsIgnoreCase("Y")) {
            tv_spare_parts.setVisibility(View.VISIBLE);
        } else {
            tv_spare_parts.setVisibility(View.GONE);
        }




        if (hide_site_visibility.equalsIgnoreCase("Y")) {
            ll_site_visible.setVisibility(View.VISIBLE);
        } else {
            ll_site_visible.setVisibility(View.GONE);
        }

        if (hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            ll_str_date_time.setVisibility(View.VISIBLE);
        } else {
            ll_str_date_time.setVisibility(View.GONE);
        }

        if (hide_disc_bat_date_time.equalsIgnoreCase("Y")) {
            ll_bttry_dschrg_date.setVisibility(View.VISIBLE);
        } else {
            ll_bttry_dschrg_date.setVisibility(View.GONE);
        }

        if (hide_eta.equalsIgnoreCase("Y")) {
            ll_eta.setVisibility(View.VISIBLE);
        } else {
            ll_eta.setVisibility(View.GONE);
        }

        if (hide_etr.equalsIgnoreCase("Y")) {
            ll_etr.setVisibility(View.VISIBLE);
        } else {
            ll_etr.setVisibility(View.GONE);
        }

        if (hide_operator.equalsIgnoreCase("Y")) {
            ll_operator.setVisibility(View.VISIBLE);
        } else {
            ll_operator.setVisibility(View.GONE);
        }

        if (hide_exempted_oper.equalsIgnoreCase("Y")) {
            ll_operator_exempted.setVisibility(View.VISIBLE);
        } else {
            ll_operator_exempted.setVisibility(View.GONE);
        }

        if (hide_effective_site.equalsIgnoreCase("Y")) {
            ll_effected_sites.setVisibility(View.VISIBLE);
        } else {
            ll_effected_sites.setVisibility(View.GONE);
        }

        if (hide_remarks.equalsIgnoreCase("Y")) {
            ll_remarks.setVisibility(View.VISIBLE);
        } else {
            ll_remarks.setVisibility(View.GONE);
        }

        if (db.getUpdateTTField("Checklist Link",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            tv_chk_link.setVisibility(View.GONE);
        }

        if (db.getUpdateTTField("First Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            ll_approval1.setVisibility(View.GONE);
        }

        if (db.getUpdateTTField("Second Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            ll_approval2.setVisibility(View.GONE);
        }


        if (hide_remarks.equalsIgnoreCase("Y")) {
            ll_remarks.setVisibility(View.VISIBLE);
        } else {
            ll_remarks.setVisibility(View.GONE);
        }

        if (hide_tt_treatement.equalsIgnoreCase("Y")) {
            ll_ticket_treatment.setVisibility(View.VISIBLE);
            if (tt_flag.equalsIgnoreCase("0")) {
                sp_ticket_treatment.setEnabled(false);
            } else {
                sp_ticket_treatment.setEnabled(true);
            }
        } else {
            ll_ticket_treatment.setVisibility(View.GONE);
        }


        if (hide_hub_siteId.equalsIgnoreCase("Y")) {    //1.2
            ll_hub_SiteId.setVisibility(View.VISIBLE);
        } else {
            ll_hub_SiteId.setVisibility(View.GONE);
        }
        if (ticket_typeId.equalsIgnoreCase(ticketType)) {
            et_servicesAffected.setEnabled(false);
            tv_serviceImpactStart.setEnabled(false);
            sp_serviceImapact.setEnabled(false);
            sp_operator.setEnabled(false);
            sp_operator_exempted.setEnabled(false);
        } else {
            et_servicesAffected.setEnabled(true);
            tv_serviceImpactStart.setEnabled(true);
            sp_serviceImapact.setEnabled(true);
            sp_operator.setEnabled(true);
            sp_operator_exempted.setEnabled(true);
        }
         //110
       if(ticket_typeId.equalsIgnoreCase("23"))
       {
           usergrpId =  mAppPreferences.getUserGroup();
           ll_delay_reason.setVisibility(View.VISIBLE);
//           if(checkTocUser())
//           {
//               sp_delay_reason.setEnabled(true);
//           }
//           else
//           {
//               sp_delay_reason.setEnabled(false);
//           }
       }
       else
       {
           ll_delay_reason.setVisibility(View.GONE);
       }

       if(!status.equalsIgnoreCase("Closed") && toc_fo_user.equalsIgnoreCase("Yes") && ticket_typeId.equalsIgnoreCase("23"))
       {
           ll_ticket_log_date.setVisibility(View.VISIBLE);
       }
       else
       {
           ll_ticket_log_date.setVisibility(View.GONE);
       }

       //110



		/*if(mAppPreferences.getUserCategory().equalsIgnoreCase("2")  && mAppPreferences.getOperatorWiseGroup().equalsIgnoreCase("Y")){  //1.0

			ll_assigned.setVisibility(View.GONE);

		}else{
			ll_assigned.setVisibility(View.VISIBLE);
		}*/

    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    private int getCategoryPos(String category, ArrayList<String> list) {
        return list.indexOf(category);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, pDateSetListener, pYear, pMonth,
                        pDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        true);
            case DATE_DIALOG_ID1:
                return new DatePickerDialog(this, pDateSetListener1, pYear, pMonth,
                        pDay);
            case TIME_DIALOG_ID1:
                return new TimePickerDialog(this, timePickerListener1, hour,
                        minute, true);
            case DATE_DIALOG_ID2:
                return new DatePickerDialog(this, pDateSetListener2, pYear, pMonth,
                        pDay);
            case TIME_DIALOG_ID2:
                return new TimePickerDialog(this, timePickerListener2, hour,
                        minute, true);
            case  DATE_DIALOG_ID4:
                return new DatePickerDialog(this,pDateSetListener4,pYear,
                        pMonth, pDay);

            case TIME_DIALOG_ID5:
                return new TimePickerDialog(this, timePickerListener5, hour, minute,
                        true);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            txt_problem_start_time.setText(Utils.updateTime(hour, minute));
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener5 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            txt_ticket_log_time.setText(Utils.updateTime(hour, minute));
        }
    };
    private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                txt_problem_start_date.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(UpdateTicket.this,"You cannot select problem start date time greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(UpdateTicket.this, "133");
            }
        }
    };

    private DatePickerDialog.OnDateSetListener serDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                tv_serviceImpactStart.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(UpdateTicket.this,"You cannot select problem start date time greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(UpdateTicket.this, "133");
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            // updateTime(hour, minute, txt_problem_end_time);
            txt_problem_end_time.setText(Utils.updateTime(hour, minute));
        }
    };
    private DatePickerDialog.OnDateSetListener pDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                txt_problem_end_date.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(UpdateTicket.this,"You cannot select problem end date time greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(UpdateTicket.this, "134");
            }
        }
    };
    // start 0.1
    private TimePickerDialog.OnTimeSetListener timePickerListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            // updateTime(hour, minute, tv_dis_bttry_time);
            tv_dis_bttry_time.setText(Utils.updateTime(hour, minute));
        }
    };
    private DatePickerDialog.OnDateSetListener pDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                tv_dis_bttry_Date.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                Utils.toast(UpdateTicket.this, "135");
            }
        }
    };

    private DatePickerDialog.OnDateSetListener pDateSetListener4 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                txt_ticket_log_date.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                Toast.makeText(UpdateTicket.this,"You cannot select Ticket log date greater than system date",Toast.LENGTH_SHORT).show();
            }
        }
    };

    // end 0.1
    public void OperatorpopUp() {
        tempOptExemptList = new ArrayList<Operator>();
        dialogOperator = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        dialogOperator.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOperator.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogOperator.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogOperator.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogOperator.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogOperator.show();
        ImageView iv_cancel = (ImageView) dialogOperator.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialogOperator.cancel();
            }
        });
        final ListView list_view = (ListView) dialogOperator.findViewById(R.id.list_view);
        TextView apply = (TextView) dialogOperator.findViewById(R.id.tv_apply);
        apply.setTypeface(Utils.typeFace(UpdateTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogOperator.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(UpdateTicket.this));
        tv_header.setText("Operator");
        dataAdapter = new MyCustomAdapter(UpdateTicket.this, R.layout.custom_operator, operatorList, 1);
        list_view.setAdapter(dataAdapter);
    }

    public void operatorSelect() {
        counter = 0;
        All_Operator_Id.setLength(0);
        tempOptExemptList.clear(); // clear swap list
        if (exemptOperatorList.size() > 0) { // swap exempted operator
            // list before clear
            for (int a = 0; a < exemptOperatorList.size(); a++) {
                tempOptExemptList.add(exemptOperatorList.get(a));
            }
        }
        exemptOperatorList.clear(); // clear exempt operator list
        flagA = 1;
        flagB = 1;
        ArrayList<Operator> OperatorList = dataAdapter.countryList;
        for (int i = 0; i < OperatorList.size(); i++) {
            Operator operator = OperatorList.get(i);
            if (operator.isSelected()) {
                check = false; // default set false
                if (tempOptExemptList.size() > 0) { // check previous
                    for (int a = 0; a < tempOptExemptList.size(); a++) {
                        if (tempOptExemptList.get(a).getCode() == operator
                                .getCode()
                                && tempOptExemptList.get(a)
                                .isSelected()) {
                            check = true;
                        }
                    }
                }
                Operator exempt_oper = new Operator(operator.getCode(),
                        operator.getName(), check); // final exempted
                exemptOperatorList.add(exempt_oper);
                counter++;
                if (counter > 1) {
                    All_Operator_Id.append(",");
                }
                All_Operator_Id.append(operator.getCode());
            }
        }
        if (counter == 0) {
            sp_operator.setText("Select Operator");
        } else {
            sp_operator.setText("" + counter + " Operator Selected");
        }
        //dialogOperator.dismiss();
        OperatorExemptPopUp(0);
    }

    public void OperatorExemptPopUp(int mode) {
        //exemptOptCounter = 0;
        //all_exempt_opt_id.setLength(0);
        dialogOperatorExempt = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        dialogOperatorExempt.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOperatorExempt.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogOperatorExempt.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogOperatorExempt.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogOperatorExempt.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogOperatorExempt.show();

        ImageView iv_cancel = (ImageView) dialogOperatorExempt.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialogOperatorExempt.cancel();
            }
        });

        final ListView list_view_exempt = (ListView) dialogOperatorExempt.findViewById(R.id.list_view);
        TextView apply = (TextView) dialogOperatorExempt.findViewById(R.id.tv_apply);
        apply.setTypeface(Utils.typeFace(UpdateTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogOperatorExempt.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(UpdateTicket.this));
        tv_header.setText("Operator Exempted");
        dataAdapterExempt = new MyCustomAdapter(UpdateTicket.this, R.layout.custom_operator, exemptOperatorList, 2);
        list_view_exempt.setAdapter(dataAdapterExempt);

        if (mode == 0) {
            exemptOptCounter = 0;
            all_exempt_opt_id.setLength(0);
            ArrayList<Operator> OptList = dataAdapterExempt.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    exemptOptCounter++;
                    if (exemptOptCounter > 1) {
                        all_exempt_opt_id.append(",");
                    }
                    all_exempt_opt_id.append(opt.getCode());
                }
            }
            if (exemptOptCounter == 0) {
                sp_operator_exempted.setText("Select Operator Exempted");
            } else {
                sp_operator_exempted.setText("" + exemptOptCounter
                        + " Operator Exempted Selected");
            }
            dialogOperatorExempt.dismiss();
        }

		/*apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				flagB = 1;
				ArrayList<Operator> OptList = dataAdapterExempt.countryList;
				for (int i = 0; i < OptList.size(); i++) {
					Operator opt = OptList.get(i);
					if (opt.isSelected()) {
						exemptOptCounter++;
						if (exemptOptCounter > 1) {
							all_exempt_opt_id.append(",");
						}
						all_exempt_opt_id.append(opt.getCode());
					}
				}
				if (exemptOptCounter == 0) {
					sp_operator_exempted.setText("Select Operator Exempted");
				} else {
					sp_operator_exempted.setText("" + exemptOptCounter
							+ " Operator Exempted Selected");
				}
				dialogOperatorExempt.dismiss();
			}
		});*/
    }

    public void operatorExemptedSelect() {
        exemptOptCounter = 0;
        all_exempt_opt_id.setLength(0);
        flagB = 1;
        ArrayList<Operator> OptList = dataAdapterExempt.countryList;
        for (int i = 0; i < OptList.size(); i++) {
            Operator opt = OptList.get(i);
            if (opt.isSelected()) {
                exemptOptCounter++;
                if (exemptOptCounter > 1) {
                    all_exempt_opt_id.append(",");
                }
                all_exempt_opt_id.append(opt.getCode());
            }
        }
        if (exemptOptCounter == 0) {
            sp_operator_exempted.setText("Select Operator Exempted");
        } else {
            sp_operator_exempted.setText("" + exemptOptCounter
                    + " Operator Exempted Selected");
        }
        //dialogOperatorExempt.dismiss();
    }

    private class MyCustomAdapter extends ArrayAdapter<Operator> {
        private ArrayList<Operator> countryList;
        int popupFlag = 0;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Operator> countryList, int popupFlag) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Operator>();
            this.countryList.addAll(countryList);
            this.popupFlag = popupFlag;
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
                        if (popupFlag == 1) {
                            operatorSelect();
                        } else if (popupFlag == 2) {
                            operatorExemptedSelect();
                        } else if (popupFlag == 3) {
                            userSelected();
                        }
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Operator country = countryList.get(position);
            holder.code.setText("");
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            if (ttCreaterUserCat.equalsIgnoreCase("2")
                    && popupFlag == 1) {
                holder.name.setEnabled(false);
            }
            holder.name.setTag(country);
            return convertView;
        }
    }

    public class UpdateTicketTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        ResponseUpdateTicket respo_update;
        Context con;
        String remarks;
        String Str_ETA;
        String Str_ETR;
        String Str_RCA;
        String Str_RCA_Cate;
        String Str_RCA_Sub_Cate;
        String Str_Problem_Start_DateTime;
        String Str_Problem_End_DateTime;
        String siteAvailable = "1";
        String affetctedSites = null;

        public UpdateTicketTask(Context con, String remarks,
                                String Str_ETA, String Str_ETR, String Str_RCA,
                                String Str_RCA_Cate, String Str_RCA_Sub_Cate, String p_s_date,
                                String p_e_date) {
            this.con = con;
            this.remarks = remarks;
            this.Str_ETA = Str_ETA;
            this.Str_ETR = Str_ETR;
            this.Str_RCA = Str_RCA;
            this.Str_RCA_Cate = Str_RCA_Cate;
            this.Str_RCA_Sub_Cate = Str_RCA_Sub_Cate;
            this.Str_Problem_Start_DateTime = p_s_date;
            this.Str_Problem_End_DateTime = p_e_date;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("dgReading", dgReading);
                obj.put("gridReading", gridReading);
                obj.put("fuelLevel", fuelLevel);
                obj.put("actionTaken", actionTaken);
                obj.put(AppConstants.UPLOAD_TYPE, "TT");
                obj.put(AppConstants.SITE_ID_ALIAS, Site_Id);
                obj.put(AppConstants.EQUIPMENT_ID, equipment_id);
                obj.put(AppConstants.SEVERITY_ID, severity_Id);
                obj.put(AppConstants.ALARM_DESCRIPTION_ID, alarm_descId);
                obj.put(AppConstants.TICKET_LOG_DATE, tkt_log_time);
                if (ticket_typeId == null || ticket_typeId.isEmpty()
                        || ticket_typeId.length() == 0) {
                    ticket_typeId = "-1";
                }
                obj.put(AppConstants.TICKET_TYPE_ID, ticket_typeId);

                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")
                        && ll_approval2.getVisibility() == View.VISIBLE
                        && sp_approval2.getSelectedItem().toString().equalsIgnoreCase("Reject")) {
                    obj.put(AppConstants.TICKET_STATUS_ID, "8");
                } else {
                    if (spstatusapprove.equalsIgnoreCase("Rejected")) {
                        obj.put(AppConstants.TICKET_STATUS_ID, "2011796");
                    } else {
                        obj.put(AppConstants.TICKET_STATUS_ID, db
                                .getInciParamId("62", sp_status.getSelectedItem()
                                        .toString().trim(), mAppPreferences.getTTModuleSelection()));
                    }
                }


                if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(),
                        mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")
                        || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(),
                        mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")) {
                    String grpId = getIntent().getExtras().getString("grpId");
                    obj.put(AppConstants.TT_ASSIGN_GROUP_ID, grpId);
                } else {
                    if (sp_group.getSelectedItem().toString().trim().equalsIgnoreCase
                            ("Select " + tv_group.getText().toString().trim())) {
                        obj.put(AppConstants.TT_ASSIGN_GROUP_ID, "0");
                    } else {
                        obj.put(AppConstants.TT_ASSIGN_GROUP_ID, db
                                .getGroupId(sp_group.getSelectedItem().toString().trim(),
                                        mAppPreferences.getTTModuleSelection()));
                    }
                }


				/*if(sp_group.getSelectedItem().toString().trim().equalsIgnoreCase
						("Select "+tv_group.getText().toString().trim())){
					obj.put( AppConstants.TT_ASSIGN_GROUP_ID, "0");
				}else{
					obj.put( AppConstants.TT_ASSIGN_GROUP_ID, db
							.getGroupId(sp_group.getSelectedItem().toString().trim(),
									mAppPreferences.getTTModuleSelection()));
				}*/

                if (All_user_Id.toString() != null
                        && !All_user_Id.toString().isEmpty()) {
                    assign_user = All_user_Id.toString();
                }

                if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(),
                        mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")
                        || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(),
                        mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")) {
                    assign_user = asgnToUid;
                }
                if (spstatusapprove.equalsIgnoreCase("Rejected")) {
                    assign_user = asgnToUid;
                }

                if (assign_user == null || assign_user.isEmpty()
                        || assign_user.length() == 0
                        || assign_user.equalsIgnoreCase("null")) {
                    assign_user = "";
                }

                String ticket_log_date_time = txt_ticket_log_date.getText().toString().trim()+" "+txt_ticket_log_time.getText().toString().trim();
                obj.put("ttLogDt",ticket_log_date_time);
                obj.put(AppConstants.TT_ASSIGN_USER_ID, assign_user);
                obj.put(AppConstants.ETA, Str_ETA);
                obj.put(AppConstants.ETR, Str_ETR);
                obj.put(AppConstants.RCA_CATE_ID, Str_RCA_Cate);
                obj.put(AppConstants.RCA, Str_RCA);
                obj.put(AppConstants.PROBLEM_START_DATE, Str_Problem_Start_DateTime);
                obj.put(AppConstants.PROBLEM_END_DATE, Str_Problem_End_DateTime);
                obj.put(AppConstants.U_ID, mAppPreferences.getUserId());
                obj.put(AppConstants.U_NAME, mAppPreferences.getLoginId());
                obj.put(AppConstants.servicesAffected, et_servicesAffected.getText().toString());
                obj.put(AppConstants.serviceImpactStart, tv_serviceImpactStart.getText().toString().trim()+ " " + tv_serviceImpactStartTime.getText().toString().trim());
                obj.put(AppConstants.REMARKS, remarks);
                obj.put(AppConstants.TICKET_ID, ticket_id);
                if (alarm_detail == null || alarm_detail.isEmpty()
                        || alarm_detail.length() == 0) {
                    alarm_detail = "";
                }
                obj.put(AppConstants.ALARM_DETAIL, alarm_detail);
                if (problem_description == null
                        || problem_description.isEmpty()
                        || problem_description.length() == 0) {
                    problem_description = "";
                }
                obj.put(AppConstants.PROBLEM_DESCRIPTION, problem_description);

                String operators = "";
                String exempted = "";
                String refTktId = "";
                if (et_dependent_site.getText().toString().length() == 0) {
                    affetctedSites = "1";
                } else {
                    affetctedSites = et_dependent_site.getText().toString()
                            .trim();
                }

                if (All_Operator_Id.toString() != null
                        && !All_Operator_Id.toString().isEmpty()) {
                    operators = All_Operator_Id.toString();
                }
                if (operators == null || operators.equalsIgnoreCase("null")
                        || operators.isEmpty() || operators.length() == 0) {
                    operators = "";
                }

                if (all_exempt_opt_id.toString() != null
                        && !all_exempt_opt_id.toString().isEmpty()) {
                    exempted = all_exempt_opt_id.toString();
                }
                if (exempted == null || exempted.equalsIgnoreCase("null")
                        || exempted.isEmpty() || exempted.length() == 0) {
                    exempted = "";
                }

                if (ll_site_visible.getVisibility() == View.VISIBLE) {
                    if (sp_site_visibility.getSelectedItem().toString()
                            .trim().equalsIgnoreCase("No")) {
                        siteAvailable = "2";
                    }
                } else {
                    siteAvailable = "0";
                }

                discharge_date_time = tv_dis_bttry_Date.getText().toString()
                        .trim()
                        + " " + tv_dis_bttry_time.getText().toString().trim();

                if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("5")
                        || db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")) {
                    submissionData = "";
                }

                String firstLevelApr = "";
                String secondLevelApr = "";
                String rejectCategory = "0";
                String rejectRemarks = "";
                //String s = getIntent().getExtras().getString("firstLevel").trim();
                //String s1 = db.getInciParamId("100", getIntent().getExtras().getString("firstLevel").trim());
                if (ll_approval1.getVisibility() == View.VISIBLE && !sp_approval1.getSelectedItem().toString().equalsIgnoreCase
                        ("Select " + tv_approval1.getText().toString())) {
                    firstLevelApr = db.getInciParamId("100", sp_approval1.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")
                        && getIntent().getExtras().getString("firstLevel").trim().length() > 0) {
                    firstLevelApr = db.getInciParamId("100", getIntent().getExtras().getString("firstLevel").trim(), mAppPreferences.getTTModuleSelection());
                }

                if (ll_approval2.getVisibility() == View.VISIBLE && !sp_approval2.getSelectedItem().toString().equalsIgnoreCase
                        ("Select " + tv_approval2.getText().toString())) {
                    secondLevelApr = db.getInciParamId("100", sp_approval2.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                } else if (db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("8")
                        && getIntent().getExtras().getString("secondLevel").trim().length() > 0) {
                    secondLevelApr = db.getInciParamId("100", getIntent().getExtras().getString("secondLevel").trim(), mAppPreferences.getTTModuleSelection());
                }


                if (ll_reject_category.getVisibility() == View.VISIBLE &&
                        sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                        !sp_reject_category.getSelectedItem().toString().trim().
                                equalsIgnoreCase("Select " + tv_reject_category.getText().toString().trim())
                        && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
                    rejectCategory = db.getInciParamId("117", sp_reject_category.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());

                }

                if (ll_reject_category.getVisibility() == View.VISIBLE &&
                        sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                        db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
                    rejectRemarks = et_reject_remarks.getText().toString().trim();
                }

                String moduleType = "TT";
                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                    moduleType = "H";
                }

                String tktTretmnt = "";
                if (ll_ticket_treatment.getVisibility() == View.VISIBLE && sp_ticket_treatment.isEnabled()) {
                    tktTretmnt = db.getInciParamId("1192", sp_ticket_treatment.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                } else {
                    tktTretmnt = "0";
                    //	tktTretmnt = getIntent().getExtras().getString("tktTretmnt");
                }
                String slR;
                if (sp_status_reason.getSelectedItemPosition() == -1) {
                    slR = "";
                } else {
                    //list_status_resion.get(sp_status_reason.getSelectedItemPosition());
                    slR = "" + sp_status_reason.getSelectedItemPosition();
                }
                String serviceImp;
                if (sp_serviceImapact.getSelectedItemPosition() == -1) {
                    serviceImp = "";
                } else {
                    //list_status_resion.get(sp_status_reason.getSelectedItemPosition());
                    serviceImp = "" + sp_serviceImapact.getSelectedItemPosition();
                }
                String fau;
                if (sp_fault_area.getSelectedItemPosition() == -1) {
                    fau = "";
                } else {
                    fau = "" + rttsFoultAreaList.get(sp_fault_area_details.getSelectedItemPosition() - 1).getFaultid();
                    //fau=""+sp_fault_area.getSelectedItemPosition();
                }
                String fauDe;
                if (sp_fault_area_details.getSelectedItemPosition() == -1) {
                    fauDe = "";
                } else {
                    fauDe = "" + rttsFoultAreaListdetails.get(sp_fault_area_details.getSelectedItemPosition() - 1).getFaultid();
                    //fauDe=""+sp_fault_area_details.getSelectedItemPosition();
                }
                String stu;
                if (sp_status_approve.getSelectedItemPosition() == -1) {
                    stu = "";
                } else {
                    stu = "" + sp_status_approve.getSelectedItemPosition();
                }
                String stuReso;
                if (sp_resolution_method.getSelectedItemPosition() == -1) {
                    stuReso = "";
                } else {
                    list_method_resolation.get(sp_resolution_method.getSelectedItemPosition());
                    stuReso = "" + sp_resolution_method.getSelectedItemPosition();
                }
                obj.put(AppConstants.serviceImpacted, serviceImp);
                //1.4
                int tempStatus = Integer.parseInt(db.getInciParamId("62", getIntent().getExtras().getString("status"), mAppPreferences.getTTModuleSelection()));  //1.1
                int currentStatus = Integer.parseInt(db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()));  //1.1
                String isSatisfy  = "-1";
                if(loggedBy.equalsIgnoreCase("Resolution Ticket for Repeated Outage")) {
                    if (Utils.msg(UpdateTicket.this, "874").equalsIgnoreCase(ticket_typeId)
                            && tempStatus == 7 && currentStatus == 7) {
                        isSatisfy = db.getInciParamDesc1("1208", sp_confirmation.getSelectedItem().toString().trim(),
                                mAppPreferences.getTTModuleSelection());
                    } else if (Utils.msg(UpdateTicket.this, "874").equalsIgnoreCase(ticket_typeId)
                            && tempStatus == 7 && currentStatus == 8) {
                        isSatisfy = db.getInciParamDesc1("1208", sp_confirmation.getSelectedItem().toString().trim(),
                                mAppPreferences.getTTModuleSelection());
                    } else if (Utils.msg(UpdateTicket.this, "874").equalsIgnoreCase(ticket_typeId)
                            && tempStatus == 6 && currentStatus == 7) {
                        isSatisfy = "0";
                    } else {
                        isSatisfy = "0";
                    }
                }


                //1.5
                String inputAssetId = et_asset.getText().toString().trim();
                //System.out.println("=submissionData tt =="+submissionData);
                String addParams = "alarmTxnId=" + alarmTxnId + "~"
                        + "refTktId=" + refTktId + "~" + "operators="
                        + operators + "~" + "affetctedSites=" + affetctedSites
                        + "~" + "siteAvailable=" + siteAvailable + "~"
                        + "countryId=" + mAppPreferences.getCounrtyID() + "~"
                        + "hubId=" + mAppPreferences.getHubID() + "~"
                        + "regionId=" + mAppPreferences.getRegionId() + "~"
                        + "circleId=" + mAppPreferences.getCircleID() + "~"
                        + "zoneId=" + mAppPreferences.getZoneID() + "~"
                        + "clusterId=" + mAppPreferences.getClusterID() + "~"
                        + "omeId=" + mAppPreferences.getPIOMEID() + "~"
                        + "appversion=" + pInfo.versionName + "~"
                        + "txtBatteryStartDateTime=" + discharge_date_time
                        + "~" + "longitude=" + longitude + "~" + "latitude="
                        + latitude + "~" + "rcaSubCategoryval="
                        + Str_RCA_Sub_Cate + "~" + "exempted=" + exempted + "~"
                        //+ "sparePart=" + sparePartDetails + "~"
                        + "sparePart=" + submissionData + "~"
                        + "trvlDistnc=" + et_trvlDistnc.getText().toString().trim() + "~"
                        + "noOfTech=" + et_noOfTech.getText().toString().trim() + "~"
                        + "wrkgNights=" + et_wrkgNights.getText().toString().trim() + "~"
                        + "hasSparePart=" + mAppPreferences.getSpareSerialFlagScreen() + "~"
                        + "spareSource=" + 1
                        + "~firstLevel=" + firstLevelApr
                        + "~secondLevel=" + secondLevelApr
                        + "~moduleType=" + moduleType
                        + "~rejCat=" + rejectCategory
                        + "~rejRmk=" + rejectRemarks
                        + "~userCat=" + mAppPreferences.getUserCategory()
                        + "~userSubCat=" + mAppPreferences.getUserSubCategory()
                        + "~tktTretmnt=" + tktTretmnt
                        + "~hubSiteId=" + et_hubSiteId.getText().toString().trim()
                        + "~statusReason=" + slR
                        + "~approvalSatus=" + stu
                        + "~faultAreaId=" + fau
                        + "~faultAreaDetail=" + fauDe
                        + "~resulationMethod=" + stuReso
                        + "~prNo=" + prNodetails
                        + "~serviceImpacted=" + serviceImp
                        + "~serviceImpactStart=" + tv_serviceImpactStart.getText().toString().trim()
                        + "~servicesAffected=" + et_servicesAffected.getText().toString().trim()
                        + "~rttsParentId="
                        + "~isUserSatisfied="+isSatisfy  //1.2
                        +"~assetId="+inputAssetId //1.5
                        +"~delayReason="+delay_value //110
                        +"~usergrpId="+usergrpId; //110

                obj.put(AppConstants.ADD_PARAM, addParams);
                obj.put(AppConstants.OPERATION, "E");
                obj.put("priorityId",priorityId);
                if(loggedBy.equalsIgnoreCase("Resolution Ticket for Repeated Outage")) {
                    obj.put(AppConstants.TXN_SOURSE, "PRB");
                }else {
                    obj.put(AppConstants.TXN_SOURSE, "M");
                }
                obj.put(AppConstants.LANGUAGE_CODE, mAppPreferences.getLanCode());
                obj.put(AppConstants.IMGS, imgInfoArray);
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI;
                } else {
                    url = moduleUrl + WebMethods.url_SaveAPI;
                }
                String res = Utils.httpMultipartBackground(url, "TT", jsonArrStrImg.toString(), obj.toString().
                                replaceAll("DefaultLatitude", latitude).replaceAll("DefaultLongitude", longitude),
                        "", "",
                        "", "", "", "", mAppPreferences.getLanCode(), "", "");
                String new_res = res.replace("[", "").replace("]", "");
                respo_update = new Gson().fromJson(new_res, ResponseUpdateTicket.class);
            } catch (Exception e) {
                //e.printStackTrace();
                respo_update = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (respo_update != null) {
                Utils.toastMsg(UpdateTicket.this, respo_update.getMessage());
                if (respo_update.getSuccess().equals("true")) {
                    if (cb_add_ref_ticket.isChecked() == true) {
                        referenceTicketPopUp();
                    } else {
                        if (mAppPreferences.getTTtabSelection().equalsIgnoreCase("Assigned")
                                || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Raised")
                                || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Resolved")) {
                            Intent i = new Intent(UpdateTicket.this, MyTickets.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            finish();
                        }
                        mAppPreferences.setTTupdateStatus(1);

                    }
                }
            } else {
                Utils.toast(UpdateTicket.this, "13");
            }
            super.onPostExecute(result);
        }
    }

    ;

    public void referenceTicketPopUp() {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setTypeface(Utils.typeFace(UpdateTicket.this));
        title.setText(Utils.msg(UpdateTicket.this, "136"));

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(UpdateTicket.this));
        positive.setText(Utils.msg(UpdateTicket.this, "63"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                Intent i = new Intent(UpdateTicket.this, AddTicket.class);
                mAppPreferences.saveRefMode(2);
                i.putExtra("equipment", equipment);
                i.putExtra("alarm_type", alarm_type);
                i.putExtra("alarm_description", alarm_description);
                i.putExtra("alarm_detail", alarm_detail);
                i.putExtra("problem_description", problem_description);
                i.putExtra("assingTo", sp_group.getSelectedItem().toString().trim());
                i.putExtra("site_id", Site_Id);
                i.putExtra("ticket_type", ticket_type);
                i.putExtra("ref_tkt_id", ticket_id);
                i.putExtra("date", txt_problem_start_date.getText().toString().trim());
                i.putExtra("time", txt_problem_start_time.getText().toString().trim());
                i.putExtra("bat_disc_date", tv_dis_bttry_Date.getText().toString().trim());
                i.putExtra("bat_disc_time", tv_dis_bttry_time.getText().toString().trim());
                i.putExtra("EffectiveSites", et_dependent_site.getText().toString().trim());
                i.putExtra("Operator", All_Operator_Id.toString());
                i.putExtra("OperatorExempt", all_exempt_opt_id.toString());
                i.putExtra("site_status", sp_site_visibility.getSelectedItem().toString().trim());
                i.putExtra("asgnToUid", assign_user.toString().trim());
                i.putExtra("hub_id", et_hubSiteId.getText().toString().trim());
                startActivity(i);
                finish();
            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(UpdateTicket.this));
        negative.setText(Utils.msg(UpdateTicket.this, "64"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (mAppPreferences.getTTtabSelection().equalsIgnoreCase("Assigned")
                        || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Raised")
                        || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Resolved")) {
                    Intent i = new Intent(UpdateTicket.this, MyTickets.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }
            }
        });
    }

    public void Resolved() {
        if (hide_rca_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase(category_name)) {
                RCAcate = getIntent().getExtras().getString("rca_category");
            } else if (sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select RCA Category")) {
                RCAcate = "";
            } else {
                RCAcate = db.getRcaId(sp_rca_category.getSelectedItem().toString(), "N", mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCAcate = "";
            ll_rca_category.setVisibility(View.GONE);
        }

        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category_sub.getSelectedItem().toString().equalsIgnoreCase(rca_sub_category_na)) {
                RCASubcate = getIntent().getExtras().getString("rca_sub_category");
            } else if (sp_rca_category_sub.getSelectedItem().toString().equalsIgnoreCase("Select RCA Sub Category")) {
                RCASubcate = "";
            } else {
                RCASubcate = db.getRcaId(sp_rca_category_sub.getSelectedItem().toString(), rca_id, mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCASubcate = "";
            ll_rca_sub_category.setVisibility(View.GONE);
        }

        RCA = et_rca.getText().toString().trim();

        GPSTracker gps = new GPSTracker(UpdateTicket.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        }

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(UpdateTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (txt_problem_start_date.length() != 0
                && txt_problem_start_time.length() == 0
                && hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem Start Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "128");
        } else if (txt_problem_end_date.length() != 0
                && txt_problem_end_time.length() == 0
                && hide_problem_end_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem End Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "129");
        } else if (TICKET_RCA.equalsIgnoreCase("YES")
                && sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && hide_rca_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "130");
        } else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && sp_rca_category_sub.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Sub Category")
                && hide_rca_sub_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Sub Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "131");
        } else if (!sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select RCA Category")
                && RCA.length() == 0 && hide_rca.equalsIgnoreCase("Y")) {
            Utils.toast(UpdateTicket.this, "132");
        } else if (et_dg_reading.getText().toString().length() == 0 &&
                hide_dg != null && hide_dg.length() > 0 && hide_dg.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_dg_reading.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (et_grid_reading.getText().toString().length() == 0 &&
                hide_grid != null && hide_grid.length() > 0 && hide_grid.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_grid_reading.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (et_fuel_level.getText().toString().length() == 0 &&
                hide_fuel != null && hide_fuel.length() > 0 && hide_fuel.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_fuel_level.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (ll_action_taken.getVisibility() == View.VISIBLE
                && sp_action_taken.getSelectedItem().toString().trim().equalsIgnoreCase("Select " + tv_action_taken.getText().toString().trim()) &&
                hide_action != null && hide_action.length() > 0 && hide_action.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_action_taken.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (db.getUpdateTTField("First Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval1.getVisibility() == View.VISIBLE
                && sp_approval1.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval1.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval1.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (db.getUpdateTTField("Second Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval2.getVisibility() == View.VISIBLE
                && sp_approval2.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval2.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval2.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (ll_approval2.getVisibility() == View.VISIBLE &&
                sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                sp_reject_category.getSelectedItem().toString().trim().
                        equalsIgnoreCase("Select " + tv_reject_category.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_reject_category.getText().toString(),
                    Toast.LENGTH_LONG).show();
        } else if (ll_approval2.getVisibility() == View.VISIBLE &&
                sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                et_reject_remarks.getText().toString().trim().length() == 0
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_reject_remarks.getText().toString(),
                    Toast.LENGTH_LONG).show();
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
        } else if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                .isEmpty())
                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                .isEmpty())) {
            // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "252");
        } else {
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                if (ll_dg_reading.getVisibility() == View.VISIBLE) {
                    dgReading = et_dg_reading.getText().toString().trim();
                }

                if (ll_grid_reading.getVisibility() == View.VISIBLE) {
                    gridReading = et_grid_reading.getText().toString().trim();
                }

                if (ll_fuel_level.getVisibility() == View.VISIBLE) {
                    fuelLevel = et_fuel_level.getText().toString().trim();
                }

                if (ll_action_taken.getVisibility() == View.VISIBLE
                        && !sp_action_taken.getSelectedItem().toString().trim().equalsIgnoreCase("Select Action Taken")) {
                    actionTaken = db.getInciParamId("1174", sp_action_taken.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                }
                sparePart();
                String ETA = et_eta.getText().toString().trim();
                String ETR = et_etr.getText().toString().trim();
                String remarks = et_remarks.getText().toString().trim();
                String p_start_dateT = txt_problem_start_date.getText()
                        .toString().trim()
                        + " "
                        + txt_problem_start_time.getText().toString().trim();
                String p_end_dateT = txt_problem_end_date.getText().toString().trim()
                        + " "
                        + txt_problem_end_time.getText().toString().trim();
                new UpdateTicketTask(UpdateTicket.this, remarks, ETA,
                        ETR, RCA, RCAcate, RCASubcate, p_start_dateT, p_end_dateT).execute();
            } else {
                // Toast.makeText(UpdateTicket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Utils.toast(UpdateTicket.this, "17");
            }
        }
    }

    public void Resostration() {
        if (hide_rca_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase(category_name)) {
                RCAcate = getIntent().getExtras().getString("rca_category");
            } else if (sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select RCA Category")) {
                RCAcate = "";
            } else {
                RCAcate = db.getRcaId(sp_rca_category.getSelectedItem().toString(), "N", mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCAcate = "";
            ll_rca_category.setVisibility(View.GONE);
        }

        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category_sub.getSelectedItem().toString().equalsIgnoreCase(rca_sub_category_na)) {
                RCASubcate = getIntent().getExtras().getString("rca_sub_category");
            } else if (sp_rca_category_sub.getSelectedItem().toString().equalsIgnoreCase("Select RCA Sub Category")) {
                RCASubcate = "";
            } else {
                RCASubcate = db.getRcaId(sp_rca_category_sub.getSelectedItem().toString(), rca_id, mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCASubcate = "";
            ll_rca_sub_category.setVisibility(View.GONE);
        }

        RCA = et_rca.getText().toString().trim();

        GPSTracker gps = new GPSTracker(UpdateTicket.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        }

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(UpdateTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (txt_problem_start_date.length() != 0
                && txt_problem_start_time.length() == 0
                && hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem Start Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "128");
        } else if (txt_problem_end_date.length() != 0
                && txt_problem_end_time.length() == 0
                && hide_problem_end_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem End Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "129");
        } else if (TICKET_RCA.equalsIgnoreCase("YES")
                && sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && hide_rca_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "130");
        } else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && sp_rca_category_sub.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Sub Category")
                && hide_rca_sub_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Sub Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "131");
        } else if (!sp_rca_category.getSelectedItem().toString().equalsIgnoreCase("Select RCA Category")
                && RCA.length() == 0 && hide_rca.equalsIgnoreCase("Y")) {
            Utils.toast(UpdateTicket.this, "132");
        } else if (et_dg_reading.getText().toString().length() == 0 &&
                hide_dg != null && hide_dg.length() > 0 && hide_dg.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_dg_reading.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (et_grid_reading.getText().toString().length() == 0 &&
                hide_grid != null && hide_grid.length() > 0 && hide_grid.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_grid_reading.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (et_fuel_level.getText().toString().length() == 0 &&
                hide_fuel != null && hide_fuel.length() > 0 && hide_fuel.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_fuel_level.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (ll_action_taken.getVisibility() == View.VISIBLE
                && sp_action_taken.getSelectedItem().toString().trim().equalsIgnoreCase("Select " + tv_action_taken.getText().toString().trim()) &&
                hide_action != null && hide_action.length() > 0 && hide_action.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_action_taken.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (db.getUpdateTTField("First Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval1.getVisibility() == View.VISIBLE
                && sp_approval1.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval1.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval1.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (db.getUpdateTTField("Second Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval2.getVisibility() == View.VISIBLE
                && sp_approval2.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval2.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval2.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (ll_approval2.getVisibility() == View.VISIBLE &&
                sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                sp_reject_category.getSelectedItem().toString().trim().
                        equalsIgnoreCase("Select " + tv_reject_category.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011796")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_reject_category.getText().toString(),
                    Toast.LENGTH_LONG).show();
        } else if (ll_approval2.getVisibility() == View.VISIBLE &&
                sp_approval2.getSelectedItem().toString().trim().equalsIgnoreCase("Reject") &&
                et_reject_remarks.getText().toString().trim().length() == 0
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("7")) {
            Toast.makeText(UpdateTicket.this, "Enter " + tv_reject_remarks.getText().toString(),
                    Toast.LENGTH_LONG).show();
        }else if (ll_confirmation.getVisibility()== View.VISIBLE && sp_confirmation.isEnabled()
                && sp_confirmation.getSelectedItem().toString().equalsIgnoreCase
                ("Select "+""+tv_isSatisfy.getText().toString())){
            Toast.makeText(UpdateTicket.this, "Select " + tv_isSatisfy.getText().toString(),
                    Toast.LENGTH_LONG).show();
        }
        else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
        } else if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                .isEmpty())
                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                .isEmpty())) {
            // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "252");
        } else {
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                if (ll_dg_reading.getVisibility() == View.VISIBLE) {
                    dgReading = et_dg_reading.getText().toString().trim();
                }

                if (ll_grid_reading.getVisibility() == View.VISIBLE) {
                    gridReading = et_grid_reading.getText().toString().trim();
                }

                if (ll_fuel_level.getVisibility() == View.VISIBLE) {
                    fuelLevel = et_fuel_level.getText().toString().trim();
                }

                if (ll_action_taken.getVisibility() == View.VISIBLE
                        && !sp_action_taken.getSelectedItem().toString().trim().equalsIgnoreCase("Select Action Taken")) {
                    actionTaken = db.getInciParamId("1174", sp_action_taken.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                }
                sparePart();
                String ETA = et_eta.getText().toString().trim();
                String ETR = et_etr.getText().toString().trim();
                String remarks = et_remarks.getText().toString().trim();
                String p_start_dateT = txt_problem_start_date.getText()
                        .toString().trim()
                        + " "
                        + txt_problem_start_time.getText().toString().trim();
                String p_end_dateT = txt_problem_end_date.getText().toString().trim()
                        + " "
                        + txt_problem_end_time.getText().toString().trim();
                new UpdateTicketTask(UpdateTicket.this, remarks, ETA,
                        ETR, RCA, RCAcate, RCASubcate, p_start_dateT, p_end_dateT).execute();
            } else {
                // Toast.makeText(UpdateTicket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Utils.toast(UpdateTicket.this, "17");
            }
        }
    }

    public void Assign() {
        //	dgReading = "";
        //	gridReading = "";
        //	fuelLevel = "";
        //	actionTaken = "";

        //	dgReading =  et_dg_reading.getText().toString().trim();
        //	gridReading = et_grid_reading.getText().toString().trim();
        //	fuelLevel =et_fuel_level.getText().toString().trim();
        //	actionTaken =  db.getInciParamId("1174", sp_action_taken.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());


        if (hide_rca_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase(category_name)) {
                RCAcate = getIntent().getExtras().getString("rca_category");
            } else if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Category")) {
                RCAcate = "";
            } else {
                RCAcate = db.getRcaId(sp_rca_category.getSelectedItem()
                        .toString(), "N", mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCAcate = "";
            ll_rca_category.setVisibility(View.GONE);
        }

        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase(rca_sub_category_na)) {
                RCASubcate = getIntent().getExtras().getString(
                        "rca_sub_category");
            } else if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Sub Category")) {
                RCASubcate = "";
            } else {
                RCASubcate = db.getRcaId(sp_rca_category_sub.getSelectedItem()
                        .toString(), rca_id, mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCASubcate = "";
            ll_rca_sub_category.setVisibility(View.GONE);
        }

        if (hide_rca.equalsIgnoreCase("Y")) {
            RCA = et_rca.getText().toString().trim();
        } else {
            RCA = "";
            ll_rca.setVisibility(View.GONE);
        }

        GPSTracker gps = new GPSTracker(UpdateTicket.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        }

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(UpdateTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (sp_group.getSelectedItem().toString()
                .equalsIgnoreCase("Select " + tv_group.getText().toString().trim())
                && hide_group.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this,
                    "Select " + tv_group.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(UpdateTicket.this, "138");
        } else if (txt_problem_start_date.length() != 0
                && txt_problem_start_time.length() == 0
                && hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem Start Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "128");
        } else if (db.getUpdateTTField("First Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval1.getVisibility() == View.VISIBLE
                && sp_approval1.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval1.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("6")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval1.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
        }/*else if(WorkFlowUtils.isMockSettingsON(this)==true){
			WorkFlowUtils.allowMockSettingsAlert(this);
		}*/ else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && sp_rca_category_sub.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Sub Category")
                && hide_rca_sub_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Sub Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "131");
        } else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && RCA.length() == 0 && hide_rca.equalsIgnoreCase("Y")) {
            Utils.toast(UpdateTicket.this, "132");
        } else if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                .isEmpty())
                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                .isEmpty())) {
            // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "252");
        } else {
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                String ETA = et_eta.getText().toString().trim();
                String ETR = et_etr.getText().toString().trim();
                String remarks = et_remarks.getText().toString().trim();
                String p_start_dateT = txt_problem_start_date.getText()
                        .toString().trim()
                        + " "
                        + txt_problem_start_time.getText().toString().trim();
                String p_end_dateT = "";
                if(!priorityName.isEmpty() && priorityName!=null) {
                    new UpdateTicketTask(UpdateTicket.this, remarks, ETA,
                            ETR, "", "", "", p_start_dateT,
                            "").execute();
                }
                else
                {
                    Toast.makeText(UpdateTicket.this,"Please select proper value of service impact for priority data",Toast.LENGTH_SHORT).show();
                }
            } else {
                // Toast.makeText(UpdateTicket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Utils.toast(UpdateTicket.this, "17");
            }
        }
    }


    public void InProgress() {
        //	dgReading = "";
        //	gridReading = "";
        //	fuelLevel = "";
        //	actionTaken = "";

        //	dgReading =  et_dg_reading.getText().toString().trim();
        //	gridReading = et_grid_reading.getText().toString().trim();
        //	fuelLevel =et_fuel_level.getText().toString().trim();
        //	actionTaken =  db.getInciParamId("1174", sp_action_taken.getSelectedItem().toString().trim(),mAppPreferences.getTTModuleSelection());


        if (hide_rca_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase(category_name)) {
                RCAcate = getIntent().getExtras().getString("rca_category");
            } else if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Category")) {
                RCAcate = "";
            } else {
                RCAcate = db.getRcaId(sp_rca_category.getSelectedItem()
                        .toString(), "N", mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCAcate = "";
            ll_rca_category.setVisibility(View.GONE);
        }

        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase(rca_sub_category_na)) {
                RCASubcate = getIntent().getExtras().getString(
                        "rca_sub_category");
            } else if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Sub Category")) {
                RCASubcate = "";
            } else {
                RCASubcate = db.getRcaId(sp_rca_category_sub.getSelectedItem()
                        .toString(), rca_id, mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCASubcate = "";
            ll_rca_sub_category.setVisibility(View.GONE);
        }

        if (hide_rca.equalsIgnoreCase("Y")) {
            RCA = et_rca.getText().toString().trim();
        } else {
            RCA = "";
            ll_rca.setVisibility(View.GONE);
        }

        GPSTracker gps = new GPSTracker(UpdateTicket.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        }

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(UpdateTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (sp_group.getSelectedItem().toString()
                .equalsIgnoreCase("Select " + tv_group.getText().toString().trim())
                && hide_group.equalsIgnoreCase("M")) {
            Toast.makeText(UpdateTicket.this,
                    "Select " + tv_group.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(UpdateTicket.this, "138");
        } else if (txt_problem_start_date.length() != 0
                && txt_problem_start_time.length() == 0
                && hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem Start Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "128");
        } else if (db.getUpdateTTField("First Level Approval",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")
                && ll_approval1.getVisibility() == View.VISIBLE
                && sp_approval1.getSelectedItem().toString().trim().
                equalsIgnoreCase("Select " + tv_approval1.getText().toString().trim())
                && db.getInciParamId("62", sp_status.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("2011794")) {
            Toast.makeText(UpdateTicket.this, "Select " + tv_approval1.getText().toString(), Toast.LENGTH_LONG).show();
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
        }/*else if(WorkFlowUtils.isMockSettingsON(this)==true){
			WorkFlowUtils.allowMockSettingsAlert(this);
		}*/ else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && sp_rca_category_sub.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Sub Category")
                && hide_rca_sub_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Sub Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "131");
        } else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && RCA.length() == 0 && hide_rca.equalsIgnoreCase("Y")) {
            Utils.toast(UpdateTicket.this, "132");
        } else if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                .isEmpty())
                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                .isEmpty())) {
            // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "252");
        } else {
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                String ETA = et_eta.getText().toString().trim();
                String ETR = et_etr.getText().toString().trim();
                String remarks = et_remarks.getText().toString().trim();
                String p_start_dateT = txt_problem_start_date.getText()
                        .toString().trim()
                        + " "
                        + txt_problem_start_time.getText().toString().trim();
                String p_end_dateT = "";
                new UpdateTicketTask(UpdateTicket.this, remarks, ETA,
                        ETR, "", "", "", p_start_dateT,
                        "").execute();
            } else {
                // Toast.makeText(UpdateTicket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Utils.toast(UpdateTicket.this, "17");
            }
        }
    }

    public void Open() {
        dgReading = "";
        gridReading = "";
        fuelLevel = "";
        actionTaken = "";
        if (hide_rca_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase(category_name)) {
                RCAcate = getIntent().getExtras().getString("rca_category");
            } else if (sp_rca_category.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Category")) {
                RCAcate = "";
            } else {
                RCAcate = db.getRcaId(sp_rca_category.getSelectedItem()
                        .toString(), "N", mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCAcate = "";
            ll_rca_category.setVisibility(View.GONE);
        }

        if (hide_rca_sub_category.equalsIgnoreCase("Y")) {
            if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase(rca_sub_category_na)) {
                RCASubcate = getIntent().getExtras().getString(
                        "rca_sub_category");
            } else if (sp_rca_category_sub.getSelectedItem().toString()
                    .equalsIgnoreCase("Select RCA Sub Category")) {
                RCASubcate = "";
            } else {
                RCASubcate = db.getRcaId(sp_rca_category_sub.getSelectedItem()
                        .toString(), rca_id, mAppPreferences.getTTModuleSelection());
            }
        } else {
            RCASubcate = "";
            ll_rca_sub_category.setVisibility(View.GONE);
        }

        if (hide_rca.equalsIgnoreCase("Y")) {
            RCA = et_rca.getText().toString().trim();
        } else {
            RCA = "";
            ll_rca.setVisibility(View.GONE);
        }


        GPSTracker gps = new GPSTracker(UpdateTicket.this);
        if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
        }

        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(UpdateTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(UpdateTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (txt_problem_start_date.length() != 0
                && txt_problem_start_time.length() == 0
                && hide_problem_start_date_time.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select Problem Start Time",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "128");
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
        }/*else if(WorkFlowUtils.isMockSettingsON(this)==true){
			WorkFlowUtils.allowMockSettingsAlert(this);
		}*/ else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && sp_rca_category_sub.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Sub Category")
                && hide_rca_sub_category.equalsIgnoreCase("Y")) {
            // Toast.makeText(UpdateTicket.this,"Select RCA Sub Category",
            // Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "131");
        } else if (!sp_rca_category.getSelectedItem().toString()
                .equalsIgnoreCase("Select RCA Category")
                && RCA.length() == 0 && hide_rca.equalsIgnoreCase("Y")) {
            Utils.toast(UpdateTicket.this, "132");
        } else if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                .isEmpty())
                || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                .isEmpty())) {
            // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
            Utils.toast(UpdateTicket.this, "252");
        } else {
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                String ETA = et_eta.getText().toString().trim();
                String ETR = et_etr.getText().toString().trim();
                String remarks = et_remarks.getText().toString().trim();
                String p_start_dateT = txt_problem_start_date.getText()
                        .toString().trim()
                        + " "
                        + txt_problem_start_time.getText().toString().trim();
                String p_end_dateT = "";
                new UpdateTicketTask(UpdateTicket.this, remarks, ETA,
                        ETR, "", "", "", p_start_dateT,
                        "").execute();
            } else {
                // Toast.makeText(UpdateTicket.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Utils.toast(UpdateTicket.this, "17");
            }
        }
    }

    @Override
    public void onBackPressed() {
        backButtonAlert("291", "63", "64");
        // super.onBackPressed();
    }

    public void setMsg() {
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            Utils.msgText(UpdateTicket.this, "573", tv_reject_category);
            Utils.msgText(UpdateTicket.this, "574", tv_reject_remarks);
            Utils.msgText(UpdateTicket.this, "555", tv_approval1);
            Utils.msgText(UpdateTicket.this, "556", tv_approval2);
            Utils.msgText(UpdateTicket.this, "549", tv_chk_link);
            Utils.msgText(UpdateTicket.this, "554", tv_brand_logo);
            Utils.msgText(UpdateTicket.this, "571", tv_status);
            Utils.msgText(UpdateTicket.this, "534", tv_group);
            Utils.msgText(UpdateTicket.this, "536", tv_site_visibility);
            Utils.msgText(UpdateTicket.this, "84", tv_problem_start_date);
            Utils.msgText(UpdateTicket.this, "85", tv_start_time);
            Utils.msgText(UpdateTicket.this, "139", tv_problem_end_date);
            Utils.msgText(UpdateTicket.this, "140", tv_problem_end_time);
            Utils.msgText(UpdateTicket.this, "187", tv_dis_Date);
            Utils.msgText(UpdateTicket.this, "188", tv_dis_time);
            Utils.msgText(UpdateTicket.this, "564", tv_eta);
            Utils.msgText(UpdateTicket.this, "566", tv_etr);
            Utils.msgText(UpdateTicket.this, "568", tv_rca_category);
            Utils.msgText(UpdateTicket.this, "569", tv_rca_category_sub);
            Utils.msgText(UpdateTicket.this, "570", tv_rca);
            Utils.msgText(UpdateTicket.this, "544", tv_operator);
            Utils.msgText(UpdateTicket.this, "545", tv_operator_exempted);
            Utils.msgText(UpdateTicket.this, "114", tv_dependent_site);
            Utils.msgText(UpdateTicket.this, "106", tv_remarks);
            Utils.msgText(UpdateTicket.this, "175", tv_dg_reading);
            Utils.msgText(UpdateTicket.this, "178", tv_grid_reading);
            Utils.msgText(UpdateTicket.this, "524", tv_fuel_level);
            Utils.msgText(UpdateTicket.this, "525", tv_action_taken);
            cb_add_ref_ticket.setTypeface(Utils.typeFace(UpdateTicket.this));
            cb_add_ref_ticket.setText(Utils.msg(UpdateTicket.this, "137"));
            Utils.msgText(UpdateTicket.this, "121", btn_update_tt);
        } else {
            Utils.msgText(UpdateTicket.this, "573", tv_reject_category);
            Utils.msgText(UpdateTicket.this, "574", tv_reject_remarks);
            Utils.msgText(UpdateTicket.this, "555", tv_approval1);
            Utils.msgText(UpdateTicket.this, "556", tv_approval2);
            Utils.msgText(UpdateTicket.this, "549", tv_chk_link);
            Utils.msgText(UpdateTicket.this, "108", tv_brand_logo);
            Utils.msgText(UpdateTicket.this, "98", tv_status);
            Utils.msgText(UpdateTicket.this, "109", tv_group);
            Utils.msgText(UpdateTicket.this, "78", tv_site_visibility);
            Utils.msgText(UpdateTicket.this, "84", tv_problem_start_date);
            Utils.msgText(UpdateTicket.this, "85", tv_start_time);
            Utils.msgText(UpdateTicket.this, "139", tv_problem_end_date);
            Utils.msgText(UpdateTicket.this, "140", tv_problem_end_time);
            Utils.msgText(UpdateTicket.this, "187", tv_dis_Date);
            Utils.msgText(UpdateTicket.this, "188", tv_dis_time);
            Utils.msgText(UpdateTicket.this, "99", tv_eta);
            Utils.msgText(UpdateTicket.this, "101", tv_etr);
            Utils.msgText(UpdateTicket.this, "103", tv_rca_category);
            Utils.msgText(UpdateTicket.this, "104", tv_rca_category_sub);
            Utils.msgText(UpdateTicket.this, "105", tv_rca);
            Utils.msgText(UpdateTicket.this, "110", tv_operator);
            Utils.msgText(UpdateTicket.this, "875", tv_asset); //1.5
            Utils.msgText(UpdateTicket.this, "112", tv_operator_exempted);
            Utils.msgText(UpdateTicket.this, "114", tv_dependent_site);
            Utils.msgText(UpdateTicket.this, "106", tv_remarks);
            Utils.msgText(UpdateTicket.this, "175", tv_dg_reading);
            Utils.msgText(UpdateTicket.this, "178", tv_grid_reading);
            Utils.msgText(UpdateTicket.this, "524", tv_fuel_level);
            Utils.msgText(UpdateTicket.this, "525", tv_action_taken);
            cb_add_ref_ticket.setTypeface(Utils.typeFace(UpdateTicket.this));
            cb_add_ref_ticket.setText(Utils.msg(UpdateTicket.this, "137"));
            Utils.msgText(UpdateTicket.this, "121", btn_update_tt);
        }

    }

    private void imageCapture(String tag, String Imglatitude1, String Imglongitude1, String fileType) {
        try {
            Intent intent;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (fileType.equalsIgnoreCase("mp4")) {
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// set the image file name
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
            this.MSGTag = tag;
            this.Imglatitude = Imglatitude1;
            this.Imglongitude = Imglongitude1;
            this.fileType = fileType;
            //ContentValues values = new ContentValues();
            //values.put(MediaStore.Images.Media.TITLE, "New Picture");
            //values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            //imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 2);
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                String subString = "TT";
                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                    subString = "HS";
                }
                //String imgname=et_siteId.getText().toString()+"-"+subString+"-"+System.currentTimeMillis()+imgCounter + ".jpg";
                String imgname = Site_Id + "-" + subString + "-" + System.currentTimeMillis() + imgCounter;
                if (imgname.contains("/")) {
                    imgname = imgname.replaceAll("/", "");
                }

                if (imgname.contains("\\")) {
                    imgname = imgname.replaceAll("\\\\", "");
                }

                if (imgname.contains(":")) {
                    imgname = imgname.replaceAll(":", "");
                }
                imgname = imgname;
                JSONObject allImgjsonObj = new JSONObject();
                if (imageUri != null)
                    filePath = FilePathFinder.getPath(this, imageUri);
                else {
                    Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            filePath = uri.toString();
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }

                FileOutputStream fos = null;
                File newfile = null;
                try {
                    //String rootPath = Environment.getExternalStorageDirectory() + AppConstants.PIC_PATH;
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (fileType.equalsIgnoreCase("jpg")
                            || fileType.equalsIgnoreCase("jpeg")
                            || fileType.equalsIgnoreCase("png")) {
                        imgname = imgname + ".jpg";
                        Bitmap bm = Utils.decodeFile(filePath);
                        String currTime = DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss");
                        //String abc = ""+str_siteId;

                        String waterMark = Utils.msg(UpdateTicket.this, "77") + "-" + str_siteId + " " +
                                "\n" + Utils.msg(UpdateTicket.this, "809") + "-" + Imglatitude + " \n" + Utils.msg(UpdateTicket.this, "810") + "-" + Imglongitude + "" +
                                " \n" + Utils.msg(UpdateTicket.this, "829") + "-" + currTime + "\n" + Utils.msg(UpdateTicket.this, "828") + "-" + MSGTag;

                        Bitmap bitmap = Utils.mark(bm, waterMark);

                        newfile = folder;
                        //newfile = new File( rootPath );
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        newfile = new File(newfile, imgname);
                        fos = new FileOutputStream(newfile);
                        newfile.createNewFile();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }

                    } else if (fileType.equalsIgnoreCase("mp4")) {
                        //String tempPath = Environment.getExternalStorageDirectory() + AppConstants.MEDIA_TEMP_PATH;
                        File folder1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        imgname = imgname + ".mp4";
                        File currentFile = new File(filePath);
                        File directory = folder1;
                        //File directory = new File(tempPath);
                        newfile = new File(directory, imgname);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        if (currentFile.exists()) {
                            InputStream in = new FileInputStream(currentFile);
                            OutputStream out = new FileOutputStream(newfile);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }


							/*double fileSize = (double) newfile.length()/(1024 * 1024);
							double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
							DecimalFormat df2 = new DecimalFormat("#.##");
							df2.setRoundingMode( RoundingMode.UP);

							if(fileSize>maxSize){
								ImageName(MSGTag,Imglatitude,Imglongitude,fileType,1,""+df2.format(fileSize));
								return;
							}*/
                        }

                    }

                    new VideoCompressor(UpdateTicket.this, newfile, imgname
                            , MSGTag, Imglatitude, Imglongitude, fileType).execute();
                    //setImages(newfile.toString(),WorkFlowUtils.CurrentDateTime(),MSGTag,Imglatitude,Imglongitude);

                    /*images information*/
						/*JSONObject obj = new JSONObject();
						obj.put( "name", MSGTag );
						obj.put( "time", WorkFlowUtils.CurrentDateTime() );
						obj.put( "path", imgname );
						obj.put( "location",Imglatitude+"$"+Imglongitude);
						imgInfoArray.put( obj );

						allImgjsonObj.put("path",newfile);
						allImgjsonObj.put("name",imgname);
						jsonArrStrImg.put(allImgjsonObj);
						imgCounter++;*/
                } catch (Exception e) {
                    //String s = e.getMessage();
                    Toast.makeText(UpdateTicket.this, "Try again for capture photo", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
        }
    }

    public void ImageName(final String tag, final String Imglatitude1, final String Imglongitude1, final String mediaFile, int flag, String size) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setTypeface(Utils.typeFace(UpdateTicket.this));
        title.setText(Utils.msg(UpdateTicket.this, "228") + " " + tag);

        if (flag == 0 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(UpdateTicket.this, "228") + " " + tag + " " +
                    Utils.msg(UpdateTicket.this, "584").
                            replaceAll("max size", "" + mAppPreferences.getVideoUploadMaxSize()));
        } else if (flag == 1 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(UpdateTicket.this, "585").
                    replaceAll("actual size", size)
                    .replaceAll("max size", "" + mAppPreferences.getVideoUploadMaxSize()));
        } else {
            title.setText(Utils.msg(UpdateTicket.this, "228") + " " + tag + " " + Utils.msg(UpdateTicket.this, "165").
                    replaceAll("s", ""));
        }


        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(UpdateTicket.this));
        positive.setText(Utils.msg(UpdateTicket.this, "7"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                imageCapture(tag, Imglatitude1, Imglongitude1, mediaFile);
            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(UpdateTicket.this));
        negative.setText(Utils.msg(UpdateTicket.this, "8"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.updateSparePartStatus();
        mAppPreferences.setTrackMode(1);
    }

    public void backButtonAlert(String confirmID, String primaryBt, String secondaryBT) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        tv_header.setTypeface(Utils.typeFace(UpdateTicket.this));
        positive.setTypeface(Utils.typeFace(UpdateTicket.this));
        negative.setTypeface(Utils.typeFace(UpdateTicket.this));
        title.setTypeface(Utils.typeFace(UpdateTicket.this));
        title.setText(Utils.msg(UpdateTicket.this, confirmID));
        // title.setText("Do you want to exit?");
        positive.setText(Utils.msg(UpdateTicket.this, primaryBt));
        negative.setText(Utils.msg(UpdateTicket.this, secondaryBT));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                Intent i = new Intent(UpdateTicket.this, TicketDetailsTabs.class);
                i.putExtra("id", ticket_id);
                //i.putExtra("rights", Raised);
                startActivity(i);
                finish();
            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }


    public void grpUser() {
        userCounter = 0;
        All_user_Id.setLength(0);
        tempOptExemptList = new ArrayList<Operator>();
        dialogUser = new Dialog(UpdateTicket.this, R.style.FullHeightDialog);
        dialogUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUser.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogUser.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogUser.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogUser.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogUser.show();

        ImageView iv_cancel = (ImageView) dialogUser.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialogUser.cancel();
            }
        });

        final ListView list_view = (ListView) dialogUser.findViewById(R.id.list_view);
        TextView apply = (TextView) dialogUser.findViewById(R.id.tv_apply);
        apply.setTypeface(Utils.typeFace(UpdateTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogUser.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(UpdateTicket.this));
        tv_header.setText("User(s)");
        if (dataAdapterUser != null) {
            list_view.setAdapter(dataAdapterUser);
            ArrayList<Operator> OptList = dataAdapterUser.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    userCounter++;
                    if (userCounter > 1) {
                        All_user_Id.append(",");
                    }
                    All_user_Id.append(opt.getCode());
                }
            }
        }

        if (userCounter == 0) {
            tv_grp_user.setText("Select User");
        } else {
            tv_grp_user.setText("" + userCounter + " Selected");
        }

		/*apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<Operator> OptList = dataAdapterUser.countryList;
				All_user_Id.setLength(0);
				userCounter = 0;
				for (int i = 0; i < OptList.size(); i++) {
					Operator opt = OptList.get(i);
					if (opt.isSelected()) {
						userCounter++;
						if (userCounter > 1) {
							All_user_Id.append(",");
						}
						All_user_Id.append(opt.getCode());
					}
				}
				if (userCounter == 0) {
					tv_grp_user.setText("Select User");
				} else {
					tv_grp_user.setText("" + userCounter + " Selected");
				}
				dialogUser.dismiss();// dismiss dialog box for operator
			}
		});*/
    }

    public void userSelected() {
        ArrayList<Operator> OptList = dataAdapterUser.countryList;
        All_user_Id.setLength(0);
        userCounter = 0;
        for (int i = 0; i < OptList.size(); i++) {
            Operator opt = OptList.get(i);
            if (opt.isSelected()) {
                userCounter++;
                if (userCounter > 1) {
                    All_user_Id.append(",");
                }
                All_user_Id.append(opt.getCode());
            }
        }
        // set selected operator in text view
        if (userCounter == 0) {
            tv_grp_user.setText("Select User");
        } else {
            tv_grp_user.setText("" + userCounter + " Selected");
        }
    }

    public void FackApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTicket.this);
        builder.setMessage("Uninstall " + mAppPreferences.getAppNameMockLocation() + " app/Remove Fack Location  in your mobile handset.");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void sparePart() {
        DataBaseHelper dbbb = new DataBaseHelper(UpdateTicket.this);
        dbbb.open();
        Cursor cursor = dbbb.getSpareParts(1, "");
        submissionData = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                submissionData = submissionData + cursor.getString(cursor.getColumnIndex("ID")) + "$" +
                        cursor.getString(cursor.getColumnIndex("Qty")) + "$" +
                        cursor.getString(cursor.getColumnIndex("SerialNumber")) + "@";
            }
        }
        if (submissionData.length() > 0) {
            submissionData = submissionData.substring(0,
                    submissionData.length() - 1);
        }
        dbbb.close();
    }

    public int validateAlarmDescription() {
        int status_flag = 0;
        if (trvlDis == 1 && et_trvlDistnc.getText().toString().trim().length() == 0) {
            Toast.makeText(UpdateTicket.this, "Distance Travelled cannot be blank", Toast.LENGTH_SHORT).show();
            et_trvlDistnc.clearFocus();
            et_trvlDistnc.requestFocus();
            status_flag = 1;
        } else if (trvlDis == 1 && et_trvlDistnc.getText().toString().trim().length() > 0 && etSum(et_trvlDistnc) == 0) {
            Toast.makeText(UpdateTicket.this, "Distance Travelled cannot be 0", Toast.LENGTH_SHORT).show();
            et_trvlDistnc.clearFocus();
            et_trvlDistnc.requestFocus();
            status_flag = 1;
        } else if (noOfTec == 1 && et_noOfTech.getText().toString().trim().length() == 0) {
            Toast.makeText(UpdateTicket.this, "No. Of Technician cannot be blank", Toast.LENGTH_SHORT).show();
            et_noOfTech.clearFocus();
            et_noOfTech.requestFocus();
            status_flag = 1;
        } else if (noOfTec == 1 && et_noOfTech.getText().toString().trim().length() > 0 && etSum(et_noOfTech) == 0) {
            Toast.makeText(UpdateTicket.this, "No. Of Technician cannot be 0", Toast.LENGTH_SHORT).show();
            et_noOfTech.clearFocus();
            et_noOfTech.requestFocus();
            status_flag = 1;
        } else if (wrkgNight == 1 && et_wrkgNights.getText().toString().trim().length() == 0) {
            Toast.makeText(UpdateTicket.this, "Working Nights cannot be blank", Toast.LENGTH_SHORT).show();
            et_wrkgNights.clearFocus();
            et_wrkgNights.requestFocus();
            status_flag = 1;
        }
        return status_flag;
    }

    public int etSum(EditText et) {
        int sum1 = 0, sum2 = 0, sum = 0;
        if (et.getText().toString().trim().contains(".")) {
            String input = "0" + et.getText().toString().trim() + "0";
            String[] data = input.split("\\.");
            //String[] data = et.getText().toString().trim().split("\\.");
            String before = data[0];
            int digits1 = Integer.parseInt(before);
            String after = data[1];
            int digits2 = Integer.parseInt(after);
            while (digits1 != 0) {
                int lastdigit = digits1 % 10;
                sum1 += lastdigit;
                digits1 /= 10;
            }

            while (digits2 != 0) {
                int lastdigit = digits2 % 10;
                sum2 += lastdigit;
                digits2 /= 10;
            }
            sum = sum1 + sum2;
        } else {
            int digits = Integer.parseInt(et.getText().toString().trim());
            while (digits != 0) {
                int lastdigit = digits % 10;
                sum += lastdigit;
                digits /= 10;
            }
        }
        return sum;
    }

    public class GetSiteGroupTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res = null;

        public GetSiteGroupTask(Context con) {
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
                nameValuePairs.add(new BasicNameValuePair("grpId", ""));
                nameValuePairs.add(new BasicNameValuePair("siteId", Site_Id));
                nameValuePairs.add(new BasicNameValuePair("flag", "S"));
                nameValuePairs.add(new BasicNameValuePair("userCate", mAppPreferences.getUserCategory()));      //1.0
                nameValuePairs.add(new BasicNameValuePair("userSubCate", mAppPreferences.getUserSubCategory()));
                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_get_site_wise_group;
                } else {
                    url = moduleUrl + "/" + WebMethods.url_get_site_wise_group;
                }
                res = Utils.httpPostRequest(con, url, nameValuePairs);
                res = "{response :" + res + "}";
            } catch (Exception e) {
                e.printStackTrace();
                res = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (res != null && res.length() > 0) {
                try {
                    list_group = new ArrayList<String>();
                    if (list_group.size() == 0) {
                        list_group.add(0, "Select Assigned To");
                    }
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("response");
                    for (int i = 0; i < subArray.length(); i++) {
                        if (subArray.getJSONObject(i).getString("GROUP_NAME").equalsIgnoreCase("ALL")
                                || subArray.getJSONObject(i).getString("GROUP_NAME").equalsIgnoreCase("Group Name")) {
                        } else {
                            list_group.add(subArray.getJSONObject(i).getString("GROUP_NAME"));
                        }
                    }
                    addItemsOnSpinner(sp_group, list_group);
                    int group_pos = getCategoryPos(assingTo, list_group);
                    sp_group.setSelection(group_pos);
                    //group();
                } catch (Exception e) {
                    list_group.clear();
                    list_group.add(0, "Select Assigned To");
                    //group();
                }
            } else {

            }
            super.onPostExecute(result);
        }
    }

    public class GetAssignUserTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res = null;
        String siteId = "", grpId = "";

        public GetAssignUserTask(Context con, String siteId, String grpId) {
            this.con = con;
            this.siteId = siteId;
            this.grpId = grpId;
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
                nameValuePairs.add(new BasicNameValuePair("grpId", grpId));
                nameValuePairs.add(new BasicNameValuePair("siteId", siteId));
                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_get_assign_user;
                } else {
                    url = moduleUrl + "/" + WebMethods.url_get_assign_user;
                }
                res = Utils.httpPostRequest(con, url, nameValuePairs);
                res = "{response :" + res + "}";
            } catch (Exception e) {
                e.printStackTrace();
                res = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (res != null && res.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("response");
                    for (int i = 0; i < subArray.length(); i++) {
                        list_user_id.add(subArray.getJSONObject(i).getString("USER_ID"));
                        list_userGRP.add(subArray.getJSONObject(i).getString("USER_NAME"));
                    }
                    defaultUser();
                } catch (Exception e) {

                }
            }
            super.onPostExecute(result);
        }
    }

    public void defaultUser() {
        if (!sp_group.getSelectedItem().toString().equalsIgnoreCase("Select " + tv_group.getText().toString().trim())) {
            if (asgnToUid != null && !asgnToUid.isEmpty()) {
                assignUserIdTT = asgnToUid.split(",");
                tv_grp_user.setText("" + assignUserIdTT.length + " " + "Selected");
            }
            boolean isUserSelected = false;
            for (int i = 0; i < list_user_id.size(); i++) {

                if (asgnToUid != null && !asgnToUid.isEmpty()) {
                    for (int a = 0; a < assignUserIdTT.length; a++) {
                        if (assignUserIdTT[a].equalsIgnoreCase(list_user_id.get(i))) {
                            isUserSelected = true;
                            break;
                        } else {
                            isUserSelected = false;
                        }
                    }
                } else {
                    isUserSelected = false;
                }

                Operator UnSelectuser = new Operator(list_user_id.get(i), list_userGRP.get(i), isUserSelected);
                userList.add(UnSelectuser);
            }

        }
        dataAdapterUser = new MyCustomAdapter(UpdateTicket.this, R.layout.custom_operator, userList, 3);
        ArrayList<Operator> OptList = dataAdapterUser.countryList;
        for (int i = 0; i < OptList.size(); i++) {
            Operator opt = OptList.get(i);
            if (opt.isSelected()) {
                userCounter++;
                if (userCounter > 1) {
                    All_user_Id.append(",");
                }
                All_user_Id.append(opt.getCode());
            }
        }
        if (userCounter == 0) {
            tv_grp_user.setText("Select User");
        } else {
            tv_grp_user.setText("" + userCounter + " Selected");
        }
    }

    public void setImages(String path, String time, String tag, String lati, String longi) {
        ViewImage64 viewImg = null;
        if (path != null) {
            viewImg = new ViewImage64();
            viewImg.setTimeStamp(time);
            viewImg.setName(tag);
            viewImg.setPath(path);
            viewImg.setLati(lati);
            viewImg.setLongi(longi);
            lhmImages.add(viewImg);
            //lhmImages.put(""+imgCounter, viewImg);
        }

        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(lhmImages, UpdateTicket.this);
        grid.setAdapter(horizontalAdapter);

        RecyclerView.LayoutManager mLayoutManager = new
                GridLayoutManager(this, gridCounter, GridLayoutManager.HORIZONTAL, false);
        grid.setLayoutManager(mLayoutManager);

    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        List<ViewImage64> imageList = Collections.emptyList();
        Context context;

        public HorizontalAdapter(List<ViewImage64> imageList, Context context) {
            this.imageList = imageList;
            this.context = context;

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView delete, grid_image, play_video;
            TextView tv_tag, tv_time_stamp, tv_lati, tv_longi;


            public MyViewHolder(View view) {
                super(view);
                grid_image = (ImageView) view.findViewById(R.id.grid_image);
                delete = (ImageView) view.findViewById(R.id.delete);
                play_video = (ImageView) view.findViewById(R.id.play_video);
                tv_lati = (TextView) view.findViewById(R.id.tv_lati);
                tv_longi = (TextView) view.findViewById(R.id.tv_longi);
                tv_tag = (TextView) view.findViewById(R.id.tv_tag);
                tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);

            }
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tt_img, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.delete.setVisibility(View.GONE);
            holder.delete.setImageResource(R.drawable.delete_icon);
            holder.grid_image.setVisibility(View.GONE);
            holder.play_video.setVisibility(View.GONE);
            holder.tv_lati.setText(" ");
            holder.tv_longi.setText(" ");
            holder.tv_time_stamp.setText(" ");
            holder.tv_tag.setText(" ");

            if (imageList != null && imageList.size() > 0 && imageList.get(position).getPath() != null) {
                if (imageList.get(position).getName() != null) {
                    holder.tv_tag.setText(Utils.msg(UpdateTicket.this, "473") + " " + imageList.get(position).getName());
                } else {
                    holder.tv_tag.setText(Utils.msg(UpdateTicket.this, "473") + " ");
                }

                if (imageList.get(position).getTimeStamp() != null) {
                    holder.tv_time_stamp.setText(Utils.msg(UpdateTicket.this, "474") + " " + imageList.get(position).getTimeStamp());
                } else {
                    holder.tv_time_stamp.setText(Utils.msg(UpdateTicket.this, "474") + " ");
                }


                if (imageList.get(position).getLati() != null
                        && !imageList.get(position).getLati().equalsIgnoreCase("DefaultLatitude")) {
                    holder.tv_lati.setText(Utils.msg(UpdateTicket.this, "215") + " : " + imageList.get(position).getLati());
                } else {
                    holder.tv_lati.setText(Utils.msg(UpdateTicket.this, "215") + " : ");
                }

                if (imageList.get(position).getLongi() != null
                        && !imageList.get(position).getLongi().equalsIgnoreCase("DefaultLongitude")) {
                    holder.tv_longi.setText(Utils.msg(UpdateTicket.this, "216") + " : " + imageList.get(position).getLongi());
                } else {
                    holder.tv_longi.setText(Utils.msg(UpdateTicket.this, "216") + " : ");
                }


                Bitmap bm = null;
                String path = imageList.get(position).getPath();
                File isfile = new File(path);

                if (isfile.exists()) {
                    if (path.contains(".jpeg") || path.contains(".JPEG")
                            || path.contains(".jpg") || path.contains(".JPG")
                            || path.contains(".png") || path.contains(".PNG")) {
                        bm = Utils.decodeFile(path);
                        holder.play_video.setTag("1");
                        holder.delete.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        bm = Utils.createVideoThumbNail(path);
                        holder.play_video.setTag("2");
                        holder.delete.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.stop_video);
                    }
                    if (bm != null) {
                        holder.grid_image.setImageBitmap(bm);
                    } else {
                        holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                    }
                }
            }

            holder.play_video.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaView(holder.play_video.getTag().toString(), imageList.get(position).getPath());
                }
            });

			/*holder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					backButtonAlert( 3, "512", "63", "64",
							gridId,photoType,imageList.get(position).getName(),imageList.get(position).getPath());
				}
			});*/
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    public void mediaView(String flag, String urlPath) {
        if (flag.equals("1")) {
            final Dialog nagDialog = new Dialog(UpdateTicket.this,
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
        } else if (flag.equals("2")) {
            Intent i = new Intent(UpdateTicket.this, ViewVideoVideoView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        }
    }


    class VideoCompressor extends AsyncTask<Void, Void, File> {

        ProgressDialog pd;
        Context con;
        File comFile;
        String comName, comMsgtag, comImgLat, comImgLong, comFileType;

        public VideoCompressor(Context con, File comFile, String comName,
                               String comMsgtag, String comImgLat, String comImgLong, String comFileType) {
            this.con = con;
            this.comFile = comFile;
            this.comName = comName;
            this.comMsgtag = comMsgtag;
            this.comImgLat = comImgLat;
            this.comImgLong = comImgLong;
            this.comFileType = comFileType;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mAppPreferences.getIsVideoCompress() == 1 && comFileType.equalsIgnoreCase("mp4")) {
                pd = ProgressDialog.show(con, null, "Start compression...");
            } else {
                pd = ProgressDialog.show(con, null, "Please wait...");
            }


        }


        @Override
        protected File doInBackground(Void... voids) {
            if (mAppPreferences.getIsVideoCompress() == 1 && comFileType.equalsIgnoreCase("mp4")) {
                return MediaController.getInstance().convertVideo(comFile.getPath(), comName);
            } else {
                return comFile;
            }

        }

        @Override
        protected void onPostExecute(File compressed) {
            super.onPostExecute(compressed);
            if (comFileType.equalsIgnoreCase("mp4") && compressed != null) {
                double fileSize = (double) compressed.length() / (1024 * 1024);
                double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
                DecimalFormat df2 = new DecimalFormat("#.##");
                df2.setRoundingMode(RoundingMode.UP);

                if (fileSize > maxSize) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    ImageName(comMsgtag, comImgLat, comImgLong, comFileType, 1, "" + df2.format(fileSize));
                    return;
                }
            } else if (comFileType.equalsIgnoreCase("mp4") && compressed == null) {
                compressed = comFile;
            }
            try {
                setImages(compressed.toString(), Utils.CurrentDateTime(), MSGTag, Imglatitude, Imglongitude);
                if (comMsgtag.length() == 0) {
                    comMsgtag = "";
                }
                JSONObject obj = new JSONObject();
                obj.put("name", comMsgtag);
                obj.put("time", Utils.CurrentDateTime());
                obj.put("path", comName);
                obj.put("location", Imglatitude + "$" + Imglongitude);
                imgInfoArray.put(obj);

                JSONObject allImgjsonObj = new JSONObject();
                allImgjsonObj.put("path", compressed);
                allImgjsonObj.put("name", comName);
                jsonArrStrImg.put(allImgjsonObj);
                imgCounter++;
                gridCounter++;
            } catch (Exception e) {
                //String s = e.getMessage();
                Toast.makeText(UpdateTicket.this, "Try again for capture photo", Toast.LENGTH_LONG).show();
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }


    private class GetImage extends AsyncTask<Void, Void, Void> {
        Context con;
        ProgressDialog pd;
        BeanGetImageList imageList;
        String txnId, scDate, activityId, sId, dgType, etsSid, imguploadflag;
        Intent i;

        private GetImage(Context con, Intent i, String txnId, String scDate, String activityId, String sId,
                         String etsSid, String dgType, String imguploadflag) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.etsSid = etsSid;
            this.dgType = dgType;
            this.imguploadflag = imguploadflag;
            this.i = i;
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
                nameValuePairs.add(new BasicNameValuePair("activityType", "0"));
                nameValuePairs.add(new BasicNameValuePair("scheduledDate", "H"));
                nameValuePairs.add(new BasicNameValuePair("dgType", txnId));

                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_GetPmImage;
                } else {
                    url = moduleUrl + WebMethods.url_GetPmImage;
                }

                String response = Utils.httpPostRequest(con, url, nameValuePairs);
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
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            DataBaseHelper dataBaseHelper = new DataBaseHelper(UpdateTicket.this);
            dataBaseHelper.open();
            dataBaseHelper.deleteActivityImages(txnId,"0");
            //dataBaseHelper.deleteActivityImages(txnId.replaceAll("TT","").replaceAll("-",""));
            if (imageList == null) {
            } else if (imageList.getImageList().size() > 0) {
                for (int i = 0; i < imageList.getImageList().size(); i++) {
                    if (imageList.getImageList().get(i).getPreImgPath() != null &&
                            imageList.getImageList().get(i).getPreImgPath() != "") {

                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr = new String[1000];
                        imgpathArr = imageList.getImageList().get(i).getPreImgPath().split("\\,");

                        if (imageList.getImageList().get(i).getPreImgName() != null) {
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get(i).getPreImgName().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreLat() != null) {
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get(i).getPreLat().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreLongt() != null) {
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get(i).getPreLongt().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreImgTimeStamp() != null) {
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get(i).getPreImgTimeStamp().split("\\,");
                        }

                        if (imgpathArr != null && imgpathArr.length > 0) {
                            for (int j = 0; j < imgpathArr.length; j++) {

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if (timeArr != null && imgpathArr.length <= timeArr.length) {
                                    time = timeArr[j];
                                }

                                if (nameArr != null && imgpathArr.length <= nameArr.length) {
                                    name = nameArr[j];
                                }

                                if (latArr != null && imgpathArr.length <= latArr.length) {
                                    lat = latArr[j];
                                }

                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                               /* dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,WorkFlowUtils.DateTimeStamp(),time,1,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );*/

                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 1, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j], mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI
                                ,"0");
                            }
                        }
                    }

                    if (imageList.getImageList().get(i).getIMAGE_PATH() != null &&
                            imageList.getImageList().get(i).getIMAGE_PATH() != "") {
                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr = new String[1000];
                        imgpathArr = imageList.getImageList().get(i).getIMAGE_PATH().split("\\,");

                        if (imageList.getImageList().get(i).getIMAGENAME() != null) {
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get(i).getIMAGENAME().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getLATITUDE() != null) {
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get(i).getLATITUDE().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getLONGITUDE() != null) {
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get(i).getLONGITUDE().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getImgTimeStamp() != null) {
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get(i).getImgTimeStamp().split("\\,");
                        }

                        if (imgpathArr != null && imgpathArr.length > 0) {
                            for (int j = 0; j < imgpathArr.length; j++) {

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if (timeArr != null && imgpathArr.length <= timeArr.length) {
                                    time = timeArr[j];
                                }

                                if (nameArr != null && imgpathArr.length <= nameArr.length) {
                                    name = nameArr[j];
                                }

                                if (latArr != null && imgpathArr.length <= latArr.length) {
                                    lat = latArr[j];
                                }

                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                                /*dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,WorkFlowUtils.DateTimeStamp(),time,2,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );
                              */
                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 2, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j], mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI
                                ,"0");
                            }
                        }
                    }
                }
            }
            dataBaseHelper.close();
            if (Utils.isNetworkAvailable(UpdateTicket.this)) {
                new CheckListDetailsTask(UpdateTicket.this, i, txnId, sId, scDate, dgType, activityId).execute();
            } else {
                Utils.toast(UpdateTicket.this, "17");
            }
            super.onPostExecute(result);
        }
    }

    private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String txnId, scDate, activityId, sId, dgType;
        BeanCheckListDetails PMCheckListDetails;

        private CheckListDetailsTask(Context con, Intent i, String txnId, String sId, String scDate, String dgType, String activityId) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.dgType = dgType;
            this.i = i;

        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("siteId", sId));
                nameValuePairs.add(new BasicNameValuePair("checkListType", "0"));
                nameValuePairs.add(new BasicNameValuePair("dgType", txnId));
                nameValuePairs.add(new BasicNameValuePair("status", "H"));
                nameValuePairs.add(new BasicNameValuePair("checkListDate", scDate));
                nameValuePairs.add(new BasicNameValuePair("languageCode", mAppPreferences.getLanCode()));

                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                Gson gson = new Gson();
                PMCheckListDetails = gson.fromJson(res, BeanCheckListDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
                PMCheckListDetails = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (PMCheckListDetails == null) {
                Utils.toast(UpdateTicket.this, "13");
            } else if (PMCheckListDetails.getPMCheckListDetail() != null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                DataBaseHelper dbHelper = new DataBaseHelper(UpdateTicket.this);
                dbHelper.open();
                dbHelper.clearReviewerCheclist();
                dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(), activityId);
                dbHelper.close();
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if (PMCheckListDetails.getPMCheckListDetail() != null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                startActivity(i);
            } else {
                Utils.toastMsg(UpdateTicket.this, "No Checklist Found.");
            }

            super.onPostExecute(result);
        }
    }

    //Api for get Get Rtts FaultArea with retrofit
    private void callGetRttsFaultAreaApi(String perntid) {
        ProgressDialog progressDialog = new ProgressDialog(UpdateTicket.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<List<GetRttsFoultAreaList>> call = request.GetRttsFaultAreaData("", "", perntid);
        call.enqueue(new Callback<List<GetRttsFoultAreaList>>() {
            @Override
            public void onResponse(Call<List<GetRttsFoultAreaList>> call,
                                   Response<List<GetRttsFoultAreaList>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //Toast.makeText(UpdateTicket.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null && response.body().size() > 0) {
                    rttsFoultAreaList = response.body();
                    if (list_faoult_area != null) {
                        list_faoult_area.clear();
                    }
                    for (int i = 0; i < (rttsFoultAreaList.size() + 1); i++) {
                        if (i == 0) {
                            list_faoult_area.add("Select Fault Area");
                        } else {
                            String aa = rttsFoultAreaList.get(i - 1).getFaultname().toString();
                            list_faoult_area.add(aa);
                        }
                    }
                    addItemsOnSpinner(sp_fault_area, list_faoult_area);
                    int faoult_Area = getCategoryPos(fouArea, list_faoult_area);
                    sp_fault_area.setSelection(faoult_Area);


                }

            }

            @Override
            public void onFailure(Call<List<GetRttsFoultAreaList>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    private void callGetRttsFaultAreaApi1(String perntid) {
        ProgressDialog progressDialog = new ProgressDialog(UpdateTicket.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<List<GetRttsFoultAreaList>> call = request.GetRttsFaultAreaData("", "", perntid);
        call.enqueue(new Callback<List<GetRttsFoultAreaList>>() {
            @Override
            public void onResponse(Call<List<GetRttsFoultAreaList>> call, Response<List<GetRttsFoultAreaList>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //Toast.makeText(UpdateTicket.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null && response.body().size() > 0) {
                    rttsFoultAreaListdetails = response.body();
                    if (list_fault_area_details != null) {
                        list_fault_area_details.clear();
                    }

                    for (int i = 0; i < (rttsFoultAreaListdetails.size() + 1); i++) {
                        if (i == 0) {
                            list_fault_area_details.add("Select Fault Area Details");
                        } else {
                            String aa = rttsFoultAreaListdetails.get(i - 1).getFaultname().toString();
                            list_fault_area_details.add(aa);
                        }

                    }
                    addItemsOnSpinner(sp_fault_area_details, list_fault_area_details);
                    int faoult_Area1 = getCategoryPos(fouAreaDetails, list_fault_area_details);
                    sp_fault_area_details.setSelection(faoult_Area1);
                }

            }

            @Override
            public void onFailure(Call<List<GetRttsFoultAreaList>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    public void callPriorityService(String siteId,String severity,String serviceImpact)
    {
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<ResponseBody> call = request.getPriorityData(siteId,severity,serviceImpact);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String response1 = response.body().string();
                        JSONArray jsonArray = new JSONArray(response1);
                        if (jsonArray.length() != 0) {
                            priorityName = jsonArray.getJSONObject(0).getString("PRIORITY_NAME");
                            priorityId = jsonArray.getJSONObject(0).getString("PRIORITY_ID");
                            sp_priority.setText(priorityName);

                        } else {
                            priorityName = "";
                            priorityId = "";
                            sp_priority.setText("");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
  //110
    public boolean checkUser()
    {
       // String id = Utils.msg(UpdateTicket.this, "906");
        String id = getIntent().getStringExtra("grpId");
        String[] group_id = mAppPreferences.getUserGroup().split(",");
        for(int i = 0 ; i<group_id.length;i++)
        {
            if(id.equals(group_id[i]))
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkAssignedUser()
    {
        // String id = Utils.msg(UpdateTicket.this, "906");
        String id = mAppPreferences.getUserId();
        String[] group_id = getIntent().getStringExtra("asgnToUid").split(",");
        for(int i = 0 ; i<group_id.length;i++)
        {
            if(id.equals(group_id[i]))
            {
                return true;
            }
        }
        return false;
    }
    //110

}
