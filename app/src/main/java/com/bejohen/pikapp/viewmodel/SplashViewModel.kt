package com.bejohen.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bejohen.pikapp.util.DL_MERCHANTID
import com.bejohen.pikapp.util.DL_TABLENO
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.UserExclusiveActivity

class SplashViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun checkOnboardingFinished(context: Context, mid: String?, tableNo: String?) {

        val isFinished = prefHelper.isOnboardingFinished() ?: false

        val isUserExclusive = prefHelper.isUserExclusive() ?: false

        if(!mid.isNullOrEmpty() && !tableNo.isNullOrEmpty()) {
            prefHelper.saveDeeplinkUrl(mid, tableNo)
        }

        if (isFinished) {
            if (isUserExclusive) {
                val userExclusiveActivity = Intent(context, UserExclusiveActivity::class.java)
                context.startActivity(userExclusiveActivity)
            } else {
                val homeActivity = Intent(context, HomeActivity::class.java)
                context.startActivity(homeActivity)
            }
        } else {
            val onboardingActivity = Intent(context, OnboardingActivity::class.java)
            context.startActivity(onboardingActivity)
        }
    }
}