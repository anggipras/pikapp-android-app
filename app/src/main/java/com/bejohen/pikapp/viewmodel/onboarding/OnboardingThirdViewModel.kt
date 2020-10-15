package com.bejohen.pikapp.viewmodel.onboarding

import android.app.Application
import android.view.View
import androidx.navigation.Navigation
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.onboarding.OnboardingViewPagerFragmentDirections
import com.bejohen.pikapp.view.onboarding.screens.OnboardingThirdDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel

class OnboardingThirdViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun setOnboardingFinished(status: Boolean) {
        prefHelper.saveOnboardingFinised(status)
    }

    fun getOnboardingFinished(): Boolean {
        return prefHelper.isOnboardingFinished() ?: false
    }

    fun goToLogin(view: View) {
        if (getOnboardingFinished()) {
            val action = OnboardingThirdDirections.actionToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            val action = OnboardingViewPagerFragmentDirections.actionToLoginOnboardingFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun goToSignup(view: View) {
//        if (getOnboardingFinished()) {
//            val action = OnboardingThirdDirections.actionToSignupFragment()
//            Navigation.findNavController(view).navigate(action)
//        } else {
            val action = OnboardingViewPagerFragmentDirections.actionToSignupOnboardingFragment()
            Navigation.findNavController(view).navigate(action)
//        }
    }
}