package com.bejohen.pikapp.viewmodel.onboarding

import android.app.Application
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel

class OnboardingThirdViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun setOnboardingFinished(status: Boolean) {
        prefHelper.saveOnboardingFinised(status)
    }


}