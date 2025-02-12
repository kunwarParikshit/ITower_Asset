package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("identity")
    @Expose
    private String identity;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
