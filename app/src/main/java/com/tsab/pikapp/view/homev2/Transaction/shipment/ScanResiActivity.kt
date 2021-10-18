package com.tsab.pikapp.view.homev2.Transaction.shipment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import com.tsab.pikapp.BuildConfig
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.activity_scan_resi.*
import timber.log.Timber

class ScanResiActivity : AppCompatActivity() {
    companion object {
        const val RESULT = "result"
    }

    private val tag = javaClass.simpleName
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_resi)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        // Listener for Camera permission request result.
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Timber.tag(tag).d("Camera permission granted!")
                setupScanner()
            } else {
                // If not granted, close the activity.
                Timber.tag(tag).d("Camera permission not granted!")
                finish()
            }
        }

        // Check for Camera permission. If not yet granted, request the permission.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Timber.tag(tag).d("Camera permission already granted!")
            setupScanner()
        } else {
            // Start permission request.
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::codeScanner.isInitialized) {
            codeScanner.startPreview()
        } else {
            setupScanner()
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (this::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun setupScanner() {
        codeScanner = CodeScanner(this, scannerView)

        // Setup parameters for the code scanner.
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner.scanMode = ScanMode.SINGLE

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isTouchFocusEnabled = true
        codeScanner.isFlashEnabled = true

        codeScanner.errorCallback = ErrorCallback { runOnUiThread { Timber.tag(tag).d(it) } }
        codeScanner.decodeCallback = DecodeCallback { scannedResult ->
            runOnUiThread {
                Timber.tag(tag).d(scannedResult.text)

                // Send result to the previous fragment.
                Intent().apply {
                    putExtra(RESULT, scannedResult.text)
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
            }
        }

        codeScanner.startPreview()
    }
}