package com.isl.audit.model;

import java.util.List;

public class PerformAuditRequest {
    private int txnId;
    private int auditId;
    private int matrixId;

    public int getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(int matrixId) {
        this.matrixId = matrixId;
    }

    private String user;
    private String ownerSiteId;

    public String getOwnerSiteId() {
        return ownerSiteId;
    }

    public void setOwnerSiteId(String ownerSiteId) {
        this.ownerSiteId = ownerSiteId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    private int siteId;
    private int status;
    private String scheduleDate;
    private String txnDate;
    private String auditType;

    public String getAudit_type() {
        return auditType;
    }

    public void setAudit_type(String auditType) {
        this.auditType = auditType;
    }

    public int getTxnId() {
        return txnId;
    }

    public void setTxnId(int txnId) {
        this.txnId = txnId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public List<PerformAuditAssetRequest> getAssets() {
        return assets;
    }

    public void setAssets(List<PerformAuditAssetRequest> assets) {
        this.assets = assets;
    }

    private List<PerformAuditAssetRequest> assets;
}