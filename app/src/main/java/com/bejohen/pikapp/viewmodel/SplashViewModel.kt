package com.bejohen.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.navigation.fragment.findNavController
import com.bejohen.pikapp.util.SharedPreferencesHelper
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.SplashActivity

class SplashViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())

    fun checkOnboardingFinished(context: Context) {

        val isFinished = prefHelper.isOnboardingFinished() ?: false

        if (isFinished) {
            val homeActivity = Intent(context, OnboardingActivity::class.java)
            context.startActivity(homeActivity)
        } else {
            val homeActivity = Intent(context, OnboardingActivity::class.java)
            context.startActivity(homeActivity)
        }
//            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
    }
}