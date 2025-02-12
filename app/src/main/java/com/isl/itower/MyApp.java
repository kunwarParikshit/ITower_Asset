package com.isl.itower;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatDelegate;

import com.isl.assetManagement.api.Client;
import com.isl.assetManagement.room.db.IAssetDatabase;
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper;
import com.isl.audit.db.AppDatabase;

/**
 * Created by dhakan on 10/11/2018.
 */

public class MyApp extends Application {
    private static IAssetDatabase iAssetDatabase;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        final DataSyncReceiver myReceiver = new DataSyncReceiver();
        registerReceiver( myReceiver, filter );
        KotlinPrefkeeper.init(this);
        Client.INSTANCE.init();
        iAssetDatabase = IAssetDatabase.Companion.getDatabase(appContext);



    }

    public static Context getAppContext() {
        return appContext;
    }

    public static IAssetDatabase getAssetDatabase() {
        return iAssetDatabase;
    }


 }