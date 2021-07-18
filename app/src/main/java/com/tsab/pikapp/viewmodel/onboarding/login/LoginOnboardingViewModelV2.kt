package com.tsab.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.LoginResponse
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import com.tsab.pikapp.models.model.LoginResponseV2
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class LoginOnboardingViewModelV2(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loginResponse = MutableLiveData<LoginResponseV2>()
    val loginErrorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()

    private var isEmailValid = false
    private var isPasswordValid = false

    fun login(username: String, pin: String) {
        checkUserInput(username, pin)
        if (isEmailValid && isPasswordValid) {
            loginProcess(username, pin)
        }
    }

    private fun checkUserInput(username: String, pin: String) {
        if (username.isEmpty()) {
            emailError.value = "Silakan masukkan email anda"
            isEmailValid = false
        } else if (!isUsernameValid(username)) {
            emailError.value = "Silakan masukkan username anda secara valid"
            isEmailValid = false
        } else {
            emailError.value = ""
            isEmailValid = true
        }

        if (pin.isEmpty()) {
            passwordError.value = "Silakan masukkan pin anda"
            isPasswordValid = false
        } else if (pin.length < 6) {
            passwordError.value = "Minimal pin 6 digit angka"
            isPasswordValid = false
        } else {
            passwordError.value = ""
            isPasswordValid = true
        }
    }

    private fun loginProcess(username: String, pin: String) {
        loading.value = true
        disposable.add(
                apiService.loginMerchant(username, pin, prefHelper.getFcmToken().toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<LoginResponseV2>() {
                            override fun onSuccess(t: LoginResponseV2) {
                                loginSuccess(t)
                            }

                            override fun onError(e: Throwable) {
                                Log.d("Debug", "error : " + e)
                                var errorResponse: ErrorResponse
                                try {
                                    val responseBody = (e as HttpException)
                                    val body = responseBody.response()?.errorBody()?.string()
                                    errorResponse =
                                            Gson().fromJson(body, ErrorResponse::class.java)
                                } catch (err: Throwable) {
                                    errorResponse =
                                            ErrorResponse(
                                                    "503",
                                                    "Service Unavailable"
                                            )
                                }
                                loginFail(errorResponse)
                                createToastShort(getApplication(), "error: ${errorResponse.errMessage}")
//                        Toast.makeText(getApplication(), "error: ${errorResponse.errMessage}", Toast.LENGTH_SHORT).show()
                            }
                        })
        )
    }

    private fun loginSuccess(response: LoginResponseV2) {
        loginResponse.value = response
        loading.value = false

        response.results?.token?.let {
            val userData: UserAccess = decodeJWT(response.results.token)
            sessionManager.setUserSession(response.results.token, System.nanoTime(), userData)
        }
        prefHelper.saveOnboardingFinised(true)
    }

    private fun loginFail(response: ErrorResponse) {
        loginErrorResponse.value = response
        loading.value = false
    }

    fun goToHome(context: Context) {
        val homeActivity = Intent(context, StoreActivity::class.java)
        context.startActivity(homeActivity)
        (context as OnboardingActivity).finish()
    }

    fun goToUserExclusive(context: Context) {
        val userExclusiveActivity = Intent(context, UserExclusiveActivity::class.java)
        context.startActivity(userExclusiveActivity)
        (context as OnboardingActivity).finish()
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
