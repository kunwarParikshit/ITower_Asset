package com.isl.preventive;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;

import com.google.gson.Gson;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.IncidentMetaList;
import com.isl.sparepart.schedule.Schedule;
import com.isl.util.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import infozech.itower.R;

public class ScheduleModuleTab extends AppCompatActivity {
    private FragmentTabHost mTabHost;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    String moduleUrl = "";
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences(ScheduleModuleTab.this);
        mAppPreferences.setPMAssetBackTask(0);
        mAppPreferences.setTTModuleSelection("655");
        mAppPreferences.setPMBackTask(657);
        db = new DataBaseHelper(this);
        db.open();
        moduleUrl = db.getModuleIP("Preventive");

        if (Utils.isNetworkAvailable(ScheduleModuleTab.this)) {
            if (!metaDataType().equalsIgnoreCase("")) {
                new IncidentMetaDataTask(ScheduleModuleTab.this).execute();
            }
          }
            setContentView(R.layout.schedule_module_tab);
            mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
            Add_Fragement_Fixed();
            mTabHost.setCurrentTab(mAppPreferences.getPMBackTask());
            mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());

            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
                @Override
                public void onTabChanged(String tabId) {
                    mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());
                }});

            }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(ScheduleModuleTab.this, Schedule.class);
            startActivity(i);
            finish();

    }

    IncidentMetaList resposnse_Incident_meta = null;
    public class IncidentMetaDataTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;

        public IncidentMetaDataTask(Context con) {
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
                nameValuePairs.add(new BasicNameValuePair("module", "Incident"));
                nameValuePairs.add(new BasicNameValuePair("datatype",	metaDataType()));
                nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
                nameValuePairs.add( new BasicNameValuePair( "lat", "1" ));
                nameValuePairs.add( new BasicNameValuePair( "lng", "2" ));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_GetMetadata;
                }else{
                    url=moduleUrl+ WebMethods.url_GetMetadata;
                }
                String res = Utils.httpPostRequest(con,url, nameValuePairs);
                resposnse_Incident_meta = gson.fromJson(res,
                        IncidentMetaList.class);
            } catch (Exception e) {
                e.printStackTrace();
                resposnse_Incident_meta = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if ((resposnse_Incident_meta == null)) {
                // Toast.makeText(IncidentManagement.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
                Utils.toast(ScheduleModuleTab.this, "70");
            } else if (resposnse_Incident_meta != null) {
                if (resposnse_Incident_meta.getParam()!=null && resposnse_Incident_meta.getParam().size() > 0) {
                    db.clearInciParamData("654");
                    db.insertInciParamcData(resposnse_Incident_meta.getParam(),"654");
                    db.dataTS(null, null, "10", db.getLoginTimeStmp("10","0"),2,"0");
                }

                if (resposnse_Incident_meta.getGroups() !=null && resposnse_Incident_meta.getGroups().size() > 0) {
                    db.clearIncigrpData("654");
                    db.insertInciGrpData(resposnse_Incident_meta.getGroups(),"654");
                    db.dataTS(null, null, "15",db.getLoginTimeStmp("15","0"), 2,"0");
                }

                if(resposnse_Incident_meta.getDgtype()!=null && resposnse_Incident_meta.getDgtype().size()>0){
                    db.clearEnergyDg();
                    db.insertEnergyDG(resposnse_Incident_meta.getDgtype());
                    db.dataTS(null, null,"17",db.getLoginTimeStmp("17","0"),2,"0");
                }

            } else {
                // Toast.makeText(IncidentManagement.this,
                // "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(ScheduleModuleTab.this, "13");
            }
        }
    }

    public String metaDataType() {
        String DataType_Str = "1";
        String j = Utils.CompareDates(db.getSaveTimeStmp("10","0"),
                db.getLoginTimeStmp("10","0"), "10");

        // For froup
        String k = Utils.CompareDates(db.getSaveTimeStmp("15","0"),
                db.getLoginTimeStmp("15","0"), "15");

        String l=Utils.CompareDates(db.getSaveTimeStmp("17","0"),db.getLoginTimeStmp("17","0"),"17");


        if (j != "1") {
            DataType_Str = j;
        }

        if (k != "1") {
            if (DataType_Str == "1") {
                DataType_Str = k;
            } else {
                DataType_Str = DataType_Str + "," + k;
            }
        }

        if(l!="1"){
            if(DataType_Str =="1"){
                DataType_Str=l;
            }else{
                DataType_Str=DataType_Str+","+l;
            }
        }

        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }



    private View prepareTabView(String id) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom1, null);
        RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        if(getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")){
            relativelayout.setBackgroundResource(R.drawable.tab_bg_unselector_tawal);
        }else{
            relativelayout.setBackgroundResource(R.drawable.tab_bg_selector);
        }
        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        tv.setTypeface(Utils.typeFace(ScheduleModuleTab.this));
        String msg = Utils.msg(ScheduleModuleTab.this,id);
        tv.setText(msg.toUpperCase());

        Button bt_back = (Button) view.findViewById(R.id.bt_back);
        bt_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(ScheduleModuleTab.this, Schedule.class);
                startActivity(i);
                finish();
            }
         });

        return view;
    }

    public void Add_Fragement_Fixed(){

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("NEXT_7_DAYS")){
            mTabHost.addTab( mTabHost.newTabSpec( "Schedule" )
                    .setIndicator( prepareTabView( "219" ) ), ScheduleFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("TILL_TODAY")){
            mTabHost.addTab( mTabHost.newTabSpec( "Done" )
                    .setIndicator( prepareTabView( "223" ) ), DoneFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("TILL_YESTERDAY")){
            mTabHost.addTab( mTabHost.newTabSpec( "Missed" )
                    .setIndicator( prepareTabView( "222" ) ), MissedFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("APPROVAL_PENDING")){
            mTabHost.addTab( mTabHost.newTabSpec( "Pending" )
                    .setIndicator( prepareTabView( "220" ) ), PendingFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("TILL_TODAY_APPROVED")){
            mTabHost.addTab( mTabHost.newTabSpec( "Verify" )
                    .setIndicator( prepareTabView( "221" ) ), VerifyFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("REJECTED")){
            mTabHost.addTab( mTabHost.newTabSpec( "Reject" )
                    .setIndicator( prepareTabView( "467" ) ), RejectFragement.class, null );
        }

        if(getIntent().getExtras().getString("type").equalsIgnoreCase("RESUBMITTED")){
            mTabHost.addTab( mTabHost.newTabSpec( "ReSubmit" )
                    .setIndicator( prepareTabView( "515" ) ), ReSubmitFragment.class, null );
        }
    }
}
