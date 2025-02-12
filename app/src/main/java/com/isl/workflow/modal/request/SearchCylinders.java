package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchCylinders {

    @SerializedName("cylinderSearchArguments")
    @Expose
    private CylinderSearchArguments cylinderSearchArguments;

    public CylinderSearchArguments getCylinderSearchArguments() {
        return cylinderSearchArguments;
    }

    public void setCylinderSearchArguments(CylinderSearchArguments cylinderSearchArguments) {
        this.cylinderSearchArguments = cylinderSearchArguments;
    }
}
