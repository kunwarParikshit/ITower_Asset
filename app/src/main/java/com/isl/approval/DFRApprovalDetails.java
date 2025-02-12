package com.isl.approval;
import com.isl.constant.WebMethods;

import infozech.itower.R;

import com.isl.modal.BeanDFRDetails;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.DFRDetails;
import com.isl.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by dhakan on 6/11/2019.
 */

public class DFRApprovalDetails extends Activity {
    RecyclerView sites_list;
    AppPreferences mAppPreferences;
    ArrayList<String> searchDDLlist;
    Spinner sp_search_by,sp_search_by_vender,sp_search_by_filler;
    LinearLayout search_by_vender,search_by_filler;
    EditText et_searchby_site;
    ArrayList<String> vendor_list,filler_list;
    DFRDetails response_list = null;
    DFRDetails filtered_list = null;
    ArrayList<BeanDFRDetails> temp;
    DataBaseHelper db;
    String moduleUrl = "",tran_date = "";
    RelativeLayout textlayout;
    LinearLayout ll_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.dfr_approval_details);
        textlayout = (RelativeLayout) findViewById( R.id.textlayout);
        ll_list = (LinearLayout) findViewById( R.id.ll_sites);
        mAppPreferences = new AppPreferences(DFRApprovalDetails.this);
        temp = new ArrayList<>();
        db = new DataBaseHelper(this);
        db.open();
        vendor_list = db.getVender("");
        filler_list = db.getFiller("6","654");
        moduleUrl = db.getModuleIP("Approval");
        db.close();
        tran_date = getIntent().getExtras().getString( "tran_date" );

        sites_list = (RecyclerView) findViewById(R.id.lv_sites);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( DFRApprovalDetails.this, LinearLayoutManager.VERTICAL, false );
        sites_list.setLayoutManager( horizontalLayoutManager );

        TextView logo = (TextView) findViewById(R.id.tv_logo);
        Utils.msgText(DFRApprovalDetails.this, "409", logo);

        TextView tv_search = (TextView) findViewById(R.id.tv_search);
        Utils.msgText(DFRApprovalDetails.this, "296", tv_search);

        if (Utils.isNetworkAvailable(DFRApprovalDetails.this)) {
            new DFRApprovalGridTask(DFRApprovalDetails.this).execute();
        } else {
            //message for No Internet Connection;
            Utils.toast(DFRApprovalDetails.this, "17");
        }

       /* final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
        if (WorkFlowUtils.isNetworkAvailable(DFRApprovalDetails.this)) {
            new DFRApprovalGridTask(DFRApprovalDetails.this).execute();
        } else {
            Toast.makeText(DFRApprovalDetails.this,"No internet connection,Try again.",Toast.LENGTH_SHORT).show();
        }
            pullToRefresh.setRefreshing(false);
            }
        });*/
        searchFunction();
        Button bt_back = (Button) findViewById(R.id.bt_back);
        Utils.msgButton(DFRApprovalDetails.this, "71", bt_back);
        bt_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(DFRApprovalDetails.this, DFRApprovalCount.class);
                startActivity(i);
                finish();
            }
        });
    }

    public class DFRApprovalGridTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String response;
        public DFRApprovalGridTask(Context con) {
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
                nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("omeID",mAppPreferences.getPIOMEID()));
                nameValuePairs.add(new BasicNameValuePair("source","M"));
                nameValuePairs.add(new BasicNameValuePair("fromDate",tran_date));
                nameValuePairs.add(new BasicNameValuePair("toDate",tran_date));
                nameValuePairs.add(new BasicNameValuePair("sID","0"));
                nameValuePairs.add(new BasicNameValuePair("dgType","0"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_dfr_approval;
                }else{
                    url=moduleUrl+ WebMethods.url_dfr_approval;
                }
                response = Utils.httpPostRequest(con,url,nameValuePairs);
                Gson gson = new Gson();
                response_list = gson.fromJson(response,DFRDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response_list == null) {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
                Intent i = new Intent(DFRApprovalDetails.this,DFRApprovalCount.class);
                startActivity(i);
                finish();
            } else if (response_list.getGetDFRApproval()!=null && response_list.getGetDFRApproval().size() > 0) {
                 textlayout.setVisibility(View.GONE);
                 ll_list.setVisibility(View.VISIBLE);
                 //searchFunction();
                 sites_list.setAdapter( new RecycleDFRDetails( DFRApprovalDetails.this, response_list));
             } else {
                textlayout.setVisibility(View.VISIBLE);
                ll_list.setVisibility(View.GONE);
                Intent i = new Intent(DFRApprovalDetails.this,DFRApprovalCount.class);
                startActivity(i);
                finish();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DFRApprovalDetails.this,DFRApprovalCount.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

     public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("Site Id");
        searchDDLlist.add("Filler");
        searchDDLlist.add("Vendor");

        sp_search_by = (Spinner) findViewById(R.id.sp_search_by);
        sp_search_by.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner(sp_search_by, searchDDLlist);

        et_searchby_site = (EditText) findViewById(R.id.et_searchby_site);
        et_searchby_site.setBackgroundResource(R.drawable.input_box );

        search_by_vender = (LinearLayout) findViewById(R.id.search_by_vender);
        sp_search_by_vender = (Spinner) findViewById(R.id.sp_search_by_vender);
        sp_search_by_vender.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner(sp_search_by_vender, vendor_list);

        search_by_filler = (LinearLayout) findViewById(R.id.search_by_filler);
        sp_search_by_filler = (Spinner) findViewById(R.id.sp_search_by_filler);
        sp_search_by_filler.setBackgroundResource(R.drawable.doted);
        addItemsOnSpinner(sp_search_by_filler, filler_list);

        sp_search_by.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                et_searchby_site.setText("");
                addItemsOnSpinner(sp_search_by_vender, vendor_list);
                addItemsOnSpinner(sp_search_by_filler, filler_list);
                hideKeyBoardEdt(sp_search_by);
                if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Site Id")) {
                     et_searchby_site.setVisibility(View.VISIBLE);
                     search_by_filler.setVisibility(View.GONE);
                     search_by_vender.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Filler")) {
                     et_searchby_site.setVisibility(View.GONE);
                     search_by_filler.setVisibility(View.VISIBLE);
                     search_by_vender.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString().equalsIgnoreCase("Vendor")) {
                     et_searchby_site.setVisibility(View.GONE);
                     search_by_filler.setVisibility(View.GONE);
                     search_by_vender.setVisibility(View.VISIBLE);
                }
               }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

         sp_search_by_vender.setOnItemSelectedListener(new OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                 if(!sp_search_by_vender.getSelectedItem().toString().trim().equalsIgnoreCase("Select Vendor")){
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

         sp_search_by_filler.setOnItemSelectedListener(new OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView,View selectedItemView, int position, long id) {
                 if(!sp_search_by_filler.getSelectedItem().toString().trim().equalsIgnoreCase("Select Filler")){
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

    }

     public void searchData(int flag) {
        temp.clear();
        if (response_list!=null && response_list.getGetDFRApproval()!=null && response_list.getGetDFRApproval().size() > 0) {
            if (flag == 1) {
                String cs = et_searchby_site.getText().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < response_list.getGetDFRApproval().size(); i++) {
                    if (response_list.getGetDFRApproval().get( i ).getSITE_ID()!=null && response_list.getGetDFRApproval().get( i ).getSITE_ID().trim().toLowerCase().contains( cs )) {
                        temp.add( response_list.getGetDFRApproval().get( i ) );
                    }
                }
            } else if (flag == 2) {
                String cs = sp_search_by_vender.getSelectedItem().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < response_list.getGetDFRApproval().size(); i++) {
                    if (response_list.getGetDFRApproval().get( i ).getOME_NAME()!=null && response_list.getGetDFRApproval().get( i ).getOME_NAME().trim().toLowerCase().equalsIgnoreCase( cs )) {
                        temp.add( response_list.getGetDFRApproval().get( i ) );
                    }
                }
            } else if (flag == 3) {
                String cs = sp_search_by_filler.getSelectedItem().toString().trim().toLowerCase( Locale.getDefault() );
                for (int i = 0; i < response_list.getGetDFRApproval().size(); i++) {
                    if (response_list.getGetDFRApproval().get( i ).getFILLER_NAME()!=null && response_list.getGetDFRApproval().get( i ).getFILLER_NAME().trim().toLowerCase().equalsIgnoreCase( cs )) {
                        temp.add( response_list.getGetDFRApproval().get( i ) );
                    }
                }
            }
        }

          filtered_list = new DFRDetails();
          filtered_list.setGetDFRApproval(temp);
         if (filtered_list.getGetDFRApproval()!=null && filtered_list.getGetDFRApproval().size() > 0) {
             textlayout.setVisibility(View.GONE);
             ll_list.setVisibility(View.VISIBLE);
             sites_list.setAdapter( new RecycleDFRDetails(DFRApprovalDetails.this, filtered_list) );
         } else {
             textlayout.setVisibility(View.VISIBLE);
             ll_list.setVisibility(View.GONE);
         }

     }

    public void unselectSearchData() {
        textlayout.setVisibility(View.GONE);
        ll_list.setVisibility(View.VISIBLE);
        sites_list.setAdapter( new RecycleDFRDetails(DFRApprovalDetails.this,response_list));
    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

   /* public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;


                }
            });
        }
    }*/

    public void  hideKeyBoardEdt(Spinner edt) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }
}
