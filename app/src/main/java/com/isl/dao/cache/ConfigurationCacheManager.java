package com.isl.dao.cache;

import com.isl.modal.FormControl;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public enum ConfigurationCacheManager {

    CONFIGURATION_CACHE_MANAGER;

    private LinkedHashMap<Integer, FormControl> formControlList;
    private FormControl imageControl;
    private JSONObject formData;

    private ConfigurationCacheManager(){
        formControlList = new LinkedHashMap<Integer, FormControl>();
        imageControl = new FormControl();
        formData = new JSONObject();
    }

    public static LinkedHashMap<Integer, FormControl> getFormControlList(){
        return CONFIGURATION_CACHE_MANAGER.formControlList;
    }

    public static FormControl getImageControl(){
        return CONFIGURATION_CACHE_MANAGER.imageControl;
    }

    public static void setImageControl(FormControl imageControl){
        CONFIGURATION_CACHE_MANAGER.imageControl = imageControl;
    }

    public static void setFormData(JSONObject formData){
        CONFIGURATION_CACHE_MANAGER.formData = formData;
    }

    public static JSONObject getFormData(){
        return CONFIGURATION_CACHE_MANAGER.formData;
    }

}
