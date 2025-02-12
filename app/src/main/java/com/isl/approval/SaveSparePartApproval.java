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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.modal.BeanSiteSpare;
import com.isl.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

/**
 * Created by dhakan on 6/11/2019.
 */
public class SaveSparePartApproval extends Activity {
    AppPreferences mAppPreferences;
    DataBaseHelper db;
    String prevQty, pendingTabRight="",moduleUrl = "",id="",txnId="",siteId="",tran_date="",status="",spareId="",approveQty="",tranType="",userId="",remarks="";
    BeanSiteSpare response_spare_list = null;
    TextView bt_submit,et_app_qty,tv_brand_logo,tv_site_id,tv_tran_date,tv_tran_type,tv_ticket_id,tv_spare_name,tv_qty,tv_seriali_no,tv_remarks,
            et_site_id,et_transaction_date,et_tran_type,et_ticket_id,txt_spare_name,txt_serial_no,tv_last_tran;
    EditText txt_qty,et_remarks;
    RadioGroup rg_tran_type;
    RadioButton rb_dfr,rb_reject;
    LinearLayout ll_buttons,ll_option,ll_app_qty,ll_qty;
    Button iv_back;
    String msgLbl="approved";
    int alertCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.spare_part_details);
        mAppPreferences = new AppPreferences(this);
        db = new DataBaseHelper(this);
        db.open();
        pendingTabRight = db.getSubMenuRight("SparePartPendingTab", "Approval");
        moduleUrl = db.getModuleIP("Approval");
        db.close();
        id = getIntent().getExtras().getString("id");
        txnId = getIntent().getExtras().getString("txnId");
        siteId = getIntent().getExtras().getString("siteId");
        tran_date = getIntent().getExtras().getString("tran_date");
        status = getIntent().getExtras().getString("status");

        tv_brand_logo = (TextView)findViewById( R.id.tv_brand_logo);
        Utils.msgText( SaveSparePartApproval.this, "459", tv_brand_logo);

        tv_site_id = (TextView)findViewById( R.id.tv_site_id);
        Utils.msgText( SaveSparePartApproval.this, "443", tv_site_id);

        tv_tran_date = (TextView)findViewById( R.id.tv_tran_date);
        Utils.msgText( SaveSparePartApproval.this, "444", tv_tran_date);

        tv_tran_type = (TextView)findViewById( R.id.tv_tran_type);
        Utils.msgText( SaveSparePartApproval.this, "460", tv_tran_type);

        tv_ticket_id = (TextView)findViewById( R.id.tv_ticket_id);
        Utils.msgText( SaveSparePartApproval.this, "445", tv_ticket_id);

        tv_spare_name = (TextView)findViewById( R.id.tv_spare_name);
        Utils.msgText( SaveSparePartApproval.this, "447", tv_spare_name);

        tv_qty = (TextView)findViewById( R.id.tv_qty);
        Utils.msgText( SaveSparePartApproval.this, "448", tv_qty);

        tv_seriali_no = (TextView)findViewById( R.id.tv_seriali_no);
        Utils.msgText( SaveSparePartApproval.this, "449", tv_seriali_no);


        tv_remarks = (TextView)findViewById( R.id.tv_remarks);
        Utils.msgText( SaveSparePartApproval.this, "450", tv_remarks);

        tv_last_tran = (TextView)findViewById( R.id.tv_last_tran);
        tv_last_tran.setPaintFlags(tv_last_tran.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        Utils.msgText( SaveSparePartApproval.this, "451", tv_last_tran);
        tv_last_tran.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Dialog();
            }
        });
        et_site_id=(TextView)findViewById( R.id.et_site_id);
        et_transaction_date=(TextView)findViewById( R.id.et_transaction_date);
        et_tran_type=(TextView)findViewById( R.id.et_tran_type);
        et_ticket_id=(TextView)findViewById( R.id.et_ticket_id);
        txt_spare_name=(TextView)findViewById( R.id.txt_spare_name);
        txt_qty=(EditText) findViewById( R.id.txt_qty);
        et_app_qty=(TextView) findViewById( R.id.et_app_qty);
        txt_serial_no=(TextView)findViewById( R.id.txt_serial_no);
        et_remarks=(EditText)findViewById( R.id.et_remarks);
        InputFilter rmkfilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                for (int i = start;i < end;i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            &&!Character.toString( source.charAt(i)).equals("!")
                            && !Character.toString( source.charAt(i)).equals("@")
                            && !Character.toString( source.charAt(i)).equals("^")
                            && !Character.toString( source.charAt(i)).equals("_")
                            && !Character.toString( source.charAt(i)).equals("-")
                            && !Character.toString( source.charAt(i)).equals("[")
                            && !Character.toString( source.charAt(i)).equals("]")
                            && !Character.toString( source.charAt(i)).equals(" ")
                            && !Character.toString( source.charAt(i)).equals("\\"))
                    {
                        return "";
                    }
                }
                return null;
            }
        };
        et_remarks.setFilters(new InputFilter[] { rmkfilter,new InputFilter.LengthFilter(200)});

        rb_dfr=(RadioButton) findViewById( R.id.rb_dfr);
        rb_dfr.setText( Utils.msg( SaveSparePartApproval.this,"452"));

        rb_reject=(RadioButton) findViewById( R.id.rb_reject);
        rb_reject.setText( Utils.msg( SaveSparePartApproval.this,"453"));

        ll_option=(LinearLayout)findViewById( R.id.ll_option);
        ll_buttons=(LinearLayout)findViewById( R.id.ll_buttons);
        ll_qty=(LinearLayout)findViewById( R.id.ll_qty);
        ll_app_qty=(LinearLayout)findViewById( R.id.ll_app_qty);

        if (Utils.isNetworkAvailable( SaveSparePartApproval.this)) {
            new GetPendingSpareTask( SaveSparePartApproval.this,"","").execute();
        } else {
            //No Internet Connection;
            Utils.toast( SaveSparePartApproval.this, "17");
        }
        rg_tran_type=(RadioGroup)findViewById( R.id.rg_tran_type);
        rg_tran_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.rb_dfr) {
                    status="2";
                    txt_qty.setBackgroundResource( R.drawable.input_box );
                    txt_qty.setEnabled(true);
                    msgLbl="approved";
                } else{
                    status="3";
                    approveQty = prevQty;
                    msgLbl="rejected";
                    txt_qty.setText(approveQty);
                    txt_qty.setBackgroundResource( R.drawable.et_data);
                    txt_qty.setEnabled(false);
                }
            }
        });
        iv_back = (Button) findViewById( R.id.iv_back);
        Utils.msgButton( SaveSparePartApproval.this, "71", iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Intent i = new Intent(SaveSparePartApproval.this, SparePartTabs.class);
                //startActivity(i);
                finish();
            }
        });

        bt_submit = (TextView)findViewById( R.id.bt_submit);
        Utils.msgText( SaveSparePartApproval.this, "454", bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validation())
                {
                    if(status.equalsIgnoreCase("1"))
                    {
                        status="2";
                    }
                    if(status.equalsIgnoreCase("3"))
                    {
                        alert(2,"468");
                    }
                    else
                    {
                       ActionSparePart();
                    }
                }
            };
        });
    }

    public void ActionSparePart()

    {
        if (Utils.isNetworkAvailable( SaveSparePartApproval.this)) {
            new SaveSparePartTask( SaveSparePartApproval.this).execute();
        } else {
            JSONObject obj = new JSONObject();
            try {

                obj.put("txnID", id);
                obj.put("status", status);
                obj.put("spareID", spareId);
                obj.put("tranType", tranType);
                obj.put("remarks", et_remarks.getText().toString().trim());
                obj.put("userId", mAppPreferences.getUserId());

                obj.put("sid", et_site_id.getText().toString());
                obj.put("txnDt", et_transaction_date.getText().toString());
                obj.put("sparePartName", txt_spare_name.getText().toString());
                obj.put("actTypeId", et_tran_type.getText().toString());
                obj.put("qty", txt_qty.getText().toString());
                obj.put("tid", et_ticket_id.getText().toString());
                obj.put("schDt", et_transaction_date.getText().toString());
                obj.put("omeName", response_spare_list.getGetSparePart().get(0).getVendor());

            } catch (JSONException e) {
            }
            // Showing message of data saving locally when Network
            // is not present.
            DataBaseHelper db = new DataBaseHelper( SaveSparePartApproval.this);
            db.open();
            //db.insertDataLocally( "SPARE",obj.toString, status, spareId,tranType, mAppPreferences.getUserId(),id,remarks);
            db.insertDataLocally("SPARE", obj.toString(), "", "", "", mAppPreferences.getUserId(),"");
            db.close();
            Utils.toast( SaveSparePartApproval.this, "66");
            Intent i = new Intent( SaveSparePartApproval.this, SparePartTabs.class);
            startActivity(i);
            finish();
        }

    }
    public boolean Validation()
     {
        Boolean valid = true;
        approveQty =  txt_qty.getText().toString().trim();

        if(approveQty.length()==0){
            Toast.makeText( SaveSparePartApproval.this, "Approved Quantity can not be blank or zero.", Toast.LENGTH_SHORT).show();
            valid = false;
        }else if(approveQty.length()>0 && Float.parseFloat(approveQty) == 0.0){
            Toast.makeText( SaveSparePartApproval.this, "Approved Quantity can not be blank or zero.", Toast.LENGTH_SHORT).show();
            valid = false;
        }else if(Float.parseFloat(approveQty)> Float.parseFloat(response_spare_list.getGetSparePart().get(0).getQty())){
            Toast.makeText( SaveSparePartApproval.this, "Approved Quantity can not be greater than Previous Quantity.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(status.equalsIgnoreCase("3"))
        {
            if(et_remarks.getText().toString().equalsIgnoreCase(""))
            {
                Toast.makeText( SaveSparePartApproval.this, "Remarks can not be blank.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        return valid;
    }
    public class SaveSparePartTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String response;
        public SaveSparePartTask(Context con) {
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
                String url ="";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("txnID",id));
                nameValuePairs.add(new BasicNameValuePair("status",status));
                nameValuePairs.add(new BasicNameValuePair("spareID",spareId));
                nameValuePairs.add(new BasicNameValuePair("approveQty",approveQty));
                nameValuePairs.add(new BasicNameValuePair("tranType",tranType));
                nameValuePairs.add(new BasicNameValuePair("userID",userId));
                nameValuePairs.add(new BasicNameValuePair("remarks",et_remarks.getText().toString()));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_save_spare_part;
                }else{
                    url=moduleUrl+ WebMethods.url_save_spare_part;
                }
                response = Utils.httpPostRequest(con,url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response == null) {

            } else if (response!=null) {

                try {
                    JSONObject reader = new JSONObject( response );
                    JSONArray objArray = reader.getJSONArray("GetSvApprovalSparePart");
                    String message = objArray.getJSONObject(0).getString("errorDesc");
                    String flag = objArray.getJSONObject(0).getString("respFlag");
                    if(flag.equalsIgnoreCase("s"))
                    {
                        Toast.makeText( SaveSparePartApproval.this, "Spare part "+msgLbl+" successfully.", Toast.LENGTH_SHORT).show();
                        // Intent i = new Intent(SaveSparePartApproval.this, SparePartTabs.class);
                       // i.putExtra("flag","0");
                       // startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText( SaveSparePartApproval.this, message, Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
            super.onPostExecute(result);
        }
    }

    public void Dialog() {
        final Dialog LastTxnDialog = new Dialog( SaveSparePartApproval.this);
        LastTxnDialog.setCanceledOnTouchOutside(false);
        LastTxnDialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        LastTxnDialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LastTxnDialog.setContentView( R.layout.spare_last_details );
        final Window window_SignIn = LastTxnDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity( Gravity.CENTER);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Drawable d = new ColorDrawable( Color.parseColor("#CC000000"));
        LastTxnDialog.getWindow().setBackgroundDrawable(d);
        LastTxnDialog.show();

        TextView tv_last_details = (TextView) LastTxnDialog.findViewById( R.id.tv_last_details);
        Utils.msgText( SaveSparePartApproval.this, "458", tv_last_details);
        ImageView iv_cancel = (ImageView) LastTxnDialog.findViewById( R.id.iv_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                LastTxnDialog.cancel();
            }
        });
        TextView tv_last_date = (TextView) LastTxnDialog.findViewById( R.id.tv_last_date);
        Utils.msgText( SaveSparePartApproval.this, "455", tv_last_date);
        TextView tv_last_tran_type = (TextView) LastTxnDialog.findViewById( R.id.tv_last_tran_type);
        Utils.msgText( SaveSparePartApproval.this, "456", tv_last_tran_type);
        TextView tv_last_id = (TextView) LastTxnDialog.findViewById( R.id.tv_last_id);
        Utils.msgText( SaveSparePartApproval.this, "457", tv_last_id);

        if(response_spare_list.getGetSparePart().get(0).getlSpareDate()!=null){
            tv_last_date.setText( Utils.msg( SaveSparePartApproval.this, "455")+response_spare_list.getGetSparePart().get(0).getlSpareDate());
        }
        if(response_spare_list.getGetSparePart().get(0).getlTransactionType()!=null){
            tv_last_tran_type.setText( Utils.msg( SaveSparePartApproval.this, "456")+response_spare_list.getGetSparePart().get(0).getlTransactionType());
        }
        if(response_spare_list.getGetSparePart().get(0).getlActivityType()!=null){
            tv_last_id.setText( Utils.msg( SaveSparePartApproval.this, "457")+response_spare_list.getGetSparePart().get(0).getlActivityType());
        }
    }

    public void Init()
    {
        if(response_spare_list.getGetSparePart().get(0).getSid()!= null)
        {
            et_site_id.setText(response_spare_list.getGetSparePart().get(0).getSid());
        }
        if(response_spare_list.getGetSparePart().get(0).getTransactionDate()!= null)
        {
            et_transaction_date.setText(response_spare_list.getGetSparePart().get(0).getTransactionDate());
        }
        if(response_spare_list.getGetSparePart().get(0).getTransactionType()!= null)
        {
            et_tran_type.setText(response_spare_list.getGetSparePart().get(0).getTransactionType());
        }
        if(response_spare_list.getGetSparePart().get(0).getaType()!= null)
        {
            et_ticket_id.setText(response_spare_list.getGetSparePart().get(0).getaType());
        }
        if(response_spare_list.getGetSparePart().get(0).getSpareName()!= null)
        {
            txt_spare_name.setText(response_spare_list.getGetSparePart().get(0).getSpareName());
        }
        if(response_spare_list.getGetSparePart().get(0).getQty()!= null)
        {
            txt_qty.setText(response_spare_list.getGetSparePart().get(0).getQty());
        }
        if(response_spare_list.getGetSparePart().get(0).getaQty()!= null)
        {
            et_app_qty.setText(response_spare_list.getGetSparePart().get(0).getaQty());
        }
        if(response_spare_list.getGetSparePart().get(0).getsNo()!= null)
        {
            txt_serial_no.setText(response_spare_list.getGetSparePart().get(0).getsNo());
        }
        if(response_spare_list.getGetSparePart().get(0).getRmks()!= null)
        {
            et_remarks.setText(response_spare_list.getGetSparePart().get(0).getRmks());
        }

        if(response_spare_list.getGetSparePart().get(0).getStatus()!= null)
        {
            if(response_spare_list.getGetSparePart().get(0).getStatus().equalsIgnoreCase("1")
                    && pendingTabRight.contains("A"))
            {
                ll_option.setVisibility(View.VISIBLE);
                bt_submit.setVisibility(View.VISIBLE);
                ll_qty.setVisibility(View.VISIBLE);
                ll_app_qty.setVisibility(View.GONE);
                txt_qty.setBackgroundResource( R.drawable.input_box );
                txt_qty.setEnabled(true);
                et_remarks.setBackgroundResource( R.drawable.input_box );
                et_remarks.setEnabled(true);

            }
            else if(response_spare_list.getGetSparePart().get(0).getStatus().equalsIgnoreCase("2")
                    && pendingTabRight.contains("A"))
            {
                ll_option.setVisibility(View.GONE);
                bt_submit.setVisibility(View.GONE);
                ll_qty.setVisibility(View.GONE);
                ll_app_qty.setVisibility(View.VISIBLE);
                txt_qty.setBackgroundResource( R.drawable.et_data);
                txt_qty.setEnabled(false);
                et_remarks.setBackgroundResource( R.drawable.et_data);
                et_remarks.setEnabled(false);
            }
            else
            {
                ll_option.setVisibility(View.GONE);
                bt_submit.setVisibility(View.GONE);
                ll_qty.setVisibility(View.VISIBLE);
                ll_app_qty.setVisibility(View.GONE);
                txt_qty.setBackgroundResource( R.drawable.et_data);
                txt_qty.setEnabled(false);
                et_remarks.setBackgroundResource( R.drawable.et_data);
                et_remarks.setEnabled(false);
            }
        }
        spareId = response_spare_list.getGetSparePart().get(0).getSpareId();
        approveQty = response_spare_list.getGetSparePart().get(0).getQty();
        prevQty = response_spare_list.getGetSparePart().get(0).getQty();
        if(response_spare_list.getGetSparePart().get(0).getTransactionType().equalsIgnoreCase("Trouble Ticket"))
        {
            tranType = "1";
        }
        else
        {
            tranType = "2";
        }
        userId = mAppPreferences.getUserId();
        remarks = response_spare_list.getGetSparePart().get(0).getRmks();
        et_remarks.setText(remarks);
    }
    public class GetPendingSpareTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String fromDate="",toDate="";
        public GetPendingSpareTask(Context con,String fromDate,String toDate) {
            this.con = con;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url ="";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
                nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
                nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
                nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
                nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
                nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
                nameValuePairs.add(new BasicNameValuePair("vendorID",mAppPreferences.getPIOMEID()));
                nameValuePairs.add(new BasicNameValuePair("siteID",siteId));
                nameValuePairs.add(new BasicNameValuePair("siteAreaType",""));
                nameValuePairs.add(new BasicNameValuePair("tranType","0"));//-- 1 -> TT,2-> PM 0-< all
                nameValuePairs.add(new BasicNameValuePair("tDate",tran_date));
                nameValuePairs.add(new BasicNameValuePair("fDate",tran_date));
                nameValuePairs.add(new BasicNameValuePair("appType",status)); //(1--PENDING,2-APPROVED,3-REJECTED )
                nameValuePairs.add(new BasicNameValuePair("repID","711"));
                nameValuePairs.add(new BasicNameValuePair("txnID",txnId));
                nameValuePairs.add(new BasicNameValuePair("source","M"));
                if(moduleUrl.equalsIgnoreCase("0")){
                    url=mAppPreferences.getConfigIP()+ WebMethods.url_get_spare_part;
                }else{
                    url=moduleUrl+ WebMethods.url_get_spare_part;
                }
                String response = Utils.httpPostRequest(con,url, nameValuePairs);
                Gson gson = new Gson();
                response_spare_list = gson.fromJson(response, BeanSiteSpare.class);
            } catch (Exception e) {
                e.printStackTrace();
                response_spare_list = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            if (response_spare_list == null) {

            } else if (response_spare_list.getGetSparePart()!=null && response_spare_list.getGetSparePart().size() > 0) {
                Init();
            } else {

            }
            super.onPostExecute(result);
        }
    }

    public void alert(final int confirmation,String msgId) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog( SaveSparePartApproval.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource( R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView( R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( SaveSparePartApproval.this ) );
        positive.setTypeface( Utils.typeFace( SaveSparePartApproval.this ) );
        negative.setTypeface( Utils.typeFace( SaveSparePartApproval.this ) );
        title.setTypeface( Utils.typeFace( SaveSparePartApproval.this ) );
        title.setText( Utils.msg( SaveSparePartApproval.this, msgId));
        positive.setText( Utils.msg( SaveSparePartApproval.this, "63"));
        negative.setText( Utils.msg( SaveSparePartApproval.this, "64"));

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                DataBaseHelper db = new DataBaseHelper( SaveSparePartApproval.this );
                db.open();
                actvity_dialog.cancel();
                if(confirmation==1){
                    finish();
                }

                if(confirmation==2){
                    actvity_dialog.cancel();
                    if(alertCount == 0)
                    {
                        alert(2,"501");
                        alertCount = 1;
                        return;
                    }
                    alertCount = 0;
                    ActionSparePart();
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
}
