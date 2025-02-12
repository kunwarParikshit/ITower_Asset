package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RescheduleAttributeModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
