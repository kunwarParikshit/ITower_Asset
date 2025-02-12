package com.isl.taskform;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AsynTaskService extends AsyncTask<String, Void, String> {

    private String res,appParam,data,taskId,oper,flag,module;
    private JSONArray jsonArrStrImg;
    public ProgressDialog progressDialog = null;
    TaskCompleted TaskCompleted;
    AppPreferences mAppPreferences;

    public AsynTaskService(Context context,String module, String taskId, String appParam, String data, JSONArray jsonArrStrImg,String oper,String flag) {
        this.taskId = taskId;
        this.appParam=appParam;
        this.data=data;
        this.jsonArrStrImg = jsonArrStrImg;
        progressDialog = new ProgressDialog(context);
        mAppPreferences = new AppPreferences(context);
        this.TaskCompleted = (TaskCompleted) context;
        this.oper=oper;
        this.flag=flag;
        this.module=module;

    }

    protected void onPreExecute() {
        progressDialog.setMessage("Please Wait....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    protected String doInBackground(String... urls) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(25);
            res = Utils.httpMultipartBackground(urls[0],module,jsonArrStrImg.toString(),data,"","",
                    appParam,"M",mAppPreferences.getUserId(),taskId,mAppPreferences.getLanCode(),oper,flag);
            res = res.replace("[", "").replace("]", "");
        } catch (Exception e) {
            res = null;
        }
        return res;
    }

    public void onPostExecute(String result) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        TaskCompleted.dataSubmitted(result);

    }

}
