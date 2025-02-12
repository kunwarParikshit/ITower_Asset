package com.isl.incident;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.ResponseRemarks;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 1/20/2020.
 */

public class AuditTrailDetails extends Fragment {
    View view;
    ResponseRemarks response_remarks = null;
    AppPreferences mAppPreferences;
    DataBaseHelper dbHelper;
    String moduleUrl = "",url = "";
    ListView lv;
    TextView txt_no_remarks_added;
    public AuditTrailDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.tt_tab_details, container, false );
        lv = (ListView) view.findViewById(R.id.lv);
        txt_no_remarks_added = (TextView) view.findViewById(R.id.txt_no_remarks_added);

        mAppPreferences = new AppPreferences(getActivity());
        dbHelper = new DataBaseHelper(getActivity());
        dbHelper.open();
        if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
            moduleUrl = dbHelper.getModuleIP("HealthSafty");
        }else{
            moduleUrl = dbHelper.getModuleIP("Incident");
        }
        //String strtext = getArguments().getString("edttext");
        String ticket_id = getActivity().getIntent().getExtras().getString("id");
        if (Utils.isNetworkAvailable(getActivity())) {
            new TikcetDetailsTask(getActivity(), ticket_id).execute();
           } else {
            Utils.toast(getActivity(), "17");
        }
        return view;
    }


    public class TikcetDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String ticket_id;

        public TikcetDetailsTask(Context con, String ticket_id) {
            this.con = con;
            this.ticket_id = ticket_id;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ticketID", ticket_id));
                //get ticket audit remarks
                   if(moduleUrl.equalsIgnoreCase("0")){
                        url=mAppPreferences.getConfigIP()+ WebMethods.url_getTicketRemarks;
                    }else{
                        url=moduleUrl+ WebMethods.url_getTicketRemarks;
                        //url=moduleUrl+ WebMethods.url_GetTicketHistory;
                    }
                    String res = Utils.httpPostRequest(con,url, nameValuePairs);
                    response_remarks = new Gson().fromJson(res,ResponseRemarks.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response_remarks == null) {
                Utils.toast(getActivity(), "267");
                getActivity().finish();
            } else {
                SetRemarks();
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    void SetRemarks() {
        if (response_remarks != null && response_remarks.getRemarks()!=null && response_remarks.getRemarks().size() > 0) {
            lv.setVisibility(View.VISIBLE);
            txt_no_remarks_added.setVisibility(View.GONE);
            lv.setFastScrollEnabled(true);
            lv.setAdapter(new AdapterRemarks(getActivity(),response_remarks.getRemarks()));
        } else {
            lv.setVisibility(View.GONE);
            txt_no_remarks_added.setVisibility(View.VISIBLE);
            txt_no_remarks_added.setText(Utils.msg(getActivity(),"267"));
        }
    }
}
