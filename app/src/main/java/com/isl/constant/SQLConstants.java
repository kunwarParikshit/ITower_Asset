package com.isl.constant;
/*Updated by Dhakan Lal on 26-Apr-2024
  CR#DM-890
  Version 1.1
  */
public interface SQLConstants {

    public static String txnLocalData = "CREATE TABLE IF NOT EXISTS TXN_LOCAL_DATA (ID INTEGER PRIMARY KEY,  "
            + " TXN_ID TEXT,FLAG TEXT,USER_ID TEXT,ACTIVITY_DATA TEXT, DATE TEXT,PRE_IMG TEXT,POST_IMG TEXT,ALL_IMGS TEXT,IP_ADDRESS TEXT);";

    public static String rejectTxnLocalData = "CREATE TABLE IF NOT EXISTS REJECT_TXN_LOCAL_DATA (ID INTEGER PRIMARY KEY,  "
            + " TXN_ID TEXT,FLAG INTEGER,USER_ID TEXT,ACTIVITY_DATA TEXT, DATE INTEGER,ERROR_MSG TEXT);";

    public static String formRights = "CREATE TABLE IF NOT EXISTS FORM_RIGHTS (ID INTEGER PRIMARY KEY,  "
            + "MAIN_MENU TEXT, SUB_MENU TEXT, RIGHTS TEXT,MAIN_MENU_SEQ INTEGER,SUB_MENU_SEQ INTEGER,MODULE_URL TEXT," +
            "MODULE_CAPTION TEXT,MENU_CAPTION TEXT,MODULE_ID TEXT,MENU_ID TEXT,SUBMENU_LINK TEXT);";

    public static String dataTS = "CREATE TABLE IF NOT EXISTS DATA_TS (ID INTEGER PRIMARY KEY,  "
            + "DATA_TYPE_ID TEXT, LOGIN_TIME_STAMP TEXT, SAVE_TIME_STAMP TEXT,SAVE_TIME_STAMP_ENEG TEXT,SAVE_TIME_STAMP_ASS TEXT,SAVE_TIME_STAMP_ASSS TEXT);";

    public static String createLocationData = "CREATE TABLE IF NOT EXISTS LOCATION_DDL (ID INTEGER PRIMARY KEY,  "
            + "COUNTRY_ID TEXT, COUNTRY TEXT, HUB_ID TEXT, HUB TEXT, REGION_ID TEXT, REGION TEXT ,CIRCLE_ID TEXT, "
            + "CIRCLE TEXT, ZONE_ID TEXT, ZONE TEXT, CLUSTER_ID TEXT, CLUSTER TEXT);";

    public static String createSupplierSQL = "CREATE TABLE IF NOT EXISTS SUPPLIER (ID INTEGER PRIMARY KEY,"
            + " SUPPLIER_NAME TEXT, SUPPLIER_ID TEXT,CIRCLE_ID TEXT,ZONE_ID TEXT,CLUSTER_ID TEXT);";

    public static String createVender = "CREATE TABLE IF NOT EXISTS VENDER (ID INTEGER PRIMARY KEY,"
            + " OME_NAME TEXT, OME_ID TEXT, CIRCLE_ID TEXT,REGION_ID TEXT);";

    public static String createEdg = "CREATE TABLE IF NOT EXISTS ENERGYDG (ID INTEGER PRIMARY KEY,"
            + " DG_TYPE TEXT, DG_DESC TEXT);";

    public static String createEparam = "CREATE TABLE IF NOT EXISTS ENERGYPARAMS (ID INTEGER PRIMARY KEY,"
            + " PARAM_TYPE TEXT, PARAM_NAME TEXT, PARAM_ID TEXT, PARAM_DESC TEXT,P_SHORT_NAME TEXT);";

    public static String createAlarmDesc = "CREATE TABLE IF NOT EXISTS ALARM_DESC (ID INTEGER PRIMARY KEY,"
            + " ALARM_ID TEXT, ALARM_DESC TEXT,EQUIP_ID TEXT,SEVERITY_ID TEXT,ALARM_CATEGORY TEXT,MODULE_ID TEXT," +
            "OPERATOR_ID TEXT,SEVERITY_NAME TEXT,EQUIP_NAME TEXT);";

    public static String createInciEquip = "CREATE TABLE IF NOT EXISTS EQUIPMENT (ID INTEGER PRIMARY KEY,"
            + " EQUIP_ID TEXT, EQUIP_NAME TEXT,MODULE_ID TEXT);";

    public static String createInciparam = "CREATE TABLE IF NOT EXISTS INCIDENT_META_PARAM (ID INTEGER PRIMARY KEY,"
            + " P_TYPE TEXT, P_ID TEXT,P_NAME TEXT,P_DESC TEXT,P_SHORT_NAME TEXT,MODULE_ID TEXT,CREATED_BY TEXT);";

    public static String createIncioperator = "CREATE TABLE IF NOT EXISTS INCIDENT_META_OPERATOR (ID INTEGER PRIMARY KEY,"
            + " OPCO_ID TEXT, OPCO_NAME TEXT,OPCO_CID TEXT,MODULE_ID TEXT);";

    public static String createIncirca = "CREATE TABLE IF NOT EXISTS INCIDENT_META_RCA (ID INTEGER PRIMARY KEY,"
            + " RCA_ID TEXT, RCA_NAME TEXT,PARENT_ID TEXT,IS_RCA_MANDATORY TEXT, MODULE_ID TEXT,USER_CATEGORY TEXT,USER_SUB_CATEGORY TEXT);";

    public static String createIncigrp= "CREATE TABLE IF NOT EXISTS INCIDENT_META_GRP (ID INTEGER PRIMARY KEY,"
            + " GRP_ID TEXT, GRP_NAME TEXT, MODULE_ID TEXT);";

    public static String createUserContact = "CREATE TABLE IF NOT EXISTS USER_CONTACT (ID INTEGER PRIMARY KEY,"
            + " GRP_ID TEXT, U_ID TEXT,U_DETAIL TEXT,U_DESI TEXT, MODULE_ID TEXT);";

    public static String createAssetparam = "CREATE TABLE IF NOT EXISTS ASSET_META_PARAM (ID INTEGER PRIMARY KEY,"
            + " P_TYPE TEXT, P_ID TEXT,P_NAME TEXT,P_DESC TEXT,P_SHORT_NAME TEXT);";

    public static String createCheckPMForm = "CREATE TABLE IF NOT EXISTS CHECKLIST_PM_FORM (ID INTEGER PRIMARY KEY,"
            + "fieldId  INTEGER, fieldType TEXT, fieldName TEXT, dataType TEXT, length TEXT, groupSeq INTEGER,"
            + " groupName TEXT, fieldSeq INTEGER, value TEXT, mandatory TEXT,TypeId TEXT,pActivityId TEXT,pFlag TEXT,"
            + "rMandatory TEXT,linkChkList Text,validateKey Text,preImgConfig Text,postImgConfig Text," +
            "preImgTag Text,postImgTag Text,preMediaType Text,postMediaType Text,MODULE_ID Text,REVIEW_REMARKS Text,equipmentId Text);";

    public static String createPMValue = "CREATE TABLE IF NOT EXISTS TEMP_CHECKLIST (ID INTEGER PRIMARY KEY,"
            + "fieldId  INTEGER, fieldType TEXT, fieldName TEXT, dataType TEXT, length TEXT, groupSeq INTEGER,"
            + " groupName TEXT, fieldSeq INTEGER, value TEXT, mandatory TEXT,status TEXT,TypeId TEXT,pActivityId TEXT," +
            "pFlag TEXT,viRemark TEXT,rMandatory TEXT,rRemark TEXT,rStatus TEXT,linkChkList Text,baseDataLevel Text);";

    public static String createNotification = "CREATE TABLE IF NOT EXISTS NOTIFICATION (ID INTEGER PRIMARY KEY,"
            + "userId INTEGER,notification  TEXT,readFlag TEXT);"; // 0.2

    public static String createTTForm = "CREATE TABLE IF NOT EXISTS CHECKLIST_TT_FORM (ID INTEGER PRIMARY KEY,"
            + "FieldId  INTEGER,Caption TEXT,AddVisable TEXT,UpdateVisable TEXT,DetailsVisable TEXT,ModuleId TEXT);"; // 0.3

    public static String createMultiLanguage = "CREATE TABLE IF NOT EXISTS MULTI_LANGUAGE (ID INTEGER PRIMARY KEY,"
            + "MessageId  INTEGER,Message TEXT);"; // 0.4

    public static String createSparePart = "CREATE TABLE IF NOT EXISTS SPARE_PARTS (ID INTEGER PRIMARY KEY,"
            + "SpareName  TEXT,MaxQty TEXT,DataType TEXT,Flag TEXT,Qty TEXT,SerialNumber TEXT,Status INTEGER,OrgQty TEXT," +
            "CATEGORY_ID TEXT,ACTIVITY_ID TEXT,SELECTION_CATEGORY TEXT);";

    public static String createUserAssoSites = "CREATE TABLE IF NOT EXISTS USER_ASSO_SITES (EtsSiteID INTEGER PRIMARY KEY,"
            + "SiteID  TEXT,lat TEXT,longi TEXT);";

    public static String createschedulelistbackup = "CREATE TABLE IF NOT EXISTS SCHEDULE_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Schedule_Date TEXT," +
            "Ets_ID TEXT,DG_Type TEXT,DG_Desc TEXT,Val_Data TEXT,Reading_Data TEXT," +
            "Operator_Site_Id TEXT,TKT_ID TEXT,genStatus TEXT,pullDate TEXT,overHaulDate TEXT," +
            "PopupFlag TEXT,pmFlag TEXT,pmNote TEXT,assetFlag TEXT)";// 0.5

    public static String createDonelistbackup = "CREATE TABLE IF NOT EXISTS DONE_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Schedule_Date TEXT," +
            "Ets_ID TEXT,DG_Type TEXT,DG_Desc TEXT,Done_Date TEXT," +
            "imgUploadFlag TEXT,Operator_Site_Id TEXT,creplandate TEXT,funcheck TEXT," +
            "TKT_ID TEXT,genStatus TEXT,pullDate TEXT,overHaulDate TEXT,PopupFlag TEXT,assetFlag TEXT)";// 0.5);";


    public static String createMissedlistbackup = "CREATE TABLE IF NOT EXISTS MISSED_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Schedule_Date TEXT,Ets_ID TEXT," +
            "DG_Type TEXT,DG_Desc TEXT,Val_Data TEXT,Reading_Data TEXT,Operator_Site_Id TEXT," +
            "TKT_ID TEXT,genStatus TEXT,pullDate TEXT,overHaulDate TEXT,PopupFlag TEXT,assetFlag TEXT)";// 0.5


    public static String createVerifylistbackup = "CREATE TABLE IF NOT EXISTS VERIFY_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Schedule_Date TEXT," +
            "Ets_ID TEXT,DG_Type TEXT,DG_Desc TEXT,Verify_Date TEXT,imgUploadFlag TEXT," +
            "Operator_Site_Id TEXT,TKT_ID TEXT,genStatus TEXT,pullDate TEXT,overHaulDate TEXT," +
            "PopupFlag TEXT,assetFlag TEXT)";// 0.5

    public static String createRejectlistbackup = "CREATE TABLE IF NOT EXISTS REJECT_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Reject_Date TEXT," +
            "Ets_ID TEXT,DG_Type TEXT,DG_Desc TEXT,Schedule_Date TEXT,imgUploadFlag TEXT," +
            "Rej_Cat TEXT,Rej_Rmks TEXT,Operator_Site_Id TEXT,TKT_ID TEXT,genStatus TEXT," +
            "pullDate TEXT,overHaulDate TEXT,PopupFlag TEXT,assetFlag TEXT)";// 0.5

    public static String configMob = "CREATE TABLE IF NOT EXISTS  CONFIG_MOB (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "ACTIVITY_ID NUMBER,PARAMETER_ID NUMBER,PARAMETER_NAME TEXT);";// 0.6

    public static String mobileInfo = "CREATE TABLE IF NOT EXISTS  MOBILE_INFO (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "Lat TEXT,Longt TEXT,BatteryStatus TEXT,NetworkCheck TEXT,Signal INTEGER,AutoTime TEXT,TimeStamp TEXT,LoginID TEXT,IMEI TEXT,GPS_STAUS TEXT,MOCK TEXT,CheckInStatus TEXT);";//

    public static String mobileLogs= "CREATE TABLE IF NOT EXISTS MOBILE_LOGS (ID INTEGER PRIMARY KEY,"
            + "Logs  TEXT);";

    public static String createTaskForm = "CREATE TABLE IF NOT EXISTS MST_TASK_FORM (TASK_TYPE_ID  INTEGER, FIELD_ID INTEGER, FIELD_NAME TEXT, JSON_KEY TEXT, DATA_TYPE INTEGER,"
            + "DROPDOWN_VAL_TYPE INTEGER,DROPDOWN_VALUE INTEGER, DATA_LENGTH INTEGER, FIELD_TYPE INTEGER,"
            + "IS_MANDATORY TEXT,GROUP_NAME TEXT,GROUP_SEQ TEXT,FIELD_SEQ TEXT,PARENT_LEVEL INTEGER,"
            + "PARENT_CHILD_RELATION TEXT,RMK_ENABLE TEXT,RMK_MANDATORY_COND TEXT,ON_CHANGE_RELOAD TEXT,INITIALIZE_BY TEXT);";

    public static String createImages = "CREATE TABLE IF NOT EXISTS PM_IMAGES (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "TXN_ID TEXT,SITE_ID TEXT,ACTIVITY_TYPE_ID TEXT,CHECKLIST_ID TEXT,SCHEDULE_DATE TEXT,IMAGE_PATH TEXT,LATI TEXT,LONGI TEXT,IMG_TAG TEXT,"
            + "UPLOAD_TIME TEXT,IMAGE_TYPE TEXT,UPLOADING_STATUS TEXT,DG_TYPE TEXT,IMG_NAME TEXT,TIME TEXT," +
            "IP_ADDRESS TEXT,ASSET_ID TEXT);";

    public static String SparePartApproval = "CREATE TABLE IF NOT EXISTS SPARE_PART_APPROVAL (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "TXN_ID TEXT,STATUS TEXT,SPARE_ID TEXT,APPROVE_QTY TEXT,TRAN_TYPE TEXT,USER_ID TEXT,REMARKS TEXT);";


    public static String pmChkListAutoSave = "CREATE TABLE IF NOT EXISTS AUTO_SAVE_PM_CHECKLIST (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "TXN_ID TEXT,DATA TEXT,PRE_IMAGES TEXT,POST_IMAGES TEXT,DATE TEXT,REMARKS TEXT,REVIEW_REMARKS TEXT,ASSET_ID TEXT);";

    public static String createReSubmitListbackup = "CREATE TABLE IF NOT EXISTS RE_SUBMIT_PM_LIST (Txn_ID TEXT,"
            + "Site_ID  TEXT,Site_Name TEXT,Param_Name TEXT,Param_ID TEXT,Status TEXT,Schedule_Date TEXT," +
            "Ets_ID TEXT,DG_Type TEXT,DG_Desc TEXT,Val_Data TEXT,Reading_Data TEXT,Operator_Site_Id TEXT," +
            "rvDate TEXT,rejRmks TEXT,rCat TEXT,Done_Date TEXT,imgUploadFlag TEXT,TKT_ID TEXT," +
            "genStatus TEXT,pullDate TEXT,overHaulDate TEXT,PopupFlag TEXT,assetFlag TEXT)";// 0.5


    public static String CheckListReviewerForm = "CREATE TABLE IF NOT EXISTS CHECKLIST_REVIEWER_FORM (ID INTEGER PRIMARY KEY,"
            + "fieldId  INTEGER, fieldType TEXT, fieldName TEXT, dataType TEXT, length TEXT, groupSeq INTEGER,"
            + " groupName TEXT, fieldSeq INTEGER, value TEXT, mandatory TEXT,TypeId TEXT,pActivityId TEXT,pFlag TEXT,"
            + "rMandatory TEXT,status Text,viRemark Text,rStatus Text,rRemark Text,preImgConfig Text,postImgConfig Text,preImgTag Text,postImgTag Text);";

    public static String dataTSHS = "CREATE TABLE IF NOT EXISTS DATA_TS_HS (ID INTEGER PRIMARY KEY,  "
            + "DATA_TYPE_ID TEXT, LOGIN_TIME_STAMP TEXT, SAVE_TIME_STAMP TEXT,SAVE_TIME_STAMP_ENEG TEXT,SAVE_TIME_STAMP_ASS TEXT,SAVE_TIME_STAMP_ASSS TEXT);";

    //1.1
    public static String scheduledAssets = "CREATE TABLE IF NOT EXISTS SCHEDULED_ASSETS " +
            "(ID INTEGER PRIMARY KEY,  "
            + "PM_TXN_ID INTEGER, EQUIPMENT_NAME TEXT, EQUIPMENT_ID INTEGER,STATUS TEXT," +
            "ASSET_ID TEXT,BRAND TEXT,MODEL TEXT,SERIAL_NUMBER TEXT,CAPACITY TEXT,SCHEDULE_DATE TEXT);";
}
