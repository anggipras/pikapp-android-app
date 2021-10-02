package com.tsab.pikapp.viewmodel.onboarding.signup

import android.app.Application
import android.view.View
import androidx.navigation.Navigation
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.login.SignupSuccessFragmentDirections
import com.tsab.pikapp.view.onboarding.login.SignupOnboardingSuccessFragmentDirections
import com.tsab.pikapp.viewmodel.BaseViewModel

class SignupOnboardingSuccessViewModel(application: Application) : BaseViewModel(application) {

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