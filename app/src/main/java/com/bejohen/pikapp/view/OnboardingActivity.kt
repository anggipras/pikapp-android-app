package com.bejohen.pikapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bejohen.pikapp.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        supportActionBar?.hide()
    }
}