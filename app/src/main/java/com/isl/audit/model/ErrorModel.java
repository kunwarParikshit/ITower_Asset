package com.isl.audit.model;

import java.util.HashMap;

public class ErrorModel {
    private String flag;
    private String message;

    public HashMap<String, String> getInValidQRCodes() {
        return inValidQRCodes;
    }

    public void setInValidQRCodes(HashMap<String, String> inValidQRCodes) {
        this.inValidQRCodes = inValidQRCodes;
    }

    private HashMap<String, String> inValidQRCodes;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
