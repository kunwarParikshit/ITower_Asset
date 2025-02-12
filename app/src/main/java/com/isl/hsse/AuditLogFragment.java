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
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.modal.AuditTrail;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import infozech.itower.R;

public class AuditLogFragment extends Fragment {
    RecyclerView rvList;
    View view;
    TextView tvMsg;
    LinearLayout dataLayout;
    RelativeLayout msgLayout;
    AppPreferences mAppPreferences;
    List<AuditTrail> auditTrailList = null;
    private String txnId;
    public AuditLogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate( R.layout.frag_access_mnt_tt,container, false);
        initialize();

        HashMap<String,String> tranData = (HashMap<String, String>) getActivity().
                getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS );
        txnId = tranData.get(Constants.TASK_ID);

        if (Utils.isNetworkAvailable(getActivity())) {
            new GetAuditLogs(getContext(),txnId).execute();
        } else {
            //No Internet Connection;
            Utils.toast(getActivity(), "17");
            msgLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
            tvMsg.setBackgroundResource(R.drawable.retry);
        }

        tvMsg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    msgLayout.setVisibility(View.GONE);
                    dataLayout.setVisibility(View.VISIBLE);
                    tvMsg.setBackgroundResource(0);
                    new GetAuditLogs(getContext(),txnId).execute();
                } else {
                    Utils.toast(getActivity(), "17");
                    msgLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                    tvMsg.setBackgroundResource(R.drawable.retry);
                }
            }
        });

        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getActivity())) {
                    new GetAuditLogs(getContext(),txnId).execute();
                } else {
                    Utils.toast(getActivity(), "17");
                    msgLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                    tvMsg.setBackgroundResource(R.drawable.retry);
                }
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    private void initialize(){

        rvList = (RecyclerView) view.findViewById(R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(),
                LinearLayoutManager.VERTICAL, false );
        rvList.setLayoutManager( horizontalLayoutManager );
        mAppPreferences = new AppPreferences(getActivity());

        msgLayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        dataLayout = (LinearLayout)  view.findViewById( R.id.ll_sites);
        tvMsg = (TextView) view.findViewById( R.id.txt_no_ticket);
    }
    public class GetAuditLogs extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String txnId;
        public GetAuditLogs(Context con,String txnId) {
            this.con = con;
            this.txnId = txnId;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url ="";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(17);
                nameValuePairs.add(new BasicNameValuePair(HsseConstant.TKT_ID,this.txnId));
                url=AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.requestAuditTrailHSSE;
                Type listType = new TypeToken<List<AuditTrail>>() {}.getType();
                String response = HttpUtils.httpGetRequest(url, nameValuePairs);
                Gson gson = new Gson();
                auditTrailList = gson.fromJson(response,listType);
            } catch (Exception e) {
                e.printStackTrace();
                auditTrailList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (auditTrailList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (auditTrailList!= null && auditTrailList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                dataLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new AuditLogAdapter(getActivity(),auditTrailList,null,1));
                //rvList.setAdapter( new TabAdapter(getActivity(),auditTrailList,null,1));
            } else {
                msgLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                tvMsg.setText("No data found.");
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}