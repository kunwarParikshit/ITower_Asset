package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyDetails {
    @SerializedName("identity")
    @Expose
    private String identity;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("marking")
    @Expose
    private String marking;
    @SerializedName("secondMarking")
    @Expose
    private String secondMarking;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("lastRemoteUpdate")
    @Expose
    private String lastRemoteUpdate;
    @SerializedName("whereAt")
    @Expose
    private String whereAt;
    @SerializedName("operationalStatus")
    @Expose
    private String operationalStatus;
    @SerializedName("pendingUpdates")
    @Expose
    private PendingUpdates pendingUpdates;
    @SerializedName("validity")
    @Expose
    private Validity validity;
    @SerializedName("remoteEnabled")
    @Expose
    private String remoteEnabled;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("bluetoothSupport")
    @Expose
    private String bluetoothSupport;
    @SerializedName("pinValidationIntervalInMinutes")
    @Expose
    private String pinValidationIntervalInMinutes;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getSecondMarking() {
        return secondMarking;
    }

    public void setSecondMarking(String secondMarking) {
        this.secondMarking = secondMarking;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getLastRemoteUpdate() {
        return lastRemoteUpdate;
    }

    public void setLastRemoteUpdate(String lastRemoteUpdate) {
        this.lastRemoteUpdate = lastRemoteUpdate;
    }

    public String getWhereAt() {
        return whereAt;
    }

    public void setWhereAt(String whereAt) {
        this.whereAt = whereAt;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public PendingUpdates getPendingUpdates() {
        return pendingUpdates;
    }

    public void setPendingUpdates(PendingUpdates pendingUpdates) {
        this.pendingUpdates = pendingUpdates;
    }

    public Validity getValidity() {
        return validity;
    }

    public void setValidity(Validity validity) {
        this.validity = validity;
    }

    public String getRemoteEnabled() {
        return remoteEnabled;
    }

    public void setRemoteEnabled(String remoteEnabled) {
        this.remoteEnabled = remoteEnabled;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getBluetoothSupport() {
        return bluetoothSupport;
    }

    public void setBluetoothSupport(String bluetoothSupport) {
        this.bluetoothSupport = bluetoothSupport;
    }

    public String getPinValidationIntervalInMinutes() {
        return pinValidationIntervalInMinutes;
    }

    public void setPinValidationIntervalInMinutes(String pinValidationIntervalInMinutes) {
        this.pinValidationIntervalInMinutes = pinValidationIntervalInMinutes;
    }

}
