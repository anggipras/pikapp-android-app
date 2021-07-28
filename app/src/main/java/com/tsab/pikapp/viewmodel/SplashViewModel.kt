package com.tsab.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.tsab.pikapp.models.model.NotificationModel
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.decodeJWT
import com.tsab.pikapp.view.*
import com.tsab.pikapp.view.homev2.HomeNavigation

class SplashViewModel(application: Application) : BaseViewModel(application) {
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())

    fun saveNotification(mrcht: String, txn: String, tableNo: String?) {
        var isMerchant = false
        if (mrcht == "true") isMerchant = true
        prefHelper.setNotificationDetail(NotificationModel(isMerchant, txn, tableNo))
    }

    fun checkOnboardingFinished(context: Context, mid: String?, address: String?,
                                tableNo: String?) {
        prefHelper.clearOrderList()
        prefHelper.deleteStoredDeepLink()
        prefHelper.deleteDeeplinkUrl()
        cartUtil.setCartType("")
        cartUtil.setCartIsNew(true)
        val isUserExclusive = prefHelper.isUserExclusive() ?: false

        val isUserLogin = sessionManager.isLoggingIn() ?: false

        if (!mid.isNullOrEmpty() || !address.isNullOrEmpty()) {
            var addressSplit: String? = null
            address?.let {
                addressSplit = address.replace('_', ' ')
            }
            prefHelper.saveDeeplinkUrl(mid, addressSplit, tableNo)
        }

        if (isUserLogin) {
            val token = sessionManager.getUserToken()
            token?.let {
                if (token.isNotEmpty()) {
                    val userData: UserAccess = decodeJWT(token)
                    if (userData.isMerchant == null) {
                        Log.d("Debug", "harus di logout nih")
                        Toast.makeText(getApplication(), "Silakan login kembali", Toast.LENGTH_LONG)
                                .show()
                        sessionManager.logout()
                        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
                        context.startActivity(onboardingActivity)
                    }

                    if (isUserExclusive) {
                        val userExclusiveActivity = Intent(context,
                                UserExclusiveActivity::class.java)
                        context.startActivity(userExclusiveActivity)
                    } else {
                        val storeActivity = Intent(context, HomeNavigation::class.java)
                        context.startActivity(storeActivity)
                    }
                } else {
                    sessionManager.logout()
                    val onboardingActivity = Intent(context, LoginV2Activity::class.java)
                    context.startActivity(onboardingActivity)
                }
            }
        } else {
            val firstApp = sessionManager.getFirstApp()
            firstApp?.let {
                if (firstApp == 1) {
                    val loginActivity = Intent(context, LoginV2Activity::class.java)
                    context.startActivity(loginActivity)
                } else {
                    val onboardingActivity = Intent(context, CarouselActivity::class.java)
                    context.startActivity(onboardingActivity)
                }
            }
        }
    }
}