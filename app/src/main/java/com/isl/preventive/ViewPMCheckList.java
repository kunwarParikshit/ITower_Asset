package com.isl.preventive;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.modal.BeanGetImage;
import com.isl.modal.ChecklistDetail;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.sparepart.SparePart;
import com.isl.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import infozech.itower.R;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewPMCheckList extends Activity implements OnClickListener {
    public ImageLoader loader = ImageLoader.getInstance();
    DisplayImageOptions op;
    List<BeanGetImage> PreimageList, PostimageList;
    LinearLayout linear, linearRemarks, ll_pre_act_photo, ll_post_act_img;
    RelativeLayout linearNoDataFound;
    Button bt_back;
    TextView tvSubmit, tvHeader, tv_no_data, tv_click_img, tv_siteId, tv_assetId,tv_spare_parts, tv_click_pre_img,
            tv_rj_cat, tv_rj_rmk, tv_rj_date, tv_rj_region, tv_projectName, tv_productName, tv_soNumber,
            tv_WorkOrderNumber,
            tv_Region, tv_District, tv_City, tv_Latitude, tv_Longitude, tv_siteTowerType;
    EditText et_site_id,et_assetId,et_rj_cat, et_rj_rmk, et_rj_date, et_projectName, et_productName, et_soNumber, et_WorkOrderNumber,
            et_Region, et_District, et_City, et_Latitude, et_Longitude, et_siteTowerType;

    String scheduledDate, siteId, activityTypeId, dgType, paramName, activityStatus, actStaus, imgUploadFlag,
            sName, txnId, rCat = "", rejRmks = "", rvDate = "";
    AppPreferences mAppPreferences;
    ProgressDialog loadChecklist;
    PackageInfo pInfo = null;//0.3
    private static LinkedHashMap<Integer, ChecklistDetail> hmPmCheklist = new LinkedHashMap<Integer, ChecklistDetail>();
    private static Map<String, TextView> grpList = new HashMap<String, TextView>();
    LayoutParams GalleryParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    ExpandableHeightGridView pre_grid = null, post_grid = null;
    ScrollView scrollView;
    int scroolFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadChecklist = new ProgressDialog(ViewPMCheckList.this);
       //loadChecklist.setMessage("Please Wait Checklist is Loading....");
        //loadChecklist.show();
        try {

            op = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.no_media_default)
                    .showImageForEmptyUri(R.drawable.no_media_default)
                    .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                    .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                    .build();

            setContentView(R.layout.view_pm_checklist_detail);

            scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    loader.init(ImageLoaderConfiguration.createDefault(ViewPMCheckList.this));
                    loader.handleSlowNetwork(true);
                    loader.pause();

                    scroolFlag = 1;

                }

            });


            mAppPreferences = new AppPreferences(ViewPMCheckList.this);
            mAppPreferences.setTrackMode(0);

            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
            }

            GalleryParam.gravity = Gravity.CENTER_HORIZONTAL;
            GalleryParam.setMargins(10, 10, 10, 0);
            //Calling method to find IDs of TextView etc.
            init();
            if (imgUploadFlag.equalsIgnoreCase("1")) {
                setImageActivityLevel();
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addElementsToLayout('P', 0, null);
                }
            }, 100);

            if (getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")) {
                if (Utils.isNetworkAvailable(ViewPMCheckList.this)) {
                    callGetFildsValueApi(txnId);
                }
            }
            bt_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            tv_spare_parts.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        Intent i = new Intent(ViewPMCheckList.this, SparePart.class);
                        i.putExtra("tranID", txnId);
                        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
                        i.putExtra("activityMode", "0");
                        i.putExtra("typeId", activityTypeId);
                        startActivity(i);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            String s = e.getMessage();
            System.out.println("error view list = ==" + s);
            //Toast.makeText(ViewPMCheckList.this, "" + s, Toast.LENGTH_LONG).show();

            //Toast.makeText(ViewPMCheckList.this,"Please Contact iTower helpdesk",Toast.LENGTH_LONG).show();
        }
    }

    // Method to find IDs and getting previous screen Values
    private void init() {
        post_grid = (ExpandableHeightGridView) findViewById(R.id.post_grid);
        pre_grid = (ExpandableHeightGridView) findViewById(R.id.pre_grid);
        ll_pre_act_photo = (LinearLayout) findViewById(R.id.ll_pre_act_photo);
        ll_post_act_img = (LinearLayout) findViewById(R.id.ll_post_act_img);

        PreimageList = new ArrayList<BeanGetImage>();
        PostimageList = new ArrayList<BeanGetImage>();

        hmPmCheklist.clear();
        txnId = getIntent().getExtras().getString("txn");
        paramName = getIntent().getExtras().getString("paramName");
        activityStatus = getIntent().getExtras().getString("Status");
        scheduledDate = getIntent().getExtras().getString("scheduledDate");
        siteId = getIntent().getExtras().getString("siteId");
        activityTypeId = getIntent().getExtras().getString("activityTypeId");
        dgType = getIntent().getExtras().getString("dgType");
        actStaus = getIntent().getExtras().getString("S");
        rCat = getIntent().getExtras().getString("rCat");
        rejRmks = getIntent().getExtras().getString("rejRmks");
        rvDate = getIntent().getExtras().getString("rvDate");
        //1.0
        imgUploadFlag = "2";

        if (dgType == null || dgType.isEmpty()) {
            dgType = "0";
        }


        tv_rj_cat = (TextView) findViewById(R.id.tv_rj_cat);
        tv_rj_rmk = (TextView) findViewById(R.id.tv_rj_rmk);
        tv_rj_date = (TextView) findViewById(R.id.tv_rj_date);
        tv_rj_region = (TextView) findViewById(R.id.tv_rj_region);

        Utils.groupTV(ViewPMCheckList.this, tv_rj_region, Utils.msg(ViewPMCheckList.this, "461"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_rj_cat, Utils.msg(ViewPMCheckList.this, "469"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_rj_rmk, Utils.msg(ViewPMCheckList.this, "470"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_rj_date, Utils.msg(ViewPMCheckList.this, "471"));

        et_rj_cat = (EditText) findViewById(R.id.et_rj_cat);
        Utils.editTextProperty(ViewPMCheckList.this, et_rj_cat);
        et_rj_cat.setEnabled(false);
        et_rj_rmk = (EditText) findViewById(R.id.et_rj_rmk);
        Utils.editTextProperty(ViewPMCheckList.this, et_rj_rmk);
        et_rj_rmk.setEnabled(false);
        et_rj_date = (EditText) findViewById(R.id.et_rj_date);
        Utils.editTextProperty(ViewPMCheckList.this, et_rj_date);
        et_rj_date.setEnabled(false);

        if (actStaus.equalsIgnoreCase("J")) {
            tv_rj_cat.setVisibility(View.VISIBLE);
            tv_rj_rmk.setVisibility(View.VISIBLE);
            tv_rj_date.setVisibility(View.VISIBLE);
            tv_rj_region.setVisibility(View.VISIBLE);
            et_rj_cat.setVisibility(View.VISIBLE);
            et_rj_rmk.setVisibility(View.VISIBLE);
            et_rj_date.setVisibility(View.VISIBLE);
            if (rCat != null) {
                et_rj_cat.setText("" + rCat);
            }
            et_rj_rmk.setVisibility(View.VISIBLE);

            if (rejRmks != null) {
                et_rj_rmk.setText("" + rejRmks);
            }
            et_rj_date.setVisibility(View.VISIBLE);
            if (rvDate != null) {
                et_rj_date.setText("" + rvDate);
            }
        }

        linearNoDataFound = (RelativeLayout) findViewById(R.id.rl_no_data_found);
        linear = (LinearLayout) findViewById(R.id.ll_textview);
        linearRemarks = (LinearLayout) findViewById(R.id.ll_remarks);

        tv_spare_parts = (TextView) findViewById(R.id.tv_spare_parts);
        Utils.msgText(ViewPMCheckList.this, "279", tv_spare_parts);
        tv_spare_parts.setPaintFlags(tv_spare_parts.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvHeader = (TextView) findViewById(R.id.tv_header);

        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")) {
            tv_spare_parts.setVisibility(View.GONE);
            Utils.msgText(ViewPMCheckList.this, "547", tvHeader);
        } else if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            tv_spare_parts.setVisibility(View.GONE);
            Utils.msgText(ViewPMCheckList.this, "548", tvHeader);
        } else {
            tv_spare_parts.setVisibility(View.VISIBLE);
            Utils.msgText(ViewPMCheckList.this, activityTypeId, tvHeader);
        }

        tv_click_img = (TextView) findViewById(R.id.tv_click_img);
        tv_click_pre_img = (TextView) findViewById(R.id.tv_click_pre_img);

        tv_siteId = (TextView) findViewById(R.id.tv_siteId);
        tv_assetId = (TextView) findViewById(R.id.tv_assetId);
        tv_projectName = (TextView) findViewById(R.id.tv_projectName);
        tv_productName = (TextView) findViewById(R.id.tv_productName);
        tv_soNumber = (TextView) findViewById(R.id.tv_soNumber);
        tv_WorkOrderNumber = (TextView) findViewById(R.id.tv_WorkOrderNumber);
        tv_Region = (TextView) findViewById(R.id.tv_Region);
        tv_District = (TextView) findViewById(R.id.tv_District);
        tv_City = (TextView) findViewById(R.id.tv_City);
        tv_Latitude = (TextView) findViewById(R.id.tv_Latitude);
        tv_Longitude = (TextView) findViewById(R.id.tv_Longitude);
        tv_siteTowerType = (TextView) findViewById(R.id.tv_siteTowerType);

        et_site_id = (EditText) findViewById(R.id.et_site_id);
        et_assetId = (EditText) findViewById(R.id.et_assetId);
        et_projectName = (EditText) findViewById(R.id.et_projectName);
        et_productName = (EditText) findViewById(R.id.et_productName);
        et_soNumber = (EditText) findViewById(R.id.et_soNumber);
        et_WorkOrderNumber = (EditText) findViewById(R.id.et_WorkOrderNumber);
        et_Region = (EditText) findViewById(R.id.et_Region);
        et_District = (EditText) findViewById(R.id.et_District);
        et_City = (EditText) findViewById(R.id.et_City);
        et_Latitude = (EditText) findViewById(R.id.et_Latitude);
        et_Longitude = (EditText) findViewById(R.id.et_Longitude);
        et_siteTowerType = (EditText) findViewById(R.id.et_siteTowerType);

        Utils.editTextProperty(ViewPMCheckList.this,et_site_id);
        et_site_id.setEnabled( false );
        Utils.editTextProperty(ViewPMCheckList.this,et_assetId);
        et_assetId.setEnabled( false );

        Utils.editTextProperty(ViewPMCheckList.this, et_projectName);
        et_projectName.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_productName);
        et_productName.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_soNumber);
        et_soNumber.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_WorkOrderNumber);
        et_WorkOrderNumber.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_Region);
        et_Region.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_District);
        et_District.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_City);
        et_City.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_Latitude);
        et_Latitude.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_Longitude);
        et_Longitude.setEnabled(false);
        Utils.editTextProperty(ViewPMCheckList.this, et_siteTowerType);
        et_siteTowerType.setEnabled(false);


        if(mAppPreferences.getSiteNameEnable()==1 && getIntent().getExtras().getString( "siteName")!=null){
            sName="("+getIntent().getExtras().getString( "siteName")+")";
        }else{
            sName="";
        }
        et_site_id.setText(siteId+sName);
        Utils.textViewProperty(ViewPMCheckList.this,tv_siteId,Utils.msg(ViewPMCheckList.this,"77"));
        Utils.textViewProperty(ViewPMCheckList.this,tv_assetId,Utils.msg(ViewPMCheckList.this,"880"));
        Utils.textViewProperty(ViewPMCheckList.this,tv_click_img,Utils.msg(ViewPMCheckList.this,"301"));
        Utils.textViewProperty(ViewPMCheckList.this,tv_click_pre_img,Utils.msg(ViewPMCheckList.this,"299"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_projectName, Utils.msg(ViewPMCheckList.this, "803"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_productName, Utils.msg(ViewPMCheckList.this, "802"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_soNumber, Utils.msg(ViewPMCheckList.this, "804"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_WorkOrderNumber, Utils.msg(ViewPMCheckList.this, "805"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_Region, Utils.msg(ViewPMCheckList.this, "806"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_District, Utils.msg(ViewPMCheckList.this, "807"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_City, Utils.msg(ViewPMCheckList.this, "808"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_Latitude, Utils.msg(ViewPMCheckList.this, "809"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_Longitude, Utils.msg(ViewPMCheckList.this, "810"));
        Utils.textViewProperty(ViewPMCheckList.this, tv_siteTowerType, Utils.msg(ViewPMCheckList.this, "811"));



        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        bt_back = (Button) findViewById(R.id.button_back);
        Utils.msgButton(ViewPMCheckList.this, "71", bt_back);
        Utils.msgText(ViewPMCheckList.this, "226", tv_no_data); // set text General PM
        //Utils.msgText( ViewPMCheckList.this, activityTypeId, tvHeader );
    }

    //Add elements to view.
    public void addElementsToLayout(char parentFlag, int prvfieldId, String fieldIdList) {
        DataBaseHelper dbHelper = new DataBaseHelper(ViewPMCheckList.this);
        dbHelper.open();
        if (fieldIdList != null) {
            fieldIdList = fieldIdList.replace("[", "").replace("]", "");
        }

        Cursor cursor = dbHelper.getReviewerChecklist(activityTypeId, fieldIdList == null ? 0 : 2, fieldIdList);
        StringBuilder imgMsg = new StringBuilder();

        ChecklistDetail pmCheckList;

        TextView textView, tvRemarksLink, capturePrePhoto, capturePostPhoto, tv_divider;
        EditText etViRemark, etReviewRemarks;
        RecyclerView pre_photo_gallery, post_photo_gallery;


        int prvFieldId = 0;
        int viewIndex = 0;
        String prvGroupName = "";
        int nextFieldId = 0;

        if (parentFlag == 'C') {
            prvFieldId = prvfieldId;
            prvGroupName = hmPmCheklist.get(prvfieldId).getGrpName();

            viewIndex = ((ViewGroup) linear).indexOfChild(hmPmCheklist.get(prvfieldId).getTvDivider());
            View view = ((ViewGroup) linear).getChildAt(viewIndex + 1);

            if (view != null) {
                nextFieldId = view.getId();
                //Check if next field is group header. Group header will have field id >=5000. Then get next view from layout
                if (nextFieldId >= 5000) {
                    view = ((ViewGroup) linear).getChildAt(viewIndex + 2);
                    nextFieldId = view.getId();
                }
            }
            viewIndex++;
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {

                imgMsg.setLength(0);

                //Add Header if groupName is R
                if (!prvGroupName.equals(cursor.getString(cursor.getColumnIndex("groupName")))) {

                    if (grpList.containsKey(cursor.getString(cursor.getColumnIndex("groupName")))) {
                        linear.removeView(grpList.get(cursor.getString(cursor.getColumnIndex("groupName"))));
                    }

                    TextView tvGroupName = new TextView(this);
                    tvGroupName.setId(cursor.getInt(cursor.getColumnIndex("fieldId")));

                    linear.addView(Utils.groupTV(ViewPMCheckList.this, tvGroupName, (cursor.getString(cursor.getColumnIndex("groupName")))), viewIndex);
                    viewIndex++;
                    grpList.put(cursor.getString(cursor.getColumnIndex("groupName")), tvGroupName);
                    //continue;
                }

                pmCheckList = new ChecklistDetail();

                //in case first element avoid setting previous field id
                if (prvFieldId != 0) {
                    pmCheckList.setPrvId(prvFieldId);
                }

                pmCheckList.setRemarkMadatory(cursor.getString(cursor.getColumnIndex("rMandatory")).charAt(0));
                pmCheckList.setGrpName(cursor.getString(cursor.getColumnIndex("groupName")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setFieldName(cursor.getString(cursor.getColumnIndex("fieldName")));
                pmCheckList.setVisitorStatus(cursor.getString(cursor.getColumnIndex("status")));
                pmCheckList.setVisitorRemarks(cursor.getString(cursor.getColumnIndex("viRemark")));

                pmCheckList.setReviwerStatus(cursor.getString(cursor.getColumnIndex("rStatus")));
                pmCheckList.setReviwerRemarks(cursor.getString(cursor.getColumnIndex("rRemark")));

                pmCheckList.setId(cursor.getInt(cursor.getColumnIndex("fieldId")));
                pmCheckList.setrFlag(cursor.getString(cursor.getColumnIndex("pFlag")).charAt(0));
                pmCheckList.setDropDownValue(cursor.getString(cursor.getColumnIndex("value")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setLength(cursor.getString(cursor.getColumnIndex("length")));
                pmCheckList.setDataType(cursor.getString(cursor.getColumnIndex("dataType")).charAt(0));
                pmCheckList.setMadatory(cursor.getString(cursor.getColumnIndex("mandatory")));
                pmCheckList.setRemarkMada(cursor.getString(cursor.getColumnIndex("rMandatory")));
                pmCheckList.setPrePhotoConfig(cursor.getString(cursor.getColumnIndex("preImgConfig"))); //0.6
                pmCheckList.setPrePhotoTag(cursor.getString(cursor.getColumnIndex("preImgTag"))); //0.6
                pmCheckList.setPostPhotoTag(cursor.getString(cursor.getColumnIndex("postImgTag")));//0.6
                pmCheckList.setPostPhotoConfig(cursor.getString(cursor.getColumnIndex("postImgConfig")));//0.6
                //pmCheckList.setChildIteams(spiltChild(cursor.getString( cursor.getColumnIndex( "pActivityId"))));

                prvFieldId = pmCheckList.getId();
                prvGroupName = pmCheckList.getGrpName();

                hmPmCheklist.put(pmCheckList.getId(), pmCheckList);

                if (imgUploadFlag.equalsIgnoreCase("2")) {

                    //Add Pre Photo extView if pre photo configured for checklist item
                    if (pmCheckList.getPreMinImg() > -1 && pmCheckList.getPreMaxImg() > -1) {
                        imgMsg.append("pre photo");
                        capturePrePhoto = new TextView(this);
                        capturePrePhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(ViewPMCheckList.this, capturePrePhoto, imgMsg.toString());
                        //pre photo gallery
                        pre_photo_gallery = new RecyclerView(ViewPMCheckList.this);
                        recyclerViewProperty(pre_photo_gallery, pmCheckList.getId() + 100);
                        pmCheckList.setCapturePrePhoto(capturePrePhoto);
                        pmCheckList.setPre_grid(pre_photo_gallery);
                        pmCheckList.setPreImgCounter(1);
                        setImages(1, pmCheckList.getId());
                    }

                    //Add Post Photo TextView if pre photo configured for checklist item
                    if (pmCheckList.getPostMinImg() > -1 && pmCheckList.getPostMaxImg() > -1) {
                        imgMsg.setLength(0);
                        imgMsg.append("post photo");

                        capturePostPhoto = new TextView(this);
                        capturePostPhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(ViewPMCheckList.this, capturePostPhoto, imgMsg.toString());
                        //post photo gallery
                        post_photo_gallery = new RecyclerView(ViewPMCheckList.this);
                        recyclerViewProperty(post_photo_gallery, pmCheckList.getId());
                        pmCheckList.setCapturePostPhoto(capturePostPhoto);
                        pmCheckList.setPost_grid(post_photo_gallery);
                        pmCheckList.setPostImgCounter(1);
                        setImages(2, pmCheckList.getId());
                    }
                }

                //For text view use show checklist name
                textView = new TextView(this);
                textView.setId(pmCheckList.getId());
                Utils.textViewProperty(ViewPMCheckList.this, textView, pmCheckList.getFieldName().trim());
                pmCheckList.setTextView(textView);

                tv_divider = new TextView(this);
                Utils.textViewDivider(ViewPMCheckList.this, tv_divider);
                if (imgUploadFlag.equalsIgnoreCase("2")) {
                    tv_divider.setVisibility(View.VISIBLE);
                } else {
                    tv_divider.setVisibility(View.GONE);
                }

                pmCheckList.setTvDivider(tv_divider);

                // remarks
                tvRemarksLink = new TextView(this);
                etViRemark = new EditText(this);
                remarklinkTV(tvRemarksLink, etViRemark, pmCheckList.getId());

                pmCheckList.setTvRemarksLink(tvRemarksLink);
                pmCheckList.setEtViRemarks(etViRemark);

                if (pmCheckList.getCapturePrePhoto() != null) {
                    linear.addView(pmCheckList.getCapturePrePhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPre_grid(), viewIndex);
                    viewIndex++;
                }

                linear.addView(pmCheckList.getTextView(), viewIndex);
                viewIndex++;

                switch (pmCheckList.getFieldType()) {
                    case 'E':
                        addEditText(pmCheckList, viewIndex);
                        viewIndex++;
                        break;
                    default:
                }

                linear.addView(pmCheckList.getTvRemarksLink(), viewIndex);
                viewIndex++;
                if (actStaus.equalsIgnoreCase("J")) {
                    addEditText1(pmCheckList, viewIndex);
                    viewIndex++;
                }


                if (pmCheckList.getCapturePostPhoto() != null) {
                    linear.addView(pmCheckList.getCapturePostPhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPost_grid(), viewIndex);
                    viewIndex++;
                }

                linear.addView(pmCheckList.getTvDivider(), viewIndex);
                viewIndex++;
                pmCheckList.getTextView().setVisibility(View.VISIBLE);
                pmCheckList.getTvDivider().setVisibility(View.VISIBLE);
                pmCheckList.getTvRemarksLink().setVisibility(View.VISIBLE);
                hmPmCheklist.put(pmCheckList.getId(), pmCheckList);
            }
        }

        if (nextFieldId != 0 && hmPmCheklist.containsKey(nextFieldId)) {
            hmPmCheklist.get(nextFieldId).setPrvId(prvFieldId);
        }
        dbHelper.close();
    }

    // Method to add EditText in form.
    private void addEditText(ChecklistDetail checkListDtl, int viewIndex) {

        final EditText editText = new EditText(this);
        Utils.editTextProperty(ViewPMCheckList.this, editText);


        if (actStaus.equalsIgnoreCase("V")) {
            editText.setText(checkListDtl.getReviwerStatus());
        } else {
            editText.setText(checkListDtl.getVisitorStatus());
        }
        checkListDtl.setFieldCat('E');
        editText.setEnabled(true);
        checkListDtl.getTvRemarksLink().setEnabled(true);
        checkListDtl.setEditText(editText);

        linear.addView(checkListDtl.getEditText(), viewIndex);

        checkListDtl.getEditText().setVisibility(View.VISIBLE);
        checkListDtl.getEditText().setFocusableInTouchMode(false);

        checkListDtl.getEditText().setId(checkListDtl.getId());
        checkListDtl.setVisible(editText.isEnabled());

    }

    // Method to add EditText in form.
    private void addEditText1(ChecklistDetail checkListDtl, int viewIndex) {

        final EditText editText1 = new EditText(this);
        Utils.editTextProperty(ViewPMCheckList.this, editText1);
        String value = checkListDtl.getReviwerRemarks();// getSavedReviewRemarks("" + checkListDtl.getId());
        editText1.setText("Rejected Remarks- " + value);
        checkListDtl.setEditText1(editText1);
        //dataType(checkListDtl);
        linear.addView(checkListDtl.getEditText1(), viewIndex);

        if (value != null){
            if (value.equalsIgnoreCase("") || value.isEmpty()) {
                checkListDtl.getEditText1().setVisibility(View.GONE);
            } else {
                checkListDtl.getEditText1().setVisibility(View.VISIBLE);

            }
       }else {
            checkListDtl.getEditText1().setVisibility(View.GONE);
        }


       /* if (checkListDtl.getFieldType() == 'D') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.calender);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            dateDialog(checkListDtl.getEditText(), checkListDtl.getId(), checkListDtl.getTextView());
        } else if (checkListDtl.getFieldType() == 'Q') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.bar_code);
            checkListDtl.getEditText().setPadding(7, 0, 100, 0);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            qrCode(checkListDtl.getEditText(), checkListDtl.getId());
        } else if (checkListDtl.getFieldType() == 'R') {
            //checkListDtl.getEditText().setEnabled(false);
            checkListDtl.getEditText().setKeyListener(null);
        }*/
    //  checkListDtl.getEditText().setKeyListener(null);
		checkListDtl.getEditText1().

    setTextColor(Color.parseColor("#ff0000"));
    // checkListDtl.getEditText().setBackgroundColor(R.color.orange);
    //checkListDtl.getEditText().setHighlightColor(R.color.color_ff0000);
    //  checkListDtl.getEditText().setId(checkListDtl.getId());
		checkListDtl.getEditText1().

    setClickable(false);
		checkListDtl.getEditText1().

    setEnabled(false);
    //checkListDtl.setVisible(editText.isEnabled());
    //sharePrefenceReviewRemarks(checkListDtl.getEditText().getId(), checkListDtl.getEditText().getText().toString().trim());
}

    //Call this method when hardware back button press
    @Override
    public void onBackPressed() {
        finish();
    }

    /*
        This method is called from 3 places  - Pre Photo, Post Photo & Remarks
     */
    @Override
    public void onClick(final View arg0) {
        arg0.setClickable(false);
        arg0.postDelayed(new Runnable() {
            @Override
            public void run() {
                arg0.setClickable(true);
            }
        }, 200);

        if (arg0 instanceof TextView) {
            int a = ((TextView) arg0).getId();
            RemarkPopup(a);
        }
    }

    public void RemarkPopup(final int id) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ViewPMCheckList.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.remark_popup);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button bt_cancel = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        bt_cancel.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        Utils.msgText(ViewPMCheckList.this, "106", title);


        final EditText et_visitor_status = (EditText) actvity_dialog.findViewById(R.id.et_ip);
        et_visitor_status.setVisibility(View.VISIBLE);
        et_visitor_status.setEnabled(false);

        final EditText et_reviewer_remark = (EditText) actvity_dialog.findViewById(R.id.et_reviewer_remark);
        et_reviewer_remark.setVisibility(View.GONE);
        et_reviewer_remark.setEnabled(false);

        if (actStaus.equalsIgnoreCase("V")) {

            if (hmPmCheklist.get(id).getVisitorStatus() != null &&
                    !hmPmCheklist.get(id).getVisitorStatus().equalsIgnoreCase("null")) {
                et_visitor_status.setText("Value:" + hmPmCheklist.get(id).getVisitorStatus());
            }

            if (hmPmCheklist.get(id).getVisitorRemarks() != null &&
                    !hmPmCheklist.get(id).getVisitorRemarks().equalsIgnoreCase("null")) {
                et_visitor_status.setText(et_visitor_status.getText().toString() + "\n" + "Remarks:" + hmPmCheklist.get(id).getVisitorRemarks());
            }

            et_reviewer_remark.setVisibility(View.VISIBLE);
            if (hmPmCheklist.get(id).getReviwerRemarks() != null &&
                    !hmPmCheklist.get(id).getReviwerRemarks().equalsIgnoreCase("null")) {
                et_reviewer_remark.setText(hmPmCheklist.get(id).getReviwerRemarks());
            }

        } else {
            if (hmPmCheklist.get(id).getVisitorStatus() != null &&
                    !hmPmCheklist.get(id).getVisitorStatus().equalsIgnoreCase("null")) {
                et_visitor_status.setText(hmPmCheklist.get(id).getVisitorRemarks());
            }
        }

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Utils.msgButton(ViewPMCheckList.this, "7", positive);
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }

    public void recyclerViewProperty(RecyclerView rv, int id) {
        rv.setLayoutParams(GalleryParam);
        rv.setBackgroundColor(getResources().getColor(R.color.bg_color_white));
        rv.setId(id);
    }

    public void remarklinkTV(TextView tv, EditText et, int id) {
        Utils.msgText(ViewPMCheckList.this, "106", tv);
        tv.setId(id);
        tv.setOnClickListener(this);
        Utils.remarksLink(ViewPMCheckList.this, tv);
        et.setTextColor(getResources().getColor(R.color.input_textcolor));
        et.setId(id);
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
    List<ViewImage64> imageList = Collections.emptyList();
    Context context;
    int gridId, photoType;

    public HorizontalAdapter(List<ViewImage64> imageList, Context context, int gridId, int photoType) {
        this.imageList = imageList;
        this.context = context;
        this.gridId = gridId;
        this.photoType = photoType;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView delete, grid_image, play_video, doc_image;
        TextView tv_tag, tv_time_stamp, tv_lati, tv_longi;

        public MyViewHolder(View view) {
            super(view);
            grid_image = (ImageView) view.findViewById(R.id.grid_image);
            doc_image = (ImageView) view.findViewById(R.id.grid_image1);
            delete = (ImageView) view.findViewById(R.id.delete);
            play_video = (ImageView) view.findViewById(R.id.play_video);
            tv_lati = (TextView) view.findViewById(R.id.tv_lati);
            tv_longi = (TextView) view.findViewById(R.id.tv_longi);
            tv_tag = (TextView) view.findViewById(R.id.tv_tag);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_img, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.delete.setVisibility(View.GONE);
        holder.delete.setImageResource(R.drawable.delete_icon);
        holder.grid_image.setVisibility(View.GONE);
        holder.doc_image.setVisibility(View.GONE);
        holder.play_video.setVisibility(View.GONE);
        holder.tv_lati.setText(" ");
        holder.tv_longi.setText(" ");
        holder.tv_time_stamp.setText(" ");
        holder.tv_tag.setText(" ");

        if (imageList != null && imageList.size() > 0 && imageList.get(position).getPath() != null) {
            if (imageList.get(position).getName() != null) {
                holder.tv_tag.setText(Utils.msg(ViewPMCheckList.this, "473") + " " + imageList.get(position).getName());
            } else {
                holder.tv_tag.setText(Utils.msg(ViewPMCheckList.this, "473") + " ");
            }

            if (imageList.get(position).getTimeStamp() != null) {
                holder.tv_time_stamp.setText(Utils.msg(ViewPMCheckList.this, "474") + " " + imageList.get(position).getTimeStamp());
            } else {
                holder.tv_time_stamp.setText(Utils.msg(ViewPMCheckList.this, "474") + " ");
            }

            if (imageList.get(position).getLati() != null) {
                holder.tv_lati.setText(Utils.msg(ViewPMCheckList.this, "215") + " : " + imageList.get(position).getLati());
            } else {
                holder.tv_lati.setText(Utils.msg(ViewPMCheckList.this, "215") + " : ");
            }

            if (imageList.get(position).getLongi() != null) {
                holder.tv_longi.setText(Utils.msg(ViewPMCheckList.this, "216") + " : " + imageList.get(position).getLongi());
            } else {
                holder.tv_longi.setText(Utils.msg(ViewPMCheckList.this, "216") + " : ");
            }

            Bitmap bm = null;
            String path = imageList.get(position).getPath();
            File isfile = new File(path);

            if (isfile.exists() && !path.contains("http")) {
                if (path.contains(".jpeg") || path.contains(".JPEG")
                        || path.contains(".jpg") || path.contains(".JPG")
                        || path.contains(".png") || path.contains(".PNG")) {
                    bm = Utils.decodeFile(path);
                    holder.play_video.setTag("1");
                    holder.delete.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                } else if (path.contains(".mp4") || path.contains(".MP4")) {
                    bm = Utils.createVideoThumbNail(path);
                    holder.play_video.setTag("2");
                    holder.delete.setVisibility(View.GONE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.stop_video);
                } else if (path.contains(".doc") || path.contains(".DOC")
                        || path.contains(".txt") || path.contains(".TXT")
                        || path.contains(".pdf") || path.contains(".PDF")
                        || path.contains(".xlsx") || path.contains(".XLSX")
                        || path.contains(".pptx") || path.contains(".PPTX")
                        || path.contains(".xls") || path.contains(".XLS")
                        || path.contains(".ppt") || path.contains(".PPT")
                        || path.contains(".csv") || path.contains(".CSV")) {
                    holder.play_video.setTag("4");
                    holder.delete.setVisibility(View.GONE);
                    holder.doc_image.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.GONE);
                    if (path.contains(".doc") || path.contains(".DOC")) {
                        holder.doc_image.setImageResource(R.drawable.image);
                    } else if (path.contains(".txt") || path.contains(".TXT")) {
                        holder.doc_image.setImageResource(R.drawable.image_txt);
                    } else if (path.contains(".pdf") || path.contains(".PDF")) {
                        holder.doc_image.setImageResource(R.drawable.image_pdf);
                    } else if (path.contains(".xlsx") || path.contains(".XLSX") || path.contains(".xls") || path.contains(".XLS")) {
                        holder.doc_image.setImageResource(R.drawable.image_xls);
                    } else if (path.contains(".pptx") || path.contains(".PPTX") || path.contains(".ppt") || path.contains(".PPT")) {
                        holder.doc_image.setImageResource(R.drawable.image_ppt);
                    } else if (path.contains(".csv") || path.contains(".CSV")) {
                        holder.doc_image.setImageResource(R.drawable.image_csv);
                    } else {
                        holder.doc_image.setImageResource(R.drawable.reports);
                    }
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                    //loader.init( ImageLoaderConfiguration.createDefault( context ) );
                    //loader.displayImage( path, holder.grid_image, op, null );
                }
                if (bm != null) {
                    holder.grid_image.setImageBitmap(bm);
                } else {
                    holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                }
            } else if (path.contains("http")) {
                if (path.contains(".jpeg") || path.contains(".JPEG")
                        || path.contains(".jpg") || path.contains(".JPG")
                        || path.contains(".png") || path.contains(".PNG")) {
                    holder.play_video.setTag("3");
                    holder.delete.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                  //  loader.init(ImageLoaderConfiguration.createDefault(context));
                        Picasso.with(ViewPMCheckList.this).load(imageList.get(position)
                                .getPath()).into(holder.grid_image);

                   // loader.displayImage(path, holder.grid_image, op, null);
                } else if (path.contains(".mp4") || path.contains(".MP4")) {
                    holder.play_video.setTag("4");
                    holder.delete.setVisibility(View.GONE);
                    holder.grid_image.setVisibility(View.VISIBLE);
                    holder.doc_image.setVisibility(View.GONE);
                    holder.grid_image.setBackgroundColor(Color.parseColor("#000000"));
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.stop_video);
                } else if (path.contains(".doc") || path.contains(".DOC")
                        || path.contains(".txt") || path.contains(".TXT")
                        || path.contains(".pdf") || path.contains(".PDF")
                        || path.contains(".xlsx") || path.contains(".XLSX")
                        || path.contains(".pptx") || path.contains(".PPTX")
                        || path.contains(".xls") || path.contains(".XLS")
                        || path.contains(".ppt") || path.contains(".PPT")
                        || path.contains(".csv") || path.contains(".CSV")) {
                    holder.play_video.setTag("4");
                    holder.delete.setVisibility(View.GONE);
                    holder.doc_image.setVisibility(View.VISIBLE);
                    holder.grid_image.setVisibility(View.GONE);
                    if (path.contains(".doc") || path.contains(".DOC")) {
                        holder.doc_image.setImageResource(R.drawable.image);
                    } else if (path.contains(".txt") || path.contains(".TXT")) {
                        holder.doc_image.setImageResource(R.drawable.image_txt);
                    } else if (path.contains(".pdf") || path.contains(".PDF")) {
                        holder.doc_image.setImageResource(R.drawable.image_pdf);
                    } else if (path.contains(".xlsx") || path.contains(".XLSX") || path.contains(".xls") || path.contains(".XLS")) {
                        holder.doc_image.setImageResource(R.drawable.image_xls);
                    } else if (path.contains(".pptx") || path.contains(".PPTX") || path.contains(".ppt") || path.contains(".PPT")) {
                        holder.doc_image.setImageResource(R.drawable.image_ppt);
                    } else if (path.contains(".csv") || path.contains(".CSV")) {
                        holder.doc_image.setImageResource(R.drawable.image_csv);
                    } else {
                        holder.doc_image.setImageResource(R.drawable.reports);
                    }
                    holder.play_video.setVisibility(View.VISIBLE);
                    holder.play_video.setImageResource(R.drawable.fullview);
                    //loader.init( ImageLoaderConfiguration.createDefault( context ) );
                    //loader.displayImage( path, holder.grid_image, op, null );
                }
            }
        }

        holder.play_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               mediaView(holder.play_video.getTag().toString(),
                 imageList.get(position).getPath());
            }
        });


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

}

    public void setImages(int prePostType, int iddd) {
        DataBaseHelper db30 = new DataBaseHelper(ViewPMCheckList.this);
        db30.open();
        Cursor cursor = db30.getChecklistItemImages(prePostType, iddd, dgType, scheduledDate,
                activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));

        List<ViewImage64> imgViewList = null;

        if (hmPmCheklist.get(iddd).getCapturePrePhoto() != null) {
            hmPmCheklist.get(iddd).getCapturePrePhoto().setVisibility(View.GONE);
        }
        if (hmPmCheklist.get(iddd).getCapturePostPhoto() != null) {
            hmPmCheklist.get(iddd).getCapturePostPhoto().setVisibility(View.GONE);
        }

        if (cursor != null) {
            imgViewList = new ArrayList<ViewImage64>();
            hmPmCheklist.get(iddd).getPreImgName().clear();
            hmPmCheklist.get(iddd).getPostImgName().clear();

            int imgCounter = 1;

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex("IMAGE_PATH"));
                String tag = cursor.getString(cursor.getColumnIndex("IMG_TAG"));
                String time = cursor.getString(cursor.getColumnIndex("TIME"));
                String imgname = cursor.getString(cursor.getColumnIndex("IMG_NAME"));
                String lati = cursor.getString(cursor.getColumnIndex("LATI"));
                String longi = cursor.getString(cursor.getColumnIndex("LONGI"));
                ViewImage64 viewImg = null;

                if (path != null) {
                    viewImg = new ViewImage64();
                    viewImg.setTimeStamp(time);
                    viewImg.setName(tag);
                    viewImg.setPath(path);
                    viewImg.setLati(lati);
                    viewImg.setLongi(longi);
                    imgViewList.add(viewImg);
                    if (prePostType == 1) {
                        hmPmCheklist.get(iddd).getPreImgName().add(imgname);
                    } else {
                        hmPmCheklist.get(iddd).getPostImgName().add(imgname);
                    }
                    imgCounter++;

                }
            }

            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(imgViewList, ViewPMCheckList.this, iddd, prePostType);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ViewPMCheckList.this, LinearLayoutManager.HORIZONTAL, false);

            if (prePostType == 1) {
                if (hmPmCheklist.get(iddd).getCapturePrePhoto() != null && imgViewList.size() > 0) {
                    //hmPmCheklist.get(iddd).getCapturePrePhoto().setVisibility(View.VISIBLE);
                }
                hmPmCheklist.get(iddd).getPre_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).getPre_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).setPreImgCounter(imgCounter);
            } else {
                if (hmPmCheklist.get(iddd).getCapturePostPhoto() != null && imgViewList.size() > 0) {
                    //hmPmCheklist.get(iddd).getCapturePostPhoto().setVisibility(View.VISIBLE);
                }
                hmPmCheklist.get(iddd).getPost_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).getPost_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).setPostImgCounter(imgCounter);
            }
        }
        db30.close();
    }

    private void setImageActivityLevel() {
        op = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.no_media_default)
                .showImageForEmptyUri(R.drawable.no_media_default)
                .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                .build();

        DataBaseHelper db30 = new DataBaseHelper(ViewPMCheckList.this);
        db30.open();
        Cursor preCursor = db30.getChecklistItemImages(1, 0, dgType,
                scheduledDate, activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));
        if (preCursor != null && preCursor.getCount() > 0) {
            while (preCursor.moveToNext()) {
                String path = preCursor.getString(preCursor.getColumnIndex("IMAGE_PATH"));
                String tag = preCursor.getString(preCursor.getColumnIndex("IMG_TAG"));
                String time = preCursor.getString(preCursor.getColumnIndex("TIME"));
                String lati = preCursor.getString(preCursor.getColumnIndex("LATI"));
                String longi = preCursor.getString(preCursor.getColumnIndex("LONGI"));

                BeanGetImage preimage = new BeanGetImage();
                preimage.setPreImgPath(path);
                preimage.setPreImgName(tag);
                preimage.setPreImgTimeStamp(time);
                preimage.setLATITUDE(lati);
                preimage.setLONGITUDE(longi);
                PreimageList.add(preimage);
            }
            ll_pre_act_photo.setVisibility(View.VISIBLE);
            pre_grid.setFastScrollEnabled(true);
            pre_grid.setAdapter(new PreItemAdapter(ViewPMCheckList.this, PreimageList, op));
            pre_grid.setExpanded(true);
        }

        Cursor postCursor = db30.getChecklistItemImages(2, 0, dgType,
                scheduledDate, activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));
        if (postCursor != null && postCursor.getCount() > 0) {
            while (postCursor.moveToNext()) {
                String path = postCursor.getString(preCursor.getColumnIndex("IMAGE_PATH"));
                String tag = postCursor.getString(preCursor.getColumnIndex("IMG_TAG"));
                String time = postCursor.getString(preCursor.getColumnIndex("TIME"));
                String lati = postCursor.getString(preCursor.getColumnIndex("LATI"));
                String longi = postCursor.getString(preCursor.getColumnIndex("LONGI"));

                BeanGetImage postimage = new BeanGetImage();
                postimage.setIMAGE_PATH(path);
                postimage.setIMAGENAME(tag);
                postimage.setImgTimeStamp(time);
                postimage.setLATITUDE(lati);
                postimage.setLONGITUDE(longi);
                PostimageList.add(postimage);
            }
            ll_post_act_img.setVisibility(View.VISIBLE);
            post_grid.setFastScrollEnabled(true);
            post_grid.setAdapter(new ItemAdapter(ViewPMCheckList.this, PostimageList, op));
            post_grid.setExpanded(true);
        }
        db30.close();
    }

    public void mediaView(String flag, String urlPath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (flag.equals("1")) {
            final Dialog nagDialog = new Dialog(ViewPMCheckList.this,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(true);
            nagDialog.setContentView(R.layout.image_zoom);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(nagDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes(lp);
            Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
            ImageView imageView = (ImageView) nagDialog.findViewById(R.id.imageView1);
            Bitmap bm = Utils.decodeFile(urlPath);
            if (bm != null) {
                imageView.setImageBitmap(bm);
            } else {
                imageView.setBackgroundColor(Color.parseColor("#000000"));
            }
            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        } else if (flag.equals("2")) {
            Intent i = new Intent(ViewPMCheckList.this, ViewVideoVideoView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        } else if (flag.equals("3")) {
            final Dialog nagDialog = new Dialog(ViewPMCheckList.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setCancelable(true);
            nagDialog.setContentView(R.layout.image_zoom);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(nagDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            nagDialog.getWindow().setAttributes(lp);
            Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
            ImageView imageView = (ImageView) nagDialog.findViewById(R.id.imageView1);
            Picasso.with(ViewPMCheckList.this).load(urlPath).into(imageView);
         //   loader.init(ImageLoaderConfiguration.createDefault(ViewPMCheckList.this));
          //  loader.displayImage(urlPath, imageView, op, null);
            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        } else if (flag.equals("4")) {
            Intent i = new Intent(ViewPMCheckList.this, ViewVideoWebView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        }
    }

    //Api for get Get PM Data with retrofit
    private void callGetFildsValueApi(String getTxnList) {
        ProgressDialog progressDialog = new ProgressDialog(ViewPMCheckList.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        int txnId1 = Integer.parseInt(txnId);
        Call<GetTxnListResponce> call = request.GetPMFildsData(txnId1);
        //Call<GetTxnListResponce> call = request.GetPMFildsData(4035693);
        call.enqueue(new Callback<GetTxnListResponce>() {
            @Override
            public void onResponse(Call<GetTxnListResponce> call, retrofit2.Response<GetTxnListResponce> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //  Toast.makeText(PMChecklist.this, ""+response.body(), Toast.LENGTH_SHORT).show();
                if (response.body() != null) {
                    //UAT 894 observation (hide below 5 field)
                    et_projectName.setVisibility(View.GONE);
                    et_productName.setVisibility(View.GONE);
                    et_soNumber.setVisibility(View.GONE);
                    et_WorkOrderNumber.setVisibility(View.GONE);
                    et_siteTowerType.setVisibility(View.GONE);

                    et_Region.setVisibility(View.VISIBLE);
                    et_District.setVisibility(View.VISIBLE);
                    et_City.setVisibility(View.VISIBLE);
                    et_Latitude.setVisibility(View.VISIBLE);
                    et_Longitude.setVisibility(View.VISIBLE);
                    et_assetId.setVisibility(View.VISIBLE);
                    et_Region.setText(response.body().getRegion());
                    et_District.setText(response.body().getDistrict());
                    et_City.setText(response.body().getCity());
                    et_Latitude.setText(response.body().getLatitude());
                    et_Longitude.setText(response.body().getLongitude());
                    et_siteTowerType.setText(response.body().getSiteTowerType());

                    if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                            || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
                        et_assetId.setText(getIntent().getExtras().getString("qrCode"));
                    }else {
                        et_assetId.setText(response.body().getAssetId());
                    }

                    /*if(!getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")){
                        et_assetId.setText(getIntent().getExtras().getString("assetId"));
                    }else {
                        et_assetId.setText(response.body().getAssetId());
                    }*/



                    if (response.body().getSONumber() != null) {
                        try {
                            JSONObject json = new JSONObject(response.body().getSONumber());
                            System.out.println("json to string"+json.toString());
                            Iterator<String> keys = json.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (key.equalsIgnoreCase("wONumber")){
                                    et_WorkOrderNumber.setText(json.getString("wONumber"));
                                }else if (key.equalsIgnoreCase("productName")) {
                                    et_productName.setText("" + json.getString("productName"));
                                }else if (key.equalsIgnoreCase("projectName")) {
                                    et_projectName.setText("" + json.getString("projectName"));
                                }else if (key.equalsIgnoreCase("sONumber")) {
                                    et_soNumber.setText("" + json.getString("sONumber"));
                                }
                                // Toast.makeText(PMChecklist.this, ""+key, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            System.out.println("Excaption SONumber"+e);
                            //  Toast.makeText(PMChecklist.this, ""+e, Toast.LENGTH_SHORT).show();
                        }

                    }
                    //UAT 894 observation (hide below 5 field)
                    tv_projectName.setVisibility(View.GONE);
                    tv_productName.setVisibility(View.GONE);
                    tv_soNumber.setVisibility(View.GONE);
                    tv_WorkOrderNumber.setVisibility(View.GONE);
                    tv_siteTowerType.setVisibility(View.GONE);

                    tv_Region.setVisibility(View.VISIBLE);
                    tv_District.setVisibility(View.VISIBLE);
                    tv_City.setVisibility(View.VISIBLE);
                    tv_Latitude.setVisibility(View.VISIBLE);
                    tv_Longitude.setVisibility(View.VISIBLE);
                    tv_assetId.setVisibility(View.VISIBLE);
                } else {
                    //UAT 894 observation (hide below 5 field)
                    et_projectName.setVisibility(View.GONE);
                    et_productName.setVisibility(View.GONE);
                    et_soNumber.setVisibility(View.GONE);
                    et_WorkOrderNumber.setVisibility(View.GONE);
                    et_siteTowerType.setVisibility(View.GONE);

                    et_Region.setVisibility(View.VISIBLE);
                    et_District.setVisibility(View.VISIBLE);
                    et_City.setVisibility(View.VISIBLE);
                    et_Latitude.setVisibility(View.VISIBLE);
                    et_Longitude.setVisibility(View.VISIBLE);
                    et_assetId.setVisibility(View.VISIBLE);
                    if(!getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                        && !getIntent().getExtras().getString("assetId").equalsIgnoreCase("0")){
                        et_assetId.setText(getIntent().getExtras().getString("qrCode"));
                    }
                    //UAT 894 observation (hide below 5 field)
                    tv_projectName.setVisibility(View.GONE);
                    tv_productName.setVisibility(View.GONE);
                    tv_soNumber.setVisibility(View.GONE);
                    tv_WorkOrderNumber.setVisibility(View.GONE);
                    tv_siteTowerType.setVisibility(View.GONE);

                    tv_Region.setVisibility(View.VISIBLE);
                    tv_District.setVisibility(View.VISIBLE);
                    tv_City.setVisibility(View.VISIBLE);
                    tv_Latitude.setVisibility(View.VISIBLE);
                    tv_Longitude.setVisibility(View.VISIBLE);
                    tv_assetId.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<GetTxnListResponce> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //UAT 894 observation (hide below 5 field)
                et_projectName.setVisibility(View.GONE);
                et_productName.setVisibility(View.GONE);
                et_soNumber.setVisibility(View.GONE);
                et_WorkOrderNumber.setVisibility(View.GONE);
                et_siteTowerType.setVisibility(View.GONE);

                et_Region.setVisibility(View.VISIBLE);
                et_District.setVisibility(View.VISIBLE);
                et_City.setVisibility(View.VISIBLE);
                et_Latitude.setVisibility(View.VISIBLE);
                et_Longitude.setVisibility(View.VISIBLE);
                et_assetId.setVisibility(View.VISIBLE);
                if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                        || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
                    et_assetId.setText(getIntent().getExtras().getString("qrCode"));
                }

                //UAT 894 observation (hide below 5 field)
                tv_projectName.setVisibility(View.GONE);
                tv_productName.setVisibility(View.GONE);
                tv_soNumber.setVisibility(View.GONE);
                tv_WorkOrderNumber.setVisibility(View.GONE);
                tv_siteTowerType.setVisibility(View.GONE);

                tv_Region.setVisibility(View.VISIBLE);
                tv_District.setVisibility(View.VISIBLE);
                tv_City.setVisibility(View.VISIBLE);
                tv_Latitude.setVisibility(View.VISIBLE);
                tv_Longitude.setVisibility(View.VISIBLE);
                tv_assetId.setVisibility(View.VISIBLE);
                //Toast.makeText(PMChecklist.this, "" + t, Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }
}