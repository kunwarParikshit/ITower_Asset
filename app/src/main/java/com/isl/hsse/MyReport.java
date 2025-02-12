package com.isl.hsse;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.MenuDetail;
import com.isl.util.Utils;

import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;
import infozech.itower.R;

public class MyReport extends AppCompatActivity {

    AppPreferences mAppPreferences;
    TextView header;
    Button btBack;
    private FragmentTabHost mTabHost;
    String report = "myTicket";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences(this);

            setContentView(R.layout.tab );
            header = (TextView) findViewById( R.id.tv_brand_logo);
            btBack = (Button)findViewById( R.id.bt_back);
            TextView tv_add_activity = (TextView) findViewById(R.id.tv_add_activity);
            tv_add_activity.setVisibility( View.GONE);
            mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
            getMenuRights();
            mTabHost.setCurrentTab(mAppPreferences.getPMBackTask());
            mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());

            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
                @Override
                public void onTabChanged(String tabId) {
                    mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());
                }});


        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    public void getMenuRights() {
        //rptType 3 means - Assigned to list, 4 means - Raised by List else based on filter parameter.
        header.setText(mAppPreferences.getModuleName());
        DataBaseHelper dbHelper = new DataBaseHelper( MyReport.this );
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( mAppPreferences.getModuleName() );
        dbHelper.close();

        if(getIntent().getExtras()!=null) {
            report = getIntent().getExtras().getString( "report" );
        }

        if(report.equalsIgnoreCase("Search")) {
            Bundle bundle = new Bundle();
            bundle.putString( HsseConstant.REPORT_NAME, "Search");
            mTabHost.addTab( mTabHost.newTabSpec( "SearchTab" )
                    .setIndicator( tabView( "SEARCH") ), ReportFragment.class, bundle);
        }else{
            for (MenuDetail menu : subMenuList) {
                String name = "";
                if (mAppPreferences.getLanCode().equalsIgnoreCase( "EN" )) {
                    name = menu.getCaption();
                } else {
                    name = Utils.msg( MyReport.this, menu.getId() );
                }
                switch (menu.getName()) {
                    case "AssignedTab":
                     if (menu.getRights().contains( "V" )) {
                         Bundle bundle = new Bundle();
                         bundle.putString( HsseConstant.REPORT_NAME, "AssignedTab" );
                         mTabHost.addTab( mTabHost.newTabSpec( "AssignedTab" )
                                 .setIndicator( tabView(name)), ReportFragment.class, bundle );
                     }
                        break;
                    case "RaisedTab":
                        if (menu.getRights().contains( "V" )) {
                            Bundle bundle1 = new Bundle();
                            bundle1.putString( HsseConstant.REPORT_NAME, "RaisedTab" );
                            mTabHost.addTab( mTabHost.newTabSpec( "RaisedTab" )
                                    .setIndicator( tabView(name) ), ReportFragment.class, bundle1 );
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Intent i = new Intent(RequestReport.this, WorkflowImpl.class);
        //startActivity(i);
        finish();
    }

    private View tabView(String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);
        RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        if (getApplicationContext().getPackageName().equalsIgnoreCase( "tawal.com.sa" )) {
            relativelayout.setBackgroundResource( R.drawable.tab_bg_selector_tawal );
        } else {
            relativelayout.setBackgroundResource( R.drawable.tab_bg_selector );
        }
        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        tv.setTypeface(Utils.typeFace( MyReport.this));
        tv.setText(name);

        if(name.equalsIgnoreCase( "SEARCH" )){
            view.setVisibility(View.GONE);
        }
        return view;
    }
}