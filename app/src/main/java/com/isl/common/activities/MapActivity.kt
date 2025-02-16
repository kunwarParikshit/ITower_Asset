package com.isl.common.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.isl.assetManagement.utils.CustomToastMsg.Companion.showCustomToast
import com.isl.itower.GPSTracker
import infozech.itower.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var startLocation = LatLng(0.0, 0.0)
    private var destinationLocation = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_map)
        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        destinationLocation = LatLng(latitude, longitude)
        val gps = GPSTracker(this)
        startLocation = LatLng(gps.latitude, gps.longitude)
        // Use Handler to delay map setup by 1 minute (60,000 milliseconds)
        Handler().postDelayed({
            setupMap()
        }, 3000)  // Delay in milliseconds
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentContainer)
                as? SupportMapFragment ?: SupportMapFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.mapFragmentContainer, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Add markers
        addMarker(startLocation, "San Francisco")
        addMarker(destinationLocation, "Los Angeles")

        // Start Google Maps Navigation
        startGoogleMapsNavigation()
    }

    private fun addMarker(position: LatLng, title: String) {
        googleMap.addMarker(MarkerOptions().position(position).title(title))
    }

    private fun startGoogleMapsNavigation() {
        val uri = "https://www.google.com/maps/dir/?api=1" +
                "&origin=${startLocation.latitude},${startLocation.longitude}" +
                "&destination=${destinationLocation.latitude},${destinationLocation.longitude}" +
                "&travelmode=driving"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showCustomToast(this, "Google Maps is not installed")
        }
    }

}
