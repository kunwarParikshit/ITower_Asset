package com.isl.incident;
import infozech.itower.R;
import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;
import com.isl.itower.HomeActivity;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import static com.google.android.material.tabs.TabLayout.MODE_FIXED;
import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;
/**
 * Created by dhakan on 1/20/2020.
 */

public class TicketDetailsTabs extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    String alarm_tt_his_tab = "V",assign_tt_details_tab = "V",audit_trail_tab = "V",
    energy_description = "V",access_description = "V",asset_description = "V";

    Button bt_back;
    TextView tv_brand_logo,tv_add_activity;
    int fixedTab = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences(TicketDetailsTabs.this);
        db = new DataBaseHelper(this);
        db.open();
        if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
            ttDetailsRight("HealthSafty");
        }else{
            ttDetailsRight("Incident");
        }

        setContentView(R.layout.ticket_details_tabs);
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        tv_brand_logo = (TextView) toolbar.findViewById( R.id.tv_brand_logo );
        bt_back = (Button) toolbar.findViewById( R.id.bt_back );
        tv_add_activity = (TextView) toolbar.findViewById(R.id.tv_add_activity);
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled( false );
        viewPager = (ViewPager) findViewById( R.id.viewpager );
        Add_Fragement_Scrool( viewPager );
        db.close();
        viewPager.setOffscreenPageLimit(1);
        tabLayout = (TabLayout) findViewById( R.id.tabs );
       	tabLayout.setupWithViewPager( viewPager );
       	if(fixedTab<=2){
            tabLayout.setTabMode(MODE_FIXED);
        }else{
            tabLayout.setTabMode(MODE_SCROLLABLE);
        }


        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
        @Override
            public void onTabSelected(TabLayout.Tab tab) {
             }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
             });

        Utils.msgText( TicketDetailsTabs.this, "554", tv_brand_logo);
        bt_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mAppPreferences.getBackModeNotifi123() == 2) {
                    finish();
                } else {
                    Intent i = new Intent(TicketDetailsTabs.this,HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });

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
            Bundle bundle = new Bundle();
            bundle.putString("id", getIntent().getExtras().getString("id"));
            bundle.putString("sid", getIntent().getExtras().getString("sid"));
            bundle.putString("tktStatus", getIntent().getExtras().getString("tktStatus"));
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAppPreferences.getBackModeNotifi123() == 2) {
            finish();
        } else {
            Intent i = new Intent(TicketDetailsTabs.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    public void ttDetailsRight(String module){
        //update_tt_tab = db.getSubMenuRight("UpdateTicketTab", "Incident");
        alarm_tt_his_tab = db.getSubMenuRight("AlarmHistoryTab", module);
        assign_tt_details_tab = db.getSubMenuRight("AssignDetailsTab", module);
        audit_trail_tab = db.getSubMenuRight("AuditTrailTab", module);
        energy_description = db.getSubMenuRight("AssignDetailsTab", module);
        access_description = db.getSubMenuRight("AssignDetailsTab", module);
        asset_description = db.getSubMenuRight("AssignDetailsTab", module);

        /*if(update_tt_tab.contains("V") || update_tt_tab.contains("M")){
            fixedTab=fixedTab+1;
        }*/

        if(alarm_tt_his_tab.contains("V")){
            fixedTab=fixedTab+1;
        }

        if(assign_tt_details_tab.contains("V")){
            fixedTab=fixedTab+1;
        }

        if(audit_trail_tab.contains("V")){
            fixedTab=fixedTab+1;
        }

        if(energy_description.contains("V")){
            fixedTab=fixedTab+1;
        }
        if(access_description.contains("V")){
            fixedTab=fixedTab+1;
        }
        if(asset_description.contains("V")){
            fixedTab=fixedTab+1;
        }

    }


    public void Add_Fragement_Scrool(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new FragTicketDetails(), Utils.msg(TicketDetailsTabs.this,"918"));

        if(alarm_tt_his_tab.contains("V")){
            adapter.addFrag(new FragAlarmHistory(), Utils.msg(TicketDetailsTabs.this,"919"));
        }

        if(assign_tt_details_tab.contains("V")){
            adapter.addFrag(new FragAssigmentDetails(), Utils.msg(TicketDetailsTabs.this,"551"));
        }

        if(audit_trail_tab.contains("V")){
            adapter.addFrag(new AuditTrailDetails(), Utils.msg(TicketDetailsTabs.this,"553"));
        }

        //if(energy_description.contains("V")){
            adapter.addFrag(new FragEnergyDescription(),  Utils.msg(TicketDetailsTabs.this,"915"));
       // }

        //if(access_description.contains("V")){
             adapter.addFrag(new FragSiteAccessDescription(),  Utils.msg(TicketDetailsTabs.this,"916"));
      //  }

        if(getIntent().getExtras().getString("tktStatus")!=null) {
            if (getIntent().getExtras().getString("tktStatus").equalsIgnoreCase("Resolved")
                    || getIntent().getExtras().getString("tktStatus").equalsIgnoreCase("Closed")) {
                //  if(asset_description.contains("V")){
                adapter.addFrag(new FragTTAssetDetails(), Utils.msg(TicketDetailsTabs.this, "917"));
            }
        }
        viewPager.setAdapter(adapter);
    }
}
