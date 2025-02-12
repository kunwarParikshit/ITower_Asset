package com.isl.audit.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class AttributeResult implements Serializable,Cloneable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("assetTypeId")
    @Expose
    private Integer assetTypeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("flag")
    @Expose
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("mandatory")
    @Expose
    private Boolean mandatory;
    @SerializedName("history")
    @Expose
    private Boolean history;
    @SerializedName("min")
    @Expose
    private String min;
    @SerializedName("max")
    @Expose
    private String max;
    @SerializedName("regex")
    @Expose
    private String regex;
    @SerializedName("selection")
    @Expose
    private String selection;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("parentId")
    @Expose
    private Object parentId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("decimal")
    @Expose
    private String decimal;
    @SerializedName("mongo_id")
    @Expose
    private String mongoId;
    @SerializedName("values")
    @Expose
    private List<ValueModel> values = null;

    private List<ImageModel> imgUrl;
    private String textValue;

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }

    private String qrValue;
    private String numValue;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public List<String> getSelectedValueList() {
        return selectedValueList;
    }

    public void setSelectedValueList(List<String> selectedValueList) {
        this.selectedValueList = selectedValueList;
    }

    private List<String> selectedValueList;



    public String getNumValue() {
        return numValue;
    }

    public void setNumValue(String numValue) {
        this.numValue = numValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public List<ImageModel> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<ImageModel> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getHistory() {
        return history;
    }

    public void setHistory(Boolean history) {
        this.history = history;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public List<ValueModel> getValues() {
        return values;
    }

    public void setValues(List<ValueModel> values) {
        this.values = values;
    }

    @NonNull
    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
