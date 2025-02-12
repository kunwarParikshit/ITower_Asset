package com.isl.workflow.modal;

public class Parameter {

    private ParamType type;
    private String field;
    private String valKey;
    private String key;
    private String value;
    private JavaScriptExpression expression;

    public JavaScriptExpression getExpression() {
        return expression;
    }

    public void setExpression(JavaScriptExpression expression) {
        this.expression = expression;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValKey() {
        return valKey;
    }

    public void setValKey(String valKey) {
        this.valKey = valKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
