package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.isEmailValid
import com.bejohen.pikapp.util.isPasswordValid
import com.bejohen.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class LoginOnboardingViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loginResponse = MutableLiveData<LoginResponse>()
    val loginLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    val emailValid = MutableLiveData<Boolean>()
    val passwordValid = MutableLiveData<Boolean>()

    var isEmailValid = false
    var isPasswordValid = false

    fun login(email: String?, password: String?) {
        var _email = ""
        var _password = ""

        email?.let {
            _email = it
            emailValidation(it)
        }
        password?.let {
            _password = it
            passwordValidation(it)
        }

        if (isEmailValid && isPasswordValid) {
            loginProcess(_email, _password)
        }
    }

    private fun emailValidation(email: String) {
        emailValid.value = email.isEmailValid()
        isEmailValid = email.isEmailValid()
    }

    private fun passwordValidation(password: String) {
        passwordValid.value = password.isPasswordValid()
        isPasswordValid = password.isPasswordValid()
    }


    private fun loginProcess(email: String, password: String) {
        loading.value = true
        disposable.add(
            apiService.loginUser(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<LoginResponse>() {
                    override fun onSuccess(t: LoginResponse) {
                        loginSuccess(t)
                        Toast.makeText(getApplication(), "LOGIN BANGSD", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        loginLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                        emailValid.value = false
                        Toast.makeText(getApplication(), "Gagal", Toast.LENGTH_SHORT).show()
                    }

                })
        )
    }

    private fun loginSuccess(response: LoginResponse) {
        loginResponse.value = response
        loginLoadError.value = false
        loading.value = false
        prefHelper.saveOnboardingFinised(true)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
