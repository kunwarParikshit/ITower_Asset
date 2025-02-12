package com.isl.assetManagement.sharedPref
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.isl.assetManagement.constants.AppConstants

object KotlinPrefkeeper {    //this class is used for saving data to shared preference
    private var prefs: SharedPreferences? = null

    @JvmStatic
    fun init(context: Context) {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)

        }
    }
    // Assuming prefs is a nullable SharedPreferences object
    var deviceUUID: String?
        get() = prefs?.getString(AppConstants.PrefsName.deviceUUID, "") ?: ""
        set(value) {
            // Check if prefs is null before trying to use it
            prefs?.edit()?.putString(AppConstants.PrefsName.deviceUUID, value)?.apply()
                ?: run {
                    // Handle the case where prefs is null (could log the error or initialize prefs)
                    Log.e("KotlinPrefkeeper", "prefs is null, cannot set deviceUUID")
                }
        }


    var assetUserId: String?
        get() = prefs!!.getString(AppConstants.PrefsName.assetUserId, "")
        set(assetUserId) = prefs!!.edit().putString(AppConstants.PrefsName.assetUserId, assetUserId)
            .apply()


    fun clear() = prefs?.edit()?.clear()?.apply()


}