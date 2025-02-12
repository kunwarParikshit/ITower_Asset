package com.isl.workflow.modal;

public class DropdownValue {
    private String id;
    private String value;
    private String val1;
    private String val2;
    private String val3;
    private String val4;
    private String val5;
    private String val6;
    private String val7;
    private String val8;
    private String val9;
    private String val10;
    private String val11;
    private String val12;
    private String val13;
    private String val14;
    private String val15;
    private String val16;
    private String val17;
    private String val18;
    private String val19;
    private String val20;
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        if(mode!=null && mode.length()>0){
            this.mode = mode;
        }else{
            this.mode = "0";
        }

    }

    public String getVal20() {
        return val20;
    }

    public void setVal20(String val20) {
        this.val20 = val20;
    }

    public String getVal16() {
        return val16;
    }

    public void setVal16(String val16) {
        this.val16 = val16;
    }

    public String getVal17() {
        return val17;
    }

    public void setVal17(String val17) {
        this.val17 = val17;
    }

    public String getVal18() {
        return val18;
    }

    public void setVal18(String val18) {
        this.val18 = val18;
    }

    public String getVal19() {
        return val19;
    }

    public void setVal19(String val19) {
        this.val19 = val19;
    }

    public String getVal11() {
        return val11;
    }

    public void setVal11(String val11) {
        this.val11 = val11;
    }

    public String getVal12() {
        return val12;
    }

    public void setVal12(String val12) {
        this.val12 = val12;
    }

    public String getVal13() {
        return val13;
    }

    public void setVal13(String val13) {
        this.val13 = val13;
    }

    public String getVal14() {
        return val14;
    }

    public void setVal14(String val14) {
        this.val14 = val14;
    }

    public String getVal15() {
        return val15;
    }

    public void setVal15(String val15) {
        this.val15 = val15;
    }

    public String getVal1() {
        return val1;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    public String getVal2() {
        return val2;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    public String getVal3() {
        return val3;
    }

    public void setVal3(String val3) {
        this.val3 = val3;
    }

    public String getVal4() {
        return val4;
    }

    public void setVal4(String val4) {
        this.val4 = val4;
    }

    public String getVal5() {
        return val5;
    }

    public void setVal5(String val5) {
        this.val5 = val5;
    }

    public String getVal6() {
        return val6;
    }

    public void setVal6(String val6) {
        this.val6 = val6;
    }

    public String getVal7() {
        return val7;
    }

    public void setVal7(String val7) {
        this.val7 = val7;
    }

    public String getVal8() {
        return val8;
    }

    public void setVal8(String val8) {
        this.val8 = val8;
    }

    public String getVal9() {
        return val9;
    }

    public void setVal9(String val9) {
        this.val9 = val9;
    }

    public String getVal10() {
        return val10;
    }

    public void setVal10(String val10) {
        this.val10 = val10;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        if(mode!=null && mode.length()>0 && mode.equalsIgnoreCase("1")){
            return value+"("+val14+")";
        }else{
            return value;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DropdownValue){
            DropdownValue d = (DropdownValue )obj;
            if(d.getValue().equals(value) && d.getId()==id ) return true;
        }
        return false;
    }
}
