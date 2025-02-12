package com.isl.taskform;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.isl.constant.AppConstants;
import com.isl.modal.FormControl;
import com.isl.util.EditTextLength;

import java.util.HashMap;

import infozech.itower.R;

/**
 * Created by dhakan on 11/26/2018.
 */

public class AutoCompleteTVControl{
    public static HashMap<String, String> prvValues;
    Context context;
    AutoCompleteTextView autoCompleteTextView;
    public AutoCompleteTVControl(HashMap<String, String> prvValues){
        AutoCompleteTVControl.prvValues = prvValues;
    }

    public AutoCompleteTextView addAutoComplete(Context context, FormControl formControl) {
        this.context=context;
        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 5, 10, 0 );

        autoCompleteTextView = new AutoCompleteTextView( context);
        autoCompleteTextView.setPadding(7, 0, 0, 0);
        autoCompleteTextView.setBackgroundResource( R.drawable.input_box );
        autoCompleteTextView.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        autoCompleteTextView.setMinimumHeight((int)(35 * scale));
        autoCompleteTextView.setTextSize( TypedValue.COMPLEX_UNIT_SP, 15f);

       //field is redable or writable or hide
        if(AppConstants.FIELD_TYPE.READ_ONLY.getValue() == formControl.getFieldType()) {
            autoCompleteTextView.setEnabled( false );
        }else if(AppConstants.FIELD_TYPE.HIDDEN.getValue() == formControl.getFieldType()
                || AppConstants.PARENT_LEVEL.CHILD.getValue()==formControl.getPlevel()) {
            autoCompleteTextView.setVisibility( View.GONE );
        }else{
            autoCompleteTextView.setEnabled( true );
        }

        if(formControl.getDataLength()!=null) {
            if (formControl.getDataLength().contains( "," )){
                new EditTextLength( autoCompleteTextView, formControl.getBefore(), formControl.getAfter());
            }else if(formControl.getDataLength()!=null) {
                autoCompleteTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(formControl.getDataLength()))});
            }
        }

        if(prvValues.containsKey(formControl.getKeyItem())) {
            if (prvValues.get(formControl.getKeyItem()) != null && !prvValues.get(formControl.getKeyItem()).equalsIgnoreCase("null")) {
                autoCompleteTextView.setText(prvValues.get(formControl.getKeyItem()));
            } else {
                autoCompleteTextView.setText("");
            }
        }

        if(formControl.getKeyItem().equalsIgnoreCase( AppConstants.SITE_ID_ALIAS )){
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
            autoCompleteTextView.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(Integer.parseInt( formControl.getDataLength() ) ) });
           // autoCompleteTextView.setFilters(new InputFilter[] { new GetSiteIdInputFilter(),new InputFilter.LengthFilter(Integer.parseInt( formControl.getDataLength() ))});

        }

        autoCompleteTextView.setThreshold( 1 );
        autoCompleteTextView.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.requestFocus();
                return false;
            }
        } );

        formControl.setAutoCompleteTextView(autoCompleteTextView );
        return autoCompleteTextView;
    }

}
