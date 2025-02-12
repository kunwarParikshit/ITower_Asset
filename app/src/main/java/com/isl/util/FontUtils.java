package com.isl.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.isl.dao.cache.AppPreferences;
import com.isl.dao.DataBaseHelper;

public class FontUtils {

    public static Typeface typeface(Context context) {
        Typeface mynFont = null;
        AppPreferences mAppPreferences = new AppPreferences(context);
        if (mAppPreferences.getLanCode().equalsIgnoreCase("my")) {
            mynFont = Typeface.createFromAsset(context.getAssets(),
                    "Myanmar3.ttf");
        } else {
            if(context.getApplicationContext().getPackageName().
                    equalsIgnoreCase("infozech.tawal")||
                    context.getApplicationContext().getPackageName().
                            equalsIgnoreCase("iz.tawal")){
                mynFont = Typeface.createFromAsset(context.getAssets(),
                        "TeshrinARLT-Regular.ttf");
            }else{
                mynFont = Typeface.DEFAULT;
            }
        }
        return mynFont;
    }

    public static TextView setCaption(Context context, String caption , int id, TextView tv) {
        AppPreferences mAppPreferences = new AppPreferences(context);
        if(mAppPreferences.getLanCode().equalsIgnoreCase("EN")){
            tv.setTypeface(FontUtils.typeface(context));
            tv.setText(caption);
        } else{
            DataBaseHelper dbHelper = new DataBaseHelper(context);
            dbHelper.open();
            String message = dbHelper.getMessage(id+"");
            tv.setTypeface(dbHelper.typeface(mAppPreferences.getLanCode()));
            tv.setText(message);
            dbHelper.close();
        }
        return tv;
    }
}
