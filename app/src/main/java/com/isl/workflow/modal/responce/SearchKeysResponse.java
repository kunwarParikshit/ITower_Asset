package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchKeysResponse {
    @SerializedName("key")
    @Expose
    private Key key;

    public Key getSKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
