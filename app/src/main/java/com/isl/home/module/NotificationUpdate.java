package com.isl.home.module;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class NotificationUpdate extends IntentService {
    public NotificationUpdate() {
        super(NotificationUpdate.class.getSimpleName());
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Intent intent1 = new Intent();
        intent1.setAction("iTower");
        sendBroadcast(intent1);
    }
}