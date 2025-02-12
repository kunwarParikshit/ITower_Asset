package com.isl.modal;

public class BeansTicketView {
	String tktId;
	String sid;
	String tDt;
	String status;
	String almDesc;
	String sName;
	String color;
	String firstLevel;
	String secondLevel;
	String digital_dis;
	String tktaction;

	public String getDigital_dis() {
		return digital_dis;
	}

	public void setDigital_dis(String digital_dis) {
		this.digital_dis = digital_dis;
	}

	public String getTktaction() {
		return tktaction;
	}

	public void setTktaction(String tktaction) {
		this.tktaction = tktaction;
	}

	public String getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(String firstLevel) {
		this.firstLevel = firstLevel;
	}

	public String getSecondLevel() {
		return secondLevel;
	}

	public void setSecondLevel(String secondLevel) {
		this.secondLevel = secondLevel;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getsName() {
		return sName;
	}
	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getTICKET_ID() {
		return tktId;
	}
	public void setTICKET_ID(String tICKET_ID) {
		tktId = tICKET_ID;
	}

	public String getSITE_ID() {
		return sid;
	}
	public void setSITE_ID(String sITE_ID) {
		sid = sITE_ID;
	}

	public String getTICKET_DATE() {
		return tDt;
	}
	public void setTICKET_DATE(String tICKET_DATE) {
		tDt = tICKET_DATE;
	}

	public String getTICKET_STATUS() {
		return status;
	}
	public void setTICKET_STATUS(String tICKET_STATUS) {
		status = tICKET_STATUS;
	}

	public String getALARM_DESCRIPTION() {
		return almDesc;
	}
	public void setALARM_DESCRIPTION(String aLARM_DESCRIPTION) {
		almDesc = aLARM_DESCRIPTION;
	}
}
