package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum RequestMethod {

    @SerializedName("post")POST("post"),
    @SerializedName("get")GET("get"),
    @SerializedName("put")PUT("put");

    private String value;

    RequestMethod(String value) {
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
