package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SerachCylinderRequest {

    @SerializedName("searchCylinders")
    @Expose
    private SearchCylinders searchCylinders;

    public SearchCylinders getSearchCylinders() {
        return searchCylinders;
    }

    public void setSearchCylinders(SearchCylinders searchCylinders) {
        this.searchCylinders = searchCylinders;
    }
}


