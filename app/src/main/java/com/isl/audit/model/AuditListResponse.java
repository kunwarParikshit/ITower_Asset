package com.isl.audit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuditListResponse {
    @SerializedName("DataGrid")
    @Expose
    private List<AuditListResult> dataGrid = null;

    public List<AuditListResult> getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(List<AuditListResult> dataGrid) {
        this.dataGrid = dataGrid;
    }
}
