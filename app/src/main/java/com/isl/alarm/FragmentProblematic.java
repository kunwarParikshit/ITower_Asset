package com.isl.alarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanDashboardList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class FragmentProblematic extends Fragment {
	AppPreferences mAppPreferences;
	String response_assigned_tickets;
	ListView listView;
	BeanDashboardList data_list;
	LinearLayout ll_data;
	RelativeLayout ll_blank;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mAppPreferences = new AppPreferences(getActivity());
		View view = inflater.inflate( R.layout.fragement_problematic_site,container, false);
		listView = (ListView) view.findViewById( R.id.lv_dg_run);
		ll_data = (LinearLayout) view.findViewById( R.id.ll_data);
		ll_blank = (RelativeLayout) view.findViewById( R.id.rl_blank);
		if (Utils.isNetworkAvailable(getActivity())) {
			new AssignedTicketsTask(getActivity(), 1).execute();
		} else {
			Toast.makeText(getActivity(), "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}
		return view;
	}

	public void setAdapter(ListView list, BeanDashboardList data) {
		AdapterProblematic adapter = new AdapterProblematic(getActivity(), data);
		list.setDivider(null);
		list.setAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
		int mode;

		public AssignedTicketsTask(Context con, int mode) {
			this.con = con;
			this.mode = mode;
		}

		@Override
		protected void onPreExecute() {
			if (mode == 1) {
				pd = ProgressDialog.show(con, null, "Loading...");
			}
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			db = new DataBaseHelper(con);
			db.open();
			moduleUrl = db.getModuleIP("Alarm");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						7);
				nameValuePairs.add(new BasicNameValuePair("CounrtyID",
						mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("HubID",
						mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("RegionID",
						mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("CircleID",
						mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("ZoneID",
						mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("ClusterID",
						mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("PIOMEID",
						mAppPreferences.getPIOMEID()));
				if(moduleUrl.equalsIgnoreCase("0")){
					url=mAppPreferences.getConfigIP()+ WebMethods.url_Get_ProblematicSite;
				}else{
					url=moduleUrl+ WebMethods.url_Get_ProblematicSite;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_Get_ProblematicSite
						:  "http://" + moduleUrl + "/Service.asmx/Problematic_Site";*/
				response_assigned_tickets = Utils.httpPostRequest(con,url,nameValuePairs);
				Gson gson = new Gson();
				data_list = gson.fromJson(response_assigned_tickets,
						BeanDashboardList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mode == 1) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
			}
			if (!getActivity().isFinishing()) {
				if ((data_list == null)) {
					//Toast.makeText(getActivity(), "Server Not Available",
					//		Toast.LENGTH_LONG).show();
					Toast.makeText(getActivity(), "No Data Found",
							Toast.LENGTH_LONG).show();
					ll_data.setVisibility(View.GONE);
					ll_blank.setVisibility(View.VISIBLE);

				} else if (data_list.getProblematic_Site_list()!=null && data_list.getProblematic_Site_list().size() > 0) {
					ll_data.setVisibility(View.VISIBLE);
					ll_blank.setVisibility(View.GONE);
					setAdapter(listView, data_list);
				} else {
					Toast.makeText(getActivity(), "No Data Found",
							Toast.LENGTH_LONG).show();
					ll_data.setVisibility(View.GONE);
					ll_blank.setVisibility(View.VISIBLE);

				}
			}
			super.onPostExecute(result);
		}
	}
}
