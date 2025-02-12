package com.isl.workflow.modal;

import java.util.List;

public class RequestHistory {

    private List<AuditTrail> auditTrail;
    private List<AssigenmentHistory> assigenmentHistory;

    public List<AuditTrail> getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(List<AuditTrail> auditTrail) {
        this.auditTrail = auditTrail;
    }

    public List<AssigenmentHistory> getAssigenmentHistory() {
        return assigenmentHistory;
    }

    public void setAssigenmentHistory(List<AssigenmentHistory> assigenmentHistory) {
        this.assigenmentHistory = assigenmentHistory;
    }


}
