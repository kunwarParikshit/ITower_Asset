package com.isl.workflow.modal;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public enum ComponentType implements Serializable{
    @SerializedName("select")SELECT("select"),
    @SerializedName("string")STRING("string"),
    @SerializedName("integer")INTEGER("integer"),
    @SerializedName("float")FLOAT("float"),
    @SerializedName("image")IMAGE("image"),
    @SerializedName("imageqr")QRIMAGE("imageqr"),
    @SerializedName("date")DATE("date"),
    @SerializedName("time")TIME("time"),
    @SerializedName("datetime")DATETIME("datetime"),
    @SerializedName("panel")PANEL("panel"),
    @SerializedName("autocomplete")AUTOCOMPLETE("autocomplete"),
    @SerializedName("button")BUTTON("button"),
    @SerializedName("submit")SUBMIT("submit"),
    @SerializedName("checkbox")CHECKBOX("checkbox"),
    @SerializedName("toggle")TOGGLE("toggle"),
    @SerializedName("multiselect")MULTISELECT("multiselect");

    private String value;

    ComponentType(String value) {
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
