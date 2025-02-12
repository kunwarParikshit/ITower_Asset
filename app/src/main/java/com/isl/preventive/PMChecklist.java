package com.isl.preventive;
/*Modified By : Dhakan lal sharma
 Modified On : 21-June-2017
 Version     : 0.5
 Purpose     : a.Check auto date & time
 b.Image capture time
 c.checklist hirungu  aa 192.168.0.223:1002

 Modified By : Dhakan lal sharma
 Modified On : 18-Mar-2024
 Version     : 0.6
 Purpose     : CR#1158

 Modified By : Dhakan lal sharma
 Modified On : 20-May-2024
 Version     : 0.7
 Purpose     : CR#890

  Modified By : Dhakan lal sharma
 Modified On : 24-May-2024
 Version     : 0.8
 Purpose     : CR#894

 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.isl.SingleSelectSearchable.SearchableSpinner;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.ExpandableHeightGridView;
import com.isl.itower.GPSTracker;
import com.isl.modal.ChecklistDetail;
import com.isl.modal.MediaInfo;
import com.isl.modal.Response;
import com.isl.photo.camera.ViewImage64;
import com.isl.photo.camera.ViewVideoVideoView;
import com.isl.photo.camera.ViewVideoWebView;
import com.isl.sparepart.SparePart;
import com.isl.userTracking.SensorRestarterBroadcastReceiver;
import com.isl.util.CustomRangeInputFilter;
import com.isl.util.FilePathFinder;
import com.isl.util.HttpUtils;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;
import com.isl.videocompressor.MediaController;
import com.isl.workflow.utils.DateTimeUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import infozech.itower.R;
import retrofit2.Call;
import retrofit2.Callback;

public class PMChecklist extends Activity implements OnClickListener, OnItemSelectedListener {
    ArrayAdapter<String> adapter;
    public ImageLoader loader = ImageLoader.getInstance();
    DisplayImageOptions op;
    ProgressDialog loadCheckList;
    private FusedLocationProviderClient fusedLocationClient;

    LinearLayout linear, ll_post_act_img, ll_pre_act_photo, rl_submit;
    RelativeLayout linearNoDataFound;
    Button btnTakePhoto, bt_back, btnTakePrePhoto, btnTakeDoc, btn_pre_browse_doc;
    TextView tvSubmit, tvHeader, tv_no_data, tv_click_img, tv_siteId, tv_spare_parts, tv_click_pre_img,
            tv_rj_cat, tv_rj_rmk, tv_rj_date, tv_rj_region, tv_projectName, tv_productName, tv_soNumber, tv_WorkOrderNumber,
            tv_Region, tv_District, tv_City, tv_Latitude, tv_Longitude, tv_siteTowerType,tv_assetId;
    EditText et_site_id,et_assetId, et_rj_cat, et_rj_rmk, et_rj_date, et_projectName, et_productName, et_soNumber, et_WorkOrderNumber,
            et_Region, et_District, et_City, et_Latitude, et_Longitude, et_siteTowerType;
    boolean txnStatus;
    int imgCounter = 1, preImgCounter = 1, preMinImage = 0, preMaxImage = 0,
            postMinImage = 0, postMaxImage = 0;
    int currFieldId, prePostFlag, imgNumber,finalCalculatedDis = 0000;
    String imgTagName, Imglatitude, Imglongitude, fileType;
    String scheduledDate, siteId, activityTypeId, dgType, paramName, activityStatus,
            latitude, longitude, actStaus,mod_latitude = "",mod_longitude = "",
            sName, txnId, rCat = "", rejRmks = "", rvDate = "",
            enbGeoFen = "", enbActWiseGeoFen = "", enbSitWiseGeoFen = "", defineRadius = "0",
            geoConfiguration  ="",AssetDgType="0";
    JSONObject savedDataJsonObj = null;
    JSONObject savedDataJsonObj1 = null;
    JSONObject savedDataJsonObjRemarks = null;
    JSONObject savedDataJsonObjRemarks1 = null;
    JSONObject savedDataJsonObjReviewRemarks1 = null;
    String moduleUrl = "";
    Bitmap bmp;

    AppPreferences mAppPreferences;
    String[] pmMessage;
    PackageInfo pInfo = null;//0.3
    private static LinkedHashMap<Integer, ChecklistDetail> hmPmCheklist = new LinkedHashMap<Integer, ChecklistDetail>();
    private static Map<String, TextView> grpList = new HashMap<String, TextView>();
    LinkedHashMap<String, ViewImage64> lhm_preImages = new LinkedHashMap<String, ViewImage64>();
    LinkedHashMap<String, ViewImage64> lhm_postImages = new LinkedHashMap<String, ViewImage64>();

    LayoutParams GalleryParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    ExpandableHeightGridView pre_grid = null, post_grid = null;

    JSONArray preImgInfoArray, postImgInfoArray, jsonArrStrImg;

    private Uri imageUri;

    public static Map<String, String> validationList;
    public static Map<String, String> prvTxnData;

    private IntentIntegrator qrScan; //qr code scanner object
    ScrollView scrollView;
    int scroolFlag = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCheckList = new ProgressDialog(PMChecklist.this);
        loadCheckList.setMessage("Please Wait Checklist is Loading....");
        //loadCheckList.show();

        try {
            op = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.no_media_default)
                    .showImageForEmptyUri(R.drawable.no_media_default)
                    .showImageOnFail(R.drawable.no_media_default).cacheInMemory()
                    .cacheOnDisc().displayer(new RoundedBitmapDisplayer(1))
                    .build();

            setContentView(R.layout.pm_checklist_form);
            scrollView = (ScrollView) findViewById(R.id.scrollView);

            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    scroolFlag = 1;
                }
            });

            mAppPreferences = new AppPreferences(PMChecklist.this);
            mAppPreferences.setTrackMode(0);


            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                //e.printStackTrace();
            }

            GalleryParam.gravity = Gravity.CENTER_HORIZONTAL;
            GalleryParam.setMargins(10, 10, 10, 0);
            //Calling method to find IDs of TextView etc.
            init();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addElementsToLayout('P', 0, null);
                }
            }, 100);


            // on click of Submit Button
            tvSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    arg0.setClickable(false);
                    arg0.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            arg0.setClickable(true);
                        }
                    }, 200);
                    if (validate()) {
                        submitData();
                       /* if (Utils.isNetworkAvailable(PMChecklist.this)) {
                            callGetFildsValueApi(1);
                        } else {
                           if (et_Latitude.getText().toString().length() > 0
                                    && et_Longitude.getText().toString().length() > 0) {
                                geoFencingInformation(latitude, longitude,
                                        et_Latitude.getText().toString(),
                                        et_Longitude.getText().toString(), 0, 1);
                            } else {
                               submitData();
                            }
                        }*/

                    }
                }
            });

            if (preMaxImage > 0 && mAppPreferences.getPMImageUploadType() == 1) {
                ll_pre_act_photo.setVisibility(View.VISIBLE);
                btnTakePhoto.setEnabled(false);
                tv_spare_parts.setEnabled(false);
            }

            if (postMaxImage > 0 && mAppPreferences.getPMImageUploadType() == 1) {
                ll_post_act_img.setVisibility(View.VISIBLE);
            }

            if (getApplicationContext().getPackageName().equalsIgnoreCase("tawal.com.sa")) {
                if (Utils.isNetworkAvailable(PMChecklist.this)) {
                    //callGetFildsValueApi(0); commit in MAST
                }
            }
            btnTakePhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    GPSTracker gps = new GPSTracker(PMChecklist.this);
                    String Imglatitude = "DefaultLatitude", Imglongitude = "DefaultLongitude";
                    try {
                        if (!Utils.hasPermissions(PMChecklist.this, AppConstants.PERMISSIONS)
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Toast.makeText(PMChecklist.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                        } else if (gps.canGetLocation() == false) {
                            gps.showSettingsAlert();
                        } else {
                            if (imgCounter <= postMaxImage) {
                                if (gps.canGetLocation() == true) {
                                    Imglatitude = String.valueOf(gps.getLatitude());
                                    Imglongitude = String.valueOf(gps.getLongitude());
                                    if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                                            || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                                    } else {
                                        Imglatitude = String.valueOf(gps.getLatitude());
                                        Imglongitude = String.valueOf(gps.getLongitude());
                                    }
                                }

                                if (pmMessage.length >= imgCounter) {
                                    int a = imgCounter - 1;
                                    ImageName(pmMessage[a], -2, 1, imgCounter, Imglatitude, Imglongitude, "jpeg", 0, "");
                                } else {
                                    imageCapture(" ", -2, 1, imgCounter, Imglatitude, Imglongitude, "jpeg");
                                }
                            } else {
                                // Toast.makeText(PMChecklist.this,"Maximum " +
                                // mAppPreferences.getPMmaximage()+
                                // "Images can be uploaded.",Toast.LENGTH_SHORT).show();
                                String s = Utils.msg(PMChecklist.this, "253");
                                s = s + " " + postMaxImage;
                                s = s + " " + Utils.msg(PMChecklist.this, "254");
                                Utils.toastMsg(PMChecklist.this, s);
                            }
                        }
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnTakeDoc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    GPSTracker gps = new GPSTracker(PMChecklist.this);
                    String Imglatitude = "DefaultLatitude", Imglongitude = "DefaultLongitude";
                    try {
                        if (!Utils.hasPermissions(PMChecklist.this, AppConstants.PERMISSIONS)
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Toast.makeText(PMChecklist.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                        } else if (gps.canGetLocation() == false) {
                            gps.showSettingsAlert();
                        } else {
                            if (imgCounter <= postMaxImage) {
                                if (gps.canGetLocation() == true) {
                                    Imglatitude = String.valueOf(gps.getLatitude());
                                    Imglongitude = String.valueOf(gps.getLongitude());
                                    if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                                            || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                                    } else {
                                        Imglatitude = String.valueOf(gps.getLatitude());
                                        Imglongitude = String.valueOf(gps.getLongitude());
                                    }
                                }

                                if (pmMessage.length >= imgCounter) {
                                    int a = imgCounter - 1;
                                    ImageName(pmMessage[a], -2, 1, imgCounter, Imglatitude, Imglongitude, "doc", 0, "");
                                } else {
                                    imageCapture(" ", -2, 1, imgCounter, Imglatitude, Imglongitude, "doc");
                                }
                            } else {
                                // Toast.makeText(PMChecklist.this,"Maximum " +
                                // mAppPreferences.getPMmaximage()+
                                // "Images can be uploaded.",Toast.LENGTH_SHORT).show();
                                String s = Utils.msg(PMChecklist.this, "253");
                                s = s + " " + postMaxImage;
                                s = s + " " + Utils.msg(PMChecklist.this, "254");
                                Utils.toastMsg(PMChecklist.this, s);
                            }
                        }
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnTakePrePhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    GPSTracker gps = new GPSTracker(PMChecklist.this);
                    String Imglatitude = "DefaultLatitude", Imglongitude = "DefaultLongitude", location = "";
                    try {
                        if (!Utils.hasPermissions(PMChecklist.this, AppConstants.PERMISSIONS)
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Toast.makeText(PMChecklist.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login", Toast.LENGTH_LONG).show();
                        } else if (gps.canGetLocation() == false) {
                            gps.showSettingsAlert();
                        } else {
                            if (preImgCounter <= preMaxImage) {
                                if (gps.canGetLocation() == true) {
                                    Imglatitude = String.valueOf(gps.getLatitude());
                                    Imglongitude = String.valueOf(gps.getLongitude());
                                    if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                                            || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                                    } else {
                                        Imglatitude = String.valueOf(gps.getLatitude());
                                        Imglongitude = String.valueOf(gps.getLongitude());
                                    }
                                }


                                if (pmMessage.length >= preImgCounter) {
                                    int a = preImgCounter - 1;
                                    ImageName(pmMessage[a], -1, 2, preImgCounter, Imglatitude, Imglongitude, "jpeg", 0, "");
                                } else {
                                    imageCapture(" ", -1, 2, preImgCounter, Imglatitude, Imglongitude, "jpeg");
                                }
                            } else {
                                // Toast.makeText(PMChecklist.this,"Maximum " +
                                // mAppPreferences.getPMmaximage()+
                                // " Images can be uploaded.",Toast.LENGTH_SHORT).show();
                                String s = Utils.msg(PMChecklist.this, "253");
                                s = s + " " + preMaxImage;
                                s = s + " " + Utils.msg(PMChecklist.this, "254");
                                Utils.toastMsg(PMChecklist.this, s);
                            }
                        }
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            bt_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFinishing()) {
                        //backButtonAlert( 1, "509", "27", "510" );
                        backButtonAlert(2, "291", "63", "64",
                                -1, -1, "", "", "");
                    }
                }
            });

            tv_spare_parts.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        Intent i = new Intent(PMChecklist.this, SparePart.class);
                        i.putExtra("tranID", txnId);
                        i.putExtra("etsSid", getIntent().getExtras().getString("etsSid"));
                        i.putExtra("activityMode", "2");
                        i.putExtra("typeId", activityTypeId);
                        startActivity(i);
                    } catch (Exception e) {
                        //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PMChecklist.this, "Please Contact iTower helpdesk", Toast.LENGTH_LONG).show();
        }
    }

    public void submitData() {
        try {
            String addParams = "";
            String preImgCon = "";
            String postImgCon = "";
            if (validate()) {

                if (checklist().length() == 0) {
                    Toast.makeText(PMChecklist.this, "Checklist not saved, Please resubmit it.", Toast.LENGTH_LONG).show();
                    return;
                }

                String current_time = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());
                JSONObject obj = new JSONObject();


                if (mAppPreferences.getPMImageUploadType() == 2) {
                    DataBaseHelper db8 = new DataBaseHelper(PMChecklist.this);
                    db8.open();
                    obj.put(AppConstants.PRE_IMG, db8.getImageparameter
                            (1, dgType, scheduledDate, activityTypeId, siteId, txnId,
                                    getIntent().getExtras().getString("assetId")));
                    obj.put(AppConstants.POST_IMG, db8.getImageparameter
                            (2, dgType, scheduledDate, activityTypeId, siteId, txnId,
                                    getIntent().getExtras().getString("assetId")));
                    preImgCon = db8.getImageInfoArr
                            (1, dgType, scheduledDate, activityTypeId, siteId, txnId,
                                    getIntent().getExtras().getString("assetId"));
                    postImgCon = db8.getImageInfoArr
                            (2, dgType, scheduledDate, activityTypeId, siteId, txnId,
                                    getIntent().getExtras().getString("assetId"));
                    db8.close();
                } else {
                    obj.put(AppConstants.PRE_IMG, "");
                    obj.put(AppConstants.POST_IMG, "");
                    preImgCon = preImgInfoArray.toString();
                    postImgCon = postImgInfoArray.toString();
                }

                obj.put(AppConstants.REJECTION_REMARKS, "");
                obj.put(AppConstants.REJECTION_CATEGORY, "");
                obj.put(AppConstants.UPLOAD_TYPE, "PM");
                obj.put(AppConstants.USER_ID_ALIAS, mAppPreferences.getUserId());
                obj.put(AppConstants.ROLE_ID, mAppPreferences.getRoleId());
                obj.put(AppConstants.SITE_ID_ALIAS, siteId.toUpperCase());
                obj.put(AppConstants.ACTIVITY_TYPE_ID, activityTypeId);
                obj.put(AppConstants.STATUS_ALIAS, activityStatus);
                obj.put(AppConstants.SCHEDULE_DATE, scheduledDate);
                obj.put(AppConstants.DG, dgType);

                obj.put("assetId",getIntent().getExtras().getString("assetId"));
                obj.put("equipmentId",getIntent().getExtras().getString("equipmentId"));

                obj.put("isRating", "");
                obj.put("photoRating", "");
                obj.put("pmRating", "");
                obj.put("visualCheck1", "");
                obj.put("visualCheck2", "");
                obj.put("visualCheck3", "");


                if (checklist().length() == 0) {
                    obj.put(AppConstants.CHECK_LIST, mAppPreferences.getPMchecklist());
                } else {
                    obj.put(AppConstants.CHECK_LIST, checklist());
                }

                obj.put(AppConstants.CURRENT_TIME, current_time);

                if(mod_latitude.length()==0 && mod_longitude.length()==0) {
                    obj.put("lat", latitude);
                    obj.put("long", longitude);
                }else{
                    obj.put("lat", mod_latitude);
                    obj.put("long", mod_longitude);
                }

                obj.put(AppConstants.REMARKS, txnId);
                String module = "PM";
                String tktId = "";
                if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")
                        || mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
                    module = "H";
                    tktId = txnId;
                }
                addParams = "appversion=" + pInfo.versionName + "~oper=D"
                        + "~moduleType=" + module + "~tktId=" + tktId
                        +"~geoConfig="+geoConfiguration
                        +"~distance="+finalCalculatedDis;
                obj.put(AppConstants.ADD_PARAM, addParams);
                obj.put(AppConstants.LANGUAGE_CODE, mAppPreferences.getLanCode());

                if (Utils.isNetworkAvailable(PMChecklist.this)
                        && mAppPreferences.getSavePMBackgroundEnable() == 0) {

                    //Check if data submit in background is enabled or not.
                    AsynTaskService task = new AsynTaskService(obj.toString(), preImgCon, postImgCon);
                    task.execute(moduleUrl + WebMethods.url_SaveAPI);
                } else {
                    //If Network is not available the store data in local database
                    DataBaseHelper db = new DataBaseHelper(PMChecklist.this);
                    db.open();
                    db.insertTxnDataLocally(txnId+getIntent().getExtras().getString("assetId")
                            ,"2", obj.toString(),
                            jsonArrStrImg.toString(), preImgCon, postImgCon, mAppPreferences.getUserId(),
                            moduleUrl + WebMethods.url_SaveAPI);
                    db.close();
                    Utils.toast(PMChecklist.this, "66");
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PMChecklist.this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            //Toast.makeText(PMChecklist.this,"Please Contact iTower helpdesk",Toast.LENGTH_LONG).show();
        }
    }

    public void ImageName(final String tag, final int fieldId, final int prePostFlag, final int imgCounter,
                          final String Imglatitude, final String Imglongitude, final String mediaFile,
                          final int flag, final String size) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(PMChecklist.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.checkbox);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setTypeface(Utils.typeFace(PMChecklist.this));
        if (flag == 0 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(PMChecklist.this, "818") + " " + tag);
        } else if (flag == 1 && mediaFile.equalsIgnoreCase("mp4")) {
            title.setText(Utils.msg(PMChecklist.this, "818"));
        } else if (mediaFile.contains("doc") || mediaFile.contains("DOC")
                || mediaFile.contains("txt") || mediaFile.contains("TXT")
                || mediaFile.contains("pdf") || mediaFile.contains("PDF")
                || mediaFile.contains("xlsx") || mediaFile.contains("XLSX")
                || mediaFile.contains("pptx") || mediaFile.contains("PPTX")
                || mediaFile.contains("xls") || mediaFile.contains("XLS")
                || mediaFile.contains("ppt") || mediaFile.contains("PPT")
                || mediaFile.contains("csv") || mediaFile.contains("CSV")) {
            title.setText(Utils.msg(PMChecklist.this, "801"));
        } else if (mediaFile.contains("SP") || mediaFile.contains("sp")
                || mediaFile.contains("Sp")) {
            title.setText(Utils.msg(PMChecklist.this, "812") + " " + tag);
        } else if (mediaFile.contains("mp4") || mediaFile.contains("MP4")) {
            title.setText(Utils.msg(PMChecklist.this, "818") + " " + tag);
        } else {
            title.setText(Utils.msg(PMChecklist.this, "817") + " " + tag);
            /*title.setText(Utils.msg(PMChecklist.this, "228") + " " + tag + " " + Utils.msg(PMChecklist.this, "165").
                    replaceAll("s", ""));*/
        }
        //title.setText( Utils.msg( PMChecklist.this, "228" ) + " " + tag);
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(PMChecklist.this));
        positive.setText(Utils.msg(PMChecklist.this, "7"));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                imageCapture(tag, fieldId, prePostFlag, imgCounter, Imglatitude, Imglongitude, mediaFile);

            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(PMChecklist.this));
        negative.setText(Utils.msg(PMChecklist.this, "8"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

/*

    	ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
			new ActivityResultCallback<Uri>() {
				@Override
				public void onActivityResult(Uri uri) {
					// Handle the returned Uri
					Toast.makeText(PMChecklist.this, ""+uri, Toast.LENGTH_SHORT).show();
				}
			});
*/

    // Method to find IDs and getting previous screen Values
    private void init() {

        if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
            AssetDgType = getIntent().getExtras().getString("AssetDgType");
        }

        txnId = getIntent().getExtras().getString("txn");

        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklist.this);
        dbHelper.open();

		/*Cursor cursor = dbHelper.getPMChecklist( activityTypeId,0,null,mAppPreferences.getTTModuleSelection());
		int a = cursor.getCount();
		if(cursor==null || (cursor!=null && cursor.getCount()==0)){
			Toast.makeText(PMChecklist.this,"No Checklist found.",Toast.LENGTH_LONG).show();
			finish();
		}*/

        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            moduleUrl = dbHelper.getModuleIP("HealthSafty");
        } else {
            moduleUrl = dbHelper.getModuleIP("Preventive");
        }


        //get Data which is auto saved from mobile db if any using txn id
        String savedDataJson = dbHelper.getAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
        String savedDataJsonRemarks = dbHelper.getAutoSaveRemarks(txnId,getIntent().getExtras().getString("assetId"));
        //Avdhesh Code<----Start
        String savedDataJsonReviewRemarks = dbHelper.getAutoSaveReviewerRemarks(txnId,getIntent().getExtras().getString("assetId"));


        //Check if transaction is present in pending data
        int count = dbHelper.getPendingTxnCount(txnId+getIntent().getExtras().getString("assetId"));
        dbHelper.close();

        txnStatus = count > 0 ? true : false;

        if (savedDataJson != null && savedDataJson.length() > 0) {
            try {
                savedDataJsonObj1 = new JSONObject(savedDataJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            txnStatus = false;
        }

        if (savedDataJsonRemarks != null && savedDataJsonRemarks.length() > 0) {
            try {
                savedDataJsonObjRemarks1 = new JSONObject(savedDataJsonRemarks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Avdhesh Start
        if (savedDataJsonReviewRemarks != null && savedDataJsonReviewRemarks.length() > 0) {
            try {
                savedDataJsonObjReviewRemarks1 = new JSONObject(savedDataJsonReviewRemarks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Avdhesh End
        preImgInfoArray = new JSONArray();
        postImgInfoArray = new JSONArray();
        jsonArrStrImg = new JSONArray();
        post_grid = (ExpandableHeightGridView) findViewById(R.id.post_grid);
        pre_grid = (ExpandableHeightGridView) findViewById(R.id.pre_grid);

        hmPmCheklist.clear();

        String MSG = getIntent().getExtras().getString("imageName");
        if (MSG.length() != 0 && MSG != null) {
            pmMessage = MSG.split("\\~");
        }

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


        tv_rj_cat = (TextView) findViewById(R.id.tv_rj_cat);
        tv_rj_rmk = (TextView) findViewById(R.id.tv_rj_rmk);
        tv_rj_date = (TextView) findViewById(R.id.tv_rj_date);
        tv_rj_region = (TextView) findViewById(R.id.tv_rj_region);

        Utils.groupTV(PMChecklist.this, tv_rj_region, Utils.msg(PMChecklist.this, "461"));
        Utils.textViewProperty(PMChecklist.this, tv_rj_cat, Utils.msg(PMChecklist.this, "469"));
        Utils.textViewProperty(PMChecklist.this, tv_rj_rmk, Utils.msg(PMChecklist.this, "470"));
        Utils.textViewProperty(PMChecklist.this, tv_rj_date, Utils.msg(PMChecklist.this, "471"));

        et_rj_cat = (EditText) findViewById(R.id.et_rj_cat);
        Utils.editTextProperty(PMChecklist.this, et_rj_cat);
        et_rj_cat.setEnabled(false);
        et_rj_rmk = (EditText) findViewById(R.id.et_rj_rmk);
        Utils.editTextProperty(PMChecklist.this, et_rj_rmk);
        et_rj_rmk.setEnabled(false);
        et_rj_date = (EditText) findViewById(R.id.et_rj_date);
        Utils.editTextProperty(PMChecklist.this, et_rj_date);
        et_rj_date.setEnabled(false);

        if (actStaus.equalsIgnoreCase("RS")) {
            tv_rj_cat.setVisibility(View.VISIBLE);
            tv_rj_rmk.setVisibility(View.VISIBLE);
            tv_rj_date.setVisibility(View.VISIBLE);
            tv_rj_region.setVisibility(View.VISIBLE);
            et_rj_cat.setVisibility(View.VISIBLE);
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
        } else {
            tv_rj_cat.setVisibility(View.GONE);
            tv_rj_rmk.setVisibility(View.GONE);
            tv_rj_date.setVisibility(View.GONE);
            et_rj_cat.setVisibility(View.GONE);
            et_rj_rmk.setVisibility(View.GONE);
            et_rj_date.setVisibility(View.GONE);
            tv_rj_region.setVisibility(View.GONE);
        }

        validationList = (HashMap<String, String>) getIntent().getSerializableExtra("valData");
        prvTxnData = (HashMap<String, String>) getIntent().getSerializableExtra("readingData");

        if (dgType == null || dgType.isEmpty()) {
            dgType = "0";
        }
        preMinImage = Integer.parseInt(getIntent().getExtras().getString("preMinImage"));
        preMaxImage = Integer.parseInt(getIntent().getExtras().getString("preMaxImage"));
        postMinImage = Integer.parseInt(getIntent().getExtras().getString("postMinImage"));
        postMaxImage = Integer.parseInt(getIntent().getExtras().getString("postMaxImage"));
        ll_pre_act_photo = (LinearLayout) findViewById(R.id.ll_pre_act_photo);

        linearNoDataFound = (RelativeLayout) findViewById(R.id.rl_no_data_found);
        linear = (LinearLayout) findViewById(R.id.ll_textview);
        ll_post_act_img = (LinearLayout) findViewById(R.id.ll_post_act_img);

        tv_spare_parts = (TextView) findViewById(R.id.tv_spare_parts);
        Utils.msgText(PMChecklist.this, "279", tv_spare_parts);
        tv_spare_parts.setPaintFlags(tv_spare_parts.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654") ||
                mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            tv_spare_parts.setVisibility(View.GONE);
        } else {
            tv_spare_parts.setVisibility(View.VISIBLE);
        }


        tv_click_img = (TextView) findViewById(R.id.tv_click_img);
        tv_click_pre_img = (TextView) findViewById(R.id.tv_click_pre_img);

        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
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
        //0.7
        if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){
            et_assetId.setText(getIntent().getExtras().getString("qrCode"));
        }


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


        Utils.editTextProperty(PMChecklist.this, et_site_id);
        et_site_id.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_assetId);
        et_assetId.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_projectName);
        et_projectName.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_productName);
        et_productName.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_soNumber);
        et_soNumber.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_WorkOrderNumber);
        et_WorkOrderNumber.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_Region);
        et_Region.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_District);
        et_District.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_City);
        et_City.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_Latitude);
        et_Latitude.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_Longitude);
        et_Longitude.setEnabled(false);
        Utils.editTextProperty(PMChecklist.this, et_siteTowerType);
        et_siteTowerType.setEnabled(false);


        //if transaction is present in pending transactions the disable submit button.
        if (txnStatus) {
            tvSubmit.setBackgroundResource(R.drawable.disable);
            tvSubmit.setEnabled(false);
        } else {
            tvSubmit.setEnabled(true);
        }


        if (mAppPreferences.getSiteNameEnable() == 1 && getIntent().getExtras().getString("siteName") != null) {
            sName = "(" + getIntent().getExtras().getString("siteName") + ")";
        } else {
            sName = "";
        }
        et_site_id.setText(siteId + sName);
        Utils.textViewProperty(PMChecklist.this, tv_siteId, Utils.msg(PMChecklist.this, "77"));
        Utils.textViewProperty(PMChecklist.this, tv_assetId, Utils.msg(PMChecklist.this, "880"));
        Utils.textViewProperty(PMChecklist.this, tv_click_img, Utils.msg(PMChecklist.this, "302"));
        Utils.textViewProperty(PMChecklist.this, tv_click_pre_img, Utils.msg(PMChecklist.this, "298"));
        Utils.textViewProperty(PMChecklist.this, tv_projectName, Utils.msg(PMChecklist.this, "803"));
        Utils.textViewProperty(PMChecklist.this, tv_productName, Utils.msg(PMChecklist.this, "802"));
        Utils.textViewProperty(PMChecklist.this, tv_soNumber, Utils.msg(PMChecklist.this, "804"));
        Utils.textViewProperty(PMChecklist.this, tv_WorkOrderNumber, Utils.msg(PMChecklist.this, "805"));
        Utils.textViewProperty(PMChecklist.this, tv_Region, Utils.msg(PMChecklist.this, "806"));
        Utils.textViewProperty(PMChecklist.this, tv_District, Utils.msg(PMChecklist.this, "807"));
        Utils.textViewProperty(PMChecklist.this, tv_City, Utils.msg(PMChecklist.this, "808"));
        Utils.textViewProperty(PMChecklist.this, tv_Latitude, Utils.msg(PMChecklist.this, "809"));
        Utils.textViewProperty(PMChecklist.this, tv_Longitude, Utils.msg(PMChecklist.this, "810"));
        Utils.textViewProperty(PMChecklist.this, tv_siteTowerType, Utils.msg(PMChecklist.this, "811"));
        Utils.msgText(PMChecklist.this, "115", tvSubmit); // set text Submit
        btnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        btnTakeDoc = (Button) findViewById(R.id.btn_browse_doc);
        btn_pre_browse_doc = (Button) findViewById(R.id.btn_pre_browse_doc);
        btnTakePrePhoto = (Button) findViewById(R.id.btn_take_pre_photo);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        bt_back = (Button) findViewById(R.id.button_back);
        rl_submit = (LinearLayout) findViewById(R.id.rl_submit);
        Utils.msgButton(PMChecklist.this, "71", bt_back);
        Utils.msgText(PMChecklist.this, "226", tv_no_data); // set text General PM
        if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")) {
            Utils.msgText(PMChecklist.this, "548", tvHeader);
        } else if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")) {
            Utils.msgText(PMChecklist.this, "547", tvHeader);
        } else {
            Utils.msgText(PMChecklist.this, activityTypeId, tvHeader);
        }
    }

    //Add elements to view.
    public void addElementsToLayout(char parentFlag, int prvfieldId, String fieldIdList) {
        DataBaseHelper dbHelper = new DataBaseHelper(PMChecklist.this);
        dbHelper.open();
        if (fieldIdList != null) {
            fieldIdList = fieldIdList.replace("[", "").replace("]", "");
        }

        Cursor cursor = dbHelper.getPMChecklist(activityTypeId, fieldIdList == null ? 0 : 2, fieldIdList, mAppPreferences.getTTModuleSelection(),
                getIntent().getExtras().getString("equipmentId"));

        if (parentFlag == 'P' && cursor == null || (cursor != null && cursor.getCount() == 0)) {
            Toast.makeText(PMChecklist.this, "No Checklist found.", Toast.LENGTH_LONG).show();
            finish();
        }

        StringBuilder imgMsg = new StringBuilder();

        ChecklistDetail pmCheckList;

        TextView textView,tvRemarksLink, capturePrePhoto, capturePostPhoto, tv_divider;
        EditText etViRemark;
        Button bt_pre_pic, bt_post_pic;
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

                    linear.addView(Utils.groupTV(PMChecklist.this, tvGroupName, (cursor.getString(cursor.getColumnIndex("groupName")))), viewIndex);
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
                pmCheckList.setItemKey(cursor.getString(cursor.getColumnIndex("validateKey")));
                pmCheckList.setGrpName(cursor.getString(cursor.getColumnIndex("groupName")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setFieldName(cursor.getString(cursor.getColumnIndex("fieldName")));
                pmCheckList.setId(cursor.getInt(cursor.getColumnIndex("fieldId")));
                pmCheckList.setrFlag(cursor.getString(cursor.getColumnIndex("pFlag")).charAt(0));
                pmCheckList.setDropDownValue(cursor.getString(cursor.getColumnIndex("value")));
                pmCheckList.setFieldType(cursor.getString(cursor.getColumnIndex("fieldType")).charAt(0));
                pmCheckList.setLength(cursor.getString(cursor.getColumnIndex("length")));
                pmCheckList.setDataType(cursor.getString(cursor.getColumnIndex("dataType")).charAt(0));
                pmCheckList.setMadatory(cursor.getString(cursor.getColumnIndex("mandatory")));
                pmCheckList.setRemarkMada(cursor.getString(cursor.getColumnIndex("rMandatory")));
                pmCheckList.setPrePhotoConfig(cursor.getString(cursor.getColumnIndex("preImgConfig")));
                pmCheckList.setPostPhotoConfig(cursor.getString(cursor.getColumnIndex("postImgConfig")));
                pmCheckList.setChildIteams(spiltChild(cursor.getString(cursor.getColumnIndex("pActivityId"))));

                //pmCheckList.setPreMediaType("doc,pdf,xlx,jpeg,jpeg");
                //pmCheckList.setPostMediaType("doc,pdf,xls,jpeg,jpeg");
                pmCheckList.setPrePhotoTag(cursor.getString(cursor.getColumnIndex("preImgTag")));
                pmCheckList.setPostPhotoTag(cursor.getString(cursor.getColumnIndex("postImgTag")));

                pmCheckList.setPreMediaInfoList(cursor.getString(cursor.getColumnIndex("preImgTag")),
                        cursor.getString(cursor.getColumnIndex("preMediaType")));


                pmCheckList.setPostMediaInfoList(cursor.getString(cursor.getColumnIndex("postImgTag")),
                        cursor.getString(cursor.getColumnIndex("postMediaType")));

                pmCheckList.setPreMediaType(cursor.getString(cursor.getColumnIndex("preMediaType")));
                pmCheckList.setPostMediaType(cursor.getString(cursor.getColumnIndex("postMediaType")));
                prvFieldId = pmCheckList.getId();
                prvGroupName = pmCheckList.getGrpName();
                //0.8
                pmCheckList.setAutoSumChecklistId(cursor.getString(cursor.getColumnIndex("linkChkList")));


                hmPmCheklist.put(pmCheckList.getId(), pmCheckList);

                if (mAppPreferences.getPMImageUploadType() == 2) {

                    //Add Pre Photo extView if pre photo configured for checklist item
                    if (pmCheckList.getPreMinImg() > -1 && pmCheckList.getPreMaxImg() > -1) {
                        String DocumentName;
                        String typeName;
                        String preMediaType = "jpeg";
                        if (pmCheckList.getPreMediaType() != null && pmCheckList.getPreMediaType().size()>0) {
                            preMediaType = pmCheckList.getPreMediaType().get(0);
                        }
                        if (preMediaType.contains("doc") || preMediaType.contains("DOC")
                                || preMediaType.contains("txt") || preMediaType.contains("TXT")
                                || preMediaType.contains("pdf") || preMediaType.contains("PDF")
                                || preMediaType.contains("xlsx") || preMediaType.contains("XLSX")
                                || preMediaType.contains("pptx") || preMediaType.contains("PPTX")
                                || preMediaType.contains("xls") || preMediaType.contains("XLS")
                                || preMediaType.contains("ppt") || preMediaType.contains("PPT")
                                || preMediaType.contains("csv") || preMediaType.contains("CSV")) {
                            typeName = Utils.msg(PMChecklist.this, "820");
                            DocumentName = Utils.msg(PMChecklist.this, "816");
                        } else if (preMediaType.equalsIgnoreCase("SP") ||
                                preMediaType.equalsIgnoreCase("sp") ||
                                preMediaType.equalsIgnoreCase("Sp")) {
                            typeName = Utils.msg(PMChecklist.this, "819");
                            DocumentName = Utils.msg(PMChecklist.this, "815");
                        } else {
                            typeName = Utils.msg(PMChecklist.this, "228");
                            DocumentName = Utils.msg(PMChecklist.this, "299");
                        }

                        imgMsg.append(typeName + " (Minimum " + pmCheckList.getPreMinImg() + " & Maximum " + pmCheckList.getPreMaxImg() + ") " + DocumentName + ".");

                        capturePrePhoto = new TextView(this);
                        capturePrePhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(PMChecklist.this, capturePrePhoto, imgMsg.toString());

                        //pre photo button
                        bt_pre_pic = new Button(PMChecklist.this);
                        // System.out.print(pmCheckList.getPostMediaType());
                        imageButtonProperty(bt_pre_pic, pmCheckList.getId(), "Pre", preMediaType);

                        //pre photo gallery
                        pre_photo_gallery = new RecyclerView(PMChecklist.this);
                        recyclerViewProperty(pre_photo_gallery, pmCheckList.getId() + 100);

                        pmCheckList.setCapturePrePhoto(capturePrePhoto);
                        pmCheckList.setPrePhoto(bt_pre_pic);
                        pmCheckList.setPre_grid(pre_photo_gallery);
                        pmCheckList.setPreImgCounter(1);
                        setImages(1, pmCheckList.getId());
                        pmCheckList.getPrePhoto().setVisibility(View.VISIBLE);
                        pmCheckList.getCapturePrePhoto().setVisibility(View.VISIBLE);
                    }

                    //Add Post Photo TextView if pre photo configured for checklist item
                    if (pmCheckList.getPostMinImg() > -1 && pmCheckList.getPostMaxImg() > -1) {
                        imgMsg.setLength(0);
                        String DocumentName;
                        String typeName;
                        String postMediaType = "jpeg";
                        if (pmCheckList.getPostMediaType() != null && pmCheckList.getPostMediaType().size()>0) {
                            postMediaType = pmCheckList.getPostMediaType().get(0);
                        }

                        if (postMediaType.contains("doc") || postMediaType.contains("DOC")
                                || postMediaType.contains("txt") || postMediaType.contains("TXT")
                                || postMediaType.contains("pdf") || postMediaType.contains("PDF")
                                || postMediaType.contains("xlsx") || postMediaType.contains("XLSX")
                                || postMediaType.contains("pptx") || postMediaType.contains("PPTX")
                                || postMediaType.contains("xls") || postMediaType.contains("XLS")
                                || postMediaType.contains("ppt") || postMediaType.contains("PPT")
                                || postMediaType.contains("csv") || postMediaType.contains("CSV")) {
                            typeName = Utils.msg(PMChecklist.this, "820");
                            DocumentName = Utils.msg(PMChecklist.this, "814");
                        } else if (postMediaType.equalsIgnoreCase("SP") ||
                                postMediaType.equalsIgnoreCase("sp") ||
                                postMediaType.equalsIgnoreCase("Sp")) {
                            typeName = Utils.msg(PMChecklist.this, "819");
                            DocumentName = Utils.msg(PMChecklist.this, "813");
                        } else {
                            typeName = Utils.msg(PMChecklist.this, "228");
                            DocumentName = Utils.msg(PMChecklist.this, "301");
                        }
                        imgMsg.append(typeName + " (Minimum " + pmCheckList.getPostMinImg() + " & Maximum " + pmCheckList.getPostMaxImg() + ") " + DocumentName + ".");

                        capturePostPhoto = new TextView(this);
                        capturePostPhoto.setId(pmCheckList.getId());
                        Utils.textViewProperty(PMChecklist.this, capturePostPhoto, imgMsg.toString());

                        //post photo button
                        bt_post_pic = new Button(PMChecklist.this);

                        imageButtonProperty(bt_post_pic, pmCheckList.getId(), "Post", postMediaType);


                        //post photo gallery
                        post_photo_gallery = new RecyclerView(PMChecklist.this);
                        recyclerViewProperty(post_photo_gallery, pmCheckList.getId());
                        pmCheckList.setCapturePostPhoto(capturePostPhoto);
                        pmCheckList.setPostPhoto(bt_post_pic);
                        pmCheckList.setPost_grid(post_photo_gallery);
                        pmCheckList.setPostImgCounter(1);
                        setImages(2, pmCheckList.getId());
                        pmCheckList.getPostPhoto().setVisibility(View.VISIBLE);
                        pmCheckList.getCapturePostPhoto().setVisibility(View.VISIBLE);
                    }
                }

                //For text view use show checklist name
                textView = new TextView(this);
                textView.setId(pmCheckList.getId());
                Utils.textViewProperty(PMChecklist.this, textView, pmCheckList.getFieldName().trim());
                pmCheckList.setTextView(textView);

                tv_divider = new TextView(this);
                Utils.textViewDivider(PMChecklist.this, tv_divider);
                if (mAppPreferences.getPMImageUploadType() == 2) {
                    tv_divider.setVisibility(View.VISIBLE);
                } else {
                    tv_divider.setVisibility(View.GONE);
                }

                pmCheckList.setTvDivider(tv_divider);

                // remarks
                tvRemarksLink = new TextView(this);
                etViRemark = new EditText(this);
                etViRemark.setText(getSavedRemarks("" + pmCheckList.getId()));
                remarklinkTV(tvRemarksLink, etViRemark, pmCheckList.getId());

                pmCheckList.setTvRemarksLink(tvRemarksLink);
                pmCheckList.setEtViRemarks(etViRemark);

                if (pmCheckList.getCapturePrePhoto() != null) {
                    linear.addView(pmCheckList.getCapturePrePhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPrePhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPre_grid(), viewIndex);
                    viewIndex++;
                }

                linear.addView(pmCheckList.getTextView(), viewIndex);
                viewIndex++;

                switch (pmCheckList.getFieldType()) {
                    case 'E':
                    case 'D':
                    case 'Q':
                    case 'R':
                    case 'T':  //0.8
                        addEditText(pmCheckList, viewIndex);
                        viewIndex++;
                        break; // break is optional
                    case 'S':
                    case 'V':
                        addSpinner(pmCheckList, viewIndex);
                        viewIndex++;
                    default:
                }

                linear.addView(pmCheckList.getTvRemarksLink(), viewIndex);
                viewIndex++;
                if (actStaus.equalsIgnoreCase("RS")) {
              /*      textView1 = new TextView(this);
                    textView1.setId(pmCheckList.getId());
                    Utils.textViewProperty(PMChecklist.this, textView1,"Reviewer Remarks".trim());
                    pmCheckList.setTextView(textView1);*/
                    addEditText1(pmCheckList, viewIndex);
                    // linear.addView(pmCheckList.getTextView(), viewIndex);
                    viewIndex++;
                }

                if (pmCheckList.getCapturePostPhoto() != null) {
                    linear.addView(pmCheckList.getCapturePostPhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPostPhoto(), viewIndex);
                    viewIndex++;
                    linear.addView(pmCheckList.getPost_grid(), viewIndex);
                    viewIndex++;
                }


                linear.addView(pmCheckList.getTvDivider(), viewIndex);
                viewIndex++;

                //Show only parent items initially
                //pmCheckList.getTextView().setVisibility(View.VISIBLE); //.6
                //pmCheckList.getTvDivider().setVisibility(View.VISIBLE);//.6
                //pmCheckList.getTvRemarksLink().setVisibility(View.VISIBLE);//.6

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
        Utils.editTextProperty(PMChecklist.this, editText);


        editText.setText(getSavedValue("" + checkListDtl.getId()));
        checkListDtl.setFieldCat('E');

        if (preMinImage > 0 && mAppPreferences.getPMImageUploadType() == 1) {
            if (preMinImage == 1) {
                if (preImgCounter > preMinImage) {
                    editText.setEnabled(true);
                    checkListDtl.getTvRemarksLink().setEnabled(true);
                } else {
                    editText.setEnabled(false);
                    checkListDtl.getTvRemarksLink().setEnabled(false);
                }
            } else if (preImgCounter <= preMinImage) {
                editText.setEnabled(false);
                checkListDtl.getTvRemarksLink().setEnabled(false);
            } else {
                editText.setEnabled(true);
                checkListDtl.getTvRemarksLink().setEnabled(true);
            }
        } else {
            editText.setEnabled(true);
            checkListDtl.getTvRemarksLink().setEnabled(true);
        }

        //setupUI( editText );
        checkListDtl.setEditText(editText);
        dataType(checkListDtl);

        linear.addView(checkListDtl.getEditText(), viewIndex);

        checkListDtl.getEditText().setVisibility(View.VISIBLE);
        if (checkListDtl.getFieldType() == 'D') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.icon_calender);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            dateDialog(checkListDtl.getEditText(), checkListDtl.getId(), checkListDtl.getTextView());
        } else if (checkListDtl.getFieldType() == 'T') {  //0.8
            checkListDtl.getEditText().setBackgroundResource(R.drawable.icon_calender);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            timeDialog(checkListDtl.getEditText(), checkListDtl.getId(),
                    checkListDtl.getTextView(),checkListDtl.getDropDownValue());
        } else if (checkListDtl.getFieldType() == 'Q') {
            checkListDtl.getEditText().setBackgroundResource(R.drawable.bar_code);
            checkListDtl.getEditText().setPadding(7, 0, 100, 0);
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            qrCode(checkListDtl.getEditText(), checkListDtl.getId());
        } else if (checkListDtl.getFieldType() == 'R') {
            //checkListDtl.getEditText().setEnabled(false);
            checkListDtl.getEditText().setKeyListener(null);
        }/*else if (checkListDtl.getFieldType() == 'E' && displayTotalSum.contains(",")) {
            checkListDtl.getEditText().setFocusableInTouchMode(false);
            checkListDtl.getEditText().setEnabled(false);
        }*/
        checkListDtl.getEditText().setId(checkListDtl.getId());
        checkListDtl.setVisible(editText.isEnabled());
        sharePrefence(checkListDtl.getEditText().getId(),
                checkListDtl.getEditText().getText().toString().trim());

        //register watcher to edittext
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (scroolFlag == 1 && mAppPreferences.getPMImageUploadType() == 2 && s.toString().length() > 0) {

                    int counter = hmPmCheklist.get(editText.getId()).getPreImgCounter() - 1;

                    if (hmPmCheklist.get(editText.getId()).getPreMinImg() > counter) {
                        String m = Utils.msg(PMChecklist.this, "257") + " " + hmPmCheklist.get(editText.getId()).getPreMinImg()
                                + " " + Utils.msg(PMChecklist.this, "258") + " "
                                + hmPmCheklist.get(editText.getId()).getPreMaxImg() + " " + Utils.msg(PMChecklist.this, "299");
                        Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + hmPmCheklist.get(editText.getId()).getFieldName().toString());
                        s.replace(0, s.length(), "");
                        return;
                    }

                    if (!checkPreviousElement(editText.getId(), 0, 2)) {
                        s.replace(0, s.length(), "");
                        return;
                    }
                }
                //auto sum of some fields and display in another fields
                //0.8
                autoSum(editText.getId());
                //generate json and save in share preferences
                sharePrefence(editText.getId(), s.toString());
            }
        };
        checkListDtl.getEditText().addTextChangedListener(watcher);
    }

    // Method to add EditText in form.
    private void addEditText1(ChecklistDetail checkListDtl, int viewIndex) {

        final EditText editText1 = new EditText(this);
        Utils.editTextProperty(PMChecklist.this, editText1);
        String value = getSavedReviewRemarks("" + checkListDtl.getId());
        editText1.setText("Rejected Remarks- " + value);
        checkListDtl.setEditText1(editText1);
        //dataType(checkListDtl);
        linear.addView(checkListDtl.getEditText1(), viewIndex);

        if (value.equalsIgnoreCase("") || value.isEmpty() || value == null) {
            checkListDtl.getEditText1().setVisibility(View.GONE);
        } else {
            checkListDtl.getEditText1().setVisibility(View.VISIBLE);

        }


        //  checkListDtl.getEditText().setKeyListener(null);
        checkListDtl.getEditText1().setTextColor(Color.parseColor("#ff0000"));
        // checkListDtl.getEditText().setBackgroundColor(R.color.orange);
        //checkListDtl.getEditText().setHighlightColor(R.color.color_ff0000);
        //  checkListDtl.getEditText().setId(checkListDtl.getId());
        checkListDtl.getEditText1().setClickable(false);
        checkListDtl.getEditText1().setEnabled(false);
        //checkListDtl.setVisible(editText.isEnabled());
        //sharePrefenceReviewRemarks(checkListDtl.getEditText().getId(), checkListDtl.getEditText().getText().toString().trim());
    }

    // Method to add Spinner in form.
    private void addSpinner(ChecklistDetail checkListDtl, int viewIndex) {

        Spinner spinner;

        if (checkListDtl.getFieldType() == 'V') {
            spinner = new SearchableSpinner(this);
        } else {
            spinner = new Spinner(this);
        }
        addItemsOnSpinner(spinner, checkListDtl.getDropDownValue(), checkListDtl.getId());
        checkListDtl.setFieldCat('S');

        spinner.setId(checkListDtl.getId());
        Utils.spinnerProperty(PMChecklist.this, spinner);

        if (preMinImage > 0 && mAppPreferences.getPMImageUploadType() == 1) {
            if (preMinImage == 1) {
                if (preImgCounter > preMinImage) {
                    spinner.setEnabled(true);
                    checkListDtl.getTvRemarksLink().setEnabled(true);
                } else {
                    spinner.setEnabled(false);
                    checkListDtl.getTvRemarksLink().setEnabled(false);
                }
            } else if (preImgCounter <= preMinImage) {
                spinner.setEnabled(false);
                checkListDtl.getTvRemarksLink().setEnabled(false);
            } else {
                spinner.setEnabled(true);
                checkListDtl.getTvRemarksLink().setEnabled(true);
            }
        } else {
            spinner.setEnabled(true);
            checkListDtl.getTvRemarksLink().setEnabled(true);
        }

        checkListDtl.setSpinner(spinner);

        checkListDtl.getSpinner().setVisibility(View.VISIBLE);

        linear.addView(checkListDtl.getSpinner(), viewIndex);
        sharePrefence(checkListDtl.getId(), spinner.getSelectedItem().toString().trim());
        checkListDtl.setVisible(spinner.isEnabled());
        spinner.setOnItemSelectedListener(this);

        //.6
        if(checkListDtl.getItemKey()!=null
                && (checkListDtl.getItemKey().equalsIgnoreCase("TX_DG_CONTROLLER_CHANGE")
                || checkListDtl.getItemKey().equalsIgnoreCase("TX_DG2_CONTROLLER_CHANGE"))){
            spinner.setVisibility(View.GONE);
            checkListDtl.getTextView().setVisibility(View.GONE);
            checkListDtl.getTvRemarksLink().setVisibility(View.GONE);
        }

    }

    // Method to Add Values in DDL.
    private void addItemsOnSpinner(Spinner spinner, String values, int jsonKey) {

        ArrayList<String> arrList = new ArrayList<String>();
        arrList.add(AppConstants.DD_SELECT_OPTION);
        arrList.addAll(Arrays.asList(values.split(",")));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, arrList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(dataAdapter);
        int pos = 0;


        if (getSavedValue("" + jsonKey).length() > 0) {
            pos = arrList.indexOf(getSavedValue("" + jsonKey));
        }

        if (pos < arrList.size() && pos >= 0) {
            spinner.setSelection(pos);
        }
    }

    public void setupUI(Spinner view) {
        // Set up touch listener for non-text box views to hide keyboard.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void dateDialog(final EditText et, final int a, final TextView textView) {
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.datePickerForPM(PMChecklist.this, hmPmCheklist.get(a).getEditText(), textView);
            }
        });
    }

    //0.8
    public void timeDialog(final EditText et, final int a, final TextView textView,final String ddValue) {
            et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ddValue!=null){
                    ArrayList<String> arrList = new ArrayList<String>();
                    arrList.add(AppConstants.DD_SELECT_OPTION);
                    arrList.addAll(Arrays.asList(ddValue.split(",")));
                    if(arrList.size()==4){
                        //String compareValue = "11:02";
                        //String compareMsg = "hiii";
                        String compareType = arrList.get(1);
                        int b  = Integer.parseInt(arrList.get(2));
                        String compareMiniute = arrList.get(3);
                        String compareValue = hmPmCheklist.get(b).getEditText().getText().toString();
                        String compareMsg = hmPmCheklist.get(b).getTextView().getText().toString();
                        UtilsTask.timePickerForPM1(PMChecklist.this, hmPmCheklist.get(a).getEditText(),
                                textView,compareType,compareValue,compareMiniute,compareMsg);
                       /* if(compareMiniute.equalsIgnoreCase("0")){
                            UtilsTask.timePickerForPM(PMChecklist.this, hmPmCheklist.get(a).getEditText(),
                                    textView);
                        }else{
                            UtilsTask.timePickerForPM1(PMChecklist.this, hmPmCheklist.get(a).getEditText(),
                                    textView,compareType,compareValue,compareMiniute,compareMsg);
                        }*/
                    }else{
                        UtilsTask.timePickerForPM(PMChecklist.this, hmPmCheklist.get(a).getEditText(),
                                textView);
                    }
                }else{
                    UtilsTask.timePickerForPM(PMChecklist.this, hmPmCheklist.get(a).getEditText(),
                            textView);
                }
            }
        });
    }

    public void qrCode(final EditText et, final int a) {
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //intializing scan object
                currFieldId = a;
                qrScan = new IntentIntegrator(PMChecklist.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(PMChecklist.this, "511"));
                qrScan.initiateScan();
            }
        });
    }

    // Method To set Validation based on DataType of field and Input Length
    // F means number decimal keypad open.
    // A means all keypad open
    // I means number keypad open
    private void dataType(ChecklistDetail chkListDtl) {

        switch (chkListDtl.getDataType()) {
            case 'F':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new CustomRangeInputFilter(chkListDtl.getLenBefore(), chkListDtl.getLenAfter())});
                break;
            case 'A':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        chkListDtl.getLenBefore())});
                break;
            case 'I':
                chkListDtl.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                chkListDtl.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        chkListDtl.getLenBefore())});

                break;
            default:
                break;
        }

    }

    //method to check skip with mendtary
   /* private boolean validateSkip() {
        for (ChecklistDetail checkListItem : hmPmCheklist.values()) {
                if ((checkListItem.getFieldCat() == 'E' && checkListItem.getEditText().getText().toString().trim().length() == 0)
                        || (checkListItem.getFieldCat() == 'S' && checkListItem.getSpinner().getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    String s = Utils.msg(PMChecklist.this, "256") + " " + checkListItem.getFieldName().toString().trim();
                    Utils.toastMsg(PMChecklist.this, s);
                    return false;
                }
        }
        return true;
    }*/

    // Method to Validate Mandatory Field

    private boolean validate() {

        boolean status = true;
        GPSTracker gps = new GPSTracker(PMChecklist.this);

        if (mAppPreferences.getPMImageUploadType() == 1 && preImgCounter <= preMinImage) {

            String s = Utils.msg(PMChecklist.this, "257") + " " + preMinImage
                    + " " + Utils.msg(PMChecklist.this, "258") + " "
                    + preMaxImage + " " + Utils.msg(PMChecklist.this, "299");
            Utils.toastMsg(PMChecklist.this, s);
            return false;
        } else if (gps.canGetLocation() == false) {
            gps.showSettingsAlert();
            return false;
        } else if (!Utils.hasPermissions(PMChecklist.this, AppConstants.LOCATION_PERMISSIONS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(PMChecklist.this, "Permission denied for device's location. Please Re-login.", Toast.LENGTH_LONG).show();
            return false;
        } else if (Utils.isAutoDateTime(this)) {
            Utils.autoDateTimeSettingsAlert(this);
            return false;
        } else if (gps.isMockLocation() == true) {
            FackApp();
            return false;
        } else if (gps.canGetLocation() == true) {

            /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(PMChecklist.this);            if (ActivityCompat.checkSelfPermission(PMChecklist.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(PMChecklist.this,
                     Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                   if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } else {
                        latitude = String.valueOf(gps.getLatitude());
                       longitude = String.valueOf(gps.getLongitude());
                    }
               }).addOnFailureListener(PMChecklist.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latitude = String.valueOf(gps.getLatitude());
                        longitude = String.valueOf(gps.getLongitude());
                    }
                });
            }*/

            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());

            if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                    || (longitude == null || latitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                // Toast.makeText(PMChecklist.this,"Wait,Latitude & Longitude is Capturing.",Toast.LENGTH_SHORT).show();
                Utils.toast(PMChecklist.this, "252");
                return false;
            } else {
                latitude = String.valueOf(gps.getLatitude());
                longitude = String.valueOf(gps.getLongitude());
            }
        }

        Map<String, String> currTxnData = null;
        Map<String, TextView> txnDataTextView = null;

        for (ChecklistDetail checkListItem : hmPmCheklist.values()) {
            //.6
            String dataKey1 = checkListItem.getItemKey();
            if(dataKey1!=null && (dataKey1.equalsIgnoreCase("TX_DG_CONTROLLER_CHANGE")
                    ||dataKey1.equalsIgnoreCase("TX_DG2_CONTROLLER_CHANGE"))){
                String inputVal1;
                if (checkListItem.getFieldCat() == 'E') {
                    inputVal1 = checkListItem.getEditText().getText().toString().trim();
                } else {
                    inputVal1 = checkListItem.getSpinner().getSelectedItem().toString().trim();
                }

                if (inputVal1.equalsIgnoreCase("null")) {
                    inputVal1 = null;
                }



                if (currTxnData == null) {
                    currTxnData = new HashMap<String, String>();
                    txnDataTextView = new HashMap<String, TextView>();
                }

                if (dgType.equalsIgnoreCase("B") || AssetDgType.equalsIgnoreCase("1")) {
                    dataKey1 = "TX_DG2_CONTROLLER_CHANGE";
                }

                currTxnData.put(dataKey1, inputVal1);
                txnDataTextView.put(dataKey1, checkListItem.getTextView());

            }

            if(checkListItem.getTextView().getVisibility()==View.GONE){
                continue;
            }
            //If Checklist item is not visible/enable the continue to next item.
            if (!checkListItem.isVisible()) {
                continue;
            }

            int preCounter = checkListItem.getPreImgCounter() - 1;
            int postCounter = checkListItem.getPostImgCounter() - 1;

			/*
				Validate Pre Min Images
				Check if images will be captured at each item level or on activity level
				1 means - Activity Level
				2 means - each checklist item level
			*/
            if (mAppPreferences.getPMImageUploadType() == 2) {
                if (checkListItem.getPreMinImg() > preCounter || checkListItem.getPreMaxImg() < preCounter) {
                    String m = Utils.msg(PMChecklist.this, "257") + " " + checkListItem.getPreMinImg()
                            + " " + Utils.msg(PMChecklist.this, "258") + " "
                            + checkListItem.getPreMaxImg() + " " + Utils.msg(PMChecklist.this, "299");
                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + checkListItem.getFieldName());
                    return false;
                }
            }

            //Check for mandatory field
            if (checkListItem.isMadatory().equalsIgnoreCase("Y")
                    || checkListItem.isMadatory().equalsIgnoreCase("S")) {
                if ((checkListItem.getFieldCat() == 'E' && checkListItem.getEditText().getText().toString().trim().length() == 0)
                        || (checkListItem.getFieldCat() == 'S' && checkListItem.getSpinner().getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    String s = Utils.msg(PMChecklist.this, "256") + " " + checkListItem.getFieldName().toString().trim();
                    Utils.toastMsg(PMChecklist.this, s);
                    return false;
                }
            }

            if (checkListItem.isRemarkMadatory()) {

				/*
					if Remarks is mandatory then validate following
					 - For Text Box - Remarks field is blank or not
					 - For Dropdown - Remark field is blank ot not + value selected in dropdwon is configured with reamsk as mandatory or not
				 */
                if (checkListItem.getEtViRemarks().getText().toString().length() == 0
                        && (checkListItem.getFieldCat() == 'E' || (checkListItem.getFieldCat() == 'S' && checkListItem.getRemarkMada().contains(checkListItem.getSpinner().getSelectedItem().toString())))) {

                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    String s1 = Utils.msg(PMChecklist.this, "255");
                    String s2 = Utils.msg(PMChecklist.this, "106");
                    s1 = s1 + " " + checkListItem.getFieldName().toString().trim() + " " + s2;
                    //Utils.toastMsg( PMChecklist.this, s1 );
                    Toast.makeText(PMChecklist.this, s1.trim(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }

			/*
				Validate Post Min Images
				Check if images will be captured at each item level or on activity level
				1 means - Activity Level
				2 means - each checklist item level
			*/
            if (mAppPreferences.getPMImageUploadType() == 2) {
                if (checkListItem.getPostMinImg() > postCounter || checkListItem.getPostMaxImg() < postCounter) {
                    String m = Utils.msg(PMChecklist.this, "257") + " " + checkListItem.getPostMinImg()
                            + " " + Utils.msg(PMChecklist.this, "258") + " "
                            + checkListItem.getPostMaxImg() + " " + Utils.msg(PMChecklist.this, "301");
                    checkListItem.getTextView().clearFocus();
                    checkListItem.getTextView().requestFocus();
                    Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + checkListItem.getFieldName());
                    return false;
                }
            }

			/*
				Collect Meter Readings & other transaction data in map using item key which identify the data parameter
			 */
            if (checkListItem.getItemKey() != null
                    && ((checkListItem.getFieldCat() == 'E' && checkListItem.getEditText().getText().toString().trim().length() > 0)
                    || (checkListItem.getFieldCat() == 'S' && !checkListItem.getSpinner().getSelectedItem().toString().equalsIgnoreCase("Select")))) {

                String inputVal;
                if (checkListItem.getFieldCat() == 'E') {
                    inputVal = checkListItem.getEditText().getText().toString().trim();
                } else {
                    inputVal = checkListItem.getSpinner().getSelectedItem().toString().trim();
                }

                if (inputVal.equalsIgnoreCase("null")) {
                    inputVal = null;
                }

                if (currTxnData == null) {
                    currTxnData = new HashMap<String, String>();
                    txnDataTextView = new HashMap<String, TextView>();
                }

                String dataKey = checkListItem.getItemKey();

                switch (checkListItem.getItemKey()) {
                    case "TX_DG_MTR_REPLACE_DT":
                        if (dgType.equalsIgnoreCase("B") || AssetDgType.equalsIgnoreCase("1")) {
                            dataKey = "TX_DG2_MTR_REPLACE_DT";
                        }
                        break;
                    case "TX_DG_READING":

                        if (dgType.equalsIgnoreCase("B") || AssetDgType.equalsIgnoreCase("1")) {
                            dataKey = "TX_DG_READING_B";
                        }
                        break;
                    case "TX_DG_CONTROLLER_CHANGE":

                        if (dgType.equalsIgnoreCase("B") || AssetDgType.equalsIgnoreCase("1")) {
                            dataKey = "TX_DG2_CONTROLLER_CHANGE";
                        }
                        break;
                    case "TX_DG_MTR_STATUS":

                        if (dgType.equalsIgnoreCase("B") || AssetDgType.equalsIgnoreCase("1")) {
                            dataKey = "TX_DG_MTR_STATUS_B";
                        }
                    default:
                        break;
                }

                currTxnData.put(dataKey, inputVal);
                txnDataTextView.put(dataKey, checkListItem.getTextView());
            }
        }

        if (currTxnData != null && currTxnData.size() > 0) {
            try {
                status = validateTxnData(currTxnData, txnDataTextView);
            } catch (Exception e) {
                status = true;
            }
        } else {
            status = true;
        }


        if (!status) {
            return false;
        }

        if (mAppPreferences.getPMImageUploadType() == 1 && imgCounter <= postMinImage) {
            String s = Utils.msg(PMChecklist.this, "257") + " "
                    + postMinImage + " "
                    + Utils.msg(PMChecklist.this, "258") + " "
                    + postMaxImage + " "
                    + Utils.msg(PMChecklist.this, "301");
            Utils.toastMsg(PMChecklist.this, s);
            return false;
        }
        return true;
    }

    //Validate the transnational data captured in checklist
    public boolean validateTxnData(Map<String, String> currTxnData, Map<String, TextView> txnDataTextView) {

        boolean status = false;

        for (String validationName : validationList.keySet()) {

            switch (validationName) {

                case "DG_REPLACEMENT_DATE_CHECK":

					/*
						If previous transaction present & current transaction have DG meter replacement date
						then replacement date should be greater then previous tran date
					 */
                    if (prvTxnData.size() > 0) {

                        //Validate for DG Type A
                        if (currTxnData.containsKey("TX_DG_MTR_REPLACE_DT")) {
                            status = validateMtrReplacementDate(currTxnData.get("TX_DG_MTR_REPLACE_DT"), prvTxnData.get("TX_DG_MTR_REPLACE_DT"), txnDataTextView.get("TX_DG_MTR_REPLACE_DT"));

                            if (!status) {
                                return false;
                            }
                        }

                        //Validate for DG Type B
                        if (currTxnData.containsKey("TX_DG2_MTR_REPLACE_DT")) {
                            status = validateMtrReplacementDate(currTxnData.get("TX_DG2_MTR_REPLACE_DT"), prvTxnData.get("TX_DG2_MTR_REPLACE_DT"), txnDataTextView.get("TX_DG2_MTR_REPLACE_DT"));

                            if (!status) {
                                return false;
                            }
                        }
                    }
                    break;
                case "GRID_REPLACEMENT_DATE_CHECK":

                    if (prvTxnData.size() > 0 && currTxnData.containsKey("TX_EB_MTR_REPLACE_DT")) {
                        status = validateMtrReplacementDate(currTxnData.get("TX_EB_MTR_REPLACE_DT"), prvTxnData.get("TX_EB_MTR_REPLACE_DT"), txnDataTextView.get("TX_EB_MTR_REPLACE_DT"));

                        if (!status) {
                            return false;
                        }
                    }
                    break;
                case "PREVIOUS_DG_READING":

                    if (currTxnData.containsKey("TX_DG_READING")
                            && currTxnData.containsKey("TX_DG_MTR_STATUS")
                            && prvTxnData.get("TX_DG_READING") != null) {
                        String isControllerChange = "0";
                        TextView controllerTV = null;
                        if(currTxnData.containsKey("TX_DG_CONTROLLER_CHANGE")){
                            isControllerChange = currTxnData.get("TX_DG_CONTROLLER_CHANGE");
                            controllerTV = txnDataTextView.get("TX_DG_CONTROLLER_CHANGE");
                            if (isControllerChange==null || isControllerChange.length()==0){
                                isControllerChange = "0";

                            }
                        }

                        status = validateReading(
                                currTxnData.get("TX_DG_MTR_STATUS"),
                                currTxnData.get("TX_DG_READING"),
                                prvTxnData.get("TX_DG_READING"),
                                txnDataTextView.get("TX_DG_READING"),
                                isControllerChange,controllerTV,
                                txnDataTextView.get("TX_DG_MTR_STATUS"));
                        if (!status) {
                            return false;
                        }
                    }

                    if (currTxnData.containsKey("TX_DG_READING_B")
                            && currTxnData.containsKey("TX_DG_MTR_STATUS_B")
                            && prvTxnData.get("TX_DG_READING_B") != null) {

                        String isControllerChange = "0";
                        TextView controllerTV = null;
                        if(currTxnData.containsKey("TX_DG2_CONTROLLER_CHANGE")){
                            isControllerChange = currTxnData.get("TX_DG2_CONTROLLER_CHANGE");
                            controllerTV = txnDataTextView.get("TX_DG2_CONTROLLER_CHANGE");
                            if (isControllerChange==null || isControllerChange.length()==0){
                                isControllerChange = "0";
                            }
                        }

                        status = validateReading(currTxnData.get("TX_DG_MTR_STATUS_B"),
                                currTxnData.get("TX_DG_READING_B"),
                                prvTxnData.get("TX_DG_READING_B"),
                                txnDataTextView.get("TX_DG_READING_B"),
                                isControllerChange,controllerTV,
                                txnDataTextView.get("TX_DG_MTR_STATUS_B"));
                        if (!status) {
                            return false;
                        }
                    }
                    break;
                case "PREVIOUS_EB_READING":

                    if (currTxnData.containsKey("TX_EB_READING")
                            && currTxnData.containsKey("TX_EB_MTR_STATUS")
                            && prvTxnData.get("TX_EB_READING") != null) {
                        status = validateReading(currTxnData.get("TX_EB_MTR_STATUS"),
                                currTxnData.get("TX_EB_READING"),
                                prvTxnData.get("TX_EB_READING"),
                                txnDataTextView.get("TX_EB_READING"),
                                "0",null,
                                txnDataTextView.get("TX_EB_MTR_STATUS"));
                        if (!status) {
                            return false;
                        }
                    }
                    break;
                case "HIGH_DG_HRS_CONS":

                    if (prvTxnData.size() > 0) {

                        //Validate high cons for DG A
                        if (currTxnData.containsKey("TX_DG_READING") && currTxnData.containsKey("TX_DG_MTR_STATUS")) {
                            status = validateHighCons(currTxnData.get("TX_DG_MTR_STATUS"), currTxnData.get("TX_DG_READING"), prvTxnData.get("TX_DG_READING"), prvTxnData.get("TX_DG_MTR_REPLACE_DT"), prvTxnData.get("DGR_PER_DAY_THERSHOLD"), 'D', txnDataTextView.get("TX_DG_READING"));

                            if (!status) {
                                return false;
                            }
                        }

                        //Validate high cons for DG B
                        if (currTxnData.containsKey("TX_DG_READING_B") && currTxnData.containsKey("TX_DG_MTR_STATUS_B")) {
                            status = validateHighCons(currTxnData.get("TX_DG_MTR_STATUS_B"), currTxnData.get("TX_DG_READING_B"), prvTxnData.get("TX_DG_READING_B"), prvTxnData.get("TX_DG2_MTR_REPLACE_DT"), prvTxnData.get("DGR_PER_DAY_THERSHOLD"), 'D', txnDataTextView.get("TX_DG_READING_B"));

                            if (!status) {
                                return false;
                            }
                        }
                    }
                case "HIGH_EB_UNIT_CONS":
                    //Validate high cons for Eb Reading
                    if (currTxnData.containsKey("TX_EB_READING") && currTxnData.containsKey("TX_EB_MTR_STATUS")) {
                        status = validateHighCons(currTxnData.get("TX_EB_MTR_STATUS"), currTxnData.get("TX_EB_READING"), prvTxnData.get("TX_EB_READING"), prvTxnData.get("TX_EB_MTR_REPLACE_DT"), prvTxnData.get("PER_DAY_GRID_UNITS"), 'E', txnDataTextView.get("TX_EB_READING"));

                        if (!status) {
                            return false;
                        }
                    }
                default:
                    break;
            }
        }

        return true;

    }

    private boolean validateHighCons(String meterStatus, String currReadingStr, String prvReadingStr, String prvTxnDateStr, String thersholdStr, char flag, TextView textView) {

        float prvReading = 0;

        if (prvReadingStr != null) {
            prvReading = Float.parseFloat(prvReadingStr);
        }

        if (prvReading > 0 && thersholdStr != null && currReadingStr != null
                && meterStatus.equalsIgnoreCase("Working")) {

            long days = Utils.diffDays(prvTxnDateStr, Utils.CurrentDate(0));
            float thershold = Float.parseFloat(thersholdStr);
            float currReading = Float.parseFloat(currReadingStr);

            float perDayCons = (currReading - prvReading) / days;
            float expReading = prvReading + (days * thershold);

            if (currReading > expReading && thershold > 0) {
                textView.clearFocus();
                textView.requestFocus();

                //D - DG Readings, E - EB Readings
                if (flag == 'D') {
                    String message = "Per Day DG Run Hrs should be less than " + thersholdStr + "(" + "Current reading :" + currReading +
                            ",  Previous reading :" + prvReading + " ,Per day run hrs : " + perDayCons + ").";
                    //Toast.makeText(PMChecklist.this, "" + message, Toast.LENGTH_LONG).show();
                    Utils.toastMsg(PMChecklist.this,message);
                } else if (flag == 'E') {
                    String message = "Per Day Grid Units should be less than or equal to " + thershold + "(" + "Current reading :" + currReadingStr +
                            ",  Previous reading :" + prvReadingStr + " ,Per day run hrs : " + perDayCons + ").";
                    //Toast.makeText(PMChecklist.this, "" + message, Toast.LENGTH_LONG).show();
                    Utils.toastMsg(PMChecklist.this,message);
                }
                return false;
            }
        }
        return true;
    }

    private boolean validateMtrReplacementDate(String currReplaceDateStr, String prvTxnDateStr, TextView textView) {

        if (currReplaceDateStr != null) {

            Date prvDate = Utils.convertStringToDate(prvTxnDateStr, "dd-MMM-yyyy");
            Date currDate = Utils.convertStringToDate(currReplaceDateStr, "dd-MMM-yyyy");
            if (Utils.checkDateCompare(prvDate, currDate)) {
                return true;
            } else {
                textView.clearFocus();
                textView.requestFocus();
                String message = textView.getText().toString() + " should be greater or equal to last transaction date. Last transaction date is " + prvTxnDateStr;
                //Toast.makeText(PMChecklist.this, "" + message, Toast.LENGTH_LONG).show();
                Utils.toastMsg(PMChecklist.this,message);
                return false;
            }
        }

        return true;
    }
    //.6
    private boolean validateReading(String meterStatus, String currReadingStr,
                                    String prvReadingStr, TextView textView,
                                    String isControllerChange,TextView controllerTV,
                                    TextView meterStatusTV) {

        float prvReading = 0;
        float currReading = 0;
        int meter = 0;

        if (meterStatus != null && meterStatus.equalsIgnoreCase("Working")) {
            meter = 1;
        }

        if (prvReadingStr != null) {
            prvReading = Float.parseFloat(prvReadingStr);
        }

        //Validate Reading for Dg A. Previous DG reading should not be faulty, not installed
        if (prvReading > 0 && currReadingStr != null && meter == 1) {

            currReading = Float.parseFloat(currReadingStr);

            if (currReading < prvReading && isControllerChange.equalsIgnoreCase("Select")) {
                hmPmCheklist.get(controllerTV.getId()).getTextView().setVisibility(View.VISIBLE);
                hmPmCheklist.get(controllerTV.getId()).getSpinner().setVisibility(View.VISIBLE);
                hmPmCheklist.get(controllerTV.getId()).getTvRemarksLink().setVisibility(View.VISIBLE);
                controllerTV.clearFocus();
                controllerTV.requestFocus();
                String s = controllerTV.getText().toString();
                String message = textView.getText().toString() + " (" + currReadingStr + ")" + " must be greater than or equal to previous transaction reading (" + prvReadingStr + "). "
                        +s+"?";
                Utils.toastMsg(PMChecklist.this,message);
                return false;
            }else if (currReading < prvReading && isControllerChange.equalsIgnoreCase("No")) {
                hmPmCheklist.get(controllerTV.getId()).getTextView().setVisibility(View.VISIBLE);
                hmPmCheklist.get(controllerTV.getId()).getSpinner().setVisibility(View.VISIBLE);
                hmPmCheklist.get(controllerTV.getId()).getTvRemarksLink().setVisibility(View.VISIBLE);
                textView.clearFocus();
                textView.requestFocus();
                String message = textView.getText().toString() + " (" + currReadingStr + ")" + " must be greater than or equal to previous transaction reading (" + prvReadingStr + ").";
                //.6
                //Utils.toastMsg(PMChecklist.this,message+" == "+isControllerChange);
                Utils.toastMsg(PMChecklist.this,message);
                return false;
            }else if (currReading < prvReading && isControllerChange.equalsIgnoreCase("0")) {
                textView.clearFocus();
                textView.requestFocus();
                String message = "If "+meterStatusTV.getText().toString()+ "is Working then "+textView.getText().toString() + " (" + currReadingStr + ")" + " must be greater than or equal to previous transaction reading (" + prvReadingStr + ")" +
                        " or Change "+meterStatusTV.getText().toString()+ " Working to Controller Changed.";
                Utils.toastMsg(PMChecklist.this,message);
                return false;
            }

        }

        return true;
    }

    // key + "@~@" + value + "@,@"+remarks$~$+ key + "@~@" + value +
    // "@,@"+remarks;
    // Method to get Filled CheckList Values separated with '~'
    public String checklist() {
        String checkListData = "";
        String value = "";
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String csvName = (folder.toString() +
                "/" + siteId + "_" + scheduledDate + "_" + activityTypeId + "_" + dgType + "_" + Utils.CurrentDate(1) + ".csv");

        //String csvName = (Environment.getExternalStorageDirectory().getAbsolutePath() +
        //		AppConstants.DOC_PATH +"/"+siteId+"_"+scheduledDate+"_"+activityTypeId+"_"+dgType+"_"+Utils.CurrentDate(1)+".csv");
        List<String[]> csvData = new ArrayList<String[]>();
        csvData.add(new String[]{"Id", "Name", "Values", "Remarks", "PrePhoto", "PostPhoto"});

        for (ChecklistDetail checkListObj : hmPmCheklist.values()) {

            String remarks = checkListObj.getEtViRemarks().getText().toString();

            if (checkListObj.getFieldCat() == 'E') {
                value = checkListObj.getEditText().getText().toString();
            } else if (checkListObj.getFieldCat() == 'S') {

                value = checkListObj.getSpinner().getSelectedItem().toString();

                if (value.equalsIgnoreCase("Select")) {
                    value = "";
                }
            }
            checkListData = checkListData + checkListObj.getId() + "$~$" + value + "$~$" + remarks + "@,@";

            //Add CSV content into List
            csvData.add(new String[]{
                    "" + checkListObj.getId(),
                    checkListObj.getFieldName(),
                    value,
                    remarks,
                    checkListObj.getPreImgName().toString().replace("[", "").replace("]", ""),
                    checkListObj.getPostImgName().toString().replace("[", "").replace("]", "")
            });
            //Generate CSV File
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(csvName));
                writer.writeAll(csvData); // csvData is adding to csv
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (checkListData.length() > 0) {
            checkListData = checkListData.substring(0, checkListData.length() - 3);
        }

        return checkListData;
    }

    private void imageCapture(String tag, int fieldId, int prePostFlag, int imgCounter,
                              String Imglatitude, String Imglongitude, String fileType) {
        try {
            Intent intent = null;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (fileType.equalsIgnoreCase("mp4")) {
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// set the image file name
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video quality to high
            } else if (fileType.contains("doc") || fileType.contains("DOC")
                    || fileType.contains("txt") || fileType.contains("TXT")
                    || fileType.contains("pdf") || fileType.contains("PDF")
                    || fileType.contains("xlsx") || fileType.contains("XLSX")
                    || fileType.contains("pptx") || fileType.contains("PPTX")
                    || fileType.contains("xls") || fileType.contains("XLS")
                    || fileType.contains("ppt") || fileType.contains("PPT")
                    || fileType.contains("csv") || fileType.contains("CSV")) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                if (fileType.contains("csv") || fileType.contains("CSV")
                        || fileType.contains("txt") || fileType.contains("TXT")) {
                    intent.setType("text/*");
                } else if (fileType.contains("pptx") || fileType.contains("PPTX") || fileType.contains("ppt") || fileType.contains("PPT")) {
                    intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                } else {
                    intent.setType("*/*");
                }

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // intent = Intent.createChooser(intent, "Select " + fileType + " File to Upload");
                //mGetContent.launch("application/*");
            } else if (fileType.equalsIgnoreCase("SP") ||
                    fileType.equalsIgnoreCase("sp") || fileType.equalsIgnoreCase("Sp")) {
                verifyStoragePermissions(this);
                OpenSignturePadDailog();
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }

            this.currFieldId = fieldId;
            this.prePostFlag = prePostFlag;
            this.imgNumber = imgCounter;
            this.imgTagName = tag;
            this.Imglatitude = Imglatitude;
            this.Imglongitude = Imglongitude;
            this.fileType = fileType;
            startActivityForResult(intent, 2);
        } catch (Exception e) {
            //   Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void OpenSignturePadDailog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.signature_pad_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        SignaturePad mSignaturePad;
        Button mClearButton, mSaveButton, mcancel_button;
        mClearButton = dialog.findViewById(R.id.clear_button);
        mSaveButton = dialog.findViewById(R.id.save_button);
        mcancel_button = dialog.findViewById(R.id.cancel_button);
        mcancel_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //To gradient color
        mSignaturePad = (SignaturePad) dialog.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //  Toast.makeText(PMChecklist.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });
        mClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                bmp = signatureBitmap;
                //startActivityForResult(intent, 2);
                showData();

                dialog.cancel();
              /*  if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(PMChecklist.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PMChecklist.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
                    Toast.makeText(PMChecklist.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PMChecklist.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        dialog.show();
    }

    private void showData() {
        String imgname = siteId + "-PM_" + imgTagName + "_" + System.currentTimeMillis() + imgNumber;

        if (imgname.contains("/")) {
            imgname = imgname.replaceAll("/", "");
        }
        if (imgname.contains("\\")) {
            imgname = imgname.replaceAll("\\\\", "");
        }

        if (imgname.contains(":")) {
            imgname = imgname.replaceAll(":", "");
        }

        //JSONObject allImgjsonObj = new JSONObject();
        String filePath = null;
        //Uri imageUri = data.getData();

        if (imageUri != null)
            filePath = FilePathFinder.getPath(this, imageUri);
        else {
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    filePath = uri.toString();
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        FileOutputStream fos = null;
        File newfile = null;
        try {
            //String rootPath = Environment.getExternalStorageDirectory() + AppConstants.PIC_PATH;
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
         /*   if (fileType.equalsIgnoreCase("jpg") ||
                    fileType.equalsIgnoreCase("jpeg")
                    || fileType.equalsIgnoreCase("png")) {
                imgname = imgname + ".jpg";
                Bitmap bm = Utils.decodeFile(filePath);
                newfile = folder;
                //newfile = new File( rootPath );
                if (!newfile.exists()) {
                    newfile.mkdirs();
                }

                newfile = new File(newfile, imgname);
                fos = new FileOutputStream(newfile);
                newfile.createNewFile();
                bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }else*/
            if (fileType.equalsIgnoreCase("SP")
                    || fileType.equalsIgnoreCase("sp")
                    || fileType.equalsIgnoreCase("Sp")) {
                imgname = imgname + ".jpg";
                Bitmap bm = bmp;//Utils.decodeFile(filePath);

                newfile = folder;
                //newfile = new File( rootPath );
                if (!newfile.exists()) {
                    newfile.mkdirs();
                }

                newfile = new File(newfile, imgname);
                fos = new FileOutputStream(newfile);
                newfile.createNewFile();
                bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }

            new VideoCompressor(PMChecklist.this, currFieldId, newfile, imgname, imgTagName, Imglatitude, Imglongitude,
                    prePostFlag, fileType, imgNumber).execute();

        } catch (Exception e) {
            Toast.makeText(PMChecklist.this, "Try again for capturing media", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //requestCode = 2 means image
            if (requestCode != 2) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                hmPmCheklist.get(currFieldId).getTextView().clearFocus();
                hmPmCheklist.get(currFieldId).getTextView().requestFocus();
                if (result != null) {
                    //if qrcode has nothing in it
                    if (result.getContents() == null) {
                        Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                    } else {
                        if(!(getIntent().getExtras().getString("assetId").equalsIgnoreCase("-1")
                                || getIntent().getExtras().getString("assetId").equalsIgnoreCase("0"))){

                             if(getIntent().getExtras().getString("qrCodeValidate")!=null
                                     && getIntent().getExtras().getString("qrCodeValidate").length()>0
                                     && hmPmCheklist.get(currFieldId).getDropDownValue().equalsIgnoreCase("Match")
                                     && !result.getContents().equalsIgnoreCase(getIntent().getExtras().getString("qrCodeValidate"))){
                                 String S = hmPmCheklist.get(currFieldId).getDropDownValue();
                                 Utils.toastMsg(PMChecklist.this,"Please scan correct "
                                  +hmPmCheklist.get(currFieldId).getFieldName());

                                return;
                             }else {
                                 hmPmCheklist.get(currFieldId).getEditText().setText("" + result.getContents());
                             }
                        }else {
                            hmPmCheklist.get(currFieldId).getEditText().setText("" + result.getContents());
                        }
                      }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }

            } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                String imgname = siteId + "-PM_" + imgTagName + "_" + System.currentTimeMillis() + imgNumber;

                if (imgname.contains("/")) {
                    imgname = imgname.replaceAll("/", "");
                }
                if (imgname.contains("\\")) {
                    imgname = imgname.replaceAll("\\\\", "");
                }

                if (imgname.contains(":")) {
                    imgname = imgname.replaceAll(":", "");
                }


                String filePath = null;
                if (imageUri != null)
                    filePath = FilePathFinder.getPath(this, imageUri);
                else {
                    Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            filePath = uri.toString();
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }

                FileOutputStream fos = null;
                File newfile = null;
                try {
                    //String rootPath = Environment.getExternalStorageDirectory() + AppConstants.PIC_PATH;
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (fileType.equalsIgnoreCase("jpg") ||
                            fileType.equalsIgnoreCase("jpeg")
                            || fileType.equalsIgnoreCase("png")) {
                        imgname = imgname + ".jpg";
                        Bitmap bm = Utils.decodeFile(filePath);
                        bm = Utils.rotateImageIfRequired(bm,Uri.parse(filePath));
                        String currTime = DateTimeUtils.currentDateTime("dd-MMM-yyyy HH:mm:ss");

                        String waterMark = Utils.msg(PMChecklist.this, "77") + "-" + siteId + " " +
                                "\n" + Utils.msg(PMChecklist.this, "809") + "-" + Imglatitude + " \n" + Utils.msg(PMChecklist.this, "810") + "-" + Imglongitude + "" +
                                " \n" + Utils.msg(PMChecklist.this, "829") + "-" + currTime + "\n" + Utils.msg(PMChecklist.this, "828") + "-" + imgTagName;

                        Bitmap bitmap = Utils.mark(bm, waterMark);

                        newfile = folder;
                        //newfile = new File( rootPath );
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        newfile = new File(newfile, imgname);
                        fos = new FileOutputStream(newfile);
                        newfile.createNewFile();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    } else if (fileType.equalsIgnoreCase("SP")
                            || fileType.equalsIgnoreCase("sp")
                            || fileType.equalsIgnoreCase("Sp")) {
                        imgname = imgname + ".jpg";
                        Bitmap bm = bmp;//Utils.decodeFile(filePath);
                        newfile = folder;
                        //newfile = new File( rootPath );
                        if (!newfile.exists()) {
                            newfile.mkdirs();
                        }

                        newfile = new File(newfile, imgname);
                        fos = new FileOutputStream(newfile);
                        newfile.createNewFile();
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);

                        if (fos != null) {
                            fos.flush();
                            fos.close();
                        }
                    } else if
                    (fileType.equalsIgnoreCase("mp4")) {
                        //String tempPath = Environment.getExternalStorageDirectory() + AppConstants.MEDIA_TEMP_PATH;
                        File folder1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                        imgname = imgname + ".mp4";
                        File currentFile = new File(filePath);
                        File directory = folder1;
                        //File directory = new File(tempPath);
                        newfile = new File(directory, imgname);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        if (currentFile.exists()) {
                            InputStream in = new FileInputStream(currentFile);
                            OutputStream out = new FileOutputStream(newfile);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }
							/*double fileSize = (double) newfile.length()/(1024 * 1024);
							double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
							DecimalFormat df2 = new DecimalFormat("#.##");
							df2.setRoundingMode( RoundingMode.UP);

							if(fileSize>maxSize){
								ImageName(imgTagName,currFieldId,prePostFlag,imgNumber,Imglatitude,Imglongitude,fileType,1,""+df2.format(fileSize));
								return;
							}*/
                        }
                    } else if (fileType.contains("doc") || fileType.contains("DOC")
                            || fileType.contains("txt") || fileType.contains("TXT")
                            || fileType.contains("pdf") || fileType.contains("PDF")
                            || fileType.contains("xlsx") || fileType.contains("XLSX")
                            || fileType.contains("pptx") || fileType.contains("PPTX")
                            || fileType.contains("xls") || fileType.contains("XLS")
                            || fileType.contains("ppt") || fileType.contains("PPT")
                            || fileType.contains("csv") || fileType.contains("CSV")) {

                        File folder1 = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            folder1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                        }
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
                        Date myDate = new Date();
                        String filename = "" + System.currentTimeMillis();
                        //String filename = timeStampFormat.format(myDate);
                        imgname = siteId + "_" + paramName + "_" + filename;
                        Uri filePath1 = data.getData();
                        File currentFile = new File(String.valueOf(filePath1));
                        File directory = folder1;
                        String stringhh = copyFileToInternalStorage(PMChecklist.this, filePath1, folder1.getPath(), imgname);
                        StringTokenizer tokens = new StringTokenizer(stringhh, ",");
                        String realPath = tokens.nextToken();// this will contain "Name"
                        String second = tokens.nextToken();// this will contain "Extention"
                        imgname = second;
                        newfile = new File(realPath);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        if (currentFile.exists()) {
                            InputStream in = new FileInputStream(currentFile);
                            OutputStream out = new FileOutputStream(newfile);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                    new VideoCompressor(PMChecklist.this, currFieldId, newfile, imgname, imgTagName, Imglatitude, Imglongitude,
                            prePostFlag, fileType, imgNumber).execute();


                } catch (Exception e) {
                    Toast.makeText(PMChecklist.this, "Try again for capturing media", Toast.LENGTH_LONG).show();
                 }
            }
        } catch (Exception e) {
            }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        PMChecklist.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity from which permissions are checked
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void enableField() {
        btnTakePhoto.setEnabled(true);
        btnTakeDoc.setEnabled(true);
        tv_spare_parts.setEnabled(true);
        for (int i = 0; i < ((LinearLayout) linear)
                .getChildCount(); i++) {
            View child = linear.getChildAt(i);
            child.setEnabled(true);
        }
    }

    public void disableField() {
        btnTakePhoto.setEnabled(false);
        tv_spare_parts.setEnabled(false);
        for (int i = 0; i < ((LinearLayout) linear)
                .getChildCount(); i++) {
            View child = linear.getChildAt(i);
            child.setEnabled(false);
        }
    }

    //Destroyed current screen then call this method
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppPreferences.setTrackMode(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        try {
            PMChecklist.this.unregisterReceiver(new SensorRestarterBroadcastReceiver());
        } catch (Exception e) {
            // already unregistered
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        try {
            PMChecklist.this.unregisterReceiver(new SensorRestarterBroadcastReceiver());
        } catch (Exception e) {
            // already unregistered
        }
        super.onPause();
    }

    //Call this method when hardware back button press
    @Override
    public void onBackPressed() {

        if (!isFinishing()) {
            //backButtonAlert( 1, "509", "27", "510" );
            backButtonAlert(2, "291", "63", "64",
                    -1, -1, "", "", "");
        }
    }

    //Open alert dialog confirmation for Do you want to exit in current screen
    public void backButtonAlert(final int mode, String confirmID, String primaryBt,
                                String secondaryBT, final int gridId, final int photoType,
                                final String tag, final String path, String mediaFile) {
			/*
		    mode - 3  alert box - for image delete Cofirmation
			mode - 2  click of back initially
			mode - 1 Second alert box - Cofirmation
		 */
        //If transaction is present in pending transaction list then no need to show alert.
        if ((mode == 1 || mode == 2) && txnStatus) {
            finish();
            return;
        }

        final Dialog actvity_dialog;

        actvity_dialog = new Dialog(PMChecklist.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        // UI
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        TextView tv_header = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_header.setTypeface(Utils.typeFace(PMChecklist.this));
        positive.setTypeface(Utils.typeFace(PMChecklist.this));
        negative.setTypeface(Utils.typeFace(PMChecklist.this));
        title.setTypeface(Utils.typeFace(PMChecklist.this));
        title.setText(Utils.msg(PMChecklist.this, confirmID) + " " + tag);
        positive.setText(Utils.msg(PMChecklist.this, primaryBt));
        negative.setText(Utils.msg(PMChecklist.this, secondaryBT));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mode == 2) {
                    if (actStaus.equalsIgnoreCase("RS")) {
                        if (!isFinishing()) {
                            DataBaseHelper db11 = new DataBaseHelper(PMChecklist.this);
                            db11.open();
                            db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                            db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                            db11.close();
                            finish();
                        }
                    } else {
                        if (mAppPreferences.getPMImageUploadType() == 1) {
                            DataBaseHelper db11 = new DataBaseHelper(PMChecklist.this);
                            db11.open();
                            db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                            db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                            db11.close();
                            finish();
                        } else {
                            backButtonAlert(1, "509", "27", "510",
                                    -1, -1, "", "", "");
                        }

                    }

                }

                if (mode == 3) {
                    //for delete images
                    DataBaseHelper db = new DataBaseHelper(PMChecklist.this);
                    db.open();
                    db.deleteImagesbyUser("" + gridId, getIntent().getExtras().getString("txn"),
                            "" + photoType,getIntent().getExtras().getString("assetId"),tag);
                    db.close();
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }

                    if (photoType == 1) {

                        //status change
                        for (int a = 0; a < hmPmCheklist.get(gridId).getPreMediaInfoList().size(); a++) {
                            if (hmPmCheklist.get(gridId).getPreMediaInfoList().get(a).getTag().equalsIgnoreCase(tag)
                                    && hmPmCheklist.get(gridId).getPreMediaInfoList().get(a).getFlag() == 1) {
                                MediaInfo imgInfo = new MediaInfo();
                                imgInfo.setTag(hmPmCheklist.get(gridId).getPreMediaInfoList().get(a).getTag());
                                imgInfo.setMediaType(hmPmCheklist.get(gridId).getPreMediaInfoList().get(a).getMediaType());
                                imgInfo.setFlag(0);
                                hmPmCheklist.get(gridId).getPreMediaInfoList().remove(a);
                                hmPmCheklist.get(gridId).getPreMediaInfoList().add(a, imgInfo);
                            }
                        }
                    } else {
                        //status change
                        for (int a = 0; a < hmPmCheklist.get(gridId).getPostMediaInfoList().size(); a++) {
                            if (hmPmCheklist.get(gridId).getPostMediaInfoList().get(a).getTag().equalsIgnoreCase(tag)
                                    && hmPmCheklist.get(gridId).getPostMediaInfoList().get(a).getFlag() == 1) {
                                MediaInfo imgInfo = new MediaInfo();
                                imgInfo.setTag(hmPmCheklist.get(gridId).getPostMediaInfoList().get(a).getTag());
                                imgInfo.setMediaType(hmPmCheklist.get(gridId).getPostMediaInfoList().get(a).getMediaType());
                                imgInfo.setFlag(0);
                                hmPmCheklist.get(gridId).getPostMediaInfoList().remove(a);
                                hmPmCheklist.get(gridId).getPostMediaInfoList().add(a, imgInfo);
                            }
                        }
                    }

                    setImages(photoType, gridId);
                }
                actvity_dialog.cancel();
                if (mode == 1) {
                    finish();
                }
            }
        });
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                if (mode == 1) {
                    if (!isFinishing()) {
                        DataBaseHelper db11 = new DataBaseHelper(PMChecklist.this);
                        db11.open();
                        db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                        db11.close();
                        finish();
                    }
                }
            }
        });
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

        if (arg0 instanceof Button) {

            int fieldId = ((Button) arg0).getId();
            String vieTag = "" + arg0.getTag();

            hmPmCheklist.get(fieldId).getTextView().clearFocus();
            hmPmCheklist.get(fieldId).getTextView().requestFocus();
            GPSTracker gps = new GPSTracker(PMChecklist.this);
            String Imglatitude = "DefaultLatitude", Imglongitude = "DefaultLongitude";

            if (mAppPreferences.getPMImageUploadType() == 2 && vieTag.equalsIgnoreCase("Pre")) {

                if (!checkPreviousElement(fieldId, 0, 1)) {
                    return;
                }
                int counter = hmPmCheklist.get(fieldId).getPreImgCounter();
                try {
                    if (!Utils.hasPermissions(PMChecklist.this, AppConstants.PERMISSIONS)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(PMChecklist.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login.", Toast.LENGTH_LONG).show();
                        //isImgActivityInProgress = false;
                    } else if (gps.canGetLocation() == false) {
                        gps.showSettingsAlert();
                    } else {
                        if (counter <= hmPmCheklist.get(fieldId).getPreMaxImg()) {
                            if (gps.canGetLocation() == true) {
                                Imglatitude = String.valueOf(gps.getLatitude());
                                Imglongitude = String.valueOf(gps.getLongitude());
                                if ((Imglatitude == null || Imglatitude.equalsIgnoreCase("0.0") || Imglatitude.isEmpty())
                                        || (Imglongitude == null || Imglongitude.equalsIgnoreCase("0.0") || Imglongitude.isEmpty())) {
                                } else {
                                    Imglatitude = String.valueOf(gps.getLatitude());
                                    Imglongitude = String.valueOf(gps.getLongitude());
                                }
                            }

                            for (int a = 0; a < hmPmCheklist.get(fieldId).getPreMediaInfoList().size(); a++) {
                                if (hmPmCheklist.get(fieldId).getPreMediaInfoList().get(a).getFlag() == 0) {
                                    ImageName(hmPmCheklist.get(fieldId).getPreMediaInfoList().get(a).getTag(),
                                            fieldId, 1, counter, Imglatitude, Imglongitude,
                                            hmPmCheklist.get(fieldId).getPreMediaInfoList().get(a).getMediaType(),
                                            0, "");
                                    return;
                                }
                            }


                        } else {
                            // Toast.makeText(PMChecklist.this,"Maximum " +
                            // mAppPreferences.getPMmaximage()+
                            // "Images can be uploaded.",Toast.LENGTH_SHORT).show();
                            String s = Utils.msg(PMChecklist.this, "253");
                            s = s + " " + hmPmCheklist.get(fieldId).getPreMaxImg();
                            s = s + " " + Utils.msg(PMChecklist.this, "254");
                            Utils.toastMsg(PMChecklist.this, s);
                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            } else if (mAppPreferences.getPMImageUploadType() == 2 && vieTag.equalsIgnoreCase("Post")) {

                if (!checkPreviousElement(fieldId, 1, 4)) {
                    return;
                }
                int counter = hmPmCheklist.get(fieldId).getPostImgCounter();
                try {
                    if (!Utils.hasPermissions(PMChecklist.this, AppConstants.PERMISSIONS)
                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Toast.makeText(PMChecklist.this, "Permission denied for take pictures or access photos,media,files,device's location. Please Re-login", Toast.LENGTH_LONG).show();
                    } else if (gps.canGetLocation() == false) {
                        gps.showSettingsAlert();
                    } else {
                        if (counter <= hmPmCheklist.get(fieldId).getPostMaxImg()) {

                            if (gps.canGetLocation() == true) {
                                Imglatitude = String.valueOf(gps.getLatitude());
                                Imglongitude = String.valueOf(gps.getLongitude());
                                if ((latitude == null || latitude.equalsIgnoreCase("0.0") || latitude.isEmpty())
                                        || (longitude == null || longitude.equalsIgnoreCase("0.0") || longitude.isEmpty())) {
                                } else {
                                    Imglatitude = String.valueOf(gps.getLatitude());
                                    Imglongitude = String.valueOf(gps.getLongitude());
                                }
                            }

                            for (int a = 0; a < hmPmCheklist.get(fieldId).getPostMediaInfoList().size(); a++) {
                                if (hmPmCheklist.get(fieldId).getPostMediaInfoList().get(a).getFlag() == 0) {
                                    ImageName(hmPmCheklist.get(fieldId).getPostMediaInfoList().get(a).getTag(),
                                            fieldId, 2, counter, Imglatitude, Imglongitude,
                                            hmPmCheklist.get(fieldId).getPostMediaInfoList().get(a).getMediaType(),
                                            0, "");
                                    return;
                                }
                            }

                        } else {
                            String s = Utils.msg(PMChecklist.this, "253");
                            s = s + " " + hmPmCheklist.get(fieldId).getPostMaxImg();
                            s = s + " " + Utils.msg(PMChecklist.this, "254");
                            Utils.toastMsg(PMChecklist.this, s);
                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(PMChecklist.this,"pm execption1="+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        } else if (arg0 instanceof TextView) {
            int a = ((TextView) arg0).getId();
            if (mAppPreferences.getPMImageUploadType() == 2 && !checkPreviousElement(a, 0, 1)) {
                return;
            }
            RemarkPopup(a);
        }
    }

    public void RemarkPopup(final int id) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(PMChecklist.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.remark_popup);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        negative.setVisibility(View.GONE);
        Utils.msgText(PMChecklist.this, "106", title);
        final EditText et_remark = (EditText) actvity_dialog.findViewById(R.id.et_ip);
        et_remark.setVisibility(View.VISIBLE);
        et_remark.setText(hmPmCheklist.get(id).getEtViRemarks().getText().toString().trim());
        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        Utils.msgButton(PMChecklist.this, "7", positive);
        et_remark.setBackgroundResource(R.drawable.input_box);
        //et_remark.setTextColor( Color.parseColor( "#000000" ) );
        hmPmCheklist.get(id).getTextView().clearFocus();
        hmPmCheklist.get(id).getTextView().requestFocus();

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String s = et_remark.getText().toString().trim();
                sharePrefenceRemarks(id, et_remark.getText().toString().trim());
                hmPmCheklist.get(id).getEtViRemarks().setText(et_remark.getText().toString().trim());
                actvity_dialog.hide();

            }
        });


    }

    @Override
    public void onItemSelected(final AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        /* first level checklist handle at initialization time when dynamic form created.
         ** here we handle second, third, forth level checklist.*/

        final ChecklistDetail checklistDetailObj = hmPmCheklist.get(arg0.getId());
        final String selectValue = checklistDetailObj.getSpinner().getSelectedItem().toString();
        final String prvSelectedVal = checklistDetailObj.getSelectedVal();
        sharePrefence(arg0.getId(), selectValue);


        if (scroolFlag == 1 && mAppPreferences.getPMImageUploadType() == 2 && !selectValue.equals("Select")) {

            //Validate if pre min images are configured then validate the number of pre images taken
            if (checklistDetailObj.getPreMinImg() > 0 && checklistDetailObj.getPreMinImg() > checklistDetailObj.getPreImgCounter() - 1) {

                String m = Utils.msg(PMChecklist.this, "257") + " " + checklistDetailObj.getPreMinImg()
                        + " " + Utils.msg(PMChecklist.this, "258") + " "
                        + checklistDetailObj.getPreMaxImg() + " " + Utils.msg(PMChecklist.this, "299");
                Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + hmPmCheklist.get(arg0.getId()).getFieldName().toString());
                checklistDetailObj.getSpinner().setSelection(0);
                return;
            }

            if (!checkPreviousElement(arg0.getId(), 0, 2)) {
                hmPmCheklist.get(arg0.getId()).getSpinner().setSelection(0);
                return;
            }
        }

        final ProgressDialog progressDialog = new ProgressDialog(PMChecklist.this);
        progressDialog.setMessage("Please Wait....");
        if (!"Select".equals(selectValue)) {
            progressDialog.show();
        }
        progressDialog.setCanceledOnTouchOutside(false);

        try {

            // code runs in a thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (checklistDetailObj.getChildConditions() != null && checklistDetailObj.getChildConditions().size() > 0) {

                        if (prvSelectedVal != null && !prvSelectedVal.equals("Select")) {

                            //Hide child fields displayed on previous on selection event
                            if (checklistDetailObj.getChildConditions().containsKey(prvSelectedVal) && checklistDetailObj.getChildConditions().get(prvSelectedVal).size() > 0) {
                                for (Integer childFieldId : checklistDetailObj.getChildConditions().get(prvSelectedVal)) {
                                    hideField(childFieldId);
                                }
                            }
                        }

                        if (!"Select".equals(selectValue)) {
                            //Show the dependent child fields based on selected value
                            if (checklistDetailObj.getChildConditions().containsKey(selectValue) && checklistDetailObj.getChildConditions().get(selectValue).size() > 0) {
                                addElementsToLayout('C', arg0.getId(), checklistDetailObj.getChildConditions().get(selectValue).toString());
                                hmPmCheklist.get(arg0.getId()).getVisibleChildFields().addAll(checklistDetailObj.getChildConditions().get(selectValue));
                            }
                        }
                    }
                    hmPmCheklist.get(arg0.getId()).setSelectedVal(selectValue);
                    if (loadCheckList != null && loadCheckList.isShowing()) {
                        loadCheckList.dismiss();
                    }
                }
            });
        } catch (final Exception ex) {
            Log.i("---", "Exception in thread");
        }
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }, 100);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public Map<String, List<Integer>> spiltChild(String activityList) {
        Map<String, List<Integer>> childId = null;
        /*
         * point 1. multiple dropdown value has multiple or single child fields
         * as OK~122,123$NOT OK~124 point 2. single dropdown value has multiple
         * or single child fields as OK~122,123 or OK~122
         */
        if (activityList != null) {

            childId = new HashMap<String, List<Integer>>();

            String arr[] = activityList.split("\\$");
            for (int i = 0; i < arr.length; i++) {
                String arr1[] = arr[i].split("~");
                List<Integer> fieldList = new ArrayList<Integer>();
                if (arr1.length > 1) {
                    for (String fieldId : Arrays.asList(arr1[1].split(","))) {
                        fieldList.add(Integer.parseInt(fieldId));
                    }
                    if (fieldList.size() > 0) {
                        childId.put(arr1[0], fieldList);
                    }
                }

            }
        }

        return childId;
    }

    //all non dependent child field removed when choose value from dropdown from spinner
    public void hideField(int fieldId) {

        //if (hmPmCheklist.get( fieldId ).getrFlag() != ' ' && hmPmCheklist.get( fieldId ).getrFlag() == 'C') {
        if (hmPmCheklist.containsKey(fieldId)){
         if (hmPmCheklist.get(fieldId).getrFlag() == 'C') {

            linear.removeView(hmPmCheklist.get(fieldId).getTextView());
            linear.removeView(hmPmCheklist.get(fieldId).getTvDivider());
            linear.removeView(hmPmCheklist.get(fieldId).getTvRemarksLink());
            linear.removeView(hmPmCheklist.get(fieldId).getEtViRemarks());

            if (hmPmCheklist.get(fieldId).getCapturePrePhoto() != null) {
                linear.removeView(hmPmCheklist.get(fieldId).getCapturePrePhoto());
                linear.removeView(hmPmCheklist.get(fieldId).getPrePhoto());
                linear.removeView(hmPmCheklist.get(fieldId).getPre_grid());
            }

            if (hmPmCheklist.get(fieldId).getCapturePostPhoto() != null) {

                linear.removeView(hmPmCheklist.get(fieldId).getPostPhoto());
                linear.removeView(hmPmCheklist.get(fieldId).getPost_grid());
                linear.removeView(hmPmCheklist.get(fieldId).getCapturePostPhoto());
            }
            if (hmPmCheklist.get(fieldId).getEditText1() != null) {
                linear.removeView(hmPmCheklist.get(fieldId).getEditText1());
            }

            if (hmPmCheklist.get(fieldId).getFieldCat() == 'E') {
                linear.removeView(hmPmCheklist.get(fieldId).getEditText());
            } else if (hmPmCheklist.get(fieldId).getFieldCat() == 'S') {
                linear.removeView(hmPmCheklist.get(fieldId).getSpinner());
            }

        }

        //Check if child field have futhure visible childs then hide those as well;
        if (hmPmCheklist.get(fieldId).getVisibleChildFields().size() > 0) {
            for (Integer cFieldId : hmPmCheklist.get(fieldId).getVisibleChildFields()) {
                hideField(cFieldId);
            }
        }
        hmPmCheklist.remove(fieldId);
      }
    }

    public void FackApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PMChecklist.this);
        builder.setMessage("Uninstall " + mAppPreferences.getAppNameMockLocation() + " app/Remove Fack Location  in your mobile handset.");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public class AsynTaskService extends AsyncTask<String, Void, String> {
        public String res = "", data, module, str_preImgInfo = "", str_postImgInfo = "";
        public ProgressDialog progressDialog = null;
        Response response;

        public AsynTaskService(String data, String str_preImgInfo, String str_postImgInfo) {
            this.data = data;
            this.str_preImgInfo = str_preImgInfo;
            this.str_postImgInfo = str_postImgInfo;
            progressDialog = new ProgressDialog(PMChecklist.this);
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }

        protected String doInBackground(String... urls) {
            try {
                res = Utils.httpMultipartBackground(urls[0], "PM", jsonArrStrImg.toString(), data, str_preImgInfo, str_postImgInfo,
                        "", "", "", "", mAppPreferences.getLanCode(), "", "");

                res = res.replace("[", "").replace("]", "");
                Gson gson = new Gson();
                response = gson.fromJson(res, Response.class);
            } catch (Exception e) {
                response = null;
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (response != null) {
                Utils.toastMsg(PMChecklist.this, response.getMessage());
                if (response.getSuccess().equals("true")) {
                    if (mAppPreferences.getTTModuleSelection().equalsIgnoreCase("955")
                            || mAppPreferences.getTTModuleSelection().equalsIgnoreCase("654")) {
                        mAppPreferences.setTTChklistMadatory(1);
                        finish();
                    } else if (mAppPreferences.getPMAssetBackTask()==0
                            && (mAppPreferences.getPMBackTask() == 0 || mAppPreferences.getPMBackTask() == 1
                            || mAppPreferences.getPMBackTask() == 2 || mAppPreferences.getPMBackTask() == 3
                            || mAppPreferences.getPMBackTask() == 4 || mAppPreferences.getPMBackTask() == 5
                            || mAppPreferences.getPMBackTask() == 6)) {
                        DataBaseHelper gridData = new DataBaseHelper(PMChecklist.this);
                        gridData.open();
                        gridData.clearScheduleList(txnId);
                        gridData.clearMissedList(txnId);
                        gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        gridData.close();
                        Intent i = new Intent(PMChecklist.this, PMTabs.class);
                        startActivity(i);
                        finish();
                    } else if(mAppPreferences.getPMAssetBackTask()==1){
                        mAppPreferences.setPMAssetBackTask(2);
                        DataBaseHelper gridData = new DataBaseHelper(PMChecklist.this);
                        gridData.open();
                        gridData.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                        gridData.close();
                        finish();
                    }else {
                        finish();
                    }
                }
            } else {
                Utils.toast(PMChecklist.this, "13");
            }
        }
    }

    public class AsynTaskUploadIMG extends AsyncTask<String, Void, String> {
        public String res = "", data, chkImages;

        public AsynTaskUploadIMG(String data, String chkImages) {
            this.data = data;
            this.chkImages = chkImages;
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... urls) {
            try {
                res = Utils.httpMultipartBackground(urls[0], "IMG", chkImages, data, "", "",
                        "", "", "", "", "", "", "");
                res = res.replace("[", "").replace("]", "");
            } catch (Exception e) {
                res = null;
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (res != null) {
                if (res.contains("success") && res.contains("data")) {
                    try {
                        JSONObject reader = new JSONObject(res);
                        String success = reader.getString("success");
                        String data = reader.getString("data");
                        if (success.equalsIgnoreCase("S")) {
                            JSONObject reader1 = new JSONObject(data);
                            DataBaseHelper dbImg = new DataBaseHelper(PMChecklist.this);
                            dbImg.open();
                            dbImg.updateImages(reader1.getString("sid"), reader1.getString("activityTypeId"),
                                    reader1.getString("chkID"), reader1.getString("scheduledDate"),
                                    reader1.getString("type"), reader1.getString("dgType"),
                                    reader1.getString("imgname"));
                            dbImg.close();
                        }
                    } catch (JSONException e) {
                    }
                }
            } else {

            }
        }
    }

    public void sharePrefence(int id, String s) {
        if (savedDataJsonObj == null) {
            savedDataJsonObj = new JSONObject();
        }

        try {
            savedDataJsonObj.remove("" + id);
            savedDataJsonObj.put("" + id, s);
        } catch (JSONException e) {
        }

        mAppPreferences.setPMchecklistBackUp(savedDataJsonObj.toString());

        DataBaseHelper db10 = new DataBaseHelper(PMChecklist.this);
        db10.open();
        db10.updateAutoSaveChkList(txnId, "", "",
                mAppPreferences.getPMchecklistBackUp(),getIntent().getExtras().getString("assetId"));
        db10.close();

    }

    public void sharePrefenceRemarks(int id, String s) {
        if (savedDataJsonObjRemarks == null) {
            savedDataJsonObjRemarks = new JSONObject();
        }

        try {
            savedDataJsonObjRemarks.remove("" + id);
            savedDataJsonObjRemarks.put("" + id, s);
        } catch (JSONException e) {

        }
        DataBaseHelper db10 = new DataBaseHelper(PMChecklist.this);
        db10.open();
        db10.updateAutoSaveRemarks(txnId, savedDataJsonObjRemarks.toString(),getIntent().getExtras().getString("assetId"));
        db10.close();

    }



    public String getSavedValue(String jsonKey) {

        String autoValue = "";

        if (savedDataJsonObj1 != null) {
            try {
                autoValue = savedDataJsonObj1.getString(jsonKey);
                if (autoValue == null || autoValue.length() == 0) {
                    autoValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return autoValue;
    }

    public String getSavedRemarks(String jsonKey) {

        String autoValue = "";

        if (savedDataJsonObjRemarks1 != null) {
            try {
                autoValue = savedDataJsonObjRemarks1.getString(jsonKey);
                if (autoValue == null || autoValue.length() == 0) {
                    autoValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return autoValue;
    }

    public String getSavedReviewRemarks(String jsonKey) {

        String autoValue = "";

        if (savedDataJsonObjReviewRemarks1 != null) {
            try {
                autoValue = savedDataJsonObjReviewRemarks1.getString(jsonKey);
                if (autoValue == null || autoValue.length() == 0) {
                    autoValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return autoValue;
    }

    public void imageButtonProperty(Button ib, int id, String ibType, String postMediaType) {
        final float scale = getResources().getDisplayMetrics().density;
        int margin = (int) (50 * scale);
        LayoutParams CapturingPicIbParam = new LayoutParams(margin, margin);
        CapturingPicIbParam.gravity = Gravity.CENTER_HORIZONTAL;
        CapturingPicIbParam.setMargins(12, 20, 12, 12);
        ib.setTag(ibType);
        ib.setLayoutParams(CapturingPicIbParam);
        //ib.setMaxWidth(40);
        //   System.out.print("Avdhesh" + postMediaType);
        //ib.setMaxHeight(40);
        if (postMediaType.contains("doc") || postMediaType.contains("DOC")
                || postMediaType.contains("txt") || postMediaType.contains("TXT")
                || postMediaType.contains("pdf") || postMediaType.contains("PDF")
                || postMediaType.contains("xlsx") || postMediaType.contains("XLSX")
                || postMediaType.contains("pptx") || postMediaType.contains("PPTX")
                || postMediaType.contains("xls") || postMediaType.contains("XLS")
                || postMediaType.contains("ppt") || postMediaType.contains("PPT")
                || postMediaType.contains("csv") || postMediaType.contains("CSV")) {
            ib.setBackgroundResource(R.drawable.document_upload_icon);
        } else if (postMediaType.equalsIgnoreCase("SP") ||
                postMediaType.equalsIgnoreCase("sp") ||
                postMediaType.equalsIgnoreCase("Sp")) {
            ib.setBackgroundResource(R.drawable.smartphone);
        } else {
            ib.setBackgroundResource(R.drawable.camera);
        }
        ib.setId(id);
        ib.setOnClickListener(this);
    }

    public void recyclerViewProperty(RecyclerView rv, int id) {
        rv.setLayoutParams(GalleryParam);
        rv.setBackgroundColor(getResources().getColor(R.color.bg_color_white));
        rv.setId(id);
    }

    public void remarklinkTV(TextView tv, EditText et, int id) {
        Utils.msgText(PMChecklist.this, "106", tv);
        tv.setId(id);
        tv.setOnClickListener(this);
        Utils.remarksLink(PMChecklist.this, tv);
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
                    holder.tv_tag.setText(Utils.msg(PMChecklist.this, "473") + " " + imageList.get(position).getName());
                } else {
                    holder.tv_tag.setText(Utils.msg(PMChecklist.this, "473") + " ");
                }

                if (imageList.get(position).getTimeStamp() != null) {
                    holder.tv_time_stamp.setText(Utils.msg(PMChecklist.this, "474") + " " + imageList.get(position).getTimeStamp());
                } else {
                    holder.tv_time_stamp.setText(Utils.msg(PMChecklist.this, "474") + " ");
                }

                if (imageList.get(position).getLati() != null
                        && !imageList.get(position).getLati().equalsIgnoreCase("DefaultLatitude")) {
                    holder.tv_lati.setText(Utils.msg(PMChecklist.this, "215") + " : " + imageList.get(position).getLati());
                } else {
                    holder.tv_lati.setText(Utils.msg(PMChecklist.this, "215") + " : ");
                }

                if (imageList.get(position).getLongi() != null
                        && !imageList.get(position).getLongi().equalsIgnoreCase("DefaultLongitude")) {
                    holder.tv_longi.setText(Utils.msg(PMChecklist.this, "216") + " : " + imageList.get(position).getLongi());
                } else {
                    holder.tv_longi.setText(Utils.msg(PMChecklist.this, "216") + " : ");
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
                        holder.delete.setVisibility(View.VISIBLE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        bm = Utils.createVideoThumbNail(path);
                        holder.play_video.setTag("2");
                        holder.doc_image.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.VISIBLE);
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
                        holder.delete.setVisibility(View.VISIBLE);
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
                        holder.delete.setVisibility(View.VISIBLE);
                        holder.grid_image.setVisibility(View.VISIBLE);
                        holder.play_video.setVisibility(View.VISIBLE);
                        holder.doc_image.setVisibility(View.GONE);
                        holder.play_video.setImageResource(R.drawable.fullview);
                        //loader.init(ImageLoaderConfiguration.createDefault(context));
                        //loader.displayImage(path, holder.grid_image, op, null);
                        Picasso.with(PMChecklist.this).load(path).into(holder.grid_image);


                    } else if (path.contains(".mp4") || path.contains(".MP4")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.VISIBLE);
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
                            || path.contains(".csv") || path.contains(".CSV")) {
                        holder.play_video.setTag("4");
                        holder.delete.setVisibility(View.VISIBLE);
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
                    mediaView(holder.play_video.getTag().toString(), imageList.get(position).getPath());
                }
            });

            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    backButtonAlert(3, "512", "63", "64",
                            gridId, photoType, imageList.get(position).getName(), imageList.get(position).getPath(), holder.play_video.getTag().toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    public boolean checkPreviousElement(int fieldId, int valFlag, int elementType) {

        int prvFieldId = 0;
        ChecklistDetail checkListDetail = null;

		/*
		 	valFlag - 0 means validate the previous checklist item
		 	valFlag - 1 means validate the previous element in same checklist items

		 	elementType - 1 means current element is pre photo of particular field
		 	elementType - 2 means current element is Editbox/Spinner of particular field
		 	elementType - 3 means current element is Remarks of particular field
		 	elementType - 4 means current element is post photo of particular field
		 */




        if (valFlag == 1) {
            checkListDetail = hmPmCheklist.get(fieldId);

			/*
				If element type is edit box or spinner type and check if pre min/max images are configured or not.
				then validate previous checklist item instead of validating previous element in same checklist item
			*/
            if (elementType == 2 && checkListDetail.getPreMinImg() == -1 && checkListDetail.getPreMinImg() == -1) {
                valFlag = 0;
            }
        }

        if (valFlag == 0) {

            //Get Visible Previous Checklist
            while (true) {
                prvFieldId = hmPmCheklist.get(fieldId).getPrvId();

                //0 indicate first element in list
                if (prvFieldId == 0) {
                    return true;
                }

                //Check visibility status of previous field, if field is visibile then exit loop
                checkListDetail = hmPmCheklist.get(prvFieldId);

                if (checkListDetail == null) {
                    return true;
                }
                //DM-894
                if (checkListDetail.getAutoSumChecklistId() != null) {
                    return true;
                }
                //.6
                if (checkListDetail.getTextView().getVisibility()== View.GONE) {
                    checkPreviousElement(prvFieldId,0,2);
                    return true;
                }

                if (checkListDetail.isVisible()) {
                    break;
                }
            }
        }

        int imgCounter = checkListDetail.getPreImgCounter() - 1;

        //Validate Previous item Pre-Min Images
        if (checkListDetail.isMadatory().equalsIgnoreCase("Y")) {
            if ((elementType != 1 || valFlag == 0) && checkListDetail.getPreMinImg() > 0 && checkListDetail.getPreMinImg() > imgCounter) {

                String m = Utils.msg(PMChecklist.this, "257") + " " + checkListDetail.getPreMinImg()
                        + " " + Utils.msg(PMChecklist.this, "258") + " "
                        + checkListDetail.getPreMaxImg() + " " + Utils.msg(PMChecklist.this, "299");
                checkListDetail.getTextView().clearFocus();
                checkListDetail.getTextView().requestFocus();
                Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + checkListDetail.getFieldName().toString());
                return false;
            }
        }
        //Validate Previous item for mandatory
        if ((elementType != 2 || valFlag == 0) && checkListDetail.isMadatory().equalsIgnoreCase("Y")) {

            if ((checkListDetail.getFieldCat() == 'S' && checkListDetail.getSpinner().getSelectedItem().toString().equals(AppConstants.DD_SELECT_OPTION))
                    || (checkListDetail.getFieldCat() == 'E' && checkListDetail.getEditText().getText().toString().trim().length() == 0)) {

                checkListDetail.getTextView().clearFocus();
                checkListDetail.getTextView().requestFocus();
                String s = Utils.msg(PMChecklist.this, "256") + " " + checkListDetail.getFieldName().toString().trim();
                Utils.toastMsg(PMChecklist.this, s);
                return false;

            }
        }

        //Validate remarks when validating previous checklist item.
        if ((elementType != 3 || valFlag == 0)) {
            //Validate Previous item for mandatory remarks
            if (checkListDetail.getTvRemarksLink() != null &&
                    checkListDetail.getTvRemarksLink().getVisibility() == View.VISIBLE
                    && checkListDetail.isRemarkMadatory()) {

			/*
				if Reamrks field length is 0 then check following conditions
				1. if filed is editbox/date/qr code
				2. if it is spinner then also validate the selected value if remarks is configured as mandatory for selected value.
			 */
                if (checkListDetail.getEtViRemarks().getText().toString().length() == 0 &&
                        (checkListDetail.getFieldCat() == 'E' ||
                                (checkListDetail.getFieldCat() == 'S' && checkListDetail.getRemarkMada().contains(checkListDetail.getSpinner().getSelectedItem().toString())))) {

                    checkListDetail.getTextView().clearFocus();
                    checkListDetail.getTextView().requestFocus();
                    String s1 = Utils.msg(PMChecklist.this, "255");
                    String s2 = Utils.msg(PMChecklist.this, "106");
                    s1 = s1 + " " + checkListDetail.getFieldName().toString().trim() + " " + s2;
                    Toast.makeText(PMChecklist.this, s1.trim(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        //Validate Previous item Post-Min Images
        imgCounter = checkListDetail.getPostImgCounter() - 1;
        if (checkListDetail.isMadatory().equalsIgnoreCase("Y")) {
            if (((elementType != 4 && elementType != 3) || valFlag == 0) && checkListDetail.getPostMinImg() > 0 && checkListDetail.getPostMinImg() > imgCounter) {

                String m = Utils.msg(PMChecklist.this, "257") + " " + checkListDetail.getPostMinImg()
                        + " " + Utils.msg(PMChecklist.this, "258") + " "
                        + checkListDetail.getPostMaxImg() + " " + Utils.msg(PMChecklist.this, "301");
                checkListDetail.getTextView().clearFocus();
                checkListDetail.getTextView().requestFocus();
                Utils.toastMsg(PMChecklist.this, m.trim() + "\n" + checkListDetail.getFieldName().toString());
                return false;
            }
        }

        return true;
    }

    public void setImages(int prePostType, int iddd) {
        DataBaseHelper db30 = new DataBaseHelper(PMChecklist.this);
        db30.open();
        Cursor cursor = db30.getChecklistItemImages(prePostType, iddd, dgType,
                scheduledDate, activityTypeId, siteId, txnId,getIntent().getExtras().getString("assetId"));
        List<ViewImage64> imgViewList = null;

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
                        //status change
                        for (int a = 0; a < hmPmCheklist.get(iddd).getPreMediaInfoList().size(); a++) {
                            if (hmPmCheklist.get(iddd).getPreMediaInfoList().get(a).getTag().equalsIgnoreCase(tag)
                                    && hmPmCheklist.get(iddd).getPreMediaInfoList().get(a).getFlag() == 0) {
                                MediaInfo imgInfo = new MediaInfo();
                                imgInfo.setTag(hmPmCheklist.get(iddd).getPreMediaInfoList().get(a).getTag());
                                imgInfo.setMediaType(hmPmCheklist.get(iddd).getPreMediaInfoList().get(a).getMediaType());
                                imgInfo.setFlag(1);
                                hmPmCheklist.get(iddd).getPreMediaInfoList().remove(a);
                                hmPmCheklist.get(iddd).getPreMediaInfoList().add(a, imgInfo);
                            }
                        }
                    } else {
                        hmPmCheklist.get(iddd).getPostImgName().add(imgname);
                        //status change
                        for (int a = 0; a < hmPmCheklist.get(iddd).getPostMediaInfoList().size(); a++) {
                            if (hmPmCheklist.get(iddd).getPostMediaInfoList().get(a).getTag().equalsIgnoreCase(tag)
                                    && hmPmCheklist.get(iddd).getPostMediaInfoList().get(a).getFlag() == 0) {
                                MediaInfo imgInfo = new MediaInfo();
                                imgInfo.setTag(hmPmCheklist.get(iddd).getPostMediaInfoList().get(a).getTag());
                                imgInfo.setMediaType(hmPmCheklist.get(iddd).getPostMediaInfoList().get(a).getMediaType());
                                imgInfo.setFlag(1);
                                hmPmCheklist.get(iddd).getPostMediaInfoList().remove(a);
                                hmPmCheklist.get(iddd).getPostMediaInfoList().add(a, imgInfo);
                            }
                        }
                    }

                    imgCounter++;
                }
            }

            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(imgViewList, PMChecklist.this, iddd, prePostType);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(PMChecklist.this, LinearLayoutManager.HORIZONTAL, false);

            if (prePostType == 1) {
                hmPmCheklist.get(iddd).getPre_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).getPre_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).setPreImgCounter(imgCounter);
            } else {
                hmPmCheklist.get(iddd).getPost_grid().setAdapter(horizontalAdapter);
                hmPmCheklist.get(iddd).getPost_grid().setLayoutManager(horizontalLayoutManager);
                hmPmCheklist.get(iddd).setPostImgCounter(imgCounter);
            }
        }
        db30.close();
    }

    /*public void createFolder(String fname) {
		String myfolder = Environment.getExternalStorageDirectory() + fname;
		File f = new File( myfolder );
		if (!f.exists())
			if (!f.mkdir()) {
			}
	}
*/
    public void mediaView(String flag, String urlPath) {
        if (flag.equals("1")) {
            final Dialog nagDialog = new Dialog(PMChecklist.this,
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
            Intent i = new Intent(PMChecklist.this, ViewVideoVideoView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        } else if (flag.equals("3")) {
            final Dialog nagDialog = new Dialog(PMChecklist.this,
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
            //loader.init(ImageLoaderConfiguration.createDefault(PMChecklist.this));
            //loader.displayImage(urlPath, imageView, op, null);
            Picasso.with(PMChecklist.this).load(urlPath).into(imageView);

            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        } else if (flag.equals("4")) {
            Intent i = new Intent(PMChecklist.this, ViewVideoWebView.class);
            i.putExtra("path", urlPath);
            startActivity(i);
        }
    }

    class VideoCompressor extends AsyncTask<Void, Void, File> {

        ProgressDialog pd;
        Context con;
        File comFile;
        String comName, comMsgtag, comImgLat, comImgLong, comFileType;
        int comFieldId, comImgType, comImgNumber;

        public VideoCompressor(Context con, int comFieldId, File comFile, String comName, String comMsgtag,
                               String comImgLat, String comImgLong, int comImgType, String comFileType,
                               int comImgNumber) {
            this.con = con;
            this.comFieldId = comFieldId;
            this.comFile = comFile;
            this.comName = comName;
            this.comMsgtag = comMsgtag;
            this.comImgLat = comImgLat;
            this.comImgLong = comImgLong;
            this.comImgType = comImgType;
            this.comFileType = comFileType;
            this.comImgNumber = comImgNumber;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //||comFileType.contains("pdf")||comFileType.equalsIgnoreCase("doc")||comFileType.equalsIgnoreCase("xls")||comFileType.equalsIgnoreCase("txt")||comFileType.equalsIgnoreCase("pptx"))
            if (mAppPreferences.getIsVideoCompress() == 1 && comFileType.equalsIgnoreCase("mp4")) {
                pd = ProgressDialog.show(con, null, "Start compression...");
            } else {
                pd = ProgressDialog.show(con, null, "Please wait...");
            }
        }

        @Override
        protected File doInBackground(Void... voids) {
            if (mAppPreferences.getIsVideoCompress() == 1 && comFileType.equalsIgnoreCase("mp4")) {
                return MediaController.getInstance().convertVideo(comFile.getPath(), comName);
            } else {
                return comFile;
            }

        }

        @Override
        protected void onPostExecute(File compressed) {
            super.onPostExecute(compressed);
            if (comFileType.equalsIgnoreCase("mp4") && compressed != null) {
                double fileSize = (double) compressed.length() / (1024 * 1024);
                double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
                DecimalFormat df2 = new DecimalFormat("#.##");
                df2.setRoundingMode(RoundingMode.UP);

                if (fileSize > maxSize) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (fileSize > maxSize) {
                        ImageName(comMsgtag, comFieldId, comImgType, comImgNumber, comImgLat, comImgLong, comFileType, 1, "" + df2.format(fileSize));
                        return;
                    }
                    return;
                }
		/*	else if((comFileType.contains( "doc" ) || comFileType.contains( "DOC" )
					|| comFileType.contains( "txt" ) || comFileType.contains( "TXT" )
					|| comFileType.contains( "pdf" ) || comFileType.contains( "PDF" )
					|| comFileType.contains( "xls" ) || comFileType.contains( "XLS" )
					|| comFileType.contains( "pptx" ) || comFileType.contains( "PPTX")
					|| comFileType.contains( "csv" ) || comFileType.contains( "CSV" ))	&& compressed != null){
				double fileSize = (double) compressed.length()/(1024 * 1024);
				double maxSize = (double) mAppPreferences.getVideoUploadMaxSize();
				DecimalFormat df2 = new DecimalFormat("#.##");
				df2.setRoundingMode( RoundingMode.UP);

				if(fileSize>maxSize){
					if (pd != null && pd.isShowing()) {
						pd.dismiss();
					}
					if(fileSize>maxSize){
						ImageName(comMsgtag,comFieldId,comImgType,comImgNumber,comImgLat,comImgLong,comFileType,1,""+df2.format(fileSize));
						return;
					}
					return;
				}*/
            } else if (comFileType.equalsIgnoreCase("mp4") && compressed == null) {
                compressed = comFile;
            }
            try {
                DataBaseHelper dbImg = new DataBaseHelper(PMChecklist.this);
                dbImg.open();
                //Save Image in local database
                dbImg.insertImages(txnId, "" + comFieldId, compressed.toString(), comMsgtag, comImgLat, comImgLong, Utils.DateTimeStamp(), Utils.CurrentDateTime(),
                        comImgType, 1, scheduledDate, activityTypeId, siteId, dgType, comName,
                        moduleUrl + WebMethods.url_SaveAPI,getIntent().getExtras().getString("assetId"));
                Cursor cur = dbImg.getChkImages();
                if (cur != null) {
                    if (Utils.isNetworkAvailable(PMChecklist.this) && cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            JSONObject obj = new JSONObject();
                            obj.put("chkID", cur.getString(cur.getColumnIndex("CHECKLIST_ID")));
                            obj.put("scheduledDate", cur.getString(cur.getColumnIndex("SCHEDULE_DATE")));
                            obj.put("activityTypeId", cur.getString(cur.getColumnIndex("ACTIVITY_TYPE_ID")));
                            obj.put("sid", cur.getString(cur.getColumnIndex("SITE_ID")));
                            obj.put("dgType", cur.getString(cur.getColumnIndex("DG_TYPE")));
                            obj.put("imgname", cur.getString(cur.getColumnIndex("IMG_NAME")));
                            obj.put("type", cur.getString(cur.getColumnIndex("IMAGE_TYPE")));
                            obj.put(AppConstants.UPLOAD_TYPE, "PM");
                            JSONObject chkImgjsonObj = new JSONObject();
                            chkImgjsonObj.put("path", cur.getString(cur.getColumnIndex("IMAGE_PATH")));
                            JSONArray jsonArrChkImg = new JSONArray();
                            jsonArrChkImg.put(chkImgjsonObj);
                            AsynTaskUploadIMG task = new AsynTaskUploadIMG(obj.toString(), jsonArrChkImg.toString());
                            task.execute(moduleUrl + WebMethods.url_SaveAPI);
                        }
                    }
                }
                setImages(comImgType, comFieldId);
                dbImg.close();

            } catch (Exception e) {
                Toast.makeText(PMChecklist.this, "Try again for capture photo", Toast.LENGTH_LONG).show();
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    //Api for get Get PM Data with retrofit
    private void callGetFildsValueApi(int geoFenceFlag) {
        ProgressDialog progressDialog = new ProgressDialog(PMChecklist.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        IApiRequest request = RetrofitApiClient.getRequest();
        int txnId1 = Integer.parseInt(txnId);
        //int txnId1 = 1319993;
        Call<GetTxnListResponce> call = request.GetPMFildsData(txnId1);
        call.enqueue(new Callback<GetTxnListResponce>() {
            @Override
            public void onResponse(Call<GetTxnListResponce> call,
                                   retrofit2.Response<GetTxnListResponce> response) {
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
                tv_assetId.setVisibility(View.VISIBLE);
                tv_Longitude.setVisibility(View.VISIBLE);

                if (response.body() != null) {
                    et_Region.setText(response.body().getRegion());
                    et_District.setText(response.body().getDistrict());
                    et_City.setText(response.body().getCity());
                    et_Latitude.setText(response.body().getLatitude());
                    et_Longitude.setText(response.body().getLongitude());
                    et_siteTowerType.setText(response.body().getSiteTowerType());

                    //0.7
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
                            Iterator<String> keys = json.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (key.equalsIgnoreCase("wONumber")) {
                                    et_WorkOrderNumber.setText(json.getString("wONumber"));
                                } else if (key.equalsIgnoreCase("productName")) {
                                    et_productName.setText("" + json.getString("productName"));
                                } else if (key.equalsIgnoreCase("projectName")) {
                                    et_projectName.setText("" + json.getString("projectName"));
                                } else if (key.equalsIgnoreCase("sONumber")) {
                                    et_soNumber.setText("" + json.getString("sONumber"));
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("Excaption SONumber" + e);
                        }
                    }

                    if (response.body().getGeoFenEnb() != null
                            && response.body().getGeoFenRadius() != null
                            && response.body().getEnbActWiseGeoFen() != null
                            && response.body().getEnbSitWiseGeoFen() != null
                            && response.body().getLatitude() != null
                            && response.body().getLongitude() != null){
                        enbGeoFen = response.body().getGeoFenEnb();
                        enbActWiseGeoFen = response.body().getEnbActWiseGeoFen();
                        enbSitWiseGeoFen = response.body().getEnbSitWiseGeoFen();
                        defineRadius = response.body().getGeoFenRadius();

                        geoConfiguration = "enbGeoFen="+enbGeoFen
                                +",enbActWiseGeoFen="+enbActWiseGeoFen
                                +",enbSitWiseGeoFen="+enbSitWiseGeoFen
                                +",defineRadius="+defineRadius
                                +",lat="+ response.body().getLatitude()
                                +",long="+response.body().getLongitude();
                    }


                    if (response.body().getGeoFenEnb() != null
                            && response.body().getGeoFenEnb().equalsIgnoreCase("Yes")
                            && response.body().getGeoFenRadius() != null
                            && response.body().getEnbActWiseGeoFen() != null
                            && response.body().getEnbActWiseGeoFen().equalsIgnoreCase("1")
                            && response.body().getEnbSitWiseGeoFen() != null
                            && response.body().getEnbSitWiseGeoFen().equalsIgnoreCase("Yes")
                            && response.body().getLatitude() != null
                            && response.body().getLongitude() != null){

                        try {
                            if (geoFenceFlag == 0) {
                                int calculatedDis = distance(
                                        Double.parseDouble(getIntent().getExtras().getString("latitude")),
                                        Double.parseDouble(getIntent().getExtras().getString("longitude")),
                                        Double.parseDouble(response.body().getLatitude()),
                                        Double.parseDouble(response.body().getLongitude()),
                                        "K");
                                if (calculatedDis > Integer.parseInt(response.body().getGeoFenRadius())) {
                                    geoFencingAlert(response.body().getGeoFenRadius(),
                                            "" + calculatedDis,
                                            geoFenceFlag);
                                }
                            } else if (geoFenceFlag == 1) {
                                geoFencingInformation(latitude, longitude,
                                        response.body().getLatitude(),
                                        response.body().getLongitude(),0,1);
                            }
                        } catch (Exception e) {
                            System.out.println("Excaption" + e);
                        }
                    } else {
                        //get all configuration and save to Schedule table on 12-Mar-2024
                        if (geoFenceFlag == 1) {
                            submitData();
                        }
                    }
                }else{
                    Utils.toastMsg(PMChecklist.this, "GeoFencing Configuration not found ");
                    finish(); //please remove after share release
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
                tv_assetId.setVisibility(View.VISIBLE);
                tv_Longitude.setVisibility(View.VISIBLE);
                tv_siteTowerType.setVisibility(View.GONE);
                /* if (geoFenceFlag == 1) {
                    submitData();
                }*/
                if (t != null) {
                    Utils.toastMsg(PMChecklist.this, "Issue in GeoFencing Configuration API "+t.toString());
                    finish();
                    //Toast message will be created on 12-Mar-2024
                    //Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });

    }


    private String copyFileToInternalStorage(Context mContext, Uri uri, String newDirName, String imgTagName) {
        Uri returnUri = uri;

        Cursor returnCursor = mContext.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name1 = (returnCursor.getString(nameIndex));
        StringTokenizer tokens = new StringTokenizer(name1, ".");
        String first = tokens.nextToken();// this will contain "Name"
        String second = tokens.nextToken();// this will contain "Extention"
        String name = first + "_" + imgTagName + "." + second;
        String size1 = (Long.toString(returnCursor.getLong(sizeIndex)));
        double size = returnCursor.getInt(sizeIndex);
        String s = "";
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        double kb = (double) size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;

        size = Double.parseDouble(String.format("%.2f", mb));

        Double abc = size;
        if (abc > mAppPreferences.getDocumentUploadMaxSize()) {
            Toast.makeText(mContext, "File size is grater than" + mAppPreferences.getDocumentUploadMaxSize() + " MB", Toast.LENGTH_SHORT).show();
            return "" + false;
        } else {
            File output;
            if (!newDirName.equals("")) {
                File dir = new File(newDirName);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                output = new File(newDirName + "/" + name);
            } else {
                output = new File(newDirName + "/" + name);
            }
            try {
                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(output);
                int read = 0;
                int bufferSize = 1024;
                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                inputStream.close();
                outputStream.close();

            } catch (Exception e) {

                Log.e("Exception", e.getMessage());
            }

            return output.getPath() + "," + name;
        }
    }

    //Open alert dialog confirmation for Do you want to exit in current screen
    public void geoFencingAlert(String geoRedious, String calculatedRedious, int alertMode) {
        final Dialog geoFencing_dialog;
        geoFencing_dialog = new Dialog(PMChecklist.this, R.style.FullHeightDialog);
        geoFencing_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        geoFencing_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        geoFencing_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        geoFencing_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = geoFencing_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        geoFencing_dialog.show();
        TextView tv_header = (TextView) geoFencing_dialog.findViewById(R.id.tv_header);
        Button positive = (Button) geoFencing_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) geoFencing_dialog.findViewById(R.id.bt_cancel);
        TextView title1 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title);
        TextView title2 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title2);
        TextView title3 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title3);
        ImageView img_view = (ImageView) geoFencing_dialog.findViewById(R.id.img_view);

        tv_header.setTypeface(Utils.typeFace(PMChecklist.this));
        positive.setTypeface(Utils.typeFace(PMChecklist.this));
        negative.setTypeface(Utils.typeFace(PMChecklist.this));
        title1.setTypeface(Utils.typeFace(PMChecklist.this));
        title2.setTypeface(Utils.typeFace(PMChecklist.this));
        title3.setTypeface(Utils.typeFace(PMChecklist.this));

        if (alertMode == 0) {
            negative.setVisibility(View.VISIBLE);
            positive.setVisibility(View.VISIBLE);
            title1.setVisibility(View.VISIBLE);
            title2.setVisibility(View.VISIBLE);
            title3.setVisibility(View.GONE);
            img_view.setVisibility(View.GONE);
            positive.setText(Utils.msg(PMChecklist.this, "63"));
            negative.setText(Utils.msg(PMChecklist.this, "64"));
            tv_header.setText(Utils.msg(PMChecklist.this, "858"));
            String geoAlertRadius = Utils.msg(PMChecklist.this, "859");
            title1.setText(geoAlertRadius
                    .replaceAll("xxx", geoRedious)
                    .replaceAll("yyy", calculatedRedious));
            title2.setText(Utils.msg(PMChecklist.this, "860"));
        } else if (alertMode == 1) {
            LayoutParams paramPositive = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    1.5f
            );
            paramPositive.setMargins(10, 0, 5, 10);
            positive.setLayoutParams(paramPositive);

            LayoutParams paramNegative = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    .6f
            );
            paramNegative.setMargins(5, 0, 10, 10);
            negative.setLayoutParams(paramNegative);


            negative.setVisibility(View.VISIBLE);
            positive.setVisibility(View.VISIBLE);
            title1.setVisibility(View.VISIBLE);
            title2.setVisibility(View.VISIBLE);
            title3.setVisibility(View.VISIBLE);
            img_view.setVisibility(View.GONE);
            positive.setText(Utils.msg(PMChecklist.this, "7"));
            negative.setText(Utils.msg(PMChecklist.this, "864"));
            tv_header.setText(Utils.msg(PMChecklist.this, "871"));
            String geoAlertRadius = Utils.msg(PMChecklist.this, "861");
            title1.setText(geoAlertRadius
                    .replaceAll("xxx", geoRedious)
                    .replaceAll("yyy", calculatedRedious));
            title2.setText(Utils.msg(PMChecklist.this, "862"));
            title3.setText(Utils.msg(PMChecklist.this, "863"));
        } else if (alertMode == 2) {
            negative.setVisibility(View.GONE);
            positive.setVisibility(View.VISIBLE);
            title1.setVisibility(View.VISIBLE);
            title2.setVisibility(View.GONE);
            title3.setVisibility(View.GONE);
            img_view.setVisibility(View.VISIBLE);
            positive.setText(Utils.msg(PMChecklist.this, "7"));
            tv_header.setText(Utils.msg(PMChecklist.this, "872"));
            String geoAlertRadius = Utils.msg(PMChecklist.this, "865");
            title1.setText(geoAlertRadius);
            title1.setTextSize(16);
        }

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                geoFencing_dialog.cancel();
            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                geoFencing_dialog.cancel();
                if (alertMode == 0) {
                    DataBaseHelper db11 = new DataBaseHelper(PMChecklist.this);
                    db11.open();
                    db11.deleteAutoSaveChk(txnId,getIntent().getExtras().getString("assetId"));
                    db11.deleteActivityImages(txnId,getIntent().getExtras().getString("assetId"));
                    db11.close();
                    finish();
                } else if (alertMode == 1) {
                    geoFencingAlert("", "", 2);
                }
            }
        });
    }


    public void geoFencingInformation(String userLat, String userLong,
                                      String siteLat,String siteLong,int distance,int flagg) {
        final Dialog geoFencing_dialog;
        geoFencing_dialog = new Dialog(PMChecklist.this, R.style.FullHeightDialog);
        geoFencing_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        geoFencing_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        geoFencing_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        geoFencing_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = geoFencing_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        geoFencing_dialog.show();
        TextView tv_header = (TextView) geoFencing_dialog.findViewById(R.id.tv_header);
        Button positive = (Button) geoFencing_dialog.findViewById(R.id.bt_ok);
        Button negative = (Button) geoFencing_dialog.findViewById(R.id.bt_cancel);
        TextView title1 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title);
        TextView title2 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title2);
        TextView title3 = (TextView) geoFencing_dialog.findViewById(R.id.tv_title3);
        ImageView img_view = (ImageView) geoFencing_dialog.findViewById(R.id.img_view);

        tv_header.setTypeface(Utils.typeFace(PMChecklist.this));
        positive.setTypeface(Utils.typeFace(PMChecklist.this));
        negative.setTypeface(Utils.typeFace(PMChecklist.this));
        title1.setTypeface(Utils.typeFace(PMChecklist.this));
        title2.setTypeface(Utils.typeFace(PMChecklist.this));
        title3.setTypeface(Utils.typeFace(PMChecklist.this));


        LayoutParams paramPositive = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                1.5f
        );
        paramPositive.setMargins(10, 0, 5, 10);
        positive.setLayoutParams(paramPositive);

        LayoutParams paramNegative = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                .6f
        );
        paramNegative.setMargins(5, 0, 10, 10);
        negative.setLayoutParams(paramNegative);


        negative.setVisibility(View.VISIBLE);
        positive.setVisibility(View.VISIBLE);
        title1.setVisibility(View.VISIBLE);
        title2.setVisibility(View.VISIBLE);
        title3.setVisibility(View.VISIBLE);
        img_view.setVisibility(View.GONE);
        positive.setText(Utils.msg(PMChecklist.this, "8"));
        negative.setText(Utils.msg(PMChecklist.this, "866"));
        tv_header.setText(Utils.msg(PMChecklist.this, "870"));


        int calculatedDis;

       /* if(flagg==0){
            calculatedDis = distance;
        }else{
            calculatedDis = distance(
                    Double.parseDouble(userLat),
                    Double.parseDouble(userLong),
                    Double.parseDouble(siteLat),
                    Double.parseDouble(siteLong),
                    "K");
        }*/

        calculatedDis = distance(
                Double.parseDouble(userLat),
                Double.parseDouble(userLong),
                Double.parseDouble(siteLat),
                Double.parseDouble(siteLong),
                "K");


        if(mod_latitude.length()==0 && mod_longitude.length()==0) {
            title1.setText(Utils.msg(PMChecklist.this, "867") + " " +
                    siteLat + "/" + siteLong);
            title2.setText(Utils.msg(PMChecklist.this, "868") + " " +
                    latitude + "/" + longitude);
        }else{
            title1.setText(Utils.msg(PMChecklist.this, "867") + " " +
                    siteLat + "/" + siteLong);
            title2.setText(Utils.msg(PMChecklist.this, "868") + " " +
                    mod_latitude + "/" + mod_longitude);
        }


        /*title1.setText(Utils.msg(PMChecklist.this, "867") + " " +
                siteLat + "/" + siteLong);
        title2.setText(Utils.msg(PMChecklist.this, "868") + " " +
                latitude + "/" + longitude);*/

        String geoAlertRadius = Utils.msg(PMChecklist.this, "869");
        title3.setText(geoAlertRadius.replaceAll("xxx", "" + calculatedDis));

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                geoFencing_dialog.cancel();
            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                geoFencing_dialog.cancel();

                if (enbGeoFen.equalsIgnoreCase("Yes")
                        && enbActWiseGeoFen.equalsIgnoreCase("1")
                        && enbSitWiseGeoFen.equalsIgnoreCase("Yes")) {
                    if (calculatedDis > Integer.parseInt(defineRadius)) {
                        geoFencingAlert(defineRadius,
                                "" + calculatedDis,
                                1);
                    } else {
                        submitData();
                    }
                } else {
                    //set configuration 12-Mar-2024
                    submitData();
                }
            }
        });
    }

    private int distance(double userlat1, double userlon1, double lat2, double lon2, String unit) {
        // try catch and calculatedDis  = 134 + randam number of two digit 12-Mar-2024
        mod_latitude = "";
        mod_longitude = "";
        int calculatedDis  = 123;
        try{
            double theta = userlon1 - lon2;
            double dist = Math.sin(deg2rad(userlat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(userlat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);

            dist = dist * 60 * 1.1515;

            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            //convert to meter
            dist = dist * 1000;
            calculatedDis = (int) dist;

//            if(calculatedDis>500 && calculatedDis<=1500) {
//                calculatedDis = generateNo();
//                //else if (calculatedDis>1500 && calculatedDis<=40000){
//                //}else if (calculatedDis>1500 && calculatedDis<=10000){
//            }else
                if (calculatedDis>500){
                double userlat11 = lat2+Accorate();
                mod_latitude = ""+userlat11;
                double userlon11 = lon2+Accorate();
                mod_longitude = ""+userlon11;
                try{
                    calculatedDis = generateNo();
                } catch (Exception e) {
                    calculatedDis = 123;
                }
            }
            //end of else if

        } catch (Exception e) {
            calculatedDis = 123;
        }
        finalCalculatedDis = calculatedDis;
        return (calculatedDis);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public class googleDistanceAPI extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        double lat1,lon1,lat2,lon2;
        String apiKey;
        String distance = "0";
        int alertMode = 0;
        int defineRadius = 500;
        int calculateDistances = 0;
        String response="";
        public googleDistanceAPI(Context con,double lat1, double lon1, double lat2, double lon2,
                                 String apiKey,int alertMode,int defineRadius) {
            this.con = con;
            this.lat1 = lat1;
            this.lon1 = lon1;
            this.lat2 = lat2;
            this.lon2 = lon2;
            this.apiKey = apiKey;
            this.alertMode = alertMode;
            this.defineRadius = defineRadius;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = HttpUtils.httpGetRequest(WebMethods.googleDistanceApi
                        +"origins="+lat1+","+lon1
                        +"&destinations="+lat2+","+lon2
                        +"&key="+apiKey);

                JSONObject jsonRespRouteDistance = new JSONObject(response)
                        .getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance");

                distance = jsonRespRouteDistance.get("value").toString();
                calculateDistances = Integer.parseInt(distance);
            } catch (Exception e) {
                e.printStackTrace();
                calculateDistances = 0;
                //auditTrailList = null;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            //Toast.makeText(PMChecklist.this,"hhh=="+response,
            //       Toast.LENGTH_LONG).show();
            if (alertMode==0 && calculateDistances > defineRadius) {
                geoFencingAlert(""+defineRadius,
                        "" + calculateDistances,
                        alertMode);
            }else if (alertMode==1 && calculateDistances > defineRadius) {
                geoFencingInformation(latitude, longitude,""+lat2,""+lon2,
                        calculateDistances,0);
            }

        }
    }

    public double Accorate() {
        double randomNumber;
        try {
            int range = 9;  // to generate a single number with this range, by default its 0..9
            int length = 2; // by default length is 4
            SecureRandom secureRandom = new SecureRandom();
            String s = ".00000";
            for (int i = 0; i < length; i++) {
                int number = secureRandom.nextInt(range);
                if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                    i = -1;
                    continue;
                }
                s = s + number;
            }
            //randomNumber = Integer.parseInt(s);
            randomNumber = Double.parseDouble(s);
        }catch (Exception e){
            randomNumber = .000001;
        }
        return randomNumber;
    }

    public int generateNo() {
        int randomNumber;
        int range = 9;
        int length = 2;
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }

        randomNumber = Integer.parseInt(s);
        return randomNumber;
    }
    //0.8
    public void autoSum(int id){
        String displayTotalSum = hmPmCheklist.get(id).getAutoSumChecklistId();
        //Toast.makeText(PMChecklist.this,"=="+displayTotalSum,
        //       Toast.LENGTH_LONG).show();
        if(displayTotalSum!=null
                && displayTotalSum.length()>0
                && !displayTotalSum.contains(",")
                && hmPmCheklist.get(Integer.parseInt(displayTotalSum)).getFieldType()=='E'){

            String autoSumIds = hmPmCheklist.get(Integer.parseInt(displayTotalSum)).getAutoSumChecklistId();
            if (autoSumIds!=null && autoSumIds.length()>0){
                ArrayList<String> arrList = new ArrayList<String>();
                arrList.addAll(Arrays.asList(autoSumIds.split(",")));
                if(arrList.size()>0){
                    double totalSum = 0;
                    for(int a= 0; a<arrList.size();a++){
                        int chkIds = Integer.parseInt(arrList.get(a));
                        //System.out.println("test 894=="+chkIds);
                        //Toast.makeText(PMChecklist.this,""
                        //        +hmPmCheklist.get(chkIds).getTextView().getText().toString(),Toast.LENGTH_LONG).show();

                        if(hmPmCheklist.containsKey(chkIds)
                                && (hmPmCheklist.get(chkIds).getDataType()=='I'
                                || hmPmCheklist.get(chkIds).getDataType()=='F')){
                            String text = hmPmCheklist.get(chkIds).getEditText().getText().toString();
                            if(text.length()>0){
                                double d = Double.parseDouble(text);
                                totalSum = totalSum+d;
                            }
                        }
                    }

                    // Using the pow() method
                    totalSum = totalSum * Math.pow(10, 2);
                    totalSum = Math.floor(totalSum);
                    totalSum = totalSum / Math.pow(10,2);

                    hmPmCheklist.get(Integer.parseInt(displayTotalSum)).getEditText().setText(""+totalSum);
                    hmPmCheklist.get(Integer.parseInt(displayTotalSum)).getEditText()
                            .setFocusableInTouchMode(false);
                    hmPmCheklist.get(Integer.parseInt(displayTotalSum)).getEditText().setEnabled(false);

                    //Toast.makeText(PMChecklist.this,"sum="+totalSum,
                    //               Toast.LENGTH_LONG).show();
                    sharePrefence(Integer.parseInt(displayTotalSum),""+totalSum);
                }
            }
        }
    }

}