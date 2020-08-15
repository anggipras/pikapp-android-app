package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.navigation.Navigation
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.login.SignupSuccessFragmentDirections
import com.bejohen.pikapp.view.onboarding.login.SignupOnboardingSuccessFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel

class SignupOnboardingSuccessViewModel(application: Application) : BaseViewModel(application)  {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getOnboardingFinished(): Boolean {
        return prefHelper.isOnboardingFinished() ?: false
    }

    fun backToLogin(view: View) {

        if (getOnboardingFinished()) {
            val action = SignupSuccessFragmentDirections.actionBackToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            val action = SignupOnboardingSuccessFragmentDirections.actionBackToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }
}