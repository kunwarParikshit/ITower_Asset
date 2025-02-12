package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SerchRequest {
    @SerializedName("searchKeys")
    @Expose
    private SearchKeys searchKeys;

    public SearchKeys getSearchKeys() {
        return searchKeys;
    }

    public void setSearchKeys(SearchKeys searchKeys) {
        this.searchKeys = searchKeys;
    }
}
