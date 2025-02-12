package com.isl.workflow.modal.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingUpdates {
    @SerializedName("update")
    @Expose
    private List<Update> update;

    public List<Update> getUpdate() {
        return update;
    }

    public void setUpdate(List<Update> update) {
        this.update = update;
    }
}
