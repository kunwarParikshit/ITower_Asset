package com.isl.workflow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.modal.ProcessStartResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InitiateWorkFlowProcess extends AsyncTask<Void, Void, String> {
    ProgressDialog pd;
    Context con;
    AppPreferences mAppPreferences;
    String processName;
    ProcessInitialization taskComplete;

    public InitiateWorkFlowProcess(Context con, String processName) {
        this.con = con;
        mAppPreferences = new AppPreferences(con);
        this.processName = processName;
        taskComplete = (ProcessInitialization)con;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show( con, null, "Initiating Process..." );
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
        Gson gson = new Gson();
        nameValuePairs.add( new BasicNameValuePair( Constants.CAMUNDA_PROCESS_NAME, processName) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.LOGIN_ID, "" + mAppPreferences.getLoginId()));
        nameValuePairs.add( new BasicNameValuePair( AppConstants.LANGUAGE_CODE_ALIAS, mAppPreferences.getLanCode()));
        String response="";

        try {
            response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.processInitialization,nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
            response = null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        ProcessStartResponse processInitializationRsp = null;

        if ((result == null)) {
            Utils.toastMsg(con, AppConstants.msg_form );
        } else{
            //System.
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ProcessStartResponse>>() {}.getType();
            List<ProcessStartResponse> processInitializationRspList = gson.fromJson(result, listType);
            if(processInitializationRspList.size()>0){
                processInitializationRsp = processInitializationRspList.get(0);
            }
        }

        if (pd.isShowing()) {
            pd.dismiss();
        }

        taskComplete.processInitializationCompleted(processInitializationRsp);
        super.onPostExecute( result );
    }
}