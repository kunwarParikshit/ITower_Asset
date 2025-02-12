package com.isl.user.tracking;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SignalChecker extends PhoneStateListener {
    public Context mContext;
    private TelephonyManager telephonyManager;
    private String TAG = "Signal Checker";
    int strength;
	private OnSignalReceiver mSignalReceiver;
    public SignalChecker(Context ctx,OnSignalReceiver signalReceiver) {
        this.mContext = ctx;
        this.mSignalReceiver = signalReceiver;
    }

    public void checkAndUpdate(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo Info = cm.getActiveNetworkInfo();
        if (Info == null || !Info.isConnectedOrConnecting()) {
            } else {
            int netType = Info.getType();
                if(netType == ConnectivityManager.TYPE_WIFI){
                Log.i(TAG, "Wifi connection");
                 int linkedSpeed = 0;
                 try {
                     WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                     linkedSpeed = wifiManager.getConnectionInfo().getRssi();
                     mSignalReceiver.onRecived(linkedSpeed);
                 }catch (SecurityException e){
                     e.printStackTrace();
                 }
                }else if(netType == ConnectivityManager.TYPE_MOBILE) {
                Log.i(TAG, "GPRS/3G connection");
                telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(this,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        }
		
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        strength  = signalStrength.getGsmSignalStrength();
        mSignalReceiver.onRecived(strength);
        telephonyManager.listen(this,PhoneStateListener.LISTEN_NONE);// unregister listener
        //Toast.makeText(mContext,"Speed of GPRS/3G connection=== "+strength,Toast.LENGTH_LONG).show();
        if (strength >= 30) {
           // Toast.makeText(mContext,"Speed of GPRS/3G connection "+"Signal Str : Good"+strength,Toast.LENGTH_LONG).show();
            //LocationDataHandler.updateLocation(mContext,mUpdateId,"Y","Good");
            //Toast.makeText(mContext, "Good", Toast.LENGTH_LONG).show();
        }
        else if(strength >= 20 && strength < 30) {
           // Toast.makeText(mContext,"Speed of GPRS/3G connection "+"Signal Str : Average"+signalStrength,Toast.LENGTH_LONG).show();
            //LocationDataHandler.updateLocation(mContext,mUpdateId,"Y","Average");
        	//Toast.makeText(mContext, "Average", Toast.LENGTH_LONG).show();
        }
        else if(strength < 20) {
           // Toast.makeText(mContext,"Speed of GPRS/3G connection "+"Signal Str : Weak"+strength,Toast.LENGTH_LONG).show();
            //LocationDataHandler.updateLocation(mContext,mUpdateId,"Y","Weak");
           // Toast.makeText(mContext, "Weak", Toast.LENGTH_LONG).show();
        }
    }

	
 public interface OnSignalReceiver{
	 void onRecived(int signal);
 }
 
}