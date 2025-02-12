package com.isl.modal;

/**
 * Created by dhakan on 6/24/2020.
 */

public class AccessManagementTicket {
    String proc_inst_id_;
    String cid;
    String sid;
    String ptp;
    String rsts;
    String requesterdate;


    public String getRequesterdate() {
        return requesterdate;
    }

    public void setRequesterdate(String requesterdate) {
        this.requesterdate = requesterdate;
    }

    public String getProc_inst_id_() {
        return proc_inst_id_;
    }

    public void setProc_inst_id_(String proc_inst_id_) {
        this.proc_inst_id_ = proc_inst_id_;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPtp() {
        return ptp;
    }

    public void setPtp(String ptp) {
        this.ptp = ptp;
    }

    public String getRsts() {
        return rsts;
    }

    public void setRsts(String rsts) {
        this.rsts = rsts;
    }
}
