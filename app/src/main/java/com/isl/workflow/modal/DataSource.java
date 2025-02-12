package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum DataSource {

    @SerializedName("url")URL("url"),
    @SerializedName("values")VALUES("values"),
    @SerializedName("resource")RESOURCE("resource"),
    @SerializedName("expression")EXPRESSION("expression"),
    @SerializedName("local")LOCAL("local");

    private String value;

    DataSource(String value) {
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
