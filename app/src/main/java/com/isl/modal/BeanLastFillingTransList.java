package com.isl.modal;

import java.util.List;

public class BeanLastFillingTransList extends BeansErrorResponse {
	List<BeanLastFillingTrans> ticket_list;

	public List<BeanLastFillingTrans> getFillingReportList() {
		return ticket_list;
	}

	public void setFillingReportList(List<BeanLastFillingTrans> ticket_list) {
		this.ticket_list = ticket_list;
	}
}
