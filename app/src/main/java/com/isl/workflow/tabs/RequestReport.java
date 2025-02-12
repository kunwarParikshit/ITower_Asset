package com.isl.workflow.tabs;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTabHost;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.isl.workflow.WorkflowImpl;
import com.isl.workflow.constant.Constants;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;

/**
 * Created by dhakan on 6/23/2020.
 */

public class RequestReport extends AppCompatActivity {

    AppPreferences mAppPreferences;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView header;
    Button btBack;
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences(this);

        if(tabCount() == 0){
            Utils.toast(RequestReport.this, "69");
            Intent j = new Intent(RequestReport.this, WorkflowImpl.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(j);
            finish();
        }
        else if(tabCount() > 3){
            setContentView( R.layout.scrool_tabs );
            Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
            header = (TextView) toolbar.findViewById( R.id.tv_brand_logo );
            btBack = (Button) toolbar.findViewById( R.id.bt_back );
            setSupportActionBar( toolbar );
            getSupportActionBar().setDisplayShowTitleEnabled( false );
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            int mode = 1;
            if(getIntent().getExtras()!=null){
                mode = 3;
            }
            getMenuRights(mode = 3);
            viewPager.setOffscreenPageLimit(1);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabMode( MODE_SCROLLABLE );
            tabLayout.setupWithViewPager( viewPager );

            tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {}

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
        else{
            setContentView(R.layout.tab );
            header = (TextView) findViewById( R.id.tv_brand_logo);
            btBack = (Button)findViewById( R.id.bt_back);
            TextView tv_add_activity = (TextView) findViewById(R.id.tv_add_activity);
            tv_add_activity.setVisibility(View.GONE);
            mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
            int mode = 0;
            if(getIntent().getExtras()!=null){
                mode = 3;
            }
            getMenuRights(mode);
            mTabHost.setCurrentTab(mAppPreferences.getPMBackTask());
            mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());

            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
                @Override
                public void onTabChanged(String tabId) {
                    mAppPreferences.setPMBackTask(mTabHost.getCurrentTab());
                }});
        }

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    public void getMenuRights(int mode) {
        header.setText(mAppPreferences.getModuleName());
        DataBaseHelper dbHelper = new DataBaseHelper( RequestReport.this );
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( mAppPreferences.getModuleName() );
        dbHelper.close();
        ViewPagerAdapter adapter = new ViewPagerAdapter( getSupportFragmentManager() );

        if (mode == 0 || mode == 1){
            for (MenuDetail menu : subMenuList) {
                String name = "";
                if (mAppPreferences.getLanCode().equalsIgnoreCase( "EN" )) {
                    name = menu.getCaption();
                } else {
                    name = Utils.msg( RequestReport.this, menu.getId() );
                }
                switch (menu.getName()) {
                    case "AssignedTab":
                        if (menu.getRights().contains( "V" )) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.TXN_SOURCE, "AssignHistoryTab");
                            //mode
                            if (mode == 0) {

                                mTabHost.addTab( mTabHost.newTabSpec( "AssignedTab" )
                                        .setIndicator(tabView(name,0)), MyRequest.class, bundle );
                            } else {
                                MyRequest myRequest = new MyRequest();
                                myRequest.setArguments(bundle);
                                adapter.addFrag( myRequest,name );
                            }
                        }
                        break;
                    case "RaisedTab":
                        if (menu.getRights().contains( "V" )) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.TXN_SOURCE, "RaisedTab");

                            if (mode == 0) {

                                mTabHost.addTab( mTabHost.newTabSpec( "RaisedTab" )
                                        .setIndicator( tabView( name ,0) ), MyRequest.class, bundle );
                            } else {
                                MyRequest myRequest = new MyRequest();
                                myRequest.setArguments(bundle);
                                adapter.addFrag( myRequest, name );
                            }
                        }
                        break;
                }
            }
        }else{
            mTabHost.addTab( mTabHost.newTabSpec( "MyReport" )
                    .setIndicator( tabView("My Report",1)), MyReport.class, null );
        }


        if(mode ==1){
            viewPager.setAdapter(adapter);
        }

    }

    @Override
    public void onBackPressed() {
        //Intent i = new Intent(RequestReport.this, WorkflowImpl.class);
        //startActivity(i);
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public int tabCount(){
        int count = 0;

        if(getIntent().getExtras()!=null){
            return 1;
        }

        DataBaseHelper dbHelper = new DataBaseHelper(RequestReport.this);
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight(mAppPreferences.getModuleName());
        dbHelper.close();
        for(MenuDetail menu : subMenuList){
            switch(menu.getName()){
                case "AssignedTab" :
                    if(menu.getRights().contains("V")){
                        count = count+ 1;
                    }
                    break;
                case "RaisedTab" :
                    if(menu.getRights().contains("V")){
                        count = count+ 1;
                    }
                    break;
            }
        }
        return count;
    }

    private View tabView(String name,int mode) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);
        RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);

        if(mode==0) {
            if (getApplicationContext().getPackageName().equalsIgnoreCase( "tawal.com.sa" )) {
                relativelayout.setBackgroundResource( R.drawable.tab_bg_selector_tawal );
            } else if (mode == 1) {
                relativelayout.setBackgroundResource( R.drawable.tab_bg_selector );
            }
        }else{
            relativelayout.setVisibility(View.GONE);
            header.setText("My Report");
        }

        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        tv.setTypeface(Utils.typeFace( RequestReport.this));
        tv.setText(name);
        return view;
    }
}
