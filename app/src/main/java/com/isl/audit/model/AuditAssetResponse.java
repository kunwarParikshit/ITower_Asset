package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuditAssetResponse {
    @SerializedName("audit_id")
    @Expose
    private Integer auditId;

    public Integer getAuditperform_id() {
        return auditperform_id;
    }

    public void setAuditperform_id(Integer auditperform_id) {
        this.auditperform_id = auditperform_id;
    }

    @SerializedName("auditperform_id")
    @Expose
    private Integer auditperform_id;
    @SerializedName("assetType")
    @Expose
    private List<AuditAssetResult> assetTypes = null;

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public List<AuditAssetResult> getAssetTypes() {
        return assetTypes;
    }

    public void setAssetTypes(List<AuditAssetResult> assetTypes) {
        this.assetTypes = assetTypes;
    }
}
