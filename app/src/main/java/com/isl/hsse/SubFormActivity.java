package com.isl.hsse;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.form.WorkFlowForm;

import java.util.HashMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;
import infozech.itower.R;

public class SubFormActivity extends FragmentActivity implements View.OnClickListener {
    private static AppPreferences mAppPreferences;
    FragmentTabHost mTabHost;
    HashMap<String,String> tranData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.tab );
        mAppPreferences = new AppPreferences( SubFormActivity.this);

        TextView header=(TextView)findViewById(R.id.tv_brand_logo);
        header.setText( AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getCaption());

        Button btBack = (Button)findViewById( R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    backScreenConfirmation("291","63","64");
             }
        });

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        if(getIntent().getExtras()!=null){
            tranData = (HashMap<String, String>) getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS);
            mTabHost.addTab( mTabHost.newTabSpec( "SubForm" )
                    .setIndicator( tabView("Sub Form",1)),
                    WorkFlowForm.class, null );

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    private View tabView(String name,int mode) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_header_custom, null);
        RelativeLayout relativelayout = (RelativeLayout) view.findViewById(R.id.relativelayout);
        relativelayout.setVisibility(View.GONE);
        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        tv.setTypeface( Utils.typeFace( SubFormActivity.this));
        tv.setText(name);
        return view;
    }

    @Override
    public void onBackPressed() {
            backScreenConfirmation("291","63","64");
         }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    public void backScreenConfirmation(String confirmID,String primaryBt,String secondaryBT) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog( SubFormActivity.this, R.style.FullHeightDialog);
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
        tv_header.setTypeface( Utils.typeFace( SubFormActivity.this));
        positive.setTypeface( Utils.typeFace( SubFormActivity.this) );
        negative.setTypeface( Utils.typeFace( SubFormActivity.this ) );
        title.setTypeface( Utils.typeFace( SubFormActivity.this ) );
        title.setText( Utils.msg( SubFormActivity.this,confirmID));
        positive.setText( Utils.msg( SubFormActivity.this,primaryBt));
        negative.setText( Utils.msg( SubFormActivity.this,secondaryBT));
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                Intent i = new Intent(SubFormActivity.this, HsseFrame.class );
                tranData.put( Constants.FORM_KEY,"EditHSSERequest");
                i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
                startActivity( i );
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
}

