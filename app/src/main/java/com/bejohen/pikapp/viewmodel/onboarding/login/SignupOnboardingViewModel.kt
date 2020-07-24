package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class SignupOnboardingViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val registerResponse = MutableLiveData<LoginResponse>()
    val registerLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    val emailValid = MutableLiveData<Boolean>()
    val passwordValid = MutableLiveData<Boolean>()

}