package com.isl.itower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.isl.api.ApiClient;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.app.auth.AuthStateManager;
import com.isl.app.auth.TokenActivity;
import com.isl.audit.util.NetworkChangeReceiver;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.home.module.AboutUs;
import com.isl.home.module.HomeFragement;
import com.isl.home.module.NotificationListFrag;
import com.isl.home.module.NotificationSettingFrag;
import com.isl.home.module.PendingFrag;
import com.isl.home.module.PolicyFragement;
import com.isl.home.module.RejectFrag;
import com.isl.modal.MultiLanguageList;
import com.isl.modal.ResponceLoginList;
import com.isl.modal.Response;
import com.isl.modal.ServiceResponce;
import com.isl.notification.ShortcutBadger;
import com.isl.userTracking.PunchInNotificationService;
import com.isl.userTracking.userttracking.LocationService;
import com.isl.util.HttpUtils;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;
import com.isl.workflow.constant.Constants;

import net.openid.appauth.AuthState;

import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import infozech.itower.R;

/*
Modified By : Dhakan Lal on 22 Sep, 2021
Purpose     : logout from ad
Version     : 1.0
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tv_logo, tv_home, tv_pending, tv_reject, tv_notification, tv_notification_setting, tv_logout, tv_about,tv_privacy, tv_change_password,
            tv_change_lang, tv_select_lang, userName, lastlogin, tv_syn,tv_syn_pending;//0.4
    ImageView notification;
    AppPreferences mAppPreferences;
    ResponceLoginList response = null;
    private Fragment mContent;
    ArrayList<String> notification_list;
    Dialog changePassPop;
    int front_mode = 1;
    String[] dataTypeID, timeStamp;
    String[] tmpDataTS = new String[2];
    PackageInfo pInfo = null;
    String nsRight,notifySettingRight,changePwdRight,changeLanguageRight;
    String assigned_tab = "", schedule_tab = "";
    private long last_pressed = 0;
    BadgeView badge;
    String mobileVersion = "";
    String refresh = "0";
    public static final String TAG_MY_WORK = "mywork";
    private NetworkManager networkManager;//108

    /*Added by Anshul Sharma*/
    private BroadcastReceiver mNetworkReceiver;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotificationCount();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("iTower");
        if (Build.VERSION.SDK_INT >= 34 && getApplicationInfo().targetSdkVersion >= 34) {
          registerReceiver(broadcastReceiver,intentFilter,RECEIVER_NOT_EXPORTED);
        } else {
           registerReceiver(broadcastReceiver,intentFilter);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
//        }else {
//            registerReceiver(broadcastReceiver, intentFilter);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        try{
            setContentView( R.layout.activity_home );


            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mobileVersion = Build.VERSION.RELEASE;

            ApiClient.init(IApiRequest.class,getApplicationContext());
            RetrofitApiClient.init(IApiRequest.class,getApplicationContext());
            mAppPreferences = new AppPreferences( HomeActivity.this );
            mAppPreferences.setTocForm("AccessRequesttoc");
            try {
                pInfo = HomeActivity.this.getPackageManager().getPackageInfo( HomeActivity.this.getPackageName(), 0 );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mNetworkReceiver = new NetworkChangeReceiver();
            if(getIntent().getExtras()!=null &&
                    getIntent().getExtras().getString("refresh")!=null){
                refresh = getIntent().getExtras().getString("refresh");
            }



            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
            dbHelper.open();
            nsRight = dbHelper.getSubMenuRight( "AppNotification", "SliderItem" );
            notifySettingRight = dbHelper.getSubMenuRight( "NotificationSetting", "SliderItem" );
            changePwdRight = dbHelper.getSubMenuRight( "ChangePwd", "SliderItem" );
            changeLanguageRight = dbHelper.getSubMenuRight( "ChangeLanguage", "SliderItem" );
            assigned_tab = dbHelper.getSubMenuRight( "AssignedTab", "Incident" );
            schedule_tab = dbHelper.getSubMenuRight( "Scheduled", "Preventive" );
            Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
            LayoutInflater li = LayoutInflater.from( this );
            View customView = li.inflate( R.layout.home_custom_top, null );
            notification_list = new ArrayList<String>();
            toolbar.addView( customView );
            setSupportActionBar( toolbar );
            tv_logo = (TextView) customView.findViewById( R.id.tv_logo );
            tv_logo.setTypeface( Utils.typeFace( HomeActivity.this ) );
            notification = (ImageView) findViewById( R.id.iv_notification );

            if (nsRight.contains( "V" )) {
                notification.setVisibility( View.VISIBLE );
            } else {
                notification.setVisibility( View.INVISIBLE );
            }

            notification.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent( HomeActivity.this, NotificationList.class );
                    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity( i );
                }
            } );

            if (mAppPreferences.getUserTracking().length() == 0) {
                mAppPreferences.setUserTracking( "OFF~06:00~22:00~60000~120000" );
            }

            badge = new BadgeView( HomeActivity.this, notification );
            dbHelper.updateSparePartStatus();
            notification_list = new ArrayList<String>();
            notification_list = dbHelper.getNotificationCount( mAppPreferences.getUserId(), "0" );
            dbHelper.close();
            if (nsRight.contains( "V" ) && notification_list != null && notification_list.size() > 0) {
                badge.setText( "" + notification_list.size() );
                badge.setTextSize( 10 );
                badge.show();
                ShortcutBadger.removeCount( HomeActivity.this );
                ShortcutBadger.applyCount( HomeActivity.this, notification_list.size() );
            }

            if (savedInstanceState != null)
                mContent = getSupportFragmentManager().getFragment( savedInstanceState, "mContent" );
            if (mContent == null)
                mContent = new HomeFragement();
            tv_logo.setText( Utils.msg( HomeActivity.this, "34" ) ); //set text iTower
            tv_logo.setText( tv_logo.getText().toString() + "       " );

            mAppPreferences.setBackPressHome( "m" );
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace( R.id.content_frame, mContent );
            ft.commit();
            DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
            drawer.addDrawerListener( toggle );
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
            navigationView.setNavigationItemSelectedListener( this );
            View header = navigationView.getHeaderView( 0 );

            userName = (TextView) header.findViewById( R.id.tv_userName );
            userName.setTypeface( Utils.typeFace( HomeActivity.this ) );
            if (mAppPreferences.getName().length() > 0) {
                userName.setText( "" + mAppPreferences.getName() );
            } else {
                userName.setText( "" );
            }


            lastlogin = (TextView) header.findViewById( R.id.tv_lastlogin );
            lastlogin.setTypeface( Utils.typeFace( HomeActivity.this ) );
            if (mAppPreferences.getLastLogin().length() > 0) {
                lastlogin.setText( "Last Login" + " : " + mAppPreferences.getLastLogin() );
            } else {
                lastlogin.setText( "" );
            }
            ListView lv_slider = (ListView) header.findViewById( R.id.lv_slider );
            SampleAdapter adapter = new SampleAdapter( HomeActivity.this );
            adapter.add( new SampleItem( Utils.msg( HomeActivity.this, "34" ), android.R.drawable.btn_star ) ); //set Text iTower
            lv_slider.setAdapter( adapter );

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        networkManager = new NetworkManager(); //108
        updateNotificationCount();
        if (refresh.equalsIgnoreCase("0") &&
                Utils.isNetworkAvailable(HomeActivity.this)) {
            //108

            networkManager.getToken(new NetworkManager.TokenCallback() {
                @Override
                public void onTokenReceived(String token) {
                    new LoginTask(HomeActivity.this,token).execute();
                }

                @Override
                public void onTokenError(String error) {
                    Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
            //108
          //  new LoginTask(HomeActivity.this).execute();
          //  getToken();
        }
    }

    @Override
    public void onBackPressed() {
        try{
            if (mAppPreferences.getBackPressHome().equalsIgnoreCase( "m" )) {
                //Toast onBackPressedToast = Toast.makeText(this,"Press back once again to Exit", Toast.LENGTH_SHORT);
                Toast onBackPressedToast = Utils.noToastShow( HomeActivity.this, "35" );
                long currentTime = System.currentTimeMillis();
                if (currentTime - last_pressed > 3000) {
                    onBackPressedToast.show();
                    last_pressed = currentTime;
                } else {
                    finishAffinity();
                    onBackPressedToast.cancel();
                    super.onBackPressed();
                }
            } else {
                home();

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /*switch (id) {
            case R.id.nav_home:
            case R.id.nav_pending:
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        tv_home.setTypeface( null, Typeface.BOLD );
        return true;
    }

    public void switchFragment(Fragment fragment, String str, String back, TextView tv) {
        if (back.equalsIgnoreCase( "n" )) {
            notification.setVisibility( View.INVISIBLE );
            badge.hide();
        } else {
            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
            dbHelper.open();
            notification_list = dbHelper.getNotificationCount( mAppPreferences.getUserId(), "0" );
            dbHelper.close();
            if(nsRight.contains("V")) {
                notification.setVisibility( View.VISIBLE );
            }
            if (nsRight.contains("V") && notification_list != null && notification_list.size() > 0) {
                badge.setText( "" + notification_list.size() );
                //badge.setText(""+64);
                badge.setTextSize( 10 );
                badge.show();
                ShortcutBadger.removeCount( HomeActivity.this );
                ShortcutBadger.applyCount( HomeActivity.this, notification_list.size() );
            }
        }
        mAppPreferences.setBackPressHome( back );
        tv_logo.setText( "" + str+"       " );
        mContent = fragment;
        if (mContent != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace( R.id.content_frame, mContent );
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
    }


    public void setLan() {
        // userName.setTypeface( WorkFlowUtils.typeFace( HomeActivity.this ) );
        //  userName.setText(mAppPreferences.getName() );
        // lastlogin.setTypeface( WorkFlowUtils.typeFace( HomeActivity.this ) );
        //lastlogin.setText("Last Login" + " : " + mAppPreferences.getLastLogin() );
        tv_home.setText("Home");
        tv_pending.setText("Pending Transactions");
        tv_reject.setText("Rejected Transactions");
        tv_notification.setText("Notification List");
        tv_notification_setting.setText("Notification Setting");
        tv_logout.setText("Logout");
        tv_privacy.setText("Privacy Policy");
        tv_about.setText("About Us");
        tv_change_password.setText("Change Password");
        tv_change_lang.setText("Change Language");
        tv_syn.setText("Sync");
        tv_syn_pending.setText("Sync Pending Transactions");
    }

    private class SampleItem {
        public String tag;
        public int iconRes;

        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }
    //custom_slider_list.xml
    public class SampleAdapter extends ArrayAdapter<SampleItem> {
        public SampleAdapter(Context context) {
            super( context, 0 );
        }

        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from( getContext() ).inflate( R.layout.home_slider_list, null );
            }
            tv_home = (TextView) view.findViewById( R.id.tv_home );
            tv_pending = (TextView) view.findViewById( R.id.tv_pending );
            tv_reject = (TextView) view.findViewById( R.id.tv_reject );

            tv_notification = (TextView) view.findViewById( R.id.tv_notification );
            TextView tv_notification_divider  = (TextView) view.findViewById( R.id.tv_notification_divider );

            tv_notification_setting = (TextView) view.findViewById(R.id.tv_notification_setting );
            TextView tv_notification_setting_divider = (TextView) view.findViewById( R.id.tv_notification_setting_divider );

            tv_logout = (TextView) view.findViewById( R.id.tv_logout );
            tv_about = (TextView) view.findViewById( R.id.tv_about );
            tv_privacy = (TextView) view.findViewById(R.id.tv_privacy);
            tv_change_password = (TextView) view.findViewById( R.id.tv_change_password );
            TextView tv_change_password_divider = (TextView) view.findViewById( R.id.tv_change_password_divider );

            tv_change_lang = (TextView) view.findViewById( R.id.tv_change_lang );
            TextView tv_change_lang_divider = (TextView) view.findViewById(R.id.tv_change_lang_divider);
            //tv_select_lang = (TextView) view.findViewById( R.id.tv_select_lang );
            tv_syn = (TextView) view.findViewById( R.id.tv_syn );
            tv_syn_pending = (TextView) view.findViewById( R.id.tv_syn_pending );
            //tv_syn_pending.setText("Sync Pending Transactions");
            setLan();

            if(changeLanguageRight.contains("V")){
                tv_change_lang.setVisibility(View.VISIBLE);
                tv_change_lang_divider.setVisibility(View.VISIBLE);
            }else{
                tv_change_lang.setVisibility(View.GONE);
                tv_change_lang_divider.setVisibility(View.GONE);
            }

            if(changePwdRight.contains("V")){
                tv_change_password.setVisibility(View.VISIBLE);
                tv_change_password_divider.setVisibility(View.VISIBLE);
            }else{
                tv_change_password.setVisibility(View.GONE);
                tv_change_password_divider.setVisibility(View.GONE);
            }

            if(nsRight.contains("V")){
                tv_notification.setVisibility(View.VISIBLE);
                tv_notification_divider.setVisibility(View.VISIBLE);
            }else{
                tv_notification.setVisibility(View.GONE);
                tv_notification_divider.setVisibility(View.GONE);
            }

            if(notifySettingRight.contains("V")){
                tv_notification_setting.setVisibility(View.VISIBLE);
                tv_notification_setting_divider.setVisibility(View.VISIBLE);
            }else{
                tv_notification_setting.setVisibility(View.GONE);
                tv_notification_setting_divider.setVisibility(View.GONE);
            }



            tv_home.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    home();
                    front_mode = 1;
                }
            } );

            tv_pending.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    pending();
                    front_mode = 2;
                }
            } );

            tv_reject.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    reject();
                    front_mode = 3;
                }
            } );

            tv_notification.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    notification();
                    front_mode = 4;
                }
            } );

            tv_notification_setting.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                   /* String s [] = new String[]{"NotificationType~4@@@SiteId~JD005252@@@ActivityType~Due Diligence - Civil@@@ScheduleDate~10-SEP-2019@@@RunHours~0@@@DoneDate~@@@DoneBy~@@@RejectBy~@@@Status~Schedule@@@AssignedTo~Aklesh  Dhar@@@EscalationLevel~First Escalation@@@CurrentRunHour~0",
                                               "NotificationType~5@@@SiteId~JD005252@@@ActivityType~Due Diligence - Civil@@@ScheduleDate~06-SEP-2019@@@RunHours~0@@@DoneDate~@@@DoneBy~@@@RejectBy~@@@Status~Missed@@@AssignedTo~Aklesh  Dhar@@@EscalationLevel~First Escalation@@@CurrentRunHour~0",
                                               "NotificationType~14@@@SiteId~JD005252@@@ActivityType~Due Diligence - Civil@@@ScheduleDate~06-SEP-2019@@@RunHours~0@@@DoneDate~@@@DoneBy~@@@RejectBy~@@@Status~Missed@@@AssignedTo~Aklesh  Dhar@@@EscalationLevel~First Escalation@@@CurrentRunHour~0",
                                                "NotificationType~12@@@SiteId~JD005252@@@ActivityType~Due Diligence - Civil@@@ScheduleDate~8-SEP-2019@@@RunHours~0@@@DoneDate~07-SEP-19@@@DoneBy~@@@RejectBy~Reviewer@@@Status~Rejected@@@AssignedTo~Aklesh  Dhar@@@EscalationLevel~First Escalation@@@CurrentRunHour~0",
                    };
                        FirebaseMsgReceive msff = new FirebaseMsgReceive();
                        msff.createNotification( s[3], HomeActivity.this );*/
                    setting();
                    front_mode = 5;
                }
            } );

            tv_privacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment newContent = new PolicyFragement();
                    switchFragment( newContent, "Privacy Policy", "n", tv_privacy );

                }
            });

            tv_about.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    FirebaseMsgReceive aa = new FirebaseMsgReceive();
                    //aa.createNotification("");
                    //aa.createNotification("NotificationType~1@@@TicketId~TT-20200303-04540838@@@SiteId~ANMD_0178@@@AlarmDescription~Alarm Test@@@AssignedTo~poornima pathak@@@TICKET_MODE~2",HomeActivity.this);
                    //aa.sendNotificationTest("hi",HomeActivity.this);
                    //aa.createNotificationChannel(HomeActivity.this);
                    aboutUs();
                    front_mode = 6;
                }
            } );

            // change passward
            tv_change_password.setOnClickListener( new View.OnClickListener() {//0.3
                @Override
                public void onClick(View arg0) {
                    changePassword();
                }
            } );

            tv_change_lang.setOnClickListener( new View.OnClickListener() {//0.3
                @Override
                public void onClick(View arg0) {
                    changeLanguage();
                }
            } );

            tv_syn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    sync();

                }
            });

            tv_syn_pending.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setClickable(false);

                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setClickable(true);

                        }
                    }, 200);

                    synPendingTransaction();
                    Intent i = new Intent( HomeActivity.this, HomeActivity.class );
                    startActivity( i );
                    finish();
                }
            } );

            tv_logout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    tv_home.setTypeface( null, Typeface.NORMAL );
                    tv_pending.setTypeface( null, Typeface.NORMAL );
                    tv_reject.setTypeface( null, Typeface.NORMAL );
                    tv_notification.setTypeface( null, Typeface.NORMAL );
                    tv_notification_setting.setTypeface( null, Typeface.NORMAL );
                    tv_about.setTypeface( null, Typeface.NORMAL );
                    tv_change_password.setTypeface( null, Typeface.NORMAL );
                    tv_syn.setTypeface( null, Typeface.NORMAL );
                    tv_syn_pending.setTypeface( null, Typeface.NORMAL );
                    tv_logout.setTypeface( null, Typeface.BOLD );
                    alert();
                }
            });
            return view;
        }
    }

    public void sync() {
        if (Utils.isNetworkAvailable( HomeActivity.this )) {
            tv_home.setTypeface( null, Typeface.NORMAL );
            tv_pending.setTypeface( null, Typeface.NORMAL );
            tv_reject.setTypeface( null, Typeface.NORMAL );
            tv_notification.setTypeface( null, Typeface.NORMAL );
            tv_notification_setting.setTypeface( null, Typeface.NORMAL );
            tv_logout.setTypeface( null, Typeface.NORMAL );
            tv_change_lang.setTypeface( null, Typeface.NORMAL );
            tv_change_password.setTypeface( null, Typeface.NORMAL );
            tv_about.setTypeface( null, Typeface.NORMAL );
            tv_syn_pending.setTypeface( null, Typeface.NORMAL );
            tv_syn.setTypeface( null, Typeface.BOLD );
            new Getversion( HomeActivity.this ).execute();
        } else {
            Utils.toast( HomeActivity.this, "17" );
            home();
        }
    }

    public void alert() {
        final Dialog logoutDialog;
        logoutDialog = new Dialog(HomeActivity.this, R.style.FullHeightDialog);
        logoutDialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        logoutDialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        logoutDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        logoutDialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = logoutDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        logoutDialog.show();
        Button positive = (Button) logoutDialog.findViewById( R.id.bt_ok );
        Button negative = (Button) logoutDialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) logoutDialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) logoutDialog.findViewById( R.id.tv_header);

        positive.setTypeface( Utils.typeFace(HomeActivity.this));
        negative.setTypeface( Utils.typeFace(HomeActivity.this));
        title.setTypeface( Utils.typeFace(HomeActivity.this));
        tv_header.setTypeface( Utils.typeFace(HomeActivity.this));

        tv_header.setText( Utils.msg(HomeActivity.this, "508"));
        title.setText( Utils.msg(HomeActivity.this, "62"));
        positive.setText( Utils.msg(HomeActivity.this, "63"));
        negative.setText( Utils.msg(HomeActivity.this, "64"));

        positive.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                logoutDialog.cancel();
                if (Utils.isNetworkAvailable(HomeActivity.this) &&
                        getApplicationContext().getPackageName().equalsIgnoreCase
                                ("tawal.com.sa")){
                    signOut();
                    new LogoutAuth2(HomeActivity.this).execute();
                  //  new LogoutTask1(HomeActivity.this).execute();

                } else if (Utils.isNetworkAvailable(HomeActivity.this) &&
                        getApplicationContext().getPackageName().equalsIgnoreCase
                                ("infozech.tawal")){
                    //getAuthSignOutToken();
                }
                else {
                    if (Utils.isNetworkAvailable(HomeActivity.this)) {
                        new LogoutTask(HomeActivity.this).execute();
                    } else {
                        Utils.toast(HomeActivity.this, "17");//No internet connection
                    }
                }
            }
        });
        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                logoutDialog.cancel();
                if (front_mode == 1) {
                    home();
                } else if (front_mode == 2) {
                    pending();
                } else if (front_mode == 3) {
                    reject();
                } else if (front_mode == 4) {
                    notification();
                } else if (front_mode == 5) {
                    setting();
                } else if (front_mode == 6) {
                    aboutUs();
                }
            }
        } );
    }


    private void signOut() {
         AuthStateManager mStateManager = AuthStateManager.getInstance(this);
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);
    }

    public class LogoutAuth2 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        public LogoutAuth2(Context con) {
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
                String response = HttpUtils.httpGetRequest(WebMethods.url_logout_ad);
               } catch (Exception e) {
                e.printStackTrace();
                //auditTrailList = null;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            //Toast.mak+eText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
          //  Utils.toast( HomeActivity.this, "36" );
//            stopLocationService();
//            stopPunchInnotificatioService();
//            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
//            i.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//            startActivity( i );
//            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
//            dbHelper.open();
//            dbHelper.clearFormRights();
//            dbHelper.close();
//            mAppPreferences.setLoginState( 0 );
//            mAppPreferences.saveSyncState( 0 );
//            //mAppPreferences.setGCMRegistationId("");
//            finish();
            new LogoutTask1(HomeActivity.this).execute();
        }
    }

    public void home() {
        Fragment newContent = new HomeFragement();
        switchFragment( newContent, Utils.msg( HomeActivity.this, "34" ), "m", tv_home ); // set Text iTower
        tv_home.setTypeface( null, Typeface.BOLD );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void pending() {
        Fragment newContent = new PendingFrag();
        switchFragment( newContent, "Pending Transactions", "n", tv_pending );
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.BOLD );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void reject() {
        Fragment newContent = new RejectFrag();
        switchFragment( newContent, "Rejected Transactions", "n", tv_reject );
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.BOLD );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void notification() {
        Fragment newContent = new NotificationListFrag();
        switchFragment( newContent, "Notification List", "n", tv_notification );
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.BOLD );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void setting() {
        Fragment newContent = new NotificationSettingFrag();
        switchFragment( newContent, "Notification Setting", "n", tv_notification_setting );
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.BOLD );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void aboutUs() {
        Fragment newContent = new AboutUs();
        switchFragment( newContent, "About Us", "n", tv_about );
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.BOLD );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
    }

    public void changePassword() {
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.NORMAL );
        tv_change_password.setTypeface( null, Typeface.BOLD );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
        changePassPopup();
    }

    public void changeLanguage() {
        tv_home.setTypeface( null, Typeface.NORMAL );
        tv_pending.setTypeface( null, Typeface.NORMAL );
        tv_reject.setTypeface( null, Typeface.NORMAL );
        tv_notification.setTypeface( null, Typeface.NORMAL );
        tv_notification_setting.setTypeface( null, Typeface.NORMAL );
        tv_logout.setTypeface( null, Typeface.NORMAL );
        tv_change_lang.setTypeface( null, Typeface.BOLD );
        tv_change_password.setTypeface( null, Typeface.NORMAL );
        tv_about.setTypeface( null, Typeface.NORMAL );
        tv_syn.setTypeface( null, Typeface.NORMAL );
        tv_syn_pending.setTypeface( null, Typeface.NORMAL );
        changeLangPopup();
    }

    public void synPendingTransaction(){
        if (Utils.isNetworkAvailable( HomeActivity.this )){
            if(Utils.getIntervalInMin(HomeActivity.this) == 1){
                DataSyncReceiver dataSync = new DataSyncReceiver();
                dataSync.postSubmitSync( HomeActivity.this );
            }
            Utils.toastMsg(HomeActivity.this,"Please wait Sync will be processed.");
        } else {
            Utils.toast( HomeActivity.this, "17" );
        }
    }



    public void changeLangPopup() {
        final ArrayList<String> lan_name = new ArrayList<>();
        final ArrayList<String> lan_code = new ArrayList<>();
        lan_name.add( "English" );
        lan_code.add( "en" );
        if(getApplicationContext().getPackageName().
                equalsIgnoreCase("in.clairvoyant.imaintain.activity")){
            lan_name.add( "Burmese" );
            lan_code.add( "my" );
        }
        AlertDialog.Builder alert = new AlertDialog.Builder( HomeActivity.this );
        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
        View convertView = (View) inflater.inflate( R.layout.language_code_list, null );
        alert.setView( convertView );
        ListView langList = (ListView) convertView.findViewById( R.id.listView1 );
        TextView title = (TextView) convertView.findViewById( R.id.tv_title );
        title.setTypeface( Utils.typeFace( HomeActivity.this ) );
        title.setText( Utils.msg( HomeActivity.this, "260" ) );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( HomeActivity.this, android.R.layout.simple_list_item_1, lan_name );
        langList.setAdapter( adapter );
        final AlertDialog ad = alert.show();
        langList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                ad.dismiss();
                String code = lan_code.get( pos );
                if (Utils.isNetworkAvailable( HomeActivity.this )) {
                    new MultiLanguage( HomeActivity.this, code ).execute();
                } else {
                    Utils.toast( HomeActivity.this, "17" );
                }
            }
        } );
    }

    public class MultiLanguage extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res, code;
        MultiLanguageList multiLanguageList = null;

        public MultiLanguage(Context con, String code) {
            this.con = con;
            this.code = code;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Loading..." );
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( "languageCode", code ) );
                res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_GetBaseData, nameValuePairs );
                Gson gson = new Gson();
                multiLanguageList = gson.fromJson( res, MultiLanguageList.class );
            } catch (Exception e) {
                e.printStackTrace();
                multiLanguageList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if ((multiLanguageList == null)) {
                //Toast.makeText(getActivity(),"Meta data not provided by server",Toast.LENGTH_LONG).show();
                Utils.toast( HomeActivity.this, "70" );
            } else if (multiLanguageList.getLanguageList() != null && multiLanguageList.getLanguageList().size() > 0) {
                DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
                dbHelper.open();
                dbHelper.clearMultiLanguage();
                dbHelper.insertMultiLanguage( multiLanguageList.getLanguageList() );
                dbHelper.close();
                mAppPreferences.setLanCode( code );
                //setLan(code);
                Intent i = new Intent( HomeActivity.this, HomeActivity.class );
                startActivity( i );
                finish();
            } else {
                //Toast.makeText(getActivity(), "Server Not Available",Toast.LENGTH_LONG).show();
                Utils.toast( HomeActivity.this, "13" );
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    public void changePassPopup() {
        changePassPop = new Dialog( HomeActivity.this, R.style.FullHeightDialog );
        changePassPop.requestWindowFeature( Window.FEATURE_NO_TITLE );
        changePassPop.getWindow().setBackgroundDrawableResource( R.color.nevermind_bg_color );
        changePassPop.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        changePassPop.setContentView( R.layout.change_passward );
        final Window window_SignIn = changePassPop.getWindow();
        window_SignIn.setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT );
        window_SignIn.clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND );
        changePassPop.show();
        final TextInputEditText et_old_pwd = (TextInputEditText) changePassPop.findViewById( R.id.et_old_pwd );
        et_old_pwd.setFocusableInTouchMode( true );
        TextView tv_brand_logo = (TextView) changePassPop.findViewById( R.id.tv_brand_logo );
        final TextInputEditText et_new_pwd = (TextInputEditText) changePassPop.findViewById( R.id.et_new_pwd );
        final TextInputEditText et_retry_pwd = (TextInputEditText) changePassPop.findViewById( R.id.et_retry_pwd );
        final Button bt_save = (Button) changePassPop.findViewById( R.id.bt_save );
        final Button bt_cancel = (Button) changePassPop.findViewById( R.id.bt_cancel );
        Utils.msgText( HomeActivity.this, "22", tv_brand_logo );
        Utils.msgButton( HomeActivity.this, "26", bt_cancel );
        Utils.msgButton( HomeActivity.this, "27", bt_save );
        bt_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changePassPop.dismiss();
                //hideKeyBoardEdt(et_new_pwd);
                //setupUI(bt_cancel);
                if (front_mode == 1) {
                    home();
                } else if (front_mode == 2) {
                    pending();
                } else if (front_mode == 3) {
                    reject();
                } else if (front_mode == 4) {
                    notification();
                } else if (front_mode == 5) {
                    setting();
                } else if (front_mode == 6) {
                    aboutUs();
                }
            }
        } );

        bt_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_old_pwd.getText().toString().trim().length() == 0) {
                    //Toast.makeText(getActivity(),"Enter Old Password",Toast.LENGTH_LONG).show();
                    Utils.toast( HomeActivity.this, "28" );
                } else if (et_new_pwd.getText().toString().trim().length() == 0) {
                    //Toast.makeText(getActivity(),"Enter New Password",Toast.LENGTH_LONG).show();
                    Utils.toast( HomeActivity.this, "29" );
                } else if (et_retry_pwd.getText().toString().trim().length() == 0) {
                    //Toast.makeText(getActivity(),"Enter Retype Password",Toast.LENGTH_LONG).show();
                    Utils.toast( HomeActivity.this, "30" );
                } else if (!(et_new_pwd.getText().toString().trim().equals( et_retry_pwd.getText().toString().trim() ))) {
                    //Toast.makeText(getActivity(),"Password does not match with Retype Password.",Toast.LENGTH_LONG).show();
                    Utils.toast( HomeActivity.this, "31" );
                } else if (et_old_pwd.getText().toString().trim().equals( et_new_pwd.getText().toString().trim() )) {
                    //Toast.makeText(getActivity(),"Enter New Password different from Old Password.",Toast.LENGTH_LONG).show();
                    Utils.toast( HomeActivity.this, "32" );
                } else {
                    if (Utils.isNetworkAvailable( HomeActivity.this )) {
                        //setupUI(bt_save);
                        new ChangePassward( HomeActivity.this, mAppPreferences.getUserId(),
                                et_old_pwd.getText().toString().trim(),
                                et_new_pwd.getText().toString().trim() ).execute();
                    } else {
                        //WorkFlowUtils.ToastMessage(getActivity(),Constants.netConnection);
                        Utils.toast( HomeActivity.this, "17" );//No internet connection
                    }
                }
            }
        } );
    }

    public class ChangePassward extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String user_id;
        String pwd;
        String newpwd;
        Response response;

        public ChangePassward(Context con, String user_id, String pwd, String newpwd) {
            this.con = con;
            this.user_id = user_id;
            this.pwd = pwd;
            this.newpwd = newpwd;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Loading..." );
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 5 );
                nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getLoginId()));
                nameValuePairs.add( new BasicNameValuePair( "userID", user_id ) );
                nameValuePairs.add( new BasicNameValuePair( "pwd", pwd ) );
                nameValuePairs.add( new BasicNameValuePair( "newPwd", newpwd ) );
                nameValuePairs.add( new BasicNameValuePair( "languageCode", "" + mAppPreferences.getLanCode() ) );
                String res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_chngPWD, nameValuePairs );
                String new_res = res.replace( "[", "" ).replace( "]", "" );
                response = new Gson().fromJson( new_res, Response.class );
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response != null) {
                Utils.toastMsg( HomeActivity.this, response.getMessage() );
                if (response.getSuccess().equals( "true" )) {
                    changePassPop.dismiss();
                    loginState();
                }
            } else {
                Utils.toast( HomeActivity.this, "13" );
            }
            super.onPostExecute( result );
        }
    }

    ;

    public class LogoutTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res;
        ResponceLoginList response = null;

        public LogoutTask(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Logging Out..." );

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( "userId", mAppPreferences.getUserId() ) );
                res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_logout, nameValuePairs );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            //Toast.makeText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
            stopLocationService();
            stopPunchInnotificatioService();
            Utils.toast( HomeActivity.this, "36" );
            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
            i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( i );
            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
            dbHelper.open();
            dbHelper.clearFormRights();
            dbHelper.close();
            mAppPreferences.setLoginState( 0 );
            mAppPreferences.saveSyncState( 0 );
            //mAppPreferences.setGCMRegistationId("");
            finish();
        }
    }


    public class LogoutTask1 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res;
        ResponceLoginList response = null;

        public LogoutTask1(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Logging Out..." );

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( "userId", mAppPreferences.getUserId() ) );
                res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_logout, nameValuePairs );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            //Toast.makeText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
            stopLocationService();
            stopPunchInnotificatioService();
            Utils.toast( HomeActivity.this, "36" );
            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
            i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( i );
            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
            dbHelper.open();
            dbHelper.clearFormRights();
            dbHelper.close();
            mAppPreferences.setLoginState( 0 );
            mAppPreferences.saveSyncState( 0 );
            //mAppPreferences.setGCMRegistationId("");
            finish();
        }
    }


    /*public class LogoutTask1 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res;
        ResponceLoginList response = null;

        public LogoutTask1(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Logging Out...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            HttpURLConnection urlConnection = null;

            String url = "https://midc-idp.infozech.com:9014/realms/MIDC/protocol/openid-connect/logout";
            String ss = mAppPreferences.getToken();

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( "client_id", "midc-mobile") );
                res = Utils.httpPostRequest( con, url, nameValuePairs );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }

            stopLocationService();
            stopPunchInnotificatioService();
            Utils.toast( HomeActivity.this, "36" );
            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
            i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( i );
            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
            dbHelper.open();
            dbHelper.clearFormRights();
            dbHelper.close();
            mAppPreferences.setLoginState( 0 );
            mAppPreferences.saveSyncState( 0 );
            //mAppPreferences.setGCMRegistationId("");
            finish();


        }
    }*/

    public class Getversion extends AsyncTask<Void, Void, Void> {
        String res;
        Context con;
        ServiceResponce response;
        ProgressDialog pd;
        public Getversion(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 16 );
            Gson gson = new Gson();
            nameValuePairs.add( new BasicNameValuePair( "version", pInfo.versionName ) );
            nameValuePairs.add( new BasicNameValuePair( "userID", mAppPreferences.getUserId() ) );
            nameValuePairs.add( new BasicNameValuePair( "roleID", mAppPreferences.getRoleId() ) );
            nameValuePairs.add( new BasicNameValuePair( "deviceID", mAppPreferences.getGCMRegistationId() ) );
            nameValuePairs.add( new BasicNameValuePair( "languageCode", "" + mAppPreferences.getLanCode() ) );
            try {
                String res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_GetAppVersion, nameValuePairs );
                String new_res = res.replace( "[", "" ).replace( "]", "" );
                response = gson.fromJson( new_res, ServiceResponce.class );
            } catch (Exception e) {
                e.printStackTrace();
                res = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (response != null) {
                //get Tracking role
                DataBaseHelper dbHelperr = new DataBaseHelper( HomeActivity.this );
                dbHelperr.open();
                /*if (dbHelperr.trackingRoleRightCount( "UserTrackOnOff", "UserTrackModule" ) > 0) {
                    dbHelperr.updateTrackingRoleRight( response.getTrackingRole() );
                }*/
                //get Tracking configuration
                if (response.getUserTracking().length() != 0) {
                    mAppPreferences.setUserTracking( response.getUserTracking() );
                    String[] dataUserTrack = response.getUserTracking().split( "\\~" );
                    mAppPreferences.setTrackingOnOff( dataUserTrack[0] );
                    mAppPreferences.setUserTrackUploadTime( Utils.dateNotification() );
                }

                /* get app config data (distance range,enable field,autodatetime flag)
                 * distance range for indicate show site in fuel filling given distance range
                 * enable disable field in fuel filling*/
               /* if (response.getDistanceRange().length() != 0) {
                    String[] dataTS = response.getDistanceRange().split( "\\~" );
                    if (dataTS.length > 8) {
                        mAppPreferences.setEnableFillingField(dataTS[0]);
                        mAppPreferences.setSiteMotorable(Integer.parseInt(dataTS[1]));
                        mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt(dataTS[2]) );
                        mAppPreferences.setSiteNameEnable(Integer.parseInt(dataTS[3])  );
                        mAppPreferences.setAutoDateTime(dataTS[4]);
                        mAppPreferences.setSearchTTDateRange(Integer.parseInt(dataTS[5]));
                        mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt(dataTS[6]));
                        mAppPreferences.setPMImageUploadType(Integer.parseInt(dataTS[7]));
                        mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt(dataTS[8]));

                        if (dataTS.length > 9){
                            mAppPreferences.setPMReviewPlanDate(Integer.parseInt(dataTS[9]));
                        }
                    }
                }*/
                try {
                    if (response.getDistanceRange().length() != 0) {
                        String[] dataTS = response.getDistanceRange().split( "\\~" );
                        if (dataTS.length > 11) {
                            mAppPreferences.setEnableFillingField( dataTS[0] );
                            mAppPreferences.setSiteMotorable( Integer.parseInt( dataTS[1] ) );
                            mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt( dataTS[2] ) );
                            mAppPreferences.setSiteNameEnable( Integer.parseInt( dataTS[3] ) );
                            mAppPreferences.setAutoDateTime( dataTS[4] );
                            mAppPreferences.setSearchTTDateRange( Integer.parseInt( dataTS[5] ) );
                            mAppPreferences.setEnablePrePopulateSitesTT( Integer.parseInt( dataTS[6] ) );
                            mAppPreferences.setPMImageUploadType( Integer.parseInt( dataTS[7] ) );
                            mAppPreferences.setPMRejectMadatoryFields( Integer.parseInt( dataTS[8] ) );
                            mAppPreferences.setPMReviewPlanDate( Integer.parseInt( dataTS[9] ) );
                            mAppPreferences.setVideoUploadMaxSize( Integer.parseInt( dataTS[10] ) );
                            mAppPreferences.setIsVideoCompress( Integer.parseInt(dataTS[11]));
                            mAppPreferences.setOperatorWiseUserField(dataTS[12]);
                            mAppPreferences.setHyperLinkPM(dataTS[13]);

                            if (dataTS.length > 14) {
                                mAppPreferences.setCalendarMonth(Integer.parseInt(dataTS[14]));
                            }
                            if (dataTS.length > 15) {
                                mAppPreferences.setDocumentUploadMaxSize(Integer.parseInt(dataTS[15]));
                            }
                        }
                    }
                }catch (Exception e){
                    mAppPreferences.setEnableFillingField("0");
                    mAppPreferences.setSiteMotorable(0);
                    mAppPreferences.setSavePMBackgroundEnable(0);
                    mAppPreferences.setSiteNameEnable(Integer.parseInt("0"));
                    mAppPreferences.setAutoDateTime("0");
                    mAppPreferences.setSearchTTDateRange(Integer.parseInt("30"));
                    mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt("0"));
                    mAppPreferences.setPMImageUploadType(Integer.parseInt("2"));
                    mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt("0"));
                    mAppPreferences.setPMReviewPlanDate(Integer.parseInt("0"));
                    mAppPreferences.setVideoUploadMaxSize(Integer.parseInt("5"));
                    mAppPreferences.setIsVideoCompress(Integer.parseInt("0"));
                    mAppPreferences.setOperatorWiseUserField("N");
                    mAppPreferences.setHyperLinkPM("0");
                }

                // TT Configuration
                mAppPreferences.setTTConfiguration( response.getTtconfiguration() );
                String ttConfig = mAppPreferences.getTTConfiguration();
                if (ttConfig.length() != 0) {
                    String baseArray[] = ttConfig.split( "@" );
                    if (baseArray.length > 3) {
                        mAppPreferences.setTTminimage( Integer.parseInt( baseArray[0] ) );
                        mAppPreferences.setTTmaximage( Integer.parseInt( baseArray[1] ) );
                        mAppPreferences.setTTimageMessage( baseArray[2] );
                        mAppPreferences.setTTMediaFileType(baseArray[3]);
                    }
                }

                // FF Configuration
                mAppPreferences.setFFConfiguration( response.getFfconfiguration() );
                String ffConfig = mAppPreferences.getFFConfiguration();
                /*if (ffConfig.length() != 0) {
                    String baseArray[] = ffConfig.split( "@" );
                    if (baseArray.length > 2) {
                        mAppPreferences.setFFminimage( Integer.parseInt( baseArray[0] ) );
                        mAppPreferences.setFFmaximage( Integer.parseInt( baseArray[1] ) );
                        mAppPreferences.setFFimageMessage( baseArray[2] );
                    }
                }*/
                if (ffConfig.length() != 0) {
                    String baseArray[] = ffConfig.split("@");
                    if (baseArray.length > 6) {
                        mAppPreferences.setApprovalRemarks(baseArray[0]);
                        mAppPreferences.setIsRating(baseArray[1]);
                        mAppPreferences.setPMphotoInfoRating(baseArray[2]);
                        mAppPreferences.setQualityPMtaskRating(baseArray[3]);
                        mAppPreferences.setVisualCheck1Rating(baseArray[4]);
                        mAppPreferences.setVisualCheck2Rating(baseArray[5]);
                        mAppPreferences.setVisualCheck3Rating(baseArray[6]);
                    }
                }

                // PM Configuration
                mAppPreferences.setPmConfiguration( response.getPmconfiguration() );
                String pmConfig = mAppPreferences.getPmConfiguration();
                if (pmConfig.length() != 0) {
                    String baseArray[] = pmConfig.split( "@" );
                    int activityID = 0, paramID = 0;
                    String paramName = "";
                    dbHelperr.clearpmconfig();
                    if (baseArray.length > 0) {
                        for (int i = 0; i < baseArray.length; i++) {
                            String tmpArray[] = baseArray[i].split( "\\$" );
                            activityID = Integer.parseInt( tmpArray[0].toString() );
                            tmpArray = tmpArray[1].split( "\\#" );
                            paramID = Integer.parseInt( tmpArray[0].toString() );
                            paramName = tmpArray[1].toString();
                            dbHelperr.insertPmConfiguration( activityID, paramID, paramName );
                        }
                    }
                    dbHelperr.close();
                }
                // reschedule after Tracking configuration change
              /*  if (mAppPreferences.getTrackingOnOff().equalsIgnoreCase( "ON" )) {
                    GetUserInfoScheduler.schedule( HomeActivity.this );
                    Intent userInfo = new Intent( HomeActivity.this, GetUserInfoService.class );
                    if (GetUserInfoService.isServiceRunning) {
                        stopService( userInfo );
                    }
                    startService( userInfo );
                }*/

                if (response.getSuccess().equalsIgnoreCase( "A" )) {
                    DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
                    dbHelper.open();
                    dbHelper.clearFormRights();
                    dbHelper.close();
                    mAppPreferences.setLoginState( 0 );
                    mAppPreferences.saveSyncState( 0 );
                    //mAppPreferences.setGCMRegistationId("");
                    PassIEMIChange( SessionExpired.class, response.getMessage().trim() );
                } else if (response.getSuccess().equalsIgnoreCase( "V" )) { //use old version app
                    mAppPreferences.setLoginState( 2 );
                    PassIEMIChange( Version.class, response.getMessage().trim() );
                } else if (response.getSuccess().equalsIgnoreCase( "T" )) {// licence expired
                    DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
                    dbHelper.open();
                    dbHelper.clearFormRights();
                    dbHelper.close();
                    mAppPreferences.setLoginState( 0 );
                    mAppPreferences.saveSyncState( 0 );
                    //mAppPreferences.setGCMRegistationId("");
                    PassIEMIChange( SessionExpired.class, response.getMessage().trim() );
                } else if (response.getSuccess().equalsIgnoreCase( "E" )) {
                    DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
                    dbHelper.open();
                    dbHelper.clearFormRights();
                    dbHelper.close();
                    mAppPreferences.setLoginState( 0 );
                    mAppPreferences.saveSyncState( 0 );
                    //mAppPreferences.setGCMRegistationId("");
                    PassIEMIChange( PasswardExpired.class, response.getMessage().trim() );
                } else if (response.getSuccess().equalsIgnoreCase( "P" )) {
                    Intent i = new Intent( HomeActivity.this, PasswardExpire.class );
                    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    i.putExtra( "msg", response.getMessage() );
                    startActivity( i );
                } else if (response.getSuccess().equalsIgnoreCase( "S" ) && response.getMessage().trim().length() != 0) { //successfully
                    mAppPreferences.setDataTS( response.getMessage() );
                    successfull();
                }
            } else {
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            home();
            super.onPostExecute( result );
        }
    }

    public void loginState() {
        DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
        dbHelper.open();
        dbHelper.clearFormRights();
        dbHelper.close();
        mAppPreferences.setLoginState( 0 );
        mAppPreferences.saveSyncState( 0 );
        //mAppPreferences.setGCMRegistationId("");
        Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
        i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity( i );
        finish();
    }

    public void PassIEMIChange(Class cls, String msg) {
        Intent i = new Intent( HomeActivity.this, cls );
        i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        i.putExtra( "msg", msg );
        startActivity( i );
    }

    public void successfull() {
        DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
        dbHelper.open();
        String[] dataTS = mAppPreferences.getDataTS().split( "," );
        dataTypeID = new String[dataTS.length];
        timeStamp = new String[dataTS.length];
        for (int i = 0; i < dataTS.length; i++) {
            tmpDataTS = dataTS[i].split( "\\~" );
            dataTypeID[i] = tmpDataTS[0];
            timeStamp[i] = tmpDataTS[1];
        }
        if (!mAppPreferences.getFirstTimeRunApp().equalsIgnoreCase( "A" )) {
            dbHelper.clearDataTS();
            dbHelper.dataTS( dataTypeID,timeStamp,"","",0,"0" );
            mAppPreferences.setFirstTimeRunApp( "A" );
        } else {
            dbHelper.dataTS( dataTypeID, timeStamp, "", "", 1,"0" );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
    //registering network change receiver
    private void registerNetworkBroadcastForNougat() {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    //unregistering network change receiver
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
            DataSyncReceiver dataSyncReceiver = new DataSyncReceiver();
            unregisterReceiver(dataSyncReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationService() {
        mAppPreferences.setUserPunchInOut("false");
        LocationService mYourService = new LocationService();
        Intent intent = new Intent(HomeActivity.this, mYourService.getClass());
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        stopService(intent);

    }

    private void stopPunchInnotificatioService () {
        PunchInNotificationService mYourService = new PunchInNotificationService();
        Intent intent = new Intent(HomeActivity.this, mYourService.getClass());
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        stopService(intent);

    }

    @MainThread
    public void authSignOut() {
        stopLocationService();
        stopPunchInnotificatioService();
        Utils.toast( HomeActivity.this, "36" );
        Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
        i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(i);
        DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
        dbHelper.open();
        dbHelper.clearFormRights();
        dbHelper.close();
        mAppPreferences.setLoginState( 0 );
        mAppPreferences.saveSyncState( 0 );
        finish();
    }

 /*   private void callAuthLogout(String token) {
        String url = "";
        if(mAppPreferences.getUserKeyclokId()==null
                ||mAppPreferences.getUserKeyclokId().equalsIgnoreCase("")){
            authSignOut();
        }else {
            url= "users/" + mAppPreferences.getUserKeyclokId() + "/logout";
        }
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging out...");
        progressDialog.show();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(120, TimeUnit.SECONDS);
        client.readTimeout(120, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder().
                baseUrl(Utils.msg( HomeActivity.this, "843" ))
                .addConverterFactory(GsonConverterFactory.create()).
                client(client.build());
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<String> call = iApiRequest.getLogoutKeyClock(url, "Bearer " + token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                authSignOut();
               *//* if (response.code()== 204) {
                    authSignOut();
               }*//*

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                authSignOut();

                *//* if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }*//*
            }
        });
    }

    private void getAuthSignOutToken() {
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(120, TimeUnit.SECONDS);
        client.readTimeout(120, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder().
                //baseUrl("https://itower-stg-app.infozech.com:9112/realms/MIDC/").
                baseUrl(Utils.msg( HomeActivity.this, "844" )).
                addConverterFactory(GsonConverterFactory.create()).
                client(client.build());
        final Retrofit retrofit = builder.build();
        IApiRequest iApiRequest = retrofit.create(IApiRequest.class);
        Call<UserResponce> call = iApiRequest.getUserIDTokenForKeyClock(
                Utils.msg( HomeActivity.this, "845" ),
                Utils.msg( HomeActivity.this, "846" ),
                Utils.msg( HomeActivity.this, "847" ),
                Utils.msg( HomeActivity.this, "848" ),
                Utils.msg( HomeActivity.this, "849" ));
        call.enqueue(new Callback<UserResponce>() {
            @Override
            public void onResponse(Call<UserResponce> call, retrofit2.Response<UserResponce> response) {
                if (response.body() != null) {
                    //Toast.makeText(getContext(), ""+response.body().access_token, Toast.LENGTH_SHORT).show();
                    if (response.body().access_token != null){
                        //mAppPreferences.setTokenID(response.body().access_token);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        callAuthLogout(response.body().access_token);
                    }else{
                        authSignOut();
                    }

                } else {
                    authSignOut();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onFailure(Call<UserResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                authSignOut();
            }
        });

    }*/

    public class LoginTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> contextRef;
        ProgressDialog pd;
        Context con;
        AppPreferences mAppPref;
        String res,token; //108
        public LoginTask(Context con,String token) {
            this.contextRef = new WeakReference<>(HomeActivity.this);
            this.con = con;
            mAppPref = new AppPreferences(HomeActivity.this);
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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                nameValuePairs.add(new BasicNameValuePair("loginId",mAppPreferences.getLoginId()));
                nameValuePairs.add(new BasicNameValuePair("pwd", "11@"));
                nameValuePairs.add(new BasicNameValuePair("mId", "40"));
                nameValuePairs.add(new BasicNameValuePair("version",pInfo.versionName));
                String addParams = "deviceId=" + mAppPref.getGCMRegistationId()
                        + "~" + "emiNo=" + "" + "~login="+"0" + "~ip="+getIPAddress()
                        +"~language="+mAppPreferences.getLanCode()
                        +"~mobileVersion="+mobileVersion
                        +"~authStatus=SSO"
                        +"~appId="+getApplicationContext().getPackageName();;
                //addParams="deviceId="+mAppPref.getGCMRegistationId()+"~"+"emiNo="+"355370060893490";//0.3
                nameValuePairs.add(new BasicNameValuePair("addParams",addParams));
                nameValuePairs.add(new BasicNameValuePair("retryCnt", "0"));
                nameValuePairs.add(new BasicNameValuePair("languageCode", "en"));
                res = Utils.httpPostRequest1(con,mAppPreferences.getConfigIP()+ WebMethods.url_Authenticate, nameValuePairs,token); //108
                response = new Gson().fromJson(res, ResponceLoginList.class);
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Context context = contextRef.get();
            if (context != null && !((Activity) context).isFinishing()) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
            }

            if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("S")
                    && response.getDetails().get(0).getMessage().length() == 0) {
                setLoginResponce(response);
            } else if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("S")
                    && response.getDetails().get(0).getMessage().length() != 0) {
                Utils.toastMsg(HomeActivity.this, response.getDetails()
                        .get(0).getMessage());
            } else if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("V")) {
                setLoginResponce(response);
                version();
            }
            super.onPostExecute(result);
        }
    }


    public void setLoginResponce(ResponceLoginList response) {
        DataBaseHelper dbHelper = new DataBaseHelper(HomeActivity.this);
        dbHelper.open();
        mAppPreferences.setRegionId(response.getDetails().get(0).getrId());
        mAppPreferences.setRoleId(response.getDetails().get(0).getRoleId());

        if(response.getDetails().get(0).getName().contains("~")){
            String [] arr = response.getDetails().get(0).getName().split("~");
            mAppPreferences.setName(arr[0]);
            mAppPreferences.setUserMailid(arr[1]);
            mAppPreferences.setUserNumber(arr[2]);
            mAppPreferences.setUserGroup(arr[3]);
            mAppPreferences.setUserGroupName(arr[4]);
        }

        mAppPreferences.setLastLogin(response.getDetails().get(0).getLastLogin());
        mAppPreferences.saveUserId(String.valueOf(response.getDetails().get(0).getuId()));
        mAppPreferences.setCounrtyID(response.getDetails().get(0).getCnId());
        mAppPreferences.setHubID(response.getDetails().get(0).gethId());
        mAppPreferences.setCircleID(response.getDetails().get(0).getCrId());
        mAppPreferences.setZoneID(response.getDetails().get(0).getzId());
        mAppPreferences.setClusterID(response.getDetails().get(0).getClId());
        mAppPreferences.setPIOMEID(response.getDetails().get(0).getOmId());
        mAppPreferences.setDataTS(response.getDetails().get(0).getDataTS());

        mAppPreferences.setUserCategory(response.getDetails().get(0).getUserCategory());  //1.0
        mAppPreferences.setUserSubCategory(response.getDetails().get(0).getUserSubCategory());
        mAppPreferences.setPmConfiguration(response.getDetails().get(0).getPMConfiguration());

        // TT Configuration
        mAppPreferences.setTTConfiguration(response.getDetails().get(0).getTtconfiguration());
        String ttConfig = mAppPreferences.getTTConfiguration();
        if (ttConfig.length() != 0) {
            String baseArray[] = ttConfig.split("@");
            if (baseArray.length > 2) {
                mAppPreferences.setTTminimage(Integer.parseInt(baseArray[0]));
                mAppPreferences.setTTmaximage(Integer.parseInt(baseArray[1]));
                mAppPreferences.setTTimageMessage(baseArray[2]);
                mAppPreferences.setTTMediaFileType(baseArray[3]);
            }
        }

        // FF Configuration
        mAppPreferences.setFFConfiguration(response.getDetails().get(0).getFfconfiguration());
        String ffConfig = mAppPreferences.getFFConfiguration();
       /* if (ffConfig.length() != 0) {
            String baseArray[] = ffConfig.split("@");
            if (baseArray.length > 0) {
                mAppPreferences.setFFminimage(Integer.parseInt(baseArray[0]));
                mAppPreferences.setFFmaximage(Integer.parseInt(baseArray[1]));
                mAppPreferences.setFFimageMessage(baseArray[2]);
            }
        }*/

        if (ffConfig.length() != 0) {
            String baseArray[] = ffConfig.split("@");
            if (baseArray.length > 6) {
                mAppPreferences.setApprovalRemarks(baseArray[0]);
                mAppPreferences.setIsRating(baseArray[1]);
                mAppPreferences.setPMphotoInfoRating(baseArray[2]);
                mAppPreferences.setQualityPMtaskRating(baseArray[3]);
                mAppPreferences.setVisualCheck1Rating(baseArray[4]);
                mAppPreferences.setVisualCheck2Rating(baseArray[5]);
                mAppPreferences.setVisualCheck3Rating(baseArray[6]);
            }
        }

        if (response.getDetails().get(0).getUserTracking().length() != 0) {
            mAppPreferences.setUserTracking(response.getDetails().get(0).getUserTracking());
            String[] dataUserTrack = response.getDetails().get(0).getUserTracking().split("\\~");
            mAppPreferences.setTrackingOnOff(dataUserTrack[0]);
            mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
        }

        if (response.getDetails().get(0).getDistanceRange().length() != 0) {
            String[] dataTS = response.getDetails().get(0).getDistanceRange().split("\\~");
            if (dataTS.length > 11) {
                mAppPreferences.setEnableFillingField(dataTS[0]);
                mAppPreferences.setSiteMotorable(Integer.parseInt(dataTS[1]));
                mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt(dataTS[2]) );
                mAppPreferences.setSiteNameEnable(Integer.parseInt(dataTS[3])  );
                mAppPreferences.setAutoDateTime(dataTS[4]);
                mAppPreferences.setSearchTTDateRange(Integer.parseInt(dataTS[5]));
                mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt(dataTS[6]));
                mAppPreferences.setPMImageUploadType(Integer.parseInt(dataTS[7]));
                mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt(dataTS[8]));
                mAppPreferences.setPMReviewPlanDate(Integer.parseInt(dataTS[9]));
                mAppPreferences.setVideoUploadMaxSize( Integer.parseInt(dataTS[10]));
                mAppPreferences.setIsVideoCompress(Integer.parseInt(dataTS[11]));
                if (dataTS.length > 13) {
                    mAppPreferences.setOperatorWiseUserField(dataTS[12]);
                    mAppPreferences.setHyperLinkPM(dataTS[13]);
                }
                if (dataTS.length > 14) {
                    mAppPreferences.setCalendarMonth(Integer.parseInt(dataTS[14]));
                }
                if (dataTS.length > 15) {
                    mAppPreferences.setDocumentUploadMaxSize(Integer.parseInt(dataTS[15]));
                }
            }
        }
        mAppPreferences.setSrvcTime(response.getDetails().get(0).getSrvcTime());
        mAppPreferences.setToggleButton("yes");
        if (response.getDetails().get(0).getDataTS().length() != 0) {
            String[] dataTS = mAppPreferences.getDataTS().split(",");
            dataTypeID = new String[dataTS.length];
            timeStamp = new String[dataTS.length];
            for (int i = 0; i < dataTS.length; i++) {
                tmpDataTS = dataTS[i].split("\\~");
                dataTypeID[i] = tmpDataTS[0];
                timeStamp[i] = tmpDataTS[1];
            }
            if (!mAppPreferences.getFirstTimeRunApp().equalsIgnoreCase("A")) {
                dbHelper.clearDataTS();
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 0,"0");
                dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),0);
                mAppPreferences.setFirstTimeRunApp("A");
            } else {
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 1,"0");
                dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),1);
            }
        }

        /*if (response.getForm()!=null && response.getForm().size() > 0) {
            AppConstants.moduleList.clear();
            dbHelper.clearFormRights();
            dbHelper.insertFormRight(response.getForm());
            Intent i = new Intent(HomeActivity.this, HomeActivity.class);
            i.putExtra("refresh", "1"); //restriction multiple time call
            startActivity(i);
            finish();
        } else {
           Utils.toastMsg(HomeActivity.this, "You are not authorized to access modules.");
           new LogoutTask(HomeActivity.this).execute();
        }*/

        dbHelper.close();

    }

    public void version() { // check app version
        mAppPreferences.setLoginState(2);
        Intent i = new Intent(HomeActivity.this, Version.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public String getIPAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()) ) {
                        return  inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }


    public void updateNotificationCount(){
        DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
        dbHelper.open();
        notification_list = dbHelper.getNotificationCount( mAppPreferences.getUserId(), "0" );
        dbHelper.close();
        if(nsRight.contains("V")) {
            notification.setVisibility( View.VISIBLE );
        }
        if (nsRight.contains("V") && notification_list != null && notification_list.size() > 0) {
            badge.setText( "" + notification_list.size() );
            badge.setTextSize( 10 );
            badge.show();
            ShortcutBadger.removeCount( HomeActivity.this );
            ShortcutBadger.applyCount( HomeActivity.this, notification_list.size() );
        }
    }


}
