package com.isl.workflow.form.control;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.api.IApiRequest;
import com.isl.api.UnsafeHttpClient;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.GPSTracker;
import com.isl.modal.Sera4Responce;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.CallService;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.modal.AuditTrail;
import com.isl.workflow.modal.CamundaModVariables;
import com.isl.workflow.modal.CamundaVariable;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.OnChangeDetail;
import com.isl.workflow.modal.request.SavePersonRequest;
import com.isl.workflow.modal.request.SearchPersonRequest;
import com.isl.workflow.modal.request.UpdateKeyValRequest;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.DateTimeUtils;
import com.isl.workflow.utils.UIUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ToggleControl {

    private static Context context;
    public Button addToggleButton(final Context context, Fields field) {

        this.context = context;
        Switch switchControl = new Switch( context);
        switchControl.setText(field.getTitle());

        if(field.getOntitle()!=null){
            switchControl.setTextOn(field.getOntitle());
        }
        if(field.getOfftitle()!=null){
            switchControl.setTextOff(field.getOfftitle());
        }

        //switchControl.setShowText(true);
        UIUtils.toggleButtonUI(context,switchControl);

        switchControl.setId(Integer.parseInt(field.getId()));
        switchControl.setTag(field.getKey());
        switchControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onClickListener(context,arg0);
            }
        } );

        switchControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });

        FormCacheManager.getFormControls().get(field.getId()).setTgButtonCtrl(switchControl);
        WorkFlowUtils.resetEnableControl(context,field);
        WorkFlowUtils.resetShowHideControl(context,field);

        return switchControl;
    }

    private void onClickListener(Context context,View arg0) {
        String fieldKey = (String) arg0.getTag();
        Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get(fieldKey);

       // boolean value = FormCacheManager.getFormControls().get(formControl.getId()).getTgButtonCtrl().isChecked();
       /* String key = "";
        boolean isKeyEditable =false;
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
            key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
            isKeyEditable = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().isEnabled();
        }*/

        String key = "";
        int isVisible = 0;
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
            key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
        }
        boolean KeyEditable = false;
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("validateKeySer")) {
            Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer");
            KeyEditable = FormCacheManager.getFormControls().get(formControl3.getId()).getButtonCtrl().isEnabled();
            isVisible= FormCacheManager.getFormControls().get(formControl3.getId()).getButtonCtrl().getVisibility();

        }
        if ((key.isEmpty() || KeyEditable) && isVisible ==0) {
            Utils.toastMsg(context,Utils.msg(context,"852"));
           // Toast.makeText(context, "Please Enter Key Serial First and Validate key", Toast.LENGTH_SHORT).show();
            FormCacheManager.getFormControls().get(formControl.getId()).getTgButtonCtrl().setChecked(false);
            return;

        }else {
        //Validate the data if any
        if (formControl.getOnClick()!=null && formControl.getOnClick().getValidate() != null && formControl.getOnClick().getValidate().size() > 0) {
            if(!DataSubmitUtils.validateFieldList(context,formControl.getOnClick().getValidate())){
                FormCacheManager.getFormControls().get(formControl.getId()).getTgButtonCtrl().setChecked(false);
                return;
            }
        }

      /*  if(formControl.getOnClick()!=null && formControl.getOnClick().getMessage()!=null){
            //System.out.println("Cofirmation*******************************111111111111111");
            DataSubmitUtils.confirmationMessage(context,formControl.getOnClick(),String.valueOf(arg0.getId()),null);

        } else{
            //System.out.println("Cofirmation*******************************");
            DataSubmitUtils.onChangeTask(context,true,String.valueOf(arg0.getId()),null);
        }*/

        if(FormCacheManager.getFormConfiguration().getFormFields().containsKey( "sitelockid" )
           && FormCacheManager.getFormControls().
                get(FormCacheManager.getFormConfiguration().getFormFields().get("sitelockid").getId()).getValue()!=null
                && FormCacheManager.getFormControls().
                get(FormCacheManager.getFormConfiguration().getFormFields().get("sitelockid").getId()).getValue().toString().length()>0){
            if(formControl.getOnClick()!=null && formControl.getOnClick().getMessage()!=null){
                confirmationMessage(context,formControl.getOnClick(),String.valueOf(arg0.getId()),null);
            }else{
                String onoff = WorkFlowUtils.getValueFromLocalData("actstartstop","actstartstop",false);
                keyGenerateSera4(context,String.valueOf(arg0.getId()),onoff);
            }
        }else{
            if(formControl.getOnClick()!=null && formControl.getOnClick().getMessage()!=null)
                DataSubmitUtils.confirmationMessage(context, formControl.getOnClick(), String.valueOf(arg0.getId()), null);
            else{
                //System.out.println("Cofirmation*******************************");
                DataSubmitUtils.onChangeTask(context,true,String.valueOf(arg0.getId()),null);
            }
        }
        }
    }


    public static void keyGenerateSera4(Context context, String id,String callingType){
        HashMap<String,Object> data = new HashMap<String,Object>();
        CamundaVariable variable = new CamundaVariable();
        String url = AppConstants.moduleList.get(FormCacheManager.getAppPreferences().
                getModuleIndex()).getBaseurl() + WebAPIs.requestKeygenerate;

        String tmpVal = (String)FormCacheManager.getPrvFormData().get("txnid");
        tmpVal = (String)FormCacheManager.getPrvFormData().get("sid");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("sid",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("asdt","asdt",false);
        if(tmpVal!=null && tmpVal.length()==0){
            tmpVal = DateTimeUtils.currentDateTime("dd.MM.yyyy HH:mm");
        }
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("asdt",variable);



        tmpVal = WorkFlowUtils.getValueFromLocalData("aedt","aedt",false);
        if(tmpVal!=null && tmpVal.length()==0){
            tmpVal = DateTimeUtils.currentDateTime("dd.MM.yyyy HH:mm");
        }
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("aedt",variable);


        tmpVal = (String)FormCacheManager.getPrvFormData().get("pedt");
        //tmpVal = DateTimeUtils.currentDateTimePlusOneDay("dd.MM.yyyy HH:mm");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("pedt",variable);

        //Toast.makeText(context,"pedt11==="+tmpVal,Toast.LENGTH_LONG).show();


        variable = new CamundaVariable();
        variable.setValue(callingType);
        data.put("callingType",variable);

        tmpVal = (String)FormCacheManager.getPrvFormData().get("sitelockid");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("sitelockid",variable);


        tmpVal = (String)FormCacheManager.getPrvFormData().get("locktxt");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("locktxt",variable);


        tmpVal = (String)FormCacheManager.getPrvFormData().get("implmail");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("implmail",variable);

        tmpVal = (String)FormCacheManager.getPrvFormData().get("implname");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("implname",variable);

        /*
         String tmpVal = (String)FormCacheManager.getPrvFormData().get("txnid");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("txnid",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("sid","sid",false);
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("sid",variable);

         variable = new CamundaVariable();
        variable.setValue("2019-05-08 11:35:43");
        data.put("asdt",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("pedt","pedt",false);
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("pedt",variable);

         variable = new CamundaVariable();
        variable.setValue("2019-05-08 11:36:39");
        data.put("aedt",variable);

        variable = new CamundaVariable();
        variable.setValue(callingType);
        data.put("callingType",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("sitelockid","sitelockid",false);
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("sitelockid",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("locktxt","locktxt",false);
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("locktxt",variable);

        ariable = new CamundaVariable();
        variable.setValue("dhruv_devgun1@yahoo.co.in");
        data.put("implmail",variable);

        tmpVal = (String)FormCacheManager.getPrvFormData().get("implname");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("implname",variable);

        */

        CamundaModVariables inVariable = new CamundaModVariables();
        inVariable.setModifications(data);
        String jsonData = Constants.gson.toJson(inVariable);

        //Toast.makeText(context,"pedt==="+data.get("pedt").toString(),Toast.LENGTH_LONG).show();

        CallSera4API task = new CallSera4API(context,id,callingType,jsonData,(String)FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID),
                (String)FormCacheManager.getPrvFormData().get(Constants.TASK_ID));
        task.execute(url);
    }

    public static class CallSera4API  extends AsyncTask<String, Void, String> {

        private String res,formDataJson,instanceId,taskId;
        public ProgressDialog progressDialog = null;
        AppPreferences mAppPreferences;
        Context context;
        String id;
        String msg = "";
        String callingType = "";
        public CallSera4API(Context context,String id, String callingType,
                            String formDataJson,String instanceId,String taskId) {
            this.id = id;
            this.formDataJson = formDataJson;
            this.instanceId = instanceId;
            this.instanceId = instanceId;
            this.taskId = taskId;
            progressDialog = new ProgressDialog(context);
            mAppPreferences = new AppPreferences(context);
            this.context = context;
            this.callingType = callingType;
           }

        protected void onPreExecute() {
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        }

        protected String doInBackground(String... urls) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( Constants.FORM_DATA, formDataJson));
                nameValuePairs.add( new BasicNameValuePair( Constants.INSTANCE_ID, instanceId));
                nameValuePairs.add( new BasicNameValuePair( AppConstants.LOGIN_ID, mAppPreferences.getLoginId()));
                nameValuePairs.add( new BasicNameValuePair( Constants.TASK_ID, taskId));
                res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
                //res = res.replace("[", "").replace("]", "");
            } catch (Exception e) {
                res = null;
            }
            return res;
        }

        public void onPostExecute(String result) {
            if (progressDialog !=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Sera4Responce>>() {}.getType();
            ArrayList<Sera4Responce> Sera4Responce = gson.fromJson(result, listType);

             //if(WorkFlowUtils.getValueFromLocalData("actstartstop","actstartstop",
               //     false).equalsIgnoreCase("true")) {
               if(callingType.equalsIgnoreCase("true")) {
                try {
                    if (Sera4Responce != null && Sera4Responce.size() >0 && Sera4Responce.get(0).getFlag()!=null){
                        if (Sera4Responce.get(0).getFlag().equalsIgnoreCase( "S" )) {
                            DataSubmitUtils.onChangeTask( context, true, id, null );
                        } else {
                            DataSubmitUtils.onChangeTask( context, false, id, null );
                            UIUtils.toastMsg( context, Sera4Responce.get(0).getMessage(), mAppPreferences.getLanCode() );

                        }
                        msg = Sera4Responce.get(0).getMessage();
                        UIUtils.toastMsg( context, msg, mAppPreferences.getLanCode());
                    }else{
                        DataSubmitUtils.onChangeTask( context, false, id, null );
                        msg = "Error while generating Key in Sera4 Server";
                        UIUtils.toastMsg( context, msg, mAppPreferences.getLanCode());
                    }

                } catch (Exception e) {
                    DataSubmitUtils.onChangeTask( context, false, id, null );
                    msg = "Error while generating Key in Sera4 Server";
                    UIUtils.toastMsg( context, msg, mAppPreferences.getLanCode());
                }


            }else{
                DataSubmitUtils.onChangeTask( context, true, id, null );
                 msg = "";
                try {
                    if (Sera4Responce != null && Sera4Responce.size() >0
                            && FormCacheManager.getFormConfiguration().getFormFields().containsKey("lpsdt")
                            && FormCacheManager.getFormConfiguration().getFormFields().containsKey( "lpedt" ) ){

                        Fields lockOTfield = FormCacheManager.getFormConfiguration().getFormFields().get("lpsdt");
                        FormFieldControl formControl = FormCacheManager.getFormControls().get(lockOTfield.getId());

                        Fields lockCTfield = FormCacheManager.getFormConfiguration().getFormFields().get("lpedt");
                        FormFieldControl formControl1 = FormCacheManager.getFormControls().get(lockCTfield.getId());

                        StringBuilder str1 = new StringBuilder("");
                        StringBuilder str2 = new StringBuilder("");
                        StringBuilder str3 = new StringBuilder("");

                        int index = 0;
                        for (Sera4Responce locktime : Sera4Responce) {
                            if (Sera4Responce.get(index).getFlag()!=null &&
                                    Sera4Responce.get(index).getFlag().equalsIgnoreCase( "S" )) {
                                str1.append(locktime.getLpsdt()).append(",");
                                str2.append(locktime.getLpedt()).append(",");
                                str3.append(locktime.getMessage()).append(",");
                            }else{
                                str3.append(locktime.getMessage()).append(",");
                            }
                            index++;
                        }

                        String lockOT = str1.toString();
                        if (lockOT.length() > 0) {
                            lockOT = lockOT.substring( 0, lockOT.length() - 1 );
                            formControl.getTextBoxCtrl().setText(lockOT);
                        }

                        String lockCT = str2.toString();
                        if (lockCT.length() > 0) {
                            lockCT = lockCT.substring( 0, lockCT.length() - 1 );
                            formControl1.getTextBoxCtrl().setText(lockCT);
                        }

                        msg = str3.toString();
                        if (msg.length() > 0) {
                            msg = msg.substring( 0, msg.length() - 1 );
                        }

                    }
                } catch (Exception e) {
                    msg = "Error while Fecthing Lock Open/Close Time";
                }
            }
            Sera4APIAuditLog(context,msg,callingType);
     }
    }

    public static void confirmationMessage(Context ctx, OnChangeDetail onChangeDetail,
                                           final String id, final DropdownValue selectedVal) {

        String msg = null;
        //Validate whether to show confirmation message or not.
        if(onChangeDetail.getMessage().getConfirmexp()!=null){
            if(onChangeDetail.getMessage().getConfirmexp().getExpression()!=null){
                Object result = WorkFlowUtils.evaluateExpression(onChangeDetail.getMessage().getConfirmexp(),false);
                //System.out.println("Confirm Expression is - "+org.mozilla.javascript.Context.toBoolean(result));
                if(org.mozilla.javascript.Context.toBoolean(result)){

                    if(onChangeDetail.getMessage().getConfirmexp().getMsgexp()!=null){
                        //System.out.println("Confirm Expression msg exp - "+onChangeDetail.getMessage().getConfirmexp().getMsgexp());
                        result = WorkFlowUtils.evaluateExpression(onChangeDetail.getMessage().getConfirmexp().getMsgexp(),false);
                        msg = org.mozilla.javascript.Context.toString(result);
                        //System.out.println("Confirm Expression is msg exp msg - "+org.mozilla.javascript.Context.toBoolean(result));
                    } else{
                        msg =onChangeDetail.getMessage().getConfirmexp().getMsg();
                    }
                } else{
                    DataSubmitUtils.onChangeTask(ctx,true,id,selectedVal);
                    return;
                }
            }
        } else{
            msg = onChangeDetail.getMessage().getMsg();
        }
        //System.out.println("Confirm Expression is msg exp msg -1212122 "+msg);
        if(msg==null && onChangeDetail.getMessage().getMsg()!=null){
            msg = onChangeDetail.getMessage().getMsg();
        }

        final Dialog actvity_dialog;
        actvity_dialog = new Dialog( ctx, R.style.FullHeightDialog);
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
        tv_header.setTypeface( Utils.typeFace(ctx));
        positive.setTypeface( Utils.typeFace(ctx) );
        negative.setTypeface( Utils.typeFace(ctx) );
        title.setTypeface( Utils.typeFace( ctx ) );
        title.setText(msg);
        title.setGravity( Gravity.CENTER_HORIZONTAL);
        positive.setText(onChangeDetail.getMessage().getPositiveText());

        if(onChangeDetail.getMessage().getNegativeText()!=null){
            negative.setText(onChangeDetail.getMessage().getNegativeText());
        } else{
            negative.setVisibility(View.INVISIBLE);
        }

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                String onoff = WorkFlowUtils.getValueFromLocalData("actstartstop","actstartstop",false);
                keyGenerateSera4(ctx,id,onoff);
                //onChangeTask(ctx,true,id,selectedVal);
            }
        } );

        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                DataSubmitUtils.onChangeTask(ctx,false,id,selectedVal);
            }
        } );
    }


    public static void Sera4APIAuditLog(Context context,String remarks,String callingType){

        HashMap<String,Object> data = new HashMap<String,Object>();
        CamundaVariable variable = new CamundaVariable();
        String subUrl = "/api/Service/UpdateRequestWorkFlow";
        String url = AppConstants.moduleList.get(FormCacheManager.getAppPreferences().
                getModuleIndex()).getBaseurl() + subUrl;

        String tmpVal = (String)FormCacheManager.getPrvFormData().get("txnid");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("txnid",variable);

        tmpVal = (String)FormCacheManager.getPrvFormData().get("sid");
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("sid",variable);

        tmpVal = WorkFlowUtils.getValueFromLocalData("asdt","asdt",false);
        if(tmpVal!=null && tmpVal.length()==0){
            tmpVal = DateTimeUtils.currentDateTime("dd.MM.yyyy HH:mm");
        }
        variable = new CamundaVariable();
        variable.setValue(tmpVal);
        data.put("asdt",variable);

        if(callingType.equalsIgnoreCase("false")){
            tmpVal = WorkFlowUtils.getValueFromLocalData("aedt","aedt",false);
            if(tmpVal!=null && tmpVal.length()==0){
                tmpVal = DateTimeUtils.currentDateTime("dd.MM.yyyy HH:mm");
            }
            variable = new CamundaVariable();
            variable.setValue(tmpVal);
            data.put("aedt",variable);
        }

        variable = new CamundaVariable();
        variable.setValue("0");
        data.put("isassigncng",variable);

        String latitude = "0.0";
        String longitude = "0.0";
        GPSTracker gps = new GPSTracker(context);
        if (gps.canGetLocation() == true) {
            latitude = String.valueOf( gps.getLatitude() );
            longitude = String.valueOf( gps.getLongitude() );
        }

        remarks = remarks +", lat: "+latitude+", long: "+longitude;
        variable = new CamundaVariable();
        variable.setValue(latitude);
        data.put(Constants.LATITUDE,variable);

        variable = new CamundaVariable();
        variable.setValue(longitude);
        data.put(Constants.LONGITUDE,variable);



       /* variable = new CamundaVariable();
        variable.setValue(callingType);
        data.put("actstartstop",variable);*/

        CamundaModVariables inVariable = new CamundaModVariables();
        inVariable.setModifications(data);
        String jsonData = Constants.gson.toJson(inVariable);


        List<AuditTrail> auditTrailObjList = new ArrayList<AuditTrail>();
        String auditJsondata ="";
        AuditTrail auditTrailObj = new AuditTrail();
        auditTrailObj.setLoginid(FormCacheManager.getAppPreferences().getLoginId());

        auditTrailObj.setLatitude(((CamundaVariable)data.get(Constants.LATITUDE)).getValue());
        auditTrailObj.setLongitude(((CamundaVariable)data.get(Constants.LONGITUDE)).getValue());
        auditTrailObj.setRemarks(remarks);
        auditTrailObj.setTxnid((String)FormCacheManager.getPrvFormData().get(Constants.TXN_ID));
        auditTrailObjList.add(auditTrailObj);
        auditJsondata = Constants.gson.toJson(auditTrailObjList);

        CallService task = new CallService(context, jsonData,auditJsondata,(String)FormCacheManager.getPrvFormData().get(Constants.PROCESS_INSTANCE_ID),"E",
               (String)FormCacheManager.getPrvFormData().get(Constants.TASK_ID),"","","",false,false,"");
        task.execute(url);


    }


}
