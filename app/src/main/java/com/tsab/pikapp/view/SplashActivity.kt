package com.tsab.pikapp.view

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.tsab.pikapp.BuildConfig
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.LatestVersionModel
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.getClientID
import com.tsab.pikapp.util.getTimestamp
import com.tsab.pikapp.util.getUUID
import com.tsab.pikapp.viewmodel.SplashViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        getVersionUpdate()

        Firebase.messaging.isAutoInitEnabled = true

        appUpdateListener = OnSuccessListener { appUpdateInfo ->
            this.appUpdateInfo = appUpdateInfo

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    myRequestCode
                )
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                Timber.d("Update on progress...")
            } else if(appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) {
                runSplash()
            } else {
                Timber.d("Update went wrong!")
            }
        }
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener(appUpdateListener)
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
                    mid = if (params[1].isNotBlank()) { params[1] } else { "M00000002" }
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

    private fun getVersionUpdate() {
        PikappApiService().api.getLatestVersion(
            getUUID(),
            getTimestamp(),
            getClientID(),
            "PUBLIC",
            "PIKAPP_ANDROID")
            .enqueue(object : Callback<LatestVersionModel> {
                override fun onResponse(
                    call: Call<LatestVersionModel>,
                    response: Response<LatestVersionModel>
                ) {
                    val versionResult = response.body()!!.results
                    if (BuildConfig.VERSION_NAME == versionResult.app_version) {
                        runSplash()
                    } else {
                        dialogOpenPlayStore()
                    }
                }

                override fun onFailure(call: Call<LatestVersionModel>, t: Throwable) {
                    Toast.makeText(baseContext, "Gagal tarik versi", Toast.LENGTH_SHORT).show()
                    runSplash()
                }
        })
    }

    private fun dialogOpenPlayStore() {
        AlertDialog.Builder(this).apply {
            setTitle("Perbarui Aplikasi")
            setMessage("Mohon perbarui aplikasi di play store terlebih dahulu")

            setPositiveButton("Ya") { _, _ ->
                // Open Play Store
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
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