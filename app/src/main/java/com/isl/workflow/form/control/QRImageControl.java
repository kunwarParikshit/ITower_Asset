package com.isl.workflow.form.control;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.isl.dao.cache.AppPreferences;
import com.isl.photo.camera.ViewImage64;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.dao.WorkFlowDatabaseHelper;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.UploadAssestDetail;
import com.isl.workflow.utils.WorkFlowUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class QRImageControl {

    Context ctx;
    AppPreferences mAppPreferences;
    String errMsg = "";
    List<UploadAssestDetail> list = new ArrayList<>();

    public QRImageControl() {
    }

    public QRImageControl(Context ctx) {
        this.ctx = ctx;
        this.mAppPreferences = new AppPreferences(ctx);
    }

    public Button qrBt(Context context, Fields field) {

        Button bt = new Button(ctx);
        bt.setId(Integer.parseInt(field.getId()));
        bt.setTag(field.getKey());
        imageButtonProperty(bt, field.getId(), field.getKey());
        FormCacheManager.getFormControls().get(field.getId()).setButtonCtrl(bt);
        if(field.isDisabled()){
            FormCacheManager.getFormControls().get(field.getId())
                    .getButtonCtrl().setEnabled(false);
        }
        if(field.isHidden()){
            FormCacheManager.getFormControls().get(field.getId())
                    .getButtonCtrl().setVisibility(View.GONE);
        }

        //FormCacheManager.getFormControls().get(field.getId()).setImgCounter(0);
        return bt;
    }

    public RecyclerView recyclerView1(Context context, Fields field) {

        RecyclerView imgGrid = new RecyclerView(ctx);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                false);
        imgGrid.setLayoutManager(horizontalLayoutManager);
        FormCacheManager.getFormControls().get(field.getId()).setImgGridView(imgGrid);

        if (FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
            initializePreviousAssetData(field);
        }
        return imgGrid;
    }

    public void displayImageName(String id, String key) {
        final Dialog actvity_dialog;
        actvity_dialog = new Dialog(ctx, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert); // operator list
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        TextView tv_confirmation = (TextView) actvity_dialog.findViewById(R.id.tv_header);
        tv_confirmation.setVisibility(View.GONE);
        TextView title = (TextView) actvity_dialog.findViewById(R.id.tv_title);
        title.setTypeface(Utils.typeFace(ctx));
        title.setText(Utils.msg(ctx, "825"));

        Button positive = (Button) actvity_dialog.findViewById(R.id.bt_ok);
        positive.setTypeface(Utils.typeFace(ctx));
        positive.setText(Utils.msg(ctx, "7"));
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                saveData(id);
            }
        });

        Button negative = (Button) actvity_dialog.findViewById(R.id.bt_cancel);
        negative.setTypeface(Utils.typeFace(ctx));
        negative.setText(Utils.msg(ctx, "8"));
        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
            }
        });
    }

    private void saveData(String id) {
        Fields formControl = null, formControl1 = null, formControl2 = null, formControl3 = null;
        if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("assettype")
                && FormCacheManager.getFormConfiguration().getFormFields().containsKey("assetlist")
                && FormCacheManager.getFormConfiguration().getFormFields().containsKey("AssetDetail")
                && FormCacheManager.getFormConfiguration().getFormFields().containsKey("assestdetails")) {

        formControl = FormCacheManager.getFormConfiguration().getFormFields().get( "assettype" );
        formControl1 = FormCacheManager.getFormConfiguration().getFormFields().get( "assetlist" );
        formControl2 = FormCacheManager.getFormConfiguration().getFormFields().get( "AssetDetail" );
        formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get( "assestdetails" );

        UploadAssestDetail uploadAssestDetail = new UploadAssestDetail();
        String selectedAssest = "" + FormCacheManager.getFormControls().get( formControl.getId() ).getSelectCtrl().getSelectedItem();
        String selectedAssestID = "" + FormCacheManager.getFormControls().get( formControl.getId() ).getSelectCtrl().getSelectedItemId();
        String selectedAssestList = "" + FormCacheManager.getFormControls().get( formControl1.getId() ).getSelectCtrl().getSelectedItem();
        String selectedAssestListID = "" + FormCacheManager.getFormControls().get( formControl1.getId() ).getSelectCtrl().getSelectedItemId();
        String assestTextDetails = "" + FormCacheManager.getFormControls().get( formControl2.getId() ).getTextBoxCtrl().getText();

        uploadAssestDetail.setId( id );
        uploadAssestDetail.setAssestid( selectedAssestID );
        uploadAssestDetail.setAssestName( selectedAssest );
        uploadAssestDetail.setAssetListid( selectedAssestListID );
        uploadAssestDetail.setAssestListName( selectedAssestList );
        uploadAssestDetail.setAssestDetails( assestTextDetails );
        list.add( uploadAssestDetail );

        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper( ctx );
        db.open();
        boolean isInerted = db.insertImages1( uploadAssestDetail, "1" );
        db.close();
        if (isInerted) {
            WorkFlowUtils.refreshDependentFields( ctx,formControl3,false );
            WorkFlowUtils.resetDependentFields( ctx,
                    formControl3,null );
        } else {
            String assestListCaption = "" + FormCacheManager.getFormControls().get( formControl1.getId() ).getCaptionCtrl().getText().toString();
            errMsg = Utils.msg( ctx, "827" ) + " " + assestListCaption + " for " + selectedAssest;
            Utils.toastMsg( ctx, errMsg );
            return;
        }
        setData(id,false );
      }
    }


    public void imageButtonProperty(Button ib, String id, String fieldKey) {

        final float scale = ctx.getResources().getDisplayMetrics().density;
        int margin = (int) (50 * scale);
        LinearLayout.LayoutParams CapturingPicIbParam = new LinearLayout.LayoutParams(margin, margin);
        CapturingPicIbParam.gravity = Gravity.CENTER_HORIZONTAL;
        CapturingPicIbParam.setMargins(12, 20, 12, 12);
        ib.setLayoutParams(CapturingPicIbParam);
        ib.setBackgroundResource(R.drawable.add_circle);

        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Fields assetTypeField = FormCacheManager.getFormConfiguration().getFormFields().get("assettype");
                FormFieldControl formControl1 = FormCacheManager.getFormControls().get(assetTypeField.getId());
                if(formControl1.getSelectedVal()!=null
                        && formControl1.getSelectCtrl().getVisibility()==View.VISIBLE
                        &&formControl1.getSelectCtrl().getSelectedItem().
                                toString().equalsIgnoreCase("Select")){
                    errMsg = Utils.msg(ctx, "256") + " " + formControl1.getCaptionCtrl().getText().toString();
                    Utils.toastMsg(ctx, errMsg);
                    return;
                }

                Fields assetAttrubute = FormCacheManager.getFormConfiguration().getFormFields().get("assetlist");
                FormFieldControl formControl2 = FormCacheManager.getFormControls().get(assetAttrubute.getId());
                if(formControl2.getSelectedVal()!=null
                        && formControl2.getSelectCtrl().getVisibility()==View.VISIBLE
                        && formControl2.getSelectCtrl().getSelectedItem().
                                toString().equalsIgnoreCase("Select")){
                    errMsg = Utils.msg(ctx, "256") + " " + formControl2.getCaptionCtrl().getText().toString();
                    Utils.toastMsg(ctx, errMsg);
                    return;
                }

                Fields assetDetails = FormCacheManager.getFormConfiguration().getFormFields().get("AssetDetail");
                FormFieldControl formControl3 = FormCacheManager.getFormControls().get(assetDetails.getId());
                if(formControl3.getTextBoxCtrl()!=null
                        && formControl3.getTextBoxCtrl().getVisibility()==View.VISIBLE
                        && formControl3.getTextBoxCtrl().getText().toString().length()==0){
                    errMsg = Utils.msg(ctx, "255") + " " + formControl3.getCaptionCtrl().getText().toString();
                    Utils.toastMsg(ctx, errMsg);
                    return;
                }
                displayImageName(String.valueOf(ib.getId()), ib.getTag().toString());
            }
        });
    }

    public void setData(String id,boolean hidden) {
        WorkFlowDatabaseHelper db = new WorkFlowDatabaseHelper(ctx);
        db.open();
        Cursor cursor = db.getWorkFlowImages(id, 0);
        if (cursor != null) {
            ArrayList imgViewList = new ArrayList<ViewImage64>();

            while (cursor.moveToNext()) {
                UploadAssestDetail viewImg = null;

                if (cursor.getString(1) != null && cursor.getString(1).length() > 0) {
                    viewImg = new UploadAssestDetail();
                    viewImg.setAssestid(cursor.getString(1));
                    viewImg.setAssestName(cursor.getString(2));
                    viewImg.setAssestListName(cursor.getString(4));
                    viewImg.setAssestDetails(cursor.getString(5));
                    imgViewList.add(viewImg);
                    FormCacheManager.getFormControls().get(id).increaseImgCounter();
                }
            }
            //FormCacheManager.getFormControls().get(id).setImgCounter(imgViewList.size());
            GridAdapter imageAdapter = new GridAdapter(imgViewList,ctx,id,hidden);
            FormCacheManager.getFormControls().get(id).getImgGridView().setAdapter(imageAdapter);
        }
        db.close();
    }

    //public void initializePreviousImages1(String fieldId, String fieldKey) {
    public void initializePreviousAssetData(Fields field) {
        WorkFlowDatabaseHelper dbHelper = new WorkFlowDatabaseHelper(ctx);
        dbHelper.open();
        dbHelper.deleteAssetDataImage(field.getId());


        if (FormCacheManager.getPrvFormData().get(field.getKey()) != null
                && !((String) FormCacheManager.getPrvFormData().get(field.getKey()))
                .equalsIgnoreCase("null")) {
            Type listType = new TypeToken<List<UploadAssestDetail>>() {
            }.getType();
            List<UploadAssestDetail> imgList = Constants.gson.fromJson((String) FormCacheManager.getPrvFormData().get(field.getKey()), listType);

            if (imgList != null && imgList.size() > 0) {
                for (UploadAssestDetail docDetail : imgList) {
                    docDetail.setId(field.getId());
                    dbHelper.insertImages1(docDetail,"3");
                }
            }
        }
        dbHelper.close();
        setData(field.getId(),field.isHidden());
    }
}


