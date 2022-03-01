package com.tsab.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.LoginResponseV2
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.decodeJWT
import com.tsab.pikapp.util.isUsernameValid
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.viewmodel.BaseViewModel
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
    val firstAppData = MutableLiveData<Int>()

    private val mutableTokenFcm = MutableLiveData<String>()
    val tokenFcm: LiveData<String> get() = mutableTokenFcm
    fun setFcmToken(token: String) {
        mutableTokenFcm.value = token
    }

    private val mutableEmailError = MutableLiveData("")
    val emailError: LiveData<String> get() = mutableEmailError
    val passwordError = MutableLiveData<String>()

    var isEmailValid = false
    private var isPasswordValid = false

    fun login(username: String, pin: String) {
        checkUserInput(username, pin)
        if (isEmailValid && isPasswordValid) {
            loginProcess(username, pin)
        }
    }

    fun validateEmail(email: String) {
        if (email.isEmpty()) {
            mutableEmailError.value = "Silakan masukkan nomor telepon anda"
            isEmailValid = false
        } else if (email.trim().length <= 8) {
            mutableEmailError.value = "Nomor telepon harus lebih dari 8 digit"
            isEmailValid = false
        } else {
            mutableEmailError.value = ""
            isEmailValid = true
        }
    }

    private fun checkUserInput(username: String, pin: String) {
        if (username.isEmpty()) {
            mutableEmailError.value = "Silakan masukkan email anda"
            isEmailValid = false
        } else if (!isUsernameValid(username)) {
            mutableEmailError.value = "Silakan masukkan username anda secara valid"
            isEmailValid = false
        } else {
            mutableEmailError.value = ""
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
        val tokenFCM = tokenFcm.value
        disposable.add(
            apiService.loginMerchant(username, pin, tokenFCM.toString())
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
                        createToastShort(
                            getApplication(),
                            "error: ${errorResponse.errMessage}"
                        )
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
            sessionManager.setUserName(response.results.username)
        }

        prefHelper.saveOnboardingFinised(true)
    }

    private fun loginFail(response: ErrorResponse) {
        loginErrorResponse.value = response
        loading.value = false
    }

    fun goToHome(context: Context) {
        //val homeActivity = Intent(context, HomeActivity::class.java)
        val homeActivity = Intent(context, StoreActivity::class.java)
        context.startActivity(homeActivity)
        (context as OnboardingActivity).finish()
    }

    fun goToUserExclusive(context: Context) {
        val userExclusiveActivity = Intent(context, UserExclusiveActivity::class.java)
        context.startActivity(userExclusiveActivity)
        (context as OnboardingActivity).finish()
    }

    fun onBackPressed() {
        val firstApp = sessionManager.getFirstApp()
        if (firstApp == 1) {
            firstAppData.value = 1
        } else {
            firstAppData.value = 0
        }
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
