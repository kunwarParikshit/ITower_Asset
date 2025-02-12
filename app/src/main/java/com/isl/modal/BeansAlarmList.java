package com.isl.modal;

import java.util.List;

public class BeansAlarmList extends BeansErrorResponse {
	List<BeansAlarmView> site_alarm_list;
	List<BeansAlarmView> GetAlarmStatus_list;
	
	public List<BeansAlarmView> getGetAlarmStatus_list() {
		return GetAlarmStatus_list;
	}
	public void setGetAlarmStatus_list(List<BeansAlarmView> getAlarmStatus_list) {
		GetAlarmStatus_list = getAlarmStatus_list;
	}
	public List<BeansAlarmView> getActiveAlarm_list() {
		return site_alarm_list;
	}
	public void setDemo_list(List<BeansAlarmView> site_alarm_list) {
		this.site_alarm_list = site_alarm_list;
	}
	@Override
	public String toString() {
		return "BeansTicketList [ticket_list=" + site_alarm_list + ", isSuccess()=" + isSuccess() + ", toString()="	+ super.toString() + "]";
	}

}
