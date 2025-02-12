package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSerachKeyResponse {
    @SerializedName("searchKeysResponse")
    @Expose
    private SearchKeysResponse searchKeysResponse;

    public SearchKeysResponse getSearchKeysResponse() {
        return searchKeysResponse;
    }

    public void setSearchKeysResponse(SearchKeysResponse searchKeysResponse) {
        this.searchKeysResponse = searchKeysResponse;
    }
}
