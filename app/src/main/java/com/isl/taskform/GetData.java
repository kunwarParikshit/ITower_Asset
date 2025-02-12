package com.isl.taskform;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.modal.Data;
import com.isl.modal.FormControl;
import com.isl.util.Utils;
import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhakan on 11/26/2018.
 */

public class GetData extends AsyncTask<String, Void, String> {

    Context context;
    AppPreferences mAppPreferences;
    TaskCompleted TaskCompleted ;
    List<NameValuePair> nameValuePairs;
    ProgressDialog pd;
    FormControl formControl;

    public GetData(Context context, List<NameValuePair> nameValuePairs,FormControl formControl) {
        this.context = context;
        mAppPreferences = new AppPreferences(context);
        this.TaskCompleted = (TaskCompleted) context;
        this.nameValuePairs = nameValuePairs;
        this.formControl = formControl;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show( context, null, "Searching..." );
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String response = Utils.httpPostRequest( context, mAppPreferences.getConfigIP() + WebMethods.url_DataList, nameValuePairs );
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Data>>(){}.getType();
        ArrayList<Data> list = gson.fromJson(result, listType);
        TaskCompleted.autoCompleteDataFetchComplete(list,this.formControl);
    }
}
