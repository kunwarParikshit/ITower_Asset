package com.isl.approval;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanDFRCounts;
import com.isl.modal.DfrCountLists;
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
 * Created by dhakan on 6/6/2019.
 */
public class DFRApprovalCount extends Activity {
    RecyclerView list_count;
    String moduleUrl = "";
    DataBaseHelper db;
    AppPreferences mAppPreferences;
    RelativeLayout textlayout;
    LinearLayout ll_list;
    ArrayList<String> searchDDLlist;
    Spinner sp_search_by;
    TextView et_date,tv_search;
    DfrCountLists dfrList;
    DfrCountLists filtered_list;
    ArrayList<BeanDFRCounts> temp;
    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dfr_approval_count);
        temp = new ArrayList<>();

        tv_search = (TextView) findViewById(R.id.tv_search);
        Utils.msgText(DFRApprovalCount.this, "296", tv_search);

        TextView tv_brand_logo = (TextView) findViewById( R.id.tv_brand_logo );
        Utils.msgText(DFRApprovalCount.this, "408", tv_brand_logo);

        textlayout = (RelativeLayout) findViewById( R.id.textlayout);
        ll_list = (LinearLayout) findViewById( R.id.ll_list);

        mAppPreferences = new AppPreferences(DFRApprovalCount.this);
        db = new DataBaseHelper(DFRApprovalCount.this);
        db.open();
        moduleUrl = db.getModuleIP("Approval");
        db.close();
        Button bt_back = (Button) findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(DFRApprovalCount.this, Approval.class);
                startActivity(i);
                finish();

            }
        });

        if (Utils.isNetworkAvailable(DFRApprovalCount.this)) {
           new ApprovalCount(DFRApprovalCount.this).execute();
        } else {
          //No Internet Connection;
            Utils.toast(DFRApprovalCount.this, "17");
        }

        list_count = (RecyclerView)findViewById( R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( DFRApprovalCount.this,
                LinearLayoutManager.VERTICAL, false );
        list_count.setLayoutManager( horizontalLayoutManager );

       /* final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
        if (WorkFlowUtils.isNetworkAvailable(DFRApprovalCount.this)) {
            new ApprovalCount(DFRApprovalCount.this).execute();
        } else {
              Toast.makeText(DFRApprovalCount.this,"No internet connection,Try again.",Toast.LENGTH_SHORT).show();
        }
              pullToRefresh.setRefreshing(false);
            }
        });*/
       //searchFunction();
    }

    public class ApprovalCount extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String responce;
        public ApprovalCount(Context con) {
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
                String url;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("omeID",mAppPreferences.getPIOMEID()));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_dfr_count;
                }else{
                    url=moduleUrl+ WebMethods.url_dfr_count;
                }
                responce = Utils.httpPostRequest(con,url,nameValuePairs);
                //responce="{\"Output\":[{\"pendingSites\":\"6 Pending Site(s)\",\"tranDate\":\"2019-01-07T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-01-11T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-01-19T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-01-31T18:30:00.000Z\"},{\"pendingSites\":\"3 Pending Site(s)\",\"tranDate\":\"2019-02-03T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-02-04T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-05T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-02-06T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-08T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-09T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-02-10T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-11T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-12T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-14T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-15T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-16T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-02-17T18:30:00.000Z\"},{\"pendingSites\":\"4 Pending Site(s)\",\"tranDate\":\"2019-02-28T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-03-01T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-03-02T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-03-03T18:30:00.000Z\"},{\"pendingSites\":\"4 Pending Site(s)\",\"tranDate\":\"2019-03-04T18:30:00.000Z\"},{\"pendingSites\":\"2 Pending Site(s)\",\"tranDate\":\"2019-03-05T18:30:00.000Z\"},{\"pendingSites\":\"1 Pending Site(s)\",\"tranDate\":\"2019-03-14T18:30:00.000Z\"}]}\n";
                Gson gson = new Gson();
                dfrList = gson.fromJson(responce,DfrCountLists.class);
            } catch (Exception e) {
                e.printStackTrace();
                dfrList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            } if (dfrList == null) {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
                searchFunction();
            } else if (dfrList.getGetDFRCount()!=null && dfrList.getGetDFRCount().size() > 0) {
                textlayout.setVisibility(View.GONE);
                ll_list.setVisibility(View.VISIBLE);

                searchFunction();
                //list_count.setAdapter( new RecycleDFRCountView( DFRApprovalCount.this, dfrList));
            } else {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
                searchFunction();
            }
            super.onPostExecute(result);
        }
     }

    public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("All");
        searchDDLlist.add("Date");

        sp_search_by = (Spinner) findViewById(R.id.sp_search_by);
        sp_search_by.setBackgroundResource(R.drawable.doted);
        //setupUI(sp_search_by);
        addItemsOnSpinner(sp_search_by, searchDDLlist);

        et_date = (TextView) findViewById(R.id.et_date);
        et_date.setBackgroundResource( R.drawable.icon_calender);

        sp_search_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                et_date.setText("");
                if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("All")) {
                    et_date.setVisibility(View.GONE);
                    list_count.setAdapter( new RecycleDFRCountView( DFRApprovalCount.this, dfrList));
                }else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Date")) {
                    et_date.setVisibility(View.VISIBLE);
                    et_date.setBackgroundResource( R.drawable.icon_calender);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
         });


        et_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DatePickerDialog(DFRApprovalCount.this, date, calendar
                        .get( Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    public void searchData() {
        temp.clear();
        if (dfrList!=null && dfrList.getGetDFRCount()!=null && dfrList.getGetDFRCount().size() > 0) {
                    String cs = et_date.getText().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < dfrList.getGetDFRCount().size(); i++) {
                    if (dfrList.getGetDFRCount().get( i ).getTRANSACTION_DATE().trim().toLowerCase().contains( cs )) {
                        temp.add( dfrList.getGetDFRCount().get( i ) );
                    }
                }

        }

        if(temp.size()>0){
            filtered_list = new DfrCountLists();
            filtered_list.setGetDFRCount(temp);
            list_count.setAdapter( new RecycleDFRCountView(DFRApprovalCount.this, filtered_list) );
        }else{
            Toast.makeText(DFRApprovalCount.this,"No data found.",Toast.LENGTH_LONG).show();
            list_count.setAdapter(null);
        }


    }


    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.YEAR, year);
            String myFormat = "dd-MMM-yyyy"; // In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            et_date.setText(sdf.format(calendar.getTime()));
            searchData();
        }
    };

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DFRApprovalCount.this, Approval.class);
        startActivity(i);
        finish();
     }
}
