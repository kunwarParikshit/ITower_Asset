package com.isl.workflow.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
import com.isl.workflow.cache.FormCacheManager;
import com.isl.workflow.constant.Constants;
import com.isl.workflow.form.control.ToggleControl;
import com.isl.workflow.modal.Fields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    public static final SimpleDateFormat dateFormatterInternal = new SimpleDateFormat(Constants.INTERNAL_DAEFULT_DATETIME_FORMAT, Locale.ENGLISH);
    public static final String DAEFULT_DATETIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";

    public static void dateTimePicker(final Context context, final String fieldId, final TextView valMsg){
        AppPreferences mAppPreferences = new AppPreferences(context);
        final Calendar cldr = Calendar.getInstance();

        Fields field = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(fieldId).getKey());
        String date = FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().getText().toString();
        Calendar inDate = Calendar.getInstance();;

        String dateFormat = Constants.DAEFULT_DATETIME_FORMAT;
        if(field.getValidations()!=null && field.getValidations().getFormat()!=null && field.getValidations().getFormat().length()>0){
            dateFormat = field.getValidations().getFormat();
        }
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat,Locale.ENGLISH);

        if(date.length()>0){
            try {
                inDate.setTime(dateFormatter.parse(date));
            } catch (Exception e) {
                e.printStackTrace();
                inDate.setTime(cldr.getTime());
            }
        }

        Calendar outDate = Calendar.getInstance();
        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        outDate.set(year, monthOfYear, dayOfMonth);

                        try {
                            String convertedDate = dateFormatter.format(outDate.getTime());
                            FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().setText(convertedDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                outDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                outDate.set(Calendar.MINUTE, minute);


                                try {
                                    boolean isFailed = false;
                                    if(field.getValidations()!=null){
                                        if (!field.getValidations().isPastdateallowed()) {
                                            if (outDate.before(cldr)) {
                                                Toast.makeText(context, "Past Timestamp not allowed", Toast.LENGTH_LONG).show();
                                                isFailed = true;
                                            }
                                        } else if(!field.getValidations().isFuturedateallowed()){
                                            if (outDate.after(cldr)) {
                                                Toast.makeText(context, "Future Timestamp not allowed", Toast.LENGTH_LONG).show();
                                                isFailed = true;
                                            }
                                        }
                                    }

                                    if(isFailed){
                                        FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().setText("");
                                        FormCacheManager.getFormControls().get(fieldId).setValue(null);
                                    } else{
                                        FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().setText(dateFormatter.format(outDate.getTime()));
                                        FormCacheManager.getFormControls().get(fieldId).setValue(dateFormatterInternal.format(outDate.getTime()));

                                        if(field.getKey().contains("psdt") && field.getKey().equalsIgnoreCase("psdt")){
                                            mAppPreferences.setPSDT(dateFormatter.format(outDate.getTime()));
                                          }

                                          if(field.getKey().contains("pedt") && field.getKey().equalsIgnoreCase("pedt")){
                                            mAppPreferences.setPEDT(dateFormatter.format(outDate.getTime()));
                                            mAppPreferences.setTimerCycleCamunda(Utils.dateTimeConversion(dateFormatter.format(outDate.getTime()),context));
                                          }

                                        if(FormCacheManager.getFormConfiguration().getFormFields().containsKey( "sitelockid" )
                                           && FormCacheManager.getPrvFormData().containsKey("asdt") && field.getKey().contains("asdt")
                                           && FormCacheManager.getPrvFormData().get("asdt").toString().length()==0
                                           && FormCacheManager.getFormControls()
                                                .get(FormCacheManager.getFormConfiguration().getFormFields()
                                                        .get("sitelockid").getId()).getValue()!=null
                                           && FormCacheManager.getFormControls()
                                                .get(FormCacheManager.getFormConfiguration().getFormFields()
                                                .get("sitelockid").getId()).getValue().toString().length()>0) {

                                            Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get("sitelockid");
                                            ToggleControl toggleControl = new ToggleControl();
                                            toggleControl.keyGenerateSera4(context,formControl.getId(),"true");
                                        }else if(FormCacheManager.getFormConfiguration().getFormFields().containsKey( "sitelockid" )
                                                && FormCacheManager.getPrvFormData().containsKey("aedt") && field.getKey().contains("aedt")
                                                && FormCacheManager.getPrvFormData().get("aedt").toString().length()==0
                                                && FormCacheManager.getFormControls()
                                                .get(FormCacheManager.getFormConfiguration().getFormFields()
                                                        .get("sitelockid").getId()).getValue()!=null
                                                && FormCacheManager.getFormControls()
                                                .get(FormCacheManager.getFormConfiguration().getFormFields()
                                                        .get("sitelockid").getId()).getValue().toString().length()>0) {
                                            Fields formControl = FormCacheManager.getFormConfiguration().getFormFields().get("sitelockid");
                                            ToggleControl toggleControl = new ToggleControl();
                                            toggleControl.keyGenerateSera4(context,formControl.getId(),"false");
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, cldr.get(Calendar.HOUR_OF_DAY), cldr.get(Calendar.MINUTE), true).show();
                    }
                }, inDate.get(Calendar.YEAR), inDate.get(Calendar.MONTH), inDate.get(Calendar.DAY_OF_MONTH));

        if(field.getValidations()!=null && !field.getValidations().isPastdateallowed()){
            picker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        }

        if(field.getValidations()!=null && !field.getValidations().isFuturedateallowed()){
            picker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        }
        picker.show();
    }

    public static void datePicker(final Context context,final String fieldId,final TextView valMsg){

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        Fields field = FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(fieldId).getKey());
        String date=FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().getText().toString();
        Calendar inDate = Calendar.getInstance();

        String dateFormat = Constants.DAEFULT_DATE_FORMAT;
        if(field.getValidations()!=null && field.getValidations().getFormat()!=null && field.getValidations().getFormat().length()>0){
            dateFormat = field.getValidations().getFormat();
        }

        final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat,Locale.ENGLISH);

        if(date.length()>0){
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

            try {
                inDate.setTime(dateFormatter.parse(date));
                day = inDate.DAY_OF_MONTH;
                month = inDate.MONTH;
                year = inDate.YEAR;

            } catch (Exception e) {
                e.printStackTrace();
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);
            }
        }
        Calendar outDate = Calendar.getInstance();

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        outDate.set(year, monthOfYear, dayOfMonth);

                        try {
                            String convertedDate = dateFormatter.format(outDate.getTime());
                            FormCacheManager.getFormControls().get(fieldId).getTextBoxCtrl().setText(convertedDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);

        if(!FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(fieldId).getKey()).getValidations().isPastdateallowed()){
            picker.getDatePicker().setMinDate(cldr.getTimeInMillis());
        }

        if(!FormCacheManager.getFormConfiguration().getFormFields().get(FormCacheManager.getFormControls().get(fieldId).getKey()).getValidations().isFuturedateallowed()){
            picker.getDatePicker().setMaxDate(cldr.getTimeInMillis());
        }
        picker.show();
        //return picker;
    }

    public static void timePicker(final Context context, final EditText et, final TextView valMsg,final boolean is24HoursFormat){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minute = cldr.get(Calendar.MINUTE);
        int second = cldr.get(Calendar.SECOND);
        String time = et.getText().toString().toString();

        if(time.length()>0){
            try {
                String[] arr = new String[5];
                arr = time.split( "\\:" );
                hour = Integer.parseInt( arr[0] );
                minute = Utils.month( arr[1] );
                second = Integer.parseInt( arr[2] );
            }catch (Exception e) {
                hour = cldr.get(Calendar.HOUR_OF_DAY);
                minute = cldr.get(Calendar.MINUTE);
                second = cldr.get(Calendar.SECOND);
            }
        }

        // date picker dialog
        TimePickerDialog picker = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {

                        et.setText( new StringBuilder().append(hour).append(":")
                                        .append(minute).toString());

                    }
                }, hour, minute, is24HoursFormat);
        picker.show();
    }

    public static boolean isFutureDate(Date date) {
        return date.after(new Date());
    }

    public static String currentDateTime(String format){
        try{
            if(format==null){
                format = DAEFULT_DATETIME_FORMAT;
            }
            SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.ENGLISH);
            return formatter.format(Calendar.getInstance().getTime());
        } catch(Exception exp){
            exp.printStackTrace();
            return Utils.CurrentDateTime();
        }

    }

    public static String currentDateTimeFormat(String currentDate){
        String formattedDate ="";
        try {
            Date date1=new SimpleDateFormat("dd.MM.yyyy HH:mm",Locale.ENGLISH).parse(currentDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ",Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0400"));
            formattedDate = dateFormat.format(date1);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Avi "+formattedDate);
        Log.d("Avi ",formattedDate);

        return formattedDate;

    }

/*    public static String currentDateTimeFormata(String date){
        String formattedDate ="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0400"));
        formattedDate = dateFormat.format(date);
        System.out.println("Avi "+formattedDate);
        Log.d("Avi ",formattedDate);

        return formattedDate;

    }*/

    public static String currentDateTimePlusOneDay(String format){
        try{
            if(format==null){
                format = DAEFULT_DATETIME_FORMAT;
            }
            SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.ENGLISH);

            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 1);
            dt = c.getTime();
            return formatter.format(dt);
        } catch(Exception exp){
            exp.printStackTrace();
            return Utils.CurrentDateTime();
        }

    }
}
