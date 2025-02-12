package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateKeyCylinderResquest {

    @SerializedName("updateKeyCylinderAuthorisations")
    @Expose
    private UpdateKeyCylinderAuthorisations updateKeyCylinderAuthorisations;

    public UpdateKeyCylinderAuthorisations getUpdateKeyCylinderAuthorisations() {
        return updateKeyCylinderAuthorisations;
    }

    public void setUpdateKeyCylinderAuthorisations(UpdateKeyCylinderAuthorisations updateKeyCylinderAuthorisations) {
        this.updateKeyCylinderAuthorisations = updateKeyCylinderAuthorisations;
    }
}
