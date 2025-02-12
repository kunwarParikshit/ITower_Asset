package com.isl.audit.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuditListResult {
    private boolean needSync=false;

    public boolean isNeedSync() {
        return needSync;
    }

    public void setNeedSync(boolean needSync) {
        this.needSync = needSync;
    }

    @SerializedName("_id")
    @Expose
    private Integer id;

    public Integer getTxnId() {
        return txnId;
    }

    public void setTxnId(Integer txnId) {
        this.txnId = txnId;
    }

    @SerializedName("txnId")
    @Expose
    private Integer txnId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @SerializedName("instanceId")
    @Expose
    private String instanceId;
    @ColumnInfo(name = "assetTypes")
    private List<AuditAssetResponse> assetTypes;

    public List<AuditAssetResponse> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AuditAssetResponse> assetTypes) {
        this.assetTypes = assetTypes;
    }

    @SerializedName("auditId")
    @Expose
    private Integer auditId;
    @SerializedName("siteId")
    @Expose
    private Integer siteId;
    @SerializedName("auditor_id")
    @Expose
    private String auditorId;
    @SerializedName("scheduleDate")
    @Expose
    private String scheduleDate;
    @SerializedName("doneStartDate")
    @Expose
    private Object doneStartDate;
    @SerializedName("doneEndDate")
    @Expose
    private Object doneEndDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("scan_qr_code")
    @Expose
    private Object scanQrCode;
    @SerializedName("audit_cycle")
    @Expose
    private Integer auditCycle;
    @SerializedName("auditType")
    @Expose
    private String auditType;
    @SerializedName("matrixId")
    @Expose
    private Integer auditMatrixId;
    @SerializedName("recurring")
    @Expose
    private Boolean recurring;
    @SerializedName("recurring_duration_unit")
    @Expose
    private String recurringDurationUnit;
    @SerializedName("recurring_duration_value")
    @Expose
    private String recurringDurationValue;
    @SerializedName("ownerSiteId")
    @Expose
    private String ownerSiteId;
    @SerializedName("auditName")
    @Expose
    private String auditName;

    private List<AssetListResult> selectedAssetList;

    public List<AssetListResult> getSelectedAssetList() {
        return selectedAssetList;
    }

    public void setSelectedAssetList(List<AssetListResult> selectedAssetList) {
        this.selectedAssetList = selectedAssetList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Object getDoneStartDate() {
        return doneStartDate;
    }

    public void setDoneStartDate(Object doneStartDate) {
        this.doneStartDate = doneStartDate;
    }

    public Object getDoneEndDate() {
        return doneEndDate;
    }

    public void setDoneEndDate(Object doneEndDate) {
        this.doneEndDate = doneEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getScanQrCode() {
        return scanQrCode;
    }

    public void setScanQrCode(Object scanQrCode) {
        this.scanQrCode = scanQrCode;
    }

    public Integer getAuditCycle() {
        return auditCycle;
    }

    public void setAuditCycle(Integer auditCycle) {
        this.auditCycle = auditCycle;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    public Integer getAuditMatrixId() {
        return auditMatrixId;
    }

    public void setAuditMatrixId(Integer auditMatrixId) {
        this.auditMatrixId = auditMatrixId;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public String getRecurringDurationUnit() {
        return recurringDurationUnit;
    }

    public void setRecurringDurationUnit(String recurringDurationUnit) {
        this.recurringDurationUnit = recurringDurationUnit;
    }

    public String getRecurringDurationValue() {
        return recurringDurationValue;
    }

    public void setRecurringDurationValue(String recurringDurationValue) {
        this.recurringDurationValue = recurringDurationValue;
    }

    public String getOwnerSiteId() {
        return ownerSiteId;
    }

    public void setOwnerSiteId(String ownerSiteId) {
        this.ownerSiteId = ownerSiteId;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }
}
