package com.bejohen.pikapp.viewmodel.userExclusive

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.UserAccess
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.view.UserExclusiveActivity
import com.bejohen.pikapp.view.userExclusive.UserExclusiveProfileFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel

class UserExclusiveHomeViewModel(application: Application): BaseViewModel(application) {

    val userData = MutableLiveData<UserAccess>()

    private var sessionManager = SessionManager(getApplication())

    fun getUserData() {
        userData.value = sessionManager.getUserData()
    }

    fun goToProfile(context: Context) {
        val userExclusiveProfileFragment = UserExclusiveProfileFragment()
        userExclusiveProfileFragment.show((context as UserExclusiveActivity).supportFragmentManager, userExclusiveProfileFragment.getTag())
    }


}