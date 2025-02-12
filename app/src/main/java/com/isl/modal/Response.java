package com.isl.modal;

public class Response {
	String success;
	String message;
	String flag;
	String errorId;
	String ticketId;
	String nexttab;
	String instanceId;
	String oldTktStatus;
	String oassigntogrp;


	public String getOassigntogrp() {
		return oassigntogrp;
	}

	public void setOassigntogrp(String oassigntogrp) {
		this.oassigntogrp = oassigntogrp;
	}

	public String getOldTktStatus() {
		return oldTktStatus;
	}

	public void setOldTktStatus(String oldTktStatus) {
		this.oldTktStatus = oldTktStatus;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getNexttab() {
		return nexttab;
	}

	public void setNexttab(String nexttab) {
		this.nexttab = nexttab;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}
	
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
