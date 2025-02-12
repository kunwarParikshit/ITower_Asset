package com.isl.dao;
/*
 Modified by :  Dheeraj Paliwal
 Purpose     :  CR# 1.8.1
 Version     :  0.3 

 Modified by :  Dhakan lal
 Modified On :  21-May-2016.
 Purpose     :  CR# 1.9.1(iMaintain)
 Version     :  0.4

 Modified by :  Dhakan lal
 Modified On :  07-Feb-2017.
 Purpose     :  Multi Language
 Version     :  0.5

 Modified by :  Dhakan lal
 Modified On :  26-Apr-2024.
 Purpose     :  CR#DM-890
 Version     :  0.6
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;

import com.isl.constant.AppConstants;
import com.isl.constant.DefaultLevel;
import com.isl.constant.SQLConstants;
import com.isl.itower.MyApp;
import com.isl.modal.AlarmDesc;
import com.isl.modal.AssetList;
import com.isl.modal.BeanMultiLanguage;
import com.isl.modal.BeanPMCheckListForm;
import com.isl.modal.BeanSiteList;
import com.isl.modal.BeanSpare;
import com.isl.modal.BeanSpareParts;
import com.isl.modal.BeansEqipment;
import com.isl.modal.BeansSiteView;
import com.isl.modal.BeansSites;
import com.isl.modal.DgType;
import com.isl.modal.Equipment;
import com.isl.modal.Field;
import com.isl.modal.FuelSuppliers;
import com.isl.modal.Groups;
import com.isl.modal.HomeModule;
import com.isl.modal.Location;
import com.isl.modal.MenuDetail;
import com.isl.modal.Operators;
import com.isl.modal.Param;
import com.isl.modal.RcaCateg;
import com.isl.modal.ResponceFormRightDetails;
import com.isl.modal.ResponseGetUserInfo;
import com.isl.modal.ResponseUserInfoList;
import com.isl.modal.TTField;
import com.isl.modal.UserContact;
import com.isl.modal.Vender;
import com.isl.util.Utils;
import com.isl.workflow.constant.SQLConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class DataBaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDB = null;
    public static final int version = 47;
    Context context;
    JSONObject savedDataJsonObj = null;

    public DataBaseHelper(Context con) {
        super( con, AppConstants.db_name, null, version );
        this.context = con;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( SQLConstants.txnLocalData ); // 0.5
        db.execSQL( SQLConstants.rejectTxnLocalData ); // 0.5
        db.execSQL( SQLConstants.formRights );
        db.execSQL( SQLConstants.createLocationData );
        db.execSQL( SQLConstants.createSupplierSQL );
        db.execSQL( SQLConstants.createVender );
        db.execSQL( SQLConstants.createEdg );
        db.execSQL( SQLConstants.createEparam );
        db.execSQL( SQLConstants.dataTS );
        db.execSQL( SQLConstants.createAlarmDesc );
        db.execSQL( SQLConstants.createInciEquip );
        db.execSQL( SQLConstants.createInciparam );
        db.execSQL( SQLConstants.createIncioperator );
        db.execSQL( SQLConstants.createIncirca );
        db.execSQL( SQLConstants.createIncigrp );
        db.execSQL( SQLConstants.createAssetparam );
        db.execSQL( SQLConstants.createCheckPMForm );
        db.execSQL( SQLConstants.createPMValue );
        db.execSQL( SQLConstants.createNotification );
        db.execSQL( SQLConstants.createTTForm );
        db.execSQL( SQLConstants.createMultiLanguage );
        db.execSQL( SQLConstants.createSparePart );
        db.execSQL( SQLConstants.createUserAssoSites );
        db.execSQL( SQLConstants.createschedulelistbackup );
        db.execSQL( SQLConstants.createDonelistbackup );
        db.execSQL( SQLConstants.createMissedlistbackup );
        db.execSQL( SQLConstants.createVerifylistbackup );
        db.execSQL( SQLConstants.configMob );
        db.execSQL( SQLConstants.mobileInfo );
        db.execSQL( SQLConstants.createUserContact );
        db.execSQL( SQLConstants.mobileLogs );
        db.execSQL( SQLConstants.createTaskForm );
        db.execSQL( SQLConstants.createImages );
        db.execSQL( SQLConstants.createRejectlistbackup );
        db.execSQL( SQLConstants.pmChkListAutoSave );
        db.execSQL( SQLConstants.createReSubmitListbackup );
        db.execSQL( SQLConstants.CheckListReviewerForm );
        db.execSQL( SQLConstants.dataTSHS );
        db.execSQL( SQLConstant.workFlowFormTable );
        db.execSQL( SQLConstant.workFlowMetaData );
        db.execSQL( SQLConstant.workFlowDataTimeStamp );
        db.execSQL( SQLConstant.workFlowImages);
        db.execSQL( SQLConstants.scheduledAssets); //.6

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS TXN_LOCAL_DATA" );
        db.execSQL( "DROP TABLE IF EXISTS REJECT_TXN_LOCAL_DATA" );
        db.execSQL( "DROP TABLE IF EXISTS FORM_RIGHTS" );
        db.execSQL( "DROP TABLE IF EXISTS LOCATION_DDL" );
        db.execSQL( "DROP TABLE IF EXISTS SUPPLIER" );
        db.execSQL( "DROP TABLE IF EXISTS VENDER" );
        db.execSQL( "DROP TABLE IF EXISTS ENERGYDG" );
        db.execSQL( "DROP TABLE IF EXISTS ENERGYPARAMS" );
        //db.execSQL("DROP TABLE IF EXISTS DATA_TS");
        db.execSQL( "DROP TABLE IF EXISTS ALARM_DESC" );
        db.execSQL( "DROP TABLE IF EXISTS EQUIPMENT" );
        db.execSQL( "DROP TABLE IF EXISTS INCIDENT_META_PARAM" );
        db.execSQL( "DROP TABLE IF EXISTS INCIDENT_META_OPERATOR" );
        db.execSQL( "DROP TABLE IF EXISTS INCIDENT_META_RCA" );
        db.execSQL( "DROP TABLE IF EXISTS INCIDENT_META_GRP" );
        db.execSQL( "DROP TABLE IF EXISTS ASSET_META_PARAM" );
        db.execSQL( "DROP TABLE IF EXISTS CHECKLIST_PM_FORM" );
        db.execSQL( "DROP TABLE IF EXISTS TEMP_CHECKLIST" );
        db.execSQL( "DROP TABLE IF EXISTS NOTIFICATION" );
        db.execSQL( "DROP TABLE IF EXISTS CHECKLIST_TT_FORM" );
        //db.execSQL("DROP TABLE IF EXISTS MULTI_LANGUAGE");
        db.execSQL( "DROP TABLE IF EXISTS SPARE_PARTS" );
        //db.execSQL("DROP TABLE IF EXISTS USER_ASSO_SITES");
        db.execSQL( "DROP TABLE IF EXISTS SCHEDULE_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS DONE_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS MISSED_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS VERIFY_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS CONFIG_MOB" );
        db.execSQL( "DROP TABLE IF EXISTS MOBILE_INFO" );
        db.execSQL( "DROP TABLE IF EXISTS USER_CONTACT" );
        db.execSQL( "DROP TABLE IF EXISTS MOBILE_LOGS" );
        db.execSQL( "DROP TABLE IF EXISTS MST_TASK_FORM" );
        db.execSQL( "DROP TABLE IF EXISTS PM_IMAGES" );
        db.execSQL( "DROP TABLE IF EXISTS REJECT_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS AUTO_SAVE_PM_CHECKLIST" );
        db.execSQL( "DROP TABLE IF EXISTS RE_SUBMIT_PM_LIST" );
        db.execSQL( "DROP TABLE IF EXISTS CHECKLIST_REVIEWER_FORM" );
        //db.execSQL("DROP TABLE IF EXISTS DATA_TS_HS");
        db.execSQL( "DROP TABLE IF EXISTS MST_WORKFLOW_FORM" );
        db.execSQL( "DROP TABLE IF EXISTS WORKFLOW_META_DATA" );
        db.execSQL( "DROP TABLE IF EXISTS WORKFLOW_DATA_TS" );
        db.execSQL( "DROP TABLE IF EXISTS WORKFLOW_IMAGES" );
        db.execSQL( "DROP TABLE IF EXISTS SCHEDULED_ASSETS" );//.6
        onCreate( db );
    }

    public DataBaseHelper open() throws SQLException {
        sqLiteDB = getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteDB.close();
    }

    public void clearTaskForm(String TypeId) {
        sqLiteDB.delete( "MST_TASK_FORM", "TASK_TYPE_ID =" + TypeId, null );
    }
    //.6 start
    public void clearScheduledAssets(String txnId) {
        sqLiteDB.delete( "SCHEDULED_ASSETS", "PM_TXN_ID =" + txnId, null );
    }

    public void insertScheduledAssets(AssetList asset_list,String txn) {
        for (int i = 0; i < asset_list.getAseetList().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "ID", asset_list.getAseetList().get(i).getTXN_ID());
            values.put( "PM_TXN_ID", txn);
            values.put( "EQUIPMENT_ID", asset_list.getAseetList().get(i).getINITIATIVE_ID());
            values.put( "EQUIPMENT_NAME", asset_list.getAseetList().get(i).getINITIATIVE_NAME());
            values.put( "STATUS", asset_list.getAseetList().get(i).getSTATUS());
            values.put( "ASSET_ID", asset_list.getAseetList().get(i).getASSET_ID());
            values.put( "BRAND", asset_list.getAseetList().get(i).getBRAND());
            values.put( "MODEL", asset_list.getAseetList().get(i).getMODEL());
            values.put( "SERIAL_NUMBER", asset_list.getAseetList().get(i).getSERIAL_NUMBER());
            values.put( "CAPACITY", asset_list.getAseetList().get(i).getCAPACITY());
            values.put( "SCHEDULE_DATE", asset_list.getAseetList().get(i).getSCHEDULE_DATE());
            sqLiteDB.insert( "SCHEDULED_ASSETS", null, values );
        }
    }

    public int assetScheduledCount(String pmId,String status,String name) {
        int a = 0;
        Cursor c = null;
        if(status.equalsIgnoreCase("S")) {
            c = sqLiteDB.rawQuery("Select * from SCHEDULED_ASSETS WHERE PM_TXN_ID='" + pmId + "' AND EQUIPMENT_NAME='" + name + "'", null);
        }else{
            c = sqLiteDB.rawQuery("Select * from SCHEDULED_ASSETS WHERE PM_TXN_ID='" + pmId + "' AND STATUS='" + status + "' AND EQUIPMENT_NAME='" + name + "'", null);
        }
        if (c != null) {
            a = c.getCount();
        }
        return a;
    }



    public AssetList getEquimentScheduled(String pm_txnId) {
        BeansEqipment pojo = null;
        ArrayList<BeansEqipment> arrayList = new ArrayList<BeansEqipment>();
        AssetList asset_list = null;

        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select DISTINCT(EQUIPMENT_NAME),EQUIPMENT_ID from SCHEDULED_ASSETS WHERE PM_TXN_ID='"
                        + pm_txnId + "'", null );

        if (c != null) {
            arrayList.clear();
            asset_list = new AssetList();
            while (c.moveToNext()) {
                pojo = new BeansEqipment();
                pojo.setINITIATIVE_NAME( c.getString(c.getColumnIndex("EQUIPMENT_NAME")));
                pojo.setINITIATIVE_ID( c.getString(c.getColumnIndex("EQUIPMENT_ID")));
                arrayList.add(pojo);
            }
            asset_list.setAseetList( arrayList );

        }
        return asset_list;
    }


    public AssetList getAssetScheduled(String pm_txnId) {
        BeansEqipment pojo = null;
        ArrayList<BeansEqipment> arrayList = new ArrayList<BeansEqipment>();
        AssetList asset_list = null;

        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select DISTINCT(EQUIPMENT_NAME) from SCHEDULED_ASSETS WHERE PM_TXN_ID='"
                        + pm_txnId + "'", null );

        if (c != null) {
            arrayList.clear();
            asset_list = new AssetList();
            while (c.moveToNext()) {
                pojo = new BeansEqipment();
                pojo.setINITIATIVE_NAME( c.getString( c.getColumnIndex("EQUIPMENT_NAME" )));
                arrayList.add(pojo);
            }
            asset_list.setAseetList( arrayList );

        }
        return asset_list;
    }


    //.6 end

    public void deleteActivityImages(String TxnID,String assetId) {
        //sqLiteDB.delete( "PM_IMAGES", "TXN_ID='" + TxnID + "'", null );
        sqLiteDB.delete( "PM_IMAGES",
                "TXN_ID =? AND " +
                        "ASSET_ID =?",
                new String[]{TxnID, assetId} );
    }

    public void deleteImagesbyUser(String chkId, String TxnID, String type, String assetId, String tag) {
        sqLiteDB.delete( "PM_IMAGES",
                "CHECKLIST_ID =? AND " +
                        "TXN_ID =? AND " +
                        "IMAGE_TYPE =? AND " +
                        "ASSET_ID =? AND " +
                        "IMG_TAG =?",
                new String[]{chkId, TxnID, type, assetId,tag} );
    }

    public void deleteAutoSaveChk(String txn,String assetId) {
        sqLiteDB.delete( "AUTO_SAVE_PM_CHECKLIST",
                "TXN_ID =? AND " +
                        "ASSET_ID =?",
                new String[]{txn, assetId});
    }

    public void deleteActivity(String Data) {
        sqLiteDB.delete( "TXN_LOCAL_DATA", "ACTIVITY_DATA=?",
                new String[]{Data} );
    }

    public void deleteLocalTxnData(String txnId) {
        sqLiteDB.delete( "TXN_LOCAL_DATA", "TXN_ID=?", new String[]{txnId} );
    }

    public void deleteLogs(String Data) {
        sqLiteDB.delete( "MOBILE_LOGS", "Logs=?",
                new String[]{Data} );
    }

    public void deleteTxnData(String Date) {
        sqLiteDB.delete( "TXN_LOCAL_DATA", "DATE=?", new String[]{Date} );
    }

    public void deleteTxnRejectData(String date) {
        sqLiteDB.delete( "REJECT_TXN_LOCAL_DATA", "DATE=?",
                new String[]{date} );
    }

    public void clearCheckListPMForm(String module) {
        sqLiteDB.delete( "CHECKLIST_PM_FORM", "MODULE_ID=?", new String[]{module} );
    }

    public void clearCheckList(String module, String typeId,String equipmentId) {
        sqLiteDB.delete( "CHECKLIST_PM_FORM",
                "TypeId=? AND " +
                        "MODULE_ID=? AND " +
                        "equipmentId=?",
                new String[]{typeId, module,equipmentId} );
    }


    public void clearReviewerCheclist() {
        sqLiteDB.delete( "CHECKLIST_REVIEWER_FORM", null, null );
    }


    public void clearFormRights() {
        sqLiteDB.delete( "FORM_RIGHTS", null, null );
    }

    public void clearDataTS() {
        sqLiteDB.delete( "DATA_TS", null, null );

        //Also delete from work flow data time stamp table.
        sqLiteDB.delete( "WORKFLOW_DATA_TS", null, null );
    }

    public void clearDataTSHS() {
        sqLiteDB.delete( "DATA_TS_HS", null, null );
    }

    public void clearLocationData() {
        sqLiteDB.delete( "LOCATION_DDL", null, null );
    }

    public void clearSupplier() {
        sqLiteDB.delete( "SUPPLIER", null, null );
    }

    public void clearVender() {
        sqLiteDB.delete( "VENDER", null, null );
    }

    public void clearEnergyDg() {
        sqLiteDB.delete( "ENERGYDG", null, null );
    }

    public void clearEnergyParams() {
        sqLiteDB.delete( "ENERGYPARAMS", null, null );
    }

    public void clearAlarmDescData(String module) {
        sqLiteDB.delete( "ALARM_DESC", "MODULE_ID=?", new String[]{module} );
    }

    public void clearInciEqpData(String module) {
        sqLiteDB.delete( "EQUIPMENT", "MODULE_ID=?", new String[]{module} );
    }

    public void clearInciParamData(String module) {
        sqLiteDB.delete( "INCIDENT_META_PARAM", "MODULE_ID=?", new String[]{module} );
    }

    public void clearInciOpcoData(String module) {
        sqLiteDB.delete( "INCIDENT_META_OPERATOR", "MODULE_ID=?", new String[]{module} );
    }

    public void clearInciRcaData(String module) {
        sqLiteDB.delete( "INCIDENT_META_RCA", "MODULE_ID=?", new String[]{module} );
    }

    public void clearIncigrpData(String module) {
        sqLiteDB.delete( "INCIDENT_META_GRP", "MODULE_ID=?", new String[]{module} );
    }

    public void clearUserContact(String module) {
        sqLiteDB.delete( "USER_CONTACT", "MODULE_ID=?", new String[]{module} );
    }

    public void clearAssetParamData() {
        sqLiteDB.delete( "ASSET_META_PARAM", null, null );
    }

    public void clearTTChecklist() {
        sqLiteDB.delete( "CHECKLIST_TT_FORM", null, null );
    }

    public void deleteNotification(String notificationData, String userId) {
        sqLiteDB.delete( "NOTIFICATION", "notification=? AND userId =?",
                new String[]{notificationData, userId} );// 0.3
    }

    public void clearMultiLanguage() {
        sqLiteDB.delete( "MULTI_LANGUAGE", null, null ); // 0.5
    }

    public void clearSparePart() {
        sqLiteDB.delete( "SPARE_PARTS", null, null ); // 0.5
    }

    public void clearSites() {
        sqLiteDB.delete( "USER_ASSO_SITES", null, null ); // 0.5
    }

    public void updateImages(String sid, String activityId, String chkID, String schDt,
                             String type, String dgType, String imgname) {
        ContentValues values = new ContentValues();
        values.put( "UPLOADING_STATUS", "3" );
        sqLiteDB.update( "PM_IMAGES", values, "SITE_ID=? AND " +
                        "ACTIVITY_TYPE_ID =? AND " +
                        "CHECKLIST_ID =? AND " +
                        "SCHEDULE_DATE =? AND " +
                        "IMAGE_TYPE=? AND " +
                        "DG_TYPE =? AND " +
                        "IMG_NAME =?",
                new String[]{sid, activityId, chkID, schDt, type, dgType, imgname} );
    }

    public Cursor getChkImages() {
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select * from PM_IMAGES WHERE UPLOADING_STATUS=1", null );
        return c;
    }

    public void updateAutoSaveChkList(String txnId,String preImgPath,String postImgPath,
                                      String data,String assetId) {
        ContentValues values = new ContentValues();
        values.put( "DATA", data );
        values.put( "PRE_IMAGES", preImgPath);
        values.put( "POST_IMAGES", postImgPath);
        values.put( "DATE", Utils.date());
        values.put( "ASSET_ID", assetId);

        sqLiteDB.update( "AUTO_SAVE_PM_CHECKLIST", values, "TXN_ID=? AND ASSET_ID =?",
                new String[]{txnId, assetId} );// 0.3

    }

    public void updateAutoSaveRemarks(String txnId, String remarks,String assetId) {
        ContentValues values = new ContentValues();
        values.put( "REMARKS", remarks );
        sqLiteDB.update( "AUTO_SAVE_PM_CHECKLIST", values, "TXN_ID=? AND ASSET_ID =?",
                new String[]{txnId, assetId} );

    }
    //Code By Avdhesh
    public void updateAutoSaveRemarks1(String txnId, String remarks, String reviewRemarks,String assetId) {
        ContentValues values = new ContentValues();
        values.put( "REMARKS", remarks );
        values.put( "REVIEW_REMARKS", reviewRemarks );
        sqLiteDB.update( "AUTO_SAVE_PM_CHECKLIST", values, "TXN_ID=? AND ASSET_ID =?",
                new String[]{txnId, assetId} );

    }

    public void insertAutoSaveChkList(String txnId, String preImgPath,String postImgPath,
                                      String data,String assetId) {
        ContentValues values = new ContentValues();
        values.put( "TXN_ID", txnId );
        values.put( "DATA", data );
        values.put( "PRE_IMAGES", preImgPath );
        values.put( "POST_IMAGES", postImgPath );
        values.put( "DATE", Utils.date() );
        values.put( "ASSET_ID", assetId);
        sqLiteDB.insert( "AUTO_SAVE_PM_CHECKLIST", null, values );
        values.clear();
    }

    public int isAlreadyAutoSaveChk(String txn,String assetId) {
        int a = 0;
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select * from AUTO_SAVE_PM_CHECKLIST WHERE TXN_ID='"
                        + txn + "' AND ASSET_ID='" + assetId + "'", null );
        if (c.getCount() > 0) {
            a = 1;
        }
        return a;
    }

    public String getAutoSaveChk(String txn,String assetId) {
        String result = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select DATA from AUTO_SAVE_PM_CHECKLIST WHERE TXN_ID='"
                        + txn + "' AND ASSET_ID='" + assetId + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = c.getString( c.getColumnIndex( "DATA" ) );
            }
        }
        return result;
    }

    public String getAutoSaveRemarks(String txn,String assetId) {
        String result = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select REMARKS from AUTO_SAVE_PM_CHECKLIST WHERE TXN_ID='"
                        + txn + "' AND ASSET_ID='" + assetId + "'", null );

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = c.getString( c.getColumnIndex( "REMARKS" ) );
            }
        }
        return result;
    }

    public String getAutoSaveReviewRemarks(String txn,String assetId) {
        String result = "";
        String reviewRemarks = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select REMARKS,POST_IMAGES from AUTO_SAVE_PM_CHECKLIST WHERE TXN_ID='"
                        + txn + "' AND ASSET_ID='" + assetId + "'", null );

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = c.getString( c.getColumnIndex( "REMARKS" ) );
                reviewRemarks = c.getString( c.getColumnIndex( "POST_IMAGES" ) );
            }
        }
        return result + "~" + reviewRemarks;
    }

    //Code By Avdhesh
    public String getAutoSaveReviewerRemarks(String txn,String assetId) {
        String result = "";
        String reviewRemarks = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select REVIEW_REMARKS,POST_IMAGES from AUTO_SAVE_PM_CHECKLIST WHERE TXN_ID='"
                        + txn + "' AND ASSET_ID='" + assetId + "'", null );

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = c.getString( c.getColumnIndex( "REVIEW_REMARKS" ) );
                reviewRemarks = c.getString( c.getColumnIndex( "POST_IMAGES" ) );
            }
        }
        return result + "~" + reviewRemarks;
    }

    public void insertImages(String txnId, String chkID, String path, String tag, String lati, String longi,
                             String time, String time1, int type, int status,String schDt, String activityId,
                             String sid, String dgType, String imgname, String ip,String assetId) {
        ContentValues values = new ContentValues();
        values.put( "TXN_ID", txnId );
        values.put( "SITE_ID", sid );
        values.put( "ACTIVITY_TYPE_ID", activityId );
        values.put( "CHECKLIST_ID", "" + chkID );
        values.put( "SCHEDULE_DATE", schDt );
        values.put( "IMAGE_PATH", path );
        values.put( "IMG_TAG", tag.trim() );
        values.put( "LATI", lati );
        values.put( "LONGI", longi );
        values.put( "UPLOAD_TIME", time );
        values.put( "TIME", time1 );
        values.put( "IMAGE_TYPE", "" + type );
        values.put( "UPLOADING_STATUS", "" + status );
        values.put( "DG_TYPE", dgType );
        values.put( "IMG_NAME", imgname );
        values.put( "IP_ADDRESS", ip );
        values.put( "ASSET_ID", assetId );
        sqLiteDB.insert( "PM_IMAGES", null, values );
        values.clear();
    }

    public Cursor getChecklistItemImages(int prePostType, int fieldId, String dgType, String scheduleDate,
                                         String activityTypeId, String sid, String txnId, String assetId) {

        Cursor c = sqLiteDB.rawQuery
                ( "Select * from PM_IMAGES WHERE IMAGE_TYPE='" + prePostType + "' AND CHECKLIST_ID='" + fieldId + "' AND DG_TYPE='" + dgType + "' AND SCHEDULE_DATE='" + scheduleDate + "' AND ACTIVITY_TYPE_ID='" + activityTypeId + "' AND SITE_ID='" + sid + "' AND TXN_ID='" + txnId + "' AND ASSET_ID='" + assetId + "'", null );
        return c;
    }

    public String getImageparameter(int type, String dgType, String SCHEDULE_DATE,
                                    String activityTypeId, String sid, String txnId, String assetId) {
        String imageinfo = "";
        Cursor c = sqLiteDB.rawQuery( "Select * from PM_IMAGES WHERE IMAGE_TYPE='" + type + "' AND DG_TYPE='" + dgType + "' AND SCHEDULE_DATE='" + SCHEDULE_DATE + "' AND ACTIVITY_TYPE_ID='" + activityTypeId + "' AND SITE_ID='" + sid + "' AND TXN_ID='" + txnId + "' AND ASSET_ID='" + assetId + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                String chkId = c.getString( c.getColumnIndex( "CHECKLIST_ID" ) );
                String name = c.getString( c.getColumnIndex( "IMG_NAME" ) );
                if (name.contains("/")) {
                    String[] paths = name.split("/");
                    if(paths!=null && paths.length>1){
                        name =  paths[1];
                    }
                    //imgname = imgname.replaceAll("/", "");
                }

                String tag = c.getString( c.getColumnIndex( "IMG_TAG" ) );
                String time = c.getString( c.getColumnIndex( "UPLOAD_TIME" ) );
                String lati = c.getString( c.getColumnIndex( "LATI" ) );
                String longi = c.getString( c.getColumnIndex( "LONGI" ) );
                if (tag == null) {
                    tag = " ";
                }

                if (time == null) {
                    time = " ";
                }
                if (chkId != null && name != null) {
                    //imageinfo=imageinfo+chkId+"~"+name+"~"+tag+"~"+time+"$";
                    imageinfo = imageinfo + chkId + "~" + name + "~" + tag + "~" + time + "~" + lati + "~" + longi + "$";
                }
            }
            if (imageinfo.length() > 0) {
                imageinfo = imageinfo.substring( 0, imageinfo.length() - 1 );
            }
        }
        return imageinfo;
    }

    public String getImageInfoArr(int type, String dgType, String SCHEDULE_DATE, String activityTypeId,
                                  String sid, String txnId, String assetId) {
        JSONObject imgobj = null;
        JSONArray jsonArray;
        String str_arr = "";
        Cursor c = sqLiteDB.rawQuery( "Select * from PM_IMAGES WHERE IMAGE_TYPE='" + type + "' AND DG_TYPE='" + dgType + "' AND SCHEDULE_DATE='" + SCHEDULE_DATE + "' AND ACTIVITY_TYPE_ID='" + activityTypeId + "' AND SITE_ID='" + sid + "' AND TXN_ID='" + txnId + "' AND ASSET_ID='" + assetId + "'", null );
        if (c != null) {
            jsonArray = new JSONArray();
            while (c.moveToNext()) {
                String chkId = c.getString( c.getColumnIndex( "CHECKLIST_ID" ) );
                String name = c.getString( c.getColumnIndex( "IMG_NAME" ) );
                if (name.contains("/")) {
                    String[] paths = name.split("/");
                    if(paths!=null && paths.length>1){
                        name =  paths[1];
                    }
                    //imgname = imgname.replaceAll("/", "");
                }
                String tag = c.getString( c.getColumnIndex( "IMG_TAG" ) );
                String time = c.getString( c.getColumnIndex( "UPLOAD_TIME" ) );
                String lati = c.getString( c.getColumnIndex( "LATI" ) );
                String longi = c.getString( c.getColumnIndex( "LONGI" ) );
                if (tag == null) {
                    tag = " ";
                }

                if (time == null) {
                    time = " ";
                }
                if (chkId != null && name != null) {
                    try {
                        imgobj = new JSONObject();
                        imgobj.put( "name", tag );
                        imgobj.put( "time", time );
                        imgobj.put( "path", name );
                        imgobj.put( "autoSavePath", "" );
                        imgobj.put( "location", "" );
                        jsonArray.put( imgobj );
                    } catch (Exception e) {
                    }
                }
            }

            if (jsonArray != null) {
                str_arr = jsonArray.toString();
            }
        }
        return str_arr;
    }

    public void taskForm(ArrayList<Field> data, String TypeId) {
        Cursor c = sqLiteDB.rawQuery(
                "Select * from MST_TASK_FORM WHERE TASK_TYPE_ID ='" + TypeId + "'", null );
        ContentValues values = new ContentValues();
        for (int i = 0; i < data.size(); i++) {
            values.put( "TASK_TYPE_ID", TypeId );
            values.put( "FIELD_ID", data.get( i ).getId() );
            values.put( "FIELD_NAME", data.get( i ).getName() );
            values.put( "JSON_KEY", data.get( i ).getKey() );
            values.put( "DATA_TYPE", data.get( i ).getdType() );
            values.put( "DROPDOWN_VAL_TYPE", data.get( i ).getDdValType() );
            values.put( "DROPDOWN_VALUE", data.get( i ).getDdVal() );
            values.put( "DATA_LENGTH", data.get( i ).getLen() );
            values.put( "FIELD_TYPE", data.get( i ).getType() );
            values.put( "IS_MANDATORY", data.get( i ).getIsMan() );
            values.put( "GROUP_NAME", data.get( i ).getGName() );
            values.put( "GROUP_SEQ", data.get( i ).getGSeq() );
            values.put( "FIELD_SEQ", data.get( i ).getSeq() );
            values.put( "PARENT_LEVEL", data.get( i ).getLevel() );
            values.put( "PARENT_CHILD_RELATION", data.get( i ).getpChRel() );
            values.put( "RMK_ENABLE", data.get( i ).getRmk() );
            values.put( "RMK_MANDATORY_COND", data.get( i ).getRmlManCon() );
            values.put( "ON_CHANGE_RELOAD", data.get( i ).getOnChgReload() );
            values.put( "INITIALIZE_BY", data.get( i ).getInitialize() );
            sqLiteDB.insert( "MST_TASK_FORM", null, values );
            values.clear();
        }

    }

    public void insertMultiLanguage(List<BeanMultiLanguage> multiLanguageList) {
        for (int i = 0; i < multiLanguageList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "MessageId", multiLanguageList.get( i ).getId() );
            values.put( "Message", multiLanguageList.get( i ).getValue() );
            sqLiteDB.insert( "MULTI_LANGUAGE", null, values );
        }
    }

    public void insertDataLocally(String flag, String activityData, String allImga, String preImg,
                                  String postImg, String userId, String ip) {
        ContentValues values = new ContentValues();
        values.put( "FLAG", flag );
        values.put( "USER_ID", userId );
        values.put( "ACTIVITY_DATA", activityData );
        values.put( "DATE", Utils.date() );
        values.put( "PRE_IMG", preImg );
        values.put( "POST_IMG", postImg );
        values.put( "ALL_IMGS", allImga );
        values.put( "IP_ADDRESS", ip );
        sqLiteDB.insert( "TXN_LOCAL_DATA", null, values );
    }

    public void insertTxnDataLocally(String txnId, String txnType, String activityData, String allImga,
                                     String preImg, String postImg, String userId, String ip) {
        ContentValues values = new ContentValues();
        values.put( "TXN_ID", txnId );
        values.put( "FLAG", txnType );
        values.put( "USER_ID", userId );
        values.put( "ACTIVITY_DATA", activityData );
        values.put( "DATE", Utils.date() );
        values.put( "PRE_IMG", preImg );
        values.put( "POST_IMG", postImg );
        values.put( "ALL_IMGS", allImga );
        values.put( "IP_ADDRESS", ip );
        sqLiteDB.insert( "TXN_LOCAL_DATA", null, values );
    }

    public void insertRejectTxnDataLocally(String txnId, String txnType, String activityData, String userId, String msg) {
        ContentValues values = new ContentValues();
        values.put( "TXN_ID", txnId );
        values.put( "FLAG", txnType );
        values.put( "USER_ID", userId );
        values.put( "ACTIVITY_DATA", activityData );
        values.put( "ERROR_MSG", msg );
        values.put( "DATE", Utils.date() );
        sqLiteDB.insert( "REJECT_TXN_LOCAL_DATA", null, values );
    }

    public void rejectLocalTxnData(String flag, String activityData, String userId, String msg) {
        ContentValues values = new ContentValues();
        values.put( "FLAG", flag );
        values.put( "USER_ID", userId );
        values.put( "ACTIVITY_DATA", activityData );
        values.put( "ERROR_MSG", msg );
        values.put( "DATE", Utils.date() );
        sqLiteDB.insert( "REJECT_TXN_LOCAL_DATA", null, values );
    }

    public void insertFormRight(ArrayList<ResponceFormRightDetails> data) {
        for (ResponceFormRightDetails formDetail : data) {
            ContentValues values = new ContentValues();
            values.put( "MAIN_MENU", formDetail.getMainMenu() );
            values.put( "SUB_MENU", formDetail.getSubMenu() );
            values.put( "RIGHTS", formDetail.getRights() );
            values.put( "MAIN_MENU_SEQ", formDetail.getMainMenuSeq() );
            values.put( "SUB_MENU_SEQ", formDetail.getSubMenuSeq() );
            values.put( "MODULE_URL", formDetail.getUrlLink() );
            values.put( "MODULE_CAPTION", formDetail.getParentCaption() );
            values.put( "MENU_CAPTION", formDetail.getMenuCaption() );
            values.put( "MODULE_ID", formDetail.getParentId() );
            values.put( "MENU_ID", formDetail.getMenuId() );
            values.put( "SUBMENU_LINK", formDetail.getSubmenuLink() );
            sqLiteDB.insert( "FORM_RIGHTS", null, values );

           /* FormRightsPOJO formRightPOJO = new FormRightsPOJO(formDetail.getMainMenu(),
                    formDetail.getSubMenu(),formDetail.getRights(),formDetail.getMainMenuSeq(),
                    formDetail.getUrlLink(),formDetail.getParentCaption(),formDetail.getMenuCaption(),
                    Integer.parseInt(formDetail.getMenuId()),Integer.parseInt(formDetail.getParentId()));

            MyApp.getMyDatabase().formRightsDao().insertFormRights(formRightPOJO);*/


        }
    }

    public void insertPMCheckListForm(ArrayList<BeanPMCheckListForm> data,
                                      String module, int flag, String txnId,
                                      Context con,String equipmentID,String assetId) {
        for (int i = 0; i < data.size(); i++) {

            if (flag == 1) {
                sharePrefence( data.get( i ).getFieldId(), data.get( i ).getDefaultValue(),txnId,con
                ,assetId);
            }
            ContentValues values = new ContentValues();
            values.put( "fieldId", data.get( i ).getFieldId() );
            values.put( "fieldType", data.get( i ).getFieldType() );
            values.put( "fieldName", data.get( i ).getFieldName() );
            values.put( "dataType", data.get( i ).getDataType() );
            values.put( "length", data.get( i ).getLength() );
            values.put( "groupSeq", data.get( i ).getGrpSeq() );
            values.put( "groupName", data.get( i ).getGrpName() );
            values.put( "fieldSeq", data.get( i ).getFieldSeq() );
            values.put( "value", data.get( i ).getValue() );
            values.put( "mandatory", data.get( i ).getMandatory() );
            values.put( "PactivityId", data.get( i ).getpActivityId() );
            values.put( "pFlag", data.get( i ).getrFlag() );
            values.put( "rMandatory", data.get( i ).getrMandatory() );
            values.put( "TypeId", data.get( i ).getAtid() );
            values.put( "linkChkList", data.get( i ).getLinkChkList() );
            values.put( "validateKey", data.get( i ).getChkunq() );
            values.put( "preImgConfig", data.get( i ).getPreImgConfig() );
            values.put( "postImgConfig", data.get( i ).getPostImgConfig() );
            values.put( "preImgTag", data.get( i ).getPreImgTag() );
            values.put( "postImgTag", data.get( i ).getPostImgTag() );
            values.put( "preMediaType", data.get( i ).getPreMedia() );
            values.put( "postMediaType", data.get( i ).getPostMedia() );
            values.put( "MODULE_ID", module );
            values.put("REVIEW_REMARKS",data.get(i).getrRemark());
            values.put("equipmentId",equipmentID);
            sqLiteDB.insert( "CHECKLIST_PM_FORM", null, values );
        }
    }

    public void insertReviewerCheckList(ArrayList<BeanPMCheckListForm> data, String TypeId,
                                        String txnId, Context con, int flag,String assetId) {
        for (int i = 0; i < data.size(); i++) {
            if (flag == 1) {
                sharePrefence( data.get( i ).getFieldId(), data.get( i ).getStatus(),txnId,con,assetId);
            }
            sharePrefence( data.get( i ).getFieldId(), data.get( i ).getStatus(),txnId,con,assetId);
            ContentValues values = new ContentValues();
            values.put( "fieldId", data.get( i ).getFieldId() );
            values.put( "fieldType", data.get( i ).getFieldType() );
            values.put( "fieldName", data.get( i ).getFieldName() );
            values.put( "dataType", data.get( i ).getDataType() );
            values.put( "length", data.get( i ).getLength() );
            values.put( "groupSeq", data.get( i ).getGrpSeq() );
            values.put( "groupName", data.get( i ).getGrpName() );
            values.put( "fieldSeq", data.get( i ).getFieldSeq() );
            values.put( "value", data.get( i ).getValue() );
            values.put( "mandatory", data.get( i ).getMandatory() );
            values.put( "pActivityId", data.get( i ).getpActivityId() );
            values.put( "pFlag", data.get( i ).getrFlag() );
            values.put( "rMandatory", data.get( i ).getrMandatory() );
            values.put( "TypeId", TypeId );
            values.put( "status", data.get( i ).getStatus() );
            values.put( "viRemark", data.get( i ).getViRemark() );
            values.put( "rRemark", data.get( i ).getrRemark() );
            values.put( "preImgConfig", "0~1" );
            values.put( "postImgConfig", "0~1" );
            values.put( "preImgTag", "image1" );
            values.put( "postImgTag", "image1" );
            sqLiteDB.insert( "CHECKLIST_REVIEWER_FORM", null, values );
        }
    }


    public void insertViewCheckList(ArrayList<BeanPMCheckListForm> data, String TypeId) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "fieldId", data.get( i ).getFieldId() );
            values.put( "fieldType", "E" );
            values.put( "fieldName", data.get( i ).getFieldName() );
            values.put( "dataType", data.get( i ).getDataType() );
            values.put( "length", data.get( i ).getLength() );
            values.put( "groupSeq", data.get( i ).getGrpSeq() );
            values.put( "groupName", data.get( i ).getGrpName() );
            values.put( "fieldSeq", data.get( i ).getFieldSeq() );
            values.put( "value", data.get( i ).getValue() );
            values.put( "mandatory", data.get( i ).getMandatory() );
            values.put( "pActivityId", data.get( i ).getpActivityId() );
            values.put( "pFlag", "P" );
            values.put( "rMandatory", data.get( i ).getrMandatory() );
            values.put( "TypeId", TypeId );
            values.put( "status", data.get( i ).getStatus() );
            values.put( "viRemark", data.get( i ).getViRemark() );
            values.put( "rStatus", data.get( i ).getRstatus() );
            values.put( "rRemark", data.get( i ).getrRemark() );
            values.put( "preImgConfig", "0~1" );
            values.put( "postImgConfig", "0~1" );
            values.put( "preImgTag", "image1" );
            values.put( "postImgTag", "image1" );
            sqLiteDB.insert( "CHECKLIST_REVIEWER_FORM", null, values );
        }
    }

    public void sharePrefence(String id, String s, String txnId, Context con,String assetId) {

        if (savedDataJsonObj == null) {
            savedDataJsonObj = new JSONObject();
        }

        try {
            savedDataJsonObj.remove( "" + id );
            savedDataJsonObj.put( "" + id, s );
        } catch (JSONException e) {
        }

        DataBaseHelper db10 = new DataBaseHelper( con );
        db10.open();
        db10.updateAutoSaveChkList( txnId, "", "",
                savedDataJsonObj.toString(),assetId);
        db10.close();
    }


    public void dataTS(String[] dataTypeID, String[] timeStamp,
                       String dataType, String saveTime, int a, String module) {
        String table = "DATA_TS";
        if (module.equalsIgnoreCase( "955" )) {
            table = "DATA_TS_HS";
        }
        if (a == 0) {
            for (int i = 0; i < dataTypeID.length; i++) {
                ContentValues values = new ContentValues();
                values.put( "DATA_TYPE_ID", dataTypeID[i] );
                values.put( "LOGIN_TIME_STAMP", timeStamp[i] );
                values.put( "SAVE_TIME_STAMP", "01-JAN-2001 16:04:47" );
                values.put( "SAVE_TIME_STAMP_ENEG", "01-JAN-2001 16:04:47" );
                values.put( "SAVE_TIME_STAMP_ASS", "01-JAN-2001 16:04:47" );
                values.put( "SAVE_TIME_STAMP_ASSS", "01-JAN-2001 16:04:47" );
                sqLiteDB.insert( table, null, values );

                values.clear();
                //Update WorkFlow Data Time
                values.put( "TYPE_ID", dataTypeID[i] );
                values.put( "LOGIN_TIME", timeStamp[i] );
                values.put( "SAVE_TIME", "01-JAN-2001 16:04:47" );
                values.put( "NAME", "01-JAN-2001 16:04:47" );
            }
        } else if (a == 1) {
            for (int i = 0; i < dataTypeID.length; i++) {
                ContentValues values = new ContentValues();
                values.put( "LOGIN_TIME_STAMP", timeStamp[i] );
                sqLiteDB.update( table, values, "DATA_TYPE_ID="
                        + dataTypeID[i], null );
            }
        } else if (a == 2) {
            ContentValues values = new ContentValues();
            values.put( "SAVE_TIME_STAMP", saveTime );
            sqLiteDB.update( table, values, "DATA_TYPE_ID=" + dataType, null );
        } else if (a == 3) { // Energy param
            ContentValues values = new ContentValues();
            values.put( "SAVE_TIME_STAMP_ENEG", saveTime );
            sqLiteDB.update( table, values, "DATA_TYPE_ID=" + dataType, null );
        } else if (a == 4) { // assets param
            ContentValues values = new ContentValues();
            values.put( "SAVE_TIME_STAMP_ASS", saveTime );
            sqLiteDB.update( table, values, "DATA_TYPE_ID=" + dataType, null );
        } else if (a == 5) { // assets param
            ContentValues values = new ContentValues();
            values.put( "SAVE_TIME_STAMP_ASSS", saveTime );
            sqLiteDB.update( table, values, "DATA_TYPE_ID=" + dataType, null );
        }

    }

    public void updateWorkFlowTimeStamp(String dataTimeStamp, int flag) {
        String[] dataTimeStampArr = dataTimeStamp.split( "," );

        String[] tmpDataTS = new String[3];
        ContentValues values = new ContentValues();
        boolean isInsert = true;
        for (int index = 0; index < dataTimeStampArr.length; index++) {
            isInsert = true;
            tmpDataTS = dataTimeStampArr[index].split( "\\~" );
            System.out.println( "dataTimeStampArr - " + dataTimeStampArr[index] );
            if (flag == 0) {
                isInsert = true;
            } else if (flag == 1) {

                Cursor c = null;
                c = sqLiteDB.rawQuery( "Select DISTINCT(TYPE_ID) from WORKFLOW_DATA_TS WHERE TYPE_ID=" + tmpDataTS[0] + "", null );

                if (c != null) {
                    while (c.moveToNext()) {
                        if (c.getString( 0 ).length() > 0) {
                            isInsert = false;
                        } else {
                            isInsert = true;
                        }
                        ;
                    }
                } else {
                    isInsert = true;
                }
            }
            System.out.println( "TYPE_ID - " + tmpDataTS[0] + ", Inserting - " + isInsert );
            if (isInsert) {
                values.put( "TYPE_ID", tmpDataTS[0] );
                values.put( "LOGIN_TIME", tmpDataTS[1] );
                values.put( "NAME", tmpDataTS[2] );
                values.put( "SAVE_TIME", "01-JAN-2001 16:04:47" );
                sqLiteDB.insert( "WORKFLOW_DATA_TS", null, values );
                values.clear();
            } else {

                values.put( "LOGIN_TIME", tmpDataTS[1] );
                values.put( "NAME", tmpDataTS[2] );
                sqLiteDB.update( "WORKFLOW_DATA_TS", values, "TYPE_ID="
                        + tmpDataTS[0], null );

                values.clear();

            }
            ;
        }
    }

    public void insertLocationData(ArrayList<Location> data) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "COUNTRY_ID", data.get( i ).getCnId() );
            values.put( "COUNTRY", data.get( i ).getCountry() );
            values.put( "HUB_ID", data.get( i ).gethId() );
            values.put( "HUB", data.get( i ).getHub() );
            values.put( "REGION_ID", data.get( i ).getrId() );
            values.put( "REGION", data.get( i ).getRegion() );
            values.put( "CIRCLE_ID", data.get( i ).getCrId() );
            values.put( "CIRCLE", data.get( i ).getCircle() );
            values.put( "ZONE_ID", data.get( i ).getzId() );
            values.put( "ZONE", data.get( i ).getZone() );
            values.put( "CLUSTER_ID", data.get( i ).getClId() );
            values.put( "CLUSTER", data.get( i ).getCluster() );
            sqLiteDB.insert( "LOCATION_DDL", null, values );
        }
    }

    public void insertIntoSupplier(ArrayList<FuelSuppliers> products) {
        for (int i = 0; i < products.size(); i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put( "SUPPLIER_NAME", products.get( i ).getSuppName() );
            initialValues.put( "SUPPLIER_ID", products.get( i ).getSuppId() );
            initialValues.put( "CIRCLE_ID", products.get( i ).getCid() );
            initialValues.put( "ZONE_ID", products.get( i ).getZid() );
            initialValues.put( "CLUSTER_ID", products.get( i ).getClid() );
            sqLiteDB.insert( "SUPPLIER", null, initialValues );
        }
    }

    public void insertEnergyDG(ArrayList<DgType> products) {
        for (int i = 0; i < products.size(); i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put( "DG_TYPE", products.get( i ).getDgType() );
            initialValues.put( "DG_DESC", products.get( i ).getDgDesc() );
            sqLiteDB.insert( "ENERGYDG", null, initialValues );
        }
    }

    public void insertEnergyVender(ArrayList<Vender> products) {
        for (int i = 0; i < products.size(); i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put( "OME_NAME", products.get( i ).getOmeName() );
            initialValues.put( "OME_ID", products.get( i ).getOmeId() );
            initialValues.put( "REGION_ID", products.get( i ).getOmeCId() );
            initialValues.put( "CIRCLE_ID", products.get( i ).getOmeCId() );
            sqLiteDB.insert( "VENDER", null, initialValues );
        }
    }

    public void insertEnergyParam(ArrayList<Param> products) {
        for (int i = 0; i < products.size(); i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put( "PARAM_TYPE", products.get( i ).getParamType() );
            initialValues.put( "PARAM_NAME", products.get( i ).getParamName() );
            initialValues.put( "PARAM_ID", products.get( i ).getParamId() );
            initialValues.put( "PARAM_DESC", products.get( i ).getParamDesc() );
            initialValues.put( "P_SHORT_NAME", products.get( i ).getpShortName() );
            sqLiteDB.insert( "ENERGYPARAMS", null, initialValues );
        }
    }

    public void insertAlarmDescData(ArrayList<AlarmDesc> data, String module) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "ALARM_ID", data.get( i ).getAlarmId() );
            values.put( "ALARM_DESC", data.get( i ).getAlarm() );
            values.put( "EQUIP_ID", data.get( i ).getEquipId() );
            values.put( "SEVERITY_ID", data.get( i ).getSeverityId() );
            values.put( "ALARM_CATEGORY", data.get( i ).getAlarmCategoryId());
            values.put( "OPERATOR_ID", data.get( i ).getOperatorId());
            values.put( "SEVERITY_NAME", data.get( i ).getSeverityName());
            values.put( "EQUIP_NAME", data.get( i ).getEquipName());
            values.put( "MODULE_ID", module );

            sqLiteDB.insert( "ALARM_DESC", null, values );
        }
    }

    public void insertInciEqpcData(ArrayList<Equipment> data, String module) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "EQUIP_ID", data.get( i ).getEquipId() );
            values.put( "EQUIP_NAME", data.get( i ).getEquipName() );
            values.put( "MODULE_ID", module );
            sqLiteDB.insert( "EQUIPMENT", null, values );
        }
    }

    public void insertInciParamcData(ArrayList<Param> data, String moduleId) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "P_TYPE", data.get( i ).getParamType() );
            values.put( "P_ID", data.get( i ).getParamId() );
            values.put( "P_NAME", data.get( i ).getParamName() );
            values.put( "P_DESC", data.get( i ).getParamDesc() );
            values.put( "P_SHORT_NAME", data.get( i ).getpShortName());
            values.put( "CREATED_BY", data.get( i ).getCreatedBy());
            values.put( "MODULE_ID", moduleId );
            sqLiteDB.insert( "INCIDENT_META_PARAM", null, values );
        }
    }

    public void insertInciOpcoData(ArrayList<Operators> data, String module) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "OPCO_ID", data.get( i ).getOpcoId() );
            values.put( "OPCO_NAME", data.get( i ).getOpcoName() );
            values.put( "OPCO_CID", data.get( i ).getOpcoCId() );
            values.put( "MODULE_ID", module );
            sqLiteDB.insert( "INCIDENT_META_OPERATOR", null, values );
        }
    }

    public void insertInciRcaData(ArrayList<RcaCateg> data, String module) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            if (data.get( i ).getUserSubCat() != null
                    && !data.get( i ).getUserSubCat().isEmpty() && data.get( i ).getUserSubCat().contains(",")) {
                String arr[] = data.get( i ).getUserSubCat().split(",");
                String s =  data.get( i ).getUserSubCat();
                for(int a = 0; a<arr.length ; a++){
                    values.put( "RCA_ID", data.get( i ).getRcaId() );
                    values.put( "RCA_NAME", data.get( i ).getRcaName() );
                    if (data.get( i ).getParentId() != null
                            && !data.get( i ).getParentId().isEmpty()) {
                        values.put( "PARENT_ID", data.get( i ).getParentId() );
                    } else {
                        values.put( "PARENT_ID", "N" );
                    }
                    values.put( "IS_RCA_MANDATORY", data.get( i ).getIsRcaMandatory() );
                    values.put( "MODULE_ID", module );
                    values.put( "USER_CATEGORY", data.get( i ).getUserCat());
                    values.put("USER_SUB_CATEGORY",arr[a]);
                    sqLiteDB.insert( "INCIDENT_META_RCA", null, values );
                }
            }else{
                values.put( "RCA_ID", data.get( i ).getRcaId() );
                values.put( "RCA_NAME", data.get( i ).getRcaName() );
                if (data.get( i ).getParentId() != null
                        && !data.get( i ).getParentId().isEmpty()) {
                    values.put( "PARENT_ID", data.get( i ).getParentId() );
                } else {
                    values.put( "PARENT_ID", "N" );
                }
                values.put( "IS_RCA_MANDATORY", data.get( i ).getIsRcaMandatory() );
                values.put( "MODULE_ID", module );
                values.put( "USER_CATEGORY", data.get( i ).getUserCat());
                values.put("USER_SUB_CATEGORY", data.get( i ).getUserSubCat());
                sqLiteDB.insert( "INCIDENT_META_RCA", null, values );
            }

        }
    }

    public void insertInciGrpData(ArrayList<Groups> data, String module) {
        ContentValues values1 = new ContentValues();
        sqLiteDB.insert( "INCIDENT_META_GRP", null, values1 );
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "GRP_ID", data.get( i ).getGrpId() );
            values.put( "GRP_NAME", data.get( i ).getGrpName() );
            values.put( "MODULE_ID", module );
            sqLiteDB.insert( "INCIDENT_META_GRP", null, values );
        }
    }

    public void insertUserContact(ArrayList<UserContact> data, String module) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "GRP_ID", data.get( i ).getGrpId() );
            values.put( "U_ID", data.get( i ).getuId() );
            values.put( "U_DETAIL", data.get( i ).getUdetail() );
            values.put( "U_DESI", data.get( i ).getDesId() );
            values.put( "MODULE_ID", module );
            sqLiteDB.insert( "USER_CONTACT", null, values );
        }
    }

    public void insertAssetParamData(ArrayList<Param> data) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "P_TYPE", data.get( i ).getParamType() );
            values.put( "P_ID", data.get( i ).getParamId() );
            values.put( "P_NAME", data.get( i ).getParamName() );
            values.put( "P_DESC", data.get( i ).getParamDesc() );
            values.put( "P_SHORT_NAME", data.get( i ).getpShortName() );
            sqLiteDB.insert( "ASSET_META_PARAM", null, values );
        }
    }

    public void insertNotificationData(String userId, String notification) {
        ContentValues values = new ContentValues();
        values.put( "userId", userId );
        values.put( "notification", notification );
        values.put( "readFlag", "0" );
        sqLiteDB.insert( "NOTIFICATION", null, values );
    }

    public void insertTTCheckList(ArrayList<TTField> data) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "FieldId", data.get( i ).getFieldId() );
            values.put( "Caption", data.get( i ).getCaption() );
            values.put( "AddVisable", data.get( i ).getOn_add_screen() );
            values.put( "UpdateVisable", data.get( i ).getOn_update_screen() );
            values.put( "DetailsVisable", data.get( i ).getOn_detail_screen() );
            values.put( "ModuleId", data.get( i ).getModuleId() );
            sqLiteDB.insert( "CHECKLIST_TT_FORM", null, values );
        }
    }

    public void insertSparePart(ArrayList<BeanSpareParts> data) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "ID", data.get( i ).getSpareId() );
            values.put( "SpareName", data.get( i ).getSpareName() );
            values.put( "DataType", data.get( i ).getSpareDatatype() );
            values.put( "MaxQty", data.get( i ).getSpareQty() );
            values.put( "Flag", data.get( i ).getFlag() );
            values.put( "Qty", "" );
            values.put( "OrgQty", "" );
            values.put( "SerialNumber", "" );
            values.put( "Status", 0 );
            values.put( "SELECTION_CATEGORY", "Select Category" );
            values.put( "CATEGORY_ID", data.get( i ).getCryId() );
            values.put( "ACTIVITY_ID", data.get( i ).getActId() );
            sqLiteDB.insert( "SPARE_PARTS", null, values );
        }
    }

    public void insertSites(ArrayList<BeansSites> sites) {
        for (int i = 0; i < sites.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "EtsSiteID", sites.get( i ).getEtsId() );
            values.put( "SiteID", sites.get( i ).getSiteId().toUpperCase() );
            values.put( "lat", sites.get( i ).getSiteLat() );
            values.put( "longi", sites.get( i ).getSiteLong() );
            sqLiteDB.insert( "USER_ASSO_SITES", null, values );
        }
    }

    /**************** get data in sqlite **************************************/
    // *****get Login time stamp*****************
    public String getLoginTimeStmp(String timeStamp, String module) {
        String loginTimestmp = null;
        Cursor c = null;
        if (module.equalsIgnoreCase( "955" )) {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(LOGIN_TIME_STAMP) from DATA_TS_HS WHERE DATA_TYPE_ID='"
                            + timeStamp + "'", null );
        } else {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(LOGIN_TIME_STAMP) from DATA_TS WHERE DATA_TYPE_ID='"
                            + timeStamp + "'", null );
        }

        if (c != null) {
            while (c.moveToNext()) {
                loginTimestmp = c.getString( c
                        .getColumnIndex( "LOGIN_TIME_STAMP" ) );
            }
        }
        return loginTimestmp;
    }


    // *****get save time stamp*****************
    public String getSaveTimeStmp(String timeStamp, String module) {
        String saveTimestmp = null;
        Cursor c = null;
        if (module.equalsIgnoreCase( "955" )) {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(SAVE_TIME_STAMP) from DATA_TS_HS WHERE DATA_TYPE_ID='"
                            + timeStamp + "'", null );
        } else {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(SAVE_TIME_STAMP) from DATA_TS WHERE DATA_TYPE_ID='"
                            + timeStamp + "'", null );
        }
        if (c != null) {
            while (c.moveToNext()) {
                saveTimestmp = c.getString( c.getColumnIndex( "SAVE_TIME_STAMP" ) );
            }
        }
        return saveTimestmp;
    }

    // *****get save time stamp*****************
    public String getSaveTimeStmpEner(String timeStamp) {
        String saveTimestmp = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(SAVE_TIME_STAMP_ENEG) from DATA_TS WHERE DATA_TYPE_ID='"
                        + timeStamp + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                saveTimestmp = c.getString( c
                        .getColumnIndex( "SAVE_TIME_STAMP_ENEG" ) );
            }
        }
        return saveTimestmp;
    }

    // *****get save time stamp*****************
    public String getSaveTimeStmpAsset(String timeStamp) {
        String saveTimestmp = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(SAVE_TIME_STAMP_ASS) from DATA_TS WHERE DATA_TYPE_ID='"
                        + timeStamp + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                saveTimestmp = c.getString( c
                        .getColumnIndex( "SAVE_TIME_STAMP_ASS" ) );
            }
        }
        return saveTimestmp;
    }

    // *****get save time stamp*****************
    public String getSaveTimeStmpAssset(String timeStamp) {
        String saveTimestmp = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(SAVE_TIME_STAMP_ASSS) from DATA_TS WHERE DATA_TYPE_ID='"
                        + timeStamp + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                saveTimestmp = c.getString( c
                        .getColumnIndex( "SAVE_TIME_STAMP_ASSS" ) );
            }
        }
        return saveTimestmp;
    }

    public ArrayList<String> getRegion(String circleId, int a) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        Cursor c = null;
        if (a == 0) {
            c = sqLiteDB
                    .rawQuery(
                            "Select DISTINCT(CIRCLE) from LOCATION_DDL ORDER BY UPPER(CIRCLE) ASC",
                            null );
        } else {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(CIRCLE) from LOCATION_DDL WHERE CIRCLE_ID IN ("
                            + circleId + ") ORDER BY UPPER(CIRCLE) ASC", null );
        }
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString( c.getColumnIndex( "CIRCLE" ) ).toString()
                        .isEmpty()
                        && c.getString( c.getColumnIndex( "CIRCLE" ) ).toString() != null) {
                    list.add( c.getString( c.getColumnIndex( "CIRCLE" ) ) );
                }
            }
        }
        return list;
    }

    public Cursor getCursor(String SQL, String where1) {
        Cursor c = null;
        String SQL1 = "";
        String[] arr = new String[3];
        arr = SQL.split( "\\$" );
        if (SQL.contains( "$" )) {
            if (where1.length() > 0) {
                SQL1 = SQL.replaceAll( "\\$", "" ).replaceAll( "\\@", where1 );
            } else {
                SQL1 = arr[0] + arr[2];
            }
        } else {
            SQL1 = SQL.replaceAll( "\\@", where1 );
        }
        c = sqLiteDB.rawQuery( SQL1, null );
        return c;
    }

    public String getCircleName(String id) {
        String name = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(CIRCLE) from LOCATION_DDL WHERE CIRCLE_ID='"
                        + id + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                name = c.getString( c.getColumnIndex( "CIRCLE" ) );
            }
        }
        return name;
    }

    public ArrayList<String> getAllSubRegions(String circle) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        list.add( "Select Sub Region" );
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(ZONE) from LOCATION_DDL WHERE CIRCLE='"
                        + circle + "' ORDER BY UPPER(ZONE) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString( c.getColumnIndex( "ZONE" ) ).toString().isEmpty()
                        && c.getString( c.getColumnIndex( "ZONE" ) ).toString() != null) {
                    list.add( c.getString( c.getColumnIndex( "ZONE" ) ) );
                }
            }
        }
        return list;

    }


    public ArrayList<String> getAllMini(String circle, String cluster) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        list.add( Utils.msg( context, "197" ) );
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(CLUSTER) from LOCATION_DDL WHERE CIRCLE='"
                        + circle + "' AND ZONE='" + cluster
                        + "' ORDER BY UPPER(CLUSTER) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString( c.getColumnIndex( "CLUSTER" ) ).toString()
                        .isEmpty()
                        && c.getString( c.getColumnIndex( "CLUSTER" ) ).toString() != null) {
                    list.add( c.getString( c.getColumnIndex( "CLUSTER" ) ) );
                }
            }
        }
        return list;
    }


    public String getMiniId(String circle, String cluster) {
        String id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(CLUSTER_ID) from LOCATION_DDL WHERE CIRCLE='"
                        + circle + "' AND CLUSTER='" + cluster + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "CLUSTER_ID" ) );
            }
        }
        return id;
    }

    // *******get dg desc************
    public ArrayList<String> getEnergyDgDesc() {
        ArrayList<String> desc_List = null;
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select DISTINCT(DG_DESC) from ENERGYDG ORDER BY DG_TYPE",
                        null );
        if (c != null) {
            desc_List = new ArrayList<String>();
            while (c.moveToNext()) {
                if (!c.getString( c.getColumnIndex( "DG_DESC" ) ).toString()
                        .isEmpty()
                        && c.getString( c.getColumnIndex( "DG_DESC" ) ).toString() != null) {
                    desc_List.add( c.getString( c.getColumnIndex( "DG_DESC" ) ) );
                }
            }
        }
        return desc_List;
    }

    public String getAssetDgId(String dgdesc) {
        String dg_id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(DG_TYPE) from ENERGYDG WHERE DG_DESC='"
                        + dgdesc + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                dg_id = c.getString( c.getColumnIndex( "DG_TYPE" ) );
            }
        }
        return dg_id;
    }

    public String getDgName(String str) {
        String dg_id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DG_DESC from ENERGYDG WHERE DG_TYPE='" + str + "'",
                null );
        if (c != null) {
            while (c.moveToNext()) {
                dg_id = c.getString( c.getColumnIndex( "DG_DESC" ) );
            }
        }
        return dg_id;
    }


    // *******get vender************
    public ArrayList<String> getVender(String circle) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        list.add( "Select Vendor" );
        // Cursor c =
        // sqLiteDB.rawQuery("Select DISTINCT(OME_NAME) from VENDER WHERE REGION_ID='"
        // + circle + "' ORDER BY UPPER(OME_NAME) ASC", null);
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select DISTINCT(OME_NAME) from VENDER ORDER BY UPPER(OME_NAME) ASC",
                        null );
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString( c.getColumnIndex( "OME_NAME" ) ).toString()
                        .isEmpty()
                        && c.getString( c.getColumnIndex( "OME_NAME" ) ).toString() != null) {
                    list.add( c.getString( c.getColumnIndex( "OME_NAME" ) ) );
                }
            }
        }
        return list;
    }

    // *******get vender id************
    public String getVenderId(String circle, String omeName) {
        String venderId = null;
        // Cursor c =
        // sqLiteDB.rawQuery("Select DISTINCT(OME_ID) from VENDER WHERE REGION_ID='"
        // + circle + "' AND OME_NAME='" + omeName + "'",null);
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(OME_ID) from VENDER WHERE OME_NAME='"
                        + omeName + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                venderId = c.getString( c.getColumnIndex( "OME_ID" ) );
            }
        }
        return venderId;
    }

    public String getVenderName(String id) {
        String venderName = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select OME_NAME from VENDER WHERE OME_ID='" + id + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                venderName = c.getString( c.getColumnIndex( "OME_NAME" ) );
            }
        }
        return venderName;
    }

    // ********get equipment *********
    public ArrayList<String> getEquipment(TextView tv, String module,String category,String userSubcategory) {
        ArrayList<String> inci_equip = null;
        Cursor c = null;
        inci_equip = new ArrayList<String>();
        inci_equip.add( "Select " + tv.getText().toString().trim() );
        if(category.equalsIgnoreCase("2")){
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(EQUIP_NAME) from ALARM_DESC WHERE MODULE_ID='"
                            + module + "' AND OPERATOR_ID='" + userSubcategory + "' ORDER BY UPPER(EQUIP_NAME) ASC", null );
        }else{
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(EQUIP_NAME) from EQUIPMENT WHERE MODULE_ID='"
                            + module + "' ORDER BY UPPER(EQUIP_NAME) ASC", null );
        }
        if (c != null) {
            while (c.moveToNext()) {
                inci_equip.add( c.getString( c.getColumnIndex( "EQUIP_NAME" ) ) );
            }
        }
        return inci_equip;
    }

    // ********get equipment *********
    public ArrayList<String> getSeverity(TextView tv, String module,String userSubcategory) {
        ArrayList<String> inci_severity= null;
        Cursor c = null;
        inci_severity = new ArrayList<String>();
        inci_severity.add( "Select " + tv.getText().toString().trim());
        c = sqLiteDB.rawQuery(
                "Select DISTINCT(SEVERITY_NAME) from ALARM_DESC WHERE MODULE_ID='"
                        + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(SEVERITY_ID) ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_severity.add( c.getString( c.getColumnIndex( "SEVERITY_NAME" ) ) );
            }
        }
        return inci_severity;
    }

    // ********get equipment *********
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getTicketType(TextView tv, String module, String userSubcategory) {
        ArrayList<String> tempList= null;
        String tempStr;
        String paramType = "33";
        Cursor c = null;

        tempList = new ArrayList<String>();
        c = sqLiteDB.rawQuery(
                "Select DISTINCT(ALARM_CATEGORY) from ALARM_DESC WHERE MODULE_ID='"
                        + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(ALARM_CATEGORY) ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                String str = c.getString(c.getColumnIndex("ALARM_CATEGORY"));
                if (!str.equalsIgnoreCase( "null" ) && !str.isEmpty()) {
                    tempList.add(str);
                }
                //tempList.add( c.getString(c.getColumnIndex("ALARM_CATEGORY")));
            }
        }

        tempStr = String.join(",", tempList);
        ArrayList<String> tkt_type= null;
        String hsse  = "HSSE";
        tkt_type = new ArrayList<String>();
        tkt_type.add( "Select " + tv.getText().toString().trim());

        Cursor c1 = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "' AND P_SHORT_NAME!='" + hsse + "' AND P_DESC IN (" + tempStr + ") ORDER BY P_NAME ASC", null );

        int a = c1.getCount();
        if (c1 != null) {
            while (c1.moveToNext()) {
                tkt_type.add( c1.getString( c1.getColumnIndex( "P_NAME" )));
            }
        }
        return tkt_type;
    }

    // ********get equipment id *********
    public String getEquipmentId(String name, String module) {
        String equipmentId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(EQUIP_ID) from EQUIPMENT WHERE EQUIP_NAME='"
                        + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                equipmentId = c.getString( c.getColumnIndex( "EQUIP_ID" ) );
                if (!equipmentId.equalsIgnoreCase( "null" )
                        && !equipmentId.isEmpty()) {
                    equipmentId = c.getString( c.getColumnIndex( "EQUIP_ID" ) );
                } else {
                    equipmentId = "R";
                }
            }
        } else {
            equipmentId = "R";
        }
        return equipmentId;
    }

    // ********get equipment id *********
    public String getEquipmentId1(String name, String module) {
        String equipmentId = "0";
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(EQUIP_ID) from EQUIPMENT WHERE EQUIP_NAME='"
                        + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                equipmentId = c.getString( c.getColumnIndex( "EQUIP_ID" ) );
                if (!equipmentId.equalsIgnoreCase( "null" )
                        && !equipmentId.isEmpty()) {
                    equipmentId = c.getString( c.getColumnIndex( "EQUIP_ID" ) );
                } else {
                    equipmentId = "0";
                }
            }
        } else {
            equipmentId = "0";
        }
        return equipmentId;
    }

    // ********get equipment id *********
    public String getEquipmentName(String id, String module) {
        String equipmentName = "";
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(EQUIP_NAME) from EQUIPMENT WHERE EQUIP_ID='"
                        + id + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                equipmentName = c.getString( c.getColumnIndex( "EQUIP_NAME" ) );
                if (!equipmentName.equalsIgnoreCase( "null" )
                        && !equipmentName.isEmpty()) {
                    equipmentName = c.getString( c.getColumnIndex( "EQUIP_NAME" ) );
                } else {
                    equipmentName = "";
                }
            }
        } else {
            equipmentName = "";
        }
        return equipmentName;
    }




    public ArrayList<String> getParamName(String paramType,String module) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        list.add("Select");
        Cursor c = sqLiteDB.rawQuery(
                "Select P_NAME from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "'ORDER BY P_DESC ASC", null);

        if (c != null) {
            while (c.moveToNext()) {
                list.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return list;
    }

    public ArrayList<String> getPreventiveParamName(String paramType,String module,String parmDesc) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "' AND P_DESC='" + parmDesc + "' ORDER BY P_NAME ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                list.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return list;
    }

    // ********get Incident Param *********
    public ArrayList<String> getInciParam1(String paramType, TextView tv, String module) {
        ArrayList<String> inci_param = null;
        inci_param = new ArrayList<String>();
        if (!paramType.equalsIgnoreCase( "20" )) {
            inci_param.add( "Select " + tv.getText().toString().trim() );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "'ORDER BY P_ID ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return inci_param;
    }

    // ********get Incident Param *********
    public ArrayList<String> getParamSort(String paramType, TextView tv,
                                          String module,String activityTypeId) {
        ArrayList<String> inci_param = null;
        inci_param = new ArrayList<String>();
        if (!paramType.equalsIgnoreCase( "20" )) {
            inci_param.add( "Select " + tv.getText().toString().trim() );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_SHORT_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_DESC='" + activityTypeId+ "' AND MODULE_ID='" + module + "'ORDER BY P_ID ASC", null );


        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_SHORT_NAME" ) ) );
            }
        }
        return inci_param;
    }

    public ArrayList<String> getInciParam3(String paramType,String module) {
        ArrayList<String> inci_param = null;
        inci_param = new ArrayList<String>();
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "'ORDER BY P_ID ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return inci_param;
    }


    public ArrayList<String> getInciParam2(String paramType, TextView tv, String module) {
        ArrayList<String> inci_param = null;
        inci_param = new ArrayList<String>();
        if (!paramType.equalsIgnoreCase( "20" )) {
            inci_param.add( "Select " + tv.getText().toString().trim() );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "'ORDER BY P_NAME ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return inci_param;
    }

    public ArrayList<String> getInciParam3(String paramType, TextView tv, String module) {
        ArrayList<String> inci_param = null;
        String hsse  = "HSSE";
        inci_param = new ArrayList<String>();
        if (!paramType.equalsIgnoreCase( "20" )) {
            inci_param.add( "Select " + tv.getText().toString().trim() );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='" + module + "' AND P_SHORT_NAME!='" + hsse + "' ORDER BY P_NAME ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return inci_param;
    }

    public ArrayList<String> getInciParam4(String paramType, TextView tv,
                                           String module,String paramId) {
        ArrayList<String> inci_param = null;
        inci_param = new ArrayList<String>();
        if (!paramType.equalsIgnoreCase( "20" )) {
            inci_param.add( "Select " + tv.getText().toString().trim() );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND MODULE_ID='"
                        + module + "' AND P_ID IN (" + paramId + ") ORDER BY P_ID ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return inci_param;
    }

    // ********get Incident Param id *********
    public String getPDesc(String paramType, String pShortName, String module) {
        String P_DESC = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select P_DESC from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_SHORT_NAME='" + pShortName + "' AND MODULE_ID='" + module + "'", null );
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                P_DESC = c.getString( c.getColumnIndex( "P_DESC" ) );
                if (!P_DESC.equalsIgnoreCase( "null" ) && !P_DESC.isEmpty()) {
                    P_DESC = c.getString( c.getColumnIndex( "P_DESC" ) );
                } else {
                    P_DESC = "R";
                }
            }
        } else {
            P_DESC = "R";
        }
        return P_DESC;
    }

    // ********get Incident Param id *********
    public String getParmName(String paramType, String pShortName, String module) {
        String P_DESC = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select P_NAME from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_SHORT_NAME='" + pShortName + "' AND MODULE_ID='" + module + "'", null );
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                P_DESC = c.getString( c.getColumnIndex( "P_NAME" ) );
                if (!P_DESC.equalsIgnoreCase( "null" ) && !P_DESC.isEmpty()) {
                    P_DESC = c.getString( c.getColumnIndex( "P_NAME" ) );
                } else {
                    P_DESC = "R";
                }
            }
        } else {
            P_DESC = "R";
        }
        return P_DESC;
    }

    // ********get Incident Param id *********
    public String getParmShortName(String paramType, String id, String module) {
        String pShortName = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select P_SHORT_NAME from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_ID='" + id + "' AND MODULE_ID='" + module + "'", null );
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                pShortName = c.getString( c.getColumnIndex( "P_SHORT_NAME" ) );
                if (!pShortName.equalsIgnoreCase( "null" ) && !pShortName.isEmpty()) {
                    pShortName = c.getString( c.getColumnIndex( "P_SHORT_NAME" ) );
                } else {
                    pShortName = "R";
                }
            }
        } else {
            pShortName = "R";
        }
        return pShortName;
    }

    // ********get Incident Param id *********
    public String getInciParamId(String paramType, String name, String module) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_ID) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_NAME='" + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_ID" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_ID" ) );
                } else {
                    pId = "0";
                }
            }
        } else {
            pId = "0";
        }
        return pId;
    }

    // ********get Incident Param id *********
    public String getInciParamIdd(String paramType, String name, String module) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_ID) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_NAME='" + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_ID" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_ID" ) );
                } else {
                    pId = "";
                }
            }
        } else {
            pId = "";
        }
        return pId;
    }


    // ********get Incident Param id *********
    public String getInciParamId23(String paramType, String pSortname, String module) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_SHORT_NAME='" + pSortname + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_NAME" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_NAME" ) );
                } else {
                    pId = "";
                }
            }
        } else {
            pId = "";
        }
        return pId;
    }

    // ********get Incident Param id *********
    public String getInciCreatedBy(String paramType, String id, String module,String activityTypeId) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(CREATED_BY) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_DESC='" + activityTypeId+ "' AND P_NAME='" + id + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "CREATED_BY" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "CREATED_BY" ) );
                } else {
                    pId = "";
                }
            }
        } else {
            pId = "";
        }
        return pId;
    }

    // ********get Incident Param id *********
    public String getInciParamDesc(String paramType, String name, String module) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_DESC) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_NAME='" + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                } else {
                    pId = "R";
                }
            }
        } else {
            pId = "R";
        }
        return pId;
    }


    // ********get Incident Param id *********
    public String getInciParamDesc1(String paramType, String name, String module) {
        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_DESC) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_NAME='" + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                } else {
                    pId = "-1";
                }
            }
        } else {
            pId = "-1";
        }
        return pId;
    }

    // ********get Incident Param id *********
    public String getInciParamName(String paramType, String pId, String module) {
        String Pname = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType + "' AND P_ID='" + pId + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                Pname = c.getString( c.getColumnIndex( "P_NAME" ) );
                if (!Pname.equalsIgnoreCase( "null" ) && !Pname.isEmpty()) {
                    Pname = c.getString( c.getColumnIndex( "P_NAME" ) );
                } else {
                    Pname = "R";
                }
            }
        } else {
            Pname = "R";
        }
        return Pname;
    }

    // func for getting all alarm description on eq & at
    public ArrayList<String> getAllAlarmDesc(String pId, String euipId, String tktType,
                                             String tktTypeMadatory, TextView tv, String module,
                                             String userCategory,String userSubcategory) {
        ArrayList<String> desc_List = null;
        String id = null;
        desc_List = new ArrayList<String>();
        if(tktType.equalsIgnoreCase("23")){

        }else {
            desc_List.add("Select " + tv.getText().toString().trim());
        }
        Cursor c = null;
        if(userCategory.equalsIgnoreCase("2")){
            if (pId.equalsIgnoreCase( "R" ) && !euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE EQUIP_ID='"
                                + euipId + "' AND MODULE_ID='" + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (!pId.equalsIgnoreCase( "R" ) && euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE SEVERITY_ID='"
                                + pId + "' AND MODULE_ID='" + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (!pId.equalsIgnoreCase( "R" ) && !euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE SEVERITY_ID='"
                                + pId + "' AND EQUIP_ID='" + euipId
                                + "' AND MODULE_ID='" + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (pId.equalsIgnoreCase( "R" ) && euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB
                        .rawQuery(
                                "Select ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE MODULE_ID='" + module + "' AND OPERATOR_ID IN (" + userSubcategory + ") ORDER BY UPPER(ALARM_DESC) ASC",
                                null );
            }
        }else{
            if (pId.equalsIgnoreCase( "R" ) && !euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select DISTINCT(ALARM_ID),ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE EQUIP_ID='"
                                + euipId + "' AND MODULE_ID='" + module + "'ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (!pId.equalsIgnoreCase( "R" ) && euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select DISTINCT(ALARM_ID),ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE SEVERITY_ID='"
                                + pId + "' AND MODULE_ID='" + module + "' ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (!pId.equalsIgnoreCase( "R" ) && !euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB.rawQuery(
                        "Select DISTINCT(ALARM_ID),ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE SEVERITY_ID='"
                                + pId + "' AND EQUIP_ID='" + euipId
                                + "' AND MODULE_ID='" + module + "'ORDER BY UPPER(ALARM_DESC) ASC", null );
            } else if (pId.equalsIgnoreCase( "R" ) && euipId.equalsIgnoreCase( "R" )) {
                c = sqLiteDB
                        .rawQuery(
                                "Select DISTINCT(ALARM_ID),ALARM_DESC,ALARM_CATEGORY from ALARM_DESC WHERE MODULE_ID='" + module + "'ORDER BY UPPER(ALARM_DESC) ASC",
                                null );
            }
        }

        /*************************************************************************************************/
        if (c != null) {
            if (!tktType.equalsIgnoreCase( "R" ) &&
                    tktTypeMadatory.equalsIgnoreCase( "M" )) {
                while (c.moveToNext()) {
                    id = c.getString( c.getColumnIndex( "ALARM_CATEGORY" ) );
                    if (id != null) {
                        String tktTypeId[] = null;
                        if (id.contains( "," )) {
                            tktTypeId = id.split( "," );
                            if (tktTypeId != null) {
                                for (int i = 0; i < tktTypeId.length; i++) {
                                    if (tktTypeId[i].equalsIgnoreCase( tktType.toString() )) {
                                        desc_List.add( c.getString( c.getColumnIndex( "ALARM_DESC" ) ) );
                                    }
                                }
                            }
                        } else {
                            if (id.equalsIgnoreCase( tktType.toString() )) {
                                desc_List.add( c.getString( c.getColumnIndex( "ALARM_DESC" ) ) );
                            }
                        }
                    }
                }
            } else {
                while (c.moveToNext()) {
                    desc_List.add( c.getString( c.getColumnIndex( "ALARM_DESC" ) ) );
                }
            }
        }
        return desc_List;
    }


    public String getDescID(String pId, String euipId, String desc, String module) {
        String id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select ALARM_ID from ALARM_DESC WHERE SEVERITY_ID= '" + pId
                        + "' AND EQUIP_ID='" + euipId + "' AND ALARM_DESC='"
                        + desc + "' AND MODULE_ID='" + module + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "ALARM_ID" ) );
            }
        }
        return id;
    }

    public String getDescIDD(String pId, String desc, String module) {
        String id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select ALARM_ID from ALARM_DESC WHERE SEVERITY_ID= '" + pId
                        + "' AND ALARM_DESC='" + desc + "' AND MODULE_ID='" + module + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "ALARM_ID" ) );
            }
        }
        return id;
    }

    public String getDescID(String desc, String module) {
        String id = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select ALARM_ID from ALARM_DESC WHERE ALARM_DESC='" + desc + "' AND MODULE_ID='" + module + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "ALARM_ID" ) );
            }
        }
        return id;
    }

    public String getDesc(String pId, String module) {
        String desc = "";
        Cursor c = sqLiteDB.rawQuery(
                "Select ALARM_DESC from ALARM_DESC WHERE ALARM_ID='" + pId
                        + "' AND MODULE_ID='" + module + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                desc = c.getString( c.getColumnIndex( "ALARM_DESC" ) );
            }
        }
        return desc;
    }

    // ********get Incident Group Meta data*********
    public ArrayList<String> getInciGrp(String module) {
        ArrayList<String> inci_grp = null;
        inci_grp = new ArrayList<String>();
        inci_grp.add( "Select Assigned To" );
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(GRP_NAME) from INCIDENT_META_GRP WHERE MODULE_ID='"
                        + module + "' ORDER BY UPPER(GRP_NAME) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                inci_grp.add( c.getString( c.getColumnIndex( "GRP_NAME" ) ) );
            }
        }
        return inci_grp;
    }

   /* public ArrayList<String> getPrePostAM() {
        String type = "-1276";
        ArrayList<String> inci_grp = null;
        inci_grp = new ArrayList<String>();
        inci_grp.add( "Select");
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(VAL1) from workflow_meta_data where type ='"
                        + type + "' ORDER BY UPPER(VAL1) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                inci_grp.add( c.getString( c.getColumnIndex( "VAL1" ) ) );
            }
        }
        return inci_grp;
    }

    public String getDescIDD(String selectedVal) {
        String id = null;
        String type = "-1276";
        Cursor c = sqLiteDB.rawQuery(
                "Select ALARM_ID from workflow_meta_data WHERE type='" + type + "' AND VAL1='" + selectedVal + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( "ID" ) );
            }
        }
        return id;
    }*/

    // ********get Incident Group Meta data*********
    public ArrayList<String> getInciGrp(TextView tv, String module) {
        ArrayList<String> inci_grp = null;
        inci_grp = new ArrayList<String>();
        inci_grp.add( "Select Assigned To" );
        //inci_grp.add("Select "+tv.getText().toString().trim());
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(GRP_NAME) from INCIDENT_META_GRP WHERE MODULE_ID='"
                        + module + "' ORDER BY UPPER(GRP_NAME) ASC", null );
        if (c != null) {
            while (c.moveToNext()) {
                inci_grp.add( c.getString( c.getColumnIndex( "GRP_NAME" ) ) );
            }
        }
        return inci_grp;
    }

    // ********get Incident Group Meta data*********
    public ArrayList<String> getInciGrp1(String module,String grpId) {
        ArrayList<String> inci_grp = null;
        inci_grp = new ArrayList<String>();
        inci_grp.add( "Select Assigned To" );

        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(GRP_NAME) from INCIDENT_META_GRP WHERE MODULE_ID='"
                        + module + "' AND GRP_ID IN (" + grpId + ") ORDER BY UPPER(GRP_NAME) ASC", null );

        if (c != null) {
            while (c.moveToNext()) {
                inci_grp.add( c.getString( c.getColumnIndex( "GRP_NAME" ) ) );
            }
        }
        return inci_grp;
    }


    public Cursor getGrpUser(String id, String module) {
        Cursor c = sqLiteDB.rawQuery( "Select * from USER_CONTACT WHERE GRP_ID='"
                + id + "' AND MODULE_ID='" + module + "' ORDER BY UPPER(U_DETAIL) ASC", null );
        return c;
    }


    public ArrayList<String> getFiller(String desId, String module) {
        ArrayList<String> filler = new ArrayList<String>();
        filler.add( "Select Filler" );
        Cursor c = sqLiteDB.rawQuery( "Select DISTINCT(U_ID),U_DETAIL,U_DESI from USER_CONTACT WHERE  MODULE_ID='" + module + "' ORDER BY UPPER(U_DETAIL) ASC", null );
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String user = c.getString( c.getColumnIndex( "U_DETAIL" ) );
                String desi = c.getString( c.getColumnIndex( "U_DESI" ) );

                if (user != null && user.length() > 0 && desi != null && desi.length() > 0) {
                    String[] arr = null;
                    String[] desiId = null;
                    arr = user.split( "-" );
                    desiId = desi.split( "," );

                    if (arr != null && arr.length > 0 && desiId != null && desiId.length > 0) {
                        for (int i = 0; i < desiId.length; i++) {
                            if (desiId[i].equalsIgnoreCase( desId )) {
                                filler.add( arr[0] );
                            }
                        }
                    }
                }
            }
        }
        return filler;
    }

    public String getGroupId(String name, String module) {
        String equipmentId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(GRP_ID) from INCIDENT_META_GRP WHERE GRP_NAME='"
                        + name + "' AND MODULE_ID='" + module + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                equipmentId = c.getString( c.getColumnIndex( "GRP_ID" ) );
                if (!equipmentId.equalsIgnoreCase( "null" )
                        && !equipmentId.isEmpty()) {
                    equipmentId = c.getString( c.getColumnIndex( "GRP_ID" ) );
                } else {
                    equipmentId = "0";
                }
            }
        } else {
            equipmentId = "0";
        }
        return equipmentId;
    }

    // ********get Incident operator(ID) Meta data*********
    public ArrayList<String> getOperatorId(String module) {
        ArrayList<String> inci_operator_id = null;
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select DISTINCT(OPCO_ID) from INCIDENT_META_OPERATOR WHERE MODULE_ID='"
                                + module + "' ORDER BY UPPER(OPCO_NAME) ASC",
                        null );
        if (c != null) {
            inci_operator_id = new ArrayList<String>();
            while (c.moveToNext()) {
                inci_operator_id.add( c.getString( c.getColumnIndex( "OPCO_ID" ) ) );
            }
        }
        return inci_operator_id;
    }

    // ********get Incident operator(Name) Meta data*********
    public ArrayList<String> getOperatorName(String module) {
        ArrayList<String> inci_operator_name = null;
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select DISTINCT(OPCO_NAME) from INCIDENT_META_OPERATOR WHERE MODULE_ID='"
                                + module + "' ORDER BY UPPER(OPCO_NAME) ASC",
                        null );
        if (c != null) {
            inci_operator_name = new ArrayList<String>();
            while (c.moveToNext()) {
                inci_operator_name.add( c.getString( c
                        .getColumnIndex( "OPCO_NAME" ) ) );
            }
        }
        return inci_operator_name;
    }

    // *****get save time rca cate id and rca sub cate*****************
    public String getRcaId(String str, String str1, String module) {
        String rcaid = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(RCA_ID) from INCIDENT_META_RCA WHERE RCA_NAME='"
                        + str + "' AND PARENT_ID='" + str1 + "' AND MODULE_ID='" + module + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                rcaid = c.getString( c.getColumnIndex( "RCA_ID" ) );
            }
        }
        return rcaid;
    }

    // ********get Incident RCA cat and sub cate (id) Meta data*********
    public ArrayList<String> getRcaName(String str, int a, String module,int userCategory,
                                        String userSubCategory) {
        ArrayList<String> inci_rca_name = new ArrayList<String>();
        Cursor c;
        if (a == 1) {
            inci_rca_name.add( "Select RCA Category" );
        } else if (a == 2) {
            inci_rca_name.add( "Select RCA Sub Category" );
        }

        if(userCategory==2){
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(RCA_NAME) from INCIDENT_META_RCA WHERE PARENT_ID='"
                            + str + "' AND MODULE_ID='" + module + "' AND USER_CATEGORY ='" + userCategory + "' AND USER_SUB_CATEGORY ='" + userSubCategory + "' ORDER BY UPPER(RCA_NAME) ASC",
                    null );
        }else{
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(RCA_NAME) from INCIDENT_META_RCA WHERE PARENT_ID='"
                            + str + "' AND MODULE_ID='" + module + "'ORDER BY UPPER(RCA_NAME) ASC",
                    null );
        }

        if (c != null) {
            while (c.moveToNext()) {
                inci_rca_name.add( c.getString( c.getColumnIndex( "RCA_NAME" ) ) );
            }
        }
        return inci_rca_name;
    }


    public void initializeModuleList(Resources resources) {

        Map<String, HomeModule> moduleMap = Utils.initializeModuleMap( resources );

        Cursor c = sqLiteDB.rawQuery( "Select DISTINCT MAIN_MENU, MODULE_URL,MODULE_CAPTION from FORM_RIGHTS WHERE MAIN_MENU_SEQ!=9 ORDER BY MAIN_MENU_SEQ",
                null );


        if (c != null) {
            while (c.moveToNext()) {
                if (moduleMap.containsKey( c.getString( c.getColumnIndex( "MAIN_MENU" ) ) )) {
                    moduleMap.get( c.getString( c.getColumnIndex( "MAIN_MENU" ) ) ).setVisible( true );
                    moduleMap.get( c.getString( c.getColumnIndex( "MAIN_MENU" ) ) ).setBaseurl( c.getString( c.getColumnIndex( "MODULE_URL" ) ) );
                    moduleMap.get( c.getString( c.getColumnIndex( "MAIN_MENU" ) ) ).setCaption( c.getString( c.getColumnIndex( "MODULE_CAPTION" ) ) );
                    AppConstants.moduleList.add( moduleMap.get( c.getString( c.getColumnIndex( "MAIN_MENU" ) ) ) );
                }
            }
        }
    }


    public String getSubMenuRight(String sun_menu_name, String menu_name) {
        String sub_menu_right = null;
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select RIGHTS from FORM_RIGHTS WHERE SUB_MENU='"
                                + sun_menu_name + "' AND MAIN_MENU='"
                                + menu_name + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                sub_menu_right = c.getString( c.getColumnIndex( "RIGHTS" ) );
                if (!sub_menu_right.equalsIgnoreCase( "null" )
                        && !sub_menu_right.isEmpty()) {
                    sub_menu_right = c.getString( c.getColumnIndex( "RIGHTS" ) );
                } else {
                    sub_menu_right = "R";
                }
            }
        } else {
            sub_menu_right = "R";
        }
        return sub_menu_right;
    }

    // ********get Asset Param Meta data*********
    public ArrayList<String> getAssetParamName(int paramType) {
        ArrayList<String> asset_param = null;
        asset_param = new ArrayList<String>();
        ;
        if (paramType == 68) {
            asset_param.add( "Select Power Vendor" );
        } else if (paramType == 5) {
            asset_param.add( "Select Site Type" );
        } else if (paramType == 67) {
            asset_param.add( "Select Site Area Type" );
        } else if (paramType == 3) {
            asset_param.add( "Select Owner" );
        } else if (paramType == 2) {
            asset_param.add( "Select Site Location" );
        } else if (paramType == 71) {
            asset_param.add( "Select Site Class" );
        } else if (paramType == 69) {
            asset_param.add( "Select Power Model" );
        }
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_NAME) from ASSET_META_PARAM WHERE P_TYPE='"
                        + paramType + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                asset_param.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return asset_param;
    }

    // ********get Asset Param Meta data*********
    public String getAssetParamId(String paramType, String paramname) {
        String asset_param = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_ID) from ASSET_META_PARAM WHERE P_NAME='"
                        + paramname + "' AND P_TYPE='" + paramType + "'", null );
        if (c != null) {
            while (c.moveToNext()) {
                asset_param = c.getString( c.getColumnIndex( "P_ID" ) );
            }
        }
        return asset_param;
    }


    public int getHSChecklist(String descID, String module) {
        int a = 0;
        Cursor c = sqLiteDB.rawQuery(
                "Select * from CHECKLIST_PM_FORM WHERE TypeId ='"
                        + descID + "' AND MODULE_ID ='" + module
                        + "' ORDER BY groupSeq ASC", null );
        if (c != null) {
            a = c.getCount();
        }
        return a;

    }

    // return pm checklist form when pm review
    //no need to change
    public Cursor getReviewerChecklist(String activityTypeId, int flag, String fieldList) {
        Cursor cursor = null;
        if (flag == 0) {

            String sql = "Select DISTINCT fieldId,fieldType,fieldName,dataType,length,groupSeq,groupName , fieldSeq , value , mandatory ,TypeId ,pActivityId ,pFlag ,rMandatory ,status ,viRemark ,rStatus ,rRemark ,preImgConfig ,postImgConfig ,preImgTag ,postImgTag from CHECKLIST_REVIEWER_FORM WHERE TypeId ='" + activityTypeId + "' AND (pFlag = 'P') ORDER BY fieldSeq ASC";
            cursor = sqLiteDB.rawQuery( sql, null );
        } else if (flag == 2) {

            String sql = "Select DISTINCT fieldId,fieldType,fieldName,dataType,length,groupSeq,groupName , fieldSeq , value , mandatory ,TypeId ,pActivityId ,pFlag ,rMandatory ,status ,viRemark ,rStatus ,rRemark ,preImgConfig ,postImgConfig ,preImgTag ,postImgTag from CHECKLIST_REVIEWER_FORM WHERE TypeId ='" + activityTypeId + "' AND fieldId IN (" + fieldList + ") ORDER BY fieldSeq ASC";

            cursor = sqLiteDB.rawQuery( sql, null );
        }
        return cursor;
    }

    // return pm checklist form when pm done
    public Cursor getPMChecklist(String activityTypeId, int flag,
                                 String fieldList, String module,String equipmentId) {
        Cursor cursor = null;
        // get equipment by PM checklist
        if(equipmentId !=null && equipmentId.length()>0) {
            if (flag == 0) {
                String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND equipmentId ='" + equipmentId + "' AND (pFlag = 'P') AND MODULE_ID ='" + module
                        + "' ORDER BY fieldSeq ASC";
                cursor = sqLiteDB.rawQuery(sql, null);
            } else if (flag == 1) {
                cursor = sqLiteDB.rawQuery(
                        "Select * from TEMP_CHECKLIST WHERE TypeId ='" + activityTypeId + "' AND equipmentId ='" + equipmentId + "' AND MODULE_ID ='" + module
                                + "'ORDER BY fieldSeq ASC", null);
            } else if (flag == 2) {

                String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND equipmentId ='" + equipmentId + "' AND fieldId IN (" + fieldList + ") AND MODULE_ID ='" + module
                        + "' ORDER BY fieldSeq ASC";

                System.err.println(sql);

                cursor = sqLiteDB.rawQuery(sql, null);
            }
        }else{
            // get all checklist
            if (flag == 0) {
                String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND (pFlag = 'P') AND MODULE_ID ='" + module
                        + "' ORDER BY fieldSeq ASC";
                cursor = sqLiteDB.rawQuery( sql, null );
            } else if (flag == 1) {
                cursor = sqLiteDB.rawQuery(
                        "Select * from TEMP_CHECKLIST WHERE TypeId ='" + activityTypeId + "' AND MODULE_ID ='" + module
                                + "'ORDER BY fieldSeq ASC", null );
            } else if (flag == 2) {

                String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND fieldId IN (" + fieldList + ") AND MODULE_ID ='" + module
                        + "' ORDER BY fieldSeq ASC";

                System.err.println( sql );

                cursor = sqLiteDB.rawQuery( sql, null );
            }

        }
        return cursor;
    }



    // return pm checklist form when pm done
    public Cursor getPMChecklist1(String activityTypeId, int flag,
                                  String fieldList, String module) {
        Cursor cursor = null;
        if (flag == 0) {
            String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND (pFlag = 'P') AND MODULE_ID ='" + module
                    + "' ORDER BY fieldSeq ASC";
            cursor = sqLiteDB.rawQuery( sql, null );
        } else if (flag == 1) {
            cursor = sqLiteDB.rawQuery(
                    "Select * from TEMP_CHECKLIST WHERE TypeId ='" + activityTypeId + "' AND MODULE_ID ='" + module
                            + "'ORDER BY fieldSeq ASC", null );
        } else if (flag == 2) {

            String sql = "Select * from CHECKLIST_PM_FORM WHERE TypeId ='" + activityTypeId + "' AND fieldId IN (" + fieldList + ") AND MODULE_ID ='" + module
                    + "' ORDER BY fieldSeq ASC";

            System.err.println( sql );

            cursor = sqLiteDB.rawQuery( sql, null );
        }
        return cursor;
    }


    public ArrayList<String> getAllNotification(String userId) {
        ArrayList<String> notification_list = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select notification from NOTIFICATION WHERE userId='" + userId
                        + "'ORDER BY ID DESC", null );
        if (c != null) {
            notification_list = new ArrayList<String>();
            while (c.moveToNext()) {
                notification_list.add( c.getString( c
                        .getColumnIndex( "notification" ) ) );
            }
        }
        return notification_list;
    }

    public ArrayList<String> getNotificationFlag(String userId) {
        ArrayList<String> notification_list = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select readFlag from NOTIFICATION WHERE userId='" + userId
                        + "'ORDER BY ID DESC", null );
        if (c != null) {
            notification_list = new ArrayList<String>();
            while (c.moveToNext()) {
                notification_list
                        .add( c.getString( c.getColumnIndex( "readFlag" ) ) );
            }
        }
        return notification_list;
    }

    public ArrayList<String> getNotificationCount(String userId, String flag) {
        ArrayList<String> notification_list = null;
        Cursor c = sqLiteDB
                .rawQuery(
                        "Select notification from NOTIFICATION WHERE userId='"
                                + userId + "'AND readFlag ='" + flag
                                + "'ORDER BY ID DESC", null );
        if (c != null) {
            notification_list = new ArrayList<String>();
            while (c.moveToNext()) {
                notification_list.add( c.getString( c
                        .getColumnIndex( "notification" ) ) );
            }
        }
        return notification_list;
    }

    public String getAddTTField(String name, String moduleId) {
        String ttField = null;
        Cursor c = sqLiteDB.rawQuery( "Select DISTINCT(AddVisable) from CHECKLIST_TT_FORM WHERE Caption='"
                + name + "' AND ModuleId='"
                + moduleId + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                ttField = c.getString( c.getColumnIndex( "AddVisable" ) );
                if (!ttField.equalsIgnoreCase( "null" ) && !ttField.isEmpty()) {
                    ttField = c.getString( c.getColumnIndex( "AddVisable" ) );
                } else {
                    ttField = "R";
                }
            }
        } else {
            ttField = "R";
        }
        return ttField;
    }

    public String getUpdateTTField(String name, String moduleId) {
        String ttField = null;
        Cursor c = sqLiteDB.rawQuery( "Select DISTINCT(UpdateVisable) from CHECKLIST_TT_FORM WHERE Caption='"
                + name + "' AND ModuleId='"
                + moduleId + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                ttField = c.getString( c.getColumnIndex( "UpdateVisable" ) );
                if (!ttField.equalsIgnoreCase( "null" ) && !ttField.isEmpty()) {
                    ttField = c.getString( c.getColumnIndex( "UpdateVisable" ) );
                } else {
                    ttField = "R";
                }
            }
        } else {
            ttField = "R";
        }
        return ttField;
    }

    public String getDetailsTTField(String name, String moduleId) {
        String ttField = null;
        //Cursor c = sqLiteDB.rawQuery(
        //		"Select DISTINCT(DetailsVisable) from CHECKLIST_TT_FORM WHERE Caption='"
        //				+ name + "'", null);
        Cursor c = sqLiteDB.rawQuery( "Select DISTINCT(DetailsVisable) from CHECKLIST_TT_FORM WHERE Caption='"
                + name + "' AND ModuleId='"
                + moduleId + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                ttField = c.getString( c.getColumnIndex( "DetailsVisable" ) );
                if (!ttField.equalsIgnoreCase( "null" ) && !ttField.isEmpty()) {
                    ttField = c.getString( c.getColumnIndex( "DetailsVisable" ) );
                } else {
                    ttField = "R";
                }
            }
        } else {
            ttField = "R";
        }
        return ttField;
    }

    public ArrayList<String> getLocalTxnData(int a, String userId) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor c = null;
        if (a == 1) {
            c = sqLiteDB.rawQuery(
                    "Select FLAG from TXN_LOCAL_DATA WHERE USER_ID='" + userId
                            + "'", null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "FLAG" ) ) );
                }
            }
        } else if (a == 2) {
            c = sqLiteDB.rawQuery(
                    "Select  DISTINCT(ACTIVITY_DATA),PRE_IMG,POST_IMG from TXN_LOCAL_DATA WHERE USER_ID='"
                            + userId + "'", null );
            if (c != null) {
                int temp = c.getCount();
                System.out.println( c.getCount() );
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "ACTIVITY_DATA" ) ) );
                }
            }
        }
        return list;
    }

    public Cursor getLocalTranData(String userId) {
        Cursor c = null;
        //c = sqLiteDB.rawQuery(
        //		"Select TXN_ID,FLAG,ACTIVITY_DATA,ALL_IMGS,PRE_IMG,POST_IMG from TXN_LOCAL_DATA WHERE USER_ID='"
        //				+ userId + "'", null);
        c = sqLiteDB.rawQuery(
                "Select * from TXN_LOCAL_DATA WHERE USER_ID='"
                        + userId + "'", null );
        return c;
    }

    public ArrayList<String> getRejectLocalTxnData(int a, String userId) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor c = null;
        if (a == 1) {
            c = sqLiteDB.rawQuery(
                    "Select FLAG from REJECT_TXN_LOCAL_DATA WHERE USER_ID='"
                            + userId + "'", null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "FLAG" ) ) );
                }
            }
        } else if (a == 2) {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(ACTIVITY_DATA) from REJECT_TXN_LOCAL_DATA WHERE USER_ID='"
                            + userId + "'", null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "ACTIVITY_DATA" ) ) );
                }
            }
        } else if (a == 3) {
            c = sqLiteDB.rawQuery(
                    "Select ERROR_MSG from REJECT_TXN_LOCAL_DATA WHERE USER_ID='"
                            + userId + "'", null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "ERROR_MSG" ) ) );
                }
            }
        }
        return list;
    }

    public ArrayList<String> getTxnDate(int a) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor c = null;
        if (a == 1) {
            c = sqLiteDB.rawQuery( "Select DATE from TXN_LOCAL_DATA", null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "DATE" ) ) );
                }
            }
        } else if (a == 2) {
            c = sqLiteDB.rawQuery( "Select DATE from REJECT_TXN_LOCAL_DATA",
                    null );
            if (c != null) {
                while (c.moveToNext()) {
                    list.add( c.getString( c.getColumnIndex( "DATE" ) ) );
                }
            }
        }
        return list;
    }

    public Typeface typeface(String languageCode) {
        Typeface mynFont = null;
        if (languageCode.equalsIgnoreCase( "my" )) {
            mynFont = Typeface.createFromAsset( context.getAssets(),
                    "Myanmar3.ttf" );
        } else {
            if (context.getApplicationContext().getPackageName().
                    equalsIgnoreCase( "infozech.tawal" ) ||
                    context.getApplicationContext().getPackageName().
                            equalsIgnoreCase( "tawal.com.sa" )) {
                mynFont = Typeface.createFromAsset( context.getAssets(),
                        "TeshrinARLT-Regular.ttf" );
            } else {
                mynFont = Typeface.DEFAULT;
            }
        }
        return mynFont;
    }

    public String getMessage(String id) {
        String message = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select Message from MULTI_LANGUAGE WHERE MessageId='" + id
                        + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                message = c.getString( c.getColumnIndex( "Message" ) );
                if (!message.equalsIgnoreCase( "null" ) && !message.isEmpty()) {
                    message = c.getString( c.getColumnIndex( "Message" ) );
                } else {
                    message = "";
                }
            }
        } else {
            message = "";
        }
        c.close();

        if (message.length() == 0 && DefaultLevel.msg().containsKey( id )) {
            message = DefaultLevel.msg().get( id );
        }

        return message;
    }

    public int isLevel() {
        int a = 0;
        Cursor c = sqLiteDB.rawQuery(
                "Select * from MULTI_LANGUAGE", null );
        if (c != null) {
            a = c.getCount();
        }
        return a;
    }

    public int getPendingTxnCount(String txnId) {
        int txnCunt = 0;
        String sql = "SELECT TXN_ID FROM TXN_LOCAL_DATA";

        if (txnId != null && txnId.length() > 0) {
            sql = sql + " WHERE TXN_ID = '" + txnId + "'";
        }

        Cursor c = sqLiteDB.rawQuery( sql, null );
        txnCunt = c.getCount();
        c.close();
        return txnCunt;
    }


    public Cursor getSpareParts(int flag, String categoryId) {
        Cursor cursor = null;
        if (flag == 0) {
            cursor = sqLiteDB.rawQuery( "Select * from SPARE_PARTS WHERE CATEGORY_ID IN (" + categoryId + ") ORDER BY UPPER(SpareName) ASC", null );
        } else if (flag == 1) {
            cursor = sqLiteDB.rawQuery( "Select * from SPARE_PARTS WHERE Status=1 ORDER BY UPPER(SpareName) ASC", null );
        }
        return cursor;
    }

    public void updateSparePartStatus() {
        ContentValues values = new ContentValues();
        values.put( "Status", "0" );
        values.put( "Qty", "" );
        values.put( "OrgQty", "" );
        values.put( "SerialNumber", "" );
        values.put( "SELECTION_CATEGORY", "Select Category" );
        sqLiteDB.update( "SPARE_PARTS", values, null, null );
    }

    public void updateSpareQty(List<BeanSpare> data) {
        for (int i = 0; i < data.size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Status", "1" );
            values.put( "Qty", data.get( i ).getQuantity() );
            values.put( "OrgQty", data.get( i ).getQuantity() );
            if (data.get( i ).getSerialno() != null
                    && !data.get( i ).getSerialno().isEmpty()) {
                values.put( "SerialNumber", data.get( i ).getSerialno() );
            } else {
                values.put( "SerialNumber", "" );
            }
            sqLiteDB.update( "SPARE_PARTS", values, "ID =?", new String[]{data.get( i ).getSpareId()} );
        }
    }

    public void addSpareQty(String id, String qty, String serialNo, String status, String Selectedcategory) {
        ContentValues values = new ContentValues();
        values.put( "Status", status );
        values.put( "Qty", qty );
        values.put( "SerialNumber", serialNo );
        values.put( "SELECTION_CATEGORY", Selectedcategory );
        sqLiteDB.update( "SPARE_PARTS", values, "ID =?", new String[]{id} );

    }


    public double getLongi(String sId) {
        String longi = null;
        double SiteLat = 0;
        Cursor c = sqLiteDB.rawQuery(
                "Select longi from USER_ASSO_SITES WHERE SiteID='" + sId + "'",
                null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                longi = c.getString( c.getColumnIndex( "longi" ) );
                if (!longi.equalsIgnoreCase( "null" ) && !longi.isEmpty()) {
                    longi = c.getString( c.getColumnIndex( "longi" ) );
                    SiteLat = Double.parseDouble( longi );
                } else {
                    SiteLat = 0;
                }
            }
        } else {
            SiteLat = 0;
        }
        return SiteLat;
    }


    // 0.6 Start
    public void insertScheduleList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID());
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID());
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName());
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME());
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS());
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE());
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid());
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE());
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc());
            values.put( "Val_Data", schedulelist.getSite_list().get( i ).getVal());
            values.put( "Reading_Data", schedulelist.getSite_list().get( i ).getLtstRdng());
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid());
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("pmFlag",schedulelist.getSite_list().get(i).getPmFlag());
            values.put("pmNote",schedulelist.getSite_list().get(i).getPmNote());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "SCHEDULE_LIST", null, values );
        }
    }

    public void insertResubmitList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID() );
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID() );
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName() );
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME() );
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS() );
            //values.put( "Status", "RS");
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE() );
            values.put( "Done_Date", schedulelist.getSite_list().get( i ).getdDate() );
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid() );
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE() );
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc() );
            values.put( "Val_Data", schedulelist.getSite_list().get( i ).getVal() );
            values.put( "Reading_Data", schedulelist.getSite_list().get( i ).getLtstRdng() );
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid() );
            values.put( "rvDate", schedulelist.getSite_list().get( i ).getRvDate() );
            values.put( "rejRmks", schedulelist.getSite_list().get( i ).getRejRmks() );
            values.put( "rCat", schedulelist.getSite_list().get( i ).getrCat() );
            values.put( "rCat", schedulelist.getSite_list().get( i ).getImgUploadflag() );
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "RE_SUBMIT_PM_LIST", null, values );
        }
    }

    // 0.6 Start
    public void insertDoneList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID() );
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID() );
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName() );
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME() );
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS() );
            values.put( "Done_Date", schedulelist.getSite_list().get( i ).getdDate() );
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE() );
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid() );
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE() );
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc() );
            values.put( "imgUploadFlag", schedulelist.getSite_list().get( i ).getImgUploadflag() );
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid() );
            values.put( "creplandate", schedulelist.getSite_list().get( i ).getCreplandate() );
            values.put( "funcheck", schedulelist.getSite_list().get( i ).getFuncheck() );
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "DONE_LIST", null, values );
        }
    }

    public void insertRejectList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID());
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID() );
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName() );
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME() );
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS() );
            values.put( "Reject_Date", schedulelist.getSite_list().get( i ).getRvDate() );
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid() );
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE() );
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc() );
            values.put( "imgUploadFlag", schedulelist.getSite_list().get( i ).getImgUploadflag() );
            values.put( "Rej_Cat", schedulelist.getSite_list().get( i ).getrCat() );
            values.put( "Rej_Rmks", schedulelist.getSite_list().get( i ).getRejRmks() );
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE() );
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid() );
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "REJECT_LIST", null, values );
        }
    }

    // 0.6 Start
    public void insertMissedList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID() );
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID() );
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName() );
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME() );
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS() );
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE() );
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid() );
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE() );
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc() );
            values.put( "Val_Data", schedulelist.getSite_list().get( i ).getVal() );
            values.put( "Reading_Data", schedulelist.getSite_list().get( i ).getLtstRdng() );
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid() );
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "MISSED_LIST", null, values );
        }
    }

    // 0.6 Start
    public void insertVerifyList(BeanSiteList schedulelist) {
        for (int i = 0; i < schedulelist.getSite_list().size(); i++) {
            ContentValues values = new ContentValues();
            values.put( "Txn_ID", schedulelist.getSite_list().get( i ).getTXN_ID() );
            values.put( "TKT_ID", schedulelist.getSite_list().get( i ).getPmTktId());
            values.put( "Site_ID", schedulelist.getSite_list().get( i ).getSITE_ID() );
            values.put( "Site_Name", schedulelist.getSite_list().get( i ).getSidName() );
            values.put( "Param_Name", schedulelist.getSite_list().get( i ).getPARAM_NAME() );
            values.put( "Param_ID", schedulelist.getSite_list().get( i ).getPARAM_ID() );
            values.put( "Status", schedulelist.getSite_list().get( i ).getACTIVITY_STATUS() );
            values.put( "Schedule_Date", schedulelist.getSite_list().get( i ).getSCHEDULE_DATE() );
            values.put( "Verify_Date", schedulelist.getSite_list().get( i ).getRvDate() );
            values.put( "Ets_ID", schedulelist.getSite_list().get( i ).getEtsSid() );
            values.put( "DG_Type", schedulelist.getSite_list().get( i ).getDG_TYPE() );
            values.put( "DG_Desc", schedulelist.getSite_list().get( i ).getDgDesc() );
            values.put( "imgUploadFlag", schedulelist.getSite_list().get( i ).getImgUploadflag() );
            values.put( "Operator_Site_Id", schedulelist.getSite_list().get( i ).getOsid() );
            values.put( "genStatus", schedulelist.getSite_list().get( i ).getGenStatus());
            values.put( "pullDate", schedulelist.getSite_list().get( i ).getPullDate());
            values.put( "overHaulDate", schedulelist.getSite_list().get( i ).getOverHaulDate());
            values.put( "PopupFlag", schedulelist.getSite_list().get( i ).getPopupFlag());
            values.put("assetFlag",schedulelist.getSite_list().get(i).getAssetFlag());
            sqLiteDB.insert( "VERIFY_LIST", null, values );
        }
    }

    public int scheduleSiteCount() {
        int a = 0;
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from SCHEDULE_LIST",
                null );
        if (c != null) {
            a = c.getCount();
        }
        return a;
    }

    public int reSubmitSiteCount() {
        int a = 0;
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from RE_SUBMIT_PM_LIST",
                null );
        if (c != null) {
            a = c.getCount();
        }
        return a;
    }

    public BeanSiteList ScheduleSite(String keyword, String Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == "I") {
            c = sqLiteDB.rawQuery(
                    "Select * from SCHEDULE_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );

        } else if (Flag == "D") {
            c = sqLiteDB.rawQuery(
                    "Select * from SCHEDULE_LIST WHERE Schedule_Date LIKE '%"
                            + keyword + "%'", null );
        } else if (Flag == "A") {
            c = sqLiteDB.rawQuery(
                    "Select * from SCHEDULE_LIST", null );
        } else if(Flag == "T") {
            c = sqLiteDB.rawQuery(
                    "Select * from SCHEDULE_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        }else if(Flag == "aID")  {
            c = sqLiteDB.rawQuery(
                    "Select * from SCHEDULE_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        }

        if (c != null) {

            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex( "Txn_ID" ) ) );
                siteView.setPmTktId( c.getString( c.getColumnIndex("TKT_ID" )));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );
                siteView.setVal( c.getString( c.getColumnIndex( "Val_Data" ) ) );
                siteView.setLtstRdng( c.getString( c.getColumnIndex( "Reading_Data" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag(c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setPmFlag(c.getString(c.getColumnIndex("pmFlag")));
                siteView.setPmNote(c.getString(c.getColumnIndex("pmNote")));
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }
            siteList.setSite_list( arrayList );


        }
        return siteList;
    }

    public BeanSiteList reSubmitSite(String keyword, int Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == 1) {
            c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );

           /* c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST WHERE Site_ID LIKE '%"
                            + s + "%' OR Operator_Site_Id LIKE '%" + s + "%' ESCAPE '\\'", null );*/
        } else if (Flag == 2) {
            c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        } else if (Flag == 3) {
            c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST WHERE Schedule_Date LIKE '%"
                            + keyword + "%'", null );
        }else if(Flag == 4) {
            c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        }else {
            c = sqLiteDB.rawQuery(
                    "Select * from RE_SUBMIT_PM_LIST", null );
        }

        if (c != null) {

            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex( "Txn_ID" ) ) );
                siteView.setPmTktId(c.getString( c.getColumnIndex("TKT_ID")));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                siteView.setdDate( c.getString( c.getColumnIndex( "Done_Date" ) ) );
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );
                siteView.setVal( c.getString( c.getColumnIndex( "Val_Data" ) ) );
                siteView.setLtstRdng( c.getString( c.getColumnIndex( "Reading_Data" ) ) );
                siteView.setImgUploadflag( c.getString( c.getColumnIndex( "imgUploadFlag" ) ) );
                siteView.setRvDate( c.getString( c.getColumnIndex( "rvDate" ) ) );
                siteView.setrCat( c.getString( c.getColumnIndex( "rCat" ) ) );
                siteView.setRejRmks( c.getString( c.getColumnIndex( "rejRmks" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag( c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }
            siteList.setSite_list( arrayList );

        }
        return siteList;
    }

    public BeanSiteList doneSite(String keyword, String Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == "I") {
            c = sqLiteDB.rawQuery(
                    "Select * from DONE_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );
        }else if (Flag == "D") {
            c = sqLiteDB.rawQuery(
                    "Select * from DONE_LIST WHERE Done_Date LIKE '%"
                            + keyword + "%'", null );
        }else if(Flag == "T") {
            c = sqLiteDB.rawQuery(
                    "Select * from DONE_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        }else if(Flag == "aID") {
            c = sqLiteDB.rawQuery(

                    "Select * from DONE_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        }

        if (c != null) {
            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex("Txn_ID")));
                siteView.setPmTktId(c.getString( c.getColumnIndex("TKT_ID")));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                siteView.setdDate( c.getString( c.getColumnIndex( "Done_Date" ) ) );
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setImgUploadflag( c.getString( c.getColumnIndex( "imgUploadFlag" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );
                siteView.setCreplandate( c.getString( c.getColumnIndex( "creplandate" ) ) );
                siteView.setFuncheck( c.getString( c.getColumnIndex( "funcheck" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag( c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }
            siteList.setSite_list( arrayList );
        }
        return siteList;
    }

    public BeanSiteList rejectSite(String keyword, String Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == "I") {
            c = sqLiteDB.rawQuery(
                    "Select * from REJECT_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );
        } else if (Flag == "D") {
            c = sqLiteDB.rawQuery(
                    "Select * from REJECT_LIST WHERE Reject_Date LIKE '%"
                            + keyword + "%'", null );
        } else if(Flag == "T") {
            c = sqLiteDB.rawQuery(
                    "Select * from REJECT_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        } else if(Flag == "aID") {
            c = sqLiteDB.rawQuery(
                    "Select * from REJECT_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        }

        if (c != null) {
            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex( "Txn_ID" ) ) );
                siteView.setPmTktId(c.getString( c.getColumnIndex("TKT_ID")));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                //siteView.setdDate(c.getString(c.getColumnIndex("Done_Date")));
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setImgUploadflag( c.getString( c.getColumnIndex( "imgUploadFlag" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );
                siteView.setRvDate( c.getString( c.getColumnIndex( "Reject_Date" ) ) );
                siteView.setrCat( c.getString( c.getColumnIndex( "Rej_Cat" ) ) );
                siteView.setRejRmks( c.getString( c.getColumnIndex( "Rej_Rmks" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag( c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }
            siteList.setSite_list( arrayList );
        }
        return siteList;
    }

    public int missSiteCount() {
        int a = 0;
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from MISSED_LIST",
                null );
        if (c != null) {
            a = c.getCount();
        }
        return a;
    }

    public BeanSiteList missedSite(String keyword, String Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == "I") {
            c = sqLiteDB.rawQuery(
                    "Select * from MISSED_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );
        } else if (Flag == "D") {
            c = sqLiteDB.rawQuery(
                    "Select * from MISSED_LIST WHERE Schedule_Date LIKE '%"
                            + keyword + "%'", null );
        } else if (Flag == "A") {
            c = sqLiteDB.rawQuery( "Select * from MISSED_LIST", null );
        }else if(Flag == "T") {
            c = sqLiteDB.rawQuery(
                    "Select * from MISSED_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        }else if(Flag == "aID") {
            c = sqLiteDB.rawQuery(
                    "Select * from MISSED_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        }

        if (c != null) {

            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex("Txn_ID" )));
                siteView.setPmTktId( c.getString( c.getColumnIndex("TKT_ID")));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setVal( c.getString( c.getColumnIndex( "Val_Data" ) ) );
                siteView.setLtstRdng( c.getString( c.getColumnIndex( "Reading_Data" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag( c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }

            siteList.setSite_list( arrayList );

        }
        return siteList;
    }

    public BeanSiteList verifySite(String keyword, String Flag) {
        BeansSiteView siteView;
        List<BeansSiteView> arrayList = new ArrayList<BeansSiteView>();
        BeanSiteList siteList = null;
        Cursor c = null;
        String s = keyword.replace( "_", "\\_" );
        String s1 = keyword.replaceAll("\\D", "");
        if (Flag == "I") {
            c = sqLiteDB.rawQuery(
                    "Select * from VERIFY_LIST WHERE Site_ID LIKE '%"
                            + keyword + "%' OR Operator_Site_Id LIKE '%" + keyword + "%' ESCAPE '\\'", null );
        } else if (Flag == "D") {
            c = sqLiteDB.rawQuery(
                    "Select * from VERIFY_LIST WHERE Verify_Date LIKE '%"
                            + keyword + "%'", null );
        }else if(Flag == "T") {
            c = sqLiteDB.rawQuery(
                    "Select * from VERIFY_LIST WHERE Txn_ID LIKE '%"
                            + s1 + "%'", null );
        }
        else if(Flag == "aID") {
            c = sqLiteDB.rawQuery(
                    "Select * from VERIFY_LIST WHERE Param_ID In (" + keyword + ")",
                    null );
        }

        if (c != null) {
            arrayList.clear();
            siteList = new BeanSiteList();
            while (c.moveToNext()) {
                siteView = new BeansSiteView();
                siteView.setTXN_ID( c.getString( c.getColumnIndex( "Txn_ID" ) ) );
                siteView.setPmTktId(c.getString( c.getColumnIndex("TKT_ID")));
                siteView.setSITE_ID( c.getString( c.getColumnIndex( "Site_ID" ) ) );
                siteView.setSidName( c.getString( c.getColumnIndex( "Site_Name" ) ) );
                siteView.setPARAM_NAME( c.getString( c.getColumnIndex( "Param_Name" ) ) );
                siteView.setPARAM_ID( c.getString( c.getColumnIndex( "Param_ID" ) ) );
                siteView.setSCHEDULE_DATE( c.getString( c.getColumnIndex( "Schedule_Date" ) ) );
                siteView.setRvDate( c.getString( c.getColumnIndex( "Verify_Date" ) ) );
                siteView.setEtsSid( c.getString( c.getColumnIndex( "Ets_ID" ) ) );
                siteView.setDG_TYPE( c.getString( c.getColumnIndex( "DG_Type" ) ) );
                siteView.setDgDesc( c.getString( c.getColumnIndex( "DG_Desc" ) ) );
                siteView.setImgUploadflag( c.getString( c.getColumnIndex( "imgUploadFlag" ) ) );
                siteView.setACTIVITY_STATUS( c.getString( c.getColumnIndex( "Status" ) ) );

                siteView.setGenStatus( c.getString( c.getColumnIndex( "genStatus" )));
                siteView.setPullDate( c.getString( c.getColumnIndex( "pullDate" ) ) );
                siteView.setOverHaulDate( c.getString( c.getColumnIndex( "overHaulDate" ) ) );
                siteView.setPopupFlag( c.getString( c.getColumnIndex( "PopupFlag" ) ) );
                siteView.setAssetFlag(c.getString(c.getColumnIndex("assetFlag")));
                arrayList.add( siteView );
            }
            siteList.setSite_list( arrayList );
        }
        return siteList;
    }

    public void clearScheduleList() {
        sqLiteDB.delete( "SCHEDULE_LIST", null, null );
    }

    public void clearResubmitPMList() {
        sqLiteDB.delete( "RE_SUBMIT_PM_LIST", null, null );
    }

    public void clearScheduleList(String txnId) {
        sqLiteDB.delete( "SCHEDULE_LIST", "Txn_ID =" + txnId, null );
    }


    public void clearMissedList() {
        sqLiteDB.delete( "MISSED_LIST", null, null );
    }

    public void clearMissedList(String txnId) {
        sqLiteDB.delete( "MISSED_LIST", "Txn_ID =" + txnId, null );
    }

    public void clearDoneList() {
        sqLiteDB.delete( "DONE_LIST", null, null );
    }

    public void clearVerifyList() {
        sqLiteDB.delete( "VERIFY_LIST", null, null );
    }

    public void clearRejectList() {
        sqLiteDB.delete( "REJECT_LIST", null, null );
    }

    public void insertPmConfiguration(int activityID, int paramID, String paramName) {
        ContentValues values = new ContentValues();
        values.put( "ACTIVITY_ID", activityID );
        values.put( "PARAMETER_ID", paramID );
        values.put( "PARAMETER_NAME", paramName );
        sqLiteDB.insert( "CONFIG_MOB", null, values );
    }

    public void clearpmconfig() {
        sqLiteDB.delete( "CONFIG_MOB", null, null );
    }

    public String getPMConfig(String activityType, String paramID) {
        String result = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from CONFIG_MOB WHERE ACTIVITY_ID='"
                + activityType + "' and PARAMETER_ID='" + paramID + "'", null );
        while (c.moveToNext()) {
            result = c.getString( c.getColumnIndex( "PARAMETER_NAME" ) );
        }
        return result;
    }

    public void updateReadNotificationFlag(String userId,
                                           String notificationData) {
        ContentValues values = new ContentValues();
        values.put( "readFlag", "1" );
        sqLiteDB.update( "NOTIFICATION", values, "notification=? AND userId =?",
                new String[]{notificationData, userId} );// 0.3
    }

    public void insertMobileInfo(ResponseGetUserInfo response) {
        ContentValues values = new ContentValues();
        values.put( "Lat", response.getLat() );
        values.put( "Longt", response.getLongt() );
        values.put( "BatteryStatus", response.getBatteryStatus() );
        values.put( "NetworkCheck", response.getNetworkCheck() );
        values.put( "Signal", response.getSignal() );
        values.put( "AutoTime", response.getAutoTime() );
        values.put( "TimeStamp", response.getTimeStamp() );
        values.put( "LoginID", response.getLoginID() );
        values.put( "IMEI", response.getImei() );
        values.put( "GPS_STAUS", response.getGps() );
        values.put( "MOCK", response.getMock() );
        values.put("CheckInStatus",response.getCheckInStatus());
        sqLiteDB.insert( "MOBILE_INFO", null, values );
    }

    public ResponseUserInfoList getMobileInfo() {
        ResponseGetUserInfo mobInfo;
        ArrayList<ResponseGetUserInfo> arrayList = new ArrayList<ResponseGetUserInfo>();
        ResponseUserInfoList resList = null;

        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from MOBILE_INFO", null );
        if (c != null) {

            arrayList.clear();
            resList = new ResponseUserInfoList();
            while (c.moveToNext()) {
                mobInfo = new ResponseGetUserInfo();
                mobInfo.setLat( c.getString( c.getColumnIndex( "Lat" ) ) );
                mobInfo.setLongt( c.getString( c.getColumnIndex( "Longt" ) ) );
                mobInfo.setBatteryStatus( c.getInt( c.getColumnIndex( "BatteryStatus" ) ) );
                mobInfo.setNetworkCheck( c.getString( c.getColumnIndex( "NetworkCheck" ) ) );
                mobInfo.setSignal( c.getInt( c.getColumnIndex( "Signal" ) ) );
                mobInfo.setAutoTime( c.getString( c.getColumnIndex( "AutoTime" ) ) );
                mobInfo.setLoginID( c.getString( c.getColumnIndex( "LoginID" ) ) );
                mobInfo.setTimeStamp( c.getString( c.getColumnIndex( "TimeStamp" ) ) );
                mobInfo.setImei( c.getString( c.getColumnIndex( "IMEI" ) ) );
                mobInfo.setGps( c.getString( c.getColumnIndex( "GPS_STAUS" ) ) );
                mobInfo.setMock( c.getString( c.getColumnIndex( "MOCK" ) ) );
                mobInfo.setCheckInStatus(c.getString(c.getColumnIndex("CheckInStatus")));
                arrayList.add( mobInfo );
            }

            resList.setDetails( arrayList );

        }
        return resList;
    }

    public void deleteMobileInfo() {
        sqLiteDB.delete( "MOBILE_INFO", null, null );
    }

    public String getModuleIP(String Caption) {
        String result = "";
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select * from FORM_RIGHTS WHERE MAIN_MENU='"
                + Caption + "'", null );
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = c.getString( c.getColumnIndex( "MODULE_URL" ) );
            }
        } else {
            result = "0";
        }
        //result = "0";
        return result;
    }

    public void updateTrackingRoleRight(String rights) {
        ContentValues values = new ContentValues();
        values.put( "RIGHTS", rights );
        sqLiteDB.update( "FORM_RIGHTS", values, "SUB_MENU =?",
                new String[]{"UserTrackOnOff"} );
    }

    public int trackingRoleRightCount(String sun_menu_name, String menu_name) {
        Cursor c = sqLiteDB.rawQuery( "Select RIGHTS from FORM_RIGHTS WHERE SUB_MENU='"
                + sun_menu_name + "' AND MAIN_MENU='"
                + menu_name + "'", null );

        return c.getCount();

    }


    public Cursor getForm(String taskTypeId) {
        Cursor c = null;
        c = sqLiteDB.rawQuery(
                "Select * from MST_TASK_FORM WHERE TASK_TYPE_ID ='" + taskTypeId + "'", null );
        return c;
    }

    public List<MenuDetail> getSubMenuRight(String parentMenu) {
        List<MenuDetail> subMenuList = new ArrayList<MenuDetail>();
        Cursor c = null;
        c = sqLiteDB.rawQuery( "Select MENU_ID,SUB_MENU,RIGHTS,MENU_CAPTION from FORM_RIGHTS WHERE MAIN_MENU='"
                + parentMenu + "' ORDER BY SUB_MENU_SEQ", null );
        int a = c.getCount();
        if (c.getCount() > 0) {
            MenuDetail menu = null;

            while (c.moveToNext()) {
                menu = new MenuDetail();
                menu.setId( c.getString( 0 ) );
                menu.setName( c.getString( 1 ) );
                menu.setRights( Arrays.asList( c.getString( 2 ).split( "," ) ) );
                menu.setCaption( c.getString( 3 ) );
                subMenuList.add( menu );
            }
        }
        return subMenuList;
    }

    public ArrayList<String> getLocation(String select, String column) {
        ArrayList<String> list = null;
        list = new ArrayList<String>();
        list.add( AppConstants.DD_SELECT_VALUE );
        Cursor c = sqLiteDB.rawQuery( select, null );
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                list.add( c.getString( c.getColumnIndex( column ) ) );
            }
        }
        return list;
    }

    public String getLocationId(String select, String column) {
        String id = null;
        Cursor c = sqLiteDB.rawQuery( select, null );
        if (c != null) {
            while (c.moveToNext()) {
                id = c.getString( c.getColumnIndex( column ) );
            }
        }
        return id;
    }

    public ArrayList<String> getPMapproval(String paramType,String module,String parmDesc) {
        ArrayList<String> list = null;
        Cursor c = null;
        list = new ArrayList<String>();
        list.add("Select");
        if(parmDesc.equalsIgnoreCase("1") || parmDesc.equalsIgnoreCase("2")
                || parmDesc.equalsIgnoreCase("3") || parmDesc.equalsIgnoreCase("4")
                || parmDesc.equalsIgnoreCase("5"))
        {
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                            + paramType + "' AND MODULE_ID='" + module + "' AND P_DESC='" + parmDesc + "' ORDER BY P_NAME ASC", null );
        }else{
            c = sqLiteDB.rawQuery(
                    "Select DISTINCT(P_NAME) from INCIDENT_META_PARAM WHERE P_TYPE='"
                            + paramType + "' AND MODULE_ID='" + module + "'ORDER BY P_ID ASC", null );
        }

        if (c != null) {
            while (c.moveToNext()) {
                list.add( c.getString( c.getColumnIndex( "P_NAME" ) ) );
            }
        }
        return list;
    }

    public String getApprvalCateId1(String paramType,String module,String pName) {

        String pId = null;
        Cursor c = sqLiteDB.rawQuery(
                "Select DISTINCT(P_DESC) from INCIDENT_META_PARAM WHERE P_TYPE='"
                        + paramType+ "' AND MODULE_ID='" + module + "'AND P_NAME='" + pName + "' ORDER BY P_NAME ASC", null );

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                if (!pId.equalsIgnoreCase( "null" ) && !pId.isEmpty()) {
                    pId = c.getString( c.getColumnIndex( "P_DESC" ) );
                } else {
                    pId = "";
                }
            }
        } else {
            pId = "";
        }
        return pId;
    }
}