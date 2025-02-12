package com.isl.hsse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.isl.workflow.constant.Constants;
import com.isl.workflow.constant.WebAPIs;

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

public class PreventiveActionsFragment extends Fragment {
    RecyclerView rvList;
    View view;
    TextView tvMsg;
    LinearLayout datLayout;
    RelativeLayout msgLayout;
    AppPreferences mAppPreferences;
    List<HashMap<String,Object>> requestList = null;
    HashMap<String,String> tranData = null;
    int maxCount = 20;
    String editRight = "";
    public PreventiveActionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.frag_personal_detail,container, false);
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
                    new GetRequestList(getActivity()).execute();
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
        tranData = (HashMap<String, String>) getActivity()
                .getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS);
        rvList = (RecyclerView) view.findViewById(R.id.list_count);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(),
                LinearLayoutManager.VERTICAL, false );
        rvList.setLayoutManager( horizontalLayoutManager );
        mAppPreferences = new AppPreferences(getActivity());
        TextView bt_add_person = (TextView) view.findViewById( R.id.bt_add_person);
        bt_add_person.setVisibility(View.GONE);
        TextView bt_add_logbook = (TextView) view.findViewById( R.id.bt_add_logbook);
        bt_add_logbook.setVisibility(View.GONE);
        TextView bt_add_corective_action = (TextView) view.findViewById( R.id.bt_add_corective_action);
        bt_add_corective_action.setVisibility(View.VISIBLE);

        if(tranData.get(HsseConstant.OLD_TKT_STATUS).equalsIgnoreCase("1693")){
            bt_add_corective_action.setVisibility(View.GONE);
        }

        try {
            maxCount = Integer.parseInt(Utils.msg( getActivity(), "719" ));
        }catch (Exception e) {
            maxCount = 20;
        }


        bt_add_corective_action.setText("Add Corrective/Preventive Actions ");
        DataBaseHelper dbHelper = new DataBaseHelper( getActivity() );
        dbHelper.open();
        List<MenuDetail> subMenuList = dbHelper.getSubMenuRight( mAppPreferences.getModuleName() );
        dbHelper.close();

        for (MenuDetail menu : subMenuList) {
            switch (menu.getName()) {
                case "Corrective/Preventive Actions Tab":
                    editRight = menu.getRights().toString();
                    if (!menu.getRights().toString().contains( "A" )) {
                        bt_add_corective_action.setVisibility( View.GONE );
                    }
                    break;
            }
        }

        bt_add_corective_action.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(tranData.get(Constants.PROCESS_INSTANCE_ID)!=null
                        && tranData.get(Constants.PROCESS_INSTANCE_ID).length()==0
                        && tranData.get(Constants.OPERATION)!=null
                        && tranData.get(Constants.OPERATION).equalsIgnoreCase("A")){
                    Utils.toastMsg(getActivity(),
                            "Please first add HSSE Ticket before adding Corrective/Preventive Actions.");
                }else if(requestList!= null && requestList.size()>=maxCount){
                    Utils.toastMsg(getActivity(),
                            "Maximum " +maxCount+ " Corrective/Preventive actions  can be added.");
                }else{
                    if(getActivity().getIntent().getExtras()!=null){
                        HashMap<String,String> tranData = (HashMap<String, String>) getActivity()
                                .getIntent().getSerializableExtra( AppConstants.TRAN_DATA_MAP_ALIAS);
                        tranData.put( Constants.FORM_KEY,"AddHSSEPreventiveActions");
                        tranData.put(Constants.TXN_ID,null);
                        tranData.put(Constants.OPERATION,"A");
                        tranData.put(Constants.TXN_SOURCE,"AddRequest");
                        tranData.put(Constants.REQUEST_FLAG,"");
                        Intent i = new Intent(getActivity(), SubFormActivity.class );
                        i.putExtra( AppConstants.TRAN_DATA_MAP_ALIAS, tranData );
                        startActivity( i );
                    }
                }
            }
        });
        msgLayout = (RelativeLayout) view.findViewById( R.id.textlayout);
        datLayout = (LinearLayout)  view.findViewById( R.id.ll_sites);
        tvMsg = (TextView) view.findViewById( R.id.txt_no_ticket);
    }

    public class GetRequestList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        public GetRequestList(Context con) {
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
                String url ="";
                url= AppConstants.moduleList.get(mAppPreferences.getModuleIndex()).getBaseurl()+ WebAPIs.getHSSEsubtabList;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("taskid",tranData.get(Constants.TASK_ID)));
                nameValuePairs.add(new BasicNameValuePair("subTabName", "pca"));
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
            if (requestList == null) {
                msgLayout.setVisibility(View.VISIBLE);
                datLayout.setVisibility(View.GONE);
                tvMsg.setText("System Error, please contact iTower helpdesk.");
            } else if (requestList!= null && requestList.size() > 0) {
                msgLayout.setVisibility(View.GONE);
                datLayout.setVisibility(View.VISIBLE);
                rvList.setAdapter( new SubTabAdapter(getActivity(),requestList,tranData,"PreventiveActions",editRight));
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