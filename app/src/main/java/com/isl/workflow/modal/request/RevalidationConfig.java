package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RevalidationConfig {
    @SerializedName("oneTimeUpdate")
    @Expose
    private Boolean oneTimeUpdate;
    @SerializedName("intervalInMinutes")
    @Expose
    private Integer intervalInMinutes;

    public Boolean getOneTimeUpdate() {
        return oneTimeUpdate;
    }

    public void setOneTimeUpdate(Boolean oneTimeUpdate) {
        this.oneTimeUpdate = oneTimeUpdate;
    }

    public Integer getIntervalInMinutes() {
        return intervalInMinutes;
    }

    public void setIntervalInMinutes(Integer intervalInMinutes) {
        this.intervalInMinutes = intervalInMinutes;
    }
}
