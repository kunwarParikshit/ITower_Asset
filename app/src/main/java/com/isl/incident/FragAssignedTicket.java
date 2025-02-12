package com.isl.incident;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSearchTktRptList;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

/**
 * Created by dhakan on 7/2/2019.
 */

public class FragAssignedTicket extends Fragment {
    View view;
    ListView ticket_list;
    TextView txt_no_ticket;
    AppPreferences mAppPreferences;
    String moduleUrl = "";
    String url = "";
    DataBaseHelper db = null;
    BeanSearchTktRptList response_tkt_rpt_list;
    public FragAssignedTicket() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.activity_existing_tickets_list,container, false);
        UtilsTask.ddFlag = true;
        mAppPreferences = new AppPreferences(getActivity());
        db = new DataBaseHelper(getActivity());
        db.open();

        if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
            moduleUrl = db.getModuleIP("HealthSafty");
        }else{
            moduleUrl = db.getModuleIP("Incident");
        }

        txt_no_ticket = (TextView) view.findViewById(R.id.txt_no_ticket);
        Utils.msgText(getActivity(), "141", txt_no_ticket);

        //mAppPreferences.setTTtabSelection(0);
        ticket_list = (ListView) view.findViewById(R.id.lv_tickets);
        ticket_list.setDivider(null);
        if (Utils.isNetworkAvailable(getActivity())) {
            new AssignedTicketsTask(getActivity()).execute();
        } else {
            //message for No Internet Connection
            Utils.toast( getActivity(), "17" );
        }

        ticket_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Intent i = new Intent(getActivity(),TicketDetailsTabs.class);
                mAppPreferences.SetBackModeNotifi123(2);
                i.putExtra("id", response_tkt_rpt_list.getTicket_list().get(arg2).getTktId());
                i.putExtra("sid", response_tkt_rpt_list.getTicket_list().get(arg2).getSid());
                startActivity(i);
              }
        });
        return view;
 }

    public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String response;
        public AssignedTicketsTask(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
                nameValuePairs.add(new BasicNameValuePair("user_id", mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("site_id", ""));
                nameValuePairs.add(new BasicNameValuePair("status", "6,2011794,2011795,2011796"));
                nameValuePairs.add(new BasicNameValuePair("alarmId", "0"));
                nameValuePairs.add(new BasicNameValuePair("ticketType", "0"));
                nameValuePairs.add(new BasicNameValuePair("fromDate", ""));
                nameValuePairs.add(new BasicNameValuePair("toDate", ""));
                nameValuePairs.add(new BasicNameValuePair("circleId",mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneId",mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterId",mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("alarmFlag","1"));
                //mode 0 means get count and 1 means ticket for rpt
                nameValuePairs.add(new BasicNameValuePair("mode","1"));
                nameValuePairs.add(new BasicNameValuePair("tktType","0"));
                if(mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")){
                    nameValuePairs.add(new BasicNameValuePair("module","H"));
                }else{
                    nameValuePairs.add(new BasicNameValuePair("module","T"));
                }
                nameValuePairs.add( new BasicNameValuePair( "userCategory", mAppPreferences.getUserCategory()));      //1.0
                nameValuePairs.add( new BasicNameValuePair( "userSubCategory", mAppPreferences.getUserSubCategory()));

                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_get_tickets;
                }else{
                    url=moduleUrl+ WebMethods.url_get_tickets;
                }
                response = Utils.httpPostRequest(con,url,nameValuePairs);
                Gson gson = new Gson();
                response_tkt_rpt_list = gson.fromJson( response, BeanSearchTktRptList.class );
            } catch (Exception e) {
                response_tkt_rpt_list=null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                    pd.dismiss();
            }
            if (response_tkt_rpt_list == null) {
                    // Server Not Available
                    Utils.toast( getActivity(), "13" );
            } else if (response_tkt_rpt_list.getTicket_list() != null && response_tkt_rpt_list.getTicket_list().size() > 0) {
                    ticket_list.setAdapter(new AdapterTickets(getActivity(), response,mAppPreferences.getTTModuleSelection()));
                    ticket_list.setVisibility(View.VISIBLE);
                    RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                    rl_no_list.setVisibility(View.GONE);
             } else {
                    //Message for No Ticket Found;
                //WorkFlowUtils.toast( getActivity(), "141" );
                ticket_list.setVisibility(View.GONE);
                RelativeLayout rl_no_list = (RelativeLayout) view.findViewById(R.id.textlayout);
                rl_no_list.setVisibility(View.VISIBLE);
                }
             super.onPostExecute(result);
        }
    }





    }
