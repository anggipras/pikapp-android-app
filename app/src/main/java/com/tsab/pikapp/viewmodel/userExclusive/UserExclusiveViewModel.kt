package com.tsab.pikapp.viewmodel.userExclusive

import android.app.Application
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel

class UserExclusiveViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getUserExclusiveFormFinished(): Boolean {
        return prefHelper.isUserExclusiveFormFinished() ?: false
    }
}
