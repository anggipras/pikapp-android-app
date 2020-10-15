package com.bejohen.pikapp.view

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        Handler().postDelayed({
            val uri: Uri? = intent.data
            var mid: String? = null
            var tableNo: String? = null
            if(uri != null) {
                val params : List<String> = uri.pathSegments
                mid = params.get(1)
                tableNo = params.get(2)
            }
            viewModel.checkOnboardingFinished(this, mid, tableNo)
            finish()
        }, 2000)
    }
}