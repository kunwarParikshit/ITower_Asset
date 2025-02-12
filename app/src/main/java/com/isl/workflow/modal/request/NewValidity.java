package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewValidity {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("revalidationConfig")
    @Expose
    private RevalidationConfig revalidationConfig;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RevalidationConfig getRevalidationConfig() {
        return revalidationConfig;
    }

    public void setRevalidationConfig(RevalidationConfig revalidationConfig) {
        this.revalidationConfig = revalidationConfig;
    }
}
