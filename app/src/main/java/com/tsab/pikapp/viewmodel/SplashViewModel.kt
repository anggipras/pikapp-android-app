package com.tsab.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.decodeJWT
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.view.UserExclusiveActivity

class SplashViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())
    fun checkOnboardingFinished(context: Context, mid: String?, tableNo: String?) {

        prefHelper.deleteStoredDeepLink()
        prefHelper.deleteDeeplinkUrl()
        cartUtil.setCartType("")
        cartUtil.setCartIsNew(true)
        val isUserExclusive = prefHelper.isUserExclusive() ?: false

        val isUserLogin = sessionManager.isLoggingIn() ?: false

        if(!mid.isNullOrEmpty()) {
            prefHelper.saveDeeplinkUrl(mid, tableNo)
        }

        if (isUserLogin) {
            val token = sessionManager.getUserToken()
            token?.let {
                if (token.isNotEmpty()) {
                    val userData: UserAccess = decodeJWT(token)
                    if(userData.isMerchant == null)
                    {
                        Log.d("Debug", "harus di logout nih")
                        Toast.makeText(getApplication(), "Silakan login kembali", Toast.LENGTH_LONG).show()
                        sessionManager.logout()
                        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
                        context.startActivity(onboardingActivity)
                    }

                    if (isUserExclusive) {
                        val userExclusiveActivity = Intent(context, UserExclusiveActivity::class.java)
                        context.startActivity(userExclusiveActivity)
                    } else {
                        val homeActivity = Intent(context, HomeActivity::class.java)
                        context.startActivity(homeActivity)
                    }
                } else {
                    sessionManager.logout()
                    val onboardingActivity = Intent(context, OnboardingActivity::class.java)
                    context.startActivity(onboardingActivity)
                }
            }

        } else {
            val onboardingActivity = Intent(context, OnboardingActivity::class.java)
            context.startActivity(onboardingActivity)
        }
    }
}