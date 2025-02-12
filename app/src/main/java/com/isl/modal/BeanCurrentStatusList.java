package com.isl.modal;

//Create for Current Status iMaintain CR#1.5.2.2 in Alarm Management

import java.util.List;

public class BeanCurrentStatusList extends BeansErrorResponse {
	List<BeanCurrentStatus> GetAlarmStatus_list;


	public List<BeanCurrentStatus> getGetAlarmStatus_list() {
		return GetAlarmStatus_list;
	}

	public void setGetAlarmStatus_list(List<BeanCurrentStatus> getAlarmStatus_list) {
		GetAlarmStatus_list = getAlarmStatus_list;
	}
	
}
