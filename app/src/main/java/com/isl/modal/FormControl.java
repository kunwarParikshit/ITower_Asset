package com.isl.modal;

import android.net.Uri;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.isl.constant.AppConstants;

import org.json.JSONArray;

import java.util.ArrayList;

public class FormControl {
    private String fieldId;
    private String fieldName;
    private int dropDownValType;
    private String dropDownValue;
    private String changeReload;
    private String pcRelation;
    private boolean isMandatory;
    private int fieldType;
    private int dataType;
    private int before;
    private int after;
    private String keyItem;
    private String dataLength;
    private TextView tv;
    private Spinner spinner;
    private EditText editText;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> imgMsg;
    private ArrayList<String> ddValues;
    private ArrayList<String> ddIds;
    private JSONArray imgInfoArray;
    private String imgData;
    private int imgCounter;
    private String imageNameTemplate;
    private Uri currImageUri;
    private String currName;
    private String imgLocation;
    private int plevel;
    private String initialize;


    public String getInitialize() {
        return initialize;
    }

    public void setInitialize(String initialize) {
        this.initialize = initialize;
    }

    public String getPCRelation() {
        return pcRelation;
    }

    public void setPCRelation(String pcRelation) {
        this.pcRelation = pcRelation;
    }

    public int getPlevel() {
        return plevel;
    }

    public void setPlevel(int plevel) {
        this.plevel = plevel;
    }

    public String getCurrName() {
        return currName;
    }

    public void setCurrName(String currName) {
        this.currName = currName;
    }

    public String getImgLocation() {
        return imgLocation;
    }

    public void setImgLocation(String imgLocation) {
        this.imgLocation = imgLocation;
    }

    public Uri getCurrImageUri() {
        return currImageUri;
    }

    public void setCurrImageUri(Uri currImageUri) {
        this.currImageUri = currImageUri;
    }

    public String getImageNameTemplate() {
        return imageNameTemplate;
    }

    public void setImageNameTemplate(String imageNameTemplate) {
        this.imageNameTemplate = imageNameTemplate;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        if(this.imgData==null){
            this.imgData = imgData;
        } else{
            this.imgData = this.imgData+AppConstants.ADD_PARAM_SEPERATOR+imgData;
        }
    }

    public int getImgCounter() {
        return imgCounter;
    }

    public void setImgCounter(int imgCounter) {
        this.imgCounter = imgCounter;
    }

    public void increaseImgCounter() {
        this.imgCounter=imgCounter+1;
    }

    public JSONArray getImgInfoArray() {
        if(imgInfoArray==null){
            imgInfoArray=new JSONArray() ;
        }
        return imgInfoArray;
    }

    public void setImgInfoArray(JSONArray imgInfoArray) {
        this.imgInfoArray = imgInfoArray;
    }

    public ArrayList<String> getImgMsg() {
        if(imgMsg==null){
            imgMsg = new ArrayList<String>();
        }
        return imgMsg;
    }

    public void setImgMsg(ArrayList<String> imgMsg) {
        this.imgMsg = imgMsg;
    }

    public int getBefore() {
        return before;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public int getAfter() {
        return after;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public ArrayList<String> getDdIds() {
        if(ddIds==null){
            ddIds = new ArrayList<String>();
        }
        return ddIds;
    }

    public void setDdIds(ArrayList<String> ddIds) {
        this.ddIds = ddIds;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getDropDownValType() {
        return dropDownValType;
    }

    public void setDropDownValType(int dropDownValType) {
        this.dropDownValType = dropDownValType;
    }

    public String getDropDownValue() {
        return dropDownValue;
    }

    public void setDropDownValue(String dropDownValue) {
        this.dropDownValue = dropDownValue;
    }

    public String getChangeReload() {
        return changeReload;
    }

    public void setChangeReload(String changeReload) {
        this.changeReload = changeReload;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getKeyItem() {
        return keyItem;
    }

    public void setKeyItem(String keyItem) {
        this.keyItem = keyItem;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public Spinner getSpinner() {

        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public ArrayList<String> getDdValues() {
        if(ddValues==null){
            ddValues = new ArrayList<String>();
        }
        return ddValues;
    }

    public void setDdValues(ArrayList<String> ddValues) {
        this.ddValues = ddValues;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public AutoCompleteTextView getAutoCompleteTextView() {
        return autoCompleteTextView;
    }

    public void setAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView;
    }
}