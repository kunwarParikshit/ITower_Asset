package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Update {
    @SerializedName("keyUpdateType")
    @Expose
    private String keyUpdateType;
    @SerializedName("overflowed")
    @Expose
    private String overflowed;

    public String getKeyUpdateType() {
        return keyUpdateType;
    }

    public void setKeyUpdateType(String keyUpdateType) {
        this.keyUpdateType = keyUpdateType;
    }

    public String getOverflowed() {
        return overflowed;
    }

    public void setOverflowed(String overflowed) {
        this.overflowed = overflowed;
    }
}
