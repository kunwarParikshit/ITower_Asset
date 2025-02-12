package com.isl.workflow.form.control;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.isl.util.FontUtils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.utils.UIUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import infozech.itower.R;

public class CheckBoxControl {

    public void CheckBoxControl(){}

    public CheckBox addCheckBox(Context context, Fields field){

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 25, 10, 0 );

        CheckBox checkBox = new CheckBox( context);
        checkBox.setText(field.getTitle());
        checkBox.setId(Integer.parseInt(field.getId()));
        checkBox.setTag(field.getKey());

        UIUtils.checkBoxUI(context,checkBox);

        FormCacheManager.getFormControls().get(field.getId()).setCheckBoxControl(checkBox);

        WorkFlowUtils.resetFieldValues(context,field,false);
        WorkFlowUtils.resetEnableControl(context,field);
        WorkFlowUtils.resetShowHideControl(context,field);

        return checkBox;

    }
}
