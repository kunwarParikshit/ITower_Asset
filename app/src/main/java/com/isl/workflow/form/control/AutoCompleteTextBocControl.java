package com.isl.workflow.form.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.api.UnsafeHttpClient;
import com.isl.constant.AppConstants;
import com.isl.dao.cache.AppPreferences;
import com.isl.itower.MyApp;
import com.isl.modal.SiteLockResponce;
import com.isl.util.EditTextLength;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.modal.DataSource;
import com.isl.workflow.modal.DropdownValue;
import com.isl.workflow.modal.Fields;
import com.isl.workflow.modal.FormFieldControl;
import com.isl.workflow.utils.DataSubmitUtils;
import com.isl.workflow.utils.WorkFlowUtils;

import infozech.itower.R;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutoCompleteTextBocControl implements OnItemClickListener, OnItemSelectedListener {

    private static Context context;
    AppPreferences mAppPreferences = new AppPreferences(MyApp.getAppContext());

    public AutoCompleteTextBocControl() {
    }

    public AutoCompleteTextView addAutoComplete(Context context, Fields field) {
        this.context = context;
        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins(10, 5, 10, 0);

        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(context);
        autoCompleteTextView.setPadding(7, 0, 0, 0);
        autoCompleteTextView.setBackgroundResource(R.drawable.input_box);
        autoCompleteTextView.setLayoutParams(InputParam);
        final float scale = context.getResources().getDisplayMetrics().density;
        autoCompleteTextView.setMinimumHeight((int) (35 * scale));
        autoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        autoCompleteTextView.setId(Integer.parseInt(field.getId()));

        if (field.getValidations() != null && field.getValidations().getLen() != null) {
            if (field.getValidations().getLen().contains(",")) {
                new EditTextLength(autoCompleteTextView, field.getValidations().getBfrLen(), field.getValidations().getAfrLen());
            } else if (field.getValidations().getLen() != null) {
                autoCompleteTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
            }
        }

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
                autoCompleteTextView.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
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
                autoCompleteTextView.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(Integer.parseInt(field.getValidations().getLen()))});
            }
        }

        autoCompleteTextView.setThreshold(field.getThershold());

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.requestFocus();
                return false;
            }
        });

        FormCacheManager.getFormControls().get(field.getId()).setAutoCompleteCtrl(autoCompleteTextView);
        //WorkFlowUtils.resetFieldValues(context,field,false);
        WorkFlowUtils.resetEnableControl(context, field);
        WorkFlowUtils.resetShowHideControl(context, field);

        if (field.getDataSrc() != null && field.getDataSrc().getValue().toString().equals(DataSource.URL.getValue().toString())) {
            autoCompleteTextView.setAdapter(new APIDataAutoCompleteAdapter(context, R.layout.spinner_dropdown, field.getKey()));
        } else {
            WorkFlowUtils.setFieldValues(context, field, FormCacheManager.getFormControls().get(field.getId()), true);
        }

        //Set Default Values
        if (autoCompleteTextView.getText().toString() == null || autoCompleteTextView.getText().toString().length() == 0) {
            autoCompleteTextView.setText(WorkFlowUtils.getDefaultValue(field));

        }

        autoCompleteTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FormCacheManager.getPrvFormData().containsKey("sid") &&
                        field.getKey().equalsIgnoreCase("sid")) {
                    autoCompleteTextView.setText((String) FormCacheManager.getPrvFormData().get("sid"));


                }
                //autoCompleteTextView.showDropDown();
            }
        }, 10);


        autoCompleteTextView.setOnItemClickListener(new AutoCompleteTextViewClickListener
                (autoCompleteTextView, this, context, field));

        //autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.setOnItemSelectedListener(this);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return autoCompleteTextView;
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onValueSelected(parent, view, position, id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onValueSelected(parent, view, position, id);
    }

    private void onValueSelected(AdapterView<?> parent, View view, int position, long id) {

        Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(String.valueOf(view.getId())).getKey());

        if (!formControl.isDisabled()) {

            DropdownValue selectedValue = (DropdownValue) parent.getItemAtPosition(position);


            FormCacheManager.getFormControls().get(formControl.getId()).getAutoCompleteCtrl()
                    .setText(selectedValue.getValue());
            FormCacheManager.getFormControls().get(formControl.getId()).getAutoCompleteCtrl()
                    .setSelection(selectedValue.getValue().length());

            if (selectedValue.getId().equalsIgnoreCase(AppConstants.DD_SELECT_ID)) {
                return;
            }



            if (formControl.getOnChange() != null && formControl.getOnChange().getMessage() != null) {
                DataSubmitUtils.confirmationMessage(context, formControl.getOnChange(), String.valueOf(view.getId()), selectedValue);

            } else {
                DataSubmitUtils.onChangeTask(context, true, String.valueOf(view.getId()), selectedValue);
            }

        }
    }
}

