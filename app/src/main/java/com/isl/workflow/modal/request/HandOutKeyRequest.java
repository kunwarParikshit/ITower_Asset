package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandOutKeyRequest {
    @SerializedName("handOutKey")
    @Expose
    private HandOutKey handOutKey;

    public HandOutKey getHandOutKey() {
        return handOutKey;
    }

    public void setHandOutKey(HandOutKey handOutKey) {
        this.handOutKey = handOutKey;
    }

}
