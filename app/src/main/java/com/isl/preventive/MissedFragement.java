package com.isl.preventive;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.GPSTracker;
import com.isl.itower.HomeActivity;
import com.isl.itower.NotificationList;
import com.isl.modal.AssetList;
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanSiteList;
import com.isl.modal.Operator;
import com.isl.notification.ShortcutBadger;
import com.isl.sparepart.schedule.AdapterSchedule;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import infozech.itower.R;
/**
 * Created by dhakan on 1/31/2019.
 */

public class MissedFragement extends Fragment {
    ListView sites_list;
    AppPreferences mAppPreferences;
    BeanSiteList response_ticket_list = null;
    DataBaseHelper db;
    Spinner sp_search_by;
    EditText et_searchby_site, et_sch_date,et_txn_id;
    Calendar myCalendar = Calendar.getInstance();
    AdapterSchedule adapterrr = null;
    TextView tv_operator, tv_search_by_logo,txt_no_ticket;
    Dialog actvity_dialog;
    ArrayList<String> list_operator_name, list_operator_id;
    ArrayList<Operator> operatorList;
    StringBuffer All_Operator_Id;
    MyCustomAdapter dataAdapter = null;
    int optCounter = 0;
    String moduleUrl = "";
    String latitude,longitude;
    View view;
    ArrayList<String> searchDDLlist;
    SwipeRefreshLayout pullToRefresh;
    private FusedLocationProviderClient fusedLocationClient;
    private NetworkManager networkManager;//108
    public MissedFragement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_site_list,container, false);
        networkManager = new NetworkManager();//108
        RelativeLayout rl_header_ticket_list = (RelativeLayout) view.findViewById(R.id.rl_header_ticket_list);
        rl_header_ticket_list.setVisibility(View.GONE);
        txt_no_ticket = (TextView) view.findViewById(R.id.txt_no_ticket);
        tv_search_by_logo = (TextView) view.findViewById(R.id.tv_search_by_logo);
        Utils.msgText(getActivity(), "225", txt_no_ticket);
        Utils.msgText(getActivity(), "296", tv_search_by_logo);
        db = new DataBaseHelper(getActivity());
        db.open();
        moduleUrl = db.getModuleIP("Preventive");
        db.close();
        tv_operator = (TextView) view.findViewById(R.id.tv_operator);
        tv_operator.setBackgroundResource(R.drawable.doted);
        sp_search_by = (Spinner) view.findViewById(R.id.sp_search_by);
        sp_search_by.setBackgroundResource(R.drawable.doted);
        et_searchby_site = (EditText) view.findViewById(R.id.et_searchby_site);
        et_searchby_site.setBackgroundResource(R.drawable.input_box );
        et_sch_date = (EditText) view.findViewById(R.id.et_sch_date);

        et_txn_id = (EditText) view.findViewById(R.id.et_txn_id);
        et_txn_id.setBackgroundResource(R.drawable.input_box );

        searchFunction();
        mAppPreferences = new AppPreferences(getActivity());
        tv_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActivityPopup();
            }
        });

        sites_list = (ListView) view.findViewById(R.id.lv_sites);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        //if (Utils.isNetworkAvailable(getActivity())) {
        if (mAppPreferences.getTicketFrmNtBr().equalsIgnoreCase("1")) {
        } else {
            db.open();
            db.deleteNotification(mAppPreferences.getTicketFrmNtBr(), mAppPreferences.getUserId());
            db.close();
        }
        db.open();
        if (Utils.isNetworkAvailable(getActivity())) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token ).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask( getActivity() ).execute();
                }
            },100);
        }else {
            if(db.missSiteCount()>0){
                response_ticket_list = db.missedSite("", "A");
                if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                    adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"M");
                    sites_list.setAdapter(adapterrr);
                    txt_no_ticket.setVisibility(View.GONE);
                }
            }else{
                txt_no_ticket.setVisibility(View.VISIBLE);
                sites_list.setAdapter(null);
            }
        }
        db.close();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask( getActivity() ).execute();
                    searchFunction();
                } else {
                    Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
                pullToRefresh.setRefreshing(false);
            }
        });



        sites_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int selectedPos,long arg3) {
                int alreadyJson = 0;
                db.open();
                String missed_tab_rights = db.getSubMenuRight("Missed", "Preventive");
                if (missed_tab_rights.contains("A")) {
                    if(isUserLocation()){
                        if (response_ticket_list.getSite_list().get(selectedPos).getAssetFlag()!=null
                                && response_ticket_list.getSite_list().get(selectedPos).getAssetFlag().length()>0
                                && response_ticket_list.getSite_list().get(selectedPos)
                                .getAssetFlag().equalsIgnoreCase("1")) {
                            activity(selectedPos,1);
                        }else {
                            activity(selectedPos,0);
                        }

                       /* if (Utils.isNetworkAvailable(getContext())) {
                            new getScheduleEquipment(getActivity(),selectedPos).execute();
                        }
                       */


                   /* if (db.isAlreadyAutoSaveChk(response_ticket_list.getSite_list().get(arg2).getTXN_ID()) == 0) {
                        db.insertAutoSaveChkList(response_ticket_list.getSite_list().get(arg2).getTXN_ID(),
                                "", "", "");
                        alreadyJson = 1;
                    }
                    activity(arg2, alreadyJson);*/
                    }
                }
                db.close();
            }
        });
        return view;
    }

    public void activity(final int selectedPos,int count) {
        HashMap<String, String> tranData = new HashMap<String, String>();

        HashMap<String, String> valData = new HashMap<String, String>();
        valData.put("DEFAULT","");

        HashMap<String, String> readingData = new HashMap<String, String>();
        readingData.put("DEFAULT","0");

        if(response_ticket_list.getSite_list().get(selectedPos).getLtstRdng()!=null){
            String[] split1 = response_ticket_list.getSite_list().get(selectedPos).getLtstRdng().split( "\\," );
            if(split1.length>0) {
                for (int i = 0; i < split1.length; i++) {
                    String[] split2 = split1[i].split( "\\~" );
                    if(split2.length>1) {
                        readingData.put( split2[0], split2[1] );
                    }
                }
            }
        }

        if(response_ticket_list.getSite_list().get(selectedPos).getVal()!=null) {
            String[] split = response_ticket_list.getSite_list().get( selectedPos ).getVal().split( "\\~" );
            if(split.length>0) {
                for(int i = 0; i<split.length; i++){
                    valData.put(split[i],"");
                }
            }
        }
        mAppPreferences.setPMchecklist("");
        mAppPreferences.setPMchecklistBackUp("");
        Intent i;
        if(count>0){
            i = new Intent( getActivity(), EquipmentList.class );
        }else{
            i = new Intent(getActivity(), PMChecklist.class);
        }
        i.putExtra("qrCode", "");
        i.putExtra("scheduledDate",response_ticket_list.getSite_list().get(selectedPos).getSCHEDULE_DATE());
        i.putExtra("S","M");
        i.putExtra("siteId", response_ticket_list.getSite_list().get(selectedPos).getSITE_ID());
        i.putExtra("siteName", response_ticket_list.getSite_list().get(selectedPos).getSidName());
        i.putExtra("activityTypeId",response_ticket_list.getSite_list().get(selectedPos).getPARAM_ID());
        i.putExtra("paramName", response_ticket_list.getSite_list().get(selectedPos).getPARAM_NAME());
        i.putExtra("Status", response_ticket_list.getSite_list().get(selectedPos).getACTIVITY_STATUS());
        i.putExtra("dgType", response_ticket_list.getSite_list().get(selectedPos).getDG_TYPE());
        i.putExtra("txn", response_ticket_list.getSite_list().get(selectedPos).getTXN_ID());
        i.putExtra("etsSid", response_ticket_list.getSite_list().get(selectedPos).getEtsSid());
        i.putExtra("preMinImage", "0");
        i.putExtra("preMaxImage", "0");
        i.putExtra("postMinImage", "0");
        i.putExtra("postMaxImage", "0");
        i.putExtra("imageName", "");
        i.putExtra( "rvDate", "");
        i.putExtra( "rejRmks", "");
        i.putExtra( "rCat", "");
        i.putExtra( "latitude", latitude);
        i.putExtra( "longitude", longitude);
        i.putExtra("valData", valData);
        i.putExtra("readingData", readingData);
        if(count>0){
            startActivity(i);
        }else{
            i.putExtra("assetId", "0");
            i.putExtra("equipmentId", "0");
            if (Utils.isNetworkAvailable(getActivity())) {
                new GetPMCheckList(getActivity(),i,response_ticket_list.getSite_list().get(selectedPos).getSITE_ID(),
                        response_ticket_list.getSite_list().get(selectedPos).getPARAM_ID(),
                        response_ticket_list.getSite_list().get(selectedPos).getTXN_ID(),selectedPos).execute();
            } else if(response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()!=null
                    && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag().length()>0
                    && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()
                    .equalsIgnoreCase("1"))
            {
                if(response_ticket_list.getSite_list().get( selectedPos).getPullDate()!=null &&
                        response_ticket_list.getSite_list().get( selectedPos).getPullDate().length()>0){
                    overallDate(0,response_ticket_list.getSite_list().get( selectedPos).getPullDate()
                            ,selectedPos,i);
                }else{
                    pullDate(0,selectedPos,i,"");
                }
            } else {
                startActivity(i);
            }

        }




        /*if (Utils.isNetworkAvailable(getActivity())) {
            new GetPMCheckList(getActivity(),i,response_ticket_list.getSite_list().get(selectedPos).getSITE_ID(),
                    response_ticket_list.getSite_list().get(selectedPos).getPARAM_ID(),
                    response_ticket_list.getSite_list().get(selectedPos).getTXN_ID(),selectedPos).execute();
        } else if(response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()!=null
                && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag().length()>0
                && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()
                .equalsIgnoreCase("1"))
        {
              if(response_ticket_list.getSite_list().get( selectedPos).getPullDate()!=null &&
                    response_ticket_list.getSite_list().get( selectedPos).getPullDate().length()>0){
                overallDate(0,response_ticket_list.getSite_list().get( selectedPos).getPullDate()
                        ,selectedPos,i);
              }else{
                pullDate(0,selectedPos,i,"");
              }
        } else {
            startActivity(i);
        }*/
    }

   /* private void alert(final int arg2, final String preMin,final String preMax, final String postMin, final String postMax,
                       final String imageName,final int alreadyJson) {
        //isImgActivityInProgress = false;
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById( R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        title.setTypeface( Utils.typeFace(getActivity()));
        title.setText(Utils.msg(getActivity(), "163") + " " + preMin + " "
                + Utils.msg(getActivity(), "300"));
        *//*title.setText( Utils.msg( getActivity(), "228" ) + " " + tag );*//*

        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        positive.setTypeface( Utils.typeFace( getActivity()) );
        positive.setText( Utils.msg( getActivity(), "7" ) );

        positive.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                activity(arg2, preMin, preMax, postMin, postMax, imageName,alreadyJson);

            }
        });

        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        negative.setVisibility(View.GONE);
        }*/

    private void pullDate(int flag,int selectedPos,Intent i,String msg) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.generator_status_popup);
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        tv_title.setTypeface( Utils.typeFace(getActivity()));
        tv_title.setText(Utils.msg(getActivity(), "739"));

        Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
        iv_cross.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    //  new PMGridTask( getActivity() ).execute();
                    searchFunction();
                }
            }
        });

        TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
        Utils.textViewProperty(getActivity(),tv_sid,Utils.msg(getActivity(),"77"));

        EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
        Utils.editTextProperty(getActivity(),et_sid);
        et_sid.setEnabled( false );
        et_sid.setText(response_ticket_list.getSite_list().get(selectedPos).getSITE_ID());

        TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
        Utils.textViewProperty(getActivity(),tv_aid,Utils.msg(getActivity(),"740"));

        EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
        Utils.editTextProperty(getActivity(),et_aid);
        et_aid.setEnabled( false );
        et_aid.setText(response_ticket_list.getSite_list().get(selectedPos).getPARAM_NAME());

        TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
        Utils.textViewProperty(getActivity(),tv_pull_date,Utils.msg(getActivity(),"743"));
        tv_pull_date.setVisibility(View.GONE);
        EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
        Utils.editTextProperty(getActivity(),et_pull_date);
        et_pull_date.setEnabled( false );
        et_pull_date.setText("");
        et_pull_date.setVisibility(View.GONE);

        TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
        Utils.textViewProperty(getActivity(),tv_overall_date,Utils.msg(getActivity(),"744"));
        tv_overall_date.setVisibility(View.GONE);
        EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
        Utils.editTextProperty(getActivity(),et_overall_date);
        et_overall_date.setEnabled( false );
        et_overall_date.setText("");
        et_overall_date.setVisibility(View.GONE);


        Button next = (Button) actvity_dialog.findViewById( R.id.bt_next );
        next.setTypeface( Utils.typeFace( getActivity()) );
        next.setText( Utils.msg( getActivity(), "742" ) );
        next.setVisibility(View.GONE);

        TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
        Utils.textViewProperty(getActivity(),tv_checklist,"");
        tv_checklist.setVisibility(View.GONE);

        Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
        add.setTypeface( Utils.typeFace( getActivity()) );
        add.setText( Utils.msg( getActivity(), "204" ) );

        TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
        Utils.textViewProperty(getActivity(),tv_gen_status,Utils.msg(getActivity(),"741"));

        Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
        sp_gen_status.setBackgroundResource(R.drawable.doted);
        Utils.spinnerProperty(getActivity(),sp_gen_status);
        db.open();
        ArrayList<String> list = db.getPreventiveParamName("1195","654","1");
        addItemsOnSpinner(sp_gen_status,list);
        db.close();

        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    actvity_dialog.cancel();
                    new UpdateGeneratorStatusTask(getActivity(),Utils.CurrentDate(2),""
                            ,selectedPos,1,i).execute();
                } else {
                    Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                overallDate(0,Utils.CurrentDate(2),selectedPos,i);
            }
        });

        if(flag == 1){
            tv_checklist.setVisibility(View.VISIBLE);
            tv_checklist.setText(""+msg);
            next.setVisibility(View.VISIBLE);
            tv_pull_date.setVisibility(View.VISIBLE);
            et_pull_date.setVisibility(View.VISIBLE);
            et_pull_date.setText(""+Utils.CurrentDate(2));
            add.setVisibility(View.GONE);
        }

    }

    private void overallDate(int flag,String pullDate, int selectedPos,Intent i) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.generator_status_popup);
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        TextView tv_title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        tv_title.setTypeface( Utils.typeFace(getActivity()));
        tv_title.setText(Utils.msg(getActivity(), "739"));

        TextView tv_checklist = (TextView) actvity_dialog.findViewById( R.id.tv_checklist );
        Utils.textViewProperty(getActivity(),tv_checklist,Utils.msg(getActivity(),"745"));
       /* tv_checklist.setTypeface( Utils.typeFace(getActivity()));
        tv_checklist.setTextColor(getActivity().getResources().getColor(R.color.textcolor));
        tv_checklist.setTextSize(15);*/

        Button iv_cross = (Button) actvity_dialog.findViewById( R.id.iv_cross );
        iv_cross.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            //108
                            networkManager.getToken(new NetworkManager.TokenCallback() {
                                @Override
                                public void onTokenReceived(String token) {
                                    new PMGridTask( getActivity(),token).execute();
                                }

                                @Override
                                public void onTokenError(String error) {
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            });
                            //108
                            //  new PMGridTask( getActivity() ).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    //   new PMGridTask( getActivity() ).execute();
                    searchFunction();
                }
            }
        });

        TextView tv_sid = (TextView) actvity_dialog.findViewById( R.id.tv_sid );
        Utils.textViewProperty(getActivity(),tv_sid,Utils.msg(getActivity(),"77"));

        EditText et_sid = (EditText) actvity_dialog.findViewById( R.id.et_sid );
        Utils.editTextProperty(getActivity(),et_sid);
        et_sid.setEnabled( false );
        et_sid.setText(response_ticket_list.getSite_list().get(selectedPos).getSITE_ID());

        TextView tv_aid = (TextView) actvity_dialog.findViewById( R.id.tv_aid );
        Utils.textViewProperty(getActivity(),tv_aid,Utils.msg(getActivity(),"740"));

        EditText et_aid = (EditText) actvity_dialog.findViewById( R.id.et_aid );
        Utils.editTextProperty(getActivity(),et_aid);
        et_aid.setEnabled( false );
        et_aid.setText(response_ticket_list.getSite_list().get(selectedPos).getPARAM_NAME());

        TextView tv_pull_date = (TextView) actvity_dialog.findViewById( R.id.tv_pull_date );
        Utils.textViewProperty(getActivity(),tv_pull_date,Utils.msg(getActivity(),"743"));
        EditText et_pull_date = (EditText) actvity_dialog.findViewById( R.id.et_pull_date );
        Utils.editTextProperty(getActivity(),et_pull_date);
        et_pull_date.setEnabled( false );
        et_pull_date.setText(pullDate);

        TextView tv_overall_date = (TextView) actvity_dialog.findViewById( R.id.tv_overall_date );
        Utils.textViewProperty(getActivity(),tv_overall_date,Utils.msg(getActivity(),"744"));
        tv_overall_date.setVisibility(View.GONE);
        EditText et_overall_date = (EditText) actvity_dialog.findViewById( R.id.et_overall_date );
        Utils.editTextProperty(getActivity(),et_overall_date);
        et_overall_date.setEnabled( false );
        et_overall_date.setText("");
        et_overall_date.setVisibility(View.GONE);

        Button no = (Button) actvity_dialog.findViewById( R.id.bt_next );
        no.setTypeface( Utils.typeFace( getActivity()) );
        no.setText( Utils.msg( getActivity(), "742" ) );
        no.setVisibility(View.GONE);

        Button add = (Button) actvity_dialog.findViewById( R.id.bt_save );
        add.setTypeface( Utils.typeFace( getActivity()) );
        add.setText( Utils.msg( getActivity(), "284" ) );

        TextView tv_gen_status = (TextView) actvity_dialog.findViewById( R.id.tv_gen_status );
        Utils.textViewProperty(getActivity(),tv_gen_status,Utils.msg(getActivity(),"741"));

        Spinner sp_gen_status = (Spinner) actvity_dialog.findViewById( R.id.sp_gen_status);
        sp_gen_status.setBackgroundResource(R.drawable.doted);
        Utils.spinnerProperty(getActivity(),sp_gen_status);
        db.open();
        ArrayList<String> list = db.getPreventiveParamName("1195","654","2");
        addItemsOnSpinner(sp_gen_status,list);
        db.close();

        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(flag==1){
                    actvity_dialog.cancel();
                    if (Utils.isNetworkAvailable(getActivity())) {
                        //108
                        networkManager.getToken(new NetworkManager.TokenCallback() {
                            @Override
                            public void onTokenReceived(String token) {
                                new PMGridTask( getActivity(),token).execute();
                            }

                            @Override
                            public void onTokenError(String error) {
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //108
                        // new PMGridTask( getActivity() ).execute();
                        searchFunction();
                    }
                    startActivity(i);
                }else{
                    if (Utils.isNetworkAvailable(getActivity())) {
                        actvity_dialog.cancel();
                        new UpdateGeneratorStatusTask(getActivity(),pullDate,Utils.CurrentDate(2),
                                selectedPos,2,i).execute();
                    } else {
                        Toast.makeText(getActivity(),"No internet connection,Try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(flag == 1){
            tv_overall_date.setVisibility(View.VISIBLE);
            et_overall_date.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            //tv_gen_status.setVisibility(View.GONE);
            //sp_gen_status.setVisibility(View.GONE);
            et_overall_date.setText(""+Utils.CurrentDate(2));
            add.setText( Utils.msg( getActivity(), "63" ));
            no.setText( Utils.msg( getActivity(), "64" ));
            tv_checklist.setVisibility(View.VISIBLE);
        }

        no.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (Utils.isNetworkAvailable(getActivity())) {
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new PMGridTask( getActivity(),token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                    // new PMGridTask(getActivity()).execute();
                    searchFunction();
                }
            }
        });
    }




    public class PMGridTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String token; //108

        public PMGridTask(Context con,String token) {
            this.con = con;
            this.token = token; //108
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = "";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("userID",mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("type","TILL_YESTERDAY"));
                nameValuePairs.add(new BasicNameValuePair("siteID", ""));
                nameValuePairs.add(new BasicNameValuePair("activityTypeFlag","1"));
                String serUrl = "";
                if(moduleUrl.equalsIgnoreCase("0")){
                    serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_getScheduled_Sites;
                }else{
                    serUrl=moduleUrl+ WebMethods.url_getScheduled_Sites;
                }
                String response = Utils.httpPostRequest1(con,serUrl, nameValuePairs,token); //108
                Gson gson = new Gson();
                response_ticket_list = gson.fromJson(response,BeanSiteList.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_ticket_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response_ticket_list == null) {
                // Toast.makeText(MissedList.this,
                Utils.toast(getActivity(), "13");
            } else if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                // 0.1 Start
                db.open();
                db.clearMissedList();
                db.insertMissedList(response_ticket_list);
                db.close();
                // 0.1 End
                sites_list.setAdapter(new AdapterSchedule(getActivity(),response_ticket_list,"M"));
                txt_no_ticket.setVisibility(View.GONE);
            } else {
                db.open();
                db.clearMissedList();
                db.close();
                txt_no_ticket.setVisibility(View.VISIBLE);
                sites_list.setAdapter(null);
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
    }


    public void onBackPressed() {
        if (mAppPreferences.getBackModeNotifi45() == 2) {
            Intent i = new Intent(getActivity(), NotificationList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            getActivity().finish();
        } else {
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ArrayList<String> notification_counter = new ArrayList<String>();
        db.open();
        notification_counter = db.getNotificationCount(
                mAppPreferences.getUserId(), "0");
        db.close();
        ShortcutBadger.removeCount(getActivity());
        if (notification_counter.size() > 0) {
            ShortcutBadger.applyCount(getActivity(),
                    notification_counter.size());
        }
    }

    // 0.1 Start
    public void searchFunction() {
        searchDDLlist = new ArrayList<String>();
        searchDDLlist.add("Site Id");
        searchDDLlist.add("Activity Type");
        searchDDLlist.add("Schedule Date");
        searchDDLlist.add("Activity ID");
        addItemsOnSpinner(sp_search_by, searchDDLlist);
        //setupUI(sp_search_by);

        sp_search_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                et_searchby_site.setText("");
                et_sch_date.setText("");
                et_txn_id.setText("");
                optCounter = 0;
                tv_operator.setText("Select Activity Type");
                hideKeyBoardEdt(sp_search_by);
                if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Site Id")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.VISIBLE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Activity Type")) {
                    tv_operator.setVisibility(View.VISIBLE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Schedule Date")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.VISIBLE);
                    et_txn_id.setVisibility(View.GONE);
                } else if (sp_search_by.getSelectedItem().toString()
                        .equalsIgnoreCase("Activity ID")) {
                    tv_operator.setVisibility(View.GONE);
                    et_searchby_site.setVisibility(View.GONE);
                    et_sch_date.setVisibility(View.GONE);
                    et_txn_id.setVisibility(View.VISIBLE);
                }
                adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"M");
                sites_list.setAdapter(adapterrr);
                operatorList = new ArrayList<Operator>();
                All_Operator_Id = new StringBuffer();
                db.open();
                list_operator_id = new ArrayList<String>();
                list_operator_name = new ArrayList<String>();
                list_operator_name = db.getInciParam1("20",tv_operator,"654");
                for (int i = 0; i < list_operator_name.size(); i++) {
                    if (!list_operator_name.get(i).equalsIgnoreCase(
                            "Please Select")) {
                        Operator oper = new Operator(db.getInciParamId("20",
                                list_operator_name.get(i),"654"), list_operator_name
                                .get(i), false);
                        operatorList.add(oper);
                    }
                }
                db.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        et_searchby_site.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                searchData("I");
            }
        });

        et_txn_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                searchData("T");
            }
        });

        et_sch_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_sch_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // String text = et_sch_date.getText().toString();
                // adapterrr.filterbydate(text);
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.YEAR, year);
            updateLabel();
        }

    };

    public void searchData(String flag){
        db.open();
        String cs = "";
        if(flag.equalsIgnoreCase( "T" )){
            cs = et_txn_id.getText().toString().toLowerCase( Locale.getDefault());
        }else{
            cs = et_searchby_site.getText().toString().toLowerCase( Locale.getDefault());
        }
        response_ticket_list = db.missedSite(cs,flag);
        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"M");
            sites_list.setAdapter(adapterrr);
            txt_no_ticket.setVisibility(View.GONE);
        } else {
            txt_no_ticket.setVisibility(View.VISIBLE);
            sites_list.setAdapter(null);
        }
        db.close();
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy-hh-mm-ss"; // In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        et_sch_date.setText(sdf.format(myCalendar.getTime()));
        db.open();
        String cs = et_sch_date.getText().toString()
                .toLowerCase(Locale.getDefault());
        response_ticket_list = db.missedSite(cs, "D");
        if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
            adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"M");
            sites_list.setAdapter(adapterrr);
            txt_no_ticket.setVisibility(View.GONE);
        } else {
            txt_no_ticket.setVisibility(View.VISIBLE);
            sites_list.setAdapter(null);
        }
        db.close();
    }

    // end 0.1
    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    public void ActivityPopup() {
        optCounter = 0;
        All_Operator_Id.setLength(0);
        actvity_dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.activity_type_popup);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        final ListView list_view = (ListView) actvity_dialog.findViewById(R.id.list_view);
        TextView apply = (TextView) actvity_dialog.findViewById(R.id.tv_apply);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setText("Actvity Type");

        ImageView iv_cancel = (ImageView) actvity_dialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });

        dataAdapter = new MyCustomAdapter(getActivity(),R.layout.custom_operator, operatorList);
        list_view.setAdapter(dataAdapter);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<Operator> OperatorList = dataAdapter.countryList;
                for (int i = 0; i < OperatorList.size(); i++) {
                    Operator operator = OperatorList.get(i);
                    if (operator.isSelected()) {
                        optCounter++;
                        if (optCounter > 1) {
                            All_Operator_Id.append(",");
                        }
                        All_Operator_Id.append(operatorList.get(i).getCode());
                    }
                }

                if (optCounter == 0) {
                    tv_operator.setText("Select Activity Type");
                    searchData("I");
                } else {
                    tv_operator.setText("" + optCounter + " Activity Selected");
                }
                actvity_dialog.dismiss();
                if (All_Operator_Id.length() > 0) {
                    db.open();
                    response_ticket_list = db.missedSite(
                            All_Operator_Id.toString(), "aID");
                    if (response_ticket_list.getSite_list()!=null && response_ticket_list.getSite_list().size() > 0) {
                        adapterrr = new AdapterSchedule(getActivity(),response_ticket_list,"M");
                        sites_list.setAdapter(adapterrr);
                        txt_no_ticket.setVisibility(View.GONE);
                    } else {
                        txt_no_ticket.setVisibility(View.VISIBLE);
                        sites_list.setAdapter(null);
                    }
                    db.close();
                }
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Operator> {
        private ArrayList<Operator> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Operator> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Operator>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.custom_operator, null);
                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);
                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Operator country = (Operator) cb.getTag();
                        country.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Operator country = countryList.get(position);
            holder.code.setText("");
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);
            return convertView;
        }
    }

    public void  hideKeyBoardEdt(Spinner edt) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    // Class to call Web Service to get PM CheckList to draw form.
    private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String siteId = "",chklistType = "0",txnId="";
        BeanCheckListDetails PMCheckList;
        int selectedPos = 0;

        private GetPMCheckList(Context con,Intent i,String siteId,String chklistType,
                               String txnId,int selectedPos) {
            this.con = con;
            this.i = i;
            this.siteId = siteId;
            this.chklistType = chklistType;
            this.txnId = txnId;
            this.selectedPos = selectedPos;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Loading..." );
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
            Gson gson = new Gson();
            nameValuePairs.add( new BasicNameValuePair( "siteId",siteId));//
            nameValuePairs.add( new BasicNameValuePair( "checkListType",chklistType)); // 0 means all checklist(20001,20002,20005...) data download
            nameValuePairs.add( new BasicNameValuePair( "checkListDate",response_ticket_list.getSite_list().get(selectedPos).getSCHEDULE_DATE() ) );
            nameValuePairs.add( new BasicNameValuePair( "status", "S")); //S or M get blank checklistdata
            nameValuePairs.add( new BasicNameValuePair( "dgType", "" ) );
            nameValuePairs.add( new BasicNameValuePair( "languageCode", mAppPreferences.getLanCode() ) );
            try {
                String url = "";
                if (moduleUrl.equalsIgnoreCase( "0" )) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest( con, url, nameValuePairs );
                PMCheckList = gson.fromJson( res, BeanCheckListDetails.class );
            } catch (Exception e) {
                //e.printStackTrace();
                PMCheckList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (PMCheckList == null) {
                // Toast.makeText(PMChecklist.this,"PM CheckList not available.Pls contact system admin.",Toast.LENGTH_LONG).show();
                Utils.toast(getActivity(),"226");
            } else if (PMCheckList != null) {
                if (PMCheckList.getPMCheckListDetail()!=null && PMCheckList.getPMCheckListDetail().size() > 0) {
                    db.open();
                    //1.1
                    int alreadyJson = 0;
                    if (db.isAlreadyAutoSaveChk(response_ticket_list.getSite_list().get(selectedPos).getTXN_ID(),"0") == 0) {
                        db.insertAutoSaveChkList(response_ticket_list.getSite_list().get(selectedPos).getTXN_ID(),
                                "", "", "","0");
                        alreadyJson = 1;
                    }

                    db.clearCheckList("655",chklistType,"0");
                    db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),"655",
                            alreadyJson,txnId,getActivity(),"0","0");
                    db.close();
                }
            } else {
                Utils.toast(getActivity(),"13");
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if(response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()!=null
                    && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag().length()>0
                    && response_ticket_list.getSite_list().get( selectedPos).getPopupFlag()
                    .equalsIgnoreCase("1"))
            {
                if(response_ticket_list.getSite_list().get( selectedPos).getPullDate()!=null &&
                        response_ticket_list.getSite_list().get( selectedPos).getPullDate().length()>0){
                    overallDate(0,response_ticket_list.getSite_list().get( selectedPos).getPullDate()
                            ,selectedPos,i);
                }else{
                    pullDate(0,selectedPos,i,"");
                }
            }
            else{
                startActivity(i);
            }
            super.onPostExecute( result );
        }
    }

    public class UpdateGeneratorStatusTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        int selectPos =0;
        String res;
        int pDsec = 1;
        String pullDate,overallDate;
        Intent i;
        public UpdateGeneratorStatusTask(Context con,String pullDate,String overallDate,int selectPos,int pDsec,Intent i) {
            this.con = con;
            this.selectPos = selectPos;
            this.pDsec = pDsec;
            this.i = i;
            this.pullDate = pullDate;
            this.overallDate = overallDate;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(16);
                nameValuePairs.add( new BasicNameValuePair("tranId",
                        response_ticket_list.getSite_list().get(selectPos).getTXN_ID()));
                nameValuePairs.add( new BasicNameValuePair("siteId",
                        response_ticket_list.getSite_list().get(selectPos).getSITE_ID()));
                nameValuePairs.add( new BasicNameValuePair("actType",
                        response_ticket_list.getSite_list().get(selectPos).getPARAM_ID()));
                nameValuePairs.add( new BasicNameValuePair("scheduleDate",
                        response_ticket_list.getSite_list().get(selectPos).getSCHEDULE_DATE()));
                nameValuePairs.add( new BasicNameValuePair("assgnUser",""));
                nameValuePairs.add( new BasicNameValuePair("assgnGrp",""));
                nameValuePairs.add( new BasicNameValuePair("reviewUser",""));
                nameValuePairs.add( new BasicNameValuePair("reviewGrp",""));
                nameValuePairs.add( new BasicNameValuePair("flag","G" ) );
                nameValuePairs.add( new BasicNameValuePair( "dgType", "0"));
                nameValuePairs.add( new BasicNameValuePair( "userId", mAppPreferences.getUserId()));
                nameValuePairs.add( new BasicNameValuePair( "pmStatus", "S"));
                nameValuePairs.add( new BasicNameValuePair( "reviewDate", ""));
                nameValuePairs.add( new BasicNameValuePair( "maxAccessDuration",""));
                nameValuePairs.add( new BasicNameValuePair( "no_of_pm_visit",""));
                nameValuePairs.add( new BasicNameValuePair( "siteAutoAccess", ""));
                nameValuePairs.add( new BasicNameValuePair( "maxAccessDuration_audit",""));
                nameValuePairs.add( new BasicNameValuePair( "no_of_pm_visit_audit",""));
                nameValuePairs.add( new BasicNameValuePair( "siteAutoAccess_audit", ""));
                nameValuePairs.add( new BasicNameValuePair( "generator_status",""+pDsec));
                nameValuePairs.add( new BasicNameValuePair( "pull_date",pullDate));
                nameValuePairs.add( new BasicNameValuePair( "overall_date",overallDate));
                String url = "";
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_add_activity;
                }else{
                    url=moduleUrl+ WebMethods.url_add_activity;
                }
                res = Utils.httpPostRequest(con , url , nameValuePairs);
                res="{response :"+res+"}";
            } catch (Exception e) {
                e.printStackTrace();
                res = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String success = "";
            String msg = "";
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (res != null && res.length()>0) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("response");
                    for(int i= 0;i<subArray.length();i++){
                        success = subArray.getJSONObject(i).getString("success");
                        msg = subArray.getJSONObject(i).getString("message");
                    }
                    Toast.makeText(con,msg,Toast.LENGTH_SHORT).show();
                    if (success.equals("true") && pDsec == 1) {
                        pullDate(1,selectPos,i,msg);
                    }else if (success.equals("true") && pDsec == 2){
                        overallDate(1,pullDate,selectPos,i);
                    }else{
                        if (pDsec == 1) {
                            pullDate(0,selectPos,i,"");
                        }else if (pDsec == 2){
                            overallDate(0,pullDate,selectPos,i);
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(con,e.getMessage(),Toast.LENGTH_SHORT).show();
                    if (pDsec == 1) {
                        pullDate(0,selectPos,i,"");
                    }else if (pDsec == 2){
                        overallDate(0,pullDate,selectPos,i);
                    }
                }
            }else {
                //if no responce then reopen popup
                Utils.toast(getActivity(), "13");
                if (pDsec == 1) {
                    pullDate(0,selectPos,i,"");
                }else if (pDsec == 2){
                    overallDate(0,pullDate,selectPos,i);
                }

            }
            super.onPostExecute(result);
        }
    }

    private boolean isUserLocation() {
        boolean status = true;
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (!Utils.hasPermissions(getActivity(), AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(getActivity(), "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
            return false;
        } else if (gps.canGetLocation() == true) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } /*else {
                        latitude = String.valueOf(gps.getLatitude());
                        longitude = String.valueOf(gps.getLongitude());
                    }*/
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //latitude = String.valueOf(gps.getLatitude());
                        //longitude = String.valueOf(gps.getLongitude());
                    }
                });
            }
            //latitude = String.valueOf(gps.getLatitude());
            //longitude = String.valueOf(gps.getLongitude());
            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(getActivity(), "252");
                return false;
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        } /*else {
                        latitude = String.valueOf(gps.getLatitude());
                        longitude = String.valueOf(gps.getLongitude());
                    }*/
                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //latitude = String.valueOf(gps.getLatitude());
                            //longitude = String.valueOf(gps.getLongitude());
                        }
                    });
                }

                //latitude = String.valueOf(gps.getLatitude());
                //longitude = String.valueOf(gps.getLongitude());
            }
        }
        return true;
    }

    public class getScheduleEquipment extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        AssetList asset_list;
        String serUrl  ="";
        int selectedPos;
        public getScheduleEquipment(Context con,final int selectedPos) {
            this.con = con;
            this.selectedPos = selectedPos;

        }
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = "";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("txnId",response_ticket_list.getSite_list().get(selectedPos).getTXN_ID()));
                nameValuePairs.add(new BasicNameValuePair("equipId","-1"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_get_AssetList;
                }else{
                    serUrl=moduleUrl+ WebMethods.url_get_AssetList;
                }
                String response_site = Utils.httpPostRequest(con,serUrl, nameValuePairs);
                Gson gson = new Gson();
                asset_list = gson.fromJson(response_site, AssetList.class);
            } catch (Exception e) {
                e.printStackTrace();
                asset_list = null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }

            if (asset_list == null)
            {
                Utils.toast(getActivity(), "13");
            }else if (asset_list.getAseetList()!=null && asset_list.getAseetList().size() > 0) {
                activity(selectedPos,1);
            }else {
                activity(selectedPos,0);
            }
            super.onPostExecute(result);
        }
    }

}