package com.isl.workflow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.modal.Response;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.utils.UIUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CallService  extends AsyncTask<String, Void, String> {

    private String res,formDataJson,instanceId,oper,auditTrail,taskId,requestFlag,newAssignee,source,message;
    //public ProgressDialog progressDialog = null;
    AppPreferences mAppPreferences;
    Context context;
    boolean showMessage,closeForm;

    public CallService(Context context, String formDataJson, String auditTrail,String instanceId, String oper,String taskId,String requestFlag,String newAssignee, String message,boolean showMessage, boolean closeForm,String source) {

        this.formDataJson = formDataJson;
        this.instanceId = instanceId;
        this.oper = oper;
        this.instanceId = instanceId;
        this.auditTrail = auditTrail;
        this.taskId = taskId;
        this.requestFlag = requestFlag;
        this.newAssignee = newAssignee;
        //progressDialog = new ProgressDialog(context);
        mAppPreferences = new AppPreferences(context);
        this.context = context;
        this.message = message;
        this.closeForm = closeForm;
        this.showMessage = showMessage;
        this.source = source;

    }

    protected void onPreExecute() {
        /*progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);*/
    }

    protected String doInBackground(String... urls) {
        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
            nameValuePairs.add( new BasicNameValuePair( Constants.FORM_DATA, formDataJson));
            nameValuePairs.add( new BasicNameValuePair( Constants.INSTANCE_ID, instanceId));
            nameValuePairs.add( new BasicNameValuePair( AppConstants.LOGIN_ID, mAppPreferences.getLoginId()));
            nameValuePairs.add( new BasicNameValuePair( Constants.OPERATION, oper));
            nameValuePairs.add( new BasicNameValuePair( Constants.AUDIT_TRAIL, auditTrail));
            nameValuePairs.add( new BasicNameValuePair( Constants.REQUEST_FLAG, requestFlag));
            nameValuePairs.add( new BasicNameValuePair( Constants.TASK_ID, taskId));
           nameValuePairs.add( new BasicNameValuePair( Constants.NEW_ASSGINEE, newAssignee));


            System.out.println("**************** formDataJson - "+formDataJson);
            System.out.println("**************** instanceId - "+instanceId);
            System.out.println("**************** LOGIN_ID - "+mAppPreferences.getLoginId());
            System.out.println("**************** AUDIT_TRAIL - "+auditTrail);
            System.out.println("**************** REQUEST_FLAG - "+requestFlag);
            System.out.println("**************** TASK_ID - "+taskId);
            System.out.println("**************** NEW_ASSGINEE - "+newAssignee);           ;
            System.out.println("**************** oper - "+oper);
            System.out.println("**************** user - "+mAppPreferences.getName());

            System.out.println("**************** urls[0] - "+urls[0]);


            res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
            res = res.replace("[", "").replace("]", "");
        } catch (Exception e) {
            res = null;
        }
        return res;
    }

    public void onPostExecute(String result) {

        Response response = null;
        Gson gson = new Gson();
        try {
            response = gson.fromJson( result, Response.class );
            if(message!=null && message.length()>0){
                response.setMessage(message);
            }
        } catch (Exception e) {
            response = null;
        }

        if(showMessage){

            if (response != null) {
                //Toast.makeText( ActivityTaskForm.this,response.getMessage(),Toast.LENGTH_LONG ).show();
                UIUtils.toastMsg( context, response.getMessage(),mAppPreferences.getLanCode());
            } else {
                //Server Not Available
                Utils.toast(context, "13" );
            }
        }

        if (closeForm) {

            if (closeForm && response.getSuccess().equals( "true" )) {

                if (source.equalsIgnoreCase( "AddRequest" )) {
                    Intent i = new Intent( context, WorkflowImpl.class );
                    ((Activity)context).startActivity( i );
                    ((Activity)context).finish();
                } else {
                    ((Activity)context).finish();
                }
            }
        }

    }
}
