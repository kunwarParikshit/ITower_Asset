package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandOutKey {
    @SerializedName("personIdentity")
    @Expose
    private String personIdentity;
    @SerializedName("keyIdentity")
    @Expose
    private String keyIdentity;
    @SerializedName("handOutDate")
    @Expose
    private String handOutDate;
    @SerializedName("handInDate")
    @Expose
    private String handInDate;
    @SerializedName("copyAccessProfiles")
    @Expose
    private String copyAccessProfiles;

    public String getPersonIdentity() {
        return personIdentity;
    }

    public void setPersonIdentity(String personIdentity) {
        this.personIdentity = personIdentity;
    }

    public String getKeyIdentity() {
        return keyIdentity;
    }

    public void setKeyIdentity(String keyIdentity) {
        this.keyIdentity = keyIdentity;
    }

    public String getHandOutDate() {
        return handOutDate;
    }

    public void setHandOutDate(String handOutDate) {
        this.handOutDate = handOutDate;
    }

    public String getHandInDate() {
        return handInDate;
    }

    public void setHandInDate(String handInDate) {
        this.handInDate = handInDate;
    }

    public String getCopyAccessProfiles() {
        return copyAccessProfiles;
    }

    public void setCopyAccessProfiles(String copyAccessProfiles) {
        this.copyAccessProfiles = copyAccessProfiles;
    }

}
