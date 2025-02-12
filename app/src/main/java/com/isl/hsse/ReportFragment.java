package com.isl.hsse;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.MenuDetail;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.WebAPIs;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import infozech.itower.R;

public class ReportFragment extends Fragment {
    RecyclerView rvList;
    View view;
    TextView tvMsg;
    LinearLayout datLayout;
    RelativeLayout msgLayout;
    AppPreferences mAppPreferences;
    List<HashMap<String,Object>> requestList = null;
    List<NameValuePair> nameValuePairs;
    String editRight = "";
    public ReportFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate( R.layout.frag_personal_detail,container, false);

        initialize();

        if(getArguments()!=null){
            String source = getArguments().getString(HsseConstant.REPORT_NAME);
            if("AssignedTab".equalsIgnoreCase(source)){
                nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("assigntogrp",mAppPreferences.getUserGroup()));
                nameValuePairs.add(new BasicNameValuePair("rptType","3"));
            } else if("RaisedTab".equalsIgnoreCase(source)){
                nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getName()));
                //nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getLoginId()));
                nameValuePairs.add(new BasicNameValuePair("rptType","4"));
            }else{
                if(getActivity().getIntent().getExtras()!=null){
                    String filterData = getActivity().getIntent().getExtras().getString("filterData");
                    try {
                        JSONObject obj = new JSONObject( filterData );
                        Iterator<String> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            nameValuePairs.add(new BasicNameValuePair(key,obj.get(key).toString()));
                        }
                    }
                    catch (Exception e){
                    }
                }
            }
        }

        if (Utils.isNetworkAvailable(getActivity())) {
            new GetRequestList(getContext(),nameValuePairs).execute();
        } else {
            Utils.toast(getActivity(), "17");
            msgLayout.setVisibility(View.VISIBLE);
            datLayout.setVisibility(View.GONE);
            tvMsg.setBackgroundResource(R.drawable.retry);
        }

        tvMsg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    msgLayout.setVisibility(View.GONE);
                    datLayout.setVisibility(View.VISIBLE);
                    tvMsg.setBackgroundResource(0);
                    new GetRequestList(getActivity(),nameValuePairs).execute();
                } else {
                    Utils.toast(getActivity(), "17");
                    msgLayout.setVisibility(View.VISIBLE);
                    datLayout.setVisibility(View.GONE);
                    tvMsg.setBackgroundResource(R.drawable.retry);
                }
            }
        });

        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getActivity())) {
                    new GetRequestList(getContext(),nameValuePairs).execute();
                } else {
                    Utils.toast(getActivity(), "17");
                    msgLayout.setVisibility(View.VISIBLE);
                    datLayout.setVisibility(View.GONE);
                    tvMsg.setBackgroundResource(R.drawable.retry);
                }
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    private void initialize(){

        rvList = (RecyclerView) view.findViewById(R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
        rvList.setLayoutManager( horizontalLayoutManager );
        mAppPreferences = new AppPreferences(getActivity());
        nameValuePairs = new ArrayList<NameValuePair>();

        TextView bt_add_person = (TextView) view.findViewById( R.id.bt_add_person);
        bt_add_person.setVisibility(View.GONE);
        TextView bt_add_logbook = (TextView) view.findViewById( R.id.bt_add_logbook);
        bt_add_logbook.setVisibility(View.GONE);
        TextView bt_add_corective_action = (TextView) view.findViewById( R.id.bt_add_corective_action);
        bt_add_corective_action.setVisibility(View.GONE);

        msgLayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        datLayout = (LinearLayout)  view.findViewById( R.id.ll_sites);
        tvMsg = (TextView) view.findViewById( R.id.txt_no_ticket);

        DataBaseHelper dbHelper = new DataBaseHelper( getActivity() );
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( mAppPreferences.getModuleName() );
        dbHelper.close();
        for (MenuDetail menu : subMenuList) {
            switch (menu.getName()) {
                case "Add Request Tab":
                    editRight = menu.getRights().toString();
                    if (!menu.getRights().toString().contains( "A" )) {
                        bt_add_person.setVisibility( View.GONE );
                    }
                    break;
            }
        }
    }

    public class GetRequestList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        List<NameValuePair> nameValuePairs;

        public GetRequestList(Context con,List<NameValuePair> nameValuePairs) {
            this.con = con;
            this.nameValuePairs = nameValuePairs;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                /*List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(10);
                nameValuePairs1.add(new BasicNameValuePair("cid", ""));
                nameValuePairs1.add(new BasicNameValuePair("zid",""));
                nameValuePairs1.add(new BasicNameValuePair("clid",""));
                nameValuePairs1.add( new BasicNameValuePair( "sid", ""));
                nameValuePairs1.add( new BasicNameValuePair( "loginId", ""));
                nameValuePairs1.add( new BasicNameValuePair( "dateType", ""));
                nameValuePairs1.add( new BasicNameValuePair( "sdate", ""));
                nameValuePairs1.add( new BasicNameValuePair( "edate", ""));
                nameValuePairs1.add( new BasicNameValuePair( "rptType", ""));
                nameValuePairs1.add( new BasicNameValuePair( "src", ""));
                nameValuePairs1.add( new BasicNameValuePair( "tktstatus", ""));
                nameValuePairs1.add( new BasicNameValuePair( "incidentType", ""));
                nameValuePairs1.add( new BasicNameValuePair( "ticketId", ""));
                nameValuePairs1.add( new BasicNameValuePair( "ticketType", ""));
                nameValuePairs1.add( new BasicNameValuePair( "severity", ""));
                nameValuePairs1.add( new BasicNameValuePair( "forcedEntry", ""));
                nameValuePairs1.add( new BasicNameValuePair( "AlarmNtficatn", ""));
                nameValuePairs1.add( new BasicNameValuePair( "rca_Cat_Id", ""));
                nameValuePairs1.add( new BasicNameValuePair( "rca_SubCat_Id", ""));
                nameValuePairs1.add( new BasicNameValuePair( "createdBy", ""));
                nameValuePairs1.add( new BasicNameValuePair( "assigntogrp", ""));*/
                String url ="";
                url= AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.getHSSEReport;
                String response = HttpUtils.httpPostRequest(url, nameValuePairs);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<HashMap<String,Object>>>() {}.getType();
                requestList = gson.fromJson(response,listType);
            } catch (Exception e) {
                e.printStackTrace();
                requestList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //msgLayout.setVisibility(View.GONE);
            //datLayout.setVisibility(View.VISIBLE);
            //rvList.setAdapter( new ReportAdapter(getActivity(),requestList,"PersonDetails"));
            if (requestList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                datLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (requestList!= null && requestList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                datLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new ReportAdapter(getActivity(),requestList,"",editRight));
            } else {
                msgLayout.setVisibility(View.VISIBLE);
                datLayout.setVisibility(View.GONE);
                tvMsg.setText("No data found.");
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(getActivity())) {
            new GetRequestList(getContext(),nameValuePairs).execute();
        } else {
            Utils.toast(getActivity(), "17");
            msgLayout.setVisibility(View.VISIBLE);
            datLayout.setVisibility(View.GONE);
            tvMsg.setBackgroundResource(R.drawable.retry);
        }

    }
}
