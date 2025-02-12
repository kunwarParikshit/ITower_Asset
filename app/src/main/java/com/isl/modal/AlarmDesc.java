package com.isl.modal;

public class AlarmDesc {
	String alarm;
	String alarmId;
	String equipId;
	String severityId;
	String alarmCategoryId;
	String operatorId;
	String severityName;
	String equipName;

	public String getSeverityName() {
		return severityName;
	}

	public void setSeverityName(String severityName) {
		this.severityName = severityName;
	}

	public String getEquipName() {
		return equipName;
	}

	public void setEquipName(String equipName) {
		this.equipName = equipName;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getEquipId() {
		return equipId;
	}

	public void setEquipId(String equipId) {
		this.equipId = equipId;
	}

	public String getSeverityId() {
		return severityId;
	}

	public void setSeverityId(String severityId) {
		this.severityId = severityId;
	}

	public String getAlarmCategoryId() {
		return alarmCategoryId;
	}

	public void setAlarmCategoryId(String alarmCategoryId) {
		this.alarmCategoryId = alarmCategoryId;
	}

}
