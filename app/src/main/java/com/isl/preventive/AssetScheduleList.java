package com.isl.preventive;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.GPSTracker;
import com.isl.modal.AssetList;
import com.isl.util.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import infozech.itower.R;

public class AssetScheduleList extends Activity {
    String latitude,longitude;
    ListView lv_equiment;
    AssetList asset_list;
    DataBaseHelper db;
    String moduleUrl = "",serUrl = "",equipmentId="",txn="",scheduledDate = "";
    AppPreferences mAppPreferences;
    int pendingCount = 0;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list);
        mAppPreferences = new AppPreferences(AssetScheduleList.this);
        db = new DataBaseHelper(AssetScheduleList.this);
        db.open();
        moduleUrl = db.getModuleIP("Schedule");
        equipmentId = getIntent().getExtras().getString("equipmentId");
        txn = getIntent().getExtras().getString("txn");

        Button bt_back = (Button) findViewById(R.id.button_back);
        Utils.msgButton(AssetScheduleList.this,"71",bt_back);
        if(getIntent().getExtras().getString("S").equalsIgnoreCase("S")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("M")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
            alertMsg();
        }
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        TextView tv_add_asset = (TextView) findViewById(R.id.tv_add_asset);
        if(getIntent().getExtras().getString("S").equalsIgnoreCase("D")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("P")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("J")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("V")){
            tv_add_asset.setVisibility(View.GONE);
        }
        tv_add_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AssetScheduleList.this, AddNewAsset.class);
                i.putExtra("calling", "assets");
                i.putExtra("valData", getIntent().getSerializableExtra("valData"));
                i.putExtra("readingData", getIntent().getSerializableExtra("readingData"));
                i.putExtra("equipmentId", getIntent().getExtras().getString("equipmentId"));
                i.putExtra("scheduledDate",getIntent().getExtras().getString("scheduledDate"));
                i.putExtra("S",getIntent().getExtras().getString("S"));
                i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
                i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
                i.putExtra("activityTypeId",getIntent().getExtras().getString("activityTypeId"));
                i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
                i.putExtra("Status", getIntent().getExtras().getString("Status"));
                i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
                i.putExtra("txn", getIntent().getExtras().getString("txn"));
                i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
                i.putExtra("preMinImage", "0");
                i.putExtra("preMaxImage", "0");
                i.putExtra("postMinImage", "0");
                i.putExtra("postMaxImage", "0");
                i.putExtra("imageName", "");
                i.putExtra( "rvDate", "");
                i.putExtra( "rejRmks", "");
                i.putExtra( "rCat", "");
                i.putExtra( "latitude", getIntent().getExtras().getString("latitude"));
                i.putExtra( "longitude", getIntent().getExtras().getString("longitude"));
                startActivity(i);
                //finish();
            }
        });

        TextView tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        Utils.msgText(AssetScheduleList.this, "887", tv_brand_logo);

        lv_equiment = (ListView) findViewById(R.id.lv_equiment);
        /*if (Utils.isNetworkAvailable(AssetScheduleList.this)) {
            new getScheduleAsset(AssetScheduleList.this).execute();
        } else {
            Utils.toast( AssetScheduleList.this, "17" );
        }*/


        lv_equiment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                if(isUserLocation(asset_list.getAseetList().get(arg2).getSTATUS())){
                    int abc = 1;
                    String rating = "0";
                    try{
                        abc = Integer.parseInt(getIntent().getExtras().getString("pendingCount"));
                    } catch (Exception e) {
                       abc = 1;
                    }

                    if(abc==1){
                        if(pendingCount==1){
                            rating = "1";
                        } else{
                            rating = "0";
                        }
                    }
                    Intent i = new Intent( AssetScheduleList.this,AssetDescription.class);
                    i.putExtra("assetId",asset_list.getAseetList().get(arg2).getASSET_ID());
                    i.putExtra("qrCode", asset_list.getAseetList().get(arg2).getQR_CODE());
                    i.putExtra("assetStatus",asset_list.getAseetList().get(arg2).getSTATUS());
                    i.putExtra("equipmentId", asset_list.getAseetList().get(arg2).getINITIATIVE_ID());
                    i.putExtra("equipType", asset_list.getAseetList().get(arg2).getINITIATIVE_NAME());
                    i.putExtra("brand",asset_list.getAseetList().get(arg2).getBRAND());
                    i.putExtra("model",asset_list.getAseetList().get(arg2).getMODEL());
                    i.putExtra("serialNo",asset_list.getAseetList().get(arg2).getSERIAL_NUMBER());
                    i.putExtra("capacity",asset_list.getAseetList().get(arg2).getCAPACITY());
                    i.putExtra("patDate", asset_list.getAseetList().get(arg2).getPAT_DATE());
                    i.putExtra("scrapDate",asset_list.getAseetList().get(arg2).getSCRAP_DATE());
                    i.putExtra("type", asset_list.getAseetList().get(arg2).getTYPE());
                    i.putExtra("field1",asset_list.getAseetList().get(arg2).getFIELD1());
                    i.putExtra("field2",asset_list.getAseetList().get(arg2).getFIELD2());
                    i.putExtra("field3",asset_list.getAseetList().get(arg2).getFIELD3());
                    i.putExtra("field4",asset_list.getAseetList().get(arg2).getFIELD4());
                    i.putExtra("field5",asset_list.getAseetList().get(arg2).getFIELD5());
                    i.putExtra("field6",asset_list.getAseetList().get(arg2).getFIELD6());
                    i.putExtra("field7",asset_list.getAseetList().get(arg2).getFIELD7());
                    i.putExtra("field8",asset_list.getAseetList().get(arg2).getFIELD8());
                    i.putExtra("field9",asset_list.getAseetList().get(arg2).getFIELD9());
                    i.putExtra("field10",asset_list.getAseetList().get(arg2).getFIELD10());
                    i.putExtra("qrCodeCond",asset_list.getAseetList().get(arg2).getQR_CODE_COND());
                    i.putExtra("brandCond",asset_list.getAseetList().get(arg2).getBRAND_COND());
                    i.putExtra("modelCond",asset_list.getAseetList().get(arg2).getMODEL_COND());
                    i.putExtra("serialNoCond",asset_list.getAseetList().get(arg2).getSERIAL_NUMBER_COND());
                    i.putExtra("ASSET_FOUND_COND",asset_list.getAseetList().get(arg2).getASSET_FOUND_COND());
                    i.putExtra("ASSET_TAG_COND", asset_list.getAseetList().get(arg2).getASSET_TAG_COND());
                    i.putExtra("AssetDgType", asset_list.getAseetList().get(arg2).getDG_TYPE());
                    i.putExtra("valData", getIntent().getSerializableExtra("valData"));
                    i.putExtra("readingData", getIntent().getSerializableExtra("readingData"));
                    i.putExtra("scheduledDate",getIntent().getExtras().getString("scheduledDate"));
                    i.putExtra("S",getIntent().getExtras().getString("S"));
                    i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
                    i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
                    i.putExtra("activityTypeId",getIntent().getExtras().getString("activityTypeId"));
                    i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
                    i.putExtra("Status", getIntent().getExtras().getString("Status"));
                    i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
                    i.putExtra("txn", getIntent().getExtras().getString("txn"));
                    i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));

                    i.putExtra("asdt", getIntent().getExtras().getString("asdt"));
                    i.putExtra("aedt", getIntent().getExtras().getString("aedt"));
                    i.putExtra( "arId", getIntent().getExtras().getString("arId"));
                    i.putExtra("amStatus", getIntent().getExtras().getString("amStatus"));
                    i.putExtra("rating",rating);

                    i.putExtra("preMinImage", "0");
                    i.putExtra("preMaxImage", "0");
                    i.putExtra("postMinImage", "0");
                    i.putExtra("postMaxImage", "0");
                    i.putExtra("imageName", "");
                    i.putExtra( "rvDate", asset_list.getAseetList().get(arg2).getREJECT_DATE());
                    i.putExtra( "rejRmks", asset_list.getAseetList().get(arg2).getREJECT_REMARKS());
                    i.putExtra( "rCat", asset_list.getAseetList().get(arg2).getREJECT_CAT());
                    //i.putExtra( "latitude", getIntent().getExtras().getString("latitude"));
                    //i.putExtra( "longitude", getIntent().getExtras().getString("longitude"));
                    i.putExtra( "latitude",latitude);
                    i.putExtra( "longitude",longitude);
                    i.putExtra( "actionreq", asset_list.getAseetList().get(arg2).getACTION_REQ());
                    i.putExtra( "assetRmks", asset_list.getAseetList().get(arg2).getASSET_REMARKS());
                    startActivity( i );
                 }
             }
        });
    }

    public class getScheduleAsset extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        public getScheduleAsset(Context con) {
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
                String url = "";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("txnId",txn));
                nameValuePairs.add(new BasicNameValuePair("equipId",equipmentId));
                if(moduleUrl.equalsIgnoreCase("0")){
                    serUrl=mAppPreferences.getConfigIP()+ WebMethods.url_get_AssetList;
                }else{
                    serUrl=moduleUrl+ WebMethods.url_get_AssetList;
                }
                String response_site = Utils.httpPostRequest(con,serUrl, nameValuePairs);
                Gson gson = new Gson();
                asset_list = gson.fromJson(response_site,AssetList.class);
            } catch (Exception e) {
                e.printStackTrace();
                asset_list = null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (asset_list == null)
            {
                Utils.toast(AssetScheduleList.this, "13");
                //Toast.makeText(MyFillingSchedules.this,"Server Not Available", Toast.LENGTH_LONG).show();
            }else if (asset_list.getAseetList()!=null && asset_list.getAseetList().size() > 0) {
                lv_equiment.setAdapter(new AssetAdapter(AssetScheduleList.this, asset_list));
            }else {
                Toast.makeText(AssetScheduleList.this,"No Asset found", Toast.LENGTH_LONG).show();
            }
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }

            super.onPostExecute(result);
        }
    }

    public class AssetAdapter extends BaseAdapter {
        Context con;
        private LayoutInflater inflater = null;
        AssetList data_list;
        public AssetAdapter(Context con, AssetList list) {
            this.con = con;
            this.data_list = list;
            pendingCount = 0;
            inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data_list.getAseetList().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View arg1, ViewGroup parent) {
            View vi = arg1;
            if (arg1 == null)
                vi = inflater.inflate(R.layout.list_item_schedule, null);

            TextView site_id = (TextView) vi.findViewById(R.id.txt_site_id);
            site_id.setTypeface( Utils.typeFace(con));
            site_id.setTextSize(16);
            site_id.setText(data_list.getAseetList().get(position).getSITE_ID());

            if(data_list.getAseetList().get(position).getNEW_ASSET_FLAG()!=null
                    && data_list.getAseetList().get(position).getNEW_ASSET_FLAG()
                    .equalsIgnoreCase("Y")){
                site_id.setText(data_list.getAseetList().get(position).getSITE_ID()
                +"(New Asset Found)");
            }

            TextView txt_dgtype = (TextView) vi.findViewById(R.id.txt_dgtype);
            txt_dgtype.setVisibility(View.VISIBLE);
            txt_dgtype.setTypeface(Utils.typeFace(con));
            txt_dgtype.setText(data_list.getAseetList().get(position).getINITIATIVE_NAME());


            TextView txt_status = (TextView) vi.findViewById(R.id.txt_status);
            txt_status.setTypeface(Utils.typeFace(con));

            if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("S")){
                Utils.msgText(con, "219", txt_status);
            }else if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("M")){
                Utils.msgText(con, "222", txt_status);
            }else if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("D")){
                Utils.msgText(con, "223", txt_status);
//                String ASSET_FOUND_COND = data_list.getAseetList().get(position).getASSET_FOUND_COND();
//                if(ASSET_FOUND_COND!=null
//                        && ASSET_FOUND_COND.equalsIgnoreCase("Asset missing")){
//
//                }else{
                    pendingCount  = pendingCount + 1;
                //}

            }else if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("R")){
                Utils.msgText(con, "221", txt_status);
            }else if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("J")){
                Utils.msgText(con, "466", txt_status);
            }else if(data_list.getAseetList().get(position).getSTATUS().equalsIgnoreCase("RS")){
                txt_status.setText("Rescheduled");
            }
            else{
                txt_status.setText(data_list.getAseetList().get(position).getSTATUS());
            }

            TextView txt_rp_date = (TextView) vi.findViewById(R.id.txt_rp_date);
            txt_rp_date.setTypeface(Utils.typeFace(con));
            txt_rp_date.setVisibility(View.GONE);

            TextView txt_checklist_type = (TextView) vi.findViewById(R.id.txt_checklist_type);
            txt_checklist_type.setTypeface(Utils.typeFace(con));
            txt_checklist_type.setVisibility(View.VISIBLE);
            if(data_list.getAseetList().get(position).getASSET_ID()!=null
               && data_list.getAseetList().get(position).getASSET_ID().length()>0){
                txt_checklist_type.setText("Asset Id : "+data_list.getAseetList().get(position).getASSET_ID());
            }else{
                txt_checklist_type.setText("Asset Id : ");
            }


            TextView txt_date = (TextView) vi.findViewById(R.id.txt_date);
            txt_date.setTypeface(Utils.typeFace(con));
            txt_date.setVisibility(View.GONE);

            TextView txt_ttikd = (TextView) vi.findViewById(R.id.txt_ttikd);
            txt_ttikd.setTypeface(Utils.typeFace(con));
            txt_ttikd.setVisibility(View.VISIBLE);
            txt_ttikd.setText(getIntent().getExtras().getString("scheduledDate"));

            return vi;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(AssetScheduleList.this)) {
            new getScheduleAsset(AssetScheduleList.this).execute();
        } else {
            Utils.toast( AssetScheduleList.this, "17" );
        }
    }

    private boolean isUserLocation(String assetStatus) {
        boolean status = true;
        latitude = getIntent().getExtras().getString("latitude");
        longitude = getIntent().getExtras().getString("longitude");
        //i.putExtra( "latitude", getIntent().getExtras().getString("latitude"));
        //i.putExtra( "longitude", getIntent().getExtras().getString("longitude"));
        if(assetStatus.equalsIgnoreCase("S")
                || assetStatus.equalsIgnoreCase("M")) {
            GPSTracker gps = new GPSTracker(AssetScheduleList.this);
            if (gps.canGetLocation() == false) {
                gps.showSettingsAlert();
                return false;
            } else if (!Utils.hasPermissions(AssetScheduleList.this, AppConstants.LOCATION_PERMISSIONS)
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(AssetScheduleList.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                return false;
            } else if (gps.canGetLocation() == true) {

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(AssetScheduleList.this);
                if (ActivityCompat.checkSelfPermission(AssetScheduleList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(AssetScheduleList.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }).addOnFailureListener(AssetScheduleList.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

                if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                        || (longitude == null || latitude.equalsIgnoreCase("0.0")
                        || longitude.isEmpty())) {
                    // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                    Utils.toast(AssetScheduleList.this, "252");
                    return false;
                } else {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(AssetScheduleList.this);
                    if (ActivityCompat.checkSelfPermission(AssetScheduleList.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(AssetScheduleList.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                            }
                        }).addOnFailureListener(AssetScheduleList.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }
        }

        return true;
    }

    public void alertMsg() {
        final Dialog actvity_dialog = new Dialog(AssetScheduleList.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.popup_information);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(AssetScheduleList.this));
        positive.setText("OK");

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(AssetScheduleList.this));
        negative.setVisibility(View.GONE);
        negative.setText("NO");

        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(AssetScheduleList.this));
        tv_header.setVisibility(View.VISIBLE);
        String msg = Utils.msg(AssetScheduleList.this,"909");
        tv_header.setText(msg);

        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setVisibility(View.VISIBLE);
        //title.setTypeface(Utils.typeFace(EquipmentList.this));
        title.setPaintFlags(title.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        String msg1 = Utils.msg(AssetScheduleList.this,"908");
        title.setText(msg1);

        TextView title2 = (TextView) actvity_dialog.findViewById(R.id.tv_title2);
        title2.setVisibility(View.VISIBLE);
        //title2.setTypeface(Utils.typeFace(EquipmentList.this));
        String msg2 = Utils.msg(AssetScheduleList.this,"907");
        title2.setText(msg2);


        negative.setText("NO");


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

}
