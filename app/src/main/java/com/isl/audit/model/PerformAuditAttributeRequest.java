package com.isl.audit.model;

import java.util.List;

public class PerformAuditAttributeRequest {
    private int id;
    private String value;
    private List<PerformAuditImageRequest> image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<PerformAuditImageRequest> getImage() {
        return image;
    }

    public void setImage(List<PerformAuditImageRequest> image) {
        this.image = image;
    }
}
