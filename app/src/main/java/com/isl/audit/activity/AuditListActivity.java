package com.isl.audit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.api.ApiClient;
import com.isl.api.IApiRequest;
import com.isl.audit.adapter.AdapterAuditListItem;
import com.isl.audit.db.AppDatabase;
import com.isl.audit.db.AuditModel;
import com.isl.audit.model.AuditAssetResponse;
import com.isl.audit.model.AuditListResult;
import com.isl.audit.util.ItemClickListener;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import infozech.itower.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*This activity displays a list of Audits depending
upon their types i.e PENDING,DONE,RESCHEDULED,UPCOMING,TODAY'S*/
/*type=0 for Pending audits
        type=1 for Done audits
        type=2 for Upcoming audits
        type=3 for Today's audits
        type=4 for Rescheduled audits */
public class AuditListActivity extends AppCompatActivity implements ItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_no_audit_found)
    TextView tvNoAuditsFound;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    private int type = 0;
    private String bucket,status,assigneeCheck,auditPerformId="";
    private boolean isExist=false;

    private ProgressDialog progressDialog;
    private List<AuditAssetResponse> auditAssetList = new ArrayList<>();
    private List<AuditListResult> auditList=new ArrayList<>();
    private List<AuditListResult> syncAuditList=new ArrayList<>();
    private List<AuditModel> auditModelList=new ArrayList<>();
    private AuditModel savedAuditModel;
    private AppDatabase appDatabase;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_list);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.button_back)
    void goToback() {
        finish();
    }

    private void init(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.header));
        }
        appPreferences=new AppPreferences(this);
         type=getIntent().getIntExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE,0);
        /*type=0 for Pending audits
        type=1 for Done audits
        type=2 for Upcoming audits
        type=3 for Today's auAdits
        type=4 for Rescheduled audits */
        if(type==0){
            bucket="past";
            status="pending";
            tvTitle.setText(getString(R.string.pending_audits));
        }else if(type==1){
            //status="done";
            status="reviewPending,completed";
           // bucket="past";
            tvTitle.setText(getString(R.string.done_audits));
        }else if(type==2){
            status="pending";
            bucket="upcoming";
            tvTitle.setText(getString(R.string.upcoming_audits));
        }else if(type==3){
            status="pending";
            bucket="today";
            tvTitle.setText(getString(R.string.today_audits));
        }else{
            status="rescheduled";
            tvTitle.setText(getString(R.string.rescheduled_audits));
        }
        assigneeCheck="true";
        appDatabase=AppDatabase.getInstance(this);

        getSavedAuditData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private void getAuditList(){

        progressDialog=ProgressDialog.show(this,null,getString(R.string.loading));
        IApiRequest request= ApiClient.getRequest();

        Call<List<AuditListResult>> call=request.getAuditList(bucket,status,assigneeCheck);

        call.enqueue(new Callback<List<AuditListResult>>() {
            @Override
            public void onResponse(Call<List<AuditListResult>> call, Response<List<AuditListResult>> response) {
                if(response.code()==200){
                    auditList=response.body();
                    if(auditList != null && auditList.size()>0){
                        if(syncAuditList.size()>0){
                            for (int i=0;i<auditList.size();i++){
                                for (int j=0;j<syncAuditList.size();j++){
                                    if(auditList.get(i).getTxnId().toString().equals(syncAuditList.get(j).getTxnId().toString())){
                                        auditList.get(i).setSelectedAssetList(syncAuditList.get(j).getSelectedAssetList());
                                        break;
                                    }
                                }
                            }
                        }
                        AuditModel auditModel=new AuditModel();
                        auditModel.setType(type);
                        auditModel.setDataGrid(auditList);
                        saveAuditData(auditModel,0);
                        savedAuditModel=auditModel;

                        setAdapter();
                    }else{
                        getAuditAssetMapping();
                        tvNoAuditsFound.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    tvNoAuditsFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<AuditListResult>> call, Throwable t) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                tvNoAuditsFound.setVisibility(View.VISIBLE);
                if(t != null){
                    Log.d("error_msg","error-->"+t.toString());
                }
            }
        });
    }

    private void setAdapter() {
        if(!TextUtils.isEmpty(appPreferences.getUnsyncAudit()) && type !=1){
            String[] arrOfStr = appPreferences.getUnsyncAudit().split(",");
            if(arrOfStr != null && arrOfStr.length>0){

                for (int i=0;i<auditList.size();i++){
                    for (int j=0;j<arrOfStr.length;j++){
                        if(auditList.get(i).getTxnId().toString().equals(arrOfStr[j])){
                            auditList.get(i).setNeedSync(true);
                            break;
                        }
                        if(j==arrOfStr.length-1){
                            String unSyncAudit=appPreferences.getUnsyncAudit();
                            auditPerformId=auditList.get(i).getTxnId().toString();
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
                        }

                    }
                }
            }
        }
        tvNoAuditsFound.setVisibility(View.GONE);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        AdapterAuditListItem adapterAuditListItem=new AdapterAuditListItem(auditList,this,this);
        rvItems.setAdapter(adapterAuditListItem);
        if(Utils.isNetworkAvailable(this) ){
            getAuditAssetMapping();
        }else{
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onItemClickListener(View view, int position) {
        if (view.getId() == R.id.card_view) {
            /*if (type == 1) {
                return;
            }*/
            Intent intent = new Intent(this, AssetsListActivity.class);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE, type);
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_STATUS, auditList.get(position).getStatus());
            intent.putExtra(AppConstants.INTENT_EXTRAS.MATRIX_ID, auditList.get(position).getAuditMatrixId());
            intent.putExtra(AppConstants.INTENT_EXTRAS.SCHEDULED_DATE, auditList.get(position).getScheduleDate());
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_TYPE_API, auditList.get(position).getAuditType());
            intent.putExtra(AppConstants.INTENT_EXTRAS.TXN_ID, auditList.get(position).getTxnId());
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_NAME, auditList.get(position).getAuditName());
            intent.putExtra(AppConstants.INTENT_EXTRAS.SITE_ID, auditList.get(position).getSiteId().toString());
            intent.putExtra(AppConstants.INTENT_EXTRAS.OWNER_SITE_ID, auditList.get(position).getOwnerSiteId());
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_ID, auditList.get(position).getAuditId().toString());
            intent.putExtra(AppConstants.INTENT_EXTRAS.AUDIT_PERFORM_ID, auditPerformId);
            startActivity(intent);
        }
    }

    private void saveAuditData(AuditModel auditModel, int edittype){
        AsyncTask.execute(() -> {
            if(isExist){
                if(edittype==0){
                    appDatabase.auditDao().updateDataGrid(type,auditModel.getDataGrid());
                }else{
                    appDatabase.auditDao().updateAssetType(type,auditModel.getAssetTypes());
                }
            }else{
                appDatabase.auditDao().insertAudit(auditModel);
                isExist=true;
            }

        });
    }


    private void getSavedAuditData(){
        AsyncTask.execute(() -> {
            savedAuditModel=appDatabase.auditDao().getAuditData(type);
            if(savedAuditModel != null){
                auditModelList=appDatabase.auditDao().getAuditList();
                auditAssetList=savedAuditModel.getAssetTypes();
                for (int i=0;i<auditModelList.size();i++){
                    if(auditModelList.get(i).getType()==type && auditModelList.get(i).getDataGrid() != null &&
                            auditModelList.get(i).getDataGrid().size()>0){
                        auditList=auditModelList.get(i).getDataGrid();
                        isExist=true;
                        break;
                    }
                }
                for (int j=0;j<auditList.size();j++){
                    if(auditList.get(j).getSelectedAssetList() != null && auditList.get(j).getSelectedAssetList().size()>0){
                        syncAuditList.add(auditList.get(j));
                    }
                }
            }

            runOnUiThread(() -> {
                if (Utils.isNetworkAvailable(AuditListActivity.this)) {
                    getAuditList();
                }else{
                    setAdapter();
                }

            });

        });
    }
    private void getAuditAssetMapping(){
        IApiRequest request = ApiClient.getRequest();

        Call<List<AuditAssetResponse>> call = request.getAuditAssetList();
        call.enqueue(new Callback<List<AuditAssetResponse>>() {
            @Override
            public void onResponse(Call<List<AuditAssetResponse>> call, Response<List<AuditAssetResponse>> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.code() == 200) {
                    auditAssetList = response.body();
                    if (auditAssetList != null && auditAssetList.size() > 0) {
                    if(savedAuditModel != null){
                        savedAuditModel.setAssetTypes(auditAssetList);
                        saveAuditData(savedAuditModel,1);
                    }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<AuditAssetResponse>> call, Throwable t) {
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