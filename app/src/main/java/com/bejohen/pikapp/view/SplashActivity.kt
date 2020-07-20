package com.bejohen.pikapp.view

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.util.SharedPreferencesHelper
import com.bejohen.pikapp.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        val typeface = Typeface.createFromAsset(assets, "NyataFtrRegular.otf")

        titleText.typeface = typeface

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        Handler().postDelayed({
            viewModel.checkOnboardingFinished(this)
            finish()
        }, 2000)

    }
}