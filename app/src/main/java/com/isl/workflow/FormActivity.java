package com.isl.workflow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isl.api.IApiRequest;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.MenuDetail;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.form.WorkFlowForm;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;
import com.isl.workflow.tabs.AssignedHistoryTab;
import com.isl.workflow.tabs.AuditLogTab;
import com.isl.workflow.tabs.RequestReport;

import java.util.HashMap;
import java.util.List;

import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dhakan on 6/24/2020.
 */


public class FormActivity  extends FragmentActivity implements View.OnClickListener {
    //private FragmentTabHost mTabHost;
    private static AppPreferences mAppPreferences;
    FragmentTabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.tab );
        mAppPreferences = new AppPreferences(FormActivity.this);
       //callGetApiRespnceOwner();

        TextView header=(TextView)findViewById(R.id.tv_brand_logo);
        header.setText(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getCaption());

        Button btBack = (Button)findViewById( R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mTabHost.getCurrentTab() == 0){
                    backScreenConfirmation("291","63","64");
                }else{
                    finish();
                }
            }
        });

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        if(getIntent().getExtras()!=null){
            HashMap<String,String> tranData = (HashMap<String, String>) getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS);
            //System.out.println("***************** Operation - "+tranData.get(Constants.OPERATION));
            if(tranData.get(Constants.OPERATION).equalsIgnoreCase("A")){
                mTabHost.addTab( mTabHost.newTabSpec( "AddRequest" )
                        .setIndicator( tabView("",1)), WorkFlowForm.class, null );
            }else{
                getMenuRights();
            }
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
        if(mode==0) {
            if (getApplicationContext().getPackageName().equalsIgnoreCase( "tawal.com.sa" )) {
                relativelayout.setBackgroundResource( R.drawable.tab_bg_selector_tawal );
            } else if (mode == 1) {
                relativelayout.setBackgroundResource( R.drawable.tab_bg_selector );
            }
        }else{
            relativelayout.setVisibility(View.GONE);
        }
        TextView tv = (TextView) view.findViewById(R.id.TabTextView);
        tv.setTypeface(Utils.typeFace( FormActivity.this));
        tv.setText(name);
        return view;
    }

    public void getMenuRights() {
        int flag =0;
        DataBaseHelper dbHelper = new DataBaseHelper( FormActivity.this);
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight(mAppPreferences.getModuleName());
        dbHelper.close();

        for(MenuDetail menu : subMenuList){
            String name = "";
            if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
                name = menu.getCaption();
            }else{
                name = Utils.msg(FormActivity.this,menu.getId());
            }
            switch(menu.getName()){
                case "RequestDetailTab" :
                    if(menu.getRights().contains("V")){
                        mTabHost.addTab( mTabHost.newTabSpec( "RequestDetailTab" )
                                .setIndicator(tabView(name,0)), WorkFlowForm.class, null );
                        //adapter.addFrag(new WorkFlowForm(),name);
                        flag = 1;
                    }
                    break;
                case "AuditLogTab" :
                    if(menu.getRights().contains("V")){
                        mTabHost.addTab( mTabHost.newTabSpec( "AuditLogTab" )
                                .setIndicator(tabView(name,0)), AuditLogTab.class, null );
                        //adapter.addFrag(new AuditLogTab(),name);
                        flag = 1;
                    }
                    break;
                case "AssignHistoryTab" :
                    if(menu.getRights().contains("V")){
                        mTabHost.addTab( mTabHost.newTabSpec( "AssignHistoryTab" )
                                .setIndicator(tabView(name,0)), AssignedHistoryTab.class, null );
                        //adapter.addFrag(new AssignedHistoryTab(),name);
                        flag = 1;
                    }
                    break;
            }
        }


        if(flag == 0){
            Utils.toast(FormActivity.this, "69");
            Intent j = new Intent(FormActivity.this, RequestReport.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(j);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(mTabHost.getCurrentTab() == 0){
            backScreenConfirmation("291","63","64");
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    public void backScreenConfirmation(String confirmID,String primaryBt,String secondaryBT) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog( FormActivity.this, R.style.FullHeightDialog);
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
        tv_header.setTypeface( Utils.typeFace(FormActivity.this));
        positive.setTypeface( Utils.typeFace(FormActivity.this) );
        negative.setTypeface( Utils.typeFace(FormActivity.this ) );
        title.setTypeface( Utils.typeFace( FormActivity.this ) );
        title.setText( Utils.msg( FormActivity.this,confirmID));
        // title.setText("Do you want to exit?");
        positive.setText( Utils.msg(FormActivity.this,primaryBt));
        negative.setText( Utils.msg( FormActivity.this,secondaryBT));
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String key ="";
                if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                    Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                    key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
                    int isVisible = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getVisibility();
                    Log.d("Avi v", "" + isVisible);
                    String formName = FormCacheManager.getFormConfiguration().getName();
                   //  key="2.1";
                    if (!key.equalsIgnoreCase("")) {
                        if (formName.equalsIgnoreCase("AccessRequestImpl") ||
                                formName.equalsIgnoreCase("AccessRequesttoc")) {
                            callGetAccessTokenForSbmit(FormActivity.this, key,actvity_dialog);
                        }else {
                            actvity_dialog.cancel();
                            finish();
                        }
                    }else {
                        actvity_dialog.cancel();
                        finish();
                    }
                }else {
                    actvity_dialog.cancel();
                    finish();
                }


            }
        } );

        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        } );
    }


    private void callGetAccessTokenForSbmit(Context context, String keySerails, Dialog actvity_dialog) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<AccessTokenResponce> call = iApiRequest.genrateAccessToken(Utils.msg(context, "844"));
        call.enqueue(new Callback<AccessTokenResponce>() {
            @Override
            public void onResponse(Call<AccessTokenResponce> call, retrofit2.Response<AccessTokenResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    // Handle successful response
                    // Process the response body
                    String token = response.body().getTokenType() + " " + response.body().getAccessToken();
                    callGetSearchKeysubmit(context, keySerails, token,actvity_dialog);
                } else {
                    Utils.toastMsg(context, Utils.msg(context, "841"));
                    actvity_dialog.cancel();
                    FormActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
                actvity_dialog.cancel();
                FormActivity.this.finish();
            }
        });
    }

    private void callGetSearchKeysubmit(Context context, String making, String Accesstoken, Dialog actvity_dialog) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\n\t\"searchKeys\": {\n\t\t\"keySearchArguments\": " +
                "{\n\t\t\t\"marking\": \"" + making + "\"\n\t\t},\n\t\t\"pagination\":" +
                " {\n\t\t\t\"firstResult\": \"0\",\n\t\t\t\"maxResults\": \"10\"\n\t\t}\n\t}\n}";
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyString);

        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<GetSerachKeyResponse> call = iApiRequest.getSearchkeysMaking(requestBody, Accesstoken);
        call.enqueue(new Callback<GetSerachKeyResponse>() {
            @Override
            public void onResponse(Call<GetSerachKeyResponse> call, retrofit2.Response<GetSerachKeyResponse> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //  Utils.toastMsg(context, "" + response.code());
                if (response.isSuccessful()) {
                    // Handle successful response
                    if (response.body() != null) {
                        if (response.body().getSearchKeysResponse().getSKey() != null) {
                            response.body().getSearchKeysResponse().getSKey().getIdentity();
                            callHandilekey(context, response.body().getSearchKeysResponse().getSKey().getIdentity(), Accesstoken,actvity_dialog);
                        } else {
                            Utils.toastMsg(context, Utils.msg(context, "845"));
                            actvity_dialog.cancel();
                            FormActivity.this.finish();
                        }
                    }
                } else {
                    // Handle error response
                    // You can check response.code() and response.message() for details
                }


            }

            @Override
            public void onFailure(Call<GetSerachKeyResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
                actvity_dialog.cancel();
                FormActivity.this.finish();
            }
        });
    }

    private void callHandilekey(Context context, String KeyIdentty, String AccessToken, Dialog actvity_dialog) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.msg(context, "841"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getHandileKey(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    // Process the successful response
                    if (response.code() == 200) {
                        actvity_dialog.cancel();
                        FormActivity.this.finish();

                    }
                    // Do something with the response body
                } else {
                    Utils.toastMsg(context, Utils.msg(context, "850"));
                    actvity_dialog.cancel();
                    FormActivity.this.finish();
                }


            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                actvity_dialog.cancel();
                FormActivity.this.finish();
                Utils.toastMsg(context, "" + t);
            }
        });
    }

}

