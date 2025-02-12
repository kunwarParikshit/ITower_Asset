package com.isl.workflow.form.control;

import static com.isl.workflow.utils.DateTimeUtils.currentDateTimeFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.isl.api.IApiRequest;
import com.isl.api.UnsafeHttpClient;
import com.isl.constant.AppConstants;
import com.isl.dao.cache.AppPreferences;
import com.isl.incident.AddNewAssetTT;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.CamundaVariable;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.SiteLocks;
import com.isl.workflow.modal.request.SavePersonRequest;
import com.isl.workflow.modal.request.SearchPersonRequest;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ButtonControl implements View.OnClickListener {

    private static Context context;
    private static AppPreferences mAppPreferences;
    private  String pulgId;

    public void ButtonControl(Context context) {
        this.context = context;
    }

    public Button addButton(Context context, Fields field) {
        this.context = context;
        mAppPreferences = new AppPreferences(context);
        Button btnControl = new Button(context);
        btnControl.setText(field.getTitle());
        Utils.buttonUI(context, btnControl);

        btnControl.setId(Integer.parseInt(field.getId()));
        btnControl.setTag(field.getKey());

        btnControl.setOnClickListener(this);
        FormCacheManager.getFormControls().get(field.getId()).setButtonCtrl(btnControl);
        WorkFlowUtils.resetEnableControl(context, field);
        WorkFlowUtils.resetShowHideControl(context, field);
        return btnControl;
    }

    @Override
    public void onClick(View arg0) {
        arg0.setClickable(false);
        arg0.postDelayed(new Runnable() {
            @Override
            public void run() {
                arg0.setClickable(true);

            }
        }, 200);



        String fieldKey = (String) arg0.getTag();
        Fields svbuttonField = FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey);
        //Validate the data if any
        if (fieldKey.equalsIgnoreCase("validateKeySer")) {
            String key = "";
            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
            }
            callGetAccessToken(key);
        } else {
            if (svbuttonField.getOnClick().getValidate() != null && svbuttonField.getOnClick().getValidate().size() > 0)
            {
                if (!DataSubmitUtils.validateFieldList(context, svbuttonField.getOnClick().getValidate())) {
                    return;
                }
            }

            String operation = (String) FormCacheManager.getPrvFormData().get(Constants.OPERATION);
            String formName = FormCacheManager.getFormConfiguration().getName();
            //AddAccessRequest
            if (formName.equalsIgnoreCase("AccessRequestImpl")
                    && FormCacheManager.getFormConfiguration().getFormFields().containsKey("sitelockid")
                    && FormCacheManager.getFormControls().get(FormCacheManager.getFormConfiguration().
                    getFormFields().get("sitelockid").getId()).getValue() != null
                    && FormCacheManager.getFormControls().get(FormCacheManager.getFormConfiguration()
                    .getFormFields().get("sitelockid").getId()).getValue().toString().length() > 0) {
                Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get("sitelockid");
                ToggleControl toggleControl = new ToggleControl();
                toggleControl.keyGenerateSera4(context, formControl.getId(), "false");
            }


            if (AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getModuleId() == 1001
                    && (operation.equalsIgnoreCase("A") ||
                    operation.equalsIgnoreCase("E"))
                    && (formName.equalsIgnoreCase("AccessRequestEdit")
                    || formName.equalsIgnoreCase("AddAccessRequest"))
                    && mAppPreferences.getSite().length() > 0) {
                if (Utils.isNetworkAvailable(context)) {
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("lock")) {
                        Fields siteLockField = FormCacheManager.getFormConfiguration().getFormFields().get("lock");
                        FormFieldControl formControl = FormCacheManager.getFormControls().get(siteLockField.getId());
                        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                        db.open();
                        if (formControl.getSelectedVal() != null && formControl.getSelectedVal().size() > 0) {
                            for (int a = 0; a < formControl.getSelectedVal().size(); a++) {
                                if (db.isSitelock(formControl.getSelectedVal().get(a).getId(), "-1207")) {
                                    Utils.toastMsg(context, "Site lock " + formControl.getSelectedVal()
                                            .get(a).getValue() + " not associated site.");
                                    formControl.getCaptionCtrl().clearFocus();
                                    formControl.getCaptionCtrl().requestFocus();
                                    return;
                                }
                            }
                        }
                    }

                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("lock")
                            && mAppPreferences.isSiteLockAPICall() == 0) {
                        try {
                            new GetSiteLocks(context, svbuttonField,
                                    String.valueOf(arg0.getId()), operation,
                                    mAppPreferences.getSite(),
                                    mAppPreferences.getName()).execute().get();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    } else if (operation.equalsIgnoreCase("A")) {
                        try {
                            new GetApprovalCount(context,
                                    svbuttonField,
                                    String.valueOf(arg0.getId())).execute().get();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    } else {
                        if (svbuttonField.getOnClick() != null && svbuttonField.getOnClick().getMessage() != null) {
                            DataSubmitUtils.confirmationMessage(context, svbuttonField.getOnClick(), String.valueOf(arg0.getId()), null);
                        } else {
                            DataSubmitUtils.onChangeTask(context, true, String.valueOf(arg0.getId()), null);
                        }
                    }
                } else {
                    Toast.makeText(context, "No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (svbuttonField.getOnClick() != null && svbuttonField.getOnClick().getMessage() != null) {
                    DataSubmitUtils.confirmationMessage(context, svbuttonField.getOnClick(), String.valueOf(arg0.getId()), null);
                } else {
                    DataSubmitUtils.onChangeTask(context, true, String.valueOf(arg0.getId()), null);
                }
            }
            //DataSubmitUtils.onChangeTask(context,true,String.valueOf(arg0.getId()),null);
        }
    }

    public static class GetApprovalCount extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;
        Context con;
        Fields svbuttonField;
        String arg0;

        public GetApprovalCount(Context con, Fields svbuttonField, String arg0) {
            this.con = con;
            this.svbuttonField = svbuttonField;
            this.arg0 = arg0;
            pd = new ProgressDialog(con);
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(Constants.INSTANCE_ID, ""));
            nameValuePairs.add(new BasicNameValuePair(Constants.S_ID, mAppPreferences.getSite()));
            nameValuePairs.add(new BasicNameValuePair(Constants.PSDT, mAppPreferences.getPSDT()));
            nameValuePairs.add(new BasicNameValuePair(Constants.PEDT, mAppPreferences.getPEDT()));
            nameValuePairs.add(new BasicNameValuePair("flag", "0"));
            try {
                response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl() + WebAPIs.requestDetail, nameValuePairs);
                //response = response.replaceAll("\"\"","");
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            /*try {

                response = HttpUtils.httpPostRequest( AppConstants.moduleList.get( mAppPreferences.getModuleIndex() ).getBaseurl() + WebAPIs.getApprovalCount, nameValuePairs );
               // response = response.replaceAll("\\[","").replaceAll("\\]","");
            } catch (Exception e) {
                e.printStackTrace();
                response = "No";
            }*/
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }

            if (result == null || result.length() == 0) {
                if (svbuttonField.getOnClick() != null && svbuttonField.getOnClick().getMessage() != null) {
                    DataSubmitUtils.confirmationMessage(context, svbuttonField.getOnClick(), arg0, null);
                } else {
                    DataSubmitUtils.onChangeTask(context, true, arg0, null);
                }
            } else {
                Gson gson = new GsonBuilder().setLenient().create();
                Type listType = new TypeToken<Map<String, CamundaVariable>>() {
                }.getType();
                Map<String, CamundaVariable> msg = gson.fromJson(result, listType);
                if (msg.containsKey("conflict") & msg.containsKey("siteMsgData") && (msg.get("conflict").getValue().toString().length() > 0 || msg.get("siteMsgData").getValue().toString().length() > 0)) {

                    confirmationMessage(context, svbuttonField, arg0, msg.get("siteMsgData").getValue().toString() + "\n" + msg.get("conflict").getValue().toString(), 1, "");
                } else {
                    if (svbuttonField.getOnClick() != null && svbuttonField.getOnClick().getMessage() != null) {
                        DataSubmitUtils.confirmationMessage(context, svbuttonField.getOnClick(), arg0, null);
                    } else {
                        DataSubmitUtils.onChangeTask(context, true, arg0, null);
                    }
                }


            }

            /*if (result == null || result.length()==0 || result.equalsIgnoreCase("\"No\"")) {
                if(svbuttonField.getOnClick()!=null && svbuttonField.getOnClick().getMessage()!=null){
                    DataSubmitUtils.confirmationMessage(context,svbuttonField.getOnClick(),arg0,null);
                } else{
                    DataSubmitUtils.onChangeTask(context,true,arg0,null);
                }
            }else{
                try {
                    if(result.contains("Yes-")){
                        result = result.replaceAll("\"Yes-","");
                        result = result.replaceAll("\"","");
                    }
                } catch (Exception e) {
                    result = null;
                }

                if (result != null && result.length()!=0) {
                    confirmationMessage(context,svbuttonField,arg0,result,1,"");
                }else{
                    if(svbuttonField.getOnClick()!=null && svbuttonField.getOnClick().getMessage()!=null){
                        DataSubmitUtils.confirmationMessage(context,svbuttonField.getOnClick(),arg0,null);
                    } else{
                        DataSubmitUtils.onChangeTask(context,true,arg0,null);
                    }
                }
            }*/
        }
    }

    public static void confirmationMessage(Context ctx, Fields formControl, String arg0, String changeid, int flag, String msg) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ctx, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(ctx));
        positive.setTypeface(Utils.typeFace(ctx));
        negative.setTypeface(Utils.typeFace(ctx));
        title.setTypeface(Utils.typeFace(ctx));
        //title.setGravity(Gravity.LEFT);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        //positive.setText("OK");
        positive.setText(Utils.msg(ctx, "7"));
        negative.setText(Utils.msg(ctx, "8"));

        if (flag == 1) {
            title.setText(changeid + "\n\nAre you sure want to continue?");
            //title.setText("Request is already approved for this site : "+changeid + " " +
            //       "Are you sure want to continue?");
            negative.setVisibility(View.VISIBLE);
            //negative.setText("Cancel");
            negative.setText(Utils.msg(ctx, "8"));
        } else {
            title.setText(msg);
        }

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                actvity_dialog.cancel();
                if (flag == 1) {
                    if (formControl.getOnClick() != null && formControl.getOnClick().getMessage() != null) {
                        DataSubmitUtils.confirmationMessage(context, formControl.getOnClick(), arg0, null);
                    } else {
                        DataSubmitUtils.onChangeTask(context, true, arg0, null);
                    }
                }
            }
        });


        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                actvity_dialog.cancel();
            }
        });

    }

    public static class GetSiteLocks extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;
        Context con;
        Fields svbuttonField;
        String arg0;
        List<SiteLocks> getSiteLocksList;
        String operation, sid, name;

        public GetSiteLocks(Context con, Fields svbuttonField, String arg0, String operation, String sid, String name) {
            this.con = con;
            this.svbuttonField = svbuttonField;
            this.arg0 = arg0;
            pd = new ProgressDialog(con);
            this.operation = operation;
            this.sid = sid;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(AppConstants.SITE_ID_ALIAS, sid));
            nameValuePairs.add(new BasicNameValuePair(AppConstants.FIRST_NAME, name));
            String response = "";

            try {
                response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl() + WebAPIs.getSiteLocks, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            mAppPreferences.setSiteLockAPICall(1);
            Fields siteLockField = FormCacheManager.getFormConfiguration().getFormFields().get("lock");
            FormFieldControl formControl = FormCacheManager.getFormControls().get(siteLockField.getId());

            //Fields siteLockAvailability = FormCacheManager.getFormConfiguration().getFormFields().get("sitelockavailable");
            //FormFieldControl siteLockAvailableControl = FormCacheManager.getFormControls().get(siteLockAvailability.getId());

            if (pd.isShowing()) {
                pd.dismiss();
            }
            if ((result == null)) {
                formControl.getCaptionCtrl().setVisibility(View.GONE);
                formControl.getMultiSelectCtrl().setVisibility(View.GONE);
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<SiteLocks>>() {
                }.getType();
                getSiteLocksList = gson.fromJson(result, listType);
            }

            if (getSiteLocksList != null && getSiteLocksList.size() > 0 && getSiteLocksList.get(0).getLock_id() != null) {
                WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(context);
                db.open();
                db.updateParamDesc("", "0", "-1207", 0);
                for (int a = 0; a < getSiteLocksList.size(); a++) {
                    if (getSiteLocksList.get(a).getLock_name().contains("~~")) {
                        //System.out.println("************** Name - "+response.getDetails().get(0).getName());
                        String[] arr = getSiteLocksList.get(a).getLock_name().split("~~");
                        db.updateParamDesc(arr[1], getSiteLocksList.get(a).getLock_id(), "-1207", 1);
                    }
                }
                db.close();


                if (formControl.getCaptionCtrl() != null && formControl.getMultiSelectCtrl() != null && formControl.getMultiSelectCtrl().getVisibility() == View.GONE) {
                    formControl.getCaptionCtrl().setVisibility(View.VISIBLE);
                    formControl.getMultiSelectCtrl().setVisibility(View.VISIBLE);
                    formControl.getCaptionCtrl().clearFocus();
                    formControl.getCaptionCtrl().requestFocus();
                    //siteLockAvailableControl.getTextBoxCtrl().setText("Yes");
                    if (operation.equals("A")) {
                        Utils.toastMsg(context, siteLockField.getOnChange().getMessage().getMsg());
                        //confirmationMessage(context,null,null,null,0,
                        //        siteLockField.getOnChange().getMessage().getMsg());
                    }

                    //Toast.makeText(context,
                    //        "This site is smart lock enabled. Please select lock type and submit request.",
                    //        Toast.LENGTH_SHORT).show();

                    //WorkFlowUtils.refreshDependentFields(context,FormCacheManager.getFormConfiguration().
                    //       getFormFields().get("sid"),false);

                    WorkFlowUtils.resetFieldValues(context, FormCacheManager.getFormConfiguration().getFormFields().get("lock"), true);
                }

            } else {
                //siteLockAvailableControl.getTextBoxCtrl().setText("No");
                formControl.getCaptionCtrl().setVisibility(View.GONE);
                formControl.getMultiSelectCtrl().setVisibility(View.GONE);
                //WorkFlowUtils.refreshDependentFields(context,FormCacheManager.getFormConfiguration().
                //        getFormFields().get("sid"),false);
                if (Utils.isNetworkAvailable(context) && operation.equals("A")) {
                    try {
                        new GetApprovalCount(context, svbuttonField, arg0).execute().get();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                } //else {
                //   Toast.makeText(context, "No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                //}
            }


           /* if(DataSubmitUtils.validateField(context,siteLockField,null,null,
                    null, false,false)){
                if (Utils.isNetworkAvailable(context)) {
                    try {
                        new GetApprovalCount(context,svbuttonField,arg0).execute().get();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
            }*/

            super.onPostExecute(result);
        }
    }

    private void callGetAccessToken(String keySerails) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<AccessTokenResponce> call = iApiRequest.genrateAccessToken(Utils.msg(context, "844"));
        call.enqueue(new Callback<AccessTokenResponce>() {
            @Override
            public void onResponse(Call<AccessTokenResponce> call, retrofit2.Response<AccessTokenResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    String token = response.body().getTokenType() + " " + response.body().getAccessToken();
                      callGetSearchKey(keySerails, token);
                } else {
                    Utils.toastMsg(context, Utils.msg(context, "851"));
                }

            }

            @Override
            public void onFailure(Call<AccessTokenResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetSearchKey(String making, String Accesstoken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();

        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyString = "{\n\t\"searchKeys\": {\n\t\t\"keySearchArguments\": {\n\t\t\t\"marking\": \"" + making + "\"\n\t\t},\n\t\t\"pagination\": {\n\t\t\t\"firstResult\": \"0\",\n\t\t\t\"maxResults\": \"10\"\n\t\t}\n\t}\n}";
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
                            callGetKeyDetails(response.body().getSearchKeysResponse().getSKey().getIdentity(), Accesstoken);
                        } else {
                            Utils.toastMsg(context, Utils.msg(context, "845"));
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
            }
        });
    }

    private void callGetKeyDetails(String KeyIdentty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getKeyDetails(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // Utils.toastMsg(context, ""+response.code());
                if (response.isSuccessful()) {
                    // Handle successful response
                    if (response.body() != null) {
                        if (response.body().getGetKeyDetailsResponse().getKeyDetails().getWhereAt().equalsIgnoreCase("HANDED_OUT")) {
                            Utils.toastMsg(context, Utils.msg(context, "842"));

                        } else if (response.body().getGetKeyDetailsResponse().getKeyDetails().getWhereAt().equalsIgnoreCase("IN_STOCK")) {
                            response.body().getGetKeyDetailsResponse().getKeyDetails().getWhereAt();
                            callGetHangOutkey(KeyIdentty, AccessToken);
                        }
                    }
                    // Process the response body
                } else {
                    // Handle error response
                    // You can check response.code() and response.message() for details
                }


            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetKeyDetailsforUpdateCylinder(String KeyIdentty,String plugIdenty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getKeyDetails(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // Utils.toastMsg(context, ""+response.code());
                if (response.isSuccessful()) {
                    // Handle successful response
                    if (response.body() != null) {
                        String key = "";
                        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                            key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
                        }

                        String msg = Utils.msg(context,"853")+" " + key +
                                Utils.msg(context,"854")+" " +plugIdenty+" "+Utils.msg(context,"855");
                        showPopUPNotification(msg);

                        if (response.body().getGetKeyDetailsResponse().getKeyDetails().getValidity().getType().equalsIgnoreCase("ALWAYS")) {
                            Utils.toastMsg(context, Utils.msg(context, "846"));
                            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                                FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setEnabled(false);
                            }
                            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("validateKeySer")) {
                                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer");
                                FormCacheManager.getFormControls().get(formControl3.getId()).getButtonCtrl().setEnabled(false);
                            }
                            if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("implname")) {
                                Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("implname");
                                FormCacheManager.getFormControls().get(formControl3.getId()).getAutoCompleteCtrl().setEnabled(false);
                            }
                        } else{
                            callGetUpdateKeyVal(KeyIdentty,AccessToken,"ALWAYS");
                         //   callGetHangOutkey(KeyIdentty, AccessToken);
                        }
                    }
                    // Process the response body
                } else {
                    // Handle error response
                    // You can check response.code() and response.message() for details
                }


            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetHangOutkey(String KeyIdentty, String AccessToken) {

        String plandStartDate = "";
        String plandEndDate = "";
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("psdt")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("psdt");
            plandStartDate = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
        }
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("pedt")) {
            Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get("pedt");
            plandEndDate = FormCacheManager.getFormControls().get(formControl.getId()).getTextBoxCtrl().getText().toString();
        }

        String curretnDate = currentDateTimeFormat(plandStartDate);
        String curretnendDate = currentDateTimeFormat(plandEndDate);
        Log.d("Avi plandStartDate", curretnDate);
        Log.d("Avi plandEndDate", curretnendDate);
        // String curretnDate="2023-04-20T04:45+0400";
        // String curretnDate2 ="2023-06-09T15:41+0400";
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String ImplemID = "";
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("imploginid")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("imploginid");
            ImplemID = "" + FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText();
            // FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setEnabled(false);
        }

        String requestBodyString = "{\n  \"handOutKey\": {\n  " + "  \"personIdentity\": \"" + ImplemID + "\",\n  " + "  \"keyIdentity\": \"" + KeyIdentty + "\",\n  " + "  \"handOutDate\": \"" + curretnDate + "\",\n " + "   \"handInDate\": \"" + curretnendDate + "\",\n  " + "  \"copyAccessProfiles\": \"true\"\n  }\n}";
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyString);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getHangoutKey(requestBody, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.code() == 200) {
                    String ablouyLockId = "";
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey(AppConstants.AbloyLockId)) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get(AppConstants.AbloyLockId);
                        ablouyLockId = "" + FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText();
                       // FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().setEnabled(false);
                    }

                    ArrayList<String> mylist = new ArrayList<String>();
                    String pulgIdenty = "";

                    if (ablouyLockId.contains(",")) {
                        String[] arrOfStr = ablouyLockId.split(",");
                        for (String s : arrOfStr) {
                            mylist.add(s);
                        }
                    } else {
                        mylist.add(ablouyLockId);
                    }
                    for (int i =0;i<mylist.size();i++) {
                        callGetSearchCylender(KeyIdentty, mylist.get(i), AccessToken);
                    }
                } else if (response.code() == 500) {
                    Utils.toastMsg(context, Utils.msg(context, "847"));
                }
            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetSearchCylender(String KeyIdentty, String abloyLockID, String AccessToken) {
        StringBuilder identities = new StringBuilder();
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("Avi Key",KeyIdentty);
        Log.d("Avi Key",abloyLockID);
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"searchCylinders\": {\r\n    \"cylinderSearchArguments\": {\r\n        \"marking\": \"" + abloyLockID + "\"\r\n    }\r\n    \r\n}\r\n}");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<ResponseBody> call = iApiRequest.searchCylinders(AccessToken, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String responseBodyString = responseBody.string();
                            System.out.println("PlugIdenty: " + responseBodyString);
                            try {
                                JSONObject jsonObject = new JSONObject(responseBodyString);
                                JSONObject searchCylindersResponse = jsonObject.getJSONObject("searchCylindersResponse");
                                JSONArray cylinders = searchCylindersResponse.getJSONArray("cylinder");

                                // Iterate through each cylinder
                                for (int i = 0; i < cylinders.length(); i++) {
                                    JSONObject cylinder = cylinders.getJSONObject(i);
                                    JSONObject cylinderPlugA = cylinder.getJSONObject("cylinderPlugA");
                                    String identity = cylinderPlugA.getString("identity");
                                    identities.append(identity).append(",");
                                }

                                String identitiesString = identities.toString();
                                identitiesString = identitiesString.substring(0, identitiesString.length() - 1);
                                Log.d("Avis", identitiesString);
                                mAppPreferences.setPlugIdentgty(identitiesString);

                                callGetUpdateKeyCylinder(KeyIdentty,identitiesString, AccessToken);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (response.code() == 500) {
                        Utils.toastMsg(context, Utils.msg(context, "848"));
                        callHandilekey(context,KeyIdentty,KeyIdentty, AccessToken);
                    } else {
                        Utils.toastMsg(context, Utils.msg(context, "848"));
                        callHandilekey(context,KeyIdentty,KeyIdentty, AccessToken);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });

    }

    private  void callHandilekey(Context context, String KeyIdentty, String plugIdenty, String AccessToken) {
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
                        /*String msg = "The key- " + plugIdenty +
                                " and locks "+KeyIdentty+" are inactive now as you have checked out from site";
                        showPopUPNotification(msg);*/
                    }
                    // Do something with the response body
                } else {
                    Utils.toastMsg(context,Utils.msg(context, "850"));
                }


            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetUpdateKeyCylinder(String KeyIdentty, String plugIdenty, String AccessToken) {
        String plugInIdenty=  mAppPreferences.getPlugIdentty();
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        //Toast.makeText(context, "abloyID "+abloyID, Toast.LENGTH_SHORT).show();
        MediaType mediaType = MediaType.parse("application/json");


        // Create the request body using a Map for dynamic properties
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> updateKeyCylinderAuthorisations = new HashMap<>();
        updateKeyCylinderAuthorisations.put("keyIdentity", KeyIdentty);

        List<String> cylinderPlugIdentities = new ArrayList<>();

        String [] arrayStr=plugIdenty.split(",");
        JSONArray mJSONArray = new JSONArray();
        for (String s: arrayStr){
            mJSONArray.put(s);
            cylinderPlugIdentities.add(s);
        }

        Map<String, Object> cylinderPlugIdentitiesMap = new HashMap<>();
        cylinderPlugIdentitiesMap.put("cylinderPlugIdentity", cylinderPlugIdentities);

        updateKeyCylinderAuthorisations.put("cylinderPlugIdentities", cylinderPlugIdentitiesMap);
        requestBody.put("updateKeyCylinderAuthorisations", updateKeyCylinderAuthorisations);

        RequestBody requestBodyString = RequestBody.create(mediaType, new Gson().toJson(requestBody));
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getUpdateKeyCylinder(requestBodyString, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    callGetKeyDetailsforUpdateCylinder(KeyIdentty,plugIdenty,AccessToken);
                }else {
                    Utils.toastMsg(context, Utils.msg(context, "849"));
                        callHandilekey(context,KeyIdentty,plugIdenty,AccessToken);
                }

            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callGetUpdateKeyVal(String KeyIdentty, String AccessToken,String always) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n " +
                "   \"keyIdentity\": \""+KeyIdentty+"\",\r\n   " +
                " \"newValidity\": {\r\n        \"type\": \"ALWAYS\",\r\n       " +
                " \"revalidationConfig\": {\r\n     " +
                "       \"oneTimeUpdate\": false,\r\n     " +
                "       \"intervalInMinutes\": 100\r\n        }\r\n    }\r\n}");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<ResponseBody > call = iApiRequest.getUpdateKeyValidate(body, AccessToken);
        call.enqueue(new Callback<ResponseBody >() {
            @Override
            public void onResponse(Call<ResponseBody > call, retrofit2.Response<ResponseBody > response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Utils.toast(context,Utils.msg(context, "846"));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody > call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callSavePerson(SavePersonRequest KeyIdentty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder.build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).addConverterFactory(GsonConverterFactory.create())
                .client(client);
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getSavePerson(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.body() != null) {
                    Utils.toastMsg(context, "" + response.body().getGetKeyDetailsResponse().getKeyDetails().getWhereAt());
                }

            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }

    private void callSearchPerson(SearchPersonRequest KeyIdentty, String AccessToken) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder.build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Utils.msg(context, "841")).addConverterFactory(GsonConverterFactory.create()).
                client(client);
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<KeyDetailsResponce> call = iApiRequest.getSearchPerson(KeyIdentty, AccessToken);
        call.enqueue(new Callback<KeyDetailsResponce>() {
            @Override
            public void onResponse(Call<KeyDetailsResponce> call, retrofit2.Response<KeyDetailsResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.body() != null) {
                    Utils.toastMsg(context, "" + response.body().getGetKeyDetailsResponse().getKeyDetails().getWhereAt());
                }

            }

            @Override
            public void onFailure(Call<KeyDetailsResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.toastMsg(context, "" + t);
            }
        });
    }


    private void showPopUPNotification(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle("Confirmation Message !");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static boolean validateDate(String text) {
        int count = 0;
        if (text.length() > 11 || text.length()<10) {
            return true;
        }

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '-') {
                count++;
            }
        }

        if (count >2 || count<2) {
            return true;
        }

        if(count==2){
            int pos1 = text.indexOf("-");
            int pos2 = text.lastIndexOf("-");

            String year = text.substring(pos2+1, text.length());
            if(year.length()>4 || year.length()<4){
                return true;
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        df.setLenient(false);
        try {
            df.parse(text);
            return false;
        } catch (ParseException ex) {
            return true;
        }
    }

}