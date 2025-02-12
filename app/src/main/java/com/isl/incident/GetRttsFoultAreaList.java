package com.isl.incident;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetRttsFoultAreaList {
    @SerializedName("FAULTID")
    @Expose
    private String faultid;
    @SerializedName("FAULTNAME")
    @Expose
    private String faultname;
    @SerializedName("PARENTID")
    @Expose
    private String parentid;
    @SerializedName("ENABLEFLAG")
    @Expose
    private String enableflag;
    @SerializedName("ISRCAMANDATORY")
    @Expose
    private String isrcamandatory;

    public String getFaultid() {
        return faultid;
    }

    public void setFaultid(String faultid) {
        this.faultid = faultid;
    }

    public String getFaultname() {
        return faultname;
    }

    public void setFaultname(String faultname) {
        this.faultname = faultname;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getEnableflag() {
        return enableflag;
    }

    public void setEnableflag(String enableflag) {
        this.enableflag = enableflag;
    }

    public String getIsrcamandatory() {
        return isrcamandatory;
    }

    public void setIsrcamandatory(String isrcamandatory) {
        this.isrcamandatory = isrcamandatory;
    }

}
