package com.isl.itower;

/**
 * Created by dhakan on 10/5/2018.
 */
import android.content.Context;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.isl.dao.cache.AppPreferences;

public class BackGroundWork extends Worker {
    //private static final String TAG = "MyWorker222";
    AppPreferences mAppPreferences;

    public BackGroundWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Context context1 = MyApp.getAppContext();
             //Toast.makeText( con,"background",Toast.LENGTH_LONG ).show();
            //DataSyncReceiver dataSync = new DataSyncReceiver();
             //dataSync.postSubmitSync( context1 );
       // return Result.SUCCESS;
        return null;
    }

}