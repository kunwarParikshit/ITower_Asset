package com.isl.modal;

import java.util.ArrayList;

/**
 * Created by Manoj Yadav on 17-Nov-2018
 */

public class FieldList {
    public ArrayList<Field> fields;

    public ArrayList<Field> getFieldList() {
        if(fields==null){
            fields=new ArrayList<Field>();
        }
        return fields;
    }

    public void setFieldList(ArrayList<Field> fields) {
        this.fields = fields;
    }
}
