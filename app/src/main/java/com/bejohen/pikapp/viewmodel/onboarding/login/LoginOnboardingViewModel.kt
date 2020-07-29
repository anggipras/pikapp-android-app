package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.ErrorResponse
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.isEmailValid
import com.bejohen.pikapp.util.isPasswordValid
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import kotlin.math.log


class LoginOnboardingViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loginResponse = MutableLiveData<LoginResponse>()
    val loginErrorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    val emailValid = MutableLiveData<Boolean>()
    val passwordValid = MutableLiveData<Boolean>()

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

        if (emailValid.value == true && passwordValid.value == true) {
            loginProcess(_email, _password)
        }
    }

    private fun emailValidation(email: String) {
        emailValid.value = email.isEmailValid()
    }

    private fun passwordValidation(password: String) {
        passwordValid.value = password.isPasswordValid()
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
                        Toast.makeText(getApplication(), "token: ${t.token}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable ) {
                        var errorResponse: ErrorResponse
                        try {
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse = Gson().fromJson<ErrorResponse>(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse("400", "Connection Problem")
                        }

                        loginFail(errorResponse)
                        Toast.makeText(getApplication(), errorResponse.errMessage, Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }

    private fun loginSuccess(response: LoginResponse) {
        loginResponse.value = response
        loading.value = false
        prefHelper.saveOnboardingFinised(true)
    }

    private fun loginFail(response: ErrorResponse) {
        loginErrorResponse.value = response
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
