package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavePersonRequest {
    @SerializedName("savePersons")
    @Expose
    private SavePersons savePersons;

    public SavePersons getSavePersons() {
        return savePersons;
    }

    public void setSavePersons(SavePersons savePersons) {
        this.savePersons = savePersons;
    }
}
