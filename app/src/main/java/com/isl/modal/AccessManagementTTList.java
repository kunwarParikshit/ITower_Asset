package com.isl.modal;

import java.util.List;

/**
 * Created by dhakan on 6/24/2020.
 */

public class AccessManagementTTList  {
    List<AccessManagementTicket> ticketList;
    List<AuditLog> auditLogList;
    List<AssignHistory> assignHisList;


    public List<AccessManagementTicket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<AccessManagementTicket> ticketList) {
        this.ticketList = ticketList;
    }


    public List<AuditLog> getAuditLogList() {
        return auditLogList;
    }

    public void setAuditLogList(List<AuditLog> auditLogList) {
        this.auditLogList = auditLogList;
    }

    public List<AssignHistory> getAssignHisList() {
        return assignHisList;
    }

    public void setAssignHisList(List<AssignHistory> assignHisList) {
        this.assignHisList = assignHisList;
    }
}
