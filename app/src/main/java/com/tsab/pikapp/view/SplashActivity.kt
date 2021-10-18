package com.tsab.pikapp.view

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.tasks.OnSuccessListener
import com.tsab.pikapp.BuildConfig
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.SplashViewModel
import timber.log.Timber
import timber.log.Timber.DebugTree

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateListener: OnSuccessListener<AppUpdateInfo>
    private lateinit var appUpdateInfo: AppUpdateInfo
    private val myRequestCode: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        runSplash()
//        Firebase.messaging.isAutoInitEnabled = true
//
//        appUpdateListener = OnSuccessListener { appUpdateInfo ->
//            this.appUpdateInfo = appUpdateInfo
//
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
//            ) {
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.IMMEDIATE,
//                    this,
//                    myRequestCode
//                )
//            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                Timber.d("Update on progress...")
//            } else if(appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) {
//                runSplash()
//            } else {
//                Timber.d("Update went wrong!")
//            }
//        }
//        appUpdateManager = AppUpdateManagerFactory.create(this)
//        appUpdateManager.appUpdateInfo.addOnSuccessListener(appUpdateListener)
    }

    private fun runSplash() {
        supportActionBar?.hide()

        if (intent != null && intent.hasExtra("is_merchant")) {
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

            if (uri != null) {
                val uriString = uri.toString()
                if (uriString.contains("list")) {
                    val params: List<String> = uri.pathSegments
                    address = params[2]
                    tableNo = params[3]
                } else {
                    val params: List<String> = uri.pathSegments
                    mid = params[1]
                    tableNo = params[2]
                }
            }
            viewModel.checkOnboardingFinished(this, mid, address, tableNo)
            finish()
        }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == myRequestCode) {
            if (resultCode != RESULT_OK) {
                // If user doesn't update, show dialog to close the app.
                showUpdateDialog()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showUpdateDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Perbarui Aplikasi")
            setMessage("Anda yakin ingin keluar?")

            setPositiveButton("Ya") { _, _ ->
                finish()
            }
            setNegativeButton("Tidak") { _, _ ->
                // Restart activity to start update flow.
                finish()
                startActivity(intent)
            }

            show()
        }
    }
}