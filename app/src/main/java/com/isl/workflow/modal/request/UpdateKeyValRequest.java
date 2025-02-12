package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateKeyValRequest {
    @SerializedName("keyIdentity")
    @Expose
    private String keyIdentity;
    @SerializedName("newValidity")
    @Expose
    private NewValidity newValidity;

    public String getKeyIdentity() {
        return keyIdentity;
    }

    public void setKeyIdentity(String keyIdentity) {
        this.keyIdentity = keyIdentity;
    }

    public NewValidity getNewValidity() {
        return newValidity;
    }

    public void setNewValidity(NewValidity newValidity) {
        this.newValidity = newValidity;
    }
}
