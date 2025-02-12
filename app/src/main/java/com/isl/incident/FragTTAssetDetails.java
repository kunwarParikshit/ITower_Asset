package com.isl.incident;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.QRList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class FragTTAssetDetails extends Fragment {
    View view;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    QRList qrList;
    String moduleUrl = "",serUrl = "";
    ListView listView;
    TextView txt_no_data;
    public FragTTAssetDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tt_asset_list, container, false);
        txt_no_data = (TextView) view.findViewById(R.id.txt_no_data);
        mAppPreferences = new AppPreferences(getActivity());
        db = new DataBaseHelper(getActivity());
        db.open();
        moduleUrl = db.getModuleIP("Schedule");
        db.close();
        initializeControlledID();
        return view;
    }

    private void initializeControlledID() {
        listView = (ListView) view.findViewById(R.id.lv_assets);
        RelativeLayout rl_header_ticket_list = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_list);
        //listView.setDivider(null);
        Button tv_add_asset = (Button) view.findViewById(R.id.tv_add_asset);
        Utils.msgText(getActivity(), "914", tv_add_asset);

        if(getActivity().getIntent().getExtras()
                .getString("tktStatus").equalsIgnoreCase("Resolved")){
            rl_header_ticket_list.setVisibility(View.VISIBLE);
            //tv_add_asset.setVisibility(View.VISIBLE);
        }else{
            rl_header_ticket_list.setVisibility(View.GONE);
            //tv_add_asset.setVisibility(View.GONE);
        }

        /*tv_add_asset.setPaintFlags(tv_add_asset.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);*/
        tv_add_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddNewAssetTT.class);
                i.putExtra("id", getActivity().getIntent().getExtras().getString("id"));
                i.putExtra("sid", getActivity().getIntent().getExtras().getString("sid"));
                startActivity(i);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        new GetQRDetails(getActivity(),getActivity().getIntent().getExtras().getString("id")).execute();
        //new GetQRDetails(getActivity(),"TT00704103").execute();
    }

    public class GetQRDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res,ticketId = "";

        public GetQRDetails(Context con,String ticketId) {
            this.con = con;
            this.ticketId = ticketId;

        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("tktId", ticketId));
                nameValuePairs.add(new BasicNameValuePair("qrCode", ""));
                if (moduleUrl.equalsIgnoreCase("0")) {
                serUrl = mAppPreferences.getConfigIP() + WebMethods.url_get_assetDetailsByQRcode;
                } else {
                serUrl = moduleUrl + WebMethods.url_get_assetDetailsByQRcode;
                }

                res = Utils.httpPostRequest(con, serUrl, nameValuePairs);
                Gson gson = new Gson();
                qrList  = gson.fromJson( res, QRList.class );
            } catch (Exception e) {
                qrList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (qrList != null && qrList.qr_list.size() > 0) {
                txt_no_data.setVisibility(View.GONE);
                listView.setAdapter(new AdapterAssets(getActivity(),res));
                listView.setVisibility(View.VISIBLE);
                listView.setFastScrollEnabled(true);
            }else{
                listView.setVisibility(View.GONE);
                txt_no_data.setVisibility(View.VISIBLE);
                txt_no_data.setText(Utils.msg(getActivity(),"267"));
            }
            super.onPostExecute(result);
        }
    }
}