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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;
import com.isl.workflow.modal.AssigenmentHistory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import infozech.itower.R;



/**
 * Created by dhakan on 6/24/2020.
 */

public class AssignedHistoryTab extends Fragment {
    RecyclerView rvList;
    TextView tvMsg;
    LinearLayout dataLayout;
    RelativeLayout msgLayout;
    View view;
    AppPreferences mAppPreferences;
    List<AssigenmentHistory> assignHistoryList = null;
    String txnId = "";

    public AssignedHistoryTab() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.frag_access_mnt_tt,container, false);

        rvList = (RecyclerView) view.findViewById(R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
        rvList.setLayoutManager( horizontalLayoutManager );
        mAppPreferences = new AppPreferences(getActivity());

        HashMap<String,String> tranData = (HashMap<String, String>) getActivity().getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS );
        txnId = tranData.get(Constants.TXN_ID);

        msgLayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        dataLayout = (LinearLayout)  view.findViewById( R.id.ll_sites);
        tvMsg = (TextView) view.findViewById( R.id.txt_no_ticket);

        if (Utils.isNetworkAvailable(getActivity())) {
            new GetAssignHistory(getContext(),txnId).execute();
        } else {
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
                    new GetAssignHistory(getContext(),txnId).execute();
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
                    new GetAssignHistory(getContext(),txnId).execute();
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
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false );
        rvList.setLayoutManager( horizontalLayoutManager );
        mAppPreferences = new AppPreferences(getActivity());
    }

    public class GetAssignHistory extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String txnId="";

        public GetAssignHistory(Context con,String txnId) {
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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair(Constants.TXN_ID,this.txnId));
                nameValuePairs.add(new BasicNameValuePair(Constants.TXN_SOURCE,"M"));

                String url=AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.requestAssigenmentHistory;

                String response = HttpUtils.httpGetRequest(url, nameValuePairs);

                Type listType = new TypeToken<List<AssigenmentHistory>>() {}.getType();
                Gson gson = new Gson();
                assignHistoryList = gson.fromJson(response,listType);
            } catch (Exception e) {
                e.printStackTrace();
                assignHistoryList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (assignHistoryList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (assignHistoryList!= null && assignHistoryList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                dataLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new TabAdapter(getActivity(),null,assignHistoryList,txnId,2));
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

