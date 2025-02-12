package com.isl.incident;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.AlarmDescList;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.modal.BeanRemarks;
import com.isl.modal.BeansTicketDetails;
import com.isl.modal.IncidentMetaList;
import com.isl.modal.ResponseRemarks;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.preventive.PMChecklistApproval;
import com.isl.preventive.ViewPMCheckList;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 1/20/2020.
 * <p>
 * Modified By : Avishek Singh
 * Modified On : 02-mar-2021
 * Version     : 1.2
 * Purpose     : iMaintain cr# 821

 * Modified By : Dhakan Lal
 * Modified On : 02-Feb-2024
 * Version     : 1.3
 * Purpose     : DM-1178

 * Modified By : Dhakan Lal

 * Modified On : 21-Mar-2024
 * Version     : 1.4

 * Purpose     : DM-1158
 *
 */

public class FragTicketDetails extends Fragment {
    View view;
    TextView et_site_id, et_ticket_id, et_ticket_logged_time,
            et_operator_site_id, et_alarm_details,
            et_ticket_logged_by, et_problem_description, et_eta, et_eta_time,
            et_etr, et_etr_time, et_rca_category, et_rca,
            txt_value_equipment, txt_value_alarm_type,
            txt_value_alarm_description, txt_value_ticket_type,
            txt_value_ticket_assignment_user_details, txt_value_status,
            et_problem_start_date_time, et_problem_end_date_time,
            et_ref_tkt_id, et_contact, txt_operator, txt_dependent_site,
            et_site_status, et_bttry_dschrg_date, et_bttry_backup,
            txt_rca_sub_category, txt_operator_exempt, et_trvlDistnc, et_noOfTech, et_wrkgNights,
            tv_servicesAffected_value, tv_serviceImpactStart, tv_serviceImpactStartTime, sp_serviceImapact, sp_priority, tv_navigate;

    TextView tv_brand_logo, tv_site_id, tv_site_status, tv_ticket_id,
            tv_ref_tkt_id, tv_problem_start_date_time,
            tv_problem_end_date_time, tv_ticket_logged_date,
            tv_bttry_dschrg_date, tv_bttry_backup, tv_operator_site_id,
            tv_equipment, tv_severity, tv_alarm_description, tv_alarm_details,
            tv_tkt_logged_by, tv_tkt_type, tv_problem_description,
            tv_assign_to, tv_contact, tv_tkt_status, tv_eta, tv_eta_time,
            tv_etr, tv_etr_time, tv_rca_category, tv_rca_sub_category, tv_rca,
            tv_operator, tv_operator_exempt, tv_eff_site, tv_remarks,
            txt_no_remarks, tv_next,
            tv_dg_reading, txt_dg_reading, tv_grid_reading, txt_grid_reading, tv_hub_siteId, txt_hub_siteId,
            tv_fuel_level, txt_fuel_level, tv_action_taken, tv_ticket_treatment, txt_action_taken, txt_ticket_treatment,
            tv_first_level, txt_first_level, tv_second_level, txt_second_level,
            tv_reject_category, txt_reject_category, tv_reject_remarks, txt_reject_remarks, tv_chk_link, tv_ref_tt_link;

    RecyclerView list_img;//list_remarks;
    List<ViewImage64> lhmImages = new ArrayList<ViewImage64>();
    BeansTicketDetails response_ticket_details = null;
    ResponseRemarks response_remarks = null;
    //List<String> img_list, img_name, img_time_stamp,img_latitute,img_longitude;
    ScrollView scrollview;
    String s1, s2, s3, s4, s5, s6, ticket_id, sName = "", my_search, assigned_tab, raised_tab, resolved_tab, serviceImpacted, ServiceAffected, serviceImactStart, serviceImactStartTime;
    ;
    LinearLayout RL;
    int minImgCounter = 0;
    AppPreferences mAppPreferences;
    DataBaseHelper dbHelper;
    IncidentMetaList resposnse_Incident_meta = null;
    AlarmDescList resposnse_alarm = null;
    DisplayImageOptions op;
    public ImageLoader loader = ImageLoader.getInstance();
    LinearLayout ll_site_id, ll_site_vsblty, ll_tkt_id, ll_ref_tkt_id,
            ll_prb_strt_date, ll_prb_end_date, ll_tkt_log_date,
            ll_bttry_dschrg, ll_bttry_backup, ll_operator_site_id,
            ll_equipment, ll_severity, ll_alrm_desc, ll_alarm_detail,
            ll_tkt_log_by, ll_tkt_type, ll_prb_desc, ll_assigned_to,
            ll_contacts, ll_tkt_status, ll_eta, ll_eta_time, ll_etr,
            ll_etr_time, ll_rca_category, ll_rca_sub_category, ll_rca,
            ll_operator, ll_operator_exempt, ll_effected_sites, ll_remarks,
            ll_trvlDistnc, ll_noOfTech, ll_wrkgNights,
            ll_dg_reading, ll_grid_reading, ll_fuel_level, ll_hub_siteId, ll_action_taken, ll_ticket_treatment,
            ll_first_level, ll_second_level, ll_reject_category, ll_reject_remarks, ll_chk_link, ll_remarks_list,
            ll_ref_tt_link;
    String moduleUrl = "";
    String url = "";
    boolean enableRca = false;
    boolean disableSupperUserField = false;

    public FragTicketDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        op = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.no_media_default)
                .showImageForEmptyUri(R.drawable.no_media_default)
                .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                .build();
        view = inflater.inflate(R.layout.ticket_details, container, false);
        sp_priority = view.findViewById(R.id.sp_priority);
        tv_navigate = view.findViewById(R.id.tv_navigate);
        mAppPreferences = new AppPreferences(getActivity());
        dbHelper = new DataBaseHelper(getActivity());
        dbHelper.open();
        getControllerIds();
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            moduleUrl = dbHelper.getModuleIP("HealthSafty");
            setMsgHS("HealthSafty");
        } else {
            moduleUrl = dbHelper.getModuleIP("Incident");
            setMsgTT("Incident");
        }
        ticket_id = getActivity().getIntent().getExtras().getString("id");
        enableRca = getActivity().getIntent().getExtras().containsKey("enableRca");


        if (Utils.isNetworkAvailable(getActivity())) {
            if (mAppPreferences.getTicketFrmNtBr().equalsIgnoreCase("1")) {
            } else {
                dbHelper.deleteNotification(mAppPreferences.getTicketFrmNtBr(), mAppPreferences.getUserId());
            }

            new TikcetDetailsTask(getActivity(), ticket_id).execute();
            if (!metaDataType().equalsIgnoreCase("")) {
                new IncidentMetaDataTask(getActivity()).execute();
            }
            if (!alarmDescDataType().equalsIgnoreCase("")) {
                new AlarmDescTask(getActivity()).execute();
            }
            showHideField();

        } else {
            Utils.toast(getActivity(), "17");
            getActivity().finish();
        }


        tv_chk_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), ViewPMCheckList.class);
                i.putExtra("S", "D");
                i.putExtra("scheduledDate", "01-JAN-2020");
                i.putExtra("siteId", response_ticket_details.getSITE_ID());
                String Sname = null;
                i.putExtra("siteName", Sname);
                i.putExtra("activityTypeId", response_ticket_details.getALARM_DESC_ID());
                i.putExtra("paramName", "");
                i.putExtra("Status", "D");
                i.putExtra("dgType", "0");
                i.putExtra("txn", ticket_id);
                i.putExtra("etsSid", response_ticket_details.getEtsid());
                i.putExtra("imgUploadFlag", "2");
                i.putExtra("rvDate", "");
                i.putExtra("rejRmks", "");
                i.putExtra("rCat", "");
                //startActivity(i);
                if (Utils.isNetworkAvailable(getActivity())) {
                    new GetImage(getActivity(), i, ticket_id, "01-JAN-2020", response_ticket_details.getALARM_DESC_ID(),
                            response_ticket_details.getSITE_ID(), response_ticket_details.getEtsid(),
                            "0", "2").execute();
                } else {
                    //No Internet Connection;
                    Utils.toast(getActivity(), "17");
                }


            }
        });

        tv_ref_tt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), AddTicket.class);
                mAppPreferences.saveRefMode(2);
                i.putExtra("equipment", response_ticket_details.getEQUIPMENT());
                i.putExtra("alarm_type", response_ticket_details.getALARM_TYPE());
                i.putExtra("alarm_description", response_ticket_details.getALARM_SHORT_DISCRIPTION());
                i.putExtra("alarm_detail", response_ticket_details.getALARM_DETAIL());
                i.putExtra("problem_description", response_ticket_details.getPROBLEM_DESC());
                i.putExtra("assingTo", response_ticket_details.getGROUP_NAME());
                i.putExtra("site_id", response_ticket_details.getSITE_ID());
                i.putExtra("ticket_type", response_ticket_details.getTICKET_TYPE());
                i.putExtra("ref_tkt_id", ticket_id);
                i.putExtra("date", response_ticket_details.getPROBLEM_START_DATE());
                i.putExtra("time", response_ticket_details.getPROBLEM_START_TIME());
                i.putExtra("bat_disc_date", response_ticket_details.getBATTERY_DIS_START_DATE());
                i.putExtra("bat_disc_time", response_ticket_details.getBATTERY_DIS_START_TIME());
                i.putExtra("EffectiveSites", response_ticket_details.getEFFECTED_SITES());
                i.putExtra("Operator", response_ticket_details.getOPERATOR_LIST());
                i.putExtra("OperatorExempt", response_ticket_details.getoExemLst());
                i.putExtra("site_status", response_ticket_details.getSITE_STATUS());
                i.putExtra("asgnToUid", response_ticket_details.getAsgnToUid());
                i.putExtra("hub_id", response_ticket_details.getHubSiteId());
                startActivity(i);
                getActivity().finish();
            }
        });

        tv_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = Double.parseDouble(response_ticket_details.getLATITUDE()); // Your latitude value
                double longitude = Double.parseDouble(response_ticket_details.getLONGITUDE()); // Your longitude value

                String uri = "google.navigation:q=" + latitude + "," + longitude;
                Uri gmmIntentUri = Uri.parse(uri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // If Google Maps app is not available, open in web browser
                if (mapIntent.resolveActivity(getContext().getPackageManager()) == null) {
                    Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude);
                    mapIntent = new Intent(Intent.ACTION_VIEW, webUri);
                }

                startActivity(mapIntent);
            }
        });

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UpdateTicket.class);
                i.putExtra("str_siteId", response_ticket_details.getSITE_ID());
                i.putExtra("equipment", response_ticket_details.getEQUIPMENT());
                i.putExtra("str_severity", response_ticket_details.getALARM_TYPE());
                i.putExtra("alarm_description", response_ticket_details.getALARM_SHORT_DISCRIPTION());
                i.putExtra("ticket_type", response_ticket_details.getTICKET_TYPE());
                i.putExtra("problem_description", response_ticket_details.getPROBLEM_DESC());
                i.putExtra("alarm_detail", response_ticket_details.getALARM_DETAIL());
                i.putExtra("id", ticket_id);
                i.putExtra("status", response_ticket_details.getTICKET_STATUS());
                i.putExtra("eta", response_ticket_details.getETA());
                i.putExtra("etr", response_ticket_details.getETR());
                i.putExtra("rca", response_ticket_details.getRCA());
                i.putExtra("rca_category", response_ticket_details.getRCA_CATEGORY());
                i.putExtra("rca_category_name", response_ticket_details.getRCA_CATEGORY_NAME());
                i.putExtra("assingTo", response_ticket_details.getGROUP_NAME());
                i.putExtra("PROBLEM_START_DATE", response_ticket_details.getPROBLEM_START_DATE());
                i.putExtra("PROBLEM_START_TIME", response_ticket_details.getPROBLEM_START_TIME());
                i.putExtra("PROBLEM_END_DATE", response_ticket_details.getPROBLEM_END_DATE());
                i.putExtra("PROBLEM_END_TIME", response_ticket_details.getPROBLEM_END_TIME());
                i.putExtra("TICKET_RCA", response_ticket_details.getTICKET_RCA());
                i.putExtra("EffectiveSites", response_ticket_details.getEFFECTED_SITES());
                i.putExtra("Operator", response_ticket_details.getOPERATOR_LIST());
                i.putExtra("OperatorExempt", response_ticket_details.getoExemLst());
                i.putExtra("asgnToUid", response_ticket_details.getAsgnToUid());
                i.putExtra("Site_Id", response_ticket_details.getSITE_ID());
                i.putExtra("tkt_log_time", response_ticket_details.getTICKET_LOG_TIME());
                i.putExtra("equipment_id", response_ticket_details.getEQUIPMENT_ID());
                i.putExtra("severity_Id", response_ticket_details.getALARM_TYPE_ID());
                i.putExtra("alarm_descId", response_ticket_details.getALARM_DESC_ID());
                i.putExtra("ticket_typeId", response_ticket_details.getTICKET_TYPE_ID());
                i.putExtra("alarmTxnId", response_ticket_details.getALARM_TXN_ID());
                i.putExtra("site_status", et_site_status.getText().toString().trim());
                i.putExtra("rca_sub_category", response_ticket_details.getRCA_SUB_CAT());
                i.putExtra("rca_sub_category_name", response_ticket_details.getRCA_SUB_CAT_NAME());
                i.putExtra("batt_disc_date", response_ticket_details.getBATTERY_DIS_START_DATE());
                i.putExtra("batt_disc_time", response_ticket_details.getBATTERY_DIS_START_TIME());
                i.putExtra("minImgCounter", minImgCounter);
                i.putExtra("enableField", disableSupperUserField);
                i.putExtra("etsid", response_ticket_details.getEtsid());
                i.putExtra("offLineFlag", response_ticket_details.getOffLineFlag());
                i.putExtra("trvlDistnc", response_ticket_details.getTrvlDistnc());
                i.putExtra("noOfTech", response_ticket_details.getNoOfTech());
                i.putExtra("wrkgNights", response_ticket_details.getWrkgNights());
                i.putExtra("enableRca", enableRca);
                i.putExtra("dgReading", response_ticket_details.getDgReading());
                i.putExtra("gridReading", response_ticket_details.getGridReading());
                i.putExtra("fuelLevel", response_ticket_details.getFuelLevel());
                i.putExtra("actionTaken", response_ticket_details.getActionTaken());
                i.putExtra("tktTretmnt", response_ticket_details.getTktTretmnt());  //tktTretmnt
                i.putExtra("tt_flag", response_ticket_details.getTt_flag());  //
                i.putExtra("tt_rev_flag", response_ticket_details.getTt_rev_flag());  //
                i.putExtra("grpId", response_ticket_details.getGrpId());
                i.putExtra("userCat", response_ticket_details.getUserCat());
                i.putExtra("userSubcat", response_ticket_details.getUserSubcat());
                i.putExtra("hubSiteId", response_ticket_details.getHubSiteId());  //hub site id    //1.2
                i.putExtra("resMethodID", response_ticket_details.getRESULATION_METHOD());  //hub site id    //1.2
                i.putExtra("resMethod", response_ticket_details.getRESULATION_METHOD_NAME());
                i.putExtra("fouArea", response_ticket_details.getFAULT_AREA_NAME());
                i.putExtra("fouAreaDetails", response_ticket_details.getFAULT_AREA_DETAIL_NAME());
                i.putExtra("fouAreaID", response_ticket_details.getFAULT_AREA());
                i.putExtra("fouAreaDetailsID", response_ticket_details.getFAULT_AREA_DETAIL());
                i.putExtra("staResion", response_ticket_details.getSTATUS_REASON_NAME());
                i.putExtra("prNo", "" + response_ticket_details.getPR_NO());
                i.putExtra("serviceAffected", "" + response_ticket_details.getSERVICE_AFFECTED());
                i.putExtra("serviceImpactStart", "" + response_ticket_details.getSERVICE_IMPACT_START());
                i.putExtra("serviceImpactStartTime", "" + response_ticket_details.getSERVICE_IMPACT_START_DATE());
                i.putExtra("serviceImpacted", "" + response_ticket_details.getSERVICE_IMPACTED());
                i.putExtra("priorityId", response_ticket_details.getPRIORITY_ID());
                i.putExtra("priorityName", response_ticket_details.getPRIORITY_NAME());
                i.putExtra("approvelStatus", "" + response_ticket_details.getAPPROVAL_STATUS());
                //1.3
                i.putExtra("isUserSatisfied", "" + response_ticket_details.getIsUserSatisfied());
                i.putExtra("loggedBy", "" + response_ticket_details.getLoggedBy());

                //dhakan
                i.putExtra("assetId", "" + response_ticket_details.getAssetId()); //1.4

                if (response_ticket_details.getFirstLevel() != null) {
                    i.putExtra("firstLevel", response_ticket_details.getFirstLevel());
                } else {
                    i.putExtra("firstLevel", "");
                }

                if (response_ticket_details.getSecondLevel() != null) {
                    i.putExtra("secondLevel", response_ticket_details.getSecondLevel());
                } else {
                    i.putExtra("secondLevel", "");
                }
                //110
                if (response_ticket_details.getDelay_rsnid() != null) {
                    i.putExtra("delay_reasonid", response_ticket_details.getDelay_rsnid());
                } else {
                    i.putExtra("delay_reasonid", "");
                }
                if (response_ticket_details.getDelay_rsn() != null) {
                    i.putExtra("delay_reason", response_ticket_details.getDelay_rsn());
                } else {
                    i.putExtra("delay_reason", "");
                }
                if (response_ticket_details.getToc_fo_user() != null) {
                    i.putExtra("toc_fo_user", response_ticket_details.getToc_fo_user());
                } else {
                    i.putExtra("toc_fo_user", "");
                }
                if (response_ticket_details.getALARM_IN_TIM() != null) {
                    i.putExtra("alarm_in_time", response_ticket_details.getALARM_IN_TIM());
                } else {
                    i.putExtra("alarm_in_time", "");
                }
                //110
                i.putExtra("rejCat", response_ticket_details.getRejCat());
                i.putExtra("rejRmk", response_ticket_details.getRejRmk());
                startActivity(i);
                getActivity().finish();
            }
        });
        return view;
    }



    public class TikcetDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String ticket_id;

        public TikcetDetailsTask(Context con, String ticket_id) {
            this.con = con;
            this.ticket_id = ticket_id;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("ticketID", ticket_id));
                nameValuePairs.add(new BasicNameValuePair("userId", mAppPreferences.getUserId()));
                //nameValuePairs.add(new BasicNameValuePair("ticketID", "TT00704103"));
                //nameValuePairs.add(new BasicNameValuePair("userId", mAppPreferences.getUserId()));

                //get ticket details
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getTicketDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getTicketDetails;
                }
                String res_1 = Utils.httpPostRequest(con, url, nameValuePairs);
                res_1 = res_1.replace("[", "").replace("]", "");
                response_ticket_details = new Gson().fromJson(res_1, BeansTicketDetails.class);

                //get ticket audit remarks
                if (!response_ticket_details.getSITE_ID().equals("")) {
                    if (moduleUrl.equalsIgnoreCase("0")) {
                        url = mAppPreferences.getConfigIP() + WebMethods.url_getTicketRemarks;
                    } else {
                        url = moduleUrl + WebMethods.url_getTicketRemarks;
                        //url=moduleUrl+ WebMethods.url_GetTicketHistory;
                    }
                    String res_2 = Utils.httpPostRequest(con, url, nameValuePairs);
                    response_remarks = new Gson().fromJson(res_2, ResponseRemarks.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response_ticket_details == null) {
                Utils.toast(getActivity(), "124");
                getActivity().finish();
            } else {
                SetRemarks();
                SetData();
                setImage();
                TTEnergyDescription();
                TTAccessDescription();
                if (dbHelper.getInciParamId("62", response_ticket_details.getTICKET_STATUS().trim(), mAppPreferences.getTTModuleSelection())
                        .equalsIgnoreCase("8")) {

                    if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")) {
                        ll_ref_tt_link.setVisibility(View.VISIBLE);
                    }

                    String supperUser[] = null;
                    if (response_ticket_details.getEnableCloseTKT() != null) {
                        if (response_ticket_details.getEnableCloseTKT().contains(",")) {
                            supperUser = response_ticket_details.getEnableCloseTKT().split(",");
                        } else {
                            supperUser = new String[1];
                            supperUser[0] = response_ticket_details.getEnableCloseTKT();
                        }
                    }
                    if (supperUser != null) {
                        for (int i = 0; i < supperUser.length; i++) {
                            if (supperUser[i].equalsIgnoreCase(mAppPreferences.getUserId())) {
                                RL.setVisibility(View.VISIBLE);
                                disableSupperUserField = true;
                                break;
                            } else {
                                RL.setVisibility(View.GONE);
                            }
                        }
                    }
                    //110
                    if (response_ticket_details.getToc_fo_user().equalsIgnoreCase("Yes") && response_ticket_details.getTICKET_TYPE_ID().equalsIgnoreCase("23") && response_ticket_details.getTICKET_STATUS().equalsIgnoreCase("Closed")) {
                        RL.setVisibility(View.VISIBLE);
                    }
                    //110
                    else {
                        RL.setVisibility(View.GONE);
                    }
                } else {
                    if (my_search.contains("M") || assigned_tab.contains("M") ||
                            raised_tab.contains("M") || resolved_tab.contains("M")) {
                        RL.setVisibility(View.VISIBLE);
                    } else {
                        RL.setVisibility(View.GONE);
                    }
                }
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    void SetRemarks() {
        if (response_remarks != null && response_remarks.getRemarks() != null && response_remarks.getRemarks().size() > 0) {
            auditTrail(response_remarks.getRemarks());
            txt_no_remarks.setVisibility(View.GONE);
            ll_remarks_list.setVisibility(View.VISIBLE);
            //list_remarks.setFastScrollEnabled(true);
            //list_remarks.setAdapter(new AdapterRemarks(getActivity(),response_remarks.getRemarks()));
            //list_remarks.setExpanded(true);
        } else {
            ll_remarks_list.setVisibility(View.GONE);
            txt_no_remarks.setVisibility(View.VISIBLE);
        }

        tv_remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                remaksDetails();
            }
        });

    }

    public void auditTrail(List<BeanRemarks> remarks_list) {

        for (int position = 0; position < remarks_list.size(); position++) {
            TextView tv_update_time = new TextView(getActivity());
            TextView tv_update_by = new TextView(getActivity());
            TextView tv_update_field = new TextView(getActivity());
            TextView tv_update_doc = new TextView(getActivity());
            TextView tv_divider = new TextView(getActivity());

            Utils.defaulttextViewProperty(getActivity(), tv_update_time);
            Utils.defaulttextViewProperty(getActivity(), tv_update_by);
            Utils.defaulttextViewProperty(getActivity(), tv_update_field);
            Utils.defaulttextViewProperty(getActivity(), tv_update_doc);
            Utils.textViewDivider(getActivity(), tv_divider);


            if (remarks_list.get(position).getDate() != null) {
                tv_update_time.setText(Html.fromHtml("<b>Updated On : </b> " + remarks_list.get(position).getDate()));
            } else {
                tv_update_time.setText(Html.fromHtml("<b>Updated On : </b> "));
            }

            if (remarks_list.get(position).getUser() != null) {
                tv_update_by.setText(Html.fromHtml("<b>Updated By : </b> " + remarks_list.get(position).getUser()));
            } else {
                tv_update_by.setText(Html.fromHtml("<b>Updated By : </b> "));
            }

            if (remarks_list.get(position).getRemarks().contains("Refrence ticket for resolution-TT")) {
                final String[] ref_link = remarks_list.get(position).getRemarks().split("-");
                if (ref_link.length >= 2) {
                    String s = ref_link[0] + " - " + "<a href=\"\">" + ref_link[1] + "</a>";
                    tv_update_field.setText(Html.fromHtml(s));

                    tv_update_field.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent i = new Intent(getActivity(), TicketDetailsTabs.class);
                            mAppPreferences.SetBackModeNotifi123(2);
                            i.putExtra("id", ref_link[1]);
                            i.putExtra("enableRca", "1");
                            startActivity(i);

                        }
                    });

                }
            } else {
                tv_update_field.setText(Html.fromHtml("<b>Updated : </b> " + remarks_list.get(position).getRemarks()
                        .replaceAll("<br>", "\n")));
            }
            if (remarks_list.get(position).getDocumentPath() != null &&
                    !remarks_list.get(position).getDocumentPath().equalsIgnoreCase("null") &&
                    remarks_list.get(position).getDocumentPath().length() != 0) {
                String s[] = remarks_list.get(position).getDocumentPath().split("\\\\");
                tv_update_doc.setText(Html.fromHtml("<b>Uploaded Document : </b> " + s[s.length - 1]));
            } else {
                tv_update_doc.setText(Html.fromHtml("<b>Uploaded Document : </b> "));
            }

            ll_remarks_list.addView(tv_update_time);
            ll_remarks_list.addView(tv_update_by);
            ll_remarks_list.addView(tv_update_field);
            ll_remarks_list.addView(tv_update_doc);
            ll_remarks_list.addView(tv_divider);

        }
    }


    public void remaksDetails() {
        final Dialog dialog_serialDetails;
        dialog_serialDetails =
                new Dialog(getActivity(), R.style.FullHeightDialog);
        dialog_serialDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_serialDetails.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        dialog_serialDetails.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tt_remaks, null);
        dialog_serialDetails.setContentView(view);
        ListView lv_remakss = (ListView) view.findViewById(R.id.lv_remakss);
        TextView tv_hader = (TextView) view.findViewById(R.id.tv_hader);
        TextView txt_no_remarks_added = (TextView) view.findViewById(R.id.txt_no_remarks_added);
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            Utils.msgText(getActivity(), "572", tv_hader);
        } else {
            Utils.msgText(getActivity(), "106", tv_hader);
        }
        Button bt_popup_close = (Button) view.findViewById(R.id.bt_popup_close);

        bt_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog_serialDetails.dismiss();
            }
        });


        final Window window_SignIn = dialog_serialDetails.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog_serialDetails.show();
        if (response_remarks != null && response_remarks.getRemarks() != null && response_remarks.getRemarks().size() > 0) {
            lv_remakss.setVisibility(View.VISIBLE);
            txt_no_remarks_added.setVisibility(View.GONE);
            lv_remakss.setAdapter(new AdapterRemarks(getActivity(), response_remarks.getRemarks()));
        } else {
            lv_remakss.setVisibility(View.GONE);
            txt_no_remarks_added.setVisibility(View.VISIBLE);
        }

    }

    void SetData() {
        et_ticket_id.setText(ticket_id);
        et_ref_tkt_id.setText(response_ticket_details.getREF_TICKET_ID());
        et_ticket_logged_time.setText(response_ticket_details
                .getTICKET_LOG_TIME());

        if (UtilsTask.ddFlag) {
            if (!response_ticket_details.getLATITUDE().isEmpty() && response_ticket_details.getLATITUDE() != null && !response_ticket_details.getLONGITUDE().isEmpty() && response_ticket_details.getLONGITUDE() != null
                    && response_ticket_details.getDigital_dis() != null) {
                tv_navigate.setVisibility(View.VISIBLE);

            } else {
                tv_navigate.setVisibility(View.GONE);
            }
        }

        if (response_ticket_details.getPRIORITY_NAME() != null) {
            sp_priority.setText(response_ticket_details.getPRIORITY_NAME());
        }

        if (mAppPreferences.getSiteNameEnable() == 1 && response_ticket_details.getsName() != null) {
            sName = "(" + response_ticket_details.getsName() + ")";
        } else {
            sName = "";
        }

        et_site_id.setText(response_ticket_details.getSITE_ID() + sName);
        if (response_ticket_details.getSITE_STATUS() == null) {
            et_site_status.setText("");
        } else {
            if (response_ticket_details.getSITE_STATUS().equalsIgnoreCase("1")) {
                et_site_status.setText("Yes");
            } else if (response_ticket_details.getSITE_STATUS()
                    .equalsIgnoreCase("2")) {
                et_site_status.setText("No");
            }
        }

        if (response_ticket_details.getDgReading() != null) {
            txt_dg_reading.setText(response_ticket_details.getDgReading());
        } else {
            txt_dg_reading.setText("");
        }

        if (response_ticket_details.getGridReading() != null) {
            txt_grid_reading.setText(response_ticket_details.getGridReading());
        } else {
            txt_grid_reading.setText("");
        }


        if (response_ticket_details.getHubSiteId() != null) {                        //1.2
            txt_hub_siteId.setText(response_ticket_details.getHubSiteId());
        } else {
            txt_hub_siteId.setText("");
        }

        if (response_ticket_details.getFuelLevel() != null) {
            txt_fuel_level.setText(response_ticket_details.getFuelLevel());
        } else {
            txt_fuel_level.setText("");
        }

        if (response_ticket_details.getActionTaken() != null) {
            txt_action_taken.setText(response_ticket_details.getActionTaken());
        } else {
            txt_action_taken.setText("");
        }

        //  txt_ticket_treatment.setText("");

        if (response_ticket_details.getTktTretmnt() != null) {
            txt_ticket_treatment.setText(response_ticket_details.getTktTretmnt());
        } else {
            txt_ticket_treatment.setText("");
        }

        if (response_ticket_details.getFirstLevel() != null) {
            txt_first_level.setText(response_ticket_details.getFirstLevel());
        } else {
            txt_first_level.setText("");
        }

        if (response_ticket_details.getSecondLevel() != null) {
            txt_second_level.setText(response_ticket_details.getSecondLevel());
        } else {
            txt_second_level.setText("");
        }

        if (response_ticket_details.getRejCat() != null) {
            txt_reject_category.setText(response_ticket_details.getRejCat());
        } else {
            txt_reject_category.setText("");
        }

        if (response_ticket_details.getRejRmk() != null) {
            txt_reject_remarks.setText(response_ticket_details.getRejRmk());
        } else {
            txt_reject_remarks.setText("");
        }

        et_operator_site_id.setText(response_ticket_details.getOPERATOR_SITE_ID());
        et_alarm_details.setText(response_ticket_details.getALARM_DETAIL());
        et_ticket_logged_by.setText(response_ticket_details.getTICKET_LOGGED_BY());
        et_problem_description.setText(response_ticket_details.getPROBLEM_DESC());
        txt_value_equipment.setText(response_ticket_details.getEQUIPMENT());
        txt_value_alarm_type.setText(response_ticket_details.getALARM_TYPE());
        txt_value_alarm_description.setText(response_ticket_details.getALARM_SHORT_DISCRIPTION());
        txt_value_ticket_type.setText(response_ticket_details.getTICKET_TYPE());
        if (response_ticket_details.getTICKET_TYPE_ID().equalsIgnoreCase("18")) {
            if (response_ticket_details.getTICKET_STATUS().equalsIgnoreCase("Refered")) {
                txt_value_ticket_assignment_user_details.setText(response_ticket_details.getGROUP_NAME());
            } else {
                txt_value_ticket_assignment_user_details.setText(response_ticket_details.getASSIGNED_TO());
            }
        } else {
            txt_value_ticket_assignment_user_details.setText(response_ticket_details.getASSIGNED_TO());
        }
        txt_value_status.setText(response_ticket_details.getTICKET_STATUS());
        et_eta.setText(response_ticket_details.getETA());
        et_eta_time.setText(response_ticket_details.getETA_TIME());
        et_etr.setText(response_ticket_details.getETR());
        et_etr_time.setText(response_ticket_details.getETR_TIME());
        et_rca_category.setText(response_ticket_details.getRCA_CATEGORY_NAME());
        et_rca.setText(response_ticket_details.getRCA());
        if (response_ticket_details.getPROBLEM_START_DATE() == null) {
            s1 = "";
        } else {
            s1 = response_ticket_details.getPROBLEM_START_DATE();
        }
        if (response_ticket_details.getPROBLEM_START_TIME() == null) {
            s2 = "";
        } else {
            s2 = response_ticket_details.getPROBLEM_START_TIME();
        }
        et_problem_start_date_time.setText(s1 + " " + s2);

        if (response_ticket_details.getPROBLEM_END_DATE() == null) {
            s3 = "";
        } else {
            s3 = response_ticket_details.getPROBLEM_END_DATE();
        }
        if (response_ticket_details.getPROBLEM_END_TIME() == null) {
            s4 = "";
        } else {
            s4 = response_ticket_details.getPROBLEM_END_TIME();
        }
        et_problem_end_date_time.setText(s3 + " " + s4);
        //Avdhesh
        if (response_ticket_details.getSERVICE_IMPACTED() == null) {
            serviceImpacted = "";
        } else {
            serviceImpacted = response_ticket_details.getSERVICE_IMPACTED();
        }
        sp_serviceImapact.setText(serviceImpacted);
        if (response_ticket_details.getSERVICE_IMPACT_START() == null) {
            serviceImactStart = "";
            serviceImactStartTime = "";
        } else {
            serviceImactStart = response_ticket_details.getSERVICE_IMPACT_START();
            serviceImactStartTime = response_ticket_details.getSERVICE_IMPACT_START_DATE();
        }
        tv_serviceImpactStart.setText(serviceImactStart);
        tv_serviceImpactStartTime.setText(serviceImactStartTime);
        if (response_ticket_details.getSERVICE_AFFECTED() == null) {
            ServiceAffected = "";
        } else {
            ServiceAffected = response_ticket_details.getSERVICE_AFFECTED();
        }
        tv_servicesAffected_value.setText(ServiceAffected);

        if (response_ticket_details.getBATTERY_DIS_START_DATE() == null) {
            s5 = "";
        } else {
            s5 = response_ticket_details.getBATTERY_DIS_START_DATE();
        }
        if (response_ticket_details.getBATTERY_DIS_START_TIME() == null) {
            s6 = "";
        } else {
            s6 = response_ticket_details.getBATTERY_DIS_START_TIME();
        }
        et_bttry_dschrg_date.setText(s5 + " " + s6);
        et_bttry_backup.setText(response_ticket_details
                .getBATTERY_BACKUP_TIME());
        txt_rca_sub_category.setText(response_ticket_details
                .getRCA_SUB_CAT_NAME());
        et_contact.setText(response_ticket_details.getCONTACTS());
        txt_operator.setText(response_ticket_details.getOPERATOR_LIST_NAME());
        txt_operator_exempt.setText(response_ticket_details.getoExemName());
        txt_dependent_site.setText(response_ticket_details.getEFFECTED_SITES());

        if (response_ticket_details.getTrvlDistnc() != null && !response_ticket_details.getTrvlDistnc().

                equalsIgnoreCase("null")) {
            et_trvlDistnc.setText(response_ticket_details.getTrvlDistnc());
        } else {
            et_trvlDistnc.setText("");
        }

        if (response_ticket_details.getNoOfTech() != null && !response_ticket_details.getNoOfTech().

                equalsIgnoreCase("null")) {
            et_noOfTech.setText(response_ticket_details.getNoOfTech());
        } else {
            et_noOfTech.setText("");
        }

        if (response_ticket_details.getWrkgNights() != null && !response_ticket_details.getWrkgNights().

                equalsIgnoreCase("null")) {
            et_wrkgNights.setText(response_ticket_details.getWrkgNights());
        } else {
            et_wrkgNights.setText("");
        }


        String pDescription = dbHelper.getPDesc("4", response_ticket_details.getALARM_DESC_ID(), mAppPreferences.getTTModuleSelection());

        if (pDescription.contains("trvlDistnc") && dbHelper.getDetailsTTField("Distance Travelled", mAppPreferences.getTTModuleSelection())
                        .

                equalsIgnoreCase("Y")) {
            ll_trvlDistnc.setVisibility(View.VISIBLE);
        } else {
            ll_trvlDistnc.setVisibility(View.GONE);
        }

        if (pDescription.contains("noOfTech") && dbHelper.getDetailsTTField("No. Of Technician", mAppPreferences.getTTModuleSelection())
                        .

                equalsIgnoreCase("Y")) {
            ll_noOfTech.setVisibility(View.VISIBLE);
        } else {
            ll_noOfTech.setVisibility(View.GONE);
        }

        if (pDescription.contains("wrkgNights") && dbHelper.getDetailsTTField("Working Nights", mAppPreferences.getTTModuleSelection())
                        .

                equalsIgnoreCase("Y")) {
            ll_wrkgNights.setVisibility(View.VISIBLE);
        } else {
            ll_wrkgNights.setVisibility(View.GONE);
        }
    }

    public class IncidentMetaDataTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;

        public IncidentMetaDataTask(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
                Gson gson = new Gson();
                nameValuePairs.add(new BasicNameValuePair("module", "Incident"));
                nameValuePairs.add(new BasicNameValuePair("datatype", metaDataType()));
                nameValuePairs.add(new BasicNameValuePair("userID", mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("lat", "1"));
                nameValuePairs.add(new BasicNameValuePair("lng", "2"));
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_GetMetadata;
                } else {
                    url = moduleUrl + WebMethods.url_GetMetadata;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                resposnse_Incident_meta = gson.fromJson(res, IncidentMetaList.class);
            } catch (Exception e) {
                e.printStackTrace();
                resposnse_Incident_meta = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if ((resposnse_Incident_meta == null)) {
                // Toast.makeText(RequestDetail.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
                //WorkFlowUtils.toast(RequestDetail.this, "70");
            } else if (resposnse_Incident_meta != null) {
                if (resposnse_Incident_meta.getParam() != null && resposnse_Incident_meta.getParam().size() > 0) {
                    dbHelper.clearInciParamData(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertInciParamcData(resposnse_Incident_meta.getParam(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "10",
                            dbHelper.getLoginTimeStmp("10", mAppPreferences.getTTModuleSelection()), 2,
                            mAppPreferences.getTTModuleSelection());
                }
                if (resposnse_Incident_meta.getOpertors() != null && resposnse_Incident_meta.getOpertors().size() > 0) {
                    dbHelper.clearInciOpcoData(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertInciOpcoData(resposnse_Incident_meta.getOpertors(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "11", dbHelper.getLoginTimeStmp("11", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
                if (resposnse_Incident_meta.getRcaCategory() != null && resposnse_Incident_meta.getRcaCategory().size() > 0) {
                    dbHelper.clearInciRcaData(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertInciRcaData(resposnse_Incident_meta.getRcaCategory(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "12", dbHelper.getLoginTimeStmp("12", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
                if (resposnse_Incident_meta.getEquipment() != null && resposnse_Incident_meta.getEquipment().size() > 0) {
                    dbHelper.clearInciEqpData(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertInciEqpcData(resposnse_Incident_meta.getEquipment(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "14", dbHelper.getLoginTimeStmp("14", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
                if (resposnse_Incident_meta.getGroups() != null && resposnse_Incident_meta.getGroups().size() > 0) {
                    dbHelper.clearIncigrpData(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertInciGrpData(resposnse_Incident_meta.getGroups(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "15", dbHelper.getLoginTimeStmp("15", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
                if (resposnse_Incident_meta.getSpareParts() != null && resposnse_Incident_meta.getSpareParts().size() > 0) {
                    dbHelper.clearSparePart();
                    dbHelper.insertSparePart(resposnse_Incident_meta.getSpareParts());
                    dbHelper.dataTS(null, null, "26", dbHelper.getLoginTimeStmp("26", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }

                if (resposnse_Incident_meta.getUser() != null && resposnse_Incident_meta.getUser().size() > 0) {
                    dbHelper.clearUserContact(mAppPreferences.getTTModuleSelection());
                    dbHelper.insertUserContact(resposnse_Incident_meta.getUser(), mAppPreferences.getTTModuleSelection());
                    dbHelper.dataTS(null, null, "22", dbHelper.getLoginTimeStmp("22", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());
                }
            } else {
                // Toast.makeText(RequestDetail.this,
                // "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(), "13");
            }
        }
    }

    public String metaDataType() {
        String DataType_Str = "1";
        // for Param
        String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("10", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("10", mAppPreferences.getTTModuleSelection()), "10");
        // for Operator
        String j = Utils.CompareDates(dbHelper.getSaveTimeStmp("11", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("11", mAppPreferences.getTTModuleSelection()), "11");
        // for RCA
        String k = Utils.CompareDates(dbHelper.getSaveTimeStmp("12", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("12", mAppPreferences.getTTModuleSelection()), "12");
        // For Equipment
        String l = Utils.CompareDates(dbHelper.getSaveTimeStmp("14", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("14", mAppPreferences.getTTModuleSelection()), "14");
        // For froup
        String m = Utils.CompareDates(dbHelper.getSaveTimeStmp("15", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("15", mAppPreferences.getTTModuleSelection()), "15");
        // For Spare Part
        String n = Utils.CompareDates(dbHelper.getSaveTimeStmp("26", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("26", mAppPreferences.getTTModuleSelection()), "26");
        // For User Primary Contact
        String o = Utils.CompareDates(dbHelper.getSaveTimeStmp("22", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("22", mAppPreferences.getTTModuleSelection()), "22");


        if (i != "1") {
            DataType_Str = i;
        }
        if (j != "1") {
            if (DataType_Str == "1") {
                DataType_Str = j;
            } else {
                DataType_Str = DataType_Str + "," + j;
            }
        }
        if (k != "1") {
            if (DataType_Str == "1") {
                DataType_Str = k;
            } else {
                DataType_Str = DataType_Str + "," + k;
            }
        }

        if (l != "1") {
            if (DataType_Str == "1") {
                DataType_Str = l;
            } else {
                DataType_Str = DataType_Str + "," + l;
            }
        }

        if (m != "1") {
            if (DataType_Str == "1") {
                DataType_Str = m;
            } else {
                DataType_Str = DataType_Str + "," + m;
            }
        }

        if (n != "1") {
            if (DataType_Str == "1") {
                DataType_Str = n;
            } else {
                DataType_Str = DataType_Str + "," + n;
            }
        }

        if (o != "1") {
            if (DataType_Str == "1") {
                DataType_Str = o;
            } else {
                DataType_Str = DataType_Str + "," + o;
            }
        }

        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }

    public String alarmDescDataType() {
        String DataType_Str = "1";
        String i = Utils.CompareDates(dbHelper.getSaveTimeStmp("19", mAppPreferences.getTTModuleSelection()),
                dbHelper.getLoginTimeStmp("19", mAppPreferences.getTTModuleSelection()), "19");
        if (i != "1") {
            DataType_Str = i;
        }
        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        return DataType_Str;
    }

    public class AlarmDescTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;

        public AlarmDescTask(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
                Gson gson = new Gson();
                nameValuePairs.add(new BasicNameValuePair("alarmId", ""));
                nameValuePairs.add(new BasicNameValuePair("equipId", ""));
                nameValuePairs.add(new BasicNameValuePair("severityId", ""));
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_GetCompleteAlarmList;
                } else {
                    url = moduleUrl + WebMethods.url_GetCompleteAlarmList;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                resposnse_alarm = gson.fromJson(res, AlarmDescList.class);
            } catch (Exception e) {
                e.printStackTrace();
                resposnse_alarm = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if ((resposnse_alarm == null)) {
                // Toast.makeText(RequestDetail.this,"Meta data not provided by server",Toast.LENGTH_LONG).show();
                //WorkFlowUtils.toast(RequestDetail.this, "70");
            } else if (resposnse_alarm.getAlarm_description() != null && resposnse_alarm.getAlarm_description().size() > 0) {
                dbHelper.clearAlarmDescData(mAppPreferences.getTTModuleSelection());
                dbHelper.insertAlarmDescData(resposnse_alarm
                        .getAlarm_description(), mAppPreferences.getTTModuleSelection());
                dbHelper.dataTS(null, null, "19",
                        dbHelper.getLoginTimeStmp("19", mAppPreferences.getTTModuleSelection()), 2, mAppPreferences.getTTModuleSelection());

            } else {
                // Toast.makeText(RequestDetail.this,
                // "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(), "13");
            }
        }
    }

    public void getControllerIds() {
        ll_remarks_list = (LinearLayout) view.findViewById(R.id.ll_remarks_list);
        tv_next = (TextView) view.findViewById(R.id.tv_next);
        RelativeLayout rl_header_ticket_details = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_details);
        rl_header_ticket_details.setVisibility(View.GONE);
        //list_remarks = (ExpandableHeightGridView) view.findViewById(R.id.lv_remarks_details);
        list_img = (RecyclerView) view.findViewById(R.id.list_img);
        list_img.setNestedScrollingEnabled(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list_img.setLayoutManager(layoutManager);

        RL = (LinearLayout) view.findViewById(R.id.rl);
        scrollview = (ScrollView) view.findViewById(R.id.scroll);
        et_ticket_id = (TextView) view.findViewById(R.id.et_ticket_id);
        et_ticket_id.setTypeface(Utils.typeFace(getActivity()));
        et_ref_tkt_id = (TextView) view.findViewById(R.id.et_ref_tkt_id);
        et_ref_tkt_id.setTypeface(Utils.typeFace(getActivity()));
        et_ticket_logged_time = (TextView) view.findViewById(R.id.et_ticket_logged_date);
        et_ticket_logged_time.setTypeface(Utils.typeFace(getActivity()));
        et_site_id = (TextView) view.findViewById(R.id.et_site_id);
        et_site_id.setTypeface(Utils.typeFace(getActivity()));
        et_site_status = (TextView) view.findViewById(R.id.et_site_status);
        et_site_status.setTypeface(Utils.typeFace(getActivity()));
        et_operator_site_id = (TextView) view.findViewById(R.id.et_operator_site_id);
        et_operator_site_id.setTypeface(Utils.typeFace(getActivity()));
        et_alarm_details = (TextView) view.findViewById(R.id.et_alarm_details);
        et_alarm_details.setTypeface(Utils.typeFace(getActivity()));
        et_ticket_logged_by = (TextView) view.findViewById(R.id.et_ticket_logged_by);
        et_ticket_logged_by.setTypeface(Utils.typeFace(getActivity()));
        et_problem_description = (TextView) view.findViewById(R.id.et_problem_description);
        et_problem_description.setTypeface(Utils.typeFace(getActivity()));
        txt_value_equipment = (TextView) view.findViewById(R.id.txt_value_equipment);
        txt_value_alarm_type = (TextView) view.findViewById(R.id.txt_value_alarm_type);
        txt_value_alarm_description = (TextView) view.findViewById(R.id.txt_value_alarm_description);
        txt_value_ticket_type = (TextView) view.findViewById(R.id.txt_value_ticket_type);
        txt_value_ticket_assignment_user_details = (TextView)
                view.findViewById(R.id.txt_value_ticket_assignment_user_details);
        txt_value_status = (TextView) view.findViewById(R.id.txt_value_status);
        et_eta = (TextView) view.findViewById(R.id.txt_eta);
        et_eta_time = (TextView) view.findViewById(R.id.txt_eta_time);
        et_etr = (TextView) view.findViewById(R.id.txt_etr);
        et_etr_time = (TextView) view.findViewById(R.id.txt_etr_time);
        et_rca_category = (TextView) view.findViewById(R.id.txt_rca_category);
        et_rca = (TextView) view.findViewById(R.id.txt_rca);
        et_problem_start_date_time = (TextView) view.findViewById(R.id.et_problem_start_date_time);
        et_problem_end_date_time = (TextView) view.findViewById(R.id.et_problem_end_date_time);
        sp_serviceImapact = (TextView) view.findViewById(R.id.sp_serviceImapact);
        tv_serviceImpactStart = (TextView) view.findViewById(R.id.tv_serviceImpactStart);
        tv_servicesAffected_value = (TextView) view.findViewById(R.id.tv_servicesAffected_value);
        tv_serviceImpactStartTime = (TextView) view.findViewById(R.id.tv_serviceImpactStartTime);
        et_bttry_dschrg_date = (TextView) view.findViewById(R.id.et_bttry_dschrg_date);
        et_bttry_backup = (TextView) view.findViewById(R.id.et_bttry_backup);
        txt_rca_sub_category = (TextView) view.findViewById(R.id.txt_rca_sub_category);
        et_contact = (TextView) view.findViewById(R.id.txt_contact);
        txt_operator = (TextView) view.findViewById(R.id.txt_operator);
        txt_operator_exempt = (TextView) view.findViewById(R.id.txt_operator_exempt);
        txt_dependent_site = (TextView) view.findViewById(R.id.txt_dependent_site);
        tv_brand_logo = (TextView) view.findViewById(R.id.tv_brand_logo);
        tv_site_id = (TextView) view.findViewById(R.id.tv_site_id);
        tv_site_status = (TextView) view.findViewById(R.id.tv_site_status);
        tv_ticket_id = (TextView) view.findViewById(R.id.tv_ticket_id);
        tv_ref_tkt_id = (TextView) view.findViewById(R.id.tv_ref_tkt_id);
        tv_problem_start_date_time = (TextView) view.findViewById(R.id.tv_problem_start_date_time);
        tv_problem_end_date_time = (TextView) view.findViewById(R.id.tv_problem_end_date_time);
        tv_ticket_logged_date = (TextView) view.findViewById(R.id.tv_ticket_logged_date);
        tv_bttry_dschrg_date = (TextView) view.findViewById(R.id.tv_bttry_dschrg_date);
        tv_bttry_backup = (TextView) view.findViewById(R.id.tv_bttry_backup);
        tv_operator_site_id = (TextView) view.findViewById(R.id.tv_operator_site_id);
        tv_equipment = (TextView) view.findViewById(R.id.tv_equipment);
        tv_severity = (TextView) view.findViewById(R.id.tv_severity);
        tv_alarm_description = (TextView) view.findViewById(R.id.tv_alarm_description);
        tv_alarm_details = (TextView) view.findViewById(R.id.tv_alarm_details);
        tv_tkt_logged_by = (TextView) view.findViewById(R.id.tv_tkt_logged_by);
        tv_tkt_type = (TextView) view.findViewById(R.id.tv_tkt_type);
        tv_problem_description = (TextView) view.findViewById(R.id.tv_problem_description);
        tv_assign_to = (TextView) view.findViewById(R.id.tv_assign_to);
        tv_contact = (TextView) view.findViewById(R.id.tv_contact);
        tv_tkt_status = (TextView) view.findViewById(R.id.tv_tkt_status);
        tv_eta = (TextView) view.findViewById(R.id.tv_eta);
        tv_eta_time = (TextView) view.findViewById(R.id.tv_eta_time);
        tv_etr = (TextView) view.findViewById(R.id.tv_etr);
        tv_etr_time = (TextView) view.findViewById(R.id.tv_etr_time);
        tv_rca_category = (TextView) view.findViewById(R.id.tv_rca_category);
        tv_rca_sub_category = (TextView) view.findViewById(R.id.tv_rca_sub_category);
        tv_rca = (TextView) view.findViewById(R.id.tv_rca);
        tv_operator = (TextView) view.findViewById(R.id.tv_operator);
        tv_operator_exempt = (TextView) view.findViewById(R.id.tv_operator_exempt);
        tv_eff_site = (TextView) view.findViewById(R.id.tv_eff_site);
        tv_remarks = (TextView) view.findViewById(R.id.tv_remarks);
        txt_no_remarks = (TextView) view.findViewById(R.id.txt_no_remarks);
        ll_site_id = (LinearLayout) view.findViewById(R.id.ll_site_id);
        ll_site_vsblty = (LinearLayout) view.findViewById(R.id.ll_site_vsblty);
        ll_tkt_id = (LinearLayout) view.findViewById(R.id.ll_tkt_id);
        ll_ref_tkt_id = (LinearLayout) view.findViewById(R.id.ll_ref_tkt_id);
        ll_prb_strt_date = (LinearLayout) view.findViewById(R.id.ll_prb_strt_date);
        ll_prb_end_date = (LinearLayout) view.findViewById(R.id.ll_prb_end_date);
        ll_tkt_log_date = (LinearLayout) view.findViewById(R.id.ll_tkt_log_date);
        ll_bttry_dschrg = (LinearLayout) view.findViewById(R.id.ll_bttry_dschrg);
        ll_bttry_backup = (LinearLayout) view.findViewById(R.id.ll_bttry_backup);
        ll_operator_site_id = (LinearLayout) view.findViewById(R.id.ll_operator_site_id);
        ll_equipment = (LinearLayout) view.findViewById(R.id.ll_equipment);
        ll_severity = (LinearLayout) view.findViewById(R.id.ll_severity);
        ll_alrm_desc = (LinearLayout) view.findViewById(R.id.ll_alrm_desc);
        ll_alarm_detail = (LinearLayout) view.findViewById(R.id.ll_alarm_detail);
        ll_tkt_log_by = (LinearLayout) view.findViewById(R.id.ll_tkt_log_by);
        ll_tkt_type = (LinearLayout) view.findViewById(R.id.ll_tkt_type);
        ll_prb_desc = (LinearLayout) view.findViewById(R.id.ll_prb_desc);
        ll_assigned_to = (LinearLayout) view.findViewById(R.id.ll_assigned_to);
        ll_contacts = (LinearLayout) view.findViewById(R.id.ll_contacts);
        ll_tkt_status = (LinearLayout) view.findViewById(R.id.ll_tkt_status);
        ll_eta = (LinearLayout) view.findViewById(R.id.ll_eta);
        ll_eta_time = (LinearLayout) view.findViewById(R.id.ll_eta_time);
        ll_etr = (LinearLayout) view.findViewById(R.id.ll_etr);
        ll_etr_time = (LinearLayout) view.findViewById(R.id.ll_etr_time);
        ll_rca_category = (LinearLayout) view.findViewById(R.id.ll_rca_category);
        ll_rca_sub_category = (LinearLayout) view.findViewById(R.id.ll_rca_sub_category);
        ll_rca = (LinearLayout) view.findViewById(R.id.ll_rca);
        ll_operator = (LinearLayout) view.findViewById(R.id.ll_operator);
        ll_operator_exempt = (LinearLayout) view.findViewById(R.id.ll_operator_exempt);
        ll_operator_exempt = (LinearLayout) view.findViewById(R.id.ll_operator_exempt);// 0.2
        ll_effected_sites = (LinearLayout) view.findViewById(R.id.ll_effected_sites);
        ll_remarks = (LinearLayout) view.findViewById(R.id.ll_remarks);
        ll_trvlDistnc = (LinearLayout) view.findViewById(R.id.ll_trvlDistnc);
        ll_noOfTech = (LinearLayout) view.findViewById(R.id.ll_noOfTech);
        ll_wrkgNights = (LinearLayout) view.findViewById(R.id.ll_wrkgNights);
        et_trvlDistnc = (TextView) view.findViewById(R.id.et_trvlDistnc);
        et_noOfTech = (TextView) view.findViewById(R.id.et_noOfTech);
        et_wrkgNights = (TextView) view.findViewById(R.id.et_wrkgNights);
        ll_dg_reading = (LinearLayout) view.findViewById(R.id.ll_dg_reading);
        ll_grid_reading = (LinearLayout) view.findViewById(R.id.ll_grid_reading);
        ll_hub_siteId = (LinearLayout) view.findViewById(R.id.ll_hub_siteId);
        ll_fuel_level = (LinearLayout) view.findViewById(R.id.ll_fuel_level);
        ll_action_taken = (LinearLayout) view.findViewById(R.id.ll_action_taken);
        ll_ticket_treatment = (LinearLayout) view.findViewById(R.id.ll_ticket_treatment);
        ll_first_level = (LinearLayout) view.findViewById(R.id.ll_first_level);
        ll_second_level = (LinearLayout) view.findViewById(R.id.ll_second_level);
        ll_reject_category = (LinearLayout) view.findViewById(R.id.ll_reject_category);
        ll_reject_remarks = (LinearLayout) view.findViewById(R.id.ll_reject_remarks);
        ll_chk_link = (LinearLayout) view.findViewById(R.id.ll_chk_link);
        ll_ref_tt_link = (LinearLayout) view.findViewById(R.id.ll_ref_tt_link);
        tv_dg_reading = (TextView) view.findViewById(R.id.tv_dg_reading);
        txt_dg_reading = (TextView) view.findViewById(R.id.txt_dg_reading);
        tv_grid_reading = (TextView) view.findViewById(R.id.tv_grid_reading);
        txt_grid_reading = (TextView) view.findViewById(R.id.txt_grid_reading);
        tv_fuel_level = (TextView) view.findViewById(R.id.tv_fuel_level);
        txt_fuel_level = (TextView) view.findViewById(R.id.txt_fuel_level);
        tv_action_taken = (TextView) view.findViewById(R.id.tv_action_taken);
        txt_action_taken = (TextView) view.findViewById(R.id.txt_action_taken);

        tv_hub_siteId = (TextView) view.findViewById(R.id.tv_hub_siteId);
        txt_hub_siteId = (TextView) view.findViewById(R.id.txt_hub_siteId);

        tv_ticket_treatment = (TextView) view.findViewById(R.id.tv_ticket_treatment);
        txt_ticket_treatment = (TextView) view.findViewById(R.id.txt_ticket_treatment);

        tv_first_level = (TextView) view.findViewById(R.id.tv_first_level);
        txt_first_level = (TextView) view.findViewById(R.id.txt_first_level);
        tv_second_level = (TextView) view.findViewById(R.id.tv_second_level);
        txt_second_level = (TextView) view.findViewById(R.id.txt_second_level);


        tv_reject_category = (TextView) view.findViewById(R.id.tv_reject_category);
        txt_reject_category = (TextView) view.findViewById(R.id.txt_reject_category);
        tv_reject_remarks = (TextView) view.findViewById(R.id.tv_reject_remarks);
        txt_reject_remarks = (TextView) view.findViewById(R.id.txt_reject_remarks);
        tv_chk_link = (TextView) view.findViewById(R.id.tv_chk_link);
        tv_chk_link.setPaintFlags(tv_chk_link.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

        tv_ref_tt_link = (TextView) view.findViewById(R.id.tv_ref_tt_link);
        tv_ref_tt_link.setPaintFlags(tv_ref_tt_link.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

    }

    public void showHideField() {

        if (!dbHelper.getDetailsTTField("DG Meter Reading", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_dg_reading.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Grid Meter Reading", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_grid_reading.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Fuel Level", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_fuel_level.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Action Taken", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_action_taken.setVisibility(View.GONE);
        }


        if (!dbHelper.getDetailsTTField("Ticket_treatment", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_ticket_treatment.setVisibility(View.GONE);
        }


        if (!dbHelper.getDetailsTTField("Hub Site Id", mAppPreferences.getTTModuleSelection())   //1.2
                .equalsIgnoreCase("Y")) {
            ll_hub_siteId.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("First Level Approval", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_first_level.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Second Level Approval", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_second_level.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Checklist Link",
                mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_chk_link.setVisibility(View.GONE);
        }


        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            ll_reject_category.setVisibility(View.VISIBLE);
            ll_reject_remarks.setVisibility(View.VISIBLE);
        } else {
            ll_reject_category.setVisibility(View.GONE);
            ll_reject_remarks.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Site Id", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_site_id.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Site Visibility", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_site_vsblty.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Ticket ID", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_tkt_id.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("Ref Ticket Id", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_ref_tkt_id.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Problem Start Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_prb_strt_date.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Problem End Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_prb_end_date.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Ticket Logged Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_tkt_log_date.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Battery Discharge Start Date Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_bttry_dschrg.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Battery Backup", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_bttry_backup.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Operator Site ID", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_operator_site_id.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Equipment", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_equipment.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Severity", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_severity.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Alarm Description", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_alrm_desc.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Alarm Detail", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_alarm_detail.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Ticket Logged By", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_tkt_log_by.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Ticket Type", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_tkt_type.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Problem Description", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_prb_desc.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Assigned To", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_assigned_to.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Contacts", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_contacts.setVisibility(View.GONE);
        }

        if (mAppPreferences.getUserCategory().equalsIgnoreCase("2")
                && mAppPreferences.getOperatorWiseUserField().equalsIgnoreCase("0")) {
            ll_assigned_to.setVisibility(View.GONE);
            ll_contacts.setVisibility(View.GONE);
        }


        if (!dbHelper.getDetailsTTField("Ticket Status", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_tkt_status.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("ETA", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_eta.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("ETA Time", mAppPreferences.getTTModuleSelection())
                .equalsIgnoreCase("Y")) {
            ll_eta_time.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("ETR", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_etr.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("ETR Time", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_etr_time.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("RCA Category", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_rca_category.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("RCA Sub Category", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase(
                "Y")) {
            ll_rca_sub_category.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("RCA", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_rca.setVisibility(View.GONE);
        }
        if (!dbHelper.getDetailsTTField("Operator", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_operator.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Exempted Operator", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase(
                "Y")) {// 0.2
            ll_operator_exempt.setVisibility(View.GONE);
        }

        if (!dbHelper.getDetailsTTField("Effected Sites", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_effected_sites.setVisibility(View.GONE);
        }


        if (dbHelper.getDetailsTTField("Remarks", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("Y")) {
            ll_remarks.setVisibility(View.VISIBLE);
            ll_remarks_list.setVisibility(View.VISIBLE);
            tv_remarks.setEnabled(false);
        } else if (dbHelper.getDetailsTTField("Remarks", mAppPreferences.getTTModuleSelection()).equalsIgnoreCase("L")) {
            ll_remarks.setVisibility(View.VISIBLE);
            tv_remarks.setEnabled(true);
            Utils.remarksLink(getActivity(), tv_remarks);
            ll_remarks_list.setVisibility(View.GONE);
        } else {
            ll_remarks.setVisibility(View.GONE);
            ll_remarks_list.setVisibility(View.GONE);
        }

    }

    // Method to set images in List View
    private void setImage() {
        ViewImage64 viewImg = null;
        lhmImages.clear();
        if (response_remarks != null && response_remarks.getRemarks() != null && response_remarks.getRemarks().size() > 0) {
            for (int i = 0; i < response_remarks.getRemarks().size(); i++) {
                if (response_remarks.getRemarks().get(i).getDocumentPath() != null
                        && !response_remarks.getRemarks().get(i).getDocumentPath().equalsIgnoreCase("null")
                        && response_remarks.getRemarks().get(i).getDocumentPath().length() != 0) {
                    String extension = response_remarks.getRemarks().get(i).getDocumentPath();
                    if (extension.contains(".jpg") || extension.contains(".png") || extension.contains(".jpeg") ||
                            extension.contains(".JPG") || extension.contains(".PNG") || extension.contains(".JPEG") ||
                            extension.contains(".MP4") || extension.contains(".mp4")) {
                        viewImg = new ViewImage64();
                        //http://192.168.3.103:5054/iTower Files/Trouble Ticket\MITH123-TT-15908059975422.mp4
                        viewImg.setTimeStamp(response_remarks.getRemarks().get(i).getIMG_TIME_STAMP());
                        viewImg.setName(response_remarks.getRemarks().get(i).getIMG_NAME());
                        viewImg.setPath(response_remarks.getRemarks().get(i).getDocumentPath());
                        //.replaceAll("192.168.3.103:5054/iTower Files/Trouble Ticket",
                        //        "203.122.7.134:5100/images"));
                        viewImg.setLati(response_remarks.getRemarks().get(i).getLATITUDE());
                        viewImg.setLongi(response_remarks.getRemarks().get(i).getLONGITUDE());
                        lhmImages.add(viewImg);
                        minImgCounter++;
                    } else {
                        minImgCounter++;
                    }

                }
            }
        } else {
            list_img.setVisibility(View.GONE);
        }

        if (lhmImages.size() > 0) {
            list_img.setVisibility(View.VISIBLE);
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(lhmImages, getActivity());
            list_img.setAdapter(horizontalAdapter);
        } else {
            list_img.setVisibility(View.GONE);
        }
    }


    public void setMsgTT(String module) {
        my_search = dbHelper.getSubMenuRight("MySearch", module);
        assigned_tab = dbHelper.getSubMenuRight("AssignedTab", module);
        raised_tab = dbHelper.getSubMenuRight("RaisedTab", module);
        resolved_tab = dbHelper.getSubMenuRight("ResolvedTab", module);
        Utils.msgText(getActivity(), "120", tv_next);
        Utils.msgText(getActivity(), "554", tv_brand_logo);
        Utils.msgText(getActivity(), "77", tv_site_id);
        Utils.msgText(getActivity(), "78", tv_site_status);
        Utils.msgText(getActivity(), "79", tv_ticket_id);
        Utils.msgText(getActivity(), "80", tv_ref_tkt_id);
        Utils.msgText(getActivity(), "86", tv_problem_start_date_time);
        Utils.msgText(getActivity(), "247", tv_problem_end_date_time);
        Utils.msgText(getActivity(), "248", tv_ticket_logged_date);
        Utils.msgText(getActivity(), "89", tv_bttry_dschrg_date);
        Utils.msgText(getActivity(), "91", tv_bttry_backup);
        Utils.msgText(getActivity(), "92", tv_operator_site_id);
        Utils.msgText(getActivity(), "81", tv_equipment);
        Utils.msgText(getActivity(), "82", tv_severity);
        Utils.msgText(getActivity(), "83", tv_alarm_description);
        Utils.msgText(getActivity(), "94", tv_alarm_details);
        Utils.msgText(getActivity(), "93", tv_tkt_logged_by);
        Utils.msgText(getActivity(), "95", tv_tkt_type);
        Utils.msgText(getActivity(), "96", tv_problem_description);
        Utils.msgText(getActivity(), "109", tv_assign_to);
        Utils.msgText(getActivity(), "97", tv_contact);
        Utils.msgText(getActivity(), "98", tv_tkt_status);
        Utils.msgText(getActivity(), "99", tv_eta);
        Utils.msgText(getActivity(), "100", tv_eta_time);
        Utils.msgText(getActivity(), "101", tv_etr);
        Utils.msgText(getActivity(), "102", tv_etr_time);
        Utils.msgText(getActivity(), "103", tv_rca_category);
        Utils.msgText(getActivity(), "104", tv_rca_sub_category);
        Utils.msgText(getActivity(), "105", tv_rca);
        Utils.msgText(getActivity(), "110", tv_operator);
        Utils.msgText(getActivity(), "112", tv_operator_exempt);
        Utils.msgText(getActivity(), "114", tv_eff_site);
        Utils.msgText(getActivity(), "106", tv_remarks);
        Utils.msgText(getActivity(), "107", txt_no_remarks);
        Utils.msgText(getActivity(), "175", tv_dg_reading);
        Utils.msgText(getActivity(), "178", tv_grid_reading);
        Utils.msgText(getActivity(), "524", tv_fuel_level);
        Utils.msgText(getActivity(), "525", tv_action_taken);
        Utils.msgText(getActivity(), "555", tv_first_level);
        Utils.msgText(getActivity(), "556", tv_second_level);
        Utils.msgText(getActivity(), "137", tv_ref_tt_link);
    }

    public void setMsgHS(String module) {
        my_search = dbHelper.getSubMenuRight("Health and Safty Search", module);
        assigned_tab = dbHelper.getSubMenuRight("AssignedTab", module);
        raised_tab = dbHelper.getSubMenuRight("RaisedTab", module);
        resolved_tab = dbHelper.getSubMenuRight("ResolvedTab", module);
        Utils.msgText(getActivity(), "120", tv_next);
        Utils.msgText(getActivity(), "554", tv_brand_logo);
        Utils.msgText(getActivity(), "77", tv_site_id);
        Utils.msgText(getActivity(), "536", tv_site_status);
        Utils.msgText(getActivity(), "92", tv_operator_site_id);
        Utils.msgText(getActivity(), "538", tv_equipment);
        Utils.msgText(getActivity(), "531", tv_severity);
        Utils.msgText(getActivity(), "533", tv_problem_description);
        Utils.msgText(getActivity(), "534", tv_assign_to);
        Utils.msgText(getActivity(), "544", tv_operator);
        Utils.msgText(getActivity(), "545", tv_operator_exempt);
        Utils.msgText(getActivity(), "557", tv_ticket_id);
        Utils.msgText(getActivity(), "80", tv_ref_tkt_id);
        Utils.msgText(getActivity(), "558", tv_problem_start_date_time);
        Utils.msgText(getActivity(), "559", tv_problem_end_date_time);
        Utils.msgText(getActivity(), "560", tv_ticket_logged_date);
        Utils.msgText(getActivity(), "561", tv_tkt_logged_by);
        Utils.msgText(getActivity(), "529", tv_tkt_type);
        Utils.msgText(getActivity(), "97", tv_contact);
        Utils.msgText(getActivity(), "571", tv_tkt_status);
        Utils.msgText(getActivity(), "572", tv_remarks);
        Utils.msgText(getActivity(), "107", txt_no_remarks);
        Utils.msgText(getActivity(), "530", tv_alarm_description);
        Utils.msgText(getActivity(), "562", tv_bttry_dschrg_date);
        Utils.msgText(getActivity(), "563", tv_alarm_details);
        Utils.msgText(getActivity(), "91", tv_bttry_backup);
        Utils.msgText(getActivity(), "564", tv_eta);
        Utils.msgText(getActivity(), "565", tv_eta_time);
        Utils.msgText(getActivity(), "566", tv_etr);
        Utils.msgText(getActivity(), "567", tv_etr_time);
        Utils.msgText(getActivity(), "568", tv_rca_category);
        Utils.msgText(getActivity(), "569", tv_rca_sub_category);
        Utils.msgText(getActivity(), "570", tv_rca);
        Utils.msgText(getActivity(), "114", tv_eff_site);
        Utils.msgText(getActivity(), "175", tv_dg_reading);
        Utils.msgText(getActivity(), "178", tv_grid_reading);
        Utils.msgText(getActivity(), "524", tv_fuel_level);
        Utils.msgText(getActivity(), "525", tv_action_taken);
        Utils.msgText(getActivity(), "555", tv_first_level);
        Utils.msgText(getActivity(), "556", tv_second_level);
        Utils.msgText(getActivity(), "573", tv_reject_category);
        Utils.msgText(getActivity(), "574", tv_reject_remarks);
    }

    private class GetImage extends AsyncTask<Void, Void, Void> {
        Context con;
        ProgressDialog pd;
        BeanGetImageList imageList;
        String txnId, scDate, activityId, sId, dgType, etsSid, imguploadflag;
        Intent i;

        private GetImage(Context con, Intent i, String txnId, String scDate, String activityId, String sId,
                         String etsSid, String dgType, String imguploadflag) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.etsSid = etsSid;
            this.dgType = dgType;
            this.imguploadflag = imguploadflag;
            this.i = i;
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
                nameValuePairs.add(new BasicNameValuePair("siteId", etsSid));
                nameValuePairs.add(new BasicNameValuePair("activityType", "0"));
                nameValuePairs.add(new BasicNameValuePair("scheduledDate", "H"));
                nameValuePairs.add(new BasicNameValuePair("dgType", txnId));

                /*nameValuePairs.add(new BasicNameValuePair("siteId", etsSid));
                nameValuePairs.add(new BasicNameValuePair("activityType", activityId));
                nameValuePairs.add(new BasicNameValuePair("scheduledDate",scDate));
                nameValuePairs.add(new BasicNameValuePair("dgType", dgType));*/
                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_GetPmImage;
                } else {
                    url = moduleUrl + WebMethods.url_GetPmImage;
                }

                String response = Utils.httpPostRequest(con, url, nameValuePairs);
                Gson gson = new Gson();
                imageList = gson.fromJson(response, BeanGetImageList.class);
            } catch (Exception e) {
                e.printStackTrace();
                imageList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
            dataBaseHelper.open();
            dataBaseHelper.deleteActivityImages(txnId, "0");
            //dataBaseHelper.deleteActivityImages(txnId.replaceAll("TT","").replaceAll("-",""));
            if (imageList == null) {
            } else if (imageList.getImageList().size() > 0) {
                for (int i = 0; i < imageList.getImageList().size(); i++) {
                    if (imageList.getImageList().get(i).getPreImgPath() != null &&
                            imageList.getImageList().get(i).getPreImgPath() != "") {

                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr = new String[1000];
                        imgpathArr = imageList.getImageList().get(i).getPreImgPath().split("\\,");

                        if (imageList.getImageList().get(i).getPreImgName() != null) {
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get(i).getPreImgName().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreLat() != null) {
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get(i).getPreLat().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreLongt() != null) {
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get(i).getPreLongt().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getPreImgTimeStamp() != null) {
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get(i).getPreImgTimeStamp().split("\\,");
                        }

                        if (imgpathArr != null && imgpathArr.length > 0) {
                            for (int j = 0; j < imgpathArr.length; j++) {

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if (timeArr != null && imgpathArr.length <= timeArr.length) {
                                    time = timeArr[j];
                                }

                                if (nameArr != null && imgpathArr.length <= nameArr.length) {
                                    name = nameArr[j];
                                }

                                if (latArr != null && imgpathArr.length <= latArr.length) {
                                    lat = latArr[j];
                                }

                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                               /* dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,WorkFlowUtils.DateTimeStamp(),time,1,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );*/

                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 1, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j], mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI
                                        , "0");
                            }
                        }
                    }

                    if (imageList.getImageList().get(i).getIMAGE_PATH() != null &&
                            imageList.getImageList().get(i).getIMAGE_PATH() != "") {
                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr = new String[1000];
                        imgpathArr = imageList.getImageList().get(i).getIMAGE_PATH().split("\\,");

                        if (imageList.getImageList().get(i).getIMAGENAME() != null) {
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get(i).getIMAGENAME().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getLATITUDE() != null) {
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get(i).getLATITUDE().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getLONGITUDE() != null) {
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get(i).getLONGITUDE().split("\\,");
                        }

                        if (imageList.getImageList().get(i).getImgTimeStamp() != null) {
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get(i).getImgTimeStamp().split("\\,");
                        }

                        if (imgpathArr != null && imgpathArr.length > 0) {
                            for (int j = 0; j < imgpathArr.length; j++) {

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if (timeArr != null && imgpathArr.length <= timeArr.length) {
                                    time = timeArr[j];
                                }

                                if (nameArr != null && imgpathArr.length <= nameArr.length) {
                                    name = nameArr[j];
                                }

                                if (latArr != null && imgpathArr.length <= latArr.length) {
                                    lat = latArr[j];
                                }

                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                                /*dataBaseHelper.insertImages(
                                        txnId,clId,"http://203.122.7.134:5100/images/"+imgpathArr[j],
                                        name,lat,longi,WorkFlowUtils.DateTimeStamp(),time,2,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI
                                );
                              */
                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 2, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j], mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI
                                        , "0");
                            }
                        }
                    }
                }
            }
            dataBaseHelper.close();
            if (Utils.isNetworkAvailable(getActivity())) {
                new CheckListDetailsTask(getActivity(), i, txnId, sId, scDate, dgType, activityId).execute();
            } else {
                Utils.toast(getActivity(), "17");
            }
            super.onPostExecute(result);
        }
    }

    private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String txnId, scDate, activityId, sId, dgType;
        BeanCheckListDetails PMCheckListDetails;

        private CheckListDetailsTask(Context con, Intent i, String txnId, String sId, String scDate, String dgType, String activityId) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.dgType = dgType;
            this.i = i;

        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("siteId", sId));
                nameValuePairs.add(new BasicNameValuePair("checkListType", "0"));
                nameValuePairs.add(new BasicNameValuePair("dgType", txnId));
                nameValuePairs.add(new BasicNameValuePair("status", "H"));
                nameValuePairs.add(new BasicNameValuePair("checkListDate", scDate));
                nameValuePairs.add(new BasicNameValuePair("languageCode", mAppPreferences.getLanCode()));

                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                Gson gson = new Gson();
                PMCheckListDetails = gson.fromJson(res, BeanCheckListDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
                PMCheckListDetails = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (PMCheckListDetails == null) {
                Utils.toast(getActivity(), "13");
            } else if (PMCheckListDetails.getPMCheckListDetail() != null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                dbHelper.open();
                dbHelper.clearReviewerCheclist();
                dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(), activityId);
                dbHelper.close();
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if (PMCheckListDetails.getPMCheckListDetail() != null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                startActivity(i);
            } else {
                Utils.toastMsg(getActivity(), "No Checklist Found.");
            }
            //startActivity(i);
            super.onPostExecute(result);
        }
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        List<ViewImage64> imageList = Collections.emptyList();
        Context context;

        public HorizontalAdapter(List<ViewImage64> imageList, Context context) {
            this.imageList = imageList;
            this.context = context;

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView delete, grid_image, play_video;
            TextView tv_tag, tv_time_stamp, tv_lati, tv_longi;


            public MyViewHolder(View view) {
                super(view);
                grid_image = (ImageView) view.findViewById(R.id.grid_image);
                delete = (ImageView) view.findViewById(R.id.delete);
                play_video = (ImageView) view.findViewById(R.id.play_video);
                tv_lati = (TextView) view.findViewById(R.id.tv_lati);
                tv_longi = (TextView) view.findViewById(R.id.tv_longi);
                tv_tag = (TextView) view.findViewById(R.id.tv_tag);
                tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);

            }
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tt_img, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.delete.setVisibility(View.GONE);
            holder.delete.setImageResource(R.drawable.delete_icon);
            holder.grid_image.setVisibility(View.GONE);
            holder.play_video.setVisibility(View.GONE);
            holder.tv_lati.setText(" ");
            holder.tv_longi.setText(" ");
            holder.tv_time_stamp.setText(" ");
            holder.tv_tag.setText(" ");

            if (imageList != null && imageList.size() > 0 && imageList.get(position).getPath() != null) {
                if (imageList.get(position).getName() != null) {
                    holder.tv_tag.setText(Utils.msg(getActivity(), "473") + " " + imageList.get(position).getName());
                } else {
                    holder.tv_tag.setText(Utils.msg(getActivity(), "473") + " ");
                }

                if (imageList.get(position).getTimeStamp() != null) {
                    holder.tv_time_stamp.setText(Utils.msg(getActivity(), "474") + " " + imageList.get(position).getTimeStamp());
                } else {
                    holder.tv_time_stamp.setText(Utils.msg(getActivity(), "474") + " ");
                }

                if (imageList.get(position).getLati() != null) {
                    holder.tv_lati.setText(Utils.msg(getActivity(), "215") + " : " + imageList.get(position).getLati());
                } else {
                    holder.tv_lati.setText(Utils.msg(getActivity(), "215") + " : ");
                }

                if (imageList.get(position).getLongi() != null) {
                    holder.tv_longi.setText(Utils.msg(getActivity(), "216") + " : " + imageList.get(position).getLongi());
                } else {
                    holder.tv_longi.setText(Utils.msg(getActivity(), "216") + " : ");
                }

                // Bitmap bm = null;
                String path = imageList.get(position).getPath();
                //File isfile = new File( path );

                if (path.contains("http")) {
                    if (path.contains(".jpeg") || path.contains(".JPEG")
                            || path.contains(".jpg") || path.contains(".JPG")
                            || path.contains(".png") || path.contains(".PNG")) {
                        holder.play_video.setTag("3");
                        holder.delete.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                        //loader.init(ImageLoaderConfiguration.createDefault(context));
                        //loader.displayImage(path, holder.grid_image, op, null);
                        Picasso.with(getActivity()).load(path).into(holder.grid_image);
                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.GONE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.stop_video);
                    }
                }
            }

            holder.play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaView(holder.play_video.getTag().toString(), imageList.get(position).getPath());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

    }

    public void mediaView(String flag, String urlPath) {
        if (flag.equals("4")) {
            Intent i = new Intent(getActivity(), ViewVideoWebView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        } else if (flag.equals("3")) {
            final Dialog nagDialog = new Dialog(getActivity(),
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(true);
            nagDialog.setContentView(R.layout.image_zoom);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(nagDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes(lp);
            Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
            ImageView imageView = (ImageView) nagDialog.findViewById(R.id.imageView1);
            //loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
            //loader.displayImage(urlPath, imageView, op, null);
            Picasso.with(getActivity()).load(urlPath).into(imageView);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        }
    }

    //110
    public boolean checkUser() {
        String id = Utils.msg(getContext(), "906");
        String[] group_id = mAppPreferences.getUserGroup().split(",");
        for (int i = 0; i < group_id.length; i++) {
            if (id.equals(group_id[i])) {
                return true;
            }
        }
        return false;
    }
    //110

    public void TTEnergyDescription() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("consumedpwr", response_ticket_details.getConsumedpwr());
        hashMap.put("gridpwr", response_ticket_details.getGridpwr());
        hashMap.put("ebvltg", response_ticket_details.getEbvltg());
        hashMap.put("genpwr", response_ticket_details.getGenpwr());
        hashMap.put("dgruntm", response_ticket_details.getDgruntm());
        hashMap.put("fuellvl", response_ticket_details.getFuellvl());
        hashMap.put("batsoc", response_ticket_details.getBatsoc());
        hashMap.put("batvltg", response_ticket_details.getBatvltg());
        hashMap.put("batterychamp", response_ticket_details.getBatterychamp());
        hashMap.put("batterydischamp", response_ticket_details.getBatterydischamp());
        hashMap.put("energySource", response_ticket_details.getEnergySource());


        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(hashMap);
        mAppPreferences.setTTEnergyDescription(hashMapString);
    }

    public void TTAccessDescription() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("actstdt", response_ticket_details.getActstdt());
        hashMap.put("actsttime", response_ticket_details.getActsttime());
        hashMap.put("actenddt", response_ticket_details.getActenddt());
        hashMap.put("actendtime", response_ticket_details.getActendtime());
        hashMap.put("accessid", response_ticket_details.getAccessid());
        hashMap.put("accreqsts", response_ticket_details.getAccreqsts());

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(hashMap);
        mAppPreferences.setTTAccessDescription(hashMapString);
    }
}