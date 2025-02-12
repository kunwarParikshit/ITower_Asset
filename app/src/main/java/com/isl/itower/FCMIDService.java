package com.isl.itower;
import android.app.Activity;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.isl.dao.cache.AppPreferences;
/**
 * Created by dhakan on 7/3/2018.
 */

public class FCMIDService extends FirebaseMessagingService {
    //public String senderId;
    //private static final String TAG = "MyFirebaseIIDService";
    AppPreferences mAppPreferences;
    public DeviceToken deviceIDReceive;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    FCMIDService(Activity context,DeviceToken idReceive) {
        //this.mContext = context;
        mAppPreferences = new AppPreferences(context);
        this.deviceIDReceive=idReceive;
    }


    public void onTokenRefresh() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String deviceToken = task.getResult();
                deviceIDReceive.onRecived(deviceToken);
                mAppPreferences.setGCMRegistationId(deviceToken);
            }
        });

        // Get updated InstanceID token.
        //String refreshedToken = String.valueOf(FirebaseMessaging.getInstance().getToken());
        //deviceIDReceive.onRecived(refreshedToken);
         //mAppPreferences.setGCMRegistationId(refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

     // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     */
public interface DeviceToken{
        void onRecived(String tokenId);
}

}