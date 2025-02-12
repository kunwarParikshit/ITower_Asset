package com.isl.workflow.modal;

import java.util.List;

public class DataResource {
    private String select;
    private List<FilterParam> filter;
    private List<Parameter> bindVar;
    private String order;
    private String innerselect;

    public String getInnerselect() {
        return innerselect;
    }

    public void setInnerselect(String innerselect) {
        this.innerselect = innerselect;
    }

    public List<Parameter> getBindVar() {
        return bindVar;
    }

    public void setBindVar(List<Parameter> bindVar) {
        this.bindVar = bindVar;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public List<FilterParam> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterParam> filter) {
        this.filter = filter;
    }
}
