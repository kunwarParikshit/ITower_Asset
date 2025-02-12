package com.isl.workflow.modal;

import java.util.List;

public class JavaScriptExpression {
    private String expression;
    private String function;
    private String msg;
    private String msgId;
    private JavaScriptExpression msgexp;
    private List<Parameter> field;

    public JavaScriptExpression getMsgexp() {
        return msgexp;
    }

    public void setMsgexp(JavaScriptExpression msgexp) {
        this.msgexp = msgexp;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Parameter> getField() {
        return field;
    }

    public void setField(List<Parameter> field) {
        this.field = field;
    }
}
