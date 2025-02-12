package com.isl.audit.model;

public class PerformAuditImageRequest {
    private String tag;
    private String path;
    private String time;
    private Double latitude;
    private Double longitude;
    private int img_tag_id;

    public int getImg_tag_id() {
        return img_tag_id;
    }

    public void setImg_tag_id(int img_tag_id) {
        this.img_tag_id = img_tag_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
