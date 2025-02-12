package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetKeyDetailsResponse {

    @SerializedName("keyDetails")
    @Expose
    private KeyDetails keyDetails;

    public KeyDetails getKeyDetails() {
        return keyDetails;
    }

    public void setKeyDetails(KeyDetails keyDetails) {
        this.keyDetails = keyDetails;
    }
}