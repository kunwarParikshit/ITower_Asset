package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuditAssetResult {
    @SerializedName("id")
    @Expose
    private String id;

    public HashMap<String, ArrayList<Integer>> getImage_tag() {
        return image_tag;
    }

    public void setImage_tag(HashMap<String, ArrayList<Integer>> image_tag) {
        this.image_tag = image_tag;
    }

    @SerializedName("image_tag")
    @Expose
    private HashMap<String, ArrayList<Integer>> image_tag;
    @SerializedName("attributes")
    @Expose
    private List<Integer> attributes = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Integer> attributes) {
        this.attributes = attributes;
    }

}
