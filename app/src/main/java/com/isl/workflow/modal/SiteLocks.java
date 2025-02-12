package com.isl.workflow.modal;

public class SiteLocks {
    String site_id;
    String lock_id;
    String lock_name;
    String Flag;
    String ClientId;
    String Msg;


    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getLock_id() {
        return lock_id;
    }

    public void setLock_id(String lock_id) {
        this.lock_id = lock_id;
    }

    public String getLock_name() {
        return lock_name;
    }

    public void setLock_name(String lock_name) {
        this.lock_name = lock_name;
    }

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
