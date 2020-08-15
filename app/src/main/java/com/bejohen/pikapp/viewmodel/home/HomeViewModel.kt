package com.bejohen.pikapp.viewmodel.home

import android.app.Application
import android.content.Context
import android.content.Intent
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.home.ProfileFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun checkUserLogin(context: Context) {
        val isLoggingIn = prefHelper.isLoggingIn() ?: false

        if (isLoggingIn) {
            val profileFragment = ProfileFragment()
            profileFragment.show((context as HomeActivity).supportFragmentManager, profileFragment.getTag())
        } else {
            val loginActivity = Intent(context, LoginActivity::class.java)
            context.startActivity(loginActivity)
        }
    }
}