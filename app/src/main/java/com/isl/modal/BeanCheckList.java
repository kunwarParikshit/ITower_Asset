package com.isl.modal;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

public class BeanCheckList {
	String madatory;
	String remarkMadatory;
	String fieldType;
	String fieldName;
	String dropDownValue;
	int id;
	TextView textView;
	TextView tvDivider;
	EditText editText;
	Spinner spinner;
	TextView tvRemarksLink;
	TextView imageUrlLink;
	EditText etViRemarks;
	EditText etRiRemarks;
	EditText etReviewRemarks;
	String rFlag;
	EditText etValue;
	HashMap<String, String> childID;
	HashMap<String, String> remarkMada;
	String linkChkList;
	String oldBaseDataStatus;
	String validation_key;
	Button prePhoto;
	Button postPhoto;
	RecyclerView pre_grid;
	RecyclerView post_grid;
	String prePhotoConfig;
	String postPhotoConfig;
	String prePhotoTag;
	String postPhotoTag;
	TextView capturePrePhoto;
	TextView capturePostPhoto;
	int preImgCounter;
	int postImgCounter;

	public TextView getImageUrlLink() {
		return imageUrlLink;
	}

	public void setImageUrlLink(TextView imageUrlLink) {
		this.imageUrlLink = imageUrlLink;
	}

	public TextView getTvDivider() {
		return tvDivider;
	}

	public void setTvDivider(TextView tvDivider) {
		this.tvDivider = tvDivider;
	}

	public int getPreImgCounter() {
		return preImgCounter;
	}

	public void setPreImgCounter(int preImgCounter) {
		this.preImgCounter = preImgCounter;
	}

	public int getPostImgCounter() {
		return postImgCounter;
	}

	public void setPostImgCounter(int postImgCounter) {
		this.postImgCounter = postImgCounter;
	}

	public String getPrePhotoTag() {
		return prePhotoTag;
	}

	public void setPrePhotoTag(String prePhotoTag) {
		this.prePhotoTag = prePhotoTag;
	}

	public String getPostPhotoTag() {
		return postPhotoTag;
	}

	public void setPostPhotoTag(String postPhotoTag) {
		this.postPhotoTag = postPhotoTag;
	}

	public TextView getCapturePrePhoto() {
		return capturePrePhoto;
	}

	public void setCapturePrePhoto(TextView capturePrePhoto) {
		this.capturePrePhoto = capturePrePhoto;
	}

	public TextView getCapturePostPhoto() {
		return capturePostPhoto;
	}

	public void setCapturePostPhoto(TextView capturePostPhoto) {
		this.capturePostPhoto = capturePostPhoto;
	}

	public String getPrePhotoConfig() {
		return prePhotoConfig;
	}

	public void setPrePhotoConfig(String prePhotoConfig) {
		this.prePhotoConfig = prePhotoConfig;
	}

	public String getPostPhotoConfig() {
		return postPhotoConfig;
	}

	public void setPostPhotoConfig(String postPhotoConfig) {
		this.postPhotoConfig = postPhotoConfig;
	}

	public RecyclerView getPre_grid() {
		return pre_grid;
	}

	public void setPre_grid(RecyclerView pre_grid) {
		this.pre_grid = pre_grid;
	}

	public RecyclerView getPost_grid() {
		return post_grid;
	}

	public void setPost_grid(RecyclerView post_grid) {
		this.post_grid = post_grid;
	}

	public Button getPrePhoto() {
		return prePhoto;
	}

	public void setPrePhoto(Button prePhoto) {
		this.prePhoto = prePhoto;
	}

	public Button getPostPhoto() {
		return postPhoto;
	}

	public void setPostPhoto(Button postPhoto) {
		this.postPhoto = postPhoto;
	}

	public String getValidation_key() {
		return validation_key;
	}

	public void setValidation_key(String validation_key) {
		this.validation_key = validation_key;
	}

	public String getOldBaseDataStatus() {
		return oldBaseDataStatus;
	}

	public void setOldBaseDataStatus(String oldBaseDataStatus) {
		this.oldBaseDataStatus = oldBaseDataStatus;
	}

	public String getLinkChkList() {
		return linkChkList;
	}

	public void setLinkChkList(String linkChkList) {
		this.linkChkList = linkChkList;
	}

	public EditText getEtRiRemarks() {
		return etRiRemarks;
	}
	public void setEtRiRemarks(EditText etRiRemarks) {
		this.etRiRemarks = etRiRemarks;
	}
	public String getMadatory() {
		return madatory;
	}
	public void setMadatory(String madatory) {
		this.madatory = madatory;
	}
	public String getRemarkMadatory() {
		return remarkMadatory;
	}
	public void setRemarkMadatory(String remarkMadatory) {
		this.remarkMadatory = remarkMadatory;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDropDownValue() {
		return dropDownValue;
	}
	public void setDropDownValue(String dropDownValue) {
		this.dropDownValue = dropDownValue;
	}
	public int getIdd() {
		return id;
	}
	public void setIdd(int id) {
		this.id = id;
	}
	public TextView getTextView() {
		return textView;
	}
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	public EditText getEditText() {
		return editText;
	}
	public void setEditText(EditText editText) {
		this.editText = editText;
	}
	public Spinner getSpinner() {
		return spinner;
	}
	public void setSpinner(Spinner spinner) {
		this.spinner = spinner;
	}
	
	public HashMap<String, String> getChildID() {
		return childID;
	}
	public void setChildID(HashMap<String, String> childID) {
		this.childID = childID;
	}

	public TextView getTvRemarksLink() {
		return tvRemarksLink;
	}
	public void setTvRemarksLink(TextView tvRemarksLink) {
		this.tvRemarksLink = tvRemarksLink;
	}

	public String getrFlag() {
		return rFlag;
	}
	public void setrFlag(String rFlag) {
		this.rFlag = rFlag;
	}

	public EditText getEtViRemarks() {
		return etViRemarks;
	}
	public void setEtViRemarks(EditText etViRemarks) {
		this.etViRemarks = etViRemarks;
	}

	public EditText getEtReviewRemarks() {
		return etReviewRemarks;
	}
	
	
	public void setEtReviewRemarks(EditText etReviewRemarks) {
		this.etReviewRemarks = etReviewRemarks;
	}
	public EditText getEtValue() {
		return etValue;
	}
	public void setEtValue(EditText etValue) {
		this.etValue = etValue;
	}
	public HashMap<String, String> getRemarkMada() {
		return remarkMada;
	}
	public void setRemarkMada(HashMap<String, String> remarkMada) {
		this.remarkMada = remarkMada;
	}
	
	
}