package com.isl.assetManagement.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.isl.assetManagement.utils.CustomToastMsg.Companion.showCustomToast
import infozech.itower.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class TestingMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val startLocation = LatLng(37.7749, -122.4194)  // San Francisco
    private val destinationLocation = LatLng(34.0522, -118.2437) // Los Angeles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_map)
        setupMap()
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

        // Fetch and draw the route
        drawRoute()
    }

    private fun addMarker(position: LatLng, title: String) {
        googleMap.addMarker(MarkerOptions().position(position).title(title))
    }

    private fun drawRoute() {
        val apiKey = packageManager.getApplicationInfo(packageName,
            PackageManager.GET_META_DATA).metaData.getString("com.google.android.geo.API_KEY") ?: ""
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${startLocation.latitude},${startLocation.longitude}" +
                "&destination=${destinationLocation.latitude},${destinationLocation.longitude}" +
                "&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL(url).readText()
                val route = parseRoute(response)

                withContext(Dispatchers.Main) {
                    if (route.isNotEmpty()) {
                        drawPolyline(route)
                        zoomToRoute()
                    } else {
                        showCustomToast(this@TestingMapActivity, "Failed to get route")
                    }
                }
            } catch (e: Exception) {
                Log.e("TestingMapActivity", "Error fetching route: ${e.message}")
                showCustomToast(this@TestingMapActivity, "Error fetching route")
            }
        }
    }

    private fun parseRoute(response: String): List<LatLng> {
        val path = mutableListOf<LatLng>()
        try {
            val json = JSONObject(response)
            val routes = json.getJSONArray("routes")

            if (routes.length() > 0) {
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")

                for (i in 0 until steps.length()) {
                    val polyline = steps.getJSONObject(i)
                        .getJSONObject("polyline")
                        .getString("points")

                    path.addAll(decodePolyline(polyline))
                }
            }
        } catch (e: Exception) {
            Log.e("TestingMapActivity", "Error parsing route: ${e.message}")
        }
        return path
    }

    private fun drawPolyline(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(path)
            .width(10f)
            .color(Color.BLUE)
            .geodesic(true)

        googleMap.addPolyline(polylineOptions)
    }

    private fun zoomToRoute() {
        val boundsBuilder = LatLngBounds.builder()
        boundsBuilder.include(startLocation)
        boundsBuilder.include(destinationLocation)

        val bounds = boundsBuilder.build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
        }

        return poly
    }
}
