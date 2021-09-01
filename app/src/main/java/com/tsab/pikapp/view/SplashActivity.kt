package com.tsab.pikapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private val MY_REQUEST_CODE : Int = 100

    private lateinit var appUpdateManager : AppUpdateManager
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE)
            } else if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                runSplash()
            }
        }
    }

    fun runSplash() {
        supportActionBar?.hide()
        getIntent().getExtras()
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        if(intent != null && intent.hasExtra("is_merchant")) {
            val isMerchant = intent.extras!!.getString("is_merchant")
            val transactionID = intent.extras!!.getString("transaction_id")
            val tableNo = intent.extras!!.getString("table_no")
            viewModel.saveNotification(isMerchant!!, transactionID!!, tableNo!!)
        }
        Handler().postDelayed({
            val uri: Uri? = intent.data
            var mid: String? = null
            var address: String? = null
            var tableNo: String? = null
            if(uri != null) {
                val uriString = uri.toString()
                if(uriString.contains("list")) {
                    val params : List<String> = uri.pathSegments
                    address = params[2]
                    tableNo = params[3]
                } else {
                    val params : List<String> = uri.pathSegments
                    mid = params[1]
                    tableNo = params[2]
                }
            }
            viewModel.checkOnboardingFinished(this, mid, address, tableNo)
            finish()
        }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Checks that the update is not stalled during 'onResume()'.
    // However, you should execute this check at all entry points into the app.
    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                } else if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    runSplash()
                }
            }
    }
}