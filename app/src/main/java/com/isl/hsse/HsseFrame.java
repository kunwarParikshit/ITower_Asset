package com.isl.hsse;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.MenuDetail;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.form.WorkFlowForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import infozech.itower.R;
import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;

public class HsseFrame extends AppCompatActivity implements View.OnClickListener {
    private static AppPreferences mAppPreferences;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    HashMap<String, String> tranData;
    WorkFlowForm addRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        addRequest = new WorkFlowForm();
       // addRequest.onCreate(savedInstanceState);
        setContentView( R.layout.scrool_tabs );
        mAppPreferences = new AppPreferences( HsseFrame.this );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView header = (TextView) toolbar.findViewById( R.id.tv_brand_logo );
        header.setText( AppConstants.moduleList.get( mAppPreferences.getModuleIndex() ).getCaption() );
        Button btBack = (Button) toolbar.findViewById( R.id.bt_back );
        btBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                backScreenConfirmation( "291", "63", "64" );
            }
        } );

        TextView tv_add_activity = (TextView) toolbar.findViewById(R.id.tv_add_activity);
        tv_add_activity.setVisibility(View.GONE);
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled( false );
        tabLayout = (TabLayout) findViewById( R.id.tabs );
        tabLayout.setTabMode( MODE_SCROLLABLE );
        viewPager = (ViewPager) findViewById( R.id.viewpager );
        getTabRight();
        switch (tranData.get( Constants.NEXT_TAB_SELECT )) {
            case "HSSE":
                viewPager.setCurrentItem(0);
                break;
            case "Personal Details":
                viewPager.setCurrentItem(1);
                break;
            case "Log Book":
                viewPager.setCurrentItem(2);
                break;
            case "Pre Action":
                viewPager.setCurrentItem(3);
                break;
            case "Exit":
                Intent i = new Intent(HsseFrame.this, Hsse.class);
                startActivity(i);
                finish();
                break;
        }
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //WorkFlowForm addRequest = new WorkFlowForm();
        addRequest.onActivityResult(requestCode, resultCode, data);
        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        //fragment.onActivityResult(requestCode, resultCode, data);
    }

  @Override
    public void onBackPressed() {
             backScreenConfirmation("291","63","64");
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(HsseAddUpdateFrame.this,"detailsaaa",Toast.LENGTH_LONG).show();
        // TODO Auto-generated method stub
    }

    public void backScreenConfirmation(String confirmID,String primaryBt,String secondaryBT) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog( HsseFrame.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView( R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( HsseFrame.this));
        positive.setTypeface( Utils.typeFace( HsseFrame.this) );
        negative.setTypeface( Utils.typeFace( HsseFrame.this ) );
        title.setTypeface( Utils.typeFace( HsseFrame.this ) );
        title.setText( Utils.msg( HsseFrame.this,confirmID));
        positive.setText( Utils.msg( HsseFrame.this,primaryBt));
        negative.setText( Utils.msg( HsseFrame.this,secondaryBT));
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                Intent i = new Intent(HsseFrame.this, Hsse.class);
                startActivity(i);
                finish();
            }
        } );

        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        } );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getTabRight() {
        if (getIntent().getExtras() != null) {
            tranData = (HashMap<String, String>) getIntent()
                    .getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS );//tranData

            DataBaseHelper dbHelper = new DataBaseHelper( HsseFrame.this );
            dbHelper.open();
            List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( mAppPreferences.getModuleName() );
            dbHelper.close();
            ViewPagerAdapter adapter = new ViewPagerAdapter( getSupportFragmentManager() );

            for (MenuDetail menu : subMenuList) {
                String name = "";
                if (mAppPreferences.getLanCode().equalsIgnoreCase( "EN" )) {
                    name = menu.getCaption();
                } else {
                    name = Utils.msg( HsseFrame.this, menu.getId() );
                }

                switch (menu.getName()) {
                    case "Add Request Tab":
                        if (menu.getRights().toString().contains( "A" )
                                || menu.getRights().toString().contains( "V" )
                                || menu.getRights().toString().contains( "E" )) {
                            //mTabHost.addTab( mTabHost.newTabSpec( "HSSE" )
                            //        .setIndicator( tabView(name)), WorkFlowForm.class, null );
                            //WorkFlowForm addRequest = new WorkFlowForm();
                            adapter.addFrag( addRequest, name);
                        }
                        break;
                    case "Person Details Tab":
                        if (menu.getRights().toString().contains( "A" )
                                || menu.getRights().toString().contains( "V" )
                                || menu.getRights().toString().contains( "E" )) {
                            // mTabHost.addTab( mTabHost.newTabSpec( "Personal Details" )
                            //         .setIndicator( tabView(name)), PersonDetailsFragment.class, null );
                            PersonDetailsFragment addPerson = new PersonDetailsFragment();
                            adapter.addFrag( addPerson, name);
                        }
                        break;
                    case "Log Book Tab":
                        if (menu.getRights().toString().contains( "A" )
                                || menu.getRights().toString().contains( "V" )
                                || menu.getRights().toString().contains( "E" )) {
                            LogBookFragment addLogBook = new LogBookFragment();
                            adapter.addFrag( addLogBook, name);
                            //mTabHost.addTab( mTabHost.newTabSpec( "Log Book" )
                            //        .setIndicator( tabView(name)), LogBookFragment.class, null );
                        }
                        break;
                    case "Corrective/Preventive Actions Tab":
                        if (menu.getRights().toString().contains( "A" )
                                || menu.getRights().toString().contains( "V" )
                                || menu.getRights().toString().contains( "E" )) {
                            PreventiveActionsFragment addAction = new PreventiveActionsFragment();
                            adapter.addFrag( addAction, name);
                            //mTabHost.addTab( mTabHost.newTabSpec( "Pre Action" )
                            //        .setIndicator( tabView(name)), PreventiveActionsFragment.class, null );
                        }
                        break;
                    case "Audit Log Tab":
                        if (menu.getRights().toString().contains( "A" )
                                || menu.getRights().toString().contains( "V" )
                                || menu.getRights().toString().contains( "E" )) {
                            AuditLogFragment auditLog = new AuditLogFragment();
                            adapter.addFrag( auditLog, name);
                        }
                        break;
                }
            }
            viewPager.setAdapter( adapter );
            tabLayout.setupWithViewPager( viewPager );
            tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1011"))
                            && tranData.get(Constants.PROCESS_INSTANCE_ID)!=null
                            && tranData.get(Constants.PROCESS_INSTANCE_ID).length()>0){
                        tranData.put(Constants.NEXT_TAB_SELECT,"HSSE");
                        tranData.put(Constants.OPERATION,"E");
                        tranData.put(Constants.FORM_KEY,"EditHSSERequest");
                        tranData.put(Constants.TXN_SOURCE,"");
                    }else if(tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1011"))
                            && tranData.get(Constants.PROCESS_INSTANCE_ID)!=null
                            && tranData.get(Constants.PROCESS_INSTANCE_ID).length()==0){
                        tranData.put( Constants.FORM_KEY,"AddHSSERequest");
                        tranData.put(Constants.TXN_ID,null);
                        tranData.put(Constants.OPERATION,"A");
                        tranData.put(Constants.TXN_SOURCE,"AddRequest");
                        tranData.put(Constants.PROCESS_INSTANCE_ID,"");
                        tranData.put(Constants.TASK_ID,"");
                        tranData.put(Constants.REQUEST_FLAG,"");
                        tranData.put(Constants.NEXT_TAB_SELECT,"HSSE");
                        tranData.put(HsseConstant.OLD_TKT_STATUS,"1691");
                        tranData.put(HsseConstant.OLD_GRP,"");
                        tranData.put(Constants.TXN_SOURCE,"");
                    }else if(( tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1018"))
                            ||tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1019"))
                            ||tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1020"))
                            ||tab.getText().toString().equalsIgnoreCase(Utils.msg(HsseFrame.this,"1021")))
                            && tranData.get(Constants.PROCESS_INSTANCE_ID)!=null
                            && tranData.get(Constants.PROCESS_INSTANCE_ID).length()==0
                            && tranData.get(Constants.OPERATION)!=null
                            && tranData.get(Constants.OPERATION).equalsIgnoreCase("A")){
                           Utils.toastMsg(HsseFrame.this,"Please first add HSSE Ticket before adding "+tab.getText().toString()+".");
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                   // Utils.toastMsg(HsseFrame.this,"reselected == "+tab.getText()+".");

                }
            });

        }
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
}
