package com.isl.modal;

public class BeansRaiseTicket {
	String userId;
	String current_time;
	String equipment;
	String alarm_type;
	String site_id;
	String alarm_short_description;
	String alarm_details;
	String ticket_log_time;
	String ticket_logged_by;
	String ticket_type;
	String problem_description;
	String user_assigned_flag;
	String user_assigned_to;
	String problem_start_date_time;
	String ref_ticket_id;
	String dependent_site;  // 0.1
	String operator;        // 0.1
	 
	
	// 0.1
	public String getDependent_site() {
		return dependent_site;
	}
	public void setDependent_site(String dependent_site) {
		this.dependent_site = dependent_site;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	//end 0.1
	public String getRef_ticket_id() {
		return ref_ticket_id;
	}
	public void setRef_ticket_id(String ref_ticket_id) {
		this.ref_ticket_id = ref_ticket_id;
	}

	public String getProblem_start_date_time() {
		return problem_start_date_time;
	}
	public void setProblem_start_date_time(String problem_start_date_time) {
		this.problem_start_date_time = problem_start_date_time;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCurrent_time() {
		return current_time;
	}
	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}

	public String getEquipment() {
		return equipment;
	}
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getAlarm_type() {
		return alarm_type;
	}
	public void setAlarm_type(String alarm_type) {
		this.alarm_type = alarm_type;
	}

	public String getSite_id() {
		return site_id;
	}
	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}

	public String getAlarm_short_description() {
		return alarm_short_description;
	}
	public void setAlarm_short_description(String alarm_short_description) {
		this.alarm_short_description = alarm_short_description;
	}

	public String getAlarm_details() {
		return alarm_details;
	}
	public void setAlarm_details(String alarm_details) {
		this.alarm_details = alarm_details;
	}

	public String getTicket_log_time() {
		return ticket_log_time;
	}
	public void setTicket_log_time(String ticket_log_time) {
		this.ticket_log_time = ticket_log_time;
	}

	public String getTicket_logged_by() {
		return ticket_logged_by;
	}
	public void setTicket_logged_by(String ticket_logged_by) {
		this.ticket_logged_by = ticket_logged_by;
	}

	public String getTicket_type() {
		return ticket_type;
	}
	public void setTicket_type(String ticket_type) {
		this.ticket_type = ticket_type;
	}

	public String getProblem_description() {
		return problem_description;
	}
	public void setProblem_description(String problem_description) {
		this.problem_description = problem_description;
	}

	public String getUser_assigned_flag() {
		return user_assigned_flag;
	}
	public void setUser_assigned_flag(String user_assigned_flag) {
		this.user_assigned_flag = user_assigned_flag;
	}

	public String getUser_assigned_to() {
		return user_assigned_to;
	}
	public void setUser_assigned_to(String user_assigned_to) {
		this.user_assigned_to = user_assigned_to;
	}

	@Override
	public String toString() {
		return "BeansRaiseTicket [userId=" + userId + ", current_time="
				+ current_time + ", equipment=" + equipment + ", alarm_type="
				+ alarm_type + ", site_id=" + site_id
				+ ", alarm_short_description=" + alarm_short_description
				+ ", alarm_details=" + alarm_details + ", ticket_log_time="
				+ ticket_log_time + ", ticket_logged_by=" + ticket_logged_by
				+ ", ticket_type=" + ticket_type + ", problem_description="
				+ problem_description + ", user_assigned_flag="
				+ user_assigned_flag + ", user_assigned_to=" + user_assigned_to
				+ ", toString()=" + super.toString() + "]";
	}

}
