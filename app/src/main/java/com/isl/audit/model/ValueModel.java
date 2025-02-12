package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ValueModel implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributeId")
    @Expose
    private Integer attributeId;
    @SerializedName("attributeValue")
    @Expose
    private String attributeValue;
    @SerializedName("parentAttributeValueId")
    @Expose
    private Object parentAttributeValueId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Object getParentAttributeValueId() {
        return parentAttributeValueId;
    }

    public void setParentAttributeValueId(Object parentAttributeValueId) {
        this.parentAttributeValueId = parentAttributeValueId;
    }
}
