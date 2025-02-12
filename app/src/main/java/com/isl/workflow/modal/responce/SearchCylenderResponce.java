package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchCylenderResponce {
    @SerializedName("searchCylindersResponse")
    @Expose
    private SearchCylindersResponse searchCylindersResponse;

    public SearchCylindersResponse getSearchCylindersResponse() {
        return searchCylindersResponse;
    }

    public void setSearchCylindersResponse(SearchCylindersResponse searchCylindersResponse) {
        this.searchCylindersResponse = searchCylindersResponse;
    }
}
