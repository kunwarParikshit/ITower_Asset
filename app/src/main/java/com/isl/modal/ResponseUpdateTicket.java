package com.isl.modal;

public class ResponseUpdateTicket {
	String message;
	String success;
	String ticket_id;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTicket_id() {
		return ticket_id;
	}
	public void setTicket_id(String ticket_id) {
		this.ticket_id = ticket_id;
	}
	
    public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}

}
