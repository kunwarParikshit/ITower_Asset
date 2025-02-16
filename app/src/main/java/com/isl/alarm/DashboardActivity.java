package com.isl.alarm;

import static com.isl.assetManagement.constants.AppConstants.WebUrlsAndInstance.epaCapacity;
import static com.isl.assetManagement.constants.AppConstants.WebUrlsAndInstance.incidentManagementAnalysis;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.HomeActivity;
import com.isl.util.Utils;
import infozech.itower.R;
/**
 * Created by dhakan on 22-Nov-2019.
 */

/**
 * Modify by dhakan on 11-May-2020.
 * Version 1.0
 */
public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView userName, lastlogin, tv_title;
    TextView tv_epa_capacity, tv_incident_management_analysis, tv_home, tv_site_ava_summary, tv_equip_summary, tv_fre_alarm, tv_open_alarm, tv_problematic;
    private Fragment mContent;
    AppPreferences mAppPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAppPreferences = new AppPreferences(DashboardActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.my_custom_view, null);
        toolbar.addView(customView);
        setSupportActionBar(toolbar);
        ImageView iv_back = (ImageView) customView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        tv_title = (TextView) customView.findViewById(R.id.tv_title);
/* FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
fab.setOnClickListener( new View.OnClickListener() {
@Override
public void onClick(View view) {
Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
.setAction( "Action", null ).show();
}
} );*/
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new FragmentSiteAvialabilitySummary();
        tv_title.setText("Site Availability Summary"); //set text iTower
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mContent);
        ft.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.tv_userName);
        userName.setTypeface(Utils.typeFace(DashboardActivity.this));
        if (mAppPreferences.getName().length() > 0) {
            userName.setText("" + mAppPreferences.getName());
        } else {
            userName.setText("");
        }

        lastlogin = (TextView) header.findViewById(R.id.tv_lastlogin);
        lastlogin.setTypeface(Utils.typeFace(DashboardActivity.this));
        if (mAppPreferences.getLastLogin().length() > 0) {
            lastlogin.setText("Last Login" + " : " + mAppPreferences.getLastLogin());
        } else {
            lastlogin.setText("");
        }

        ListView lv_slider = (ListView) header.findViewById(R.id.lv_slider);
        SampleAdapter adapter = new SampleAdapter(DashboardActivity.this);
        adapter.add(new SampleItem("Site Availability Summary", android.R.drawable.btn_star)); //set Text iTower
        lv_slider.setAdapter(adapter);
    }

    /* @Override
    public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
    if (drawer.isDrawerOpen( GravityCompat.START )) {
    drawer.closeDrawer( GravityCompat.START );
    } else {
    super.onBackPressed();
    }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.dashboard, menu );
    return true;
    }*/
/* @Override
public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
int id = item.getItemId();
//noinspection SimplifiableIfStatement
if (id == R.id.action_settings) {
return true;
}
return super.onOptionsItemSelected( item );
}*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
// Handle navigation view item clicks here.
        int id = item.getItemId();
/*if (id == R.id.nav_camera) {
// Handle the camera action
} else if (id == R.id.nav_gallery) {
}*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchFragment(Fragment fragment, String str) {
        tv_title.setText("" + str);
        mContent = fragment;
        if (mContent != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mContent);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private class SampleItem {
        public String tag;
        public int iconRes;

        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }

    public class SampleAdapter extends ArrayAdapter<SampleItem> {
        public SampleAdapter(Context context) {
            super(context, 0);
        }

        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.custom_slider_list, null);
            }
            tv_epa_capacity = (TextView) view.findViewById(R.id.tv_epa_capacity);
            tv_incident_management_analysis = (TextView) view.findViewById(R.id.tv_incident_management_analysis);
            tv_home = (TextView) view.findViewById(R.id.tv_home);
            tv_site_ava_summary = (TextView) view.findViewById(R.id.tv_site_ava_summary);
            tv_equip_summary = (TextView) view.findViewById(R.id.tv_equip_summary);
            tv_fre_alarm = (TextView) view.findViewById(R.id.tv_fre_alarm);
            tv_open_alarm = (TextView) view.findViewById(R.id.tv_open_alarm);
            tv_problematic = (TextView) view.findViewById(R.id.tv_problematic);
            tv_epa_capacity.setOnClickListener(arg0 -> {
//                Fragment newContent = new GeckoViewFragment(epaCapacity);
//                switchFragment(newContent, "TT Dashboard:");
            });
            tv_incident_management_analysis.setOnClickListener(arg0 -> {
//                Fragment newContent = new GeckoViewFragment(incidentManagementAnalysis);
//                switchFragment(newContent, "Live SLA Dashboard");
            });
            tv_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(DashboardActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });
            tv_site_ava_summary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Fragment newContent = new FragmentSiteAvialabilitySummary();
                    switchFragment(newContent, "Site Availability Summary");
//tv_site_ava_summary.setTypeface(null, Typeface.BOLD);
/* tv_equip_summary.setTypeface(null, Typeface.NORMAL);
tv_fre_alarm.setTypeface(null, Typeface.NORMAL);
tv_open_alarm.setTypeface(null, Typeface.NORMAL);
tv_problematic.setTypeface(null, Typeface.NORMAL);*/
                }
            });
            tv_equip_summary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Fragment newContent = new FragmentEquipmentWiseSummary();
                    switchFragment(newContent, "Equipment Wise Alarm Summary");
//tv_site_ava_summary.setTypeface(null, Typeface.NORMAL);
//tv_equip_summary.setTypeface(null, Typeface.BOLD);
/*tv_fre_alarm.setTypeface(null, Typeface.NORMAL);
tv_open_alarm.setTypeface(null, Typeface.NORMAL);
tv_problematic.setTypeface(null, Typeface.NORMAL);*/
                }
            });
            tv_fre_alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Fragment newContent = new FragmentFrequentlyAlarm();
                    switchFragment(newContent, "Most Frequently Alarm");
/*tv_site_ava_summary.setTypeface(null, Typeface.NORMAL);
tv_equip_summary.setTypeface(null, Typeface.NORMAL);
//tv_fre_alarm.setTypeface(null, Typeface.BOLD);
tv_open_alarm.setTypeface(null, Typeface.NORMAL);
tv_problematic.setTypeface(null, Typeface.NORMAL);*/
                }
            });
            tv_open_alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Fragment newContent = new FragementOpenAlarm();
                    switchFragment(newContent, "Open Alarm Summary");
/*tv_site_ava_summary.setTypeface(null, Typeface.NORMAL);
tv_equip_summary.setTypeface(null, Typeface.NORMAL);
tv_fre_alarm.setTypeface(null, Typeface.NORMAL);
//tv_open_alarm.setTypeface(null, Typeface.BOLD);
tv_problematic.setTypeface(null, Typeface.NORMAL);*/
                }
            });
            tv_problematic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Fragment newContent = new FragmentProblematic();
                    switchFragment(newContent, "Critical Alarm Site");
/*tv_site_ava_summary.setTypeface(null, Typeface.NORMAL);
tv_equip_summary.setTypeface(null, Typeface.NORMAL);
tv_fre_alarm.setTypeface(null, Typeface.NORMAL);
tv_open_alarm.setTypeface(null, Typeface.NORMAL);
tv_problematic.setTypeface(null, Typeface.BOLD);*/
                }
            });
            return view;
        }
    }
}