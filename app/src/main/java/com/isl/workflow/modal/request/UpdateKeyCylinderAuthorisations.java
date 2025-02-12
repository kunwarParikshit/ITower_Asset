package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateKeyCylinderAuthorisations {
    @SerializedName("keyIdentity")
    @Expose
    private String keyIdentity;
    @SerializedName("cylinderPlugIdentities")
    @Expose
    private CylinderPlugIdentities cylinderPlugIdentities;

    public String getKeyIdentity() {
        return keyIdentity;
    }

    public void setKeyIdentity(String keyIdentity) {
        this.keyIdentity = keyIdentity;
    }

    public CylinderPlugIdentities getCylinderPlugIdentities() {
        return cylinderPlugIdentities;
    }

    public void setCylinderPlugIdentities(CylinderPlugIdentities cylinderPlugIdentities) {
        this.cylinderPlugIdentities = cylinderPlugIdentities;
    }
}
