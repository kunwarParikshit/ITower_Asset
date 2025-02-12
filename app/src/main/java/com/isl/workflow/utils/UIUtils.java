package com.isl.workflow.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.isl.util.Utils;
import com.isl.workflow.FormActivity;

import infozech.itower.R;

public class UIUtils {
    public static boolean isMatch = false; //109
    public static boolean key = false;

    public static Switch toggleButtonUI(Context ctx, Switch swh){
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int height = (int)(60 * scale);
        int width = (int)(220 * scale);
        int margin = (int)(10 * scale);
        LinearLayout.LayoutParams GNameParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,height);
        GNameParam.setMargins( margin, margin, margin, 0 );
        swh.setLayoutParams( GNameParam );
        swh.setGravity(Gravity.LEFT);
        swh.setChecked(false);
        swh.setTextColor(ctx.getResources().getColor(R.color.textcolor ));
        swh.setSwitchMinWidth((int)(65 * scale));
        swh.setSwitchPadding((int)(10 * scale));
        return swh;
    }

    public static CheckBox checkBoxUI(Context ctx, CheckBox checkBox){

        LinearLayout.LayoutParams InputParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        InputParam.setMargins( 10, 25, 10, 0 );
        checkBox.setPadding(7, 20, 0, 0);
        checkBox.setLayoutParams(InputParam);
        checkBox.setTextSize( TypedValue.COMPLEX_UNIT_SP, 15f);
        checkBox.setTextColor(ctx.getResources().getColor(R.color.textcolor ));
        checkBox.setLayoutDirection(CheckBox.LAYOUT_DIRECTION_RTL);

        return checkBox;
    }

    public static Toast toastMsg(Context context, String msg,String languageCode) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.toast_layout, null);
        TextView tv = (TextView) vi.findViewById(R.id.text);
        tv.setTypeface(typeface(languageCode,context));
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        Toast toast = new Toast(context.getApplicationContext());
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(vi);
        toast.show();
        return toast;
    }

    public static Typeface typeface(String languageCode,Context context) {
        Typeface mynFont = null;
        if (languageCode.equalsIgnoreCase("my")) {
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
}