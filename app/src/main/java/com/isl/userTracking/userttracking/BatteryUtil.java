package com.isl.userTracking.userttracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

public class BatteryUtil {

    public static final int REQUEST_BATTERY_OPTIMIZATION = 10010;
    public static boolean flag = true;

    public static boolean isBatteryOptimizationEnabled(Context context) {
        if(flag) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static void requestBatteryOptimizationPermission(Activity activity) {
//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//        activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION);

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
       activity.startActivity(intent);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

  public static void getUnrestrictedBAttery(Context context,String msg)
    {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Auto Start (Battery Unrestricted) should be enable");
            alertDialog.setMessage(msg);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            flag = false;
                            dialog.dismiss();
                        }
                    });


            alertDialog.show();
        }




}
