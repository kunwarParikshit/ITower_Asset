package com.isl.util;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import com.isl.util.Utils;

/**
 * Created by dhakan on 9/13/2018.
 */
public class UtilsTask {

    public static boolean ddFlag = false;  //This flag is used for digital dispatch only for assigned ticket for ticket detail fragment


    public static void datePicker(final Context contect,final EditText et,final TextView valMsg){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String date=et.getText().toString().toString();


        if(date.length()>0){
            try {
                String[] arr = new String[5];
                arr = date.split( "\\-" );
                day = Integer.parseInt( arr[0] );
                month = Utils.month( arr[1] );
                year = Integer.parseInt( arr[2] );
            }catch (Exception e) {
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);
            }
        }

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(contect,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date d = Utils.convertStringToDate(
                                new StringBuilder().append(monthOfYear + 1).append("/")
                                        .append(dayOfMonth).append("/").append(year).toString(),
                                "MM/dd/yyyy");
                        if (Utils.checkValidation(d)) {
                            et.setText( Utils.changeDateFormat(
                                    new StringBuilder().append(monthOfYear + 1).append("/")
                                            .append(dayOfMonth).append("/").append(year)
                                            .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));
                        }else{
                            Utils.toastMsg(contect, valMsg.getText().toString()+" "+"cannot be greater than Current Date");
                            //From Date cannot be greater than Current Date;
                        }
                    }
                }, year, month, day);
        picker.show();
        // return picker;
    }

    //added by Avdhesh
    public static void datePickerForPM(final Context contect,final EditText et,final TextView valMsg){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String date=et.getText().toString().toString();


        if(date.length()>0){
            try {
                String[] arr = new String[5];
                arr = date.split( "\\-" );
                day = Integer.parseInt( arr[0] );
                month = Utils.month( arr[1] );
                year = Integer.parseInt( arr[2] );
            }catch (Exception e) {
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);
            }
        }

        // date picker dialog
        DatePickerDialog picker = new DatePickerDialog(contect,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date d = Utils.convertStringToDate(
                                new StringBuilder().append(monthOfYear + 1).append("/")
                                        .append(dayOfMonth).append("/").append(year).toString(),
                                "MM/dd/yyyy");
                        et.setText( Utils.changeDateFormat(
                                new StringBuilder().append(monthOfYear + 1).append("/")
                                        .append(dayOfMonth).append("/").append(year)
                                       .toString(), "MM/dd/yyyy", "dd-MMM-yyyy"));

                       /* if (Utils.checkValidation(d)) {
                        }else{
                            Utils.toastMsg(contect, valMsg.getText().toString()+" "+"cannot be greater than Current Date");
                            //From Date cannot be greater than Current Date;
                        }*/
                    }
                }, year, month, day);
        picker.show();
        // return picker;
    }


    public static void timePickerForPM(final Context context,final EditText et,
                                       final TextView valMsg){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        String time=et.getText().toString().toString();


        if(time.length()>0){
            try {
                String[] arr = new String[2];
                arr = time.split( "\\:" );
                hour = Integer.parseInt( arr[0] );
                minute = Integer.parseInt( arr[1] );
            }catch (Exception e) {
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minute = mcurrentTime.get(Calendar.MINUTE);
            }
        }

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                et.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute,true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }




    public static void timePickerForPM1(final Context context,final EditText et,
                                       final TextView valMsg,
                                        final String compareType, final String compareValue,
                                        final String compareMiniute, final String compareMsg){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        String time=et.getText().toString().toString();

        if(time.length()>0){
            try {
                String[] arr = new String[2];
                arr = time.split( "\\:" );
                hour = Integer.parseInt( arr[0] );
                minute = Integer.parseInt( arr[1] );
            }catch (Exception e) {
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minute = mcurrentTime.get(Calendar.MINUTE);
            }


        }

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);

                String time1 = compareValue;
                if(time1.length()>0){
                    try {
                        String[] arr1 = new String[2];
                        arr1 = time1.split( "\\:" );
                        hour1 = Integer.parseInt( arr1[0] );
                        minute1 = Integer.parseInt( arr1[1]);
                    }catch (Exception e) {
                        hour1 = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute1 = mcurrentTime.get(Calendar.MINUTE);
                    }

                    int selectedMin = selectedHour * 60;
                    selectedMin = selectedMin+selectedMinute;

                    int selectedMin1 = hour1 * 60;
                    selectedMin1 = selectedMin1+minute1;

                    int differenceMin = Integer.parseInt(compareMiniute);
                    differenceMin = differenceMin * 60;

                    int X = selectedMin-selectedMin1;
                    int Y = selectedMin1-selectedMin;

                    if(differenceMin==0){
                        if(compareType.equalsIgnoreCase("G") && X<=0){
                            valMsg.clearFocus();
                            valMsg.requestFocus();
                            String s = valMsg.getText().toString()
                                     +" should be greater than "+compareMsg;
                            Utils.toastMsg(context, s);
                            return;
                        }else if(compareType.equalsIgnoreCase("L")
                                && Y<=0){
                            String s = valMsg.getText().toString()
                                    +" should be less than "+compareMsg;
                            Utils.toastMsg(context, s);
                            return;
                        }
                    }else{
                        if(compareType.equalsIgnoreCase("G") && X<differenceMin){
                            valMsg.clearFocus();
                            valMsg.requestFocus();
                            String s = "Difference b/w "+valMsg.getText().toString()
                                    +" and "+compareMsg
                                    +" should be "+compareMiniute+ " hours";
                            Utils.toastMsg(context, s);
                            return;
                        }else if(compareType.equalsIgnoreCase("L")
                                && Y<differenceMin){
                            String s = "Difference b/w "+valMsg.getText().toString()
                                    +" and "+compareMsg
                                    +" is "+compareMiniute+ " hours";
                            Utils.toastMsg(context, s);
                            return;
                        }
                    }
                }
                et.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute,true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    public static String getZoneSql(String SQL,String cid,String zid){
        String sql="";
        String where="";
        if(!cid.equalsIgnoreCase( "0" )){
            where=where+" CIRCLE_ID IN ("+cid+")";
        }
        if(!zid.equalsIgnoreCase( "0" )){
            where= where+" AND ZONE_ID IN ("+zid+")";
        }

        if(where.length()>0){
            where = "WHERE "+where;
        }
        String[] arr = new String[3];
        arr = SQL.split( "\\$" );

        sql = arr[0] +where+ arr[2];
        return sql;
    }

    public static String getClusterSql(String SQL,String cid,String zid,String clid){
        String sql="";
        String where="";
        if(!cid.equalsIgnoreCase( "0" )){
            where=where+" WHERE CIRCLE_ID IN ("+cid+")";
        }
        if(!zid.equalsIgnoreCase( "0" )){
            if(where.length()>0){
                where =where+ "AND ZONE_ID IN ("+zid+")";
            } else{
                where = "WHERE ZONE_ID IN ("+zid+")";
            }

        }

        if(!clid.equalsIgnoreCase( "0" )){
            if(where.length()>0){
                where =where+ "AND CLUSTER_ID IN ("+clid+")";
            } else{
                where = "WHERE CLUSTER_ID IN ("+clid+")";
            }
        }


        String[] arr = new String[3];
        arr = SQL.split( "\\$" );

        sql = arr[0] +where+ arr[2];
        return sql;
    }
}

