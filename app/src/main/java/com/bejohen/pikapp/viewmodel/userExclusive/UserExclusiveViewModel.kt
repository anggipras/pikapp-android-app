package com.bejohen.pikapp.viewmodel.userExclusive

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.LoginResponse
import com.bejohen.pikapp.models.model.UserAccess
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel

class UserExclusiveViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getUserExclusiveFormFinished(): Boolean {
        return prefHelper.isUserExclusiveFormFinished() ?: false
    }
}
