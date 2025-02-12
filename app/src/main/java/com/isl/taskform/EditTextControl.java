package com.isl.taskform;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.itower.MyApp;
import com.isl.modal.FormControl;
import com.isl.util.EditTextLength;
import com.isl.util.Utils;
import com.isl.util.UtilsTask;

import java.util.HashMap;

import infozech.itower.R;

public class EditTextControl {

    public static HashMap<String, String> prvValues;
    AppPreferences mAppPreferences = new AppPreferences( MyApp.getAppContext() );

    public EditTextControl(HashMap<String, String> prvValues){
        EditTextControl.prvValues = prvValues;
    }

    public EditText addEditText(final Context context, FormControl formControl) {

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 5, 10, 0 );

        EditText editText = new EditText( context);
        editText.setPadding(7, 0, 0, 0);
        editText.setBackgroundResource( R.drawable.input_box );
        editText.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        editText.setMinimumHeight((int)(35 * scale));
        editText.setTextSize( TypedValue.COMPLEX_UNIT_SP, 15f);
        //keypad open A> Alpha number, F>number decimal, I>number
        if(AppConstants.DATA_TYPE.ALPHANUMERIC.getValue() == formControl.getDataType()){
            editText.setInputType( InputType.TYPE_CLASS_TEXT );
        }else  if(AppConstants.DATA_TYPE.NUMBER.getValue() == formControl.getDataType()){
            editText.setInputType( InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL );
        }else  if(AppConstants.DATA_TYPE.INTEGER.getValue() == formControl.getDataType()){
            editText.setInputType( InputType.TYPE_CLASS_NUMBER );
        }

        //field is redable or writable or hide
        if(AppConstants.FIELD_TYPE.READ_ONLY.getValue() == formControl.getFieldType()) {
            editText.setEnabled( false );
        }else if(AppConstants.FIELD_TYPE.HIDDEN.getValue() == formControl.getFieldType()
              || AppConstants.PARENT_LEVEL.CHILD.getValue()==formControl.getPlevel()) {
            editText.setVisibility( View.GONE );
        }else{
            editText.setEnabled( true );
        }

        if(formControl.getDataLength()!=null) {
            if (formControl.getDataLength().contains( "," )){
                new EditTextLength( editText, formControl.getBefore(), formControl.getAfter());
            }else if(formControl.getDataLength()!=null) {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(formControl.getDataLength()))});
            }
        }

        if(prvValues.containsKey(formControl.getKeyItem())) {
            if (prvValues.get(formControl.getKeyItem()) != null && !prvValues.get(formControl.getKeyItem()).equalsIgnoreCase("null")) {
                editText.setText(prvValues.get(formControl.getKeyItem()));
            } else {
                editText.setText("");
            }
        }

        if(formControl.getKeyItem().equalsIgnoreCase(AppConstants.SITE_ID_ALIAS) || formControl.getKeyItem().equalsIgnoreCase(AppConstants.SLIP_NO_ALIAS)
                || formControl.getKeyItem().equalsIgnoreCase(AppConstants.VEHICLE_NO)){
            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start,
                                           int end, Spanned dest, int dstart, int dend) {

                    for (int i = start;i < end;i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                !Character.toString(source.charAt(i)).equals("_") && !Character.toString( source.charAt(i)).equals("@")
                                && !Character.toString( source.charAt(i)).equals(":")
                                && !Character.toString( source.charAt(i)).equals("-")
                                && !Character.toString( source.charAt(i)).equals("/")
                                && !Character.toString( source.charAt(i)).equals("\\")) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            editText.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(Integer.parseInt( formControl.getDataLength() ) ) });
        }

        if(formControl.getKeyItem().equalsIgnoreCase(AppConstants.REMARKS)){
            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start,
                                           int end, Spanned dest, int dstart, int dend) {

                    for (int i = start;i < end;i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i)) && Character.toString(source.charAt(i)).equals("#"))
                        {
                            return "";
                        }else if (!Character.isLetterOrDigit(source.charAt(i)) && Character.toString(source.charAt(i)).equals("~"))
                        {
                            return "";
                        }else if (!Character.isLetterOrDigit(source.charAt(i)) && Character.toString(source.charAt(i)).equals("&"))
                        {
                            return "";
                        }
                    }
                    return null;
                }
            };
            editText.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(Integer.parseInt( formControl.getDataLength() ) ) });
        }
        setInitializeValue(formControl,editText);
        formControl.setEditText( editText );
        return editText;
    }

    public EditText addDate(final Context context,final FormControl formControl){

        final EditText editText = addEditText(context,formControl);
        editText.setBackgroundResource( R.drawable.icon_calender);
        editText.setFocusableInTouchMode( false );
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsTask.datePicker(context,editText,formControl.getTv());
            }
        });
        return editText;
    }

    public EditText addTime(final Context context,FormControl formControl){
        final EditText editText = addEditText(context,formControl);


        if(AppConstants.FIELD_TYPE.READ_ONLY.getValue() != formControl.getFieldType()
                && AppConstants.FIELD_TYPE.HIDDEN.getValue() != formControl.getFieldType()) {
            editText.setEnabled( true );
            editText.setHint( "HH24:MI:SS" );

            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start,
                                           int end, Spanned dest, int dstart, int dend) {

                    for (int i = start;i < end;i++) {
                        if (!Character.isDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i)).equals(":")) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            editText.setFilters(new InputFilter[]{filter});
        }

        return editText;
    }

    public void setInitializeValue(FormControl formControl,EditText et){
        if(formControl.getInitialize()!=null){
            if(formControl.getInitialize().equalsIgnoreCase(AppConstants.FILLER_ID_KEY)){
                et.setText(mAppPreferences.getUserId());
            }else if(formControl.getInitialize().equalsIgnoreCase( AppConstants.FILLING_DATE_KEY )){
                if (prvValues.containsKey(AppConstants.FILLING_DATE_KEY )){
                    et.setText(prvValues.get(AppConstants.FILLING_DATE_KEY ));
                }else{
                    et.setText( Utils.CurrentDate(0));
                }

            }else if(formControl.getInitialize().equalsIgnoreCase( AppConstants.FILLING_TIME_KEY )){
                et.setText(Utils.CurrentTime());
            }
        }
    }
}
