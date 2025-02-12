package com.isl.modal;
import java.util.List;
public class ResponseRemarks extends BeansErrorResponse {
	List<BeanRemarks> remarks;

	public List<BeanRemarks> getRemarks() {
		return remarks;
	}

	public void setRemarks(List<BeanRemarks> remarks) {
		this.remarks = remarks;
	}
}
