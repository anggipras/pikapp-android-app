package com.bejohen.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity

class SplashViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun checkOnboardingFinished(context: Context) {

        val isFinished = prefHelper.isOnboardingFinished() ?: false

        if (isFinished) {
            val homeActivity = Intent(context, HomeActivity::class.java)
            context.startActivity(homeActivity)
        } else {
            val onboardingActivity = Intent(context, OnboardingActivity::class.java)
            context.startActivity(onboardingActivity)
        }
    }
}