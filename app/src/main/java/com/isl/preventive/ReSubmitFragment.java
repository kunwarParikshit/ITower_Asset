package com.isl.preventive;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.GPSTracker;
import com.isl.itower.HomeActivity;
import com.isl.itower.NotificationList;
import com.isl.modal.AssetList;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.modal.BeanSiteList;
import com.isl.modal.Operator;
import com.isl.notification.ShortcutBadger;
import com.isl.sparepart.schedule.AdapterSchedule;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import infozech.itower.R;
/**
 * Created by dhakan on 11/22/2019.
 */
public class ReSubmitFragment extends Fragment {
    ListView sites_list;
    AppPreferences mAppPreferences;
    BeanSiteList response_ticket_list = null;
    DataBaseHelper db;
    Spinner sp_search_by;
    EditText et_searchby_site, et_sch_date,et_txn_id;
    Calendar myCalendar = Calendar.getInstance();
    AdapterSchedule adapterrr = null;
    TextView tv_operator, tv_search_by_logo,txt_no_ticket;
    Dialog actvity_dialog;
    ArrayList<String> list_operator_name, list_operator_id;
    ArrayList<Operator> operatorList;
    StringBuffer All_Operator_Id;
    MyCustomAdapter dataAdapter = null;
    int optCounter = 0;
    String moduleUrl = "";
    String url = "";
    String latitude,longitude;
    View view;
    ArrayList<String> searchDDLlist;
    SwipeRefreshLayout pullToRefresh;
    int searchDateFlag = 0; //0-All,1-siteid,2-activity type id,3-Schedule date,4-Done date,5-reject date search function call
    JSONObject savedDataJsonObjRemarks = null;
    JSONObject savedDataJsonObjReviewRemarks = null;
    JSONObject savedDataJsonObj = null;
    private NetworkManager networkManager;//108
    private FusedLocationProviderClient fusedLocationClient;
    public ReSubmitFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_site_list,container, false);
        networkManager = new NetworkManager();//108
        RelativeLayout rl_header_ticket_list = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_list);
        rl_header_ticket_list.setVisibility(View.GONE);
        txt_no_ticket = (TextView) view.findViewById(R.id.txt_no_ticket);
        Utils.msgText(getActivity(), "225", txt_no_ticket);
        tv_search_by_logo = (TextView) view.findViewById(R.id.tv_search_by_logo);
        Utils.msgText(getActivity(), "296", tv_search_by_logo);
        mAppPreferences = new AppPreferences(getActivity());
        db = new DataBaseHelper(getActivity());
        db.open();
        moduleUrl = db.getModuleIP("Preventive");
        db.close();
        tv_operator = (TextView) view.findViewById(R.id.tv_operator);
        tv_operator.setBackgroundResource(R.drawable.doted);
        sp_search_by = (Spinner) view.findViewById(R.id.sp_search_by);
        sp_search_by.setBackgroundResource(R.drawable.doted);
        et_searchby_site = (EditText) view.findViewById(R.id.et_searchby_site);
        et_searchby_site.setBackgroundResource(R.drawable.input_box );
        et_sch_date = (EditText) view.findViewById(R.id.et_sch_date);

        et_txn_id = (EditText) view.findViewById(R.id.et_txn_id);
        et_txn_id.setBackgroundResource(R.drawable.input_box );

        searchFunction();
        tv_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActivityPopup();
            }
        });
        sites_list = (ListView) view.findViewById(R.id.lv_sites);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        if (mAppPreferences.getTicketFrmNtBr().equalsIgnoreCase("1")) {
        } else {
            db.open();
            db.deleteNotification(mAppPreferences.getTicketFrmNtBr(),mAppPreferences.getUserId());
            db.close();
        }

        db.open();
        if (Utils.isNetworkAvailable(getActivity())) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    //  new PMGridTask( getActivity() ).execute();
                }
            },100);

        }else{
            if(db.reSubmitSiteCount()>0){
                response_ticket_list = db.reSubmitSite("", 0);
                if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                    adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"RS");
                    sites_list.setAdapter(adapterrr);
                   /* pullToRefresh.setVisibility(View.VISIBLE);
                    RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                    rl_no_list.setVisibility(View.GONE);*/
                    txt_no_ticket.setVisibility(View.GONE);
                }
            }else{
               /* pullToRefresh.setVisibility(View.GONE);
                RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                rl_no_list.setVisibility(View.VISIBLE);*/
                txt_no_ticket.setVisibility(View.VISIBLE);
                sites_list.setAdapter(null);
            }
        }
        db.close();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask( getActivity() ).execute();
                    searchFunction();
                } else {
                    Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
                pullToRefresh.setRefreshing(false);
            }
        });

        sites_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedPos,long arg3) {
                db.open();
                String resubmit_tab_right = db.getSubMenuRight("ReSubmit", "Preventive");

                if(resubmit_tab_right.contains("A")) {
                    if(isUserLocation()){
                        if (response_ticket_list.getSite_list().get(selectedPos).getAssetFlag()!=null
                                && response_ticket_list.getSite_list().get(selectedPos).getAssetFlag().length()>0
                                && response_ticket_list.getSite_list().get(selectedPos)
                                .getAssetFlag().equalsIgnoreCase("1")) {
                            activity(selectedPos,1);
                        }else {
                            if (db.isAlreadyAutoSaveChk(response_ticket_list.getSite_list()
                                    .get(selectedPos).getTXN_ID(),"0") == 0) {
                                db.insertAutoSaveChkList(response_ticket_list.getSite_list()
                                                .get(selectedPos).getTXN_ID(),
                                        "", "", "","0");
                            }
                            activity(selectedPos,0);
                        }

                       /* if (Utils.isNetworkAvailable(getContext())) {
                            new getScheduleEquipment(getActivity(),arg2).execute();
                        }*/

                    /*if (db.isAlreadyAutoSaveChk(response_ticket_list.getSite_list().get(arg2).getTXN_ID()) == 0) {
                        db.insertAutoSaveChkList(response_ticket_list.getSite_list().get(arg2).getTXN_ID(),
                                "", "", "");
                    }
                    if (response_ticket_list.getSite_list().get(arg2).getPopupFlag() != null
                            && response_ticket_list.getSite_list().get(arg2).getPopupFlag().length() > 0
                            && response_ticket_list.getSite_list().get(arg2).getPopupFlag()
                            .equalsIgnoreCase("1")) {
                        if (response_ticket_list.getSite_list().get(arg2).getPullDate() != null &&
                                response_ticket_list.getSite_list().get(arg2).getPullDate().length() > 0) {
                            overallDate(0, response_ticket_list.getSite_list().get(arg2).getPullDate()
                                    , arg2);
                        } else {
                            pullDate(0, arg2, "");
                        }
                    } else {
                        activity(arg2);
                    }*/
                    }
                    db.close();
                }

            }
        });
        return view;
    }


    public void searchData(int flag){
        db.open();
        //String cs = et_searchby_site.getText().toString().toLowerCase( Locale.getDefault());
        String cs = "";
        if(flag==4){
            cs = et_txn_id.getText().toString().toLowerCase( Locale.getDefault());
        }else{
            cs = et_searchby_site.getText().toString().toLowerCase( Locale.getDefault());
        }
        response_ticket_list = db.reSubmitSite(cs, flag);
        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"RS");
            sites_list.setAdapter(adapterrr);
           /* pullToRefresh.setVisibility(View.VISIBLE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.GONE);*/
            txt_no_ticket.setVisibility(View.GONE);
        } else {
           /* pullToRefresh.setVisibility(View.GONE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.VISIBLE);*/
            txt_no_ticket.setVisibility(View.VISIBLE);
            sites_list.setAdapter(null);
        }
        db.close();
    }




    public class PMGridTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String token; //108

        public PMGridTask(Context con,String token) {
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
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
                //nameValuePairs.add(new BasicNameValuePair("type", "NEXT_7_DAYS"));
                nameValuePairs.add(new BasicNameValuePair("type", "RESUBMITTED"));
                nameValuePairs.add(new BasicNameValuePair("siteID", ""));
                nameValuePairs.add(new BasicNameValuePair("activityTypeFlag","1"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
                }else{
                    url=moduleUrl+ WebMethods.url_getScheduled_Sites;
                }
                String response = Utils.httpPostRequest1(con,url, nameValuePairs,token); //108
                Gson gson = new Gson();
                response_ticket_list = gson.fromJson(response,BeanSiteList.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_ticket_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response_ticket_list == null) {
                Utils.toast(getActivity(), "13");
            } else if (response_ticket_list.getSite_list()!=null &&
                    response_ticket_list.getSite_list().size() > 0) {
                db.open();
                db.clearResubmitPMList();
                db.insertResubmitList(response_ticket_list);
                db.close();
                sites_list.setAdapter(new AdapterSchedule(getActivity(),response_ticket_list,"RS"));
                /*pullToRefresh.setVisibility(View.VISIBLE);
                RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                rl_no_list.setVisibility(View.GONE);*/
                txt_no_ticket.setVisibility(View.GONE);
            } else {
                db.open();
                db.clearResubmitPMList();
                db.close();
                /*pullToRefresh.setVisibility(View.GONE);
                RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                rl_no_list.setVisibility(View.VISIBLE);*/
                txt_no_ticket.setVisibility(View.VISIBLE);
                sites_list.setAdapter(null);
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }


    public void onBackPressed() {
        if (mAppPreferences.getBackModeNotifi45() == 2) {
            Intent i = new Intent(getActivity(), NotificationList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            getActivity().finish();
        } else {
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ArrayList<String> notification_counter = new ArrayList<String>();
        db.open();
        notification_counter = db.getNotificationCount(
                mAppPreferences.getUserId(), "0");
        db.close();
        ShortcutBadger.removeCount(getActivity());
        if (notification_counter.size() > 0) {
            ShortcutBadger.applyCount(getActivity(),
                    notification_counter.size());
        }
    }

    // 0.1 Start
    public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("Site Id");
        searchDDLlist.add("Activity Type");
        searchDDLlist.add("Schedule Date");
        searchDDLlist.add("Activity ID");
        tv_operator.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner(sp_search_by, searchDDLlist);
        sp_search_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                et_searchby_site.setText("");
                et_sch_date.setText("");
                et_txn_id.setText("");
                optCounter = 0;
                tv_operator.setText("Select Activity Type");
                hideKeyBoardEdt(sp_search_by);
                if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Site Id")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.VISIBLE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.GONE);
                    searchDateFlag = 1;
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Activity Type")) {
                    tv_operator.setVisibility(View.VISIBLE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.GONE);
                    searchDateFlag = 2;
                } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Schedule Date")) {
                    searchDateFlag = 3;
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.VISIBLE);
                    et_txn_id.setVisibility(View.GONE);
                }else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Activity ID")) {
                    searchDateFlag = 4;
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.VISIBLE);
                }

                adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"RS");
                sites_list.setAdapter(adapterrr);
                operatorList = new ArrayList<Operator>();
                All_Operator_Id = new StringBuffer();

                db.open();
                list_operator_id = new ArrayList<String>();
                list_operator_name = new ArrayList<String>();
                list_operator_name = db.getInciParam1("20",tv_operator,"654");

                for (int i = 0; i < list_operator_name.size(); i++) {
                    if (!list_operator_name.get(i).equalsIgnoreCase(
                            "Please Select")) {
                        Operator oper = new Operator(db.getInciParamId("20",
                                list_operator_name.get(i),"654"), list_operator_name
                                .get(i), false);
                        operatorList.add(oper);
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
            public void onTextChanged(CharSequence cs, int arg1, int arg2,int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                searchData(1);
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
                searchData(4);
            }
        });

        et_sch_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_sch_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
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
        String myFormat = "dd-MMM-yyyy-hh-mm-ss"; // In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        et_sch_date.setText(sdf.format(myCalendar.getTime()));
        db.open();
        String cs = et_sch_date.getText().toString().toLowerCase(Locale.getDefault());
        response_ticket_list = db.reSubmitSite(cs, searchDateFlag);

        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"RS");
            sites_list.setAdapter(adapterrr);
           /* pullToRefresh.setVisibility(View.VISIBLE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.GONE);*/
            txt_no_ticket.setVisibility(View.GONE);
        } else {
           /* pullToRefresh.setVisibility(View.GONE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.VISIBLE);*/
            txt_no_ticket.setVisibility(View.VISIBLE);
            sites_list.setAdapter(null);
        }
        db.close();
    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void ActivityPopup() {
        optCounter = 0;
        All_Operator_Id.setLength(0);
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        final ListView list_view = (ListView) actvity_dialog.findViewById(R.id.list_view);
        TextView apply = (TextView) actvity_dialog.findViewById(R.id.tv_apply);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setText("Actvity Type");

        ImageView iv_cancel = (ImageView) actvity_dialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });

        dataAdapter = new MyCustomAdapter(getActivity(),R.layout.custom_operator, operatorList);
        list_view.setAdapter(dataAdapter);
        apply.setOnClickListener(new View.OnClickListener() {

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
                    searchData(1);
                } else {
                    tv_operator.setText("" + optCounter + " Activity Selected");
                }
                actvity_dialog.dismiss();// dismiss dialog box for operator
                if (All_Operator_Id.length() > 0) {
                    db.open();
                    response_ticket_list = db.reSubmitSite(All_Operator_Id.toString(), 2);
                    if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                        adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"RS");
                        sites_list.setAdapter(adapterrr);
                       /* pullToRefresh.setVisibility(View.VISIBLE);
                        RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                        rl_no_list.setVisibility(View.GONE);*/
                        txt_no_ticket.setVisibility(View.GONE);
                    } else {
                       /* pullToRefresh.setVisibility(View.GONE);
                        RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                        rl_no_list.setVisibility(View.VISIBLE);*/
                        txt_no_ticket.setVisibility(View.VISIBLE);
                        sites_list.setAdapter(null);
                    }
                    db.close();
                }
            }

        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Operator> {
        private ArrayList<Operator> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Operator> countryList) {
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
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.custom_operator, null);
                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);
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

    public void  hideKeyBoardEdt(Spinner edt) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    private class GetImage extends AsyncTask<Void, Void, Void> {
        Context con;
        ProgressDialog pd;
        BeanGetImageList imageList;
        String txnId,scDate,activityId,sId,dgType,etsSid;
        Intent i;
        private GetImage(Context con,Intent i,String txnId,String scDate,String activityId,String sId,
                         String etsSid,String dgType) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.etsSid = etsSid;
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
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
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

                                dataBaseHelper.insertImages(
                                        txnId,
                                        imageList.getImageList().get(i).getClID(),
                                        imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
                                        name,
                                        lat,
                                        longi,
                                        time.replaceAll("-","").replaceAll(":","").replaceAll(" ",""),
                                        time,
                                        1,
                                        3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],
                                        mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,"0");
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

                                if(timeArr!=null && imgpathArr.length<=timeArr.length){
                                    time = timeArr[j];
                                }

                                if(name!=null && imgpathArr.length<=nameArr.length){
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

                                dataBaseHelper.insertImages(
                                        txnId,
                                        imageList.getImageList().get(i).getClID(),
                                        imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
                                        name,
                                        lat,
                                        longi,
                                        time.replaceAll("-","").replaceAll(":","").replaceAll(" ",""),
                                        time,
                                        2,
                                        3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],
                                        mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,"0");
                            }
                        }
                    }
                }
            }

            dataBaseHelper.close();
            if (Utils.isNetworkAvailable(getActivity())) {
                new CheckListDetailsTask(getActivity(),i,txnId,sId,scDate,dgType,activityId).execute();
            } else {
                Utils.toast(getActivity(), "17");
            }
            super.onPostExecute(result);
        }
    }

    private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String txnId,scDate,activityId,sId,dgType;
        BeanCheckListDetails PMCheckListDetails;
        private CheckListDetailsTask(Context con,Intent i,String txnId,String sId,String scDate,
                                     String dgType,String activityId) {
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("siteId", sId));
                nameValuePairs.add(new BasicNameValuePair("checkListType",activityId));
                nameValuePairs.add(new BasicNameValuePair("checkListDate",scDate));
                nameValuePairs.add(new BasicNameValuePair("status", "D"));
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
                Utils.toast(getActivity(), "13");
            } else if (PMCheckListDetails.getPMCheckListDetail()!=null &&
                    PMCheckListDetails.getPMCheckListDetail().size() > 0) {

                for(int a = 0; a<PMCheckListDetails.getPMCheckListDetail().size();a++){
                    sharePrefence(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                            PMCheckListDetails.getPMCheckListDetail().get(a).getStatus(),txnId);

                    sharePrefenceRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                            PMCheckListDetails.getPMCheckListDetail().get(a).getViRemark(),txnId);

                    sharePrefenceReviewRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                            PMCheckListDetails.getPMCheckListDetail().get(a).getrRemark(),txnId);
                }

            } else {

            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (Utils.isNetworkAvailable(getActivity())) {
                new GetPMCheckList(getActivity(),0,i,sId,activityId,txnId,scDate).execute();
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
        } catch (JSONException e) {

        }

        DataBaseHelper db10 = new DataBaseHelper( getActivity() );
        db10.open();
        String ss = db10.getAutoSaveChk(txnId,"0");
        db10.updateAutoSaveChkList(txnId,"","",
                savedDataJsonObj.toString(),"0");
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
        DataBaseHelper db10 = new DataBaseHelper(getActivity());
        db10.open();
        db10.updateAutoSaveRemarks(txnId,savedDataJsonObjRemarks.toString(),"0");
        //db10.updateAutoSaveRemarks1(txnId,savedDataJsonObjRemarks.toString(),savedDataJsonObjRemarks.toString());
        db10.close();
    }

    public void sharePrefenceReviewRemarks(String id,String s,String txnId){

        if(savedDataJsonObjReviewRemarks==null){
            savedDataJsonObjReviewRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjReviewRemarks.remove( "" + id);
            savedDataJsonObjReviewRemarks.put( "" +id,s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(getActivity());
        db10.open();
        db10.updateAutoSaveRemarks(txnId,savedDataJsonObjRemarks.toString(),"0");
        db10.updateAutoSaveRemarks1(txnId,savedDataJsonObjRemarks.toString(),
                savedDataJsonObjReviewRemarks.toString(),"0");
        db10.close();
    }

    // Class to call Web Service to get PM CheckList to draw form.
    private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String siteId = "",chklistType = "0",txnId="";
        BeanCheckListDetails PMCheckList;
        int alreadyJson = 0;
        String scDate = "";
        private GetPMCheckList(Context con,int alreadyJson,Intent i,
                               String siteId,String chklistType,String txnId,String scDate) {
            this.con = con;
            this.i = i;
            this.siteId = siteId;
            this.chklistType = chklistType;
            this.alreadyJson = alreadyJson;
            this.txnId = txnId;
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
            nameValuePairs.add( new BasicNameValuePair( "checkListDate",scDate) );
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
                Utils.toast(getActivity(),"226");
            } else if (PMCheckList != null) {
                if (PMCheckList.getPMCheckListDetail()!=null && PMCheckList.getPMCheckListDetail().size() > 0) {
                    //DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                    db.open();
                    db.clearCheckList("655",chklistType,"0");
                    db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),"655",alreadyJson,txnId,getActivity(),"0","0");
                    db.close();
                }
            } else {
                Utils.toast(getActivity(),"13");
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            startActivity(i);
            super.onPostExecute( result );
        }
    }

    private void pullDate(int flag,int selectedPos,String msg) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.generator_status_popup);
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        tv_title.setTypeface( Utils.typeFace(getActivity()));
        tv_title.setText(Utils.msg(getActivity(), "739"));

        Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
        iv_cross.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    //  new PMGridTask( getActivity() ).execute();
                    searchFunction();
                }
            }
        });

        TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
        Utils.textViewProperty(getActivity(),tv_sid,Utils.msg(getActivity(),"77"));

        EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
        Utils.editTextProperty(getActivity(),et_sid);
        et_sid.setEnabled( false );
        et_sid.setText(response_ticket_list.getSite_list().get(selectedPos).getSITE_ID());

        TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
        Utils.textViewProperty(getActivity(),tv_aid,Utils.msg(getActivity(),"740"));

        EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
        Utils.editTextProperty(getActivity(),et_aid);
        et_aid.setEnabled( false );
        et_aid.setText(response_ticket_list.getSite_list().get(selectedPos).getPARAM_NAME());

        TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
        Utils.textViewProperty(getActivity(),tv_pull_date,Utils.msg(getActivity(),"743"));
        tv_pull_date.setVisibility(View.GONE);
        EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
        Utils.editTextProperty(getActivity(),et_pull_date);
        et_pull_date.setEnabled( false );
        et_pull_date.setText("");
        et_pull_date.setVisibility(View.GONE);

        TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
        Utils.textViewProperty(getActivity(),tv_overall_date,Utils.msg(getActivity(),"744"));
        tv_overall_date.setVisibility(View.GONE);
        EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
        Utils.editTextProperty(getActivity(),et_overall_date);
        et_overall_date.setEnabled( false );
        et_overall_date.setText("");
        et_overall_date.setVisibility(View.GONE);


        Button next = (Button) actvity_dialog.findViewById( R.id.bt_next );
        next.setTypeface( Utils.typeFace( getActivity()) );
        next.setText( Utils.msg( getActivity(), "742" ) );
        next.setVisibility(View.GONE);

        TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
        Utils.textViewProperty(getActivity(),tv_checklist,"");
        tv_checklist.setVisibility(View.GONE);

        Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
        add.setTypeface( Utils.typeFace( getActivity()) );
        add.setText( Utils.msg( getActivity(), "204" ) );

        TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
        Utils.textViewProperty(getActivity(),tv_gen_status,Utils.msg(getActivity(),"741"));

        Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
        sp_gen_status.setBackgroundResource(R.drawable.doted);
        Utils.spinnerProperty(getActivity(),sp_gen_status);
        db.open();
        ArrayList<String> list = db.getPreventiveParamName("1195","654","1");
        addItemsOnSpinner(sp_gen_status,list);
        db.close();

        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    actvity_dialog.cancel();
                    new UpdateGeneratorStatusTask(getActivity(),Utils.CurrentDate(2),""
                            ,selectedPos,1).execute();
                } else {
                    Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                overallDate(0,Utils.CurrentDate(2),selectedPos);
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

    private void overallDate(int flag,String pullDate, int selectedPos) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.generator_status_popup);
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        tv_title.setTypeface( Utils.typeFace(getActivity()));
        tv_title.setText(Utils.msg(getActivity(), "739"));

        TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
        Utils.textViewProperty(getActivity(),tv_checklist,Utils.msg(getActivity(),"745"));
       /* tv_checklist.setTypeface( Utils.typeFace(getActivity()));
        tv_checklist.setTextColor(getActivity().getResources().getColor(R.color.textcolor));
        tv_checklist.setTextSize(15);*/

        Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
        iv_cross.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask( getActivity() ).execute();
                    searchFunction();
                }
            }
        });

        TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
        Utils.textViewProperty(getActivity(),tv_sid,Utils.msg(getActivity(),"77"));

        EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
        Utils.editTextProperty(getActivity(),et_sid);
        et_sid.setEnabled( false );
        et_sid.setText(response_ticket_list.getSite_list().get(selectedPos).getSITE_ID());

        TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
        Utils.textViewProperty(getActivity(),tv_aid,Utils.msg(getActivity(),"740"));

        EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
        Utils.editTextProperty(getActivity(),et_aid);
        et_aid.setEnabled( false );
        et_aid.setText(response_ticket_list.getSite_list().get(selectedPos).getPARAM_NAME());

        TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
        Utils.textViewProperty(getActivity(),tv_pull_date,Utils.msg(getActivity(),"743"));
        EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
        Utils.editTextProperty(getActivity(),et_pull_date);
        et_pull_date.setEnabled( false );
        et_pull_date.setText(pullDate);

        TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
        Utils.textViewProperty(getActivity(),tv_overall_date,Utils.msg(getActivity(),"744"));
        tv_overall_date.setVisibility(View.GONE);
        EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
        Utils.editTextProperty(getActivity(),et_overall_date);
        et_overall_date.setEnabled( false );
        et_overall_date.setText("");
        et_overall_date.setVisibility(View.GONE);

        Button no = (Button) actvity_dialog.findViewById( R.id.bt_next );
        no.setTypeface( Utils.typeFace( getActivity()) );
        no.setText( Utils.msg( getActivity(), "742" ) );
        no.setVisibility(View.GONE);

        Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
        add.setTypeface( Utils.typeFace( getActivity()) );
        add.setText( Utils.msg( getActivity(), "284" ) );

        TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
        Utils.textViewProperty(getActivity(),tv_gen_status,Utils.msg(getActivity(),"741"));

        Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
        sp_gen_status.setBackgroundResource(R.drawable.doted);
        Utils.spinnerProperty(getActivity(),sp_gen_status);
        db.open();
        ArrayList<String> list = db.getPreventiveParamName("1195","654","2");
        addItemsOnSpinner(sp_gen_status,list);
        db.close();

        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(flag==1){
                    actvity_dialog.cancel();
                    if (Utils.isNetworkAvailable(getActivity())) {
                        //108
                        networkManager.getToken(new NetworkManager.TokenCallback() {
                            @Override
                            public void onTokenReceived(String token) {
                                new PMGridTask( getActivity(),token).execute();
                            }

                            @Override
                            public void onTokenError(String error) {
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //108
                        //   new PMGridTask( getActivity() ).execute();
                        searchFunction();
                    }
                    activity(selectedPos,0);
                }else{
                    if (Utils.isNetworkAvailable(getActivity())) {
                        actvity_dialog.cancel();
                        new UpdateGeneratorStatusTask(getActivity(),pullDate,Utils.CurrentDate(2),
                                selectedPos,2).execute();
                    } else {
                        Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
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
            add.setText( Utils.msg( getActivity(), "63" ));
            no.setText( Utils.msg( getActivity(), "64" ));
            tv_checklist.setVisibility(View.VISIBLE);
        }

        no.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask(getActivity()).execute();
                    searchFunction();
                }
            }
        });
    }

    public class UpdateGeneratorStatusTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        int selectPos =0;
        String res;
        int pDsec = 1;
        String pullDate,overallDate;
        public UpdateGeneratorStatusTask(Context con,String pullDate,String overallDate,int selectPos,int pDsec) {
            this.con = con;
            this.selectPos = selectPos;
            this.pDsec = pDsec;
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
                        response_ticket_list.getSite_list().get(selectPos).getTXN_ID()));
                nameValuePairs.add( new BasicNameValuePair("siteId",
                        response_ticket_list.getSite_list().get(selectPos).getSITE_ID()));
                nameValuePairs.add( new BasicNameValuePair("actType",
                        response_ticket_list.getSite_list().get(selectPos).getPARAM_ID()));
                nameValuePairs.add( new BasicNameValuePair("scheduleDate",
                        response_ticket_list.getSite_list().get(selectPos).getSCHEDULE_DATE()));
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
                        pullDate(1,selectPos,msg);
                    }else if (success.equals("true") && pDsec == 2){
                        overallDate(1,pullDate,selectPos);
                    }else{
                        if (pDsec == 1) {
                            pullDate(0,selectPos,"");
                        }else if (pDsec == 2){
                            overallDate(0,pullDate,selectPos);
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(con,e.getMessage(),Toast.LENGTH_SHORT).show();
                    if (pDsec == 1) {
                        pullDate(0,selectPos,"");
                    }else if (pDsec == 2){
                        overallDate(0,pullDate,selectPos);
                    }
                }
            }else {
                //if no responce then reopen popup
                Utils.toast(getActivity(), "13");
                if (pDsec == 1) {
                    pullDate(0,selectPos,"");
                }else if (pDsec == 2){
                    overallDate(0,pullDate,selectPos);
                }

            }
            super.onPostExecute(result);
        }
    }

    private boolean isUserLocation() {
        boolean status = true;
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (!Utils.hasPermissions(getActivity(), AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(getActivity(), "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
            return false;
        } else if (gps.canGetLocation() == true) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } /*else {
                        latitude = String.valueOf(gps.getLatitude());
                        longitude = String.valueOf(gps.getLongitude());
                    }*/
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //latitude = String.valueOf(gps.getLatitude());
                        //longitude = String.valueOf(gps.getLongitude());
                    }
                });
            }
            //latitude = String.valueOf(gps.getLatitude());
            //longitude = String.valueOf(gps.getLongitude());
            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(getActivity(), "252");
                return false;
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        } /*else {
                        latitude = String.valueOf(gps.getLatitude());
                        longitude = String.valueOf(gps.getLongitude());
                    }*/
                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //latitude = String.valueOf(gps.getLatitude());
                            //longitude = String.valueOf(gps.getLongitude());
                        }
                    });
                }

                //latitude = String.valueOf(gps.getLatitude());
                //longitude = String.valueOf(gps.getLongitude());
            }
        }
        return true;
    }

    public class getScheduleEquipment extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        AssetList asset_list;
        String serUrl  ="";
        int selectedPos;
        public getScheduleEquipment(Context con,final int selectedPos) {
            this.con = con;
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
                String url = "";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("txnId",response_ticket_list.getSite_list().get(selectedPos).getTXN_ID()));
                nameValuePairs.add(new BasicNameValuePair("equipId","-1"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_get_AssetList;
                }else{
                    serUrl=moduleUrl+ WebMethods.url_get_AssetList;
                }
                String response_site = Utils.httpPostRequest(con,serUrl, nameValuePairs);
                Gson gson = new Gson();
                asset_list = gson.fromJson(response_site, AssetList.class);
            } catch (Exception e) {
                e.printStackTrace();
                asset_list = null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }

            if (asset_list == null)
            {
                Utils.toast(getActivity(), "13");
            }else if (asset_list.getAseetList()!=null && asset_list.getAseetList().size() > 0) {
                activity(selectedPos,1);
            }else {
                activity(selectedPos,0);
            }
            super.onPostExecute(result);
        }
    }


    //public void activity(final int arg2, final String preMin,final String preMax, final String postMin, final String postMax,String imageName) {
    public void activity(final int arg2,int count) {
        HashMap<String, String> valData = new HashMap<String, String>();
        HashMap<String, String> readingData = new HashMap<String, String>();

        if(response_ticket_list.getSite_list().get(arg2).getLtstRdng()!=null){
            String[] split1 = response_ticket_list.getSite_list().get(arg2).getLtstRdng().split( "\\," );
            if(split1.length>0) {
                for (int i = 0; i < split1.length; i++) {
                    String[] split2 = split1[i].split( "\\~" );
                    if(split2.length>1) {
                        readingData.put( split2[0], split2[1] );
                    }
                }
            }
        }

        if(response_ticket_list.getSite_list().get(arg2).getVal()!=null) {
            String[] split = response_ticket_list.getSite_list().get( arg2 ).getVal().split( "\\~" );
            if(split.length>0) {
                for(int i = 0; i<split.length; i++){
                    valData.put(split[i],"");
                }
            }
        }
        mAppPreferences.setPMchecklist("");
        mAppPreferences.setPMchecklistBackUp("");
        Intent i;
        if(count>0){
            i = new Intent( getActivity(), EquipmentList.class );
        }else{
            i = new Intent(getActivity(), PMChecklist.class);
        }
        i.putExtra("qrCode", "");
        i.putExtra("scheduledDate",response_ticket_list.getSite_list().get(arg2).getSCHEDULE_DATE());
        i.putExtra("S","RS");
        i.putExtra("siteId", response_ticket_list.getSite_list().get(arg2).getSITE_ID());
        i.putExtra("siteName", response_ticket_list.getSite_list().get(arg2).getSidName());
        i.putExtra("activityTypeId",response_ticket_list.getSite_list().get(arg2).getPARAM_ID());
        i.putExtra("paramName", response_ticket_list.getSite_list().get(arg2).getPARAM_NAME());
        i.putExtra("Status", response_ticket_list.getSite_list().get(arg2).getACTIVITY_STATUS());
        i.putExtra("dgType", response_ticket_list.getSite_list().get(arg2).getDG_TYPE());
        i.putExtra("txn", response_ticket_list.getSite_list().get(arg2).getTXN_ID());
        i.putExtra("etsSid", response_ticket_list.getSite_list().get(arg2).getEtsSid());
        i.putExtra("preMinImage", "0");
        i.putExtra("preMaxImage", "0");
        i.putExtra("postMinImage", "0");
        i.putExtra("postMaxImage", "0");
        i.putExtra("imageName", "");
        i.putExtra("valData", valData);
        i.putExtra("readingData", readingData);
        i.putExtra( "rvDate", response_ticket_list.getSite_list().get( arg2 ).getRvDate() );
        i.putExtra( "rejRmks", response_ticket_list.getSite_list().get( arg2 ).getRejRmks() );
        i.putExtra( "rCat", response_ticket_list.getSite_list().get( arg2 ).getrCat() );

        if(count>0){
            startActivity(i);
        }else{
            i.putExtra("assetId", "0");
            i.putExtra("equipmentId", "0");
            if (Utils.isNetworkAvailable(getActivity())) {
                new GetImage(getActivity(),i,response_ticket_list.getSite_list().get(arg2).getTXN_ID(),
                        response_ticket_list.getSite_list().get(arg2).getSCHEDULE_DATE(),
                        response_ticket_list.getSite_list().get(arg2).getPARAM_ID(),
                        response_ticket_list.getSite_list().get(arg2).getSITE_ID(),
                        response_ticket_list.getSite_list().get(arg2).getEtsSid(),
                        response_ticket_list.getSite_list().get(arg2).getDG_TYPE()).execute();
            } else {
                //No Internet Connection;
                Utils.toast(getActivity(), "17");
                //getActivity().finish();
            }
        }

    }
}