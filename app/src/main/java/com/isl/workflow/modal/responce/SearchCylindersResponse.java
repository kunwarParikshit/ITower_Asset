package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchCylindersResponse {
    @SerializedName("cylinder")
    @Expose
    private List<Cylinder> cylinder;

    public List<Cylinder> getCylinder() {
        return cylinder;
    }

    public void setCylinder(List<Cylinder> cylinder) {
        this.cylinder = cylinder;
    }
}
