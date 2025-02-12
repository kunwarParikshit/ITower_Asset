package com.isl.userTracking.userttracking.RequestModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildUserTrackingModel {

    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("locationTime")
    @Expose
    private String locationTime;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("batteryPercent")
    @Expose
    private Integer batteryPercent;
    @SerializedName("gpsValue")
    @Expose
    private Integer gpsValue;
    @SerializedName("networkSignalStatus")
    @Expose
    private Integer networkSignalStatus;
    @SerializedName("mockValue")
    @Expose
    private String mockValue;
    @SerializedName("permission")
    @Expose
    private String permission;
    @SerializedName("checkInStatus")
    @Expose
    private String checkInStatus;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
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

    public Integer getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(Integer batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public Integer getGpsValue() {
        return gpsValue;
    }

    public void setGpsValue(Integer gpsValue) {
        this.gpsValue = gpsValue;
    }

    public Integer getNetworkSignalStatus() {
        return networkSignalStatus;
    }

    public void setNetworkSignalStatus(Integer networkSignalStatus) {
        this.networkSignalStatus = networkSignalStatus;
    }

    public String getMockValue() {
        return mockValue;
    }

    public void setMockValue(String mockValue) {
        this.mockValue = mockValue;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

}
