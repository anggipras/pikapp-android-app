package com.tsab.pikapp.view

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        intent.extras
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

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
}