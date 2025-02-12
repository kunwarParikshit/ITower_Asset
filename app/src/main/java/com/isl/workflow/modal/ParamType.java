package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum ParamType {

    @SerializedName("tran")TRAN("tran"),
    @SerializedName("constant")CONSTANT("constant"),
    @SerializedName("session")SESSION("session"),
    @SerializedName("form")FORM("form"),
    @SerializedName("expression")EXPRESSION("expression");

    private String value;

    ParamType(String value) {
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
