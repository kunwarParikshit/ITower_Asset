package com.isl.taskform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.isl.dao.cache.ConfigurationCacheManager;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.energy.filling.FillingTransactionInput;
import com.isl.energy.withdrawal.FuelPurchaseGridRPT;
import com.isl.modal.Data;
import com.isl.modal.FormControl;
import com.isl.modal.Response;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.List;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.itower.GPSTracker;

import infozech.itower.R;

import com.isl.photo.camera.ImageAdapter64;
import com.isl.photo.camera.ViewImage64;

import android.view.View.OnClickListener;

/**
 * Modify by dhakan on 11/26/2018.
 * add Auto Complete TextView 0.1
 */

public class ActivityTaskForm extends Activity implements OnClickListener, TaskCompleted {
    AppPreferences mAppPreferences;
    String moduleUrl = "", latitude = "", longitude = "";
    LinearLayout linear;
    TextView textView, tv_brand_logo;
    Button bt_submit, bt_back, btn_take_photo;
    LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinkedHashMap<String, ViewImage64> lhmImages = new LinkedHashMap<String, ViewImage64>();
    ExpandableHeightGridView grid = null;
    String selEM,txnSid;
    public static HashMap<String, String> tranData;
    ImageControl imgControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_form);
        previousValue();
        init();
        selEM = getIntent().getStringExtra("sel");
        bt_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonAlert("291", "63", "64");

            }
        });

        bt_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData("");
            }
        });
        //initializeForm();
      //  new GetFormData(ActivityTaskForm.this, tranData.get(AppConstants.TASK_STATE_ID_ALIAS)).execute();

        if (!formDataType().equalsIgnoreCase("")) {
            if (Utils.isNetworkAvailable(ActivityTaskForm.this)) {
                new GetFormData(ActivityTaskForm.this, tranData.get(AppConstants.TASK_STATE_ID_ALIAS)).execute();
            } else {
                initializeForm();
            }
        } else {
            initializeForm();
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(ActivityTaskForm.this, "edit click", Toast.LENGTH_LONG).show();
    }

    public void init() {
        ConfigurationCacheManager.getFormControlList().clear();
        ConfigurationCacheManager.setImageControl(new FormControl());

        lhmImages.clear();
        mAppPreferences = new AppPreferences(this);
        //imgInfoArray = new JSONArray();
        //jsonArrStrImg = new JSONArray();
        DataBaseHelper dbHelper = new DataBaseHelper(ActivityTaskForm.this);
        dbHelper.open();
        moduleUrl = dbHelper.getModuleIP("Energy");
        dbHelper.close();
        grid = (ExpandableHeightGridView) findViewById(R.id.grid);
        linear = (LinearLayout) findViewById(R.id.ll_textview);
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        tv_brand_logo = (TextView) findViewById(R.id.tv_brand_logo);
        TextView tv_last_trans = (TextView) findViewById(R.id.tv_last_trans);
        Utils.msgText(ActivityTaskForm.this, "167", tv_last_trans);
        tv_last_trans.setPaintFlags(tv_last_trans.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_last_trans.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(ActivityTaskForm.this, FillingTransactionInput.class);
                i.putExtra("flag", "A");
                i.putExtra("sid", getSiteID());
                i.putExtra("dg", getDgType());
                startActivity(i);
            }
        });
        if (tranData.get(AppConstants.TASK_STATE_ID_ALIAS).equalsIgnoreCase("21")) {
            tv_last_trans.setVisibility(View.VISIBLE);
        }
        // set Text Fuel Purchase
        Utils.msgText(ActivityTaskForm.this, tranData.get(AppConstants.HEADER_CAPTION_ID), tv_brand_logo);
        Utils.msgButton(ActivityTaskForm.this, "115", bt_submit);
        bt_back = (Button) findViewById(R.id.bt_back);
        Utils.msgButton(ActivityTaskForm.this, "71", bt_back);
    }


    @Override
    public void initializeForm() {
        DataBaseHelper dbHelper = new DataBaseHelper(ActivityTaskForm.this);
        dbHelper.open();
        Cursor c = dbHelper.getForm(tranData.get(AppConstants.TASK_STATE_ID_ALIAS));

        // dbHelper.close();
        if (c != null) {

            EditTextControl editTextControl = new EditTextControl(tranData);
            SpinnerControl spinnerControl = new SpinnerControl(tranData);
            AutoCompleteTVControl autoCompleteControl = new AutoCompleteTVControl(tranData);//0.1

            tvParam.setMargins(10, 20, 10, 0);

            while (c.moveToNext()) {

                textView = new TextView(this);
                textView.setTypeface(Utils.typeFace(ActivityTaskForm.this));
                String s = c.getString(c.getColumnIndex(AppConstants.FIELD_NAME_ALIAS));
                textView.setText("" + c.getString(c.getColumnIndex(AppConstants.FIELD_NAME_ALIAS)));
                textView.setTextColor(Color.parseColor("#2B4E81"));
                textView.setTextSize(15);
                textView.setLayoutParams(tvParam);
                textView.setFocusable(true);
                textView.setFocusableInTouchMode(true);

                linear.addView(textView);
                FormControl formControl = new FormControl();

                formControl.setTv(textView);

                formControl.setDataType(c.getInt(c.getColumnIndex(AppConstants.DATA_TYPE_ALIAS)));
                formControl.setFieldId(c.getString(c.getColumnIndex(AppConstants.FIELD_ID_ALIAS)));
                formControl.setFieldType(c.getInt(c.getColumnIndex(AppConstants.FIELD_TYPE_ALIAS)));
                formControl.setFieldType(c.getInt(c.getColumnIndex(AppConstants.FIELD_TYPE_ALIAS)));
                formControl.setDataLength(c.getString(c.getColumnIndex(AppConstants.DATA_LENGTH_ALIAS)));
                formControl.setDropDownValType(c.getInt(c.getColumnIndex(AppConstants.DROPDOWN_VAL_TYPE_ALIAS)));
                formControl.setDropDownValue(c.getString(c.getColumnIndex(AppConstants.DROPDOWN_VALUE_ALIAS)));
                formControl.setChangeReload(c.getString(c.getColumnIndex(AppConstants.ON_CHANGE_RELOAD_ALIAS)));
                formControl.setKeyItem(c.getString(c.getColumnIndex(AppConstants.JSON_KEY_ALIAS)));
                formControl.setPlevel(c.getInt(c.getColumnIndex(AppConstants.PARENT_LEVEL_ALIAS)));
                formControl.setPCRelation(c.getString(c.getColumnIndex(AppConstants.PARENT_CHILD_RELATION)));
                formControl.setInitialize(c.getString(c.getColumnIndex(AppConstants.INITIALIZE_BY)));
                formControl.setMandatory(c.getString(c.getColumnIndex(AppConstants.IS_MANDATORY_ALIAS)).equalsIgnoreCase("Y") ? true : false);

                if (formControl.getDataLength() != null) {

                    String[] arr = formControl.getDataLength().split(AppConstants.COMMA);

                    if (arr.length > 1) {
                        formControl.setBefore(Integer.parseInt(arr[0]));
                        formControl.setAfter(Integer.parseInt(arr[1]));
                    }
                }
                if (selEM.equalsIgnoreCase("EM")){
                    if (formControl.getFieldId().equalsIgnoreCase("75")||
                            formControl.getFieldId().equalsIgnoreCase("76")||
                            formControl.getFieldId().equalsIgnoreCase("74")||
                            formControl.getFieldId().equalsIgnoreCase("73")){
                        formControl.setFieldType(3);
                    }
                }else if (selEM.equalsIgnoreCase("FS")){
                    if (formControl.getFieldId().equalsIgnoreCase("73")){
                        formControl.setFieldType(3);
                    }
                    if (formControl.getFieldId().equalsIgnoreCase("1")||
                            formControl.getFieldId().equalsIgnoreCase("23")||
                            formControl.getFieldId().equalsIgnoreCase("72")){
                        formControl.setFieldType(1);
                    }
                }
                if (AppConstants.FIELD_TYPE.HIDDEN.getValue() == formControl.getFieldType()
                        || AppConstants.PARENT_LEVEL.CHILD.getValue() == formControl.getPlevel()) {
                    textView.setVisibility(View.GONE);
                }

                if ((AppConstants.DATA_TYPE.ALPHANUMERIC.getValue() == formControl.getDataType()
                        || AppConstants.DATA_TYPE.NUMBER.getValue() == formControl.getDataType()
                        || AppConstants.DATA_TYPE.INTEGER.getValue() == formControl.getDataType())
                        && AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() != formControl.getDropDownValType()
                        && AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() != formControl.getDropDownValType()) {
                    //Create Textbox
                    linear.addView(editTextControl.addEditText(ActivityTaskForm.this, formControl));
                    //editTextControl.addEditText(ActivityTaskForm.this,formControl).setOnClickListener(this);


                } else if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType()
                        || AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType()) {
                    //else if(AppConstants.DATA_TYPE.DROPDOWN.getValue() ==  formControl.getDataType()){
                    //Create Dropdown
                    linear.addView(spinnerControl.addSpinner(ActivityTaskForm.this, formControl));

                } else if (AppConstants.DATA_TYPE.DATE.getValue() == formControl.getDataType()) {
                    //Create Textbox for Date Control
                    linear.addView(editTextControl.addDate(ActivityTaskForm.this, formControl));

                } else if (AppConstants.DATA_TYPE.TIME.getValue() == formControl.getDataType()) {
                    //Create Textbox for Time
                    linear.addView(editTextControl.addTime(ActivityTaskForm.this, formControl));

                } else if (AppConstants.DATA_TYPE.AUTOCOMPLETE.getValue() == formControl.getDataType()) {
                    // 0.1 Create AutoCompleteTextView for for pre populate site id
                    linear.addView(autoCompleteControl.addAutoComplete(ActivityTaskForm.this, formControl));
                    getAutoCompletedataList(formControl);

                } else if (AppConstants.DATA_TYPE.IMAGE.getValue() == formControl.getDataType()) {

                    if (formControl.getDropDownValue() != null) {
                        formControl.getImgMsg().addAll(Arrays.asList(formControl.getDropDownValue().split(AppConstants.COMMA)));
                    }

                    if (AppConstants.FIELD_TYPE.READ_WRITE.getValue() == formControl.getFieldType()) {
                        formControl.setImageNameTemplate(tranData.get(AppConstants.IMG_NAME_TEMP_ALIAS));
                        imgControl = new ImageControl(ActivityTaskForm.this, formControl, btn_take_photo);

                        imgControl.initliazeImageControl();
                    }
                    ConfigurationCacheManager.setImageControl(formControl);
                }
                ConfigurationCacheManager.getFormControlList().put(Integer.parseInt(formControl.getFieldId()), formControl);
            }
        }
    }

    /*public void backButtonAlert() {

        AlertDialog.Builder alert = new AlertDialog.Builder( ActivityTaskForm.this );
        LayoutInflater inflater = ActivityTaskForm.this.getLayoutInflater();
        View view = inflater.inflate( R.layout.custom_alert, null );
        Button positive = (Button) view.findViewById( R.id.bt_ok );
        Button negative = (Button) view.findViewById( R.id.bt_cancel );
        TextView title = (TextView) view.findViewById( R.id.tv_title );
        EditText et = (EditText) view.findViewById( R.id.et_ip );
        et.setVisibility( View.GONE );
        positive.setTypeface( WorkFlowUtils.typeFace( ActivityTaskForm.this ) );
        negative.setTypeface( WorkFlowUtils.typeFace( ActivityTaskForm.this ) );
        title.setTypeface( WorkFlowUtils.typeFace( ActivityTaskForm.this ) );
        // Do you want to exit
        title.setText( WorkFlowUtils.msg( ActivityTaskForm.this, "291" ) );
        positive.setText( WorkFlowUtils.msg( ActivityTaskForm.this, "63" ) );
        negative.setText( WorkFlowUtils.msg( ActivityTaskForm.this, "64" ) );
        alert.setView( view );
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        positive.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alertDialog.cancel();
                if (tranData.get( AppConstants.FLAG_ALIAS ).equalsIgnoreCase( "1" )) {
                    Intent i = new Intent( ActivityTaskForm.this, FuelPurchaseGridRPT.class );
                    startActivity( i );
                    finish();
                } else {
                    finish();
                }

            }
        } );
        negative.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alertDialog.cancel();
            }
        } );
    }*/

    public void backButtonAlert(String confirmID, String primaryBt, String secondaryBT) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ActivityTaskForm.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(
                R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        positive.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        negative.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        title.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        title.setText(Utils.msg(ActivityTaskForm.this, confirmID));
        // title.setText("Do you want to exit?");
        positive.setText(Utils.msg(ActivityTaskForm.this, primaryBt));
        negative.setText(Utils.msg(ActivityTaskForm.this, secondaryBT));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (tranData.get(AppConstants.FLAG_ALIAS).equalsIgnoreCase("1")) {
                    Intent i = new Intent(ActivityTaskForm.this, FuelPurchaseGridRPT.class);
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }

            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppPreferences.setTrackMode(1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ViewImage64 viewImg = imgControl.onActivityResult(requestCode, resultCode, data, getSiteID());
        /*view post images in grid*/
        if (viewImg != null) {
            lhmImages.put("" + ConfigurationCacheManager.getImageControl().getImgCounter(), viewImg);
        }
        ImageAdapter64 adapter = new ImageAdapter64(ActivityTaskForm.this, lhmImages);
        grid.setFastScrollEnabled(true);
        grid.setAdapter(adapter);
        grid.setExpanded(true);

    }
    @Override
    public void dataSubmitted(String result) {
        Gson gson = new Gson();
        Response response;
        try {
            response = gson.fromJson(result, Response.class);
        } catch (Exception e) {
            response = null;
        }

        if (response != null) {
            //Toast.makeText( ActivityTaskForm.this,response.getMessage(),Toast.LENGTH_LONG ).show();
            Utils.toastMsg(ActivityTaskForm.this, response.getMessage());
            if (response.getSuccess().equals("true")) {
                if (tranData.get(AppConstants.FLAG_ALIAS).equalsIgnoreCase("1")) {
                    Intent i = new Intent(ActivityTaskForm.this, FuelPurchaseGridRPT.class);
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }
            } else if (response.getErrorId().equalsIgnoreCase("270")) {
                alert(response.getMessage());
            }
        } else {
            //Server Not Available
            Utils.toast(ActivityTaskForm.this, "13");
        }
    }

    private boolean validate() {
        GPSTracker gps = new GPSTracker(ActivityTaskForm.this);
        //First Validation for GPS OFF and Location Permission Denied
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (gps.isMockLocation() == true) {
            FackApp();
            return false;
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
            return false;
        } else if (gps.canGetLocation() == true) {
            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());
            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                //errId = "252";
                Utils.toast(ActivityTaskForm.this, "252");
                return false;
            } else {
                latitude = String.valueOf(gps.getLatitude());
                longitude = String.valueOf(gps.getLongitude());
            }

        } else if (!Utils.hasPermissions(ActivityTaskForm.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(ActivityTaskForm.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
            return false;
        }

        String errId = "";
        String errMsg = "";

        for (FormControl formControl : ConfigurationCacheManager.getFormControlList().values()) {

            boolean isValidationFailed = false;
            String key = formControl.getKeyItem();
            String value = "";

            switch (formControl.getDataType()) {
                case 2: //For Dropdown

                    if (formControl.getSpinner().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                        if (formControl.getSpinner().getSelectedItem().toString().trim().equalsIgnoreCase(AppConstants.DD_SELECT_VALUE)) {
                            isValidationFailed = true;
                            errId = "256";
                        }
                    }

                    if (formControl.getSpinner().getVisibility() == View.VISIBLE && AppConstants.FIELD_TYPE.HIDDEN.getValue() != formControl.getFieldType()) {
                        if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType()) {
                            value = formControl.getDdIds().get(formControl.getSpinner().getSelectedItemPosition());
                        } else if (AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType()) {
                            value = formControl.getSpinner().getSelectedItem().toString().trim();
                        }
                    } else {
                        value = "";
                    }


                    break;
                case 7: //Time

                    if (formControl.getEditText().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                        if (formControl.getEditText().getText().toString().trim().length() == 0) {
                            isValidationFailed = true;
                            errId = "256";
                        } else {
                            if (!Utils.timeValidate(formControl.getEditText().getText().toString().trim())) {
                                isValidationFailed = true;
                                errMsg = "Invalid" + " " + formControl.getTv().getText().toString();
                            }
                        }
                    }

                    value = formControl.getEditText().getText().toString().trim();


                    break;
                case 6: //Date

                    if (formControl.getEditText().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                        if (formControl.getEditText().getText().toString().trim().length() == 0) {
                            isValidationFailed = true;
                            errId = "255";
                        }
                    }

                    value = formControl.getEditText().getText().toString().trim();

                    break;
                case 1: //Alphanumeric
                case 3: //Number
                case 4: //Integer

                    if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() != formControl.getDropDownValType()
                            && AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() != formControl.getDropDownValType()) {
                        if (formControl.getEditText().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                            if (formControl.getEditText().getText().toString().trim().length() == 0) {
                                isValidationFailed = true;
                                errId = "255";
                            }
                        }
                        value = formControl.getEditText().getText().toString().trim();
                    } else if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType()
                            || AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType()) {
                        if (formControl.getSpinner().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                            if (formControl.getSpinner().getSelectedItem().toString().trim().equalsIgnoreCase(AppConstants.DD_SELECT_VALUE)) {
                                isValidationFailed = true;
                                errId = "256";
                            }
                        }

                        if (formControl.getSpinner().getVisibility() == View.VISIBLE && AppConstants.FIELD_TYPE.HIDDEN.getValue() != formControl.getFieldType()) {

                            if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType()) {
                                value = formControl.getDdIds().get(formControl.getSpinner().getSelectedItemPosition());
                            } else if (AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType()) {
                                value = formControl.getSpinner().getSelectedItem().toString().trim();
                            }
                        } else {
                            value = "";
                        }

                    }
                    break;
                case 8: //AutoComplete TextView

                    if (formControl.getAutoCompleteTextView().getVisibility() == View.VISIBLE && formControl.isMandatory()) {
                        if (formControl.getAutoCompleteTextView().getText().toString().trim().length() == 0) {
                            isValidationFailed = true;
                            errId = "255";
                        }
                    }

                    value = formControl.getAutoCompleteTextView().getText().toString().trim();

                    break;
                case 5: //Images

                    if (ConfigurationCacheManager.getImageControl().getImgCounter() < ConfigurationCacheManager.getImageControl().getBefore()) {
                        isValidationFailed = true;
                        errMsg = Utils.msg(ActivityTaskForm.this, "257") + " "
                                + ConfigurationCacheManager.getImageControl().getBefore() + " "
                                + Utils.msg(ActivityTaskForm.this, "258") + " "
                                + ConfigurationCacheManager.getImageControl().getAfter() + " "
                                + Utils.msg(ActivityTaskForm.this, "301");

                    } else {
                        //if(ConfigurationCacheManager.getImageControl()!=null && ConfigurationCacheManager.getImageControl().getBefore()>0){
                        if (ConfigurationCacheManager.getImageControl() != null && ConfigurationCacheManager.getImageControl().getImgCounter() > 0) {
                            value = ConfigurationCacheManager.getImageControl().getImgData()
                                    .replaceAll("latitude", latitude)
                                    .replaceAll("longitude", longitude);
                        }
                    }
                    break;
                default:

            }

            if (isValidationFailed) {
                formControl.getTv().clearFocus();
                formControl.getTv().requestFocus();
                if (errMsg == null || errMsg.isEmpty()) {
                    errMsg = Utils.msg(ActivityTaskForm.this, errId) + " " + formControl.getTv().getText().toString();
                }
                Utils.toastMsg(ActivityTaskForm.this, errMsg);
                return false;
            } else {
                try {
                    ConfigurationCacheManager.getFormData().put(key, value);
                } catch (JSONException e) {
                }
            }
        }

        return true;
    }

    private void previousValue() {

        tranData = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstants.TRAN_DATA_MAP_ALIAS);
        txnSid =tranData.get(AppConstants.SITE_ID_ALIAS);
        if (tranData.get(AppConstants.IS_EDIT_ALIAS).equalsIgnoreCase("0")) {
            LinearLayout rl_submit = (LinearLayout) findViewById(R.id.rl_submit);
            rl_submit.setVisibility(View.GONE);
        }
    }

    public String formDataType() {
        String DataType_Str = "1";
        String i = "";
        DataBaseHelper db = new DataBaseHelper(ActivityTaskForm.this);
        db.open();

        if (tranData.get(AppConstants.TASK_STATE_ID_ALIAS).equalsIgnoreCase("11")) {
            i = Utils.CompareDates(db.getSaveTimeStmpEner("27"), db.getLoginTimeStmp("27", "0"), "27");
        } else if (tranData.get(AppConstants.TASK_STATE_ID_ALIAS).equalsIgnoreCase("12")) {
            i = Utils.CompareDates(db.getSaveTimeStmp("27", "0"), db.getLoginTimeStmp("27", "0"), "27");
        } else if (tranData.get(AppConstants.TASK_STATE_ID_ALIAS).equalsIgnoreCase("13")) {
            i = Utils.CompareDates(db.getSaveTimeStmpAsset("27"), db.getLoginTimeStmp("27", "0"), "27");
        } else if (tranData.get(AppConstants.TASK_STATE_ID_ALIAS).equalsIgnoreCase("21")) {
            i = Utils.CompareDates(db.getSaveTimeStmpAssset("27"), db.getLoginTimeStmp("27", "0"), "27");
        }

        if (i != "1") {
            DataType_Str = i;
        }
        if (DataType_Str == "1") {
            DataType_Str = "";
        }
        db.close();
        return DataType_Str;
    }

    // call this method when hardware back button press
    @Override
    public void onBackPressed() {
        backButtonAlert("291", "63", "64");
    }

    private void getAutoCompletedataList(FormControl formControl) {
        GPSTracker gps = new GPSTracker(ActivityTaskForm.this);
        if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
        } else {
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());
            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude
                    .isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude
                    .isEmpty())) {
                Utils.toast(ActivityTaskForm.this, "252");
            } else {
                if (Utils.isNetworkAvailable(ActivityTaskForm.this)) {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId()));
                    nameValuePairs.add(new BasicNameValuePair(AppConstants.LAT_ALIAS, latitude));
                    nameValuePairs.add(new BasicNameValuePair(AppConstants.LNG_ALIAS, longitude));
                    nameValuePairs.add(new BasicNameValuePair(AppConstants.KEY_ALIAS, formControl.getKeyItem()));
                    new GetData(ActivityTaskForm.this, nameValuePairs, formControl).execute();
                }
            }
        }
    }

    @Override
    public void autoCompleteDataFetchComplete(ArrayList<Data> list, FormControl formControl) {
        ArrayList<String> listdata = new ArrayList<String>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                listdata.add(list.get(i).getData());
            }
        }
        //AdapterAutoComplete adapter = new AdapterAutoComplete(ActivityTaskForm.this,list);
        //ArrayAdapter<Data> adapter = new ArrayAdapter<Data>( ActivityTaskForm.this, android.R.layout.select_dialog_item, list );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityTaskForm.this, android.R.layout.select_dialog_item, listdata);
        formControl.getAutoCompleteTextView().setAdapter(adapter);// setting the adapter data into the AutoCompleteTextView
        formControl.getAutoCompleteTextView().setText(txnSid);


    }

    public void submitData(String flag) {
        if (validate()) {
            DataBaseHelper db = new DataBaseHelper(ActivityTaskForm.this);
            db.open();

            String url = "";

            if (moduleUrl.equalsIgnoreCase("0")) {
                url = mAppPreferences.getConfigIP() + WebMethods.url_SaveAPI;
            } else {
                url = moduleUrl + WebMethods.url_SaveAPI;
            }
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }

            String appParam = tranData.get(AppConstants.ADD_PARAM_ALIAS) + AppConstants.ADD_PARAM_SEPERATOR + AppConstants.LAT_ALIAS + "=" + latitude
                    + AppConstants.ADD_PARAM_SEPERATOR + AppConstants.LONG_ALIAS + "=" + longitude + AppConstants.ADD_PARAM_SEPERATOR + AppConstants.APP_VERSIONS + "=" + pInfo.versionName + AppConstants.ADD_PARAM_SEPERATOR + AppConstants.TRAN_ID_ALIAS + "="+tranData.get(AppConstants.TRANSID) + AppConstants.ADD_PARAM_SEPERATOR + "esid=";

            if (Utils.isNetworkAvailable(ActivityTaskForm.this)) {
                //String s1=appParam;
                //String s2=ConfigurationCacheManager.getFormData().toString();
                //String s3=ConfigurationCacheManager.getImageControl().getImgInfoArray().toString();
                //String s4=url;
                AsynTaskService task = new AsynTaskService(ActivityTaskForm.this,
                        tranData.get(AppConstants.MODULE),
                        tranData.get(AppConstants.TASK_STATE_ID_ALIAS),
                        appParam,
                        ConfigurationCacheManager.getFormData().toString(),
                        ConfigurationCacheManager.getImageControl().getImgInfoArray(),
                        tranData.get(AppConstants.OPERATION), flag);
                task.execute(url);
            } else {
                db.insertDataLocally(tranData.get(AppConstants.MODULE), ConfigurationCacheManager.getFormData().toString(), ConfigurationCacheManager.getImageControl().getImgInfoArray().toString(), appParam, tranData.get(AppConstants.TASK_STATE_ID_ALIAS), mAppPreferences.getUserId(),tranData.get(AppConstants.OPERATION));
                Utils.toast(ActivityTaskForm.this, "66");
                // No internet connection.Data stored locally in the app
                finish();
            }
            db.close();

        }
    }

    private void alert(String cphMsg) {
        final Dialog actvity_dialog = new Dialog(ActivityTaskForm.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        positive.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        negative.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        title.setTypeface(Utils.typeFace(ActivityTaskForm.this));
        title.setText(cphMsg);
        positive.setText(Utils.msg(ActivityTaskForm.this, "7"));
        negative.setText(Utils.msg(ActivityTaskForm.this, "8"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                submitData("Y");

            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }

    public String getSiteID() {
        String siteID = "";
        for (FormControl formControl : ConfigurationCacheManager.getFormControlList().values()) {
            String key = formControl.getKeyItem();
            int dataType = formControl.getDataType();
            if (key.equalsIgnoreCase("sid") && dataType == 8) {
                siteID = formControl.getAutoCompleteTextView().getText().toString();
            } else if (key.equalsIgnoreCase("sid") && dataType == 1) {
                siteID = formControl.getEditText().getText().toString();
            }
        }
        return siteID;
    }

    public String getDgType() {
        String value = "";
        for (FormControl formControl : ConfigurationCacheManager.getFormControlList().values()) {
            String key = formControl.getKeyItem();
            if (key.equalsIgnoreCase("dg") && formControl.getSpinner().getVisibility() == View.VISIBLE && AppConstants.FIELD_TYPE.HIDDEN.getValue() != formControl.getFieldType()) {
                if (AppConstants.DROPDOWN_VAL_TYPE.SQL.getValue() == formControl.getDropDownValType()) {
                    value = formControl.getDdValues().get(formControl.getSpinner().getSelectedItemPosition());
                } else if (AppConstants.DROPDOWN_VAL_TYPE.INLINE.getValue() == formControl.getDropDownValType()) {
                    value = formControl.getSpinner().getSelectedItem().toString().trim();
                }
            }
        }
        return value;
    }

    public void FackApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTaskForm.this);
        builder.setMessage("Uninstall " + mAppPreferences.getAppNameMockLocation() + " app/Remove Fack Location  in your mobile handset.");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}