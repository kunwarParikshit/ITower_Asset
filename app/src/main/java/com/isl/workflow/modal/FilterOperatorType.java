package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum FilterOperatorType {

    @SerializedName("in")IN("in"),
    @SerializedName("notin")NOTIN("notin"),
    @SerializedName("equal")EQUAL("equal"),
    @SerializedName("like")LIKE("like");

    private String value;

    FilterOperatorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
