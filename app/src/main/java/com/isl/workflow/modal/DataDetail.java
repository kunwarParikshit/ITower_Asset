package com.isl.workflow.modal;

import java.util.List;

public class DataDetail {
    private DataResource resource;
    private ServiceDetail service;
    private String json;
    private String custom;
    private List<Value> values;
    private LocalValue local;
    private JavaScriptExpression expression;

    public JavaScriptExpression getExpression() {
        return expression;
    }

    public void setExpression(JavaScriptExpression expression) {
        this.expression = expression;
    }

    public LocalValue getLocal() {
        return local;
    }

    public void setLocal(LocalValue local) {
        this.local = local;
    }

    public DataResource getResource() {
        return resource;
    }

    public void setResource(DataResource resource) {
        this.resource = resource;
    }

    public ServiceDetail getService() {
        return service;
    }

    public void setService(ServiceDetail url) {
        this.service = service;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }
}
