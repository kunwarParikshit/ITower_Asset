package com.isl.workflow.form.control;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.isl.util.EditTextLength;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.utils.DateTimeUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import java.util.Calendar;

import infozech.itower.R;

public class TextBoxControl {

    public static Calendar outDate;
    private static Context context;
    //AppPreferences mAppPreferences = new AppPreferences( MyApp.getAppContext() );

    //public TextBoxControl(HashMap<String, String> prvValues){}

    public TextBoxControl() {
    }

    public EditText addTextBoxControl(final Context context, Fields field) {
        this.context = context;
        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins(10, 5, 10, 0);

        EditText editText = new EditText(context);
        editText.setPadding(7, 0, 0, 0);
        editText.setBackgroundResource(R.drawable.input_box);
        editText.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        editText.setMinimumHeight((int) (35 * scale));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);

        switch (field.getType()) {
            case FLOAT:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case INTEGER:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case STRING:
                //editText.setInputType( InputType.TYPE_CLASS_TEXT );
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                break;
            default:
                break;
        }
        /*
        if(field.isHidden()){
            editText.setVisibility( View.GONE );
        } else {
            editText.setEnabled(!field.isDisabled());
        }
        */

        if (field.getValidations() != null && field.getValidations().getLen() != null) {
            if (field.getValidations().getLen().contains(",")) {
                new EditTextLength(editText, field.getValidations().getBfrLen(), field.getValidations().getAfrLen());
            } else if (field.getValidations().getLen() != null) {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
            }
        }
        /*
        if(FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
            if (FormCacheManager.getPrvFormData().get(field.getKey()) != null && !((String)FormCacheManager.getPrvFormData().get(field.getKey())).equalsIgnoreCase("null")) {
                editText.setText((String)FormCacheManager.getPrvFormData().get(field.getKey()));
            } else {
                editText.setText("");
            }
        }
        */
        if (field.getValidations() != null) {
            //Set the special chars allowed in textbox
            if (field.getValidations().getAllowChr() != null && field.getValidations().getAllowChr().size() > 0) {

                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                    !field.getValidations().getAllowChr().contains(source.charAt(i))) {
                                return "";
                            }
                        }
                        return null;
                    }
                };
                editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
            }

            //Set the special chars blocked in textbox
            if (field.getValidations().getBlockChr() != null && field.getValidations().getBlockChr().size() > 0) {

                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                    field.getValidations().getBlockChr().contains(source.charAt(i))) {
                                return "";
                            }
                        }
                        return null;
                    }
                };
                editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
            }
        }


        //Set Default Values
        /*
        if(editText.getText().toString()==null ||editText.getText().toString().length()==0){
            editText.setText(WorkFlowUtils.getDefaultValue(field));
        }*/
        FormCacheManager.getFormControls().get(field.getId()).setTextBoxCtrl(editText);
        WorkFlowUtils.resetFieldValues(context, field, false);
        WorkFlowUtils.resetEnableControl(context, field);
        WorkFlowUtils.resetShowHideControl(context, field);

        return editText;
    }

    public EditText addDate(final Context context, Fields field) {

        final EditText editText = addTextBoxControl(context, field);
        editText.setBackgroundResource(R.drawable.icon_calender);
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeUtils.datePicker(context, field.getId(), FormCacheManager.getFormControls().get(field.getId()).getCaptionCtrl());
            }
        });
        return editText;
    }

    public EditText addDateTime(final Context context, Fields field) {

        final EditText editText = addTextBoxControl(context, field);
        editText.setBackgroundResource(R.drawable.icon_calender);
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String formName = (String) FormCacheManager.getPrvFormData().get(Constants.FORM_KEY);
                Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get(field.getKey());





                if (formName.equalsIgnoreCase("AccessRequesttoc") &&
                        field.getKey().equalsIgnoreCase("asdt")) {

                    String key = "";
                    int isVisible = 0;
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("keySerNo")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("keySerNo");
                        key = FormCacheManager.getFormControls().get(formControl3.getId()).getTextBoxCtrl().getText().toString();
                    }
                    boolean KeyEditable = false;
                    if (FormCacheManager.getFormConfiguration().getFormFields().containsKey("validateKeySer")) {
                        Fields formControl3 = FormCacheManager.getFormConfiguration().getFormFields().get("validateKeySer");
                        KeyEditable = FormCacheManager.getFormControls().get(formControl3.getId()).getButtonCtrl().isEnabled();
                        isVisible= FormCacheManager.getFormControls().get(formControl3.getId()).getButtonCtrl().getVisibility();

                    }
                    if ((key.isEmpty() || KeyEditable) && isVisible ==0) {
                        Utils.toastMsg(context,Utils.msg(context,"852"));
                        // Toast.makeText(context, "Please Enter Key Serial First and Validate key", Toast.LENGTH_SHORT).show();
                        return;

                    }else {
                        DateTimeUtils.dateTimePicker(context, field.getId(), FormCacheManager.getFormControls().get(field.getId()).getCaptionCtrl());
                    }
                } else {

                    DateTimeUtils.dateTimePicker(context, field.getId(), FormCacheManager.getFormControls().get(field.getId()).getCaptionCtrl());
                }
            }
        });

        if (FormCacheManager.getPrvFormData().containsKey(field.getKey())) {
            if (FormCacheManager.getPrvFormData().get(field.getKey()) != null && !((String) FormCacheManager.getPrvFormData().get(field.getKey())).equalsIgnoreCase("null")) {
                editText.setText((String) FormCacheManager.getPrvFormData().get(field.getKey()));
            } else {
                editText.setText("");
            }
        }
        return editText;
    }

    public EditText addTime(final Context context, Fields field) {
        final EditText editText = addTextBoxControl(context, field);

        if (!field.isDisabled() && !field.isHidden()) {
            editText.setBackgroundResource(R.drawable.icon_calender);
            editText.setEnabled(true);
            editText.setHint("HH24:MI");
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimeUtils.timePicker(context, editText, FormCacheManager.getFormControls().get(field.getId()).getCaptionCtrl(), true);
                }
            });

            //editText.setInputType( InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME );
        }

        return editText;
    }

}
