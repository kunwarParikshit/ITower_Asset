package com.isl.hsse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.HomeActivity;
import com.isl.modal.MenuDetail;
import com.isl.util.Utils;
import com.isl.workflow.InitiateWorkFlowProcess;
import com.isl.workflow.MetaData;
import com.isl.workflow.ProcessInitialization;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.ProcessStartResponse;
import com.isl.workflow.utils.UIUtils;

import java.util.HashMap;
import java.util.List;

import infozech.itower.R;

public class Hsse extends Activity implements ProcessInitialization {

    TextView tvAddRequest,tvMyRequest,tvMySearch,tvHeader;
    AppPreferences mAppPreferences;
    WorkFlowDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_hsse);
        dbHelper = new WorkFlowDatabaseHelper( Hsse.this);
        dbHelper.open();

        initialize();
        AppConstants.moduleList.get(mAppPreferences.getModuleIndex())
                .setDataTypeId("-33,-61,-162,-163,-164,-165,-166,-167,-168,-169,-170,-172,-173,-1231,-174,14,15,19,21,22,23,200,-1280");


      /*  AppConstants.moduleList.get(mAppPreferences.getModuleIndex())
               .setDataTypeId("-33,-61,-162,-163,-164,-165,-166,-167,-168,-169,-170,-172,-173,-174,-1231,14,15,19,21,22,23,200");
    */
        try{
        //String modifiedMetaData = dbHelper.getModifedMetaDataTypeIds( AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getDataTypeId());
        String modifiedMetaData = "-33,-61,-162,-163,-164,-165,-166,-167,-168,-169,-170,-172,-173,-1231,-174,14,15,19,21,22,23,200,-1280";
            System.out.println("********************modifiedMetaData - "+modifiedMetaData);

            if (Utils.isNetworkAvailable( Hsse.this)) {
                //if (modifiedMetaData!=null && modifiedMetaData.trim().length()>0) {
                    new MetaData( Hsse.this,modifiedMetaData,mAppPreferences.getModuleIndex()).execute();
               // }
            } else {
                Utils.toast( Hsse.this, "17");
                Intent i = new Intent( Hsse.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        } catch(Exception exp){
            //exp.printStackTrace();
        }

        dbHelper.close();
    }

    private void initialize(){

        mAppPreferences = new AppPreferences( Hsse.this);
        tvAddRequest = (TextView) findViewById( R.id.tv_add_request);
        tvMyRequest = (TextView) findViewById( R.id.tv_my_request);
        tvMySearch = (TextView) findViewById( R.id.tv_my_search);
        tvHeader = (TextView) findViewById( R.id.tv_header);
        tvHeader.setTypeface( UIUtils.typeface(mAppPreferences.getLanCode(), Hsse.this));
        tvHeader.setText(mAppPreferences.getModuleName());

        Button iv_back = (Button) findViewById( R.id.iv_back);
        Utils.msgButton( Hsse.this, "71", iv_back);
        try {
            getMenuRights();
        }catch (Exception e){

        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent( Hsse.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nextActivity();
            }
        });

        tvMyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent( Hsse.this, MyReport.class);
                startActivity(i);
            }
        });

        tvMySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent( Hsse.this, SearchHsseTicket.class);
                startActivity(i);
            }
        });
    }

    public void getMenuRights() {

        int flag =0;
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleName());

        for(MenuDetail menu : subMenuList){
            AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getSubMenuList().put(menu.getName(),menu);

            switch(menu.getName()){
                case "AddRequest" :
                    if(menu.getRights().contains("A")){
                        tvAddRequest.setVisibility(View.VISIBLE);
                        tvAddRequest.setTypeface( UIUtils.typeface(mAppPreferences.getLanCode(), Hsse.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvAddRequest.setText(menu.getCaption());
                        } else{
                            tvAddRequest.setText( Utils.msg( Hsse.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
                case "MyRequest" :
                    if(menu.getRights().contains("V")){
                        tvMyRequest.setVisibility(View.VISIBLE);
                        tvMyRequest.setTypeface( UIUtils.typeface(mAppPreferences.getLanCode(), Hsse.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMyRequest.setText(menu.getCaption());
                        } else{
                            tvMyRequest.setText( Utils.msg( Hsse.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
                case "SearchRequest" :
                    if(menu.getRights().contains("V")){
                        tvMySearch.setVisibility(View.VISIBLE);
                        tvMySearch.setTypeface( UIUtils.typeface(mAppPreferences.getLanCode(), Hsse.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMySearch.setText(menu.getCaption());
                        } else{
                            tvMySearch.setText( Utils.msg( Hsse.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
            }
        }

        //In case no right fo back to previous screen
        if(flag == 0){
            Utils.toast( Hsse.this, "69");
            Intent j = new Intent( Hsse.this, HomeActivity.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(j);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent( Hsse.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void nextActivity() {

        String menuLink = AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getSubMenuList().get("AddRequest").getMenuLink();

        //Initiate Work Flow & get Instance id & form name
        if(menuLink!=null && menuLink.length()>0){
            if (Utils.isNetworkAvailable( Hsse.this)) {
                new InitiateWorkFlowProcess( Hsse.this,menuLink).execute();
            } else {
                Toast.makeText( Hsse.this,"No Internet Connection,Unable Initiating Process. Try again.", Toast.LENGTH_SHORT).show();
                finish();
                //InitializeWorkFlowForm();
            }
        } else{
            ProcessStartResponse processInitializationRsp = null;
            processInitializationCompleted(processInitializationRsp);
        }
    }

    @Override
    public void processInitializationCompleted(ProcessStartResponse processInitializationRsp){
        HashMap<String,Object> tranData =new HashMap<String,Object>();
        tranData.put( Constants.FORM_KEY,"AddHSSERequest");
        tranData.put( Constants.TXN_ID,null);
        tranData.put( Constants.OPERATION,"A");
        tranData.put( Constants.TXN_SOURCE,"AddRequest");
        tranData.put( Constants.PROCESS_INSTANCE_ID,"");
        tranData.put( Constants.TASK_ID,"");
        tranData.put( Constants.REQUEST_FLAG,"");
        tranData.put( Constants.NEXT_TAB_SELECT,"HSSE");
        tranData.put( HsseConstant.OLD_TKT_STATUS,"1691");
        tranData.put( HsseConstant.OLD_GRP,"");
        Intent i = new Intent( Hsse.this, HsseFrame.class );
        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
        startActivity( i );
    }

}


