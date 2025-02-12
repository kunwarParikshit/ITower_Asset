package com.isl.incident;
/*Modified By : Dhakan Lal Sharma
 Modified On : 15-June-2016
 Version     : 0.3
 Purpose     : iMaintain cr# 1.9.1.1

 Modified By : Dhakan Lal Sharma
 Modified On : 20-Oct-2016
 Version     : 0.4
 Purpose     : iMaintain cr# 1.9.1.5

 Modified By : Dhakan lal sharma
 Modified On : 21-June-2017
 Version     : 0.5
 Purpose     : a.Check auto date & time
 b.Image capture time

 Modified By : Avishek Singh
 Modified On : 27-Aug-2020
 Version     : 1.0

 Modified By : Avishek Singh
 Modified On : 02-mar-2021
 Version     : 1.2
 Purpose     : iMaintain cr# 821

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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.hsse.HsseConstant;
import com.isl.itower.GPSTracker;
import com.isl.itower.HomeActivity;
import com.isl.itower.ValidateUDetails;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.Operator;
import com.isl.modal.Response;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.preventive.PMChecklist;
import com.isl.util.DecimalDigitsInputFilter;
import com.isl.util.FilePathFinder;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.videocompressor.MediaController;
import com.isl.workflow.modal.AuditTrail;
import com.isl.workflow.modal.DropdownValue;
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
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import infozech.itower.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddTicket extends Activity {
    AutoCompleteTextView et_siteId, et_opco_siteId;
    TextView tv_operator_exempt, tv_grp_user, tv_grp;
    boolean check = false;
    TextView tv_chk_link, tv_brand_logo, tv_siteId, tv_site_visibility, tv_equipment,
            tv_sererity, tv_alarm_desc, tv_strTime, tv_bttry_dschrg_time,
            tv_assigned, tv_ref_tkt, tv_prb_strDate, tv_prb_strTime,
            tv_operator, tv_ref_tkt_id, tv_bttry_Date, tv_bttry_time,
            tv_strDate, tv_bttry_dschrg_Date, tv_alarmDetails, tv_tickettype,
            tv_problemDesc, tv_opera, tv_opera_exempt, tv_eff_site, tv_opco_siteId,tv_priority1,
            txt_no_ticket, tv_click_img, tvSubmit,serviceImpactStartTime,tv_serviceImpactStartTime,
            tv_serviceImpactStart, tv_serviceImapact,serviceImpactStart,tv_servicesAffected;
    int imgCounter = 1, uploadMin = 0, uploadMax = 0;
    Spinner sp_severity, sp_siteStatus, sp_serviceImapact;
    public SearchableSpinner sp_tktType, sp_equipment, sp_alarmDesc;//,sp_group;
    //public EditText tv_tickettypee;
    ArrayAdapter<String> multiAdapter;
    ArrayList<String> list_equipment, list_alarmType, list_alarmDesc,
            list_tktType, list_group, list_userGRP, list_serviceImpact,
            list_operator_name, list_operator_id, list_user_id, siteStatusList;
    ArrayList<String> prePopulateSitelist = new ArrayList<>();
    ArrayList<String> prePopulateOpcoSitelist = new ArrayList<>();
    String equipId, severityId, alarmDescId, tktTypeId, groupId, data, serviceImapactID, ticketType = "",
            refTktId = "", discharge_date_time, latitude = "", longitude = "",priorityId ="",priorityName="",
            filePath = "", MSGTag, asgnToUid = "", randomNo = "", autoResolved = "", Imglatitude = "DefaultLatitude",
            Imglongitude = "DefaultLongitude", fileType = "jpg",remarks = "Ticket Created :";

    String[] ttmsg, assignUserIdTT, ttMedia;
    EditText et_alarmDetails, et_problemDesc, et_deptSite, et_trvlDistnc, et_noOfTech, et_wrkgNights, et_hubSiteId, et_servicesAffected;
    Button bt_back, btnTakePhoto;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    private Uri imageUri;
    int trvlDis = 0, noOfTec = 0, wrkgNight = 0;
    Date d;
    private int hour, minute, pYear, pMonth, pDay;
    static final int TIME_DIALOG_ID = 1111, DATE_DIALOG_ID = 0, DATE_DIALOG_SERVICE_ID = 11,
            DISC_TIME_DIALOG_ID = 2222, DISC_DATE_DIALOG_ID = 1,TIME_DIALOG_ID_SIT=1121;
    Dialog dialogOperator, dialogOperatorExempt, dialogUser;
    StringBuffer All_Operator_Id, all_exempt_opt_id, All_user_Id;
    int optCounter = 0, exemptOptCounter = 0, ref_mode = 1, userCounter = 0;
    MyCustomAdapter dataAdapter = null, dataAdapterExempt = null, dataAdapterUser = null;
    ArrayList<Operator> operatorList, exemptOperatorList, tempOptExemptList, userList;
    //RelativeLayout rl;
    PackageInfo pInfo = null;
    LinearLayout ll_site_id_add, ll_site_visibility_add, ll_equipment_add,
            ll_severity_add, ll_alrm_description_add, ll_prblm_strt_date_time,
            ll_bttry_dschrg_date_time, ll_alarm_detail_add, ll_tkt_type_add,
            ll_prblm_description_add, ll_assigned_to_add, ll_user, ll_operator_add,
            ll_operator_exempt, ll_effected_sites_add, ll_trvlDistnc, ll_noOfTech,ll_priority,
            ll_wrkgNights, ll_hub_SiteId, rl_submit, ll_opco_siteId, ll_serviceImapact_add, ll_serviceImpactStart, ll_servicesAffected;
    RelativeLayout rl_no_textlayout;
    LinkedHashMap<String, String> lhm_opcoSite = new LinkedHashMap<String, String>();
    //LinkedHashMap<String, ViewImage64> lhmImages = new LinkedHashMap<String, ViewImage64>();
    List<ViewImage64> lhmImages = new ArrayList<ViewImage64>();
    JSONArray imgInfoArray, jsonArrStrImg;
    RecyclerView grid = null;
    int counter1 = 0;
    String TicketTypeMandatory = "", moduleUrl = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.add_ticket);
            tv_priority1 = (TextView) findViewById(R.id.tv_priority1);
            ll_priority = (LinearLayout) findViewById(R.id.ll_priority);
            //Utils.createFolder(AppConstants.MEDIA_TEMP_PATH);
            //Utils.createFolder(AppConstants.DOC_PATH);
            //Utils.createFolder(AppConstants.PIC_PATH);
            grid = (RecyclerView) findViewById(R.id.grid);
            grid.setNestedScrollingEnabled(false);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(AddTicket.this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            grid.setLayoutManager(layoutManager);
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                //e.printStackTrace();
            }
            // Calling method to find IDs of TextView etc.
            init();
            setMsg();
            getDataFromLocal();
            if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                moduleUrl = db.getModuleIP("HealthSafty");
            } else {
                moduleUrl = db.getModuleIP("Incident");
            }
            ticketType = Utils.msg(this, "836");
           /* if (mAppPreferences.getEnablePrePopulateSitesTT() == 1) {
                GPSTracker gps = new GPSTracker(AddTicket.this);
                if (gps.canGetLocation() == false) {
                    gps.showSettingsAlert();
                } else {
                    latitude = String.valueOf(gps.getLatitude());
                    longitude = String.valueOf(gps.getLongitude());
                    if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                            .isEmpty())
                            || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                            .isEmpty())) {
                    } else {
                        if (Utils.isNetworkAvailable(AddTicket.this)) {
                           new SiteDetailsTask(AddTicket.this, latitude, longitude).execute();
                        }
                    }
                }
            }*/


            if (Utils.isNetworkAvailable(AddTicket.this)) {
                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")
                        && !pmCheckListDataType().equalsIgnoreCase("")) {
                    new GetPMCheckList(AddTicket.this).execute();
                }
            } else {
                Utils.toast(AddTicket.this, "17");
                //Intent i = new Intent(AddTicket.this,HomeActivity.class);
                //startActivity(i);
                //finish();
            }

            //int permission = PermissionChecker.checkSelfPermission(AddTicket.this, Manifest.permission.READ_PHONE_STATE);
			/*if (permission == PermissionChecker.PERMISSION_GRANTED) {
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				randomNo = telephonyManager.getDeviceId();
			}*/
            randomNo = System.currentTimeMillis() + "" + generateOTP();

            tv_chk_link.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateChecklistLink()) {
                        if (db.isAlreadyAutoSaveChk(randomNo,"0") == 0) {
                            db.insertAutoSaveChkList(randomNo,
                                    "", "", "","");
                        }
                        chkListLink();
                    }
                }
            });

            /*et_opco_siteId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                    //Object item = parent.getItemAtPosition(position);
                    String item = parent.getItemAtPosition(position).toString();
                    String searchSite = lhm_opcoSite.get(item);
                    et_siteId.setText(searchSite);
                }
            });*/

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    tv_grp.setText("Select Assigned To");
                    lhmImages.clear();
                    imgCounter = 1;
                    HorizontalAdapter horizontalAdapter = new HorizontalAdapter(lhmImages, AddTicket.this);
                    grid.setAdapter(horizontalAdapter);
                    horizontalAdapter.notifyDataSetChanged();
                    callPriorityService(et_siteId.getText().toString(),severityId,serviceImapactID);

                }
            };

            et_siteId.addTextChangedListener(watcher);
            sp_equipment.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    equipId = db.getEquipmentId(sp_equipment.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    severityId = db.getInciParamId("61", sp_severity.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    serviceImapactID = db.getInciParamDesc("1221", sp_serviceImapact.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    tktTypeId = db.getInciParamDesc("33", sp_tktType.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                    if (tktTypeId.equalsIgnoreCase(ticketType)) {
                    } else {
                        list_alarmDesc = db.getAllAlarmDesc(severityId, equipId, tktTypeId, TicketTypeMandatory,
                                tv_alarm_desc, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserCategory(),
                                mAppPreferences.getUserSubCategory());
                        addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);
                        sp_alarmDesc.setClickable(true);
                    }
                    if (ref_mode == 2) {
                        String alarm_description = getIntent().getExtras().getString("alarm_description");
                        int descPos = getCategoryPos(alarm_description, list_alarmDesc);
                        sp_alarmDesc.setSelection(descPos);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            sp_severity.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    equipId = db.getEquipmentId(sp_equipment.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    severityId = db.getInciParamId("61", sp_severity.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    tktTypeId = db.getInciParamDesc("33", sp_tktType.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                    autoResolved = db.getParmShortName("61", severityId, mAppPreferences.getTTModuleSelection());
                    if (tktTypeId.equalsIgnoreCase(ticketType)) {
                    } else {
                        list_alarmDesc = db.getAllAlarmDesc(severityId, equipId, tktTypeId, TicketTypeMandatory,
                                tv_alarm_desc, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserCategory(),
                                mAppPreferences.getUserSubCategory());
                        addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);
                        sp_alarmDesc.setClickable(true);
                    }
                    if (ref_mode == 2) {
                        String alarm_description = getIntent().getExtras().getString("alarm_description");
                        int descPos = getCategoryPos(alarm_description, list_alarmDesc);
                        sp_alarmDesc.setSelection(descPos);
                    }
                    if(!sp_severity.getSelectedItem().toString().equalsIgnoreCase("Select Severity"))
                    {
                        callPriorityService(et_siteId.getText().toString(),severityId,serviceImapactID);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            sp_serviceImapact.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // String site_id = et_siteId.getText().toString();
                    // String severity = sp_severity.getSelectedItem().toString();
                    serviceImapactID = db.getInciParamDesc("1221", sp_serviceImapact.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    // if (serviceImapactID.equalsIgnoreCase("0")) {
                    if (sp_serviceImapact.getSelectedItem().toString().equalsIgnoreCase("Select Service Impact")) {
                        ll_serviceImpactStart.setVisibility(View.GONE);
                        ll_servicesAffected.setVisibility(View.GONE);
                    } else {
                        ll_serviceImpactStart.setVisibility(View.VISIBLE);
                        ll_servicesAffected.setVisibility(View.VISIBLE);
                        callPriorityService(et_siteId.getText().toString(),severityId,serviceImapactID);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            sp_alarmDesc.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    equipId = db.getEquipmentId(sp_equipment.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    severityId = db.getInciParamId("61", sp_severity.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    if (sp_equipment.getSelectedItem().toString().trim()
                            .equalsIgnoreCase("Select " + tv_equipment.getText().toString().trim())) {
                        alarmDescId = db.getDescIDD(severityId, sp_alarmDesc.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    } else {
                        alarmDescId = db.getDescID(severityId, equipId, sp_alarmDesc.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                    }

                    String pShortName = db.getPDesc("4", alarmDescId, mAppPreferences.getTTModuleSelection());
                    et_trvlDistnc.setText("");
                    et_noOfTech.setText("");
                    et_wrkgNights.setText("");

                    if (pShortName.contains("trvlDistnc") && db.getAddTTField("Distance Travelled", mAppPreferences.getTTModuleSelection())
                            .equalsIgnoreCase("Y")) {
                        ll_trvlDistnc.setVisibility(View.VISIBLE);
                        trvlDis = 1;
                    } else {
                        ll_trvlDistnc.setVisibility(View.GONE);
                        trvlDis = 0;
                    }

                    if (pShortName.contains("noOfTech") && db.getAddTTField("No. Of Technician", mAppPreferences.getTTModuleSelection())
                            .equalsIgnoreCase("Y")) {
                        ll_noOfTech.setVisibility(View.VISIBLE);
                        noOfTec = 1;
                    } else {
                        ll_noOfTech.setVisibility(View.GONE);
                        noOfTec = 0;
                    }

                    if (pShortName.contains("wrkgNights") && db.getAddTTField("Working Nights", mAppPreferences.getTTModuleSelection()).
                            equalsIgnoreCase("Y")) {
                        ll_wrkgNights.setVisibility(View.VISIBLE);
                        wrkgNight = 1;
                    } else {
                        ll_wrkgNights.setVisibility(View.GONE);
                        wrkgNight = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            // get ticket type id as Repair or Replacement
            sp_tktType.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (sp_tktType.getSelectedItem().toString()
                            .equals("Select " + tv_tickettype.getText().toString().trim())) {
                        tktTypeId = "-1";
                    } else {
                        equipId = db.getEquipmentId(sp_equipment.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                        severityId = db.getInciParamId("61", sp_severity.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                        tktTypeId = db.getInciParamDesc("33", sp_tktType.getSelectedItem().toString().trim(), mAppPreferences.getTTModuleSelection());
                        serviceImapactID = db.getInciParamDesc("1221", sp_serviceImapact.getSelectedItem().toString(), mAppPreferences.getTTModuleSelection());
                        addItemsOnSpinner(sp_serviceImapact, list_serviceImpact);
                        addItemsOnSpinner(sp_severity, list_alarmType);

                        if (tktTypeId.equalsIgnoreCase(ticketType)) {
                            ll_user.setVisibility(View.GONE);
                            list_alarmDesc = db.getAllAlarmDesc("1", equipId, tktTypeId, TicketTypeMandatory,
                                    tv_alarm_desc, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserCategory(),
                                    mAppPreferences.getUserSubCategory());
                            addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);
                            sp_alarmDesc.setClickable(false);
                            sp_alarmDesc.setEnabled(false);
                            int stc = 0;
                            for (int i = 0; i < list_operator_id.size(); i++) {
                                if (list_operator_id.get(i).equalsIgnoreCase(Utils.msg(AddTicket.this, "838"))) {
                                    stc = i;
                                }
                            }

                            operatorList.get(stc).setSelected(true);
                            tv_operator.setText("1" + " Operator Selected");
                            Operator operExemp = new Operator(
                                    list_operator_id.get(stc),
                                    list_operator_name.get(stc), true);
                            exemptOperatorList.add(operExemp);
                            int opEx = 0;
                            for (int j = 0; j < exemptOperatorList.size(); j++) {
                                //  if (exemptOperatorList.get(j).getCode().equalsIgnoreCase(Utils.msg(AddTicket.this,"838")))
                                if (exemptOperatorList.get(j).isSelected()) {
                                    opEx = opEx + 1;
                                }
                            }

                            tv_operator_exempt.setText(opEx + " Operator Exempted Selected");

                        } else {
                            ll_user.setVisibility(View.VISIBLE);
                            list_alarmDesc = db.getAllAlarmDesc(severityId, equipId, tktTypeId, TicketTypeMandatory,
                                    tv_alarm_desc, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserCategory(),
                                    mAppPreferences.getUserSubCategory());
                            addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);
                            sp_alarmDesc.setClickable(true);
                        }
                        if (ref_mode == 2) {
                            String alarm_description = getIntent().getExtras().getString("alarm_description");
                            int descPos = getCategoryPos(alarm_description, list_alarmDesc);
                            sp_alarmDesc.setSelection(descPos);
                            String alarm_type = getIntent().getExtras().getString("alarm_type");
                            int alarmPos = getCategoryPos(alarm_type, list_alarmType);
                            sp_severity.setSelection(alarmPos);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            if (ref_mode == 2) { // ref_mode
                et_siteId.setEnabled(false);
                et_opco_siteId.setEnabled(false);
                addReferenceTicket();
                tv_ref_tkt.setVisibility(View.VISIBLE);
                tv_ref_tkt_id.setVisibility(View.VISIBLE);
            } else if(ref_mode == 3){
                et_siteId.setEnabled(false);
                et_opco_siteId.setEnabled(false);
                tv_ref_tkt.setVisibility(View.VISIBLE);
                tv_ref_tkt_id.setVisibility(View.VISIBLE);
                addCorrectiveTicket();
            } else {
                et_siteId.setEnabled(true);
                et_opco_siteId.setEnabled(true);
                tv_ref_tkt.setVisibility(View.GONE);
                tv_ref_tkt_id.setVisibility(View.GONE);
                if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")) {
                    operatorUser();
                } else {
                    for (int i = 0; i < list_operator_name.size(); i++) {
                        if (!list_operator_name.get(i)
                                .equalsIgnoreCase("Please Select")) {
                            Operator oper = new Operator(list_operator_id.get(i),
                                    list_operator_name.get(i), false);
                            operatorList.add(oper);
                        }
                    }
                }
            }
            final Calendar cal = Calendar.getInstance();
            pYear = cal.get(Calendar.YEAR);
            pMonth = cal.get(Calendar.MONTH);
            pDay = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);

            tv_prb_strDate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_ID);
                }
            });

            tv_serviceImpactStart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DATE_DIALOG_SERVICE_ID);
                }
            });

            tv_serviceImpactStartTime.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_serviceImpactStart.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID_SIT);
                    else
                        Toast.makeText(AddTicket.this,"Select Service Impact Start Date first",Toast.LENGTH_SHORT).show();
//                        Utils.toast(AddTicket.this, "126");
                }
            });

            tv_bttry_Date.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(DISC_DATE_DIALOG_ID);
                }
            });

            tv_prb_strTime.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_prb_strDate.getText().toString().length() > 0)
                        showDialog(TIME_DIALOG_ID);
                    else
                        // Toast.makeText(AddTicket.this,"Select Problem Start Date first",Toast.LENGTH_SHORT).show();
                        Utils.toast(AddTicket.this, "126");
                }
            });

            tv_bttry_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_bttry_Date.getText().toString().length() > 0)
                        showDialog(DISC_TIME_DIALOG_ID);
                    else
                        // Toast.makeText(AddTicket.this,"Select Problem Start Date first",Toast.LENGTH_SHORT).show();
                        Utils.toast(AddTicket.this, "126");
                }
            });

            tvSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setClickable(false);

                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setClickable(true);

                        }
                    }, 200);


                    if (validate()) {
                        if (db.getAddTTField("Checklist Link", mAppPreferences.getTTModuleSelection())
                                .equalsIgnoreCase("Y")
                                && db.isAlreadyAutoSaveChk(randomNo,"0") == 1
                                && mAppPreferences.getTTChklistMadatory() == 0) {
                            backButtonAlert("583", "63", "64", 1);
                        } else {
                            if(!priorityName.equalsIgnoreCase("") && !priorityName.isEmpty() && priorityName!=null) {
                                if (sp_serviceImapact.getSelectedItem().toString().equalsIgnoreCase("Outage") || sp_serviceImapact.getSelectedItem().toString().equalsIgnoreCase("Degraded")) {
                                    if(!tv_serviceImpactStart.getText().toString().isEmpty() && tv_serviceImpactStart.getText().toString() != null && !tv_serviceImpactStartTime.getText().toString().isEmpty() && tv_serviceImpactStartTime.getText().toString() != null)
                                    {
                                        submitData();
                                    }
                                    else
                                    {
                                        Toast.makeText(AddTicket.this,"Please select Service Impact Start Date and Time!!",Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else {
                                    submitData();
                                }

                            }
                            else
                            {
                                Toast.makeText(AddTicket.this,"Please select proper values of severity and service impact for priority data",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            btnTakePhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    GPSTracker gps = new GPSTracker(AddTicket.this);
                    String Imglatitude1 = "DefaultLatitude", Imglongitude1 = "DefaultLongitude";
                    if (!Utils.hasPermissions(AddTicket.this, AppConstants.PERMISSIONS)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //ActivityCompat.requestPermissions(FuelFillingActivity.this, Constants.PERMISSIONS,100);
                        Toast.makeText(AddTicket.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                    } else if (gps.canGetLocation() == false) {
                        gps.showSettingsAlert();
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
                            }

                            String type = "jpg";
                            if (ttMedia.length >= imgCounter) {
                                int a = imgCounter - 1;
                                type = ttMedia[a];
                            }

                            if (ttmsg.length >= imgCounter) {
                                int a = imgCounter - 1;
                                String siteId = et_siteId.getText().toString();
                                if (siteId.isEmpty() || siteId.equalsIgnoreCase("") || siteId == null) {
                                    //  et_siteId.setError(Utils.msg(AddTicket.this, "830"));
                                    Utils.toastMsg(AddTicket.this, Utils.msg(AddTicket.this, "830"));
                                    return;
                                }
                                ImageName(ttmsg[a], Imglatitude1, Imglongitude1, type, 0, "");
                            } else {
                                String siteId = et_siteId.getText().toString();
                                if (siteId.isEmpty() || siteId.equalsIgnoreCase("") || siteId == null) {
                                    et_siteId.setError(Utils.msg(AddTicket.this, "830"));
                                    Utils.toastMsg(AddTicket.this, Utils.msg(AddTicket.this, "830"));
                                    return;
                                }
                                imageCapture(" ", Imglatitude1, Imglongitude1, type);
                            }

                        } else {
                            // Toast.makeText(PMChecklist.this,"Maximum " +
                            // mAppPreferences.getPMmaximage()+
                            // " Images can be uploaded.",Toast.LENGTH_SHORT).show();
                            String s = Utils.msg(AddTicket.this, "253");
                            s = s + " " + uploadMax;
                            s = s + " " + Utils.msg(AddTicket.this, "254");
                            Utils.toastMsg(AddTicket.this, s);
                        }
                    }
                }
            });
        } catch (Exception e) {
            String s = e.getMessage();
            String s1 = e.getMessage();
            //e.printStackTrace();
        }
    }


    public void chkListLink() {
        HashMap<String, String> valData = new HashMap<String, String>();
        valData.put("DEFAULT", "");
        HashMap<String, String> readingData = new HashMap<String, String>();
        readingData.put("DEFAULT", "0");
        Intent i = new Intent(AddTicket.this, PMChecklist.class);
        //i.putExtra("scheduledDate",WorkFlowUtils.CurrentDate(1));
        i.putExtra("scheduledDate", "01-JAN-2020");
        i.putExtra("S", "S");
        i.putExtra("siteId", et_siteId.getText().toString().trim().toUpperCase());
        String Sname = null;
        i.putExtra("siteName", Sname);
        i.putExtra("activityTypeId", alarmDescId);
        i.putExtra("paramName", "");
        i.putExtra("Status", "S");
        i.putExtra("dgType", "0");
        i.putExtra("txn", randomNo);
        i.putExtra("etsSid", "");
        i.putExtra("preMinImage", "0");
        i.putExtra("preMaxImage", "0");
        i.putExtra("postMinImage", "0");
        i.putExtra("postMaxImage", "0");
        i.putExtra("imageName", "img");
        i.putExtra("valData", valData);
        i.putExtra("readingData", readingData);
        i.putExtra("rvDate", "");
        i.putExtra("rejRmks", "");
        i.putExtra("rCat", "");
        startActivity(i);
    }

    public void submitData() {
        String site_visibility = "1";
        String affetctedSites = null;

        //if (validate()) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dgReading", "");
            obj.put("gridReading", "");
            obj.put("fuelLevel", "");
            obj.put("actionTaken", "");
            obj.put(AppConstants.UPLOAD_TYPE, "TT");
            obj.put(AppConstants.SITE_ID_ALIAS, et_siteId.getText().toString().trim());
            if (sp_equipment.getSelectedItem().toString().trim()
                    .equalsIgnoreCase("Select " + tv_equipment.getText().toString().trim())
                    || !db.getAddTTField("Equipment", mAppPreferences.getTTModuleSelection())
                    .equalsIgnoreCase("Y")) {
                equipId = "0";
                obj.put(AppConstants.EQUIPMENT_ID, equipId);
            } else {
                obj.put(AppConstants.EQUIPMENT_ID, equipId);
            }

            if (!db.getAddTTField("Severity", mAppPreferences.getTTModuleSelection())
                    .equalsIgnoreCase("Y")) {
                severityId = "0";
                obj.put(AppConstants.SEVERITY_ID, severityId);
            } else {
                obj.put(AppConstants.SEVERITY_ID, severityId);
            }
            obj.put(AppConstants.TICKET_LOG_DATE, Utils.CurrentDateTime());

            if (sp_tktType.getSelectedItem().toString().trim().
                    equalsIgnoreCase("Select " + tv_tickettype.getText().toString().trim())) {
                tktTypeId = "-1";
                obj.put(AppConstants.TICKET_TYPE_ID, tktTypeId);
            } else {
                obj.put(AppConstants.TICKET_TYPE_ID, tktTypeId);
            }

            if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")
                    && autoResolved.equalsIgnoreCase("0")) {
                obj.put(AppConstants.TICKET_STATUS_ID, "7");
            } else if (tktTypeId.equalsIgnoreCase(ticketType)) {
                obj.put(AppConstants.TICKET_STATUS_ID, "2011794");
            } else {
                obj.put(AppConstants.TICKET_STATUS_ID, "5");
            }

            if (tv_grp.getText().toString().trim().equalsIgnoreCase("Select Assigned To")) {
                groupId = "0";
                obj.put(AppConstants.TT_ASSIGN_GROUP_ID, groupId);
            } else {
                obj.put(AppConstants.TT_ASSIGN_GROUP_ID, groupId);
            }
            String assign_user = "";
            if (All_user_Id.toString() != null
                    && !All_user_Id.toString().isEmpty()) {
                assign_user = All_user_Id.toString();
            }

            if (assign_user == null || assign_user.isEmpty()
                    || assign_user.length() == 0
                    || assign_user.equalsIgnoreCase("null")) {
                assign_user = "";
            }
            obj.put(AppConstants.TT_ASSIGN_USER_ID, assign_user);
            obj.put(AppConstants.ETA, "");
            obj.put(AppConstants.ETR, "");
            obj.put(AppConstants.RCA_CATE_ID, "");
            obj.put(AppConstants.RCA, "");

            String probStrtDateTime = tv_prb_strDate.getText()
                    .toString().trim()
                    + " " + tv_prb_strTime.getText().toString().trim();

            String serviceImpactStart = tv_serviceImpactStart.getText()
                    .toString().trim()
                    + " " + tv_serviceImpactStartTime.getText().toString().trim();

//          String serviceImpactStart = tv_serviceImpactStart.getText().toString().trim();
            obj.put(AppConstants.PROBLEM_START_DATE, probStrtDateTime);
            obj.put(AppConstants.PROBLEM_END_DATE, "");
            obj.put(AppConstants.U_ID, mAppPreferences.getUserId());
            obj.put(AppConstants.U_NAME, mAppPreferences.getLoginId());
            obj.put(AppConstants.REMARKS, remarks);
            obj.put(AppConstants.ALARM_DETAIL, et_alarmDetails.getText().toString().trim());
            obj.put(AppConstants.PROBLEM_DESCRIPTION, et_problemDesc.getText().toString().trim());
            obj.put(AppConstants.TICKET_ID, "");
            //Avdhesh
            if (!db.getAddTTField("Alarm Description", mAppPreferences.getTTModuleSelection())
                    .equalsIgnoreCase("Y")) {
                alarmDescId = "0";
                obj.put(AppConstants.ALARM_DESCRIPTION_ID, alarmDescId);
            } else if (tktTypeId.equalsIgnoreCase(ticketType)) {
                obj.put(AppConstants.ALARM_DESCRIPTION_ID, Utils.msg(this, "837"));
            } else {
                obj.put(AppConstants.ALARM_DESCRIPTION_ID, alarmDescId);
            }
            obj.put(AppConstants.servicesAffected, "" + et_servicesAffected.getText().toString());
            obj.put(AppConstants.serviceImpacted, "" + serviceImapactID);
            obj.put(AppConstants.serviceImpactStart, "" + serviceImpactStart);
            obj.put("alarmDes", db.getDesc(alarmDescId, mAppPreferences.getTTModuleSelection()));
            String alarmTxnId = "";
            String operators = "";
            String exempted = "";
            if (et_deptSite.getText().toString().length() == 0) {
                affetctedSites = "1";
            } else {
                affetctedSites = et_deptSite.getText().toString()
                        .trim();
            }


            if (All_Operator_Id.toString() != null
                    && !All_Operator_Id.toString().isEmpty()) {
                operators = All_Operator_Id.toString();
            }
            if (operators == null || operators.isEmpty()
                    || operators.length() == 0
                    || operators.equalsIgnoreCase("null")) {
                operators = "";
            }

            if (all_exempt_opt_id.toString() != null
                    && !all_exempt_opt_id.toString().isEmpty()) {
                exempted = all_exempt_opt_id.toString();
            }

            if (exempted == null || exempted.isEmpty()
                    || exempted.length() == 0
                    || exempted.equalsIgnoreCase("null")) {
                exempted = "";
            }

            if (sp_siteStatus.getSelectedItem().toString().trim()
                    .equalsIgnoreCase("Yes")) {
                site_visibility = "1";
            } else if (sp_siteStatus.getSelectedItem().toString()
                    .trim().equalsIgnoreCase("No")) {
                site_visibility = "2";
            }

            if (!db.getAddTTField("Site Visibility", mAppPreferences.getTTModuleSelection())
                    .equalsIgnoreCase("Y")) {
                site_visibility = "0";
            }

            refTktId = tv_ref_tkt_id.getText().toString().trim();
            discharge_date_time = tv_bttry_Date.getText().toString()
                    .trim()
                    + " " + tv_bttry_time.getText().toString().trim();
            String moduleType = "TT";
            if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                moduleType = "H";
            }


            String addParams = "alarmTxnId=" + alarmTxnId + "~"
                    + "refTktId=" + refTktId + "~" + "operators="
                    + operators + "~" + "affetctedSites="
                    + affetctedSites + "~" + "siteAvailable="
                    + site_visibility + "~" + "countryId="
                    + mAppPreferences.getCounrtyID() + "~" + "hubId="
                    + mAppPreferences.getHubID() + "~" + "regionId="
                    + mAppPreferences.getRegionId() + "~" + "circleId="
                    + mAppPreferences.getCircleID() + "~" + "zoneId="
                    + mAppPreferences.getZoneID() + "~" + "clusterId="
                    + mAppPreferences.getClusterID() + "~" + "omeId="
                    + mAppPreferences.getPIOMEID() + "~"
                    + "appversion=" + pInfo.versionName + "~"
                    + "txtBatteryStartDateTime=" + discharge_date_time
                    + "~" + "longitude=" + longitude + "~"
                    + "latitude=" + latitude + "~"
                    + "exempted=" + exempted + "~"
                    + "trvlDistnc=" + et_trvlDistnc.getText().toString().trim() + "~"
                    + "noOfTech=" + et_noOfTech.getText().toString().trim() + "~"
                    + "wrkgNights=" + et_wrkgNights.getText().toString().trim() + "~"
                    + "moduleType=" + moduleType + "~"
                    + "tktId=" + randomNo
                    + "~firstLevel="
                    + "~secondLevel="
                    + "~userCat=" + mAppPreferences.getUserCategory()
                    + "~userSubCat=" + mAppPreferences.getUserSubCategory()
                    + "~hubSiteId=" + et_hubSiteId.getText().toString().trim()
                    + "~prNo="
                    + "~originatorId="
                    + "~abstract="
                    + "~serviceImpacted=" + serviceImapactID
                    + "~serviceImpactStart=" + serviceImpactStart
                    + "~quantityEquivCircuit="
                    + "~servicesAffected=" + et_servicesAffected.getText().toString()
                    + "~initialAnalysis="
                    + "~xReferences="
                    + "~twoGNode="
                    + "~threeGNode="
                    + "~Lte="
                    + "~iPSite="
                    + "~originatorGroup="
                    + "~lastReferredGroup=";    //1.2
            obj.put(AppConstants.ADD_PARAM, addParams);
            obj.put(AppConstants.OPERATION, "A");
            obj.put("priorityId",priorityId);
            obj.put(AppConstants.TXN_SOURSE, "M");
            obj.put(AppConstants.LANGUAGE_CODE, mAppPreferences.getLanCode());
            obj.put(AppConstants.IMGS, imgInfoArray);
            //obj.put( AppConstants.IMGS, imgInfoArray.toString().replaceAll("latitude",latitude)
            //		.replaceAll("longitude",longitude));
        } catch (
                JSONException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        String url = "";
        if (moduleUrl.equalsIgnoreCase("0")) {
            url = mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI;
        } else {
            url = moduleUrl + WebMethods.url_SaveAPI;
        }
        // when net present
        if (Utils.isNetworkAvailable(AddTicket.this)) {
            String submitData = obj.toString().replaceAll("DefaultLatitude", latitude);
            Log.d("submitData", "" + submitData);
            submitData = submitData.replaceAll("DefaultLongitude", longitude);
            AsynTaskService task = new AsynTaskService(submitData);

            //AsynTaskService task = new AsynTaskService(obj.toString().
            //		replaceAll("DefaultLatitude",latitude).replaceAll("DefaultLongitude",longitude));
            task.execute(url);
        } else {
            // 1 means trouble tt data flag
            db.insertDataLocally("1", obj.toString().
                            replaceAll("DefaultLatitude", latitude).replaceAll("DefaultLongitude", longitude)
                    , jsonArrStrImg.toString(), "", "", mAppPreferences.getUserId(), url);
            // Showing message of data saving locally when Network
            // is not present.
            // Toast.makeText(AddTicket.this,"No internet connection.Data stored locally in the app.",Toast.LENGTH_SHORT).show();
            Utils.toast(AddTicket.this, "66");
            // show message No internet connection.Data stored
            // locally in the app.
            // return back to previous screen.
            finish();
            // end 0.3
        }
        //}
    }

    public void referenceTicketPopUp() {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(AddTicket.this, R.style.FullHeightDialog);
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
        title.setTypeface(Utils.typeFace(AddTicket.this));
        title.setText(Utils.msg(AddTicket.this, "747"));
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(AddTicket.this));
        positive.setText(Utils.msg(AddTicket.this, "63"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                Intent i = new Intent(AddTicket.this, AddTicket.class);
                mAppPreferences.saveRefMode(2);
                i.putExtra("equipment", getIntent().getExtras().getString("equipment"));
                i.putExtra("alarm_type", getIntent().getExtras().getString("alarm_type"));
                i.putExtra("alarm_description", getIntent().getExtras().getString("alarm_description"));
                i.putExtra("alarm_detail", getIntent().getExtras().getString("alarm_detail"));
                i.putExtra("problem_description", getIntent().getExtras().getString("problem_description"));
                i.putExtra("assingTo", getIntent().getExtras().getString("assingTo"));
                i.putExtra("site_id", getIntent().getExtras().getString("site_id"));
                i.putExtra("ticket_type", getIntent().getExtras().getString("ticket_type"));
                i.putExtra("ref_tkt_id", refTktId);
                i.putExtra("date", getIntent().getExtras().getString("date"));
                i.putExtra("time", getIntent().getExtras().getString("time"));
                i.putExtra("bat_disc_date", getIntent().getExtras().getString("bat_disc_date"));
                i.putExtra("bat_disc_time", getIntent().getExtras().getString("bat_disc_time"));
                i.putExtra("EffectiveSites", getIntent().getExtras().getString("EffectiveSites"));
                i.putExtra("Operator", getIntent().getExtras().getString("Operator"));
                i.putExtra("OperatorExempt", getIntent().getExtras().getString("OperatorExempt"));
                i.putExtra("site_status", getIntent().getExtras().getString("site_status"));
                i.putExtra("asgnToUid", getIntent().getExtras().getString("asgnToUid"));
                i.putExtra("hub_id", getIntent().getExtras().getString("hub_id"));
                startActivity(i);
                finish();
                //recreate();
            }
        });
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(AddTicket.this));
        negative.setText(Utils.msg(AddTicket.this, "64"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (mAppPreferences.getTTtabSelection().equalsIgnoreCase("Assigned")
                        || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Raised")
                        || mAppPreferences.getTTtabSelection().equalsIgnoreCase("Resolved")) {
                    Intent i = new Intent(AddTicket.this, MyTickets.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                } else {
                    finish();
                }
            }
        });
    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(AddTicket.this));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(AddTicket.this));
                v.setPadding(10, 15, 10, 15);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void init() {
        //createFolder(AppConstants.PIC_PATH);
        imgInfoArray = new JSONArray();
        jsonArrStrImg = new JSONArray();
        mAppPreferences = new AppPreferences(AddTicket.this);
        mAppPreferences.setTrackMode(0);
		/*if (mAppPreferences.getTTimageMessage().length() != 0) {
			ttmsg = mAppPreferences.getTTimageMessage().split("\\~");
		}*/
        ref_mode = mAppPreferences.getRefMode();
        All_Operator_Id = new StringBuffer();
        all_exempt_opt_id = new StringBuffer();
        All_user_Id = new StringBuffer();
        db = new DataBaseHelper(AddTicket.this);
        db.open();
        TicketTypeMandatory = db.getAddTTField("Ticket Type", mAppPreferences.getTTModuleSelection());
        ll_trvlDistnc = (LinearLayout) findViewById(R.id.ll_trvlDistnc);
        ll_noOfTech = (LinearLayout) findViewById(R.id.ll_noOfTech);
        ll_wrkgNights = (LinearLayout) findViewById(R.id.ll_wrkgNights);
        ll_hub_SiteId = (LinearLayout) findViewById(R.id.ll_hub_siteId);
        ll_serviceImapact_add = (LinearLayout) findViewById(R.id.ll_serviceImapact_add);
        ll_serviceImpactStart = (LinearLayout) findViewById(R.id.ll_serviceImpactStart);
        ll_servicesAffected = (LinearLayout) findViewById(R.id.ll_servicesAffected);

        et_trvlDistnc = (EditText) findViewById(R.id.et_trvlDistnc);
        //new EditTextLength( et_trvlDistnc, 10, 2 );
        et_trvlDistnc.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        et_noOfTech = (EditText) findViewById(R.id.et_noOfTech);
        et_wrkgNights = (EditText) findViewById(R.id.et_wrkgNights);
        et_hubSiteId = (EditText) findViewById(R.id.et_hubSiteId);
        et_servicesAffected = (EditText) findViewById(R.id.et_servicesAffected);
        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        tv_chk_link = (TextView) findViewById(R.id.tv_chk_link);
        tv_chk_link.setPaintFlags(tv_chk_link.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        tv_ref_tkt = (TextView) findViewById(R.id.tv_tkt);
        tv_site_visibility = (TextView) findViewById(R.id.tv_site_visibility);
        tv_siteId = (TextView) findViewById(R.id.tv_siteId);
        tv_siteId.setFocusable(true);
        tv_siteId.setFocusableInTouchMode(true);

        btnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        tv_click_img = (TextView) findViewById(R.id.tv_click_img);
        Utils.msgText(AddTicket.this, "240", tv_click_img);

        tv_equipment = (TextView) findViewById(R.id.tv_equipment);
        tv_equipment.setFocusable(true);
        tv_equipment.setFocusableInTouchMode(true);

        tv_sererity = (TextView) findViewById(R.id.tv_sererity);
        tv_sererity.setFocusable(true);
        tv_sererity.setFocusableInTouchMode(true);

        tv_serviceImapact = (TextView) findViewById(R.id.tv_serviceImapact);
        tv_serviceImapact.setFocusable(true);
        tv_serviceImapact.setFocusableInTouchMode(true);

        serviceImpactStart= (TextView) findViewById(R.id.serviceImpactStart);
        serviceImpactStart.setFocusable(true);
        serviceImpactStart.setFocusableInTouchMode(true);

        tv_servicesAffected= (TextView) findViewById(R.id.tv_servicesAffected);
        tv_servicesAffected.setFocusable(true);
        tv_servicesAffected.setFocusableInTouchMode(true);

        tv_alarm_desc = (TextView) findViewById(R.id.tv_alarm_desc);
        tv_alarm_desc.setFocusable(true);
        tv_alarm_desc.setFocusableInTouchMode(true);

        tv_strDate = (TextView) findViewById(R.id.tv_strDate);
        tv_strTime = (TextView) findViewById(R.id.tv_strTime);
        tv_strTime.setFocusableInTouchMode(true);
        tv_strTime.setFocusable(true);

        tv_bttry_dschrg_Date = (TextView) findViewById(R.id.tv_bttry_dschrg_Date);
        tv_bttry_dschrg_time = (TextView) findViewById(R.id.tv_bttry_dschrg_time);
        tv_bttry_dschrg_time.setFocusableInTouchMode(true);
        tv_bttry_dschrg_time.setFocusable(true);

        tv_alarmDetails = (TextView) findViewById(R.id.tv_alarmDetails);
        tv_tickettype = (TextView) findViewById(R.id.tv_tickettype);





		/*multiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		multiAdapter.add("Item1");
		multiAdapter.add("Item2");
		multiAdapter.add("Item3");
		multiAdapter.add("Item4");
		multiAdapter.add("Item5");*/

        //tv_tickettypee.setAdapter(multiAdapter,false,onSelectedListener);
        // set initial selection
        //boolean[] selectedItems = new boolean[multiAdapter.getCount()];
        //selectedItems[1] = true; // select second item
        //tv_tickettypee.setSelected(selectedItems);


        tv_problemDesc = (TextView) findViewById(R.id.tv_problemDesc);
        tv_assigned = (TextView) findViewById(R.id.tv_assigned);
        tv_assigned.setFocusable(true);
        tv_assigned.setFocusableInTouchMode(true);
        tv_opera = (TextView) findViewById(R.id.tv_opera);
        tv_opera_exempt = (TextView) findViewById(R.id.tv_opera_exempt);
        tv_eff_site = (TextView) findViewById(R.id.tv_eff_site);
        txt_no_ticket = (TextView) findViewById(R.id.txt_no_ticket);
        sp_siteStatus = (Spinner) findViewById(R.id.sp_site_status);
        sp_serviceImapact = (Spinner) findViewById(R.id.sp_serviceImapact);
        //setupUI(sp_siteStatus);
        tv_ref_tkt_id = (TextView) findViewById(R.id.et_ref_tkt);
        bt_back = (Button) findViewById(R.id.button_back);
        Utils.msgButton(AddTicket.this, "71", bt_back);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        sp_equipment = (SearchableSpinner) findViewById(R.id.sp_equipment);
        //setupUI(sp_equipment);
        sp_severity = (Spinner) findViewById(R.id.sp_alarmType);
        //setupUI(sp_alarmType);
        sp_alarmDesc = (SearchableSpinner) findViewById(R.id.sp_alarmDesc);
        //setupUI(sp_alarmDesc);
        sp_tktType = (SearchableSpinner) findViewById(R.id.sp_tktType);
        //setupUI(sp_tktType);
        //sp_group = (SearchableSpinner) findViewById(R.id.sp_group);
        //setupUI(sp_group);
        int threshold  = 3;
        try {
            threshold = Integer.parseInt(Utils.msg(AddTicket.this, "937"));
        }catch (Exception e){
            threshold  = 3;
        }
        et_siteId = (AutoCompleteTextView) findViewById(R.id.et_siteId);
        et_siteId.setTypeface(Utils.typeFace(AddTicket.this));
        et_siteId.setThreshold(threshold);

        et_opco_siteId = (AutoCompleteTextView) findViewById(R.id.et_opco_siteId);
        et_opco_siteId.setTypeface(Utils.typeFace(AddTicket.this));
        et_opco_siteId.setThreshold(threshold);

        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            moduleUrl = db.getModuleIP("HealthSafty");
        } else {
            moduleUrl = db.getModuleIP("Incident");
        }
        String siteSearchUrl = "";
        if (moduleUrl.equalsIgnoreCase("0")) {
            siteSearchUrl = mAppPreferences.getConfigIP() + WebMethods.url_get_siteDetails;
        } else {
            siteSearchUrl = moduleUrl + WebMethods.url_get_siteDetails;
        }

        if (mAppPreferences.getEnablePrePopulateSitesTT() == 1) {
            et_siteId.setAdapter(new AutoCompleteAdapter(AddTicket.this, siteSearchUrl
                    ,4,R.layout.spinner_dropdown));
        }

        et_siteId.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DropdownValue selectedValue = (DropdownValue) parent.getItemAtPosition(position);
                et_siteId.setText(selectedValue.getValue());
                et_siteId.setSelection(selectedValue.getValue().trim().length());
                et_opco_siteId.setText(selectedValue.getVal1());
                et_opco_siteId.setSelection(selectedValue.getVal1().trim().length());
            }
        });




        if (mAppPreferences.getEnablePrePopulateSitesTT() == 1) {
            et_opco_siteId.setAdapter(new AutoCompleteAdapter(AddTicket.this, siteSearchUrl,
                    2,R.layout.spinner_dropdown));
        }
        et_opco_siteId.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DropdownValue selectedValue = (DropdownValue) parent.getItemAtPosition(position);
                et_siteId.setText(selectedValue.getVal1());
                et_siteId.setSelection(selectedValue.getVal1().trim().length());

                et_opco_siteId.setText(selectedValue.getValue());
                et_opco_siteId.setSelection(selectedValue.getValue().trim().length());

            }
        });

        et_alarmDetails = (EditText) findViewById(R.id.et_alarmDetails);
        et_alarmDetails.setTypeface(Utils.typeFace(AddTicket.this));

        et_problemDesc = (EditText) findViewById(R.id.et_problemDesc);
        et_problemDesc.setTypeface(Utils.typeFace(AddTicket.this));

        et_deptSite = (EditText) findViewById(R.id.et_deptSite);
        et_deptSite.setTypeface(Utils.typeFace(AddTicket.this));

        tv_prb_strDate = (TextView) findViewById(R.id.tv_prb_strDate);
        tv_prb_strDate.setVisibility(View.VISIBLE);
        tv_prb_strDate.setTypeface(Utils.typeFace(AddTicket.this));

        tv_serviceImpactStart = (TextView) findViewById(R.id.tv_serviceImpactStart);
        tv_serviceImpactStart.setVisibility(View.VISIBLE);
        tv_serviceImpactStart.setTypeface(Utils.typeFace(AddTicket.this));

        tv_serviceImpactStartTime = (TextView) findViewById(R.id.tv_serviceImpactStartTime);
        tv_serviceImpactStartTime.setVisibility(View.VISIBLE);
        tv_serviceImpactStartTime.setTypeface(Utils.typeFace(AddTicket.this));

        serviceImpactStartTime = (TextView) findViewById(R.id.serviceImpactStartTime);
        serviceImpactStartTime.setFocusableInTouchMode(true);
        serviceImpactStartTime.setFocusable(true);

        tv_prb_strTime = (TextView) findViewById(R.id.tv_prb_strTime);
        tv_prb_strTime.setVisibility(View.VISIBLE);
        tv_prb_strTime.setTypeface(Utils.typeFace(AddTicket.this));

        tv_bttry_Date = (TextView) findViewById(R.id.tv_bttry_Date);
        tv_bttry_Date.setVisibility(View.VISIBLE);
        tv_bttry_Date.setTypeface(Utils.typeFace(AddTicket.this));

        tv_bttry_time = (TextView) findViewById(R.id.tv_bttry_time);
        tv_bttry_time.setVisibility(View.VISIBLE);
        tv_bttry_time.setTypeface(Utils.typeFace(AddTicket.this));

        tv_operator = (TextView) findViewById(R.id.tv_operator);
        tv_operator.setTypeface(Utils.typeFace(AddTicket.this));

        tv_grp_user = (TextView) findViewById(R.id.tv_grp_user);
        tv_grp_user.setTypeface(Utils.typeFace(AddTicket.this));

        tv_grp = (TextView) findViewById(R.id.tv_grp);
        tv_siteId.setFocusable(true);
        tv_siteId.setFocusableInTouchMode(true);
        tv_grp.setTypeface(Utils.typeFace(AddTicket.this));

        tv_operator_exempt = (TextView) findViewById(R.id.tv_operator_exempt);
        tv_operator_exempt.setTypeface(Utils.typeFace(AddTicket.this));

        sp_siteStatus.setBackgroundResource(R.drawable.doted);
        sp_serviceImapact.setBackgroundResource(R.drawable.doted);
        sp_equipment.setBackgroundResource(R.drawable.doted);
        sp_severity.setBackgroundResource(R.drawable.doted);
        sp_alarmDesc.setBackgroundResource(R.drawable.doted);
        sp_tktType.setBackgroundResource(R.drawable.doted);
        //sp_group.setBackgroundResource(R.drawable.doted);
        ll_site_id_add = (LinearLayout) findViewById(R.id.ll_site_id_add);
        ll_site_visibility_add = (LinearLayout) findViewById(R.id.ll_site_visibility_add);
        ll_equipment_add = (LinearLayout) findViewById(R.id.ll_equipment_add);
        ll_severity_add = (LinearLayout) findViewById(R.id.ll_severity_add);
        ll_alrm_description_add = (LinearLayout) findViewById(R.id.ll_alrm_description_add);
        ll_prblm_strt_date_time = (LinearLayout) findViewById(R.id.ll_prblm_strt_date_time);
        ll_bttry_dschrg_date_time = (LinearLayout) findViewById(R.id.ll_bttry_dschrg_date_time);
        ll_alarm_detail_add = (LinearLayout) findViewById(R.id.ll_alarm_detail_add);
        ll_tkt_type_add = (LinearLayout) findViewById(R.id.ll_tkt_type_add);
        ll_prblm_description_add = (LinearLayout) findViewById(R.id.ll_prblm_description_add);
        ll_assigned_to_add = (LinearLayout) findViewById(R.id.ll_assigned_to_add);
        ll_user = (LinearLayout) findViewById(R.id.ll_user);
        ll_operator_add = (LinearLayout) findViewById(R.id.ll_operator_add);
        ll_operator_exempt = (LinearLayout) findViewById(R.id.ll_operator_exempt);
        ll_effected_sites_add = (LinearLayout) findViewById(R.id.ll_effected_sites_add);
        rl_submit = (LinearLayout) findViewById(R.id.rl_submit);
        rl_no_textlayout = (RelativeLayout) findViewById(R.id.rl_no_textlayout);
        ll_opco_siteId = (LinearLayout) findViewById(R.id.ll_opco_siteId);
        tv_opco_siteId = (TextView) findViewById(R.id.tv_opco_siteId);

        if (!db.getAddTTField("Site Id", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_site_id_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (!db.getAddTTField("Hub Site Id", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {    //1.2
            ll_hub_SiteId.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (!db.getAddTTField("Site Visibility", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_site_visibility_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Equipment", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_equipment_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Severity", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_severity_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Alarm Description", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_alrm_description_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Problem Start Date Time", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_prblm_strt_date_time.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Alarm Detail", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_alarm_detail_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Battery Discharge Start Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_bttry_dschrg_date_time.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (db.getAddTTField("Ticket Type", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            ll_tkt_type_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Problem Description", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_prblm_description_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (db.getAddTTField("Assigned To", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            ll_assigned_to_add.setVisibility(View.GONE);
            ll_user.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        } else {
            ll_assigned_to_add.setVisibility(View.VISIBLE);
            ll_user.setVisibility(View.VISIBLE);

            if (mAppPreferences.getUserCategory().equalsIgnoreCase("2") &&
                    (mAppPreferences.getOperatorWiseUserField().equalsIgnoreCase("0") ||
                            mAppPreferences.getOperatorWiseUserField().equalsIgnoreCase("2"))) {  //1.0
                ll_user.setVisibility(View.GONE);
            }
            counter1 = counter1 + 1;
        }

        if (!db.getAddTTField("Operator", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_operator_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }
        if (!db.getAddTTField("Effected Sites", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_effected_sites_add.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (!db.getAddTTField("Exempted Operator", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_operator_exempt.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (!db.getAddTTField("Opco Site Id", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_opco_siteId.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (db.getAddTTField("Checklist Link", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
            tv_chk_link.setVisibility(View.GONE);
            counter1 = counter1 + 1;
        }

        if (counter1 == 17) {
            rl_submit.setVisibility(View.GONE);
            rl_no_textlayout.setVisibility(View.VISIBLE);
        } else {
            rl_submit.setVisibility(View.VISIBLE);
            rl_no_textlayout.setVisibility(View.GONE);
        }
        // end 0.2
        bt_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                backButtonAlert("291", "63", "64", 0);
            }
        });

        tv_operator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                OperatorpopUp();
            }
        });

        tv_operator_exempt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                OperatorExemptPopUp(1);
            }
        });

        tv_grp_user.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!db.getAddTTField("Assigned To", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("N")) {
                    if (tv_grp.getText().toString().trim().equals("Select Assigned To")) {
                        Utils.toast(AddTicket.this, "138");// Select Assigned To
                        tv_assigned.clearFocus();
                        tv_assigned.requestFocus();
                    } else {
                        grpUser();
                    }
                }
            }
        });


        tv_grp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_siteId.getText().toString().length() == 0) {
                    Utils.toast(AddTicket.this, "152"); // Site Id cannot be blank.
                    tv_siteId.clearFocus();
                    tv_siteId.requestFocus();
                    return;
                } else {
                    tv_grp.clearFocus();
                    tv_grp.requestFocus();

                    if (Utils.isNetworkAvailable(AddTicket.this)) {
                        GetSiteGroupTask task = new GetSiteGroupTask(AddTicket.this);
                        task.execute();
                    } else {
                        list_group.clear();
                        list_group = db.getInciGrp(tv_grp, mAppPreferences.getTTModuleSelection());
                        group();
                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDataFromLocal() {
        list_operator_name = db.getOperatorName(mAppPreferences.getTTModuleSelection());
        list_operator_id = db.getOperatorId(mAppPreferences.getTTModuleSelection());
        operatorList = new ArrayList<Operator>();
        userList = new ArrayList<Operator>();
        exemptOperatorList = new ArrayList<Operator>();

        list_equipment = db.getEquipment(tv_equipment, mAppPreferences.getTTModuleSelection(),
                mAppPreferences.getUserCategory(), mAppPreferences.getUserSubCategory());

        if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")) {
            list_alarmType = db.getSeverity(tv_sererity, mAppPreferences.getTTModuleSelection(),
                    mAppPreferences.getUserSubCategory());
        } else {
            list_alarmType = db.getInciParam1("61", tv_sererity, mAppPreferences.getTTModuleSelection());
        }
        if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")) {
            list_serviceImpact = db.getSeverity(tv_serviceImapact, mAppPreferences.getTTModuleSelection(),
                    mAppPreferences.getUserSubCategory());
        } else {
            list_serviceImpact = db.getInciParam1("1221", tv_serviceImapact, mAppPreferences.getTTModuleSelection());
        }

        list_alarmDesc = db.getAllAlarmDesc("R", "R", "R", TicketTypeMandatory,
                tv_alarm_desc, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserCategory(),
                mAppPreferences.getUserSubCategory());

        if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")) {
            list_tktType = db.getTicketType(tv_tickettype, mAppPreferences.getTTModuleSelection(), mAppPreferences.getUserSubCategory());
        } else {
            list_tktType = db.getInciParam3("33", tv_tickettype, mAppPreferences.getTTModuleSelection());
        }

        list_group = new ArrayList<String>();
        siteStatusList = new ArrayList<String>();
        siteStatusList.add("Yes");
        siteStatusList.add("No");
       /* list_serviceImpact = new ArrayList<String>();
        list_serviceImpact.add("Select Service Impact");
        list_serviceImpact.add("Outage");
        list_serviceImpact.add("Degraded");
        list_serviceImpact.add("No");*/
        addItemsOnSpinner(sp_siteStatus, siteStatusList);
        addItemsOnSpinner(sp_serviceImapact, list_serviceImpact);
        addItemsOnSpinner(sp_equipment, list_equipment);
        addItemsOnSpinner(sp_severity, list_alarmType);
        addItemsOnSpinner(sp_alarmDesc, list_alarmDesc);
        addItemsOnSpinner(sp_tktType, list_tktType);
        //addItemsOnSpinner(sp_group, list_group);
    }

    private boolean validate() {
        boolean status = true;
        GPSTracker gps = new GPSTracker(AddTicket.this);
        if (gps.canGetLocation() == false) {
            status = false;
            gps.showSettingsAlert();
        } else if (!Utils.hasPermissions(AddTicket.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            status = false;
            Toast.makeText(AddTicket.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
        } else if (Utils.isAutoDateTime(this)) {
            status = false;
            Utils.autoDateTimeSettingsAlert(this);
        } else if (gps.isMockLocation() == true) {
            FackApp();
            status = false;
        } else if (imgCounter <= uploadMin) {
            status = false;
            //Capture Minimum 0 and Maximum 5 Images.
            String s1 = Utils.msg(AddTicket.this, "257") + " "
                    + uploadMin + " "
                    + Utils.msg(AddTicket.this, "258") + " "
                    + uploadMax + " "
                    + Utils.msg(AddTicket.this, "165");
            Utils.toastMsg(AddTicket.this, s1);
        } else if (db.getAddTTField("Checklist Link", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("M") && mAppPreferences.getTTChklistMadatory() == 0) {
            status = false;
            Toast.makeText(AddTicket.this, "Please " + tv_chk_link.getText().toString(),
                    Toast.LENGTH_LONG).show();
        } else if (et_siteId.getText().toString().trim().length() == 0
                && db.getAddTTField("Site Id", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            Utils.toast(AddTicket.this, "152"); // Site Id cannot be blank.
            tv_siteId.clearFocus();
            tv_siteId.requestFocus();
            status = false;
            return status;
        }
        if (sp_tktType.getSelectedItem().toString().equals("Select " + tv_tickettype.getText().toString().trim())
                && TicketTypeMandatory.equalsIgnoreCase("M")) {
            Toast.makeText(AddTicket.this, "Select " + tv_tickettype.getText().toString(), Toast.LENGTH_SHORT).show();
            tv_tickettype.clearFocus();
            tv_tickettype.requestFocus();
            status = false;
        } else if (sp_severity.getSelectedItem().toString().trim()
                .equals("Select " + tv_sererity.getText().toString().trim())
                && db.getAddTTField("Severity", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            //WorkFlowUtils.toast(AddTicket.this, "249"); // Select Severity.
            Toast.makeText(AddTicket.this, "Select " + tv_sererity.getText().toString(), Toast.LENGTH_SHORT).show();
            tv_sererity.clearFocus();
            tv_sererity.requestFocus();
            status = false;
        } else if (trvlDis == 1 && et_trvlDistnc.getText().toString().trim().length() > 0 && etSum(et_trvlDistnc) == 0) {
            Toast.makeText(AddTicket.this, "Distance Travelled cannot be 0", Toast.LENGTH_SHORT).show();
            et_trvlDistnc.clearFocus();
            et_trvlDistnc.requestFocus();
            status = false;
        } else if (noOfTec == 1 && et_noOfTech.getText().toString().trim().length() > 0 && etSum(et_noOfTech) == 0) {
            Toast.makeText(AddTicket.this, "No. Of Technician cannot be 0", Toast.LENGTH_SHORT).show();
            et_noOfTech.clearFocus();
            et_noOfTech.requestFocus();
            status = false;
        } else if (sp_alarmDesc.getSelectedItem().toString().trim()
                .equals("Select " + tv_alarm_desc.getText().toString().trim())
                && db.getAddTTField("Alarm Description", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            Toast.makeText(AddTicket.this, "Select " + tv_alarm_desc.getText().toString(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(AddTicket.this, "250"); // Select Alarm Description
            tv_alarm_desc.clearFocus();
            tv_alarm_desc.requestFocus();
            status = false;
        } else if (tv_prb_strDate.length() != 0
                && tv_prb_strTime.length() == 0
                && db.getAddTTField("Problem Start Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            Toast.makeText(AddTicket.this, "Select " + tv_strTime.getText().toString(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(AddTicket.this, "128"); // Select Alarm Description Select Problem Start Time
            tv_strTime.clearFocus();
            tv_strTime.requestFocus();
            status = false;
        } else if (tv_bttry_Date.length() != 0
                && tv_bttry_time.length() == 0
                && db.getAddTTField("Battery Discharge Start Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            Toast.makeText(AddTicket.this, "Select " + tv_bttry_dschrg_time.getText().toString(),
                    Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(AddTicket.this, "251"); // Select Battery Discharge Start Time
            tv_bttry_dschrg_time.clearFocus();
            tv_bttry_dschrg_time.requestFocus();
            status = false;
        } else if (tv_grp.getText().toString().trim().equals("Select Assigned To")
                && db.getAddTTField("Assigned To", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")) {
            Toast.makeText(AddTicket.this, "Select " + tv_assigned.getText().toString(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(AddTicket.this, "138");// Select Assigned To
            tv_assigned.clearFocus();
            tv_assigned.requestFocus();
            status = false;
        } else if (sp_serviceImapact.getSelectedItem().toString().trim().equalsIgnoreCase("Select " + tv_serviceImapact.getText().toString().trim())
                && db.getAddTTField("Service Impact",mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")) {
            //WorkFlowUtils.toast(AddTicket.this, "249"); // Select Severity.
            Toast.makeText(AddTicket.this, "Select " + tv_serviceImapact.getText().toString(), Toast.LENGTH_SHORT).show();
            tv_serviceImapact.clearFocus();
            tv_serviceImapact.requestFocus();
            status = false;
        } else if ((tv_serviceImpactStart.getText().toString().trim().equalsIgnoreCase("")
                || tv_serviceImpactStart.getText().toString() == null || tv_serviceImpactStart.getText().length() == 0) &&
                db.getAddTTField("Service Impact Start",mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")) {
            //WorkFlowUtils.toast(AddTicket.this, "249"); // Select Severity.
            Toast.makeText(AddTicket.this, "Select Service Impact Start Date", Toast.LENGTH_SHORT).show();
            tv_serviceImpactStart.clearFocus();
            tv_serviceImpactStart.requestFocus();
            status = false;
        } else if ((et_servicesAffected.getText().toString().trim().equalsIgnoreCase("")
                || et_servicesAffected.getText().toString() == null || et_servicesAffected.getText().length() == 0) &&
                db.getAddTTField("Services Effected",mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("M")) {
            //WorkFlowUtils.toast(AddTicket.this, "249"); // Select Severity.
            Toast.makeText(AddTicket.this, "Services Effected can not be blank", Toast.LENGTH_SHORT).show();
            et_servicesAffected.clearFocus();
            et_servicesAffected.requestFocus();
            status = false;
        } else if (gps.canGetLocation()) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            //latitude = "28.12345";
            //longitude = "77.12345";
            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                    .isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                    .isEmpty())) {
                status = false;
                // Toast.makeText(FuelFillingActivity.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(AddTicket.this, "252");
            }
        }
        return status;
    }

    private boolean validateChecklistLink() {
        boolean status = true;
        if (et_siteId.getText().toString().trim().length() == 0
                && db.getAddTTField("Site Id", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            Utils.toast(AddTicket.this, "152"); // Site Id cannot be blank.
            tv_siteId.clearFocus();
            tv_siteId.requestFocus();
            status = false;
            return status;
        } else if (sp_tktType.getSelectedItem().toString().equals("Select " + tv_tickettype.getText().toString().trim())
                && TicketTypeMandatory.equalsIgnoreCase("M")) {
            Toast.makeText(AddTicket.this, "Select " + tv_tickettype.getText().toString(), Toast.LENGTH_SHORT).show();
            tv_tickettype.clearFocus();
            tv_tickettype.requestFocus();
            status = false;
        } else if (sp_severity.getSelectedItem().toString().trim()
                .equals("Select " + tv_sererity.getText().toString().trim())
                && db.getAddTTField("Severity", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            //WorkFlowUtils.toast(AddTicket.this, "249"); // Select Severity.
            Toast.makeText(AddTicket.this, "Select " + tv_sererity.getText().toString(), Toast.LENGTH_SHORT).show();
            tv_sererity.clearFocus();
            tv_sererity.requestFocus();
            status = false;
        } else if (sp_alarmDesc.getSelectedItem().toString().trim()
                .equals("Select " + tv_alarm_desc.getText().toString().trim())
                && db.getAddTTField("Alarm Description", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            Toast.makeText(AddTicket.this, "Select " + tv_alarm_desc.getText().toString(), Toast.LENGTH_SHORT).show();
            //WorkFlowUtils.toast(AddTicket.this, "250"); // Select Alarm Description
            tv_alarm_desc.clearFocus();
            tv_alarm_desc.requestFocus();
            status = false;
        } else if (db.getHSChecklist(alarmDescId, mAppPreferences.getTTModuleSelection()) == 0) {
            Toast.makeText(AddTicket.this, "Health and Safety Checklist not Configure for "
                    + sp_alarmDesc.getSelectedItem().toString().trim() + " " + tv_alarm_desc.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            status = false;
        }
        return status;
    }

    public void FackApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTicket.this);
        builder.setMessage("Uninstall " + mAppPreferences.getAppNameMockLocation() + " app/Remove Fack Location  in your mobile handset.");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addCorrectiveTicket(){
        remarks = getIntent().getExtras().getString("remarks");


        String site_id = getIntent().getExtras().getString("site_id");
        et_siteId.setText(site_id);


        // ref ticket id
        refTktId = getIntent().getExtras().getString("ref_tkt_id");
        tv_ref_tkt_id.setText("" + refTktId);

        // Ticket type
        String ticket_type = getIntent().getExtras().getString("ticket_type");
        sp_tktType.setEnabled(false);
        sp_tktType.setClickable(false);
        int tktTypepos = getCategoryPos(ticket_type, list_tktType);
        sp_tktType.setSelection(tktTypepos);


    }

    public void addReferenceTicket() {
        String site_id = getIntent().getExtras().getString("site_id");
        et_siteId.setText(site_id);

        asgnToUid = getIntent().getExtras().getString("asgnToUid");

        String groupName = getIntent().getExtras().getString("assingTo");
        tv_grp.setText(groupName);
        groupId = db.getGroupId(groupName.trim(), "654");

        // ref ticket id
        refTktId = getIntent().getExtras().getString("ref_tkt_id");
        tv_ref_tkt_id.setText("" + refTktId);


        // alarm_detail
        String alarm_detail = getIntent().getExtras().getString("alarm_detail");
        et_alarmDetails.setText(alarm_detail);

        // problem_description
        String problem_description = getIntent().getExtras().getString(
                "problem_description");
        et_problemDesc.setText(problem_description);

        // Effective Site
        String Effective_Site = getIntent().getExtras().getString(
                "EffectiveSites");
        et_deptSite.setText(Effective_Site);

        String bat_disc_date = getIntent().getExtras().getString(
                "bat_disc_date");
        tv_bttry_Date.setText(bat_disc_date);

        String bat_disc_time = getIntent().getExtras().getString(
                "bat_disc_time");
        tv_bttry_time.setText(bat_disc_time);

        // site status
        String site_status = getIntent().getExtras().getString("site_status");
        int statusPos = getCategoryPos(site_status, siteStatusList);
        sp_siteStatus.setSelection(statusPos);

        // Ticket type
        String ticket_type = getIntent().getExtras().getString("ticket_type");
        int tktTypepos = getCategoryPos(ticket_type, list_tktType);
        sp_tktType.setSelection(tktTypepos);

        // equipment
        String equipment = getIntent().getExtras().getString("equipment");
        int equiPos = getCategoryPos(equipment, list_equipment);
        sp_equipment.setSelection(equiPos);

        // alarm type
        String alarm_type = getIntent().getExtras().getString("alarm_type");
        int alarmPos = getCategoryPos(alarm_type, list_alarmType);
        sp_severity.setSelection(alarmPos);


        //Hub Id
        String hubId = getIntent().getExtras().getString(
                "hub_id");
        et_hubSiteId.setText(hubId);

        String Operator = getIntent().getExtras().getString("Operator");
        String OperatorExempt = getIntent().getExtras().getString(
                "OperatorExempt");
        if (Operator.equalsIgnoreCase("null") || Operator.isEmpty()
                || Operator == null) {
            Operator = "";
            for (int i = 0; i < list_operator_id.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    Operator operator = new Operator(list_operator_id.get(i),
                            list_operator_name.get(i), false);
                    operatorList.add(operator);
                }
            }
        }
        All_Operator_Id.append(Operator);
        all_exempt_opt_id.append(OperatorExempt);
        if (Operator != null && !Operator.isEmpty()
                && !Operator.equalsIgnoreCase("null")) {
            String[] words = Operator.split(",");
            String[] words1;
            boolean isSelected = false;
            boolean isExempSelected = false;
            tv_operator.setText("" + words.length + " " + "Operator Selected");
            for (int i = 0; i < list_operator_id.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    if (Operator.contains(list_operator_id.get(i))) {
                        isSelected = true;
                        if (OperatorExempt != null && !OperatorExempt.isEmpty()
                                && !OperatorExempt.equalsIgnoreCase("null")) {
                            words1 = OperatorExempt.split(",");
                            isExempSelected = true;
                            tv_operator_exempt.setText("" + words1.length + " "
                                    + "Operator Exempted Selected");
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
        }

        if (Utils.isNetworkAvailable(AddTicket.this)) {
            All_user_Id.setLength(0);
            userList.clear();
            userCounter = 0;
            tv_grp_user.setText("Select User");
            list_user_id = new ArrayList<String>();
            list_userGRP = new ArrayList<String>();
            list_user_id.clear();
            list_userGRP.clear();
            GetAssignUserTask task = new GetAssignUserTask
                    (AddTicket.this, et_siteId.getText().toString(), groupId);
            task.execute();
        }
    }

    private int getCategoryPos(String category, ArrayList<String> list) {
        return list.indexOf(category);
    }

    public void OperatorpopUp() {
        tempOptExemptList = new ArrayList<Operator>();
        dialogOperator = new Dialog(AddTicket.this, R.style.FullHeightDialog);
        dialogOperator.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOperator.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogOperator.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogOperator.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogOperator.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
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
        apply.setTypeface(Utils.typeFace(AddTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogOperator.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddTicket.this));
        tv_header.setText("Operator");
        dataAdapter = new MyCustomAdapter(AddTicket.this, R.layout.custom_operator, operatorList, 1);
        list_view.setAdapter(dataAdapter);
    }

    public void operatorSelect() {
        optCounter = 0;
        All_Operator_Id.setLength(0); // operator id set is blank
        tempOptExemptList.clear();
        if (exemptOperatorList.size() > 0) {
            for (int a = 0; a < exemptOperatorList.size(); a++) {
                tempOptExemptList.add(exemptOperatorList.get(a));
            }
        }
        exemptOperatorList.clear(); // clear exempt operator list
        ArrayList<Operator> OperatorList = dataAdapter.countryList; // get
        for (int i = 0; i < OperatorList.size(); i++) {
            Operator operator = OperatorList.get(i);
            if (operator.isSelected()) {
                check = false;
                if (tempOptExemptList.size() > 0) {
                    for (int a = 0; a < tempOptExemptList.size(); a++) {
                        if (tempOptExemptList.get(a).getCode() == operator.getCode() && tempOptExemptList.get(a).isSelected()) {
                            check = true;
                        }
                    }
                }
                Operator exempt_oper = new Operator(operator.getCode(), operator.getName(), check);
                exemptOperatorList.add(exempt_oper);
                optCounter++;
                if (optCounter > 1) {
                    All_Operator_Id.append(",");
                }
                All_Operator_Id.append(operator.getCode());
            }
        }
        if (optCounter == 0) {
            tv_operator.setText("Select Operator");
        } else {
            tv_operator.setText("" + optCounter + " Operator Selected");
        }
        OperatorExemptPopUp(0);
    }

    public void operatorUser() {
        //String Operator = "1";
        String Operator = mAppPreferences.getUserSubCategory();
        if (Operator.equalsIgnoreCase("null") || Operator.isEmpty()
                || Operator == null) {
            Operator = "";
            for (int i = 0; i < list_operator_id.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    Operator operator = new Operator(list_operator_id.get(i),
                            list_operator_name.get(i), false);
                    operatorList.add(operator);
                }
            }
        }
        All_Operator_Id.append(Operator);
        if (Operator != null && !Operator.isEmpty()
                && !Operator.equalsIgnoreCase("null")) {
            String[] words = Operator.split(",");
            boolean isSelected = false;
            tv_operator.setText("" + words.length + " " + "Operator Selected");
            for (int i = 0; i < list_operator_id.size(); i++) {
                if (!list_operator_name.get(i)
                        .equalsIgnoreCase("Please Select")) {
                    if (Operator.contains(list_operator_id.get(i))) {
                        isSelected = true;
                        Operator operExemp = new Operator(
                                list_operator_id.get(i),
                                list_operator_name.get(i), false);
                        exemptOperatorList.add(operExemp);
                    } else {
                        isSelected = false;
                    }
                    Operator operator = new Operator(list_operator_id.get(i),
                            list_operator_name.get(i), isSelected);
                    operatorList.add(operator);
                }
            }
        }
    }

    // start 0.4
    public void OperatorExemptPopUp(int mode) {
        dialogOperatorExempt = new Dialog(AddTicket.this, R.style.FullHeightDialog);
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
        apply.setTypeface(Utils.typeFace(AddTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogOperatorExempt.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddTicket.this));
        tv_header.setText("Operator Exempted");
        dataAdapterExempt = new MyCustomAdapter(AddTicket.this, R.layout.custom_operator, exemptOperatorList, 2);
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
                tv_operator_exempt.setText("Select Operator Exempted");
            } else {
                tv_operator_exempt.setText("" + exemptOptCounter
                        + " Operator Exempted Selected");
            }
            dialogOperatorExempt.dismiss();
        }
    }

    public void operatorExemptedSelect() {
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
            tv_operator_exempt.setText("Select Operator Exempted");
        } else {
            tv_operator_exempt.setText("" + exemptOptCounter
                    + " Operator Exempted Selected");
        }

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            // updateTime(hour, minute,tv_prb_strTime);
            tv_prb_strTime.setText(Utils.updateTime(hour, minute));
        }
    };
    private TimePickerDialog.OnTimeSetListener timePickerListenerSIT = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            // updateTime(hour, minute,tv_prb_strTime);
            tv_serviceImpactStartTime.setText(Utils.updateTime(hour, minute));
        }
    };
    /**
     * Callback received when the user "picks" a date in the dialog
     */
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
                tv_prb_strDate.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(AddTicket.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(AddTicket.this, "151");
            }
        }
    };

    private DatePickerDialog.OnDateSetListener pDateSetListenerservice = new DatePickerDialog.OnDateSetListener() {
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
                // Toast.makeText(AddTicket.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(AddTicket.this, "151");
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListenerDisc = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hour = hourOfDay;
            minute = minutes;
            // updateTime(hour, minute,tv_bttry_time);
            tv_bttry_time.setText(Utils.updateTime(hour, minute));
        }
    };
    /**
     * Callback received when the user "picks" a date in the dialog
     */
    private DatePickerDialog.OnDateSetListener pDateSetListenerDisc = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkValidation(d)) {
                tv_bttry_Date.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(AddTicket.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
                Utils.toast(AddTicket.this, "151");
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, pDateSetListener, pYear, pMonth,
                        pDay);
            case DATE_DIALOG_SERVICE_ID:
                return new DatePickerDialog(this, pDateSetListenerservice, pYear, pMonth,
                        pDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        true);
            case DISC_DATE_DIALOG_ID:
                return new DatePickerDialog(this, pDateSetListenerDisc, pYear,
                        pMonth, pDay);
            case DISC_TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListenerDisc, hour,
                        minute, true);
//            timePickerListenerSIT
            case TIME_DIALOG_ID_SIT:
                return new TimePickerDialog(this, timePickerListenerSIT, hour,
                        minute, true);
        }
        return null;
    }

    private class MyCustomAdapter extends ArrayAdapter<Operator> {
        private ArrayList<Operator> countryList;
        int popupFlag = 0;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Operator> countryList, int popupFlag) {
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
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.name.setTypeface(Utils.typeFace(AddTicket.this));
                convertView.setTag(holder);
                if (tktTypeId.equalsIgnoreCase(ticketType)) {
                    holder.name.setChecked(true);
                }
                if (popupFlag == 1) {
                    if (tktTypeId.equalsIgnoreCase(ticketType)) {
                        // country.setSelected(t);
                    }
                }
                holder.name.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Operator country = (Operator) cb.getTag();
                        country.setSelected(cb.isChecked());
                        if (popupFlag == 1) {
                            if (tktTypeId.equalsIgnoreCase(ticketType)) {
                                countryList.get(2).setSelected(true);
                                country.setSelected(cb.isChecked());
                                // country.setSelected(t);
                            }
                        }
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
            if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")
                    && popupFlag == 1) {
                holder.name.setEnabled(false);
            }
            holder.name.setTag(country);
            return convertView;
        }
    }

    public void setMsg() {
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
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
            Utils.msgText(AddTicket.this, "529", tv_tickettype);
            Utils.msgText(AddTicket.this, "530", tv_alarm_desc);
            Utils.msgText(AddTicket.this, "531", tv_sererity);
            Utils.msgText(AddTicket.this, "532", tv_brand_logo);
            Utils.msgText(AddTicket.this, "533", tv_problemDesc);
            Utils.msgText(AddTicket.this, "534", tv_assigned);

            Utils.msgText(AddTicket.this, "535", tv_opco_siteId);
            Utils.msgText(AddTicket.this, "536", tv_site_visibility);
            Utils.msgText(AddTicket.this, "537", tv_ref_tkt);
            Utils.msgText(AddTicket.this, "538", tv_equipment);
            Utils.msgText(AddTicket.this, "539", tv_strDate);
            Utils.msgText(AddTicket.this, "540", tv_strTime);
            Utils.msgText(AddTicket.this, "541", tv_bttry_dschrg_Date);
            Utils.msgText(AddTicket.this, "542", tv_bttry_dschrg_time);
            Utils.msgText(AddTicket.this, "543", tv_alarmDetails);
            Utils.msgText(AddTicket.this, "544", tv_opera);
            Utils.msgText(AddTicket.this, "545", tv_opera_exempt);
            Utils.msgText(AddTicket.this, "546", tv_eff_site);
            Utils.msgText(AddTicket.this, "548", tv_chk_link);
        } else {
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

            Utils.msgText(AddTicket.this, "547", tv_chk_link);
            Utils.msgText(AddTicket.this, "73", tv_brand_logo);
            Utils.msgText(AddTicket.this, "82", tv_sererity);
            Utils.msgText(AddTicket.this, "83", tv_alarm_desc);
            Utils.msgText(AddTicket.this, "95", tv_tickettype);
            Utils.msgText(AddTicket.this, "96", tv_problemDesc);
            Utils.msgText(AddTicket.this, "109", tv_assigned);

            Utils.msgText(AddTicket.this, "92", tv_opco_siteId);
            Utils.msgText(AddTicket.this, "78", tv_site_visibility);
            Utils.msgText(AddTicket.this, "80", tv_ref_tkt);
            Utils.msgText(AddTicket.this, "81", tv_equipment);
            Utils.msgText(AddTicket.this, "84", tv_strDate);
            Utils.msgText(AddTicket.this, "85", tv_strTime);
            Utils.msgText(AddTicket.this, "87", tv_bttry_dschrg_Date);
            Utils.msgText(AddTicket.this, "88", tv_bttry_dschrg_time);
            Utils.msgText(AddTicket.this, "94", tv_alarmDetails);
            Utils.msgText(AddTicket.this, "110", tv_opera);
            Utils.msgText(AddTicket.this, "112", tv_opera_exempt);
            Utils.msgText(AddTicket.this, "114", tv_eff_site);
        }
        Utils.msgText(AddTicket.this, "77", tv_siteId);
        Utils.msgText(AddTicket.this, "116", txt_no_ticket);
        Utils.msgText(AddTicket.this, "115", tvSubmit);


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
                String imgname = et_siteId.getText().toString() + "-" + subString + "-" + System.currentTimeMillis() + imgCounter;
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
                //JSONObject allImgjsonObj = new JSONObject();
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

                        String abc = "" + et_siteId.getText().toString();

                        String waterMark = Utils.msg(AddTicket.this, "77") + "-" + abc + " " +
                                "\n" + Utils.msg(AddTicket.this, "809") + "-" + Imglatitude + " \n" + Utils.msg(AddTicket.this, "810") + "-" + Imglongitude + "" +
                                " \n" + Utils.msg(AddTicket.this, "829") + "-" + currTime + "\n" + Utils.msg(AddTicket.this, "828") + "-" + MSGTag;

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
                        //setImages(newfile.toString(),WorkFlowUtils.CurrentDateTime(),MSGTag,Imglatitude,Imglongitude);

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
								//public void ImageName(final String tag,final String Imglatitude1,final String Imglongitude1,final String mediaFile) {
								ImageName(MSGTag,Imglatitude,Imglongitude,fileType,1,""+df2.format(fileSize));
								return;
							}*/
                        }
                    }

                    new VideoCompressor(AddTicket.this, newfile, imgname
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
                    Toast.makeText(AddTicket.this, "Try again for capture photo", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
        }
    }

    public void ImageName(final String tag, final String Imglatitude1, final String Imglongitude1, final String mediaFile, int flag, String size) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(AddTicket.this, R.style.FullHeightDialog);
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
        title.setTypeface(Utils.typeFace(AddTicket.this));
        title.setText(Utils.msg(AddTicket.this, "228") + " " + tag);

        if (flag == 0 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(AddTicket.this, "228") + " " + tag + " " +
                    Utils.msg(AddTicket.this, "584").
                            replaceAll("max size", "" + mAppPreferences.getVideoUploadMaxSize()));
        } else if (flag == 1 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(AddTicket.this, "585").
                    replaceAll("actual size", size)
                    .replaceAll("max size", "" + mAppPreferences.getVideoUploadMaxSize()));
        } else {
            title.setText(Utils.msg(AddTicket.this, "228") + " " + tag + " " + Utils.msg(AddTicket.this, "165").
                    replaceAll("s", ""));
        }

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(AddTicket.this));
        positive.setText(Utils.msg(AddTicket.this, "7"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                imageCapture(tag, Imglatitude1, Imglongitude1, mediaFile);
            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(AddTicket.this));
        negative.setText(Utils.msg(AddTicket.this, "8"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

    public static String msg(Context context, String id) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        dbHelper.open();
        String message = dbHelper.getMessage(id);
        dbHelper.close();
        return message;
    }

    @Override
    public void onBackPressed() {
        backButtonAlert("291", "63", "64", 0);
    }

    public void backButtonAlert(String confirmID, String primaryBt, String secondaryBT, final int flag) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(AddTicket.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddTicket.this));
        positive.setTypeface(Utils.typeFace(AddTicket.this));
        negative.setTypeface(Utils.typeFace(AddTicket.this));
        title.setTypeface(Utils.typeFace(AddTicket.this));
        title.setText(Utils.msg(AddTicket.this, confirmID));
        // title.setText("Do you want to exit?");
        positive.setText(Utils.msg(AddTicket.this, primaryBt));
        negative.setText(Utils.msg(AddTicket.this, secondaryBT));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (flag == 1) {
                    submitData();
                } else {
                    if (AppPreferences.getRefMode() == 3){
                        mAppPreferences.setTTModuleSelection("655");
                        finish();
                    }else {
                        Intent i = new Intent(AddTicket.this, IncidentManagement.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (flag == 1) {
                    chkListLink();
                }
            }
        });
    }

    public void grpUser() {
        userCounter = 0;
        All_user_Id.setLength(0);
        tempOptExemptList = new ArrayList<Operator>();
        dialogUser = new Dialog(AddTicket.this, R.style.FullHeightDialog);
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
        apply.setTypeface(Utils.typeFace(AddTicket.this));
        apply.setVisibility(View.GONE);
        TextView tv_header = (TextView) dialogUser.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddTicket.this));
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


        // set selected operator in text view
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
				// set selected operator in text view
				if (userCounter == 0) {
					tv_grp_user.setText("Select User");
				} else {
					tv_grp_user.setText("" + userCounter + " Selected");
				}
				dialogUser.dismiss();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        mAppPreferences.setTrackMode(1);
    }

    public class AsynTaskService extends AsyncTask<String, Void, String> {
        public String res = "", data;
        public ProgressDialog progressDialog = null;
        Response response;

        public AsynTaskService(String data) {
            this.data = data;
            progressDialog = new ProgressDialog(AddTicket.this);
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }

        protected String doInBackground(String... urls) {
            try {
                res = Utils.httpMultipartBackground(urls[0], "TT", jsonArrStrImg.toString(), data, "", "",
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
                Utils.toastMsg(AddTicket.this, response.getMessage());
                if (response.getSuccess().equals("true")) {
                    if (AppPreferences.getRefMode() == 2) {
                        referenceTicketPopUp();
                    } else if (AppPreferences.getRefMode() == 3){
                        //mAppPreferences.setTTModuleSelection("655");
                        Intent i = new Intent(AddTicket.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        db.deleteAutoSaveChk(randomNo,"0");
                        mAppPreferences.setTTChklistMadatory(0);
                        //db.deleteActivityImages(randomNo);
                        Intent i = new Intent(AddTicket.this, IncidentManagement.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }

                }
            } else {
                Utils.toast(AddTicket.this, "13");
            }
        }
    }

    /*public class SiteDetailsTask extends AsyncTask<Void, Void, Void> {
        Context con;
        EnergyMetaList dataResponse;
        ProgressDialog pd;
        String lat = "1", lng = "2";

        public SiteDetailsTask(Context con, String lat, String lng) {
            this.con = con;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Searching Sites...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("module", "Energy"));
            nameValuePairs.add(new BasicNameValuePair("datatype", "27"));
            nameValuePairs.add(new BasicNameValuePair("userID", mAppPreferences.getUserId()));
            nameValuePairs.add(new BasicNameValuePair("lat", lat));
            nameValuePairs.add(new BasicNameValuePair("lng", lng));
            String url = "";
            if (moduleUrl.equalsIgnoreCase("0")) {
                url = mAppPreferences.getConfigIP() + WebMethods.url_GetMetadata;
            } else {
                url = moduleUrl + WebMethods.url_GetMetadata;
            }
            String response = Utils.httpPostRequest(con, url, nameValuePairs);
            Gson gson = new Gson();
            try {
                dataResponse = gson.fromJson(response, EnergyMetaList.class);
            } catch (Exception e) {
                e.printStackTrace();
                dataResponse = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (dataResponse != null) {
                if (dataResponse.getSites() != null && dataResponse.getSites().size() > 0) {
                    for (int i = 0; i < dataResponse.getSites().size(); i++) {
                        prePopulateSitelist.add(dataResponse.getSites().get(i).getSiteId());
                        lhm_opcoSite.put(dataResponse.getSites().get(i).getOpcoSiteId(),
                                dataResponse.getSites().get(i).getSiteId());
                        if (dataResponse.getSites().get(i).getOpcoSiteId().length() > 0
                                && dataResponse.getSites().get(i).getOpcoSiteId() != null) {
                            prePopulateOpcoSitelist.add(dataResponse.getSites().get(i).getOpcoSiteId());
                        }
                    }
                    ArrayAdapter<String> siteIdAdapter = new ArrayAdapter<String>(AddTicket.this, android.R.layout.select_dialog_item, prePopulateSitelist);
                    et_siteId.setAdapter(siteIdAdapter);// setting the adapter data into the AutoCompleteTextView
                    et_siteId.setThreshold(1);
                    et_siteId.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                            et_siteId.showDropDown();
                            et_siteId.requestFocus();
                            return false;
                        }
                    });

                    ArrayAdapter<String> opcoSiteIdadapter = new ArrayAdapter<String>(AddTicket.this, android.R.layout.select_dialog_item, prePopulateOpcoSitelist);
                    et_opco_siteId.setAdapter(opcoSiteIdadapter);// setting the adapter data into the AutoCompleteTextView
                    et_opco_siteId.setThreshold(1);
                    et_opco_siteId.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                            et_opco_siteId.showDropDown();
                            et_opco_siteId.requestFocus();
                            return false;
                        }
                    });
                }
            }
            super.onPostExecute(result);
        }
    }*/

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

    public int generateOTP() {
        int randomNumber;
        int range = 9;  // to generate a single number with this range, by default its 0..9
        int length = 4; // by default length is 4
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }

        randomNumber = Integer.parseInt(s);

        return randomNumber;
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
                nameValuePairs.add(new BasicNameValuePair("siteId", et_siteId.getText().toString().trim().toUpperCase()));
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
                    group();
                } catch (Exception e) {
                    list_group.clear();
                    list_group.add(0, "Select Assigned To");
                    group();
                }
            } else {

            }
            super.onPostExecute(result);
        }
    }

    public void group() {
        final ArrayAdapter<String> adapter;
        final Dialog actvity_dialog = new Dialog(AddTicket.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.searchable_list_dialog);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity(Gravity.CENTER);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Drawable d = new ColorDrawable(Color.parseColor("#CC000000"));
        actvity_dialog.getWindow().setBackgroundDrawable(d);
        actvity_dialog.show();
        ListView lv = (ListView) actvity_dialog.findViewById(R.id.listItems);
        Button close = (Button) actvity_dialog.findViewById(R.id.btn_close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.dismiss();
            }
        });
        SearchView search = (SearchView) actvity_dialog.findViewById(R.id.search);
        search.setVisibility(View.GONE);
        EditText inputSearch = (EditText) actvity_dialog.findViewById(R.id.search1);
        inputSearch.setVisibility(View.VISIBLE);
        inputSearch.setFocusable(true);
        // Adding items to listview
        adapter = new ArrayAdapter(AddTicket.this, R.layout.spinner_search_text, list_group) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(AddTicket.this));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(AddTicket.this));
                v.setPadding(10, 15, 10, 15);
                return v;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        lv.setAdapter(adapter);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvity_dialog.dismiss();
                String selectedItem = (String) parent.getItemAtPosition(position);
                groupId = db.getGroupId(selectedItem.trim(), "654");
                All_user_Id.setLength(0);
                userList.clear();
                userCounter = 0;
                tv_grp_user.setText("Select User");
                list_user_id = new ArrayList<String>();
                list_userGRP = new ArrayList<String>();
                list_user_id.clear();
                list_userGRP.clear();
                tv_grp.setText(selectedItem);
                if (Utils.isNetworkAvailable(AddTicket.this)) {
                    GetAssignUserTask task = new GetAssignUserTask
                            (AddTicket.this, et_siteId.getText().toString(), groupId);
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
            }
        });
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
                nameValuePairs.add(new BasicNameValuePair("siteId", siteId.trim().toUpperCase()));
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
        if (!tv_grp.getText().toString().equalsIgnoreCase("Select Assigned To")) {
            if (asgnToUid != null && !asgnToUid.isEmpty()) {
                assignUserIdTT = asgnToUid.split(",");
                tv_grp_user.setText("" + assignUserIdTT.length + " " + "Selected");
            }
            boolean isUserSelected = false;
            for (int i = 0; i < list_user_id.size(); i++) {
                if (asgnToUid != null && !asgnToUid.isEmpty() && asgnToUid.contains(list_user_id.get(i))) {
                    isUserSelected = true;
                } else {
                    isUserSelected = false;
                }
                Operator UnSelectuser = new Operator(list_user_id.get(i), list_userGRP.get(i), isUserSelected);
                userList.add(UnSelectuser);
            }
        }

        dataAdapterUser = new MyCustomAdapter(AddTicket.this, R.layout.custom_operator, userList, 3);
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

    // Class to call Web Service to get PM CheckList to draw form.
    private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        BeanCheckListDetails PMCheckList;

        private GetPMCheckList(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Gson gson = new Gson();
            nameValuePairs.add(new BasicNameValuePair("siteId", ""));
            nameValuePairs.add(new BasicNameValuePair("checkListType", "0")); // 0 means all checklist(20001,20002,20005...) data download
            nameValuePairs.add(new BasicNameValuePair("checkListDate", ""));
            nameValuePairs.add(new BasicNameValuePair("status", "S")); //S or M get blank checklistdata
            nameValuePairs.add(new BasicNameValuePair("dgType", ""));
            nameValuePairs.add(new BasicNameValuePair("languageCode", mAppPreferences.getLanCode()));
            String url = "";
            try {
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                PMCheckList = gson.fromJson(res, BeanCheckListDetails.class);
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
                Utils.toast(AddTicket.this, "226");
            } else if (PMCheckList != null) {
                if (PMCheckList.getPMCheckListDetail() != null && PMCheckList.getPMCheckListDetail().size() > 0) {
                    //DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                    db.clearCheckListPMForm(mAppPreferences.getTTModuleSelection());
                    db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),
                            mAppPreferences.getTTModuleSelection(),
                            0, "", AddTicket.this,"0","0");
                    db.dataTS(null, null, "20",
                            db.getLoginTimeStmp("20", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
            } else {
                Utils.toast(AddTicket.this, "13");
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    private String pmCheckListDataType() {
        String DataType_Str = "1";
        String i = Utils.CompareDates(db.getSaveTimeStmp("20", mAppPreferences.getTTModuleSelection()),
                db.getLoginTimeStmp("20", mAppPreferences.getTTModuleSelection()), "20");
        if (i != "1") {
            DataType_Str = i;
        }
        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }

	/*public void createFolder(String fname) {
		String myfolder = Environment.getExternalStorageDirectory() + fname;
		File f = new File( myfolder );
		if (!f.exists())
			if (!f.mkdir()) {
			}
	}*/


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

        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(lhmImages, AddTicket.this);
        grid.setAdapter(horizontalAdapter);

        RecyclerView.LayoutManager mLayoutManager =
                new GridLayoutManager(this, imgCounter, GridLayoutManager.HORIZONTAL, false);
        grid.setLayoutManager(mLayoutManager);
        //ImageAdapter64 adapter = new ImageAdapter64(AddTicket.this, lhmImages);
        //grid.setFastScrollEnabled(true);
        //grid.setAdapter(adapter);
        //grid.setExpanded(true);
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
                    holder.tv_tag.setText(Utils.msg(AddTicket.this, "473") + " " + imageList.get(position).getName());
                } else {
                    holder.tv_tag.setText(Utils.msg(AddTicket.this, "473") + " ");
                }

                if (imageList.get(position).getTimeStamp() != null) {
                    holder.tv_time_stamp.setText(Utils.msg(AddTicket.this, "474") + " " + imageList.get(position).getTimeStamp());
                } else {
                    holder.tv_time_stamp.setText(Utils.msg(AddTicket.this, "474") + " ");
                }

                if (imageList.get(position).getLati() != null
                        && !imageList.get(position).getLati().equalsIgnoreCase("DefaultLatitude")) {
                    holder.tv_lati.setText(Utils.msg(AddTicket.this, "215") + " : " + imageList.get(position).getLati());
                } else {
                    holder.tv_lati.setText(Utils.msg(AddTicket.this, "215") + " : ");
                }

                if (imageList.get(position).getLongi() != null
                        && !imageList.get(position).getLongi().equalsIgnoreCase("DefaultLongitude")) {
                    holder.tv_longi.setText(Utils.msg(AddTicket.this, "216") + " : " + imageList.get(position).getLongi());
                } else {
                    holder.tv_longi.setText(Utils.msg(AddTicket.this, "216") + " : ");
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
            final Dialog nagDialog = new Dialog(AddTicket.this,
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
            Intent i = new Intent(AddTicket.this, ViewVideoVideoView.class);
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
            } catch (Exception e) {
                //String s = e.getMessage();
                Toast.makeText(AddTicket.this, "Try again for capture photo", Toast.LENGTH_LONG).show();
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    public void callPriorityService(String siteId,String severity,String serviceImpact)
    {
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<ResponseBody> call = request.getPriorityData(siteId,severity,serviceImpact);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String response1 = response.body().string();
                        JSONArray jsonArray = new JSONArray(response1);
                        if (jsonArray.length() != 0) {
                            priorityName = jsonArray.getJSONObject(0).getString("PRIORITY_NAME");
                            priorityId = jsonArray.getJSONObject(0).getString("PRIORITY_ID");
                            ll_priority.setVisibility(View.VISIBLE);
                            tv_priority1.setText(priorityName);
                        } else {
                            ll_priority.setVisibility(View.VISIBLE);
                            tv_priority1.setText("");
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

//	private MultiSelectControl.MultiSpinnerListener onSelectedListener = new MultiSelectControl.MultiSpinnerListener() {
//
//		public void onItemsSelected(boolean[] selected) {
//			// Do something here with the selected items
//
//			StringBuilder builder = new StringBuilder();
//
//			for (int i = 0; i < selected.length; i++) {
//				if (selected[i]) {
//					builder.append(multiAdapter.getItem(i)).append(" ");
//				}
//			}
//
//			Toast.makeText(AddTicket.this, builder.toString(), Toast.LENGTH_SHORT).show();
//		}
//	};

    private void retrieveData(Editable s) {

    }

    public class GetSites extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String searchable;
        ArrayList<DropdownValue> ddValues = null;
        public GetSites(Context con,String searchable) {
            this.con = con;
            this.searchable = searchable;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url ="";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(17);
                nameValuePairs.add(new BasicNameValuePair(HsseConstant.TKT_ID,this.searchable));

                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_get_siteDetails;
                } else {
                    url = moduleUrl + WebMethods.url_get_siteDetails;
                }

                //url=AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.requestAuditTrailHSSE;
                Type listType = new TypeToken<List<AuditTrail>>() {}.getType();
                String response = HttpUtils.httpGetRequest(url, nameValuePairs);
                Gson gson = new Gson();
                ddValues = gson.fromJson(response, listType);
                //auditTrailList = gson.fromJson(response,listType);
            } catch (Exception e) {
                e.printStackTrace();
                //auditTrailList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            /*if (auditTrailList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (auditTrailList!= null && auditTrailList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                dataLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new AuditLogAdapter(getActivity(),auditTrailList,null,1));
            } else {
                msgLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                tvMsg.setText("No data found.");
            }*/
            super.onPostExecute(result);
        }
    }
}
