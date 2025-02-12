package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchKeys {
    @SerializedName("keySearchArguments")
    @Expose
    private KeySearchArguments keySearchArguments;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public KeySearchArguments getKeySearchArguments() {
        return keySearchArguments;
    }

    public void setKeySearchArguments(KeySearchArguments keySearchArguments) {
        this.keySearchArguments = keySearchArguments;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
