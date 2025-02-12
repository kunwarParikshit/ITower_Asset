package com.isl.workflow.modal;

import android.net.Uri;

public class UploadAssestDetail {
    private String id;
    private String assettype;
    private String assettypeid;
    private String assetListid;
    private String qrcode;
    private String assestDetail;

    public String getAssetListid() {
        return assetListid;
    }

    public void setAssetListid(String assetListid) {
        this.assetListid = assetListid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssestName() {
        return assettype;
    }

    public void setAssestName(String assestName) {
        this.assettype = assestName;
    }

    public String getAssestid() {
        return assettypeid;
    }

    public void setAssestid(String assestid) {
        this.assettypeid = assestid;
    }

    public String getAssestListName() {
        return qrcode;
    }

    public void setAssestListName(String assestListName) {
        this.qrcode = assestListName;
    }

    public String getAssestDetails() {
        return assestDetail;
    }

    public void setAssestDetails(String assestDetails) {
        this.assestDetail = assestDetails;
    }
}
