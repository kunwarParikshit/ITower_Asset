package com.isl.constant;

import android.graphics.Bitmap;

import com.isl.modal.HomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AppConstants {

    public static final String db_name = "DATABASE_IMAINTAIN";
    //public static final String VIDEO_COMPRESSOR_APPLICATION_DIR_NAME = "iTower";
    //public static final String VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR = "/Picture/";
    //public static final String MEDIA_TEMP_PATH = "/iTower/Temp";
    public static final String CREATED_DATE = "createddate";
    public static final String INCIDENT_TYPE = "incidenttypetxt";
    public static final String TICKET_STATUS = "tktstatustxt";
   // public static final String DOC_PATH = "/iTower/Document";
    //public static final String PIC_PATH = "/iTower/Picture";
    public static final String TASK_STATE_ID_ALIAS = "tsid";
    public static final String SITE_ID_ALIAS="sid";
    public static final String AbloyLockId="AbloyLockId";
    public static final String keySerNo="keySerNo";
    public static final String SLIP_NO_ALIAS="slno";
    public static final String VEHICLE_NO = "vno";
    public static final String COUNTRY_ID_ALIAS = "coid";
    public static final String HUB_ID_ALIAS = "hid";
    public static final String REGION_ID_ALIAS="rid";
    public static final String CIRCLE_ID_ALIAS="cid";
    public static final String ZONE_ID_ALIAS="zid";
    public static final String CLUSTER_ID_ALIAS="clid";
    public static final String OME_ID_ALIAS="oid";
    public static final String TRAN_DATA_MAP_ALIAS="tranData";
    public static final String IS_EDIT_ALIAS="isedit";
    public static final String FLAG_ALIAS="flag";
    public static final String ADD_PARAM_ALIAS="appParam";
    public static String ADD_PARAM = "addPrms";
    public static final String LAT_ALIAS="lat";
    public static final String LONG_ALIAS="long";
    public static final String LNG_ALIAS="lng";
    public static final String USER_ID_ALIAS="uid";
    public static final String LANGUAGE_CODE_ALIAS="lgCode";
    public static final String FIRST_NAME="fName";
    public static final String UPLOAD_TYPE = "upType";
    public static final String TRAN_DATA = "tData";
    public static final String IMGS = "imgs";
    public static final String DG_TYPE = "dgType";
    public static final String TXN_ID = "txnId";
    public static final String TXN_DATE = "txnDt";
    public static final String FILLER_ID = "filId";
    public static final String IMG_MSG_ALIAS = "imgMsg";
    public static final String IMG_NAME_TEMP_ALIAS = "imgNameTemplate";
    public static final String LANGUAGE = "lc";
    public static final String KEY_ALIAS = "key";
    public static final String TXN_SOURSE = "src";
    public static final String REMARK = "rmk";
    public static final String OPERATION = "opr";
    public static final String MODULE = "module";
    public static final String LANGUAGE_CODE = "lcode";
    public static final String LATITUDE = "lat=";
    public static final String LONGITUDE = "~long=";
    public static final String APP_VERSION = "~appversion=";
    public static final String APP_VERSIONS = "appversion";
    public static final String FILLING_TIME = "~fillTime=";
    public static final String FAULTY_SINCE = "~fas=";
    public static final String USER_NAME = "uname";
    public static final String ADD_PARAMS_ALIAS = "addParam";
    public static final String CPH_FALG = "cph";
    public static final String TRAN_ID_ALIAS = "tranId";
    public static final String REJECTION_REMARKS = "rejRmk";
    public static final String REJECTION_CATEGORY = "rejRmkCategory";
    public static final String REJECTION_TXT = "rejRmkTxt";
    public static final String PRE_IMG = "chkImgPre";
    public static final String POST_IMG = "chkImgPost";
    public static final String USER_MOBILE = "unumber";
    public static final String USER_MAILID = "umailid";
    public static final String USER_GROUP = "ugroup";
    public static final String USER_GROUP_NAME = "ugroupname";
    //trouble ticket keys
    public static final String EQUIPMENT_ID = "eqpId";
    public static final String SEVERITY_ID = "severityId";
    public static final String ALARM_DESCRIPTION_ID = "aldescId";
    public static final String TICKET_LOG_DATE = "ttLogDt";
    public static final String TICKET_TYPE_ID = "ttTypeId";
    public static final String TICKET_STATUS_ID = "ttStatusId";
    public static final String TT_ASSIGN_GROUP_ID = "aGrpId";
    public static final String TT_ASSIGN_USER_ID = "aUsrId";
    public static final String ETA = "eta";
    public static final String ETR = "etr";
    public static final String RCA_CATE_ID = "rcaCatId";
    public static final String RCA = "rca";
    public static final String PROBLEM_START_DATE = "pStDt";
    public static final String PROBLEM_END_DATE = "pEdDt";
    public static final String U_NAME = "uName";//
    public static final String U_ID = "uId";//
    public static final String REMARKS = "rmks";//rmks
    public static final String ALARM_DETAIL = "aldesc";
    public static final String PROBLEM_DESCRIPTION = "pDesc";
    public static final String TICKET_ID = "ticketId";
    public static final String ticket_id = "ticketid";
    public static final String PRE_IMGS = "preImgs";
    public static final String POST_IMGS = "postImgs";
    public static final String CHECK_LIST = "chkList";
    public static final String ROLE_ID = "rlId";
    public static final String ACTIVITY_TYPE_ID = "actTypeId";
    public static final String STATUS_ALIAS = "status";
    public static final String SCHEDULE_DATE = "schDt";
    public static final String DG = "dg";
    public static final String CURRENT_TIME = "cTime";
    public static final String FIRST_TIME_RUN_APP="firstTime";
    public static final String FIRST_TIME_HS="firstTimeHS";
    public static final String LAST_LOGIN_TIME="lastLogin";
    public static final String USER_ROLE_ID="roleId";
    public static final String LOGIN_STATE="login";
    public static final String SYNC_STATE="sync";
    public static final String REF_TICKET_MODE="refTktMode";
    public static final String SITE_ID_NAME="sidName";
    public static final String ETS_ID="etsId";
    public static final String APP_CONFIG_IP="configIp";
    public static final String FORM_RIGHTS="rights";
    public static final String TIME_STAMP="dataTS";
    public static final String SITE_AUDIT_RIGHTS="sidAuditRights";
    public static final String AUDIT_MODE="auditMode";
    public static final String FCM_ID="fcmId";
    public static final String POPUP_NOTIFICATION="popup";
    public static final String NOTIFICATION_SOUND="sound";
    public static final String NOTIFICATION_SOUND_NAME="soundName";
    public static final String NOTIFICATION_VIBRATE="vibrate";
    public static final String NOTIFICATION_LIST_CLASS_FLAG="nlf";
    public static final String HOME_SCREEN_FLAG="hcf";
    public static final String FILLING_MIN_IMAGES="ffMinImage";
    public static final String FILLING_MAX_IMAGES="ffMaxImage";
    public static final String FILLING_IMGS_MESSAGE="ffImageMessage";
    public static final String TT_MIN_IMAGES="ttMinImage";
    public static final String TT_MAX_IMAGES="ttMaxImage";
    public static final String TT_IMGS_MESSAGE="ttImageMessage";
    public static final String TT_MIN_IMAGES_HS="ttMinImageHS";
    public static final String TT_MAX_IMAGES_HS="ttMaxImageHS";
    public static final String TT_IMGS_MESSAGE_HS="ttImageMessageHS";
    public static final String SERVICE_RECURSIVE_TIME="srvcTime";
    public static final String SUBMIT_PM_BACKGROUND="bGround";
    public static final String SITE_NAME_ENABLE="sNameEnable";
    public static final String SEARCH_TKT_DATE_RANGE="stktDateRange";
    public static final String ENABLE_PREPOPULATE_SITES_TT="enablePrePopulateSitesTT";
    public static final String CHECK_POPUP="foreground";
    public static final String TOAST_FLAG="toast";
    public static final String TOAST_SHOW="toastShow";
    public static final String TT_ASSIGN_NOTIFICATION_CHK="ttAssignNotify";
    public static final String TT_UPDATE_NOTIFICATION_CHK="ttUpdateNotify";
    public static final String TT_ESC_NOTIFICATION_CHK="ttESCNotify";
    public static final String PM_SCHEDULE_NOTIFICATION_CHK="pmScheduleNotify";
    public static final String PM_ESC_NOTIFICATION_CHK="pmESCNotify";
    public static final String TOGGLE_BUTTON="togglebtn";
    public static final String BACK_SCREEN="back";
    public static final String PM_TABS="pmtabs";
    public static final String BACK_PRESS_HOMESCREEN="backPress";
    public static final String MAX_LOGIN_ATTEMPT="max";
    public static final String ENABLE_FF_FIELDS="enable";
    public static final String LOGIN_ID="loginId";
    public static final String PASSWORD="password";
    public static final String PM_CONFIGURATION="pmconfiguration";
    public static final String TT_CONFIGURATION="ttconfiguration";
    public static final String FF_CONFIGURATION="ffconfiguration";
    public static final String CHECK_AUTO_DATE_TIME="autoDateTime";
    public static final String USER_TRACKING="userTracking";
    public static final String USER_TRACKING_UPLOAD_TIME="userTrackUploadTime";
    public static final String MOCK_LOCATION="mockSetting";
    public static final String NOTIFICATION_SETTING="appSettingNotification";
    public static final String TRACKING_MODE="TrackMode";
    public static final String TRACKING_ENABLE="trackingEnable";
    public static final String HEADER_CAPTION_ID="hcid";
    public static final String ZERO="0";
    public static final String COMMA=",";
    public static final String ADD_PARAM_SEPERATOR="~";
    public static final String USER_CATEGORY = "userCategory";
    public static final String USER_SUB_CATEGORY = "userSubCategory";
    public static final String OPERATOR_WISE_USER_FIELD = "operatorWiseUserField";
    public static final String PM_HYPER_LINK = "hyperLinkPM";
    public static final String DD_SELECT_VALUE="Select";
    public static final String DD_SELECTED_VALUE=" Selected";
    public static final String DD_SELECT_ID="0";
    public static final String DATA_TYPE_ALIAS = "DATA_TYPE";
    public static final String FIELD_TYPE_ALIAS = "FIELD_TYPE";
    public static final String FIELD_NAME_ALIAS = "FIELD_NAME";
    public static final String DATA_LENGTH_ALIAS = "DATA_LENGTH";
    public static final String DROPDOWN_VAL_TYPE_ALIAS = "DROPDOWN_VAL_TYPE";
    public static final String DROPDOWN_VALUE_ALIAS = "DROPDOWN_VALUE";
    public static final String FIELD_ID_ALIAS = "FIELD_ID";
    public static final String ON_CHANGE_RELOAD_ALIAS = "ON_CHANGE_RELOAD";
    public static final String JSON_KEY_ALIAS = "JSON_KEY";
    public static final String IS_MANDATORY_ALIAS = "IS_MANDATORY";
    public static final String PARENT_LEVEL_ALIAS = "PARENT_LEVEL";
    public static final String PARENT_CHILD_RELATION ="PARENT_CHILD_RELATION";
    public static final String INITIALIZE_BY ="INITIALIZE_BY";
    public static final String IS_EDIT ="IS_EDIT";
    public static final String FILLER ="FILLER";
    public static final String TRAN_DATE ="TRAN_DATE";
    public static final String FILL_TIME ="FILLTIME";
    public static final String DD_SELECT_OPTION ="Select";
    public static final String SQURE_BRACKET_START ="[";
    public static final String SESSION ="Session";
    public static final String FILLING_DATE_KEY ="fdt";
    public static final String FILLING_TIME_KEY ="ftm";
    public static final String FILLER_ID_KEY ="fidtl";
    public static final String msg_form="Form not available. Please contact system admin";
    public static final String msg_txn_data="Unable to get transaction detail for particular transaction.";
    //Added by Avdhesh
    public static final String USER_GIVEN_PERMISSION ="permission";
    public static final String PUNCH_IN_OUT ="inout";
    public static final String PLUG_IDENTY ="plugidentty";
    public static final String CHECK_OUT_TIME ="checkout";
    public static final String CHECK_IN_TIME ="checkin";
    public static final String TIME_INTERVAL="timeinterval";


    public static final String TRANSID ="tranid";
    public static final String TRANNAME ="tranname";
    public static final String PDT ="pdt";
    public static final String PQTY ="pqty";
    public static final String TRANTYPE="trantyp";
    public static final String TKTID="tktid";
    public static final String FWO="fwo";
    public static final String trantypeFF="trantypeFF";
    public static final String ClassModule="classmodule";

    public static final String serviceImpacted="serviceImpacted";
    public static final String serviceImpactStart="serviceImpactStart";
    public static final String servicesAffected="servicesAffected";

    /*
    public static HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
    //public static HashMap<String, Integer> hashMap_my = new HashMap<String, Integer>();
    public static HashMap<String, Class> classMap = new HashMap<String, Class>();
    public static HashMap<String, String> mainModule = new HashMap<String, String>();*/
    public List<HomeModule> moduleList = new ArrayList<HomeModule>();


    public static String[] ALL_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
    };

    public static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
    };

    public static String[] LOCATION_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public static enum FIELD_TYPE {
        READ_ONLY(1),
        READ_WRITE(2),
        HIDDEN(3);

        private int value;

        FIELD_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }
    }

    public static enum DATA_TYPE {
        ALPHANUMERIC(1),
        DROPDOWN(2),
        NUMBER(3),
        INTEGER(4),
        IMAGE(5),
        DATE(6),
        TIME(7),
        AUTOCOMPLETE(8),
        HEADER(9);

        private int value;

        DATA_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }
    }

    public static enum DROPDOWN_VAL_TYPE {
        SQL(1),
        INLINE(2);

        private int value;

        DROPDOWN_VAL_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }

        public static DROPDOWN_VAL_TYPE fromValue(String v) {
            return valueOf(v);
        }
    }

    public static enum USER_ROLE {
        TECHNICIAN(32),
        CIRCLE_MANAGER(33),
        TECH_FILLER(36),
        FILLER(34);

        private int value;

        USER_ROLE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }

        public static USER_ROLE fromValue(String v) {
            return valueOf(v);
        }
    }

    public static enum ENERGY_TASK_TYPE {
        LOCATION_TASK(1),
        ENERGY_PARAMS(2),
        ENERGY_PURCHASE_RPT(3),
        ENERGY_FILLING_RPT(4),
        GET_SITES(5);

        private int value;

        ENERGY_TASK_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }

        public static ENERGY_TASK_TYPE fromValue(String v) {
            return valueOf(v);
        }
    }

    public static enum PARENT_LEVEL{
        PARENT(1),
        CHILD(2);

        private int value;

        PARENT_LEVEL(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.getValue());
        }

        public static PARENT_LEVEL fromValue(String v) {
            return valueOf(v);
        }


    }



    /*Added by Anshul Sharma*/
    public static class  INTENT_EXTRAS{
        public static final String AUDIT_TYPE="audit_type";
        public static final String AUDIT_STATUS="audit_status";
        public static final String AUDIT_TYPE_API="audit_type_api";
        public static final String SCHEDULED_DATE="scheduled_date";
        public static final String TXN_ID="txn_id";
        public static final String INSTANCE_ID="instance_id";
        public static final String MATRIX_ID="matrix_id";
        public static final String AUDIT_NAME="audit_name";
        public static final String SITE_ID="site_id";
        public static final String OWNER_SITE_ID="owner_site_id";
        public static final String POSITION="position";
        public static final String ASSET_TYPE_ID="asset_type_id";
        public static final String ASSET_POSITION="asset_position";
        public static final String QR_CODE="qr_code";
        public static final String AUDIT_ID="audit_id";
        public static final String QR_LIST="qr_list";
        public static final String AUDIT_PERFORM_ID="audit_perform_id";
        public static final String ASSET_MODEL="asset_model";
        public static final String ADD_CHILD="add_child";
        public static final String ADD_PARENT="add_parent";
        public static final String PARENT="parent";
        public static final String PARENT_NAME="parent_name";
        public static final String CHILD_ASSET_LIST="child_asset_list";
        public static final String PARENT_ASSET_LIST="parent_asset_list";
    }
    public static class  ATTRIBUTE_TYPE{
        public static final String NUMBER="Number";
        public static final String TEXT="Text";
        public static final String PICKLIST="Picklist";
        public static final String IMAGE="Image";
        public static final String DATE="Date";
        public static final String QR_CODE="QRCode";
        public static final String SERIAL_NUMBER="serialNumber";
    }
    public static class  IMAGE_FORMAT_TYPE{
        public static final String SITE_ID="siteId";
        public static final String ASSET_TYPE="assetType";
        public static final String AUDIT_NAME="auditName";
        public static final String TIME="time";
        public static final String ATTRIBUTE="attribute";
    }
    public static String[] CAMERA_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };



}
