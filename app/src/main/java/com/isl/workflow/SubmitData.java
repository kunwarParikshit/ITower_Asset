package com.isl.workflow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.hsse.HsseConstant;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.modal.CamundaVariable;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.UIUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmitData extends AsyncTask<String, Void, String> {

    private String res,formDataJson,instanceId,oper,taskId,requestFlag,newAssignee,oldTktStatus,oassigntogrp;
    private String auditTrail;
    public ProgressDialog progressDialog = null;
    AppPreferences mAppPreferences;
    Context context;

    public SubmitData(Context context, String formDataJson, String auditTrail, String instanceId,
                      String oper,String taskId,String oldTktStatus,String oassigntogrp,String requestFlag,String newAssignee ) {

        this.formDataJson = formDataJson;
        this.instanceId = instanceId;
        this.oper = oper;
        this.instanceId = instanceId;
        this.auditTrail = auditTrail;
        this.taskId = taskId;
        this.oldTktStatus = oldTktStatus;
        this.oassigntogrp  = oassigntogrp;
        this.requestFlag = requestFlag;
        this.newAssignee = newAssignee;
        progressDialog = new ProgressDialog(context);
        mAppPreferences = new AppPreferences(context);
        this.context = context;
        return;

    }

    protected void onPreExecute() {
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    protected String doInBackground(String... urls) {
        try {
            Map<String, CamundaVariable> requestDetail = null;
            Gson gson = new Gson();
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
            String sys = null;
            if(formDataJson.contains("variables")){
                JSONObject reader = new JSONObject(formDataJson);
                sys  = reader.getJSONObject("variables").toString();
                Type listType = new TypeToken<Map<String, CamundaVariable>>(){}.getType();
                requestDetail = gson.fromJson(sys, listType);
            }

            if(formDataJson.contains("variables")
                    &&formDataJson.contains("module")
                    && requestDetail.get("module").getValue().equalsIgnoreCase("AccessManagement")
                    && formDataJson.contains("implementerdate")
                    && formDataJson.contains("implementer")
                    && formDataJson.contains("approval2")
                    && formDataJson.contains("rqsts")
                    && requestDetail.get("implementerdate").getValue().length()>0
                    && requestDetail.get("implementer").getValue().length()>0
                    && requestDetail.get("rqsts").getValue().equalsIgnoreCase("5")
                    && requestDetail.get("approval2").getValue().equalsIgnoreCase("close")){
                formDataJson = formDataJson.replaceAll( "approval2","garbage" );
            }



            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
            nameValuePairs.add( new BasicNameValuePair( Constants.FORM_DATA, formDataJson));
            nameValuePairs.add( new BasicNameValuePair( Constants.INSTANCE_ID, instanceId));
            nameValuePairs.add( new BasicNameValuePair( AppConstants.LOGIN_ID, mAppPreferences.getLoginId()));
            nameValuePairs.add( new BasicNameValuePair( Constants.OPERATION, oper));
            nameValuePairs.add( new BasicNameValuePair( Constants.AUDIT_TRAIL, auditTrail));
            nameValuePairs.add( new BasicNameValuePair( Constants.REQUEST_FLAG, requestFlag));
            nameValuePairs.add( new BasicNameValuePair( Constants.TASK_ID, taskId));
            nameValuePairs.add( new BasicNameValuePair( Constants.NEW_ASSGINEE, newAssignee));
            nameValuePairs.add( new BasicNameValuePair( HsseConstant.OLD_TKT_STATUS, oldTktStatus));
            nameValuePairs.add( new BasicNameValuePair( HsseConstant.OLD_GRP, oassigntogrp));
            nameValuePairs.add( new BasicNameValuePair( Constants.USER_NAME,mAppPreferences.getName()));


            System.out.println("**************** formDataJson - "+formDataJson);
            System.out.println("**************** instanceId - "+instanceId);
            System.out.println("**************** LOGIN_ID - "+mAppPreferences.getLoginId());
            System.out.println("**************** AUDIT_TRAIL - "+auditTrail);
            System.out.println("**************** REQUEST_FLAG - "+requestFlag);
            System.out.println("**************** TASK_ID - "+taskId);
            System.out.println("**************** NEW_ASSGINEE - "+newAssignee);
            System.out.println("**************** oldTktStatus - "+oldTktStatus);
            System.out.println("**************** oassigntogrp - "+oassigntogrp);
            System.out.println("**************** oper - "+oper);
            System.out.println("**************** user - "+mAppPreferences.getName());
            System.out.println("**************** urls[0] - "+urls[0]);

            ////New
            if(!formDataJson.isEmpty())
            {
                if(formDataJson.contains("taskname") && formDataJson.contains("rqststxt") && formDataJson.contains("asdt") && formDataJson.contains("aedt")) {
                    JSONObject jsonObject1 = new JSONObject(formDataJson);
                    JSONObject jsonObject = jsonObject1.getJSONObject("variables");
                    String rqststxt = jsonObject.getJSONObject("rqststxt").getString("value");
                    String taskname = jsonObject.getJSONObject("taskname").getString("value");
                    String asdt = jsonObject.getJSONObject("asdt").getString("value");
                    String aedt = jsonObject.getJSONObject("aedt").getString("value");
                    if (taskname.equalsIgnoreCase("AccessRequesttoc")) {
                        if(rqststxt.equalsIgnoreCase("Activity not Attended"))
                        {
                            res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
                            res = res.replace("[", "").replace("]", "");
                        }
                        else {
                            if(!asdt.isEmpty() && asdt!=null && !aedt.isEmpty() && aedt!=null )
                            {
                                res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
                                res = res.replace("[", "").replace("]", "");
                            }
                            else
                            {
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"Please select actual start date and end date",Toast.LENGTH_SHORT).show();

                                    }
                                });



                            }
                        }

                    }

                    else
                    {
                        res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
                        res = res.replace("[", "").replace("]", "");
                    }

                }

                else
                {
                    res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
                    res = res.replace("[", "").replace("]", "");

                }

            }
           // res = HttpUtils.httpPostRequest(urls[0],nameValuePairs);
          // System.out.println("**************** res for cp edit - "+res);
       //     res = res.replace("[", "").replace("]", "");
        } catch (Exception e) {
            res = null;
        }
        return res;
    }
    public void onPostExecute(String result) {
       // Toast.makeText(context,res,Toast.LENGTH_LONG).show();
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        DataSubmitUtils.dataSubmitted(context,result,mAppPreferences.getLanCode());
    }


}

