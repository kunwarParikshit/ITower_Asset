package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RescheduleResultModel {
    @SerializedName("attributes")
    @Expose
    private List<RescheduleAttributeModel> attributes = null;
    @SerializedName("parentAsset")
    @Expose
    private String parentAsset;
    @SerializedName("qrCode")
    @Expose
    private String qrCode;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("typeId")
    @Expose
    private Integer typeId;

    public List<RescheduleAttributeModel> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<RescheduleAttributeModel> attributes) {
        this.attributes = attributes;
    }

    public String getParentAsset() {
        return parentAsset;
    }

    public void setParentAsset(String parentAsset) {
        this.parentAsset = parentAsset;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
