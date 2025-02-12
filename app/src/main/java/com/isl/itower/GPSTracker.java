package com.isl.itower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.KeyEvent;

public class GPSTracker extends Service implements LocationListener {
	private final Context mContext;
	boolean isGPSEnabled = false,isNetworkEnabled = false;
	public boolean canGetLocation = false;
	public boolean canMockLocation = false;
	Location location;
	double latitude,longitude;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			//locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					//if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					//		|| ContextCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

						locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this );
					//}
					//locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this );

					if (locationManager != null) {

						//if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						//		|| ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

							location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						//}

						//location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {

						//if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						//		|| ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
						//{
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

						//}

						//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {

							//if (ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							//		|| ActivityCompat.checkSelfPermission((Activity)mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
							//{
								location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

							//}

							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}


	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}
	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}
	/**
	 * Function to check GPS/wifi enabled
	 *
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public boolean isMockLocation() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location != null && location.isFromMockProvider();
	}

	/**
	 * Function to get distance
	 * */
	//public double getDistance(double sidlat,double sidlong){
	public double getDistance(double siteLat,double siteLog,double userLat,double userLog){
		float [] dist = new float[1];
		Location.distanceBetween(siteLat,siteLog,userLat,userLog,dist);
		return dist[0];
	}
	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		// Setting Dialog Title
		alertDialog.setTitle("GPS Settings");
		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Please enable to continue");
		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
				//((Activity) mContext).finish();ACTION_LOCATION_SOURCE_SETTINGS
			}
		});
		alertDialog.setCancelable(false);
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				//((Activity) mContext).finish();
			}
		});

		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					((Activity) mContext).finish();
				}
				return false;
			}
		});
		// Showing Alert Message
		alertDialog.show();
	}
	@Override
	public void onLocationChanged(Location location) {
		//Toast.makeText(GPSTracker.this, ""+location.getLatitude()+"--"+location.getLongitude(), Toast.LENGTH_LONG).show();

	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
