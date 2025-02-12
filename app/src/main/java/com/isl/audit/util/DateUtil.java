package com.isl.audit.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    public static String DATE_FORMAT1="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String DATE_FORMAT2="dd-MM-yyyy";
    public static String DATE_FORMAT3="dd-MMM-yyyy";
    public static String DATE_FORMAT4="yyyy-MM-dd-hh-mm-ss";
    public static String DATE_FORMAT5="dd-MMM-yyyy HH:mm:ss";
    public static String formatDate(String inputFormat, String outputFormat, String dateToBeParsed){
        DateFormat originalFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
       // DateFormat targetFormat = new SimpleDateFormat(outputFormat,Locale.getDefault());
        DateFormat targetFormat = new SimpleDateFormat(outputFormat,Locale.ENGLISH);
        targetFormat.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = originalFormat.parse(dateToBeParsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate="";
        if(date != null){
            formattedDate = targetFormat.format(date);
        }else{
            formattedDate = "";
        }

        return formattedDate;
    }
    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT5,Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        return formatter.format(date);
    }
    public static String getCurrentDate(String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format,Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
