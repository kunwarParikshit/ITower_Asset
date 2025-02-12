package com.isl.workflow.modal;

import java.util.List;

public class Fields {

    private String title;
    private String ontitle;
    private String offtitle;
    private String id;
    private String key;
    private ComponentType type;
    private boolean hidden = false;
    private boolean disabled = false;
    private boolean persistent = true;
    private boolean multiple = false;
    private boolean searchEnabled = false;
    private boolean clearOnRefresh = false;
    private boolean auditTrail = true;
    private boolean seperateAuditTrail = false;
    private int thershold;
    private String customClass;
    private CustomRule rule;
    private List<Parameter> persistentVal;
    private Validations validations;
    private DataSource dataSrc;
    private DataDetail data;
    private String idProperty;
    private String valProperty;
    private DefaultValue defaultValue;
    private OnChangeDetail onChange;
    private OnChangeDetail onClick;
    private List<String> imgTag;
    private List<String> mediaFormate;
    private String idFieldName;

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public boolean isSeperateAuditTrail() {
        return seperateAuditTrail;
    }

    public void setSeperateAuditTrail(boolean seperateAuditTrail) {
        this.seperateAuditTrail = seperateAuditTrail;
    }

    public String getOntitle() {
        return ontitle;
    }

    public void setOntitle(String ontitle) {
        this.ontitle = ontitle;
    }

    public String getOfftitle() {
        return offtitle;
    }

    public void setOfftitle(String offtitle) {
        this.offtitle = offtitle;
    }

    public boolean isAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(boolean auditTrail) {
        this.auditTrail = auditTrail;
    }

    public List<Parameter> getPersistentVal() {
        return persistentVal;
    }

    public void setPersistentVal(List<Parameter> persistentVal) {
        this.persistentVal = persistentVal;
    }

    public CustomRule getRule() {
        return rule;
    }

    public void setRule(CustomRule rule) {
        this.rule = rule;
    }

    public OnChangeDetail getOnClick() {
        return onClick;
    }

    public void setOnClick(OnChangeDetail onClick) {
        this.onClick = onClick;
    }

    public int getThershold() {
        return thershold;
    }

    public void setThershold(int thershold) {
        this.thershold = thershold;
    }

    public List<String> getImgTag() {
        return imgTag;
    }

    public void setImgTag(List<String> imgTag) {
        this.imgTag = imgTag;
    }

    public List<String> getMediaFormate() {
        return mediaFormate;
    }

    public void setMediaFormate(List<String> mediaFormate) {
        this.mediaFormate = mediaFormate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
    }

    public boolean isClearOnRefresh() {
        return clearOnRefresh;
    }

    public void setClearOnRefresh(boolean clearOnRefresh) {
        this.clearOnRefresh = clearOnRefresh;
    }

    public String getCustomClass() {
        return customClass;
    }

    public void setCustomClass(String customClass) {
        this.customClass = customClass;
    }

    public Validations getValidations() {
        return validations;
    }

    public void setValidations(Validations validations) {
        this.validations = validations;
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

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    public String getValProperty() {
        return valProperty;
    }

    public void setValProperty(String valProperty) {
        this.valProperty = valProperty;
    }

    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(DefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public OnChangeDetail getOnChange() {
        return onChange;
    }

    public void setOnChange(OnChangeDetail onChange) {
        this.onChange = onChange;
    }

}
