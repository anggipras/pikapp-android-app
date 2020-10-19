package com.tsab.pikapp.viewmodel.userExclusive

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.view.userExclusive.UserExclusiveProfileFragment
import com.tsab.pikapp.viewmodel.BaseViewModel

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