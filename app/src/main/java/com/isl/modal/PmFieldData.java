package com.isl.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PmFieldData {

    @SerializedName("FIELD_KEY")
    @Expose
    private String fieldKey;
    @SerializedName("FIELD_VISIBILITY")
    @Expose
    private String fieldVisibility;

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldName) {
        this.fieldKey = fieldName;
    }

    public String getFieldVisibility() {
        return fieldVisibility;
    }

    public void setFieldVisibility(String fieldVisibility) {
        this.fieldVisibility = fieldVisibility;
    }
}
