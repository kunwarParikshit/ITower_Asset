package com.isl.incident;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.modal.QRList;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import infozech.itower.R;

public class AddNewAssetTT extends Activity {
    AppPreferences mAppPreferences;
    Spinner sp_equipment_type, sp_asset_id_tag, sp_action;
    TextView tv_equipment_type, tv_asset_id_tag, tv_action,tv_asset_id, tv_manual_brand, tv_manual_model,
            tv_manual_ser_no,tv_manual_qr_code, tvSubmit, tv_capacity, tv_warranty_date,tv_remarks;
    EditText et_manual_brand, et_manual_model, et_manual_serial_no, et_manual_qr_code, et_capacity,
            et_warranty_date,et_asset_id,et_remarks;
    DataBaseHelper db;
    String moduleUrl = "",serUrl = "";
    QRList qrList;
    ArrayList<String> list_equipment_type;
    int scanFlag  = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState); // Move this statement before the try block
            setContentView(R.layout.tt_add_asset);
            mAppPreferences = new AppPreferences(AddNewAssetTT.this);
            db = new DataBaseHelper(AddNewAssetTT.this);
            db.open();
            moduleUrl = db.getModuleIP("Incident");
            db.close();
            initializeControlledID();
        } catch (Exception e) {
            e.printStackTrace();
            // Add a log statement to track the exception details
            Log.e("AddNewAsset", "Exception in onCreate: " + e.toString());
        }
    }

    private void initializeControlledID() {

        tv_equipment_type = (TextView) findViewById(R.id.tv_equipment_type);
        tv_asset_id_tag = (TextView) findViewById(R.id.tv_asset_id_tag);
        tv_action = (TextView) findViewById(R.id.tv_action);
        tv_asset_id = (TextView) findViewById(R.id.tv_asset_id);
        tv_manual_brand = (TextView) findViewById(R.id.tv_manual_brand);
        tv_manual_model = (TextView) findViewById(R.id.tv_manual_model);
        tv_manual_ser_no = (TextView) findViewById(R.id.tv_manual_ser_no);
        tv_manual_qr_code = (TextView) findViewById(R.id.tv_manual_qr_code);
        tv_capacity = (TextView) findViewById(R.id.tv_capacity);
        tv_warranty_date = (TextView) findViewById(R.id.tv_warranty_date);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        tv_remarks  = (TextView) findViewById(R.id.tv_remarks);


        Utils.msgText(AddNewAssetTT.this, "891", tv_equipment_type);
        Utils.msgText(AddNewAssetTT.this, "913", tv_manual_brand);
        Utils.msgText(AddNewAssetTT.this, "878", tv_manual_model);
        Utils.msgText(AddNewAssetTT.this, "879", tv_manual_ser_no);
        Utils.msgText(AddNewAssetTT.this, "923", tv_manual_qr_code);
        Utils.msgText(AddNewAssetTT.this, "910", tv_asset_id_tag);
        Utils.msgText(AddNewAssetTT.this, "911", tv_action);
        Utils.msgText(AddNewAssetTT.this, "885", tv_asset_id);
        Utils.msgText(AddNewAssetTT.this, "889", tvSubmit);
        Utils.msgText(AddNewAssetTT.this, "881", tv_capacity);
        Utils.msgText(AddNewAssetTT.this, "912", tv_warranty_date);
        Utils.msgText(AddNewAssetTT.this, "920", tv_remarks);


        et_asset_id = (EditText) findViewById(R.id.et_asset_id);
        et_manual_brand = (EditText) findViewById(R.id.et_manual_brand);
        et_manual_model = (EditText) findViewById(R.id.et_manual_model);
        et_manual_serial_no = (EditText) findViewById(R.id.et_manual_serial_no);
        et_capacity = (EditText) findViewById(R.id.et_capacity);
        et_remarks  = (EditText) findViewById(R.id.et_remarks);
        et_remarks.setEnabled(true);

        et_warranty_date = (EditText) findViewById(R.id.et_warranty_date);


        et_manual_qr_code = (EditText) findViewById(R.id.et_manual_qr_code);
        et_manual_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator qrScan = new IntentIntegrator(AddNewAssetTT.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(AddNewAssetTT.this, "511"));
                qrScan.initiateScan();
            }
        });

        db.open();
        list_equipment_type = db.getEquipment(tv_equipment_type, mAppPreferences.getTTModuleSelection(),
                mAppPreferences.getUserCategory(), mAppPreferences.getUserSubCategory());

        ArrayList<String> list_action = db.getParamName("1280","654");
        db.close();

        sp_action = (Spinner) findViewById(R.id.sp_action);
        addItemsOnSpinner(sp_action, list_action);

        sp_equipment_type = (Spinner) findViewById(R.id.sp_equipment_type);
        addItemsOnSpinner(sp_equipment_type, list_equipment_type);

        sp_equipment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                db.open();
                String equipId = db.getEquipmentId1(sp_equipment_type.getSelectedItem().toString(),
                        mAppPreferences.getTTModuleSelection());
                db.close();
                if(sp_equipment_type.getSelectedItem().toString()
                        .equalsIgnoreCase("Select "+tv_equipment_type.getText().toString())){
                    reSetValues(0);
                    ArrayList<String> list_QR = new ArrayList<String>();
                    list_QR.add("Select");
                    list_QR.add("NA");
                    addItemsOnSpinner(sp_asset_id_tag, list_QR);
                    return;

                }

                if(scanFlag==1){
                    scanFlag = 0;
                    return;
                }
                if (Utils.isNetworkAvailable(AddNewAssetTT.this)) {
                    new GetQRDetails(AddNewAssetTT.this,0,equipId).execute();
                } else {
                    // "No Internet Connection"
                    Utils.toast( AddNewAssetTT.this, "17" );
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_asset_id_tag = (Spinner) findViewById(R.id.sp_asset_id_tag);
        ArrayList<String> list_QR = new ArrayList<String>();
        list_QR.add("Select");
        addItemsOnSpinner(sp_asset_id_tag, list_QR);

        sp_asset_id_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if(sp_equipment_type.getSelectedItem().toString()
                        .equalsIgnoreCase("Select "+tv_equipment_type.getText().toString())){
                    reSetValues(0);
                    return;
                }

                if(sp_asset_id_tag.getSelectedItem().toString()
                        .equalsIgnoreCase("Select")){
                    reSetValues(0);
                    return;
                }

                if(sp_asset_id_tag.getSelectedItem().toString()
                        .equalsIgnoreCase("NA")){
                    reSetValues(0);
                    return;
                }

                /*if(scanFlag==1){
                    return;
                }*/
                setValues(position,0);//0 means call from qr code dd list , 1 means call from qr code scan


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                     String str_arr = "";
                     try {
                         JSONArray jsonArray = new JSONArray();
                         JSONObject obj = new JSONObject();
                         obj.put("assettype", sp_equipment_type.getSelectedItem().toString());
                         obj.put("assetlist", sp_asset_id_tag.getSelectedItem().toString());
                         obj.put("qrcode", et_manual_qr_code.getText().toString());
                         obj.put("assetid", et_asset_id.getText().toString());
                         db.open();
                         String equipId = db.getEquipmentId1(sp_equipment_type.getSelectedItem().toString(),
                                 mAppPreferences.getTTModuleSelection());
                         db.close();
                         obj.put("assettypeid", equipId);
                         obj.put("manufacturer", et_manual_brand.getText().toString());
                         obj.put("model", et_manual_model.getText().toString());
                         obj.put("serialnumber", et_manual_serial_no.getText().toString());
                         obj.put("capacity", et_capacity.getText().toString());
                         obj.put("warrantydate", et_warranty_date.getText().toString());
                         obj.put("actionreq", sp_action.getSelectedItem().toString());
                         obj.put("remarks", et_remarks.getText().toString());
                         jsonArray.put(obj);
                         if (jsonArray != null) {
                             str_arr = jsonArray.toString();
                         }
                     }catch (Exception e) {
                         e.printStackTrace();
                         Toast.makeText(AddNewAssetTT.this,
                                 e.getMessage(),
                                 Toast.LENGTH_LONG).show();
                     }

                     if (Utils.isNetworkAvailable(AddNewAssetTT.this)) {
                         new SaveAssetDetails(AddNewAssetTT.this,str_arr).execute();
                     } else {
                         // "No Internet Connection"
                         Utils.toast( AddNewAssetTT.this, "17" );
                     }
                }
            }
        });

        Button bt_back = (Button) findViewById(R.id.button_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddNewAssetTT.this, R.layout.spinner_text, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(AddNewAssetTT.this));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(AddNewAssetTT.this));
                v.setPadding(10, 15, 10, 15);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    // Here you can handle the scanned QR code data
                    String scannedData = result.getContents();
                    et_manual_qr_code.setText(scannedData);

                    if (Utils.isNetworkAvailable(AddNewAssetTT.this)) {
                        new GetQRDetails(AddNewAssetTT.this,1,scannedData).execute();
                    } else {
                        // "No Internet Connection"
                        Utils.toast( AddNewAssetTT.this, "17" );
                    }

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class GetQRDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res,initiativeId = "";
        int mode = 0; //0 means call from asset type dropdown and 1 means call from scan QR code
        public GetQRDetails(Context con,int mode,String initiativeId) {
            this.con = con;
            this.initiativeId = initiativeId;
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
                if(mode==0) {
                    nameValuePairs.add(new BasicNameValuePair("siteId", getIntent().getExtras().getString("sid")));
                    nameValuePairs.add(new BasicNameValuePair("initiativeId", initiativeId));
                    if (moduleUrl.equalsIgnoreCase("0")) {
                        serUrl = mAppPreferences.getConfigIP() + WebMethods.url_get_QRCode;
                    } else {
                        serUrl = moduleUrl + WebMethods.url_get_QRCode;
                    }
                }else{
                    nameValuePairs.add(new BasicNameValuePair("tktId", ""));
                    nameValuePairs.add(new BasicNameValuePair("qrCode", initiativeId));
                    if (moduleUrl.equalsIgnoreCase("0")) {
                        serUrl = mAppPreferences.getConfigIP() + WebMethods.url_get_assetDetailsByQRcode;
                    } else {
                        serUrl = moduleUrl + WebMethods.url_get_assetDetailsByQRcode;
                    }
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

            if(mode==0) {
                ArrayList<String> list_QR = new ArrayList<String>();
                list_QR.add("Select");
                if (qrList != null && qrList.qr_list.size() > 0) {
                    for (int a = 0; a < qrList.qr_list.size(); a++) {
                        String assetId = qrList.getQRDetails().get(a).getASSET_ID();
                        if (assetId != null && assetId.length() > 0) {
                            list_QR.add("" + qrList.getQRDetails().get(a).getASSET_QR_CODE()
                                    + "(" + assetId + ")");
                        } else {
                            list_QR.add("" + qrList.getQRDetails().get(a).getASSET_QR_CODE());
                        }
                    }
                }
                list_QR.add(("NA"));
                addItemsOnSpinner(sp_asset_id_tag, list_QR);
                reSetValues(mode);
            }else{
                if (qrList != null && qrList.qr_list.size() > 0) {
                    scanFlag = 1;
                    setValues(0, mode);
                }else{
                    reSetValues(mode);
                }
            }

            super.onPostExecute(result);
        }
    }

    public void setValues(int pos, int mode){

        int position = 0;

          if(mode==0){
            position = pos-1;//remove "select" postion

            if(qrList.qr_list.size()>0
                    && qrList.getQRDetails().get(position).getASSET_QR_CODE()!=null
                    && qrList.getQRDetails().get(position).getASSET_QR_CODE().length()>0){
                et_manual_qr_code.setText(qrList.getQRDetails().get(position).getASSET_QR_CODE()) ;
            }else{
                et_manual_qr_code.setText("") ;
            }
        }

        if(mode==1){
            String s = "";
            if(qrList.qr_list.size()>0
                    && qrList.getQRDetails().get(position).getASSET_ID()!=null
                    && qrList.getQRDetails().get(position).getASSET_ID().length()>0){
                s = "("+qrList.getQRDetails().get(position).getASSET_ID()+")" ;
            }


            ArrayList<String> list_QR = new ArrayList<String>();
            list_QR.add("Select");
            list_QR.add(qrList.getQRDetails().get(position).getASSET_QR_CODE()+s);
            list_QR.add("NA");
            addItemsOnSpinner(sp_asset_id_tag, list_QR);
            sp_asset_id_tag.setSelection(1);

            db.open();
            String s1 = db.getEquipmentName(qrList.getQRDetails().get(position).getINITIATIVE_ID(),"654");
            db.close();
            int selectPos = list_equipment_type.indexOf(s1);
            sp_equipment_type.setSelection(selectPos);

        }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getASSET_ID()!=null
                && qrList.getQRDetails().get(position).getASSET_ID().length()>0){
            et_asset_id.setText(qrList.getQRDetails().get(position).getASSET_ID()) ;
            //et_asset_id.setEnabled(false);
        }else{
            et_asset_id.setText("") ;
            //et_asset_id.setEnabled(true);
        }

        if(qrList.getQRDetails().get(position).getSITE_ID()!=null
                && qrList.getQRDetails().get(position).getSITE_ID().length()>0
                && !qrList.getQRDetails().get(position).getSITE_ID()
                .equalsIgnoreCase(getIntent().getExtras().getString("sid"))){
            et_remarks.setText(Utils.msg(AddNewAssetTT.this,"922")+" "
                    +qrList.getQRDetails().get(position).getSITE_ID());
        }else{
            et_remarks.setText("");
        }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getWARRANTY_DATE()!=null
                && qrList.getQRDetails().get(position).getWARRANTY_DATE().length()>0){
            et_warranty_date.setText(qrList.getQRDetails().get(position).getWARRANTY_DATE()) ;
        }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getMANUFATURER()!=null
                && qrList.getQRDetails().get(position).getMANUFATURER().length()>0){
            et_manual_brand.setText(qrList.getQRDetails().get(position).getMANUFATURER()) ;
        }else{
            et_manual_brand.setText("") ;
         }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getMODEL()!=null
                && qrList.getQRDetails().get(position).getMODEL().length()>0){
            et_manual_model.setText(qrList.getQRDetails().get(position).getMODEL()) ;
            //et_manual_model.setEnabled(false);
        }else{
            et_manual_model.setText("") ;
            //et_manual_model.setEnabled(true);
        }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getSERIAL_NUMBER()!=null
                && qrList.getQRDetails().get(position).getSERIAL_NUMBER().length()>0){
            et_manual_serial_no.setText(qrList.getQRDetails().get(position).getSERIAL_NUMBER()) ;
            //et_manual_serial_no.setEnabled(false);
        }else{
            et_manual_serial_no.setText("") ;
            //et_manual_serial_no.setEnabled(true);
        }

        if(qrList.qr_list.size()>0
                && qrList.getQRDetails().get(position).getCAPACITY()!=null
                && qrList.getQRDetails().get(position).getCAPACITY().length()>0){
            et_capacity.setText(qrList.getQRDetails().get(position).getCAPACITY()) ;
            //et_capacity.setEnabled(false);
        }else{
            et_capacity.setText("") ;
            //et_capacity.setEnabled(true);
        }



    }

    public void reSetValues(int mode){
             et_asset_id.setText("") ;
             et_warranty_date.setText("") ;
             et_manual_brand.setText("") ;
             et_manual_model.setText("") ;
             et_manual_serial_no.setText("") ;
             et_capacity.setText("") ;
             et_remarks.setText("");

             if (qrList != null && qrList.qr_list.size() > 0) {
                 et_manual_qr_code.setText("");
             }

            /*if(mode==0) { //scan qr code or not
                et_manual_qr_code.setText("");
            }*/

            if(mode==1) {
                ArrayList<String> list_QR = new ArrayList<String>();
                list_QR.add("Select");
                list_QR.add(("NA"));
                addItemsOnSpinner(sp_asset_id_tag, list_QR);
                addItemsOnSpinner(sp_equipment_type, list_equipment_type);
            }


    }

    public class SaveAssetDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String data = "",res = "";
        public SaveAssetDetails(Context con,String data) {
            this.con = con;
            this.data = data;
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
                nameValuePairs.add(new BasicNameValuePair("tktId",
                        getIntent().getExtras().getString("id")));
                nameValuePairs.add(new BasicNameValuePair("assetDetails", data));
                nameValuePairs.add(new BasicNameValuePair("flag", "TT"));
                 if (moduleUrl.equalsIgnoreCase("0")) {
                   serUrl = mAppPreferences.getConfigIP() + WebMethods.url_save_assetDetails;
                  } else {
                  serUrl = moduleUrl + WebMethods.url_save_assetDetails;
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
            String success = "";
            String msg = "";
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if (res != null && res.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("SaveUpdateAssetDetails");
                    if(subArray!=null && subArray.length()>0) {
                        success = subArray.getJSONObject(0).getString("respFlag");
                        msg = subArray.getJSONObject(0).getString("errorDesc");
                        //Utils.toastMsg(AddNewAssetTT.this,msg);
                    }
                    if (success.equals("S")) {
                        finish();
                    } else {
                        msg  ="issue in add asset API";
                        Utils.toastMsg(AddNewAssetTT.this,msg);
                    }
                } catch (Exception e) {
                    msg  ="issue in add asset API";
                    Utils.toastMsg(AddNewAssetTT.this,msg);
                }
            } else {
                msg  ="issue in add asset API";
                Utils.toastMsg(AddNewAssetTT.this,msg);

            }

            super.onPostExecute(result);
        }
    }

    private boolean validate() {
        if(et_manual_qr_code.getText().toString().length()==0){
            String msg = "Scan "+tv_manual_qr_code.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if (sp_equipment_type.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_equipment_type.getText().toString())) {
            String msg = "Select "+tv_equipment_type.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if (sp_asset_id_tag.getSelectedItem().toString()
                .equalsIgnoreCase("Select")) {
            String msg = "Select "+tv_asset_id_tag.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if (sp_action.getSelectedItem().toString()
                .equalsIgnoreCase("Select")) {
            String msg = "Select "+tv_action.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_asset_id.getText().toString().length()==0){
            String msg = "Enter "+tv_asset_id.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_warranty_date.getText().toString().length()==0){
            String msg = "Enter "+tv_warranty_date.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(!et_warranty_date.getText().toString().equalsIgnoreCase("NA")
                  && validateDate(et_warranty_date.getText().toString())){
            String msg = "Invalid Warranty Date format";
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_manual_brand.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_brand.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_manual_model.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_model.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_manual_serial_no.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_ser_no.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }else if(et_capacity.getText().toString().length()==0){
            String msg = "Enter "+tv_capacity.getText().toString();
            Utils.toastMsg(AddNewAssetTT.this,msg);
            return false;
        }
        return true;
    }


        public static boolean validateDate(String text) {
            int count = 0;
            if (text.length() > 11 || text.length()<10) {
                return true;
            }

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '-') {
                    count++;
                }
            }

            if (count >2 || count<2) {
                return true;
            }

            if(count==2){
                int pos1 = text.indexOf("-");
                int pos2 = text.lastIndexOf("-");

                String year = text.substring(pos2+1, text.length());
                        if(year.length()>4 || year.length()<4){
                            return true;
              }
            }

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            df.setLenient(false);
            try {
                df.parse(text);
                return false;
            } catch (ParseException ex) {
                return true;
            }
        }

}