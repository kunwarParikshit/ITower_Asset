package com.isl.workflow.form.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.api.UnsafeHttpClient;
import com.isl.constant.AppConstants;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.MyApp;
import com.isl.modal.SiteLockResponce;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import infozech.itower.R;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectControl implements OnItemSelectedListener {

    AppPreferences mAppPreferences = new AppPreferences(MyApp.getAppContext());
    private static Context context;

    public SelectControl() {
    }

    public Spinner addSpinner(Context context, Fields field) {
        this.context = context;

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins(10, 5, 10, 0);
        Spinner spinner = new Spinner(context);

        spinner.setBackgroundResource(R.drawable.doted);
        spinner.setId(Integer.parseInt(field.getId()));
        spinner.setTag(field.getKey());
        spinner.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        spinner.setMinimumHeight((int) (35 * scale));

        FormCacheManager.getFormControls().get(field.getId()).setSelectCtrl(spinner);
        //System.out.println("********************** - "+field.getKey());
        WorkFlowUtils.resetEnableControl(context, field);
        WorkFlowUtils.resetShowHideControl(context, field);

        WorkFlowUtils.setFieldValues(context, field, FormCacheManager.getFormControls().get(field.getId()), true);
        WorkFlowUtils.resetFieldValues(context, field, false);

        spinner.setOnItemSelectedListener(SelectControl.this);
        //FormCacheManager.getFormControls().get(field.getId()).setSelectCtrl(spinner);
        return spinner;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

        Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().
                get(FormCacheManager.getFormControls().get(String.valueOf(arg0.getId())).getKey());

       // FormCacheManager.getFormConfiguration().getFormFields().co
       // FormCacheManager.getFormConfiguration().getFormFields().
        //        get(FormCacheManager.getFormControls().get(String.valueOf(arg0.getId()))

        if (((String) FormCacheManager.getFormControls().get(formControl.getId()).getSelectCtrl().getTag()).equalsIgnoreCase("" + position)) {
            FormCacheManager.getFormControls().get(formControl.getId()).getSelectCtrl().setTag(formControl.getKey());
            return;
        }

        FormCacheManager.getFormControls().get(formControl.getId()).getSelectCtrl().setTag(formControl.getKey());
       /* if (formControl.getKey().equalsIgnoreCase("pmname")) {
            String siteId="";
            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("sid")) {
                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("sid");
                siteId=FormCacheManager.getFormControls().get(formControl3.getId()).getAutoCompleteCtrl().getText().toString();
            }
             //callGetApiResponceOwner(context, mAppPreferences.getUserId(), siteId);
            //Toast.makeText(context, mAppPreferences.getUserId(), Toast.LENGTH_SHORT).show();
        }*/
        if (!formControl.isDisabled()) {

            DropdownValue selectedValue = (DropdownValue) FormCacheManager.getFormControls().get(formControl.getId()).getSelectCtrl().getSelectedItem();
            if (selectedValue.getId().equalsIgnoreCase(AppConstants.DD_SELECT_ID)) {
                return;
            }

            if (formControl.getOnChange() != null && formControl.getOnChange().getMessage() != null) {
                DataSubmitUtils.confirmationMessage(context, formControl.getOnChange(), String.valueOf(arg0.getId()), selectedValue);

            } else {
                DataSubmitUtils.onChangeTask(context, true, String.valueOf(arg0.getId()), selectedValue);
            }
        }
    }


   /* private static void callGetApiResponceOwner(Context context, String implementerID, String siteID) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
     *//*   OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(120, TimeUnit.SECONDS);
        client.readTimeout(120, TimeUnit.SECONDS);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://203.122.7.134:6100/").addConverterFactory(GsonConverterFactory.create()).client(client.build());
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);*//*
        IApiRequest request = RetrofitApiClient.getRequest();
        Call<SiteLockResponce> call = request.getOwnerName(implementerID);
        call.enqueue(new Callback<SiteLockResponce>() {
            @Override
            public void onResponse(Call<SiteLockResponce> call, retrofit2.Response<SiteLockResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.body() != null) {
                    // Toast.makeText(FormActivity.this, ""+response.body().getOwnerName(), Toast.LENGTH_SHORT).show();
                    callGetApiRespnce(siteID, "Tawal,JIO34", context, response.body().getUserType());
                }
            }

            @Override
            public void onFailure(Call<SiteLockResponce> call, Throwable t) {
                Toast.makeText(context, "" + t, Toast.LENGTH_SHORT).show();

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        });
    }


    private static void callGetApiRespnce(String site_id, String ownerName, Context context, String userType) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String authCred = Credentials.basic("postgres", "11@");
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://192.168.0.162:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeHttpClient.getUnsafeOkHttpClient());
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<SiteLockResponce> call = iApiRequest.getSitelockAbloyID(site_id, ownerName, userType, authCred);
        call.enqueue(new Callback<SiteLockResponce>() {
            @Override
            public void onResponse(Call<SiteLockResponce> call, retrofit2.Response<SiteLockResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.body() != null) {
                    //Utils.toastMsg(context, "" + response.body().getAbloyIds());
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("AbloyLockId")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("AbloyLockId");
                        FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setText("");
                        FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setText(response.body().getAbloyIds());
                    }
                }
             //   Utils.toastMsg(context, "" + response.body().getAbloyIds());

            }

            @Override
            public void onFailure(Call<SiteLockResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
              //  Utils.toastMsg(context, "" + t);
            }
        });
    }
*/
}
