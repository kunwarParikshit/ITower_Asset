package com.isl.workflow.form.control;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.utils.MultiSelectUtils;
import com.isl.workflow.utils.WorkFlowUtils;
import java.util.Calendar;
import infozech.itower.R;

public class MultiSelectControl {
    public static Calendar outDate;

    public MultiSelectControl(){
    }

    public EditText addMultiSelect(final Context context,Fields field){

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 5, 10, 0 );

        EditText editText = new EditText( context);
        editText.setPadding(7, 0, 0, 0);
        editText.setBackgroundResource( R.drawable.doted);
        editText.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        editText.setMinimumHeight((int)(35 * scale));
        editText.setTextSize( TypedValue.COMPLEX_UNIT_SP, 15f);
        editText.setBackgroundResource( R.drawable.doted);
        editText.setFocusableInTouchMode( false );

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectUtils.multiSelectDialog(context,editText,field, FormCacheManager.getFormControls().get(field.getId()).getCaptionCtrl());
            }
        });

        if(FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
            if (FormCacheManager.getPrvFormData().get(field.getKey()) != null && !((String)FormCacheManager.getPrvFormData().get(field.getKey())).equalsIgnoreCase("null")) {
                editText.setText((String)FormCacheManager.getPrvFormData().get(field.getKey()));
            } else {
                editText.setText("");
            }
        }
        FormCacheManager.getFormControls().get(field.getId()).setMultiSelectCtrl(editText);
        WorkFlowUtils.resetEnableControl(context,field);
        WorkFlowUtils.resetShowHideControl(context,field);
        WorkFlowUtils.resetFieldValues(context,field,false);
        return editText;
    }

}
