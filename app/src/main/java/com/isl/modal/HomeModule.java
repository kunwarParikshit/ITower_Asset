package com.isl.modal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeModule {

    private String moduleName;
    private String caption;
    private int moduleId;
    private int moduleImg;
    private Class moduleClass;
    private boolean visible;
    private String baseurl;
    private String shortName;
    private String dataTypeId;
    private Map<String,MenuDetail> subMenuList;

    public HomeModule(String moduleName,int moduleId,int moduleImg,Class moduleClass,boolean visible,String baseurl,String dataTypeIds,String shortName){
        this.moduleName = moduleName;
        this.moduleId = moduleId;
        this.moduleImg = moduleImg;
        this.moduleClass = moduleClass;
        this.visible=visible;
        this.baseurl = baseurl;
        this.dataTypeId = dataTypeId;
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Map<String,MenuDetail> getSubMenuList() {
        if(subMenuList==null){
            subMenuList = new HashMap<String,MenuDetail>();
        }
        return subMenuList;
    }

    public void setSubMenuList(Map<String,MenuDetail> subMenuList) {
        this.subMenuList = subMenuList;
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getModuleId() {
        return moduleId;
    }

    public int getModuleImg() {
        return moduleImg;
    }

    public Class getModuleClass() {
        return moduleClass;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public void setModuleImg(int moduleImg) {
        this.moduleImg = moduleImg;
    }

    public void setModuleClass(Class moduleClass) {
        this.moduleClass = moduleClass;
    }
}