package com.isl.workflow.tabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 6/23/2020.
 */

public class MyRequest extends Fragment {

    RecyclerView rvList;
    View view;
    TextView tvMsg;
    LinearLayout datLayout;
    RelativeLayout msgLayout;
    AppPreferences mAppPreferences;
    List<HashMap<String,Object>> requestList = null;
    private String rptType;
    public MyRequest() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate( R.layout.frag_access_mnt_tt,container, false);
        initialize();

        if (Utils.isNetworkAvailable(getActivity())) {
            new GetRequestList(getContext()).execute();
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
                    new GetRequestList(getContext()).execute();
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
                    new GetRequestList(getContext()).execute();
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

        msgLayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        datLayout = (LinearLayout)  view.findViewById( R.id.ll_sites);
        tvMsg = (TextView) view.findViewById( R.id.txt_no_ticket);

        if(getArguments()!=null){
            String source = getArguments().getString(Constants.TXN_SOURCE);
            if("RaisedTab".equalsIgnoreCase(source)){
                rptType = "4";
            } else{
                rptType = "3";
            }
        } else{
            rptType = "3";
        }
    }

    public class GetRequestList extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;
        Context con;

        public GetRequestList(Context con) {
            this.con = con;
            //this.nameValuePairs = nameValuePairs;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("cid",""));
                nameValuePairs.add(new BasicNameValuePair("zid",""));
                nameValuePairs.add(new BasicNameValuePair("clid",""));
                nameValuePairs.add(new BasicNameValuePair("dmn",""));
                nameValuePairs.add(new BasicNameValuePair("cngtype",""));
                nameValuePairs.add(new BasicNameValuePair("ppr",""));
                nameValuePairs.add(new BasicNameValuePair("prdtype",""));
                nameValuePairs.add(new BasicNameValuePair("asts",""));
                nameValuePairs.add(new BasicNameValuePair("rqsts",""));
                nameValuePairs.add(new BasicNameValuePair("rqname",""));
                nameValuePairs.add(new BasicNameValuePair("txnid",""));
                nameValuePairs.add(new BasicNameValuePair("sid",""));
                //String s1 = mAppPreferences.getUserGroupName();
                //String s2 = mAppPreferences.getLoginId();
                nameValuePairs.add(new BasicNameValuePair("userGrpLvl",mAppPreferences.getUserGroupName()));
                nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getLoginId()));
                nameValuePairs.add(new BasicNameValuePair("dateType",""));
                nameValuePairs.add(new BasicNameValuePair("sdate",""));
                nameValuePairs.add(new BasicNameValuePair("edate",""));
                nameValuePairs.add(new BasicNameValuePair("rptType",rptType)); //3 - Assigned, 4 - Raised
                nameValuePairs.add(new BasicNameValuePair("src","M"));
                //System.out.println("******************************** Rpt Type - "+rptType);
                //System.out.println("******************************** User Group - "+mAppPreferences.getUserGroup());
                //System.out.println("******************************** loginId - "+mAppPreferences.getLoginId());
                String response = HttpUtils.httpPostRequest(
                        AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()
                                + WebAPIs.requestList,nameValuePairs);
                //Gson gson = new Gson();
                Type listType = new TypeToken<List<HashMap<String,Object>>>() {}.getType();
                requestList = Constants.gson.fromJson(response,listType);
            } catch (Exception e) {
                e.printStackTrace();
                requestList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (requestList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                datLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (requestList!= null && requestList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                datLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new GridAdapter(getActivity(),requestList,"MyRequest"));
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
            new GetRequestList(getContext()).execute();
        } else {
            Utils.toast(getActivity(), "17");
            msgLayout.setVisibility(View.VISIBLE);
            datLayout.setVisibility(View.GONE);
            tvMsg.setBackgroundResource(R.drawable.retry);
        }
    }

}

