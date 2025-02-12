package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("firstResult")
    @Expose
    private String firstResult;
    @SerializedName("maxResults")
    @Expose
    private String maxResults;

    public String getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(String firstResult) {
        this.firstResult = firstResult;
    }

    public String getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(String maxResults) {
        this.maxResults = maxResults;
    }
}
