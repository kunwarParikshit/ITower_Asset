package com.isl.preventive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.isl.modal.BeanCheckListDetails;
import com.isl.modal.BeanGetImageList;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;

public class AssetDescription extends Activity {
    public static Map<String, String> validations;
    TextView tv_remarks,tv_action,tv_warranty_date,tv_brand_logo, tv_assetId,et_assetId, tv_brand, tv_model, tv_ser_no, tv_qr_value,
            tv_capacity, tv_manual_brand, tv_manual_model,
            tv_manual_ser_no, tv_manual_qr_code,tv_asset_status, tv_asset_id_tag,tvSubmit,
            tv_equipment_type,tv_pat_date, tv_scrap_date, tv_type,tv_field1,
            tv_field2,tv_field3,tv_field4,tv_field5,tv_field6,tv_field7,tv_field8,tv_field9,tv_field10
            ,et_equipment_type,et_assets_status,et_asset_id_tag;
    Button btn_back;
    EditText et_remarks,et_warranty_date,et_manual_brand, et_manual_model,
            et_manual_serial_no, et_manual_qr_code, et_manual_qr_code2;

    EditText et_brand, et_model, et_serial_no,et_capacity, et_qr_value,
            et_pat_date, et_scrap_date, et_type, et_field1,et_field2,et_field3,et_field4,et_field5,
            et_field6,et_field7,et_field8,et_field9,et_field10;
    Spinner sp_action,sp_assets_status, sp_asset_id_tag,sp_equipment_type;
    DataBaseHelper db;
    AppPreferences mAppPreferences;
    String moduleUrl = "",serUrl = "",qrCode = "";
    int temp_flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState); // Move this statement before the try block
            setContentView(R.layout.asset_desciption);
            validations = new HashMap<String, String>();
            initializedControlledID();
            disbledFields();
        } catch (Exception e) {
            e.printStackTrace();
            // Add a log statement to track the exception details
            Log.e("Asset_Description", "Exception in onCreate: " + e.toString());
        }
    }

    private void initializedControlledID() {
        mAppPreferences = new AppPreferences(AssetDescription.this);
        db = new DataBaseHelper(AssetDescription.this);
        db.open();
        moduleUrl = db.getModuleIP("Schedule");

        tv_action = (TextView) findViewById(R.id.tv_action);

        tv_warranty_date = (TextView) findViewById(R.id.tv_warranty_date);
        et_warranty_date = (EditText) findViewById(R.id.et_warranty_date);
        et_warranty_date.setText(getIntent().getExtras().getString("field1"));

        tv_remarks  = (TextView) findViewById(R.id.tv_remarks);
        et_remarks  = (EditText) findViewById(R.id.et_remarks);
        et_remarks.setText(getIntent().getExtras().getString("assetRmks"));

        btn_back = (Button) findViewById(R.id.button_back);
        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        tv_assetId = (TextView) findViewById(R.id.tv_assetId);
        tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_ser_no = (TextView) findViewById(R.id.tv_ser_no);
        tv_qr_value = (TextView) findViewById(R.id.tv_qr_value);
        tv_capacity = (TextView) findViewById(R.id.tv_capacity) ;
        tvSubmit =  (TextView) findViewById(R.id.tvSubmit) ;

        tv_equipment_type = (TextView) findViewById(R.id.tv_equipment_type);
        et_equipment_type = (TextView) findViewById(R.id.et_equipment_type);
        et_assets_status = (TextView) findViewById(R.id.et_assets_status);
        et_asset_id_tag = (TextView) findViewById(R.id.et_asset_id_tag);

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

        Utils.msgText(AssetDescription.this, "911", tv_action);
        Utils.msgText(AssetDescription.this, "912", tv_warranty_date);
        Utils.msgText(AssetDescription.this, "920", tv_remarks);
        Utils.msgText(AssetDescription.this, "884", tv_brand_logo);
        Utils.msgText(AssetDescription.this, "885", tv_assetId);
        Utils.msgText(AssetDescription.this, "877", tv_brand);
        Utils.msgText(AssetDescription.this, "878", tv_model);
        Utils.msgText(AssetDescription.this, "879", tv_ser_no);
        Utils.msgText(AssetDescription.this, "880", tv_qr_value);
        Utils.msgText(AssetDescription.this, "881", tv_capacity);
        Utils.msgText(AssetDescription.this, "888", tvSubmit);

        Utils.msgText(AssetDescription.this, "891", tv_equipment_type);
        Utils.msgText(AssetDescription.this, "893", tv_pat_date);
        Utils.msgText(AssetDescription.this, "894", tv_scrap_date);
        Utils.msgText(AssetDescription.this, "895", tv_type);
        Utils.msgText(AssetDescription.this, "896", tv_field1);
        Utils.msgText(AssetDescription.this, "897", tv_field2);
        Utils.msgText(AssetDescription.this, "898", tv_field3);
        Utils.msgText(AssetDescription.this, "899", tv_field4);
        Utils.msgText(AssetDescription.this, "900", tv_field5);
        Utils.msgText(AssetDescription.this, "901", tv_field6);
        Utils.msgText(AssetDescription.this, "902", tv_field7);
        Utils.msgText(AssetDescription.this, "903", tv_field8);
        Utils.msgText(AssetDescription.this, "904", tv_field9);
        Utils.msgText(AssetDescription.this, "905", tv_field10);

        String ASSET_FOUND_COND = getIntent().getExtras().getString("ASSET_FOUND_COND");

        if(ASSET_FOUND_COND!=null
               && ASSET_FOUND_COND.equalsIgnoreCase("Asset missing")
               && getIntent().getExtras().getString("S").equalsIgnoreCase("P")
               && getIntent().getExtras().getString("assetStatus")
                .equalsIgnoreCase("D")){
            tvSubmit.setVisibility(View.VISIBLE);
        }else if(ASSET_FOUND_COND!=null
                && ASSET_FOUND_COND.equalsIgnoreCase("Asset missing")
                && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")
                && getIntent().getExtras().getString("assetStatus")
                .equalsIgnoreCase("RS")){
            tvSubmit.setVisibility(View.VISIBLE);
        } else if(ASSET_FOUND_COND!=null
                && ASSET_FOUND_COND.equalsIgnoreCase("Asset missing")){
            tvSubmit.setVisibility(View.GONE);
        }
        //EditText
        String assetId = getIntent().getExtras().getString("assetId");
        et_assetId = (TextView) findViewById(R.id.et_assetId);
        et_assetId.setText(assetId);

        String brand = getIntent().getExtras().getString("brand");
        et_brand = (EditText) findViewById(R.id.et_brand);
        et_brand.setText(brand);


        String model = getIntent().getExtras().getString("model");
        et_model = (EditText) findViewById(R.id.et_model);
        et_model.setText(model);

        String serialNo = getIntent().getExtras().getString("serialNo");
        et_serial_no = (EditText) findViewById(R.id.et_serial_no);
        et_serial_no.setText(serialNo);

        et_qr_value = (EditText) findViewById(R.id.et_qr_value);
        et_qr_value.setText(getIntent().getExtras().getString("qrCode"));

        String capacity = getIntent().getExtras().getString("capacity");
        et_capacity = (EditText) findViewById(R.id.et_capacity);
        et_capacity.setText(capacity);

        et_pat_date = (EditText)findViewById(R.id.et_pat_date);
        et_pat_date.setText(getIntent().getExtras().getString("patDate"));
        et_pat_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsTask.datePickerForPM(AssetDescription.this,
                        et_pat_date, tv_pat_date);
                //showFromDateTimeDialogPat(et_pat_date);
            }
        });

        et_scrap_date = (EditText)findViewById(R.id.et_scrap_date);
        et_scrap_date.setText(getIntent().getExtras().getString("scrapDate"));
        et_scrap_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.datePickerForPM(AssetDescription.this,
                        et_scrap_date, tv_scrap_date);

            }
        });

        et_type = (EditText)findViewById(R.id.et_type);
        et_type.setText(getIntent().getExtras().getString("type"));

        et_field1 = (EditText)findViewById(R.id.et_field1);
        et_field1.setText(getIntent().getExtras().getString("field1"));

        et_field2 = (EditText)findViewById(R.id.et_field2);
        et_field2.setText(getIntent().getExtras().getString("field2"));

        et_field3 = (EditText)findViewById(R.id.et_field3);
        et_field3.setText(getIntent().getExtras().getString("field3"));

        et_field4 = (EditText)findViewById(R.id.et_field4);
        et_field4.setText(getIntent().getExtras().getString("field4"));

        et_field5 = (EditText)findViewById(R.id.et_field5);
        et_field5.setText(getIntent().getExtras().getString("field5"));

        et_field6 = (EditText)findViewById(R.id.et_field6);
        et_field6.setText(getIntent().getExtras().getString("field6"));

        et_field7 = (EditText)findViewById(R.id.et_field7);
        et_field7.setText(getIntent().getExtras().getString("field7"));

        et_field8 = (EditText)findViewById(R.id.et_field8);
        et_field8.setText(getIntent().getExtras().getString("field8"));

        et_field9 = (EditText)findViewById(R.id.et_field9);
        et_field9.setText(getIntent().getExtras().getString("field9"));

        et_field10 = (EditText)findViewById(R.id.et_field10);
        et_field10.setText(getIntent().getExtras().getString("field10"));


        tv_manual_brand = (TextView) findViewById(R.id.tv_manual_brand);
        tv_manual_model = (TextView) findViewById(R.id.tv_manual_model);
        tv_manual_ser_no = (TextView) findViewById(R.id.tv_manual_ser_no);
        tv_manual_qr_code = (TextView) findViewById(R.id.tv_manual_qr_code);
        tv_asset_status = (TextView) findViewById(R.id.tv_asset_status);
        tv_asset_id_tag = (TextView) findViewById(R.id.tv_asset_id_tag);

        Utils.msgText(AssetDescription.this, "877", tv_manual_brand);
        Utils.msgText(AssetDescription.this, "878", tv_manual_model);
        Utils.msgText(AssetDescription.this, "879", tv_manual_ser_no);
        Utils.msgText(AssetDescription.this, "880", tv_manual_qr_code);
        Utils.msgText(AssetDescription.this, "882", tv_asset_status);
        Utils.msgText(AssetDescription.this, "883", tv_asset_id_tag);


        et_manual_brand = (EditText) findViewById(R.id.et_manual_brand);
        et_manual_model = (EditText) findViewById(R.id.et_manual_model);
        et_manual_serial_no = (EditText) findViewById(R.id.et_manual_serial_no);
        et_manual_qr_code = (EditText) findViewById(R.id.et_manual_qr_code);
        et_manual_qr_code2 = (EditText) findViewById(R.id.et_manual_qr_code2);

        ArrayList<String> list_action = db.getParamName("1280","654");
        sp_action = (Spinner) findViewById(R.id.sp_action);
        addItemsOnSpinner(sp_action, list_action);
        int pos21 = getCategoryPos(getIntent().getExtras().getString("actionreq"),
                list_action);
        sp_action.setSelection(pos21);
        //sp_action.setEnabled(false);


        ArrayList<String> list_equipment_type = db.getParamSort("1266", tv_equipment_type,
                "654",getIntent().getExtras().getString("activityTypeId"));
        sp_equipment_type = (Spinner) findViewById(R.id.sp_equipment_type);
        addItemsOnSpinner(sp_equipment_type, list_equipment_type);
        int pos = getCategoryPos(getIntent().getExtras().getString("equipType"),
                list_equipment_type);
        sp_equipment_type.setSelection(pos);
        sp_equipment_type.setEnabled(false);

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


        sp_assets_status = (Spinner) findViewById(R.id.sp_assets_status);
        ArrayList<String> list_asset_status = db.getInciParam1("1267",tv_asset_status, "654");
        addItemsOnSpinner(sp_assets_status, list_asset_status);
        if(getIntent().getExtras().getString("ASSET_FOUND_COND")!=null){
            int pos1 = getCategoryPos(getIntent().getExtras().getString("ASSET_FOUND_COND"),
                    list_asset_status);
            sp_assets_status.setSelection(pos1);
        }

        sp_asset_id_tag = (Spinner) findViewById(R.id.sp_asset_id_tag);
        ArrayList<String> list_asset_tag_status = db.getInciParam1("1268",tv_asset_id_tag,
                "654");
        addItemsOnSpinner(sp_asset_id_tag, list_asset_tag_status);
        if(getIntent().getExtras().getString("ASSET_TAG_COND")!=null){
            int pos2 = getCategoryPos(getIntent().getExtras().getString("ASSET_TAG_COND"),
                    list_asset_tag_status);
            sp_asset_id_tag.setSelection(pos2);
        }

        db.close();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent i = new Intent(AssetDescription.this, PMTabs.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //startActivity(i);
                finish();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               //Schedule tab
               if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                   && getIntent().getExtras().getString("S").equalsIgnoreCase("S")){
                    viewPMChecklist("D");
               }else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("S")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("S")){
                   AddAsset("S");
               }
                //missed tab
                else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                        && getIntent().getExtras().getString("S").equalsIgnoreCase("M")){
                    viewPMChecklist("D");
                }else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("S")
                        && getIntent().getExtras().getString("S").equalsIgnoreCase("M")){
                   AddAsset("S");
                }
               //re-submit tab
               else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")){
                   viewPMChecklist("D");
               }else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")){
                   viewPMChecklist("D");
               }else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")){
                   viewPMChecklist("D");
               }else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("RS")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("RS")){
                   //AddAsset();
                   viewPMChecklist("RS");
               }
               //Done tab
               else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                        && getIntent().getExtras().getString("S").equalsIgnoreCase("D")){
                    viewPMChecklist("D");
               }
               //Verify tab
               else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("V")){
                   viewPMChecklist("V");
               }
               //Reject tab
               else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("J")){
                   viewPMChecklist("J");
               }
               //Pending tab
               else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("P")){
                   PMChecklistApproval("D");
               } else if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")
                       && getIntent().getExtras().getString("S").equalsIgnoreCase("P")){
                   viewPMChecklist("V");
               }else{
                   viewPMChecklist("D");
               }
            }
        });

        sp_assets_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String assetStatus = sp_assets_status.getSelectedItem().toString();
                db.open();
                String assetStatusPdesc = db.getInciParamDesc("1267",assetStatus,"654");
                db.close();
                sp_asset_id_tag.setVisibility(View.GONE);
                tv_asset_id_tag.setVisibility(View.GONE);
                tv_manual_qr_code.setVisibility(View.GONE);
                et_manual_qr_code.setVisibility(View.GONE);
                et_manual_qr_code2.setVisibility(View.GONE);
                tv_manual_brand.setVisibility(View.GONE);
                et_manual_brand.setVisibility(View.GONE);
                tv_manual_model.setVisibility(View.GONE);
                et_manual_model.setVisibility(View.GONE);
                tv_manual_ser_no.setVisibility(View.GONE);
                et_manual_serial_no.setVisibility(View.GONE);

                et_qr_value.setFocusableInTouchMode(true);
                et_qr_value.setEnabled(true);

                et_brand.setFocusableInTouchMode(true);
                et_brand.setEnabled(true);

                et_model.setFocusableInTouchMode(true);
                et_model.setEnabled(true);

                et_serial_no.setFocusableInTouchMode(true);
                et_serial_no.setEnabled(true);

                Utils.msgText(AssetDescription.this, "888", tvSubmit);

                sp_asset_id_tag.setSelection(0);
                et_manual_qr_code.setText("");
                et_manual_qr_code2.setText("");
                et_manual_brand.setText("");
                et_manual_model.setText("");
                et_manual_serial_no.setText("");

                db.open();
                ArrayList<String> list_asset_tag_status = db.getInciParam1("1268",tv_asset_id_tag, "654");
                addItemsOnSpinner(sp_asset_id_tag, list_asset_tag_status);

                if(getIntent().getExtras().getString("ASSET_TAG_COND")!=null){
                    int pos = getCategoryPos(getIntent().getExtras().getString("ASSET_TAG_COND"),
                            list_asset_tag_status);
                    sp_asset_id_tag.setSelection(pos);
                }
                db.close();
                if(getIntent().getExtras().getString("qrCodeCond")!=null){
                    et_manual_qr_code.setText(getIntent().getExtras().getString("qrCodeCond"));
                    et_manual_qr_code2.setText(getIntent().getExtras().getString("qrCodeCond"));
                }
                if(getIntent().getExtras().getString("brandCond")!=null){
                    et_manual_brand.setText(getIntent().getExtras().getString("brandCond"));
                }
                if(getIntent().getExtras().getString("modelCond")!=null){
                    et_manual_model.setText(getIntent().getExtras().getString("modelCond"));
                }
                if(getIntent().getExtras().getString("serialNoCond")!=null){
                    et_manual_serial_no.setText(getIntent().getExtras().getString("serialNoCond"));
                }

                if(assetStatusPdesc.equalsIgnoreCase("1")
                        || assetStatusPdesc.equalsIgnoreCase("3")){
                    tv_asset_id_tag.setVisibility(View.VISIBLE);
                    sp_asset_id_tag.setVisibility(View.VISIBLE);
                    if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                      || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
                      || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")){
                        et_asset_id_tag.setText("" + sp_asset_id_tag.getSelectedItem().toString());
                        et_asset_id_tag.setVisibility(View.VISIBLE);
                        sp_asset_id_tag.setVisibility(View.GONE);
                    }
                    et_manual_brand.setText("");
                    et_manual_model.setText("");
                    et_manual_serial_no.setText("");
                }else if(assetStatusPdesc.equalsIgnoreCase("2")){
                    Utils.msgText(AssetDescription.this, "889", tvSubmit);
                    sp_asset_id_tag.setSelection(0);
                    et_manual_qr_code.setText("");
                    et_manual_qr_code2.setText("");
                }
                if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                      || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
                      || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")) {
                    et_assets_status.setText("" + sp_assets_status.getSelectedItem().toString());
                    et_assets_status.setVisibility(View.VISIBLE);
                    sp_assets_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

         sp_asset_id_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String assetStatus = sp_assets_status.getSelectedItem().toString();
                String assetIdTag = sp_asset_id_tag.getSelectedItem().toString();
                db.open();
                String assetStatusPdesc = db.getInciParamDesc("1267",assetStatus,"654");
                String assetIdTagPdesc = db.getInciParamDesc("1268",assetIdTag,"654");
                db.close();
                tv_manual_qr_code.setVisibility(View.GONE);
                et_manual_qr_code.setVisibility(View.GONE);
                et_manual_qr_code2.setVisibility(View.GONE);
                tv_manual_brand.setVisibility(View.GONE);
                et_manual_brand.setVisibility(View.GONE);
                tv_manual_model.setVisibility(View.GONE);
                et_manual_model.setVisibility(View.GONE);
                tv_manual_ser_no.setVisibility(View.GONE);
                et_manual_serial_no.setVisibility(View.GONE);
                et_manual_qr_code.setText("");
                et_manual_qr_code2.setText("");
                et_manual_brand.setText("");
                et_manual_model.setText("");
                et_manual_serial_no.setText("");

                et_qr_value.setFocusableInTouchMode(true);
                et_qr_value.setEnabled(true);

                et_brand.setFocusableInTouchMode(true);
                et_brand.setEnabled(true);

                et_model.setFocusableInTouchMode(true);
                et_model.setEnabled(true);

                et_serial_no.setFocusableInTouchMode(true);
                et_serial_no.setEnabled(true);

                //Asset found and Asset tag available
                if(assetStatusPdesc.equalsIgnoreCase("1")
                    && assetIdTagPdesc.equalsIgnoreCase("1")){
                    tv_manual_qr_code.setVisibility(View.VISIBLE);
                    et_manual_qr_code.setVisibility(View.VISIBLE);
                    et_qr_value.setText(getIntent().getExtras().getString("qrCode"));
                    if(getIntent().getExtras().getString("qrCodeCond")!=null){
                        et_manual_qr_code.setText(getIntent().getExtras().getString("qrCodeCond"));
                        et_manual_qr_code2.setText(getIntent().getExtras().getString("qrCodeCond"));
                    }
                    et_qr_value.setFocusableInTouchMode(false);
                    et_qr_value.setEnabled(false);
                    scanQR(et_manual_qr_code);

               //Asset found and Asset tag not available
                }else if(assetStatusPdesc.equalsIgnoreCase("1")
                        && assetIdTagPdesc.equalsIgnoreCase("2")){
                    tv_manual_qr_code.setVisibility(View.VISIBLE);
                    et_manual_qr_code2.setVisibility(View.VISIBLE);
                    et_qr_value.setText(getIntent().getExtras().getString("qrCode"));
                    if(getIntent().getExtras().getString("qrCodeCond")!=null){
                        et_manual_qr_code.setText(getIntent().getExtras().getString("qrCodeCond"));
                        et_manual_qr_code2.setText(getIntent().getExtras().getString("qrCodeCond"));
                    }
                    et_qr_value.setFocusableInTouchMode(false);
                    et_qr_value.setEnabled(false);

                //Asset missing but similar asset found and Asset tag available
                }else if(assetStatusPdesc.equalsIgnoreCase("3")
                        && assetIdTagPdesc.equalsIgnoreCase("1")){
                    tv_manual_qr_code.setVisibility(View.VISIBLE);
                    et_manual_qr_code.setVisibility(View.VISIBLE);
                    et_qr_value.setText(getIntent().getExtras().getString("qrCode"));
                    if(getIntent().getExtras().getString("qrCodeCond")!=null){
                        et_manual_qr_code.setText(getIntent().getExtras().getString("qrCodeCond"));
                        et_manual_qr_code2.setText(getIntent().getExtras().getString("qrCodeCond"));
                    }
                    et_qr_value.setFocusableInTouchMode(false);
                    et_qr_value.setEnabled(false);
                    scanQR(et_manual_qr_code);

                //Asset missing but similar asset found and Asset tag not available
                }else if(assetStatusPdesc.equalsIgnoreCase("3")
                        && assetIdTagPdesc.equalsIgnoreCase("2")){
                    tv_manual_brand.setVisibility(View.VISIBLE);
                    et_manual_brand.setVisibility(View.VISIBLE);
                    tv_manual_model.setVisibility(View.VISIBLE);
                    et_manual_model.setVisibility(View.VISIBLE);
                    tv_manual_ser_no.setVisibility(View.VISIBLE);
                    et_manual_serial_no.setVisibility(View.VISIBLE);

                    et_brand.setText(getIntent().getExtras().getString("brand"));
                    et_brand.setFocusableInTouchMode(false);
                    et_brand.setEnabled(false);

                    et_model.setText(getIntent().getExtras().getString("model"));
                    et_model.setFocusableInTouchMode(false);
                    et_model.setEnabled(false);

                    et_serial_no.setText(getIntent().getExtras().getString("serialNo"));
                    et_serial_no.setFocusableInTouchMode(false);
                    et_serial_no.setEnabled(false);

                    if(getIntent().getExtras().getString("brandCond")!=null){
                        et_manual_brand.setText(getIntent().getExtras().getString("brandCond"));
                    }
                    if(getIntent().getExtras().getString("modelCond")!=null){
                        et_manual_model.setText(getIntent().getExtras().getString("modelCond"));
                    }
                    if(getIntent().getExtras().getString("serialNoCond")!=null){
                        et_manual_serial_no.setText(getIntent().getExtras().getString("serialNoCond"));
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private  void scanQR(final EditText et){
        et_manual_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator qrScan = new IntentIntegrator(AssetDescription.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(AssetDescription.this, "511"));
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

    public void addItemsOnSpinner(Spinner spinner, ArrayList<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(AssetDescription.this));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(AssetDescription.this));
                v.setPadding(10, 15, 10, 15);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
    }

    private boolean validate() {
        String assetStatus = sp_assets_status.getSelectedItem().toString();
        db.open();
        String assetIdTag = sp_asset_id_tag.getSelectedItem().toString();
        String assetIdTagPdesc = db.getInciParamDesc("1268",assetIdTag,"654");
        String assetStatusPdesc = db.getInciParamDesc("1267",assetStatus,"654");
        db.close();
        if (sp_equipment_type.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_equipment_type.getText().toString())) {
            String msg = "Select "+tv_equipment_type.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("BRAND")
                && validations.get("BRAND").equalsIgnoreCase("M")
                && et_brand.getVisibility()==View.VISIBLE
                && et_brand.isEnabled()
                && et_brand.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_brand.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("MODEL")
                && validations.get("MODEL").equalsIgnoreCase("M")
                && et_model.getVisibility()==View.VISIBLE
                && et_model.isEnabled()
                && et_model.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_model.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("SERIAL_NUMBER")
                && validations.get("SERIAL_NUMBER").equalsIgnoreCase("M")
                && et_serial_no.getVisibility()==View.VISIBLE
                && et_serial_no.isEnabled()
                && et_serial_no.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_ser_no.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("QR_CODE")
                && validations.get("QR_CODE").equalsIgnoreCase("M")
                && et_qr_value.getVisibility()==View.VISIBLE
                && et_qr_value.isEnabled()
                && et_qr_value.getText().toString().length()==0){
            String msg = "Enter "+tv_qr_value.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }
        else if(validations.containsKey("PAT_DATE")
                && validations.get("PAT_DATE").equalsIgnoreCase("M")
                && et_pat_date.getVisibility()==View.VISIBLE
                && et_pat_date.getText().toString().length()==0){
            String msg = "Enter "+tv_pat_date.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("SCRAP_DATE")
                && validations.get("SCRAP_DATE").equalsIgnoreCase("M")
                && et_scrap_date.getVisibility()==View.VISIBLE
                && et_scrap_date.getText().toString().length()==0){
            String msg = "Enter "+tv_scrap_date.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("CAPACITY")
                && validations.get("CAPACITY").equalsIgnoreCase("M")
                && et_capacity.getVisibility()==View.VISIBLE
                && et_capacity.getText().toString().length()==0){
            String msg = "Enter "+tv_capacity.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("TYPE")
                && validations.get("TYPE").equalsIgnoreCase("M")
                && et_type.getVisibility()==View.VISIBLE
                && et_type.getText().toString().length()==0){
            String msg = "Enter "+tv_type.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }/*else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_field1.getVisibility()==View.VISIBLE
                && et_field1.getText().toString().length()==0){
            String msg = "Enter "+tv_field1.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }*/else if(validations.containsKey("FIELD2")
                && validations.get("FIELD2").equalsIgnoreCase("M")
                && et_field2.getVisibility()==View.VISIBLE
                && et_field2.getText().toString().length()==0){
            String msg = "Enter "+tv_field2.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD3")
                && validations.get("FIELD3").equalsIgnoreCase("M")
                && et_field3.getVisibility()==View.VISIBLE
                && et_field3.getText().toString().length()==0){
            String msg = "Enter "+tv_field3.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD4")
                && validations.get("FIELD4").equalsIgnoreCase("M")
                && et_field4.getVisibility()==View.VISIBLE
                && et_field4.getText().toString().length()==0){
            String msg = "Enter "+tv_field4.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD5")
                && validations.get("FIELD5").equalsIgnoreCase("M")
                && et_field5.getVisibility()==View.VISIBLE
                && et_field5.getText().toString().length()==0){
            String msg = "Enter "+tv_field5.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD6")
                && validations.get("FIELD6").equalsIgnoreCase("M")
                && et_field6.getVisibility()==View.VISIBLE
                && et_field6.getText().toString().length()==0){
            String msg = "Enter "+tv_field6.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD7")
                && validations.get("FIELD7").equalsIgnoreCase("M")
                && et_field7.getVisibility()==View.VISIBLE
                && et_field7.getText().toString().length()==0){
            String msg = "Enter "+tv_field7.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD8")
                && validations.get("FIELD8").equalsIgnoreCase("M")
                && et_field8.getVisibility()==View.VISIBLE
                && et_field8.getText().toString().length()==0){
            String msg = "Enter "+tv_field8.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD9")
                && validations.get("FIELD9").equalsIgnoreCase("M")
                && et_field9.getVisibility()==View.VISIBLE
                && et_field9.getText().toString().length()==0){
            String msg = "Enter "+tv_field9.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD10")
                && validations.get("FIELD10").equalsIgnoreCase("M")
                && et_field10.getVisibility()==View.VISIBLE
                && et_field10.getText().toString().length()==0){
            String msg = "Enter "+tv_field10.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_warranty_date.getVisibility()==View.VISIBLE
                && et_warranty_date.getText().toString().length()==0){
            String msg = "Enter "+tv_warranty_date.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if(validations.containsKey("FIELD1")
                && validations.get("FIELD1").equalsIgnoreCase("M")
                && et_warranty_date.getVisibility()==View.VISIBLE
                && !et_warranty_date.getText().toString().equalsIgnoreCase("NA")
                && validateDate(et_warranty_date.getText().toString())){
            String msg = "Invalid Warranty Date format";
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (validations.containsKey("ASSET_ACTION")
                && validations.get("ASSET_ACTION").equalsIgnoreCase("M")
                && sp_action.getVisibility()==View.VISIBLE
                && sp_action.getSelectedItem().toString()
                .equalsIgnoreCase("Select")) {
            String msg = "Select "+tv_action.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (validations.containsKey("ASSET_REMARKS")
                && validations.get("ASSET_REMARKS").equalsIgnoreCase("M")
                && et_remarks.getVisibility()==View.VISIBLE
                && et_remarks.getText().toString().length()==0) {
            String msg = "Enter "+tv_remarks.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }

        else if (sp_assets_status.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_asset_status.getText().toString())) {
            String msg = "Select "+tv_asset_status.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        } else if (sp_asset_id_tag.getSelectedItem().toString()
                .equalsIgnoreCase("Select "+tv_asset_id_tag.getText().toString())
                && !assetStatusPdesc.equalsIgnoreCase("2")) {
            String msg = "Select "+tv_asset_id_tag.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("1")
                && assetIdTagPdesc.equalsIgnoreCase("2")
                && et_manual_qr_code2.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_qr_code.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("3")
                && assetIdTagPdesc.equalsIgnoreCase("1")
                && et_manual_qr_code.getText().toString().length()==0){
            String msg = "Please Scan "+tv_manual_qr_code.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("1")
                    && assetIdTagPdesc.equalsIgnoreCase("1")
                    && et_manual_qr_code.getText().toString().length()==0){
            String msg = "Please Scan "+tv_manual_qr_code.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("1")
                && assetIdTagPdesc.equalsIgnoreCase("1")
                && et_manual_qr_code.getText().toString().length()!=0
                && et_qr_value.getText().toString().length()!=0
                && !et_qr_value.getText().toString().trim().
                equalsIgnoreCase(et_manual_qr_code.getText().toString().trim())){
            String s = et_manual_qr_code.getText().toString().trim();
            String s1 = et_qr_value.getText().toString().trim();
            String msg = "Please scan correct "+tv_manual_qr_code.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("1")
                && assetIdTagPdesc.equalsIgnoreCase("2")
                && et_manual_qr_code2.getText().toString().length()!=0
                && et_qr_value.getText().toString().length()!=0
                && !et_qr_value.getText().toString().trim().
                equalsIgnoreCase(et_manual_qr_code2.getText().toString().trim())){
            //String msg = "Enter correct "+tv_manual_qr_code.getText().toString();
            String msg = Utils.msg(AssetDescription.this,"906");
            //String msg = "In case the asset have the same QR code shown above, copy the QR code from above";
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("3")
                && assetIdTagPdesc.equalsIgnoreCase("2")
                && et_manual_brand.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_brand.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("3")
                && assetIdTagPdesc.equalsIgnoreCase("2")
                && et_manual_model.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_model.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
            return false;
        }else if (assetStatusPdesc.equalsIgnoreCase("3")
                && assetIdTagPdesc.equalsIgnoreCase("2")
                && et_manual_serial_no.getText().toString().length()==0){
            String msg = "Enter "+tv_manual_ser_no.getText().toString();
            Utils.toastMsg(AssetDescription.this,msg);
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


    public class AddActivityAssetTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res,status;
        String assetIdTagPdesc = "", assetStatusPdesc ="",assetFoundCond = "",assetTagCond = "";
        public AddActivityAssetTask(Context con,String status) {
            this.con = con;
            this.status = status;
            db.open();

            String assetStatus = sp_assets_status.getSelectedItem().toString();
            assetStatusPdesc = db.getInciParamDesc("1267",assetStatus,"654");

            /*if(assetStatusPdesc.equalsIgnoreCase("1")
                    || assetStatusPdesc.equalsIgnoreCase("3")){

            }*/

            String assetIdTag = sp_asset_id_tag.getSelectedItem().toString();
            assetIdTagPdesc = db.getInciParamDesc("1268",assetIdTag,"654");

            assetFoundCond = db.getInciParamIdd("1267",
                    sp_assets_status.getSelectedItem().toString(), "654");

            assetTagCond = db.getInciParamIdd("1268",
                    sp_asset_id_tag.getSelectedItem().toString(),"654");
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
                        getIntent().getExtras().getString("assetId")));

                nameValuePairs.add(new BasicNameValuePair("siteID",
                        getIntent().getExtras().getString("siteId")));

                nameValuePairs.add(new BasicNameValuePair("oper",
                        "E"));

                nameValuePairs.add(new BasicNameValuePair("equipType",
                        getIntent().getExtras().getString("equipType")));

                nameValuePairs.add(new BasicNameValuePair("equipID",
                        getIntent().getExtras().getString("equipmentId")));

                //fill bu user
                nameValuePairs.add(new BasicNameValuePair("brand",
                        et_brand.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("model",
                        et_model.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("serialNum",
                        et_serial_no.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("capacity",
                        et_capacity.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("patDate",
                        et_pat_date.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("scrapDate",
                        et_scrap_date.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("type",
                        et_type.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("qrCode",
                        et_qr_value.getText().toString()));

                qrCode = "";
                if (assetStatusPdesc.equalsIgnoreCase("1")
                        && assetIdTagPdesc.equalsIgnoreCase("1")){
                    qrCode = et_manual_qr_code.getText().toString();
                }else if (assetStatusPdesc.equalsIgnoreCase("1")
                        && assetIdTagPdesc.equalsIgnoreCase("2")){
                    qrCode = et_manual_qr_code2.getText().toString();
                }//Asset missing but similar asset found //Asset tag available
                else if (assetStatusPdesc.equalsIgnoreCase("3")
                        && assetIdTagPdesc.equalsIgnoreCase("1")){
                    qrCode = et_manual_qr_code.getText().toString();
                }
                /*else if (assetStatusPdesc.equalsIgnoreCase("3")
                        && assetIdTagPdesc.equalsIgnoreCase("2")){
                    qrCode = et_manual_qr_code2.getText().toString();
                }*/


                //condition
                nameValuePairs.add(new BasicNameValuePair("assetFoundCond", assetFoundCond));
                nameValuePairs.add(new BasicNameValuePair("assetTagCond", assetTagCond));
                nameValuePairs.add(new BasicNameValuePair("qrCodeCond",
                        qrCode));
               nameValuePairs.add(new BasicNameValuePair("modelCond",et_manual_model.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("serialNumCond",et_manual_serial_no.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("brandCond",et_manual_brand.getText().toString()));


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
                         success = subArray.getJSONObject(0).getString("respFlag");
                         msg = subArray.getJSONObject(0).getString("errorDesc");
                         Utils.toastMsg(AssetDescription.this,msg);
                         //Toast.makeText(AssetDescription.this, msg, Toast.LENGTH_SHORT).show();
                        if (success.equals("S")) {
                            nextActivity(status);
                        }
                    }
                } catch (Exception e) {

                }
            } else {
                nextActivity(status);
            }
            super.onPostExecute(result);
        }
    }

    public void nextActivity(String status){
        String assetStatus = sp_assets_status.getSelectedItem().toString();
        db.open();
        String assetStatusPdesc = db.getInciParamDesc("1267", assetStatus, "654");
        db.close();
        Intent i = null;
        if (assetStatusPdesc.equalsIgnoreCase("1")
                || assetStatusPdesc.equalsIgnoreCase("3")) {
            mAppPreferences.setPMAssetBackTask(1);
            i = new Intent(AssetDescription.this, PMChecklist.class);
            i.putExtra("qrCodeValidate", qrCode);
            i.putExtra("AssetDgType", getIntent().getExtras().getString("AssetDgType"));
            i.putExtra("qrCode", getIntent().getExtras().getString("qrCode"));
            i.putExtra("assetId", getIntent().getExtras().getString("assetId"));
            i.putExtra("equipmentId", getIntent().getExtras().getString("equipmentId"));
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
            //startActivity(i);
            //finish();
            new GetPMCheckList(AssetDescription.this,i,
                    getIntent().getExtras().getString("siteId"),
                    getIntent().getExtras().getString("activityTypeId"),
                    getIntent().getExtras().getString("txn"),
                    getIntent().getExtras().getString("equipmentId"),
                    getIntent().getExtras().getString("assetId"),status).execute();


        } else if (assetStatusPdesc.equalsIgnoreCase("2")) {
            finish();
        }


    }

    private int getCategoryPos(String category, ArrayList<String> list) {
        return list.indexOf(category);
    }

    public void showFields(Map<String,String> map){

        if(map.containsKey("BRAND")){
            tv_brand.setVisibility(View.VISIBLE);
            et_brand.setVisibility(View.VISIBLE);
        }else{
            tv_brand.setVisibility(View.GONE);
            et_brand.setVisibility(View.GONE);
        }

        if(map.containsKey("MODEL")){
            tv_model.setVisibility(View.VISIBLE);
            et_model.setVisibility(View.VISIBLE);
        }else{
            tv_model.setVisibility(View.GONE);
            et_model.setVisibility(View.GONE);
        }

        if(map.containsKey("SERIAL_NUMBER")){
            tv_ser_no.setVisibility(View.VISIBLE);
            et_serial_no.setVisibility(View.VISIBLE);
        }else{
            tv_ser_no.setVisibility(View.GONE);
            et_serial_no.setVisibility(View.GONE);
        }

        tv_qr_value.setVisibility(View.VISIBLE);
        et_qr_value.setVisibility(View.VISIBLE);


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
            tv_field1.setVisibility(View.GONE);
            et_field1.setVisibility(View.GONE);
        }else{
            tv_warranty_date.setVisibility(View.GONE);
            et_warranty_date.setVisibility(View.GONE);
            tv_field1.setVisibility(View.GONE);
            et_field1.setVisibility(View.GONE);
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



        if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
                || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
                || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")) {
            et_equipment_type.setText("" + sp_equipment_type.getSelectedItem().toString());
            et_equipment_type.setVisibility(View.VISIBLE);
            sp_equipment_type.setVisibility(View.GONE);
        }

    }

    private class GetImage extends AsyncTask<Void, Void, Void> {
        Context con;
        ProgressDialog pd;
        BeanGetImageList imageList;
        String txnId,scDate,activityId,sId,dgType,etsSid,imguploadflag,assetID,status = "D";;
        Intent i;
        int mode = 0;
        private GetImage(Context con,Intent i,String txnId,String scDate,String activityId,String sId,
                         String etsSid,String dgType,String imguploadflag,String assetID,
                         String status,int mode) {
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
                nameValuePairs.add(new BasicNameValuePair("scheduledDate",scDate));
                nameValuePairs.add(new BasicNameValuePair("dgType", dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID",assetID));
                String url = "";
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_GetPmImage;
                }else{
                    url=moduleUrl+ WebMethods.url_GetPmImage;
                }

                String response = Utils.httpPostRequest(con,url, nameValuePairs);
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
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            DataBaseHelper dataBaseHelper = new DataBaseHelper(AssetDescription.this);
            dataBaseHelper.open();
            dataBaseHelper.deleteActivityImages(txnId,assetID);
            if (imageList == null) {
            }else if (imageList.getImageList().size() > 0) {
                for (int i = 0; i < imageList.getImageList().size(); i++) {
                    if (imageList.getImageList().get( i ).getPreImgPath() != null &&
                            imageList.getImageList().get( i ).getPreImgPath() != "") {
                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr = new String[1000];
                        imgpathArr = imageList.getImageList().get( i ).getPreImgPath().split( "\\," );

                        if(imageList.getImageList().get(i).getPreImgName()!=null){
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get( i ).getPreImgName().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getPreLat()!=null){
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get( i ).getPreLat().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getPreLongt()!=null){
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get( i ).getPreLongt().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getPreImgTimeStamp()!=null){
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get( i ).getPreImgTimeStamp().split( "\\," );
                        }

                        if(imgpathArr!=null && imgpathArr.length>0) {
                            for(int j=0; j<imgpathArr.length; j++){

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if(timeArr!=null && imgpathArr.length<=timeArr.length){
                                    time = timeArr[j];
                                }

                                if(nameArr!=null && imgpathArr.length<=nameArr.length){
                                    name = nameArr[j];
                                }

                                if(latArr!=null && imgpathArr.length<=latArr.length){
                                    lat = latArr[j];
                                }else if(lat.length()==1 && imageList.getImageList().get( i ).getLATITUDE()!=null){
                                    lat = imageList.getImageList().get( i ).getLATITUDE();
                                }


                                if(longArr!=null && imgpathArr.length<=longArr.length){
                                    longi = longArr[j];
                                }else if(longi.length()==1 && imageList.getImageList().get( i ).getLONGITUDE()!=null){
                                    longi = imageList.getImageList().get( i ).getLONGITUDE();
                                }

                                if(imguploadflag.equalsIgnoreCase("2")){
                                    clId = imageList.getImageList().get(i).getClID();
                                }

                                dataBaseHelper.insertImages(
                                        txnId,clId,imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
                                        name,lat,longi,Utils.DateTimeStamp(),time,1,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],
                                        mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,
                                        assetID);
                            }
                        }
                    }

                    if (imageList.getImageList().get( i ).getIMAGE_PATH() != null &&
                            imageList.getImageList().get( i ).getIMAGE_PATH() != "") {
                        String[] imgpathArr = null;
                        String[] timeArr = null;
                        String[] nameArr = null;
                        String[] latArr = null;
                        String[] longArr = null;

                        imgpathArr =new String[1000];
                        imgpathArr = imageList.getImageList().get(i).getIMAGE_PATH().split( "\\," );

                        if(imageList.getImageList().get(i).getIMAGENAME()!=null){
                            nameArr = new String[1000];
                            nameArr = imageList.getImageList().get(i).getIMAGENAME().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getLATITUDE()!=null){
                            latArr = new String[1000];
                            latArr = imageList.getImageList().get( i ).getLATITUDE().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getLONGITUDE()!=null){
                            longArr = new String[1000];
                            longArr = imageList.getImageList().get( i ).getLONGITUDE().split( "\\," );
                        }

                        if(imageList.getImageList().get(i).getImgTimeStamp()!=null){
                            timeArr = new String[1000];
                            timeArr = imageList.getImageList().get(i).getImgTimeStamp().split( "\\," );
                        }

                        if(imgpathArr!=null && imgpathArr.length>0) {
                            for (int j = 0; j < imgpathArr.length; j++) {

                                String name = " ";
                                String time = " ";
                                String lat = " ";
                                String longi = " ";
                                String clId = "0";
                                if(timeArr!=null && imgpathArr.length<=timeArr.length){
                                    time = timeArr[j];
                                }

                                if(nameArr!=null && imgpathArr.length<=nameArr.length){
                                    name = nameArr[j];
                                }

                                if(latArr!=null && imgpathArr.length<=latArr.length){
                                    lat = latArr[j];
                                }else if(lat.length()==1 && imageList.getImageList().get( i ).getLATITUDE()!=null){
                                    lat = imageList.getImageList().get( i ).getLATITUDE();
                                }


                                if(longArr!=null && imgpathArr.length<=longArr.length){
                                    longi = longArr[j];
                                }else if(longi.length()==1 && imageList.getImageList().get( i ).getLONGITUDE()!=null){
                                    longi = imageList.getImageList().get( i ).getLONGITUDE();
                                }

                                if(imguploadflag.equalsIgnoreCase("2")){
                                    clId = imageList.getImageList().get(i).getClID();
                                }
                                        dataBaseHelper.insertImages(
                                        txnId,clId,imageList.getImageList().get( i ).getImageURL()+imgpathArr[j],
                                        name,lat,longi,Utils.DateTimeStamp(),time,2,3,
                                        scDate,activityId,sId,dgType,imgpathArr[j],
                                                mAppPreferences.getConfigIP()+WebMethods.url_SaveAPI,
                                                assetID
                                );
                            }
                        }
                    }
                }
            }
            dataBaseHelper.close();
            if (Utils.isNetworkAvailable(AssetDescription.this)) {
              new CheckListDetailsTask(AssetDescription.this,i,txnId,sId,scDate,dgType,
                        activityId,assetID,status,mode).execute();
            } else {
                Utils.toast(AssetDescription.this, "17");
            }
            super.onPostExecute(result);
        }
    }

    private class CheckListDetailsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String txnId,scDate,activityId,sId,dgType,assetID;
        BeanCheckListDetails PMCheckListDetails;
        String status = "D";
        int mode = 0;
        private CheckListDetailsTask(Context con,Intent i,String txnId,String sId,String scDate,
                                     String dgType,String activityId,String assetID,
                                     String status,int mode) {
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
                nameValuePairs.add(new BasicNameValuePair("checkListType",activityId));
                nameValuePairs.add(new BasicNameValuePair("checkListDate",scDate));
                nameValuePairs.add(new BasicNameValuePair("status", status));
                nameValuePairs.add(new BasicNameValuePair("languageCode",mAppPreferences.getLanCode()));
                nameValuePairs.add(new BasicNameValuePair("dgType",dgType));
                nameValuePairs.add(new BasicNameValuePair("assetID",assetID));
                nameValuePairs.add(new BasicNameValuePair("equipID",""));
                String url = "";
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_getCheckListDetails;
                }else{
                    url=moduleUrl+ WebMethods.url_getCheckListDetails;
                }
                String res = Utils.httpPostRequest(con,url, nameValuePairs);
                Gson gson = new Gson();
                PMCheckListDetails = gson.fromJson(res,BeanCheckListDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
                PMCheckListDetails = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (PMCheckListDetails == null) {
                Utils.toast(AssetDescription.this, "13");
            } else{
                if(status.equalsIgnoreCase("RS")){
                    if (PMCheckListDetails.getPMCheckListDetail()!=null &&
                            PMCheckListDetails.getPMCheckListDetail().size() > 0) {
                        db.open();
                        if (db.isAlreadyAutoSaveChk(txnId,assetID) == 0) {
                            db.insertAutoSaveChkList(txnId,"", "", "",assetID);
                        }
                        db.close();
                        for(int a = 0; a<PMCheckListDetails.getPMCheckListDetail().size();a++){
                            sharePrefence(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getStatus(),txnId,assetID);

                            sharePrefenceRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getViRemark(),txnId,assetID);

                            sharePrefenceReviewRemarks(PMCheckListDetails.getPMCheckListDetail().get(a).getFieldId(),
                                    PMCheckListDetails.getPMCheckListDetail().get(a).getrRemark(),txnId,assetID);
                        }

                    } else {

                    }
                    if (pd !=null && pd.isShowing()) {
                        pd.dismiss();
                    }
                   AddAsset(status);
                }else{
                    DataBaseHelper dbHelper = new DataBaseHelper(AssetDescription.this);
                    dbHelper.open();
                    dbHelper.clearReviewerCheclist();
                    if (PMCheckListDetails.getPMCheckListDetail()!=null
                            && PMCheckListDetails.getPMCheckListDetail().size() > 0) {

                        if(mode==1) {
                            dbHelper.insertReviewerCheckList(PMCheckListDetails.getPMCheckListDetail(),
                                    activityId,txnId,AssetDescription.this,1,
                                    getIntent().getExtras().getString("assetId"));
                        }else{
                            dbHelper.insertViewCheckList(PMCheckListDetails.getPMCheckListDetail(), activityId);
                        }
                        dbHelper.close();

                    }

                    if (pd !=null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    startActivity(i);
                    finish();
                }
            }
            super.onPostExecute(result);
        }
    }

    public void viewPMChecklist(String status){
        Intent i = new Intent( AssetDescription.this, ViewPMCheckList.class);
        String txnId = "",sDate = "",actid = "",sid = "",etsId = "",dgType = "",
                imguploadflag = "2",assetID="";
        i.putExtra("qrCode", getIntent().getExtras().getString("qrCode"));
        i.putExtra("assetId", getIntent().getExtras().getString("assetId"));
        i.putExtra("S",status);
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
        i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
        i.putExtra("Status", getIntent().getExtras().getString("Status"));
        i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
        i.putExtra("txn", getIntent().getExtras().getString("txn"));
        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
        i.putExtra( "imgUploadFlag",imguploadflag);
        i.putExtra( "rCat",getIntent().getExtras().getString("rCat"));
        i.putExtra( "rejRmks",getIntent().getExtras().getString("rejRmks"));
        i.putExtra( "rvDate",getIntent().getExtras().getString("rvDate"));

        sid =  getIntent().getExtras().getString("siteId");
        sDate = getIntent().getExtras().getString("scheduledDate");
        actid = getIntent().getExtras().getString("activityTypeId");
        txnId = getIntent().getExtras().getString("txn");
        dgType = getIntent().getExtras().getString("dgType");
        etsId =  getIntent().getExtras().getString("etsSid");
        assetID = getIntent().getExtras().getString("assetId");

        if (Utils.isNetworkAvailable(AssetDescription.this)) {
            new GetImage(AssetDescription.this,i,txnId,sDate,actid,sid,etsId,dgType,
                    imguploadflag,assetID,status,0).execute();
        } else {
            //No Internet Connection;
            Utils.toast(AssetDescription.this, "17");
        }
    }



    public void PMChecklistApproval(String status){
        mAppPreferences.setPMAssetBackTask(1);
        Intent i = new Intent( AssetDescription.this, PMChecklistApproval.class);
        String txnId = "",sDate = "",actid = "",sid = "",etsId = "",dgType = "",
                imguploadflag = "2",assetID="";

        i.putExtra("assetCondition", getIntent().getExtras().getString("ASSET_FOUND_COND"));
        i.putExtra("assetId", getIntent().getExtras().getString("assetId"));
        i.putExtra("equipmentId", getIntent().getExtras().getString("equipmentId"));
        i.putExtra("S","P");
        i.putExtra("scheduledDate", getIntent().getExtras().getString("scheduledDate"));
        i.putExtra("siteId", getIntent().getExtras().getString("siteId"));
        i.putExtra("siteName", getIntent().getExtras().getString("siteName"));
        i.putExtra("activityTypeId", getIntent().getExtras().getString("activityTypeId"));
        i.putExtra("paramName", getIntent().getExtras().getString("paramName"));
        i.putExtra("Status", getIntent().getExtras().getString("Status"));
        i.putExtra("dgType", getIntent().getExtras().getString("dgType"));
        i.putExtra("txn", getIntent().getExtras().getString("txn"));
        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
        i.putExtra( "imgUploadFlag",imguploadflag);
        i.putExtra( "rCat","");
        i.putExtra( "rejRmks","");
        i.putExtra( "rvDate","");

        i.putExtra("asdt", getIntent().getExtras().getString("asdt"));
        i.putExtra("aedt", getIntent().getExtras().getString("aedt"));
        i.putExtra( "arId", getIntent().getExtras().getString("arId"));
        i.putExtra("amStatus", getIntent().getExtras().getString("amStatus"));
        i.putExtra("rating",getIntent().getExtras().getString("rating"));

        sid =  getIntent().getExtras().getString("siteId");
        sDate = getIntent().getExtras().getString("scheduledDate");
        actid = getIntent().getExtras().getString("activityTypeId");
        txnId = getIntent().getExtras().getString("txn");
        dgType = getIntent().getExtras().getString("dgType");
        etsId =  getIntent().getExtras().getString("etsSid");
        assetID = getIntent().getExtras().getString("assetId");
        db.open();
        //db.deleteAutoSaveChk(txnId);
        //db.insertAutoSaveChkList(txnId,"","","",assetID);
        if(db.isAlreadyAutoSaveChk(txnId,assetID)== 0){
            db.insertAutoSaveChkList(txnId,"","","",assetID);
            temp_flag =1;
        }
        else
        {
            temp_flag=0;
        }
        db.close();

        if (Utils.isNetworkAvailable(AssetDescription.this)) {
            new GetImage(AssetDescription.this,i,txnId,sDate,actid,sid,etsId,dgType,
                    imguploadflag,assetID,status,1).execute();
        } else {
            //No Internet Connection;
            Utils.toast(AssetDescription.this, "17");
        }
    }

    public void AddAsset(String status){
        if (validate()) {
            if(Utils.isNetworkAvailable(AssetDescription.this)) {
                AddActivityAssetTask task =
                        new AddActivityAssetTask(AssetDescription.this,status);
                task.execute();
            }else{
                Utils.toast(AssetDescription.this, "17" );
            }
        }
    }

    JSONObject savedDataJsonObjRemarks = null;
    JSONObject savedDataJsonObjReviewRemarks = null;
    JSONObject savedDataJsonObj = null;

    public void sharePrefence(String id,String s,String txnId,String assetID){

        if(savedDataJsonObj==null){
            savedDataJsonObj = new JSONObject();
        }

        try {
            savedDataJsonObj.remove( "" + id);
            savedDataJsonObj.put( "" +id,s);
        } catch (JSONException e) {

        }

        DataBaseHelper db10 = new DataBaseHelper(AssetDescription.this);
        db10.open();
        db10.updateAutoSaveChkList(txnId,"","",savedDataJsonObj.toString(),assetID);
        db10.close();

    }

    public void sharePrefenceRemarks(String id,String s,String txnId,String assetID){

        if(savedDataJsonObjRemarks==null){
            savedDataJsonObjRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjRemarks.remove( "" + id);
            savedDataJsonObjRemarks.put( "" +id,s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(AssetDescription.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId,savedDataJsonObjRemarks.toString(),assetID);
        //db10.updateAutoSaveRemarks1(txnId,savedDataJsonObjRemarks.toString(),savedDataJsonObjRemarks.toString());
        db10.close();
    }

    public void sharePrefenceReviewRemarks(String id,String s,String txnId,String assetID){

        if(savedDataJsonObjReviewRemarks==null){
            savedDataJsonObjReviewRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjReviewRemarks.remove( "" + id);
            savedDataJsonObjReviewRemarks.put( "" +id,s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(AssetDescription.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId,savedDataJsonObjRemarks.toString(),assetID);
        db10.updateAutoSaveRemarks1(txnId,savedDataJsonObjRemarks.toString(),
                savedDataJsonObjReviewRemarks.toString(),assetID);
        db10.close();
    }


    private class GetPMCheckList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        Intent i;
        String siteId = "",chklistType = "0",txnId="",status;
        BeanCheckListDetails PMCheckList;
        String equipmentID = "-1";
        String assetId = "-1";
        private GetPMCheckList(Context con,Intent i,String siteId,String chklistType,
                               String txnId,String equipmentID,String assetId,String status) {
            this.con = con;
            this.i = i;
            this.siteId = siteId;
            this.chklistType = chklistType;
            this.txnId = txnId;
            this.equipmentID = equipmentID;
            this.assetId = assetId;
            this.status=status;
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
            nameValuePairs.add( new BasicNameValuePair( "checkListDate",getIntent().getExtras().getString("scheduledDate") ) );
            nameValuePairs.add( new BasicNameValuePair( "status", "S")); //S or M get blank checklistdata
            nameValuePairs.add( new BasicNameValuePair( "dgType", "" ));
            nameValuePairs.add( new BasicNameValuePair( "languageCode", mAppPreferences.getLanCode() ) );
            nameValuePairs.add( new BasicNameValuePair( "assetID", ""));
            nameValuePairs.add( new BasicNameValuePair( "equipID", equipmentID));
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
                Utils.toast(AssetDescription.this,"226");
            } else if (PMCheckList != null) {
                if (PMCheckList.getPMCheckListDetail()!=null
                        && PMCheckList.getPMCheckListDetail().size() > 0) {
                    db.open();

                    int alreadyJson = 0;
                    if (status!="RS" &&
                            db.isAlreadyAutoSaveChk(txnId,assetId) == 0) {
                        db.insertAutoSaveChkList(txnId,"", "", "",assetId);
                        alreadyJson = 1;
                    }



                    db.clearCheckList("655",chklistType,equipmentID);
                    db.insertPMCheckListForm(PMCheckList.getPMCheckListDetail(),
                            "655",alreadyJson,txnId,AssetDescription.this,
                            equipmentID,assetId);
                    db.close();
                }
            } else {
                Utils.toast(AssetDescription.this,"13");
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            startActivity(i);
            finish();
            super.onPostExecute( result );
        }
    }

    public void disbledFields(){
        if(getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("D")
           || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("J")
           || getIntent().getExtras().getString("assetStatus").equalsIgnoreCase("R")
           ){

                et_brand.setEnabled(false);
                et_brand.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_brand.setBackgroundResource( R.drawable.et_data);

                et_model.setEnabled(false);
                et_model.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_model.setBackgroundResource( R.drawable.et_data);

                et_serial_no.setEnabled(false);
                et_serial_no.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_serial_no.setBackgroundResource( R.drawable.et_data);

                et_qr_value.setEnabled(false);
                et_qr_value.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_qr_value.setBackgroundResource( R.drawable.et_data);

                et_pat_date.setEnabled(false);
                et_pat_date.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_pat_date.setBackgroundResource( R.drawable.et_data);

                et_scrap_date.setEnabled(false);
                et_scrap_date.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_scrap_date.setBackgroundResource( R.drawable.et_data);

                et_capacity.setEnabled(false);
                et_capacity.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_capacity.setBackgroundResource( R.drawable.et_data);

                et_type.setEnabled(false);
                et_type.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_type.setBackgroundResource( R.drawable.et_data);

                et_field1.setEnabled(false);
                et_field1.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field1.setBackgroundResource( R.drawable.et_data);

                et_field2.setEnabled(false);
                et_field2.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field2.setBackgroundResource( R.drawable.et_data);

                et_field3.setEnabled(false);
                et_field3.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field3.setBackgroundResource( R.drawable.et_data);

                et_field4.setEnabled(false);
                et_field4.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field4.setBackgroundResource( R.drawable.et_data);

                et_field5.setEnabled(false);
                et_field5.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field5.setBackgroundResource( R.drawable.et_data);

                et_field6.setEnabled(false);
                et_field6.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field6.setBackgroundResource( R.drawable.et_data);

                et_field7.setEnabled(false);
                et_field7.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field7.setBackgroundResource( R.drawable.et_data);

                et_field8.setEnabled(false);
                et_field8.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field8.setBackgroundResource( R.drawable.et_data);

                et_field9.setEnabled(false);
                et_field9.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field9.setBackgroundResource( R.drawable.et_data);

                et_field10.setEnabled(false);
                et_field10.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_field10.setBackgroundResource( R.drawable.et_data);

                et_manual_serial_no.setEnabled(false);
                et_manual_serial_no.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_manual_serial_no.setBackgroundResource( R.drawable.et_data);

                et_manual_model.setEnabled(false);
                et_manual_model.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_manual_model.setBackgroundResource( R.drawable.et_data);

                et_manual_brand.setEnabled(false);
                et_manual_brand.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_manual_brand.setBackgroundResource( R.drawable.et_data);

                et_manual_qr_code2.setEnabled(false);
                et_manual_qr_code2.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_manual_qr_code2.setBackgroundResource( R.drawable.et_data);

                et_manual_qr_code.setEnabled(false);
                et_manual_qr_code.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_manual_qr_code.setBackgroundResource( R.drawable.et_data);

                et_remarks.setEnabled(false);
                et_remarks.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_remarks.setBackgroundResource( R.drawable.et_data);

                et_warranty_date.setEnabled(false);
                et_warranty_date.setTextColor( Color.parseColor( "#A4A0A0" ));
                et_warranty_date.setBackgroundResource( R.drawable.et_data);

                sp_action.setEnabled(false);

      }
    }
}




