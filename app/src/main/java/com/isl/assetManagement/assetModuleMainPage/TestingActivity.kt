package com.isl.assetManagement.assetModuleMainPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.isl.assetManagement.utils.Util
import infozech.itower.R

class TestingActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        imageView = findViewById(R.id.imageView)
        captureButton = findViewById(R.id.captureButton)

        captureButton.setOnClickListener {
            Util.Companion.CameraUtils.handleCaptureButtonClick(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Util.Companion.CameraUtils.handleActivityResult(requestCode, resultCode, imageView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Util.Companion.CameraUtils.handlePermissionsResult(requestCode, grantResults, this)
    }
}

