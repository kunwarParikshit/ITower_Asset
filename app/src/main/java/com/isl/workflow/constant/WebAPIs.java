package com.isl.workflow.constant;

public interface WebAPIs {
    public static String metaData = "/api/Service/GetMetaData?typeId=";
    public static String formConfiguration = "/api/Service/GetFormConfiguration?";
    public static String processInitialization = "/api/Service/initiateCamundaFlow?";
    public static String requestList="/api/Service/ChangRequestList";
    public static String requestDetail="/api/Service/GetSingleChangeRequest?";
    public static String saveImage = "/api/upload/Document";
    public static String requestAuditTrail="/api/Service/RequestAuditTrail?";
    public static String requestAssigenmentHistory="/api/Service/RequestAssigenmentHistory?";
    //public static String getApprovalCount = "/api/Service/getSiteData?";
    public static String getApprovalCount = "/api/Service/GetChangeRequestConflict";
    //HSSE
    public static String getHSSEReport="/api/Service/getHSSEReport";
    public static String getHSSErequestDetail="/api/Service/GetHSSESingleChangeRequest?";
    public static String getHSSESubTabrequestDetail="/api/Service/GetHSSESubTabSingleChangeRequest?";
    public static String getHSSEsubtabList="/api/Service/getHSSESubTabsGridData";
    public static String requestAuditTrailHSSE="/api/Service/HSSERequestAuditTrail?";
    public static String saveTicketDisableData="/api/Service/saveTicketDisableData";
    public static String getSiteLocks="/api/Service/getSiteLocks?";
    public static String requestKeygenerate="/api/Service/getSera4Key";

}