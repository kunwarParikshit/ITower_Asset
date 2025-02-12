package com.isl.workflow.modal;

public class FilterParam {
    private FilterParamType type;
    private String column;
    private String field;
    private String valKey;
    private FilterOperatorType oper;
    private String prefix;
    private String suffix;
    private String defVal;

    public String getDefVal() {
        return defVal;
    }

    public void setDefVal(String defVal) {
        this.defVal = defVal;
    }

    public String getValKey() {
        return valKey;
    }

    public void setValKey(String valKey) {
        this.valKey = valKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public FilterOperatorType getOper() {
        return oper;
    }

    public void setOper(FilterOperatorType oper) {
        this.oper = oper;
    }

    public FilterParamType getType() {
        return type;
    }

    public void setType(FilterParamType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
