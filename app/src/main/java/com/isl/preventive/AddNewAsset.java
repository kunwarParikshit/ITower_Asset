package com.isl.preventive;

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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;

public class AddNewAsset extends Activity {
    public static Map<String, String> validations;
    Button btn_back;
    Spinner sp_equipment_type,sp_asset_id_tag,sp_action;
    TextView tv_remarks,tv_action,tv_warranty_date,tv_brand_logo, tv_equipment_type,tv_asset_id_tag,tv_manual_brand, tv_manual_model, tv_manual_ser_no,
            tv_manual_qr_code, tvSubmit,tv_capacity,tv_pat_date, tv_scrap_date, tv_type,tv_field1,
            tv_field2,tv_field3,tv_field4,tv_field5,tv_field6,tv_field7,tv_field8,tv_field9,tv_field10;
    EditText et_remarks,et_warranty_date,et_manual_brand,et_manual_model,et_manual_serial_no,et_manual_qr_code, et_capacity,
            et_pat_date, et_scrap_date, et_type, et_field1,et_field2,et_field3,et_field4,et_field5,
            et_field6,et_field7,et_field8,et_field9,et_field10;
    DataBaseHelper db;
    String moduleUrl = "",serUrl = "";
    AppPreferences mAppPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState); // Move this statement before the try block
            setContentView(R.layout.add_new_asset);
            validations = new HashMap<String, String>();

            mAppPreferences = new AppPreferences(AddNewAsset.this);
            db = new DataBaseHelper(AddNewAsset.this);
            db.open();
            moduleUrl = db.getModuleIP("Schedule");
            db.close();
            initializeControlledID();

        } catch (Exception e) {
            e.printStackTrace();
            // Add a log statement to track the exception details
            Log.e("AddNewAsset", "Exception in onCreate: " + e.toString());
        }
    }
    private void initializeControlledID() {
        tv_remarks  = (TextView) findViewById(R.id.tv_remarks);
        tv_action = (TextView) findViewById(R.id.tv_action);
        tv_warranty_date = (TextView) findViewById(R.id.tv_warranty_date);
        et_warranty_date = (EditText) findViewById(R.id.et_warranty_date);
        et_remarks  = (EditText) findViewById(R.id.et_remarks);

        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        tv_equipment_type = (TextView) findViewById(R.id.tv_equipment_type);
        tv_asset_id_tag = (TextView)findViewById(R.id.tv_asset_id_tag);
        tv_manual_brand = (TextView)findViewById(R.id.tv_manual_brand);
        tv_manual_model = (TextView)findViewById(R.id.tv_manual_model);
        tv_manual_ser_no = (TextView)findViewById(R.id.tv_manual_ser_no);
        tv_manual_qr_code = (TextView)findViewById(R.id.tv_manual_qr_code);

        tv_capacity = (TextView)findViewById(R.id.tv_capacity);
        tv_pat_date = (TextView)findViewById(R.id.tv_pat_date);
        tv_scrap_date = (TextView)findViewById(R.id.tv_scrap_date);
        tv_type = (TextView)findViewById(R.id.tv_type);
        tv_field1 = (TextView)findViewById(R.id.tv_field1);
        tv_field2 = (TextView)findViewById(R.id.tv_field2);
        tv_field3 = (TextView)findViewById(R.id.tv_field3);
        tv_field4 = (TextView)findViewById(R.id.tv_field4);
        tv_field5 = (TextView)findViewById(R.id.tv_field5);
        tv_field6 = (TextView)findViewById(R.id.tv_field6);
        tv_field7 = (TextView)findViewById(R.id.tv_field7);
        tv_field8 = (TextView)findViewById(R.id.tv_field8);
        tv_field9 = (TextView)findViewById(R.id.tv_field9);
        tv_field10 = (TextView)findViewById(R.id.tv_field10);

        tvSubmit = (TextView)findViewById(R.id.tvSubmit);
        Utils.msgText(AddNewAsset.this, "911", tv_action);
        Utils.msgText(AddNewAsset.this, "912", tv_warranty_date);
        Utils.msgText(AddNewAsset.this, "920", tv_remarks);
        Utils.msgText(AddNewAsset.this, "891", tv_equipment_type);
        Utils.msgText(AddNewAsset.this, "877", tv_manual_brand);
        Utils.msgText(AddNewAsset.this, "878", tv_manual_model);
        Utils.msgText(AddNewAsset.this, "879", tv_manual_ser_no);
        Utils.msgText(AddNewAsset.this, "880", tv_manual_qr_code);
        Utils.msgText(AddNewAsset.this, "883", tv_asset_id_tag);
        Utils.msgText(AddNewAsset.this, "889", tvSubmit);
        Utils.msgText(AddNewAsset.this, "890", tv_brand_logo);
        Utils.msgText(AddNewAsset.this, "881", tv_capacity);
        Utils.msgText(AddNewAsset.this, "893", tv_pat_date);
        Utils.msgText(AddNewAsset.this, "894", tv_scrap_date);
        Utils.msgText(AddNewAsset.this, "895", tv_type);
        Utils.msgText(AddNewAsset.this, "896", tv_field1);
        Utils.msgText(AddNewAsset.this, "897", tv_field2);
        Utils.msgText(AddNewAsset.this, "898", tv_field3);
        Utils.msgText(AddNewAsset.this, "899", tv_field4);
        Utils.msgText(AddNewAsset.this, "900", tv_field5);
        Utils.msgText(AddNewAsset.this, "901", tv_field6);
        Utils.msgText(AddNewAsset.this, "902", tv_field7);
        Utils.msgText(AddNewAsset.this, "903", tv_field8);
        Utils.msgText(AddNewAsset.this, "904", tv_field9);
        Utils.msgText(AddNewAsset.this, "905", tv_field10);


        et_manual_brand = (EditText)findViewById(R.id.et_manual_brand);
        et_manual_model = (EditText)findViewById(R.id.et_manual_model);
        et_manual_serial_no = (EditText)findViewById(R.id.et_manual_serial_no);
        et_capacity = (EditText)findViewById(R.id.et_capacity);

        et_pat_date = (EditText)findViewById(R.id.et_pat_date);
        et_pat_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsTask.datePickerForPM(AddNewAsset.this,
                        et_pat_date, tv_pat_date);
                //showFromDateTimeDialogPat(et_pat_date);
            }
        });

        et_scrap_date = (EditText)findViewById(R.id.et_scrap_date);
        et_scrap_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.datePickerForPM(AddNewAsset.this,
                        et_scrap_date, tv_scrap_date);
                //showFromDateTimeDialogScrap(et_scrap_date);
            }
        });
        et_manual_qr_code = (EditText)findViewById(R.id.et_manual_qr_code);
        et_type = (EditText)findViewById(R.id.et_type);
        et_field1 = (EditText)findViewById(R.id.et_field1);
        et_field2 = (EditText)findViewById(R.id.et_field2);
        et_field3 = (EditText)findViewById(R.id.et_field3);
        et_field4 = (EditText)findViewById(R.id.et_field4);
        et_field5 = (EditText)findViewById(R.id.et_field5);
        et_field6 = (EditText)findViewById(R.id.et_field6);
        et_field7 = (EditText)findViewById(R.id.et_field7);
        et_field8 = (EditText)findViewById(R.id.et_field8);
        et_field9 = (EditText)findViewById(R.id.et_field9);
        et_field10 = (EditText)findViewById(R.id.et_field10);
        btn_back = (Button) findViewById(R.id.button_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        db.open();
        ArrayList<String> list_equipment_type = db.getParamSort
                ("1266", tv_equipment_type, "654",
                        getIntent().getExtras().getString("activityTypeId"));

        ArrayList<String> list_asset_tag_status = db.getInciParam1("1268",tv_asset_id_tag, "654");
        ArrayList<String> list_action = db.getParamName("1280","654");
        db.close();


        sp_action = (Spinner) findViewById(R.id.sp_action);
        addItemsOnSpinner(sp_action, list_action);


        sp_equipment_type = (Spinner) findViewById(R.id.sp_equipment_type);
        addItemsOnSpinner(sp_equipment_type, list_equipment_type);

        sp_equipment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                validations.clear();
                validations.put("default","default");
                db.open();
                String equipmentId = db.getInciParamId23("1266",
                        sp_equipment_type.getSelectedItem().toString(),"654");

                String showHideFields = db.getInciCreatedBy("1266",equipmentId
                        ,"654",getIntent().getExtras().getString("activityTypeId"));
                db.close();
                if(showHideFields!=null && showHideFields.length()>0){
                    if(showHideFields.contains(",")){
                        String arr1[] = showHideFields.split(",");
                        if(arr1!=null && arr1.length>0){
                            for(int a=0;a<arr1.length;a++){
                                String arr2[] = arr1[a].split("~");
                                if(arr2!=null && arr2.length>0){
                                    //validations.put(arr2[0], "M");
                                    validations.put(arr2[0], arr2[1]);
                                }
                            }
                        }
                    }
                }
                showFields(validations);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        sp_asset_id_tag = (Spinner) findViewById(R.id.sp_asset_id_tag);
        addItemsOnSpinner(sp_asset_id_tag, list_asset_tag_status);
        sp_asset_id_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String assetIdTag = sp_asset_id_tag.getSelectedItem().toString();
                db.open();
                String assetIdTagPdesc = db.getInciParamDesc("1268",assetIdTag,"654");
                db.close();
                tv_manual_qr_code.setVisibility(View.GONE);
                et_manual_qr_code.setVisibility(View.GONE);

                if (assetIdTagPdesc.equalsIgnoreCase("1")) {
                    tv_manual_qr_code.setVisibility(View.VISIBLE);
                    et_manual_qr_code.setVisibility(View.VISIBLE);
                    scanQR();

                }
                if(assetIdTagPdesc.equals("2")) {
                    tv_manual_qr_code.setVisibility(View.GONE);
                    et_manual_qr_code.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    AddActivityAssetTask task = new AddActivityAssetTask(AddNewAsset.this);
                    task.execute();
                }
            }
        });

    }
    private  void scanQR(){
        et_manual_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator qrScan = new IntentIntegrator(AddNewAsset.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(AddNewAsset.this, "511"));
                qrScan.initiateScan();

            }
        });
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
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public class AddActivityAssetTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res,assetTagCond = "",equipmentId = "";

        public AddActivityAssetTask(Context con) {
            this.con = con;
            db.open();

            assetTagCond = db.getInciParamId("1268",
                    sp_asset_id_tag.getSelectedItem().toString(),"654");

            equipmentId = db.getInciParamId23("1266",
                    sp_equipment_type.getSelectedItem().toString(),"654");
            db.close();
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(13);
                //master data
                nameValuePairs.add(new BasicNameValuePair("userID",
                        mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("tranID",
                        getIntent().getExtras().getString("txn")));
                nameValuePairs.add(new BasicNameValuePair("assetID",
                        ""));
                nameValuePairs.add(new BasicNameValuePair("siteID",
                        getIntent().getExtras().getString("siteId")));
                nameValuePairs.add(new BasicNameValuePair("oper",
                        "A"));
                nameValuePairs.add(new BasicNameValuePair("equipType",
                        sp_equipment_type.getSelectedItem().toString()));
                nameValuePairs.add(new BasicNameValuePair("equipID",
                        ""+equipmentId));

                //fill bu user
                nameValuePairs.add(new BasicNameValuePair("brand",
                        et_manual_brand.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("model",
                        et_manual_model.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("serialNum",
                        et_manual_serial_no.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("capacity",
                        et_capacity.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("patDate",
                        et_pat_date.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("scrapDate",
                        et_scrap_date.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("type",
                        et_type.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("qrCode",
                        et_manual_qr_code.getText().toString()));

               //extra
                nameValuePairs.add(new BasicNameValuePair("field1",
                        et_warranty_date.getText().toString()));

                nameValuePairs.add(new BasicNameValuePair("field2",
                        et_field2.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field3",
                        et_field3.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field4",
                        et_field4.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field5",
                        et_field5.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field6",
                        et_field6.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field7",
                        et_field7.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field8",
                        et_field8.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field9",
                        et_field9.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("field10",
                        et_field10.getText().toString()));
                //condition
                String qrCodeCond = null;
                nameValuePairs.add(new BasicNameValuePair("assetFoundCond",""));
                nameValuePairs.add(new BasicNameValuePair("assetTagCond", assetTagCond));
                nameValuePairs.add(new BasicNameValuePair("qrCodeCond",qrCodeCond));
                nameValuePairs.add(new BasicNameValuePair("brandCond",""));
                nameValuePairs.add(new BasicNameValuePair("modelCond",""));
                nameValuePairs.add(new BasicNameValuePair("serialNumCond",""));

                db.open();
                String actionreq = db.getInciParamIdd("1280",
                        sp_action.getSelectedItem().toString(),"654");
                nameValuePairs.add(new BasicNameValuePair("actionreq",actionreq));
                nameValuePairs.add(new BasicNameValuePair("remarks",et_remarks.getText().toString()));


                if (moduleUrl.equalsIgnoreCase("0")) {
                    serUrl = mAppPreferences.getConfigIP() + WebMethods.url_Add_Asset;
                } else {
                    serUrl = moduleUrl + WebMethods.url_Add_Asset;
                }
                res = Utils.httpPostRequest(con, serUrl, nameValuePairs);

                //res = "{response :" + res + "}";
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
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (res != null && res.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray subArray = jsonObject.getJSONArray("SaveUpdateAssetDetails");
                    if(subArray!=null && subArray.length()>0) {
                        //for (int i = 0; i < subArray.length(); i++) {
                            success = subArray.getJSONObject(0).getString("respFlag");
                            msg = subArray.getJSONObject(0).getString("errorDesc");
                        //}
                        Utils.toastMsg(AddNewAsset.this,msg);
                        //Toast.makeText(AddNewAsset.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    if (success.equals("S")) {
                        finish();
                    } else {

                    }
                } catch (Exception e) {

                }
            } else {
                msg  ="issue in add asset API";
                Toast.makeText(AddNewAsset.this, msg, Toast.LENGTH_SHORT).show();

            }
            super.onPostExecute(result);
        }
    }

        public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddNewAsset.this, R.layout.spinner_text, list) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(Utils.typeFace(AddNewAsset.this));
                    return v;
                }

                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTypeface(Utils.typeFace(AddNewAsset.this));
                    v.setPadding(10, 15, 10, 15);
                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(dataAdapter);
        }

    private boolean validate() {
        if (sp_equipment_type.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_equipment_type.getText().toString())) {
            String msg = "Select "+tv_equipment_type.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if (sp_asset_id_tag.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_asset_id_tag.getText().toString())) {
            String msg = "Select "+tv_asset_id_tag.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("BRAND")
                && validations.get("BRAND").equalsIgnoreCase("M")
                && et_manual_brand.getVisibility()==View.VISIBLE
                && et_manual_brand.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_manual_brand.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("MODEL")
                && validations.get("MODEL").equalsIgnoreCase("M")
                && et_manual_model.getVisibility()==View.VISIBLE
                && et_manual_model.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_manual_model.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("SERIAL_NUMBER")
                && validations.get("SERIAL_NUMBER").equalsIgnoreCase("M")
                && et_manual_serial_no.getVisibility()==View.VISIBLE
                && et_manual_serial_no.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_manual_ser_no.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("PAT_DATE")
                && validations.get("PAT_DATE").equalsIgnoreCase("M")
                && et_pat_date.getVisibility()==View.VISIBLE
                && et_pat_date.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_pat_date.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("SCRAP_DATE")
                && validations.get("SCRAP_DATE").equalsIgnoreCase("M")
                && et_scrap_date.getVisibility()==View.VISIBLE
                && et_scrap_date.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_scrap_date.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("CAPACITY")
                && validations.get("CAPACITY").equalsIgnoreCase("M")
                && et_capacity.getVisibility()==View.VISIBLE
                && et_capacity.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_capacity.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("TYPE")
                && validations.get("TYPE").equalsIgnoreCase("M")
                && et_type.getVisibility()==View.VISIBLE
                && et_type.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_type.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }/*else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_field1.getVisibility()==View.VISIBLE
                && et_field1.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field1.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }*/else if(validations.containsKey("FIELD2")
                && validations.get("FIELD2").equalsIgnoreCase("M")
                && et_field2.getVisibility()==View.VISIBLE
                && et_field2.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field2.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD3")
                && validations.get("FIELD3").equalsIgnoreCase("M")
                && et_field3.getVisibility()==View.VISIBLE
                && et_field3.getText().toString().length()==0
               ){
            String msg = "Enter "+tv_field3.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD4")
                && validations.get("FIELD4").equalsIgnoreCase("M")
                && et_field4.getVisibility()==View.VISIBLE
                && et_field4.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field4.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD5")
                && validations.get("FIELD5").equalsIgnoreCase("M")
                && et_field5.getVisibility()==View.VISIBLE
                && et_field5.getText().toString().length()==0
               ){
            String msg = "Enter "+tv_field5.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD6")
                && validations.get("FIELD6").equalsIgnoreCase("M")
                && et_field6.getVisibility()==View.VISIBLE
                && et_field6.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field6.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD7")
                && validations.get("FIELD7").equalsIgnoreCase("M")
                && et_field7.getVisibility()==View.VISIBLE
                && et_field7.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field7.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD8")
                && validations.get("FIELD8").equalsIgnoreCase("M")
                && et_field8.getVisibility()==View.VISIBLE
                && et_field8.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field8.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD9")
                && validations.get("FIELD9").equalsIgnoreCase("M")
                && et_field9.getVisibility()==View.VISIBLE
                && et_field9.getText().toString().length()==0
               ){
            String msg = "Enter "+tv_field9.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD10")
                && validations.get("FIELD10").equalsIgnoreCase("M")
                && et_field10.getVisibility()==View.VISIBLE
                && et_field10.getText().toString().length()==0
                ){
            String msg = "Enter "+tv_field10.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_warranty_date.getVisibility()==View.VISIBLE
                && et_warranty_date.getText().toString().length()==0){
            String msg = "Enter "+tv_warranty_date.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_warranty_date.getVisibility()==View.VISIBLE
                && !et_warranty_date.getText().toString().equalsIgnoreCase("NA")
                && validateDate(et_warranty_date.getText().toString())){
            String msg = "Invalid Warranty Date format";
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if (validations.containsKey("ASSET_ACTION")
                && validations.get("ASSET_ACTION").equalsIgnoreCase("M")
                && sp_action.getVisibility()==View.VISIBLE
                && sp_action.getSelectedItem().toString()
                .equalsIgnoreCase("Select")) {
            String msg = "Select "+tv_action.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
            return false;
        }else if (validations.containsKey("ASSET_REMARKS")
                && validations.get("ASSET_REMARKS").equalsIgnoreCase("M")
                && et_remarks.getVisibility()==View.VISIBLE
                && et_remarks.getText().toString().length()==0) {
            String msg = "Enter "+tv_remarks.getText().toString();
            Utils.toastMsg(AddNewAsset.this,msg);
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

    public void showFields(Map<String,String> map){

        if(map.containsKey("BRAND")){
            tv_manual_brand.setVisibility(View.VISIBLE);
            et_manual_brand.setVisibility(View.VISIBLE);
        }else{
            tv_manual_brand.setVisibility(View.GONE);
            et_manual_brand.setVisibility(View.GONE);
        }

        if(map.containsKey("MODEL")){
            tv_manual_model.setVisibility(View.VISIBLE);
            et_manual_model.setVisibility(View.VISIBLE);
        }else{
            tv_manual_model.setVisibility(View.GONE);
            et_manual_model.setVisibility(View.GONE);
        }

        if(map.containsKey("SERIAL_NUMBER")){
            tv_manual_ser_no.setVisibility(View.VISIBLE);
            et_manual_serial_no.setVisibility(View.VISIBLE);
        }else{
            tv_manual_ser_no.setVisibility(View.GONE);
            et_manual_serial_no.setVisibility(View.GONE);
        }

        if(map.containsKey("PAT_DATE")){
            tv_pat_date.setVisibility(View.VISIBLE);
            et_pat_date.setVisibility(View.VISIBLE);
        }else{
            tv_pat_date.setVisibility(View.GONE);
            et_pat_date.setVisibility(View.GONE);
        }

        if(map.containsKey("SCRAP_DATE")){
            tv_scrap_date.setVisibility(View.VISIBLE);
            et_scrap_date.setVisibility(View.VISIBLE);
        }else{
            tv_scrap_date.setVisibility(View.GONE);
            et_scrap_date.setVisibility(View.GONE);
        }

        if(map.containsKey("CAPACITY")){
            tv_capacity.setVisibility(View.VISIBLE);
            et_capacity.setVisibility(View.VISIBLE);
        }else{
            tv_capacity.setVisibility(View.GONE);
            et_capacity.setVisibility(View.GONE);
        }

        if(map.containsKey("TYPE")){
            tv_type.setVisibility(View.VISIBLE);
            et_type.setVisibility(View.VISIBLE);
        }else{
            tv_type.setVisibility(View.GONE);
            et_type.setVisibility(View.GONE);
        }


        if(map.containsKey("FIELD1")){
            tv_warranty_date.setVisibility(View.VISIBLE);
            et_warranty_date.setVisibility(View.VISIBLE);
        }else{
            tv_warranty_date.setVisibility(View.GONE);
            et_warranty_date.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD2")){
            tv_field2.setVisibility(View.VISIBLE);
            et_field2.setVisibility(View.VISIBLE);
        }else{
            tv_field2.setVisibility(View.GONE);
            et_field2.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD3")){
            tv_field3.setVisibility(View.VISIBLE);
            et_field3.setVisibility(View.VISIBLE);
        }else{
            tv_field3.setVisibility(View.GONE);
            et_field3.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD4")){
            tv_field4.setVisibility(View.VISIBLE);
            et_field4.setVisibility(View.VISIBLE);
        }else{
            tv_field4.setVisibility(View.GONE);
            et_field4.setVisibility(View.GONE);
        }


        if(map.containsKey("FIELD5")){
            tv_field5.setVisibility(View.VISIBLE);
            et_field5.setVisibility(View.VISIBLE);
        }else{
            tv_field5.setVisibility(View.GONE);
            et_field5.setVisibility(View.GONE);
        }


        if(map.containsKey("FIELD6")){
            tv_field6.setVisibility(View.VISIBLE);
            et_field6.setVisibility(View.VISIBLE);
        }else{
            tv_field6.setVisibility(View.GONE);
            et_field6.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD7")){
            tv_field7.setVisibility(View.VISIBLE);
            et_field7.setVisibility(View.VISIBLE);
        }else{
            tv_field7.setVisibility(View.GONE);
            et_field7.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD8")){
            tv_field8.setVisibility(View.VISIBLE);
            et_field8.setVisibility(View.VISIBLE);
        }else{
            tv_field8.setVisibility(View.GONE);
            et_field8.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD9")){
            tv_field9.setVisibility(View.VISIBLE);
            et_field9.setVisibility(View.VISIBLE);
        }else{
            tv_field9.setVisibility(View.GONE);
            et_field9.setVisibility(View.GONE);
        }

        if(map.containsKey("FIELD10")){
            tv_field10.setVisibility(View.VISIBLE);
            et_field10.setVisibility(View.VISIBLE);
        }else{
            tv_field10.setVisibility(View.GONE);
            et_field10.setVisibility(View.GONE);
        }

        if(map.containsKey("ASSET_ACTION")){
            tv_action.setVisibility(View.VISIBLE);
            sp_action.setVisibility(View.VISIBLE);
        }else{
            tv_action.setVisibility(View.GONE);
            sp_action.setVisibility(View.GONE);
        }

        if(map.containsKey("ASSET_REMARKS")){
            tv_remarks.setVisibility(View.VISIBLE);
            et_remarks.setVisibility(View.VISIBLE);
        }else{
            tv_remarks.setVisibility(View.GONE);
            et_remarks.setVisibility(View.GONE);
        }
    }
}
