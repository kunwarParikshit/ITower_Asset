package com.isl.audit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.HomeActivity;
import com.isl.modal.MenuDetail;
import com.isl.util.Utils;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.utils.UIUtils;

import java.util.List;

import infozech.itower.R;

public class AuditManagementActivity extends Activity {


    private TextView tvMyRequest,tvMySearch,tvHeader;
    private AppPreferences mAppPreferences;
    private WorkFlowDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_management);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.header));
        }

        initialize();

    }

    private void initialize(){
        dbHelper = new WorkFlowDatabaseHelper(AuditManagementActivity.this);
        dbHelper.open();
        mAppPreferences = new AppPreferences(AuditManagementActivity.this);
        tvMyRequest = (TextView) findViewById(R.id.tv_my_request);
        tvMySearch = (TextView) findViewById(R.id.tv_my_search);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        tvHeader.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),AuditManagementActivity.this));
        tvHeader.setText(mAppPreferences.getModuleName());

        Button iv_back = (Button) findViewById(R.id.iv_back);
        Utils.msgButton(AuditManagementActivity.this, "71", iv_back);

        getMenuRights();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(AuditManagementActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });


        tvMyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(AuditManagementActivity.this, MyAuditsActivity.class);
                startActivity(i);
            }
        });


        dbHelper.close();
    }

    public void getMenuRights() {

        int flag =0;
        dbHelper.open();

        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleName());

        for(MenuDetail menu : subMenuList){
            AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getSubMenuList().put(menu.getName(),menu);

            switch(menu.getName()){
                case "MyRequest" :
                    if(menu.getRights().contains("V")){
                        tvMyRequest.setVisibility(View.VISIBLE);
                        tvMyRequest.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),AuditManagementActivity.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMyRequest.setText(menu.getCaption());
                        } else{
                            tvMyRequest.setText(Utils.msg(AuditManagementActivity.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
                case "SearchRequest" :
                    if(menu.getRights().contains("V")){
                        tvMySearch.setVisibility(View.VISIBLE);
                        tvMySearch.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),AuditManagementActivity.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMySearch.setText(menu.getCaption());
                        } else{
                            tvMySearch.setText(Utils.msg(AuditManagementActivity.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
            }
        }

        //In case no right fo back to previous screen
        if(flag == 0){
            Utils.toast(AuditManagementActivity.this, "69");
            Intent j = new Intent(AuditManagementActivity.this, HomeActivity.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(j);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AuditManagementActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        dbHelper.close();
    }
}
