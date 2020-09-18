package com.bejohen.pikapp.viewmodel.home

import android.app.Application
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel

class HomeActivityViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())

    fun saveDeeplink(mid: String, tableNo: String) {
        prefHelper.saveDeeplinkUrl(mid, tableNo)
    }

    fun validateDeeplink() {
        val deepLink = prefHelper.getDeeplink()
    }
}