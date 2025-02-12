package com.isl.alarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class FragmentSiteAvialabilitySummary extends Fragment {
	AppPreferences mAppPreferences;
	String response_assigned_tickets;
	ListView listView;
	TextView tv_column, tv_availability, tv_mttr, tv_site_down;
	BeanDashboardList data_list;
	int a = 1;
	int b = 1;
	int c = 1;
	int d;
	int m = 1;
	View header;
	LinearLayout ll_data;
	RelativeLayout ll_blank;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mAppPreferences = new AppPreferences(getActivity());
		View view = inflater.inflate( R.layout.fragement_site_availity_summary,container, false);
		listView = (ListView) view.findViewById( R.id.lv_dg_run);
		ll_data = (LinearLayout) view.findViewById( R.id.ll_data);
		ll_blank = (RelativeLayout) view.findViewById( R.id.rl_blank);
		tv_availability = (TextView) view.findViewById( R.id.tv_availability);
		tv_mttr = (TextView) view.findViewById( R.id.tv_mttr);
		tv_site_down = (TextView) view.findViewById( R.id.tv_site_down);

		if (Utils.isNetworkAvailable(getActivity())) {
			new AssignedTicketsTask(getActivity(), 1).execute();
		} else {
			Toast.makeText(getActivity(), "No Internet Connection",
					Toast.LENGTH_SHORT).show();
		}
		tv_column = (TextView) view.findViewById( R.id.tv_columns);
		tv_column.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater headerinflate = getActivity().getLayoutInflater();
				View coloumwindow = (View) headerinflate.inflate( R.layout.column_st_ava, null);
				final PopupWindow popupMessage = new PopupWindow(coloumwindow,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,false);
				popupMessage.setOutsideTouchable(true);
				popupMessage.setContentView(coloumwindow);
				popupMessage.setBackgroundDrawable(new BitmapDrawable());
				popupMessage.setOutsideTouchable(true);
				popupMessage.setFocusable(true);
				popupMessage.showAsDropDown(tv_column, 0, 0);
				final CheckBox cb_ava_col = (CheckBox) coloumwindow.findViewById( R.id.cb_ava_col);
				final CheckBox cb_mttr_col = (CheckBox) coloumwindow.findViewById( R.id.cb_mttr_col);
				final CheckBox cb_stdw_col = (CheckBox) coloumwindow.findViewById( R.id.cb_stdw_col);
				if (a == 1) {
					cb_ava_col.setChecked(true);
				}
				if (b == 1) {
					cb_mttr_col.setChecked(true);
				}
				if (c == 1) {
					cb_stdw_col.setChecked(true);
				}
				cb_ava_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						d = a + b + c;
						if (d == 1) {
							a = 1;
							cb_ava_col.setChecked(true);
							tv_availability.setVisibility(View.VISIBLE);
						} else {
							if (a == 1) {
								a = 0;
								cb_ava_col.setChecked(false);
								tv_availability.setVisibility(View.GONE);
							} else if (a == 0) {
								a = 1;
								cb_ava_col.setChecked(true);
								tv_availability.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c);
					}
				});
				cb_mttr_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						d = a + b + c;
						if (d == 1) {
							b = 1;
							cb_mttr_col.setChecked(true);
							tv_mttr.setVisibility(View.VISIBLE);
						} else {
							if (b == 1) {
								b = 0;
								cb_mttr_col.setChecked(false);
								tv_mttr.setVisibility(View.GONE);
							} else if (b == 0) {
								b = 1;
								cb_mttr_col.setChecked(true);
								tv_mttr.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c);
					}
				});
				cb_stdw_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						d = a + b + c;
						if (d == 1) {
							c = 1;
							cb_stdw_col.setChecked(true);
							tv_site_down.setVisibility(View.VISIBLE);
						} else {
							if (c == 1) {
								cb_stdw_col.setChecked(false);
								c = 0;
								tv_site_down.setVisibility(View.GONE);
							} else if (c == 0) {
								c = 1;
								cb_stdw_col.setChecked(true);
								tv_site_down.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c);
					}
				});
			}
		});
		return view;
	}

	public void setAdapter(ListView list, BeanDashboardList datalist, int a,
                           int b, int c) {
		AdapterSiteAvialability adapter = new AdapterSiteAvialability(getActivity(), datalist, a, b, c);
		list.setDivider(null);
		list.setAdapter(adapter);
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
					url=mAppPreferences.getConfigIP()+ WebMethods.url_Get_site_Ava_summary;
				}else{
					url=moduleUrl+ WebMethods.url_Get_site_Ava_summary;
				}

				response_assigned_tickets = Utils.httpPostRequest(con,url,
						nameValuePairs);
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

				} else if (data_list.getSiteAvailabilitySummary_list()!=null && data_list.getSiteAvailabilitySummary_list().size() > 0) {
					ll_data.setVisibility(View.VISIBLE);
					ll_blank.setVisibility(View.GONE);
					setAdapter(listView, data_list, a, b, c);
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
