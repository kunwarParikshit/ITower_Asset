package com.isl.audit.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.isl.api.ApiClient;
import com.isl.api.IApiRequest;
import com.isl.audit.adapter.AdapterAttributesItems;
import com.isl.audit.db.AppDatabase;
import com.isl.audit.db.AuditModel;
import com.isl.audit.model.AssetListResult;
import com.isl.audit.model.AttributeResult;
import com.isl.audit.model.AuditAssetResponse;
import com.isl.audit.model.AuditAssetResult;
import com.isl.audit.model.ImageModel;
import com.isl.audit.model.UploadImageResponse;
import com.isl.audit.model.ValueModel;
import com.isl.audit.util.DateUtil;
import com.isl.audit.util.ItemClickListener;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.GPSTracker;
import com.isl.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* This activity is for form rendering depending upon asset type selected.
 * All the attributes of selected asset type are displayed
 * 5 different UI are created for each attribute type i.e TEXT,NUMBER,DATE,PICKLIST,IMAGE */

public class AddAssetActivity extends AppCompatActivity implements ItemClickListener {
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.spinner_asset_type)
    Spinner spinnerAssetType;

    @BindView(R.id.rv_attributes)
    RecyclerView rvAttributes;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_parent_asset_value)
    TextView tvParentAssetValue;
    @BindView(R.id.tv_add_parent_asset)
    TextView tvAddParentAsset;
    @BindView(R.id.tv_select_asset_type)
    TextView tvSelectAssetType;

    private boolean isEdit = false, isAssetExist, enableAddParent, scanQR;
    private double lat, lng;

    private int selectedAssetPos, imageCapturePos, maxImagesAllowed, editPos, type, dbAuditPos, imgPos = 0, adapterPos = 0;
    private File destination;
    private String filePath, auditId, assetName, siteId, auditName, parent, qrCode, parentName, imgBaseUrl;
    private IntentIntegrator qrScan;
    private int assetPos = -1,assetTypeId=0;

    private ProgressDialog progressDialog;
    private ArrayList<String> qrList = new ArrayList<>();
    private List<AssetListResult> assetList = new ArrayList<>();
    private List<AssetListResult> assetListFinal = new ArrayList<>();
    private List<AssetListResult> assetListFinalForParent = new ArrayList<>();
    private List<AuditAssetResponse> auditList = new ArrayList<>();
    private List<AuditAssetResult> auditAssetList = new ArrayList<>();
    private List<AttributeResult> attributesList = new ArrayList<>();
    private List<Integer> auditAssetAttributesList = new ArrayList<>();
    private HashMap<String, ArrayList<Integer>> image_tag;
    private List<Integer> childAssetList = new ArrayList<>();
    private List<AttributeResult> attributesListFinal = new ArrayList<>();
    private final List<String> assetNamesList = new ArrayList<>();
    private AdapterAttributesItems adapterAttributesItems;
    private AssetListResult assetListModel;
    private AppDatabase appDatabase;
    private boolean addChild, addParent;

    private AuditModel auditModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.header));
        }

        DataBaseHelper db = new DataBaseHelper(this);
        db.open();
        imgBaseUrl = db.getModuleIP("Audits");
        if(imgBaseUrl.contains("~")){
            imgBaseUrl=imgBaseUrl.substring(imgBaseUrl.indexOf("~")+1);
        }
        if (imgBaseUrl.contains("http") || imgBaseUrl.contains("https")) {

        } else {
            imgBaseUrl = "https://" + imgBaseUrl;
        }
        imgBaseUrl=imgBaseUrl+"/";

        appDatabase = AppDatabase.getInstance(this);
        auditId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID);
        type = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, 0);
        siteId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.SITE_ID);
        auditName = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME);
        editPos = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.POSITION, -1);
        qrList = getIntent().getStringArrayListExtra(AppConstants.INTENT_EXTRAS.QR_LIST);
        assetPos = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.ASSET_POSITION, -1);
        if (getIntent().hasExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL)) {
            isEdit = true;
            assetListModel = (AssetListResult) getIntent().getSerializableExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL);
            parent = assetListModel.getParent();
            qrCode=assetListModel.getQr_code();
            parentName = assetListModel.getParentName();
            if (!TextUtils.isEmpty(parent)) {
                tvAddParentAsset.setText(getString(R.string.parent_asset));
                tvParentAssetValue.setText(parent);
            }
        }
        if (TextUtils.isEmpty(parent) && getIntent().hasExtra(AppConstants.INTENT_EXTRAS.PARENT)) {
            parent = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.PARENT);
            parentName = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.PARENT_NAME);
            if (!TextUtils.isEmpty(parent)) {
                tvAddParentAsset.setText(getString(R.string.parent_asset));
                tvParentAssetValue.setText(parent);
                tvAddParentAsset.setVisibility(View.VISIBLE);
                tvParentAssetValue.setVisibility(View.VISIBLE);
            }

        }
        if (getIntent().hasExtra(AppConstants.INTENT_EXTRAS.ADD_CHILD)) {
            addChild = getIntent().getBooleanExtra(AppConstants.INTENT_EXTRAS.ADD_CHILD, false);
            tvTitle.setText(getString(R.string.add_child_asset));
            tvSelectAssetType.setText(getString(R.string.select_child_asset_type));
            assetTypeId=getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.ASSET_TYPE_ID,0);
            childAssetList = getIntent().getIntegerArrayListExtra(AppConstants.INTENT_EXTRAS.CHILD_ASSET_LIST);
        } else if (getIntent().hasExtra(AppConstants.INTENT_EXTRAS.ADD_PARENT)) {
            addParent = getIntent().getBooleanExtra(AppConstants.INTENT_EXTRAS.ADD_PARENT, false);
            assetTypeId=getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.ASSET_TYPE_ID,0);
            tvTitle.setText(getString(R.string.add_parent_asset2));
            tvSelectAssetType.setText(getString(R.string.select_parent_asset_type));
            childAssetList = getIntent().getIntegerArrayListExtra(AppConstants.INTENT_EXTRAS.PARENT_ASSET_LIST);

        }
        tvTitle.setTypeface(null, Typeface.BOLD);
        getSavedData();


        spinnerAssetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssetPos = position;
                if (position > 0) {
                    rvAttributes.setVisibility(View.VISIBLE);
                    calculateAttributes(position);
                } else {
                    rvAttributes.setVisibility(View.GONE);
                    if (!addChild) {
                        tvAddParentAsset.setVisibility(View.GONE);
                        tvParentAssetValue.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        GPSTracker gps = new GPSTracker(AddAssetActivity.this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
    }

    @OnClick(R.id.tv_add_parent_asset)
    void addParent() {
        scanQR = false;
        if (enableAddParent) {
            qrScan = new IntentIntegrator(AddAssetActivity.this);
            qrScan.setOrientationLocked(false);
            qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            qrScan.setPrompt(Utils.msg(AddAssetActivity.this, "511"));
            qrScan.initiateScan();
        }

    }

    //This method is used to fetch attributes of selected asset
    private void setAttributesAdapter() {
        if (attributesListFinal.size() > 0) {
            tvSubmit.setVisibility(View.VISIBLE);
        }
        boolean viewOnly=false;
        if(type==1){
            viewOnly=true;
            tvSubmit.setVisibility(View.GONE);
            spinnerAssetType.setEnabled(false);
            tvTitle.setText(assetName);
        }
        adapterAttributesItems = new AdapterAttributesItems(attributesListFinal, this, this,viewOnly,imgBaseUrl);
        adapterAttributesItems.setHasStableIds(true);
        rvAttributes.setAdapter(adapterAttributesItems);
        rvAttributes.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.button_back)
    void finishActivity() {
        finish();
    }

    //Checking all the validations before adding asset
    @OnClick(R.id.tv_submit)
    void checkValidations() {
        hideKeyBoard(rlMain);
        boolean isTrue = false;

        for (int i = 0; i < attributesListFinal.size(); i++) {
            if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.TEXT)) {
                isTrue = checkTextValidation(i);
                if (!isTrue) {
                    return;
                }
            } else if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.NUMBER)) {
                isTrue = checkNumberValidation(i);
                if (!isTrue) {
                    return;
                }
            } else if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.IMAGE)) {
                if (attributesListFinal.get(i).getMandatory()) {
                    isTrue = checkImageValidation(i);
                    if (!isTrue) {
                        return;
                    }
                }
            } else if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.PICKLIST)) {
                if (attributesListFinal.get(i).getMandatory()) {
                    isTrue = checkSpinnerValidation(i);
                    if (!isTrue) {
                        return;
                    }
                }

            } else if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.QR_CODE)) {
                if (attributesListFinal.get(i).getMandatory()) {
                    isTrue = checkQRValidation(i);
                    if (!isTrue) {
                        return;
                    }
                }
            }else if (attributesListFinal.get(i).getType().equals(AppConstants.ATTRIBUTE_TYPE.SERIAL_NUMBER)) {
                if (attributesListFinal.get(i).getMandatory()) {
                    isTrue = checkTextValidation(i);
                    if (!isTrue) {
                        return;
                    }
                }
            } else {
                if (attributesListFinal.get(i).getMandatory()) {
                    isTrue = checkDateValidation(i);
                    if (!isTrue) {
                        return;
                    }
                }
            }
        }
        sendDataToListScreen(0);

    }

    private void askUserToAddChild() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.add_child_asset_msg)).setCancelable(
                false).setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        sendDataToListScreen(1);
                    }
                }).setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        sendDataToListScreen(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void calculateChildAssets() {
        //ArrayList<Integer> childAssets = new ArrayList<>();
        if (type == 1) {
            for (int i = 0; i < assetListFinal.size(); i++) {
                List<Integer> parentList = assetListFinal.get(i).getParentTypes();
                int assetId = assetListFinal.get(selectedAssetPos).getAssetTypeId();
                if (parentList != null && parentList.size() > 0) {
                    if (parentList.contains(assetId)) {
                        childAssetList.add(assetListFinal.get(i).getAssetTypeId());
                    }
                }
            }
        }

    }

    private ArrayList<Integer> getChildAsset(){
        ArrayList<Integer> childAssets = new ArrayList<>();
        if (!addParent) {
            for (int i = 0; i < assetListFinal.size(); i++) {
                if (assetListFinal.get(selectedAssetPos).getParentTypes() != null && assetListFinal.get(selectedAssetPos).getParentTypes().size() > 0) {
                    List<Integer> parentList = assetListFinal.get(selectedAssetPos).getParentTypes();
                    Integer assetId = assetListFinal.get(i).getAssetTypeId();
                    if (assetId != null) {
                        if (parentList != null && parentList.size() > 0) {
                            if (parentList.contains(assetId)) {
                                childAssets.add(assetListFinal.get(i).getAssetTypeId());
                            }
                        }
                    }
                } else {
                    List<Integer> parentList = assetListFinal.get(i).getParentTypes();
                    Integer assetId = assetListFinal.get(selectedAssetPos).getAssetTypeId();
                    if (assetId != null) {
                        if (parentList != null && parentList.size() > 0) {
                            if (parentList.contains(assetId)) {
                                childAssets.add(assetListFinal.get(i).getAssetTypeId());
                            }
                        }
                    }
                }


            }
        } else {
            for (int i = 0; i < auditList.size(); i++) {
                if (auditList.get(i).getAuditId().toString().equals(auditId)) {
                    auditAssetList = auditList.get(i).getAssetTypes();
                    break;
                }
            }
            for (int i = 0; i < assetList.size(); i++) {
                for (int j = 0; j < auditAssetList.size(); j++) {
                    if (assetList.get(i).getAssetTypeId() == null) {
                        continue;
                    }
                    if (assetList.get(i).getAssetTypeId().toString().equals(auditAssetList.get(j).getId())) {
                        assetListFinalForParent.add(assetList.get(i));
                        break;
                    }
                }
            }
            int pos = -1;
            for (int x = 0; x < assetListFinalForParent.size(); x++) {
                if (assetListFinalForParent.get(x).getAssetTypeId().toString().equals(assetListFinal.get(selectedAssetPos).getAssetTypeId().toString())) {
                    pos = x;
                    break;
                }
            }
            for (int i = 0; i < assetListFinalForParent.size(); i++) {
                List<Integer> parentList = assetListFinalForParent.get(i).getParentTypes();
                int assetId = assetListFinalForParent.get(pos).getAssetTypeId();
                if (parentList != null && parentList.size() > 0) {
                    if (parentList.contains(assetId)) {
                        childAssets.add(assetListFinalForParent.get(i).getAssetTypeId());
                    }
                }
            }
        }
        return childAssets;
    }

    private void sendDataToListScreen(int type) {
        ArrayList<Integer> childAssets = new ArrayList<>();
        childAssets=getChildAsset();

        AssetListResult assetListResult = assetListFinal.get(selectedAssetPos);
        assetListResult.setParent(parent);
        assetListResult.setParentName(parentName);
        assetListResult.setAttributes(attributesListFinal);
        if (childAssets.size() > 0)
            assetListResult.setChildAssets(childAssets);
        assetListResult.setQr_code(qrCode);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL, (Serializable) assetListResult);
        returnIntent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, editPos);
        returnIntent.putExtra(AppConstants.INTENT_EXTRAS.ADD_CHILD, addChild);
        returnIntent.putExtra(AppConstants.INTENT_EXTRAS.ADD_PARENT, addParent);
        returnIntent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_POSITION, assetPos);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private Boolean checkTextValidation(int pos) {
        if (attributesListFinal.get(pos).getMandatory()) {
            if (TextUtils.isEmpty(attributesListFinal.get(pos).getTextValue())) {
                Utils.toastMsgCenter(AddAssetActivity.this, getString(R.string.please_enter) + " " + attributesListFinal.get(pos).getName());
                return false;
            } else {
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
                    if (attributesListFinal.get(pos).getTextValue().length() < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " Min length should be " + attributesListFinal.get(pos).getMin());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
                    if (attributesListFinal.get(pos).getTextValue().length() > Integer.parseInt(attributesListFinal.get(pos).getMax())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " Max length should be " + attributesListFinal.get(pos).getMax());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getRegex()) && !Pattern.matches(attributesListFinal.get(pos).getRegex(), attributesListFinal.get(pos).getTextValue())) {
                    Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " " + getString(R.string.regex_error));
                    return false;
                }

                return true;
            }
        } else {
            if (!TextUtils.isEmpty(attributesListFinal.get(pos).getTextValue())) {
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
                    if (attributesListFinal.get(pos).getTextValue().length() < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " Min length should be " + attributesListFinal.get(pos).getMin());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
                    if (attributesListFinal.get(pos).getTextValue().length() > Integer.parseInt(attributesListFinal.get(pos).getMax())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " Max length should be " + attributesListFinal.get(pos).getMax());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getRegex())) {
                    String regex = attributesListFinal.get(pos).getRegex().replaceAll("/", "");
                    if (!Pattern.matches(regex, attributesListFinal.get(pos).getTextValue())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " " + getString(R.string.regex_error));
                        return false;
                    }

                }
            }
            return true;
        }

    }

    private Boolean checkNumberValidation(int pos) {
        if (attributesListFinal.get(pos).getMandatory()) {
            if (TextUtils.isEmpty(attributesListFinal.get(pos).getNumValue())) {
                Utils.toastMsgCenter(AddAssetActivity.this, getString(R.string.please_enter) + " " + attributesListFinal.get(pos).getName());
                return false;
            } else {
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
                    if (Double.parseDouble(attributesListFinal.get(pos).getNumValue()) < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + "Min value should be " + attributesListFinal.get(pos).getMin());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
                    if (Double.parseDouble(attributesListFinal.get(pos).getNumValue()) > Integer.parseInt(attributesListFinal.get(pos).getMax())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + "Max value should be " + attributesListFinal.get(pos).getMax());

                        return false;
                    }
                }
                return true;
            }
        } else {
            if (!TextUtils.isEmpty(attributesListFinal.get(pos).getNumValue())) {
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
                    if (Double.parseDouble(attributesListFinal.get(pos).getNumValue()) < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + "Min value should be " + attributesListFinal.get(pos).getMin());
                        return false;
                    }
                }
                if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
                    if (Double.parseDouble(attributesListFinal.get(pos).getNumValue()) > Integer.parseInt(attributesListFinal.get(pos).getMax())) {
                        Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + "Max value should be " + attributesListFinal.get(pos).getMax());
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private Boolean checkImageValidation(int pos) {
        if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
            int minImage = Integer.parseInt(attributesListFinal.get(pos).getMin());
            int max=0;
            if (!TextUtils.isEmpty(attributesList.get(pos).getMax())) {
                max = Integer.parseInt(attributesList.get(pos).getMax());
                if(attributesList.get(pos).getValues() != null && attributesList.get(pos).getValues().size()>0){
                    if(max!=attributesList.get(pos).getValues().size()){
                        max=attributesList.get(pos).getValues().size();
                    }
                }
            }
            if(minImage>max){
                minImage=max;
            }

            if (attributesListFinal.get(pos).getImgUrl() == null || attributesListFinal.get(pos).getImgUrl().size() < minImage) {
                Utils.toastMsgCenter(AddAssetActivity.this, "Min selected " + attributesListFinal.get(pos).getName() + " should be " + attributesListFinal.get(pos).getMin());
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private Boolean checkDateValidation(int pos) {

        if (TextUtils.isEmpty(attributesListFinal.get(pos).getDate())) {
            Utils.toastMsgCenter(AddAssetActivity.this, getString(R.string.please_select) + " " + attributesListFinal.get(pos).getName() + " " + getString(R.string.date));
            return false;
        }
        return true;
    }

    private Boolean checkQRValidation(int pos) {

        if (TextUtils.isEmpty(attributesListFinal.get(pos).getQrValue())) {
            Utils.toastMsgCenter(AddAssetActivity.this, getString(R.string.please_scan) + " " + attributesListFinal.get(pos).getName());
            return false;
        }
        if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin()) && !TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
            int min = Integer.parseInt(attributesListFinal.get(pos).getMin());
            int max = Integer.parseInt(attributesListFinal.get(pos).getMax());
            if (min == max) {
                if (attributesListFinal.get(pos).getQrValue().length() < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                    Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " length must be " + attributesListFinal.get(pos).getMin() + " digits.");
                    return false;
                }
            }
        }
        if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMin())) {
            if (attributesListFinal.get(pos).getQrValue().length() < Integer.parseInt(attributesListFinal.get(pos).getMin())) {
                Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " min length should be " + attributesListFinal.get(pos).getMin());
                return false;
            }
        }
        if (!TextUtils.isEmpty(attributesListFinal.get(pos).getMax())) {
            if (attributesListFinal.get(pos).getQrValue().length() > Integer.parseInt(attributesListFinal.get(pos).getMax())) {
                Utils.toastMsgCenter(AddAssetActivity.this, attributesListFinal.get(pos).getName() + " max length should be " + attributesListFinal.get(pos).getMax());
                return false;
            }
        }
        /*if (!TextUtils.isEmpty(attributesListFinal.get(pos).getRegex())) {
            String regex = attributesListFinal.get(pos).getRegex().replaceAll("/", "");
            if (!Pattern.matches(regex, attributesListFinal.get(pos).getQrValue())) {
                Utils.toastMsgCenter(AddAssetActivity.this, "Invalid QR code");
                return false;
            }

        }*/
        return true;
    }

    private Boolean checkSpinnerValidation(int pos) {
        if (attributesListFinal.get(pos).getSelectedValueList() == null || attributesListFinal.get(pos).getSelectedValueList().size() == 0) {
            Utils.toastMsgCenter(AddAssetActivity.this, getString(R.string.please_select) + " " + attributesListFinal.get(pos).getName());
            return false;
        } else {
            return true;
        }
    }

    //Method to call API for Assets List
    private void getAssetTypesList() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));

        IApiRequest request = ApiClient.getRequest();
        Call<List<AssetListResult>> call = request.getAssetTypesList();
        call.enqueue(new Callback<List<AssetListResult>>() {

            @Override
            public void onResponse(Call<List<AssetListResult>> call, Response<List<AssetListResult>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.code() == 200) {
                    assetList = response.body();
                    if (assetList != null && assetList.size() > 0) {
                        auditModel.setAssetList(assetList);
                        updateDataInDB(auditModel);
                        //getFinalAssetsList();
                        calculateAssets();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AssetListResult>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    private void getFinalAssetsList() {

        if (assetListFinal.size() > 0) {
            setAdapter();
        } else {
            if (addChild && (childAssetList == null || childAssetList.size() == 0)) {
                Utils.toastMsgCenter(this, getString(R.string.no_child_assets_available));
                return;
            }
            if (addParent && (childAssetList == null || childAssetList.size() == 0)) {
                Utils.toastMsgCenter(this, getString(R.string.no_parent_assets_available));
                return;
            }
            tvSubmit.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.no_asset_type_added), Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        AssetListResult assetListRes = new AssetListResult();
        if (addChild) {
            assetListRes.setName(getString(R.string.select_child_asset_type));
        } else if (addParent) {
            assetListRes.setName(getString(R.string.select_parent_asset_type));
        } else {
            assetListRes.setName(getString(R.string.select_asset_type));
        }
        if (!assetListFinal.get(0).getName().equals(getString(R.string.select_asset_type)))
            assetListFinal.add(0, assetListRes);
        for (AssetListResult assetListResult : assetListFinal) {
            assetNamesList.add(assetListResult.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, assetNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssetType.setAdapter(adapter);
        if (isEdit) {
            for (int i = 0; i < assetNamesList.size(); i++) {
                if (assetNamesList.get(i).equals(assetListModel.getName())) {
                    spinnerAssetType.setSelection(i);
                    break;
                }
            }
        }

    }


    @Override
    public void onItemClickListener(View view, int position) {

        switch (view.getId()) {
            case R.id.iv_click: {
                if (attributesListFinal.get(position).getMax() != null) {
                    maxImagesAllowed = Integer.parseInt(attributesListFinal.get(position).getMax());
                }
                if (attributesListFinal.get(position).getImgUrl() != null && attributesListFinal.get(position).getImgUrl().size() >= maxImagesAllowed) {
                    Toast.makeText(this, getString(R.string.max_image_select_msg) + " " + maxImagesAllowed + " " + getString(R.string.images2), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (attributesListFinal.get(position).getImgUrl() != null) {
                    imgPos = attributesListFinal.get(position).getImgUrl().size();
                }
                imageCapturePos = position;
                if (attributesListFinal.get(position).getValues() != null && attributesListFinal.get(position).getValues().size() > imgPos) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.capture) + " " + attributesListFinal.get(position).getValues().get(imgPos).getAttributeValue() + " " + getString(R.string.image)).setCancelable(
                            false).setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    if (!Utils.hasPermissions(AddAssetActivity.this, AppConstants.CAMERA_PERMISSIONS)) {
                                        ActivityCompat.requestPermissions(AddAssetActivity.this, AppConstants.CAMERA_PERMISSIONS, 1000);
                                    } else {
                                        imageCapture();
                                    }
                                }
                            }).setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Utils.toastMsgCenter(this, getString(R.string.max_image_select_msg2));
                  //  Toast.makeText(this, getString(R.string.max_image_select_msg2), Toast.LENGTH_SHORT).show();
                    return;
                   /* if (!Utils.hasPermissions(AddAssetActivity.this, AppConstants.CAMERA_PERMISSIONS)) {
                        ActivityCompat.requestPermissions(AddAssetActivity.this, AppConstants.CAMERA_PERMISSIONS, 1000);
                    } else {
                        imageCapture();
                    }*/
                }
                break;
            }
            case R.id.iv_capture: {
                scanQR = true;
                adapterPos = position;
                qrScan = new IntentIntegrator(AddAssetActivity.this);
                qrScan.setOrientationLocked(false);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt(Utils.msg(AddAssetActivity.this, "511"));
                qrScan.initiateScan();
                break;
            }
        }
    }

    protected void imageCapture() {
        String name = new SimpleDateFormat(DateUtil.DATE_FORMAT4, Locale.ENGLISH).format(new Date());
        destination = new File(Environment.getExternalStorageDirectory(), name + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", destination));
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            try {
                long size = destination.length() / 1024;
                Log.d("SIZE-->", "" + size);
                try {
                    destination = new Compressor(this).compressToFile(destination);
                } catch (Exception e) {
                    Log.d("Error", e.toString());
                }
                size = destination.length() / 1024;
                Log.d("SIZE-->", "" + size);
                filePath = destination.getAbsolutePath();
                System.out.println("file path::" + filePath);
                if (Utils.isNetworkAvailable(AddAssetActivity.this)) {
                    uploadImage(filePath);
                } else {
                    List<ImageModel> imagesList = attributesListFinal.get(imageCapturePos).getImgUrl();
                    if (imagesList == null) {
                        imagesList = new ArrayList<>();
                    }
                    String fileName = getName();
                    ImageModel imageModel = new ImageModel();
                    imageModel.setName(fileName);
                    imageModel.setLatitude(lat);
                    imageModel.setImg_tag_id(attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getId());
                    if (attributesListFinal.get(imageCapturePos).getValues() != null &&
                            attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue() != null) {
                        imageModel.setTag(attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue());
                    } else {
                        imageModel.setTag(fileName);
                    }
                    imageModel.setTime(DateUtil.getCurrentTime());
                    imageModel.setLongitude(lng);
                    imageModel.setPath(filePath);
                    imagesList.add(imageModel);
                    attributesListFinal.get(imageCapturePos).setImgUrl(imagesList);
                    if (adapterAttributesItems != null) {
                        adapterAttributesItems.notifyDataSetChanged();
                    }
                }

            } catch (Exception th) {
                th.printStackTrace();
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && !TextUtils.isEmpty(result.getContents())) {
                if (scanQR) {
                    if (qrList.contains(result.getContents())) {
                        if (isEdit) {
                            int index = qrList.indexOf(result.getContents());
                            if (index != editPos) {
                                Utils.toastMsgCenter(this, getString(R.string.duplicate_qr));
                                return;
                            }
                        } else {
                            Utils.toastMsgCenter(this, getString(R.string.duplicate_qr));
                            return;
                        }

                    }
                    qrCode = result.getContents();
                    if(!Pattern.matches("[0-9]+",qrCode)){
                        Utils.toastMsgCenter(this, getString(R.string.invalid_qr));
                        return;
                    }
                    attributesListFinal.get(adapterPos).setQrValue(result.getContents());
                    if (adapterAttributesItems != null) {
                        adapterAttributesItems.notifyDataSetChanged();
                    }
                } else {
                    parent = result.getContents();
                    tvAddParentAsset.setText(getString(R.string.parent_asset));
                    tvParentAssetValue.setText(parent);
                }

            }

        }
    }

    //method to call API for image upload
    private void uploadImage(String filePath) {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        File file = new File(filePath);
        String fileName = getName()+".jpg";
        Log.d("FILE-->", fileName);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part selectedFile = MultipartBody.Part.createFormData("file", fileName, requestBody);
        RequestBody module = RequestBody.create(MediaType.parse("text/plain"), "audit");

        IApiRequest request = ApiClient.getRequest();
        Call<UploadImageResponse> call = request.uploadImage(selectedFile, module);
        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, Response<UploadImageResponse> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.code() == 200) {
                    UploadImageResponse uploadImageResponse = response.body();
                    if (uploadImageResponse != null) {
                        List<ImageModel> imagesList = attributesListFinal.get(imageCapturePos).getImgUrl();
                        if (imagesList == null) {
                            imagesList = new ArrayList<>();
                        }
                        Log.d("IMAGE-->", uploadImageResponse.getImgPath());
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(fileName);
                        imageModel.setLatitude(lat);
                        imageModel.setImg_tag_id(attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getId());
                        imageModel.setTime(DateUtil.getCurrentTime());
                        imageModel.setLongitude(lng);
                        if (attributesListFinal.get(imageCapturePos).getValues() != null &&
                                attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue() != null) {
                            imageModel.setTag(attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue());
                        } else {
                            imageModel.setTag(fileName);
                        }
                        imageModel.setUrlPath(uploadImageResponse.getImgPath());
                        imageModel.setPath(filePath);
                        imagesList.add(imageModel);
                        attributesListFinal.get(imageCapturePos).setImgUrl(imagesList);
                        if (adapterAttributesItems != null) {
                            adapterAttributesItems.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Log.d("error_msg", "error-->" + t.toString());
            }
        });
    }

    //method to generate image name depending upon API provided format
    private String getName() {
        //   attributesListFinal.get(imageCapturePos).setFormat("assetType-attribute-auditName-siteId");
        if (!TextUtils.isEmpty(attributesListFinal.get(imageCapturePos).getFormat())) {
            String[] arrOfStr = attributesListFinal.get(imageCapturePos).getFormat().split("-");
            String imgName = "";
            for (int i = 0; i < arrOfStr.length; i++) {
                if (arrOfStr[i].equals(AppConstants.IMAGE_FORMAT_TYPE.ASSET_TYPE)) {
                    if (TextUtils.isEmpty(imgName)) {
                        imgName = "" + assetName;
                    } else {
                        imgName = imgName + "-" + assetName;
                    }
                } else if (arrOfStr[i].equals(AppConstants.IMAGE_FORMAT_TYPE.ATTRIBUTE)) {
                    if (TextUtils.isEmpty(imgName)) {
                        imgName = "" + attributesListFinal.get(imageCapturePos).getName();
                    } else {
                        imgName = imgName + "-" + attributesListFinal.get(imageCapturePos).getName();
                    }
                } else if (arrOfStr[i].equals(AppConstants.IMAGE_FORMAT_TYPE.SITE_ID)) {
                    if (TextUtils.isEmpty(imgName)) {
                        imgName = "" + siteId;
                    } else {
                        imgName = imgName + "-" + siteId;
                    }
                } else if (arrOfStr[i].equals(AppConstants.IMAGE_FORMAT_TYPE.AUDIT_NAME)) {
                    if (TextUtils.isEmpty(imgName)) {
                        imgName = "" + auditName;
                    } else {
                        imgName = imgName + "-" + auditName;
                    }
                } else if (arrOfStr[i].equals(AppConstants.IMAGE_FORMAT_TYPE.TIME)) {
                    if (TextUtils.isEmpty(imgName)) {
                        imgName = "" + System.currentTimeMillis();
                    } else {
                        imgName = imgName + "-" + System.currentTimeMillis();
                    }
                } else {
                    return "file" + System.currentTimeMillis();
                }
            }
            if (attributesListFinal.get(imageCapturePos).getValues() != null && attributesListFinal.get(imageCapturePos).getValues().size() >= imgPos) {
                Log.d("IMAGE_NAME-->", attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue() + "-" + imgName);
                return attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue() + "-" + imgName;
            } else {
                Log.d("IMAGE_NAME-->", imgName);
                return imgName;
            }

        } else {
            if (attributesListFinal.get(imageCapturePos).getValues() != null && attributesListFinal.get(imageCapturePos).getValues().size() >= imgPos) {
                return attributesListFinal.get(imageCapturePos).getValues().get(imgPos).getAttributeValue();
            } else {
                return "file" + System.currentTimeMillis();
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                            ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE))
                                    .clearApplicationUserData();
                        }
                    } else if (Manifest.permission.CAMERA.equals(permission)
                            || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)
                            || Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {

                        ActivityCompat.requestPermissions(this, AppConstants.CAMERA_PERMISSIONS, 1000);
                    }
                }
            }
        }
    }

    private void getSavedData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                auditModel = appDatabase.auditDao().getAuditData(type);
                auditList = auditModel.getAssetTypes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < auditModel.getDataGrid().size(); i++) {
                            if (auditId.equals(auditModel.getDataGrid().get(i).getAuditId().toString())) {
                                dbAuditPos = i;
                                break;
                            }
                        }

                        assetList = auditModel.getAssetList();
                        calculateAssets();
                        /*if (Utils.isNetworkAvailable(AddAssetActivity.this)) {
                            getAssetTypesList();
                        } else {
                            for (int i = 0; i < auditModel.getDataGrid().size(); i++) {
                                if (auditId.equals(auditModel.getDataGrid().get(i).getAuditId().toString())) {
                                    dbAuditPos = i;
                                    break;
                                }
                            }

                            assetList = auditModel.getAssetList();
                            calculateAssets();
                        }*/
                    }
                });

            }
        });
    }

    private void updateDataInDB(AuditModel auditModel) {
        AsyncTask.execute(() -> {
            appDatabase.auditDao().updateDataGrid(0, auditModel.getDataGrid());
            appDatabase.auditDao().updateAssetList(type, auditModel.getAssetList());
        });

    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void calculateAssets() {

        AsyncTask.execute(() -> {
            if (auditList == null || assetList == null)
                return;

            //assetListFinal = assetList;
            if (addChild || addParent) {
                for (int i = 0; i < auditList.size(); i++) {
                    if (auditList.get(i).getAuditId().toString().equals(auditId)) {
                        auditAssetList = auditList.get(i).getAssetTypes();
                        break;
                    }
                }
                if (childAssetList != null && childAssetList.size() > 0) {
                    assetListFinal.clear();
                    for (int i = 0; i < assetList.size(); i++) {
                        if (childAssetList.contains(assetList.get(i).getAssetTypeId())) {
                            assetListFinal.add(assetList.get(i));
                        }
                    }
                } else {
                    assetListFinal.clear();
                    for (int i = 0; i < auditList.size(); i++) {
                        if (auditList.get(i).getAuditId().toString().equals(auditId)) {
                            auditAssetList = auditList.get(i).getAssetTypes();
                            break;
                        }
                    }
                    for (int i = 0; i < assetList.size(); i++) {
                        for (int j = 0; j < auditAssetList.size(); j++) {
                            if (assetList.get(i).getAssetTypeId() == null) {
                                continue;
                            }
                            if (assetList.get(i).getAssetTypeId().toString().equals(auditAssetList.get(j).getId())) {
                                assetListFinal.add(assetList.get(i));
                                if(assetTypeId==assetList.get(i).getAssetTypeId()){
                                    selectedAssetPos=assetListFinal.size()-1;
                                }
                                break;
                            }
                        }
                    }
                    childAssetList=getChildAsset();
                    if(childAssetList.size()>0){
                        calculateAssets();
                        return;
                    }else{
                        assetListFinal.clear();
                    }
                }
            } else {
                // assetListFinal = assetList;
                for (int i = 0; i < auditList.size(); i++) {
                    if (auditList.get(i).getAuditId().toString().equals(auditId)) {
                        auditAssetList = auditList.get(i).getAssetTypes();
                        break;
                    }
                }
                for (int i = 0; i < assetList.size(); i++) {
                    for (int j = 0; j < auditAssetList.size(); j++) {
                        if (assetList.get(i).getAssetTypeId() == null) {
                            continue;
                        }
                        if (assetList.get(i).getAssetTypeId().toString().equals(auditAssetList.get(j).getId())) {
                            assetListFinal.add(assetList.get(i));
                            break;
                        }
                    }
                }
            }
            runOnUiThread(this::getFinalAssetsList);
        });
    }

    private void calculateAttributes(int position) {
        AsyncTask.execute(() -> {
            assetName = assetListFinal.get(position).getName();
            String assetId = assetListFinal.get(position).getAssetTypeId().toString();
            if (isEdit) {
                attributesList = assetListModel.getAttributes();
            } else {
                attributesList = assetListFinal.get(position).getAttributes();
            }
            for (int x = 0; x < auditAssetList.size(); x++) {
                if (auditAssetList.get(x).getId().equals(assetId)) {
                    auditAssetAttributesList = auditAssetList.get(x).getAttributes();
                    image_tag = auditAssetList.get(x).getImage_tag();
                    break;
                }
            }
            attributesListFinal = new ArrayList<>();
            for (int i = 0; i < attributesList.size(); i++) {
                for (int j = 0; j < auditAssetAttributesList.size(); j++) {
                    if (attributesList.get(i).getId().equals(auditAssetAttributesList.get(j))) {
                        attributesListFinal.add(attributesList.get(i));
                        break;
                    }
                }
                if (image_tag != null && image_tag.size() > 0) {
                    if (image_tag.containsKey(attributesList.get(i).getId().toString())) {
                        List<Integer> allowedList = image_tag.get(attributesList.get(i).getId().toString());
                        List<ValueModel> valueList = attributesList.get(i).getValues();
                        List<ValueModel> valueListFinal = new ArrayList<>();
                        for (int j = 0; j < valueList.size(); j++) {
                            for (int k = 0; k < allowedList.size(); k++) {
                                if (valueList.get(j).getId().toString().equals(allowedList.get(k).toString())) {
                                    valueListFinal.add(valueList.get(j));
                                    break;
                                }
                            }
                        }
                        attributesList.get(i).setValues(valueListFinal);
                        attributesListFinal.add(attributesList.get(i));
                    }
                }
            }


            runOnUiThread(this::setAttributesAdapter);
        });
    }
}