package com.isl.audit.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.isl.audit.activity.AlertActivity;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            AppPreferences appPreferences=new AppPreferences(context);
            if (isOnline(context)) {
                if(!TextUtils.isEmpty(appPreferences.getUnsyncAudit())){
                    context.startActivity(new Intent(context, AlertActivity.class));
                }
            } else {
                Utils.toastMsgCenter(context,"Connection lost");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
           // return (netInfo != null && netInfo.isConnected());
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
    /*private void showAlertDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(R.string.app_name)
                .setMessage(("...."))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.sync_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(context,MyAuditsActivity.class));
                    }
                });


        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            alertDialog.getWindow().setType(type);
        }
        alertDialog.show();

    }*/
}