package com.isl.modal;

public class ResponseTechnician {
	String n7Day;
	String tYes;
	String tTdy;

	public String getNEXT_7_DAYS() {
		return n7Day;
	}
	public void setNEXT_7_DAYS(String nEXT_7_DAYS) {
		n7Day = nEXT_7_DAYS;
	}

	public String getTILL_YESTERDAY() {
		return tYes;
	}
	public void setTILL_YESTERDAY(String tILL_YESTERDAY) {
		tYes = tILL_YESTERDAY;
	}

	public String getTILL_TODAY() {
		return tTdy;
	}
	public void setTILL_TODAY(String tILL_TODAY) {
		tTdy = tILL_TODAY;
	}

}
