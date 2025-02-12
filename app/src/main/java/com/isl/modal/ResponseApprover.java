package com.isl.modal;

public class ResponseApprover {
	String APPROVAL_PENDING;
	String TILL_YESTERDAY;
	String TILL_TODAY_APPROVED;

	public String getAPPROVAL_PENDING() {
		return APPROVAL_PENDING;
	}
	public void setAPPROVAL_PENDING(String aPPROVAL_PENDING) {
		APPROVAL_PENDING = aPPROVAL_PENDING;
	}

	public String getTILL_YESTERDAY() {
		return TILL_YESTERDAY;
	}
	public void setTILL_YESTERDAY(String tILL_YESTERDAY) {
		TILL_YESTERDAY = tILL_YESTERDAY;
	}

	public String getTILL_TODAY_APPROVED() {
		return TILL_TODAY_APPROVED;
	}
	public void setTILL_TODAY_APPROVED(String tILL_TODAY_APPROVED) {
		TILL_TODAY_APPROVED = tILL_TODAY_APPROVED;
	}
}
