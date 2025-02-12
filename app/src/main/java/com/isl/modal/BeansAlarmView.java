package com.isl.modal;

public class BeansAlarmView {
	String ALARM_DESCRIPTION;
	String TIME_DATE;
	String ALARM_TYPE;
	String ALARM_SINCE;
	String FIELDS;
	String COL;
	
	public String getFIELDS() {
		return FIELDS;
	}
	public void setFIELDS(String fIELDS) {
		FIELDS = fIELDS;
	}

	public String getCOL() {
		return COL;
	}

	public void setCOL(String cOL) {
		COL = cOL;
	}

	public String get_DESCRIPTION() {
		return ALARM_DESCRIPTION;
	}
	public void set_DESCRIPTION(String ALARM_DESCRIPTION) {
		this.ALARM_DESCRIPTION = ALARM_DESCRIPTION;
	}

	public String getDATE() {
		return TIME_DATE;
	}
	public void setDATE(String TIME_DATE) {
		this.TIME_DATE = TIME_DATE;
	}

	public String getSINCE() {
		return ALARM_SINCE;
	}
	public void setSINCE(String ALARM_SINCE) {
		this.ALARM_SINCE = ALARM_SINCE;
	}

	public String getAlamType() {
		return ALARM_TYPE;
	}
	public void setAlamType(String ALARM_TYPE) {
		this.ALARM_TYPE = ALARM_TYPE;
	}
}
