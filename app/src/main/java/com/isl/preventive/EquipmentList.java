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
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.util.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import infozech.itower.R;

public class EquipmentList extends Activity {
    ListView lv_equiment;
    AssetList asset_list;
    AssetList equipment_list;
    DataBaseHelper db;
    String moduleUrl = "", serUrl = "";
    AppPreferences mAppPreferences;
    private FusedLocationProviderClient fusedLocationClient;
    String latitude, longitude;
    int pendingCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list);
        mAppPreferences = new AppPreferences(EquipmentList.this);
        db = new DataBaseHelper(EquipmentList.this);
        db.open();
        moduleUrl = db.getModuleIP("Schedule");
        db.close();
        if(getIntent().getExtras().getString("S").equalsIgnoreCase("S")
           || getIntent().getExtras().getString("S").equalsIgnoreCase("M")
           || getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
            alertMsg();
        }
        Button bt_back = (Button) findViewById(R.id.button_back);
        Utils.msgButton(EquipmentList.this, "71", bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("NEXT_7_DAYS")
                        || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_YESTERDAY")
                        || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("RESUBMITTED")
                        || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_TODAY")
                        ||mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("APPROVAL_PENDING")
                        || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_TODAY_APPROVED")
                        || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("REJECTED")){
                    Intent i = new Intent(EquipmentList.this,ScheduleModuleTab.class);
                    i.putExtra("type", mAppPreferences.getPMScheduleCountType());
                    startActivity(i);
                    finish();
                }else if (mAppPreferences.getPMAssetBackTask() == 2) {
                    Intent i = new Intent(EquipmentList.this, PMTabs.class);
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }

            }
        });

        TextView tv_add_asset = (TextView) findViewById(R.id.tv_add_asset);
        if (getIntent().getExtras().getString("S").equalsIgnoreCase("D")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("V")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("P")
                || getIntent().getExtras().getString("S").equalsIgnoreCase("J")) {
            tv_add_asset.setVisibility(View.GONE);
        }

        tv_add_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EquipmentList.this, AddNewAsset.class);
                i.putExtra("calling", "equipments");
                i.putExtra("valData", getIntent().getSerializableExtra("valData"));
                i.putExtra("readingData", getIntent().getSerializableExtra("readingData"));
                i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
                i.putExtra("S", getIntent().getExtras().getString("S"));
                i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
                i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
                i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
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
                i.putExtra("rvDate", "");
                i.putExtra("rejRmks", "");
                i.putExtra("rCat", "");
                i.putExtra("latitude", getIntent().getExtras().getString("latitude"));
                i.putExtra("longitude", getIntent().getExtras().getString("longitude"));
                startActivity(i);
                //finish();
            }
        });
        TextView tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        Utils.msgText(EquipmentList.this, "886", tv_brand_logo);
        lv_equiment = (ListView) findViewById(R.id.lv_equiment);
        /*if (Utils.isNetworkAvailable(EquipmentList.this)) {
            new getScheduleEquipment(EquipmentList.this).execute();
        } else {
            Utils.toast( EquipmentList.this, "17" );
        }*/
        lv_equiment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent i = null;


                if (asset_list.getAseetList().get(arg2).getINITIATIVE_ID()
                        .equalsIgnoreCase("-1")) {
                    //schedule tab
                    //activity done but open through schedule tab
                    if (asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("S")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }//activity schedule but open through schedule tab
                    else if (getIntent().getExtras().getString("S").equalsIgnoreCase("S") &&
                            isUserLocation() && asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("0")
                    ) {
                        mAppPreferences.setPMAssetBackTask(1);
                        i = new Intent(EquipmentList.this, PMChecklist.class);
                        i.putExtra("assetId", "-1");
                        i.putExtra("equipmentId", "-1");
                        i.putExtra("qrCode", "");
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        pmChecklist(i, arg2, "-1");
                    }
                    //missed tab
                    //activity done but open through missed tab
                    else if (asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("M")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }//activity missed but open through missed tab
                    else if (getIntent().getExtras().getString("S").equalsIgnoreCase("M") &&
                            isUserLocation() && asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("0")
                    ) {
                        mAppPreferences.setPMAssetBackTask(1);
                        i = new Intent(EquipmentList.this, PMChecklist.class);
                        i.putExtra("assetId", "-1");
                        i.putExtra("equipmentId", "-1");
                        i.putExtra("S", getIntent().getExtras().getString("S"));
                        i.putExtra("qrCode", "");
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        pmChecklist(i, arg2, "-1");
                    }
                    //re-submit tab
                    else if (asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    } else if (asset_list.getAseetList().get(arg2).getREV_CNT_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    } else if (asset_list.getAseetList().get(arg2).getREJ_CNT_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    } else if (asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("0")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
                        mAppPreferences.setPMAssetBackTask(1);
                        i = new Intent(EquipmentList.this, PMChecklist.class);
                        i.putExtra("assetId", "-1");
                        i.putExtra("equipmentId", "-1");
                        i.putExtra("qrCode", "");
                        i.putExtra("latitude", getIntent().getExtras().getString("latitude"));
                        i.putExtra("longitude", getIntent().getExtras().getString("longitude"));
                        pmChecklist(i, arg2, "-1");
                    }
                    //done tab
                    //activity done but open through done tab
                    else if (getIntent().getExtras().getString("S").equalsIgnoreCase("D")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }
                    //verify tab
                    //activity verify but open through verify tab
                    else if (getIntent().getExtras().getString("S").equalsIgnoreCase("V")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }
                    //Reject tab
                    //activity Reject but open through Reject tab
                    else if (getIntent().getExtras().getString("S").equalsIgnoreCase("J")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }
                    //pending tab
                    //activity done but open through pending tab
                    else if (asset_list.getAseetList().get(arg2).getCOUNT_D_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("P")) {
                        PMChecklistApproval();
                    }//activity verify but open through pending tab
                    else if (asset_list.getAseetList().get(arg2).getREV_CNT_STATUS()
                            .equalsIgnoreCase("1")
                            && getIntent().getExtras().getString("S").equalsIgnoreCase("P")) {
                        i = new Intent(EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }/*else{
                        i = new Intent( EquipmentList.this, ViewPMCheckList.class);
                        viewPMChecklist(i);
                    }*/
                } else {
                    i = new Intent(EquipmentList.this, AssetScheduleList.class);
                    i.putExtra("equipmentId", asset_list.getAseetList().get(arg2).getINITIATIVE_ID());
                    assetList(i, arg2, "-1");
                    //pmChecklist(i,arg2,"-1");
                }
            }
        });
    }

    public class getScheduleEquipment extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;

        public getScheduleEquipment(Context con) {
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
                nameValuePairs.add(new BasicNameValuePair("txnId", getIntent().getExtras().getString("txn")));
                nameValuePairs.add(new BasicNameValuePair("equipId", "0"));
                if (moduleUrl.equalsIgnoreCase("0")) {
                    serUrl = mAppPreferences.getConfigIP() + WebMethods.url_get_AssetList;
                } else {
                    serUrl = moduleUrl + WebMethods.url_get_AssetList;
                }
                String response_site = Utils.httpPostRequest(con, serUrl, nameValuePairs);
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
            if (asset_list == null) {
                Utils.toast(EquipmentList.this, "13");
                //Toast.makeText(MyFillingSchedules.this,"Server Not Available", Toast.LENGTH_LONG).show();
            } else if (asset_list.getAseetList() != null && asset_list.getAseetList().size() > 0) {
                lv_equiment.setAdapter(new EquipmentAdapter(EquipmentList.this,
                        asset_list));
                lv_equiment.setVisibility(View.VISIBLE);
                db.close();
            } else {
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            super.onPostExecute(result);
        }
    }

    public class EquipmentAdapter extends BaseAdapter {
        Context con;
        private LayoutInflater inflater = null;
        AssetList data_list;

        public EquipmentAdapter(Context con, AssetList list) {
            pendingCount = 0;
            this.con = con;
            this.data_list = list;
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
                vi = inflater.inflate(R.layout.custom_equipment, null);
            TextView name = (TextView) vi.findViewById(R.id.name);
            name.setTypeface(Utils.typeFace(con));
            name.setText(data_list.getAseetList().get(position).getINITIATIVE_NAME());
            TextView count = (TextView) vi.findViewById(R.id.count);

            if (getIntent().getExtras().getString("S").equalsIgnoreCase("S")
                    || getIntent().getExtras().getString("S").equalsIgnoreCase("M")) {
                count.setText(data_list.getAseetList().get(position).getCOUNT_D_STATUS()
                        + "/" + data_list.getAseetList().get(position).getTOTAL_COUNT());
            } else {
                int c = 0;
                int d = 0;
                try {
                    int a = Integer.parseInt(data_list.getAseetList().get(position).getREV_CNT_STATUS());
                    int b = Integer.parseInt(data_list.getAseetList().get(position).getREJ_CNT_STATUS());
                    d = Integer.parseInt(data_list.getAseetList().get(position).getTOTAL_COUNT());
                    c = a + b;
                } catch (Exception e) {
                    c = 0;
                    d = 0;
                }
                count.setText(c + "/" + data_list.getAseetList().get(position).getTOTAL_COUNT());
                if(c<d){
                    pendingCount = pendingCount+1;
                }

            }

            if (data_list.getAseetList().get(position).getINITIATIVE_ID()
                    .equalsIgnoreCase("-1")) {
                name.setText(data_list.getAseetList().get(position).getINITIATIVE_NAME() + " CHECKLIST");
            }
            return vi;
        }
    }

    // Class to call Web Service to get PM CheckList to draw form.
    private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String siteId = "", chklistType = "0", txnId = "";
        BeanCheckListDetails PMCheckList;
        String equipmentID = "-1";
        String assetId = "-1";

        private GetPMCheckList(Context con, Intent i, String siteId, String chklistType,
                               String txnId, String equipmentID, String assetId) {
            this.con = con;
            this.i = i;
            this.siteId = siteId;
            this.chklistType = chklistType;
            this.txnId = txnId;
            this.equipmentID = equipmentID;
            this.assetId = assetId;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Gson gson = new Gson();
            nameValuePairs.add(new BasicNameValuePair("siteId", siteId));//
            nameValuePairs.add(new BasicNameValuePair("checkListType", chklistType)); // 0 means all checklist(20001,20002,20005...) data download
            nameValuePairs.add(new BasicNameValuePair("checkListDate", getIntent().getExtras().getString("scheduledDate")));
            nameValuePairs.add(new BasicNameValuePair("status", "S")); //S or M get blank checklistdata
            nameValuePairs.add(new BasicNameValuePair("dgType", ""));
            nameValuePairs.add(new BasicNameValuePair("languageCode", mAppPreferences.getLanCode()));
            nameValuePairs.add(new BasicNameValuePair("assetID", ""));
            nameValuePairs.add(new BasicNameValuePair("equipID", equipmentID));
            try {
                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_getCheckListDetails;
                } else {
                    url = moduleUrl + WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest(con, url, nameValuePairs);
                PMCheckList = gson.fromJson(res, BeanCheckListDetails.class);
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
                Utils.toast(EquipmentList.this, "226");
            } else if (PMCheckList != null) {
                if (PMCheckList.getPMCheckListDetail() != null
                        && PMCheckList.getPMCheckListDetail().size() > 0) {
                    db.open();

                    int alreadyJson = 0;
                    if (db.isAlreadyAutoSaveChk(txnId, assetId) == 0) {
                        db.insertAutoSaveChkList(txnId, "", "", "", assetId);
                        alreadyJson = 1;
                    }
                    db.clearCheckList("655", chklistType, equipmentID);
                    db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),
                            "655", alreadyJson, txnId, EquipmentList.this,
                            equipmentID, assetId);
                    db.close();
                }
            } else {
                Utils.toast(EquipmentList.this, "13");
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            startActivity(i);
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(EquipmentList.this)) {
            new getScheduleEquipment(EquipmentList.this).execute();
        } else {
            Utils.toast(EquipmentList.this, "17");
        }
    }

    public void viewPMChecklist(Intent i) {

        String txnId = "", sDate = "", actid = "", sid = "", etsId = "", dgType = "",
                imguploadflag = "2", assetID = "-1";

        i.putExtra("assetId", "-1");
        i.putExtra("equipmentId", "-1");
        i.putExtra("qrCode", "");
        i.putExtra("S", getIntent().getExtras().getString("S"));
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
        i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
        i.putExtra("Status", getIntent().getExtras().getString("Status"));
        i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
        i.putExtra("txn", getIntent().getExtras().getString("txn"));
        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
        i.putExtra("imgUploadFlag", imguploadflag);
        i.putExtra("rCat", "");
        i.putExtra("rejRmks", "");
        i.putExtra("rvDate", "");

        sid = getIntent().getExtras().getString("siteId");
        sDate = getIntent().getExtras().getString("scheduledDate");
        actid = getIntent().getExtras().getString("activityTypeId");
        txnId = getIntent().getExtras().getString("txn");
        dgType = getIntent().getExtras().getString("dgType");
        etsId = getIntent().getExtras().getString("etsSid");

        if (Utils.isNetworkAvailable(EquipmentList.this)) {
            new GetImage(EquipmentList.this, i, txnId, sDate, actid, sid, etsId, dgType,
                    imguploadflag, assetID, "D", 0).execute();
        } else {
            //No Internet Connection;
            Utils.toast(EquipmentList.this, "17");
        }
    }

    public void PMChecklistApproval() {
        mAppPreferences.setPMBackTask(-1);
        mAppPreferences.setPMAssetBackTask(1);
        Intent i = new Intent(EquipmentList.this, PMChecklistApproval.class);
        String txnId = "", sDate = "", actid = "", sid = "", etsId = "", dgType = "",
                imguploadflag = "2", assetID = "-1";

        i.putExtra("assetId", "-1");
        i.putExtra("equipmentId", "-1");
        i.putExtra("S", "P");
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
        i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
        i.putExtra("Status", getIntent().getExtras().getString("Status"));
        i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
        i.putExtra("txn", getIntent().getExtras().getString("txn"));
        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
        i.putExtra("imgUploadFlag", imguploadflag);
        i.putExtra("rCat", "");
        i.putExtra("rejRmks", "");
        i.putExtra("rvDate", "");

        i.putExtra("asdt", getIntent().getExtras().getString("asdt"));
        i.putExtra("aedt", getIntent().getExtras().getString("aedt"));
        i.putExtra( "arId", getIntent().getExtras().getString("arId"));
        i.putExtra("amStatus", getIntent().getExtras().getString("amStatus"));
        i.putExtra("rating",""+pendingCount);


        sid = getIntent().getExtras().getString("siteId");
        sDate = getIntent().getExtras().getString("scheduledDate");
        actid = getIntent().getExtras().getString("activityTypeId");
        txnId = getIntent().getExtras().getString("txn");
        dgType = getIntent().getExtras().getString("dgType");
        etsId = getIntent().getExtras().getString("etsSid");
        //assetID = getIntent().getExtras().getString("assetId");
        db.open();
        db.deleteAutoSaveChk(txnId, assetID);
        db.insertAutoSaveChkList(txnId, "", "", "", assetID);
        /*if(db.isAlreadyAutoSaveChk(txnId)== 0){
            db.insertAutoSaveChkList(txnId,"","","");
            temp_flag =1;
        }
        else
        {
            temp_flag=0;
        }*/
        db.close();

        if (Utils.isNetworkAvailable(EquipmentList.this)) {
            new GetImage(EquipmentList.this, i, txnId, sDate, actid, sid, etsId, dgType,
                    imguploadflag, assetID, "D", 1).execute();
        } else {
            //No Internet Connection;
            Utils.toast(EquipmentList.this, "17");
        }
    }

    public void assetList(Intent i, int pos, String assetId) {
        i.putExtra("S", getIntent().getExtras().getString("S"));
        i.putExtra("valData", getIntent().getSerializableExtra("valData"));
        i.putExtra("readingData", getIntent().getSerializableExtra("readingData"));
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
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
        i.putExtra("rvDate", "");
        i.putExtra("rejRmks", "");
        i.putExtra("rCat", "");
        i.putExtra("latitude", getIntent().getExtras().getString("latitude"));
        i.putExtra("longitude", getIntent().getExtras().getString("longitude"));

        i.putExtra("asdt", getIntent().getExtras().getString("asdt"));
        i.putExtra("aedt", getIntent().getExtras().getString("aedt"));
        i.putExtra( "arId", getIntent().getExtras().getString("arId"));
        i.putExtra("amStatus", getIntent().getExtras().getString("amStatus"));
        i.putExtra("pendingCount",""+pendingCount);

        startActivity(i);

    }


    public void pmChecklist(Intent i, int pos, String assetId) {
        i.putExtra("S", getIntent().getExtras().getString("S"));
        i.putExtra("valData", getIntent().getSerializableExtra("valData"));
        i.putExtra("readingData", getIntent().getSerializableExtra("readingData"));
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
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
        i.putExtra("rvDate", "");
        i.putExtra("rejRmks", "");
        i.putExtra("rCat", "");
        i.putExtra("latitude", getIntent().getExtras().getString("latitude"));
        i.putExtra("longitude", getIntent().getExtras().getString("longitude"));

        if (getIntent().getExtras().getString("S").equalsIgnoreCase("RS")) {
            if (Utils.isNetworkAvailable(EquipmentList.this)) {
                new GetImage(EquipmentList.this, i,
                        getIntent().getExtras().getString("txn"),
                        getIntent().getExtras().getString("scheduledDate"),
                        getIntent().getExtras().getString("activityTypeId"),
                        getIntent().getExtras().getString("siteId"),
                        getIntent().getExtras().getString("etsSid"),
                        getIntent().getExtras().getString("dgType"),
                        "2", "-1", "D", 2).execute();
            } else {
                //No Internet Connection;
                Utils.toast(EquipmentList.this, "17");
            }
        } else {
            new GetPMCheckList(EquipmentList.this, i,
                    getIntent().getExtras().getString("siteId"),
                    getIntent().getExtras().getString("activityTypeId"),
                    getIntent().getExtras().getString("txn"),
                    asset_list.getAseetList().get(pos).getINITIATIVE_ID(),
                    assetId).execute();
        }


    }

    private class GetImage extends AsyncTask<Void, Void, Void> {
        Context con;
        ProgressDialog pd;
        BeanGetImageList imageList;
        String txnId, scDate, activityId, sId, dgType, etsSid, imguploadflag, assetID, status = "D";
        ;
        Intent i;
        int mode = 0;

        private GetImage(Context con, Intent i, String txnId, String scDate, String activityId, String sId,
                         String etsSid, String dgType, String imguploadflag, String assetID,
                         String status, int mode) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.etsSid = etsSid;
            this.dgType = dgType;
            this.imguploadflag = imguploadflag;
            this.i = i;
            this.assetID = assetID;
            this.status = status;
            this.mode = mode;
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
                nameValuePairs.add(new BasicNameValuePair("activityType", activityId));
                nameValuePairs.add(new BasicNameValuePair("scheduledDate", scDate));
                nameValuePairs.add(new BasicNameValuePair("dgType", dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID", assetID));
                String url = "";
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_GetPmImage;
                } else {
                    url = moduleUrl + WebMethods.url_GetPmImage;
                }

                String response = Utils.httpPostRequest(con, url, nameValuePairs);
                //response.replaceAll("http://18.136.133.138:9000","https://midc.infozech.com:9000");
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
            DataBaseHelper dataBaseHelper = new DataBaseHelper(EquipmentList.this);
            dataBaseHelper.open();
            dataBaseHelper.deleteActivityImages(txnId, assetID);
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
                                } else if (lat.length() == 1 && imageList.getImageList().get(i).getLATITUDE() != null) {
                                    lat = imageList.getImageList().get(i).getLATITUDE();
                                }


                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                } else if (longi.length() == 1 && imageList.getImageList().get(i).getLONGITUDE() != null) {
                                    longi = imageList.getImageList().get(i).getLONGITUDE();
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 1, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j],
                                        mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI,
                                        assetID
                                );
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
                                } else if (lat.length() == 1 && imageList.getImageList().get(i).getLATITUDE() != null) {
                                    lat = imageList.getImageList().get(i).getLATITUDE();
                                }


                                if (longArr != null && imgpathArr.length <= longArr.length) {
                                    longi = longArr[j];
                                } else if (longi.length() == 1 && imageList.getImageList().get(i).getLONGITUDE() != null) {
                                    longi = imageList.getImageList().get(i).getLONGITUDE();
                                }

                                if (imguploadflag.equalsIgnoreCase("2")) {
                                    clId = imageList.getImageList().get(i).getClID();
                                }
                                dataBaseHelper.insertImages(
                                        txnId, clId, imageList.getImageList().get(i).getImageURL() + imgpathArr[j],
                                        name, lat, longi, Utils.DateTimeStamp(), time, 2, 3,
                                        scDate, activityId, sId, dgType, imgpathArr[j],
                                        mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI,
                                        assetID
                                );
                            }
                        }
                    }
                }
            }
            dataBaseHelper.close();
            if (Utils.isNetworkAvailable(EquipmentList.this)) {
                new CheckListDetailsTask(EquipmentList.this, i, txnId, sId, scDate, dgType,
                        activityId, assetID, status, mode).execute();
            } else {
                Utils.toast(EquipmentList.this, "17");
            }
            super.onPostExecute(result);
        }
    }

    private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String txnId, scDate, activityId, sId, dgType, assetID;
        BeanCheckListDetails PMCheckListDetails;
        String status = "D";
        int mode = 0;

        private CheckListDetailsTask(Context con, Intent i, String txnId, String sId, String scDate,
                                     String dgType, String activityId, String assetID,
                                     String status, int mode) {
            this.con = con;
            this.txnId = txnId;
            this.scDate = scDate;
            this.activityId = activityId;
            this.sId = sId;
            this.dgType = dgType;
            this.i = i;
            this.assetID = assetID;
            this.status = status;
            this.mode = mode;

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
                nameValuePairs.add(new BasicNameValuePair("siteId", sId));
                nameValuePairs.add(new BasicNameValuePair("checkListType", activityId));
                nameValuePairs.add(new BasicNameValuePair("checkListDate", scDate));
                nameValuePairs.add(new BasicNameValuePair("status", status));
                nameValuePairs.add(new BasicNameValuePair("languageCode", mAppPreferences.getLanCode()));
                nameValuePairs.add(new BasicNameValuePair("dgType", dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID", assetID));
                nameValuePairs.add(new BasicNameValuePair("equipID", ""));
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
                Utils.toast(EquipmentList.this, "13");
            } else {
                if (mode == 2) {
                    if (PMCheckListDetails.getPMCheckListDetail() != null &&
                            PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                        for (int a = 0; a < PMCheckListDetails.getPMCheckListDetail().size(); a++) {
                            sharePrefence(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getStatus(), txnId,
                                    assetID);

                            sharePrefenceRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getViRemark(), txnId, assetID);

                            sharePrefenceReviewRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getrRemark(), txnId, assetID);
                        }

                    } else {

                    }
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (Utils.isNetworkAvailable(EquipmentList.this)) {
                        new GetPMCheckList(EquipmentList.this, i, sId, activityId, txnId,
                                "0", assetID).execute();
                    } else {
                        startActivity(i);
                    }
                } else {
                    DataBaseHelper dbHelper = new DataBaseHelper(EquipmentList.this);
                    dbHelper.open();
                    dbHelper.clearReviewerCheclist();
                    if (PMCheckListDetails.getPMCheckListDetail() != null && PMCheckListDetails.getPMCheckListDetail().size() > 0) {

                        if (mode == 1) {
                            dbHelper.insertReviewerCheckList(PMCheckListDetails.getPMCheckListDetail(),
                                    activityId, txnId, EquipmentList.this, 1, assetID);
                        } else {
                            dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(), activityId);
                        }
                        dbHelper.close();

                    }

                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    startActivity(i);
                }
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        if(mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("NEXT_7_DAYS")
                || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_YESTERDAY")
                || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("RESUBMITTED")
                || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_TODAY")
                ||mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("APPROVAL_PENDING")
                || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("TILL_TODAY_APPROVED")
                || mAppPreferences.getPMScheduleCountType().equalsIgnoreCase("REJECTED")){
            Intent i = new Intent(EquipmentList.this,ScheduleModuleTab.class);
            i.putExtra("type", mAppPreferences.getPMScheduleCountType());
            startActivity(i);
            finish();
        }else if (mAppPreferences.getPMAssetBackTask() == 2) {
            Intent i = new Intent(EquipmentList.this, PMTabs.class);
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }

    JSONObject savedDataJsonObjRemarks = null;
    JSONObject savedDataJsonObjReviewRemarks = null;
    JSONObject savedDataJsonObj = null;

    public void sharePrefence(String id, String s, String txnId, String assetID) {

        if (savedDataJsonObj == null) {
            savedDataJsonObj = new JSONObject();
        }

        try {
            savedDataJsonObj.remove("" + id);
            savedDataJsonObj.put("" + id, s);
        } catch (JSONException e) {

        }

        DataBaseHelper db10 = new DataBaseHelper(EquipmentList.this);
        db10.open();
        db10.updateAutoSaveChkList(txnId, "", "", savedDataJsonObj.toString(), assetID);
        db10.close();

    }

    public void sharePrefenceRemarks(String id, String s, String txnId, String assetID) {

        if (savedDataJsonObjRemarks == null) {
            savedDataJsonObjRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjRemarks.remove("" + id);
            savedDataJsonObjRemarks.put("" + id, s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(EquipmentList.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId, savedDataJsonObjRemarks.toString(), assetID);
        //db10.updateAutoSaveRemarks1(txnId,savedDataJsonObjRemarks.toString(),savedDataJsonObjRemarks.toString());
        db10.close();
    }

    public void sharePrefenceReviewRemarks(String id, String s, String txnId, String assetID) {

        if (savedDataJsonObjReviewRemarks == null) {
            savedDataJsonObjReviewRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjReviewRemarks.remove("" + id);
            savedDataJsonObjReviewRemarks.put("" + id, s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(EquipmentList.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId, savedDataJsonObjRemarks.toString(), assetID);
        db10.updateAutoSaveRemarks1(txnId, savedDataJsonObjRemarks.toString(),
                savedDataJsonObjReviewRemarks.toString(), assetID);
        db10.close();
    }

    private boolean isUserLocation() {
        //boolean status = true;
        GPSTracker gps = new GPSTracker(EquipmentList.this);
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (!Utils.hasPermissions(EquipmentList.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(EquipmentList.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
            return false;
        } else if (gps.canGetLocation() == true) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(EquipmentList.this);
            if (ActivityCompat.checkSelfPermission(EquipmentList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(EquipmentList.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    }
                }).addOnFailureListener(EquipmentList.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0")
                    || longitude.isEmpty())) {
                // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(EquipmentList.this, "252");
                return false;
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(EquipmentList.this);
                if (ActivityCompat.checkSelfPermission(EquipmentList.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(EquipmentList.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }).addOnFailureListener(EquipmentList.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                return true;
            }
        }
        return true;
    }

    public void alertMsg() {
        final Dialog actvity_dialog = new Dialog(EquipmentList.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.popup_information);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(EquipmentList.this));
        positive.setText("OK");

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(EquipmentList.this));
        negative.setVisibility(View.GONE);
        negative.setText("NO");

        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(EquipmentList.this));
        tv_header.setVisibility(View.VISIBLE);
        String msg = Utils.msg(EquipmentList.this,"909");
        tv_header.setText(msg);

        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setVisibility(View.VISIBLE);
        //title.setTypeface(Utils.typeFace(EquipmentList.this));
        title.setPaintFlags(title.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        String msg1 = Utils.msg(EquipmentList.this,"908");
        title.setText(msg1);

        TextView title2 = (TextView) actvity_dialog.findViewById(R.id.tv_title2);
        title2.setVisibility(View.VISIBLE);
        //title2.setTypeface(Utils.typeFace(EquipmentList.this));
        String msg2 = Utils.msg(EquipmentList.this,"907");
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