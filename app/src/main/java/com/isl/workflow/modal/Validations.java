package com.isl.workflow.modal;

import java.util.List;

public class Validations {

    private boolean required = false;
    private JavaScriptExpression requiredexp;
    private List<JavaScriptExpression> validateexp;
    private int min;
    private int max;
    private String len;
    private boolean pastdateallowed = true;
    private boolean futuredateallowed = true;
    private String format;
    private List<Character> allowChr;
    private List<Character> blockChr;
    private int bfrLen;
    private int afrLen;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isPastdateallowed() {
        return pastdateallowed;
    }

    public void setPastdateallowed(boolean pastdateallowed) {
        this.pastdateallowed = pastdateallowed;
    }

    public boolean isFuturedateallowed() {
        return futuredateallowed;
    }

    public void setFuturedateallowed(boolean futuredateallowed) {
        this.futuredateallowed = futuredateallowed;
    }

    public JavaScriptExpression getRequiredexp() {
        return requiredexp;
    }

    public void setRequiredexp(JavaScriptExpression requiredexp) {
        this.requiredexp = requiredexp;
    }

    public List<Character> getAllowChr() {
        return allowChr;
    }

    public void setAllowChr(List<Character> allowChr) {
        this.allowChr = allowChr;
    }

    public List<Character> getBlockChr() {
        return blockChr;
    }

    public void setBlockChr(List<Character> blockChr) {
        this.blockChr = blockChr;
    }

    public int getBfrLen() {
        return bfrLen;
    }

    public void setBfrLen(int bfrLen) {
        this.bfrLen = bfrLen;
    }

    public int getAfrLen() {
        return afrLen;
    }

    public void setAfrLen(int afrLen) {
        this.afrLen = afrLen;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<JavaScriptExpression> getValidateexp() {
        return validateexp;
    }

    public void setValidateexp(List<JavaScriptExpression> validateexp) {
        this.validateexp = validateexp;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

}
