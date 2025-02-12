package com.isl.workflow.modal.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchPersons {
    @SerializedName("personSearchArguments")
    @Expose
    private PersonSearchArguments personSearchArguments;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public PersonSearchArguments getPersonSearchArguments() {
        return personSearchArguments;
    }

    public void setPersonSearchArguments(PersonSearchArguments personSearchArguments) {
        this.personSearchArguments = personSearchArguments;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
