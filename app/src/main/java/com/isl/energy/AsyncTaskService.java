package com.isl.energy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;

import java.util.List;

import com.isl.util.Utils;
import com.isl.constant.AppConstants.ENERGY_TASK_TYPE;

/**
 * Created by dhakan on 11/16/2018.
 */


public class AsyncTaskService extends AsyncTask<String, String, String> {

    private Context mContext;
    private ProgressDialog mProgress;
    private List<NameValuePair> nameValuePairs;
    private TaskCompleted mCallback;
    private String url;
    private ENERGY_TASK_TYPE task;

    public AsyncTaskService(Context context,ENERGY_TASK_TYPE task,String url, List<NameValuePair> nameValuePairs){
        this.mContext = context;
        this.mCallback = (TaskCompleted) context;
        this.nameValuePairs=nameValuePairs;
        this.url=url;
        this.task=task;
    }

    @Override
    public void onPreExecute() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Please wait...");
        mProgress.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgress.setMessage(values[0]);
    }

    @Override
    protected String doInBackground(String... values) {
        String response="";
        try {
            response = Utils.httpPostRequest(mContext,url, nameValuePairs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }

    @Override
    protected void onPostExecute(String results) {
        mProgress.dismiss();
        //This is where you return data back to caller
        switch (task){
            case LOCATION_TASK:
                mCallback.onLocationTaskComplete(results);
                break;
            case ENERGY_PARAMS:
                mCallback.onEnergyParamTaskComplete(results);
                break;
            case ENERGY_PURCHASE_RPT:
                mCallback.onPurchaseGridRPTComplete(results);
                break;
            case ENERGY_FILLING_RPT:
                mCallback.onFillingGridRPTComplete(results);
                break;
           }
    }
}