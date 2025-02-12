package com.isl.modal;

import java.util.List;
public class BeanDashboardList extends BeansErrorResponse {
	List<BeanDashBoard> OpenAlarmSummary_list;
	List<BeanDashBoard> Problematic_Site_list;
	List<BeanDashBoard> SiteAvailabilitySummary_list;
	List<BeanDashBoard> EquipmentwiseSummary_list;
	List<BeanDashBoard> Mos_Frequent_Alarms_list;
	
	public List<BeanDashBoard> getOpenAlarmSummary_list() {
		return OpenAlarmSummary_list;
	}
	public void setOpenAlarmSummary_list(List<BeanDashBoard> openAlarmSummary_list) {
		OpenAlarmSummary_list = openAlarmSummary_list;
	}
	public List<BeanDashBoard> getProblematic_Site_list() {
		return Problematic_Site_list;
	}
	public void setProblematic_Site_list(List<BeanDashBoard> problematic_Site_list) {
		Problematic_Site_list = problematic_Site_list;
	}
	public List<BeanDashBoard> getSiteAvailabilitySummary_list() {
		return SiteAvailabilitySummary_list;
	}
	public void setSiteAvailabilitySummary_list(List<BeanDashBoard> siteAvailabilitySummary_list) {
		SiteAvailabilitySummary_list = siteAvailabilitySummary_list;
	}
	public List<BeanDashBoard> getEquipmentwiseSummary_list() {
		return EquipmentwiseSummary_list;
	}
	public void setEquipmentwiseSummary_list(List<BeanDashBoard> equipmentwiseSummary_list) {
		EquipmentwiseSummary_list = equipmentwiseSummary_list;
	}
	public List<BeanDashBoard> getMos_Frequent_Alarms_list() {
		return Mos_Frequent_Alarms_list;
	}
	public void setMos_Frequent_Alarms_list(List<BeanDashBoard> mos_Frequent_Alarms_list) {
		Mos_Frequent_Alarms_list = mos_Frequent_Alarms_list;
	}
	}
