package com.isl.workflow.constant;

import com.google.gson.Gson;

public interface Constants {

    public static Gson gson = new Gson();

    public static final String DAEFULT_DATETIME_FORMAT = "dd-MMM-yyyy HH:mm";
    public static final String INTERNAL_DAEFULT_DATETIME_FORMAT = "MM/dd/yyyy HH:mm";
    public static final String DAEFULT_DATE_FORMAT = "dd-MMM-yyyy";
    public static final String FORM_KEY = "formKey";
    public static final String EDIT_FORM_KEY = "formKeyEdit";
    public static final String FORM_NAME = "formName";
    public static final String CAMUNDA_PROCESS_NAME = "camundaApi";
    public static final String PRODUCTTYPE_ALIAS = "prdtype";
    public static final String TXT_PRODUCTTYPE_ALIAS = "prdtypetxt";
    public static final String REQUEST_STATUS = "rqsts";
    public static final String TXT_REQUEST_STATUS = "rqststxt";
    public static final String TXT_DISPLAY_STATUS = "dispsts";
    public static final String REQUESTER_DATE = "requesterdate";
    public static final String TXN_SOURCE = "src";
    public static final String OPERATION = "oper";
    public static final String EDIT_RIGHTS = "editRights";
    public static final String NEXT_TAB_SELECT = "nexttab";
    public static final String ACTION = "action";


    public static final String PROCESS_INSTANCE_ID = "proc_inst_id_";
    public static final String PROCESS_INSTANCE_KEY = "proc_def_key_";
    public static final String CALLING_TYPE = "callingType";
    public static final String INSTANCE_ID = "instanceId";
    public static final String INSTANCE_NAME = "instanceName";
    public static final String SITE_ID = "siteId";
    public static final String S_ID = "sid";
    public static final String PSDT = "psdt";
    public static final String PEDT = "pedt";


    public static final String FORM_DATA = "formData";
    public static final String T_ID = "tid";
    public static final String TXN_ID = "txnid";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";
    public static final String USER_GROUP = "userGrpLvl";
    //public static final String ASSIGNED_USER = "assigneduser";
    public static final String ASSIGNED_USER = "assignee";
    //public static final String ASSIGNED_GROUP = "assignedgroup";
    public static final String ASSIGNED_GROUP = "agroup";
    public static final String EDIT_GROUP = "editgroup";
    public static final String EDIT_USER = "edituser";
    public static final String AUDIT_TRAIL = "auditTrail";
    public static final String REQUEST_FLAG = "requestFlag";
    public static final String NEW_ASSGINEE = "newAssinee";
    public static final String TASK_ID = "taskid";
    public static final String FORM_KEY_IMPL = "formKeyImpl";
    public static final String IMP_LOGIN_ID = "imploginid";
    public static final String IMPLEMENTER = "implementer";
    public static final String USER_NAME = "username";
    //changes by Avdhesh
    static final int LOCATION_SERVICE_ID = 175;
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

}
