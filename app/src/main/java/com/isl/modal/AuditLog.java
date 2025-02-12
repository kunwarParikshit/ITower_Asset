package com.isl.modal;

/**
 * Created by dhakan on 6/27/2020.
 */

public class AuditLog {

      String ch_id;
      String change_request_log_date;
      String remarks;
      String created_by;

    public String getCh_id() {
        return ch_id;
    }

    public void setCh_id(String ch_id) {
        this.ch_id = ch_id;
    }

    public String getChange_request_log_date() {
        return change_request_log_date;
    }

    public void setChange_request_log_date(String change_request_log_date) {
        this.change_request_log_date = change_request_log_date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
