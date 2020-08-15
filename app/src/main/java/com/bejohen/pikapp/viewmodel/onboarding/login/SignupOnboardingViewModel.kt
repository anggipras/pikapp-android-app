package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.isEmailValid
import com.bejohen.pikapp.view.login.SignupFragmentDirections
import com.bejohen.pikapp.view.onboarding.login.SignupOnboardingFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class SignupOnboardingViewModel(application: Application) : BaseViewModel(application) {

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

    fun getOnboardingFinished(): Boolean {
        return prefHelper.isOnboardingFinished() ?: false
    }

    fun emailValidation(mail: String) {

        email = mail
        isEmailValid = mail.isEmailValid()
        emailValid.value = mail.isEmailValid()
    }

    fun register(fullName: String, phone: String, password: String, confPassword: String) {
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

    fun goToSuccess(view: View) {
        if (getOnboardingFinished()) {
            val action = SignupFragmentDirections.actionToSignupSuccessFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            val action = SignupOnboardingFragmentDirections.actionToSignupOnboardingSuccessFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}