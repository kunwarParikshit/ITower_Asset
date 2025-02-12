package com.isl.approval;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSiteSpare;
import com.isl.modal.BeanSpareView;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import infozech.itower.R;
/**
 * Created by dhakan on 6/11/2019.
 */
public class FragApprovedSparePart extends Fragment {
    RecyclerView sites_list;
    View view;
    AppPreferences mAppPreferences;
    BeanSiteSpare response_spare_list = null;
    BeanSiteSpare filtered_list = null;
    ArrayList<BeanSpareView> temp;
    DataBaseHelper db;
    Spinner sp_search_by,sp_tran_type,sp_vender;
    LinearLayout ll_tran_type,ll_vender,ll_list;
    HorizontalScrollView hh_date;
    RelativeLayout textlayout;
    TextView et_date_from,et_date_till,tv_search_by_logo;
    EditText et_searchby_site;
    Calendar myCalendar1 = Calendar.getInstance();
    Calendar myCalendar2 = Calendar.getInstance();
    Date d3,d4;
    String moduleUrl = "",fromDateCal="",toDateCal="";
    ArrayList<String> searchDDLlist,tranType_list,vendor_list;

    public FragApprovedSparePart() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.frag_pending_spare_part,container, false);
        textlayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        ll_list = (LinearLayout) view.findViewById( R.id.ll_sites);
        temp = new ArrayList<>();
        sites_list = (RecyclerView) view.findViewById(R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
        sites_list.setLayoutManager( horizontalLayoutManager );

        et_searchby_site = (EditText) view.findViewById( R.id.et_searchby_site );
        et_searchby_site.setBackgroundResource( R.drawable.input_box );

        sp_search_by = (Spinner) view.findViewById( R.id.sp_search_by );
        sp_search_by.setBackgroundResource( R.drawable.doted );

        sp_tran_type =  (Spinner) view.findViewById( R.id.sp_tran_type );
        sp_tran_type.setBackgroundResource( R.drawable.doted );

        sp_vender =  (Spinner) view.findViewById( R.id.sp_vender );
        sp_vender.setBackgroundResource( R.drawable.doted );

        ll_tran_type = (LinearLayout) view.findViewById( R.id.ll_tran_type );
        ll_vender = (LinearLayout) view.findViewById( R.id.ll_vender );
        hh_date = (HorizontalScrollView) view.findViewById( R.id.hh_date );

        tv_search_by_logo = (TextView) view.findViewById(R.id.tv_search_by_logo);
        Utils.msgText(getActivity(), "296", tv_search_by_logo);

        mAppPreferences = new AppPreferences(getActivity());
        db = new DataBaseHelper(getActivity());
        db.open();
        vendor_list = db. getVender("");
        moduleUrl = db.getModuleIP("Approval");
        db.close();

        if (Utils.isNetworkAvailable(getActivity())) {
            new GetPendingSpareTask(getContext(),"","").execute();
        } else {
            //No Internet Connection;
            Utils.toast(getActivity(), "17");
        }
       /* final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (WorkFlowUtils.isNetworkAvailable(getActivity())) {
                    new GetPendingSpareTask(getContext(),"","").execute();
                } else {
                    Toast.makeText(getActivity(),"No internet connection,Try again.",Toast.LENGTH_SHORT).show();
                }
                pullToRefresh.setRefreshing(false);
            }
        });*/

        searchFunction();
        return view;
    }

    public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("Site Id");
        searchDDLlist.add("Transaction Type");
        searchDDLlist.add("Transaction Date");
        searchDDLlist.add("Vendor");
        tranType_list = new ArrayList<String>();
        tranType_list.add("Select Transaction Type");
        tranType_list.add("Trouble Ticket");
        tranType_list.add("Preventive Maintenance");

        et_searchby_site.setBackgroundResource( R.drawable.input_box );

        sp_search_by.setBackgroundResource( R.drawable.doted );
        addItemsOnSpinner(sp_search_by, searchDDLlist);

        sp_tran_type.setBackgroundResource( R.drawable.doted );
        addItemsOnSpinner(sp_tran_type, tranType_list);


        sp_vender.setBackgroundResource( R.drawable.doted );
        addItemsOnSpinner(sp_vender, vendor_list);

        sp_search_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                sp_tran_type.setBackgroundResource( R.drawable.doted );
                sp_vender.setBackgroundResource( R.drawable.doted );
                sp_search_by.setBackgroundResource( R.drawable.doted );
                et_searchby_site.setBackgroundResource( R.drawable.input_box );
                et_searchby_site.setText("");
                addItemsOnSpinner(sp_vender, vendor_list);
                addItemsOnSpinner(sp_tran_type, tranType_list);
                hideKeyBoardEdt(sp_search_by);
                if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Site Id")) {
                    et_searchby_site.setVisibility(View.VISIBLE);
                    et_searchby_site.setCursorVisible(true);
                    ll_tran_type.setVisibility(View.GONE);
                    ll_vender.setVisibility(View.GONE);
                    hh_date.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Transaction Type")) {
                    et_searchby_site.setVisibility(View.GONE);
                    ll_tran_type.setVisibility(View.VISIBLE);
                    ll_vender.setVisibility(View.GONE);
                    hh_date.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Transaction Date")) {
                    et_searchby_site.setVisibility(View.GONE);
                    ll_tran_type.setVisibility(View.GONE);
                    ll_vender.setVisibility(View.GONE);
                    hh_date.setVisibility(View.VISIBLE);
                     } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Vendor")) {
                    et_searchby_site.setVisibility(View.GONE);
                    ll_tran_type.setVisibility(View.GONE);
                    ll_vender.setVisibility(View.VISIBLE);
                    hh_date.setVisibility(View.GONE);
                }
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

        sp_vender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                if(!sp_vender.getSelectedItem().toString().trim().equalsIgnoreCase("Select Vendor")){
                    searchData(2);
                }else{
                    unselectSearchData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        sp_tran_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                if(!sp_tran_type.getSelectedItem().toString().trim().equalsIgnoreCase("Select Transaction Type")){
                    searchData(3);
                }else{
                    unselectSearchData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        TextView tv_apply = (TextView) view.findViewById(R.id.tv_apply);
        et_date_from = (TextView) view.findViewById(R.id.et_date_from);
        et_date_from.setBackgroundResource( R.drawable.icon_calender);
        et_date_from.setText(fromDateCal);

        et_date_till = (TextView) view.findViewById(R.id.et_date_till);
        et_date_till.setBackgroundResource( R.drawable.icon_calender);
        et_date_till.setText(toDateCal);

        et_date_from.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(getActivity(), fromDate, myCalendar1
                        .get( Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_date_till.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(getActivity(), toDate, myCalendar2
                        .get( Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        tv_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                fromDateCal = et_date_from.getText().toString();
                toDateCal = et_date_till.getText().toString();
                int a=0;
                if(fromDateCal.length()==0){
                    a=1;
                    Toast.makeText(getActivity(),"From Date cannot be blank.",Toast.LENGTH_SHORT).show();
                }else if(toDateCal.length()==0){
                    a=1;
                    Toast.makeText(getActivity(),"To Date cannot be blank.",Toast.LENGTH_SHORT).show();
                }else if(Utils.diffDays(fromDateCal,toDateCal)>mAppPreferences.getSearchTTDateRange()){
                    a=1;
                    Toast.makeText(getActivity(),"From Date and To Date Difference cannot be greater than "+mAppPreferences.getSearchTTDateRange()+" Days",Toast.LENGTH_LONG).show();
                }else if(fromDateCal.length()!=0 && toDateCal.length()!=0){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
                    try{
                        d3 = sdf.parse(fromDateCal);
                        d4 = sdf.parse(toDateCal);
                    }catch(Exception  e){

                    }
                    if (Utils.checkDateCompare(d3, d4)) {
                        a=0;
                    } else {
                        a=1;
                        Toast.makeText(getActivity(),"From Date cannot be greater than To Date",Toast.LENGTH_LONG).show();
                    }
                }

                if(a==0){
                    //dateDialog.cancel();
                    if (Utils.isNetworkAvailable(getActivity())) {
                        new GetPendingSpareTask(getContext(),fromDateCal,toDateCal).execute();
                    } else {
                        //No Internet Connection;
                        Utils.toast(getActivity(), "17");
                    }
                }
            }
        });
    }

    public void searchData(int flag) {
        temp.clear();
        if (response_spare_list!=null && response_spare_list.getGetSparePart()!=null
                && response_spare_list.getGetSparePart().size() > 0) {
            if (flag == 1) {
                String cs = et_searchby_site.getText().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < response_spare_list.getGetSparePart().size(); i++) {
                    if (response_spare_list.getGetSparePart().get(i).getSid()!=null
                            && response_spare_list.getGetSparePart().get(i).getSid().trim().toLowerCase().contains( cs )) {
                        temp.add(response_spare_list.getGetSparePart().get(i) );
                    }
                }
            } else if (flag == 2) {
                String cs = sp_vender.getSelectedItem().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < response_spare_list.getGetSparePart().size(); i++) {
                    if (response_spare_list.getGetSparePart().get( i ).getVendor()!=null
                            && response_spare_list.getGetSparePart().get( i ).getVendor().trim().toLowerCase().equalsIgnoreCase( cs )) {
                        temp.add(response_spare_list.getGetSparePart().get( i ) );
                    }
                }
            } else if (flag == 3) {
                String cs = sp_tran_type.getSelectedItem().toString().trim().toLowerCase( Locale.getDefault());
                for (int i = 0; i < response_spare_list.getGetSparePart().size(); i++) {
                    if (response_spare_list.getGetSparePart().get( i ).getTransactionType()!=null
                            &&response_spare_list.getGetSparePart().get( i ).getTransactionType().trim().toLowerCase().equalsIgnoreCase( cs )) {
                        temp.add(response_spare_list.getGetSparePart().get( i ) );
                    }
                }


            }
        }
        filtered_list = new BeanSiteSpare();
        filtered_list.setGetSparePart(temp);
        if (filtered_list.getGetSparePart()!=null && filtered_list.getGetSparePart().size() > 0) {
            textlayout.setVisibility(View.GONE);
            ll_list.setVisibility(View.VISIBLE);
            sites_list.setAdapter( new RecycleSparePendingAdapter(getActivity(),filtered_list));
        } else {
            textlayout.setVisibility(View.VISIBLE);
            ll_list.setVisibility(View.GONE);
        }
    }

    public void unselectSearchData() {
        textlayout.setVisibility(View.GONE);
        ll_list.setVisibility(View.VISIBLE);
        sites_list.setAdapter( new RecycleSparePendingAdapter(getActivity(),response_spare_list));
    }

    /*public void dateDialog() {
        final Dialog dateDialog = new Dialog(getActivity());
        dateDialog.setCanceledOnTouchOutside(false);
        dateDialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        dateDialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dateDialog.setContentView(R.layout.date );
        final Window window_SignIn = dateDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity( Gravity.CENTER);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Drawable d = new ColorDrawable( Color.parseColor("#CC000000"));
        dateDialog.getWindow().setBackgroundDrawable(d);
        //dateDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dateDialog.show();
        ImageView iv_cancel = (ImageView) dateDialog.findViewById(R.id.iv_cancel);
        TextView tv_apply = (TextView) dateDialog.findViewById(R.id.tv_apply);
        et_date_from = (TextView) dateDialog.findViewById(R.id.et_date_from);
        et_date_from.setBackgroundResource( R.drawable.calender );
        et_date_from.setText(fromDateCal);

        et_date_till = (TextView) dateDialog.findViewById(R.id.et_date_till);
        et_date_till.setBackgroundResource( R.drawable.calender );
        et_date_till.setText(toDateCal);

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dateDialog.cancel();
            }
        });

        et_date_from.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(getActivity(), fromDate, myCalendar1
                        .get( Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_date_till.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(getActivity(), toDate, myCalendar2
                        .get( Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        tv_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                fromDateCal = et_date_from.getText().toString();
                toDateCal = et_date_till.getText().toString();
                int a=0;
                if(fromDateCal.length()==0){
                    a=1;
                    Toast.makeText(getActivity(),"From Date cannot be blank.",Toast.LENGTH_SHORT).show();
                }else if(toDateCal.length()==0){
                    a=1;
                    Toast.makeText(getActivity(),"To Date cannot be blank.",Toast.LENGTH_SHORT).show();
                }else if(fromDateCal.length()!=0 && toDateCal.length()!=0){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    try{
                        d3 = sdf.parse(fromDateCal);
                        d4 = sdf.parse(toDateCal);
                    }catch(Exception  e){

                    }
                    if (WorkFlowUtils.checkDateCompare(d3, d4)) {
                        a=0;
                    } else {
                        a=1;
                        Toast.makeText(getActivity(),"From Date cannot be greater than To Date",Toast.LENGTH_LONG).show();
                    }
                }

                if(a==0){
                    dateDialog.cancel();
                    if (WorkFlowUtils.isNetworkAvailable(getActivity())) {
                        new GetPendingSpareTask(getContext(),fromDateCal,toDateCal).execute();
                    } else {
                        //No Internet Connection;
                        WorkFlowUtils.toast(getActivity(), "17");
                    }
                }
            }
        });
    }*/


    DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar1.set(Calendar.MONTH, monthOfYear);
            myCalendar1.set(Calendar.YEAR, year);
            String myFormat = "dd-MMM-yyyy"; // In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            et_date_from.setText(sdf.format(myCalendar1.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.YEAR, year);
            String myFormat = "dd-MMM-yyyy"; // In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            et_date_till.setText(sdf.format(myCalendar2.getTime()));
        }
    };


    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public class GetPendingSpareTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String fromDate="",toDate="";
        public GetPendingSpareTask(Context con,String fromDate,String toDate) {
            this.con = con;
            this.fromDate = fromDate;
            this.toDate = toDate;
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("vendorID",mAppPreferences.getPIOMEID()));
                nameValuePairs.add(new BasicNameValuePair("siteID",""));
                nameValuePairs.add(new BasicNameValuePair("siteAreaType",""));
                nameValuePairs.add(new BasicNameValuePair("tranType","0"));//-- 1 -> TT,2-> PM 0-< all
                nameValuePairs.add(new BasicNameValuePair("tDate",toDate));
                nameValuePairs.add(new BasicNameValuePair("fDate",fromDate));
                nameValuePairs.add(new BasicNameValuePair("appType","2")); //(1--PENDING,2-APPROVED,3-REJECTED )
                nameValuePairs.add(new BasicNameValuePair("repID","925"));
                nameValuePairs.add(new BasicNameValuePair("txnID",""));
                nameValuePairs.add(new BasicNameValuePair("source","M"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_get_spare_part;
                }else{
                    url=moduleUrl+ WebMethods.url_get_spare_part;
                }
                String response = Utils.httpPostRequest(con,url, nameValuePairs);
                Gson gson = new Gson();
                response_spare_list = gson.fromJson(response,BeanSiteSpare.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_spare_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response_spare_list == null) {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
            } else if (response_spare_list.getGetSparePart()!=null && response_spare_list.getGetSparePart().size() > 0) {
                textlayout.setVisibility(View.GONE);
                ll_list.setVisibility(View.VISIBLE);
                //searchFunction();
                sites_list.setAdapter( new RecycleSparePendingAdapter(getActivity(),response_spare_list));
            } else {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

   /* public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }
    }*/

    public void  hideKeyBoardEdt(Spinner edt) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }
}
