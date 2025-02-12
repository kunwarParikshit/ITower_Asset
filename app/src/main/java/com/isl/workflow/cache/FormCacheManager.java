package com.isl.workflow.cache;

import com.isl.dao.cache.AppPreferences;
import com.isl.itower.MyApp;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.modal.FormObject;

import java.util.HashMap;
import java.util.Map;

public enum FormCacheManager {

    FORM_CACHE_MANAGER;

    private FormObject formConfiguration;
    private Map<String, FormFieldControl> formControls;
    //private Map<String,Object> formData;
    private Map<String,Object> prvFormData;
    private boolean confirmationResponse;

    private AppPreferences appPreferences;

    private FormCacheManager(){
        formConfiguration = new FormObject();
        formControls = new HashMap<String, FormFieldControl> ();
        //formData = new HashMap<String, Object>();
        prvFormData = new HashMap<String, Object>();
        appPreferences = new AppPreferences(MyApp.getAppContext());
        confirmationResponse = false;
    }

    public static boolean isConfirmationResponse() {
        return FORM_CACHE_MANAGER.confirmationResponse;
    }

    public static void setConfirmationResponse(boolean confirmationResponse) {
        FORM_CACHE_MANAGER.confirmationResponse = confirmationResponse;
    }

    public static AppPreferences getAppPreferences() {
        return FORM_CACHE_MANAGER.appPreferences;
    }

    public static void setAppPreferences(AppPreferences appPreferences) {
        FORM_CACHE_MANAGER.appPreferences = appPreferences;
    }

    public static Map<String, Object> getPrvFormData() {
        return FORM_CACHE_MANAGER.prvFormData;
    }

    /*public static Map<String, Object> getFormData() {
        return FORM_CACHE_MANAGER.formData;
    }*/

    public static FormObject getFormConfiguration(){
        return FORM_CACHE_MANAGER.formConfiguration;
    }

    public static void setFormConfiguration(FormObject formConfiguration){
        FORM_CACHE_MANAGER.formConfiguration = formConfiguration;
    }

    public static Map<String, FormFieldControl> getFormControls(){
        return FORM_CACHE_MANAGER.formControls;
    }

}
