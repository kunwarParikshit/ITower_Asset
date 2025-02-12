package com.isl.workflow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.modal.MetaDataObject;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MetaData extends AsyncTask<Void, Void, String>{

    ProgressDialog pd;
    Context contxt;
    String modifiedMetaData;
    int moduleIndex;
    AppPreferences mAppPreferences;

    public MetaData(Context con,String modifiedMetaData,int moduleIndex) {
        this.contxt = con;
        this.modifiedMetaData = modifiedMetaData;
        this.moduleIndex = moduleIndex;
        this.mAppPreferences = new AppPreferences(con);
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(contxt, null, "Loading...");
        pd.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = null;
        try {
            response = HttpUtils.httpGetRequest(AppConstants.moduleList.get(moduleIndex).getBaseurl()+ WebAPIs.metaData+modifiedMetaData);
        } catch (Exception e) {
            e.printStackTrace();
            response = null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if ((result == null)) {
            Utils.toast(contxt, "70");
        } else {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MetaDataObject>>() {}.getType();
            ArrayList<MetaDataObject> metaDataList = gson.fromJson(result, listType);

            if (metaDataList !=null && metaDataList.size() > 0) {
                WorkFlowDatabaseHelper dbHelper = new WorkFlowDatabaseHelper(contxt);
                dbHelper.open();
                dbHelper.clearWorkFlowMetaData(modifiedMetaData);
                dbHelper.insertWorkFlowMetaData(metaDataList);
                dbHelper.updateDataTimestamp(modifiedMetaData);
                dbHelper.close();
            }
        }

        if (pd !=null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
