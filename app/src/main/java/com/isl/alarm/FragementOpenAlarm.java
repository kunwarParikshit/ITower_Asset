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

public class FragementOpenAlarm extends Fragment {
	ListView listView;
	TextView tv_column, tv_site_down, tv_critical, tv_major, tv_minor, tv_info;
	BeanDashboardList data_list;
	String response_assigned_tickets;
	LinearLayout ll_data;
	RelativeLayout ll_blank;
	int a = 1, b = 1, c = 1, d = 1, e = 1, f, m = 1;
	AppPreferences mAppPreferences;
	String moduleUrl = "";
	String url = "";
	DataBaseHelper db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mAppPreferences = new AppPreferences(getActivity());
		View view = inflater.inflate( R.layout.fragement_open_alarm, container,false);
		listView = (ListView) view.findViewById( R.id.lv_dg_run);
		ll_data = (LinearLayout) view.findViewById( R.id.ll_data);
		ll_blank = (RelativeLayout) view.findViewById( R.id.rl_blank);
		tv_site_down = (TextView) view.findViewById( R.id.tv_site_down);
		tv_critical = (TextView) view.findViewById( R.id.tv_critical);
		tv_major = (TextView) view.findViewById( R.id.tv_major);
		tv_minor = (TextView) view.findViewById( R.id.tv_minor);
		tv_info = (TextView) view.findViewById( R.id.tv_info);
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
				LayoutInflater headerinflate = getActivity()
						.getLayoutInflater();
				View coloumwindow = (View) headerinflate.inflate(
						R.layout.column_open_alarm, null);
				final PopupWindow popupMessage = new PopupWindow(coloumwindow,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				popupMessage.setOutsideTouchable(true);
				popupMessage.setContentView(coloumwindow);
				popupMessage.setBackgroundDrawable(new BitmapDrawable());
				popupMessage.setOutsideTouchable(true);
				popupMessage.setFocusable(true);
				popupMessage.showAsDropDown(tv_column, 0, 0);
				final CheckBox cb_sitedw_col = (CheckBox) coloumwindow
						.findViewById( R.id.cb_sitedw_col);
				final CheckBox cb_critical_col = (CheckBox) coloumwindow
						.findViewById( R.id.cb_critical_col);
				final CheckBox cb_major_col = (CheckBox) coloumwindow
						.findViewById( R.id.cb_major_col);
				final CheckBox cb_minor_col = (CheckBox) coloumwindow
						.findViewById( R.id.cb_minor_col);
				final CheckBox cb_info_col = (CheckBox) coloumwindow
						.findViewById( R.id.cb_info_col);
				if (a == 1) {
					cb_sitedw_col.setChecked(true);
				}
				if (b == 1) {
					cb_critical_col.setChecked(true);
				}
				if (c == 1) {
					cb_major_col.setChecked(true);
				}
				if (d == 1) {
					cb_minor_col.setChecked(true);
				}
				if (e == 1) {
					cb_info_col.setChecked(true);
				}
				cb_sitedw_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						f = a + b + c + d + e;
						if (f == 1) {
							a = 1;
							cb_sitedw_col.setChecked(true);
							tv_site_down.setVisibility(View.VISIBLE);
						} else {
							if (a == 1) {
								a = 0;
								cb_sitedw_col.setChecked(false);
								tv_site_down.setVisibility(View.GONE);
							} else if (a == 0) {
								a = 1;
								cb_sitedw_col.setChecked(true);
								tv_site_down.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c, d, e);
					}
				});
				cb_critical_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						f = a + b + c + d + e;
						if (f == 1) {
							b = 1;
							cb_critical_col.setChecked(true);
							tv_critical.setVisibility(View.VISIBLE);
						} else {
							if (b == 1) {
								b = 0;
								cb_critical_col.setChecked(false);
								tv_critical.setVisibility(View.GONE);
							} else if (b == 0) {
								b = 1;
								cb_critical_col.setChecked(true);
								tv_critical.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c, d, e);
					}
				});

				cb_major_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						f = a + b + c + d + e;
						if (f == 1) {
							c = 1;
							cb_major_col.setChecked(true);
							tv_major.setVisibility(View.VISIBLE);
						} else {
							if (c == 1) {
								c = 0;
								cb_major_col.setChecked(false);
								tv_major.setVisibility(View.GONE);
							} else if (c == 0) {
								c = 1;
								cb_major_col.setChecked(true);
								tv_major.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c, d, e);
					}
				});

				cb_minor_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						f = a + b + c + d + e;
						if (f == 1) {
							d = 1;
							cb_minor_col.setChecked(true);
							tv_minor.setVisibility(View.VISIBLE);
						} else {
							if (d == 1) {
								d = 0;
								cb_minor_col.setChecked(false);
								tv_minor.setVisibility(View.GONE);
							} else if (d == 0) {
								d = 1;
								cb_minor_col.setChecked(true);
								tv_minor.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c, d, e);
					}
				});

				cb_info_col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						f = a + b + c + d + e;
						if (f == 1) {
							e = 1;
							cb_info_col.setChecked(true);
							tv_info.setVisibility(View.VISIBLE);
						} else {
							if (e == 1) {
								e = 0;
								cb_info_col.setChecked(false);
								tv_info.setVisibility(View.GONE);
							} else if (e == 0) {
								e = 1;
								cb_info_col.setChecked(true);
								tv_info.setVisibility(View.VISIBLE);
							}
						}
						popupMessage.dismiss();
						setAdapter(listView, data_list, a, b, c, d, e);
					}
				});
			}
		});
		return view;
	}

	public void setAdapter(ListView list, BeanDashboardList data, int a, int b,
                           int c, int d, int e) {
		AdapterOpenAlarm adapter = new AdapterOpenAlarm(getActivity(), data, a,
				b, c, d, e);
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
					url=mAppPreferences.getConfigIP()+ WebMethods.url_Get_OpenAlarmSummary;
				}else{
					url=moduleUrl+ WebMethods.url_Get_OpenAlarmSummary;
				}
				/*url = (moduleUrl.isEmpty()) ? Constants.url_Get_OpenAlarmSummary
						:  "http://" + moduleUrl + "/Service.asmx/OpenAlarmSummary";*/
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

				} else if (data_list.getOpenAlarmSummary_list()!=null && data_list.getOpenAlarmSummary_list().size() > 0) {
					ll_data.setVisibility(View.VISIBLE);
					ll_blank.setVisibility(View.GONE);
					setAdapter(listView, data_list, a, b, c, d, e);
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
