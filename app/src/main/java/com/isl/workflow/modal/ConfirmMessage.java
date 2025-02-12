package com.isl.workflow.modal;

public class ConfirmMessage {
    private String positiveText;
    private String negativeText;
    private String msg;
    private JavaScriptExpression confirmexp;

    public JavaScriptExpression getConfirmexp() {
        return confirmexp;
    }

    public void setConfirmexp(JavaScriptExpression confirmexp) {
        this.confirmexp = confirmexp;
    }

    public String getPositiveText() {
        return positiveText;
    }

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public String getNegativeText() {
        return negativeText;
    }

    public void setNegativeText(String negativeText) {
        this.negativeText = negativeText;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
