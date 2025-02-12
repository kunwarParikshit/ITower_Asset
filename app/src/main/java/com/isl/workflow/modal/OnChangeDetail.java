package com.isl.workflow.modal;

import java.util.List;

public class OnChangeDetail {
		
	private List<String> refresh;
	private List<String> show;
	private List<String> hide;
	private List<String> enable;
	private List<String> disable;
	private List<String> clear;
	private List<String> validate;
	//private List<String> reset;
	/*private List<ShowHideValues> show;
	private List<ShowHideValues> hide;
	*/
	private ConfirmMessage message;
	private List<ShowHideValues> reset;
	private List<ServiceDetail> servicecall;

	public List<String> getValidate() {
		return validate;
	}

	public void setValidate(List<String> validate) {
		this.validate = validate;
	}

	public List<String> getClear() {
		return clear;
	}

	public void setClear(List<String> clear) {
		this.clear = clear;
	}

	public ConfirmMessage getMessage() {
		return message;
	}

	public void setMessage(ConfirmMessage message) {
		this.message = message;
	}

	public List<ServiceDetail> getServicecall() {
		return servicecall;
	}

	public void setServicecall(List<ServiceDetail> servicecall) {
		this.servicecall = servicecall;
	}

	public List<String> getEnable() {
		return enable;
	}

	public void setEnable(List<String> enable) {
		this.enable = enable;
	}

	public List<String> getDisable() {
		return disable;
	}

	public void setDisable(List<String> disable) {
		this.disable = disable;
	}

	public List<ShowHideValues> getReset() {
		return reset;
	}

	public void setReset(List<ShowHideValues> reset) {
		this.reset = reset;
	}

	public List<String> getHide() {
		return hide;
	}

	public void setHide(List<String> hide) {
		this.hide = hide;
	}

	public List<String> getRefresh() {
		return refresh;
	}
	public void setRefresh(List<String> refresh) {
		this.refresh = refresh;
	}
	public List<String> getShow() {
		return show;
	}
	public void setShow(List<String> show) {
		this.show = show;
	}
}
