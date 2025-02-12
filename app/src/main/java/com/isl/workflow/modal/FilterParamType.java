package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum FilterParamType {

    @SerializedName("session")SESSION("session"),
    @SerializedName("local")LOCAL("local");

    private String value;

    FilterParamType(String value) {
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
