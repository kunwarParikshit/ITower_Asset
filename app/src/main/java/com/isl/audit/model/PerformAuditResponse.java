package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PerformAuditResponse {
    @SerializedName("flag")
    @Expose
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message;
}
