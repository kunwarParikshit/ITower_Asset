package com.isl.modal;
/*Modified by :  Dhakan lal
Modified On :  19-May-2016.
Purpose     :  CR# 1.9.1(get parentId,isRcaMandatory)
Version     :  0.1 */

public class RcaCateg {
	String rcaId;
	String rcaName;
	String parentId;
	String isRcaMandatory;
	String userCat;
	String userSubCat;
	String grpId;
	String actTypeId;
	String assignGrpId;
	String reviewGrpId;

	public String getUserCat() {
		return userCat;
	}

	public void setUserCat(String userCat) {
		this.userCat = userCat;
	}

	public String getUserSubCat() {
		return userSubCat;
	}

	public void setUserSubCat(String userSubCat) {
		this.userSubCat = userSubCat;
	}

	public String getRcaId() {
		return rcaId;
	}
	
	public void setRcaId(String rcaId) {
		this.rcaId = rcaId;
	}
	
	public String getRcaName() {
		return rcaName;
	}
	
	public void setRcaName(String rcaName) {
		this.rcaName = rcaName;
	}
    // start 0.1
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getIsRcaMandatory() {
		return isRcaMandatory;
	}

	public void setIsRcaMandatory(String isRcaMandatory) {
		this.isRcaMandatory = isRcaMandatory;
	}

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getActTypeId() {
		return actTypeId;
	}

	public void setActTypeId(String actTypeId) {
		this.actTypeId = actTypeId;
	}

	public String getAssignGrpId() {
		return assignGrpId;
	}

	public void setAssignGrpId(String assignGrpId) {
		this.assignGrpId = assignGrpId;
	}

	public String getReviewGrpId() {
		return reviewGrpId;
	}

	public void setReviewGrpId(String reviewGrpId) {
		this.reviewGrpId = reviewGrpId;
	}
}
