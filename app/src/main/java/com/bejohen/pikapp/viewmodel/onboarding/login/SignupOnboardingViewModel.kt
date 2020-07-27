package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.isEmailValid
import com.bejohen.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class SignupOnboardingViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    var email = ""

    val registerResponse = MutableLiveData<LoginResponse>()
    val registerLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    val emailValid = MutableLiveData<Boolean>()
    val passwordValid = MutableLiveData<Boolean>()

    var isEmailValid = false

    fun emailValidation(mail: String) {

        email = mail
        isEmailValid = mail.isEmailValid()
        emailValid.value = mail.isEmailValid()
    }

    fun register(fullName : String, phone: String, password: String, confPassword: String) {
        loading.value = true
        registerSuccess()
    }

    private fun registerProcess() {

    }

    private fun registerSuccess() {
//        registerResponse.value = response

        registerLoadError.value = false
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}