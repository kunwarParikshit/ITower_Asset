package com.isl.preventive;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.api.RetrofitApiClient;
import com.isl.api.IApiRequest;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.GPSTracker;
import com.isl.modal.EnergyMetaList;
import com.isl.modal.Operator;
import com.isl.modal.PmFieldData;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import infozech.itower.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPMScheduling extends Activity {
    AppPreferences mAppPreferences;
    Button btn_back;
    LinearLayout ll_dg_type;
    AutoCompleteTextView et_siteId;
    String latitude = "", longitude = "", reviewGrpId = "", assgnGrpId = "", asgnToUid = "";
    private int hour, minute, pYear, pMonth, pDay;
    TextView tv_activity_type, tv_dg_type, tv_assigngroup, tv_assigned, tv_review_group, tv_review_user, tv_review_pln_date,
            tv_reviewplndate, tv_schedule_date, tv_scheduledate, tv_grp_user, tv_reviewuser, tv_brand_logo, tv_siteId, tv_no_of_pm_visits, tv_site_auto_access, tv_max_site_access_duration,
            tv_no_of_pm_visits_audit, tv_site_auto_access_audit, tv_max_site_access_duration_audit, tvSubmit;
    EditText et_no_of_pm_visits, et_max_site_access_duration, et_no_of_pm_visits_audit, et_max_site_access_duration_audit;
    Spinner sp_activity_type, sp_dg_type, sp_assign_group, sp_review_group, sp_site_auto_access, sp_site_auto_access_audit;
    ArrayList<String> list_activity, list_dg, list_site_auto_access, list_site_auto_access_audit, list_group1, list_group2, list_user_id, list_userGRP;
    DataBaseHelper db;
    Date d;
    static final int REVIEW_DATE_DIALOG = 1111, SCHEDULE_DIALOG_ID = 0;
    StringBuffer All_Operator_Id, all_exempt_opt_id, All_assign_user, All_review_user;
    ArrayList<Operator> operatorList, exemptOperatorList, assignUserList, reviewUserList, assignList, reviewList;
    int reviewCounter = 0, assignUserCounter = 0;
    String[] assignUserIdTT;
    MyCustomAdapter dataAdapterAssignUser = null, dataAdapterReviewUser = null;
    Dialog dialogUser;
    String moduleUrl = "";
    String url = "";
    int inital_flag = 0;
    int inital_flag1 = 0;
    //list by Avdhesh
    List<PmFieldData> pmFieldDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.add_activity);
            InitilizeControllerId();
            setMsg();
            setDropdownList();
            //call Role Api by Avdhesh
            setManualFildDisble();
            callAuditApi();
           // if (getPackageName().equalsIgnoreCase("tawal.com.sa")) {
           //     callAuditApi();
            //} //else {
             //   setManualFildEnable();
           // }


         /*   GetActivityRole task=new GetActivityRole(AddPMScheduling.this,"0","A");
            task.execute();*/
            GPSTracker gps = new GPSTracker(AddPMScheduling.this);
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
                    if (Utils.isNetworkAvailable(AddPMScheduling.this)) {
                        new SiteDetailsTask(AddPMScheduling.this, latitude, longitude).execute();
                    }
                }
            }
            final Calendar cal = Calendar.getInstance();
            pYear = cal.get(Calendar.YEAR);
            pMonth = cal.get(Calendar.MONTH);
            pDay = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            tv_scheduledate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(SCHEDULE_DIALOG_ID);
                }
            });

            tv_reviewplndate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showDialog(REVIEW_DATE_DIALOG);
                }
            });

            sp_assign_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    if (et_siteId.getText().toString().length() == 0 && inital_flag > 0
                            && !sp_assign_group.getSelectedItem().toString().equalsIgnoreCase("Select Assigned To")) {
                        Utils.toast(AddPMScheduling.this, "152"); // Site Id cannot be blank.
                        tv_siteId.clearFocus();
                        tv_siteId.requestFocus();
                        addItemsOnSpinner(sp_assign_group, list_group1);
                        return;
                    }

                    assgnGrpId = db.getGroupId(sp_assign_group.getSelectedItem().toString().trim(), "654");
                    All_assign_user.setLength(0);
                    assignList.clear();
                    assignUserCounter = 0;
                    tv_grp_user.setText("Select User");
                    list_user_id = new ArrayList<String>();
                    list_userGRP = new ArrayList<String>();
                    list_user_id.clear();
                    list_userGRP.clear();
                    inital_flag = 1;
                    if (!sp_assign_group.getSelectedItem().toString().trim().equalsIgnoreCase("Select Assigned To")) {
                        GetAssignUserTask task = new GetAssignUserTask(AddPMScheduling.this, et_siteId.getText().toString(), assgnGrpId, "A");
                        task.execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            tv_grp_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (sp_assign_group.getSelectedItem().toString().trim().equals("Select Assigned To")) {
                        //Utils.toast(AddPMScheduling.this, "138");// Select Assigned To
                        Toast.makeText(AddPMScheduling.this, "Select Assigned Group", Toast.LENGTH_SHORT).show();
                        tv_assigned.clearFocus();
                        tv_assigned.requestFocus();
                    } else {
                        AssingGrpUser();
                    }

                }
            });

            sp_review_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (et_siteId.getText().toString().length() == 0 && inital_flag1 > 0
                            && !sp_review_group.getSelectedItem().toString().equalsIgnoreCase("Select Review Group")) {
                        Utils.toast(AddPMScheduling.this, "152");
                        tv_siteId.clearFocus();
                        tv_siteId.requestFocus();
                        addItemsOnSpinner(sp_review_group, list_group2);
                        return;
                    }

                    reviewGrpId = db.getGroupId(sp_review_group.getSelectedItem().toString().trim(), "654");
                    All_review_user.setLength(0);
                    reviewList.clear();
                    reviewCounter = 0;
                    tv_reviewuser.setText("Select User");
                    list_user_id = new ArrayList<String>();
                    list_userGRP = new ArrayList<String>();
                    list_user_id.clear();
                    list_userGRP.clear();
                    inital_flag1 = 1;

                    if (!sp_review_group.getSelectedItem().toString().trim().equalsIgnoreCase("Select Review Group")) {
                        GetAssignUserTask task = new GetAssignUserTask(AddPMScheduling.this, et_siteId.getText().toString(), reviewGrpId, "R");
                        task.execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            tv_reviewuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (sp_review_group.getSelectedItem().toString().trim().equals("Select Review Group")) {
                        //Utils.toast(AddPMScheduling.this, "138");// Select Assigned To
                        Toast.makeText(AddPMScheduling.this, "Select Review Group", Toast.LENGTH_SHORT).show();
                        tv_review_group.clearFocus();
                        tv_review_group.requestFocus();
                    } else {
                        ReviewGrpUser();
                    }

                }
            });

            sp_activity_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String paramDesc = db.getInciParamDesc("20", sp_activity_type.getSelectedItem().toString(), "654");
                    if (paramDesc.indexOf('A') > -1 || paramDesc.equalsIgnoreCase("R")) {
                        ll_dg_type.setVisibility(View.GONE);
                    } else {
                        addItemsOnSpinner(sp_dg_type, list_dg);
                        ll_dg_type.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validate()) {
                        AddActivityTask task = new AddActivityTask(AddPMScheduling.this);
                        task.execute();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void AssignUser() {
        if (!sp_assign_group.getSelectedItem().toString().equalsIgnoreCase("Select Assigned To")) {
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
                assignList.add(UnSelectuser);
            }
        }

        dataAdapterAssignUser = new MyCustomAdapter(AddPMScheduling.this, R.layout.custom_operator, assignList);
        if (dataAdapterAssignUser != null) {
            ArrayList<Operator> OptList = dataAdapterAssignUser.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    assignUserCounter++;
                    if (assignUserCounter > 1) {
                        All_assign_user.append(",");
                    }
                    All_assign_user.append(opt.getCode());
                }
            }
        }
        if (assignUserCounter == 0) {
            tv_grp_user.setText("Select User");
        } else {
            tv_grp_user.setText("" + assignUserCounter + " Selected");
        }
    }

    public void ReviewUser() {
        if (!sp_review_group.getSelectedItem().toString().equalsIgnoreCase("Select Review Group")) {
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
                reviewList.add(UnSelectuser);
            }
        }

        dataAdapterReviewUser = new MyCustomAdapter(AddPMScheduling.this, R.layout.custom_operator, reviewList);
        if (dataAdapterReviewUser != null) {
            ArrayList<Operator> OptList = dataAdapterReviewUser.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    reviewCounter++;
                    if (reviewCounter > 1) {
                        All_review_user.append(",");
                    }
                    All_review_user.append(opt.getCode());
                }
            }
        }
        if (reviewCounter == 0) {
            tv_reviewuser.setText("Select User");
        } else {
            tv_reviewuser.setText("" + reviewCounter + " Selected");
        }
    }

    public int etSum(EditText et) {
        int sum1 = 0, sum2 = 0, sum = 0;
        int digits = Integer.parseInt(et.getText().toString().trim());
        while (digits != 0) {
            int lastdigit = digits % 10;
            sum += lastdigit;
            digits /= 10;
        }
        return sum;
    }

    public class AddActivityTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        //Response response;
        String res;

        public AddActivityTask(Context con) {
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(13);
                nameValuePairs.add(new BasicNameValuePair("tranId", ""));
                nameValuePairs.add(new BasicNameValuePair("siteId", et_siteId.getText().toString().trim().toUpperCase()));
                nameValuePairs.add(new BasicNameValuePair("actType", db.getInciParamId("20", sp_activity_type.getSelectedItem().toString(), "654")));
                nameValuePairs.add(new BasicNameValuePair("scheduleDate", tv_scheduledate.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("assgnUser", All_assign_user.toString()));
                nameValuePairs.add(new BasicNameValuePair("assgnGrp", assgnGrpId));
                nameValuePairs.add(new BasicNameValuePair("reviewUser", All_review_user.toString()));
                if (!sp_review_group.getSelectedItem().toString().trim().equalsIgnoreCase("Select Review Group")) {
                    nameValuePairs.add(new BasicNameValuePair("reviewGrp", reviewGrpId));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("reviewGrp", ""));
                }
                nameValuePairs.add(new BasicNameValuePair("flag", "A"));
                if (ll_dg_type.getVisibility() == View.VISIBLE) {
                    nameValuePairs.add(new BasicNameValuePair("dgType", db.getAssetDgId(sp_dg_type.getSelectedItem().toString())));
                } else {
                    nameValuePairs.add(new BasicNameValuePair("dgType", "0"));
                }
                nameValuePairs.add(new BasicNameValuePair("userId", mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("pmStatus", ""));
                nameValuePairs.add(new BasicNameValuePair("reviewDate", tv_reviewplndate.getText().toString()));

                String max_duration = "";
                if (et_max_site_access_duration.getText().toString().length() > 0) {
                    max_duration = et_max_site_access_duration.getText().toString().trim();
                }
                nameValuePairs.add(new BasicNameValuePair("maxAccessDuration", max_duration));

                String no_of_pmVisit = "";
                if (et_no_of_pm_visits.getText().toString().length() > 0) {
                    no_of_pmVisit = et_no_of_pm_visits.getText().toString().trim();
                }
                nameValuePairs.add(new BasicNameValuePair("no_of_pm_visit", no_of_pmVisit));

                String auto_access = "";
                if (!sp_site_auto_access.getSelectedItem().toString().equalsIgnoreCase("Select " +
                        tv_site_auto_access.getText().toString())) {
                    auto_access = db.getInciParamDesc("171",
                            sp_site_auto_access.getSelectedItem().toString(), "654");
                }
                nameValuePairs.add(new BasicNameValuePair("siteAutoAccess", "" + auto_access));


                String max_duration_audit = "";
                if (et_max_site_access_duration_audit.getText().toString().length() > 0) {
                    max_duration_audit = et_max_site_access_duration_audit.getText().toString().trim();
                }
                nameValuePairs.add(new BasicNameValuePair("maxAccessDuration_audit", max_duration_audit));

                String no_of_pmVisit_audit = "";
                if (et_no_of_pm_visits_audit.getText().toString().length() > 0) {
                    no_of_pmVisit_audit = et_no_of_pm_visits_audit.getText().toString().trim();
                }
                nameValuePairs.add(new BasicNameValuePair("no_of_pm_visit_audit", no_of_pmVisit_audit));

                String auto_access_audit = "";
                if (!sp_site_auto_access_audit.getSelectedItem().toString().equalsIgnoreCase("Select " +
                        tv_site_auto_access_audit.getText().toString())) {
                    auto_access_audit = db.getInciParamDesc("171",
                            sp_site_auto_access_audit.getSelectedItem().toString(), "654");
                }
                nameValuePairs.add(new BasicNameValuePair("siteAutoAccess_audit", "" + auto_access_audit));

                nameValuePairs.add(new BasicNameValuePair("gensetStatus", ""));
                nameValuePairs.add(new BasicNameValuePair("pullDate", ""));
                nameValuePairs.add(new BasicNameValuePair("overallDate", ""));


                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_add_activity;
                } else {
                    url = moduleUrl + WebMethods.url_add_activity;
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
            String success = "";
            String msg = "";
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (res != null && res.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("response");
                    for (int i = 0; i < subArray.length(); i++) {
                        success = subArray.getJSONObject(i).getString("success");
                        msg = subArray.getJSONObject(i).getString("message");
                    }
                    Toast.makeText(AddPMScheduling.this, msg, Toast.LENGTH_SHORT).show();
                    if (success.equals("true")) {
                        Intent i = new Intent(AddPMScheduling.this, PMTabs.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {

                    }
                } catch (Exception e) {

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
        String assignFlag = "";
        String siteId = "", grpId = "";

        public GetAssignUserTask(Context con, String siteId, String grpId, String flag) {
            this.con = con;
            this.siteId = siteId;
            this.grpId = grpId;
            this.assignFlag = flag;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("grpId", grpId));
                nameValuePairs.add(new BasicNameValuePair("siteId", siteId.trim().toUpperCase()));
                moduleUrl = db.getModuleIP("Preventive");
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_get_assign_user;
                } else {
                    url = moduleUrl + "/" + WebMethods.url_get_assign_user;
                }
                res = Utils.httpPostRequest(con, url, nameValuePairs);
                //res = res.replace("[", "").replace("]", "");
                res = "{response :" + res + "}";
                //Gson gson = new Gson();
                //response = gson.fromJson(res,Response.class);
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
                    if (assignFlag == "A") {
                        AssignUser();
                    } else {
                        ReviewUser();
                    }
                } catch (Exception e) {

                }

            } else {

            }
            super.onPostExecute(result);
        }
    }

    //Api for get Role with retrofit
    private void callAuditApi() {
        ProgressDialog progressDialog = new ProgressDialog(AddPMScheduling.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<List<PmFieldData>> call = request.GetManualPMFieldconfigData(Integer.parseInt(mAppPreferences.getRoleId()), "A", 326);
        call.enqueue(new Callback<List<PmFieldData>>() {
            @Override
            public void onResponse(Call<List<PmFieldData>> call, Response<List<PmFieldData>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //  Toast.makeText(AddPMScheduling.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null && response.body().size()>0) {
                        pmFieldDataList = response.body();
                        if (pmFieldDataList!=null && pmFieldDataList.size()>0
                                && pmFieldDataList.get(0).getFieldKey() != null
                                && pmFieldDataList.get(0).getFieldVisibility()!=null
                                && pmFieldDataList.get(0).getFieldVisibility().equalsIgnoreCase("E")) {
                            StringSplit(pmFieldDataList.get(0).getFieldKey());
                        } else {
                            setManualFildDisble();
                        }
                } else {
                    setManualFildEnable();
                }

            }

            @Override
            public void onFailure(Call<List<PmFieldData>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
                setManualFildEnable();
            }
        });
    }

    public boolean validate() {
        if (et_siteId.getText().toString().length() == 0) {
            Utils.toast(AddPMScheduling.this, "152"); // Site Id cannot be blank.
            tv_siteId.clearFocus();
            tv_siteId.requestFocus();
            return false;
        }
        if (sp_activity_type.getSelectedItem().toString().equals("Select Activity Type")) {
            Toast.makeText(AddPMScheduling.this, "Select Activity Type", Toast.LENGTH_SHORT).show();
            tv_activity_type.clearFocus();
            tv_activity_type.requestFocus();
            return false;
        }
        if (ll_dg_type.getVisibility() == View.VISIBLE) {
            if (sp_dg_type.getSelectedItem().toString().equals("Select DG Type")) {
                Toast.makeText(AddPMScheduling.this, "Select DG Type", Toast.LENGTH_SHORT).show();
                tv_dg_type.clearFocus();
                tv_dg_type.requestFocus();
                return false;
            }
        }
        if (sp_assign_group.getSelectedItem().toString().equals("Select Assigned To")) {
            Toast.makeText(AddPMScheduling.this, "Select Assigned Group", Toast.LENGTH_SHORT).show();
            tv_assigngroup.clearFocus();
            tv_assigngroup.requestFocus();
            return false;
        }
        if (tv_scheduledate.getText().toString().length() == 0) {
            Toast.makeText(AddPMScheduling.this, "Select Scheduled Date", Toast.LENGTH_SHORT).show();
            tv_assigngroup.clearFocus();
            tv_assigngroup.requestFocus();
            return false;
        }

        if (et_no_of_pm_visits.getText().toString().length() > 0 && etSum(et_no_of_pm_visits) == 0) {
            Toast.makeText(AddPMScheduling.this, tv_no_of_pm_visits.getText().toString() +
                    " cannot be 0", Toast.LENGTH_SHORT).show();
            tv_no_of_pm_visits.clearFocus();
            tv_no_of_pm_visits.requestFocus();
            return false;
        }

        if (et_max_site_access_duration.getText().toString().trim().length() > 0 && etSum(et_max_site_access_duration) == 0) {
            Toast.makeText(AddPMScheduling.this, tv_max_site_access_duration.getText().toString() +
                    " cannot be 0", Toast.LENGTH_SHORT).show();
            tv_max_site_access_duration.clearFocus();
            tv_max_site_access_duration.requestFocus();
            return false;
        }

        if (et_no_of_pm_visits_audit.getText().toString().length() > 0 && etSum(et_no_of_pm_visits_audit) == 0) {
            Toast.makeText(AddPMScheduling.this, tv_no_of_pm_visits_audit.getText().toString() +
                    " cannot be 0", Toast.LENGTH_SHORT).show();
            tv_no_of_pm_visits_audit.clearFocus();
            tv_no_of_pm_visits_audit.requestFocus();
            return false;
        }

        if (et_max_site_access_duration_audit.getText().toString().trim().length() > 0 && etSum(et_max_site_access_duration_audit) == 0) {
            Toast.makeText(AddPMScheduling.this, tv_max_site_access_duration_audit.getText().toString() +
                    " cannot be 0", Toast.LENGTH_SHORT).show();
            tv_max_site_access_duration_audit.clearFocus();
            tv_max_site_access_duration_audit.requestFocus();
            return false;
        }

        return true;
    }

    public void AssingGrpUser() {
        assignUserCounter = 0;
        All_assign_user.setLength(0);
        assignUserList = new ArrayList<Operator>();
        dialogUser = new Dialog(AddPMScheduling.this, R.style.FullHeightDialog);
        dialogUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUser.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogUser.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogUser.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogUser.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogUser.show();

        ImageView iv_cancel = (ImageView) dialogUser.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialogUser.cancel();
            }
        });

        final ListView list_view = (ListView) dialogUser.findViewById(R.id.list_view);
        TextView apply = (TextView) dialogUser.findViewById(R.id.tv_apply);
        apply.setTypeface(Utils.typeFace(AddPMScheduling.this));
        TextView tv_header = (TextView) dialogUser.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddPMScheduling.this));
        tv_header.setText("User(s)");
        list_view.setAdapter(dataAdapterAssignUser);
        if (dataAdapterAssignUser != null) {
            ArrayList<Operator> OptList = dataAdapterAssignUser.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    assignUserCounter++;
                    if (assignUserCounter > 1) {
                        All_assign_user.append(",");
                    }
                    All_assign_user.append(opt.getCode());
                }
            }

        }
        // set selected operator in text view
        if (assignUserCounter == 0) {
            tv_grp_user.setText("Select User");
        } else {
            tv_grp_user.setText("" + assignUserCounter + " Selected");
        }

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<Operator> OptList = dataAdapterAssignUser.countryList;
                All_assign_user.setLength(0);
                assignUserCounter = 0;
                for (int i = 0; i < OptList.size(); i++) {
                    Operator opt = OptList.get(i);
                    if (opt.isSelected()) {
                        assignUserCounter++;
                        if (assignUserCounter > 1) {
                            All_assign_user.append(",");
                        }
                        All_assign_user.append(opt.getCode());
                    }
                }
                // set selected operator in text view
                if (assignUserCounter == 0) {
                    tv_grp_user.setText("Select User");
                } else {
                    tv_grp_user.setText("" + assignUserCounter + " Selected");
                }
                dialogUser.dismiss();
            }
        });
    }

    public void ReviewGrpUser() {
        reviewCounter = 0;
        All_review_user.setLength(0);
        reviewUserList = new ArrayList<Operator>();
        dialogUser = new Dialog(AddPMScheduling.this, R.style.FullHeightDialog);
        dialogUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUser.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialogUser.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogUser.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = dialogUser.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogUser.show();

        ImageView iv_cancel = (ImageView) dialogUser.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialogUser.cancel();
            }
        });

        final ListView list_view = (ListView) dialogUser.findViewById(R.id.list_view);
        TextView apply = (TextView) dialogUser.findViewById(R.id.tv_apply);
        apply.setTypeface(Utils.typeFace(AddPMScheduling.this));
        TextView tv_header = (TextView) dialogUser.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AddPMScheduling.this));
        tv_header.setText("User(s)");
        list_view.setAdapter(dataAdapterReviewUser);
        if (dataAdapterReviewUser != null) {
            ArrayList<Operator> OptList = dataAdapterReviewUser.countryList;
            for (int i = 0; i < OptList.size(); i++) {
                Operator opt = OptList.get(i);
                if (opt.isSelected()) {
                    reviewCounter++;
                    if (reviewCounter > 1) {
                        All_review_user.append(",");
                    }
                    All_review_user.append(opt.getCode());
                }
            }
        }
        // set selected operator in text view
        if (reviewCounter == 0) {
            tv_reviewuser.setText("Select User");
        } else {
            tv_reviewuser.setText("" + reviewCounter + " Selected");
        }

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<Operator> OptList = dataAdapterReviewUser.countryList;
                All_review_user.setLength(0);
                reviewCounter = 0;
                for (int i = 0; i < OptList.size(); i++) {
                    Operator opt = OptList.get(i);
                    if (opt.isSelected()) {
                        reviewCounter++;
                        if (reviewCounter > 1) {
                            All_review_user.append(",");
                        }
                        All_review_user.append(opt.getCode());
                    }
                }
                // set selected operator in text view
                if (reviewCounter == 0) {
                    tv_reviewuser.setText("Select User");
                } else {
                    tv_reviewuser.setText("" + reviewCounter + " Selected");
                }
                dialogUser.dismiss();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener pDateScheduleListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkBeforeValidation(d)) {
                //Condation to perivous or new funcationlaty on calnder
                    // if (pmFieldDataList.size()!=0){
                    if (Utils.checkAfterValidation(pMonth,pYear, AddPMScheduling.this).equalsIgnoreCase("0")) {
                        tv_scheduledate.setText(Utils.changeDateFormat(
                                new StringBuilder().append(pMonth + 1).append("/")
                                        .append(pDay).append("/").append(pYear)
                                        .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
                    } else {
                        Toast.makeText(AddPMScheduling.this, "You cannot select Month and year greater than "+Utils.checkAfterValidation(pMonth,pYear, AddPMScheduling.this), Toast.LENGTH_LONG).show();
                    }
            } else {
                // Toast.makeText(AddTicket.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
                Toast.makeText(AddPMScheduling.this, "You cannot select date less than current date", Toast.LENGTH_LONG).show();
                //Utils.toast(AddPMScheduling.this, "151");
            }
        }
    };

    private DatePickerDialog.OnDateSetListener pDateReviewListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            d = Utils.convertStringToDate(new StringBuilder()
                    .append(pMonth + 1).append("/").append(pDay).append("/")
                    .append(pYear).toString(), "MM/dd/yyyy");
            if (Utils.checkBeforeValidation(d)) {
                tv_reviewplndate.setText(Utils.changeDateFormat(
                        new StringBuilder().append(pMonth + 1).append("/")
                                .append(pDay).append("/").append(pYear)
                                .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
            } else {
                // Toast.makeText(AddTicket.this,"You cannot select date greater than system date",Toast.LENGTH_LONG).show();
                Toast.makeText(AddPMScheduling.this, "You cannot select date less than current date", Toast.LENGTH_LONG).show();
                //Utils.toast(AddPMScheduling.this, "151");
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case SCHEDULE_DIALOG_ID:
                return new DatePickerDialog(this, pDateScheduleListener, pYear, pMonth,
                        pDay);
            case REVIEW_DATE_DIALOG:
                return new DatePickerDialog(this, pDateReviewListener, pYear, pMonth,
                        pDay);
        }
        return null;
    }

    private void setMsg() {
        Utils.msgButton(AddPMScheduling.this, "71", btn_back);
        Utils.msgText(AddPMScheduling.this, "77", tv_siteId);
        Utils.msgText(AddPMScheduling.this, "523", tv_brand_logo);
        Utils.msgText(AddPMScheduling.this, "516", tv_activity_type);
        Utils.msgText(AddPMScheduling.this, "168", tv_dg_type);
        Utils.msgText(AddPMScheduling.this, "517", tv_assigngroup);
        Utils.msgText(AddPMScheduling.this, "518", tv_assigned);
        Utils.msgText(AddPMScheduling.this, "519", tv_review_group);
        Utils.msgText(AddPMScheduling.this, "520", tv_review_user);
        Utils.msgText(AddPMScheduling.this, "521", tv_review_pln_date);
        Utils.msgText(AddPMScheduling.this, "522", tv_schedule_date);
        ;
        ;
        Utils.msgText(AddPMScheduling.this, "731", tv_no_of_pm_visits);
        Utils.msgText(AddPMScheduling.this, "732", tv_site_auto_access);
        Utils.msgText(AddPMScheduling.this, "733", tv_max_site_access_duration);
        Utils.msgText(AddPMScheduling.this, "734", tv_no_of_pm_visits_audit);
        Utils.msgText(AddPMScheduling.this, "735", tv_site_auto_access_audit);
        Utils.msgText(AddPMScheduling.this, "736", tv_max_site_access_duration_audit);
        Utils.msgText(AddPMScheduling.this, "115", tvSubmit);
    }

    private void InitilizeControllerId() {
        db = new DataBaseHelper(AddPMScheduling.this);
        db.open();
        mAppPreferences = new AppPreferences(AddPMScheduling.this);
        moduleUrl = db.getModuleIP("Preventive");
        btn_back = (Button) findViewById(R.id.button_back);
        ll_dg_type = (LinearLayout) findViewById(R.id.ll_dg_type);
        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        tv_siteId = (TextView) findViewById(R.id.tv_siteId);
        et_siteId = (AutoCompleteTextView) findViewById(R.id.et_siteId);
        et_siteId.setTypeface(Utils.typeFace(AddPMScheduling.this));

        tv_activity_type = (TextView) findViewById(R.id.tv_activity_type);
        sp_activity_type = (Spinner) findViewById(R.id.sp_activity_type);
        sp_activity_type.setBackgroundResource(R.drawable.doted);

        tv_dg_type = (TextView) findViewById(R.id.tv_dg_type);
        sp_dg_type = (Spinner) findViewById(R.id.sp_dg_type);
        sp_dg_type.setBackgroundResource(R.drawable.doted);

        tv_assigngroup = (TextView) findViewById(R.id.tv_assigngroup);
        sp_assign_group = (Spinner) findViewById(R.id.sp_assign_group);
        tv_assigned = (TextView) findViewById(R.id.tv_assigned);
        tv_grp_user = (TextView) findViewById(R.id.tv_grp_user);
        tv_review_group = (TextView) findViewById(R.id.tv_review_group);
        sp_review_group = (Spinner) findViewById(R.id.sp_review_group);
        tv_review_user = (TextView) findViewById(R.id.tv_review_user);
        tv_reviewuser = (TextView) findViewById(R.id.tv_reviewuser);
        tv_review_pln_date = (TextView) findViewById(R.id.tv_review_pln_date);
        tv_reviewplndate = (TextView) findViewById(R.id.tv_reviewplndate);
        tv_schedule_date = (TextView) findViewById(R.id.tv_schedule_date);
        tv_scheduledate = (TextView) findViewById(R.id.tv_scheduledate);

        tv_no_of_pm_visits = (TextView) findViewById(R.id.tv_no_of_pm_visits);
        et_no_of_pm_visits = (EditText) findViewById(R.id.et_no_of_pm_visits);

        tv_site_auto_access = (TextView) findViewById(R.id.tv_site_auto_access);
        sp_site_auto_access = (Spinner) findViewById(R.id.sp_site_auto_access);

        tv_max_site_access_duration = (TextView) findViewById(R.id.tv_max_site_access_duration);
        et_max_site_access_duration = (EditText) findViewById(R.id.et_max_site_access_duration);


        tv_no_of_pm_visits_audit = (TextView) findViewById(R.id.tv_no_of_pm_visits_audit);
        et_no_of_pm_visits_audit = (EditText) findViewById(R.id.et_no_of_pm_visits_audit);

        tv_site_auto_access_audit = (TextView) findViewById(R.id.tv_site_auto_access_audit);
        sp_site_auto_access_audit = (Spinner) findViewById(R.id.sp_site_auto_access_audit);

        tv_max_site_access_duration_audit = (TextView) findViewById(R.id.tv_max_site_access_duration_audit);
        et_max_site_access_duration_audit = (EditText) findViewById(R.id.et_max_site_access_duration_audit);

        tv_scheduledate.setVisibility(View.VISIBLE);

        if (mAppPreferences.getPMReviewPlanDate() == 1) {
            tv_review_pln_date.setVisibility(View.VISIBLE);
            tv_reviewplndate.setVisibility(View.VISIBLE);
        } else {
            tv_review_pln_date.setVisibility(View.GONE);
            tv_reviewplndate.setVisibility(View.GONE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(AddPMScheduling.this, PMTabs.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    private void setDropdownList() {
        All_Operator_Id = new StringBuffer();
        all_exempt_opt_id = new StringBuffer();
        All_assign_user = new StringBuffer();
        All_review_user = new StringBuffer();
        operatorList = new ArrayList<Operator>();
        assignList = new ArrayList<Operator>();
        reviewList = new ArrayList<Operator>();
        exemptOperatorList = new ArrayList<Operator>();

        list_activity = db.getInciParam1("20", tv_activity_type, "654");
        list_activity.add(0, "Select Activity Type");

        list_dg = db.getEnergyDgDesc();
        list_dg.add(0, "Select DG Type");

        list_site_auto_access = db.getInciParam1("171", tv_site_auto_access, "654");
        list_site_auto_access_audit = db.getInciParam1("171", tv_site_auto_access_audit, "654");

        list_group1 = db.getInciGrp("654");

        list_group2 = db.getInciGrp("654");
        list_group2.remove(0);
        list_group2.add(0, "Select Review Group");

        addItemsOnSpinner(sp_activity_type, list_activity);
        addItemsOnSpinner(sp_assign_group, list_group1);
        addItemsOnSpinner(sp_review_group, list_group2);
        addItemsOnSpinner(sp_dg_type, list_dg);
        addItemsOnSpinner(sp_site_auto_access, list_site_auto_access);
        addItemsOnSpinner(sp_site_auto_access_audit, list_site_auto_access_audit);

    }

    //Split api responce by Avdhesh
    public void StringSplit(String abc) {

        // String s = " ;String; String; String; String, String; String;;String;String; String; String; ;String;String;String;String";
        //String[] strs = s.split("[,\\s\\;]");
        String[] strs = abc.split(",");
        //  System.out.println("Substrings length:"+strs.length);
        for (int i = 0; i < strs.length; i++) {
            System.out.println("Str[" + i + "]:" + strs[i]);
            setManualFild(strs[i]);
        }

    }

    //Manual enable/disbale according condation fild by Avdhesh
    private void setManualFild(String str) {
        if (str.equalsIgnoreCase("sid")) {
            et_siteId.setEnabled(true);
        } else if (str.equalsIgnoreCase("acttypeid")) {
            sp_activity_type.setEnabled(true);
        } else if (str.equalsIgnoreCase("dgType")) {
            sp_dg_type.setEnabled(true);
        } else if (str.equalsIgnoreCase("schDt")) {
            tv_scheduledate.setEnabled(true);
        } else if (str.equalsIgnoreCase("AssignGrp")) {
            sp_assign_group.setEnabled(true);
        } else if (str.equalsIgnoreCase("currRwPlDt")) {
            tv_reviewplndate.setEnabled(true);
        } else if (str.equalsIgnoreCase("ArrAssignToUser")) {
            tv_grp_user.setEnabled(true);
        } else if (str.equalsIgnoreCase("ArrRwAssignUser")) {
            tv_reviewuser.setEnabled(true);
        } else if (str.equalsIgnoreCase("rwAssignGrp")) {
            sp_review_group.setEnabled(true);
        } else if (str.equalsIgnoreCase("pmVisit")) {
            et_no_of_pm_visits.setEnabled(true);
        } else if (str.equalsIgnoreCase("autoAccessId")) {
            sp_site_auto_access.setEnabled(true);
        } else if (str.equalsIgnoreCase("accDur")) {
            et_max_site_access_duration.setEnabled(true);
        } else if (str.equalsIgnoreCase("pmVisitAudit")) {
            et_no_of_pm_visits_audit.setEnabled(true);
        } else if (str.equalsIgnoreCase("autoAccessIdAudit")) {
            sp_site_auto_access_audit.setEnabled(true);
        } else if (str.equalsIgnoreCase("accDurAudit")) {
            et_max_site_access_duration_audit.setEnabled(true);
        } else if (str.equalsIgnoreCase("pulldate")) {
            // et_max_site_access_duration_audit.setEnabled(true);
        } else if (str.equalsIgnoreCase("gnStatus")) {
            //et_max_site_access_duration_audit.setEnabled(true);
        } else if (str.equalsIgnoreCase("overhouldate")) {
            //et_max_site_access_duration_audit.setEnabled(true);
        }


    }

    //Manual disable fild by Avdhesh
    private void setManualFildDisble() {

        et_siteId.setEnabled(false);
        sp_activity_type.setEnabled(false);
        sp_dg_type.setEnabled(false);
        tv_scheduledate.setEnabled(false);
        sp_assign_group.setEnabled(false);
        tv_reviewplndate.setEnabled(false);
        tv_grp_user.setEnabled(false);
        tv_reviewuser.setEnabled(false);
        sp_review_group.setEnabled(false);

        et_no_of_pm_visits_audit.setEnabled(false);
        sp_site_auto_access_audit.setEnabled(false);
        et_max_site_access_duration_audit.setEnabled(false);
        et_max_site_access_duration.setEnabled(false);
        sp_site_auto_access.setEnabled(false);
        et_no_of_pm_visits.setEnabled(false);


    }

    //Manual enable fild by Avdhesh
    private void setManualFildEnable() {

        et_siteId.setEnabled(true);
        sp_activity_type.setEnabled(true);
        sp_dg_type.setEnabled(true);
        tv_scheduledate.setEnabled(true);
        sp_assign_group.setEnabled(true);
        tv_reviewplndate.setEnabled(true);
        tv_grp_user.setEnabled(true);
        tv_reviewuser.setEnabled(true);
        sp_review_group.setEnabled(true);

        et_no_of_pm_visits_audit.setEnabled(true);
        sp_site_auto_access_audit.setEnabled(true);
        et_max_site_access_duration_audit.setEnabled(true);
        et_max_site_access_duration.setEnabled(true);
        sp_site_auto_access.setEnabled(true);
        et_no_of_pm_visits.setEnabled(true);


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddPMScheduling.this, PMTabs.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        final int mSelected = spinner.getSelectedItemPosition();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(AddPMScheduling.this));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(AddPMScheduling.this));
                v.setPadding(10, 15, 10, 15);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public class SiteDetailsTask extends AsyncTask<Void, Void, Void> {
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
            String response = Utils.httpPostRequest(con, mAppPreferences.getConfigIP() + WebMethods.url_GetMetadata, nameValuePairs);
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
                    ArrayList<String> prePopulateSitelist = new ArrayList<>();
                    for (int i = 0; i < dataResponse.getSites().size(); i++) {
                        prePopulateSitelist.add(dataResponse.getSites().get(i).getSiteId());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddPMScheduling.this, android.R.layout.select_dialog_item, prePopulateSitelist);
                    et_siteId.setAdapter(adapter);// setting the adapter data into the AutoCompleteTextView
                    et_siteId.setThreshold(1);
                    et_siteId.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                            et_siteId.showDropDown();
                            et_siteId.requestFocus();
                            return false;
                        }
                    });

                }
            }
            super.onPostExecute(result);
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Operator> {
        private ArrayList<Operator> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Operator> countryList) {
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
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.name.setTypeface(Utils.typeFace(AddPMScheduling.this));
                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
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

}
