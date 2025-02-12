package com.isl.taskform;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.modal.FieldList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;

public class GetFormData extends AsyncTask<Void, Void, Void> {
    TaskCompleted taskComplete;
    ProgressDialog pd;
    Context con;
    FieldList fieldList;
    String taskId = "";
    AppPreferences mAppPreferences;

    public GetFormData(Context con,String taskId) {
        this.taskId = taskId;
        this.con = con;
        mAppPreferences = new AppPreferences(con);
        taskComplete = (TaskCompleted)con;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show( con, null, "Loading..." );
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
        Gson gson = new Gson();

        nameValuePairs.add( new BasicNameValuePair(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId() ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.TASK_STATE_ID_ALIAS, "" + taskId ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.ADD_PARAM_ALIAS, "" ) );
        nameValuePairs.add( new BasicNameValuePair( AppConstants.LANGUAGE_CODE_ALIAS, mAppPreferences.getLanCode() ) );

        try {
            String url = mAppPreferences.getConfigIP() + WebMethods.urlFormTask;
            String res = Utils.httpPostRequest( con, url, nameValuePairs );
            fieldList = gson.fromJson( res, FieldList.class );
        } catch (Exception e) {
            e.printStackTrace();
            fieldList = null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if ((fieldList == null)) {
            Utils.toastMsg(con, AppConstants.msg_form );
        } else if (fieldList != null) {
            if (fieldList.getFieldList().size() > 0) {
                DataBaseHelper db = new DataBaseHelper(con);
                db.open();
                db.clearTaskForm( taskId );
                db.taskForm( fieldList.getFieldList(), taskId );
                //db.insertFormData( fieldList.getFieldList(), taskId );

                if(taskId.equalsIgnoreCase( "11" )){
                    db.dataTS(null, null,"27",db.getLoginTimeStmp("27","0"),3,"0");
                }else if(taskId.equalsIgnoreCase( "12" )){
                    db.dataTS(null, null,"27",db.getLoginTimeStmp("27","0"),2,"0");
                }else  if(taskId.equalsIgnoreCase( "13" )){
                    db.dataTS(null, null,"27",db.getLoginTimeStmp("27","0"),4,"0");
                }else  if(taskId.equalsIgnoreCase( "21" )){
                    db.dataTS(null, null,"27",db.getLoginTimeStmp("27","0"),5,"0");
                }
                db.close();
            }
        } else {
            Utils.toast(con, "13" );
        }
        if (pd.isShowing()) {
            pd.dismiss();
        }
        taskComplete.initializeForm();
        super.onPostExecute( result );
    }
}