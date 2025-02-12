package com.isl.workflow.form.control;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.utils.WorkFlowUtils;

import java.util.List;

public class AutoCompleteTextViewClickListener implements OnItemClickListener {
    Context context;
    Fields field;
    AutoCompleteTextView mAutoComplete;
    OnItemClickListener mOriginalListener;

    public AutoCompleteTextViewClickListener(AutoCompleteTextView acTextView, OnItemClickListener originalListener, Context context, Fields field) {
        mAutoComplete = acTextView;
        mOriginalListener = originalListener;
        this.context = context;
        this.field = field;
    }

    public void onItemClick(AdapterView<?> adView, View view, int position,long id) {
        //Toast.makeText(context,"hhhhh",Toast.LENGTH_LONG).show();
        mOriginalListener.onItemClick(adView, mAutoComplete, position, id);
        /*List<String> showList = field.getOnChange().getShow();
        Fields field = FormCacheManager.getFormConfiguration().getFormFields().get(showList.get(0));
        FormFieldControl formControl = FormCacheManager.getFormControls().get(field.getId());
        formControl.getCaptionCtrl().setVisibility( View.VISIBLE );
        formControl.getMultiSelectCtrl().setVisibility( View.VISIBLE );*/

    }
}