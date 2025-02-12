package com.isl.audit.model;

import java.util.List;

public class PerformAuditAssetRequest {
    private Integer id;
    private int typeId;
    private String parentAsset;
    private String qrCode;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQr_code() {
        return qrCode;
    }

    public void setQr_code(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getParentAsset() {
        return parentAsset;
    }

    public void setParentAsset(String parentAsset) {
        this.parentAsset = parentAsset;
    }

    private List<PerformAuditAttributeRequest> attributes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public List<PerformAuditAttributeRequest> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PerformAuditAttributeRequest> attributes) {
        this.attributes = attributes;
    }


}
