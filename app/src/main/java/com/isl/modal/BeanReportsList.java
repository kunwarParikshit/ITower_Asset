package com.isl.modal;

import java.util.List;

public class BeanReportsList extends BeansErrorResponse {
	List<BeanReports> Missing_Asset_Details_list;
	List<BeanReports> Site_Audit_Details_list;
	List<BeanReports> Site_Equipment_Audit_Summary;
	
	public List<BeanReports> getMissing_Asset_Details_list() {
	     return Missing_Asset_Details_list;
	}
	public void setMissing_Asset_Details_list(List<BeanReports> missing_Asset_Details_list) {
		 Missing_Asset_Details_list = missing_Asset_Details_list;
	}
	public List<BeanReports> getSite_Audit_Details_list() {
		 return Site_Audit_Details_list;
	}
	public void setSite_Audit_Details_list(List<BeanReports> site_Audit_Details_list) {
		 Site_Audit_Details_list = site_Audit_Details_list;
	}
	public List<BeanReports> getSite_Equipment_Audit_Summary() {
		 return Site_Equipment_Audit_Summary;
	}
	public void setSite_Equipment_Audit_Summary(List<BeanReports> site_Equipment_Audit_Summary) {
		 Site_Equipment_Audit_Summary = site_Equipment_Audit_Summary;
	}
    }
