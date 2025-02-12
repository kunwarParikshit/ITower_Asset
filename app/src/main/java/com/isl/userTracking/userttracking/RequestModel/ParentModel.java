package com.isl.userTracking.userttracking.RequestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParentModel {
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("ingester")
    @Expose
    private String ingester;
    @SerializedName("data")
    @Expose
    private List<ChildUserTrackingModel> data;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("mapping")
    @Expose
    private String mapping;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getIngester() {
        return ingester;
    }

    public void setIngester(String ingester) {
        this.ingester = ingester;
    }

    public List<ChildUserTrackingModel> getData() {
        return data;
    }

    public void setData(List<ChildUserTrackingModel> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
