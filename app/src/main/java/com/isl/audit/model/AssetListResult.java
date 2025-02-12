package com.isl.audit.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssetListResult implements Serializable,Cloneable {
    @SerializedName("assetTypeId")
    @Expose
    private Integer assetTypeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("shortCode")
    @Expose
    private String shortCode;
    @SerializedName("category")
    @Expose
    private Integer category;
    @SerializedName("group")
    @Expose
    private Integer group;
    @SerializedName("groupName")
    @Expose
    private String groupName;
    @SerializedName("attributes")
    @Expose
    private List<AttributeResult> attributes = null;

    public List<AssetListResult> getChildAssetsResultList() {
        return childAssetsResultList;
    }

    public void setChildAssetsResultList(List<AssetListResult> childAssetsResultList) {
        this.childAssetsResultList = childAssetsResultList;
    }

    private List<AssetListResult> childAssetsResultList = null;
    @SerializedName("parentTypes")
    @Expose
    private List<Integer> parentTypes = null;

    private String qr_code;
    private boolean showError;
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public ArrayList<Integer> getChildAssets() {
        return childAssets;
    }

    public void setChildAssets(ArrayList<Integer> childAssets) {
        this.childAssets = childAssets;
    }

    private ArrayList<Integer> childAssets = null;

    private String parent;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    private String parentName;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Integer assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<AttributeResult> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeResult> attributes) {
        this.attributes = attributes;
    }

    public List<Integer> getParentTypes() {
        return parentTypes;
    }

    public void setParentTypes(List<Integer> parentTypes) {
        this.parentTypes = parentTypes;
    }

    @NonNull
    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
