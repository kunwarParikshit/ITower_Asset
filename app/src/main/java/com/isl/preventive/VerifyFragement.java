package com.isl.preventive;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.AssetList;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.modal.BeanSiteList;
import com.isl.modal.Operator;
import com.isl.sparepart.schedule.AdapterSchedule;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import infozech.itower.R;

/**
 * Created by dhakan on 2/4/2019.
 */

public class VerifyFragement  extends Fragment {
    ListView sites_list;
    AppPreferences mAppPreferences;
    String response_assigned_tickets;
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
    String serUrl = "";
    ArrayList<String> searchDDLlist;
    View view;
    SwipeRefreshLayout pullToRefresh;
    private NetworkManager networkManager;//108
    public VerifyFragement() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_site_list,container, false);
        networkManager = new NetworkManager();//108
        RelativeLayout rl_header_ticket_list = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_list);
        rl_header_ticket_list.setVisibility(View.GONE);
        db = new DataBaseHelper(getActivity());
        db.open();
        db.clearVerifyList();
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
        txt_no_ticket = (TextView) view.findViewById(R.id.txt_no_ticket);
        Utils.msgText(getActivity(), "225", txt_no_ticket); // set text No Activity Found
        tv_search_by_logo = (TextView) view.findViewById(R.id.tv_search_by_logo);
        Utils.msgText(getActivity(), "296", tv_search_by_logo);
        mAppPreferences = new AppPreferences(getActivity());
        tv_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActivityPopup();
            }
        });
        sites_list = (ListView) view.findViewById(R.id.lv_sites);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
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
        } else {
            Utils.toast( getActivity(), "17" );
        }

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
                    //  new PMGridTask(getActivity()).execute();
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

                if (response_ticket_list.getSite_list().get(selectedPos).getAssetFlag()!=null
                        && response_ticket_list.getSite_list().get(selectedPos).getAssetFlag().length()>0
                        && response_ticket_list.getSite_list().get(selectedPos)
                        .getAssetFlag().equalsIgnoreCase("1")) {
                    viewAssetList(selectedPos);
                }else {
                    viewPMChecklist(selectedPos);
                }

                //dhakan 958
               /* if (Utils.isNetworkAvailable(getActivity())) {
                    new getScheduleEquipment(getActivity(),arg2).execute();
                } else {
                    Utils.toast(getActivity(), "17" );
                }*/


                /*Intent i = new Intent( getActivity(), ViewPMCheckList.class);
                String txnId = "",sDate = "",actid = "",sid = "",etsId = "",dgType = "",imguploadflag = "2";
                i.putExtra("S","V");
                i.putExtra( "scheduledDate", response_ticket_list.getSite_list().get( arg2 ).getSCHEDULE_DATE());
                i.putExtra( "siteId", response_ticket_list.getSite_list().get( arg2 ).getSITE_ID());
                i.putExtra( "siteName", response_ticket_list.getSite_list().get( arg2 ).getSidName());
                i.putExtra( "activityTypeId", response_ticket_list.getSite_list().get( arg2 ).getPARAM_ID());
                i.putExtra( "paramName", response_ticket_list.getSite_list().get( arg2 ).getPARAM_NAME());
                i.putExtra( "Status", response_ticket_list.getSite_list().get( arg2 ).getACTIVITY_STATUS());
                i.putExtra( "dgType", response_ticket_list.getSite_list().get( arg2 ).getDG_TYPE());
                i.putExtra( "txn", response_ticket_list.getSite_list().get( arg2 ).getTXN_ID());
                i.putExtra( "etsSid", response_ticket_list.getSite_list().get( arg2 ).getEtsSid() );
                i.putExtra( "imgUploadFlag", response_ticket_list.getSite_list().get( arg2 ).getImgUploadflag());
                i.putExtra( "rCat","");
                i.putExtra( "rejRmks","");
                i.putExtra( "rvDate","");
                sid =  response_ticket_list.getSite_list().get( arg2 ).getSITE_ID();
                sDate = response_ticket_list.getSite_list().get( arg2 ).getSCHEDULE_DATE();
                actid = response_ticket_list.getSite_list().get( arg2 ).getPARAM_ID();
                txnId = response_ticket_list.getSite_list().get( arg2 ).getTXN_ID();
                dgType = response_ticket_list.getSite_list().get( arg2 ).getDG_TYPE();
                etsId =  response_ticket_list.getSite_list().get( arg2 ).getEtsSid();
                imguploadflag = response_ticket_list.getSite_list().get( arg2 ).getImgUploadflag();

                if (Utils.isNetworkAvailable(getActivity())) {
                    new GetImage(getActivity(),i,txnId,sDate,actid,sid,etsId,dgType,imguploadflag).execute();
                } else {
                    //No Internet Connection;
                    Utils.toast(getActivity(), "17");
                }*/
            }
        });
        return view;
    }

    public void searchData(String flag){
        db.open();
        //String cs = et_searchby_site.getText().toString().toLowerCase( Locale.getDefault());
        String cs = "";
        if(flag.equalsIgnoreCase( "T" )){
            cs = et_txn_id.getText().toString().toLowerCase( Locale.getDefault());
        }else{
            cs = et_searchby_site.getText().toString().toLowerCase( Locale.getDefault());
        }
        response_ticket_list = db.verifySite(cs,flag);
        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"V");
            sites_list.setAdapter(adapterrr);
            /*pullToRefresh.setVisibility(View.VISIBLE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.GONE);*/
            txt_no_ticket.setVisibility(View.GONE);
        } else {
            /*pullToRefresh.setVisibility(View.GONE);
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
                String url = "";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("type","TILL_TODAY_APPROVED"));
                nameValuePairs.add(new BasicNameValuePair("siteID", ""));
                nameValuePairs.add(new BasicNameValuePair("activityTypeFlag","1"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
                }else{
                    serUrl=moduleUrl+ WebMethods.url_getScheduled_Sites;
                }
                response_assigned_tickets = Utils.httpPostRequest1(con,serUrl,nameValuePairs,token); //108
                Gson gson = new Gson();
                response_ticket_list = gson.fromJson(response_assigned_tickets,BeanSiteList.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_ticket_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((response_ticket_list == null)) {
                // Toast.makeText(ApprovedList.this,"Server Not Available",
                // Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(), "13");
            } else if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                db.open();
                db.clearVerifyList();
                db.insertVerifyList(response_ticket_list);
                db.close();
                sites_list.setAdapter(new AdapterSchedule(getActivity(),response_ticket_list,"V"));
                /*pullToRefresh.setVisibility(View.VISIBLE);
                RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                rl_no_list.setVisibility(View.GONE);*/
                txt_no_ticket.setVisibility(View.GONE);
            } else {
                db.open();
                db.clearVerifyList();
                db.close();
               /* pullToRefresh.setVisibility(View.GONE);
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

    // 0.1 Start
    public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("Site Id");
        searchDDLlist.add("Activity Type");
        searchDDLlist.add("Verify Date");
        searchDDLlist.add("Activity ID");
        sp_search_by.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner(sp_search_by, searchDDLlist);
        //setupUI(sp_search_by);
        sp_search_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
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
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Verify Date")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.VISIBLE);
                    et_txn_id.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Activity ID")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.VISIBLE);
                }

                adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"V");
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

        String myFormat = "dd-MMM-yyyy-hh-mm-ss"; // In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        et_sch_date.setText(sdf.format(myCalendar.getTime()));
        db.open();
        String cs = et_sch_date.getText().toString()
                .toLowerCase(Locale.getDefault());
        response_ticket_list = db.verifySite(cs, "D");
        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"V");
            sites_list.setAdapter(adapterrr);
            /*pullToRefresh.setVisibility(View.VISIBLE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.GONE);*/
            txt_no_ticket.setVisibility(View.GONE);
        } else {
            // Toast.makeText(ApprovedList.this,
            // "No Activity Found",Toast.LENGTH_LONG).show();
            // Utils.toast(ApprovedList.this, "225");
            /*pullToRefresh.setVisibility(View.GONE);
            RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
            rl_no_list.setVisibility(View.VISIBLE);*/
            txt_no_ticket.setVisibility(View.VISIBLE);
            sites_list.setAdapter(null);
        }
        db.close();
    }

    // end 0.1
    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

   /*public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService( Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }
    }*/

    public void ActivityPopup() {
        optCounter = 0;
        All_Operator_Id.setLength(0);
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.activity_type_popup); // operator list
        // UI
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
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

        dataAdapter = new MyCustomAdapter(getActivity(), R.layout.custom_operator, operatorList);
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
                    searchData("I");
                } else {
                    tv_operator.setText("" + optCounter + " Activity Selected");
                }
                actvity_dialog.dismiss();// dismiss dialog box for operator
                if (All_Operator_Id.length() > 0) {
                    db.open();
                    response_ticket_list = db.verifySite(
                            All_Operator_Id.toString(), "aID");
                    if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                        adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"V");
                        sites_list.setAdapter(adapterrr);
                       /* pullToRefresh.setVisibility(View.VISIBLE);
                        RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                        rl_no_list.setVisibility(View.GONE);*/
                        txt_no_ticket.setVisibility(View.GONE);
                    } else {
                        // Toast.makeText(ApprovedList.this,
                        // "No Activity Found",Toast.LENGTH_LONG).show();
                        // Utils.toast(ApprovedList.this, "225");
                        /*pullToRefresh.setVisibility(View.GONE);
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
        String txnId,scDate,activityId,sId,dgType,etsSid,imguploadflag;
        Intent i;
        private GetImage(Context con,Intent i,String txnId,String scDate,String activityId,String sId,
                         String etsSid,String dgType,String imguploadflag) {
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
                nameValuePairs.add(new BasicNameValuePair("activityType", activityId));
                nameValuePairs.add(new BasicNameValuePair("scheduledDate",scDate));
                nameValuePairs.add(new BasicNameValuePair("dgType", dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID","-10"));
                String url = "";
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
                                String clId = "0";
                                if(timeArr!=null && imgpathArr.length<=timeArr.length){
                                    time = timeArr[j];
                                }

                                if(nameArr!=null && imgpathArr.length<=nameArr.length){
                                    name = nameArr[j];
                                }

                                if(latArr!=null && imgpathArr.length<=latArr.length){
                                    lat = latArr[j];
                                }

                                if(longArr!=null && imgpathArr.length<=longArr.length){
                                    longi = longArr[j];
                                }

                                if(imguploadflag.equalsIgnoreCase("2")){
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                                /*dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,Utils.DateTimeStamp(),time,1,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );*/

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
                                }

                                if(longArr!=null && imgpathArr.length<=longArr.length){
                                    longi = longArr[j];
                                }

                                if(imguploadflag.equalsIgnoreCase("2")){
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                               /* dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,Utils.DateTimeStamp(),time,2,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );*/

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
        private CheckListDetailsTask(Context con,Intent i,String txnId,String sId,String scDate,String dgType,String activityId) {
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
                nameValuePairs.add(new BasicNameValuePair("status", "R"));
                nameValuePairs.add(new BasicNameValuePair("languageCode",mAppPreferences.getLanCode()));
                nameValuePairs.add(new BasicNameValuePair("dgType",dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID","0"));
                nameValuePairs.add(new BasicNameValuePair("equipID",""));
                String url = "";
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
            } else if (PMCheckListDetails.getPMCheckListDetail()!=null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                dbHelper.open();
                dbHelper.clearReviewerCheclist();
                dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(),activityId);
                dbHelper.close();
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            startActivity(i);
            super.onPostExecute(result);
        }
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
                viewAssetList(selectedPos);
            }else {
                viewPMChecklist(selectedPos);
            }
            super.onPostExecute(result);
        }
    }

    public void viewAssetList(int arg2){
        Intent i = new Intent( getActivity(), EquipmentList.class );
        i.putExtra("S","V");
        i.putExtra("scheduledDate",response_ticket_list.getSite_list().get(arg2).getSCHEDULE_DATE());
        i.putExtra("siteId", response_ticket_list.getSite_list().get(arg2).getSITE_ID());
        i.putExtra("siteName", response_ticket_list.getSite_list().get(arg2).getSidName());
        i.putExtra("activityTypeId",response_ticket_list.getSite_list().get(arg2).getPARAM_ID());
        i.putExtra("paramName", response_ticket_list.getSite_list().get(arg2).getPARAM_NAME());
        i.putExtra("Status", response_ticket_list.getSite_list().get(arg2).getACTIVITY_STATUS());
        i.putExtra("dgType", response_ticket_list.getSite_list().get(arg2).getDG_TYPE());
        i.putExtra("txn", response_ticket_list.getSite_list().get(arg2).getTXN_ID());
        i.putExtra("etsSid", response_ticket_list.getSite_list().get(arg2).getEtsSid());
        i.putExtra("imageName", "");
        i.putExtra( "imgUploadFlag", response_ticket_list.getSite_list().get( arg2 ).getImgUploadflag());
        i.putExtra( "rvDate", "");
        i.putExtra( "rejRmks", "");
        i.putExtra( "rCat", "");
        startActivity(i);

    }

    public void viewPMChecklist(int arg2){
        Intent i = new Intent( getActivity(), ViewPMCheckList.class);
        String txnId = "",sDate = "",actid = "",sid = "",etsId = "",dgType = "",imguploadflag = "2";
        i.putExtra("assetId","0");
        i.putExtra("qrCode", "");
        i.putExtra("S","V");
        i.putExtra( "scheduledDate", response_ticket_list.getSite_list().get( arg2 ).getSCHEDULE_DATE());
        i.putExtra( "siteId", response_ticket_list.getSite_list().get( arg2 ).getSITE_ID());
        i.putExtra( "siteName", response_ticket_list.getSite_list().get( arg2 ).getSidName());
        i.putExtra( "activityTypeId", response_ticket_list.getSite_list().get( arg2 ).getPARAM_ID());
        i.putExtra( "paramName", response_ticket_list.getSite_list().get( arg2 ).getPARAM_NAME());
        i.putExtra( "Status", response_ticket_list.getSite_list().get( arg2 ).getACTIVITY_STATUS());
        i.putExtra( "dgType", response_ticket_list.getSite_list().get( arg2 ).getDG_TYPE());
        i.putExtra( "txn", response_ticket_list.getSite_list().get( arg2 ).getTXN_ID());
        i.putExtra( "etsSid", response_ticket_list.getSite_list().get( arg2 ).getEtsSid() );
        i.putExtra( "imgUploadFlag", response_ticket_list.getSite_list().get( arg2 ).getImgUploadflag());
        i.putExtra( "rCat","");
        i.putExtra( "rejRmks","");
        i.putExtra( "rvDate","");
        sid =  response_ticket_list.getSite_list().get( arg2 ).getSITE_ID();
        sDate = response_ticket_list.getSite_list().get( arg2 ).getSCHEDULE_DATE();
        actid = response_ticket_list.getSite_list().get( arg2 ).getPARAM_ID();
        txnId = response_ticket_list.getSite_list().get( arg2 ).getTXN_ID();
        dgType = response_ticket_list.getSite_list().get( arg2 ).getDG_TYPE();
        etsId =  response_ticket_list.getSite_list().get( arg2 ).getEtsSid();
        imguploadflag = response_ticket_list.getSite_list().get( arg2 ).getImgUploadflag();
        if (Utils.isNetworkAvailable(getActivity())) {
            new GetImage(getActivity(),i,txnId,sDate,actid,sid,etsId,dgType,imguploadflag).execute();
        } else {
            //No Internet Connection;
            Utils.toast(getActivity(), "17");
        }
    }
}
