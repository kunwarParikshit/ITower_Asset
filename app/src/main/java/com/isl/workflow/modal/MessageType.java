package com.isl.workflow.modal;

import com.google.gson.annotations.SerializedName;

public enum MessageType {

    @SerializedName("confirm")CONFIRM("confirm"),
    @SerializedName("normal")NORMAL("normal");

    private String value;

    MessageType(String value) {
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
