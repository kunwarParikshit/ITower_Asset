package com.isl.preventive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTxnListResponce {
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("projectName")
    @Expose
    private String projectName;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("SONumber")
    @Expose
    private String sONumber;
    @SerializedName("workOrderNumber")
    @Expose
    private String workOrderNumber;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("siteTowerType")
    @Expose
    private String siteTowerType;

    //Enable Geo fencing
    @SerializedName("geoFenEnb")
    @Expose
    private String geoFenEnb;

    //Enable Geo fencing activity wise
    @SerializedName("enbActWiseGeoFen")
    @Expose
    private String enbActWiseGeoFen;

    //Enable Geo fencing site wise
    @SerializedName("enbSitWiseGeoFen")
    @Expose
    private String enbSitWiseGeoFen;

    //Geo fencing radius
    @SerializedName("geoFenRadius")
    @Expose
    private String geoFenRadius;


    //Geo fencing radius
    @SerializedName("calculateLogic")
    @Expose
    private String calculateLogic;

    //Geo fencing radius
    @SerializedName("apiKey")
    @Expose
    private String apiKey;

    @SerializedName("assetId")
    @Expose
    private String assetId;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }


    public String getCalculateLogic() {
        return calculateLogic;
    }

    public void setCalculateLogic(String calculateLogic) {
        this.calculateLogic = calculateLogic;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getGeoFenEnb() {
        return geoFenEnb;
    }

    public void setGeoFenEnb(String geoFenEnb) {
        this.geoFenEnb = geoFenEnb;
    }

    public String getEnbActWiseGeoFen() {
        return enbActWiseGeoFen;
    }

    public void setEnbActWiseGeoFen(String enbActWiseGeoFen) {
        this.enbActWiseGeoFen = enbActWiseGeoFen;
    }

    public String getEnbSitWiseGeoFen() {
        return enbSitWiseGeoFen;
    }

    public void setEnbSitWiseGeoFen(String enbSitWiseGeoFen) {
        this.enbSitWiseGeoFen = enbSitWiseGeoFen;
    }

    public String getGeoFenRadius() {
        return geoFenRadius;
    }

    public void setGeoFenRadius(String geoFenRadius) {
        this.geoFenRadius = geoFenRadius;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSONumber() {
        return sONumber;
    }

    public void setSONumber(String sONumber) {
        this.sONumber = sONumber;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSiteTowerType() {
        return siteTowerType;
    }

    public void setSiteTowerType(String siteTowerType) {
        this.siteTowerType = siteTowerType;
    }

}
