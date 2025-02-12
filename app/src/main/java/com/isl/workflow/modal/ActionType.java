package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum ActionType {

    @SerializedName("new")NEW("new"),
    @SerializedName("new-submit")NEWSUBMIT("new-submit"),
    @SerializedName("modification")MODIFICATION("modification"),
    @SerializedName("modification-submit")MODIFICATIONSUBMIT("modification-submit"),
    @SerializedName("none")NONE("none");

    private String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
