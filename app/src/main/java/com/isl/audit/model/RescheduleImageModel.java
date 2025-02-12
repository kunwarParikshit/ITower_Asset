package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RescheduleImageModel {
    @SerializedName("img_tag_id")
    @Expose
    private Integer imgTagId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("time")
    @Expose
    private String time;

    public Integer getImgTagId() {
        return imgTagId;
    }

    public void setImgTagId(Integer imgTagId) {
        this.imgTagId = imgTagId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
