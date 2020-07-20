package com.bejohen.pikapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (isOnboardinFinished()) {
//                val homeActivity = Intent(this, HomeActivity::class.java)
//                startActivity(homeActivity)
//                finish()
            } else {
//                val onboardingActivity = Intent(this, HomeActivity::class.java)
//                startActivity(onboardingActivity)
//                finish()
            }
        }, 2000)
    }

    private fun isOnboardinFinished(): Boolean {
        val sharedPref = getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}