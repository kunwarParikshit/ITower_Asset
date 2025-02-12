package com.isl.workflow.modal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormObject {

    private String key;
    private String name;
    private String title;
    private int id;
    private DataSource dataSrc;
    private DataDetail data;
    private List<Component> components;
    //private ServiceDetail dataSave;
    private Map<String,Fields> formFields;

    public Map<String, Fields> getFormFields() {
        if(formFields==null){
            formFields = new LinkedHashMap<String, Fields>();
        }
        return formFields;
    }

    public void setFormFields(Map<String, Fields> formFields) {
        this.formFields = formFields;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.key = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DataSource getDataSrc() {
        return dataSrc;
    }

    public void setDataSrc(DataSource dataSrc) {
        this.dataSrc = dataSrc;
    }

    public DataDetail getData() {
        return data;
    }

    public void setData(DataDetail data) {
        this.data = data;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    /*
    public ServiceDetail getDataSave() {
        return dataSave;
    }

    public void setDataSave(ServiceDetail dataSave) {
        this.dataSave = dataSave;
    }*/
}
