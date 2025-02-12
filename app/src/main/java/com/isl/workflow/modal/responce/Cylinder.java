package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cylinder {
    @SerializedName("identity")
    @Expose
    private String identity;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("marking")
    @Expose
    private String marking;
    @SerializedName("plannedLocation")
    @Expose
    private String plannedLocation;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("cylinderPlugA")
    @Expose
    private CylinderPlugA cylinderPlugA;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("importDate")
    @Expose
    private String importDate;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarking() {
        return marking;
    }

    public void setMarking(String marking) {
        this.marking = marking;
    }

    public String getPlannedLocation() {
        return plannedLocation;
    }

    public void setPlannedLocation(String plannedLocation) {
        this.plannedLocation = plannedLocation;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CylinderPlugA getCylinderPlugA() {
        return cylinderPlugA;
    }

    public void setCylinderPlugA(CylinderPlugA cylinderPlugA) {
        this.cylinderPlugA = cylinderPlugA;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

}
