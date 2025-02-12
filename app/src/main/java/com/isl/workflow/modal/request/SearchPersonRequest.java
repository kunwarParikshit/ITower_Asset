package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchPersonRequest {
    @SerializedName("searchPersons")
    @Expose
    private SearchPersons searchPersons;

    public SearchPersons getSearchPersons() {
        return searchPersons;
    }

    public void setSearchPersons(SearchPersons searchPersons) {
        this.searchPersons = searchPersons;
    }
}
