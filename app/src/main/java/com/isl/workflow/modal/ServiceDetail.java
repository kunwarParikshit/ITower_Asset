package com.isl.workflow.modal;

import java.util.List;

public class ServiceDetail {

    private String url;
    private ActionType action = ActionType.NONE;
    private boolean validateFormdata = false;
    private List<String> validateParameters;
    private boolean genrateAuditTrail= false;
    private boolean saveLocalInFailure= false;
    private boolean onSuccessCloseForm= false;
    private boolean showMessage = false;
    private String msg;
    private JavaScriptExpression msgexp;
    private JavaScriptExpression expression;
    private RequestMethod request;
    private List<Parameter> params;

    public JavaScriptExpression getMsgexp() {
        return msgexp;
    }

    public void setMsgexp(JavaScriptExpression msgexp) {
        this.msgexp = msgexp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOnSuccessCloseForm() {
        return onSuccessCloseForm;
    }

    public void setOnSuccessCloseForm(boolean onSuccessCloseForm) {
        this.onSuccessCloseForm = onSuccessCloseForm;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public boolean isSaveLocalInFailure() {
        return saveLocalInFailure;
    }

    public void setSaveLocalInFailure(boolean saveLocalInFailure) {
        this.saveLocalInFailure = saveLocalInFailure;
    }

    public boolean isGenrateAuditTrail() {
        return genrateAuditTrail;
    }

    public void setGenrateAuditTrail(boolean genrateAuditTrail) {
        this.genrateAuditTrail = genrateAuditTrail;
    }

    public List<String> getValidateParameters() {
        return validateParameters;
    }

    public void setValidateParameters(List<String> validateParameters) {
        this.validateParameters = validateParameters;
    }

    public boolean isValidateFormdata() {
        return validateFormdata;
    }

    public void setValidateFormdata(boolean validateFormdata) {
        this.validateFormdata = validateFormdata;
    }

    public JavaScriptExpression getExpression() {
        return expression;
    }

    public void setExpression(JavaScriptExpression expression) {
        this.expression = expression;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod getRequest() {
        return request;
    }

    public void setRequest(RequestMethod request) {
        this.request = request;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }
}