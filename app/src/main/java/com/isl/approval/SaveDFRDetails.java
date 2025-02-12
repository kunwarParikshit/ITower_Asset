package com.isl.approval;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.energy.filling.FillingTransactionDetails;
import com.isl.energy.filling.FillingTransactions;
import com.isl.modal.BeanLastFillingTransList;
import com.isl.modal.DFRMoreDetails;
import com.isl.modal.ResponeDfrSave;
import com.isl.util.DigitsAfterDecimal;
import com.isl.util.InputMix;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 6/12/2019.
 * Modified by Vishal on 13/Jan/2019
 * Purpose to change mode while submiting data for dfr mode 1, instead of 0(Merging relase 1.90 bug fixing #1272)
 * Version 0.1
 */

public class SaveDFRDetails extends Activity {
    TextView tv_header, tv_last_detail,tv_current_detail,tv_siteId, tv_date, tv_standard_cph,
            tv_actual_cph, tv_actual_cph_dfr, tv_actual_cph_mobile, tv_actual_cph_sensor,
            tv_transaction_type,
            tv_fill_qty, tv_fill_qty_dfr, tv_fill_qty_mobile, tv_fill_qty_sensor,
            tv_genset_reading, tv_genset_reading_dfr, tv_genset_reading_mobile, tv_genset_reading_sensor,
            tv_opening_stock, tv_opening_stock_dfr, tv_opening_stock_mobile, tv_opening_stock_sensor,
            tv_grid_reading, tv_grid_reading_dfr, tv_grid_reading_mobile, tv_grid_reading_sensor,
            tv_mortable, tv_mortable_dfr, tv_mortable_mobile, tv_mortable_sensor, tv_remarks;
    EditText et_remarks, et_site_id, et_date, et_standard_cph,
            et_actual_cph_dfr, et_actual_cph_mobile, et_actual_cph_sensor,
            et_fill_qty_dfr, et_fill_qty_mobile, et_fill_qty_sensor,
            et_genset_reading_dfr, et_genset_reading_mobile, et_genset_reading_sensor,
            et_opening_stock_dfr, et_opening_stock_mobile, et_opening_stock_sensor,
            et_grid_reading_dfr, et_grid_reading_mobile, et_grid_reading_sensor,et_fwoid;
    LinearLayout rg_tran_type;
    RadioButton rb_dfr, rb_mobile, rb_sensor, rb_reject;
    CheckBox cb_dfr, cb_mobile, cb_sensor;
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    String dfrRight = "", moduleUrl = "", tran_date = "", siteId = "",dgType="",dgType1="",fwoid="";
    DFRMoreDetails response_list;
    int m = 0;
    String msgLbl = "";
    boolean status = true;
    int alertCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_dfr_details);
        init();
        selectReject();

        tv_last_detail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Dialog();
            }
        });

        tv_current_detail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                  if (Utils.isNetworkAvailable( SaveDFRDetails.this)) {
                        new FillingReport( SaveDFRDetails.this,
                                et_site_id.getText().toString().trim(), dgType1,
                                "Date", et_date.getText().toString().trim(),
                                et_date.getText().toString().trim()).execute();
                    } else {
                          Utils.toast( SaveDFRDetails.this, "17");
                    }

                }});

        Button bt_back = (Button) findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent i = new Intent(DFRApprovalCount.this, Approval.class);
                //startActivity(i);
                finish();

            }
        });

        TextView bt_submit = (TextView) findViewById(R.id.bt_submit);
        Utils.msgText(SaveDFRDetails.this, "431", bt_submit);
        if (dfrRight.contains("A")) {
            bt_submit.setVisibility(View.VISIBLE);
        } else {
            bt_submit.setVisibility(View.GONE);
        }
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (validate()) {
                    int mode = 0;
                    String app = "";
                    String aos = "", afill = "", adgr = "", aebr = "", ahilst = "No";
                    if (rb_dfr.isChecked()) {
                        msgLbl = "approved";
                        mode = 1;//0.1
                        app = "DFR";
                        aos = et_opening_stock_dfr.getText().toString().trim();
                        afill = et_fill_qty_dfr.getText().toString().trim();
                        adgr = et_genset_reading_dfr.getText().toString().trim();
                        aebr = et_grid_reading_dfr.getText().toString().trim();
                        if (cb_dfr.isChecked()) {
                            ahilst = "Yes";
                        } else {
                            ahilst = "No";
                        }
                    } else if (rb_mobile.isChecked()) {
                        msgLbl = "approved";
                        mode = 2;//0.1
                        app = "MOBILE";
                        aos = et_opening_stock_mobile.getText().toString().trim();
                        afill = et_fill_qty_mobile.getText().toString().trim();
                        adgr = et_genset_reading_mobile.getText().toString().trim();
                        aebr = et_grid_reading_mobile.getText().toString().trim();
                        if (cb_mobile.isChecked()) {
                            ahilst = "Yes";
                        } else {
                            ahilst = "No";
                        }
                    } else if (rb_sensor.isChecked()) {
                        msgLbl = "approved";
                        mode = 3;//0.1
                        app = "RMS";
                        aos = et_opening_stock_sensor.getText().toString().trim();
                        afill = et_fill_qty_sensor.getText().toString().trim();
                        adgr = et_genset_reading_sensor.getText().toString().trim();
                        aebr = et_grid_reading_sensor.getText().toString().trim();
                        if (cb_sensor.isChecked()) {
                            ahilst = "Yes";
                        } else {
                            ahilst = "No";
                        }
                    } else if (rb_reject.isChecked()) {

                        msgLbl = "Rejected";
                        mode = 0;
                        app = "REJECT";
                    }
                    if (mode == 0) {
                        alert(2, "468");
                    } else {
                        ActionOnDFR(ahilst, mode, app,aos,afill,adgr,aebr);
                    }


                }

            }
        });

        if (Utils.isNetworkAvailable(SaveDFRDetails.this)) {
            new DFRApprovalGridTask(SaveDFRDetails.this).execute();
        } else {
            //message for No Internet Connection;
            Utils.toast(SaveDFRDetails.this, "17");
        }


        rb_dfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_fill_qty_dfr.getText().toString().length() > 0) {
                    rb_dfr.setChecked(true);
                    rb_mobile.setChecked(false);
                    rb_sensor.setChecked(false);
                    rb_reject.setChecked(false);
                    setValue();
                    selectDFR();
                    m = 1;
                } else {
                    rb_dfr.setChecked(false);
                    if (m == 2) {
                        rb_mobile.setChecked(true);
                        rb_sensor.setChecked(false);
                        rb_reject.setChecked(false);
                    } else if (m == 3) {
                        rb_mobile.setChecked(false);
                        rb_sensor.setChecked(true);
                        rb_reject.setChecked(false);
                    } else if (m == 4) {
                        rb_mobile.setChecked(false);
                        rb_sensor.setChecked(false);
                        rb_reject.setChecked(true);
                    }

                }


            }
        });

        rb_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_fill_qty_mobile.getText().toString().length() > 0) {
                    rb_dfr.setChecked(false);
                    rb_mobile.setChecked(true);
                    rb_sensor.setChecked(false);
                    rb_reject.setChecked(false);
                    setValue();
                    selectMobile();
                    m = 2;
                } else {
                    rb_mobile.setChecked(false);
                    if (m == 1) {
                        rb_dfr.setChecked(true);
                        rb_sensor.setChecked(false);
                        rb_reject.setChecked(false);
                    } else if (m == 3) {
                        rb_dfr.setChecked(false);
                        rb_sensor.setChecked(true);
                        rb_reject.setChecked(false);
                    } else if (m == 4) {
                        rb_dfr.setChecked(false);
                        rb_sensor.setChecked(false);
                        rb_reject.setChecked(true);
                    }
                }
            }
        });

        rb_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_fill_qty_sensor.getText().toString().length() > 0) {
                    rb_dfr.setChecked(false);
                    rb_mobile.setChecked(false);
                    rb_sensor.setChecked(true);
                    rb_reject.setChecked(false);
                    setValue();
                    selectSensor();
                    m = 3;
                } else {
                    rb_sensor.setChecked(false);
                    if (m == 1) {
                        rb_dfr.setChecked(true);
                        rb_mobile.setChecked(false);
                        rb_reject.setChecked(false);
                    } else if (m == 2) {
                        rb_dfr.setChecked(false);
                        rb_mobile.setChecked(true);
                        rb_reject.setChecked(false);
                    } else if (m == 4) {
                        rb_dfr.setChecked(false);
                        rb_mobile.setChecked(false);
                        rb_reject.setChecked(true);
                    }
                }

            }
        });

        rb_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setValue();
                selectReject();
                m = 4;
                rb_dfr.setChecked(false);
                rb_mobile.setChecked(false);
                rb_sensor.setChecked(false);
                rb_reject.setChecked(true);

            }
        });
    }

    public void ActionOnDFR(String ahilst, int mode, String app,String aos,String afill,String adgr,String aebr) {
        if (mAppPreferences.getSiteMotorable() == 0) {
            ahilst = "No";
        }
        String xmlData = submitData(mode, app, et_remarks.getText().toString().trim().replaceAll("&", "&amp;"), aos, afill, adgr, aebr, ahilst);
        if (Utils.isNetworkAvailable(SaveDFRDetails.this)) {
            new SaveDFRApproval(SaveDFRDetails.this, xmlData).execute();
        } else {
            JSONObject obj = new JSONObject();
            try {
                obj.put("countryID", mAppPreferences.getCounrtyID());
                obj.put("hubID", mAppPreferences.getHubID());
                obj.put("regionID", mAppPreferences.getRegionId());
                obj.put("circleID", mAppPreferences.getCircleID());
                obj.put("zoneID", mAppPreferences.getZoneID());
                obj.put("clusterID", mAppPreferences.getClusterID());
                obj.put("omeID", mAppPreferences.getPIOMEID());
                obj.put("xmlData", xmlData);
                obj.put("userId", mAppPreferences.getUserId());

                obj.put("sid", et_site_id.getText().toString());
                obj.put("schDt", et_date.getText().toString());
                obj.put("dg", response_list.getGetDFRApproval().get(0).getDG_TYPE());
                obj.put("filler", response_list.getGetDFRApproval().get(0).getFILLER_NAME());
                obj.put("omeName", response_list.getGetDFRApproval().get(0).getOME_NAME());
                //response_list.getGetDFRApproval().get(0).get

            } catch (JSONException e) {
            }
            // Showing message of data saving locally when Network
            // is not present.
            DataBaseHelper db = new DataBaseHelper(SaveDFRDetails.this);
            db.open();
            //db.insertDataLocally( "SPARE",obj.toString, status, spareId,tranType, mAppPreferences.getUserId(),id,remarks);
            db.insertDataLocally("DFR", obj.toString(), "", "", "", mAppPreferences.getUserId(),"");
            db.close();
            Utils.toast(SaveDFRDetails.this, "66");
            Intent i = new Intent(SaveDFRDetails.this, SparePartTabs.class);
            startActivity(i);
            finish();
        }
    }

    public void init() {
        siteId = getIntent().getExtras().getString("siteId");
        fwoid = getIntent().getExtras().getString("fwoid");
        tran_date = getIntent().getExtras().getString("tran_date");
        dgType = getIntent().getExtras().getString("dgType");
        dgType1 = getIntent().getExtras().getString("dgType1");
        mAppPreferences = new AppPreferences(SaveDFRDetails.this);
        db = new DataBaseHelper(this);
        db.open();
        dfrRight = db.getSubMenuRight("DFR Approval", "Approval");
        moduleUrl = db.getModuleIP("Approval");
        db.close();
        tv_header = (TextView) findViewById(R.id.tv_header);
        Utils.msgText(SaveDFRDetails.this, "412", tv_header);
        et_remarks = (EditText) findViewById(R.id.et_remarks);
        InputFilter rmkfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.toString(source.charAt(i)).equals("`")
                            && !Character.toString(source.charAt(i)).equals("!")
                            && !Character.toString(source.charAt(i)).equals("@")
                            && !Character.toString(source.charAt(i)).equals("$")
                            && !Character.toString(source.charAt(i)).equals("%")
                            && !Character.toString(source.charAt(i)).equals("^")
                            && !Character.toString(source.charAt(i)).equals("&")
                            && !Character.toString(source.charAt(i)).equals("*")
                            && !Character.toString(source.charAt(i)).equals("(")
                            && !Character.toString(source.charAt(i)).equals(")")
                            && !Character.toString(source.charAt(i)).equals("_")
                            && !Character.toString(source.charAt(i)).equals("-")
                            && !Character.toString(source.charAt(i)).equals("+")
                            && !Character.toString(source.charAt(i)).equals("=")
                            && !Character.toString(source.charAt(i)).equals("{")
                            && !Character.toString(source.charAt(i)).equals("}")
                            && !Character.toString(source.charAt(i)).equals("[")
                            && !Character.toString(source.charAt(i)).equals("]")
                            && !Character.toString(source.charAt(i)).equals("|")
                            && !Character.toString(source.charAt(i)).equals("\\")
                            && !Character.toString(source.charAt(i)).equals(":")
                            && !Character.toString(source.charAt(i)).equals("\"")
                            && !Character.toString(source.charAt(i)).equals(";")
                            && !Character.toString(source.charAt(i)).equals("'")
                            && !Character.toString(source.charAt(i)).equals("?")
                            && !Character.toString(source.charAt(i)).equals(".")
                            && !Character.toString(source.charAt(i)).equals(" ")
                            && !Character.toString(source.charAt(i)).equals("/")) {
                        return "";
                    }
                }
                return null;
            }
        };
        et_remarks.setFilters(new InputFilter[]{rmkfilter, new InputFilter.LengthFilter(250)});

        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        Utils.msgText(SaveDFRDetails.this, "430", tv_remarks);

        tv_last_detail = (TextView) findViewById(R.id.tv_last_detail);
        tv_last_detail.setPaintFlags(tv_last_detail.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        Utils.msgText(SaveDFRDetails.this, "429", tv_last_detail);


        tv_current_detail= (TextView) findViewById(R.id.tv_current_detail);
        tv_current_detail.setPaintFlags(tv_current_detail.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        Utils.msgText(SaveDFRDetails.this, "580", tv_current_detail);

        tv_siteId = (TextView) findViewById(R.id.tv_siteId);
        Utils.msgText(SaveDFRDetails.this, "414", tv_siteId);
        tv_date = (TextView) findViewById(R.id.tv_date);
        Utils.msgText(SaveDFRDetails.this, "415", tv_date);
        tv_standard_cph = (TextView) findViewById(R.id.tv_standard_cph);
        Utils.msgText(SaveDFRDetails.this, "416", tv_standard_cph);
        tv_actual_cph = (TextView) findViewById(R.id.tv_actual_cph);
        Utils.msgText(SaveDFRDetails.this, "417", tv_actual_cph);

        tv_actual_cph_dfr = (TextView) findViewById(R.id.tv_actual_cph_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_actual_cph_dfr);

        tv_actual_cph_mobile = (TextView) findViewById(R.id.tv_actual_cph_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_actual_cph_mobile);

        tv_actual_cph_sensor = (TextView) findViewById(R.id.tv_actual_cph_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_actual_cph_sensor);

        tv_transaction_type = (TextView) findViewById(R.id.tv_transaction_type);
        tv_transaction_type.setFocusable(true);
        tv_transaction_type.setFocusableInTouchMode(true);
        Utils.msgText(SaveDFRDetails.this, "422", tv_transaction_type);
        //rest code

        tv_fill_qty = (TextView) findViewById(R.id.tv_fill_qty);
        tv_fill_qty.setFocusable(true);
        tv_fill_qty.setFocusableInTouchMode(true);
        Utils.msgText(SaveDFRDetails.this, "424", tv_fill_qty);

        tv_fill_qty_dfr = (TextView) findViewById(R.id.tv_fill_qty_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_fill_qty_dfr);

        tv_fill_qty_mobile = (TextView) findViewById(R.id.tv_fill_qty_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_fill_qty_mobile);

        tv_fill_qty_sensor = (TextView) findViewById(R.id.tv_fill_qty_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_fill_qty_sensor);

        tv_genset_reading = (TextView) findViewById(R.id.tv_genset_reading);
        tv_fill_qty.setFocusable(true);
        tv_fill_qty.setFocusableInTouchMode(true);
        Utils.msgText(SaveDFRDetails.this, "425", tv_genset_reading);

        tv_genset_reading_dfr = (TextView) findViewById(R.id.tv_genset_reading_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_genset_reading_dfr);

        tv_genset_reading_mobile = (TextView) findViewById(R.id.tv_genset_reading_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_genset_reading_mobile);

        tv_genset_reading_sensor = (TextView) findViewById(R.id.tv_genset_reading_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_genset_reading_sensor);

        tv_opening_stock = (TextView) findViewById(R.id.tv_opening_stock);
        tv_opening_stock.setFocusable(true);
        tv_opening_stock.setFocusableInTouchMode(true);
        Utils.msgText(SaveDFRDetails.this, "427", tv_opening_stock);

        tv_opening_stock_dfr = (TextView) findViewById(R.id.tv_opening_stock_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_opening_stock_dfr);

        tv_opening_stock_mobile = (TextView) findViewById(R.id.tv_opening_stock_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_opening_stock_mobile);

        tv_opening_stock_sensor = (TextView) findViewById(R.id.tv_opening_stock_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_opening_stock_sensor);

        tv_grid_reading = (TextView) findViewById(R.id.tv_grid_reading);
        tv_grid_reading.setFocusable(true);
        tv_grid_reading.setFocusableInTouchMode(true);
        Utils.msgText(SaveDFRDetails.this, "426", tv_grid_reading);

        tv_grid_reading_dfr = (TextView) findViewById(R.id.tv_grid_reading_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_grid_reading_dfr);

        tv_grid_reading_mobile = (TextView) findViewById(R.id.tv_grid_reading_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_grid_reading_mobile);

        tv_grid_reading_sensor = (TextView) findViewById(R.id.tv_grid_reading_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_grid_reading_sensor);

        tv_mortable = (TextView) findViewById(R.id.tv_mortable);
        Utils.msgText(SaveDFRDetails.this, "428", tv_mortable);

        tv_mortable_dfr = (TextView) findViewById(R.id.tv_mortable_dfr);
        Utils.msgText(SaveDFRDetails.this, "419", tv_mortable_dfr);

        tv_mortable_mobile = (TextView) findViewById(R.id.tv_mortable_mobile);
        Utils.msgText(SaveDFRDetails.this, "418", tv_mortable_mobile);

        tv_mortable_sensor = (TextView) findViewById(R.id.tv_mortable_sensor);
        Utils.msgText(SaveDFRDetails.this, "420", tv_mortable_sensor);

        et_site_id = (EditText) findViewById(R.id.et_site_id);
        et_site_id.setText(siteId);

        et_fwoid= (EditText) findViewById(R.id.et_fwoid);
        et_fwoid.setText(fwoid);

        et_date = (EditText) findViewById(R.id.et_date);
        et_date.setText(tran_date);

        et_standard_cph = (EditText) findViewById(R.id.et_standard_cph);
        et_actual_cph_dfr = (EditText) findViewById(R.id.et_actual_cph_dfr);
        et_actual_cph_mobile = (EditText) findViewById(R.id.et_actual_cph_mobile);
        et_actual_cph_sensor = (EditText) findViewById(R.id.et_actual_cph_sensor);


        InputFilter filter = new DigitsAfterDecimal(2);
        et_fill_qty_dfr = (EditText) findViewById(R.id.et_fill_qty_dfr);
        et_fill_qty_dfr.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});
        et_fill_qty_mobile = (EditText) findViewById(R.id.et_fill_qty_mobile);
        et_fill_qty_mobile.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});
        et_fill_qty_sensor = (EditText) findViewById(R.id.et_fill_qty_sensor);
        et_fill_qty_sensor.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});

        et_genset_reading_dfr = (EditText) findViewById(R.id.et_genset_reading_dfr);
        et_genset_reading_mobile = (EditText) findViewById(R.id.et_genset_reading_mobile);
        et_genset_reading_sensor = (EditText) findViewById(R.id.et_genset_reading_sensor);

        et_opening_stock_dfr = (EditText) findViewById(R.id.et_opening_stock_dfr);
        et_opening_stock_dfr.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});
        et_opening_stock_mobile = (EditText) findViewById(R.id.et_opening_stock_mobile);
        et_opening_stock_mobile.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});
        et_opening_stock_sensor = (EditText) findViewById(R.id.et_opening_stock_sensor);
        et_opening_stock_sensor.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("9"))});

        et_grid_reading_dfr = (EditText) findViewById(R.id.et_grid_reading_dfr);
        et_grid_reading_mobile = (EditText) findViewById(R.id.et_grid_reading_mobile);
        et_grid_reading_sensor = (EditText) findViewById(R.id.et_grid_reading_sensor);

        rg_tran_type = (LinearLayout) findViewById(R.id.rg_tran_type);
        //rg_tran_type.clearCheck();

        rb_dfr = (RadioButton) findViewById(R.id.rb_dfr);
        rb_dfr.setText(Utils.msg(SaveDFRDetails.this, "419"));

        rb_mobile = (RadioButton) findViewById(R.id.rb_mobile);
        rb_mobile.setText(Utils.msg(SaveDFRDetails.this, "418"));

        rb_sensor = (RadioButton) findViewById(R.id.rb_sensor);
        rb_sensor.setText(Utils.msg(SaveDFRDetails.this, "420"));

        rb_reject = (RadioButton) findViewById(R.id.rb_reject);
        rb_reject.setText(Utils.msg(SaveDFRDetails.this, "421"));

        cb_dfr = (CheckBox) findViewById(R.id.cb_dfr);
        cb_mobile = (CheckBox) findViewById(R.id.cb_mobile);
        cb_sensor = (CheckBox) findViewById(R.id.cb_sensor);
        if (mAppPreferences.getSiteMotorable() == 0) {
            cb_dfr.setVisibility(View.GONE);
            cb_mobile.setVisibility(View.GONE);
            cb_sensor.setVisibility(View.GONE);
            tv_mortable.setVisibility(View.GONE);
            tv_mortable_dfr.setVisibility(View.GONE);
            tv_mortable_mobile.setVisibility(View.GONE);
            tv_mortable_sensor.setVisibility(View.GONE);
        }


    }

    public void selectDFR() {
        setValue();
        InputFilter filter = new InputMix(2);

        cb_dfr.setEnabled(true);
        cb_mobile.setEnabled(false);
        cb_sensor.setEnabled(false);
        cb_dfr.setAlpha(1.0f);
        cb_mobile.setAlpha(.4f);
        cb_sensor.setAlpha(.4f);

        et_fill_qty_dfr.setEnabled(true);
        et_fill_qty_dfr.setBackgroundResource(R.drawable.input_box );

        et_fill_qty_mobile.setEnabled(false);
        et_fill_qty_mobile.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_sensor.setEnabled(false);
        et_fill_qty_sensor.setBackgroundResource(R.drawable.et_data);

        et_genset_reading_dfr.setEnabled(true);
        et_genset_reading_dfr.setBackgroundResource(R.drawable.input_box );
        et_genset_reading_dfr.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});


        et_genset_reading_mobile.setEnabled(false);
        et_genset_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_sensor.setEnabled(false);
        et_genset_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_opening_stock_dfr.setEnabled(true);
        et_opening_stock_dfr.setBackgroundResource(R.drawable.input_box );

        et_opening_stock_mobile.setEnabled(false);
        et_opening_stock_mobile.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_sensor.setEnabled(false);
        et_opening_stock_sensor.setBackgroundResource(R.drawable.et_data);

        et_grid_reading_dfr.setEnabled(true);
        et_grid_reading_dfr.setBackgroundResource(R.drawable.input_box );
        et_grid_reading_dfr.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});

        et_grid_reading_mobile.setEnabled(false);
        et_grid_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("10"))});


        et_grid_reading_sensor.setEnabled(false);
        et_grid_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("10"))});

    }

    public void selectMobile() {
        setValue();
        InputFilter filter = new InputMix(2);
        cb_dfr.setEnabled(false);
        cb_mobile.setEnabled(true);
        cb_sensor.setEnabled(false);
        cb_dfr.setAlpha(.4f);
        cb_mobile.setAlpha(1.0f);
        cb_sensor.setAlpha(.4f);

        et_fill_qty_dfr.setEnabled(false);
        et_fill_qty_dfr.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_mobile.setEnabled(true);
        et_fill_qty_mobile.setBackgroundResource(R.drawable.input_box );

        et_fill_qty_sensor.setEnabled(false);
        et_fill_qty_sensor.setBackgroundResource(R.drawable.et_data);

        et_genset_reading_dfr.setEnabled(false);
        et_genset_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_mobile.setEnabled(true);
        et_genset_reading_mobile.setBackgroundResource(R.drawable.input_box );
        et_genset_reading_mobile.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});


        et_genset_reading_sensor.setEnabled(false);
        et_genset_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_opening_stock_dfr.setEnabled(false);
        et_opening_stock_dfr.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_mobile.setEnabled(true);
        et_opening_stock_mobile.setBackgroundResource(R.drawable.input_box );

        et_opening_stock_sensor.setEnabled(false);
        et_opening_stock_sensor.setBackgroundResource(R.drawable.et_data);

        et_grid_reading_dfr.setEnabled(false);
        et_grid_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_grid_reading_mobile.setEnabled(true);
        et_grid_reading_mobile.setBackgroundResource(R.drawable.input_box );
        et_grid_reading_mobile.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});


        et_grid_reading_sensor.setEnabled(false);
        et_grid_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


    }

    public void selectSensor() {
        setValue();
        InputFilter filter = new InputMix(2);
        cb_dfr.setEnabled(false);
        cb_mobile.setEnabled(false);
        cb_sensor.setEnabled(true);

        cb_dfr.setAlpha(.4f);
        cb_mobile.setAlpha(.4f);
        cb_sensor.setAlpha(1.0f);

        et_fill_qty_dfr.setEnabled(false);
        et_fill_qty_dfr.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_mobile.setEnabled(false);
        et_fill_qty_mobile.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_sensor.setEnabled(true);
        et_fill_qty_sensor.setBackgroundResource(R.drawable.input_box );


        et_genset_reading_dfr.setEnabled(false);
        et_genset_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_mobile.setEnabled(false);
        et_genset_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_sensor.setEnabled(true);
        et_genset_reading_sensor.setBackgroundResource(R.drawable.input_box );
        et_genset_reading_sensor.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});


        et_opening_stock_dfr.setEnabled(false);
        et_opening_stock_dfr.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_mobile.setEnabled(false);
        et_opening_stock_mobile.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_sensor.setEnabled(true);
        et_opening_stock_sensor.setBackgroundResource(R.drawable.input_box );


        et_grid_reading_dfr.setEnabled(false);
        et_grid_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_grid_reading_mobile.setEnabled(false);
        et_grid_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_grid_reading_sensor.setEnabled(true);
        et_grid_reading_sensor.setBackgroundResource(R.drawable.input_box );
        et_grid_reading_sensor.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt("10"))});

    }

    public void selectReject() {
        cb_dfr.setEnabled(false);
        cb_mobile.setEnabled(false);
        cb_sensor.setEnabled(false);
        cb_dfr.setAlpha(.4f);
        cb_mobile.setAlpha(.4f);
        cb_sensor.setAlpha(.4f);

        et_fill_qty_dfr.setEnabled(false);
        et_fill_qty_dfr.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_mobile.setEnabled(false);
        et_fill_qty_mobile.setBackgroundResource(R.drawable.et_data);

        et_fill_qty_sensor.setEnabled(false);
        et_fill_qty_sensor.setBackgroundResource(R.drawable.et_data);

        et_genset_reading_dfr.setEnabled(false);
        et_genset_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_mobile.setEnabled(false);
        et_genset_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_genset_reading_sensor.setEnabled(false);
        et_genset_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_genset_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_opening_stock_dfr.setEnabled(false);
        et_opening_stock_dfr.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_mobile.setEnabled(false);
        et_opening_stock_mobile.setBackgroundResource(R.drawable.et_data);

        et_opening_stock_sensor.setEnabled(false);
        et_opening_stock_sensor.setBackgroundResource(R.drawable.et_data);

        et_grid_reading_dfr.setEnabled(false);
        et_grid_reading_dfr.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_grid_reading_mobile.setEnabled(false);
        et_grid_reading_mobile.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});


        et_grid_reading_sensor.setEnabled(false);
        et_grid_reading_sensor.setBackgroundResource(R.drawable.et_data);
        et_grid_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});

    }

    public boolean validate() {
        boolean status = true;
        int a = 0;
        if (rb_dfr.isChecked()) {
            a = 1;
        } else if (rb_mobile.isChecked()) {
            a = 1;
        } else if (rb_sensor.isChecked()) {
            a = 1;
        } else if (rb_reject.isChecked()) {
            a = 1;
        }

        if (a == 0) {
            tv_transaction_type.clearFocus();
            tv_transaction_type.requestFocus();
            status = false;
            Toast.makeText(SaveDFRDetails.this, "Select " + tv_transaction_type.getText().toString(), Toast.LENGTH_LONG).show();
            return status;
        }

        if (rb_dfr.isChecked()) {
            if (et_fill_qty_dfr.getText().toString().length() == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                Utils.toast(SaveDFRDetails.this, "159");
                return status;
            } else if (et_genset_reading_dfr.getText().toString().length() == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                //message eg. Genset Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "161");
                return status;
            } else if (et_opening_stock_dfr.getText().toString().length() == 0) {
                status = false;
                tv_opening_stock.clearFocus();
                tv_opening_stock.requestFocus();
                //message eg. Opening Stock (Ltrs.) cannot be blank.
                Utils.toast(SaveDFRDetails.this, "160");
                return status;
            } else if (et_grid_reading_dfr.getText().toString().length() == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                //message eg. Grid Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "162");
                return status;
            } else if (et_fill_qty_dfr.getText().toString().length() > 0 && etSum(et_fill_qty_dfr) == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Filled Qty.(Ltrs.) cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_genset_reading_dfr.getText().toString().length() > 0 && etSum(et_genset_reading_dfr) == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Genset Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_grid_reading_dfr.getText().toString().length() > 0 && etSum(et_grid_reading_dfr) == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Grid Meter Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            }


        }


        if (rb_mobile.isChecked()) {
            if (et_fill_qty_mobile.getText().toString().length() == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                //message eg. Filled Qty. (Ltrs.) cannot be blank.
                Utils.toast(SaveDFRDetails.this, "159");
                return status;
            } else if (et_genset_reading_mobile.getText().toString().length() == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                //message eg. Genset Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "161");
                return status;
            } else if (et_opening_stock_mobile.getText().toString().length() == 0) {
                status = false;
                tv_opening_stock.clearFocus();
                tv_opening_stock.requestFocus();
                //message eg. Opening Stock (Ltrs.) cannot be blank.
                Utils.toast(SaveDFRDetails.this, "160");
                return status;
            } else if (et_grid_reading_mobile.getText().toString().length() == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                //message eg. Grid Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "162");
                return status;
            } else if (et_fill_qty_mobile.getText().toString().length() > 0 && etSum(et_fill_qty_mobile) == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Filled Qty.(Ltrs.) cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_genset_reading_mobile.getText().toString().length() > 0 && etSum(et_genset_reading_mobile) == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Genset Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_grid_reading_mobile.getText().toString().length() > 0 && etSum(et_grid_reading_mobile) == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Grid Meter Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            }
        }

        if (rb_sensor.isChecked()) {
            if (et_fill_qty_sensor.getText().toString().length() == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                //message eg. Filled Qty. (Ltrs.) cannot be blank.
                Utils.toast(SaveDFRDetails.this, "159");
                return status;
            } else if (et_genset_reading_sensor.getText().toString().length() == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                //message eg. Genset Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "161");
                return status;
            } else if (et_opening_stock_sensor.getText().toString().length() == 0) {
                status = false;
                tv_opening_stock.clearFocus();
                tv_opening_stock.requestFocus();
                //message eg. Opening Stock (Ltrs.) cannot be blank.
                Utils.toast(SaveDFRDetails.this, "160");
                return status;
            } else if (et_grid_reading_sensor.getText().toString().length() == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                //message eg. Grid Meter Reading cannot be blank.
                Utils.toast(SaveDFRDetails.this, "162");
                return status;
            } else if (et_fill_qty_sensor.getText().toString().length() > 0 && etSum(et_fill_qty_sensor) == 0) {
                status = false;
                tv_fill_qty.clearFocus();
                tv_fill_qty.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Filled Qty.(Ltrs.) cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_genset_reading_sensor.getText().toString().length() > 0 && etSum(et_genset_reading_sensor) == 0) {
                status = false;
                tv_genset_reading.clearFocus();
                tv_genset_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Genset Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            } else if (et_grid_reading_sensor.getText().toString().length() > 0 && etSum(et_grid_reading_sensor) == 0) {
                status = false;
                tv_grid_reading.clearFocus();
                tv_grid_reading.requestFocus();
                Toast.makeText(SaveDFRDetails.this, "Grid Meter Reading cannot be 0.",
                        Toast.LENGTH_LONG).show();
                return status;
            }

        }

        if (rb_reject.isChecked()) {
            if (et_remarks.getText().toString().length() == 0) {
                status = false;
                Toast.makeText(SaveDFRDetails.this, "Approval remarks cannot left blank if approval type is Reject.",
                        Toast.LENGTH_LONG).show();
                return status;
            }
        }
        return status;

    }

    public class DFRApprovalGridTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String response;

        public DFRApprovalGridTask(Context con) {
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
                String url;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
                nameValuePairs.add(new BasicNameValuePair("countryID", mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("hubID", mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("regionID", mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("circleID", mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID", mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID", mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("omeID", mAppPreferences.getPIOMEID()));
                nameValuePairs.add(new BasicNameValuePair("source", "A"));
                nameValuePairs.add(new BasicNameValuePair("fromDate", tran_date));
                nameValuePairs.add(new BasicNameValuePair("toDate", tran_date));
                nameValuePairs.add(new BasicNameValuePair("sID", siteId));
                nameValuePairs.add(new BasicNameValuePair("dgType",dgType));
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_dfr_approval;
                } else {
                    url = moduleUrl + WebMethods.url_dfr_approval;
                }
                response = Utils.httpPostRequest(con, url, nameValuePairs);
                Gson gson = new Gson();
                response_list = gson.fromJson(response, DFRMoreDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
                // response_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response_list.getGetDFRApproval() != null && response_list.getGetDFRApproval().size() > 0) {
                setValue();
            }
            super.onPostExecute(result);
        }
    }

    public void setValue() {
        et_genset_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});
        et_genset_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});
        et_genset_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});
        et_grid_reading_dfr.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});
        et_grid_reading_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});
        et_grid_reading_sensor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("30"))});

        //1<--DFR, 2<-- Mobile, 9<--Sensor
        for (int i = 0; i < response_list.getGetDFRApproval().size(); i++) {
            if (response_list.getGetDFRApproval().get(i).getTRAN_TYPE_INT().equalsIgnoreCase("1")) {
                if (response_list.getGetDFRApproval().get(i).getDG_READING() != null) {
                    et_genset_reading_dfr.setText("" + response_list.getGetDFRApproval().get(i).getDG_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getEB_READING() != null) {

                    et_grid_reading_dfr.setText("" + response_list.getGetDFRApproval().get(i).getEB_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getFILLED_QTY() != null) {
                    et_fill_qty_dfr.setText("" + response_list.getGetDFRApproval().get(i).getFILLED_QTY());
                }
                if (response_list.getGetDFRApproval().get(i).getOPENING_STK() != null) {
                    et_opening_stock_dfr.setText("" + response_list.getGetDFRApproval().get(i).getOPENING_STK());
                }
                if (response_list.getGetDFRApproval().get(i).getACTUAL_CPH() != null) {
                    et_actual_cph_dfr.setText("" + response_list.getGetDFRApproval().get(i).getACTUAL_CPH());
                }
                if (response_list.getGetDFRApproval().get(i).getSTANDARD_CPH() != null) {
                    et_standard_cph.setText("" + response_list.getGetDFRApproval().get(i).getSTANDARD_CPH());

                }
                if (response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE() != null &&
                        response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE().equalsIgnoreCase("1")) {
                    cb_dfr.setChecked(true);
                }

            } else if (response_list.getGetDFRApproval().get(i).getTRAN_TYPE_INT().equalsIgnoreCase("2")) {
                if (response_list.getGetDFRApproval().get(i).getDG_READING() != null) {
                    et_genset_reading_mobile.setText("" + response_list.getGetDFRApproval().get(i).getDG_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getEB_READING() != null) {
                    et_grid_reading_mobile.setText("" + response_list.getGetDFRApproval().get(i).getEB_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getFILLED_QTY() != null) {
                    et_fill_qty_mobile.setText("" + response_list.getGetDFRApproval().get(i).getFILLED_QTY());
                }
                if (response_list.getGetDFRApproval().get(i).getOPENING_STK() != null) {
                    et_opening_stock_mobile.setText("" + response_list.getGetDFRApproval().get(i).getOPENING_STK());
                }
                if (response_list.getGetDFRApproval().get(i).getACTUAL_CPH() != null) {
                    et_actual_cph_mobile.setText("" + response_list.getGetDFRApproval().get(i).getACTUAL_CPH());
                }
                if (response_list.getGetDFRApproval().get(i).getSTANDARD_CPH() != null) {
                    et_standard_cph.setText("" + response_list.getGetDFRApproval().get(i).getSTANDARD_CPH());
                }
                if (response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE() != null &&
                        response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE().equalsIgnoreCase("1")) {
                    cb_mobile.setChecked(true);
                }


            } else if (response_list.getGetDFRApproval().get(i).getTRAN_TYPE_INT().equalsIgnoreCase("9")) {
                if (response_list.getGetDFRApproval().get(i).getDG_READING() != null) {
                    et_genset_reading_sensor.setText("" + response_list.getGetDFRApproval().get(i).getDG_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getEB_READING() != null) {
                    et_grid_reading_sensor.setText("" + response_list.getGetDFRApproval().get(i).getEB_READING());
                }
                if (response_list.getGetDFRApproval().get(i).getFILLED_QTY() != null) {
                    et_fill_qty_sensor.setText("" + response_list.getGetDFRApproval().get(i).getFILLED_QTY());
                }
                if (response_list.getGetDFRApproval().get(i).getOPENING_STK() != null) {
                    et_opening_stock_sensor.setText("" + response_list.getGetDFRApproval().get(i).getOPENING_STK());
                }
                if (response_list.getGetDFRApproval().get(i).getACTUAL_CPH() != null) {
                    et_actual_cph_sensor.setText("" + response_list.getGetDFRApproval().get(i).getACTUAL_CPH());
                }
                if (response_list.getGetDFRApproval().get(i).getSTANDARD_CPH() != null) {
                    et_standard_cph.setText("" + response_list.getGetDFRApproval().get(i).getSTANDARD_CPH());
                }
                if (response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE() != null &&
                        response_list.getGetDFRApproval().get(i).getSITE_MOTORABLE().equalsIgnoreCase("1")) {
                    cb_sensor.setChecked(true);
                }
            }
        }
    }

    public void Dialog() {
        final Dialog LastTxnDialog = new Dialog(SaveDFRDetails.this);
        LastTxnDialog.setCanceledOnTouchOutside(false);
        LastTxnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LastTxnDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LastTxnDialog.setContentView(R.layout.dfr_last_details);
        final Window window_SignIn = LastTxnDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity(Gravity.CENTER);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Drawable d = new ColorDrawable(Color.parseColor("#CC000000"));
        LastTxnDialog.getWindow().setBackgroundDrawable(d);
        LastTxnDialog.show();

        //LastTxnDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //LastTxnDialog.show();
        ImageView iv_cancel = (ImageView) LastTxnDialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                LastTxnDialog.cancel();
            }
        });

        TextView tv_last_date = (TextView) LastTxnDialog.findViewById(R.id.tv_last_date);
        Utils.msgText(SaveDFRDetails.this, "432", tv_last_date);

        TextView tv_last_qty = (TextView) LastTxnDialog.findViewById(R.id.tv_last_qty);
        Utils.msgText(SaveDFRDetails.this, "433", tv_last_qty);

        TextView tv_last_genset = (TextView) LastTxnDialog.findViewById(R.id.tv_last_genset);
        Utils.msgText(SaveDFRDetails.this, "434", tv_last_genset);

        TextView tv_last_opening = (TextView) LastTxnDialog.findViewById(R.id.tv_last_opening);
        Utils.msgText(SaveDFRDetails.this, "435", tv_last_opening);

        TextView tv_last_grid = (TextView) LastTxnDialog.findViewById(R.id.tv_last_grid);
        Utils.msgText(SaveDFRDetails.this, "436", tv_last_grid);

        if (response_list.getGetDFRApproval().get(0).getLAST_FILLING() != null) {
            tv_last_date.setText(Utils.msg(SaveDFRDetails.this, "432")
                    + response_list.getGetDFRApproval().get(0).getLAST_FILLING());
        }

        if (response_list.getGetDFRApproval().get(0).getLAST_DIESEL_QTY() != null) {
            tv_last_qty.setText(Utils.msg(SaveDFRDetails.this, "433")
                    + response_list.getGetDFRApproval().get(0).getLAST_DIESEL_QTY());
        }

        if (response_list.getGetDFRApproval().get(0).getLAST_DG_READING() != null) {
            tv_last_genset.setText(Utils.msg(SaveDFRDetails.this, "434")
                    + response_list.getGetDFRApproval().get(0).getLAST_DG_READING());
        }

        if (response_list.getGetDFRApproval().get(0).getLAST_DG_STOCK() != null) {
            tv_last_opening.setText(Utils.msg(SaveDFRDetails.this, "435")
                    + response_list.getGetDFRApproval().get(0).getLAST_DG_STOCK());
        }

        if (response_list.getGetDFRApproval().get(0).getLAST_EB_READING() != null) {
            tv_last_grid.setText(Utils.msg(SaveDFRDetails.this, "436")
                    + response_list.getGetDFRApproval().get(0).getLAST_EB_READING());
        }
    }

    public String submitData(int mode, String app, String rmks, String aos, String afill, String adgr, String aebr,
                             String ahilst) {
        String key = "<ROWSET>\n" +
                "  <ROW>\n" +
                "    <sid>Site Id</sid>\n" +
                "    <sname>Site Name</sname>\n" +
                "    <dg>Genset Type</dg>\n" +
                "    <zname>Sub Region</zname>\n" +
                "    <fno>Filler Mobile No.</fno>\n" +
                "    <tdt>Filling Date</tdt>\n" +
                "    <time>Filling Time</time>\n" +
                "    <rate>Fuel Rate</rate>\n" +
                "    <vno>Voucher No.</vno>\n" +
                "    <smc>SMC Engineer Name</smc>\n" +
                "    <scph>Standard CPH</scph>\n" +
                "    <mos>Opening Stock Mobile</mos>\n" +
                "    <mfill>Filled Qty. (Ltr.) Mobile</mfill>\n" +
                "    <mdgr>Genset Reading Mobile</mdgr>\n" +
                "    <mebr>Grid Reading Mobile</mebr>\n" +
                "    <mdrdt>Genset Meter Replacement Date Mobile</mdrdt>\n" +
                "    <mhilst>Site Motorable Mobile</mhilst>\n" +
                "    <mcph>Actual CPH Mobile</mcph>\n" +
                "    <dos>Opening Stock DFR</dos>\n" +
                "    <dfill>Filled Qty. (Ltr.) DFR</dfill>\n" +
                "    <ddgr>Genset Reading DFR</ddgr>\n" +
                "    <debr>Grid Reading DFR</debr>\n" +
                "    <ddrdt>Genset Meter Replacement Date DFR</ddrdt>\n" +
                "    <dhilst>Site Motorable DFR</dhilst>\n" +
                "    <dcph>Actual CPH DFR</dcph>\n" +
                "    <ros>Opening Stock RMS</ros>\n" +
                "    <rfill>Filled Qty. (Ltr.) RMS</rfill>\n" +
                "    <rdgr>Genset Reading RMS</rdgr>\n" +
                "    <rebr>Grid Reading RMS</rebr>\n" +
                "    <rdrdt>Genset Meter Replacement Date RMS</rdrdt>\n" +
                "    <rcph>Actual CPH RMS</rcph>\n" +
                "    <aos>Approved Opening Stock</aos>\n" +
                "    <afill>Approved Filled Qty. (Ltr.)</afill>\n" +
                "    <adgr>Approved Genset Reading</adgr>\n" +
                "    <aebr>Approved Grid Reading</aebr>\n" +
                "    <ahilst>Approved Site Motorable</ahilst>\n" +
                "    <app>Approval Type (DFR/MOBILE/RMS/REJECT)</app>\n" +
                "    <appr>Approved Reason</appr>\n" +
                "    <lt>Last Filling Date</lt>\n" +
                "    <ldur>Duration Since Last Filling</ldur>\n" +
                "    <lmode>Last Filling Reporting Mode</lmode>\n" +
                "    <los>Last Fill Opening Stock</los>\n" +
                "    <lfill>Last Fill Qty. (Ltr.)</lfill>\n" +
                "    <ldgr>Last Fill Genset Reading</ldgr>\n" +
                "    <lebr>Last Fill Grid Reading</lebr>\n" +
                "    <ldrdt>Last Genset Meter Replacement Date</ldrdt>\n" +
                "  </ROW>\n" +
                "  <ROW>\n" +
                "    <sid>" + response_list.getGetDFRApproval().get(0).getSITE_ID() + "</sid>\n" +
                "    <sname>" + response_list.getGetDFRApproval().get(0).getSITE_NAME() + "</sname>\n" +
                "    <dg>" + response_list.getGetDFRApproval().get(0).getDG_TYPE() + "</dg>\n" +
                "    <zname>" + response_list.getGetDFRApproval().get(0).getZONE_NAME() + "</zname>\n" +
                "    <fno>" + response_list.getGetDFRApproval().get(0).getFILLER_NO() + "</fno>\n" +
                "    <tdt>" + response_list.getGetDFRApproval().get(0).getTRAN_DATE() + "</tdt>\n" +
                "    <time>" + response_list.getGetDFRApproval().get(mode).getFILLING_TIME() + "</time>\n" +
                "    <rate>" + response_list.getGetDFRApproval().get(0).getFUEL_RATE() + "</rate>\n" +
                "    <vno>" + response_list.getGetDFRApproval().get(0).getVEHICLE_NO() + "</vno>\n" +
                "    <smc>" + response_list.getGetDFRApproval().get(0).getSMC_ENGG() + "</smc>\n" +
                "    <scph>" + response_list.getGetDFRApproval().get(0).getSTANDARD_CPH() + "</scph>\n" +
                "    <mos>" + response_list.getGetDFRApproval().get(1).getOPENING_STK() + "</mos>\n" +
                "    <mfill>" + response_list.getGetDFRApproval().get(1).getFILLED_QTY() + "</mfill>\n" +
                "    <mdgr>" + response_list.getGetDFRApproval().get(1).getDG_READING() + "</mdgr>\n" +
                "    <mebr>" + response_list.getGetDFRApproval().get(1).getEB_READING() + "</mebr>\n" +
                "    <mdrdt></mdrdt>\n" +
                "    <mhilst>" + response_list.getGetDFRApproval().get(1).getSITE_MOTORABLE() + "</mhilst>\n" +
                "    <mcph>" + response_list.getGetDFRApproval().get(1).getACTUAL_CPH() + "</mcph>\n" +
                "    <dos>" + response_list.getGetDFRApproval().get(0).getOPENING_STK() + "</dos>\n" +
                "    <dfill>" + response_list.getGetDFRApproval().get(0).getFILLED_QTY() + "</dfill>\n" +
                "    <ddgr>" + response_list.getGetDFRApproval().get(0).getDG_READING() + "</ddgr>\n" +
                "    <debr>" + response_list.getGetDFRApproval().get(0).getEB_READING() + "</debr>\n" +
                "    <ddrdt></ddrdt>\n" +
                "    <dhilst>" + response_list.getGetDFRApproval().get(0).getSITE_MOTORABLE() + "</dhilst>\n" +
                "    <dcph>" + response_list.getGetDFRApproval().get(0).getACTUAL_CPH() + "</dcph>\n" +
                "    <ros>" + response_list.getGetDFRApproval().get(2).getOPENING_STK() + "</ros>\n" +
                "    <rfill>" + response_list.getGetDFRApproval().get(2).getFILLED_QTY() + "</rfill>\n" +
                "    <rdgr>" + response_list.getGetDFRApproval().get(2).getDG_READING() + "</rdgr>\n" +
                "    <rebr>" + response_list.getGetDFRApproval().get(2).getEB_READING() + "</rebr>\n" +
                "    <rdrdt></rdrdt>\n" +
                "    <rcph>" + response_list.getGetDFRApproval().get(2).getACTUAL_CPH() + "</rcph>\n" +
                "    <aos>" + aos + "</aos>\n" +
                "    <afill>" + afill + "</afill>\n" +
                "    <adgr>" + adgr + "</adgr>\n" +
                "    <aebr>" + aebr + "</aebr>\n" +
                "    <ahilst>" + ahilst + "</ahilst>\n" +
                "    <app>" + app + "</app>\n" +
                "    <appr>" + rmks + "</appr>\n" +
                "    <lt>" + response_list.getGetDFRApproval().get(0).getLAST_FILLING() + "</lt>\n" +
                "    <ldur></ldur>\n" +
                "    <lmode>" + "Mobile" + "</lmode>\n" +
                "    <los>" + response_list.getGetDFRApproval().get(0).getLAST_DG_STOCK() + "</los>\n" +
                "    <lfill>" + response_list.getGetDFRApproval().get(0).getLAST_DIESEL_QTY() + "</lfill>\n" +
                "    <ldgr>" + response_list.getGetDFRApproval().get(0).getLAST_DG_READING() + "</ldgr>\n" +
                "    <lebr>" + response_list.getGetDFRApproval().get(0).getLAST_EB_READING() + "</lebr>\n" +
                "    <ldrdt></ldrdt>\n" +
                "    </ROW>\n" +
                "    </ROWSET>\n";
        return key;
    }

    public class SaveDFRApproval extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String response;
        String xml;
        ResponeDfrSave saveResponse;

        public SaveDFRApproval(Context con, String xml) {
            this.con = con;
            this.xml = xml;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
                nameValuePairs.add(new BasicNameValuePair("countryID", mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("hubID", mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("regionID", mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("circleID", mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID", mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID", mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("omeID", mAppPreferences.getPIOMEID()));
                nameValuePairs.add(new BasicNameValuePair("flag", "2"));
                nameValuePairs.add(new BasicNameValuePair("userName", mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("xmlData", xml));
                if (moduleUrl.equalsIgnoreCase("0")) {
                    url = mAppPreferences.getConfigIP() + WebMethods.url_dfr_SaveDFRApproval;
                } else {
                    url = moduleUrl + WebMethods.url_dfr_SaveDFRApproval;
                }
                response = Utils.httpPostRequest(con, url, nameValuePairs);
                Gson gson = new Gson();
                saveResponse = gson.fromJson(response, ResponeDfrSave.class);
            } catch (Exception e) {
                e.printStackTrace();
                saveResponse = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (saveResponse == null) {
                Utils.toast(SaveDFRDetails.this, "13");
            } else if (saveResponse.getSaveDFRApproval() != null && saveResponse.getSaveDFRApproval().size() > 0) {
                if (saveResponse.getSaveDFRApproval().get(0).getResponse() != null) {
                    Toast.makeText(SaveDFRDetails.this, "" + saveResponse.getSaveDFRApproval().get(0).getResponse(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SaveDFRDetails.this, "Transaction " + msgLbl + " Successfully.",
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SaveDFRDetails.this, DFRApprovalDetails.class);
                    i.putExtra("tran_date", tran_date);
                    startActivity(i);
                    finish();
                }
            } else {
                Utils.toast(SaveDFRDetails.this, "13");
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public int etSum(EditText et) {
        int sum1 = 0, sum2 = 0, sum = 0;
        String regexStr = "^[0-9\\.]*$";

        if (et.getText().toString().trim().matches(regexStr)) {
            if (et.getText().toString().trim().contains(".")) {
                String input = "0"+et.getText().toString().trim()+"0";
                //String[] data = et.getText().toString().trim().split("\\.");
                String[] data = input.split("\\.");
                String before = data[0];
                int digits1 = Integer.parseInt(before);
                String after = data[1];
                int digits2 = Integer.parseInt(after);
                while (digits1 != 0) {
                    int lastdigit = digits1 % 10;
                    sum1 += lastdigit;
                    digits1 /= 10;
                }

                while (digits2 != 0) {
                    int lastdigit = digits2 % 10;
                    sum2 += lastdigit;
                    digits2 /= 10;
                }
                sum = sum1 + sum2;
            } else {
                int digits = Integer.parseInt(et.getText().toString().trim());
                while (digits != 0) {
                    int lastdigit = digits % 10;
                    sum += lastdigit;
                    digits /= 10;
                }
            }
        } else {
            sum = 1;
        }
        return sum;
    }

    public void alert(final int confirmation, String msgId) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(SaveDFRDetails.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( SaveDFRDetails.this ) );
        positive.setTypeface( Utils.typeFace( SaveDFRDetails.this ) );
        negative.setTypeface( Utils.typeFace( SaveDFRDetails.this ) );
        title.setTypeface( Utils.typeFace( SaveDFRDetails.this ) );
        title.setText(Utils.msg(SaveDFRDetails.this, msgId));
        positive.setText(Utils.msg(SaveDFRDetails.this, "63"));
        negative.setText(Utils.msg(SaveDFRDetails.this, "64"));
        positive.setText(Utils.msg(SaveDFRDetails.this, "63"));
        negative.setText(Utils.msg(SaveDFRDetails.this, "64"));

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                DataBaseHelper db = new DataBaseHelper(SaveDFRDetails.this);
                db.open();
                actvity_dialog.cancel();
                if (confirmation == 1) {
                    finish();
                }

                if (confirmation == 2) {
                    actvity_dialog.cancel();
                    if (alertCount == 0) {
                        alert(2, "501");
                        alertCount = 1;
                        return;

                    } else {
                        msgLbl = "Rejected";
                        alertCount=0;
                        ActionOnDFR("No", 0, "REJECT","","","","");

                    }
                }
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                alertCount = 0;
            }
        });
    }

    public class FillingReport extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        String res;
        Context con;
        String site_id;
        String dg;
        String age;
        String fDate;
        String tDate;
        String flag = "A";
        BeanLastFillingTransList fillingReportList;
        public FillingReport(Context con, String site_id, String dgType,
                             String age, String fDate, String tDate) {
            this.con = con;
            this.site_id = site_id;
            this.dg = dgType;
            this.age = age;
            this.fDate = fDate;
            this.tDate = tDate;

        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
                nameValuePairs.add(new BasicNameValuePair("userID",	mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("siteID", site_id));
                nameValuePairs.add(new BasicNameValuePair("dgType", dg));
                //nameValuePairs.add(new BasicNameValuePair("age", age));
                nameValuePairs.add(new BasicNameValuePair("age", "TRANSACTION"));
                nameValuePairs.add(new BasicNameValuePair("fromDate", fDate));
                nameValuePairs.add(new BasicNameValuePair("toDate", tDate));
                nameValuePairs.add(new BasicNameValuePair("addParam", ""));
                nameValuePairs.add(new BasicNameValuePair("flag", "D")); //S for location user site && A for filler association site
                nameValuePairs.add(new BasicNameValuePair("languageCode", "" + mAppPreferences.getLanCode()));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_GetFillingReport;
                }else{
                    url=moduleUrl+ WebMethods.url_GetFillingReport;
                }
                res = Utils.httpPostRequest(con,url, nameValuePairs);
                Gson gson = new Gson();
                fillingReportList = gson.fromJson(res,BeanLastFillingTransList.class);
            } catch (Exception e) {
                e.printStackTrace();
                fillingReportList = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }

            if ((fillingReportList == null)) {
                Utils.toast( SaveDFRDetails.this, "13");
            } else if (fillingReportList.getFillingReportList()!=null && fillingReportList.getFillingReportList().size() > 0
                    && fillingReportList.getFillingReportList().get(0).getFLAG().equalsIgnoreCase("S")) {
                if (flag.equalsIgnoreCase("A")) {
                    Intent i = new Intent( SaveDFRDetails.this,FillingTransactionDetails.class);
                    i.putExtra("res", res);
                    i.putExtra("flag", flag);
                    i.putExtra("pos", 0);
                    startActivity(i);
                } else {
                    Intent i = new Intent( SaveDFRDetails.this,FillingTransactions.class);
                    i.putExtra("res", res);
                    i.putExtra("flag", flag);
                    startActivity(i);
                }

            } else if (fillingReportList.getFillingReportList()!=null && fillingReportList.getFillingReportList().size() > 0
                    && fillingReportList.getFillingReportList().get(0).getFLAG().equalsIgnoreCase("F")) {
                Utils.toastMsg( SaveDFRDetails.this, fillingReportList.getFillingReportList().get(0).getMSG());
            } else {
                // Toast.makeText(LastFillingTrans.this,"Report Not Found",Toast.LENGTH_LONG).show();
                Utils.toast( SaveDFRDetails.this, "212");
            }
        }
    }
}

