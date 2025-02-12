package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CylinderPlugIdentities {

    @SerializedName("cylinderPlugIdentity")
    @Expose
    private List<String> cylinderPlugIdentity;

    public List<String> getCylinderPlugIdentity() {
        return cylinderPlugIdentity;
    }

    public void setCylinderPlugIdentity(List<String> cylinderPlugIdentity) {
        this.cylinderPlugIdentity = cylinderPlugIdentity;
    }
}
