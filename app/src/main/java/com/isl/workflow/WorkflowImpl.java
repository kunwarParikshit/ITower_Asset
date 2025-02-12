package com.isl.workflow;
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
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.ProcessStartResponse;
import com.isl.workflow.tabs.RequestReport;
import com.isl.workflow.utils.UIUtils;
import java.util.HashMap;
import java.util.List;
import infozech.itower.R;

/**
 * Created by dhakan on 6/19/2020.
 */

public class WorkflowImpl extends Activity implements ProcessInitialization {

    TextView tvAddRequest,tvMyRequest,tvMySearch,tvHeader;
    AppPreferences mAppPreferences;
    WorkFlowDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow_management);
        dbHelper = new WorkFlowDatabaseHelper(WorkflowImpl.this);
        dbHelper.open();

        initialize();
      //  AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).setDataTypeId("-20,-131,-132,-133,-134,-135,-136,-137,-139,-138,-140,-141,-143,-1207,-1208,15,200");
        AppConstants.moduleList.get(mAppPreferences.getModuleIndex())
                .setDataTypeId("-33,-61,-162,-163,-164,-165,-166,-167,-168,-169,-170,-172,-173,-1231,-174,14,15,19,21,22,23,200,-20,-131,-132,-133,-134,-135,-136,-137,-139,-138,-140,-141,-143,-1207,-1208,15,200,-1276");
        try{
             //String modifiedMetaData = dbHelper.getModifedMetaDataTypeIds(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getDataTypeId());
            String modifiedMetaData = "-33,-61,-162,-163,-164,-165,-166,-167,-168,-169,-170,-172,-173,-1231,-174,14,15,19,21,22,23,200,-20,-131,-132,-133,-134,-135,-136,-137,-139,-138,-140,-141,-143,-1207,-1208,15,200,-1276";
            System.out.println("********************modifiedMetaData - "+modifiedMetaData);


            if (Utils.isNetworkAvailable(WorkflowImpl.this)) {
                if (modifiedMetaData!=null && modifiedMetaData.trim().length()>0) {
                    new MetaData(WorkflowImpl.this,modifiedMetaData,mAppPreferences.getModuleIndex()).execute();
                }
            } else {
                Utils.toast(WorkflowImpl.this, "17");
                Intent i = new Intent(WorkflowImpl.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        } catch(Exception exp){
            exp.printStackTrace();
        }

        dbHelper.close();

        if(getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey("link")){
            nextActivity();
        }

    }

    private void initialize(){
        mAppPreferences = new AppPreferences(WorkflowImpl.this);
        tvAddRequest = (TextView) findViewById(R.id.tv_add_request);
        tvMyRequest = (TextView) findViewById(R.id.tv_my_request);
        tvMySearch = (TextView) findViewById(R.id.tv_my_search);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        tvHeader.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),WorkflowImpl.this));
        tvHeader.setText(mAppPreferences.getModuleName());
        mAppPreferences.setSiteLockAPICall(0);

        Button iv_back = (Button) findViewById(R.id.iv_back);
        Utils.msgButton(WorkflowImpl.this, "71", iv_back);

        getMenuRights();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(WorkflowImpl.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAppPreferences.setSite("");
                mAppPreferences.setPEDT("");
                mAppPreferences.setPSDT("");
               nextActivity();
            }
        });

        tvMyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(WorkflowImpl.this, RequestReport.class);
                startActivity(i);
            }
        });

        tvMySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(WorkflowImpl.this, SearchRequest.class);
                startActivity(i);
            }
        });
    }

    public void getMenuRights() {

        int flag =0;
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleName());

        for(MenuDetail menu : subMenuList){
            AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getSubMenuList().put(menu.getName(),menu);

            switch(menu.getName()){
                case "AddRequest" :
                    if(menu.getRights().contains("A")){
                        tvAddRequest.setVisibility(View.VISIBLE);
                        tvAddRequest.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),WorkflowImpl.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvAddRequest.setText(menu.getCaption());
                        } else{
                            tvAddRequest.setText(Utils.msg(WorkflowImpl.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
                case "MyRequest" :
                    if(menu.getRights().contains("V")){
                        tvMyRequest.setVisibility(View.VISIBLE);
                        tvMyRequest.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),WorkflowImpl.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMyRequest.setText(menu.getCaption());
                        } else{
                            tvMyRequest.setText(Utils.msg(WorkflowImpl.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
                case "SearchRequest" :
                    if(menu.getRights().contains("V")){
                        tvMySearch.setVisibility(View.VISIBLE);
                        tvMySearch.setTypeface(UIUtils.typeface(mAppPreferences.getLanCode(),WorkflowImpl.this));

                        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                            tvMySearch.setText(menu.getCaption());
                        } else{
                            tvMySearch.setText(Utils.msg(WorkflowImpl.this,menu.getId()));
                        }

                        flag = 1;
                    }
                    break;
            }
        }

        //In case no right fo back to previous screen
        if(flag == 0){
            Utils.toast(WorkflowImpl.this, "69");
            Intent j = new Intent(WorkflowImpl.this, HomeActivity.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(j);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(WorkflowImpl.this, HomeActivity.class);
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
            if (Utils.isNetworkAvailable(WorkflowImpl.this)) {
                new InitiateWorkFlowProcess(WorkflowImpl.this,menuLink).execute();
            } else {
                Toast.makeText( WorkflowImpl.this,"No Internet Connection,Unable Initiating Process. Try again.", Toast.LENGTH_SHORT).show();
                finish();
           }
        } else{
            ProcessStartResponse processInitializationRsp = null;
            processInitializationCompleted(processInitializationRsp);
        }
    }

    @Override
    public void processInitializationCompleted(ProcessStartResponse processInitializationRsp){
        HashMap<String,Object> tranData =new HashMap<String,Object>();
        tranData.put(Constants.FORM_KEY,"AddAccessRequest");
        tranData.put(Constants.TXN_ID,null);
        tranData.put(Constants.OPERATION,"A");
        tranData.put(Constants.TXN_SOURCE,"AddRequest");
        tranData.put(Constants.PROCESS_INSTANCE_ID,"");
        tranData.put(Constants.TASK_ID,"");
        tranData.put(Constants.REQUEST_FLAG,"");

        if(getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey("link")){
            tranData.put("sid",getIntent().getExtras().getString("sid"));
            tranData.put("ppr",getIntent().getExtras().getString("ppr"));
            tranData.put("acttype",getIntent().getExtras().getString("acttype"));
            tranData.put("tranId",getIntent().getExtras().getString("tranId"));
            tranData.put("esid",getIntent().getExtras().getString("esid"));
        }

        Intent i = new Intent( WorkflowImpl.this, FormActivity.class );
        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
        startActivity( i );
    }

}

