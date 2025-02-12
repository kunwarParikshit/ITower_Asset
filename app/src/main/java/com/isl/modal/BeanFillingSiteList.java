package com.isl.modal;

import java.util.List;

public class BeanFillingSiteList extends BeansErrorResponse {
	List<BeansSiteFillingView> site_list;
	
	public List<BeansSiteFillingView> getSite_list() {
		return site_list;
	}
	public void setSite_list(List<BeansSiteFillingView> site_list) {
		this.site_list = site_list;
	}
    }
