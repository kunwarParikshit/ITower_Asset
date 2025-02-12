package com.isl.audit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.isl.api.ApiClient;
import com.isl.api.IApiRequest;
import com.isl.audit.adapter.AdapterAssetList;
import com.isl.audit.db.AppDatabase;
import com.isl.audit.db.AuditModel;
import com.isl.audit.model.AssetListResult;
import com.isl.audit.model.AttributeResult;
import com.isl.audit.model.AuditListResult;
import com.isl.audit.model.ErrorModel;
import com.isl.audit.model.ImageModel;
import com.isl.audit.model.PerformAuditAssetRequest;
import com.isl.audit.model.PerformAuditAttributeRequest;
import com.isl.audit.model.PerformAuditImageRequest;
import com.isl.audit.model.PerformAuditRequest;
import com.isl.audit.model.PerformAuditResponse;
import com.isl.audit.model.RescheduleAttributeModel;
import com.isl.audit.model.RescheduleResultModel;
import com.isl.audit.model.UploadImageResponse;
import com.isl.audit.util.DateUtil;
import com.isl.audit.util.ItemClickListener;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.dao.DataBaseHelper;
import com.isl.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import infozech.itower.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* User added Assets are displayed in this activity
 * User can launch Add Asset screen from here
 * User can modify/delete added assets
 * User can perform Audit from here */
public class AssetsListActivity extends AppCompatActivity implements ItemClickListener {

    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_add_asset)
    TextView tvAddAsset;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;

    @BindView(R.id.rv_assets)
    RecyclerView rvAssets;
    private AppPreferences appPreferences;
    private AppDatabase appDatabase;
    private AuditListResult selectedAudit = new AuditListResult();
    private AuditModel auditModel = new AuditModel();

    private List<AssetListResult> assetsList = new ArrayList<>();
    private List<RescheduleResultModel> reschList = new ArrayList<>();
    private List<Integer> childToDeleteList = new ArrayList<>();
    private ArrayList<String> qrList = new ArrayList<>();
    private List<AssetListResult> assetDetailList = new ArrayList<>();

    private AdapterAssetList adapterAssetList;
    private ProgressDialog progressDialog;

    private String auditName,auditStatus, auditId, auditPerformId, siteId, ownerSiteId, scheduledDate, auditType;
    private final int ADD_ASSET_REQ_CODE = 1000;
    private int type, selectedAuditIndex, txnId, auditMatrixId;
    private String auditUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_list);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.tv_save)
    void saveDataToDB() {
        computeRequestData(1);
       /* for(int i=0;i<1000;i++){
            AssetListResult assetListResult=new AssetListResult();
            assetListResult.setAttributes(assetsList.get(i).getAttributes());
            assetListResult.setAssetTypeId(assetsList.get(i).getAssetTypeId());
            assetListResult.setAssetTypeId(assetsList.get(i).getAssetTypeId());
            assetsList.add(assetListResult);
        }
        for(int i=0;i<1000;i++){
            assetsList.get(i).setQr_code("000"+String.valueOf(100000+i));
        }
        computeRequestData(1);*/
    }

    @OnClick(R.id.rl_add)
    void launchAddAsset() {
        Intent intent = new Intent(this, AddAssetActivity.class);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.QR_LIST, qrList);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditName);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
        intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, siteId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, -1);
        startActivityForResult(intent, ADD_ASSET_REQ_CODE);
    }

    @OnClick(R.id.button_back)
    void finishActivity() {
        finish();
    }

    @OnClick(R.id.tv_submit)
    void submitData() {
        if (assetsList.size() > 0) {

            checkImage();
        } else {
            Utils.toastMsgCenter(this, getString(R.string.please_add_assets));
        }
    }

    private void checkImage() {
        if (!Utils.isNetworkAvailable(this)) {
            computeRequestData(0);
            return;
        }
        for (int i = 0; i < assetsList.size(); i++) {
            List<AttributeResult> attrList = assetsList.get(i).getAttributes();
            for (int j = 0; j < attrList.size(); j++) {
                if (attrList.get(j).getType().equals(AppConstants.ATTRIBUTE_TYPE.IMAGE)) {
                    List<ImageModel> imgList = attrList.get(j).getImgUrl();
                    if(imgList != null){
                        for (int k = 0; k < imgList.size(); k++) {
                            if (TextUtils.isEmpty(imgList.get(k).getUrlPath())) {
                                uploadImage(i, j, k);
                                return;
                            }
                        }
                    }

                }
            }
        }
        computeRequestData(0);
    }

    private void uploadImage(int i, int j, int k) {

        String filePath = assetsList.get(i).getAttributes().get(j).getImgUrl().get(k).getPath();
        String fileName = assetsList.get(i).getAttributes().get(j).getImgUrl().get(k).getName();

        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        File file = new File(filePath);
        // fileName="QR Tag Photo 1-16340-Battery Bank-Battery Bank QR Photo-1630428009899";
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
                        assetsList.get(i).getAttributes().get(j).getImgUrl().get(k).setUrlPath(uploadImageResponse.getImgPath());
                        checkImage();
                    }
                }

            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                checkImage();

                Log.d("error_msg", "error-->" + t.toString());
            }
        });
    }

    private void makeRequest(PerformAuditRequest performAuditRequest, int type) {

        String json = new Gson().toJson(performAuditRequest);
        Log.d("AUDIT-->", json);
        Handler mainHandler = new Handler(getMainLooper());
        if (type == 0) {
            Runnable runnable = () -> {
                if (!Utils.isNetworkAvailable(this)) {
                    Utils.toastMsgCenter(this, getString(R.string.network_unavailable_msg));
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    saveAuditRequestToDb();
                    String unSyncAudit = appPreferences.getUnsyncAudit();
                    String auditPerformId = String.valueOf(txnId);
                    if (!TextUtils.isEmpty(unSyncAudit)) {
                        if (!unSyncAudit.contains(auditPerformId)) {
                            unSyncAudit = unSyncAudit + "," + auditPerformId;
                        }
                    } else {
                        unSyncAudit = auditPerformId;
                    }
                    Log.d("UNSYNC_AUDIT-->", unSyncAudit);
                    appPreferences.setUnsyncAudit(unSyncAudit);
                } else {
                    String unSyncAudit = appPreferences.getUnsyncAudit();
                    String auditPerformId = String.valueOf(txnId);
                    if (!TextUtils.isEmpty(unSyncAudit)) {
                        if (unSyncAudit.contains("," + auditPerformId)) {
                            unSyncAudit = unSyncAudit.replace("," + auditPerformId, "");
                        } else if (unSyncAudit.contains(auditPerformId + ",")) {
                            unSyncAudit = unSyncAudit.replace(auditPerformId + ",", "");
                        } else if (unSyncAudit.contains(auditPerformId)) {
                            unSyncAudit = unSyncAudit.replace(auditPerformId, "");
                        }
                        Log.d("UNSYNC_AUDIT-->", unSyncAudit);
                        appPreferences.setUnsyncAudit(unSyncAudit);

                    }
                    callAuditApi(performAuditRequest);
                }
            };
            mainHandler.post(runnable);
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                //return;
            }

            saveAuditRequestToDb();

            if(type==1){
                String unSyncAudit = appPreferences.getUnsyncAudit();
                String auditPerformId = String.valueOf(txnId);
                if (!TextUtils.isEmpty(unSyncAudit)) {
                    if (!unSyncAudit.contains(auditPerformId)) {
                        unSyncAudit = unSyncAudit + "," + auditPerformId;
                    }
                } else {
                    unSyncAudit = auditPerformId;
                }
                Log.d("UNSYNC_AUDIT-->", unSyncAudit);
                appPreferences.setUnsyncAudit(unSyncAudit);
                runOnUiThread(() -> {
                    Utils.toastMsgCenter(AssetsListActivity.this, getString(R.string.data_saved));
                    finish();
                });
            }

        }
    }

    private void printData() {
        for (int i = 0; i < 1000; i++) {
            AssetListResult assetListResult = new AssetListResult();
            assetListResult.setAttributes(assetsList.get(i).getAttributes());
            assetListResult.setAssetTypeId(assetsList.get(i).getAssetTypeId());
            assetListResult.setAssetTypeId(assetsList.get(i).getAssetTypeId());
            assetsList.add(assetListResult);
        }
        for (int i = 0; i < 1000; i++) {
            assetsList.get(i).setQr_code(String.valueOf(1000000 + i));
        }
        computeRequestData(1);
    }

    private void callAuditApi(PerformAuditRequest performAuditRequest) {
        String url = auditUrl + "/audit/" + txnId + "/perform";
      //  String url=auditUrl+"/api/audit/"+txnId+"/perform";
        IApiRequest request = ApiClient.getRequest();
        Log.d("URL-->", url);

        Call<PerformAuditResponse> call = request.performAudit(url, performAuditRequest);
        call.enqueue(new Callback<PerformAuditResponse>() {
            @Override
            public void onResponse(Call<PerformAuditResponse> call, Response<PerformAuditResponse> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                updateSyncValueInDB();
                if (response.code() == 200) {
                    PerformAuditResponse performAuditResponse = response.body();
                    if (performAuditResponse != null && !TextUtils.isEmpty(performAuditResponse.getMessage())) {
                        if (performAuditResponse.getFlag().equalsIgnoreCase("S")) {
                            showAlertDialog(getString(R.string.audit_submit_success), 0);
                        } else {
                            showAlertDialog(performAuditResponse.getMessage(), 1);
                        }

                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Gson gson = new Gson();
                            ErrorModel errorModel = gson.fromJson(response.errorBody().charStream(), ErrorModel.class);
                            if (errorModel != null && !TextUtils.isEmpty(errorModel.getMessage())) {
                                showAlertDialog(errorModel.getMessage(), 1);
                                if (errorModel.getInValidQRCodes() != null && errorModel.getInValidQRCodes().size() > 0) {
                                    for (int i = 0; i < assetsList.size(); i++) {
                                        if (errorModel.getInValidQRCodes().containsKey(assetsList.get(i).getQr_code())) {
                                            assetsList.get(i).setShowError(true);
                                            assetsList.get(i).setErrorMsg(errorModel.getInValidQRCodes().get(assetsList.get(i).getQr_code()));
                                        } else {
                                            assetsList.get(i).setShowError(false);
                                            assetsList.get(i).setErrorMsg("");
                                        }
                                    }
                                    adapterAssetList.notifyDataSetChanged();
                                } else {
                                    for (int i = 0; i < assetsList.size(); i++) {
                                        assetsList.get(i).setShowError(false);
                                        assetsList.get(i).setErrorMsg("");
                                    }
                                    adapterAssetList.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<PerformAuditResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    private void saveAuditRequestToDb() {
        AsyncTask.execute(() -> {
            selectedAudit.setSelectedAssetList(assetsList);
            selectedAudit.setNeedSync(true);
            auditModel.getDataGrid().set(selectedAuditIndex, selectedAudit);
            appDatabase.auditDao().updateDataGrid(type, auditModel.getDataGrid());
        });
    }

    private void updateSyncValueInDB() {
        AsyncTask.execute(() -> {
            selectedAudit.setNeedSync(false);
            selectedAudit.setSelectedAssetList(null);
            auditModel.getDataGrid().set(selectedAuditIndex, selectedAudit);
            appDatabase.auditDao().updateDataGrid(type, auditModel.getDataGrid());
        });
    }

    private void getSavedData() {
        AsyncTask.execute(() -> {
            auditModel = appDatabase.auditDao().getAuditData(type);
            List<AuditListResult> auditList = auditModel.getDataGrid();
            for (int i = 0; i < auditList.size(); i++) {
                if (auditList.get(i).getTxnId().toString().equals(String.valueOf(txnId))) {
                    selectedAudit = auditList.get(i);
                    selectedAuditIndex = i;
                    break;
                }
            }
            if (selectedAudit.getSelectedAssetList() == null || selectedAudit.getSelectedAssetList().size() == 0) {

            } else {
                assetsList = selectedAudit.getSelectedAssetList();
                for (int i = 0; i < assetsList.size(); i++) {
                    qrList.add(assetsList.get(i).getQr_code());
                }

                runOnUiThread(() -> {
                    tvSave.setVisibility(View.VISIBLE);
                    setAdapter();
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAssetDetailsList();
                }
            });

        });
    }

    private void getAssetDetailsList() {
        if (Utils.isNetworkAvailable(this)) {
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
                        assetDetailList = response.body();
                        if (assetDetailList != null && assetDetailList.size() > 0) {
                            auditModel.setAssetList(assetDetailList);
                            updateDataInDB(auditModel);
                            if(assetsList.size()>0){
                                return;
                            }
                            if (type == 4 || type==1) {
                                getRescheduledDataList();
                            }
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
    }


    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.header));
        }
        if (getIntent() != null) {
            auditId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID);
            auditType = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE_API);
            auditPerformId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_PERFORM_ID);
            auditName = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME);
            siteId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.SITE_ID);
            scheduledDate = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.SCHEDULED_DATE);
            type = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, 0);
            txnId = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.TXN_ID, 0);
            auditMatrixId = getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.MATRIX_ID, 0);
            ownerSiteId = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.OWNER_SITE_ID);
            auditStatus = getIntent().getStringExtra(AppConstants.INTENT_EXTRAS.AUDIT_STATUS);
            tvTitle.setText(auditName);
        }
        Log.d("AUDIT_ID->", auditId);
        Log.d("AUDIT_PERFORM_ID->", auditPerformId);
        Log.d("USER_ID-->", new AppPreferences(this).getUserId());
        Log.d("USER_ID2-->", new AppPreferences(this).getLoginId());
        appPreferences = new AppPreferences(this);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvSubmit.setTypeface(null, Typeface.BOLD);
        tvAddAsset.setTypeface(null, Typeface.NORMAL);
        appDatabase = AppDatabase.getInstance(this);
        getSavedData();

        DataBaseHelper db = new DataBaseHelper(this);
        db.open();
        auditUrl = db.getModuleIP("Audits");
        if(auditUrl.contains("~")){
            auditUrl=auditUrl.substring(0,auditUrl.indexOf("~"));
        }
        if (auditUrl.contains("http") || auditUrl.contains("https")) {

        } else {
            auditUrl = "https://" + auditUrl;
        }
        Log.d("URL_IP-->", auditUrl);
    }

    private void setAdapter() {
        tvAddAsset.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.VISIBLE);
        if (adapterAssetList != null) {
            adapterAssetList.notifyDataSetChanged();
        } else {
            adapterAssetList = new AdapterAssetList(assetsList, this, this::onItemClickListener,type);
            rvAssets.setAdapter(adapterAssetList);
            rvAssets.setLayoutManager(new LinearLayoutManager(this));
        }
        if(type==1){
            tvSave.setVisibility(View.INVISIBLE);
            tvAddAsset.setVisibility(View.GONE);
            rlAdd.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.GONE);
        }

    }

    private void updateDataInDB(AuditModel auditModel) {
        AsyncTask.execute(() -> {
            appDatabase.auditDao().updateDataGrid(0, auditModel.getDataGrid());
            appDatabase.auditDao().updateAssetList(type, auditModel.getAssetList());
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ASSET_REQ_CODE && resultCode == RESULT_OK) {
            AssetListResult assetListResult = (AssetListResult) data.getSerializableExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL);
            int pos = data.getIntExtra(AppConstants.INTENT_EXTRAS.POSITION, -1);
            int assetPos = data.getIntExtra(AppConstants.INTENT_EXTRAS.ASSET_POSITION, -1);

            //String qrCode = data.getStringExtra(AppConstants.INTENT_EXTRAS.QR_CODE);
            boolean addChild = data.getBooleanExtra(AppConstants.INTENT_EXTRAS.ADD_CHILD, false);
            boolean addparent = data.getBooleanExtra(AppConstants.INTENT_EXTRAS.ADD_PARENT, false);
            if (assetListResult != null) {
                if (assetPos > -1) {
                    if (addChild) {
                        if (assetsList.size() > assetPos + 1) {
                            qrList.add(assetPos + 1, assetListResult.getQr_code());
                            assetsList.add(assetPos + 1, assetListResult);
                        } else {
                            assetsList.add(assetListResult);
                            qrList.add(assetListResult.getQr_code());
                            tvSave.setVisibility(View.VISIBLE);
                        }
                    } else if (addparent) {
                        qrList.add(assetPos, assetListResult.getQr_code());
                        assetsList.add(assetPos, assetListResult);
                        assetsList.get(assetPos + 1).setParentName(assetListResult.getName());
                        assetsList.get(assetPos + 1).setParent(assetListResult.getQr_code());
                    }
                } else {
                    if (pos >= 0) {
                        qrList.set(pos, assetListResult.getQr_code());
                        assetsList.set(pos, assetListResult);
                        if(assetListResult.getParentTypes()==null && assetListResult.getChildAssets() != null){
                            for (int p=0;p<assetListResult.getChildAssets().size();p++){
                                for (int q=0;q<assetsList.size();q++){
                                    if(assetListResult.getChildAssets().get(p).toString().equals(assetsList.get(q).getAssetTypeId().toString())){
                                        if(!TextUtils.isEmpty(assetsList.get(q).getParent())){
                                            assetsList.get(q).setParent(assetListResult.getQr_code());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        assetsList.add(assetListResult);
                        qrList.add(assetListResult.getQr_code());
                        tvSave.setVisibility(View.VISIBLE);
                    }

                }

                if (!TextUtils.isEmpty(assetListResult.getParent()))
                    Log.d("PARENT->", assetListResult.getParent());
            }

            setAdapter();

        }
    }

    private void launchAddChild(AssetListResult assetListResult, int pos) {
        Intent intent = new Intent(this, AddAssetActivity.class);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditName);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
        intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_POSITION, pos);
        intent.putStringArrayListExtra(AppConstants.INTENT_EXTRAS.QR_LIST, qrList);
        intent.putExtra(AppConstants.INTENT_EXTRAS.PARENT, assetListResult.getQr_code());
        intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, siteId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.ADD_CHILD, true);
        intent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, -1);
        intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_TYPE_ID, assetListResult.getAssetTypeId());
        intent.putExtra(AppConstants.INTENT_EXTRAS.PARENT_NAME, assetListResult.getName());
        intent.putIntegerArrayListExtra(AppConstants.INTENT_EXTRAS.CHILD_ASSET_LIST, assetListResult.getChildAssets());
        startActivityForResult(intent, ADD_ASSET_REQ_CODE);
    }

    private void launchAddParent(AssetListResult assetListResult, int pos) {
        Intent intent = new Intent(this, AddAssetActivity.class);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditName);
        intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
        intent.putExtra(AppConstants.INTENT_EXTRAS.ADD_PARENT, true);
        intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_TYPE_ID, assetListResult.getAssetTypeId());
        intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_POSITION, pos);
        intent.putStringArrayListExtra(AppConstants.INTENT_EXTRAS.QR_LIST, qrList);
        intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, siteId);
        intent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, -1);
        intent.putExtra(AppConstants.INTENT_EXTRAS.PARENT_NAME, assetListResult.getName());
        intent.putIntegerArrayListExtra(AppConstants.INTENT_EXTRAS.PARENT_ASSET_LIST, (ArrayList<Integer>) assetListResult.getChildAssets());
        startActivityForResult(intent, ADD_ASSET_REQ_CODE);
    }

    @Override
    public void onItemClickListener(View view, int position) {
        if (view.getId() == R.id.iv_edit) {
            Intent intent = new Intent(this, AddAssetActivity.class);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditId);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
            intent.putExtra(AppConstants.INTENT_EXTRAS.QR_LIST, qrList);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditName);
            intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, siteId);
            intent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, position);
            intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL, assetsList.get(position));
            startActivityForResult(intent, ADD_ASSET_REQ_CODE);
        } else if (view.getId() == R.id.iv_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_asset_msg));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                dialog.dismiss();
                deleteParent(assetsList.get(position), position);
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (view.getId() == R.id.tv_add_child) {
            launchAddChild(assetsList.get(position), position);
        } else if (view.getId() == R.id.tv_add_parent) {
            launchAddParent(assetsList.get(position), position);
        } else if (view.getId() == R.id.iv_warning) {
            showAlertDialog(assetsList.get(position).getErrorMsg(), 1);
        }else if (view.getId() == R.id.cv_main) {
            Intent intent = new Intent(this, AddAssetActivity.class);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditId);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
            intent.putExtra(AppConstants.INTENT_EXTRAS.QR_LIST, qrList);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditName);
            intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, siteId);
            intent.putExtra(AppConstants.INTENT_EXTRAS.POSITION, position);
            intent.putExtra(AppConstants.INTENT_EXTRAS.ASSET_MODEL, assetsList.get(position));
            startActivityForResult(intent, ADD_ASSET_REQ_CODE);
        }
    }

    private void showAlertDialog(String msg, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle(getString(R.string.app_name));
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
            if (type == 0) {
                Intent intent = new Intent(AssetsListActivity.this, MyAuditsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //  builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void computeRequestData(int type) {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        Runnable runnable = () -> {
            PerformAuditRequest performAuditRequest = new PerformAuditRequest();

            List<PerformAuditAssetRequest> assetList = new ArrayList<>();
            for (int i = 0; i < assetsList.size(); i++) {
                PerformAuditAssetRequest performAuditAssetRequest = new PerformAuditAssetRequest();
                List<PerformAuditAttributeRequest> attrList = new ArrayList<>();
                for (int j = 0; j < assetsList.get(i).getAttributes().size(); j++) {
                    PerformAuditAttributeRequest attributeRequest = new PerformAuditAttributeRequest();
                    AttributeResult attributeResult = assetsList.get(i).getAttributes().get(j);

                    if (attributeResult.getType().equals(AppConstants.ATTRIBUTE_TYPE.IMAGE)) {
                        if (attributeResult.getImgUrl() != null) {
                            List<PerformAuditImageRequest> imageList = new ArrayList<>();
                            for (int x = 0; x < attributeResult.getImgUrl().size(); x++) {
                                PerformAuditImageRequest imageRequest = new PerformAuditImageRequest();
                                imageRequest.setTag(attributeResult.getImgUrl().get(x).getName());
                                imageRequest.setPath(attributeResult.getImgUrl().get(x).getUrlPath());
                                imageRequest.setTime(attributeResult.getImgUrl().get(x).getTime());
                                imageRequest.setLatitude(attributeResult.getImgUrl().get(x).getLatitude());
                                imageRequest.setLongitude(attributeResult.getImgUrl().get(x).getLongitude());
                                imageRequest.setImg_tag_id(attributeResult.getImgUrl().get(x).getImg_tag_id());
                                imageList.add(imageRequest);
                            }
                            attributeRequest.setImage(imageList);
                            attributeRequest.setValue("Static value");
                        }
                    } else if (attributeResult.getType().equals(AppConstants.ATTRIBUTE_TYPE.TEXT)) {
                        attributeRequest.setValue(attributeResult.getTextValue());
                    } else if (attributeResult.getType().equals(AppConstants.ATTRIBUTE_TYPE.NUMBER)) {
                        attributeRequest.setValue(attributeResult.getNumValue());
                    } else if (attributeResult.getType().equals(AppConstants.ATTRIBUTE_TYPE.PICKLIST)) {
                        if (attributeResult.getSelectedValueList() != null && attributeResult.getSelectedValueList().size() > 0) {
                            String selectedValue = "";
                            for (int x = 0; x < attributeResult.getSelectedValueList().size(); x++) {
                                if (selectedValue.isEmpty()) {
                                    selectedValue = attributeResult.getSelectedValueList().get(x);
                                } else {
                                    selectedValue = selectedValue + "," + attributeResult.getSelectedValueList().get(x);
                                }
                            }
                            if (!selectedValue.isEmpty()) {
                                attributeRequest.setValue(selectedValue);
                            }
                        }

                    } else if (attributeResult.getType().equals(AppConstants.ATTRIBUTE_TYPE.DATE)) {
                        attributeRequest.setValue(attributeResult.getDate());
                    }else{
                        attributeRequest.setValue(attributeResult.getTextValue());
                    }
                    if (!TextUtils.isEmpty(attributeRequest.getValue()) || attributeRequest.getImage() != null) {
                        attributeRequest.setId(attributeResult.getId());
                        attrList.add(attributeRequest);
                    }

                }
                performAuditAssetRequest.setAttributes(attrList);
                if (!TextUtils.isEmpty(assetsList.get(i).getParent())) {
                    performAuditAssetRequest.setParentAsset(assetsList.get(i).getParent());
                }
                performAuditAssetRequest.setQr_code(assetsList.get(i).getQr_code());
                if (TextUtils.isEmpty(performAuditAssetRequest.getQr_code())) {
                    try {
                        performAuditAssetRequest.setQr_code(assetsList.get(i).getAttributes().get(0).getQrValue());
                    } catch (Exception e) {
                        Log.d("error", e.toString());
                    }
                }
                performAuditAssetRequest.setTypeId(assetsList.get(i).getAssetTypeId());
                performAuditAssetRequest.setType(assetsList.get(i).getName());
                assetList.add(performAuditAssetRequest);

            }
            Log.d("ASSET-->", assetList.size() + "");
            performAuditRequest.setAssets(assetList);
            performAuditRequest.setSiteId(Integer.parseInt(siteId));
            if (!TextUtils.isEmpty(scheduledDate)) {
                String date = DateUtil.formatDate(DateUtil.DATE_FORMAT1, DateUtil.DATE_FORMAT3, scheduledDate);
                performAuditRequest.setScheduleDate(date);
            }

            performAuditRequest.setStatus(2);
            performAuditRequest.setTxnDate(DateUtil.getCurrentTime());
            performAuditRequest.setTxnId(txnId);
            performAuditRequest.setMatrixId(auditMatrixId);
            performAuditRequest.setUser(new AppPreferences(this).getLoginId());
            performAuditRequest.setAudit_type(auditType);
            performAuditRequest.setAuditId(Integer.parseInt(auditId));
            performAuditRequest.setOwnerSiteId(ownerSiteId);
            makeRequest(performAuditRequest, type);
        };
        Thread thread1 = new Thread(runnable);
        thread1.start();
    }

    private void deleteParent(AssetListResult assetListResult, int pos) {
        fetchChildAssets(assetListResult.getQr_code());
        if (childToDeleteList.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_parent_asset_msg));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.delete_all), (dialog, which) -> {
                dialog.dismiss();
                for (int i = 0; i < childToDeleteList.size(); i++) {
                    int removePos = childToDeleteList.get(i);
                    assetsList.remove(removePos);
                    qrList.remove(removePos);
                }
                notifyAdapter(pos);
            });
            builder.setNegativeButton(getString(R.string.keep_them), (dialog, which) -> {
                dialog.dismiss();
                for (int i = 0; i < childToDeleteList.size(); i++) {
                    assetsList.get(childToDeleteList.get(i)).setParent("");
                    assetsList.get(childToDeleteList.get(i)).setParentName("");
                }
                notifyAdapter(pos);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            notifyAdapter(pos);
        }
    }

    private void fetchChildAssets(String qr) {
        childToDeleteList.clear();
        for (int i = assetsList.size() - 1; i >= 0; i--) {
            if (!TextUtils.isEmpty(assetsList.get(i).getParent()) && assetsList.get(i).getParent().equals(qr)) {
                childToDeleteList.add(i);
            }
        }
    }

    private void notifyAdapter(int pos) {
        if(pos<assetsList.size()){
            assetsList.remove(pos);
            qrList.remove(pos);
        }

        if (assetsList.size() > 0) {
            adapterAssetList.notifyDataSetChanged();
        } else {
            tvAddAsset.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.GONE);
            tvSave.setVisibility(View.INVISIBLE);
        }
    }

    private void getRescheduledDataList() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        IApiRequest apiRequest = ApiClient.getRequest();
        Call<List<RescheduleResultModel>> call = apiRequest.getRescheuleData(txnId, "rescheduled");
        if(type==1){
            call=apiRequest.getRescheuleData(txnId, auditStatus);
        }
        call.enqueue(new Callback<List<RescheduleResultModel>>() {
            @Override
            public void onResponse(Call<List<RescheduleResultModel>> call, Response<List<RescheduleResultModel>> response) {

                reschList = response.body();
                calculateData();
            }

            @Override
            public void onFailure(Call<List<RescheduleResultModel>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (t != null) {
                    Log.d("error_msg", "error-->" + t.toString());
                }
            }
        });
    }

    //method to make asset list from rescheduled data fetched from API
    private void calculateData() {
        AsyncTask.execute(() -> {
            for (int i = 0; i < reschList.size(); i++) {
                for (int j = 0; j < assetDetailList.size(); j++) {
                    if (reschList.get(i).getTypeId().toString().equals(assetDetailList.get(j).getAssetTypeId().toString())) {
                        AssetListResult assetListResult=new AssetListResult();
                        assetListResult.setAttributes(assetDetailList.get(j).getAttributes());
                        assetListResult.setAssetTypeId(assetDetailList.get(j).getAssetTypeId());
                        assetListResult.setName(assetDetailList.get(j).getName());
                        assetListResult.setParentTypes(assetDetailList.get(j).getParentTypes());
                        assetsList.add(assetListResult);

                        assetsList.get(assetsList.size() - 1).setQr_code(reschList.get(i).getQrCode());
                        qrList.add(reschList.get(i).getQrCode());
                        assetsList.get(assetsList.size() - 1).setParent(reschList.get(i).getParentAsset());
                        if(assetsList.size()>1){
                            String pName="";
                            int size=assetsList.size()-2;
                            for (int z=size; z>=0;z--){
                                if(TextUtils.isEmpty(assetsList.get(z).getParent())){
                                    pName=assetsList.get(z).getName();
                                    break;
                                }
                            }
                            assetsList.get(assetsList.size()-1).setParentName(pName);
                        }

                        List<RescheduleAttributeModel> reschAttrList = reschList.get(i).getAttributes();
                        List<AttributeResult> attributeResultList = assetDetailList.get(j).getAttributes();
                        attributeResultList.get(0).setQrValue(reschList.get(i).getQrCode());
                        for (int k = 0; k < reschAttrList.size(); k++) {
                            for (int l = 0; l < attributeResultList.size(); l++) {
                                if (reschAttrList.get(k).getId().toString().equals(attributeResultList.get(l).getId().toString())) {
                                    if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.TEXT)) {
                                        attributeResultList.get(l).setTextValue(reschAttrList.get(k).getValue());
                                    } else if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.NUMBER)) {
                                        attributeResultList.get(l).setNumValue(reschAttrList.get(k).getValue());
                                    } else if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.DATE)) {
                                        attributeResultList.get(l).setDate(reschAttrList.get(k).getValue());
                                    } else if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.PICKLIST)) {
                                        List<String> selectedValueList=new ArrayList<>();
                                        selectedValueList.add(reschAttrList.get(k).getValue());
                                        attributeResultList.get(l).setSelectedValueList(selectedValueList);
                                    } else if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.IMAGE)) {
                                        String imgVal=reschAttrList.get(k).getValue();
                                        //new Gson().fromJson(imgVal, ImageModel.class);
                                        try{
                                            JSONArray jsonArray=new JSONArray(imgVal);
                                            List<ImageModel> imgUrl=new ArrayList<>();
                                            for (int x=0;x<jsonArray.length();x++){
                                                JSONObject jsonObject=jsonArray.getJSONObject(x);
                                                Integer imgTagId=Integer.parseInt(jsonObject.get("img_tag_id").toString());
                                                ImageModel imageModel=new ImageModel();
                                                imageModel.setImg_tag_id(imgTagId);
                                                imageModel.setUrlPath(jsonObject.get("path").toString());
                                                imageModel.setName(jsonObject.get("tag").toString());
                                                imageModel.setTime(jsonObject.get("time").toString());
                                                imageModel.setLongitude(Double.parseDouble(jsonObject.get("longitude").toString()));
                                                imageModel.setLatitude(Double.parseDouble(jsonObject.get("latitude").toString()));

                                                for (int p=0;p<attributeResultList.get(l).getValues().size();p++){
                                                    if(imgTagId.toString().equals(attributeResultList.get(l).getValues().get(p).getId().toString())){
                                                        imageModel.setTag(attributeResultList.get(l).getValues().get(p).getAttributeValue());
                                                        break;
                                                    }
                                                }

                                                imgUrl.add(imageModel);
                                            }
                                            attributeResultList.get(l).setImgUrl(imgUrl);
                                            Log.d("",""+assetsList.size());
                                        }catch (Exception e){
                                            Log.d("",""+assetsList.size());
                                        }

                                    }else if (reschAttrList.get(k).getType().equals(AppConstants.ATTRIBUTE_TYPE.QR_CODE)){
                                        attributeResultList.get(l).setQrValue(reschAttrList.get(k).getValue());
                                    } else{
                                        attributeResultList.get(l).setTextValue(reschAttrList.get(k).getValue());
                                    }
                                    assetsList.get(assetsList.size()-1).setAttributes(attributeResultList);
                                    break;
                                }
                            }
                        }



                    }
                }
            }
        });

        Log.d("",""+assetsList.size());
        runOnUiThread(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if(assetsList.size()>0){
                  //  computeRequestData(2);
                    tvSave.setVisibility(View.VISIBLE);
                }
            }
            setAdapter();
        });
    }
}